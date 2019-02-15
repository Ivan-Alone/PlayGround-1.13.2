package ru.ivan_alone.playground.minecraft.mixin;

import java.util.Collection;
import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Ordering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameType;
import ru.ivan_alone.playground.minecraft.config.PGConfig;

@Mixin(GuiIngame.class)
public class MixinGuiIngame extends Gui {
	@Shadow 
	private int scaledWidth;
	
	private static String durationAdecvate(PotionEffect effect) {
		if (effect.getIsPotionDurationMax()) {
			return "**:**";
		}
		int ticks = effect.getDuration();
		int seconds = ticks / 20;
		int min = seconds / 60;
		seconds -= min * 60;
		return min + ":" + (seconds > 9 ? Integer.valueOf(seconds) : new StringBuilder().append("0").append(seconds).toString());
	}
	
	@Inject(method = "renderHotbar", at = @At("RETURN"))
	protected void renderHotbar(float partialTicks, CallbackInfo ci) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player.experienceLevel > 0 && !mc.player.isRidingHorse() && mc.playerController.getCurrentGameType().equals(GameType.CREATIVE) && PGConfig.getConfig().getValueBool("a.misc.oldexpcreative")) {
			mc.profiler.startSection("expLevel");
			String lvt_3_2_ = "" + mc.player.experienceLevel;
			int xPos = (mc.mainWindow.getScaledWidth() - mc.fontRenderer.getStringWidth(lvt_3_2_)) / 2;
			int yPos = mc.mainWindow.getScaledHeight() - 31 - 4;
			mc.fontRenderer.drawString(lvt_3_2_, (float) (xPos + 1), (float) yPos, 0);
			mc.fontRenderer.drawString(lvt_3_2_, (float) (xPos - 1), (float) yPos, 0);
			mc.fontRenderer.drawString(lvt_3_2_, (float) xPos, (float) (yPos + 1), 0);
			mc.fontRenderer.drawString(lvt_3_2_, (float) xPos, (float) (yPos - 1), 0);
			mc.fontRenderer.drawString(lvt_3_2_, (float) xPos, (float) yPos, 8453920);
			mc.profiler.endSection();
		}
	}

	@Inject(method = "renderPotionEffects", at = @At("HEAD"), cancellable = true)
	protected void renderPotionEffects(CallbackInfo ci) {
		Minecraft mc = Minecraft.getInstance();

		if (!mc.gameSettings.showDebugInfo && PGConfig.getConfig().getValueBool("a.misc.potionsduration")) {
			Collection<PotionEffect> effects = mc.player.getActivePotionEffects();
			if (!effects.isEmpty()) {
				mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
				GlStateManager.enableBlend();
				int lvt_2_1_ = 0;
				int lvt_3_1_ = 0;
				Iterator<PotionEffect> iterator = Ordering.natural().reverse().sortedCopy(effects).iterator();
	
				while (iterator.hasNext()) {
					PotionEffect effect = (PotionEffect) iterator.next();
					Potion potion = effect.getPotion();
					if (potion.hasStatusIcon() && effect.isShowIcon()) {
						int scaledWidth = this.scaledWidth;
						int scaledHeight = 1;
						if (mc.isDemo()) {
							scaledHeight += 15;
						}
	
						int lvt_9_1_ = potion.getStatusIconIndex();
						if (potion.isBeneficial()) {
							++lvt_2_1_;
							scaledWidth -= 25 * lvt_2_1_;
						} else {
							++lvt_3_1_;
							scaledWidth -= 25 * lvt_3_1_;
							scaledHeight += 26;
						}
	
						GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
						float lvt_10_1_ = 1.0F;
						int lvt_11_1_;
						if (effect.isAmbient()) {
							this.drawTexturedModalRect(scaledWidth, scaledHeight, 165, 166, 24, 24);
						} else {
							this.drawTexturedModalRect(scaledWidth, scaledHeight, 141, 166, 24, 24);
							if (effect.getDuration() <= 200) {
								lvt_11_1_ = 10 - effect.getDuration() / 20;
								lvt_10_1_ = MathHelper.clamp((float) effect.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F,
										0.5F)
										+ MathHelper.cos((float) effect.getDuration() * 3.1415927F / 5.0F)
												* MathHelper.clamp((float) lvt_11_1_ / 10.0F * 0.25F, 0.0F, 0.25F);
							}
						}
	
						GlStateManager.color4f(1.0F, 1.0F, 1.0F, lvt_10_1_);
						lvt_11_1_ = lvt_9_1_ % 12;
						int lvt_12_1_ = lvt_9_1_ / 12;
						this.drawTexturedModalRect(scaledWidth + 3, scaledHeight + 3, lvt_11_1_ * 18, 198 + lvt_12_1_ * 18, 18, 18);

						GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
						String var16 = MixinGuiIngame.durationAdecvate(effect);
						int width = mc.fontRenderer.getStringWidth(var16);
						int height = mc.fontRenderer.FONT_HEIGHT;
						drawRect(scaledWidth - width / 2 + 11, scaledHeight - height / 2 + 5, scaledWidth + width / 2 + 13, scaledHeight + height / 2 + 7, 0x55000000);
						mc.fontRenderer.drawString(var16, scaledWidth - width / 2 + 12, scaledHeight - height / 2 + 6, 0xFFFFFF);
						mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
					}
				}
			}
			ci.cancel();
		}
	}
}
