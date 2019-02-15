package ru.ivan_alone.playground.minecraft.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Config {
	public ConfigNodeSelialized[] parameters;
	public Config(Map<String, ConfigNode> config) {
		List<ConfigNodeSelialized> nodes = new ArrayList<ConfigNodeSelialized>();
		for (Entry<String, ConfigNode> ent : config.entrySet()) {
			nodes.add(new ConfigNodeSelialized(ent.getKey(), ent.getValue().getValue(), ent.getValue().getType().toString()));
		}
		parameters = nodes.toArray(new ConfigNodeSelialized[nodes.size()]);
	}
	public Map<String, ConfigNode> toHashMap() {
		Map<String, ConfigNode> config = new HashMap<String, ConfigNode>();
		
		for (ConfigNodeSelialized node : this.parameters) {
			config.put(node.key, new ConfigNode(node.key, node.value, node.type));
		}
		
		return config;
	}
} 