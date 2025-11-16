package ru.kelcu.windows.utils.overlay;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.openal.AL;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.api.events.client.ScreenEvents;
import ru.kelcuprum.alinlib.api.events.client.GuiRenderEvents;
import ru.kelcuprum.alinlib.info.Player;

import java.util.ArrayList;
import java.util.List;

public class OverlayHandler implements GuiRenderEvents, ScreenEvents.ScreenRender {
    public void render(GuiGraphics guiGraphics) {
        boolean activateWindows = (!Player.isLicenseAccount() && !FabricLoader.getInstance().isDevelopmentEnvironment()) || Windows.config.getBoolean("ALWAYS_ACTIVATE_MINECRAFT", false);
        if(!activateWindows) return;
        int width = Math.min(150, Math.max(AlinLib.MINECRAFT.font.width(Component.translatable("minedows.activate.description")), AlinLib.MINECRAFT.font.width(Component.translatable("minedows.activate"))));
        ArrayList<FormattedCharSequence> list = new ArrayList<>();
        list.addAll(AlinLib.MINECRAFT.font.split(Component.translatable("minedows.activate"), width));
        list.addAll(AlinLib.MINECRAFT.font.split(Component.translatable("minedows.activate.description"), width));
        int x = guiGraphics.guiWidth()-10-width;
        int lines = list.size();
        int y = guiGraphics.guiHeight() - 10 - (AlinLib.MINECRAFT.screen instanceof DesktopScreen ? ((DesktopScreen) AlinLib.MINECRAFT.screen).taskbarSize : 0) - (AlinLib.MINECRAFT.font.lineHeight*lines+(5*(lines-1)));
        for(FormattedCharSequence formattedCharSequence : list){
            guiGraphics.drawString(AlinLib.MINECRAFT.font, formattedCharSequence, x, y, 0xCFFFFFFF, false);
            y+=(AlinLib.MINECRAFT.font.lineHeight+5);
        }
    }

    @Override
    public void onScreenRender(Screen screen, GuiGraphics guiGraphics, int mx130, int my, float tick) {
        if(DesktopScreen.currentRenderedWindow != null) return;
        if(AlinLib.MINECRAFT.screen instanceof DesktopScreen || AlinLib.MINECRAFT.level == null) render(guiGraphics);
    }

    @Override
    public void onRender(GuiGraphics guiGraphics, float tickDelta) {
        render(guiGraphics);
    }
}
