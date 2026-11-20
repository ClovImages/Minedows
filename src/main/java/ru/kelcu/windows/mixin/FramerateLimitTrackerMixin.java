package ru.kelcu.windows.mixin;

import com.mojang.blaze3d.platform.FramerateLimitTracker;
import com.mojang.blaze3d.platform.Monitor;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.kelcu.windows.Windows;
import ru.kelcuprum.alinlib.AlinLib;

@Mixin(FramerateLimitTracker.class)
public class FramerateLimitTrackerMixin {
    @Inject(method = "getFramerateLimit", at = @At("HEAD"), cancellable = true)
    protected void getFramerateLimit(CallbackInfoReturnable<Integer> cir) {
        if(!Windows.config.getBoolean("FIXES.SMOOTH_MENU", false) || Minecraft.getInstance().level != null) return;
        Monitor monitor = Minecraft.getInstance().getWindow().findBestMonitor();
        if(monitor != null) cir.setReturnValue((int) monitor.currentMode().getRefreshRate());
    }
}
