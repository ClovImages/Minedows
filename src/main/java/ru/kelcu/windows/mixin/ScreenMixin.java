package ru.kelcu.windows.mixin;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcuprum.alinlib.AlinLib;

import static ru.kelcuprum.alinlib.gui.Colors.BLACK_ALPHA;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow
    protected Font font;

    @Shadow
    public abstract void init(Minecraft minecraft, int i, int j);

    @Shadow
    public int width;

    @Shadow
    public int height;

    @Inject(method = "render", at=@At("RETURN"))
    public void render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci){
//        String debug = String.format("mX: %s | mY: %s", i, j);
//        guiGraphics.drawString(font, debug, 5, 5, 0xFF000000, false);
    }

    @Inject(method = "renderBlurredBackground", at=@At("HEAD"), cancellable = true)
    public void renderBackground(GuiGraphics guiGraphics, CallbackInfo ci){
        if((Screen) (Object) this instanceof DesktopScreen || Minecraft.getInstance().screen instanceof DesktopScreen || DesktopScreen.currentRenderedWindow != null) ci.cancel();
    }
    @Inject(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;)V", at=@At("HEAD"), cancellable = true)
    public void renderMenuBackgroundTexture(GuiGraphics guiGraphics, CallbackInfo ci){
        if((Screen) (Object) this instanceof DesktopScreen) return;
        else if(Minecraft.getInstance().screen instanceof DesktopScreen || DesktopScreen.currentRenderedWindow != null) ci.cancel();
    }
    @Inject(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;IIII)V", at=@At("HEAD"), cancellable = true)
    public void renderMenuBackgroundTexture(GuiGraphics guiGraphics, int i, int j, int k, int l, CallbackInfo ci){
        if((Screen) (Object) this instanceof DesktopScreen) return;
        else if(Minecraft.getInstance().screen instanceof DesktopScreen || DesktopScreen.currentRenderedWindow != null) ci.cancel();
    }
    @Inject(method = "renderPanorama", at=@At("HEAD"), cancellable = true)
    public void renderPanorama(GuiGraphics guiGraphics, float f, CallbackInfo ci){
        if(!((Screen) (Object) this instanceof DesktopScreen) && AlinLib.MINECRAFT.screen instanceof DesktopScreen) ci.cancel();
        if((Minecraft.getInstance().screen instanceof DesktopScreen && Windows.config.getNumber("WALLPAPER.TYPE", 0).intValue() != 2) || DesktopScreen.currentRenderedWindow != null) ci.cancel();
    }
}
