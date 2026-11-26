package ru.kelcu.windows.screens.options;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.components.Action;
import ru.kelcu.windows.components.builders.WindowBuilder;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcu.windows.screens.apps.ExplorerScreen;
import ru.kelcu.windows.screens.components.LabelWidget;
import ru.kelcu.windows.screens.info.WindowsVersion;
import ru.kelcu.windows.screens.options.theme.ThemeEditorScreen;
import ru.kelcu.windows.utils.ThemeManager;
import ru.kelcu.windows.utils.WindowUtils;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.selector.SelectorBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.components.text.CategoryBox;
import ru.kelcuprum.alinlib.gui.components.text.TextBox;
import ru.kelcuprum.alinlib.gui.config.DesignScreen;
import ru.kelcuprum.alinlib.gui.screens.ConfigScreenBuilder;
import ru.kelcuprum.alinlib.info.Player;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ru.kelcu.windows.Windows.config;

public class ControlPanelScreen extends Screen {
    public ArrayList<LabelWidget> widgets = new ArrayList<>();
    public ControlPanelScreen() {
        super(Component.translatable("minedows.control"));
    }
    public void initLabels(){
        widgets = new ArrayList<>();
        widgets.add(new LabelWidget(-50, 0, 40, new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.control.minecraft"), GuiUtils.getResourceLocation("windows","textures/start/icons/shutdown.png"), new WindowBuilder().setScreen(new OptionsScreen(null, Minecraft.getInstance().options)))));
        widgets.add(new LabelWidget(-50, 0, 40, new Action(Action.Type.OPEN_SCREEN, Component.translatable("alinlib"), GuiUtils.getResourceLocation("alinlib","icon.png"), new WindowBuilder().setScreen(DesignScreen.build(null)))));
        if(FabricLoader.getInstance().isModLoaded("rinku")) widgets.add(new LabelWidget(-50, 0, 40, new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.browser.options"), GuiUtils.getResourceLocation("windows","textures/browser/icon.png"), new WindowBuilder().setScreen(getBrowserOption()).setSize(450, 200))));
        widgets.add(new LabelWidget(-50, 0, 40, new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.system_options"), GuiUtils.getResourceLocation("windows","textures/start/icons/options.png"), new WindowBuilder().setScreen(getSystemOption()))));
        widgets.add(new LabelWidget(-50, 0, 40, new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.themes.editor"), GuiUtils.getResourceLocation("windows","textures/start/icons/themes.png"), new WindowBuilder().setScreen(new ThemeEditorScreen()).setSize(625, 398).setResizable(false).setIcon(GuiUtils.getResourceLocation("windows","textures/start/icons/themes.png")))));
        widgets.add(new LabelWidget(-50, 0, 40, new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.control.label"), GuiUtils.getResourceLocation("windows","textures/start/icons/empty.png"), new WindowBuilder().setScreen(new LabelsPanelScreen()).setSize(325, 320).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/empty.png")))));
        widgets.add(new LabelWidget(-50, 0, 40, new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.control.auto_run"), GuiUtils.getResourceLocation("windows","textures/start/icons/executable.png"), new WindowBuilder().setScreen(new AutoRunPanelScreen()).setSize(325, 320).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/executable.png")))));
        widgets.add(new LabelWidget(-50, 0, 40, new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.start.about"), GuiUtils.getResourceLocation("windows", "textures/start/icons/500.png"), new WindowBuilder().setScreen(new WindowsVersion()).setSize(325, 220).setResizable(false).setButtons(1).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/500.png")))));
    }

    public ArrayList<AbstractWidget> widgetss = new ArrayList<>();
    private ConfigureScrolWidget scroller;

    public ArrayList<AbstractWidget> panel_widgetss = new ArrayList<>();
    private ConfigureScrolWidget panel_scroller;

    @Override
    protected void init() {
        super.init();
        panelInit();
        initLabels();
        widgetss = new ArrayList<>();
        int x = 195;
        int xD = 195;
        int y = 5;
        int oneLine = 0;
        boolean isFirst = true;
        for(LabelWidget labelWidget : widgets){
            labelWidget.setPosition(x, y);
            labelWidget.setBlackDuck(true);
            x += (labelWidget.getWidth()+10);
            if(isFirst) oneLine++;
            if(x+labelWidget.getWidth()+10 > width){
                isFirst = false;
                x = xD;
                y+=labelWidget.getHeight()+10;
            }
            widgetss.add(labelWidget);
        }
        int finalOneLine = oneLine;
        this.scroller = addRenderableWidget(new ConfigureScrolWidget(width-4, 1, 3, this.height-2, Component.empty(), scroller -> {
            scroller.innerHeight = 4;
            int currentPos = 0;
            int lastHeight = 0;
            for (AbstractWidget widget : widgetss) {
                if (widget.visible) {
                    widget.setY(5 + (int) (scroller.innerHeight - scroller.scrollAmount()));
                    lastHeight = widget.getHeight();
                    currentPos++;
                    if(currentPos == finalOneLine) {
                        scroller.innerHeight += (lastHeight+10);
                        currentPos = 0;
                    }
                } else widget.setY(-widget.getHeight());
            }
            if(0 < currentPos && currentPos < finalOneLine) scroller.innerHeight += (lastHeight+10);
            scroller.innerHeight -= 8;
        }));
        addWidgetsToScroller(widgetss);
    }

    ExplorerScreen.XPCategoryBox other;
    ControlCategoryBox control;
    Identifier icon = GuiUtils.getResourceLocation("windows", "textures/start/icons/computer_gear.png");
    public void panelInit(){
        int x = 11;
        panel_widgetss = new ArrayList<>();
        if(control == null) {
            control = new ControlCategoryBox(x, 5, 160, 20, true, Component.translatable("minedows.control"), icon);
            control.addValue(new ButtonBuilder(Component.translatable("minedows.system_options"), (b) -> {
                DesktopScreen.addWindow(new WindowBuilder().setScreen(getSystemOption()).build());
            }).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/options.png"))
                    .setCentered(false)
                    .setStyle(LabelsPanelScreen.nothingStyle)
                    .setSize(150, 16));
            control.addValue(new ButtonBuilder(Component.translatable("minedows.themes.editor"), (b) -> {
                DesktopScreen.addWindow(new WindowBuilder().setScreen(new ThemeEditorScreen()).setSize(625, 398).setResizable(false).setIcon(GuiUtils.getResourceLocation("windows","textures/start/icons/themes.png")).build());
            }).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/themes.png"))
                    .setCentered(false)
                    .setStyle(LabelsPanelScreen.nothingStyle)
                    .setSize(150, 16));
            control.addValue(new ButtonBuilder(Component.translatable("minedows.control.label"), (b) -> {
                DesktopScreen.addWindow(new WindowBuilder().setScreen(new LabelsPanelScreen()).setSize(325, 320).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/empty.png")).build());
            }).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/empty.png"))
                    .setCentered(false)
                    .setStyle(LabelsPanelScreen.nothingStyle)
                    .setSize(150, 16));
            control.addValue(new ButtonBuilder(Component.translatable("minedows.control.auto_run"), (b) -> {
                DesktopScreen.addWindow(new WindowBuilder().setScreen(new AutoRunPanelScreen()).setSize(325, 320).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/executable.png")).build());
            }).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/executable.png"))
                    .setCentered(false)
                    .setStyle(LabelsPanelScreen.nothingStyle)
                    .setSize(150, 16));
        }
        panel_widgetss.add(control);
        panel_widgetss.addAll(control.values);
        if(other == null) {
            other = new ExplorerScreen.XPCategoryBox(x, 5, 160, 20, true, Component.translatable("minedows.explorer.other_places"));
            other.addValue(new ButtonBuilder(Component.translatable("minedows.control.minecraft"), (b) -> {
                DesktopScreen.addWindow(new WindowBuilder().setScreen(new OptionsScreen(null, Minecraft.getInstance().options)).build());
            }).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/shutdown.png"))
                    .setCentered(false)
                    .setStyle(LabelsPanelScreen.nothingStyle)
                    .setSize(150, 16));
            if(FabricLoader.getInstance().isModLoaded("rinku"))
                other.addValue(new ButtonBuilder(Component.translatable("minedows.browser.options"), (b) -> {
                    DesktopScreen.addWindow(new WindowBuilder().setScreen(getBrowserOption()).setSize(450, 200).setIcon(GuiUtils.getResourceLocation("windows","textures/browser/icon.png")).build());
                }).setIcon(GuiUtils.getResourceLocation("windows","textures/browser/icon.png"))
                        .setCentered(false)
                        .setStyle(LabelsPanelScreen.nothingStyle)
                        .setSize(150, 16));

            other.addValue(new ButtonBuilder(Component.translatable("alinlib"), (b) -> {
                DesktopScreen.addWindow(new WindowBuilder().setScreen(DesignScreen.build(null)).setIcon(GuiUtils.getResourceLocation("alinlib","icon.png")).build());
            }).setIcon(GuiUtils.getResourceLocation("alinlib","icon.png"))
                    .setCentered(false)
                    .setStyle(LabelsPanelScreen.nothingStyle)
                    .setSize(150, 16));
            other.addValue(new ButtonBuilder(Component.translatable("minedows.start.about"), (b) -> {
                DesktopScreen.addWindow(new WindowBuilder().setScreen(new WindowsVersion()).setSize(325, 220).setResizable(false).setButtons(1).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/500.png")).build());
            }).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/500.png"))
                    .setCentered(false)
                    .setStyle(LabelsPanelScreen.nothingStyle)
                    .setSize(150, 16));
        }
        panel_widgetss.add(other);
        panel_widgetss.addAll(other.values);
        this.panel_scroller = new ConfigureScrolWidget(177, 1, 3, this.height - 2, Component.empty(), scroller -> {
            scroller.innerHeight = 4;
            int lastHeight = 0;
            for (AbstractWidget widget : panel_widgetss) {
                if (widget.visible) {
                    boolean onCategory = false;
                    if (other.values.contains(widget) || control.values.contains(widget)) {
                        onCategory = true;
                        widget.setX(other.getX() + 5);
                        widget.setY((int) (scroller.innerHeight - scroller.scrollAmount()));
                        if((other.values.contains(widget) && other.values.getLast() == widget) || (control.values.contains(widget) && control.values.getLast() == widget)) onCategory = false;
                    } else {
                        widget.setY(5 + (int) (scroller.innerHeight - scroller.scrollAmount()));
                    }
                    lastHeight = widget.getHeight();
                    scroller.innerHeight += (lastHeight + (onCategory ? 2 : 10));
                } else widget.setY(-widget.getHeight());
            }
            scroller.innerHeight -= 8;
        });
        addRenderableWidget(panel_scroller);
        addWidgetsToScroller(panel_widgetss, panel_scroller);
    }

    public void addWidgetsToScroller(List<AbstractWidget> widgets) {
        addWidgetsToScroller(widgets, this.scroller);
    }


    public void addWidgetsToScroller(AbstractWidget widget) {
        addWidgetsToScroller(widget, this.scroller);
    }

    public void addWidgetsToScroller(List<AbstractWidget> widgets, ConfigureScrolWidget scroller) {
        for (AbstractWidget widget : widgets) addWidgetsToScroller(widget, scroller);
    }
    public void addWidgetsToScroller(AbstractWidget widget, ConfigureScrolWidget scroller) {
        widget.setY(-100);
        scroller.addWidget(widget);
        this.addWidget(widget);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        WindowUtils.welcomeToWhiteSpace(guiGraphics, 0, 0, width, height);
        for(int i = 0; i<height-1; i++){
            guiGraphics.fill(1, 1+i, 180, 2+i, GuiUtils.interpolate(0xFF7ca3e8, 0xFF677eda, i/(float) (height-2)));
        }
    }
    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        super.extractRenderState(guiGraphics, i, j, f);
        guiGraphics.enableScissor(1, 1, width-1, height-1);
        if (scroller != null) for (AbstractWidget widget : scroller.widgets) widget.extractRenderState(guiGraphics, i, j, f);
        guiGraphics.disableScissor();

        guiGraphics.enableScissor(1, 1, 181, height-1);
        if (panel_scroller != null) for (AbstractWidget widget : panel_scroller.widgets) widget.extractRenderState(guiGraphics, i, j, f);
        guiGraphics.disableScissor();
    }
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean scr = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (!scr && scroller != null) scr = scroller.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (!scr && panel_scroller != null && mouseX < 150) scr = panel_scroller.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        return scr;
    }
    @Override
    public void tick() {
        if (scroller != null) scroller.onScroll.accept(scroller);
        if (panel_scroller != null) panel_scroller.onScroll.accept(panel_scroller);
        super.tick();
    }
    // -=-=-=

    public Screen getSystemOption(){
        ConfigScreenBuilder builder = new ConfigScreenBuilder(null, Component.translatable("minedows.system_options")).setType(LabelsPanelScreen.nothingStyle);
        builder.addWidget(new HorizontalRuleBuilder(Component.translatable("minedows.system_options.boot")).setStyle(LabelsPanelScreen.nothingStyle))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.boot.fast"), false).setConfig(Windows.config, "FASTBOOT").setStyle(Windows.minedowsStyle))

                .addWidget(new HorizontalRuleBuilder(Component.translatable("minedows.system_options.sounds")).setStyle(LabelsPanelScreen.nothingStyle))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.sounds.disable_click_sound"), false).setConfig(Windows.config, "DISABLE_CLICK_SOUND").setStyle(Windows.minedowsStyle))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.sounds.disable_error_sound"), false).setConfig(Windows.config, "DISABLE_ERROR_SOUND").setStyle(Windows.minedowsStyle))
                // -=-=-=-=-
                .addWidget(new HorizontalRuleBuilder(Component.translatable("minedows.system_options.ui")).setStyle(LabelsPanelScreen.nothingStyle))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.enable_classic_style"), false).setConfig(Windows.config, "ENABLE_CLASSIC_STYLE").setStyle(Windows.minedowsStyle))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.enable_new_ui"), false).setConfig(Windows.config, "ENABLE_NEW_UI").setStyle(Windows.minedowsStyle))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.death"), true).setConfig(Windows.config, "DEATH").setStyle(Windows.minedowsStyle))
                .addWidget(new HorizontalRuleBuilder(Component.translatable("minedows.system_options.other")).setStyle(LabelsPanelScreen.nothingStyle));
        if(Windows.isDeveloperPreview()) builder.addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.disable_warn_text"), false).setConfig(Windows.config, "DISABLE_WARN_TEXT").setStyle(Windows.minedowsStyle));
        builder.addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.fix_smooth_menu"), false).setConfig(Windows.config, "FIXES.SMOOTH_MENU").setStyle(Windows.minedowsStyle))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.enable_pause_screen"), false).setConfig(Windows.config, "ENABLE_PAUSE_SCREEN").setStyle(Windows.minedowsStyle))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.open_pause_screen"), false).setConfig(Windows.config, "OPEN_PAUSE_SCREEN").setStyle(Windows.minedowsStyle))
                .addWidget(new EditBoxBuilder(Component.translatable("minedows.system_options.other.explorer.home_page")).setValue(System.getProperty("user.home")).setConfig(Windows.config, "EXPLORER.DEFAULT_PAGE").setStyle(Windows.minedowsStyle))
                .addWidget(new EditBoxBuilder(Component.translatable("minedows.system_options.other.explorer.links")).setValue(String.format("%s%sDesktop", System.getProperty("user.home"), File.separator)).setConfig(Windows.config, "explorer_links").setStyle(Windows.minedowsStyle));
        if(Player.isLicenseAccount() || FabricLoader.getInstance().isDevelopmentEnvironment())
            builder.addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.activate"), false).setConfig(Windows.config, "ALWAYS_ACTIVATE_MINECRAFT").setStyle(Windows.minedowsStyle));
//        builder.addWidget(new HorizontalRuleBuilder(Component.translatable("minedows.system_options.for_arts")).setStyle(LabelsPanelScreen.nothingStyle))
//                .addWidget(new TextBuilder(Component.translatable("minedows.system_options.for_arts.description")).setType(TextBuilder.TYPE.BLOCKQUOTE))
//                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.for_arts.enable"), false).setConfig(Windows.config, "enable_art_programms").setStyle(Windows.minedowsStyle));
        return new SystemOptionsScreen(builder);
    }

    public Screen getBrowserOption(){
        ConfigScreenBuilder builder = new ConfigScreenBuilder(null, Component.translatable("minedows.browser.options")).setType(LabelsPanelScreen.nothingStyle);
        builder.addWidget(new HorizontalRuleBuilder(Component.translatable("minedows.browser.options.search")).setStyle(LabelsPanelScreen.nothingStyle))
                .addWidget(new SelectorBuilder(Component.translatable("minedows.browser.options.search.list")).setList(new String[]{
                        "Google",
                        "Yandex",
                        "DuckDuckGo",
                        "StartPage",
                        "Custom"
                }).setValue(0).setConfig(Windows.config, "BROWSER.SEARCH").setStyle(Windows.minedowsStyle))
                .addWidget(new EditBoxBuilder(Component.translatable("minedows.browser.options.search.custom")).setValue("https://google.com/search?q=%s").setConfig(Windows.config, "BROWSER.SEARCH.CUSTOM").setStyle(Windows.minedowsStyle))
                .addWidget(new HorizontalRuleBuilder(Component.translatable("minedows.browser.options.other")).setStyle(LabelsPanelScreen.nothingStyle))
                .addWidget(new EditBoxBuilder(Component.translatable("minedows.browser.options.home_page")).setValue("https://www.google.com").setConfig(Windows.config, "BROWSER.HOME_PAGE").setStyle(Windows.minedowsStyle));
        return new SystemOptionsScreen(builder);
    }

    public static class ControlCategoryBox extends CategoryBox {
        private Identifier icon;
        public ControlCategoryBox(int x, int y, int width, int height, boolean state, Component label, Identifier icon) {
            super(x, y, width, height, state, label);
            this.icon = icon;
        }


        public void extractWidgetRenderState(GuiGraphicsExtractor GuiGraphicsExtractor, int i, int j, float f) {
            if (this.visible) {
                GuiGraphicsExtractor.fill(getX(), getY(), getRight(), getY()+getHeight(), 0xFF14469e);
                int iconW = icon == null ? 0 : height+7;
                if(icon != null){
                    GuiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, icon, getX()-7, getY()-7, 0, 0, iconW, iconW, iconW, iconW);
                }
                for(int z = 0; z<(getWidth()/2);z++){
                    GuiGraphicsExtractor.fill(getX()+(getWidth()/2)+z, getY(), getX()+(getWidth()/2)+z+1, getY()+getHeight(), GuiUtils.interpolate(0xFF14469e, 0xFF255dc3, (z/(float)(getWidth()/2))));
                }
                int y = this.getY() + (this.getHeight() - 8) / 2;
                GuiGraphicsExtractor.text(Minecraft.getInstance().font, this.getState() ? "▼" : "▶", this.getX() + this.getWidth() - 5 - Minecraft.getInstance().font.width(this.getState() ? "▼" : "▶"), y, 0xFFebfaff, false);
                if(width-15 - height - Minecraft.getInstance().font.width(this.getState() ? "▼" : "▶") > Minecraft.getInstance().font.width(getName()))
                    GuiGraphicsExtractor.text(Minecraft.getInstance().font, this.getName(), this.getX() + 5 + height, y, 0xFFebfaff, false);
                else TextBox.renderScrollingString(GuiGraphicsExtractor, Minecraft.getInstance().font, this.getName(), this.getX() + 5 + height, this.getY(), this.getX() + this.getWidth() - 10 - Minecraft.getInstance().font.width(this.getState() ? "▼" : "▶"), this.getY() + this.getHeight(), 0xFFebfaff, false);
                if(getState() && !values.isEmpty()){
                    int yW = 0;
                    for(AbstractWidget widget : values) yW = Math.max(yW, widget.getBottom());
                    yW+=5;
                    GuiGraphicsExtractor.fill(getX(), getY()+getHeight(), getRight(), yW, 0xFFd7e0f7);
                }
                if (this.isHovered()) {
                    GuiGraphicsExtractor.requestCursor(this.isActive() ? CursorTypes.POINTING_HAND : CursorTypes.NOT_ALLOWED);
                }
            }
        }
    }
}
