package ru.ivan_alone.playground.minecraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MixinMinecraft {
	/** 
	 * @reason We replace version type of Minecraft to PlayGround version with Rift ModLoader 
	 * @author Ivan_Alone
	 */
	@Overwrite
	public String getVersionType() {
		return "PlayGround Minecraft / Rift ML";
	}
}