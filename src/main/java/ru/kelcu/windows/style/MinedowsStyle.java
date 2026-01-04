package ru.kelcu.windows.style;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcu.windows.utils.WinColors;
import ru.kelcu.windows.utils.WindowUtils;
import ru.kelcuprum.alinlib.gui.Colors;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.styles.AbstractStyle;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;

import static ru.kelcuprum.alinlib.gui.GuiUtils.interpolate;

public class MinedowsStyle extends AbstractStyle {
    public MinedowsStyle() {
        super("minedows", Component.translatable("minedows.winver.name", FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion().getFriendlyString()));
    }

    @Override
    public void renderBackground$widget(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean active, boolean isHoveredOrFocused) {
        WindowUtils.renderPanel(guiGraphics, x, y, x+width, y+height, isHoveredOrFocused);
    }

    @Override
    public void renderBackground$editbox(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean active, boolean isHoveredOrFocused){
        WindowUtils.welcomeToWhiteSpace(guiGraphics, x, y, width, height);
    }

    @Override
    public void renderBackground$slider(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean active, boolean isHoveredOrFocused, double position) {
        int color = 0xff717171;
        int colorb = isHoveredOrFocused ? Colors.getWinColor() : 0xFFC0C0C0;

        int color1 = 0x20000000;
        int color2 = 0x3F000000;
        int color3 = 0x7F000000;
        int color4 = 0xF5000000;

        // light
        // dark light
        // dark
        // very dark
        guiGraphics.fill(x, y, x+width, y+height, color);
        //
        guiGraphics.fill(x, y+1, x+width-1, y+2, color3);
        guiGraphics.fill(x, y+2, x+1, y+height-1, color3);
        //
        guiGraphics.fill(x+1, y+2, x+width-2, y+height-2, color2);
        //
        guiGraphics.fill(x+width-2, y+2, x+width-1, y+height-2, color1);
        guiGraphics.fill(x+1, y+height-2, x+width-1, y+height-1, color1);
        //
        guiGraphics.fill(x-1, y, x, y+height, color4);
        guiGraphics.fill(x, y+1, x+width, y, color4);
//        if(isHoveredOrFocused){
        int widthS = 6;
        int xS = x + (int)(position * (double)(width - widthS));
        guiGraphics.fill(xS, y, xS+widthS, y+height, colorb);
        //
        guiGraphics.fill(xS+1, y+1, xS+widthS-2, y+2, color1);
        guiGraphics.fill(xS+1, y+2, xS+2, y+height-1, color1);
        //
        guiGraphics.fill(xS+2, y+2, xS+widthS-2, y+height-2, color2);
        //
        guiGraphics.fill(xS+widthS-2, y+1, xS+widthS-1, y+height-2, color3);
        guiGraphics.fill(xS+1, y+height-2, xS+widthS-1, y+height-1, color3);
        //
        guiGraphics.fill(xS+widthS-1, y, xS+widthS, y+height-1, color4);
        guiGraphics.fill(xS, y+height-1, xS+widthS, y+height, color4);
//        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        WindowUtils.renderPanel(guiGraphics, x, y, width, height);
    }
    @Override
    public void renderTitleBackground(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        int factWidth = width-x;
        int[] colors = WinColors.getTitleGradientColors();
        for(int i = 0; i<factWidth; i++){
            guiGraphics.fill(x+i, y, x+1+i, height, interpolate(colors[0], colors[1], (float) i /factWidth));
        }
        int color1 = 0x20000000;
        int color2 = 0x3F000000;
        int color3 = 0x7F000000;
        int color4 = 0xF5000000;
        guiGraphics.fill(x+1, y+1, width-2, y+2, color1);
        guiGraphics.fill(x+1, y+2, x+2, height-1, color1);
        //
        guiGraphics.fill(x+2, y+2, width-2, height-2, color2);
        //
        guiGraphics.fill(width-2, y+1, width-1, height-2, color3);
        guiGraphics.fill(x+1, height-2, width-1, height-1, color3);
        //
        guiGraphics.fill(width-1, y, width, height-1, color4);
        guiGraphics.fill(x, height-1, width, height, color4);
    }

    @Override
    public void renderToastBackground(ToastBuilder toastBuilder, GuiGraphics guiGraphics, int x, int y, int width, int height, double timeline) {
        y+=1; height-=1; width-=2;
        guiGraphics.fill(x+1, y+1, x-1+width, y-1+height, 0xFFffffe1);
        guiGraphics.fill(x+1, y+1, x+width+1, y+3, 0xFFffffe1);
        //tochki
        guiGraphics.fill(x+1, y+1, x+2, y+2, 0xFF000000);
        guiGraphics.fill(x+1+width, y+1, x+2+width, y+2, 0xFF000000);
        guiGraphics.fill(x+width, y+2, x+1+width, y+3, 0xFF000000);

        guiGraphics.fill(x+1, y-1+height, x+2, y-2+height, 0xFF000000);
        guiGraphics.fill(x-1+width, y-1+height, x-2+width, y-2+height, 0xFF000000);
        // poloski
        guiGraphics.fill(x+2, y, x+2+width, y+1, 0xFF000000);
        guiGraphics.fill(x+2, y+height, x-2+width, y-1+height, 0xFF000000);
        guiGraphics.fill(x, y+2, x+1, y-2+height, 0xFF000000);
        guiGraphics.fill(x+width, y+3, x-1+width, y-2+height, 0xFF000000);
    }

    @Override
    public void renderBlockquoteBackground(TextBuilder builder, GuiGraphics guiGraphics, int x, int y, int width, int height, int[] colors) {
        width-=2;
        guiGraphics.fill(x+1, y+1, x-1+width, y-1+height, 0xFFffffe1);
        guiGraphics.fill(x+1, y+1, x+width+1, y+3, 0xFFffffe1);
        //tochki
        guiGraphics.fill(x+1, y+1, x+2, y+2, 0xFF000000);
        guiGraphics.fill(x+1+width, y+1, x+2+width, y+2, 0xFF000000);
        guiGraphics.fill(x+width, y+2, x+1+width, y+3, 0xFF000000);

        guiGraphics.fill(x+1, y-1+height, x+2, y-2+height, 0xFF000000);
        guiGraphics.fill(x-1+width, y-1+height, x-2+width, y-2+height, 0xFF000000);
        // poloski
        guiGraphics.fill(x+2, y, x+2+width, y+1, 0xFF000000);
        guiGraphics.fill(x+2, y+height, x-2+width, y-1+height, 0xFF000000);
        guiGraphics.fill(x, y+2, x+1, y-2+height, 0xFF000000);
        guiGraphics.fill(x+width, y+3, x-1+width, y-2+height, 0xFF000000);
    }

    public void renderTooltip(GuiGraphics guiGraphics, int x, int y, int width, int height){
        guiGraphics.fill(x+1, y+1, x-1+width, y-1+height, 0xFFffffe1);
        //tochki
        guiGraphics.fill(x+1, y+1, x+2, y+2, 0xFF000000);
        guiGraphics.fill(x-1+width, y+1, x-2+width, y+2, 0xFF000000);
//        guiGraphics.fill(x+width, y+2, x+1+width, y+3, 0xFF000000);

        guiGraphics.fill(x+1, y-1+height, x+2, y-2+height, 0xFF000000);
        guiGraphics.fill(x-1+width, y-1+height, x-2+width, y-2+height, 0xFF000000);
        // poloski
        guiGraphics.fill(x+2, y, x-2+width, y+1, 0xFF000000);
        guiGraphics.fill(x+2, y+height, x-2+width, y-1+height, 0xFF000000);
        guiGraphics.fill(x, y+2, x+1, y-2+height, 0xFF000000);
        guiGraphics.fill(x+width, y+2, x-1+width, y-2+height, 0xFF000000);
    }

    @Override
    public int getToastTextColor() {
        return 0xFF000000;
    }

    @Override
    public int getTextColor(TextBuilder.TYPE type) {
        return type == TextBuilder.TYPE.BLOCKQUOTE ? 0xFF000000 : super.getTextColor(type);
    }

    @Override
    public int getTextColor(boolean active) {
        return  active ? WinColors.getTextColorWithMainColor() : 0xFFadb5bd;
    }

    @Override
    public int getCheckBoxColor(boolean isActive) {
        return getTextColor(isActive);
    }

    @Override
    public int getHorizontalRuleColor() {
        return WinColors.getHorizontalRuleColors()[0];
    }

    @Override
    public boolean textShadow(TextBuilder.TYPE type) {
        return false;
    }

    @Override
    public boolean textShadow() {
        return false;
    }

    @Override
    public boolean editBoxShadow() {
        return false;
    }

    @Override
    public int getEditBoxColor(boolean active) {
        return 0xFF000000;
    }

    @Override
    public boolean supportWhiteIcons() {
        return false;
    }

    @Override
    public boolean titleShadow() {
        return false;
    }

    @Override
    public int getTextTitleColor() {
        return 0xFFFFFFFF;
    }
}
