package ru.kelcu.windows.screens.apps.wordpad;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WritableBookContent;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.sdl.SDLDialog;
import org.lwjgl.sdl.SDL_DialogFileCallbackI;
import org.lwjgl.sdl.SDL_DialogFileFilter;
import org.lwjgl.system.MemoryUtil;
import ru.kelcu.windows.screens.apps.PaintScreen;
import ru.kelcu.windows.utils.WinColors;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.lwjgl.sdl.SDLError.SDL_GetError;

// TODO: СДЕЛАТЬ
public class WordPad extends Screen {
    public WordPad() {
        super(Component.translatable("minedows.wordpad"));
        for(int i = 0; i<100; i++){
            pages.add("");
        }
        colors.put("0", 0xFF000000);
        colors.put("1", 0xFF0000AA);
        colors.put("2", 0xFF00AA00);
        colors.put("3", 0xFF00AAAA);
        colors.put("4", 0xFFAA0000);
        colors.put("5", 0xFFAA00AA);
        colors.put("6", 0xFFFFAA00);
        colors.put("7", 0xFFAAAAAA);
        colors.put("8", 0xFF555555);
        colors.put("9", 0xFF5555FF);
        colors.put("a", 0xFF55FF55);
        colors.put("b", 0xFF55FFFF);
        colors.put("c", 0xFFFF5555);
        colors.put("d", 0xFFFF55FF);
        colors.put("e", 0xFFFFFF55);
        colors.put("f", 0xFFFFFFFF);
    }
    private HashMap<String, Integer> colors = new HashMap<>();
    private Player owner;
    private ItemStack book;
    private InteractionHand hand;
    private List<String> pages = Lists.newArrayList();
    public WordPad(final Player owner, final ItemStack book, final InteractionHand hand, final WritableBookContent content){
        this();
        this.pages = Lists.newArrayList();
        this.owner = owner;
        this.book = book;
        this.hand = hand;
        Stream<String> var10000 = content.getPages(Minecraft.getInstance().isTextFilteringEnabled());
        var10000.forEach(this.pages::add);
        for(int i = 0; i<100; i++){
            if(pages.size()-1 < i) pages.add("");
        }
        int i = 0;
        for(String s : pages){
            if(!s.isBlank()) strings.put(i, s);
            i++;
        }
    }
    public int yForPages = 57;
    HashMap<Integer, String> strings = new HashMap<>();
    HashMap<Integer, MultiLineEditBox> editBoxs = new HashMap<>();
    ArrayList<AbstractWidget> widgets = new ArrayList<>();
    private ConfigureScrolWidget scroller;

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        // =-=-=-=-=-= CONTROLS
        int xb = 5;
        int yb = 5;
        // - File & format
        addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.wordpad.open_file")
        , (b) -> loadFile()).setSprite(Identifier.fromNamespaceAndPath("windows", "textures/wordpad/file.png")).setPosition(xb, yb)
                .setSize(20, 20).build());
        xb+=20;
        addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.wordpad.open_from_game"),
                (boo) -> {
                    boolean b = true;
                    if(minecraft.level != null){
                        ItemStack is = minecraft.player.getItemBySlot(EquipmentSlot.MAINHAND);
                        if(is.is(Items.WRITABLE_BOOK)){
                           minecraft.gui.setScreen(new WordPad(minecraft.player, is, InteractionHand.MAIN_HAND,
                                   is.get(DataComponents.WRITABLE_BOOK_CONTENT)));
                        } else {
                            b = false;
                        }
                    } else b = false;
                    if(!b) new ToastBuilder().setTitle(Component.translatable("minedows.wordpad")).setMessage(Component.translatable("minedows.wordpad.wrong_book")).setType(ToastBuilder.Type.ERROR).buildAndShow();
                }).setSprite(Identifier.fromNamespaceAndPath("windows", "textures/wordpad/folder.png")).setPosition(xb, yb)
                .setSize(20, 20).build());
        xb+=20;
        addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.wordpad.save"),
                (b) -> {
                    if(minecraft.hasControlDown()) saveFile();
                    else saveToBook();
                }).setSprite(Identifier.fromNamespaceAndPath("windows", "textures/wordpad/save.png")).setPosition(xb, yb)
                .setSize(20, 20).build());
        //
        xb+=30;
        addRenderableWidget(new ButtonBuilder(Component.literal("§rR"), (b) -> setColor("r")).setPosition(xb, yb)
                .setSize(20, 20).build());
        xb+=20;
        addRenderableWidget(new ButtonBuilder(Component.literal("§lB"), (b) -> setColor("l")).setPosition(xb, yb)
                .setSize(20, 20).build());
        xb+=20;
        addRenderableWidget(new ButtonBuilder(Component.literal("§mS"), (b) -> setColor("m")).setPosition(xb, yb)
                .setSize(20, 20).build());
        xb+=20;
        addRenderableWidget(new ButtonBuilder(Component.literal("§nU"), (b) -> setColor("n")).setPosition(xb, yb)
                .setSize(20, 20).build());
        xb+=20;
        addRenderableWidget(new ButtonBuilder(Component.literal("§oI"), (b) -> setColor("o")).setPosition(xb, yb)
                .setSize(20, 20).build());
        xb+=20;
        addRenderableWidget(new ButtonBuilder(Component.literal("§kK"), (b) -> setColor("k")).setPosition(xb, yb)
                .setSize(20, 20).build());
        xb+= 30;
        //-=-=-=-=-=-=-=- todo
        int pc = 0;
        int si = 10;
        for(String color : colors.keySet()){
            if(xb+20 > width){
                xb = 5; yb+=20;
            }
            addRenderableWidget(new PaintScreen.ButtonColor(new ButtonBuilder(Component.empty(),
                    (s) -> setColor(color)).setSize(si, si).setPosition(xb, yb), colors.get(color)));
            if(pc == 0) {
                yb+=si;
                pc++;
            }
            else {
                pc = 0;
                xb+=si;
                yb-=si;
            }
        }
        xb = 0; yb+=24;
        yForPages = yb+5;
        // =-=-=-=-=-= PAGES
        widgets.clear();
        widgets = new ArrayList<>();
        int x = (width/2) - (122/2);
        int padding = (192-134) / 2;
        int y = yForPages+padding;
        for(int i = 0; i < 100; i++){
            MultiLineEditBox page = MultiLineEditBox.builder().setShowDecorations(false).setTextColor(-16777216).setCursorColor(-16777216).setShowBackground(false).setTextShadow(false).setX((this.width - 114) / 2 - 8).setY(28).build(this.font, 122, 134, CommonComponents.EMPTY);
            page.setCharacterLimit(1024);
            page.setLineLimit(126 / 9);
            final int p = i;
            page.setValueListener((value) -> {
                pages.set(p, value);
            });
            page.setPosition(x, y);
            if(pages.get(i) != null) page.setValue(pages.get(i));
            editBoxs.put(i, page);
            widgets.add(page);
            y = page.getBottom()+(padding*2)+1;
        }
        this.scroller = addRenderableWidget(new ConfigureScrolWidget(width-4, yForPages+2, 3, this.height-yForPages-3, Component.empty(), scroller -> {
            scroller.innerHeight = yForPages+(padding)+5;
            for (AbstractWidget widget : widgets) {
                if (widget.visible) {
                    widget.setPosition(x, (int) (scroller.innerHeight - scroller.scrollAmount()));
                    scroller.innerHeight += (widget.getHeight() + (padding*2) + 1);
                } else widget.setY(-widget.getHeight());
            }
            scroller.innerHeight-=8;
        }));
        for(AbstractWidget widget : widgets){
            scroller.addWidget(widget);
            addWidget(widget);
        }
    }
    public void setColor(String code){
        if(active != null){
            insertText(active, "§"+code);
            setFocused(active);
        }
    }
    public void insertText(MultiLineEditBox editBox, final String input) {
        if (!input.isEmpty() || editBox.textField.hasSelection()) {
            String text = input;
            MultilineTextField.StringView selected = editBox.textField.getSelected();
            if(selected.beginIndex() != selected.endIndex()){
                text = input + editBox.textField.value().substring(selected.beginIndex(), selected.endIndex()).replace("\n", "\n"+input)+"§r";
            }
            String newValue = (new StringBuilder(editBox.textField.value())).replace(selected.beginIndex(), selected.endIndex(), text).toString();
            if (!editBox.textField.overflowsLineLimit(newValue)) {
                editBox.textField.setValue(newValue);
                editBox.textField.cursor = selected.beginIndex + text.length();
                editBox.textField.selectCursor = editBox.textField.cursor;
                editBox.textField.onValueChange();
            }
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, a);
        guiGraphics.enableScissor(0, yForPages, width, height);
        if (scroller != null) for (AbstractWidget widget : scroller.widgets) widget.extractRenderState(guiGraphics, mouseX, mouseY, a);
        guiGraphics.disableScissor();
    }
    private MultiLineEditBox active = null;
    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        super.setFocused(focused);
        if(focused instanceof MultiLineEditBox) active = (MultiLineEditBox) focused;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int[] colors = WinColors.getHorizontalRuleColors();
        graphics.fill(5, yForPages-2, width-5, yForPages, colors[0]);
        graphics.fill(5, yForPages-2, width-5, yForPages-1, colors[1]);
        graphics.enableScissor(0, yForPages, width, height);
        for(int i = 0; i<100; i++){
            MultiLineEditBox box = editBoxs.getOrDefault(i, null);
            if(box != null){
                int x = (width / 2) - (192 / 2);
                int y = box.getY()-((192 - box.getHeight()) / 2);
                if(height < y || y+192 < 0) continue;
                graphics.blit(RenderPipelines.GUI_TEXTURED, BookViewScreen.BOOK_LOCATION, x, y, 0.0F, 0.0F, 192, 192, 256, 256);
                graphics.text(font, Component.translatable("book.pageIndicator", i+1, 100), box.getX()+3, box.getY()-font.lineHeight-3, -16777216, false);
            }
        }
        graphics.disableScissor();
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



    public void saveFile(){
        File defaultPath = new File(System.getProperty("user.home")).getAbsoluteFile();
        String defaultString = defaultPath.getAbsolutePath();
        if (defaultPath.isDirectory() && !defaultString.endsWith(File.separator)) {
            defaultString += File.separator;
        }
        SDL_DialogFileFilter.Buffer filters = SDL_DialogFileFilter.calloc(1);
        ByteBuffer filterName = MemoryUtil.memASCII(
                Component.translatable("minedows.wordpad.save.filter_description").getString());
        ByteBuffer filterPattern = MemoryUtil.memASCII("book");
        filters.get(0).name(filterName).pattern(filterPattern);

        SDL_DialogFileCallbackI pendingFileDialog = (userdata, filelist, filter) -> {
            try {
                if (filelist == MemoryUtil.NULL) {
                    AlinLib.LOG.error("SDL file dialog failed: {}", SDL_GetError());
                    return;
                }

                List<String> picked = new ArrayList<>();
                for (long p = filelist; p != 0L; p += PointerBuffer.POINTER_SIZE) {
                    long s = MemoryUtil.memGetAddress(p);
                    if (s == MemoryUtil.NULL) break;
                    picked.add(MemoryUtil.memUTF8(s));
                }
                if (picked.isEmpty()) {
                    AlinLib.LOG.log("Dialog cancelled by user");
                    return;
                }

                String pathh = picked.get(0);
                Path path = new File(pathh).toPath();
                Files.createDirectories(path.getParent());
                Files.write(path, toString().getBytes(StandardCharsets.UTF_8));
            } catch(Exception exception) {
                exception.printStackTrace();
            } finally {
                filters.free();
                MemoryUtil.memFree(filterName);
                MemoryUtil.memFree(filterPattern);
            }
        };
        SDLDialog.SDL_ShowSaveFileDialog(pendingFileDialog, 0L, 0L, filters, defaultString);
    }
    public void saveToBook(){

        boolean b = true;
        if(minecraft.level != null){
            ItemStack is = minecraft.player.getItemBySlot(EquipmentSlot.MAINHAND);
            if(is.is(Items.WRITABLE_BOOK)){
                List<String> pp = eraseEmptyTrailingPages(this.pages);
                int slot = minecraft.player.getInventory().getSelectedSlot();
                this.minecraft.getConnection().send(new ServerboundEditBookPacket(slot, pp, Optional.empty()));
                new ToastBuilder().setTitle(Component.translatable("minedows.wordpad")).setMessage(Component.translatable("minedows.wordpad.save.book_done")).buildAndShow();
            } else {
                b = false;
            }
        } else b = false;
        if(!b) new ToastBuilder().setTitle(Component.translatable("minedows.wordpad")).setMessage(Component.translatable("minedows.wordpad.wrong_book")).setType(ToastBuilder.Type.ERROR).buildAndShow();
    }
    private List<String> eraseEmptyTrailingPages(List<String> pages) {
        ListIterator<String> pagesIt = pages.listIterator(pages.size());

        while(pagesIt.hasPrevious() && (pagesIt.previous().isEmpty())) {
            pagesIt.remove();
        }
        return pages;
    }
    public void loadFile(){
        File defaultPath = new File(System.getProperty("user.home")).getAbsoluteFile();
        String defaultString = defaultPath.getAbsolutePath();
        if (defaultPath.isDirectory() && !defaultString.endsWith(File.separator)) {
            defaultString += File.separator;
        }
        SDL_DialogFileFilter.Buffer filters = SDL_DialogFileFilter.calloc(1);
        ByteBuffer filterName = MemoryUtil.memASCII(
                Component.translatable("minedows.wordpad.save.filter_description").getString());
        ByteBuffer filterPattern = MemoryUtil.memASCII("book");
        filters.get(0).name(filterName).pattern(filterPattern);
        AtomicBoolean b = new AtomicBoolean(false);
        AtomicReference<Path> path = new AtomicReference<>();
        SDL_DialogFileCallbackI pendingFileDialog = (userdata, filelist, filter) -> {
            try {
                if (filelist == MemoryUtil.NULL) {
                    AlinLib.LOG.error("SDL file dialog failed: {}", SDL_GetError());
                    b.set(true);
                    return;
                }

                List<String> picked = new ArrayList<>();
                for (long p = filelist; p != 0L; p += PointerBuffer.POINTER_SIZE) {
                    long s = MemoryUtil.memGetAddress(p);
                    if (s == MemoryUtil.NULL) break;
                    picked.add(MemoryUtil.memUTF8(s));
                }
                if (picked.isEmpty()) {
                    AlinLib.LOG.log("Dialog cancelled by user");
                    b.set(true);
                    return;
                }

                String pathh = picked.get(0);
                JsonArray jsonArray = GsonHelper.parseArray(Files.readString(Path.of(pathh)));
                int i = 0;
                for(JsonElement jsonElement : jsonArray){
                    pages.set(i, jsonElement.isJsonNull() ? null : jsonElement.getAsString());
                    i++;
                }
                b.set(true);
            } catch(Exception exception) {
                exception.printStackTrace();
            } finally {
                filters.free();
                MemoryUtil.memFree(filterName);
                MemoryUtil.memFree(filterPattern);
            }
        };
        SDLDialog.SDL_ShowOpenFileDialog(pendingFileDialog, 0L, 0L, filters, defaultString, false);
        while(!b.get());
        rebuildWidgets();
    }

    @Override
    public String toString() {
        JsonArray a = new JsonArray();
        for(String page : pages){
            a.add(page);
        }
        return a.toString();
    }
}
