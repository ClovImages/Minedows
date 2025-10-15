package ru.kelcu.windows.screens.apps;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.mixin.ExampleTextureMixin;
import ru.kelcu.windows.utils.WinColors;
import ru.kelcu.windows.utils.WindowUtils;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.buttons.Button;
import ru.kelcuprum.alinlib.gui.components.editbox.EditBox;

import java.awt.*;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class BrowserScreen extends Screen {
    private static final int BROWSER_DRAW_OFFSET = 1;
    private static final int BROWSER_DRAW_TITLE_OFFSET = 24;

    public MCEFBrowser browser;
    public String titleTab = "";
    public String tooltipHell = "";
    protected ResourceLocation exampleLocation;
    protected ExampleTexture exampleTexture;

    public static class ExampleTexture extends AbstractTexture {
        protected final ExampleGlTexture glTexture;

        public ExampleTexture(int id, @NotNull String label) {
            this.glTexture = new ExampleGlTexture(5, label, TextureFormat.RGBA8, 100, 100, 1, 1, id);
            this.glTexture.setTextureFilter(FilterMode.NEAREST, false);
            this.texture = this.glTexture;
            GpuDevice device = RenderSystem.getDevice();
            this.textureView = device.createTextureView(this.texture);
        }

        public void setId(int id) {
            this.glTexture.setGlId(id);
        }

        public void setWidth(int width) {
            this.glTexture.setWidth(width);
        }

        public void setHeight(int height) {
            this.glTexture.setHeight(height);
        }

    }
    public static class ExampleGlTexture extends GlTexture {

        protected int width;
        protected int height;
        public ExampleGlTexture(int usage, String label, TextureFormat texFormat, int width, int height, int depthOrLayers, int mipLevels, int glId) {
            super(usage, label, texFormat, width, height, depthOrLayers, mipLevels, glId);
            this.width = width;
            this.height = height;
        }

        @Override
        public int getWidth(int i) {
            return this.width >> i;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        @Override
        public int getHeight(int i) {
            return this.height >> i;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        @Override
        public int glId() {
            return this.id;
        }

        public void setGlId(int id) {
            ((ExampleTextureMixin) this).setId(id);
        }

    }
    public static boolean isFirstHuy = true;

    public BrowserScreen() {
        super(Component.literal("Clovium"));
        if(isFirstHuy){
            isFirstHuy = false;
                MCEF.getSettings().setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) MCEF/2 Clovium/69 Chrome/119.0.0.0 Safari/537.36");
        }
    }

    public EditBox editBox;
    public Button buttonReload;

    @Override
    protected void init() {
        super.init();
        if (browser == null) {
            exampleLocation = ResourceLocation.fromNamespaceAndPath("example", "frame_" + UUID.randomUUID().toString().replace("-", ""));
            exampleTexture = new ExampleTexture(-1, this.exampleLocation.toString());
            String url = Windows.config.getString("BROWSER.HOME_PAGE", "https://www.google.com");
            boolean transparent = false;
            browser = MCEF.createBrowser(url, transparent);
            resizeBrowser();
            Minecraft.getInstance().getTextureManager().register(this.exampleLocation, this.exampleTexture);
        }
        int x = 0;
        int y = 0;
        addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.browser.back"), (s) -> {
            if(browser.canGoBack()) browser.goBack();
        }).setSprite(WinColors.getLightIcon("textures/browser/back")).setSize(20, 20).setPosition(2, 1).setStyle(Windows.minedowsStyle).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.browser.forward"), (s) -> {
            if(browser.canGoForward()) browser.goForward();
        }).setSprite(WinColors.getLightIcon("textures/browser/forward")).setSize(20, 20).setPosition(22, 1).setStyle(Windows.minedowsStyle).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.browser.home"), (s) -> {
            browser.loadURL(Windows.config.getString("BROWSER.HOME_PAGE", "https://www.google.com"));
        }).setSprite(WinColors.getLightIcon("textures/browser/home")).setSize(20, 20).setPosition(46, 1).setStyle(Windows.minedowsStyle).build());
        buttonReload = (Button) addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.browser.reload"), (s) -> {
            if(browser.isLoading()) browser.stopLoad();
            else browser.reload();
        }).setSprite(WinColors.getLightIcon("textures/browser/reload")).setSize(20, 20).setPosition(70, 1).setStyle(Windows.minedowsStyle).build());
        editBox = new ru.kelcu.windows.screens.components.alinlib.EditBox(new EditBoxBuilder().setValue(browser.getURL()).setSize(width-96, 20).setPosition(94, 1));
        addRenderableWidget(editBox);
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
        this.exampleTexture.setId(this.browser.getRenderer().getTextureID());
        this.exampleTexture.setWidth(this.width);
        this.exampleTexture.setHeight(this.height);
    }

    @Override
    public void resize(Minecraft minecraft, int i, int j) {
        super.resize(minecraft, i, j);
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
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        this.updateFrame();
        WindowUtils.welcomeToWhiteSpace(guiGraphics, 0, BROWSER_DRAW_TITLE_OFFSET, width, height-BROWSER_DRAW_TITLE_OFFSET);
        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                this.exampleLocation,
                BROWSER_DRAW_OFFSET, BROWSER_DRAW_OFFSET+BROWSER_DRAW_TITLE_OFFSET,
                0, 0,
                width - BROWSER_DRAW_OFFSET * 2, height - BROWSER_DRAW_TITLE_OFFSET - BROWSER_DRAW_OFFSET * 2,
                width - BROWSER_DRAW_OFFSET * 2, height - BROWSER_DRAW_TITLE_OFFSET - BROWSER_DRAW_OFFSET * 2,
                Color.white.getRGB()
        );
        if(!tooltipHell.isEmpty()) guiGraphics.setTooltipForNextFrame(Component.literal(tooltipHell), i, j);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(mouseX > BROWSER_DRAW_TITLE_OFFSET) setFocused(null);
        browser.sendMousePress(mouseX(mouseX), mouseY(mouseY), button);
        browser.setFocus(true);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        browser.sendMouseRelease(mouseX(mouseX), mouseY(mouseY), button);
        browser.setFocus(true);
        return super.mouseReleased(mouseX, mouseY, button);
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
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        browser.sendMouseWheel(mouseX(mouseX), mouseY(mouseY), scrollY, 0);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(editBox.isFocused()){
            if(keyCode == GLFW.GLFW_KEY_ENTER) {
                updateUrl();
                setFocused(false);
                setFocused(null);
            }
            return editBox.keyPressed(keyCode, scanCode, modifiers);
        }
        browser.sendKeyPress(keyCode, scanCode, modifiers);
        browser.setFocus(true);
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        } else return false;
    }

    public void updateUrl(){
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
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if(editBox.isFocused()) return super.keyReleased(keyCode, scanCode, modifiers);
        browser.sendKeyRelease(keyCode, scanCode, modifiers);
        browser.setFocus(true);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if(editBox.isFocused()) return super.charTyped(codePoint, modifiers);
        if (codePoint == (char) 0) return false;
        browser.sendKeyTyped(codePoint, modifiers);
        browser.setFocus(true);
        return super.charTyped(codePoint, modifiers);
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
}
