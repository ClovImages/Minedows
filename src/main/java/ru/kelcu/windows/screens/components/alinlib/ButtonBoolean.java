//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ru.kelcu.windows.screens.components.alinlib;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.Icons;
import ru.kelcuprum.alinlib.gui.components.Resetable;
import ru.kelcuprum.alinlib.gui.components.builder.AbstractBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.buttons.Button;
import ru.kelcuprum.alinlib.gui.components.text.TextBox;

public class ButtonBoolean extends Button implements Resetable {
    public final ButtonBooleanBuilder builder;
    protected Component volumeState;
    public boolean value;

    public ButtonBoolean(AbstractBuilder builder) {
        super(builder);
        this.builder = (ButtonBooleanBuilder)builder;
        if (((ButtonBooleanBuilder)builder).hasConfigurable()) {
            this.value = ((ButtonBooleanBuilder)builder).config.getBoolean(((ButtonBooleanBuilder)builder).configType, ((ButtonBooleanBuilder)builder).defaultValue);
        } else {
            this.value = ((ButtonBooleanBuilder)builder).defaultValue;
        }

        this.volumeState = this.value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
        this.setMessage(Component.literal(builder.getTitle().getString() + ": ").append(this.volumeState));
    }

    public void onPress() {
        if (this.active) {
            this.setValue(!this.value);
            if (this.builder.hasConfigurable()) {
                this.builder.config.setBoolean(this.builder.configType, this.value);
            }

            if (this.builder.getOnPress() != null) {
                this.builder.getOnPress().onPress(this.value);
            }

        }
    }
    @Override
    public void onClick(double d, double e) {
        this.onPress();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.builder.getStyle() != null) {
            this.builder.getStyle().renderBackground$widget(guiGraphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.active, this.isHoveredOrFocused());
        }
    }

    public void renderText(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (GuiUtils.isDoesNotFit(this.builder.isCheckBox ? this.builder.getTitle() : this.getMessage(), this.getWidthComponent(), this.getHeight())) {
            this.renderScrollingString(guiGraphics, AlinLib.MINECRAFT.font, this.builder.isCheckBox ? this.builder.getTitle() : this.getMessage(), (this.getHeight() - 8) / 2, this.builder.getStyle().getTextColor(this.active), this.builder.getStyle().textShadow());
        } else {
            guiGraphics.drawString(AlinLib.MINECRAFT.font, this.builder.getTitle(), this.getXComponent() + (this.getHeight() - 8) / 2, this.getY() + (this.getHeight() - 8) / 2, this.builder.getStyle().getTextColor(this.active), this.builder.getStyle().textShadow());
            if (!this.builder.isCheckBox) {
                guiGraphics.drawString(AlinLib.MINECRAFT.font, this.volumeState, this.getX() + this.getWidth() - AlinLib.MINECRAFT.font.width(this.volumeState.getString()) - (this.getHeight() - 8) / 2, this.getY() + (this.getHeight() - 8) / 2, this.builder.getStyle().getTextColor(this.active), this.builder.getStyle().textShadow());
            }
        }

        if (this.builder.isCheckBox) {
            int boxHeight = this.getHeight() - 10;
            int boxX = 5;
            int boxY = 5;
            int color = this.builder.getStyle().getCheckBoxColor(this.value);
            guiGraphics.fill(this.getX() + boxX, this.getY() + boxY, this.getX() + boxX + boxHeight, this.getY() + boxY + 1, color);
            guiGraphics.fill(this.getX() + boxX, this.getY() + boxHeight + boxY - 1, this.getX() + boxX + boxHeight, this.getY() + boxHeight + boxY, color);
            guiGraphics.fill(this.getX() + boxX, this.getY() + boxY + 1, this.getX() + boxX + 1, this.getY() + boxY + boxHeight, color);
            guiGraphics.fill(this.getX() + boxX + boxHeight - 1, this.getY() + boxY + 1, this.getX() + boxX + boxHeight, this.getY() + boxY + boxHeight, color);
            if (this.value) {
                guiGraphics.fill(this.getX() + boxX + 2, this.getY() + boxY + 2, this.getX() + boxX + boxHeight - 2, this.getY() + boxY + boxHeight - 2, color);
            }
        }

    }

    @Override
    protected int getXComponent() {
        return this.getX() + this.getHeight() - 2;
    }
    @Override
    protected int getWidthComponent() {
        return this.getWidth() - this.getHeight() - 2;
    }

    public ButtonBoolean setValue(boolean value) {
        this.value = value;
        this.volumeState = this.value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
        this.setMessage(Component.literal(this.builder.getTitle().getString() + ": ").append(this.volumeState));
        return this;
    }

    protected void renderScrollingString(GuiGraphics guiGraphics, Font font, Component message, int x, int color, boolean shadow) {
        int k = this.getXComponent() + x;
        int l = this.getX() + this.getWidth() - x;

        TextBox.renderScrollingString(guiGraphics, font, message, k, this.getY(), l, this.getY() + this.height, color, shadow);
    }

    public void resetValue() {
        if (this.builder.hasConfigurable()) {
            this.builder.config.setBoolean(this.builder.configType, this.builder.defaultValue);
        }

        this.setValue(this.builder.defaultValue);
    }

    protected boolean isResetable() {
        return this.builder.hasConfigurable();
    }

    public ButtonBoolean setDescription(Component description) {
        this.builder.setDescription(description);
        return this;
    }

    public Component getDescription() {
        return this.builder.getDescription();
    }

    public interface OnPress {
        void onPress(Boolean var1);
    }
}
