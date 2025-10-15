package ru.kelcu.windows.mixin.components;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcu.windows.components.Window;
import ru.kelcu.windows.screens.DesktopScreen;

import java.util.Objects;

@Mixin(ServerSelectionList.LANHeader.class)
public class LANHeaderMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "render", at=@At("HEAD"), cancellable = true)
    public void render(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f, CallbackInfo ci){
        if(this.minecraft.screen instanceof DesktopScreen){
            int width = 0;
            if(DesktopScreen.currentRenderedWindow != null && DesktopScreen.currentRenderedWindow.screen instanceof JoinMultiplayerScreen)
                width = DesktopScreen.currentRenderedWindow.screen.width;
            else {
                for (Window window : DesktopScreen.windows) {
                    if (window.screen instanceof JoinMultiplayerScreen) {
                        width = window.screen.width;
                    }
                }
            }
            int var10000 = j + m / 2;
            Objects.requireNonNull(this.minecraft.font);
            int p = var10000 - 9 / 2;
            guiGraphics.drawString(this.minecraft.font, ServerSelectionList.SCANNING_LABEL, width / 2 - this.minecraft.font.width(ServerSelectionList.SCANNING_LABEL) / 2, p, -1);
            String string = LoadingDotsText.get(Util.getMillis());
            Font var10001 = this.minecraft.font;
            int var10003 = width / 2 - this.minecraft.font.width(string) / 2;
            Objects.requireNonNull(this.minecraft.font);
            guiGraphics.drawString(var10001, string, var10003, p + 9, -8355712);
            ci.cancel();
        }
    }

}
