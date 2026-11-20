package ru.kelcu.windows.screens.info;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import ru.kelcu.windows.utils.WinColors;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WindowsVersion extends Screen {
    public WindowsVersion() {
        super(Component.translatable("minedows.start.about"));
    }
    int pos = 0;
    long lastChange = System.currentTimeMillis();
    String[] testers = {
           "Kel_Cleverov",
           "GRUI_72",
            "Statuxia",
            "SeF0rt",
            "FlurryFy_",
            "Pweiz",
            "Tea_Rom"
    };

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        super.extractRenderState(guiGraphics, i, j, f);
        int x = 5, y = 5;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "icon.png"), x, y, 0, 0, 50, 50, 50, 50);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/start/icons/clovi.png"), x+2, y+57, 0, 0, 46, 46, 46, 46);
        x = 60; y = 15;
        guiGraphics.text(font, Component.translatable("minedows.winver.title"), x, y, WinColors.getTextColorWithMainColor(), false);
        y+=font.lineHeight+3;
        guiGraphics.text(font, Component.translatable("minedows.winver.name", FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion().getFriendlyString()), x, y, WinColors.getTextColorWithMainColor(), false);
        y+=font.lineHeight+3;
        guiGraphics.text(font, Component.translatable("minedows.winver.copyright", LocalDate.now().getYear()), x, y, WinColors.getTextColorWithMainColor(), false);
        y+=font.lineHeight+3;
        y+=3;
        int[] colors = WinColors.getHorizontalRuleColors();
        guiGraphics.fill(x, y, width-4, y+2, colors[0]);
        guiGraphics.fill(x, y, width-4, y+1, colors[1]);
        y+=8;
        List<FormattedCharSequence> texts = new ArrayList<>();
        texts.addAll(font.split(Component.translatable("minedows.winver.microsoft_icons"), width-x-5));
        texts.addAll(font.split(Component.translatable("minedows.winver.oneshot_startup_sound"), width-x-5));
        texts.addAll(font.split(Component.translatable("minedows.winver.clovi"), width-x-5));
        for(FormattedCharSequence formattedCharSequence : texts) {
            guiGraphics.text(font, formattedCharSequence, x, y, WinColors.getTextColorWithMainColor(), false);
            y += font.lineHeight + 3;
        }
        y+=3;
        guiGraphics.fill(x, y, width-4, y+2, colors[0]);
        guiGraphics.fill(x, y, width-4, y+1, colors[1]);
        y+=8;
        if(System.currentTimeMillis()-lastChange >= 5000) changePos();
        for(FormattedCharSequence formattedCharSequence : font.split(Component.translatable("minedows.winver.tester", testers[pos]), width-x-5)) {
            guiGraphics.text(font, formattedCharSequence, x, y, WinColors.getTextColorWithMainColor(), false);
            y += font.lineHeight + 3;
        }
    }
    public void changePos(){
        lastChange = System.currentTimeMillis();
        if(pos+1 == testers.length) pos = 0;
        else pos++;
    }

    @Override
    protected void init() {
        addRenderableWidget(new ButtonBuilder(CommonComponents.GUI_OK, (s) -> onClose()).setWidth(20+font.width(CommonComponents.GUI_OK)).setPosition(width-25-font.width(CommonComponents.GUI_OK), height-25).build());
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().gui.setScreen(null);
    }
}
