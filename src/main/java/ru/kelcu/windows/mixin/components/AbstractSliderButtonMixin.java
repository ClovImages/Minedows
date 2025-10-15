package ru.kelcu.windows.mixin.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcu.windows.Windows;
import ru.kelcuprum.alinlib.gui.GuiUtils;

@Mixin(AbstractSliderButton.class)
public abstract class AbstractSliderButtonMixin extends AbstractWidget {

    @Shadow protected double value;

    public AbstractSliderButtonMixin(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    @Override
    @Unique
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {

    }

    @Override
    @Unique
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        Windows.minedowsStyle.renderBackground$slider(guiGraphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.active, this.isHoveredOrFocused(), this.value);
        int k = this.active ? 16777215 : 10526880;
        this.renderScrollingString(guiGraphics, Minecraft.getInstance().font, 2, k | Mth.ceil(this.alpha * 255.0F) << 24);
        ci.cancel();
    }
}
