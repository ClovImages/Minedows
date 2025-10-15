package ru.kelcu.windows.mixin.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcu.windows.SoundLoader;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.info.Player;
import ru.kelcu.windows.Windows;

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
        if(!(Minecraft.getInstance().screen instanceof DesktopScreen)) return;
        if(!isNiko()){
            this.deathScore = Component.translatable("deathScreen.score.value", new Object[]{Component.literal(Integer.toString(this.minecraft.player.getScore())).withStyle(ChatFormatting.YELLOW)});
            Component component = this.hardcore ? Component.translatable("deathScreen.spectate") : Component.translatable("deathScreen.respawn");
            int x = width-210;
            int y = height-23;
            addRenderableWidget(new ButtonBuilder(component, (s) -> this.minecraft.player.respawn())
                    .setPosition(x, y).setSize(100, 18).build());
            addRenderableWidget(new ButtonBuilder(Component.translatable("deathScreen.titleScreen"),
                    (s) -> PauseScreen.disconnectFromWorld(Minecraft.getInstance(), ClientLevel.DEFAULT_QUIT_MESSAGE))
                    .setPosition(x+105, y).setSize(100, 18).build());
        }
        cl.cancel();
    }
    private long startTime = System.currentTimeMillis();
    private int timeShow = 2000;
    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        if(!(Minecraft.getInstance().screen instanceof DesktopScreen)) return;
        ci.cancel();
    }

    @Unique
    public boolean isNiko(){
        return !Windows.config.getBoolean("DEATH.NIKO.ONLY_HARDCORE", true) || hardcore;
    }
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        if(!(Minecraft.getInstance().screen instanceof DesktopScreen)) return;
        super.render(guiGraphics, i, j, f);
        if(isNiko()){
            ci.cancel();
            if(!isInited && (System.currentTimeMillis()-startTime) > timeShow) {
                isInited = true;
                if(minecraft.getWindow().isFullscreen()) minecraft.getWindow().toggleFullScreen();
                TinyFileDialogs.tinyfd_messageBox("", String.format("You killed %s.", Player.getName()), "ok", "error", true);
                minecraft.player.respawn();
                minecraft.getSoundManager().stop();
                SoundLoader.tryPlay();
                minecraft.stop();
            }
        } else {
            int y = 5;
            if (this.causeOfDeath != null) {
                guiGraphics.drawString(this.font, this.causeOfDeath, 5, y, 0xFF000000, false);
            }
            y+=15;

            guiGraphics.drawString(this.font, this.deathScore, 5, y, 0xFF000000, false);
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
