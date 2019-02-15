package ru.ivan_alone.playground.minecraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import ru.ivan_alone.playground.minecraft.popenchant.PopEnchant;

@Mixin(Minecraft.class)
public class MixinPopEnchantMod2 {
	@Inject(method = "runTick", at = @At("RETURN"))
	public void runTick(CallbackInfo ci) {
		PopEnchant.getPopEnchant().tick(Minecraft.getInstance(), Minecraft.getInstance().getRenderPartialTicks(), Minecraft.getInstance().world != null, true);
	}
}
