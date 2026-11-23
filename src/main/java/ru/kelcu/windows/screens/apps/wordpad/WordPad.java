package ru.kelcu.windows.screens.apps.wordpad;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import org.jspecify.annotations.Nullable;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

// TODO: СДЕЛАТЬ
public class WordPad extends Screen {
    public WordPad() {
        super(Component.translatable("minedows.wordpad"));
        for(int i = 0; i<100; i++){
            pages.add(null);
        }
    }
    private Player owner;
    private ItemStack book;
    private InteractionHand hand;
    private List<String> pages = Lists.newArrayList();
    public WordPad(final Player owner, final ItemStack book, final InteractionHand hand, final WritableBookContent content){
        this();
        this.owner = owner;
        this.book = book;
        this.hand = hand;
        Stream var10000 = content.getPages(Minecraft.getInstance().isTextFilteringEnabled());
        List var10001 = this.pages;
        Objects.requireNonNull(var10001);
        var10000.forEach(var10001::add);
        int i = 0;
        for(String s : pages){
            if(!s.isBlank()) strings.put(0, s);
            i++;
        }
    }

    HashMap<Integer, String> strings = new HashMap<>();
    HashMap<Integer, MultiLineEditBox> editBoxs = new HashMap<>();
    ArrayList<AbstractWidget> widgets = new ArrayList<>();
    private ConfigureScrolWidget scroller;
    @Override
    protected void init() {
        super.init();
        widgets.clear();
        widgets = new ArrayList<>();
        int x = (width/2) - (122/2);
        int padding = (192-134) / 2;
        int y = 24+padding;
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
        this.scroller = addRenderableWidget(new ConfigureScrolWidget(width-4, 25, 3, this.height-25, Component.empty(), scroller -> {
            scroller.innerHeight = 24+(padding);
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

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, a);

        guiGraphics.enableScissor(0, 24, width, height);
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
        for(int i = 0; i<100; i++){
            MultiLineEditBox box = editBoxs.getOrDefault(i, null);
            if(box != null){
                int x = (width / 2) - (192 / 2);
                int y = box.getY()-((192 - box.getHeight()) / 2);
                graphics.blit(RenderPipelines.GUI_TEXTURED, BookViewScreen.BOOK_LOCATION, x, y, 0.0F, 0.0F, 192, 192, 256, 256);
                graphics.text(font, Component.translatable("book.pageIndicator", i+1, 100), box.getX(), box.getY()-font.lineHeight-3, -16777216, false);
            }
        }
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
