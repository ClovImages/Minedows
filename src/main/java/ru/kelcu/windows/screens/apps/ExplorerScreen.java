package ru.kelcu.windows.screens.apps;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.glfw.GLFW;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.components.Action;
import ru.kelcu.windows.screens.components.LabelWidget;
import ru.kelcu.windows.utils.MediaType;
import ru.kelcu.windows.utils.SoundUtils;
import ru.kelcu.windows.utils.WinColors;
import ru.kelcu.windows.utils.WindowUtils;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.editbox.EditBox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExplorerScreen extends Screen {
    public String path;
    public ExplorerScreen(){
        this(System.getProperty("user.home"));
    }
    public ExplorerScreen(String path) {
        super(Component.translatable("minedows.explorer"));
        this.path = path;
        history.add(path);
    }
    int yForFolder = 24;

    public ArrayList<AbstractWidget> widgetss = new ArrayList<>();
    private ConfigureScrolWidget scroller;
    public void changePath(String path){
        this.path = path;
        if(!history.getLast().equals(path)) {
            history.add(path);
            rebuildWidgets();
        }
    }
    public EditBox editBox;
    public ArrayList<String> history = new ArrayList<>();
    @Override
    protected void init() {
        super.init();
        //
        addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.browser.back"), (s) -> {
            back();
        }).setSprite(WinColors.getLightIcon("textures/browser/back")).setSize(20, 20).setPosition(2, 1).setStyle(Windows.minedowsStyle).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.browser.forward"), (s) -> {
            forward();
        }).setSprite(WinColors.getLightIcon("textures/browser/forward")).setSize(20, 20).setPosition(22, 1).setStyle(Windows.minedowsStyle).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.browser.home"), (s) -> {
            changePath(System.getProperty("user.home"));
        }).setSprite(WinColors.getLightIcon("textures/browser/home")).setSize(20, 20).setPosition(46, 1).setStyle(Windows.minedowsStyle).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.browser.reload"), (s) -> {
            rebuildWidgets();
        }).setSprite(WinColors.getLightIcon("textures/browser/reload")).setSize(20, 20).setPosition(70, 1).setStyle(Windows.minedowsStyle).build());
        editBox = new ru.kelcu.windows.screens.components.alinlib.EditBox(new EditBoxBuilder().setValue(path).setSize(width-96, 20).setPosition(94, 1));
        addRenderableWidget(editBox);
        //
        initLabels();
        widgetss = new ArrayList<>();
        int x = 135;
        int xD = 135;
        int y = 5+yForFolder;
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
        this.scroller = addRenderableWidget(new ConfigureScrolWidget(width-4, yForFolder+1, 3, this.height-2-yForFolder, Component.empty(), scroller -> {
            scroller.innerHeight = 4;
            int currentPos = 0;
            int lastHeight = 0;
            for (AbstractWidget widget : widgetss) {
                if (widget.visible) {
                    widget.setY(yForFolder+5 + (int) (scroller.innerHeight - scroller.scrollAmount()));
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
    public void back(){
        if(history.isEmpty() || history.size() == 1) {
            SoundUtils.error();
            return;
        }
        history.addFirst(history.getLast());
        history.removeLast();
        path = history.getLast();
        rebuildWidgets();
    }
    public void forward(){
        if(history.isEmpty() || history.size() == 1) {
            SoundUtils.error();
            return;
        }
        history.addLast(history.getFirst());
        history.removeFirst();
        path = history.getLast();
        rebuildWidgets();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (editBox.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                changePath(editBox.getValue());
                setFocused(false);
                setFocused(null);
            }
            return editBox.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void initLabels() {
        try {
            widgets = new ArrayList<>();
            File file = new File(path);
            if(file.exists()){
                if(file.isDirectory()){
                    if(file.listFiles() == null) {
                        Util.getPlatform().openPath(file.toPath());
                        minecraft.setScreen(null);
                    } else {
                        ArrayList<File> files = new ArrayList<>();
                        for(File f : file.listFiles()) if(f.isDirectory()) files.add(f);
                        for(File f : file.listFiles()) if(!f.isDirectory()) files.add(f);
                        for(File f : files){
                            if(f.getName().startsWith(".") && !Windows.config.getBoolean("EXPLORER.VIEW_HIDDEN_FILES", false)) continue;
                            if(f.isDirectory()) widgets.add(new LabelWidget(-50, 0, 40, new Action(() ->
                                    changePath(f.getAbsolutePath()), Component.literal(f.getName()), GuiUtils.getResourceLocation("windows", String.format("textures/start/icons/%s.png", f.getName().equals("ВК Видео") ? "realms" : f.listFiles() == null || f.listFiles().length == 0 ? "empty_folder" : "folder")))));
                            else {
                                String[] args = f.getName().split("\\.");
                                String type = args.length == 1 ? "" : MediaType.getByExtension(args[args.length-1]) == null ? "" : MediaType.getByExtension(args[args.length-1]).getMIME().split("/")[0];
                                if(f.getName().toLowerCase().endsWith(".exe") || f.getName().toLowerCase().endsWith(".appimage") || f.getName().toLowerCase().endsWith(".app")) type = "exe";
                                switch (type){
                                    case "text" -> widgets.add(new LabelWidget(-50, 0, 40, new Action(Action.Type.OPEN_SCREEN
                                            , Component.literal(f.getName()), GuiUtils.getResourceLocation("windows", "textures/start/icons/txt.png"), new NotepadScreen(f.toPath()))));
                                    case "video", "audio" -> widgets.add(new LabelWidget(-50, 0, 40, new Action(() -> Util.getPlatform().openFile(f)
                                            , Component.literal(f.getName()), GuiUtils.getResourceLocation("windows", "textures/start/icons/video.png"))));
                                    case "image" -> widgets.add(new LabelWidget(-50, 0, 40, new Action(() -> Util.getPlatform().openFile(f)
                                            , Component.literal(f.getName()), GuiUtils.getResourceLocation("windows", "textures/start/icons/image.png"))));
                                    case "exe" -> widgets.add(new LabelWidget(-50, 0, 40, new Action(() -> Util.getPlatform().openFile(f)
                                            , Component.literal(f.getName()), GuiUtils.getResourceLocation("windows", "textures/start/icons/executable.png"))));
                                    default -> widgets.add(new LabelWidget(-50, 0, 40, new Action(() -> Util.getPlatform().openFile(f)
                                            , Component.literal(f.getName()), GuiUtils.getResourceLocation("windows", "textures/start/icons/file.png"))));
                                }
                            }
                        }
                    }
                } else {
                    Util.getPlatform().openFile(file);
                    minecraft.setScreen(null);
                }
            } else minecraft.setScreen(null);
        } catch (Exception ex){
            minecraft.setScreen(new ErrorScreen(Component.literal("Explorer crashes"), Component.literal(ex.getLocalizedMessage().isBlank() ? ex.getMessage() : ex.getLocalizedMessage())));
        }

//        widgets.add(new LabelWidget(-50, 0, 40, new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.control.minecraft"), GuiUtils.getResourceLocation("windows", "textures/start/icons/shutdown.png"), new WindowBuilder().setScreen(new OptionsScreen(null, Minecraft.getInstance().options)))));
    }
    public ArrayList<LabelWidget> widgets = new ArrayList<>();
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        WindowUtils.welcomeToWhiteSpace(guiGraphics, 0, yForFolder, width, height-yForFolder);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/options/background.png"), 1, yForFolder+1, 0, 0, 100, 150, 100, 150);
        int fruik =90;
        int x = 10;
        int y = yForFolder+10;
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
    public Component getTitle() {
        File file = new File(path);
        Component name = file.exists() ? Component.literal(file.getName()) : super.getTitle();
        if(file.getName().equals(System.getProperty("user.name"))) name = Component.translatable("minedows.explorer.user_folder");
        return name;
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
    public boolean mouseClicked(double d, double e, int i) {
        boolean st = true;
        GuiEventListener selected = null;
        for (GuiEventListener guiEventListener : this.children()) {
            if (scroller != null && scroller.widgets.contains(guiEventListener)) {
                if (e >= yForFolder) {
                    if(guiEventListener instanceof AbstractWidget){
                        if(((AbstractWidget) guiEventListener).getX() < d && d < ((AbstractWidget) guiEventListener).getRight() &&
                                ((AbstractWidget) guiEventListener).getY() < e && e < ((AbstractWidget) guiEventListener).getBottom()){
                            if (guiEventListener.mouseClicked(d, e, i)) {
                                st = false;
                                selected = guiEventListener;
                                break;
                            }
                        }
                    } else {
                        if (guiEventListener.mouseClicked(d, e, i)) {
                            st = false;
                            selected = guiEventListener;
                            break;
                        }
                    }
                }
            } else if(e <= yForFolder){
                if (guiEventListener.mouseClicked(d, e, i)) {
                    st = false;
                    selected = guiEventListener;
                    break;
                }
            }
        }

        this.setFocused(selected);
        if (i == 0)
            this.setDragging(true);

        return st;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        guiGraphics.enableScissor(1, yForFolder+1, width-1, height-1);
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
}
