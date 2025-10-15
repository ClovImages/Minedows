package ru.kelcu.windows.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import ru.kelcu.windows.Windows;
import ru.kelcuprum.alinlib.gui.GuiUtils;

import static ru.kelcu.windows.Windows.config;

public class WinColors {
    public static int[] getPerlinColors(){
        return new int[]{config.getNumber("PERLIN.COLOR_START", 0Xff48cae4).intValue(), config.getNumber("PERLIN.COLOR_END", 0xFFf4f4f9).intValue()};
    }
    public static int[] getTitleGradientColors() {
        ThemeManager.Theme theme = ThemeManager.getSelectedTheme();
        return new int[]{theme.startTitle(), theme.endTitle()};
    }
    public static int[] getStartMenuGradient() {
        ThemeManager.Theme theme = ThemeManager.getSelectedTheme();
        return new int[]{theme.startStartMenu(), theme.endStartMenu()};
    }

    public static ResourceLocation getLightIcon(String location){
        return getLightIcon("windows", location);
    }
    public static ResourceLocation getLightIcon(String path, String location){
        ThemeManager.Theme theme = ThemeManager.getSelectedTheme();
        return GuiUtils.getResourceLocation(path, String.format("%s%s.png", location, isLightColor(theme.mainColor()) ? "" : "_l"));
    }

    public static int[] getWindowColors(){
        // Лева первая - e2e0e3 - 226
        // Лева вторая - ffffff - 255
        // База контента - cbcbcb - 203
        // Право первая - 928f92 - 146
        // Право вторая 000808 - 8;
        int[] colors = new int[5];
        ThemeManager.Theme theme = ThemeManager.getSelectedTheme();
        int mainColor = theme.mainColor();
        int r = ARGB.red(mainColor);
        int g = ARGB.green(mainColor);
        int b = ARGB.blue(mainColor);
        colors[0] = ARGB.color(255, Math.min(255, r+23), Math.min(255, g+23), Math.min(255, b+23));
        colors[1] = ARGB.color(255, Math.min(255, r+52), Math.min(255, g+52), Math.min(255, b+52));
        colors[2] = ARGB.color(255, r, g, b);
        colors[3] = ARGB.color(255, Math.max(0, r-57), Math.max(0, g-57), Math.max(0, b-57));
        colors[4] = ARGB.color(255, Math.max(0, r-195), Math.max(0, g-195), Math.max(0, b-195));
        return colors;
    }
    public static int[] getActiveWindowColors(){
        // Лева первая - e2e0e3 - 226
        // Лева вторая - ffffff - 255
        // База контента - cbcbcb - 203
        // Право первая - 928f92 - 146
        // Право вторая 000808 - 8;
        int[] colors = new int[5];
        ThemeManager.Theme theme = ThemeManager.getSelectedTheme();
        int mainColor = theme.activeColor();
        int r = ARGB.red(mainColor);
        int g = ARGB.green(mainColor);
        int b = ARGB.blue(mainColor);
        colors[0] = ARGB.color(255, Math.min(255, r+23), Math.min(255, g+23), Math.min(255, b+23));
        colors[1] = ARGB.color(255, Math.min(255, r+52), Math.min(255, g+52), Math.min(255, b+52));
        colors[2] = ARGB.color(255, r, g, b);
        colors[3] = ARGB.color(255, Math.max(0, r-57), Math.max(0, g-57), Math.max(0, b-57));
        colors[4] = ARGB.color(255, Math.max(0, r-195), Math.max(0, g-195), Math.max(0, b-195));
        return colors;
    }

    public static int[] getHorizontalRuleColors(){
        int[] colors = new int[2];
        ThemeManager.Theme theme = ThemeManager.getSelectedTheme();
        int mainColor = theme.mainColor();
        int r = ARGB.red(mainColor);
        int g = ARGB.green(mainColor);
        int b = ARGB.blue(mainColor);
        // f7f7f7 - 247
        // База контента - cbcbcb - 203
        // 848484 - 132
        colors[0] = ARGB.color(255, Math.min(255, r+44), Math.min(255, g+44), Math.min(255, b+44));
        colors[1] = ARGB.color(255, Math.max(0, r-71), Math.max(0, g-71), Math.max(0, b-71));
        return colors;
    }

    public static int getTextColorWithMainColor(){
        ThemeManager.Theme theme = ThemeManager.getSelectedTheme();
        int mainColor = theme.mainColor();
        return isLightColor(mainColor) ? 0xFF000000 : 0xffF7F7F7;
    }

    public static boolean isLightColor(int rgb){
        int red = (rgb >> 16) & 0xff;
        int green = (rgb >> 8) & 0xff;
        int blue = (rgb) & 0xff;
        boolean rL = checkColor(red, 150, 256);
        boolean gL = checkColor(green, 150, 256);
        boolean bL = checkColor(blue, 150, 256);
        return rL || bL || gL;
    }
    public static boolean checkColor(int color, int min, int max){
        return min < color && color < max;
    }
}
