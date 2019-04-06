package ru.ivan_alone.playground.minecraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.network.NetworkManager;
import ru.ivan_alone.playground.minecraft.PGConstants;
import ru.ivan_alone.playground.minecraft.config.PGConfig;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {
	@Shadow
	private boolean isEncrypted;

	/** 
	 * @reason We need replace this method to activate correct working heads in TAB-menu
	 * @author Ivan_Alone
	 */
	@Overwrite
	public boolean isEncrypted() {
		boolean doShowheads = PGConfig.getConfig().getValueBool("a.tablist.showheads");
		boolean doShowheadsNonPG = PGConfig.getConfig().getValueBool("a.tablist.showheads.nonpg");
		
		if (doShowheads) {
			 if (doShowheadsNonPG || PGConstants.isPlayerOnPG((NetworkManager)(Object)this)) {
				 return true;
			 }
		}
		
		return this.isEncrypted;
	}
}
