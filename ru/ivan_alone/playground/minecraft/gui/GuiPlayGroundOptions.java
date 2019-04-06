package ru.ivan_alone.playground.minecraft.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import ru.ivan_alone.playground.minecraft.config.ConfigNode;
import ru.ivan_alone.playground.minecraft.config.ConfigNodeType;
import ru.ivan_alone.playground.minecraft.config.IConfigStringReceiver;
import ru.ivan_alone.playground.minecraft.config.PGConfig;

public class GuiPlayGroundOptions extends GuiScreen implements IConfigStringReceiver {
	protected String title = "Options";
	protected GuiScreen lastScreen;
	private Map<Integer, String> buttonsMap = new HashMap<Integer, String>();
	
	private static final String PG_Prefix = "pg.";
	
	public GuiPlayGroundOptions(GuiScreen lastScreen) {
		this.lastScreen = lastScreen;
	}
	
	protected void initGui() {
		this.title = I18n.format("pg.options.title", new Object[0]);
		int i = 0;
		for (String key : PGConfig.getConfig().getConfigKeys()) {
			String text = PG_Prefix+key;
			boolean isHidden = PGConfig.getConfig().getNode(key).isHidden();
			GuiButton button = new GuiPGButton(text.hashCode(), width / 2 + (i % 2 == 0 ? -155 : 5), height / 6 + (24 * (int)(i / 2)) - 6, 150, 20, I18n.format(text, new Object[0]) + (isHidden ? "" : ": " + PGConfig.getConfig().getValue(key)), PGConfig.getConfig().getNode(key).getColor()) {
				public void onClick(double p_onClick_1_, double p_onClick_3_) {
					switch (PGConfig.getConfig().getType(key)) {
						case BOOLEAN:
							boolean t = PGConfig.getConfig().getValueBool(key);
							PGConfig.getConfig().setValueBool(key, !t);
							if (!isHidden) {
								this.displayString = I18n.format(text, new Object[0]) + ": " + PGConfig.getConfig().getValue(key);
							}
							break;
						case RANGE:
							int[] range = PGConfig.getConfig().getRange(key);
							int t1 = PGConfig.getConfig().getValueInt(key);
							t1++;
							
							if (t1 > range[1]) t1 = range[0];
							PGConfig.getConfig().setValueInt(key, t1);
							if (!isHidden) {
								this.displayString = I18n.format(text, new Object[0]) + ": " + PGConfig.getConfig().getValue(key);
							}
							break;
						case STRING:
							GuiStringEditor gui = new GuiStringEditor(GuiPlayGroundOptions.this, key, PGConfig.getConfig().getValue(key), GuiPlayGroundOptions.this, this);
							GuiPlayGroundOptions.this.close();
							mc.displayGuiScreen(gui);
						default:
					}
					GuiPlayGroundOptions.this.redrawButtons();
				}
			};
			this.buttonsMap.put(text.hashCode(), text);
			addButton(button);
			i++;
		}
		
		addButton(new GuiButton(200, width / 2 - 100, height / 6 + 168, I18n.format("gui.done", new Object[0])) {
			public void onClick(double p_onClick_1_, double p_onClick_3_) {
				mc.displayGuiScreen(lastScreen);
			}
		});
		
		GuiPlayGroundOptions.this.redrawButtons();
	}

	public void close() {
		super.close();
	}
	
	public void redrawButtons() {
		PGConfig config = PGConfig.getConfig();
		this.buttons.forEach(new Consumer<GuiButton>() {
			@Override
			public void accept(GuiButton button) {
				if (button.id == 200) return;
				button.enabled = true;
				String nodeName = GuiPlayGroundOptions.this.buttonsMap.get(button.id).substring(PG_Prefix.length());
				ConfigNode node = config.getNode(nodeName);
				if (node != null && node.getParent() != null && !node.getParent().equals("")) {
					ConfigNode parent = config.getNode(node.getParent());
					if (parent != null && parent.getType() == ConfigNodeType.BOOLEAN && !parent.getValueBool()) {
						button.enabled = false;
					}
				}
			}
		});
	}

	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		drawDefaultBackground();
		drawCenteredString(fontRenderer, title, width / 2, 15, 16777215);
		super.render(p_render_1_, p_render_2_, p_render_3_);
	}

	@Override
	public void returnStringCallback(String key, String value, GuiButton guiButton) {
		PGConfig.getConfig().setValue(key, value, ConfigNodeType.STRING);
		ConfigNode nd = PGConfig.getConfig().getNode(key);
		if (!nd.isHidden()) {
			guiButton.displayString = I18n.format(PG_Prefix+key, new Object[0]) + ": " + PGConfig.getConfig().getValue(key);
		}
	}
}
