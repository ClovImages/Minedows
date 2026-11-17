package ru.kelcu.windows.screens.apps.browser;

import com.mojang.blaze3d.platform.InputConstants;
import de.keksuccino.rinku.Rinku;
import de.keksuccino.rinku.RinkuBrowser;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.fabricmc.fabric.mixin.client.gametest.input.WindowMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
//#if MC >= 12110
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//#endif
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefAuthCallback;
import org.cef.callback.CefCallback;
import org.cef.callback.CefFileDialogCallback;
import org.cef.callback.CefJSDialogCallback;
import org.cef.handler.*;
import org.cef.misc.BoolRef;
import org.cef.network.CefRequest;
import org.jetbrains.annotations.NotNull;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.components.Window;
import ru.kelcu.windows.components.builders.WindowBuilder;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcu.windows.screens.components.VerticalConfigureScrolWidget;
import ru.kelcu.windows.utils.WinColors;
import ru.kelcu.windows.utils.WindowUtils;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.components.buttons.Button;
import ru.kelcuprum.alinlib.gui.components.editbox.EditBox;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;
import ru.kelcuprum.alinlib.info.Player;

import java.awt.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class BrowserScreen extends Screen {
    private static final int BROWSER_DRAW_OFFSET = 1;
    private int BROWSER_DRAW_TITLE_OFFSET = 24;
    public static boolean waitAccessToken = false;
    public RinkuBrowser browser;
    public String titleTab = "";
    public String tooltipHell = "";
    public String statusHell = "";
    public static boolean isFirstHuy = true;
    public String url;
    public ErrorPage errorPage;
    public BrowserScreen() {
        this(Windows.config.getString("BROWSER.HOME_PAGE", "https://www.google.com"));
    }
    public BrowserScreen(String url) {
        super(Component.literal("Clovium"));
        if(!Rinku.isInitialized()) return;
        if(isFirstHuy){
            isFirstHuy = false;
            if(Rinku.getSettings().getUserAgent() != null && !Rinku.getSettings().getUserAgent().toLowerCase().contains("clovium")) Rinku.getSettings().setUserAgent(Rinku.getSettings().getUserAgent()+ " Clovium/67");
            Rinku.getSettings().setUseCache(true);
            DialogsHandler handler = new DialogsHandler(this);
            Rinku.getClient().getHandle().addDialogHandler(handler);
            Rinku.getClient().getHandle().addJSDialogHandler(handler);
            Rinku.getClient().getHandle().addLoadHandler(handler);
            Rinku.getClient().getHandle().addRequestHandler(handler);
        }
        this.url = url;
    }
    public static class DialogsHandler implements CefDialogHandler, CefJSDialogHandler, CefRequestHandler, CefLoadHandler {
        public BrowserScreen screen;
        public DialogsHandler(BrowserScreen screen){
            this.screen = screen;
        }
        @Override
        public boolean onFileDialog(CefBrowser browser, FileDialogMode mode, String title, String defaultFilePath, Vector<String> acceptFilters, CefFileDialogCallback callback) {
            return false;
        }


        @Override
        public boolean onJSDialog(CefBrowser browser, String origin_url, JSDialogType dialog_type, String message_text, String default_prompt_text, CefJSDialogCallback callback, BoolRef suppress_message) {
            switch (dialog_type) {
                case JSDIALOGTYPE_CONFIRM, JSDIALOGTYPE_PROMPT -> {
                    if(waitAccessToken){
                        callback.Continue(Player.isLicenseAccount(), Minecraft.getInstance().getUser().getAccessToken());
                    } else DesktopScreen.addWindow(new WindowBuilder().setSize(600, 120).setScreen(new BrowserDialogScreen(dialog_type, message_text, default_prompt_text, callback, suppress_message)).build());
                }
                default -> {
                    new ToastBuilder().setIcon(GuiUtils.getResourceLocation("windows", "textures/browser/icon.png")).setTitle(Component.literal("Clovium")).setMessage(Component.literal(message_text)).buildAndShow();
                }
            }
            return true;
        }

        @Override
        public boolean onBeforeUnloadDialog(CefBrowser browser, String message_text, boolean is_reload, CefJSDialogCallback callback) {
            return false;
        }

        @Override
        public void onResetDialogState(CefBrowser browser) {

        }

        @Override
        public void onDialogClosed(CefBrowser browser) {

        }

        @Override
        public boolean onBeforeBrowse(CefBrowser browser, CefFrame frame, CefRequest request, boolean user_gesture, boolean is_redirect) {
            return false;
        }

        @Override
        public boolean onOpenURLFromTab(CefBrowser browser, CefFrame frame, String target_url, boolean user_gesture) {
            DesktopScreen.addWindow(new WindowBuilder().setIcon(GuiUtils.getResourceLocation("windows", "textures/browser/icon.png")).setScreen(new BrowserScreen(target_url)).build());
            AlinLib.LOG.log(target_url);
            return true;
        }

        @Override
        public CefResourceRequestHandler getResourceRequestHandler(CefBrowser browser, CefFrame frame, CefRequest request, boolean isNavigation, boolean isDownload, String requestInitiator, BoolRef disableDefaultHandling) {
            return null;
        }

        @Override
        public boolean getAuthCredentials(CefBrowser browser, String origin_url, boolean isProxy, String host, int port, String realm, String scheme, CefAuthCallback callback) {
            return false;
        }

        @Override
        public boolean onCertificateError(CefBrowser browser, CefLoadHandler.ErrorCode cert_error, String request_url, CefCallback callback) {
            return false;
        }

        @Override
        public void onRenderProcessTerminated(CefBrowser browser, TerminationStatus status, int error_code, String error_string) {

        }

        @Override
        public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {

        }

        @Override
        public void onLoadStart(CefBrowser browser, CefFrame frame, CefRequest.TransitionType transitionType) {

        }

        @Override
        public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {

        }

        @Override
        public void onLoadError(CefBrowser browser, CefFrame frame, ErrorCode errorCode, String errorText, String failedUrl) {
            ((BrowserScreen) (getWindowByBrowser(browser).screen)).errorPage = new ErrorPage(errorCode, errorText, failedUrl);
        }
    }
    public record ErrorPage(CefLoadHandler.ErrorCode errorCode, String errorText, String failedUrl){};

    public EditBox editBox;
    public Button buttonReload;

    @Override
    protected void init() {
        super.init();
        if (browser == null) {
            boolean transparent = false;
            browser = Rinku.createBrowser(url, transparent);
            resizeBrowser();
        }
        int x = 0;
        int y = 1;
        addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.browser.back"), (s) -> {
            errorPage = null;
            if(browser.canGoBack()) browser.goBack();
        }).setSprite(WinColors.getLightIcon("textures/browser/back")).setSize(20, 20).setPosition(2, y).setStyle(Windows.minedowsStyle).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.browser.forward"), (s) -> {
            errorPage = null;
            if(browser.canGoForward()) browser.goForward();
        }).setSprite(WinColors.getLightIcon("textures/browser/forward")).setSize(20, 20).setPosition(22, y).setStyle(Windows.minedowsStyle).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.browser.home"), (s) -> {
            errorPage = null;
            browser.loadURL(Windows.config.getString("BROWSER.HOME_PAGE", "https://www.google.com"));
        }).setSprite(WinColors.getLightIcon("textures/browser/home")).setSize(20, 20).setPosition(46, y).setStyle(Windows.minedowsStyle).build());
        editBox = new ru.kelcu.windows.screens.components.alinlib.EditBox(new EditBoxBuilder().setValue(browser.getURL()).setSize(width-96, 20).setPosition(70, y));
        addRenderableWidget(editBox);
        buttonReload = (Button) addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.browser.reload"), (s) -> {
            errorPage = null;
            if(browser.isLoading()) browser.stopLoad();
            else browser.reload();
        }).setSprite(WinColors.getLightIcon("textures/browser/reload")).setSize(20, 20).setPosition(width-22, y).setStyle(Windows.minedowsStyle).build());
    }

    private int mouseX(double x) {
        return (int) ((x - BROWSER_DRAW_OFFSET) * minecraft.getWindow().getGuiScale());
    }

    private int mouseY(double y) {
        return (int) ((y - BROWSER_DRAW_TITLE_OFFSET - BROWSER_DRAW_OFFSET) * minecraft.getWindow().getGuiScale());
    }

    private int scaleX(double x) {
        return (int) ((x - BROWSER_DRAW_OFFSET * 2) * minecraft.getWindow().getGuiScale());
    }

    private int scaleY(double y) {
        return (int) ((y - BROWSER_DRAW_TITLE_OFFSET - BROWSER_DRAW_OFFSET * 2) * minecraft.getWindow().getGuiScale());
    }

    private void resizeBrowser() {
        if (width > 100 && height > 100) {
            browser.resize(scaleX(width), scaleY(height));
        }
    }

    private void updateFrame() {
    }

    @Override
    public void resize(int i, int j) {
        super.resize(i, j);
        resizeBrowser();
    }

    @Override
    public void onClose() {
        browser.close();
        super.onClose();
    }

    @Override
    public void onFilesDrop(List<Path> list) {

        super.onFilesDrop(list);
    }

    @Override
    public void tick() {
        super.tick();
        if(buttonReload != null && browser != null) ((ButtonBuilder) buttonReload.builder).setIcon(browser.isLoading() ? GuiUtils.getResourceLocation("windows", "textures/browser/cancel.png") : GuiUtils.getResourceLocation("windows", "textures/browser/reload.png"));
    }
    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        if(!fullscreen) super.extractRenderState(guiGraphics, i, j, f);
        this.updateFrame();
        WindowUtils.welcomeToWhiteSpace(guiGraphics, 0, BROWSER_DRAW_TITLE_OFFSET, width, height-BROWSER_DRAW_TITLE_OFFSET);

        if(errorPage != null){
            int x = 5, y = 5 + BROWSER_DRAW_TITLE_OFFSET;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/start/icons/error.png"), x, y, 0, 0, 32, 32, 32, 32);
            guiGraphics.text(font, Component.translatable("minedows.browser.error_page"), x+37, y+16-(font.lineHeight/2), Windows.minedowsStyle.getTextColor(TextBuilder.TYPE.BLOCKQUOTE), Windows.minedowsStyle.textShadow(TextBuilder.TYPE.BLOCKQUOTE));
            int[] colors = WinColors.getHorizontalRuleColors();
            guiGraphics.fill(x+37, y+30, width-4, y+32, colors[0]);
            guiGraphics.fill(x+37, y+30, width-4, y+31, colors[1]);
            y+=37;
            guiGraphics.text(font, String.format("%s: %s", errorPage.errorCode.getCode(), errorPage.errorCode.name()), x, y, Windows.minedowsStyle.getTextColor(TextBuilder.TYPE.BLOCKQUOTE), Windows.minedowsStyle.textShadow(TextBuilder.TYPE.BLOCKQUOTE));
            y+=font.lineHeight+2;
            guiGraphics.text(font, Component.empty().append(errorPage.failedUrl).setStyle(Style.EMPTY.withItalic(true)), x, y, Windows.minedowsStyle.getTextColor(false), Windows.minedowsStyle.textShadow(TextBuilder.TYPE.BLOCKQUOTE));
        }

        if(browser.isTextureReady() && errorPage == null) guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                browser.getTextureIdentifier(),
                BROWSER_DRAW_OFFSET, BROWSER_DRAW_OFFSET+BROWSER_DRAW_TITLE_OFFSET,
                0, 0,
                width - BROWSER_DRAW_OFFSET * 2, height - BROWSER_DRAW_TITLE_OFFSET - BROWSER_DRAW_OFFSET * 2,
                width - BROWSER_DRAW_OFFSET * 2, height - BROWSER_DRAW_TITLE_OFFSET - BROWSER_DRAW_OFFSET * 2,
                Color.white.getRGB()
        );

        if(!tooltipHell.isEmpty()) guiGraphics.setTooltipForNextFrame(Component.literal(tooltipHell), i, j);
        if(!statusHell.isEmpty()) {
            String u = statusHell.length() < 48 ? statusHell : statusHell.substring(0, 48)+"...";
            Windows.minedowsStyle.renderTooltip(guiGraphics, 5, height-25, 12+font.width(u), 20);
            guiGraphics.text(font, u, 11, height-19, Windows.minedowsStyle.getTextColor(TextBuilder.TYPE.BLOCKQUOTE), Windows.minedowsStyle.textShadow(TextBuilder.TYPE.BLOCKQUOTE));
        }
        if(System.currentTimeMillis() - zoomView < 5000){
            guiGraphics.setTooltipForNextFrame(Component.literal(String.format("%.2f", (browser.getZoomLevel()*100))+"%"), i, j);
        }
        if(browser.isLoading()) guiGraphics.requestCursor(CursorTypes.ARROW);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean b) {
        double mouseX = mouseButtonEvent.x();
        double mouseY = mouseButtonEvent.y();
        int button = mouseButtonEvent.button();
        //#endif
        if(mouseX > BROWSER_DRAW_TITLE_OFFSET) setFocused(null);
        browser.sendMousePress(mouseX(mouseX), mouseY(mouseY), button);
        browser.setFocus(true);
        if(fullscreen) return true;
        return super.mouseClicked(
                //#if MC < 12110
                //$$mouseX, mouseY, button
                //#else
                mouseButtonEvent, b
                //#endif
                );
    }

    @Override
    public boolean mouseReleased(
            //#if MC < 12110
            //$$double mouseX, double mouseY, int button) {
            //#else
            MouseButtonEvent mouseButtonEvent
    ) {
        double mouseX = mouseButtonEvent.x();
        double mouseY = mouseButtonEvent.y();
        int button = mouseButtonEvent.button();
        //#endif
        browser.sendMouseRelease(mouseX(mouseX), mouseY(mouseY), button);
        browser.setFocus(true);
        return super.mouseReleased(
                //#if MC < 12110
                //$$mouseX, mouseY, button
                //#else
                mouseButtonEvent
                //#endif
        );
    }



    @Override
    public @NotNull Component getTitle() {
        if(browser.getFocusedFrame() == null) return super.getTitle();
        return browser.isLoading() ? Component.literal("Loading...") : titleTab.isBlank() ? super.getTitle() : Component.empty().append(titleTab).append(" — ").append(super.getTitle());//super.getTitle();
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        browser.sendMouseMove(mouseX(mouseX), mouseY(mouseY));
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(
        //#if MC < 12110
        //$$double mouseX, double mouseY, int button, double dragX, double dragY
        //#else
        MouseButtonEvent mouseButtonEvent, double dragX, double dragY
        //#endif
    ) {
        return super.mouseDragged(
                //#if MC < 12110
                //$$mouseX, mouseY, button, dragX,  dragY
                //#else
                mouseButtonEvent, dragX, dragY
                //#endif
        );
    }
    long zoomView = 0;
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if(minecraft.hasControlDown()){
            zoomView = System.currentTimeMillis();
            double d = browser.getZoomLevel();
            d += scrollY < 0 ? -0.05 : 0.05;
            browser.setZoomLevel(d);
        } else browser.sendMouseWheel(mouseX(mouseX), mouseY(mouseY), scrollY, 0);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
    public boolean fullscreen = false;
    @Override
    //#if MC < 12110
    //$$public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    //#else
    public boolean keyPressed(KeyEvent keyEvent) {
        int keyCode = keyEvent.key();
        int scanCode = keyEvent.keycode();
        int modifiers = keyEvent.modifiers();
        //#endif
        if(keyCode == InputConstants.KEY_F10){
            fullscreen = !fullscreen;
            BROWSER_DRAW_TITLE_OFFSET = fullscreen ? 0 : 24;
            rebuildWidgets();
            resizeBrowser();
            return true;
        }
        if(editBox.isFocused()){
            if(keyEvent.isSelection()) {
                updateUrl();
                setFocused(false);
                setFocused(null);
            }
            return editBox.keyPressed(
                    //#if MC < 12110
                    //$$keyCode, scanCode, modifiers
                    //#else
                    keyEvent
                    //#endif
            );
        }
        browser.sendKeyPress(keyCode, scanCode, modifiers);
        browser.setFocus(true);
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        } else return false;
    }

    public void updateUrl(){
        errorPage = null;
        String url = editBox.getValue();
        if(url.toLowerCase().contains("vk.ru") || url.toLowerCase().contains("vk.com")){
            double r = Math.random();
            if(r<0.25) throw new RuntimeException("Какие нафиг ВК? Я сказал Clovisoft");
        }
        String[] args = url.split("\\.");
        boolean search = false;
        if(!url.contains("://")){
            if(args.length >= 2){
                for(String hell : args){
                    if(hell.matches("[ ,!]")) search = true;
                }
            } else search = true;
        }
        browser.loadURL(search ? getSearchURL(url) : url);
    }

    @Override
    //#if MC < 12110
    //$$public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
    //#else
    public boolean keyReleased(KeyEvent keyEvent) {
        int keyCode = keyEvent.key(); int scanCode = keyEvent.keycode(); int modifiers = keyEvent.modifiers();
        //#endif
        if(editBox.isFocused()) return super.keyReleased(
                //#if MC < 12110
                //$$keyCode, scanCode, modifiers
                //#else
                keyEvent
                //#endif
        );
        browser.sendKeyRelease(keyCode, scanCode, modifiers);
        browser.setFocus(true);
        return super.keyReleased(
                //#if MC < 12110
                //$$keyCode, scanCode, modifiers
                //#else
                keyEvent
                //#endif
        );
    }

    @Override
    public boolean charTyped(
            //#if MC < 12110
            //$$ char codePoint, int modifiers) {
            //#else
            CharacterEvent characterEvent) {
        char codePoint = characterEvent.codepointAsString().charAt(0);
//        int modifiers = characterEvent.codepoint();
        //#endif

        if(editBox.isFocused()) return super.charTyped(
                //#if MC < 12110
                //$$ codePoint, modifiers
                //#else
                characterEvent
                //#endif
        );
        if (codePoint == (char) 0) return false;
        browser.sendKeyTyped(codePoint, 0);
        browser.setFocus(true);
        return super.charTyped(
                //#if MC < 12110
                //$$ codePoint, modifiers
                //#else
                characterEvent
                //#endif
        );
    }

    public static String getSearchURL(String query){
        return switch (Windows.config.getNumber("BROWSER.SEARCH", 0).intValue()){
            case 4 -> String.format(Windows.config.getString("BROWSER.SEARCH.CUSTOM", "https://google.com/search?q=%s"), query);
            case 3 -> String.format("https://www.startpage.com/do/dsearch?q=%s", query);
            case 2 -> String.format("https://duckduckgo.com/?q=%s", query);
            case 1 -> String.format("https://yandex.ru/search?text=%s", query);
            default -> String.format("https://google.com/search?q=%s", query);
        };
    }
    public static ru.kelcu.windows.components.Window getWindowByBrowser(CefBrowser browser){
        for(ru.kelcu.windows.components.Window window : DesktopScreen.windows){
            if(window.screen instanceof BrowserScreen){
                if(((BrowserScreen) window.screen).browser.getIdentifier() == browser.getIdentifier()) return window;
            }
        }
        return null;
    }
}
