package ru.kelcu.windows.mixin.game_ui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.components.Window;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcu.windows.utils.SoundUtils;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;

import java.nio.file.Path;
import java.util.List;

import static ru.kelcu.windows.screens.DesktopScreen.removeWindow;
import static ru.kelcu.windows.screens.DesktopScreen.renderWindow;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    @Shadow
    private CommandSuggestions commandSuggestions;

    @Shadow
    private ChatComponent.DisplayMode displayMode;

    @Shadow
    protected abstract boolean insertionClickMode();

    protected ChatScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "keyPressed", at=@At("HEAD"), cancellable = true)
    public void keyPressed(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir){
        int i = keyEvent.key();
        int j = keyEvent.keycode();
        int k = keyEvent.modifiers();
        //#endif
        if(DesktopScreen.windows.isEmpty()) return;
        else for(Window window : DesktopScreen.windows){
            if(window.active && window.visible && window.pinned) {
                if(i == InputConstants.KEY_P &&
                        //#if MC >= 12110
                        keyEvent.
                                //#endif
                                        hasControlDown()){
                    window.changePin();
                    String title = window.screen.getTitle().getString();
                    if(title.isEmpty()) title = window.screen.getClass().getSimpleName()+".exe";
                    new ToastBuilder().setTitle(Component.literal(title)).setMessage(Component.translatable(String.format("minedows.window.%s", window.pinned ? "pinned" : "unpinned"))).setStyle(Windows.minedowsStyle).buildAndShow();
                } else if(window.y < 0 && i == InputConstants.KEY_TAB) {
                    window.setPosition(0, 0);
                    cir.setReturnValue(true);
                    return;
                } else cir.setReturnValue(window.screen.keyPressed(keyEvent));
            }
        }
    }
    @Override

    //#if MC < 12110
    //$$public boolean keyReleased(int i, int j, int k) {
    //#else
    public boolean keyReleased(KeyEvent keyEvent) {
        //#endif
        if(DesktopScreen.windows.isEmpty()) return super.keyReleased(
                //#if MC < 12110
                //$$i, j, k
                //#else
                keyEvent
                //#endif
        );
        else for(Window window : DesktopScreen.windows){
            if(window.active && window.visible && window.pinned) {
                return window.screen.keyReleased(
                        //#if MC < 12110
                        //$$i, j, k
                        //#else
                        keyEvent
                        //#endif
                );
            }
        }
        return super.keyReleased(
                //#if MC < 12110
                //$$i, j, k
                //#else
                keyEvent
                //#endif
        );
    }
    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (Window window : DesktopScreen.windows) {
            if(window.visible && window.pinned) renderWindow(guiGraphics, window, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        //#if MC > 12110
        guiGraphics.extractDeferredElements(mouseX, mouseY, partialTick);
//        guiGraphics.deferredOutlines.clear();
        guiGraphics.deferredTooltip = null;
        guiGraphics.fill(2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.options.getBackgroundColor(Integer.MIN_VALUE));
        this.minecraft.gui.hud.getChat().extractRenderState(guiGraphics, this.font, this.minecraft.gui.hud.getGuiTicks(), mouseX, mouseY, this.displayMode, this.insertionClickMode());
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        this.commandSuggestions.extractRenderState(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void mouseMoved(double d, double e) {
        for(Window window : DesktopScreen.windows){
            if(window.isDragging || window.isResized) break;
            if(window.active && window.visible && window.pinned) {
                window.screen.mouseMoved(d-(window.x+3), e-(window.y+19));
            }
        }
        super.mouseMoved(d, e);
    }
    @Override
    //#if MC < 12110
    //$$public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
    //#else
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double dragX, double dragY) {
        double mouseX = mouseButtonEvent.x();
        double mouseY = mouseButtonEvent.y();
        int button = mouseButtonEvent.button();
        //#endif
        if(!DesktopScreen.windows.isEmpty()){
            Window window = DesktopScreen.windows.getLast();
            if(window.pinned){
                if(window.isScreenDragging){
                    int x = (int) window.x;
                    int y = (int) window.y;
                    window.screen.mouseDragged(
                            //#if MC < 12110
                            //$$mouseX-(x+3), mouseY-(y+19), button
                            //#else
                            new MouseButtonEvent(mouseX-(x+3), mouseY-(y+19), mouseButtonEvent.buttonInfo())
                            //#endif
                            , dragX, dragY);
                    return true;
                } else if(window.isDragging) {
                    window.setPosition(window.x + dragX, window.y + dragY);
                    return true;
                } else if(window.isResized){
                    window.setSize(window.width+dragX, window.height+dragY);
                    window.screen.resize((int) window.width - 6, (int) window.height - 22);
                    return true;
                }
            }
        }
        for(int i = DesktopScreen.windows.size()-1; i>=0; i--){
            Window window = DesktopScreen.windows.get(i);
            if(!window.active || !window.visible || !window.pinned) continue;
            if(window.x < mouseX && mouseX < window.x+window.width && window.y < mouseY && mouseY < window.y+window.height && !window.isDragging && !window.isResized){
                boolean ret;
                if(mouseY < window.y+19 && !window.maximize){
                    window.setDragging(true);
                    window.setPosition(window.x+dragX, window.y+dragY);
                    ret = true;
                } else if(window.isResizable() && !window.maximize
                        && window.x+window.width-14 < mouseX && mouseX < window.x + window.width+6
                        && window.y+window.height-14 < mouseY && mouseY < window.y + window.height+6){
                    window.setResized(true);
                    window.setSize(window.width+dragX, window.height+dragY);
                    window.screen.resize((int) window.width - 6, (int) window.height - 22);
                    ret = true;
                } else {
                    window.setScreenDragging(true);
                    int x = (int) window.x;
                    int y = (int) window.y;
//                    AlinLib.LOG.log("drag %s | x:%s  y: %s dx: %s dy: %s", window.screen.getTitle().getString(), mouseX, mouseY, dragX, dragY);
                    ret = window.screen.mouseDragged(
                            //#if MC < 12110
                            //$$mouseX-(x+3), mouseY-(y+19), button
                            //#else
                            new MouseButtonEvent(mouseX-(x+3), mouseY-(y+19), mouseButtonEvent.buttonInfo())
                            //#endif
                            , dragX, dragY);
                }
                DesktopScreen.windows.getLast().active = false;
                window.active = true;
                DesktopScreen.windows.remove(i);
                DesktopScreen.windows.addLast(window);
                return ret;
            }
        }
        //#if MC < 12110
        //$$return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        //#else
        return super.mouseDragged(mouseButtonEvent, dragX, dragY);
        //#endif

    }
    @Override
    public void onFilesDrop(List<Path> list) {
        for(Window window : DesktopScreen.windows){
            if(window.active && window.visible && window.pinned) {
                window.screen.onFilesDrop(list);
            }
        }
    }
    @Override
    //#if MC < 12110
    //$$public boolean charTyped(char c, int i) {
    //#else
    public boolean charTyped(CharacterEvent characterEvent) {
        //#endif
        if(DesktopScreen.windows.isEmpty()) return super.charTyped(
                //#if MC < 12110
                //$$c, i
                //#else
                characterEvent
                //#endif
        );
        else for(Window window : DesktopScreen.windows){
            if(window.active && window.visible && window.pinned) {
                return window.screen.charTyped(
                        //#if MC < 12110
                        //$$c, i
                        //#else
                        characterEvent
                        //#endif
                );
            }
        }
        return super.charTyped(
                //#if MC < 12110
                //$$c, i
                //#else
                characterEvent
                //#endif
        );
    }

    @Override
    public boolean isMouseOver(double d, double e) {
        boolean ret = false;
        for(int i = DesktopScreen.windows.size()-1; i>=0; i--){
            Window window = DesktopScreen.windows.get(i);
            if(window.x < d && d < window.x+window.width && window.y < e && e < window.y+window.height && window.visible && window.pinned){
                int x = (int) window.x;
                int y = (int) window.y;
                ret = window.screen.isMouseOver(d-(x+3), e-(y+19));
                if(ret) break;
            }
        }
        return ret || super.isMouseOver(d, e);
    }

    @Override
    //#if MC < 12110
    //$$public boolean mouseReleased(double d, double e, int j) {
    //#else
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        double d = mouseButtonEvent.x();
        double e = mouseButtonEvent.y();
        int j = mouseButtonEvent.button();
        //#endif
        for(int i = DesktopScreen.windows.size()-1; i>=0; i--){
            Window window = DesktopScreen.windows.get(i);
            if (j == InputConstants.MOUSE_BUTTON_LEFT) {
                if(window.isDragging) window.setDragging(false);
                else if(window.isResized) window.setResized(false);
                else if(window.isScreenDragging) window.setScreenDragging(false);
            } else if(window.isDragging || window.isResized || window.isScreenDragging) break;
            if(window.x < d && d < window.x+window.width && window.y < e && e < window.y+window.height && window.visible && window.active && window.pinned){
                int x = (int) window.x;
                int y = (int) window.y;
                window.screen.mouseReleased(
                        //#if MC < 12110
                        //$$d-(x+3), e-(y+19), j
                        //#else
                        new MouseButtonEvent(d-(x+3), e-(y+19), mouseButtonEvent.buttonInfo())
                        //#endif
                );
            }
        }
        //#if MC < 12110
        //$$return super.mouseReleased(d, e, j);
        //#else
        return super.mouseReleased(mouseButtonEvent);
        //#endif
    }

    @Inject(method = "mouseScrolled", at=@At("HEAD"), cancellable = true)
    public void mouseScrolled(double d, double e, double f, double g, CallbackInfoReturnable<Boolean> cir) {
            for (int i = DesktopScreen.windows.size() - 1; i >= 0; i--) {
                Window window = DesktopScreen.windows.get(i);
                if(window.isDragging || window.isResized || window.isScreenDragging) continue;
                if (window.x < d && d < window.x + window.width && window.y < e && e < window.y + window.height && window.visible && window.pinned) {
                    int x = (int) window.x;
                    int y = (int) window.y;
//                AlinLib.LOG.log("click %s | x:%s  y: %s", window.screen.getTitle().getString(), d, e);
                    cir.setReturnValue(window.screen.mouseScrolled(d - (x + 3), e - (y + 19), f, g));
                }
            }
    }
    @Unique
    private static int buttonSize = 10;
    @Inject(method = "mouseClicked", at=@At("HEAD"), cancellable = true)
    public void mouseClicked(
            //#if MC < 12110
            //$$double d, double e, int j
            //#else
            MouseButtonEvent mouseButtonEvent, boolean b, CallbackInfoReturnable<Boolean> cir
            //#endif
    ) {
        //#if MC >= 12110
        double d = mouseButtonEvent.x();
        double e = mouseButtonEvent.y();
        int j = mouseButtonEvent.button();
        //#endif

        for(int i = DesktopScreen.windows.size()-1; i>=0; i--){
            Window window = DesktopScreen.windows.get(i);
            if(window.x < d && d < window.x+window.width && window.y < e && e < window.y+window.height && window.visible && window.pinned){
                int x = (int) window.x;
                int y = (int) window.y;
                int width = (int) window.width;
                DesktopScreen.windows.getLast().active = false;
                window.active = true;
                if(window.getButtons() < 2 && j == InputConstants.MOUSE_BUTTON_LEFT){
                    int xb = x+width-5-buttonSize;
                    if(xb < d && d < xb+buttonSize && y+5 < e && e < y+5+buttonSize) {
                        removeWindow(window);
                        if(!DesktopScreen.windows.isEmpty() && DesktopScreen.windows.getLast().visible) DesktopScreen.windows.getLast().active = true;
                        SoundUtils.click();
                        cir.setReturnValue(true);
                    }
                    xb -= (2+buttonSize);
                    if(window.getButtons() == 0 && window.isResizable()){
                        if(xb < d && d < xb+buttonSize && y+5 < e && e < y+5+buttonSize) {
                            window.changeMax(this.width, this.height);
                            window.screen.resize((int) window.width - 6, (int) window.height - 22);
                            SoundUtils.click();
                            cir.setReturnValue(true);
                        }
                        xb -= buttonSize;
                        if(xb < d && d < xb+buttonSize && y+5 < e && e < y+5+buttonSize){
                            DesktopScreen.windows.remove(i);
                            window.visible = false;
                            window.active = false;
                            DesktopScreen.windows.addFirst(window);
                            SoundUtils.click();
                            cir.setReturnValue(true);
                        }
                    }
                }
                DesktopScreen.windows.remove(i);
                DesktopScreen.windows.addLast(window);
//                AlinLib.LOG.log("click %s | x:%s  y: %s", window.screen.getTitle().getString(), d, e);
                cir.setReturnValue(window.screen.mouseClicked(
                        //#if MC < 12110
                        //$$d-(x+3), e-(y+19), j
                        //#else
                        new MouseButtonEvent(d-(x+3), e-(y+19), mouseButtonEvent.buttonInfo()), b
                        //#endif
                ));
                return;
            }
        }
        if(!DesktopScreen.windows.isEmpty()) DesktopScreen.windows.getLast().active = false;
    }

    @Override
    public void tick() {
        super.tick();
        try {
            for(Window window : DesktopScreen.windows){
                if(window.screen instanceof StatsScreen) ((StatsScreen) window.screen).onStatsUpdated();
                window.screen.tick();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
