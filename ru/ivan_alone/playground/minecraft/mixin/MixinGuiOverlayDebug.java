package ru.ivan_alone.playground.minecraft.mixin;

import java.util.List;
import java.util.Locale;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.chunk.Chunk;
import ru.ivan_alone.playground.minecraft.PGConstants;
import ru.ivan_alone.playground.minecraft.config.PGConfig;

@Mixin(GuiOverlayDebug.class)
public abstract class MixinGuiOverlayDebug extends Gui {
	@Shadow
	private RayTraceResult rayTraceBlock;
	@Shadow
	private RayTraceResult rayTraceFluid;
	@Shadow
	abstract void renderLagometer();
	
	private long bytesToMb2(long bytes) {
		return bytes / 1024L / 1024L;
	}

	private void renderDebugOnLeft(boolean drawAdditional) {
		Minecraft mc = Minecraft.getInstance();

        Entity entity = mc.getRenderViewEntity();
        EnumFacing lvt_8_1_ = entity.getHorizontalFacing();
        String lvt_9_1_ = "Invalid";
        switch(lvt_8_1_) {
        	default:
	        case NORTH:
	            lvt_9_1_ = "Z-";
	            break;
	        case SOUTH:
	            lvt_9_1_ = "Z+";
	            break;
	        case WEST:
	            lvt_9_1_ = "X-";
	            break;
	        case EAST:
	            lvt_9_1_ = "X+";
        }

        BlockPos blockPos = new BlockPos(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().getBoundingBox().minY, mc.getRenderViewEntity().posZ);
        
		String[] data = mc.debug.split(" ");
		String[] debugMC = new String[] {data[0], data[2].split("\\(")[1]};
        
        List<String> list = Lists.newArrayList(new String[]{
        	PGConstants.getMinecraftTitle(), 
			debugMC[0] + " " + I18n.format("debugpg.left.fps", new Object[0]) + (drawAdditional ? (" (" + debugMC[1] + " " + I18n.format("debugpg.left.chunkupdates", new Object[0]) + ")") : ""), 
		});
        
        list.add(I18n.format("debugpg.left.renderdistance", new Object[0]) + ": " + mc.worldRenderer.getDebugInfoRenders().split("D:")[1].split(",")[0].trim());
        list.add(I18n.format("debugpg.left.entities", new Object[0]) + ": " + mc.worldRenderer.getDebugInfoEntities().split("E:")[1].split(",")[0].trim());
        list.add(I18n.format("debugpg.left.particles", new Object[0]) + ": " + mc.particles.getStatistics());
        list.add("");
        list.add(I18n.format("debugpg.left.x", new Object[0]) + String.format(Locale.ROOT, ": %.2f", mc.getRenderViewEntity().posX));
        list.add(I18n.format("debugpg.left.y", new Object[0]) + String.format(Locale.ROOT, ": %.2f", mc.getRenderViewEntity().getBoundingBox().minY));
        list.add(I18n.format("debugpg.left.z", new Object[0]) + String.format(Locale.ROOT, ": %.2f", mc.getRenderViewEntity().posZ));
        if (drawAdditional) {
        	list.add(String.format(I18n.format("debugpg.left.chunk", new Object[0]) + ": %d %d %d " + I18n.format("debugpg.left.chunkAt", new Object[0]) + " %d %d %d", blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15, blockPos.getX() >> 4, blockPos.getY() >> 4, blockPos.getZ() >> 4));
        }
        list.add(I18n.format("debugpg.left.facing", new Object[0]) + String.format(Locale.ROOT, ": %s (%s)", I18n.format("pg.facing." + lvt_8_1_.toString(), new Object[0]), lvt_9_1_));
        if (drawAdditional) {
        	list.add(String.format(Locale.ROOT, I18n.format("debugpg.left.angles", new Object[0]) + ": %.1f" + I18n.format("debugpg.left.angles.pitch", new Object[0]) + "%.1f" + I18n.format("debugpg.left.angles.yaw", new Object[0]), MathHelper.wrapDegrees(entity.rotationPitch), MathHelper.wrapDegrees(entity.rotationYaw)));
        }
	
        
        
		if (mc.world != null) {
			Chunk chunk = mc.world.getChunk(blockPos);
			if (mc.world.isBlockLoaded(blockPos) && blockPos.getY() >= 0 && blockPos.getY() < 256) {
				if (!chunk.isEmpty()) {
					String biomeTranslationKey = "pg.biomeapi." + Integer.toString(IRegistry.BIOME.getId(chunk.getBiome(blockPos))) + ".name";
					String translation = I18n.format(biomeTranslationKey, new Object[0]);
					String biome = I18n.format("debugpg.left.biome", new Object[0]) + ": " + (!translation.equals(biomeTranslationKey) ? translation : IRegistry.BIOME.getKey(chunk.getBiome(blockPos))).toString();
					list.add(biome);
					list.add(I18n.format("debugpg.left.light", new Object[0]) + ": " + chunk.getLightSubtracted(blockPos, 0, chunk.getWorld().dimension.hasSkyLight()) + " (" + chunk.getLight(EnumLightType.SKY, blockPos, chunk.getWorld().dimension.hasSkyLight()) + " " + I18n.format("debugpg.left.light.sky", new Object[0]) + ", " + chunk.getLight(EnumLightType.BLOCK, blockPos, chunk.getWorld().dimension.hasSkyLight()) + " " + I18n.format("debugpg.left.light.block", new Object[0]) + ")");
					list.add(String.format(Locale.ROOT, I18n.format("debugpg.left.day", new Object[0]) + ": %d", mc.world.getDayTime() / 24000L));
				} else {
					list.add(I18n.format("debugpg.left.waitchunk", new Object[0]));
				}
			} else {
				list.add(I18n.format("debugpg.left.outside", new Object[0]));
			}
		}

        BlockPos blockPos2;
        
    	if (this.rayTraceBlock != null && this.rayTraceBlock.type == Type.BLOCK) {
        	blockPos2 = this.rayTraceBlock.getBlockPos();
            list.add(String.format(I18n.format("debugpg.left.looking", new Object[0]) + ": %d %d %d", blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()));
        }

    	
        if (this.rayTraceFluid != null && this.rayTraceFluid.type == Type.BLOCK && (this.rayTraceBlock == null || !(this.rayTraceFluid.getBlockPos().equals(this.rayTraceBlock.getBlockPos())))) {
        	blockPos2 = this.rayTraceFluid.getBlockPos();
            list.add(String.format(I18n.format("debugpg.left.lookingLiquid", new Object[0]) + ": %d %d %d", blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()));
        }
        
		list.add("");

		if (drawAdditional) {
			list.add(I18n.format("debugpg.left.debug", new Object[0]) + ": ");
			list.add(I18n.format("debugpg.left.debug.pie", new Object[0]) + ": " + (mc.gameSettings.showDebugProfilerChart ? I18n.format("debugpg.left.debug.visible", new Object[0]) : I18n.format("debugpg.left.debug.hidden", new Object[0])));
			list.add(I18n.format("debugpg.left.debug.fps", new Object[0]) + ": " + (mc.gameSettings.showLagometer ? I18n.format("debugpg.left.debug.visible", new Object[0]) : I18n.format("debugpg.left.debug.hidden", new Object[0])));
		}
		list.add(I18n.format("debugpg.left.help", new Object[0]));
		
        
		int i = 0;
		for (String text : list) {
			if (!Strings.isNullOrEmpty(text)) {
				int _1 = mc.fontRenderer.FONT_HEIGHT;
				int _2 = mc.fontRenderer.getStringWidth(text);
				int _3 = 2 + _1 * i;
				drawRect(1, _3 - 1, 2 + _2 + 1, _3 + _1 - 1, -1873784752);
				mc.fontRenderer.drawString(text, 2.0F, (float) _3, 0xE0E0E0);
			}
			i++;
		}

	}

	private void renderDebugOnRight() {
		Minecraft mc = Minecraft.getInstance();
		
		long maxMemory = Runtime.getRuntime().maxMemory();
		long totalMemory = Runtime.getRuntime().totalMemory();
		long freeMemory = Runtime.getRuntime().freeMemory();
		long busyMemory = totalMemory - freeMemory;
		
		int i = 0;
		for (String text : new String[]{
			String.format(I18n.format("debugpg.right.javaver", new Object[0]) + ": %s %s", System.getProperty("java.version"), I18n.format("debugpg.right.java" + Integer.toString(mc.isJava64bit() ? 64 : 32), new Object[0])),
			String.format(I18n.format("debugpg.right.memory", new Object[0]) + ": % 2d%% %03d/%03dMB", busyMemory * 100L / maxMemory, bytesToMb2(busyMemory), bytesToMb2(maxMemory)),
			String.format(I18n.format("debugpg.right.allocated", new Object[0]) + ": % 2d%% %03dMB", totalMemory * 100L / maxMemory, bytesToMb2(totalMemory)), 
			"",
			String.format(I18n.format("debugpg.right.cpu", new Object[0]) + ": %s", OpenGlHelper.getCpu()), 
			"",
			String.format(I18n.format("debugpg.right.display", new Object[0]) + ": %dx%d (%s)", mc.mainWindow.getFramebufferWidth(), mc.mainWindow.getFramebufferHeight(), GlStateManager.getString(GL11.GL_VENDOR)),
			GlStateManager.getString(GL11.GL_RENDERER), GlStateManager.getString(GL11.GL_VERSION)
		}) {
			if (!Strings.isNullOrEmpty(text)) {
				int _1 = mc.fontRenderer.FONT_HEIGHT;
				int _2 = mc.fontRenderer.getStringWidth(text);
				int _3 = mc.mainWindow.getScaledWidth() - 2 - _2;
				int _4 = 2 + _1 * i;
				drawRect(_3 - 1, _4 - 1, _3 + _2 + 1, _4 + _1 - 1, -1873784752);
				mc.fontRenderer.drawString(text, (float) _3, (float) _4, 14737632);
			}
			i++;
		}
	}

	
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void render(CallbackInfo ci) {
		
		if (PGConfig.getConfig().getValueBool("a.debug.pgactive")) {
			Minecraft mc = Minecraft.getInstance();
			
			Entity entity = mc.getRenderViewEntity();
			
			this.rayTraceBlock = entity.rayTrace(20.0D, 0.0F, RayTraceFluidMode.NEVER);
			this.rayTraceFluid = entity.rayTrace(20.0D, 0.0F, RayTraceFluidMode.ALWAYS);
			
			mc.profiler.startSection("debug");
			GlStateManager.pushMatrix();
			
			renderDebugOnLeft(PGConfig.getConfig().getValueBool("a.debug.pgadvance"));
			renderDebugOnRight();
			
			GlStateManager.popMatrix();
			
			if (mc.gameSettings.showLagometer) {
				this.renderLagometer();
			}
			
			mc.profiler.endSection();
			
			ci.cancel();
		}
	}
}
