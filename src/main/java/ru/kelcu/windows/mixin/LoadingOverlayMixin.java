package ru.kelcu.windows.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.utils.WallpaperUtil;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin {
    @Unique
    private static boolean isFirstFrame = true;
    @Unique
    private static long startLoading = 0l;
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (Windows.gameStarted){
            long l = Util.getMillis();
            if (this.fadeIn && this.fadeInStart == -1L) {
                this.fadeInStart = l;
            }
            float f = 0;
            float g = 0;
            float t = this.reload.getActualProgress();
            this.currentProgress = Mth.clamp(this.currentProgress * 0.95F + t * 0.050000012F, 0.0F, 1.0F);
            f = this.fadeOutStart > -1L ? (float) (l - this.fadeOutStart) / 1000.0F : -1.0F;
            g = this.fadeInStart > -1L ? (float) (l - this.fadeInStart) / 500.0F : -1.0F;
            guiGraphics.fill(0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), 0xFF000000);
            long time = System.currentTimeMillis() % 2000;
            String dots = ".".repeat((int) (time/500));
            guiGraphics.drawCenteredString(AlinLib.MINECRAFT.font, Component.translatable("minedows.please_wait", dots), guiGraphics.guiWidth()/2, guiGraphics.guiHeight()/2-(AlinLib.MINECRAFT.font.lineHeight/2), 0xFFe77830);
            // End
            if (f >= 2.0F) {
                this.minecraft.setOverlay(null);
            }

            if (this.fadeOutStart == -1L && this.reload.isDone() && (!this.fadeIn || g >= 2.0F)) {
                try {
                    this.reload.checkExceptions();
                    this.onFinish.accept(Optional.empty());
                } catch (Throwable var23) {
                    this.onFinish.accept(Optional.of(var23));
                }

                this.fadeOutStart = Util.getMillis();
                if (this.minecraft.screen != null) {
                    this.minecraft.screen.init(this.minecraft, guiGraphics.guiWidth(), guiGraphics.guiHeight());
                }
            }
        } else {
            if(FabricLoader.getInstance().isModLoaded("pplhelper_april")) return;
            if(isFirstFrame){
                isFirstFrame = false;
                startLoading = System.currentTimeMillis();
            }
            long l = Util.getMillis();
            if (this.fadeIn && this.fadeInStart == -1L) {
                this.fadeInStart = l;
            }
            float f = 0;
            float g = 0;
            long time = System.currentTimeMillis() - startLoading;
            if(time >= 15000 || Windows.config.getBoolean("FASTBOOT", false)) {
                float t = this.reload.getActualProgress();
                this.currentProgress = Mth.clamp(this.currentProgress * 0.95F + t * 0.050000012F, 0.0F, 1.0F);
                f = this.fadeOutStart > -1L ? (float) (l - this.fadeOutStart) / 1000.0F : -1.0F;
                g = this.fadeInStart > -1L ? (float) (l - this.fadeInStart) / 500.0F : -1.0F;
            }
            // Alpha
            int k;
            int kB;
            if (f >= 1.0F) {
                if (this.minecraft.screen != null) {
                    this.minecraft.screen.render(guiGraphics, 0, 0, partialTick);
                }
                k = kB = Mth.ceil((1.0F - Mth.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
            } else if (this.fadeIn) {
                if (this.minecraft.screen != null && g < 1.0F) {
                    this.minecraft.screen.render(guiGraphics, mouseX, mouseY, partialTick);
                }
                k = kB = Mth.ceil(Mth.clamp(g, 0.15, 1.0) * 255.0);
            } else {
                k = -1;
                kB = 255;
            }
            // render
            int x = 0;
            int y = 0;
            double scale;
            int www = 1472;
            int whh = 1024;
            scale = (double) www / guiGraphics.guiWidth();
            int ww = (int) (www / scale);
            int wh = (int) (whh / scale);
            if(wh < guiGraphics.guiHeight()){
                scale = (double) whh / guiGraphics.guiHeight();
                ww = (int) (www / scale);
                wh = guiGraphics.guiHeight();
            }
            x = (ww-guiGraphics.guiWidth()) / 2;
            y = (wh-guiGraphics.guiHeight()) / 2;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/boot/background.png"), 0, 0, x, y, ww, wh, ww, wh);
            guiGraphics.fill(0, guiGraphics.guiHeight()-15, guiGraphics.guiWidth(), guiGraphics.guiHeight(), 0xFFF0F0F0);
            int barPos = (int) ((guiGraphics.guiWidth()+180) * ((System.currentTimeMillis()%7500)/7500.0));
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/boot/bar.png"), -180+barPos, guiGraphics.guiHeight()-15, 0, 0, 180, 15, 180, 15);
            int maxW = (int) Math.min(443, guiGraphics.guiHeight()*0.65);
            scale = maxW/443.0;
            int maxH = (int) (325*scale);

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/boot/logo.png"), (guiGraphics.guiWidth()/2)-(maxW/2), (guiGraphics.guiHeight()/2)-(maxH/2), 0,0, maxW, maxH, maxW, maxH);
            // End
            if (f >= 2.0F) {
                this.minecraft.setOverlay(null);
            }

            if (this.fadeOutStart == -1L && this.reload.isDone() && (!this.fadeIn || g >= 2.0F)) {
                try {
                    this.reload.checkExceptions();
                    this.onFinish.accept(Optional.empty());
                } catch (Throwable var23) {
                    this.onFinish.accept(Optional.of(var23));
                }

                this.fadeOutStart = Util.getMillis();
                if (this.minecraft.screen != null) {
                    this.minecraft.screen.init(this.minecraft, guiGraphics.guiWidth(), guiGraphics.guiHeight());
                }
            }
        }
        ci.cancel();
    }

    @Shadow
    private float currentProgress;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private long fadeOutStart;

    @Shadow
    @Final
    private ReloadInstance reload;

    @Shadow
    @Final
    private boolean fadeIn;

    @Shadow
    private long fadeInStart;

    @Shadow
    @Final
    private Consumer<Optional<Throwable>> onFinish;
}
