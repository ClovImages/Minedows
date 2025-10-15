package ru.kelcu.windows.mixin.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcuprum.alinlib.AlinLib;

@Mixin(TooltipRenderUtil.class)
public class TooltipRenderUtilMixin {
    @Inject(method = "renderTooltipBackground", at=@At("HEAD"), cancellable = true)
    private static void renderTooltipBackground(GuiGraphics guiGraphics, int x, int y, int width, int height, ResourceLocation sprite, CallbackInfo ci){
        if(AlinLib.MINECRAFT.screen instanceof DesktopScreen) {
            x -= 5;
            y -= 5;
            width += 10;
            height += 10;
            Windows.minedowsStyle.renderTooltip(guiGraphics, x, y, width, height);
            ci.cancel();
        }
    }
}
