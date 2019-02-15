package ru.ivan_alone.playground.minecraft.mixin;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;
import net.optifine.player.CapeImageBuffer;
import net.optifine.player.CapeUtils;
import ru.ivan_alone.playground.minecraft.util.PGRegistry;
import ru.ivan_alone.playground.minecraft.util.ReflectMirror;

@Mixin(CapeUtils.class)
public class MixinOptifineCapeUtils {
	@Shadow(remap = false)
    private static Pattern PATTERN_USERNAME;

	/** 
	 * @reason PlayGround Minecraft can operate with PlayGround Capes, so we must check PG repository to find cape  
	 * @author Ivan_Alone
	 */
	@Overwrite(remap = false)
	public static void downloadCape(AbstractClientPlayer player) {
		String username = player.getGameProfile().getName();

        if (username != null && !username.isEmpty() && !username.contains("\u0000") && PATTERN_USERNAME.matcher(username).matches()) {
			String pgCapeUrl = "http://mc-playground.com/Launcher/PGCapes/" + username + ".png";
			String ofCapeUrl = "http://s.optifine.net/capes/" + username + ".png";

			HttpURLConnection httpurlconnection = null;

			try {
				httpurlconnection = (HttpURLConnection) (new URL(pgCapeUrl))
						.openConnection(Minecraft.getInstance().getProxy());
				httpurlconnection.setDoInput(true);
				httpurlconnection.setDoOutput(false);
				httpurlconnection.connect();
				int mptHash = httpurlconnection.getResponseCode();
				if (mptHash >= 200 && mptHash < 400) {
					ofCapeUrl = pgCapeUrl;
				}
			} catch (Exception var13) {
				;
			} finally {
				if (httpurlconnection != null) {
					httpurlconnection.disconnect();
				}

			}
			final boolean addElytra = ofCapeUrl.equals(pgCapeUrl);
            String s2 = FilenameUtils.getBaseName(ofCapeUrl);
			
            ResourceLocation resourcelocation = new ResourceLocation("capeof/" + s2);
            TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
            ITextureObject itextureobject = texturemanager.getTexture(resourcelocation);

            if (itextureobject != null && itextureobject instanceof ThreadDownloadImageData)
            {
                ThreadDownloadImageData threaddownloadimagedata = (ThreadDownloadImageData)itextureobject;
                
				Boolean imageFound = ReflectMirror.get(threaddownloadimagedata, ThreadDownloadImageData.class, Boolean.class, new String[] {"imageFound"});

                if (imageFound != null) {
                    if (imageFound) {
                    	ReflectMirror.call(player, AbstractClientPlayer.class, void.class, new String[] {"setLocationOfCape"}, new Class[] {ResourceLocation.class}, new Object[] {resourcelocation});
						if (addElytra) {
							PGRegistry.CAPES_ELYTRAS.put(username, resourcelocation);
						}
                    }

                    return;
                }
            }
            
            CapeImageBuffer capeimagebuffer = new CapeImageBuffer(player, resourcelocation);
            PGRegistry.FROM_THE_SHADOWS.put(capeimagebuffer, addElytra);
            
            ResourceLocation resourcelocationempty = new ResourceLocation("optifine/ctm/default/empty.png");
            ThreadDownloadImageData texture = new ThreadDownloadImageData((File)null, ofCapeUrl, resourcelocationempty, capeimagebuffer);
			ReflectMirror.set(texture, ThreadDownloadImageData.class, new String[] {"pipeline"}, true);
			
            texturemanager.loadTexture(resourcelocation, texture);
			if (addElytra) {
				PGRegistry.CAPES_ELYTRAS.put(username, resourcelocation);
			}
		}

	}
}
