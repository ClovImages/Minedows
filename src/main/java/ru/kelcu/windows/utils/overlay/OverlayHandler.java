package ru.kelcu.windows.utils.overlay;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.api.events.client.ScreenEvents;
import ru.kelcuprum.alinlib.api.events.client.GuiRenderEvents;
import ru.kelcuprum.alinlib.info.Player;

import java.util.ArrayList;

public class OverlayHandler implements GuiRenderEvents, ScreenEvents.ScreenRender {
    public void render(GuiGraphicsExtractor guiGraphics) {
        boolean activateWindows = (!Player.isLicenseAccount() && !FabricLoader.getInstance().isDevelopmentEnvironment()) || Windows.config.getBoolean("ALWAYS_ACTIVATE_MINECRAFT", false);
        if(!activateWindows) return;
        int width = Math.clamp(Minecraft.getInstance().font.width(Component.translatable("minedows.activate.description")), Minecraft.getInstance().font.width(Component.translatable("minedows.activate")), 150);
        ArrayList<FormattedCharSequence> list = new ArrayList<>();
        list.addAll(Minecraft.getInstance().font.split(Component.translatable("minedows.activate"), width));
        list.addAll(Minecraft.getInstance().font.split(Component.translatable("minedows.activate.description"), width));
        int x = guiGraphics.guiWidth()-10-width;
        int lines = list.size();
        int y = guiGraphics.guiHeight() - 10 - (Minecraft.getInstance().gui.screen() instanceof DesktopScreen ? ((DesktopScreen) Minecraft.getInstance().gui.screen()).taskbarSize : 0) - (Minecraft.getInstance().font.lineHeight*lines+(5*(lines-1)));
        for(FormattedCharSequence formattedCharSequence : list){
            guiGraphics.text(Minecraft.getInstance().font, formattedCharSequence, x, y, 0xCFFFFFFF, false);
            y+=(Minecraft.getInstance().font.lineHeight+5);
        }
    }

    @Override
    public void onScreenRender(Screen screen, GuiGraphicsExtractor guiGraphics, int mx130, int my, float tick) {
        if(DesktopScreen.currentRenderedWindow != null) return;
        if(Minecraft.getInstance().gui.screen() instanceof DesktopScreen || Minecraft.getInstance().level == null) render(guiGraphics);
    }

    @Override
    public void onRender(GuiGraphicsExtractor guiGraphics, float tickDelta) {
        render(guiGraphics);
    }
}
