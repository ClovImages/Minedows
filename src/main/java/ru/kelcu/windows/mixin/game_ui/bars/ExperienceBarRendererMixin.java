package ru.kelcu.windows.mixin.game_ui.bars;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.contextualbar.ExperienceBarRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.utils.WinColors;

import static ru.kelcuprum.alinlib.gui.Colors.SEADRIVE;

@Mixin(ExperienceBarRenderer.class)
public class ExperienceBarRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    public void renderBackground(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci){
        if(!Windows.config.getBoolean("ENABLE_NEW_UI", false)) return;
        assert this.minecraft.player != null;
        renderBar(guiGraphics, getHotBarX(guiGraphics), getHotBarY(guiGraphics)-7, SEADRIVE, 186, 5, this.minecraft.player.experienceProgress);

        ci.cancel();
    }
    @Unique
    void renderBar(GuiGraphics guiGraphics, int x, int y, int color, int size, int height, double value){
        int[] colors = WinColors.getWindowColors();
        // 3
        // 4
        // 2
        guiGraphics.fill(x, y, x+size, y+height, colors[3]);
        guiGraphics.fill(x+1, y+1, x+size, y+height, colors[1]);
        guiGraphics.fill(x+1, y+1, x+size-1, y+height-1, colors[2]);
        int ih = (size-2) / 60;
        for(int i = 0; i<(int) (60*value); i++){
            guiGraphics.fill(x+1+(i*ih), y+1, x+1+(i*ih)+(ih-1), y-1+height, color);
        }
    }
    @Unique
    int getAlphaBarColor(int color){
        return 0x75000000;
    }
    @Unique
    public int getHotBarX(GuiGraphics guiGraphics){
        return guiGraphics.guiWidth() / 2 - (186 / 2);
    }
    @Unique
    public int getHotBarY(GuiGraphics guiGraphics){
        return guiGraphics.guiHeight() - 26;
    }
}
