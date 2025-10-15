package ru.kelcu.windows.screens.options;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.components.builders.WindowBuilder;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcu.windows.screens.options.style.AirStyle;
import ru.kelcu.windows.style.MinedowsStyle;
import ru.kelcu.windows.utils.WallpaperUtil;
import ru.kelcu.windows.utils.WinColors;
import ru.kelcu.windows.utils.WindowUtils;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.components.ImageWidget;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.HorizontalRuleBuilder;
import ru.kelcuprum.alinlib.gui.components.buttons.Button;
import ru.kelcuprum.alinlib.gui.screens.ConfigScreenBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BackgroundSettings extends Screen {
    public BackgroundSettings() {
        super(Component.translatable("minedows.control.background"));
    }

    public static MinedowsStyle airStyle = Windows.minedowsStyle;
    public Button image;

    public ArrayList<AbstractWidget> widgets = new ArrayList<>();
    private ConfigureScrolWidget scroller;

    @Override
    protected void init() {
        super.init();
        int y = 150 + 8 + (font.lineHeight+5)*2;
        int x = 10;
        addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.control.background.wallpaper.conf"), (s) -> {
            DesktopScreen.addWindow(new WindowBuilder().setScreen(getConfig()).build());
        })
                .setSize(width-60-((width-40)/2), 18).setPosition(x+30+((width-40)/2), y).setStyle(Windows.minedowsStyle).build());
        image = (Button) addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.control.background.select.short"), (s) -> {
            String file = openTrackEditor();
            if(!file.isBlank()){
                try {
                    WallpaperUtil.loadFileWallpaper(Path.of(file));
                    Windows.config.setString("WALLPAPER.FILE", file);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        })
                .setSize(width-60-((width-40)/2), 18).setPosition(x+30+((width-40)/2), y+21).setStyle(Windows.minedowsStyle).build());
        addRenderableWidget(new ButtonBooleanBuilder(Component.translatable("minedows.control.label.dark_text"), false)
                .setConfig(Windows.config, "LABEL.DARK_TEXT")
                .setSize(width-60-((width-40)/2), 18).setPosition(x+30+((width-40)/2), y+42).setStyle(Windows.minedowsStyle).build());
        widgets = new ArrayList<>();
        this.scroller = addRenderableWidget(new ConfigureScrolWidget(x+((width-40)/2)+16, y+1, 3, height-y-27, Component.empty(), scroller -> {
            scroller.innerHeight = 0;
            for (AbstractWidget widget : widgets) {
                if (widget.visible) {
                    widget.setY((150 + 8 + (font.lineHeight+5)*2)+ 5 + (int) (scroller.innerHeight - scroller.scrollAmount()));
                    scroller.innerHeight += (widget.getHeight()+2);
                } else widget.setY(-widget.getHeight());
            }
        }));
        y+=5;
        widgets.add(new ButtonBuilder(Component.translatable("minedows.control.background.wallpaper.perlin_noise"), (s) -> {
            Windows.config.setNumber("WALLPAPER.TYPE", 1);
        })
                .setCentered(false)
                .setSize(((width-40)/2), 16)
                .setPosition(x+15, y)
                .setStyle(airStyle)
                .build());
        y+=20;
        widgets.add(new ButtonBuilder(Component.translatable("minedows.control.background.wallpaper.mono_color"), (s) -> {
            Windows.config.setNumber("WALLPAPER.TYPE", 0);
        })
                .setCentered(false)
                .setSize(((width-40)/2), 16)
                .setPosition(x+15, y)
                .setStyle(airStyle)
                .build());
        y+=20;
        widgets.add(new ButtonBuilder(Component.translatable("minedows.control.background.wallpaper.panorama"), (s) -> {
            Windows.config.setNumber("WALLPAPER.TYPE", 2);
        })
                .setCentered(false)
                .setSize(((width-40)/2), 16)
                .setPosition(x+15, y)
                .setStyle(airStyle)
                .build());
        y+=20;
        for(WallpaperUtil.WallpaperData wallpaperData : WallpaperUtil.wallpapers.values()){
            widgets.add(new ButtonBuilder(wallpaperData.name(), (s) -> {
                WallpaperUtil.loadWallpaper(wallpaperData);
                Windows.config.setString("WALLPAPER.FILE", wallpaperData.id());
                Windows.config.setNumber("WALLPAPER.TYPE", 3);
            })
                    .setCentered(false)
                    .setSize(((width-40)/2), 16)
                    .setPosition(x+15, y)
                    .setStyle(airStyle)
                    .build());
            y+=20;
        }
        widgets.add(new ButtonBuilder(Component.translatable("minedows.control.background.wallpaper.image"), (s) -> {
            if(hasShiftDown()){
                WallpaperUtil.fluffy();
                Windows.config.setString("WALLPAPER.FILE", "fluffy_forever");
            } else {
                String file = Windows.config.getString("WALLPAPER.FILE", "");
                if(file.isEmpty()) file = openTrackEditor();
                try {
                    if(file.isBlank() || file.equals("fluffy_forever")){
                        WallpaperUtil.fluffy();
                        Windows.config.setString("WALLPAPER.FILE", "fluffy_forever");
                        Windows.config.setNumber("WALLPAPER.TYPE", 3);
                    } else {
                        WallpaperUtil.loadFileWallpaper(Path.of(file));
                        Windows.config.setString("WALLPAPER.FILE", file);
                        Windows.config.setNumber("WALLPAPER.TYPE", 3);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        })
                .setCentered(false)
                .setSize(((width-40)/2), 16)
                .setPosition(x+15, y)
                .setStyle(airStyle)
                .build());
        addWidgetsToScroller(widgets);
    }

    public static String openTrackEditor(){
        MemoryStack stack = MemoryStack.stackPush();
        PointerBuffer filters = stack.mallocPointer(8);
        filters.put(stack.UTF8("*.png"));
        filters.put(stack.UTF8("*.jpg"));

        filters.flip();
        File defaultPath = new File(System.getProperty("user.home")).getAbsoluteFile();
        String defaultString = defaultPath.getAbsolutePath();
        if(defaultPath.isDirectory() && !defaultString.endsWith(File.separator)){
            defaultString += File.separator;
        }

        String result = TinyFileDialogs.tinyfd_openFileDialog(Component.translatable("minedows.control.background.select").getString(), defaultString, filters, Component.translatable("minedows.control.background.select.filter_description").getString(), false);
        if(result == null) return "";
        else if(new File(result).exists()) return result;
        else return "";
    }

    @Override
    public Component getTitle() {
        if(Windows.config.getString("WALLPAPER.FILE", "").equals("fluffy_forever") && Windows.config.getNumber("WALLPAPER.TYPE", 0).intValue() == 3) return Component.translatable("minedows.fluffy");
        return super.getTitle();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        int displayWidth = 160;
        int displayHeight = 90;
        int xDisplay = (width/2)-(displayWidth/2);
        int yDisplay = 20;
        WindowUtils.renderPanel(guiGraphics, xDisplay-10, yDisplay-10, xDisplay+displayWidth+10, yDisplay+displayHeight+10);
        WindowUtils.welcomeToWhiteSpace(guiGraphics, xDisplay-1, yDisplay-1, displayWidth+2, displayHeight+2);
        switch(Windows.config.getNumber("WALLPAPER.TYPE", 0).intValue()){
            case 1 -> guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("minedows", "perlin"), xDisplay, yDisplay, 0, 0, displayWidth, displayHeight, minecraft.screen.width, minecraft.screen.height);
            case 2 -> {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("textures/gui/title/background/panorama_0.png"), xDisplay, yDisplay, 0, 35, displayWidth, displayHeight, displayWidth, displayWidth);
//                renderPanorama(guiGraphics, f);
                guiGraphics.fill(xDisplay, yDisplay, xDisplay+displayWidth, yDisplay+displayHeight, 0x5F000000);
            }
            case 3 -> {
                int x = 0;
                int y = 0;
                double scale;
                scale = (double) WallpaperUtil.width / displayWidth;
                int ww = (int) (WallpaperUtil.width / scale);
                int wh = (int) (WallpaperUtil.height / scale);
                if(wh < displayHeight){
                    scale = (double) WallpaperUtil.height / displayHeight;
                    ww = (int) (WallpaperUtil.width / scale);
                    wh = displayHeight;
                }
                x = (ww-displayWidth) / 2;
                y = (wh-displayHeight) / 2;
                guiGraphics.enableScissor(xDisplay, yDisplay, xDisplay+displayWidth, yDisplay+displayHeight);
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, WallpaperUtil.location, xDisplay, yDisplay, x, y, ww, wh, ww, wh);
                guiGraphics.disableScissor();
            }
            default -> guiGraphics.fill(xDisplay, yDisplay, xDisplay+displayWidth, yDisplay+displayHeight, Windows.config.getNumber("WALLPAPER.COLOR", 0xFF177f96).intValue());
        }
        int x = 10;
        int y = 150;
        guiGraphics.drawString(font, Component.translatable("minedows.control.background.wallpaper"), x+10, y, WinColors.getTextColorWithMainColor(), false);
        y+=font.lineHeight+5;
        int[] colors = WinColors.getHorizontalRuleColors();
        guiGraphics.renderOutline(x, y, width-20, height-180, colors[0]);
        guiGraphics.renderOutline(x+1, y+1, width-22, height-182, colors[0]);
        guiGraphics.renderOutline(x, y, width-20, height-180, colors[0]);
        guiGraphics.renderOutline(x, y, width-21, height-181, colors[1]);
        y+=8;
        guiGraphics.drawString(font, Component.translatable("minedows.control.background.wallpaper.select"), x+10, y, WinColors.getTextColorWithMainColor(), false);
        y+=font.lineHeight+5;
        WindowUtils.welcomeToWhiteSpace(guiGraphics, x+10, y, x+((width-40)/2), height-y-25);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        int x = 10;
        int y = 169+(font.lineHeight*2);
        guiGraphics.enableScissor(x+10, y, x+((width-40)/2)+15, height-26);
        if (scroller != null) for (AbstractWidget widget : scroller.widgets) widget.render(guiGraphics, i, j, f);
        guiGraphics.disableScissor();
    }
    @Override
    public boolean mouseClicked(double d, double e, int i) {
        boolean st = true;
        GuiEventListener selected = null;
        int x = 10;
        int y = 163+font.lineHeight;
        for (GuiEventListener guiEventListener : this.children()) {
            if (scroller != null && scroller.widgets.contains(guiEventListener)) {
                if ((d >= x && d <= x + ((width-40)/2)) && e >= y && e <= height-25)
                    if (guiEventListener.mouseClicked(d, e, i)) {
                        st = false;
                        selected = guiEventListener;
                        break;
                    }
            } else if (guiEventListener.mouseClicked(d, e, i)) {
                st = false;
                selected = guiEventListener;
                break;
            }
        }

        this.setFocused(selected);
        if (i == 0)
            this.setDragging(true);

        return st;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean scr = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (!scr && scroller != null) scr = scroller.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        return scr;
    }

    public Screen getConfig(){
        ConfigScreenBuilder builder = new ConfigScreenBuilder(null, Component.translatable("minedows.control.background.wallpaper.configs"));
        builder.addWidget(new HorizontalRuleBuilder(Component.translatable("minedows.control.background.wallpaper.mono_color")))
                .addWidget(new EditBoxBuilder(Component.translatable("minedows.control.background.wallpaper.color")).setColor(0xFF177f96).setConfig(Windows.config, "WALLPAPER.COLOR"))
                .addWidget(new HorizontalRuleBuilder(Component.translatable("minedows.control.background.wallpaper.perlin_noise")))
                .addWidget(new EditBoxBuilder(Component.translatable("minedows.control.background.wallpaper.perlin_noise.color_start")).setColor(0Xff48cae4).setConfig(Windows.config, "PERLIN.COLOR_START"))
                .addWidget(new EditBoxBuilder(Component.translatable("minedows.control.background.wallpaper.perlin_noise.color_end")).setColor(0xFFf4f4f9).setConfig(Windows.config, "PERLIN.COLOR_END"));
        return builder.build();
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
        scroller.addWidget(widget);
        this.addWidget(widget);
    }

    @Override
    public void tick() {
        super.tick();
        if (scroller != null) scroller.onScroll.accept(scroller);
        image.setActive(Windows.config.getNumber("WALLPAPER.TYPE", 0).intValue() == 3);
    }
}
