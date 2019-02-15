package ru.ivan_alone.playground.minecraft.mixin;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketJoinGame;
import ru.ivan_alone.playground.minecraft.PGConstants;
import ru.ivan_alone.playground.minecraft.config.PGConfig;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
	@Shadow
	private NetworkManager netManager;
	
	@Inject(method = "handleJoinGame(Lnet/minecraft/network/play/server/SPacketJoinGame;)V", at = @At("RETURN"))
	public void handleJoinGame(SPacketJoinGame packetIn, CallbackInfo ci) {
		String password = PGConfig.getConfig().getValue("c.authme.password").trim();
		
		if (this.netManager.getRemoteAddress().toString().startsWith(PGConstants.playGroundAddressIP.split(":")[0])) {
			if (!password.equals("")) {
				LogManager.getLogger().info("Permitted autologin in PlayGround Minecraft");
				Minecraft.getInstance().player.sendChatMessage("/login " + password);
			}
		}
	}
}
