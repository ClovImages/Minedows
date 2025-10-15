package ru.kelcu.windows.screens;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.RealmsMainScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.glfw.GLFW;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.components.Action;
import ru.kelcu.windows.components.Cursor;
import ru.kelcu.windows.components.Window;
import ru.kelcu.windows.components.builders.WindowBuilder;
import ru.kelcu.windows.mods.CatalogueActions;
import ru.kelcu.windows.mods.ModMenuActions;
import ru.kelcu.windows.screens.apps.BrowserScreen;
import ru.kelcu.windows.screens.apps.CalcScreen;
import ru.kelcu.windows.screens.apps.PaintScreen;
import ru.kelcu.windows.screens.components.LabelWidget;
import ru.kelcu.windows.screens.components.VerticalConfigureScrolWidget;
import ru.kelcu.windows.screens.options.ControlPanelScreen;
import ru.kelcu.windows.screens.options.SoundMixerScreen;
import ru.kelcu.windows.utils.*;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.config.Config;
import ru.kelcuprum.alinlib.gui.GuiUtils;

import java.nio.file.Path;
import java.util.*;

import static ru.kelcuprum.alinlib.gui.Colors.BLACK_ALPHA;
import static ru.kelcuprum.alinlib.gui.GuiUtils.interpolate;

public class DesktopScreen extends Screen {
    public static ArrayList<Window> windows = new ArrayList<>();
    public DesktopScreen() {
        super(Component.translatable("minedows.desktop"));
    }

    public boolean startMenuShowed = false;
    public static long lastClosedWindow;

    @Override
    public boolean isPauseScreen() {
        boolean b = true;
        for(Window window : windows){
            if (window.screen instanceof ReceivingLevelScreen || window.screen instanceof LevelLoadingScreen) {
                b = false;
                break;
            }
        }
        return b;
    }

    VerticalConfigureScrolWidget verticalConfigureScrolWidget;
    int xWindows;
    @Override
    protected void init() {
        int x = 7, y = 7;
        for(Action action : getLabelActions()){
                Config config = new Config(Windows.config.getJsonObject(action.title.toString(), new JsonObject()));
                if (config.getBoolean("enable", true)) {
                    LabelWidget label = new LabelWidget(config.getNumber("x", x).intValue(), config.getNumber("y", y).intValue(), 40,
                            action);
                    label.setConfig(action.title.toString(), config);
                    label.setDefaultPosition(x, y);
                    if (!Windows.config.getBoolean("LABEL.CUSTOM_POS", false)) label.setPosition(x, y);
                    if (label.getY() + label.getHeight() > height - taskbarSize) {
//                        if (label.getY() == y) {
                            y = 7;
                            x += label.getWidth() + 7;
//                        }
                        label.setPosition(x, y);
                    }
                    addRenderableWidget(label);
                    y += (label.getHeight() + 7);
                }
        }
        if(Windows.isModMenuInstalled()){
            ArrayList<ModMenuActions.ModInfo> mods = ModMenuActions.getMods();
            for(ModMenuActions.ModInfo mod : mods){
                Config config = new Config(Windows.config.getJsonObject(mod.id(), new JsonObject()));
                if(config.getBoolean("enable", !mod.isLibrary())) {
                    LabelWidget label = new LabelWidget(config.getNumber("x", x).intValue(), config.getNumber("y", y).intValue(), 40,
                            new Action(Action.Type.OPEN_SCREEN, Component.literal(mod.name()), mod.icon(), mod.screen()));
                    label.setConfig(mod.id(), config);
                    label.setDefaultPosition(x, y);
                    if(!Windows.config.getBoolean("LABEL.CUSTOM_POS", false)) label.setPosition(x, y);
                    if (label.getY() + label.getHeight() > height - taskbarSize) {
                        if (label.getY() == y) {
                            y = 7;
                            x += label.getWidth() + 7;
                        }
                        label.setPosition(x, y);
                    }
                    addRenderableWidget(label);
                    y += (label.getHeight() + 7);
                }
            }
        }
//        if(windows.isEmpty()){
//            addWindow(new WindowBuilder().setScreen(new BrowserScreen()).build());
//        }
//        addRenderableWidget(new LabelWidget(5, 5, 40, new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.start.debug"), GuiUtils.getResourceLocation("windows", "textures/start/icons/world.png"), new DebugScreen())));
//        addRenderableWidget(new LabelWidget(50, 5, 40, new Action(Action.Type.OPEN_SCREEN, Component.translatable("title.multiplayer.realms"), GuiUtils.getResourceLocation("windows", "textures/start/icons/realms.png"), new RealmsMainScreen(null))));
        if(verticalConfigureScrolWidget == null){
            verticalConfigureScrolWidget = new VerticalConfigureScrolWidget(xWindowTask, height, width-taskbarSize-xWindowTask-((2 + 8 + font.width(AlinLib.localization.getParsedText("{time}")))), 3, Component.empty(), (scroller) -> {
                scroller.innerHeight = 0;
                for (Window widget : taskbarWindow) {
                    int xAdditional = 0;
                    if(widget.icon != null) xAdditional=15;
                    if(taskbarWindow.indexOf(widget) == 0) {
                        xWindows = xWindowTask + (int) (scroller.innerHeight - scroller.scrollAmount());
                    }
                    Component title = widget.screen.getTitle().equals(Component.empty()) ? Component.literal(String.format("%s.exe", widget.screen.getClass().getSimpleName())) : widget.screen.getTitle();
                    scroller.innerHeight += (2 + 8 + xAdditional + font.width(title) + 2);
                }
                scroller.innerHeight -=8;
            });
        } else {
            verticalConfigureScrolWidget.innerHeight = 0;
            verticalConfigureScrolWidget.setScrollAmount(0);
            verticalConfigureScrolWidget.setWidth(width-taskbarSize-xWindowTask-((2 + 8 + font.width(AlinLib.localization.getParsedText("{time}")))));
            verticalConfigureScrolWidget.setPosition(xWindowTask, height);
        }
        for(Window window : windows){
            if(window.maximize){
                window.setSize(width, height-taskbarSize);
                window.screen.resize(this.minecraft, (int) window.width - 6, (int) window.height - 22);
            } else {
                if(window.resizable && (height < window.height || width < window.width)){
                    int wi = 0;
                    int he = 0;
                    if(height < window.height) he = (int) (height*0.75);
                    else he = (int) window.height;
                    if(width < window.width) wi = (int) (width*0.75);
                    else wi = (int) window.width;
                    window.setSize(wi, he);
                    window.screen.resize(this.minecraft, wi-6, he-19);
                }
                if(window.x < 0 || window.y < 0 || window.x > width || window.y > height-taskbarSize){
                    window.setPosition(0, 0);
                }
            }
        }
    }

    @Override
    public void rebuildWidgets() {
        super.rebuildWidgets();
        for(Window window : windows){
            window.screen.rebuildWidgets();
        }
    }

    public static ArrayList<Action> getLabelActions(){
        ArrayList<Action> apps = new ArrayList<>();
        apps.add(new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.paint"), GuiUtils.getResourceLocation("windows", "textures/start/icons/paint.png"), new WindowBuilder().setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/paint.png")).setScreen(new PaintScreen())));
        apps.add(new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.calc"), GuiUtils.getResourceLocation("windows", "textures/start/icons/calc.png"), new WindowBuilder().setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/calc.png")).setResizable(false).setSize(206, 165).setScreen(new CalcScreen())));
        if(FabricLoader.getInstance().isModLoaded("mcef")) apps.add(new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.start.browser"), GuiUtils.getResourceLocation("windows", "textures/browser/icon.png"), new WindowBuilder().setIcon(GuiUtils.getResourceLocation("windows", "textures/browser/icon.png")).setScreen(new BrowserScreen())));
        apps.add(new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.control"), GuiUtils.getResourceLocation("windows", "textures/start/icons/computer_gear.png"), new WindowBuilder().setSize(325, 220).setScreen(new ControlPanelScreen())));
        apps.add(new Action(() -> {
            addWindow(new WindowBuilder().setScreen(AlinLib.MINECRAFT.level != null ? new PauseScreen(true) : new TitleScreen()).build());
        }, Component.literal("Minecraft Menu"), GuiUtils.getResourceLocation("windows", "textures/start/icons/minecraft.png")));
        return apps;
    }

    public static ArrayList<Window> taskbarWindow = new ArrayList<>();
    public static void addWindow(Window window){
        if(window.visible && !windows.isEmpty()) {
            window.active = true;
            windows.getLast().active = false;
        }
        window.screen.init(AlinLib.MINECRAFT, (int) window.width - 6, (int) window.height - 22);
        windows.add(window);
        taskbarWindow.add(window);
    }

    public static void removeWindow(Window window){
        if(windows.contains(window)) {
            lastClosedWindow = System.currentTimeMillis();
            window.screen.removed();
            windows.remove(window);
            taskbarWindow.remove(window);
            window.screen.onClose();
        }
    }

    Component startComponent = Component.translatable("minedows.start");

    public Perlin2D perlin = new Perlin2D(0L);
    int xPerlin = 0;
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Background
        if(minecraft.level != null) super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        else {
            switch(Windows.config.getNumber("WALLPAPER.TYPE", 0).intValue()){
                case 1 -> guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("minedows", "perlin"), 0, 0, 0, 0, width, height, width, height);
                case 2 -> {
                    renderPanorama(guiGraphics, partialTick);
                    guiGraphics.fill(0, 0, width, height, 0x5F000000);
                }
                case 3 -> {
                    int x = 0;
                    int y = 0;
                    double scale;
                    scale = (double) WallpaperUtil.width / width;
                    int ww = (int) (WallpaperUtil.width / scale);
                    int wh = (int) (WallpaperUtil.height / scale);
                    if(wh < height){
                        scale = (double) WallpaperUtil.height / height;
                        ww = (int) (WallpaperUtil.width / scale);
                        wh = height;
                    }
                    x = (ww-width) / 2;
                    y = (wh-height) / 2;
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, WallpaperUtil.location, 0, 0, x, y, ww, wh, ww, wh);
                }
                default -> guiGraphics.fill(0, 0, width, height, Windows.config.getNumber("WALLPAPER.COLOR", 0xFF177f96).intValue());
            }
//            int SEED = 0;
//            double FREQUENCY = 1.0 / 24.0;
//            for(int xd = 0; xd<width;xd+=6){
//                for(int yd = 0; yd<height;yd+=6){
//                    float value = perlin.getNoise((xd+xPerlin) / 100f, yd / 100f, 8, 0.2f) + .5f;
//                    int color = value > 0.5 ? 0xFF000000 : 0xFFFFFFFF;
//                    guiGraphics.fill(xd, yd, xd+6, yd+6, color);
//                }
//            }
//            xPerlin++;
//
        }
    }

    int taskbarSize = 20;
    int startMenuWidth = 150;
    int startMenuHeight = 150;
    public HashMap<String, ArrayList<Action>> getActionsMainMenu(){
        HashMap<String, ArrayList<Action>> links = new HashMap<>();
        if(minecraft == null) return links;
        ArrayList<Action> system = new ArrayList<>();
        system.add(new Action(Action.Type.STOP_GAME, Component.translatable("minedows.start.stop"), GuiUtils.getResourceLocation("windows", "textures/start/icons/shutdown.png")));
        if(Minecraft.getInstance().level != null) {
            system.add(new Action(Action.Type.DISCONNECT, Component.translatable("minedows.start.disconnect"), GuiUtils.getResourceLocation("windows", "textures/start/icons/key_world.png")));
            system.add(new Action(Action.Type.UNPAUSE_GAME, Component.translatable("minedows.start.resume"), GuiUtils.getResourceLocation("windows", "textures/start/icons/pwgoodstore.png")));
        }
        links.put("0", system);
        ArrayList<Action> config = new ArrayList<>();
        config.add(new Action(Action.Type.OPEN_SCREEN, Component.translatable("options.accessibility"), GuiUtils.getResourceLocation("windows", "textures/start/icons/accessibility.png"), new AccessibilityOptionsScreen(null, minecraft.options)));
        config.add(new Action(Action.Type.OPEN_SCREEN, Component.translatable("options.language"), GuiUtils.getResourceLocation("windows", "textures/start/icons/language.png"), new LanguageSelectScreen(null, minecraft.options, minecraft.getLanguageManager())));
        config.add(new Action(Action.Type.OPEN_SCREEN, Component.translatable("menu.options"), GuiUtils.getResourceLocation("windows", "textures/start/icons/options.png"), new OptionsScreen(null, minecraft.options)));
        config.add(new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.control"), GuiUtils.getResourceLocation("windows", "textures/start/icons/computer_gear.png"), new WindowBuilder().setSize(325, 220).setScreen(new ControlPanelScreen())));
        links.put("1", config);
        ArrayList<Action> title = new ArrayList<>();
        if(Minecraft.getInstance().level != null) {
            title.add(new Action(Action.Type.OPEN_SCREEN, Component.translatable("gui.stats"), GuiUtils.getResourceLocation("windows", "textures/start/icons/stats.png"), new StatsScreen(null, Minecraft.getInstance().player.getStats())));
            title.add(new Action(Action.Type.OPEN_SCREEN, Component.translatable("gui.advancements"), GuiUtils.getResourceLocation("windows", "textures/start/icons/advancements.png"), new AdvancementsScreen(Minecraft.getInstance().player.connection.getAdvancements(), null)));
            if(this.minecraft.hasSingleplayerServer() && !this.minecraft.getSingleplayerServer().isPublished()) title.add(new Action(Action.Type.OPEN_SCREEN, Component.translatable("menu.shareToLan"), GuiUtils.getResourceLocation("windows", "textures/start/icons/world_network.png"), new ShareToLanScreen(null)));
        } else {
            title.add(new Action(Action.Type.OPEN_SCREEN, Component.translatable("menu.online"), GuiUtils.getResourceLocation("windows", "textures/start/icons/realms.png"), new RealmsMainScreen(null)));
            title.add(new Action(Action.Type.OPEN_SCREEN, Component.translatable("multiplayer.title"), GuiUtils.getResourceLocation("windows", "textures/start/icons/world_network.png"), new JoinMultiplayerScreen(null)));
            title.add(new Action(Action.Type.OPEN_SCREEN, Component.translatable("title.singleplayer"), GuiUtils.getResourceLocation("windows", "textures/start/icons/world.png"), new SelectWorldScreen(null)));
        }
        links.put("2", title);
        ArrayList<Action> apps = new ArrayList<>();
        if(Windows.isModMenuInstalled() || Windows.isCatalogueInstalled()){
            apps.add(new Action(Action.Type.OPEN_SCREEN, (Windows.isModMenuInstalled() ? ModMenuActions.getModText() : Windows.isCatalogueInstalled() ? Component.translatable("catalogue.gui.mod_list") : Component.empty()), GuiUtils.getResourceLocation("windows", "textures/start/icons/cmd.png"), Windows.isCatalogueInstalled() ? CatalogueActions.getScreen() : ModMenuActions.getScreen()));
        }
        apps.add(new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.paint"), GuiUtils.getResourceLocation("windows", "textures/start/icons/paint.png"), new WindowBuilder().setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/paint.png")).setScreen(new PaintScreen())));
        apps.add(new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.calc"), GuiUtils.getResourceLocation("windows", "textures/start/icons/calc.png"), new WindowBuilder().setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/calc.png")).setResizable(false).setSize(206, 165).setScreen(new CalcScreen())));
        if(FabricLoader.getInstance().isModLoaded("mcef")) apps.add(new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.start.browser"), GuiUtils.getResourceLocation("windows", "textures/browser/icon.png"), new WindowBuilder().setIcon(GuiUtils.getResourceLocation("windows", "textures/browser/icon.png")).setScreen(new BrowserScreen())));
        if(FabricLoader.getInstance().isDevelopmentEnvironment()) {
            apps.add(new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.start.debug"), GuiUtils.getResourceLocation("windows", "textures/start/icons/debug.png"), new DebugScreen()));
        }
        links.put("3", apps);
        return links;
    }



    public void updateStartMenuSizes(HashMap<String, ArrayList<Action>> categories){
        startMenuWidth = startMenuHeight = 150;
        boolean isFirst = true;
        int yA = 8;
        int xA = 55;
        for(String category : categories.keySet()){
            ArrayList<Action> actions = categories.get(category);
            if(actions.isEmpty()) continue;
            if(isFirst) isFirst = false;
            else {
                yA +=8;
            }
            for(Action action : actions){
                yA += 22;
                xA = Math.max(55+font.width(action.title), xA);
            }
        }
        startMenuHeight = Math.max(150, yA);
        startMenuWidth = Math.max(150, xA);
    }
    public int xWindowTask = 0;
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, getMouseForLabels(mouseX, mouseY)[0], getMouseForLabels(mouseX, mouseY)[1], partialTick);
        if(Windows.isDeveloperPreview() && !Windows.config.getBoolean("DISABLE_WARN_TEXT", false)){
            Component warn = Component.translatable("minedows.warn.development.build");
            List<FormattedCharSequence> warning = font.split(warn, width/2);
            int yG = height-11-taskbarSize-(font.lineHeight*2);
            for(FormattedCharSequence w : warning) yG -= (3 + font.lineHeight);
            String title = String.format("Minedows 98 v%s", FabricLoader.getInstance().getModContainer("minedows").get().getMetadata().getVersion().getFriendlyString());
            String copy = "@Clovisoft";
            guiGraphics.drawString(font, title, width-5- font.width(title), yG, -1);
            yG+=(3+font.lineHeight);
            for(FormattedCharSequence formattedCharSequence : warning) {
                guiGraphics.drawString(font, formattedCharSequence, width - 5 - font.width(formattedCharSequence), yG, -1);
                yG += (3 + font.lineHeight);
            }
            guiGraphics.drawString(font, copy, width-5- font.width(copy), yG, -1);
        }
        // Window
        for (Window window : windows) {
            if(window.visible) renderWindow(guiGraphics, window, mouseX, mouseY, partialTick);
        }
        int textY = height - taskbarSize + 4 + ((taskbarSize - 4 - font.lineHeight) / 2);
        // Panel
        int[] colors = WinColors.getWindowColors();
        int[] hcolors = WinColors.getHorizontalRuleColors();
        guiGraphics.fill(0, height - (taskbarSize), width, height, colors[2]);
        guiGraphics.fill(0, height - (taskbarSize), width, height - (taskbarSize-2), colors[0]);
        guiGraphics.fill(0, height - (taskbarSize-1), width, height - (taskbarSize-2), colors[1]);
        // -=-=-=- Left elements

        if(2 < mouseX && mouseX < 2 + 8 + font.width(startComponent) && height-(taskbarSize-3) < mouseY && mouseY < height-1)
            WindowUtils.renderRevertPanel(guiGraphics,2, height-(taskbarSize-3),  2 + 8 + font.width(startComponent), height - 1);
        else WindowUtils.renderPanel(guiGraphics,2, height-(taskbarSize-3),  2 + 8 + font.width(startComponent), height - 1);
        guiGraphics.drawString(font, startComponent, 6, textY, WinColors.getTextColorWithMainColor(), false);

        if(startMenuShowed){
            HashMap<String, ArrayList<Action>> categories = getActionsMainMenu();
            updateStartMenuSizes(categories);
            int y = height - (taskbarSize-3) - startMenuHeight;
            WindowUtils.renderPanel(guiGraphics, 0, y, startMenuWidth, y+startMenuHeight);
            guiGraphics.fillGradient(3, y+3, 18, y-3+startMenuHeight, WinColors.getStartMenuGradient()[0], WinColors.getStartMenuGradient()[1]);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/start/logo.png"), 3, y-111+startMenuHeight, 0, 0, 15, 108, 15, 108);
            boolean isFirst = true;
            int yA = y+startMenuHeight-3;
            for(String category : categories.keySet()){
                ArrayList<Action> actions = categories.get(category);
                if(actions.isEmpty()) continue;
                if(isFirst) isFirst = false;
                else {
                    yA -=5;
                    guiGraphics.fill(20, yA, startMenuWidth-4, yA+2, hcolors[0]);
                    guiGraphics.fill(20, yA, startMenuWidth-4, yA+1, hcolors[1]);
                    yA -=3;
                }
                for(Action action : actions){
                    yA -= 22;
                    boolean focused = 20 < mouseX && mouseX < startMenuWidth - 4 && yA < mouseY && mouseY < yA + 20;
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, action.icon, 23, yA, 0, 0, 20, 20, 20, 20);
                    guiGraphics.drawString(font, Component.empty().append(action.title).withStyle(Style.EMPTY.withUnderlined(focused)),48, yA+(10-(font.lineHeight/2)), WinColors.getTextColorWithMainColor(), false);
                }
            }
        }

        int x = 2 + 8 + font.width(startComponent) + 4;
        int y = height - taskbarSize + 5;

        guiGraphics.fill(x, y, x + 2, height-3, hcolors[0]);
        guiGraphics.fill(x, y, x + 1, height-3, hcolors[1]);
        x += 4;
        guiGraphics.fill(x, y, x + 2, height-3, hcolors[0]);
        guiGraphics.fill(x, y, x + 1, height-3, hcolors[1]);
        x += 6;
        xWindowTask = x;
        verticalConfigureScrolWidget.setWidth(width-taskbarSize-xWindowTask-4-((2 + 8 + font.width(AlinLib.localization.getParsedText("{time}")))));
        verticalConfigureScrolWidget.setPosition(xWindowTask, height);
        int xW = xWindows;
        guiGraphics.enableScissor(xWindowTask, height-startMenuHeight, xWindowTask+verticalConfigureScrolWidget.getWidth(), height);
        for(Window window : taskbarWindow){
            Component title = window.screen.getTitle().equals(Component.empty()) ? Component.literal(String.format("%s.exe", window.screen.getClass().getSimpleName())) : window.screen.getTitle();
            int xAdditional = window.icon == null ? 0 : 15;
            if((xW < mouseX && mouseX < (xW+2 + 8 + xAdditional + font.width(title)) && height-taskbarSize+3 < mouseY && mouseY < height-1) || window.active)
                WindowUtils.renderRevertPanel(guiGraphics, xW, height-taskbarSize+3, xW+2 + 8 + xAdditional + font.width(title), height-1);
            else WindowUtils.renderPanel(guiGraphics, xW, height-taskbarSize+3, xW+2 + 8 + xAdditional + font.width(title), height-1);
            if(window.icon != null){
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, window.icon, xW+5, textY, 0, 0, 10, 10, 10, 10);
                xW+=15;
            }
            guiGraphics.drawString(font, title, xW+5, textY, WinColors.getTextColorWithMainColor(), false);
            xW+= (2 + 8 + font.width(title) + 2);
        }
        guiGraphics.disableScissor();
        float sound = AlinLib.MINECRAFT.options.getFinalSoundSourceVolume(SoundSource.MASTER);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, WinColors.getLightIcon(String.format("textures/start/volume_%s",
                sound == 0 ? "muted" : sound <= 0.1 ? "low" : sound <= 0.8 ? "ok" : "max")), xWindowTask+verticalConfigureScrolWidget.getWidth(), height-taskbarSize+3, 0 ,0, 16, 16, 16, 16);
        // -=-=-=- Right Elements
        String time = AlinLib.localization.getParsedText("{time}");
        guiGraphics.fill(width - 2, height - (taskbarSize-3), width - (2 + 9 + font.width(time)), height - 1, colors[3]);
        guiGraphics.fill(width - 2, height - (taskbarSize-4), width - (1 + 9 + font.width(time)), height - 1, colors[0]);
        guiGraphics.fill(width - 3, height - (taskbarSize-4), width - (1 + 9 + font.width(time)), height - 2, colors[2]);
        guiGraphics.drawString(font, time, width - 6 - font.width(time), textY+1, WinColors.getTextColorWithMainColor(), false);
    }

    public int[] getMouseForLabels(int x, int y){
        int[] coordinates = new int[]{x, y};
        for(Window window : windows){
            if(window.active && window.visible){
                if(window.x < x && x < window.x+window.width && window.y < y && y < window.y+window.height){
                    coordinates = new int[]{width, height};
                    break;
                }
            }
        }
        if(startMenuShowed){
            HashMap<String, ArrayList<Action>> categories = getActionsMainMenu();
            updateStartMenuSizes(categories);
            int yw = height - (taskbarSize-3) - startMenuHeight;
            if(0 < x && x < startMenuWidth && yw<y && y < yw+startMenuHeight) coordinates = new int[]{width, height};
        }
        return coordinates;
    }

    static int buttonSize = 10;
    public static Window currentRenderedWindow;
    public static void renderWindow(GuiGraphics guiGraphics, Window window, int mouseX, int mouseY, float tick) {
        int x = (int) window.x;
        int y = (int) window.y;
        int width = (int) window.width;
        int height = (int) window.height;
        int mouseXforScreen = mouseX-(x+3);
        int mouseYforScreen = mouseY-(y+19);
        if(!window.active) mouseXforScreen = mouseYforScreen = -20;
        currentRenderedWindow = window;
        //
        WindowUtils.renderPanel(guiGraphics,x, y, x + width, y + height);
        if(window.resizable && !window.maximize){
            guiGraphics.fill(x+width-4, y+height-4, x+width-3, y+height-3, 0xFF1e1e1e);
            guiGraphics.fill(x+width-4, y+height-6, x+width-3, y+height-5, 0xFF1e1e1e);
            guiGraphics.fill(x+width-4, y+height-8, x+width-3, y+height-7, 0xFF1e1e1e);

            guiGraphics.fill(x+width-5, y+height-5, x+width-4, y+height-4, 0xFF1e1e1e);
            guiGraphics.fill(x+width-5, y+height-7, x+width-4, y+height-6, 0xFF1e1e1e);


            guiGraphics.fill(x+width-6, y+height-4, x+width-5, y+height-3, 0xFF1e1e1e);
            guiGraphics.fill(x+width-6, y+height-6, x+width-5, y+height-5, 0xFF1e1e1e);


            guiGraphics.fill(x+width-7, y+height-5, x+width-6, y+height-4, 0xFF1e1e1e);

            guiGraphics.fill(x+width-8, y+height-4, x+width-7, y+height-3, 0xFF1e1e1e);
        }
        //
        int[] colors = WinColors.getTitleGradientColors();
        if(!window.active) colors = new int[]{getGrayColor(colors[0]), getGrayColor(colors[1])};
        for (int i = 0; i < width - 6; i++) {
            guiGraphics.fill(x + 3 + i, y + 3, x + 4 + i, y + 17, interpolate(colors[0], colors[1], (float) i / (width - 6)));
        }
        int xb = x+width-5-buttonSize;
        int buttonsSizes = 0;
        if(window.buttons < 2){
            WindowUtils.renderPanel(guiGraphics, xb, y+5, xb+buttonSize,y+5+buttonSize);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, WinColors.getLightIcon("textures/window/close"), xb, y+5, 0,0,buttonSize, buttonSize, buttonSize, buttonSize);
            if(xb < mouseX && mouseX < xb+buttonSize && y+5 < mouseY && mouseY < y+5+buttonSize) guiGraphics.fill(xb, y+5, xb+buttonSize, y+5+buttonSize, BLACK_ALPHA);
            buttonsSizes +=buttonSize+2;
            xb -= (2+buttonSize);
            if(window.buttons == 0 && window.resizable){
                WindowUtils.renderPanel(guiGraphics, xb, y+5, xb+buttonSize,y+5+buttonSize);
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, WinColors.getLightIcon(window.maximize ? "textures/window/window" : "textures/window/maximaze"), xb, y+5, 0,0,buttonSize, buttonSize, buttonSize, buttonSize);
                if(xb < mouseX && mouseX < xb+buttonSize && y+5 < mouseY && mouseY < y+5+buttonSize) guiGraphics.fill(xb, y+5, xb+buttonSize, y+5+buttonSize, BLACK_ALPHA);
                xb -= buttonSize;
                buttonsSizes +=buttonSize+2;
                WindowUtils.renderPanel(guiGraphics, xb, y+5, xb+buttonSize,y+5+buttonSize);
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, WinColors.getLightIcon("textures/window/mini"), xb, y+5, 0,0,buttonSize, buttonSize, buttonSize, buttonSize);
                if(xb < mouseX && mouseX < xb+buttonSize && y+5 < mouseY && mouseY < y+5+buttonSize) guiGraphics.fill(xb, y+5, xb+buttonSize, y+5+buttonSize, BLACK_ALPHA);
                buttonsSizes +=buttonSize+2;
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
        if(AlinLib.MINECRAFT.font.width(title) > maxTitleWidth){
            title = AlinLib.MINECRAFT.font.substrByWidth(Component.literal(title), maxTitleWidth - (AlinLib.MINECRAFT.font.width("..."))).getString()+"...";
        }
        guiGraphics.drawString(AlinLib.MINECRAFT.font, title, x +xTitle+ 6, y + 6, 0xFFFFFFFF, false);
        guiGraphics.pose().pushMatrix();
        guiGraphics.enableScissor(x+3, y+19, x+width-3, y+height-3);
        guiGraphics.pose().translate(x+3, y+19);
//        window.screen.renderWithTooltip(guiGraphics, mouseXforScreen, mouseYforScreen, tick);
        guiGraphics.nextStratum();
        window.screen.renderBackground(guiGraphics, mouseXforScreen, mouseYforScreen, tick);
        guiGraphics.nextStratum();
        window.screen.render(guiGraphics, mouseXforScreen, mouseYforScreen, tick);
        guiGraphics.disableScissor();
        guiGraphics.renderDeferredTooltip();
        guiGraphics.pose().translate(-x-3, -y-19);
        guiGraphics.pose().popMatrix();
//        guiGraphics.drawString(AlinLib.MINECRAFT.font, String.format("moved = %s", window.isDragging), x, y-12, -1);
        if((x+width-14 < mouseX && mouseX < x+width+6
        && y+height-14 < mouseY && mouseY < y+height+6 && window.resizable && !window.maximize) || window.isResized) Cursor.setDragging();
        else Cursor.reset();
        currentRenderedWindow = null;
    }

    public static int getGrayColor(int color){
        int r = ARGB.red(color);
        int g = ARGB.green(color);
        int b = ARGB.blue(color);
        int a = (r+g+b) / 3;
        return ARGB.color(ARGB.alpha(color), a, a, a);
    }

    @Override
    public void mouseMoved(double d, double e) {
        for(Window window : DesktopScreen.windows){
            if(window.isDragging || window.isResized) break;
            if(window.active && window.visible) {
                window.screen.mouseMoved(d-(window.x+3), e-(window.y+19));
            }
        }
        super.mouseMoved(d, e);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(!windows.isEmpty()){
            Window window = windows.getLast();
            if(window.isScreenDragging){
                int x = (int) window.x;
                int y = (int) window.y;
                window.screen.mouseDragged(mouseX-(x+3), mouseY-(y+19), button, dragX, dragY);
                return true;
            } else if(window.isDragging) {
                window.setPosition(window.x + dragX, window.y + dragY);
                return true;
            } else if(window.isResized){
                window.setSize(window.width+dragX, window.height+dragY);
                window.screen.resize(this.minecraft, (int) window.width - 6, (int) window.height - 22);
                return true;
            }
        }
        for(int i = windows.size()-1; i>=0; i--){
            Window window = windows.get(i);
            if(!window.active || !window.visible) continue;
            if(window.x < mouseX && mouseX < window.x+window.width && window.y < mouseY && mouseY < window.y+window.height && !window.isDragging && !window.isResized){
                boolean ret;
                if(mouseY < window.y+19 && !window.maximize){
                    window.setDragging(true);
                    window.setPosition(window.x+dragX, window.y+dragY);
                    ret = true;
                } else if(window.resizable && !window.maximize
                        && window.x+window.width-14 < mouseX && mouseX < window.x + window.width+6
                && window.y+window.height-14 < mouseY && mouseY < window.y + window.height+6){
                    window.setResized(true);
                    window.setSize(window.width+dragX, window.height+dragY);
                    window.screen.resize(this.minecraft, (int) window.width - 6, (int) window.height - 22);
                    ret = true;
                } else {
                    window.setScreenDragging(true);
                    int x = (int) window.x;
                    int y = (int) window.y;
//                    AlinLib.LOG.log("drag %s | x:%s  y: %s dx: %s dy: %s", window.screen.getTitle().getString(), mouseX, mouseY, dragX, dragY);
                    ret = window.screen.mouseDragged(mouseX-(x+3), mouseY-(y+19), button, dragX, dragY);
                }
                windows.getLast().active = false;
                window.active = true;
                windows.remove(i);
                windows.addLast(window);
                return ret;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    @Override
    public void onFilesDrop(List<Path> list) {
        for(Window window : DesktopScreen.windows){
            if(window.active && window.visible) {
                window.screen.onFilesDrop(list);
            }
        }
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if(windows.isEmpty()) return super.keyPressed(i, j, k);
        else for(Window window : DesktopScreen.windows){
            if(window.active && window.visible) {
                if(window.y < 0 && i == GLFW.GLFW_KEY_TAB) {
                    window.setPosition(0, 0);
                    return true;
                } else return window.screen.keyPressed(i, j, k);
            }
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    public boolean keyReleased(int i, int j, int k) {
        if(windows.isEmpty()) return super.keyReleased(i, j, k);
        else for(Window window : DesktopScreen.windows){
            if(window.active && window.visible) {
                return window.screen.keyReleased(i, j, k);
            }
        }
        return super.keyReleased(i, j, k);
    }

    @Override
    public boolean charTyped(char c, int i) {
        if(windows.isEmpty()) return super.charTyped(c, i);
        else for(Window window : DesktopScreen.windows){
            if(window.active && window.visible) {
                return window.screen.charTyped(c, i);
            }
        }
        return super.charTyped(c, i);
    }

    @Override
    public boolean isMouseOver(double d, double e) {
        boolean ret = false;
        for(int i = windows.size()-1; i>=0; i--){
            Window window = windows.get(i);
            if(window.x < d && d < window.x+window.width && window.y < e && e < window.y+window.height && window.visible){
                int x = (int) window.x;
                int y = (int) window.y;
                ret = window.screen.isMouseOver(d-(x+3), e-(y+19));
                if(ret) break;
            }
        }
        return ret || super.isMouseOver(d, e);
    }

    @Override
    public boolean mouseReleased(double d, double e, int j) {
        for(int i = windows.size()-1; i>=0; i--){
            Window window = windows.get(i);
            if (j == 0) {
                if(window.isDragging) window.setDragging(false);
                else if(window.isResized) window.setResized(false);
                else if(window.isScreenDragging) window.setScreenDragging(false);
            } else if(window.isDragging || window.isResized || window.isScreenDragging) break;
            if(window.x < d && d < window.x+window.width && window.y < e && e < window.y+window.height && window.visible && window.active){
                int x = (int) window.x;
                int y = (int) window.y;
                window.screen.mouseReleased(d-(x+3), e-(y+19), j);
            }
        }
        return super.mouseReleased(d, e, j);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f, double g) {
        if(height-taskbarSize < e && e < height){
            boolean v =  verticalConfigureScrolWidget.mouseScrolled(d, e, f, g);
            AlinLib.LOG.log("v: %s | x: %s", v, xWindows);
            return v;
        } else {
            for (int i = windows.size() - 1; i >= 0; i--) {
                Window window = windows.get(i);
                if(window.isDragging || window.isResized || window.isScreenDragging) continue;
                if (window.x < d && d < window.x + window.width && window.y < e && e < window.y + window.height && window.visible) {
                    int x = (int) window.x;
                    int y = (int) window.y;
//                AlinLib.LOG.log("click %s | x:%s  y: %s", window.screen.getTitle().getString(), d, e);
                    return window.screen.mouseScrolled(d - (x + 3), e - (y + 19), f, g);
                }
            }
        }
        return super.mouseScrolled(d, e, f, g);
    }

    @Override
    public boolean mouseClicked(double d, double e, int j
                                //#if MC >= 12109
                                //$$, boolean b
                                //#endif
    ) {

        if(2 < d && d < 2 + 8 + font.width(startComponent) && height-(taskbarSize-3) < e && e < height-1 && j == GLFW.GLFW_MOUSE_BUTTON_LEFT){
            if(Windows.config.getString("WALLPAPER.FILE", "").equals("fluffy_forever") && Windows.config.getNumber("WALLPAPER.TYPE", 0).intValue() == 3)
                AlinLib.MINECRAFT.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.CAT_AMBIENT, 1f, 1f));
            this.startMenuShowed = !startMenuShowed;
            if(!windows.isEmpty()) windows.getLast().active = false;
            SoundUtils.click();
            return false;
        } else if(xWindowTask < d && d < xWindowTask+verticalConfigureScrolWidget.getWidth() && height-taskbarSize < e && e < height){
            int xW = xWindows;
            for(Window window : taskbarWindow){
                Component title = window.screen.getTitle().equals(Component.empty()) ? Component.literal(String.format("%s.exe", window.screen.getClass().getSimpleName())) : window.screen.getTitle();
                int xAdditional = window.icon == null ? 0 : 15;
                if((xW < d && d < (xW+2 + xAdditional + 8 + font.width(title)) && height-taskbarSize+3 < e && e < height-1)){
                    if(window.active) {
                        window.visible = !window.visible;
                        if(!window.visible) {
                            windows.removeIf(window1 -> window1.uuid == window.uuid);
                            windows.addFirst(window);
                        }
                    } else {
                        for(Window window1 : windows) {
                            window1.active = false;
                        }
                        window.visible = true;

                        windows.removeIf(window1 -> window1.uuid == window.uuid);
                        windows.addLast(window);
                    }
                    SoundUtils.click();
                    window.active = !window.active;
                    if(windows.getLast().visible) windows.getLast().active = true;
                    return true;
                }
                xW+= (2 + 8 + xAdditional + font.width(title) + 2);
            }
        } else if(xWindowTask+verticalConfigureScrolWidget.getWidth() < d && d < xWindowTask+verticalConfigureScrolWidget.getWidth()+16
        && height-taskbarSize < e && e < height){
            for(Window window : windows){
                if(window.screen instanceof SoundMixerScreen){
                    windows.getLast().active = false;
                    windows.remove(window);
                    window.active = true;
                    windows.addLast(window);
                    return true;
                }
            }
            addWindow(new WindowBuilder().setSize(460, 200).setPosition(width-460, height-taskbarSize-200).setIcon(GuiUtils.getResourceLocation("windows", "textures/sound_mixer/main.png")).setResizable(false).setScreen(new SoundMixerScreen()).build());
            return true;
        }
        int yS = height - (taskbarSize-3) - startMenuHeight;
        if(startMenuShowed && 0 < d && d < startMenuWidth && yS < e && e < yS+startMenuHeight){
            HashMap<String, ArrayList<Action>> categories = getActionsMainMenu();
            updateStartMenuSizes(categories);
            int y = height - (taskbarSize-3) - startMenuHeight;
            int x = 20;
            boolean isFirst = true;
            int yA = y+startMenuHeight-3;
            for(String category : categories.keySet()){
                ArrayList<Action> actions = categories.get(category);
                if(actions.isEmpty()) continue;
                if(isFirst) isFirst = false;
                else {
                    yA -=8;
                }
                for(Action action : actions){
                    yA -= 22;
                    if(x < d && d < startMenuWidth-4 && yA < e && e < yA+20){
                        SoundUtils.click();
                        startMenuShowed = false;
                        switch (action.type){
                            case STOP_GAME -> Minecraft.getInstance().stop();
                            case UNPAUSE_GAME -> Minecraft.getInstance().setScreen(null);
                            case DISCONNECT -> PauseScreen.disconnectFromWorld(this.minecraft, ClientLevel.DEFAULT_QUIT_MESSAGE);
                            case OPEN_SCREEN -> addWindow(action.getWindow());
                            case EXECUTE_ACTION -> action.execute.execute();
                        }
                        return true;
                    }
                }
            }
        } else startMenuShowed = false;
        for(int i = windows.size()-1; i>=0; i--){
            Window window = windows.get(i);
            if(window.x < d && d < window.x+window.width && window.y < e && e < window.y+window.height && window.visible){
                int x = (int) window.x;
                int y = (int) window.y;
                int width = (int) window.width;
                windows.getLast().active = false;
                window.active = true;
                if(window.buttons < 2 && j == GLFW.GLFW_MOUSE_BUTTON_LEFT){
                    int xb = x+width-5-buttonSize;
                    if(xb < d && d < xb+buttonSize && y+5 < e && e < y+5+buttonSize) {
                        removeWindow(window);
                        if(!windows.isEmpty() && windows.getLast().visible) windows.getLast().active = true;
                        SoundUtils.click();
                        return true;
                    }
                    xb -= (2+buttonSize);
                    if(window.buttons == 0 && window.resizable){
                        if(xb < d && d < xb+buttonSize && y+5 < e && e < y+5+buttonSize) {
                            window.changeMax(this.width, this.height-taskbarSize);
                            window.screen.resize(this.minecraft, (int) window.width - 6, (int) window.height - 22);
                            SoundUtils.click();
                            return true;
                        }
                        xb -= buttonSize;
                        if(xb < d && d < xb+buttonSize && y+5 < e && e < y+5+buttonSize){
                            windows.remove(i);
                            window.visible = false;
                            window.active = false;
                            windows.addFirst(window);
                            SoundUtils.click();
                            return true;
                        }
                    }
                }
                windows.remove(i);
                windows.addLast(window);
//                AlinLib.LOG.log("click %s | x:%s  y: %s", window.screen.getTitle().getString(), d, e);
                return window.screen.mouseClicked(d-(x+3), e-(y+19), j);
            }
        }
        if(!windows.isEmpty()) windows.getLast().active = false;
        return super.mouseClicked(d, e, j);
    }

    @Override
    public void tick() {
        super.tick();
        verticalConfigureScrolWidget.onScroll.accept(verticalConfigureScrolWidget);
        try {
            for(Window window : windows){
                if(window.screen instanceof StatsScreen) ((StatsScreen) window.screen).onStatsUpdated();
                window.screen.tick();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onClose() {
        if(minecraft.level != null) minecraft.setScreen(null);
    }
}
