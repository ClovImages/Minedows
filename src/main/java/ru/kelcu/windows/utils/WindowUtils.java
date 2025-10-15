package ru.kelcu.windows.utils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import ru.kelcu.windows.components.builders.WindowBuilder;

public class WindowUtils {
    public void renderHorizontalRule(GuiGraphics guiGraphics, int x, int y, int x1){
        int[] colors = WinColors.getHorizontalRuleColors();
        guiGraphics.fill(x, y, x1, y+2, colors[0]);
        guiGraphics.fill(x, y, x1, y+1, colors[1]);
    }

    public static WindowBuilder getBuilderByScreen(Screen screen){
        if(screen instanceof DeathScreen) return new WindowBuilder().setSize(325, 80).setResizable(false).setButtons(2).setScreen(screen);
        return new WindowBuilder().setScreen(screen);
    }

    public static void welcomeToWhiteSpace(GuiGraphics guiGraphics, int x, int y, int width, int height){
        int[] colors = WinColors.getWindowColors();
        guiGraphics.fill(x, y, x+width, y+height, colors[4]);
        guiGraphics.fill(x+1, y+1, x+width, y+height, colors[0]);
        guiGraphics.fill(x+1, y+1, x+width-1, y+height-1, 0xFFFFFFFF);
    }

    public static void renderPanel(GuiGraphics guiGraphics, int x, int y, int x1, int y1){
        renderPanel(guiGraphics, x, y, x1, y1, false);
    }
    public static void renderPanel(GuiGraphics guiGraphics, int x, int y, int x1, int y1, boolean active){
        int width = x1-x;
        int height = y1-y;
        int[] colors = active ? WinColors.getActiveWindowColors() : WinColors.getWindowColors();
        guiGraphics.fill(x, y, x + width, y + height, colors[0]); // L
        guiGraphics.fill(x + 1, y + 1, x + width, y + height, colors[4]); // R
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, colors[1]); // L
        guiGraphics.fill(x + 2, y + 2, x + width - 1, y + height - 1, colors[3]); // R
        guiGraphics.fill(x + 2, y + 2, x + width - 2, y + height - 2, colors[2]); // C
    }
    public static void renderRevertPanel(GuiGraphics guiGraphics, int x, int y, int x1, int y1){
        int width = x1-x;
        int height = y1-y;
        int[] colors = WinColors.getWindowColors();
        guiGraphics.fill(x, y, x + width, y + height, colors[3]); // L
        guiGraphics.fill(x + 1, y + 1, x + width, y + height, colors[1]); // R
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, colors[4]); // L
        guiGraphics.fill(x + 2, y + 2, x + width - 1, y + height - 1, colors[0]); // R
        guiGraphics.fill(x + 2, y + 2, x + width - 2, y + height - 2, colors[2]); // C
    }
}
