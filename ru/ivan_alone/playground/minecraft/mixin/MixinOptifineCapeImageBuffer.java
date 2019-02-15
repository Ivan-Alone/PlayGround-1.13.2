package ru.ivan_alone.playground.minecraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import net.optifine.player.CapeImageBuffer;
import ru.ivan_alone.playground.minecraft.util.PGRegistry;
import ru.ivan_alone.playground.minecraft.util.ReflectMirror;

@Mixin(CapeImageBuffer.class)
public class MixinOptifineCapeImageBuffer {
	@Shadow(remap = false)
	private AbstractClientPlayer player;
	@Shadow(remap = false)
	private ResourceLocation resourceLocation;
	
	@SuppressWarnings("unlikely-arg-type")
	public void skinAvailable(CallbackInfo call) {
        if (this.player != null) {
    		if (PGRegistry.FROM_THE_SHADOWS.get(this)) {
    			PGRegistry.CAPES_ELYTRAS.put(this.player.getGameProfile().getName(), this.resourceLocation);
    		}
    		ReflectMirror.call(this.player, AbstractClientPlayer.class, void.class, new String[] {"setLocationOfCape"}, new Class[] {ResourceLocation.class}, new Object[] {this.resourceLocation});
        }
        
        this.player = null;
    }
}
