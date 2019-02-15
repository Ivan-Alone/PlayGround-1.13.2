package ru.ivan_alone.playground.minecraft.config;

public class ConfigNodeSelialized {
	public String key;
	public String value;
	public String type;
	public ConfigNodeSelialized(String key, String value, String type) {
		this.key = key;
		this.type = type;
		this.value = value;
	}
}