package ru.kelcu.windows.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.components.Window;
import ru.kelcu.windows.components.builders.WindowBuilder;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcu.windows.utils.WindowUtils;

@Mixin(Minecraft.class)
public abstract class MinecraftScreen{
    @Shadow
    @Nullable
    public Screen screen;

    @Shadow
    @Nullable
    public ClientLevel level;

    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Inject(method = "setScreen", at=@At("HEAD"), cancellable = true)
    public void setScreen(Screen screen, CallbackInfo ci){
        if(isNotLegalScreen(screen)) return;
        if(this.screen instanceof DesktopScreen){
            if(System.currentTimeMillis() - DesktopScreen.lastClosedWindow <= 200) {
                DesktopScreen.lastClosedWindow = 0;
                ci.cancel();
                return;
            }
            if(screen instanceof DesktopScreen) {
                screen = DesktopScreen.windows.isEmpty() ? null : DesktopScreen.windows.getLast().active ? DesktopScreen.windows.getLast().lastScreen : null;
            } else if(screen instanceof TitleScreen){
                for(Window window : DesktopScreen.windows){
                    if(window.active && window.visible) {
                        if(window.screen instanceof GenericMessageScreen || window.screen instanceof JoinMultiplayerScreen) screen = null;
                        break;
                    }
                }
            }
            Window currentWindow = null;
            for(Window window : DesktopScreen.windows){
                if(window.active && window.visible) {
                    currentWindow = window;
                    break;
                }
            }
            if(screen instanceof DeathScreen) currentWindow = null;
            if(currentWindow == null){
                if(screen != null){
                    currentWindow = WindowUtils.getBuilderByScreen(screen).build();
                    DesktopScreen.addWindow(currentWindow);
                } else if(level != null){
                    return;
                }
            }
            if(screen == null){
                DesktopScreen.removeWindow(currentWindow);
            } else {
                currentWindow.setScreen(screen);
                currentWindow.screen.init((Minecraft) (Object) this, (int) currentWindow.width-6, (int) currentWindow.height-22);
            }
            ci.cancel();
        } else if(screen instanceof TitleScreen || (screen instanceof PauseScreen && !Windows.config.getBoolean("ENABLE_PAUSE_SCREEN", false)) || screen instanceof WinScreen || screen instanceof DeathScreen || screen instanceof DisconnectedScreen){
            setScreen(new DesktopScreen());
            try {
                for(Window window : DesktopScreen.windows){
                    if((window.screen instanceof TitleScreen || window.screen instanceof PauseScreen || window.screen instanceof WinScreen || window.screen instanceof DeathScreen || window.screen instanceof ReceivingLevelScreen))
                        DesktopScreen.removeWindow(window);
                }
                if(Windows.config.getBoolean("OPEN_PAUSE_SCREEN", false) && (screen instanceof PauseScreen)) DesktopScreen.addWindow(new WindowBuilder().setScreen(screen).build());
            } catch (Exception ignored){}
            if(screen instanceof WinScreen || screen instanceof DeathScreen || screen instanceof DisconnectedScreen){
                DesktopScreen.addWindow(WindowUtils.getBuilderByScreen(screen).build());
            }
            ci.cancel();
        }
    }
    @Unique
    public boolean isNotLegalScreen(Screen screen){
        return screen instanceof AbstractContainerScreen || screen instanceof AbstractFurnaceScreen || screen instanceof AbstractSignEditScreen || screen instanceof BookEditScreen || screen instanceof AbstractCommandBlockEditScreen || screen instanceof OutOfMemoryScreen;
    }
}
