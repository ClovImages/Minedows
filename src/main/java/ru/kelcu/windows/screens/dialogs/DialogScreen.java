package ru.kelcu.windows.screens.dialogs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.components.Action;
import ru.kelcu.windows.screens.AbstractWindowedScreen;
import ru.kelcu.windows.utils.WinColors;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;

import static ru.kelcu.windows.screens.DesktopScreen.addWindow;

public class DialogScreen extends AbstractWindowedScreen {
    public Component message;
    public Action[] actions = null;
    public ResourceLocation icon = null;
    public DialogScreen(Component title, Component message, ResourceLocation icon){
        this(title, message, new Action[]{
                new Action(() -> { AlinLib.MINECRAFT.setScreen(null); }, Component.literal("OK"), null)
        }, icon);
    }
    public DialogScreen(Component title, Component message, Action[] actions, ResourceLocation icon) {
        super(title);
        this.message = message;
        this.actions = actions;
        this.icon = icon;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        int y = 10;
        for(FormattedCharSequence formattedCharSequence : AlinLib.MINECRAFT.font.split(message, width-20)){
            guiGraphics.drawString(AlinLib.MINECRAFT.font, formattedCharSequence, 10, y, WinColors.getTextColorWithMainColor(), false);
            y+=(AlinLib.MINECRAFT.font.lineHeight+3);
        };
    }

    @Override
    protected void init() {
        super.init();
        if(actions == null || actions.length == 0) return;
        int w = 0;
        for(Action action : actions) w = Math.max(w, AlinLib.MINECRAFT.font.width(action.title)+20);
        int x = width - (w+5) * actions.length-2;
        for(Action action : actions){
            addRenderableWidget(new ButtonBuilder(action.title, (b) -> Windows.executeAction(action))
                    .setSize(w, 16).setPosition(x, height-23).setStyle(Windows.minedowsStyle).build());
            x+= (w+5);
        }
    }

    @Override
    public ResourceLocation icon() {
        return icon;
    }

    @Override
    public boolean resizable() {
        return false;
    }

    @Override
    public int getWindowType() {
        return 0;
    }

    @Override
    public int maxHeight() {
        return height();
    }

    @Override
    public int minHeight() {
        return height();
    }

    @Override
    public int minWidth() {
        return 300;
    }

    @Override
    public int maxWidth() {
        return 300;
    }

    @Override
    public int height() {
        return 40 + (AlinLib.MINECRAFT.font.lineHeight+3)*AlinLib.MINECRAFT.font.split(message, width-20).size();
    }

    @Override
    public int width() {
        return 300;
    }
}
