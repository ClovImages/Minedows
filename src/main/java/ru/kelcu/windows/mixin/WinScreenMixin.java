package ru.kelcu.windows.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcu.windows.screens.DesktopScreen;

@Mixin(WinScreen.class)
public class WinScreenMixin extends Screen{
    protected WinScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "renderBackground", at=@At("HEAD"), cancellable = true)
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci){
        if(Minecraft.getInstance().screen instanceof DesktopScreen){
            TextureManager textureManager = Minecraft.getInstance().getTextureManager();
            TextureSetup textureSetup = TextureSetup.doubleTexture(textureManager.getTexture(TheEndPortalRenderer.END_SKY_LOCATION).getTextureView(), textureManager.getTexture(TheEndPortalRenderer.END_PORTAL_LOCATION).getTextureView());
            guiGraphics.fill(RenderPipelines.END_PORTAL, textureSetup, 0, 0, this.width, this.height);
            ci.cancel();
        }
    }
}
