package ru.kelcu.windows.screens.apps.for_arts;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.utils.WinColors;

public class FakeAudioPlayerScreen extends Screen {
    public FakeAudioPlayerScreen() {
        super(Component.translatable("minedows.audio_player.with_title", Component.translatable("minedows.audio_player.fake_title")));
    }
    double value = 0.5;
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        int img_width = 64;
        int x = 2;
        int y = 0;
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath("windows", "textures/audio_player/icon.png"), x, y, 0, 0, img_width, img_width, img_width, img_width);
        x+=(img_width+20);
        y = (img_width/2) - (((font.lineHeight*3) + 15) / 2);
        graphics.text(font, Component.empty().append(Component.translatable("minedows.audio_player.fake_title")).setStyle(Style.EMPTY.withBold(true)), x, y, WinColors.getTextColorWithMainColor());
        y+= (font.lineHeight+5);
        graphics.text(font, Component.translatable("minedows.audio_player.fake_author"), x, y, WinColors.getTextColorWithMainColor());
        y+= (font.lineHeight+10);
        long time = Windows.config.getNumber("art.audio_player.time", 300000).longValue();
        long cTime = (long) (time * value);
        String ct = getTimestamp(cTime);
        String t = getTimestamp(time);
        int t1 = font.width(ct);
        int t2 = font.width(t);
        graphics.text(font, ct, x, y, WinColors.getTextColorWithMainColor());
        graphics.text(font, t, width-10-t2, y, WinColors.getTextColorWithMainColor());
        int w = width - x - t1 - t2 - 20;
        int h = font.lineHeight / 3;
        graphics.fill(x+t1+5, y+h, x+t1+5+w, y+(h*2), 0x7F000000);
        graphics.fill(x+t1+5, y+h, x+t1+5+ (int) (w*value), y+(h*2), WinColors.getTextColorWithMainColor());

    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if(scrollY < 0) value = Math.max(0, value-0.01);
        else value = Math.min(1, value+0.01);
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    public static String getTimestamp(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }
}
