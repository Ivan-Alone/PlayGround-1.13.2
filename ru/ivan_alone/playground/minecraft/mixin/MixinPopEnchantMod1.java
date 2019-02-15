package ru.ivan_alone.playground.minecraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import ru.ivan_alone.playground.minecraft.popenchant.PopEnchant;

@Mixin(GuiIngame.class)
public class MixinPopEnchantMod1 {
	@Inject(method = "renderGameOverlay", at = @At("RETURN"))
	public void renderGameOverlay(float p_renderGameOverlay_1_, CallbackInfo ci) {
		PopEnchant.getPopEnchant().render(Minecraft.getInstance().mainWindow.getScaledWidth(), Minecraft.getInstance().mainWindow.getScaledHeight());
	}
}
