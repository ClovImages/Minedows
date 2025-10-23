package ru.kelcu.windows.screens.options;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.components.Action;
import ru.kelcu.windows.components.builders.WindowBuilder;
import ru.kelcu.windows.screens.components.LabelConfWidget;
import ru.kelcu.windows.screens.components.LabelWidget;
import ru.kelcu.windows.screens.info.WindowsVersion;
import ru.kelcu.windows.utils.ThemeManager;
import ru.kelcu.windows.utils.WindowUtils;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.selector.SelectorBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.config.DesignScreen;
import ru.kelcuprum.alinlib.gui.screens.ConfigScreenBuilder;

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
        if(FabricLoader.getInstance().isModLoaded("mcef")) widgets.add(new LabelWidget(-50, 0, 40, new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.browser.options"), GuiUtils.getResourceLocation("windows","textures/browser/icon.png"), new WindowBuilder().setScreen(getBrowserOption()).setSize(450, 200))));
        widgets.add(new LabelWidget(-50, 0, 40, new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.system_options"), GuiUtils.getResourceLocation("windows","textures/start/icons/options.png"), new WindowBuilder().setScreen(getSystemOption()))));
        widgets.add(new LabelWidget(-50, 0, 40, new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.control.background"), GuiUtils.getResourceLocation("windows","textures/start/icons/background.png"), new WindowBuilder().setScreen(new BackgroundSettings()).setSize(325, 320).setResizable(false).setButtons(1).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/background.png")))));
        widgets.add(new LabelWidget(-50, 0, 40, new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.control.label"), GuiUtils.getResourceLocation("windows","textures/start/icons/empty.png"), new WindowBuilder().setScreen(new LabelsPanelScreen()).setSize(325, 320).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/empty.png")))));
        widgets.add(new LabelWidget(-50, 0, 40, new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.start.about"), GuiUtils.getResourceLocation("windows", "textures/start/icons/500.png"), new WindowBuilder().setScreen(new WindowsVersion()).setSize(325, 220).setResizable(false).setButtons(1).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/500.png")))));
    }

    public ArrayList<AbstractWidget> widgetss = new ArrayList<>();
    private ConfigureScrolWidget scroller;
    @Override
    protected void init() {
        super.init();
        initLabels();
        widgetss = new ArrayList<>();
        int x = 135;
        int xD = 135;
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
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        super.renderBackground(guiGraphics, i, j, f);
        WindowUtils.welcomeToWhiteSpace(guiGraphics, 0, 0, width, height);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/options/background.png"), 1, 1, 0, 0, 100, 150, 100, 150);
        int fruik =90;
        int x = 10;
        int y = 10;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/start/icons/computer_gear.png"), x, y, 0, 0, 35, 35, 35, 35);
        y+=45;
        for(FormattedCharSequence formattedCharSequence : font.split(Component.empty().append(getTitle()).withStyle(Style.EMPTY.withBold(true)), fruik)){
            guiGraphics.drawString(font, formattedCharSequence, x, y, 0xFF000000, false);
            y += font.lineHeight + 3;
        }
        y+=5;
        int[] silly = {
                0xFFf7623e,
                0xFFffd83f,
                0xFF8cd866,
                0xFF3fb2ff
        };
        int oneGRUI = (fruik+30) / silly.length;
        for(int q = 0; q < silly.length; q++){
            guiGraphics.fill(1+(oneGRUI*q), y, 1+oneGRUI+(oneGRUI*q), y+2, silly[q]);
        }
        y+=15;
        for(FormattedCharSequence formattedCharSequence : font.split(Component.translatable(widgets.isEmpty() ? "minedows.control.description.empty" : "minedows.control.description"), fruik)){
            guiGraphics.drawString(font, formattedCharSequence, x, y, 0xFF000000, false);
            y += font.lineHeight + 3;
        }
    }
    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        guiGraphics.enableScissor(1, 1, width-1, height-1);
        if (scroller != null) for (AbstractWidget widget : scroller.widgets) widget.render(guiGraphics, i, j, f);
        guiGraphics.disableScissor();
    }
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean scr = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (!scr && scroller != null) scr = scroller.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        return scr;
    }
    @Override
    public void tick() {
        if (scroller != null) scroller.onScroll.accept(scroller);
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

                .addWidget(new HorizontalRuleBuilder(Component.translatable("minedows.system_options.themes")).setStyle(LabelsPanelScreen.nothingStyle))
                .addWidget(new SelectorBuilder(Component.translatable("minedows.system_options.themes.theme")).setValue(ThemeManager.getThemePosition(ThemeManager.getSelectedTheme()))
                        .setList(ThemeManager.getThemesNames()).setOnPress((s) -> {
                            config.setString("THEME", ThemeManager.getThemesID()[s.getPosition()]);
                        }).setStyle(Windows.minedowsStyle))
                .addWidget(new HorizontalRuleBuilder(Component.translatable("minedows.system_options.theme_color")).setStyle(LabelsPanelScreen.nothingStyle))
                .addWidget(new TextBuilder(Component.translatable("minedows.control.colors_tips")).setType(TextBuilder.TYPE.BLOCKQUOTE).setStyle(Windows.minedowsStyle))
                .addWidget(new EditBoxBuilder(Component.translatable("minedows.control.background.theme.color")).setColor(0xFFcbcbcb).setConfig(Windows.config, "THEME.MAIN_COLOR").setStyle(Windows.minedowsStyle))
                .addWidget(new EditBoxBuilder(Component.translatable("minedows.control.background.theme.active_color")).setColor(0xFFAFC9D1).setConfig(Windows.config, "THEME.ACTIVE_COLOR").setStyle(Windows.minedowsStyle))
                .addWidget(new TextBuilder(Component.translatable("minedows.control.start")).setAlign(TextBuilder.ALIGN.LEFT).setStyle(LabelsPanelScreen.nothingStyle))
                .addWidget(new EditBoxBuilder(Component.translatable("minedows.control.start.color_start")).setColor(-14644786).setConfig(Windows.config, "START.GRADIENT.START").setStyle(Windows.minedowsStyle))
                .addWidget(new EditBoxBuilder(Component.translatable("minedows.control.start.color_end")).setColor(-16777088).setConfig(Windows.config, "START.GRADIENT.END").setStyle(Windows.minedowsStyle))
                .addWidget(new TextBuilder(Component.translatable("minedows.control.title")).setAlign(TextBuilder.ALIGN.LEFT).setStyle(LabelsPanelScreen.nothingStyle))
                .addWidget(new EditBoxBuilder(Component.translatable("minedows.control.title.color_start")).setColor(-16777088).setConfig(Windows.config, "TITLE.GRADIENT.START").setStyle(Windows.minedowsStyle))
                .addWidget(new EditBoxBuilder(Component.translatable("minedows.control.title.color_end")).setColor(-14644786).setConfig(Windows.config, "TITLE.GRADIENT.END").setStyle(Windows.minedowsStyle))
                .addWidget(new HorizontalRuleBuilder(Component.translatable("minedows.system_options.other")).setStyle(LabelsPanelScreen.nothingStyle));
        if(Windows.isDeveloperPreview()) builder.addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.disable_warn_text"), false).setConfig(Windows.config, "DISABLE_WARN_TEXT").setStyle(Windows.minedowsStyle));
        builder.addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.fix_smooth_menu"), false).setConfig(Windows.config, "FIXES.SMOOTH_MENU").setStyle(Windows.minedowsStyle))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.enable_pause_screen"), false).setConfig(Windows.config, "ENABLE_PAUSE_SCREEN").setStyle(Windows.minedowsStyle))
                .addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.system_options.other.open_pause_screen"), false).setConfig(Windows.config, "OPEN_PAUSE_SCREEN").setStyle(Windows.minedowsStyle));
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
}
