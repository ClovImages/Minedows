package ru.kelcu.windows.mixin.game_ui.bars;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.contextualbar.LocatorBarRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.kelcu.windows.Windows;

@Mixin(LocatorBarRenderer.class)
public class LocatorBarRendererMixin{
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/contextualbar/LocatorBarRenderer;top(Lcom/mojang/blaze3d/platform/Window;)I"))
    public int render(LocatorBarRenderer instance, Window window){
        if(!Windows.config.getBoolean("ENABLE_NEW_UI", false)) return instance.top(window);
        return 5;
    }
    @Redirect(method = "renderBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/contextualbar/LocatorBarRenderer;top(Lcom/mojang/blaze3d/platform/Window;)I"))
    public int renderBackground(LocatorBarRenderer instance, Window window){
        if(!Windows.config.getBoolean("ENABLE_NEW_UI", false)) return instance.top(window);
        return 5;
    }
}
