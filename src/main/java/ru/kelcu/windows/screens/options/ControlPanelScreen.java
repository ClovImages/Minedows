package ru.kelcu.windows.screens.options;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.components.Action;
import ru.kelcu.windows.components.builders.WindowBuilder;
import ru.kelcu.windows.mods.ModMenuActions;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcu.windows.screens.apps.ExplorerScreen;
import ru.kelcu.windows.screens.components.LabelConfWidget;
import ru.kelcu.windows.screens.components.LabelWidget;
import ru.kelcu.windows.screens.components.alinlib.ButtonBoolean;
import ru.kelcu.windows.screens.info.WindowsVersion;
import ru.kelcu.windows.screens.options.theme.ThemeEditorScreen;
import ru.kelcu.windows.style.NothingStyle;
import ru.kelcu.windows.utils.WindowUtils;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.config.Config;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.components.builder.AbstractBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.selector.SelectorBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.components.buttons.Button;
import ru.kelcuprum.alinlib.gui.components.text.CategoryBox;
import ru.kelcuprum.alinlib.gui.components.text.TextBox;
import ru.kelcuprum.alinlib.gui.config.DesignScreen;
import ru.kelcuprum.alinlib.gui.screens.ConfigScreenBuilder;
import ru.kelcuprum.alinlib.info.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ru.kelcu.windows.screens.DesktopScreen.getLabelActions;

public class ControlPanelScreen extends Screen {
    public static NothingStyle nothingStyle = new NothingStyle();
    public static NothingStyle anotherNothingStyle = new NothingStyle(true);
    public final Type type;
    public ControlPanelScreen(Type type) {
        super(Component.translatable("minedows.control"));
        this.type = type;
    }
    public ControlPanelScreen(){
        this(Type.START_PAGE);
    }
    public ArrayList<AbstractWidget> widgetsList;
    public ControlPanelScreen(Type type, ArrayList<AbstractWidget> widgetsList){
        this(type);
        this.widgetsList = widgetsList;
    }
    public ConfigScreenBuilder builder;
    public ControlPanelScreen(Type type, ConfigScreenBuilder builder){
        this(type);
        this.builder = builder;
    }

    public enum Type {
        LABELS,
        LINES,
        START_PAGE;
    }

    public ArrayList<AbstractWidget> widgetss = new ArrayList<>();
    private ConfigureScrolWidget scroller;

    public ArrayList<AbstractWidget> panel_widgetss = new ArrayList<>();
    private ConfigureScrolWidget panel_scroller;

    @Override
    public @NonNull Component getTitle() {
        return builder == null ? super.getTitle() : builder.getTitle();
    }

    @Override
    protected void init() {
        super.init();
        panelInit();
        int x = 195;
        final int fX = x;
        widgetss = new ArrayList<>();
        int oneLine = 0;
        boolean isFirst = true;
        if(widgetsList != null) widgetss.addAll(widgetsList);
        else if(builder != null) widgetss.addAll(builder.widgets);
        else if(type == Type.START_PAGE){
            int w = (width-fX-25) / 2;
            int y = 4;
            widgetss.add(new TextBuilder(Component.translatable("minedows.control.description")).setAlign(TextBuilder.ALIGN.LEFT).setType(TextBuilder.TYPE.MESSAGE)
                    .setStyle(anotherNothingStyle)
                    .setPosition(x, y)
                    .setWidth(width-15-fX)
                    .build());
            y+= 25;
            widgetss.add(new ControlButton(new ButtonBuilder(Component.translatable("minedows.system_options"), (b) -> {
                minecraft.gui.setScreen(new ControlPanelScreen(Type.LINES, getSystemOption()));
            })
                    .setRightTitle(Component.translatable("minedows.system_options.description"))
                    .setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/options.png"))
                    .setStyle(anotherNothingStyle)
                    .setPosition(x, y)
                    .setSize(w, 36)));
            x += w+10;
            widgetss.add(new ControlButton(new ButtonBuilder(Component.translatable("minedows.themes.editor"), (b) -> {
                DesktopScreen.addWindow(new WindowBuilder().setScreen(new ThemeEditorScreen()).setSize(625, 398).setResizable(false).setIcon(GuiUtils.getResourceLocation("windows","textures/start/icons/themes.png")).build());

            })
                    .setRightTitle(Component.translatable("minedows.themes.editor.description"))
                    .setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/themes.png"))
                    .setStyle(anotherNothingStyle)
                    .setSize(w, 36)
                    .setPosition(x, y)));
            x = fX; y+=31;
            widgetss.add(new ControlButton(new ButtonBuilder(Component.translatable("minedows.control.label"), (b) -> {
                minecraft.gui.setScreen(new ControlPanelScreen(Type.LABELS, getLabels()));

            })
                    .setRightTitle(Component.translatable("minedows.control.label.description"))
                    .setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/empty.png"))
                    .setStyle(anotherNothingStyle)
                    .setPosition(x, y)
                    .setSize(w, 36)));
            x += w+10;
            widgetss.add(new ControlButton(new ButtonBuilder(Component.translatable("minedows.control.auto_run"), (b) -> {
                minecraft.gui.setScreen(new ControlPanelScreen(Type.LABELS, getAutoStart()));
            })
                    .setRightTitle(Component.translatable("minedows.control.auto_run.description"))
                    .setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/executable.png"))
                    .setStyle(anotherNothingStyle)
                    .setPosition(x, y)
                    .setSize(w, 36)));
            x = fX; y+=31;
            oneLine = 2;
        }
        int y = 4;
        if(type == Type.LABELS){
            for(AbstractWidget labelWidget : widgetss){
                labelWidget.setPosition(x, y);
                x += (labelWidget.getWidth()+10);
                if(isFirst) oneLine++;
                if(x+labelWidget.getWidth()+10 > width){
                    isFirst = false;
                    x = fX;
                    y+=labelWidget.getHeight()+10;
                }
            }
        } else if(type == Type.LINES) {
            for(AbstractWidget widget : widgetss){
                widget.setWidth(width-fX-15);
                widget.setPosition(fX, y);
                y = widget.getBottom()+5;
            }
        }

        final int finalOneLine = oneLine;

        this.scroller = addRenderableWidget(new ConfigureScrolWidget(width-4, 1, 3, this.height-2, Component.empty(), scroller -> {
            scroller.innerHeight = 4;
            int currentPos = 0;
            int lastHeight = 0;
            for (AbstractWidget widget : widgetss) {
                if (widget.visible) {
                    lastHeight = widget.getHeight();
                    if(type == Type.LABELS || type == Type.START_PAGE){
                        widget.setY(5 + (int) (scroller.innerHeight - scroller.scrollAmount()));
                        currentPos++;
                        if(currentPos == finalOneLine || widget instanceof TextBox) {
                            scroller.innerHeight += (lastHeight+10);
                            currentPos = 0;
                        }
                    } else {
                        widget.setPosition(fX, (int) (scroller.innerHeight - scroller.scrollAmount()));
                        scroller.innerHeight += (widget.getHeight() + 5);
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
    ExplorerScreen.XPCategoryBox conf;
    Identifier icon = GuiUtils.getResourceLocation("windows", "textures/start/icons/computer_gear.png");
    public void panelInit(){
        int x = 11;
        panel_widgetss = new ArrayList<>();
        if(control == null) {
            control = new ControlCategoryBox(x, 5, 160, 20, true, Component.translatable("minedows.control"), icon);
            control.addValue(new ButtonBuilder(Component.translatable("minedows.system_options"), (b) -> {
                minecraft.gui.setScreen(new ControlPanelScreen(Type.LINES, getSystemOption()));
            }).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/options.png"))
                    .setCentered(false)
                    .setStyle(ControlPanelScreen.nothingStyle)
                    .setSize(150, 16));
            control.addValue(new ButtonBuilder(Component.translatable("minedows.themes.editor"), (b) -> {
                DesktopScreen.addWindow(new WindowBuilder().setScreen(new ThemeEditorScreen()).setSize(625, 398).setResizable(false).setIcon(GuiUtils.getResourceLocation("windows","textures/start/icons/themes.png")).build());
            }).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/themes.png"))
                    .setCentered(false)
                    .setStyle(ControlPanelScreen.nothingStyle)
                    .setSize(150, 16));
            control.addValue(new ButtonBuilder(Component.translatable("minedows.control.label"), (b) -> {
                minecraft.gui.setScreen(new ControlPanelScreen(Type.LABELS, getLabels()));
            }).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/empty.png"))
                    .setCentered(false)
                    .setStyle(ControlPanelScreen.nothingStyle)
                    .setSize(150, 16));
            control.addValue(new ButtonBuilder(Component.translatable("minedows.control.auto_run"), (b) -> {
                minecraft.gui.setScreen(new ControlPanelScreen(Type.LABELS, getAutoStart()));
            }).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/executable.png"))
                    .setCentered(false)
                    .setStyle(ControlPanelScreen.nothingStyle)
                    .setSize(150, 16));
        }
        panel_widgetss.add(control);
        panel_widgetss.addAll(control.values);
        if(conf == null && builder != null && !builder.panelWidgets.isEmpty()){
            conf = new ExplorerScreen.XPCategoryBox(x, 5, 160, 20, true, builder.title);
            conf.addValues(builder.panelWidgets);
        }
        if(conf != null) {
            panel_widgetss.add(conf);
            panel_widgetss.addAll(conf.values);
        }
        if(other == null) {
            other = new ExplorerScreen.XPCategoryBox(x, 5, 160, 20, true, Component.translatable("minedows.explorer.other_places"));
            other.addValue(new ButtonBuilder(Component.translatable("minedows.control.minecraft"), (b) -> {
                DesktopScreen.addWindow(new WindowBuilder().setScreen(new OptionsScreen(null, Minecraft.getInstance().options)).build());
            }).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/shutdown.png"))
                    .setCentered(false)
                    .setStyle(ControlPanelScreen.nothingStyle)
                    .setSize(150, 16));
            if(FabricLoader.getInstance().isModLoaded("rinku"))
                other.addValue(new ButtonBuilder(Component.translatable("minedows.browser.options"), (b) -> {
                    minecraft.gui.setScreen(new ControlPanelScreen(Type.LINES, getBrowserOption()));
                }).setIcon(GuiUtils.getResourceLocation("windows","textures/browser/icon.png"))
                        .setCentered(false)
                        .setStyle(ControlPanelScreen.nothingStyle)
                        .setSize(150, 16));

            other.addValue(new ButtonBuilder(Component.translatable("alinlib"), (b) -> {
                DesktopScreen.addWindow(new WindowBuilder().setScreen(DesignScreen.build(null)).setIcon(GuiUtils.getResourceLocation("alinlib","icon.png")).build());
            }).setIcon(GuiUtils.getResourceLocation("alinlib","icon.png"))
                    .setCentered(false)
                    .setStyle(ControlPanelScreen.nothingStyle)
                    .setSize(150, 16));
            other.addValue(new ButtonBuilder(Component.translatable("minedows.start.about"), (b) -> {
                DesktopScreen.addWindow(new WindowBuilder().setScreen(new WindowsVersion()).setSize(325, 220).setResizable(false).setButtons(1).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/500.png")).build());
            }).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/500.png"))
                    .setCentered(false)
                    .setStyle(ControlPanelScreen.nothingStyle)
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
                    if (other.values.contains(widget) || control.values.contains(widget) || (conf != null && conf.values.contains(widget))) {
                        onCategory = true;
                        widget.setX(other.getX() + 5);
                        widget.setY((int) (scroller.innerHeight - scroller.scrollAmount()));
                        if((other.values.contains(widget) && other.values.getLast() == widget) || (control.values.contains(widget) && control.values.getLast() == widget)
                                || (conf != null && conf.values.contains(widget) && conf.values.getLast() == widget)) onCategory = false;
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
        if(type == Type.START_PAGE){
            guiGraphics.fill(1, 1, width-1, height-1, 0xFF6476d4);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/start/icons/computer_gear.png"), width-(int) (height*0.75), (int) (height*0.25), 0, 0, (int) (height*0.75), (int) (height*0.75), (int) (height*0.75), (int) (height*0.75), 0x1e6476d4);
        }
        for(int i = 0; i<height-2; i++){
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
        if (!scr && scroller != null && mouseX > 160) scr = scroller.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (!scr && panel_scroller != null && mouseX < 160) scr = panel_scroller.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        return scr;
    }
    @Override
    public void tick() {
        if (scroller != null) scroller.onScroll.accept(scroller);
        if (panel_scroller != null) panel_scroller.onScroll.accept(panel_scroller);
        super.tick();
    }
    // -=-=-=

    public ConfigScreenBuilder getSystemOption(){
        ConfigScreenBuilder builder = new ConfigScreenBuilder(null, Component.translatable("minedows.system_options")).setType(ControlPanelScreen.nothingStyle);
        builder.addWidget(new HorizontalRuleBuilder(Component.translatable("minedows.system_options.boot")).setStyle(ControlPanelScreen.nothingStyle))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.boot.fast"), false).setConfig(Windows.config, "FASTBOOT").setStyle(Windows.minedowsStyle))

                .addWidget(new HorizontalRuleBuilder(Component.translatable("minedows.system_options.sounds")).setStyle(ControlPanelScreen.nothingStyle))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.sounds.disable_click_sound"), false).setConfig(Windows.config, "DISABLE_CLICK_SOUND").setStyle(Windows.minedowsStyle))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.sounds.disable_error_sound"), false).setConfig(Windows.config, "DISABLE_ERROR_SOUND").setStyle(Windows.minedowsStyle))
                // -=-=-=-=-
                .addWidget(new HorizontalRuleBuilder(Component.translatable("minedows.system_options.ui")).setStyle(ControlPanelScreen.nothingStyle))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.enable_classic_style"), false).setConfig(Windows.config, "ENABLE_CLASSIC_STYLE").setStyle(Windows.minedowsStyle))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.enable_new_ui"), false).setConfig(Windows.config, "ENABLE_NEW_UI").setStyle(Windows.minedowsStyle))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.death"), true).setConfig(Windows.config, "DEATH").setStyle(Windows.minedowsStyle))
                .addWidget(new HorizontalRuleBuilder(Component.translatable("minedows.system_options.other")).setStyle(ControlPanelScreen.nothingStyle));
        if(Windows.isDeveloperPreview()) builder.addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.disable_warn_text"), false).setConfig(Windows.config, "DISABLE_WARN_TEXT").setStyle(Windows.minedowsStyle));
        builder.addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.fix_smooth_menu"), false).setConfig(Windows.config, "FIXES.SMOOTH_MENU").setStyle(Windows.minedowsStyle))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.enable_pause_screen"), false).setConfig(Windows.config, "ENABLE_PAUSE_SCREEN").setStyle(Windows.minedowsStyle))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.open_pause_screen"), false).setConfig(Windows.config, "OPEN_PAUSE_SCREEN").setStyle(Windows.minedowsStyle))
                .addWidget(new EditBoxBuilder(Component.translatable("minedows.system_options.other.explorer.home_page")).setValue(System.getProperty("user.home")).setConfig(Windows.config, "EXPLORER.DEFAULT_PAGE").setStyle(Windows.minedowsStyle))
                .addWidget(new EditBoxBuilder(Component.translatable("minedows.system_options.other.explorer.links")).setValue(String.format("%s%sDesktop", System.getProperty("user.home"), File.separator)).setConfig(Windows.config, "explorer_links").setStyle(Windows.minedowsStyle));
        if(Player.isLicenseAccount() || FabricLoader.getInstance().isDevelopmentEnvironment())
            builder.addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.activate"), false).setConfig(Windows.config, "ALWAYS_ACTIVATE_MINECRAFT").setStyle(Windows.minedowsStyle));
//        builder.addWidget(new HorizontalRuleBuilder(Component.translatable("minedows.system_options.for_arts")).setStyle(ControlPanelScreen.nothingStyle))
//                .addWidget(new TextBuilder(Component.translatable("minedows.system_options.for_arts.description")).setType(TextBuilder.TYPE.BLOCKQUOTE))
//                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.for_arts.enable"), false).setConfig(Windows.config, "enable_art_programms").setStyle(Windows.minedowsStyle));
        return builder;
    }
    public ConfigScreenBuilder getBrowserOption(){
        ConfigScreenBuilder builder = new ConfigScreenBuilder(null, Component.translatable("minedows.browser.options")).setType(ControlPanelScreen.nothingStyle);
        builder.addWidget(new HorizontalRuleBuilder(Component.translatable("minedows.browser.options.search")).setStyle(ControlPanelScreen.nothingStyle))
                .addWidget(new SelectorBuilder(Component.translatable("minedows.browser.options.search.list")).setList(new String[]{
                        "Google",
                        "Yandex",
                        "DuckDuckGo",
                        "StartPage",
                        "Custom"
                }).setValue(0).setConfig(Windows.config, "BROWSER.SEARCH").setStyle(Windows.minedowsStyle))
                .addWidget(new EditBoxBuilder(Component.translatable("minedows.browser.options.search.custom")).setValue("https://google.com/search?q=%s").setConfig(Windows.config, "BROWSER.SEARCH.CUSTOM").setStyle(Windows.minedowsStyle))
                .addWidget(new HorizontalRuleBuilder(Component.translatable("minedows.browser.options.other")).setStyle(ControlPanelScreen.nothingStyle))
                .addWidget(new EditBoxBuilder(Component.translatable("minedows.browser.options.home_page")).setValue("https://www.google.com").setConfig(Windows.config, "BROWSER.HOME_PAGE").setStyle(Windows.minedowsStyle));
        return (builder);
    }
    public ConfigScreenBuilder getLabels(){
        ConfigScreenBuilder b = new ConfigScreenBuilder(null, Component.translatable("minedows.control.label")).setType(ControlPanelScreen.nothingStyle);
        for(Action action : getLabelActions()){
            Config config = new Config(Windows.config.getJsonObject(action.title.toString(), new JsonObject()));
            LabelConfWidget label = new LabelConfWidget(-50, 0, 40,
                    action);
            label.setBlackDuck(true);
            label.setConfig(action.title.toString(), config);
            b.addWidget(label);
        }
        if(Windows.isModMenuInstalled()){
            ArrayList<ModMenuActions.ModInfo> mods = ModMenuActions.getMods();
            for(ModMenuActions.ModInfo mod : mods){
                Config config = new Config(Windows.config.getJsonObject(mod.id(), new JsonObject()));
                LabelConfWidget label = new LabelConfWidget(-50, 0, 40,
                        new Action(Action.Type.OPEN_SCREEN, Component.literal(mod.name()), mod.icon(), mod.screen()));
                label.setBlackDuck(true);
                label.setConfig(mod.id(), config);
                label.setDefaultValue(!mod.isLibrary());
                b.addWidget(label);
            }
        }
        b.addPanelWidget(new ButtonBoolean(new ButtonBooleanBuilder(Component.translatable("minedows.control.label.custom_pos"), false)
                .setConfig(Windows.config, "LABEL.CUSTOM_POS").setPosition(5, -18).setSize(115, 18).setStyle(nothingStyle)));
        b.addPanelWidget(new ButtonBoolean(new ButtonBooleanBuilder(Component.translatable("minedows.control.label.dark_text"), false)
                .setConfig(Windows.config, "LABEL.DARK_TEXT").setPosition(5, -18).setSize(115, 18).setStyle(nothingStyle)));
        return b;
    }
    public ConfigScreenBuilder getAutoStart(){
        ConfigScreenBuilder b = new ConfigScreenBuilder(null, Component.translatable("minedows.control.auto_run"));
        for(Action action : getLabelActions()){
            Config config = new Config(Windows.config.getJsonObject("auto_run."+action.title.toString(), new JsonObject()));
            LabelConfWidget label = new LabelConfWidget(-50, 0, 40,
                    action);
            label.setBlackDuck(true);
            label.setConfig("auto_run."+action.title.toString(), config);
            label.setDefaultValue(false);
            b.addWidget(label);
        }
        if(Windows.isModMenuInstalled()){
            ArrayList<ModMenuActions.ModInfo> mods = ModMenuActions.getMods();
            for(ModMenuActions.ModInfo mod : mods){
                Config config = new Config(Windows.config.getJsonObject("auto_run."+mod.id(), new JsonObject()));
                LabelConfWidget label = new LabelConfWidget(-50, 0, 40,
                        new Action(Action.Type.OPEN_SCREEN, Component.literal(mod.name()), mod.icon(), mod.screen()));
                label.setBlackDuck(true);
                label.setConfig("auto_run."+mod.id(), config);
                label.setDefaultValue(false);
                b.addWidget(label);
            }
        }
        return b;
    }
    public static class ControlButton extends Button {
        public ControlButton(AbstractBuilder builder) {
            super(builder);
        }

        @Override
        public void renderText(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTicks) {
            if(((ButtonBuilder) builder).sprite == null){
                if (GuiUtils.isDoesNotFit(builder.getTitle(), this.getWidthComponent(), this.getHeight())) {
                    TextBox.renderScrollingString(GuiGraphicsExtractor, Minecraft.getInstance().font, builder.getTitle(), getXComponent(), this.getY(), getRight()-2, this.getY() + (this.getHeight()/2), this.builder.getStyle().getTextColor(this.active), this.builder.getStyle().textShadow());
                } else {
                    GuiGraphicsExtractor.text(AlinLib.MINECRAFT.font, this.builder.getTitle(), this.getXComponent(), this.getY() + 6, this.builder.getStyle().getTextColor(this.active), this.builder.getStyle().textShadow());
                }

                if (GuiUtils.isDoesNotFit(((ButtonBuilder) builder).getRightTitle(), this.getWidthComponent(), this.getHeight())) {
                    TextBox.renderScrollingString(GuiGraphicsExtractor, Minecraft.getInstance().font, ((ButtonBuilder) builder).getRightTitle(), getXComponent(), this.getY() + (this.getHeight()/2), getRight()-2, this.getY() + this.getHeight(), this.builder.getStyle().getTextColor(this.active), this.builder.getStyle().textShadow());
                } else {
                    GuiGraphicsExtractor.text(AlinLib.MINECRAFT.font, ((ButtonBuilder) builder).getRightTitle(), this.getXComponent(), this.getBottom() - 6 - Minecraft.getInstance().font.lineHeight, this.builder.getStyle().getTextColor(this.active), this.builder.getStyle().textShadow());
                }

                if (((ButtonBuilder)this.builder).icon != null) {
                    GuiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, ((ButtonBuilder)this.builder).icon, this.getX()+2, this.getY()+2, 0.0F, 0.0F, this.getHeight()-4, this.getHeight()-4, this.getHeight()-4, this.getHeight()-4);
                }
            } else this.renderSprite(GuiGraphicsExtractor, mouseX, mouseY, partialTicks);
        }
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
