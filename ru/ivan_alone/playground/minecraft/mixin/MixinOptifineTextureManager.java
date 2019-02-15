package ru.ivan_alone.playground.minecraft.mixin;

import java.io.InputStream;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import ru.ivan_alone.playground.minecraft.PGConstants;

@Mixin(TextureManager.class)
public class MixinOptifineTextureManager {
	@Shadow
	private Map<String, Integer> mapTextureCounters;
	
	@Inject(method = "getDynamicTextureLocation", at = @At("HEAD"), cancellable = true)
	public void getDynamicTextureLocation(String name, DynamicTexture texture, CallbackInfoReturnable<ResourceLocation> ci) {
		if (name.equals("logo")) {
			texture = getMojangLogoTexturePGOverride(texture);
			
			Integer integer = this.mapTextureCounters.get(name);

			if (integer == null) {
				integer = 1;
			} else {
				integer = integer + 1;
			}

			this.mapTextureCounters.put(name, integer);
			
			ResourceLocation resourcelocation = new ResourceLocation(String.format("dynamic/%s_%d", name, integer));
			TextureManager.class.cast(this).loadTexture(resourcelocation, texture);
			
			ci.setReturnValue(resourcelocation);

			ci.cancel();
		}
	}
	
	private static DynamicTexture getMojangLogoTexturePGOverride(DynamicTexture texDefault) {
		try {
			IResource iresource = Minecraft.getInstance().getResourceManager().getResource(PGConstants.logoPlayGround);
			InputStream inputstream = iresource == null ? null : iresource.getInputStream();

			if (inputstream == null) {
				return texDefault;
			} else {
				NativeImage nativeimage = NativeImage.read(inputstream);

				if (nativeimage == null) {
					return texDefault;
				} else {
					DynamicTexture dynamictexture = new DynamicTexture(nativeimage);
					return dynamictexture;
				}
			}
		} catch (Exception exception) {
			return texDefault;
		}

	}
}
