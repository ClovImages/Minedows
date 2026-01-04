package ru.kelcu.windows.mixin.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
import org.lwjgl.glfw.GLFW;
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

    @Shadow private Component deathScore;

    @Shadow protected abstract void handleExitToTitleScreen();

    @Shadow @Final private Component causeOfDeath;

    @Shadow @Nullable
    protected abstract Style getClickedComponentStyleAt(int i);

    @Shadow
    protected abstract void init();

    @Unique
    public boolean isInited = false;

    protected DeathScreenMixin() {
        super(null);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    void init(CallbackInfo cl) {
        if(isDisabled()) return;
        if(!isNiko()){
            this.deathScore = Component.translatable("deathScreen.score.value", new Object[]{Component.literal(Integer.toString(this.minecraft.player.getScore())).withStyle(ChatFormatting.YELLOW)});
            Component component = this.hardcore ? Component.translatable("deathScreen.spectate") : Component.translatable("deathScreen.respawn");
            int x = width-210;
            int y = height-23;
            addRenderableWidget(new ButtonBuilder(component, (s) -> this.minecraft.player.respawn())
                    .setPosition(x, y).setSize(100, 18).build());
            addRenderableWidget(new ButtonBuilder(Component.translatable("deathScreen.titleScreen"),
                    (s) ->
                            //#if MC < 12110
                            //$$PauseScreen.disconnectFromWorld(AlinLib.MINECRAFT, ClientLevel.DEFAULT_QUIT_MESSAGE)
                            //#else
                            AlinLib.MINECRAFT.getReportingContext().draftReportHandled(AlinLib.MINECRAFT, AlinLib.MINECRAFT.screen, () -> AlinLib.MINECRAFT.disconnectFromWorld(ClientLevel.DEFAULT_QUIT_MESSAGE), true)
                            //#endif
                            )
                    .setPosition(x+105, y).setSize(100, 18).build());
        }
        cl.cancel();
    }
    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        if(isDisabled()) return;
        ci.cancel();
    }

    @Unique
    public boolean isNiko(){
        return Windows.config.getBoolean("DEATH.HARDCORE", true) && hardcore;
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
        int j = keyEvent.scancode();
        int k = keyEvent.modifiers();
        //#endif
        if(isDisabled()) return super.keyPressed(
                //#if MC < 12110
                //$$i, j, k
                //#else
                keyEvent
                //#endif
        );
        if(isNiko()){
            if(i == GLFW.GLFW_KEY_S) this.minecraft.player.respawn();
            else if(i == GLFW.GLFW_KEY_T)
                //#if MC < 12110
                //$$PauseScreen.disconnectFromWorld(AlinLib.MINECRAFT, ClientLevel.DEFAULT_QUIT_MESSAGE);
                //#else
                AlinLib.MINECRAFT.getReportingContext().draftReportHandled(AlinLib.MINECRAFT, AlinLib.MINECRAFT.screen, () -> AlinLib.MINECRAFT.disconnectFromWorld(ClientLevel.DEFAULT_QUIT_MESSAGE), true);
            //#endif

        }
        return super.keyPressed(
                //#if MC < 12110
                //$$i, j, k
                //#else
                keyEvent
                //#endif
        );
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        if(isDisabled()) return;
        super.render(guiGraphics, i, j, f);
        if(isNiko()){
            ci.cancel();
            guiGraphics.fill(0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), 0xFFFF0000);
            int w = (int) (guiGraphics.guiWidth() * 0.6);
            ArrayList<FormattedCharSequence> messages = new ArrayList<>();
            if(causeOfDeath != null) messages.addAll(font.split(causeOfDeath, w));
            if(deathScore != null) messages.addAll(font.split(deathScore, w));
            int h = 30 + (font.lineHeight*2) + (font.lineHeight*messages.size()) + (2*(messages.size()-1));
            int x = guiGraphics.guiWidth() / 2 - w / 2;
            int y = guiGraphics.guiHeight() / 2 - h / 2;
            guiGraphics.fill(guiGraphics.guiWidth()/2-(font.width(Component.literal("CRITICAL ERROR"))/2)-6, y-3, guiGraphics.guiWidth()/2+(font.width(Component.literal("CRITICAL ERROR"))/2)+6, y+ font.lineHeight+3, -1);
            guiGraphics.drawString(font, Component.literal("CRITICAL ERROR"), guiGraphics.guiWidth()/2-(font.width(Component.literal("CRITICAL ERROR"))/2), y, 0xFFFF0000, false);
            y+=15+font.lineHeight;
            for(FormattedCharSequence formattedCharSequence : messages){
                guiGraphics.drawString(font, formattedCharSequence, guiGraphics.guiWidth()/2-(font.width(formattedCharSequence)/2), y, -1, false);
                y+=2+font.lineHeight;
            }
            y+=13;
            guiGraphics.drawString(font, Component.translatable("minedows.bsod.hardcore.spectate", Component.translatable("deathScreen.spectate")),
                    guiGraphics.guiWidth()/2-(font.width(Component.translatable("minedows.bsod.hardcore.spectate", Component.translatable("deathScreen.spectate")))/2), y, -1, false);
            y+=13;
            guiGraphics.drawString(font, Component.translatable("minedows.bsod.hardcore.title", Component.translatable("deathScreen.titleScreen")),
                    guiGraphics.guiWidth()/2-(font.width(Component.translatable("minedows.bsod.hardcore.title", Component.translatable("deathScreen.titleScreen")))/2), y, -1, false);
        } else {
            int y = 5;
            if (this.causeOfDeath != null) {
                guiGraphics.drawString(this.font, this.causeOfDeath, 5, y, WinColors.getTextColorWithMainColor(), false);
            }
            y+=15;

            guiGraphics.drawString(this.font, this.deathScore, 5, y, WinColors.getTextColorWithMainColor(), false);
            if (this.causeOfDeath != null && j > (y-15)) {
                Objects.requireNonNull(this.font);
                if (j < (y-15) + 9) {
                    Style style = this.getClickedComponentStyleAt(i);
                    guiGraphics.renderComponentHoverEffect(this.font, style, i, j);
                }
            }
            ci.cancel();
        }

    }
}
