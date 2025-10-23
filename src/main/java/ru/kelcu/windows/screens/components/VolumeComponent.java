package ru.kelcu.windows.screens.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
//#if MC >= 12106
import net.minecraft.client.renderer.RenderPipelines;
//#endif
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.utils.WinColors;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;

import static ru.kelcuprum.alinlib.gui.Colors.*;

public class VolumeComponent extends AbstractSliderButton {
    public final SoundSource soundSource;
    public VolumeComponent(int x, int y, int width, int height, SoundSource soundSource) {
        super(x, y, width, height, Component.empty(), AlinLib.MINECRAFT.options.getSoundSourceVolume(soundSource));
        this.soundSource = soundSource;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        int[] colors = WinColors.getHorizontalRuleColors();
        guiGraphics.fill(getX()+(getWidth()/2)-1, getY(), getX()+(getWidth()/2)+1, getBottom(), colors[0]);
        guiGraphics.fill(getX()+(getWidth()/2)-1, getY(), getX()+(getWidth()/2), getBottom()-1, colors[1]);
        int w = Math.min(getWidth(), 20);
        int y = (int) (getY()+getHeight()-(getHeight()*value));
        Windows.minedowsStyle.renderBackground$widget(guiGraphics, getX()+(getWidth()/2)-(w/2), y-5, w, 10, active, isHoveredOrFocused());
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,getSpeakerVolumeIcon(), getX()+(getWidth()/2)-7, getBottom()+3, 0f, 0f, 14, 14, 14, 14);
        if(isHovered())
            guiGraphics.setTooltipForNextFrame(Component.literal((int) (value*100)+"%"), i, j);
    }

    public ResourceLocation getSpeakerVolumeIcon(){
        float f = AlinLib.MINECRAFT.options.getSoundSourceVolume(soundSource);
        return GuiUtils.getResourceLocation("windows", String.format("textures/start/volume_%s.png",
                f == 0 ? "muted" : f <= 0.1 ? "low" : f <= 0.8 ? "ok" : "max"));
    }

    @Override
    protected void updateMessage() {

    }

    private void setValueFromMouse(double d) {
        double y = (d-getY());
        double h = height-y;
        this.setValue(h/(double) height);
        applyValue();
    }
    @Override
    protected void onDrag(double d, double e, double f, double g) {
        this.setValueFromMouse(e);
    }
    @Override
    public void onClick(double d, double e) {
        this.setValueFromMouse(e);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f, double g) {
        setValue(this.value + (g / 100));
        applyValue();
        return super.mouseScrolled(d, e, f, g);
    }

//    private void setValue(double d) {
//        double e = this.value;
//        this.value = Mth.clamp(d, 0.0, 1.0);
//        if (e != this.value) {
//            this.applyValue();
//        }
//
//        this.updateMessage();
//    }

    @Override
    protected void applyValue() {
        AlinLib.MINECRAFT.options.getSoundSourceOptionInstance(soundSource).set(value);
        AlinLib.MINECRAFT.options.save();
    }
}
