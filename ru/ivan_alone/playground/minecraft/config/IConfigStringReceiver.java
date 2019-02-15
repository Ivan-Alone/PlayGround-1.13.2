package ru.ivan_alone.playground.minecraft.config;

import net.minecraft.client.gui.GuiButton;

public interface IConfigStringReceiver {
	public void returnStringCallback(String key, String value, GuiButton guiButton);
}
