package ru.kelcu.windows.mixin.components.elemets;

import net.minecraft.client.gui.render.state.ColoredRectangleRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.kelcu.windows.components.Window;
import ru.kelcu.windows.screens.DesktopScreen;

@Mixin(ColoredRectangleRenderState.class)
public class ColoredRectangleRenderStateMixin {
//    @ModifyVariable(method = "<init>(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/gui/render/TextureSetup;Lorg/joml/Matrix3x2f;IIIIIILnet/minecraft/client/gui/navigation/ScreenRectangle;Lnet/minecraft/client/gui/navigation/ScreenRectangle;)V", at = @At("HEAD"), index = 4, argsOnly = true)
//    private static int x0(int value){
//        if(DesktopScreen.currentRenderedWindow != null){
//            Window window = DesktopScreen.currentRenderedWindow;
//            return value + (int) (window.x+3);
//        }
//        return value;
//    }
//    @ModifyVariable(method = "<init>(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/gui/render/TextureSetup;Lorg/joml/Matrix3x2f;IIIIIILnet/minecraft/client/gui/navigation/ScreenRectangle;Lnet/minecraft/client/gui/navigation/ScreenRectangle;)V", at = @At("HEAD"), index = 5, argsOnly = true)
//    private static int y0(int value){
//        if(DesktopScreen.currentRenderedWindow != null){
//            Window window = DesktopScreen.currentRenderedWindow;
//            return value + (int) (window.y+19);
//        }
//        return value;
//    }
//    @ModifyVariable(method = "<init>(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/gui/render/TextureSetup;Lorg/joml/Matrix3x2f;IIIIIILnet/minecraft/client/gui/navigation/ScreenRectangle;Lnet/minecraft/client/gui/navigation/ScreenRectangle;)V", at = @At("HEAD"), index = 6, argsOnly = true)
//    private static int x1(int value){
//        if(DesktopScreen.currentRenderedWindow != null){
//            Window window = DesktopScreen.currentRenderedWindow;
//            return value + (int) (window.x+3);
//        }
//        return value;
//    }
//    @ModifyVariable(method = "<init>(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/gui/render/TextureSetup;Lorg/joml/Matrix3x2f;IIIIIILnet/minecraft/client/gui/navigation/ScreenRectangle;Lnet/minecraft/client/gui/navigation/ScreenRectangle;)V", at = @At("HEAD"), index = 7, argsOnly = true)
//    private static int y1(int value){
//        if(DesktopScreen.currentRenderedWindow != null){
//            Window window = DesktopScreen.currentRenderedWindow;
//            return value + (int) (window.y+19);
//        }
//        return value;
//    }
}
