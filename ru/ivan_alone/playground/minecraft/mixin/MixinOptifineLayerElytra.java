package ru.ivan_alone.playground.minecraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.model.ModelElytra;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import ru.ivan_alone.playground.minecraft.util.PGRegistry;

@Mixin(LayerElytra.class)
public class MixinOptifineLayerElytra {
	@Shadow
	protected RenderLivingBase<?> renderPlayer;
	@Shadow
	private ModelElytra modelElytra;
	
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
		ItemStack itemstack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if (itemstack != null) {
			if (itemstack.getItem() == Items.ELYTRA && entity instanceof AbstractClientPlayer && !hasElytraOwnTexture(itemstack)) {
	            AbstractClientPlayer player = (AbstractClientPlayer)entity;
	            ResourceLocation cloakPGOpti = PGRegistry.CAPES_ELYTRAS.get(player.getName().getString());
				if (cloakPGOpti != null) {
		            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		            GlStateManager.enableBlend();
		            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		            
					this.renderPlayer.bindTexture(cloakPGOpti);
					
		            GlStateManager.pushMatrix();
		            GlStateManager.translatef(0.0F, 0.0F, 0.125F);
		            this.modelElytra.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
		            this.modelElytra.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
	
		            if (itemstack.isEnchanted()) {
		                LayerArmorBase.renderEnchantedGlint(this.renderPlayer, entity, this.modelElytra, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
		            }
	
		            GlStateManager.disableBlend();
		            GlStateManager.popMatrix();
		            
		            ci.cancel();
	            }
			}
		}
    }
	
	private boolean hasElytraOwnTexture(ItemStack itemstack) {
		if (itemstack.getTag() != null && itemstack.getTag().get("display") != null) {
			NBTTagCompound tagCompound = (NBTTagCompound)itemstack.getTag().get("display");
            if (tagCompound.getTagId("Lore") == 9) {
                NBTTagList loreList = tagCompound.getList("Lore", 8);
                for (int i = 0; i < loreList.size(); ++i) {
                	String loreInode = loreList.getString(i);
    				if (loreInode.startsWith("\u00a78tx:") || loreInode.startsWith("\u00a78id:")) {
    					return true;
    				}
                }
            }
		}
		return false;
	}
}
