package ru.kelcu.windows.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

@Mixin(LoadingOverlay.class)
public class LoadingOverlayMixin {
    @Unique
    private static boolean isFirstFrame = true;
    @Unique
    private static long startLoading = 0l;
    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
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
            guiGraphics.centeredText(AlinLib.MINECRAFT.font, Component.translatable("minedows.please_wait", dots), guiGraphics.guiWidth()/2, guiGraphics.guiHeight()/2-(AlinLib.MINECRAFT.font.lineHeight/2), 0xFFe77830);
            // End
            if (f >= 2.0F) {
                this.minecraft.gui.setOverlay(null);
            }

            if (this.fadeOutStart == -1L && this.reload.isDone() && (!this.fadeIn || g >= 2.0F)) {
                try {
                    this.reload.checkExceptions();
                    this.onFinish.accept(Optional.empty());
                } catch (Throwable var23) {
                    this.onFinish.accept(Optional.of(var23));
                }

                this.fadeOutStart = Util.getMillis();
                if (this.minecraft.gui.screen() != null) {
                    this.minecraft.gui.screen().init(guiGraphics.guiWidth(), guiGraphics.guiHeight());
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
                if (this.minecraft.gui.screen() != null) {
                    this.minecraft.gui.screen().extractRenderState(guiGraphics, 0, 0, partialTick);
                }
                k = kB = Mth.ceil((1.0F - Mth.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
            } else if (this.fadeIn) {
                if (this.minecraft.gui.screen() != null && g < 1.0F) {
                    this.minecraft.gui.screen().extractBackground(guiGraphics, mouseX, mouseY, partialTick);
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
            int www = 980;
            int whh = 785;
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
            guiGraphics.fill(0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), 0xFFFFFFFF);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/boot/corp.png"), guiGraphics.guiWidth()-70, 5, 0, 0, 65, 12, 65, 12);
//            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/boot/background.png"), 0, 0, x, y, ww, wh, ww, wh);
            // ДЛЯ МЕНЯ
            /*
            - Бегущая хуетень - 10px
            - Прогресс бар - 10px
            - отступы бара - 10px
            - копирайт - размер шрифта
            - 10+10+30+9 - 49
             */
            // Анимационная штучка
            guiGraphics.fill(0, guiGraphics.guiHeight()-49, guiGraphics.guiWidth(), guiGraphics.guiHeight()-39, 0xFF332252);
            int barPos = (int) ((guiGraphics.guiWidth()+180) * ((System.currentTimeMillis()%7500)/7500.0));
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/boot/bar.png"), -180+barPos, guiGraphics.guiHeight()-49, 0, 0, 180, 10, 180, 15);
            guiGraphics.fill(0, guiGraphics.guiHeight()-39, guiGraphics.guiWidth(), guiGraphics.guiHeight(), 0xFFc0c0c0);
            int maxW = (int) Math.min(443, guiGraphics.guiHeight()*0.65);
            scale = maxW/443.0;
            int maxH = (int) (325*scale);
            // - Прогресс
            int hCube = 10;
            int pCount = 16;
            int wp = 10*pCount+(pCount-1);
            String start = "Starting up...";
            int xp = (guiGraphics.guiWidth()/2) - ((wp+2)/2) - 5 - (minecraft.font.width(start)/2);
            guiGraphics.text(minecraft.font, start, xp, guiGraphics.guiHeight()-31+((10-minecraft.font.lineHeight)/2), 0xFF000000, false);
            xp+= (5+minecraft.font.width(start));
            guiGraphics.fill(xp-1, guiGraphics.guiHeight()-32, xp+1+wp, guiGraphics.guiHeight()-20, 0xFF8b8b8b);
            guiGraphics.fill(xp, guiGraphics.guiHeight()-31, xp+1+wp, guiGraphics.guiHeight()-20, 0xFFfafafa);
            guiGraphics.fill(xp, guiGraphics.guiHeight()-31, xp+wp, guiGraphics.guiHeight()-21, 0xFFc0c0c0);
            int pP = (int) (pCount * currentProgress);
            for(int i = 0; i <= pP; i++){
                int xPro = xp+(hCube*i)+i;
                guiGraphics.fill(xPro, guiGraphics.guiHeight()-31, xPro+hCube, guiGraphics.guiHeight()-(31-hCube), 0xFF00289c);
            }
            String copyr = String.format("Copyright © 2025-%s  Clovisoft Corporation", LocalDate.now().getYear());
            guiGraphics.text(minecraft.font, copyr, (guiGraphics.guiWidth()/2)-(minecraft.font.width(copyr)/2), guiGraphics.guiHeight()-15, 0xFF000000, false);

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/boot/logo.png"), (guiGraphics.guiWidth()/2)-(maxW/2), (guiGraphics.guiHeight()/2)-(maxH/2)-20, 0,0, maxW, maxH, maxW, maxH);
            // End
            if (f >= 2.0F) {
                this.minecraft.gui.setOverlay(null);
            }

            if (this.fadeOutStart == -1L && this.reload.isDone() && (!this.fadeIn || g >= 2.0F)) {
                try {
                    this.reload.checkExceptions();
                    this.onFinish.accept(Optional.empty());
                } catch (Throwable var23) {
                    this.onFinish.accept(Optional.of(var23));
                }

                this.fadeOutStart = Util.getMillis();
                if (this.minecraft.gui.screen() != null) {
                    this.minecraft.gui.screen().init(guiGraphics.guiWidth(), guiGraphics.guiHeight());
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
