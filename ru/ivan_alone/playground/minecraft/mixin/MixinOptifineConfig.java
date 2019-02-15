package ru.ivan_alone.playground.minecraft.mixin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackInfoClient;
import net.minecraft.resources.FilePack;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackList;
import ru.ivan_alone.playground.minecraft.PGConstants;

@Mixin(targets = {"Config"}, remap = false)
public class MixinOptifineConfig {
	/** 
	 * @reason We want modify Optifine resourcepack loader to inject this mod into resourcepath
	 * @author Ivan_Alone
	 */
	@Overwrite(remap = false)
    public static IResourcePack[] getResourcePacks()
    {
        ResourcePackList<ResourcePackInfoClient> resourcepacklist = Minecraft.getInstance().getResourcePackList();
        Collection<ResourcePackInfoClient> collection = resourcepacklist.getPackInfos();
        List<IResourcePack> list = new ArrayList<IResourcePack>();

        for (ResourcePackInfoClient resourcepackinfoclient : collection) {
            IResourcePack iresourcepack = resourcepackinfoclient.getResourcePack();

            if (iresourcepack != Minecraft.getInstance().getPackFinder().getVanillaPack()) {
                list.add(iresourcepack);
            }
        }
        
		String path = new File(PGConstants.class.getResource(PGConstants.pgModInfoFilename).getFile()).getParent();
		
		if (path.charAt(path.length()-1) == '!') {
			path = path.substring(0, path.length()-1);
		}
		
		final String invalidPrefix = "file:";
		
		if (path.startsWith(invalidPrefix)) {
			path = path.substring(invalidPrefix.length());
		}
		
        list.add(new FilePack(new File(path)));

        IResourcePack[] airesourcepack = (IResourcePack[])list.toArray(new IResourcePack[list.size()]);
        return airesourcepack;
    }
}
