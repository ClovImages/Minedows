package ru.kelcu.windows.mixin.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcuprum.alinlib.AlinLib;

@Mixin(TooltipRenderUtil.class)
public class TooltipRenderUtilMixin {
    @Inject(method = "extractTooltipBackground", at=@At("HEAD"), cancellable = true)
    private static void renderTooltipBackground(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height, Identifier sprite, CallbackInfo ci){
        if(Minecraft.getInstance().gui.screen() instanceof DesktopScreen || Minecraft.getInstance().gui.screen() instanceof ChatScreen) {
            x -= 5;
            y -= 5;
            width += 10;
            height += 10;
            Windows.minedowsStyle.renderTooltip(guiGraphics, x, y, width, height);
            ci.cancel();
        }
    }
}
