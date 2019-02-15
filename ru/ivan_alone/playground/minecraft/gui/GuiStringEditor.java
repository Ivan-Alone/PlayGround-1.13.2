package ru.ivan_alone.playground.minecraft.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import ru.ivan_alone.playground.minecraft.config.IConfigStringReceiver;

public class GuiStringEditor extends GuiScreen {
	protected GuiTextField passwordTextField;
	protected GuiButton doneButton;
	protected GuiButton cancelButton;

	private String title;

	private GuiScreen parent;
	private IConfigStringReceiver callback;
	private String key;
	private String value;
	private GuiButton guiButton;

	public GuiStringEditor(GuiScreen parent, String key, String value, IConfigStringReceiver callback, GuiButton guiButton) {
		this.key = key;
		this.value = value;
		this.parent = parent;
		this.callback = callback;
		this.guiButton = guiButton;
	}

	protected void initGui() {
		this.title = I18n.format("pg.autologin.password.title", new Object[0]);
		
		this.mc.keyboardListener.enableRepeatEvents(true);
		this.doneButton = this.addButton(new GuiButton(0, this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.format((String) "gui.done", (Object[]) new Object[0])) {
			public void onClick(double p_onClick_1_, double p_onClick_3_) {
				GuiStringEditor.this.callback.returnStringCallback(GuiStringEditor.this.key, GuiStringEditor.this.passwordTextField.getText(), GuiStringEditor.this.guiButton);
				Minecraft.getInstance().displayGuiScreen(GuiStringEditor.this.parent);
			}
		});
		this.cancelButton = this.addButton(new GuiButton(1, this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.format((String) "gui.cancel", (Object[]) new Object[0])) {
			public void onClick(double p_onClick_1_, double p_onClick_3_) {
				Minecraft.getInstance().displayGuiScreen(GuiStringEditor.this.parent);
			}
		});
		this.passwordTextField = new GuiTextField(2, this.fontRenderer, this.width / 2 - 150, 50, 300, 20) {
			public void setFocused(boolean p_setFocused_1_) {
				super.setFocused(p_setFocused_1_);
			}
		};
		this.passwordTextField.setMaxStringLength(32500);
		this.passwordTextField.setText(this.value != null ? this.value : "");
		this.children.add(this.passwordTextField);
		this.passwordTextField.setFocused(true);
		this.setFocused(this.passwordTextField);
	}

	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		drawDefaultBackground();
		drawCenteredString(fontRenderer, title, width / 2, 15, 0xFFFFFF);

		this.drawString(this.fontRenderer, I18n.format("pg.autologin.password.tip", new Object[0]), this.width / 2 - 150, 33, 0xA0A0A0);
		
		GL11.glPushMatrix();
		GL11.glScalef(0.5F, 0.5F, 1.0F);
		this.drawString(this.fontRenderer, I18n.format("pg.autologin.password.tip2", new Object[0]), (this.width / 2 - 150) * 2, (43) * 2, 0xA0A0A0);
		GL11.glPopMatrix();
		
		this.passwordTextField.drawTextField(p_render_1_, p_render_2_, p_render_3_);
		super.render(p_render_1_, p_render_2_, p_render_3_);
	}
}