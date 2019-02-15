package ru.ivan_alone.playground.minecraft.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonImageNew extends GuiButtonImage {

	public GuiButtonImageNew(int id, int posX, int posY, int width, int height, int imagePosX, int imagePosY, int imagePosYHover, ResourceLocation image) {
		super(id, posX, posY, width, height, imagePosX, imagePosY, imagePosYHover, image);
	}
	
	@Override
	public void render(int mousePosX, int mousePosY, float renderPass) {
		if (!this.visible) {
			return;
		}
		
		this.hovered = ((mousePosX >= this.x) && (mousePosY >= this.y) && (mousePosX < this.x + this.width) && (mousePosY < this.y + this.height));

		Minecraft mc = Minecraft.getInstance();
		mc.getTextureManager().bindTexture(GuiButton.BUTTON_TEXTURES);
		GlStateManager.disableDepthTest();

		int yTexStart = 66;
		
		if (this.hovered) {
			yTexStart += 20;
		}
		this.drawTexturedModalRect(this.x, this.y, 0, yTexStart, this.width / 2, this.height);
		this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, yTexStart, this.width / 2, this.height);
		
		GlStateManager.enableDepthTest();
		
		super.render(mousePosX, mousePosY, renderPass);
	}
}
