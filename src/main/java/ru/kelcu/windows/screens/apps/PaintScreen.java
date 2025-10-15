package ru.kelcu.windows.screens.apps;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.screens.components.alinlib.EditBox;
import ru.kelcu.windows.screens.components.VerticalConfigureScrolWidget;
import ru.kelcu.windows.screens.options.style.AirStyle;
import ru.kelcu.windows.utils.WinColors;
import ru.kelcu.windows.utils.WindowUtils;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.components.builder.AbstractBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.slider.SliderBuilder;
import ru.kelcuprum.alinlib.gui.components.buttons.Button;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static ru.kelcu.windows.Windows.minedowsStyle;

public class PaintScreen extends Screen {
    public BufferedImage bufferedImage;
    public PaintScreen() {
        super(Component.translatable("minedows.paint"));
    }
    public final UUID uuid = UUID.randomUUID();
    int x = 42;
    int y = 2;
    EditBox color;
    int colorsMaxWidth = 0;
    public VerticalConfigureScrolWidget scroller;
    public ArrayList<AbstractWidget> widgets = new ArrayList<>();
    Button back;
    Button forward;
    @Override
    protected void init() {
        super.init();
        widgets = new ArrayList<>();
        int y = 0;
        addRenderableWidget(new ButtonBuilder(Component.translatable("windows.paint.pen"), (s) -> {
            isPanMode = true; isWihxhxMode = false;
        })
                .setSprite(GuiUtils.getResourceLocation("windows", "textures/paint/pan.png")).setSize(20, 20).setPosition(0, y)
                .setStyle(minedowsStyle).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("windows.paint.brush"), (s) -> isPanMode = isWihxhxMode = false)
                .setSprite(GuiUtils.getResourceLocation("windows", "textures/paint/brush.png")).setSize(20, 20).setPosition(20, y)
                .setStyle(minedowsStyle).build());
        y+=20;
        addRenderableWidget(new ButtonBuilder(Component.translatable("windows.paint.eraser"), (s) -> isWihxhxMode =  true)
                .setSprite(GuiUtils.getResourceLocation("windows", "textures/paint/wihxhx.png")).setSize(20, 20).setPosition(0, y)
                .setStyle(minedowsStyle).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("windows.paint.pick_color"), (s) -> {
            isPickMode = true; isWihxhxMode = false;
        })
                .setSprite(GuiUtils.getResourceLocation("windows", "textures/paint/pick.png")).setSize(20, 20).setPosition(20, y)
                .setStyle(minedowsStyle).build());
        y+=20;
        back = (Button) addRenderableWidget(new ButtonBuilder(Component.translatable("windows.paint.back"), (s) -> back())
                .setSprite(WinColors.getLightIcon( "textures/browser/back")).setSize(20, 20).setPosition(0, y)
                .setStyle(minedowsStyle).build());
        forward = (Button) addRenderableWidget(new ButtonBuilder(Component.translatable("windows.paint.forward"), (s) -> {
            forward();
        })
                .setSprite(WinColors.getLightIcon("textures/browser/forward")).setSize(20, 20).setPosition(20, y)
                .setStyle(minedowsStyle).build());
        y+=20;
        addRenderableWidget(new ButtonBuilder(Component.translatable("windows.paint.save"), (s) -> {
            try {
                savePaint();
            } catch (Exception exception){
                exception.printStackTrace();
            }
        })
                .setSprite(GuiUtils.getResourceLocation("windows", "textures/paint/save.png")).setSize(20, 20).setPosition(0, y)
                .setStyle(minedowsStyle).build());
        y = height-58;

        addRenderableWidget(new SliderBuilder(Component.translatable("minedows.paint.marker_size"), (s) -> {
            this.markerSize = (int) (1 + (19*s));
        }).setDefaultValue(markerSize).setMin(1).setMax(20).setSize(120, 16).setPosition(2, height-16).setStyle(new AirStyle()).build());

        addRenderableWidget(new SliderBuilder(Component.translatable("minedows.paint.eraser_size"), (s) -> {
            this.eraserSize = (int) (1 + (19*s));
        }).setDefaultValue(markerSize).setMin(1).setMax(20).setSize(120, 16).setPosition(124, height-16).setStyle(new AirStyle()).build());
        color = addRenderableWidget(new EditBox(new EditBoxBuilder(Component.translatable("minedows.paint.color"))
                .setColor(panColor).setResponder((s)->{
                    AlinLib.LOG.log(s);
                    setColor((int)Long.parseLong(s.toUpperCase(), 16));}).setSize(120, 16).setPosition(246, height-16).setStyle(new AirStyle())));

        int x = 44;
        int finalX = x;
        scroller = new VerticalConfigureScrolWidget(44, height, width-44, 4, Component.empty(), (s)->{
            scroller.innerHeight = 0;
            int i = 0;
            for (AbstractWidget widget : widgets) {
                if (widget.visible) {
                    widget.setPosition((finalX + (int) (scroller.innerHeight - scroller.scrollAmount())), height-58+(widget.getHeight()*i));
                    if(i == 1){
                        i=0;
                        scroller.innerHeight += (widget.getWidth());
                    } else i++;
                } else widget.setY(-widget.getHeight());
            }
        });
        addRenderableWidget(scroller);
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFFFFFFFF));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF000000));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFF7fb238));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFFf7e9a3));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFFc7c7c7));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFFff0000));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFFa0a0ff));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFFa7a7a7));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFF007c00));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFFa4a8b8));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFF976d4d));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF707070));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFF4040ff));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF8f7748));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFFfffcf5));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFFd87f33));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFFb24cd8));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF6699d8));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFFe5e533));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF7fcc19));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFFf27fa5));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF4c4c4c));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFF999999));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF4c7f99));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFF7f3fb2));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF334cb2));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFF664c33));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF667f33));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFF993333));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF191919));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFFfaee4d));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF5cdbd5));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFF4a80ff));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF00d93a));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFF815631));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF700200));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFFd1b1a1));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF9f5224));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFF95576c));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF706c8a));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFFba8524));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF677535));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFFa04d4e));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF392923));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFF876b62));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF575c5c));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFF7a4958));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF4c3e5c));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFF4c3223));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF4c522a));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFF8e3c2e));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF251610));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFFbd3031));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF943f61));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFF5c191d));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF167e86));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFF3a8e8c));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF562c3e));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFF14b485));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF646464));
        x+=20;
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y), 0xFFd8af93));
        widgets.add(new ButtonColor(new ButtonBuilder(Component.empty(), (s) -> setColor(((ButtonColor) s).color)).setSize(20, 20).setPosition(x, y+20), 0xFF7fa796));
        x+=20;
        colorsMaxWidth = x;
        addWidgetsToScroller(widgets, scroller);
        resize();
        if(history.isEmpty()) addCurrentImageToHistory();
    }
    public void addWidgetsToScroller(java.util.List<AbstractWidget> widgets) {
        addWidgetsToScroller(widgets, this.scroller);
    }


    public void addWidgetsToScroller(AbstractWidget widget) {
        addWidgetsToScroller(widget, this.scroller);
    }

    public void addWidgetsToScroller(java.util.List<AbstractWidget> widgets, ConfigureScrolWidget scroller) {
        for (AbstractWidget widget : widgets) addWidgetsToScroller(widget, scroller);
    }
    public void addWidgetsToScroller(AbstractWidget widget, ConfigureScrolWidget scroller) {
        widget.setY(-100);
        scroller.addWidget(widget);
        this.addWidget(widget);
    }

    public void addWidgetsToScroller(List<AbstractWidget> widgets, VerticalConfigureScrolWidget scroller) {
        for (AbstractWidget widget : widgets) addWidgetsToScroller(widget, scroller);
    }
    public void addWidgetsToScroller(AbstractWidget widget, VerticalConfigureScrolWidget scroller) {
        widget.setY(-100);
        scroller.addWidget(widget);
        this.addWidget(widget);
    }

    public void savePaint() throws IOException {
        MemoryStack stack = MemoryStack.stackPush();
        PointerBuffer filters = stack.mallocPointer(8);
        filters.put(stack.UTF8("*.png"));

        filters.flip();
        File defaultPath = new File(System.getProperty("user.home")).getAbsoluteFile();
        String defaultString = defaultPath.getAbsolutePath();
        if(defaultPath.isDirectory() && !defaultString.endsWith(File.separator)){
            defaultString += File.separator;
        }

        String result = TinyFileDialogs.tinyfd_saveFileDialog(Component.translatable("minedows.control.background.save").getString(), defaultString, filters, Component.translatable("minedows.control.background.select.filter_description").getString());
        if(result == null) return;
        Path path = Path.of(result);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        Files.createDirectories(path.getParent());
        Files.write(path, byteArrayOutputStream.toByteArray());
    }

    public void resize(){
        BufferedImage bufferedImage1 = new BufferedImage(Math.max(width-x-3, 1), Math.max(height-64, 1), BufferedImage.TYPE_INT_ARGB);
        for(int y = 0; y<bufferedImage1.getHeight();y++){
            for(int x = 0; x<bufferedImage1.getWidth();x++){
                bufferedImage1.setRGB(x, y, 0xFFFFFFFF);
            }
        }
        if(bufferedImage != null) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                for (int x = 0; x < bufferedImage.getWidth(); x++) {
                    if (y < bufferedImage1.getHeight() && x < bufferedImage1.getWidth())
                        bufferedImage1.setRGB(x, y, bufferedImage.getRGB(x, y));
                }
            }
        }
        bufferedImage = bufferedImage1;
        try {
            updateTexture();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        super.renderBackground(guiGraphics, i, j, f);
        WindowUtils.welcomeToWhiteSpace(guiGraphics, x, 1, width-x-1, height-62);
        int xm = (int) (i-x);
        int ym = (int) (j-y);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("minedows", String.format("paint-%s", uuid)), x+1, y, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getWidth(), bufferedImage.getHeight());
        if(x < i && i < width-1 && y < j && j < y+bufferedImage.getHeight()) {
            if (FabricLoader.getInstance().isDevelopmentEnvironment())
                guiGraphics.drawString(font, String.format("%sx%s %s", xm, ym, isDragging()), i + 2, j - 9, 0xFF000000, false);
            guiGraphics.fill(i - (getMarkerSize()-1), j - (getMarkerSize()-1), i + (getMarkerSize()+1), j + (getMarkerSize()+1), 0xFF000000);
            if(isWihxhxMode) guiGraphics.blit(RenderPipelines.GUI_TEXTURED,  GuiUtils.getResourceLocation("windows", "textures/paint/wihxhx.png"), i-3, j-10, 0, 0, 16, 16, 16, 16);
            else if(isPickMode) guiGraphics.blit(RenderPipelines.GUI_TEXTURED,  GuiUtils.getResourceLocation("windows", "textures/paint/pick.png"), i-3, j-10, 0, 0, 16, 16, 16, 16);
            else if(isPanMode) guiGraphics.blit(RenderPipelines.GUI_TEXTURED,  GuiUtils.getResourceLocation("windows", "textures/paint/pan.png"), i-3, j-10, 0, 0, 16, 16, 16, 16);
        }
        WindowUtils.welcomeToWhiteSpace(guiGraphics, 2, height-58, 40, 40);
        guiGraphics.fill(3, height-57, 41, height-19, panColor);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        int xm = (int) (d-x);
        int ym = (int) (e-y);
        boolean st = true;
        if(x < d && xm < bufferedImage.getWidth() && y < e && ym < bufferedImage.getHeight()){
                if(isPickMode){
                    isPickMode = false;
                    panColor = bufferedImage.getRGB(xm, ym);
                } else {
                    draw(xm, ym);
                }
        } else {
//            return super.mouseClicked(d, e, i);

            if(height-60 < e && e < height-20) {
                GuiEventListener selected = null;
                for (GuiEventListener guiEventListener : this.children()) {
                    if (scroller != null && scroller.widgets.contains(guiEventListener)) {
                        if (d >= 44 && d <= width) {
                            if (guiEventListener.mouseClicked(d, e, i)) {
                                st = false;
                                selected = guiEventListener;
                                break;
                            }
                        }
                    }
                }

                this.setFocused(selected);
            } else st = super.mouseClicked(d, e, i);
        }
        return st;
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double d1, double e1) {
        int xm = (int) (d-x);
        int ym = (int) (e-y);
        if(x < d && xm < bufferedImage.getWidth() && y < e && ym < bufferedImage.getHeight()){
            setFocused(false);
            setFocused(null);
            isPainting = true;
            draw(xm, ym);
        } else {
            isPainting = false;
        }
        return super.mouseDragged(d, e, i, d1, e1) || isPainting;
    }

    @Override
    public void mouseMoved(double d, double e) {
        if(isPainting){
            int xm = (int) (d-x);
            int ym = (int) (e-y);
            if(d-x < bufferedImage.getWidth() && e-y < bufferedImage.getHeight()){
                draw(xm, ym);
            }
        }
        super.mouseMoved(d, e);
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        if(i == 0 && isPainting){
            isPainting = false;
        }
        return super.mouseReleased(d, e, i);
    }

    int lastX = 0;
    int lastY = 0;
    long lastHell = 0;

    public void draw(int x, int y){
        if(x < 0 || y < 0) return;
        new Thread(() -> {
            if(System.currentTimeMillis() - lastHell < 50){
                Graphics2D g2d = bufferedImage.createGraphics();
                g2d.setColor(new Color(getPancakeColor()));
                for(int i = -getMarkerSize(); i <= getMarkerSize(); i++)
                    for(int j = -getMarkerSize(); j <= getMarkerSize(); j++)
                        g2d.drawLine(x+i, y+j, lastX+i, lastY+j);
                g2d.dispose();
            }
            lastHell = System.currentTimeMillis();
            lastX = x;
            lastY = y;
            drawOnePixel(x, y);
            try {
                updateTexture();
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }).start();
    }

    public void drawOnePixel(int x, int y){
        for(int xi = x-getMarkerSize(); xi<=x+getMarkerSize(); xi++){
            for(int yi = y-getMarkerSize(); yi<=y+getMarkerSize(); yi++){
                if(xi >= 0 && yi >= 0 && xi < bufferedImage.getWidth() && yi < bufferedImage.getHeight()) bufferedImage.setRGB(xi, yi, getPancakeColor());
            }
        }
    }

    public ArrayList<BufferedImage> history = new ArrayList<>();
    public boolean isPainting = false;
    public int positionOnHistory = 0;
    public boolean isNotPosition = false;
    public final int maxSize = 50;
    public void addCurrentImageToHistory(){
        if(history.size() == (maxSize+1)){
            history.removeFirst();
        }
        if(history.size()-1 != positionOnHistory && !history.isEmpty()){
            AlinLib.LOG.log("he");
            ArrayList<BufferedImage> newHistory = new ArrayList<>();
            for(int i = 0; i<=positionOnHistory;i++){
                newHistory.add(history.get(i));
            }
            history = newHistory;
        }
        positionOnHistory = history.size();
        BufferedImage newB = cloneBufferedImage(bufferedImage);
        history.addLast(newB);
//        AlinLib.LOG.log("Position: %s | %s", positionOnHistory, history.size());
    }

    public static BufferedImage cloneBufferedImage(BufferedImage bufferedImage){
        BufferedImage newB = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
        for(int x = 0; x<newB.getWidth();x++){
            for(int y = 0; y<newB.getHeight();y++){
                newB.setRGB(x, y, bufferedImage.getRGB(x, y));
            }
        }
        return newB;
    }

    public void back(){
        try {
            if(positionOnHistory != 0) {
                isNotPosition = true;
                positionOnHistory = Math.max(0, positionOnHistory - 1);
//                AlinLib.LOG.log("b - %s", positionOnHistory);
                bufferedImage = cloneBufferedImage(history.get(positionOnHistory));
                resize();
                updateTexture();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void forward(){
        try {
            if(positionOnHistory != history.size()+1) {
                positionOnHistory = Math.min(positionOnHistory + 1, history.size() - 1);
//                AlinLib.LOG.log("f - %s", positionOnHistory);
                bufferedImage = cloneBufferedImage(history.get(positionOnHistory));
                resize();
                updateTexture();
            }
    } catch (Exception ex){
        ex.printStackTrace();
    }
    }

    public void reset(){
        bufferedImage = null;
        resize();
        history = new ArrayList<>();
        positionOnHistory=0;
        addCurrentImageToHistory();
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if(i == GLFW.GLFW_KEY_DELETE) reset();
        else if(i == GLFW.GLFW_KEY_Z && hasControlDown()){
            if(hasShiftDown()) forward();
            else back();
        }
        return super.keyPressed(i, j, k);
    }

    boolean isPanMode = false;
    boolean isPickMode = false;
    boolean isWihxhxMode = false;
    int markerSize = 1;
    int eraserSize = 1;
    int panColor = 0xFF000000;
    public int getMarkerSize(){
        return isPickMode ? 0 : isWihxhxMode ? eraserSize : isPanMode ? 0 : markerSize;
    }
    public int getPancakeColor(){
        if(isWihxhxMode) return 0xFFffffff;
        else if(isPanMode){
            int color = panColor;
            int r = ARGB.red(color);
            int g = ARGB.green(color);
            int b = ARGB.blue(color);
            int a = (int) ((255*(0.5)) + (255*0.5*Math.random()));
            return ARGB.color(a, r, g, b);
        } else return panColor;
    }
    public boolean lastDodep = false;
    @Override
    public void tick() {
        if (scroller != null) scroller.onScroll.accept(scroller);

        if(lastDodep != isPainting){
            if(lastDodep) addCurrentImageToHistory();
            lastDodep = isPainting;
        }
        int vColor = color.getValue().isBlank() ? 0 : (int) Long.parseLong(color.getValue(), 16);
        if(panColor != vColor){
            panColor =  vColor;
        }
    }
    public void setColor(int color1){
        panColor = color1;
        color.setValue(Integer.toHexString(panColor));
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f, double g) {
        if (scroller != null) return scroller.mouseScrolled(d, e, f, g);
        return super.mouseScrolled(d, e, f, g);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        guiGraphics.enableScissor(44, 0, width, height);
        if (scroller != null) for (AbstractWidget widget : scroller.widgets) widget.render(guiGraphics, i, j, f);
        guiGraphics.disableScissor();
    }

    public static class ButtonColor extends Button{
        public final int color;
        public ButtonColor(AbstractBuilder builder, int color) {
            super(builder);
            this.color = color;
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
            WindowUtils.welcomeToWhiteSpace(guiGraphics, getX(), getY(), getRight()-getX(), getBottom()-getY());
            guiGraphics.fill(getX()+1, getY()+1, getRight()-1, getBottom()-1, color);
        }
    }

    public void updateTexture() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        InputStream is = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        NativeImage image = NativeImage.read(is);
        AtomicReference<DynamicTexture> texture = new AtomicReference<>();
        Minecraft.getInstance().execute(() -> {
            texture.set(new DynamicTexture(() -> "perlin", image));
            Minecraft.getInstance().getTextureManager().register(GuiUtils.getResourceLocation("minedows", String.format("paint-%s", uuid)), texture.get());
        });
    }
}
