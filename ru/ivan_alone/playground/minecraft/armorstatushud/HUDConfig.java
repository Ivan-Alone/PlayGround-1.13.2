package ru.ivan_alone.playground.minecraft.armorstatushud;

import ru.ivan_alone.playground.minecraft.config.IConfigurable;
import ru.ivan_alone.playground.minecraft.config.PGConfig;

public class HUDConfig implements IConfigurable {
	public static boolean enableArmorStatusHUD = true;
	public static boolean renderTextDamage = true;
	public static HUDLocation location = HUDLocation.RIGHT;
	
	@Override
	public void updateConfig() {
		renderTextDamage = PGConfig.getConfig().getValueBool("b.armorstatushud.remained");
		enableArmorStatusHUD = PGConfig.getConfig().getValueBool("b.armorstatushud.enabled");
		switch (PGConfig.getConfig().getValueInt("b.armorstatushud.location")) {
			default:
			case 0: 
				location = HUDLocation.LEFT;
				break;
			case 1: 
				location = HUDLocation.LEFT_HORIZONTAL;
				break;
			case 2: 
				location = HUDLocation.RIGHT;
				break;
			case 3: 
				location = HUDLocation.RIGHT_HORIZONTAL;
				break;
		}
	}
}
