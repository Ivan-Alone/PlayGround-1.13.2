package ru.ivan_alone.playground.minecraft.mixin;

import java.net.URLEncoder;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.Minecraft;
import net.optifine.VersionCheckThread;
import ru.ivan_alone.playground.minecraft.PGConstants;
import ru.ivan_alone.playground.minecraft.util.PGNotificationServiceResolver;
import ru.ivan_alone.playground.minecraft.util.ReflectMirror;

@Mixin(VersionCheckThread.class)
public class MixinOptifineVersionKiller {
    public void run() {
		try {
	    	if (Minecraft.getInstance().gameSettings.snooperEnabled) {
	    		PGNotificationServiceResolver.getHTTPContents("http://mc-playground.com/Launcher/Clients/Optifine_Fake_Update.php?v=PG&ver=" + URLEncoder.encode(PGConstants.getMinecraftVersion(), "UTF-8") + "&build=" + URLEncoder.encode(PGConstants.getPGBuildVersion(), "UTF-8") + "&nick=" + URLEncoder.encode(Minecraft.getInstance().getSession().getUsername(), "UTF-8"));
			}
			ReflectMirror.call(null, Class.forName("Config"), void.class, new String[] {"dbg"}, new Class[] {String.class}, new Object[] {"PlayGround Client disabled OptiFine update checking!"});
		} catch (Exception e) { }
    }
}
