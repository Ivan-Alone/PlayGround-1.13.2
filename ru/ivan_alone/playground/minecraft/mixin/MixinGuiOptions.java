package ru.ivan_alone.playground.minecraft.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import ru.ivan_alone.playground.minecraft.gui.GuiPlayGroundOptions;

@Mixin(GuiOptions.class)
public class MixinGuiOptions extends GuiScreen {
	
	@SuppressWarnings("unused")
	@Inject(method = "initGui", at = @At("RETURN"))
	protected void initGui(CallbackInfo ci) {
		GuiOptions __this__ = GuiOptions.class.cast(this);
		
		boolean flag = false;
		int[] pos = new int[2];
		
		for (int i = 0; i < this.buttons.size(); i++) {
			GuiButton button = this.buttons.get(i);
			if (button.id == GameSettings.Options.REALMS_NOTIFICATIONS.getOrdinal()) {
				pos = new int[]{button.x, button.y}; 
				__this__.getChildren().remove(button);
				this.buttons.remove(button);
				flag = true;
				break;
			}
		}
		
		if (flag) {
			GuiButton pgSettings = addButton(new GuiButton(new Random().nextInt(), pos[0], pos[1], 150, 20, I18n.format("pg.options.title", new Object[0]) + "...") {
				public void onClick(double x, double y) {
					mc.displayGuiScreen(new GuiPlayGroundOptions(__this__));
				}
			});
		}
	}
}
