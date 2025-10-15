package ru.kelcu.windows.screens.options.style;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ru.kelcu.windows.utils.WindowUtils;
import ru.kelcuprum.alinlib.gui.Colors;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.styles.AbstractStyle;

public class AirStyle extends AbstractStyle {
    public AirStyle() {
        super("air", Component.literal("as;j'df"));
    }

    @Override
    public void renderBackground$widget(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean active, boolean isHoveredOrFocused) {
    }

    @Override
    public void renderBackground$slider(GuiGraphics guiGraphics, int x, int y, int width, int height, boolean active, boolean isHoveredOrFocused, double position) {
        WindowUtils.welcomeToWhiteSpace(guiGraphics,x+2, y+2, width-4, height-4);
        int color = 0xff717171;
        int colorb = isHoveredOrFocused ? Colors.getWinColor() : 0xFFC0C0C0;

        int color1 = 0x20000000;
        int color2 = 0x3F000000;
        int color3 = 0x7F000000;
        int color4 = 0xF5000000;
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
    }

    @Override
    public int getTextColor(TextBuilder.TYPE type) {
        return type == TextBuilder.TYPE.BLOCKQUOTE ? 0xFF000000 : super.getTextColor(type);
    }

    @Override
    public int getTextColor(boolean active) {
        return active ? 0xFF000000 : 0xFF2e2e2e;
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
    public boolean sliderShadow() {
        return false;
    }
}
