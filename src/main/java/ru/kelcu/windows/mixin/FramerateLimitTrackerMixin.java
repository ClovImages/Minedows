package ru.kelcu.windows.mixin;

import com.mojang.blaze3d.platform.FramerateLimitTracker;
import com.mojang.blaze3d.platform.Monitor;
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
        if(!Windows.config.getBoolean("FIXES.SMOOTH_MENU", false) || AlinLib.MINECRAFT.level != null) return;
        Monitor monitor = AlinLib.MINECRAFT.getWindow().findBestMonitor();
        if(monitor != null) cir.setReturnValue(monitor.getCurrentMode().getRefreshRate());
    }
}
