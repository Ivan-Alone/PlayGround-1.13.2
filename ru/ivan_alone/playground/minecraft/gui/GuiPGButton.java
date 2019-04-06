package ru.ivan_alone.playground.minecraft.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import ru.ivan_alone.playground.minecraft.util.GuiPGButtonTextColor;

public class GuiPGButton extends GuiButton {

	private GuiPGButtonTextColor color;
	
	public GuiPGButton(int id, int x, int y, int width, int height, String text, GuiPGButtonTextColor color) {
		super(id, x, y, width, height, text);
		this.color = color;
	}
	
	public void render(int mouseX, int mouseY, float partialTicks) {
		if (!this.visible) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		FontRenderer fontRenderer = mc.fontRenderer;
		mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
		GlStateManager.color4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
		this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
		int hoverState = this.getHoverState(this.hovered);
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate((GlStateManager.SourceFactor) GlStateManager.SourceFactor.SRC_ALPHA,
				(GlStateManager.DestFactor) GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
				(GlStateManager.SourceFactor) GlStateManager.SourceFactor.ONE,
				(GlStateManager.DestFactor) GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc((GlStateManager.SourceFactor) GlStateManager.SourceFactor.SRC_ALPHA,
				(GlStateManager.DestFactor) GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		this.drawTexturedModalRect(this.x, this.y, 0, 46 + hoverState * 20, this.width / 2, this.height);
		this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + hoverState * 20, this.width / 2, this.height);
		this.renderBg(mc, mouseX, mouseY);
		
		int textColor = this.color.getEnabledColor();
		if (!this.enabled) {
			textColor = this.color.getDisabledColor();
		} else if (this.hovered) {
			textColor = this.color.getHoveredColor();
		}
		this.drawCenteredString(fontRenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, textColor);
	}

}
