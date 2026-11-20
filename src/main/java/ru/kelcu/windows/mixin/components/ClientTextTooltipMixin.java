package ru.kelcu.windows.mixin.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.util.FormattedCharSequence;
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

@Mixin(ClientTextTooltip.class)
public class ClientTextTooltipMixin {
    @Mutable
    @Shadow
    @Final
    private FormattedCharSequence text;

    @Inject(method = "extractText", at=@At("HEAD"), cancellable = true)
    public void render(GuiGraphicsExtractor guiGraphics, Font font, int i, int j, CallbackInfo ci){
        if(Minecraft.getInstance().gui.screen() instanceof DesktopScreen || Minecraft.getInstance().gui.screen() instanceof ChatScreen) {
            guiGraphics.text(font, text, i, j, Windows.minedowsStyle.getTextColor(TextBuilder.TYPE.BLOCKQUOTE), Windows.minedowsStyle.textShadow(TextBuilder.TYPE.BLOCKQUOTE));
            ci.cancel();
        }
    }
}
