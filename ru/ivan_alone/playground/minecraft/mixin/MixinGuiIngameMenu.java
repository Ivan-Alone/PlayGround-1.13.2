package ru.ivan_alone.playground.minecraft.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import ru.ivan_alone.playground.minecraft.gui.GuiPlayGroundOptions;

@Mixin(GuiIngameMenu.class)
public class MixinGuiIngameMenu extends GuiScreen {

	@SuppressWarnings("unused")
	@Inject(method = "initGui", at = @At("RETURN"))
	protected void initGui(CallbackInfo ci) {
		GuiScreen __this__ = this;
		GuiButton pgSettings = addButton(new GuiButton(new Random().nextInt(), width / 2 - 100, height / 4 + 72 + -16, I18n.format("pg.options.title", new Object[0])) {
			public void onClick(double x, double y) {
				Minecraft.getInstance().displayGuiScreen(new GuiPlayGroundOptions(__this__));
			}
		});
	}
}
