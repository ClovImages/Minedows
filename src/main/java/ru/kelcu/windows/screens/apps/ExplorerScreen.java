package ru.kelcu.windows.screens.apps;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.client.gui.screens.Screen;
//#if MC >= 12110
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//#endif
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.components.Action;
import ru.kelcu.windows.screens.components.LabelWidget;
import ru.kelcu.windows.screens.options.ControlPanelScreen;
import ru.kelcu.windows.utils.MediaType;
import ru.kelcu.windows.utils.SoundUtils;
import ru.kelcu.windows.utils.WinColors;
import ru.kelcu.windows.utils.WindowUtils;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.editbox.EditBox;
import ru.kelcuprum.alinlib.gui.components.text.CategoryBox;
import ru.kelcuprum.alinlib.gui.components.text.TextBox;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

// TODO: Переделать дизайн
public class ExplorerScreen extends Screen {
    public String path;
    public ExplorerScreen(){
        this(Windows.config.getString("EXPLORER.DEFAULT_PAGE", System.getProperty("user.home")));
    }
    public ExplorerScreen(String path) {
        super(Component.translatable("minedows.explorer"));
        this.path = path;
        history.add(path);
    }
    int yForFolder = 24;

    public ArrayList<AbstractWidget> widgetss = new ArrayList<>();
    private ConfigureScrolWidget scroller;

    public ArrayList<AbstractWidget> panel_widgetss = new ArrayList<>();
    private ConfigureScrolWidget panel_scroller;
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
            changePath(Windows.config.getString("EXPLORER.DEFAULT_PAGE", System.getProperty("user.home")));
        }).setSprite(WinColors.getLightIcon("textures/browser/home")).setSize(20, 20).setPosition(46, 1).setStyle(Windows.minedowsStyle).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.browser.reload"), (s) -> {
            rebuildWidgets();
        }).setSprite(WinColors.getLightIcon("textures/browser/reload")).setSize(20, 20).setPosition(70, 1).setStyle(Windows.minedowsStyle).build());
        editBox = new ru.kelcu.windows.screens.components.alinlib.EditBox(new EditBoxBuilder().setValue(path).setSize(width-96, 20).setPosition(94, 1));
        addRenderableWidget(editBox);
        //
        initLabels();
        widgetss = new ArrayList<>();
        int x = 195;
        int xD = 195;
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
        panelInit();
    }
    XPCategoryBox other;
    XPCategoryBox drives;

    public void panelInit(){
        int x = 11;
            panel_widgetss = new ArrayList<>();
            if(other == null) {
                other = new XPCategoryBox(x, 5 + yForFolder, 160, 20, true, Component.translatable("minedows.explorer.other_places"));
                other.addValue(new ButtonBuilder(Component.translatable("minedows.explorer.user_folder"), (b) -> changePath(Windows.config.getString("EXPLORER.DEFAULT_PAGE", System.getProperty("user.home")))).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/folder.png"))
                        .setCentered(false)
                        .setStyle(ControlPanelScreen.nothingStyle)
                        .setSize(150, 16));
                other.addValue(new ButtonBuilder(Component.translatable("minedows.explorer.this_computer"), (b) -> changePath("")).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/computer_gear.png"))
                        .setCentered(false)
                        .setStyle(ControlPanelScreen.nothingStyle)
                        .setSize(150, 16));
            }
            panel_widgetss.add(other);
            panel_widgetss.addAll(other.values);
            if(drives == null){
                drives = new XPCategoryBox(x, 5 + yForFolder, 160, 20, false, Component.translatable("minedows.explorer.drives"));
                for (File f : File.listRoots()) {
                    drives.addValue(new ButtonBuilder(Component.literal(fsv.getSystemDisplayName(f)), (b) -> changePath(f.toPath().toString())).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/drive/hard_disk_drive_cool.png"))
                            .setCentered(false)
                            .setStyle(ControlPanelScreen.nothingStyle)
                            .setSize(150, 16));
                }
            }
            panel_widgetss.add(drives);
            panel_widgetss.addAll(drives.values);
            this.panel_scroller = new ConfigureScrolWidget(177, yForFolder + 1, 3, this.height - 2 - yForFolder, Component.empty(), scroller -> {
                scroller.innerHeight = 4;
                int lastHeight = 0;
                for (AbstractWidget widget : panel_widgetss) {
                    if (widget.visible) {
                        boolean onCategory = false;
                        if (other.values.contains(widget) || drives.values.contains(widget)) {
                            onCategory = true;
                            widget.setX(other.getX() + 5);
                            widget.setY(yForFolder + (int) (scroller.innerHeight - scroller.scrollAmount()));
                            if((other.values.contains(widget) && other.values.getLast() == widget) || (drives.values.contains(widget) && drives.values.getLast() == widget)) onCategory = false;
                        } else {
                            widget.setY(yForFolder + 5 + (int) (scroller.innerHeight - scroller.scrollAmount()));
                        }
                        lastHeight = widget.getHeight();
                        scroller.innerHeight += (lastHeight + (onCategory ? 2 : 10));
                    } else widget.setY(-widget.getHeight());
                }
                scroller.innerHeight -= 8;
            });
        addWidgetsToScroller(panel_scroller);
        addWidgetsToScroller(panel_widgetss, panel_scroller);
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
    //#if MC < 12110
    //$$public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    //#else
    public boolean keyPressed(KeyEvent keyEvent) {
        int keyCode = keyEvent.key();
        int scanCode = keyEvent.keycode();
        int modifiers = keyEvent.modifiers();
        //#endif
        if (editBox.isFocused()) {
            if (keyEvent.isSelection()) {
                changePath(editBox.getValue());
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
        return super.keyPressed(
                //#if MC < 12110
                //$$keyCode, scanCode, modifiers
                //#else
                keyEvent
                //#endif
        );
    }

    private FileSystemView fsv = FileSystemView.getFileSystemView();
    public void initLabels() {
        try {
            widgets = new ArrayList<>();
            File file = new File(path);
            if(path.isBlank()){

                File[] roots = File.listRoots();
                for(File f : roots){
                    String icon = fsv.isFloppyDrive(f) ? "removable_disk_drive" : fsv.isDrive(f) ? "hard_disk_drive_cool" : "cd_drive_purple";
                    widgets.add(new LabelWidget(-50, 0, 40, new Action(() ->
                            changePath(f.getAbsolutePath()), Component.literal(fsv.getSystemDisplayName(f)), GuiUtils.getResourceLocation("windows", String.format("textures/start/icons/drive/%s.png", icon)))));
                }
            } else if(file.exists()){
                if(file.isDirectory()){
                    if(file.listFiles() == null) {
                        Blaze3D.openPath(file.toPath());
                        minecraft.gui.setScreen(null);
                    } else {
                        ArrayList<File> files = new ArrayList<>();
                        for(File f : file.listFiles()) if(f.isDirectory() && !f.isHidden() && f.canRead()) files.add(f);
                        for(File f : file.listFiles()) if(!f.isDirectory() && !f.isHidden() && f.canRead()) files.add(f);
                        for(File f : files){
                            if(f.getName().startsWith(".") && !Windows.config.getBoolean("EXPLORER.VIEW_HIDDEN_FILES", false)) continue;
                            if(f.isDirectory()) widgets.add(new LabelWidget(-50, 0, 40, new Action(() ->
                                    changePath(f.getAbsolutePath()), Component.literal(fsv.getSystemDisplayName(f)), GuiUtils.getResourceLocation("windows", String.format("textures/start/icons/%s.png", f.getName().equals("ВК Видео") ? "realms" : f.listFiles() == null || f.listFiles().length == 0 ? "empty_folder" : "folder")))));
                            else {
                                String[] args = f.getName().split("\\.");
                                String type = args.length == 1 ? "" : MediaType.getByExtension(args[args.length-1]) == null ? "" : MediaType.getByExtension(args[args.length-1]).getMIME().split("/")[0];
                                if(f.getName().toLowerCase().endsWith(".exe") || f.getName().toLowerCase().endsWith(".appimage") || f.getName().toLowerCase().endsWith(".app")) type = "exe";
                                else if(f.getName().toLowerCase().endsWith(".lnk")) type = "link";
                                switch (type){
                                    case "text" -> widgets.add(new LabelWidget(-50, 0, 40, new Action(Action.Type.OPEN_SCREEN
                                            , Component.literal(fsv.getSystemDisplayName(f)), GuiUtils.getResourceLocation("windows", "textures/start/icons/txt.png"), new NotepadScreen(f.toPath()))));
                                    case "video", "audio" -> widgets.add(new LabelWidget(-50, 0, 40, new Action(() -> Blaze3D.openPath(f.toPath())
                                            , Component.literal(fsv.getSystemDisplayName(f)), GuiUtils.getResourceLocation("windows", "textures/start/icons/video.png"))));
                                    case "image" -> widgets.add(new LabelWidget(-50, 0, 40, new Action(() -> Blaze3D.openPath(f.toPath())
                                            , Component.literal(fsv.getSystemDisplayName(f)), GuiUtils.getResourceLocation("windows", "textures/start/icons/image.png"))));
                                    case "exe" -> widgets.add(new LabelWidget(-50, 0, 40, new Action(() -> Blaze3D.openPath(f.toPath())
                                            , Component.literal(fsv.getSystemDisplayName(f)), GuiUtils.getResourceLocation("windows", "textures/start/icons/executable.png"))));
                                    case "link" -> {
                                        String n = fsv.getSystemDisplayName(f);
                                        if(n.endsWith(".lnk")) n = n.substring(0, n.length()-4);
                                        widgets.add(new LabelWidget(-50, 0, 40, new Action(() -> Blaze3D.openPath(f.toPath())
                                            , Component.literal(n), getIcon(f))));
                                    }
                                    default -> widgets.add(new LabelWidget(-50, 0, 40, new Action(() -> Blaze3D.openPath(f.toPath())
                                            , Component.literal(fsv.getSystemDisplayName(f)), GuiUtils.getResourceLocation("windows", "textures/start/icons/file.png"))));
                                }
                            }
                        }
                    }
                } else {
                    Blaze3D.openPath(file.toPath());
                    minecraft.gui.setScreen(null);
                }
            } else minecraft.gui.setScreen(null);
        } catch (Exception ex){
            minecraft.gui.setScreen(new ErrorScreen(Component.literal("Explorer crashes"), Component.literal(ex.getLocalizedMessage().isBlank() ? ex.getMessage() : ex.getLocalizedMessage())));
        }
    }

    public ArrayList<LabelWidget> widgets = new ArrayList<>();
    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        WindowUtils.welcomeToWhiteSpace(guiGraphics, 0, yForFolder, width, height-yForFolder);
        for(int i = yForFolder; i<height-2; i++){
            guiGraphics.fill(1, 1+i, 180, 2+i, GuiUtils.interpolate(0xFF7ca3e8, 0xFF677eda, i/(float) (height-2)));
        }
    }

    @Override
    public Component getTitle() {
        if(path.isBlank()){
            return Component.translatable("minedows.explorer.this_computer");
        }
        File file = new File(path);
        String fname = fsv.getSystemDisplayName(file);
        Component name = file.exists() ? Component.literal(fname) : super.getTitle();
        if(fname.equals(System.getProperty("user.name"))) name = Component.translatable("minedows.explorer.user_folder");
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
    public boolean mouseClicked(
            //#if MC < 12110
            //$$double d, double e, int i) {
            //#else
            MouseButtonEvent mouseButtonEvent, boolean b
    ) {
        double d = mouseButtonEvent.x();
        double e = mouseButtonEvent.y();
        int i = mouseButtonEvent.button();
        //#endif
        boolean st = true;
        GuiEventListener selected = null;
        for (GuiEventListener guiEventListener : this.children()) {
            if (scroller != null && scroller.widgets.contains(guiEventListener)) {
                if (e >= yForFolder) {
                    if(guiEventListener instanceof AbstractWidget){
                        if(((AbstractWidget) guiEventListener).getX() < d && d < ((AbstractWidget) guiEventListener).getRight() &&
                                ((AbstractWidget) guiEventListener).getY() < e && e < ((AbstractWidget) guiEventListener).getBottom()){
                            if (guiEventListener.mouseClicked(
                                    //#if MC < 12110
                                    //$$d, e, i
                                    //#else
                                    mouseButtonEvent, b
                                    //#endif
                            )) {
                                st = false;
                                selected = guiEventListener;
                                break;
                            }
                        }
                    } else {
                        if (guiEventListener.mouseClicked(
                                //#if MC < 12110
                                //$$d, e, i
                                //#else
                                mouseButtonEvent, b
                                //#endif
                        )) {
                            st = false;
                            selected = guiEventListener;
                            break;
                        }
                    }
                }
            } else if (panel_scroller != null && panel_scroller.widgets.contains(guiEventListener)) {
                if (e >= yForFolder) {
                    if(guiEventListener instanceof AbstractWidget){
                        if(((AbstractWidget) guiEventListener).getX() < d && d < ((AbstractWidget) guiEventListener).getRight() &&
                                ((AbstractWidget) guiEventListener).getY() < e && e < ((AbstractWidget) guiEventListener).getBottom()){
                            if (guiEventListener.mouseClicked(
                                    //#if MC < 12110
                                    //$$d, e, i
                                    //#else
                                    mouseButtonEvent, b
                                    //#endif
                            )) {
                                st = false;
                                selected = guiEventListener;
                                break;
                            }
                        }
                    } else {
                        if (guiEventListener.mouseClicked(
                                //#if MC < 12110
                                //$$d, e, i
                                //#else
                                mouseButtonEvent, b
                                //#endif
                        )) {
                            st = false;
                            selected = guiEventListener;
                            break;
                        }
                    }
                }
            } else if(e <= yForFolder){
                if (guiEventListener.mouseClicked(
                        //#if MC < 12110
                        //$$d, e, i
                        //#else
                        mouseButtonEvent, b
                        //#endif
                )) {
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
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        super.extractRenderState(guiGraphics, i, j, f);
        guiGraphics.enableScissor(181, yForFolder+1, width-1, height-1);
        if (scroller != null) for (AbstractWidget widget : scroller.widgets) widget.extractRenderState(guiGraphics, i, j, f);
        guiGraphics.disableScissor();

        guiGraphics.enableScissor(1, yForFolder+1, 181, height-1);
        if (panel_scroller != null) for (AbstractWidget widget : panel_scroller.widgets) widget.extractRenderState(guiGraphics, i, j, f);
        guiGraphics.disableScissor();
    }
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean scr = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (!scr && scroller != null && mouseX > 150) scr = scroller.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (!scr && panel_scroller != null && mouseX < 150) scr = panel_scroller.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        return scr;
    }
    public int lastDrives = File.listRoots().length;
    @Override
    public void tick() {
        if (scroller != null) scroller.onScroll.accept(scroller);
        if (panel_scroller != null) panel_scroller.onScroll.accept(panel_scroller);
        if(lastDrives != File.listRoots().length){
            lastDrives = File.listRoots().length;
            rebuildWidgets();
        }
        super.tick();
    }

    // -=-=-=-

    public static HashMap<File, Identifier> resourceLocationMap = new HashMap<>();
    public static HashMap<File, Boolean> urls = new HashMap<>();
    public static Identifier getIcon(File file){
        if (resourceLocationMap.containsKey(file)) return resourceLocationMap.get(file);
        else {
            if (!urls.getOrDefault(file, false)) {
                urls.put(file, true);
                Identifier identifier = GuiUtils.getResourceLocation("clovrer", String.format("file_%s_%s", Math.random(), System.currentTimeMillis()));
                resourceLocationMap.put(file, identifier);
                new Thread(() -> {
                    FileSystemView fsv = FileSystemView.getFileSystemView();
                    Icon icon = fsv.getSystemIcon(file, 256, 256);
                    int width = icon.getIconWidth();
                    int height = icon.getIconHeight();

                    // TYPE_INT_ARGB preserves transparency
                    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = image.createGraphics();
                    icon.paintIcon(null, g2d, 0, 0);
                    g2d.dispose();
                    NativeImage ni = new NativeImage(width, height, true);
                    for(int x = 0; x<width; x++){
                        for(int y=0; y<height;y++){
                            ni.setPixel(x, y, image.getRGB(x, y));
                        }
                    }
                    AtomicReference<DynamicTexture> texture = new AtomicReference<>();
                    Minecraft.getInstance().execute(() -> {
                        texture.set(new DynamicTexture(identifier::toString, ni));
                        Minecraft.getInstance().getTextureManager().register(identifier, texture.get());
                    });
                }).start();
                return identifier;
            }
            return GuiUtils.getResourceLocation("windows", "textures/start/icons/file.png");
        }
    }
    public static class XPCategoryBox extends CategoryBox {

        public XPCategoryBox(int x, int y, int width, int height, boolean state, Component label) {
            super(x, y, width, height, state, label);
        }


        public void extractWidgetRenderState(GuiGraphicsExtractor GuiGraphicsExtractor, int i, int j, float f) {
            if (this.visible) {
                GuiGraphicsExtractor.fill(getX(), getY(), getRight(), getY()+getHeight(), 0xFFFFFFFF);
                for(int z = 0; z<(getWidth()/2);z++){
                    GuiGraphicsExtractor.fill(getX()+(getWidth()/2)+z, getY(), getX()+(getWidth()/2)+z+1, getY()+getHeight(), GuiUtils.interpolate(0xFFFFFFFF, 0xFFc8d4f7, (z/(float)(getWidth()/2))));
                }
                int y = this.getY() + (this.getHeight() - 8) / 2;
                GuiGraphicsExtractor.text(Minecraft.getInstance().font, this.getState() ? "▼" : "▶", this.getX() + this.getWidth() - 5 - Minecraft.getInstance().font.width(this.getState() ? "▼" : "▶"), y, 0xFF1c5ec7, false);
                if(width-15 - Minecraft.getInstance().font.width(this.getState() ? "▼" : "▶") > Minecraft.getInstance().font.width(getName()))
                    GuiGraphicsExtractor.text(Minecraft.getInstance().font, this.getName(), this.getX() + 5, y, 0xFF1c5ec7, false);
                else TextBox.renderScrollingString(GuiGraphicsExtractor, Minecraft.getInstance().font, this.getName(), this.getX() + 5, this.getY(), this.getX() + this.getWidth() - 10 - Minecraft.getInstance().font.width(this.getState() ? "▼" : "▶"), this.getY() + this.getHeight(), 0xFF1c5ec7, false);
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
