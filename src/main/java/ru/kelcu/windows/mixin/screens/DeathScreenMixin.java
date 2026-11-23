package ru.kelcu.windows.mixin.screens;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
//#if MC >= 12110
import net.minecraft.client.input.KeyEvent;
//#endif
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcu.windows.utils.WinColors;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcu.windows.Windows;

import java.util.ArrayList;
import java.util.Objects;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {

    @Shadow
    @Final
    private boolean hardcore;

    @Unique
    private Component deathScore;

    @Shadow protected abstract void handleExitToTitleScreen();

    @Shadow @Final private Component causeOfDeath;

    @Shadow
    protected abstract void init();

    protected DeathScreenMixin() {
        super(null);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    void init(CallbackInfo cl) {
        if(isDisabled()) return;
        cl.cancel();
    }
    @Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
    public void renderBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f, CallbackInfo ci) {
        if(isDisabled()) return;
        ci.cancel();
    }

    @Unique
    public boolean isDisabled(){
        return !Windows.config.getBoolean("DEATH", true);
    }

    @Override
    //#if MC < 12110
    //$$public boolean keyPressed(int i, int j, int k) {
    //#else
    public boolean keyPressed(KeyEvent keyEvent) {
        int i = keyEvent.key();
        //#endif
        if(isDisabled()) return super.keyPressed(
                //#if MC < 12110
                //$$i, j, k
                //#else
                keyEvent
                //#endif
        );
        if(i == InputConstants.KEY_S) this.minecraft.player.respawn();
        else if(i == InputConstants.KEY_T)
            //#if MC < 12110
            //$$PauseScreen.disconnectFromWorld(Minecraft.getInstance(), ClientLevel.DEFAULT_QUIT_MESSAGE);
            //#else
            Minecraft.getInstance().getReportingContext().draftReportHandled(Minecraft.getInstance(), Minecraft.getInstance().gui.screen(), () -> Minecraft.getInstance().disconnectFromWorld(ClientLevel.DEFAULT_QUIT_MESSAGE), true);
            //#endif


        return super.keyPressed(
                //#if MC < 12110
                //$$i, j, k
                //#else
                keyEvent
                //#endif
        );
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    public void render(GuiGraphicsExtractor guiGraphics, int i, int j, float f, CallbackInfo ci) {
        if(isDisabled()) return;
        super.extractRenderState(guiGraphics, i, j, f);
        ci.cancel();
        guiGraphics.fill(0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), (hardcore ? 0xFFFF0000 : 0xFF0000FF));
        int w = (int) (guiGraphics.guiWidth() * 0.6);
        ArrayList<FormattedCharSequence> messages = new ArrayList<>();
        if(causeOfDeath != null) messages.addAll(font.split(causeOfDeath, w));
        if(deathScore != null) messages.addAll(font.split(deathScore, w));
        int h = 30 + (font.lineHeight*2) + (font.lineHeight*messages.size()) + (2*(messages.size()-1));
        int x = guiGraphics.guiWidth() / 2 - w / 2;
        int y = guiGraphics.guiHeight() / 2 - h / 2;
        guiGraphics.fill(guiGraphics.guiWidth()/2-(font.width(Component.literal("CRITICAL ERROR"))/2)-6, y-3, guiGraphics.guiWidth()/2+(font.width(Component.literal("CRITICAL ERROR"))/2)+6, y+ font.lineHeight+3, -1);
        guiGraphics.text(font, Component.literal("CRITICAL ERROR"), guiGraphics.guiWidth()/2-(font.width(Component.literal("CRITICAL ERROR"))/2), y, (hardcore ? 0xFFFF0000 : 0xFF0000FF), false);
        y+=15+font.lineHeight;
        for(FormattedCharSequence formattedCharSequence : messages){
            guiGraphics.text(font, formattedCharSequence, guiGraphics.guiWidth()/2-(font.width(formattedCharSequence)/2), y, -1, false);
            y+=2+font.lineHeight;
        }
        y+=13;
        Component component = this.hardcore ? Component.translatable("deathScreen.spectate") : Component.translatable("deathScreen.respawn");
        guiGraphics.text(font, Component.translatable("minedows.bsod.hardcore.spectate", component),
                guiGraphics.guiWidth()/2-(font.width(Component.translatable("minedows.bsod.hardcore.spectate",component))/2), y, -1, false);
        y+=13;
        guiGraphics.text(font, Component.translatable("minedows.bsod.hardcore.title", Component.translatable("deathScreen.titleScreen")),
                guiGraphics.guiWidth()/2-(font.width(Component.translatable("minedows.bsod.hardcore.title", Component.translatable("deathScreen.titleScreen")))/2), y, -1, false);
    }
}
