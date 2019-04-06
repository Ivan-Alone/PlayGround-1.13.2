package ru.ivan_alone.playground.minecraft.util;

import net.minecraft.util.text.TextFormatting;

public class GuiPGButtonTextColor {
	private int enabledColor;
	private int hoveredColor;
	private int disabledColor;
	
	public GuiPGButtonTextColor() {
		this.enabledColor = 0xE0E0E0;
		this.hoveredColor = 0xFFFFA0;
		this.disabledColor = 0xA0A0A0;
		this.init();
	}
	
	/** 
	 * @author Ivan_Alone
	 * @reason overwritable method for initialization  
	 **/
	protected void init() { }
	
	public void setHoveredColor(TextFormatting color) {
		if (color != null && color.getColor() != null) {
			this.hoveredColor = color.getColor().intValue();
		}
	}
	
	public void setEnabledColor(TextFormatting color) {
		if (color != null && color.getColor() != null) {
			this.enabledColor = color.getColor().intValue();
		}
	}
	
	public void setDisabledColor(TextFormatting color) {
		if (color != null && color.getColor() != null) {
			this.disabledColor = color.getColor().intValue();
		}
	}
	
	public int getHoveredColor() {
		return this.hoveredColor;
	}
	
	public int getEnabledColor() {
		return this.enabledColor;
	}
	
	public int getDisabledColor() {
		return this.disabledColor;
	}
}
