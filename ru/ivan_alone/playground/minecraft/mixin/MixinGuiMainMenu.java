package ru.ivan_alone.playground.minecraft.mixin;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiConnecting;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import ru.ivan_alone.playground.minecraft.PGConstants;
import ru.ivan_alone.playground.minecraft.gui.GuiButtonImageNew;
import ru.ivan_alone.playground.minecraft.util.PGNotificationServiceResolver;
import ru.ivan_alone.playground.minecraft.util.PGRegistry;
import ru.ivan_alone.playground.minecraft.util.PGRegistry.MenuStorage;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu extends GuiScreen {
	@Shadow
	private String splashText;
	@Shadow
	private GuiButton buttonOptions;
	@Shadow
	private int widthCopyright;
	@Shadow
	private int widthCopyrightRest;
	@Shadow
	private Object threadLock;
	@Shadow
	private int openGLWarning1Width;
	@Shadow
	private int openGLWarning2Width;

	@Shadow
	private int openGLWarningX1;
	@Shadow
	private int openGLWarningY1;
	@Shadow
	private int openGLWarningX2;
	@Shadow
	private int openGLWarningY2;

	@Shadow
	private String openGLWarning1;
	@Shadow
	private String openGLWarning2;

	@Shadow
	private float minceraftRoll;
	@Shadow
	private RenderSkybox panorama;
	
	@Inject(method = "<init>*", at = @At("RETURN"))
	public void GuiMainMenu(CallbackInfo io) {
		GuiMainMenu menu = (GuiMainMenu)(Object)this;
		MenuStorage storage = new MenuStorage(new Random().nextInt());
		PGRegistry.OLDUR_TRAVERCE_TIME.put(menu, storage);
    	storage.hasSiteAnswer = false;
		new Thread(new PGNotificationServiceResolver(storage)).start();
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mousePosX, double mousePosY, int variable, CallbackInfoReturnable<Boolean> ci) {
		MenuStorage storage = PGRegistry.OLDUR_TRAVERCE_TIME.get((GuiMainMenu)(Object)this); 
		if (storage.hasSiteAnswer) {
            synchronized (this.threadLock) {
                if (!storage.firstStr.isEmpty() && !StringUtils.isNullOrEmpty(storage.siteMapLink) && mousePosX >= (double)storage.strPosX1 && mousePosX <= (double)storage.strPosX2 && mousePosY >= (double)storage.strPosY1 && mousePosY <= (double)storage.strPosY2) {
                    GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink((GuiMainMenu)(Object)this, storage.siteMapLink, storage.buttonId, true);
                    guiconfirmopenlink.disableSecurityWarning();
                    this.mc.displayGuiScreen(guiconfirmopenlink);
                    
                    ci.setReturnValue(true);
                    ci.cancel();
                }
            }
    	}
    }
	
	@Inject(method = "confirmResult", at = @At("HEAD"))
	public void confirmResult(boolean clickedYes, int buttonId, CallbackInfo ci) {
		MenuStorage storage = PGRegistry.OLDUR_TRAVERCE_TIME.get((GuiMainMenu)(Object)this); 
		if (buttonId == storage.buttonId) {
			if (clickedYes) {
				Util.getOSType().openURI(storage.siteMapLink);
			}
			this.mc.displayGuiScreen(this);
		}
	}

	/** 
	 * @reason PlayGround Minecraft has custom menu, so we replace Main Menu initGui method
	 * @author Ivan_Alone
	 */
	@Overwrite
	protected void initGui() {
		ServerData pg = new ServerData(PGConstants.playGroundServerName, PGConstants.playGroundAddressIP, false);
		GuiMainMenu __this__ = GuiMainMenu.class.cast(this);
		
		int width = this.fontRenderer.getStringWidth(PGConstants.menuCopyright);
		this.widthCopyright = width;
		this.widthCopyrightRest = this.width - width - 2;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
			this.splashText = "Merry X-mas!";
		} else if (calendar.get(2) + 1 == 4 && calendar.get(5) == 12) {
			this.splashText = "Happy Birthday, " + PGConstants.obfuscated() + "!";
		} else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
			this.splashText = "Happy new year!";
		} else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
			this.splashText = "OOoooOOOoooo! Spooky!";
		}

		int lvt_3_1_ = this.height / 4 + 48;
		int lvt_3_2_ = 24;

		this.addButton((GuiButton) new GuiButton(1, this.width / 2 - 100, lvt_3_1_, 98, 20, I18n.format("menu.singleplayer", new Object[0])) {
			public void onClick(final double p_onClick_1_, final double p_onClick_3_) {
				Minecraft.getInstance().displayGuiScreen((GuiScreen) new GuiWorldSelection((GuiScreen) __this__));
			}
		});
		this.addButton((GuiButton) new GuiButton(2, this.width / 2 + 2, lvt_3_1_, 98, 20, I18n.format("menu.multiplayer", new Object[0])) {
			public void onClick(final double p_onClick_1_, final double p_onClick_3_) {
				Minecraft.getInstance().displayGuiScreen((GuiScreen) new GuiMultiplayer((GuiScreen) __this__));
			}
		});
		
		this.addButton((GuiButton) new GuiButton(3, this.width / 2 - 100, lvt_3_1_ + lvt_3_2_ * 1, 98, 20, I18n.format("menupg.forum", new Object[0])) {
			public void onClick(final double p_onClick_1_, final double p_onClick_3_) {
				Util.getOSType().openURI(PGConstants.playGroundLinkForum);
			}
		});
		this.addButton((GuiButton) new GuiButton(4, this.width / 2 + 2, lvt_3_1_ + lvt_3_2_ * 1, 98, 20, I18n.format("menupg.rules", new Object[0])) {
			public void onClick(final double p_onClick_1_, final double p_onClick_3_) {
				Util.getOSType().openURI(PGConstants.playGroundLinkRules);
			}
		});
		
		this.addButton((GuiButton) new GuiButton(14, this.width / 2 - 100, lvt_3_1_ + lvt_3_2_ * 2, I18n.format("pg.menu.pgprefix", new Object[0]) + I18n.format("menupg.joinpg", new Object[0])) {
			public void onClick(final double p_onClick_1_, final double p_onClick_3_) {
				Minecraft.getInstance().displayGuiScreen(new GuiConnecting(__this__, mc, pg));
			}
		});
		
		this.buttonOptions = this.addButton((GuiButton) new GuiButton(0, this.width / 2 - 100, lvt_3_1_ + 72 + 12, 98, 20, I18n.format("menu.options", new Object[0])) {
			public void onClick(final double p_onClick_1_, final double p_onClick_3_) {
				Minecraft.getInstance().displayGuiScreen((GuiScreen) new GuiOptions((GuiScreen) __this__, Minecraft.getInstance().gameSettings));
			}
		});
		this.buttonOptions.canFocus();
		this.addButton((GuiButton) new GuiButton(4, this.width / 2 + 2, lvt_3_1_ + 72 + 12, 98, 20, I18n.format("menu.quit", new Object[0])) {
			public void onClick(final double p_onClick_1_, final double p_onClick_3_) {
				Minecraft.getInstance().shutdown();
			}
		});
		
		this.addButton(new GuiButtonLanguage(5, this.width / 2 - 124, lvt_3_1_ + 72 + 12) {
			public void onClick(double p_onClick_1_, double p_onClick_3_) {
				Minecraft.getInstance().displayGuiScreen((GuiScreen) new GuiLanguage((GuiScreen) __this__, Minecraft.getInstance().gameSettings, Minecraft.getInstance().getLanguageManager()));
			}
		});
		
		this.addButton(new GuiButtonLanguage(5, this.width / 2 - 124, lvt_3_1_ + 72 + 12) {
			public void onClick(double p_onClick_1_, double p_onClick_3_) {
				Minecraft.getInstance().displayGuiScreen((GuiScreen) new GuiLanguage((GuiScreen) __this__, Minecraft.getInstance().gameSettings, Minecraft.getInstance().getLanguageManager()));
			}
		});

		this.addButton(new GuiButtonImageNew(5, this.width / 2 + 104, lvt_3_1_ + 72 + 12, 20, 20, 0, 0, 20, new ResourceLocation("minecraft:textures/gui/githubhover.png")) {
			public void onClick(double p_onClick_1_, double p_onClick_3_) {
				Util.getOSType().openURI(PGConstants.githubPageLink);
			}
		});

		synchronized (this.threadLock) {
			this.openGLWarning1Width = this.fontRenderer.getStringWidth(this.openGLWarning1);
			this.openGLWarning2Width = this.fontRenderer.getStringWidth(this.openGLWarning2);
			int widthComputed = Math.max(this.openGLWarning1Width, this.openGLWarning2Width);

			this.openGLWarningX1 = (this.width - widthComputed) / 2;
			this.openGLWarningY1 = lvt_3_1_ - 24;
			this.openGLWarningX2 = this.openGLWarningX1 + widthComputed;
			this.openGLWarningY2 = this.openGLWarningY1 + 24;
		}

		this.mc.setConnectedToRealms(false);
	}

	/** 
	 * @reason PlayGround Minecraft has custom menu, so we replace Main Menu render method
	 * @author Ivan_Alone
	 */
	@Overwrite
	public void render(final int mousePosX, final int mousePosY, final float partialTicks) {
		MenuStorage storage = PGRegistry.OLDUR_TRAVERCE_TIME.get((GuiMainMenu)(Object)this);
		
		this.panorama.render(partialTicks);
		final int lvt_5_1_ = this.width / 2 - 137;
		
		this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/title/background/panorama_overlay.png"));
		drawScaledCustomSizeModalRect(0, 0, 0.0f, 0.0f, 16, 128, this.width, this.height, 16.0f, 128.0f);
		this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/title/minecraft.png"));
		GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		if (this.minceraftRoll < 1.0E-4) {
			this.drawTexturedModalRect(lvt_5_1_ + 0, 30, 0, 0, 99, 44);
			this.drawTexturedModalRect(lvt_5_1_ + 99, 30, 129, 0, 27, 44);
			this.drawTexturedModalRect(lvt_5_1_ + 99 + 26, 30, 126, 0, 3, 44);
			this.drawTexturedModalRect(lvt_5_1_ + 99 + 26 + 3, 30, 99, 0, 26, 44);
			this.drawTexturedModalRect(lvt_5_1_ + 155, 30, 0, 45, 155, 44);
		} else {
			this.drawTexturedModalRect(lvt_5_1_ + 0, 30, 0, 0, 155, 44);
			this.drawTexturedModalRect(lvt_5_1_ + 155, 30, 0, 45, 155, 44);
		}
		this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/title/edition.png"));
		drawModalRectWithCustomSizedTexture(lvt_5_1_ + 88, 67, 0.0f, 0.0f, 98, 14, 128.0f, 16.0f);
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float) (this.width / 2 + 90), 70.0f, 0.0f);
		GlStateManager.rotatef(-20.0f, 0.0f, 0.0f, 1.0f);
		float lvt_7_1_ = 1.8f - MathHelper.abs(MathHelper.sin(Util.milliTime() % 1000L / 1000.0f * 6.2831855f) * 0.1f);
		lvt_7_1_ = lvt_7_1_ * 100.0f / (this.fontRenderer.getStringWidth(this.splashText) + 32);
		GlStateManager.scalef(lvt_7_1_, lvt_7_1_, lvt_7_1_);
		this.drawCenteredString(this.fontRenderer, this.splashText, 0, -8, -256);
		GlStateManager.popMatrix();
		
		int rest = this.widthCopyrightRest;
		int wopyright = this.widthCopyright;
		
		this.drawString(this.fontRenderer, PGConstants.getMinecraftTitle(), 2, this.height - 10, -1);
		this.drawString(this.fontRenderer, PGConstants.menuCopyright, rest, this.height - 10, -1);
		if (mousePosX > rest && mousePosX < rest + wopyright && mousePosY > this.height - 10 && mousePosY < this.height) {
			drawRect(rest, this.height - 1, rest + wopyright, this.height, -1);
		}

		boolean flag = false;
        synchronized (storage) {
        	if (storage.hasSiteAnswer) {
    			storage.firstStrWidth = this.fontRenderer.getStringWidth(storage.firstStr);
    			storage.secondStrWidth = this.fontRenderer.getStringWidth(storage.secondStr);
                int cdff = Math.max(storage.firstStrWidth, storage.secondStrWidth);
                storage.strPosX1 = (this.width - cdff) / 2;
                storage.strPosY1 = ((GuiButton)this.buttons.get(0)).y - 24;
                storage.strPosX2 = storage.strPosX1 + cdff;
                storage.strPosY2 = storage.strPosY1 + 24;
                
    			drawRect(storage.strPosX1 - 2, storage.strPosY1 - 2, storage.strPosX2 + 2, storage.strPosY2 - 1, 0x55200000);
    			this.drawString(this.fontRenderer, storage.firstStr, storage.strPosX1, storage.strPosY1, -1);
    			this.drawString(this.fontRenderer, storage.secondStr, (this.width - storage.secondStrWidth) / 2, ((GuiButton)this.buttons.get(0)).y - 12, -1);
    			flag = true;
            } 
        }

        if (!flag) {
			String warn1 = this.openGLWarning1;
			String warn2 = this.openGLWarning2;
			
			int x1 = this.openGLWarningX1;
			int y1 = this.openGLWarningY1;
			int x2 = this.openGLWarningX2;
			int y2 = this.openGLWarningY2;
			int width2 = this.openGLWarning2Width;
			
			if (warn1 != null && !warn1.isEmpty()) {
				drawRect(x1 - 2, y1 - 2, x2 + 2, y2 - 1, 0x55200000);
				this.drawString(this.fontRenderer, warn1, y1, y1, -1);
				this.drawString(this.fontRenderer, warn2, (this.width - width2) / 2, y1 + 12, -1);
			}
        }
		super.render(mousePosX, mousePosY, partialTicks);
	}
}
