package ru.kelcu.windows.screens.components.alinlib;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import ru.kelcu.windows.Windows;
import ru.kelcuprum.alinlib.gui.Colors;
import ru.kelcuprum.alinlib.gui.components.builder.AbstractBuilder;

public class EditBox extends ru.kelcuprum.alinlib.gui.components.editbox.EditBox {
    public EditBox(AbstractBuilder builder) {
        super(builder);
        this.builder.setStyle(Windows.minedowsStyle);
        this.setTextColor(0xFF000000);
        this.setTextShadow(false);
    }
    public void renderText(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        String volume1 = font.plainSubstrByWidth(this.builder.secret ? Component.translatable("alinlib.editbox.secret").getString() : getValue(), getX() + getWidth() - (getPositionContent(this.builder.secret ? Component.translatable("alinlib.editbox.secret").getString() : getValue())));
        guiGraphics.drawString(font,
                //#if MC < 12110
                //$$formatter.apply(volume1, displayPos)
                //#else
                volume1
                //#endif
                , getX() + (getHeight() - 8) / 2, getY() + (getHeight() - 8) / 2, isError ? Colors.GROUPIE : 0xFF000000, false);
    }
}