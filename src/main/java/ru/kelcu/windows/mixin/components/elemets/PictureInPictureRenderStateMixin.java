package ru.kelcu.windows.mixin.components.elemets;

import net.minecraft.client.gui.render.state.pip.GuiEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import ru.kelcu.windows.components.Window;
import ru.kelcu.windows.screens.DesktopScreen;

@Mixin(GuiEntityRenderState.class)
public abstract class PictureInPictureRenderStateMixin {
    @ModifyVariable(method = "<init>(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lorg/joml/Vector3f;Lorg/joml/Quaternionf;Lorg/joml/Quaternionf;IIIIFLnet/minecraft/client/gui/navigation/ScreenRectangle;)V", at = @At("HEAD"), index = 5, argsOnly = true)
    private static int x0(int value){
        if(DesktopScreen.currentRenderedWindow != null){
            Window window = DesktopScreen.currentRenderedWindow;
            return value + (int) (window.x+3);
        }
        return value;
    }
    @ModifyVariable(method = "<init>(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lorg/joml/Vector3f;Lorg/joml/Quaternionf;Lorg/joml/Quaternionf;IIIIFLnet/minecraft/client/gui/navigation/ScreenRectangle;)V", at = @At("HEAD"), index = 6, argsOnly = true)
    private static int y0(int value){
        if(DesktopScreen.currentRenderedWindow != null){
            Window window = DesktopScreen.currentRenderedWindow;
            return value + (int) (window.y+19);
        }
        return value;
    }
    @ModifyVariable(method = "<init>(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lorg/joml/Vector3f;Lorg/joml/Quaternionf;Lorg/joml/Quaternionf;IIIIFLnet/minecraft/client/gui/navigation/ScreenRectangle;)V", at = @At("HEAD"), index = 7, argsOnly = true)
    private static int x1(int value){
        if(DesktopScreen.currentRenderedWindow != null){
            Window window = DesktopScreen.currentRenderedWindow;
            return value + (int) (window.x+3);
        }
        return value;
    }
    @ModifyVariable(method = "<init>(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lorg/joml/Vector3f;Lorg/joml/Quaternionf;Lorg/joml/Quaternionf;IIIIFLnet/minecraft/client/gui/navigation/ScreenRectangle;)V", at = @At("HEAD"), index = 8, argsOnly = true)
    private static int y1(int value){
        if(DesktopScreen.currentRenderedWindow != null){
            Window window = DesktopScreen.currentRenderedWindow;
            return value + (int) (window.y+19);
        }
        return value;
    }
}
