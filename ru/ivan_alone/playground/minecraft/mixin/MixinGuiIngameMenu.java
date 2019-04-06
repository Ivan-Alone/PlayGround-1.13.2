package ru.ivan_alone.playground.minecraft.mixin;

import java.util.Iterator;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDirtMessageScreen;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import ru.ivan_alone.playground.minecraft.PGConstants;
import ru.ivan_alone.playground.minecraft.gui.GuiPlayGroundOptions;

@Mixin(GuiIngameMenu.class)
public class MixinGuiIngameMenu extends GuiScreen {

	@SuppressWarnings("unused")
	@Inject(method = "initGui", at = @At("RETURN"))
	protected void initGui(CallbackInfo ci) {
		GuiScreen __this__ = this;
		Minecraft mc = Minecraft.getInstance();
		
		/*
		 * Easter Egg: 
		 * "You probably noticed that the exit button was removed from the game menu"
		 * Think about it, my anime friend
		 */
		
		Iterator<GuiButton> iterator = this.buttons.iterator();
		while(iterator.hasNext()) {
			GuiButton object = iterator.next();
			if (object.id == 1) {
				this.children.remove(object);
				iterator.remove();
			}
		}
		
		GuiButton disconnectButton = this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + -16, I18n.format((String) "menu.returnToMenu", (Object[]) new Object[0])) {
			public void onClick(double p_onClick_1_, double p_onClick_3_) {
				boolean isSingleplayer = mc.isIntegratedServerRunning();
				boolean isRealms = mc.isConnectedToRealms();
				
				this.enabled = false;
				
				mc.world.sendQuittingDisconnectingPacket();
				if (isSingleplayer) {
					mc.loadWorld(null, (GuiScreen) new GuiDirtMessageScreen(I18n.format((String) "menu.savingLevel", (Object[]) new Object[0])));
				} else {
					mc.loadWorld(null);
				}
				
				if (isSingleplayer || PGConstants.joinedFromMainMenu) {
					mc.displayGuiScreen((GuiScreen) new GuiMainMenu());
					PGConstants.joinedFromMainMenu = false;
				} else if (isRealms) {
					RealmsBridge lvt_7_1_ = new RealmsBridge();
					lvt_7_1_.switchToRealms((GuiScreen) new GuiMainMenu());
				} else {
					mc.displayGuiScreen((GuiScreen) new GuiMultiplayer((GuiScreen) new GuiMainMenu()));
				}
			}
		});
		if (!mc.isIntegratedServerRunning()) {
			disconnectButton.displayString = I18n.format((String) "menu.disconnect", (Object[]) new Object[0]);
		}

		GuiButton pgSettings = this.addButton(new GuiButton(new Random().nextInt(), width / 2 - 100, height / 4 + 72 + -16,
				I18n.format("pg.options.title", new Object[0])) {
			public void onClick(double x, double y) {
				mc.displayGuiScreen(new GuiPlayGroundOptions(__this__));
			}
		});
	}
}
