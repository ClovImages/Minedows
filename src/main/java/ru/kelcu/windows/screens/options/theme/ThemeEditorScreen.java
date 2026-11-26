package ru.kelcu.windows.screens.options.theme;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.components.Cursor;
import ru.kelcu.windows.components.Window;
import ru.kelcu.windows.components.builders.WindowBuilder;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcu.windows.screens.apps.ExplorerScreen;
import ru.kelcu.windows.screens.apps.NotepadScreen;
import ru.kelcu.windows.screens.components.LabelWidget;
import ru.kelcu.windows.screens.components.alinlib.ButtonBoolean;
import ru.kelcu.windows.screens.options.BackgroundSettings;
import ru.kelcu.windows.screens.options.LabelsPanelScreen;
import ru.kelcu.windows.utils.SoundUtils;
import ru.kelcu.windows.utils.ThemeManager;
import ru.kelcu.windows.utils.WinColors;
import ru.kelcu.windows.utils.WindowUtils;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.selector.SelectorBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.components.text.TextBox;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;

import static ru.kelcu.windows.Windows.config;
import static ru.kelcu.windows.Windows.minedowsStyle;
import static ru.kelcu.windows.screens.DesktopScreen.getGrayColor;
import static ru.kelcuprum.alinlib.gui.Colors.*;
import static ru.kelcuprum.alinlib.gui.GuiUtils.interpolate;

public class ThemeEditorScreen extends Screen {
    public ThemeEditorScreen() {
        super(Component.translatable("minedows.themes.editor"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, a);
        int y = 10;
//        WindowUtils.welcomeToWhiteSpace(guiGraphics, 5, y, width-160, height-45);
//        guiGraphics.fill(6, y+1, width-156, height-6, Windows.config.getNumber("WALLPAPER.COLOR", 0xFF177f96).intValue());
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractBackground(guiGraphics, mouseX, mouseY, a);
        int y = 40;
        BackgroundSettings.extractWallpaper(guiGraphics, width-160, height-45, 5, y, minecraft);
        guiGraphics.enableScissor(5, y, width-155, height-5);
        if(active_window != null){
            renderWindow(guiGraphics, active_window, 97, y+30, mouseX, mouseY, a);
        }
        if(window != null){
            renderWindow(guiGraphics, window, 80, y+20, mouseX, mouseY, a);
        }
        guiGraphics.disableScissor();
    }
    Window window;
    Window active_window;
    @Override
    protected void init() {
        super.init();
        if(window == null){
            window = new WindowBuilder(Component.translatable("minedows.notepad")).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/notepad.png")).setScreen(new NotepadScreen()).setSize(200, 167).build();
            window.screen.init((int) window.width - 6, (int) window.height - 22);
        }
        if(active_window == null){
            active_window = new WindowBuilder(Component.translatable("minedows.explorer.user_folder")).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/folder.png")).setScreen(new ExplorerScreen()).setSize(267, 200).build();
            active_window.active = false;
            active_window.screen.init((int) active_window.width - 6, (int) active_window.height - 22);
        }
        int lY = 50;
        LabelWidget l1 = new LabelWidget(15, lY, 40, DesktopScreen.getLabelActions().getFirst());
        addRenderableOnly(l1);
        lY+= (l1.getHeight() + 7);
        LabelWidget l2 = new LabelWidget(15, lY, 40, DesktopScreen.getLabelActions().get(1));
        addRenderableOnly(l2);
        lY+= (l2.getHeight() + 7);
        LabelWidget l3 = new LabelWidget(15, lY, 40, DesktopScreen.getLabelActions().get(2));
        addRenderableOnly(l3);
        lY+= (l3.getHeight() + 7);

        int x = 5;
        int y = 10;
        addRenderableWidget(new SelectorBuilder(Component.translatable("minedows.system_options.themes.theme")).setValue(ThemeManager.getThemePosition(ThemeManager.getSelectedTheme()))
                .setList(ThemeManager.getThemesNames()).setOnPress((s) -> {
                    config.setString("THEME", ThemeManager.getThemesID()[s.getPosition()]);
                }).setStyle(Windows.minedowsStyle).setPosition(x, y).setWidth(300).build());
        x+=305;
        addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.themes.editor.create"), (b) -> {
            if(Math.random() < 0.25){
                new ToastBuilder().setTitle(Component.literal("Meow?")).setMessage(Component.literal("Waiting for something to happen?")).setIcon("windows", "textures/meow.png").buildAndShow();
            }
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.CAT_AMBIENT_BABY.value(), 1f, 1f));
        }).setStyle(minedowsStyle).setPosition(x, y).setWidth(155).build());

        x=width-149;
        addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.control.background"), (b) -> {
            DesktopScreen.addWindow(new WindowBuilder().setScreen(new BackgroundSettings()).setSize(325, 320).setResizable(false).setButtons(1).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/background.png")).build());
        }).setStyle(minedowsStyle).setPosition(x, y).setWidth(145).build());
        y+=29;
        addRenderableWidget(new HorizontalRuleBuilder(Component.translatable("minedows.control.colors")).setStyle(Windows.minedowsStyle).setPosition(x, y).setWidth(145).build());
        y+=15;
        addRenderableWidget(new EditBoxBuilder(Component.translatable("minedows.control.background.theme.color")).setColor(0xFFcbcbcb).setConfig(Windows.config, "THEME.MAIN_COLOR").setStyle(Windows.minedowsStyle).setPosition(x, y).setWidth(145).build());
        y+=25;
        addRenderableWidget(new EditBoxBuilder(Component.translatable("minedows.control.background.theme.active_color")).setColor(0xFFAFC9D1).setConfig(Windows.config, "THEME.ACTIVE_COLOR").setStyle(Windows.minedowsStyle).setPosition(x, y).setWidth(145).build());
        y+=25;
        addRenderableWidget(new HorizontalRuleBuilder(Component.translatable("minedows.control.start")).setStyle(Windows.minedowsStyle).setPosition(x, y).setWidth(145).build());
        y+=15;
        addRenderableWidget(new EditBoxBuilder(Component.translatable("minedows.control.start.color_start")).setColor(-14644786).setConfig(Windows.config, "START.GRADIENT.START").setStyle(Windows.minedowsStyle).setPosition(x, y).setWidth(145).build());
        y+=25;
        addRenderableWidget(new EditBoxBuilder(Component.translatable("minedows.control.start.color_end")).setColor(-16777088).setConfig(Windows.config, "START.GRADIENT.END").setStyle(Windows.minedowsStyle).setPosition(x, y).setWidth(145).build());
        y+=25;
        addRenderableWidget(new HorizontalRuleBuilder(Component.translatable("minedows.control.title")).setStyle(Windows.minedowsStyle).setPosition(x, y).setWidth(145).build());
        y+=15;
        addRenderableWidget(new EditBoxBuilder(Component.translatable("minedows.control.title.color_start")).setColor(-16777088).setConfig(Windows.config, "TITLE.GRADIENT.START").setStyle(Windows.minedowsStyle).setPosition(x, y).setWidth(145).build());
        y+=25;
        addRenderableWidget(new EditBoxBuilder(Component.translatable("minedows.control.title.color_end")).setColor(-14644786).setConfig(Windows.config, "TITLE.GRADIENT.END").setStyle(Windows.minedowsStyle).setPosition(x, y).setWidth(145).build());
        y+=25;
        addRenderableWidget(new ButtonBoolean(new ButtonBooleanBuilder(Component.translatable("minedows.control.label.dark_text"), false)
                .setConfig(Windows.config, "LABEL.DARK_TEXT").setPosition(x, y).setSize(145, 18).setStyle(minedowsStyle)));
        y+=23;
        addRenderableWidget(new ButtonBoolean(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.enable_classic_style"), false)
                .setConfig(Windows.config, "ENABLE_CLASSIC_STYLE").setPosition(x, y).setSize(145, 18).setStyle(minedowsStyle)));
        y+=23;
        addRenderableWidget(new ButtonBoolean(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.overwrite_light"), false)
                .setConfig(Windows.config, "THEME.OVERWRITE_LIGHT").setPosition(x, y).setSize(145, 18).setStyle(minedowsStyle)));
        y+=23;
        TextBox t = (TextBox) new TextBuilder(Component.translatable("minedows.control.colors_tips")).setType(TextBuilder.TYPE.BLOCKQUOTE).setStyle(Windows.minedowsStyle).setPosition(x, y).setWidth(145).build();
        addRenderableWidget(t);
        y+=(t.getHeight()+5);
    }


    public static void renderWindow(GuiGraphicsExtractor guiGraphics, Window window, int x, int y, int mouseX, int mouseY, float tick) {
//        int x = (int) window.x;
//        int y = (int) window.y;
        int width = (int) window.width;
        int height = (int) window.height;
        int mouseXforScreen = mouseX-(x+3);
        int mouseYforScreen = mouseY-(y+19);
        if(!window.active) mouseXforScreen = mouseYforScreen = -20;
        // TODO: Добавить проверку классической темы
        int buttonsSizes = 0;
        int buttonSize;
        int buttonY;
        if(Windows.config.getBoolean("ENABLE_CLASSIC_STYLE", false)) {
            buttonSize = 10;
            buttonY = 5;
            WindowUtils.renderPanel(guiGraphics, x, y, x + width, y + height);
            if (window.isResizable() && !window.maximize) {
                guiGraphics.fill(x + width - 4, y + height - 4, x + width - 3, y + height - 3, 0xFF1e1e1e);
                guiGraphics.fill(x + width - 4, y + height - 6, x + width - 3, y + height - 5, 0xFF1e1e1e);
                guiGraphics.fill(x + width - 4, y + height - 8, x + width - 3, y + height - 7, 0xFF1e1e1e);

                guiGraphics.fill(x + width - 5, y + height - 5, x + width - 4, y + height - 4, 0xFF1e1e1e);
                guiGraphics.fill(x + width - 5, y + height - 7, x + width - 4, y + height - 6, 0xFF1e1e1e);


                guiGraphics.fill(x + width - 6, y + height - 4, x + width - 5, y + height - 3, 0xFF1e1e1e);
                guiGraphics.fill(x + width - 6, y + height - 6, x + width - 5, y + height - 5, 0xFF1e1e1e);


                guiGraphics.fill(x + width - 7, y + height - 5, x + width - 6, y + height - 4, 0xFF1e1e1e);

                guiGraphics.fill(x + width - 8, y + height - 4, x + width - 7, y + height - 3, 0xFF1e1e1e);
            }
            //
            int[] colors = WinColors.getTitleGradientColors();
            if (!window.active) colors = new int[]{getGrayColor(colors[0]), getGrayColor(colors[1])};
            for (int i = 0; i < width - 6; i++) {
                guiGraphics.fill(x + 3 + i, y + 3, x + 4 + i, y + 17, interpolate(colors[0], colors[1], (float) i / (width - 6)));
            }
            int xb = x + width - 5 - buttonSize;

            if (window.getButtons() < 2) {
                WindowUtils.renderPanel(guiGraphics, xb, y + buttonY, xb + buttonSize, y + buttonY + buttonSize);
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, WinColors.getLightIcon("textures/window/close"), xb, y + 5, 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
                if (xb < mouseX && mouseX < xb + buttonSize && y + buttonY < mouseY && mouseY < y + buttonY + buttonSize)
                    guiGraphics.fill(xb, y + buttonY, xb + buttonSize, y + buttonY + buttonSize, BLACK_ALPHA);
                buttonsSizes += buttonSize + 2;
                xb -= (2 + buttonSize);
                if (window.getButtons() == 0 && window.isResizable()) {
                    WindowUtils.renderPanel(guiGraphics, xb, y + buttonY, xb + buttonSize, y + buttonY + buttonSize);
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, WinColors.getLightIcon(window.maximize ? "textures/window/window" : "textures/window/maximaze"), xb, y + 5, 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
                    if (xb < mouseX && mouseX < xb + buttonSize && y + buttonY < mouseY && mouseY < y + buttonY + buttonSize)
                        guiGraphics.fill(xb, y + buttonY, xb + buttonSize, y + buttonY + buttonSize, BLACK_ALPHA);
                    xb -= buttonSize;
                    buttonsSizes += buttonSize + 2;
                    WindowUtils.renderPanel(guiGraphics, xb, y + buttonY, xb + buttonSize, y + buttonY + buttonSize);
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, WinColors.getLightIcon("textures/window/mini"), xb, y + 5, 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
                    if (xb < mouseX && mouseX < xb + buttonSize && y + buttonY < mouseY && mouseY < y + buttonY + buttonSize)
                        guiGraphics.fill(xb, y + buttonY, xb + buttonSize, y + buttonY + buttonSize, BLACK_ALPHA);
                    buttonsSizes += buttonSize + 2;
                }
            }
        } else {
            buttonSize = 12;
            buttonY = 4;
            int xb = x + width - 5 - buttonSize;
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "frame"), width, height, 0, 0, x+1, y+1, width, height, 0x2E000000);
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "frame"), width, height, 0, 0, x+2, y+2, width, height, 0x1E000000);
            int wincolor = WinColors.getWindowColors()[0];
            double p = window.active ? 1 : 1.275;
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "frame"), width, height, 0, 0, x, y, width, height, Math.min(ARGB.color(ARGB.alpha(wincolor), (int) (Math.min(ARGB.red(wincolor)*p, 255)), (int) (Math.min(ARGB.green(wincolor)*p, 255)), (int) (Math.min(ARGB.blue(wincolor)*p, 255))), 0xFFFFFFFF));
            if (window.getButtons() < 2) {
                boolean b1 = xb < mouseX && mouseX < xb + buttonSize && y + buttonY < mouseY && mouseY < y + buttonY + buttonSize;
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "button"), buttonSize, buttonSize, 0, 0, xb, y + buttonY, buttonSize, buttonSize, b1 ? SELFISH : GROUPIE);
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/window/close_l.png"), xb, y + buttonY, 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
                buttonsSizes += buttonSize + 2;
                xb -= (2 + buttonSize);
                if (window.getButtons() == 0 && window.isResizable()) {
                    boolean b2 = xb < mouseX && mouseX < xb + buttonSize && y + buttonY < mouseY && mouseY < y + buttonY + buttonSize;
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "button"), buttonSize, buttonSize, 0, 0, xb, y + buttonY, buttonSize, buttonSize, Math.min(WinColors.getWindowColors()[2] + (b2 ? 0x001E1E1E : 0), 0xFFFFFFFF));
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, WinColors.getLightIcon(window.maximize ? "textures/window/window" : "textures/window/maximaze"), xb, y + buttonY, 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
                    xb -= (2 + buttonSize);
                    buttonsSizes += buttonSize + 2;

                    boolean b3 = xb < mouseX && mouseX < xb + buttonSize && y + buttonY < mouseY && mouseY < y + buttonY + buttonSize;
                    guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "button"), buttonSize, buttonSize, 0, 0, xb, y + buttonY, buttonSize, buttonSize, Math.min(WinColors.getWindowColors()[2] + (b3 ? 0x001E1E1E : 0), 0xFFFFFFFF));
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, WinColors.getLightIcon("textures/window/mini"), xb, y + buttonY, 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
                    buttonsSizes += buttonSize + 2;
                }
            }
        }
        //
        int xTitle = 0;
        if(window.icon != null){
            xTitle+=15;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, window.icon, x+6, y+5, 0, 0, 10, 10, 10, 10);
        }
        int maxTitleWidth = width - xTitle - buttonsSizes-10;
        String title = window.screen.getTitle().getString();
        if(title.isEmpty()) title = window.screen.getClass().getSimpleName()+".exe";
        if(Minecraft.getInstance().font.width(title) > maxTitleWidth){
            title = Minecraft.getInstance().font.substrByWidth(Component.literal(title), maxTitleWidth - (Minecraft.getInstance().font.width("..."))).getString()+"...";
        }
        guiGraphics.text(Minecraft.getInstance().font, title, x +xTitle+ 6, y + 6, 0xFFFFFFFF, false);
        guiGraphics.pose().pushMatrix();
        guiGraphics.enableScissor(x+3, y+(buttonY*2+buttonSize), x+width-3, y+height-3);
        guiGraphics.pose().translate(x+3, y+19);
//        window.screen.renderWithTooltip(guiGraphics, mouseXforScreen, mouseYforScreen, tick);
        guiGraphics.nextStratum();
        window.screen.extractBackground(guiGraphics, mouseXforScreen, mouseYforScreen, tick);
        guiGraphics.nextStratum();
        window.screen.extractRenderState(guiGraphics, mouseXforScreen, mouseYforScreen, tick);
        //#if MC >= 12110
//        if (!guiGraphics.deferredOutlines.isEmpty()) {
//            guiGraphics.nextStratum();
//
//            for(GuiGraphicsExtractor.OutlineBox outlineBox : guiGraphics.deferredOutlines) {
//                outlineBox.render(guiGraphics);
//            }
//
//            guiGraphics.deferredOutlines.clear();
//        }
        //#endif
        guiGraphics.disableScissor();
        //#if MC < 12110
        //$$guiGraphics.renderDeferredTooltip();
        //#else
        if (guiGraphics.deferredTooltip != null) {
            guiGraphics.nextStratum();
            guiGraphics.deferredTooltip.run();
            guiGraphics.deferredTooltip = null;
        }
        guiGraphics.extractDeferredElements(mouseXforScreen, mouseYforScreen, tick);
        //#endif
        guiGraphics.pose().translate(-x-3, -y-19);
        guiGraphics.pose().popMatrix();
//        guiGraphics.drawString(Minecraft.getInstance().font, String.format("moved = %s", window.isDragging), x, y-12, -1);
        if((x+width-14 < mouseX && mouseX < x+width+6
                && y+height-14 < mouseY && mouseY < y+height+6 && window.isResizable() && !window.maximize) || window.isResized) Cursor.setDragging(guiGraphics);
        else Cursor.reset(guiGraphics);
    }
}
