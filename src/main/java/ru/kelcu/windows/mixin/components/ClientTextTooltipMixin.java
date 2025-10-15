package ru.kelcu.windows.mixin.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;

import static java.lang.Integer.MAX_VALUE;
import static ru.kelcu.windows.utils.WinColors.isLightColor;

@Mixin(ClientTextTooltip.class)
public class ClientTextTooltipMixin {
    @Mutable
    @Shadow
    @Final
    private FormattedCharSequence text;

    @Inject(method = "renderText", at=@At("HEAD"), cancellable = true)
    public void render(GuiGraphics guiGraphics, Font font, int i, int j, CallbackInfo ci){
        if(AlinLib.MINECRAFT.screen instanceof DesktopScreen) {
            guiGraphics.drawString(font, text, i, j, Windows.minedowsStyle.getTextColor(TextBuilder.TYPE.BLOCKQUOTE), Windows.minedowsStyle.textShadow(TextBuilder.TYPE.BLOCKQUOTE));
            ci.cancel();
        }
    }
}
