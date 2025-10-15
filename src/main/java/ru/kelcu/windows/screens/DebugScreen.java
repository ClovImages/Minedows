package ru.kelcu.windows.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;

public class DebugScreen extends Screen {
    protected DebugScreen() {
        super(Component.empty());
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        TextureSetup textureSetup = TextureSetup.doubleTexture(textureManager.getTexture(TheEndPortalRenderer.END_SKY_LOCATION).getTextureView(), textureManager.getTexture(TheEndPortalRenderer.END_PORTAL_LOCATION).getTextureView());
        guiGraphics.fill(RenderPipelines.END_PORTAL, textureSetup, 0, 0, this.width, this.height);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        guiGraphics.fill(i-1, j-1, i+1, j+1, 0xFFFF0000);
        int y = 5;
        guiGraphics.drawString(font, String.format("Мышь X: %s | Мышь Y: %s", i, j), 5, y, 0xFFFFffff, false);
        y+=10;
        guiGraphics.drawString(font, AlinLib.localization.getParsedText("{minecraft.fps}FPS"), 5, y, 0xFFFFffff, false);
        y+=10;
        assert this.minecraft != null;
        assert this.minecraft.screen != null;
        guiGraphics.drawString(font, String.format("Текущий скрин: %s", this.minecraft.screen.getTitle().getString().isBlank() ? this.minecraft.screen.getClass().getCanonicalName() : this.minecraft.screen.getTitle().getString()), 5, y, 0xFFFFffff, false);
    }
}
