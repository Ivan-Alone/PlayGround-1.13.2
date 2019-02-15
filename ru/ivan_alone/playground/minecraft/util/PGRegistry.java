package ru.ivan_alone.playground.minecraft.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.util.ResourceLocation;

public class PGRegistry {
	public static final Map<String, ResourceLocation> CAPES_ELYTRAS = new HashMap<String, ResourceLocation>();
	public static final Map<IImageBuffer, Boolean> FROM_THE_SHADOWS = new HashMap<IImageBuffer, Boolean>();
	public static final Map<GuiMainMenu, MenuStorage> OLDUR_TRAVERCE_TIME = new HashMap<GuiMainMenu, MenuStorage>();
	
	public static class MenuStorage {
		public MenuStorage(int buttonId) {
			this.buttonId = buttonId;
		}
		
	    public String firstStr;
	    public String secondStr;
	    public String siteMapLink;
	    
	    public int buttonId;
	    
	    public int strPosX1;
	    public int strPosY1;
	    public int strPosX2;
	    public int strPosY2;
	    public int firstStrWidth;
	    public int secondStrWidth;
	    public boolean hasSiteAnswer = false;
	}
}
