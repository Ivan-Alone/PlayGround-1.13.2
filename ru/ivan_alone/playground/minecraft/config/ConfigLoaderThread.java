package ru.ivan_alone.playground.minecraft.config;

import net.minecraft.client.Minecraft;

public class ConfigLoaderThread implements Runnable {

	@Override
	public void run() {
		while (Minecraft.getInstance() == null) {
			
		}
		PGConfig.getConfig().reloadConfig();
	}

}
