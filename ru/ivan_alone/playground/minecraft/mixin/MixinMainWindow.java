package ru.ivan_alone.playground.minecraft.mixin;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Monitor;
import net.minecraft.client.renderer.VirtualScreen;
import ru.ivan_alone.playground.minecraft.PGConstants;

@Mixin(MainWindow.class)
public class MixinMainWindow {
	@Shadow
	private VirtualScreen virtualScreen;
	@Shadow
	private Monitor monitor;

	/** 
	 * @reason Safe setup PlayGround Minecraft window title, checking is MC PG Site down 
	 * @author Ivan_Alone
	 */
	@Overwrite
	private void setMonitorFromVirtualScreen() {
		MainWindow __this__ = (MainWindow)(Object)this;
		GLFW.glfwSetWindowTitle(__this__.getHandle(), (CharSequence)PGConstants.getWindowTitle());
		
		PGConstants.runDownDetector();
		
		this.monitor = virtualScreen.getMonitor(__this__);
	}
}
