package ru.ivan_alone.playground.minecraft.config;

import ru.ivan_alone.playground.minecraft.util.GuiPGButtonTextColor;

public class ConfigNode {
	private String key;
	private String value;
	private GuiPGButtonTextColor color;
	private ConfigNodeType type;
	private int[] range;
	private String dependsFrom;
	private boolean hiddenValue = false;
	
	public ConfigNode(String key, String value, ConfigNodeType type, GuiPGButtonTextColor color) {
		this(key, value, type, "", color);
	}
	
	public ConfigNode(String key, String value, ConfigNodeType type, String dependsFrom, GuiPGButtonTextColor color) {
		this(key, value, type, new int[] {0, 1}, dependsFrom, color);
	}
	
	public ConfigNode(String key, String value, String type, GuiPGButtonTextColor color) {
		this(key, value, type, "", color);
	}
	
	public ConfigNode(String key, String value, String type, String dependsFrom, GuiPGButtonTextColor color) {
		this(key, value, ConfigNodeType.valueOf(type == null ? ConfigNodeType.STRING.name() : type), new int[] {0, 1}, dependsFrom, color);
	}
	
	public ConfigNode(String key, String value, ConfigNodeType type, int[] range, GuiPGButtonTextColor color) {
		this(key, value, type, range, "", color);
	}
	
	public ConfigNode(String key, String value, ConfigNodeType type) {
		this(key, value, type, "");
	}
	
	public ConfigNode(String key, String value, ConfigNodeType type, String dependsFrom) {
		this(key, value, type, new int[] {0, 1}, dependsFrom, null);
	}
	
	public ConfigNode(String key, String value, String type) {
		this(key, value, type, "");
	}
	
	public ConfigNode(String key, String value, String type, String dependsFrom) {
		this(key, value, ConfigNodeType.valueOf(type == null ? ConfigNodeType.STRING.name() : type), new int[] {0, 1}, dependsFrom, null);
	}
	
	public ConfigNode(String key, String value, ConfigNodeType type, int[] range) {
		this(key, value, type, range, "", null);
	}
	
	public ConfigNode(String key, String value, ConfigNodeType type, int[] range, String dependsFrom, GuiPGButtonTextColor color) {
		this.key = key;
		this.type = type;
		this.dependsFrom = dependsFrom;
		this.color = color;
		switch(this.type) {
			default:
			case BOOLEAN:
				this.value = !(Integer.parseInt(value) == 0) ? "1" : "0";
				break;
			case RANGE:
				this.range = new int[] {range[0], range[1]};
				this.value = Integer.toString(Integer.parseInt(value));
				break;
			case STRING:
				this.value = value;
				break;
		}
	}
	
	public String getKey() {
		return this.key;
	}
	
	public boolean getValueBool() {
		return !(Integer.parseInt(this.value) == 0);
	}
	
	public String getValue() {
		return this.value;
	}
	
	public ConfigNodeType getType() {
		return this.type;
	}
	
	public void setType(ConfigNodeType type) {
		this.type = type;
	}
	
	public int[] getRange() {
		return this.range;
	}

	public void setRange(int[] range) {
		this.range = range;
	}
	
	public String[] toStringArray() {
		return new String[] { this.key, this.value };
	}
	
	public String getParent() {
		return this.dependsFrom;
	}

	public void setParent(String parent) {
		this.dependsFrom = parent;
	}
	
	public void setHiddenValue(boolean hidden) {
		this.hiddenValue = hidden;
	}
	
	public boolean isHidden() {
		return this.hiddenValue;
	}

	public GuiPGButtonTextColor getColor() {
		return this.color == null ? new GuiPGButtonTextColor() : this.color;
	}

	public void setColor(GuiPGButtonTextColor color2) {
		this.color = color2;
	}
}