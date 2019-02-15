package ru.ivan_alone.playground.minecraft;

import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

public class Client implements InitializationListener {

	@Override
	public void onInitialization() {
		MixinBootstrap.init();
		boolean optifine = true;
		try {
			Class.forName("net.optifine.ClearWater");
		} catch (Exception e) {
			optifine = false;
		}
		if (optifine) {
			Mixins.addConfiguration("mixins.playground_minecraft.json");
		} else {
			Mixins.addConfiguration("mixins.playground_minecraft.dev.json");
		}
	}
}
