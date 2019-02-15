package ru.ivan_alone.playground.minecraft.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ru.ivan_alone.playground.minecraft.PGConstants;
import ru.ivan_alone.playground.minecraft.util.PGRegistry.MenuStorage;

public class PGNotificationServiceResolver implements Runnable {
	private MenuStorage storage;
	
	public PGNotificationServiceResolver(MenuStorage storage) {
		this.storage = storage;
	}
	
	@Override
	public void run() {
		Gson json = new GsonBuilder().create();
        try {
    		NotificationContainer container = json.fromJson(PGNotificationServiceResolver.getHTTPContents(PGConstants.notificationService), NotificationContainer.class);
    		if (container.show()) {
				synchronized (this.storage) {
					Notification notification = container.getNotification();
					this.storage.firstStr    = notification.getLines()[0];
					this.storage.secondStr   = notification.getLines()[1];
					this.storage.siteMapLink = notification.getUrl();
					this.storage.hasSiteAnswer = true;
				}
            }
        } catch (Exception e){ }
	}
	
    public static String getHTTPContents(String url) {
		String r = "";
		HttpURLConnection getter = null;
    	try {
    		getter = (HttpURLConnection)(new URL(url)).openConnection();
    		InputStream in = new BufferedInputStream(getter.getInputStream());
    		r = IOUtils.toString(in, "UTF-8");
    	} catch (Exception e) {} finally {
            if (getter != null) {
            	getter.disconnect();
            }
        }
		return r;
	}
    
    public static class NotificationContainer {
    	private boolean display;
    	private Notification notification;
    	public boolean show() {
    		return this.display;
    	}
    	public Notification getNotification() {
    		return this.notification;
    	}
    }
    public static class Notification {
    	private String[] text;
    	private String url;
    	public String[] getLines() {
    		return new String[] {this.text[0], this.text[1]};
    	}
    	public String getUrl() {
    		return this.url;
    	}
    }
}
