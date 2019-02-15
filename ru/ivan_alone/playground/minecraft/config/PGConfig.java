package ru.ivan_alone.playground.minecraft.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.client.Minecraft;

public class PGConfig {
	private static PGConfig instance = null;
	private GsonBuilder builder = new GsonBuilder();
	private Gson gson = builder.create();
	private Map<String, ConfigNode> config = new HashMap<String, ConfigNode>();
	
	private List<IConfigurable> forRefresh = new ArrayList<IConfigurable>();
	
	private static final ConfigNode[] DEFAULT_CONFIG = new ConfigNode[] {
		new ConfigNode("a.debug.pgactive", "1", ConfigNodeType.BOOLEAN),
		new ConfigNode("a.debug.pgadvance", "0", ConfigNodeType.BOOLEAN, "a.debug.pgactive"),
		new ConfigNode("b.armorstatushud.enabled", "1", ConfigNodeType.BOOLEAN),
		new ConfigNode("b.armorstatushud.location", "2", ConfigNodeType.RANGE, new int[] {0, 3}, "b.armorstatushud.enabled"),
		new ConfigNode("b.armorstatushud.remained", "1", ConfigNodeType.BOOLEAN, "b.armorstatushud.enabled"),
		new ConfigNode("a.misc.potionsduration", "1", ConfigNodeType.BOOLEAN),
		new ConfigNode("a.popenchant.enabled", "1", ConfigNodeType.BOOLEAN),
		new ConfigNode("a.misc.oldexpcreative", "0", ConfigNodeType.BOOLEAN),
		new ConfigNode("a.popenchant.showall", "0", ConfigNodeType.BOOLEAN, "a.popenchant.enabled"),
		new ConfigNode("c.authme.password", "", ConfigNodeType.STRING)
	};
	
	static {
		for (ConfigNode node : DEFAULT_CONFIG) {
			if (node.getKey().equals("c.authme.password")) {
				node.setHiddenValue(true);
			}
		}
	}
	
	private PGConfig() {
		new Thread(new ConfigLoaderThread()).start();
	}
	
	public static PGConfig getConfig() {
		if (instance == null) {
			instance = new PGConfig();
		}
		return instance;
	}
	
	private File getConfigFile() {
		if (Minecraft.getInstance() != null) {
			return new File(Minecraft.getInstance().gameDir, "playgroundcfg.json");
		}
		return null;
	}
	
	private void initDefaults() {
		for (ConfigNode var : DEFAULT_CONFIG) {
			if (!this.config.containsKey(var.getKey())) {
				this.config.put(var.getKey(), var);
			}
			
			ConfigNode node = this.config.get(var.getKey());
			if (node.getType() != var.getType()) {
				node.setType(var.getType());
			}
			if (node.getType() == ConfigNodeType.RANGE && (node.getRange() == null || (node.getRange()[0] == 0 && node.getRange()[1] == 1))) {
				node.setRange(var.getRange());
			} 
			if (node.getParent() == null || node.getParent().equals("")) {
				node.setParent(var.getParent());
			} 
			if (var.isHidden()) {
				node.setHiddenValue(true);
			} 
		}
	}
	
	public List<String> getConfigKeys() {
		this.initDefaults();
		List<String> list = Arrays.asList(this.config.keySet().toArray(new String[this.config.keySet().size()]));
		list.sort(new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				return a.compareTo(b);
			}
		});
		return list;
	}
	
	public ConfigNodeType getType(String key) {
		return this.config.get(key).getType();
	}
	
	public int[] getRange(String key) {
		return this.config.get(key).getRange();
	}
	
	public void reloadConfig() {
		try {
			if (!this.getConfigFile().exists()) {
				this.getConfigFile().createNewFile();
			}
			byte[] configJson = IOUtils.readFully(new FileInputStream(this.getConfigFile()), (int) this.getConfigFile().length());
			String json = new String(configJson, StandardCharsets.UTF_8);
			
			Config cfg = gson.fromJson(json, Config.class);
			if (cfg == null) {
				this.config = new HashMap<String, ConfigNode>();
			} else {
				this.config = cfg.toHashMap();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.writeConfig();
	}
	
	private void writeConfig() {
		this.initDefaults();
		String json = gson.toJson(new Config(this.config));
		try {
			if (!this.getConfigFile().exists()) {
				this.getConfigFile().createNewFile();
			}
			IOUtils.write(json, new FileOutputStream(this.getConfigFile()), StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (IConfigurable obj : this.forRefresh) {
			obj.updateConfig();
		}
	}
	
	public void addConfigurable(IConfigurable obj) {
		this.forRefresh.add(obj);
	}
	
	public void setValue(String key, String value, ConfigNodeType type) {
		this.config.put(key, new ConfigNode(key, value, type));
		this.writeConfig();
	}
	
	public void setValueInt(String key, String value) {
		this.setValue(key, value, ConfigNodeType.STRING);
	}
	
	public void setValueInt(String key, int value) {
		this.setValue(key, Integer.toString(value), ConfigNodeType.RANGE);
	}
	
	public void setValueFloat(String key, float value) {
		this.setValue(key, Float.toString(value), ConfigNodeType.STRING);
	}
	
	public void setValueBool(String key, boolean value) {
		this.setValue(key, value ? "1" : "0", ConfigNodeType.BOOLEAN);
	}
	
	public String getValue(String key) {
		return this.config.get(key).getValue();
	}
	
	public int getValueInt(String key) {
		return Integer.parseInt(this.getValue(key));
	}
	
	public float getValueFloat(String key) {
		return Float.parseFloat(this.getValue(key));
	}
	
	public boolean getValueBool(String key) {
		return !(Integer.parseInt(this.getValue(key)) == 0);
	}

	public ConfigNode getNode(String key) {
		return this.config.get(key);
	}
}
