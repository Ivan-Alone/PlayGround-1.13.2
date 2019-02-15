package ru.ivan_alone.playground.minecraft.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;
import ru.ivan_alone.playground.minecraft.PGConstants;
import ru.ivan_alone.playground.minecraft.util.ReflectMirror;

@Mixin(GuiVideoSettings.class)
public abstract class MixinOptifineGuiVideoSettings extends GuiScreen {
	@Shadow(remap = false)
	protected List<GuiButton> buttonList;
	@Shadow
	protected String screenTitle;
	
	public void render(int mouseX, int mouseY, float partialTicks) {
		GuiVideoSettings __this__ = GuiVideoSettings.class.cast(this);
		Minecraft mc = Minecraft.getInstance();
		
		__this__.drawDefaultBackground();
		__this__.drawCenteredString(mc.fontRenderer, this.screenTitle, __this__.width / 2, 15, 0xFFFFFF);
		__this__.drawString(mc.fontRenderer, PGConstants.getPGBuildInfo(), 2, __this__.height - 10, 0x808080);
		int i = mc.fontRenderer.getStringWidth(PGConstants.getMinecraftTitle());
		__this__.drawString(mc.fontRenderer, PGConstants.getMinecraftTitle(), __this__.width - i - 2, __this__.height - 10, 0x808080);
		
		for (i = 0; i < this.buttons.size(); ++i) {
			((GuiButton) this.buttons.get(i)).render(mouseX, mouseY, partialTicks);
		}

		for (i = 0; i < this.labels.size(); ++i) {
			((GuiLabel) this.labels.get(i)).render(mouseX, mouseY, partialTicks);
		}
		
		Object tooltipManager = ReflectMirror.get(__this__, GuiVideoSettings.class, Object.class, new String[] {"tooltipManager"});
		try {
			ReflectMirror.call(tooltipManager, Class.forName("net.optifine.gui.TooltipManager"), new String[] {"drawTooltips"}, new Class[] {int.class, int.class, List.class}, new Object[] {mouseX, mouseY, this.buttonList});
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
