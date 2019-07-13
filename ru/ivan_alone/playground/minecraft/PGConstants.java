package ru.ivan_alone.playground.minecraft;

import java.awt.CardLayout;
import java.awt.datatransfer.ClipboardOwner;
import java.io.InputStream;
import java.io.Writer;
import java.lang.annotation.Retention;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.ResourceLocation;
import ru.ivan_alone.playground.minecraft.armorstatushud.HUDConfig;
import ru.ivan_alone.playground.minecraft.config.PGConfig;

public class PGConstants {
	private PGConstants() {
	}
	
	public static boolean joinedFromMainMenu = false;
	
	private static boolean siteDown = true;
	
	private static String buildTime = null;
	private static Object nCache = null;

	public static final ResourceLocation logoPlayGround = new ResourceLocation("textures/gui/title/playground.png");
	public static final String winTitlePrefix = PGConstants.getMinecraftTitle() + " " + PGConstants.getPGBuildVersion() + " - ";
	public static final String menuCopyright = "Copyright Mojang AB. Built by " + PGConstants.obfuscated();

    public static final String notificationService = "http://mc-playground.com/Launcher/Clients/NotificationService.php";

	public static final String playGroundServerName = "PlayGround Minecraft Survival 1.13";
    public static final String playGroundAddressIP = "minecraft.playground.ru:25565";

    public static final String playGroundLinkForum = "http://forums.playground.ru/minecraft/";
    public static final String playGroundLinkRules = "http://mc-playground.com/rules.html";
    
    public static final String githubPageLink = "https://github.com/ivan-alone/PlayGround-1.13.2/";
    
    public static final String buildInfoFilename = "/playground.json";
    public static final String pgModInfoFilename = "/riftmod.json";
    
	public static String getPGBuildVersion() {
		return "#5.1";
	}

	public static void runDownDetector() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection httpurlconnection = null;
	
				try {
					int timeout = 2000;
					long time = System.currentTimeMillis();
					httpurlconnection = (HttpURLConnection) (new URL(PGConstants.notificationService)).openConnection(Minecraft.getInstance().getProxy());
					httpurlconnection.setConnectTimeout(timeout + 500);
					httpurlconnection.setDoInput(true);
					httpurlconnection.setDoOutput(false);
					httpurlconnection.connect();
					if (time + timeout > System.currentTimeMillis()) {
						PGConstants.siteDown = false;
					}
				} catch (Exception e) {
				} finally {
					if (httpurlconnection != null) {
						httpurlconnection.disconnect();
					}
				}
			}
		}).start();
	}

	public static boolean isSiteDown() {
		return PGConstants.siteDown;
	}

	public static String getPGBuildInfo() {
		if (PGConstants.buildTime == null) {
			PGConstants.buildTime = "<Time_Cant_Be_Readen>";

			InputStream input = PGConstants.class.getResourceAsStream("/playground.json");
			try {
				byte[] buffer = new byte[input.available()];
				IOUtils.readFully(input, buffer);
				String stringBuffer = new String(buffer);

				Gson json = new GsonBuilder().create();
				Object playGroundJson = json.fromJson(stringBuffer, PGJSON.class);

				PGConstants.buildTime = playGroundJson.toString();
			} catch (Exception e) {
			}
		}
		return "Built by " + PGConstants.obfuscated() + ", " + PGConstants.buildTime;
	}

	public static String getMinecraftVersion() {
		return "1.13.2";
	}

	public static String getMinecraftTitle() {
		return "Minecraft " + PGConstants.getMinecraftVersion() + " PG";
	}
	
	public static String getWindowTitle() {
		PGConfig.getConfig().addConfigurable(new HUDConfig());
		return PGConstants.winTitlePrefix + Minecraft.getInstance().getSession().getUsername();
	}
	
	/**
	 * @reason Too obfuscated method, good luck have fun
	 * @author Ivan_Alone
	 */
	public static String obfuscated() { 
		if (PGConstants.nCache == null) {
			byte[] a = new byte[] { 46, 95, 92, 110, 105 };
			Object b = Object.class.cast(PGConstants.class.getName().split(new String(new byte[] { a[2], a[0] }))[1]);
			Object c = String.class.cast(b).split(
					new String(new byte[] { (byte) (Retention.class.getName().length() + Writer.class.getName().length()
							+ Buffer.class.getName().length() + ClipboardOwner.class.getName().length()) }));
			Object d = new String[String[].class.cast(c).length];
			Object e = 0;
			for (Object part : Object[].class.cast(c)) {
				((Object[]) d)[Integer.class
						.cast(e)] = new String(new char[] { String.class.cast(part).charAt(Integer.class.cast(e) % 1) })
								.toUpperCase()
								+ String.class.cast(part)
										.substring((int) Math.pow(Double.parseDouble(Integer.class.cast(e).toString()),
												(double) new Boolean(false).compareTo(false)));
				e = Integer.class.cast(e) - new Boolean(System.err.toString()).compareTo(true);
			}
			try {
				Method ms = Class
						.forName(
								String.class
										.cast(ClassLoader.class.getName() + String.class.getName()).substring(
												ThreadDeath.class.getName().length())) 
						.getMethod(
								new String(
										new byte[] {
												(byte) (a[4] + (int) Math.pow(
														Double.parseDouble(Integer.class
																.cast((int) System.currentTimeMillis()).toString()),
														(double) new Boolean(false).compareTo(false))),
												(byte) (Math.log10(CardLayout.class.getSimpleName().length()) + a[3]),
												a[4], a[3] }),
								CharSequence.class, CharSequence[].class);
				PGConstants.nCache = ms.invoke(null, new String(new byte[] { a[1] }), String[].class.cast(d));
			} catch (Exception t) {
			}
		}
		return (String) PGConstants.nCache;
	}

	private static class PGJSON {
		private String build;

		@Override
		public String toString() {
			return build;
		}
	}

	public static boolean isPlayerOnPG(NetworkManager netManager) {
		return netManager.getRemoteAddress().toString().startsWith(PGConstants.playGroundAddressIP.split(":")[0]);
	}
}
