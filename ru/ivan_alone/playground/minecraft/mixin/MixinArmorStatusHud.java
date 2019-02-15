package ru.ivan_alone.playground.minecraft.mixin;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import ru.ivan_alone.playground.minecraft.armorstatushud.HUDConfig;
import ru.ivan_alone.playground.minecraft.armorstatushud.HUDLocation;

@Mixin(GuiIngame.class)
public class MixinArmorStatusHud extends Gui {
	
	@Inject(method = "renderHotbar", at = @At("RETURN"))
	protected void renderHotbar(float p_renderGameOverlay_1_, CallbackInfo ci) {
		if (!HUDConfig.enableArmorStatusHUD) return;

		Minecraft mc = Minecraft.getInstance();
		int scaledWidth = mc.mainWindow.getScaledWidth();
		int scaledHeight = mc.mainWindow.getScaledHeight();

		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
		RenderHelper.enableGUIStandardItemLighting();

		NonNullList<ItemStack> armorInventory = HUDLocation.invert(mc.player.inventory.armorInventory);
		
		int right_shift = 1;
		if (HUDConfig.location == HUDLocation.RIGHT_HORIZONTAL) {
			int offreckoning = 1;
			for (ItemStack is : armorInventory) { 
				if (!(is.getCount() > 0)) continue;
				offreckoning += 17 + (!(is.isDamageable() && HUDConfig.renderTextDamage) ? 0 : mc.fontRenderer.getStringWidth(Integer.toString(is.getMaxDamage() - is.getDamage())) + 2);
			}
			right_shift = scaledWidth - offreckoning;
		}
		
		int size = armorInventory.size();
		int i = size-1;
		int last = right_shift;
		
		for (ItemStack is : armorInventory) {
			ItemStack iss = is.copy();

			if (iss.getCount() > 0 || HUDConfig.location == HUDLocation.LEFT || HUDConfig.location == HUDLocation.RIGHT) {
				if (iss.getCount() > 0) {
					iss.setCount(1);
				}
				String text = null;
				if (iss.isDamageable() && HUDConfig.renderTextDamage) {
					text = Integer.toString(iss.getMaxDamage() - iss.getDamage());
				}
				int x, y, xT, yT;
				switch (HUDConfig.location) {
					case LEFT:
					default:
						x = 1;
						y = (scaledHeight - 18) - i * 16;
						xT = x + 17;
						yT = y + (16 - mc.fontRenderer.FONT_HEIGHT) / 2 + 1;
						break;
					case RIGHT:
						x = scaledWidth - 17;
						y = (scaledHeight - 18) - i * 16;
						xT = x - 1 - (text == null ? 0 : mc.fontRenderer.getStringWidth(text));
						yT = y + (16 - mc.fontRenderer.FONT_HEIGHT) / 2 + 1;
						break;
					case LEFT_HORIZONTAL:
					case RIGHT_HORIZONTAL:
						x = last;
						last += 17 + (text == null ? 0 : (mc.fontRenderer.getStringWidth(text) + 2));
						y = scaledHeight - 18;
						xT = x + 18;
						yT = y + (16 - mc.fontRenderer.FONT_HEIGHT) / 2 + 1;
						break;
				}
				i--;
				final float min = (HUDConfig.location == HUDLocation.LEFT || HUDConfig.location == HUDLocation.RIGHT ? -1 : 1) * 200F;
				float zLvl = this.zLevel;
				this.zLevel = min;
				mc.getItemRenderer().zLevel = min;
				
				GlStateManager.pushMatrix();
				GlStateManager.translatef(0.0F, 0.0F, min);
				GlStateManager.depthFunc(GL11.GL_LESS);
				
				mc.getItemRenderer().renderItemAndEffectIntoGUI(mc.player, iss, x, y);
				mc.getItemRenderer().renderItemOverlays(mc.fontRenderer, iss, x, y);
				
				GlStateManager.depthFunc(GL11.GL_LEQUAL);
				GlStateManager.popMatrix();
				
				mc.getItemRenderer().zLevel = 0.0F;
				this.zLevel = zLvl;
				
				if (text != null) {
					GlStateManager.disableLighting();
					GlStateManager.disableDepthTest();
					GlStateManager.disableBlend();
					mc.fontRenderer.drawStringWithShadow(text, (float) (xT), (float) (yT), 0xFFFFFF);
					GlStateManager.enableBlend();
					GlStateManager.enableLighting();
					GlStateManager.enableDepthTest();
				}
			}
		}
		
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
	}
}
