package ru.kelcu.windows.mixin.figura;

import org.figuramc.figura.utils.ui.UIHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.kelcu.windows.components.Window;
import ru.kelcu.windows.screens.DesktopScreen;

@Mixin(UIHelper.class)
public class UIHelperMixin {
    @ModifyVariable(method = "renderBackgroundTexture", at = @At("HEAD"), index = 2, argsOnly = true)
    private static float x0(float value) {
        if (DesktopScreen.currentRenderedWindow != null) {
            Window window = DesktopScreen.currentRenderedWindow;
            return value + (int) (window.x + 3);
        }
        return value;
    }

    @ModifyVariable(method = "renderBackgroundTexture", at = @At("HEAD"), index = 3, argsOnly = true)
    private static float y0(float value) {
        if (DesktopScreen.currentRenderedWindow != null) {
            Window window = DesktopScreen.currentRenderedWindow;
            return value + (int) (window.y + 19);
        }
        return value;
    }

    @ModifyVariable(method = "renderHalfTexture(Lnet/minecraft/client/gui/GuiGraphics;IIIIFFIIIILnet/minecraft/resources/ResourceLocation;)V", at = @At("HEAD"), index = 2, argsOnly = true)
    private static int x0renderSprite(int value) {
        if (DesktopScreen.currentRenderedWindow != null) {
            Window window = DesktopScreen.currentRenderedWindow;
            return value + (int) (window.x + 3);
        }
        return value;
    }

    @ModifyVariable(method = "renderHalfTexture(Lnet/minecraft/client/gui/GuiGraphics;IIIIFFIIIILnet/minecraft/resources/ResourceLocation;)V", at = @At("HEAD"), index = 3, argsOnly = true)
    private static int y0renderSprite(int value) {
        if (DesktopScreen.currentRenderedWindow != null) {
            Window window = DesktopScreen.currentRenderedWindow;
            return value + (int) (window.y + 19);
        }
        return value;
    }

    @ModifyVariable(method = "blitSliced(Lnet/minecraft/client/gui/GuiGraphics;IIIIFFIIIILnet/minecraft/resources/ResourceLocation;)V", at = @At("HEAD"), index = 1, argsOnly = true)
    private static int x0blitSliced(int value) {
        if (DesktopScreen.currentRenderedWindow != null) {
            Window window = DesktopScreen.currentRenderedWindow;
            return value + (int) (window.x + 3);
        }
        return value;
    }

    @ModifyVariable(method = "blitSliced(Lnet/minecraft/client/gui/GuiGraphics;IIIIFFIIIILnet/minecraft/resources/ResourceLocation;)V", at = @At("HEAD"), index = 2, argsOnly = true)
    private static int y0blitSliced(int value) {
        if (DesktopScreen.currentRenderedWindow != null) {
            Window window = DesktopScreen.currentRenderedWindow;
            return value + (int) (window.y + 19);
        }
        return value;
    }
}
