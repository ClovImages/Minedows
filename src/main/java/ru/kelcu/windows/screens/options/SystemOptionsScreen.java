package ru.kelcu.windows.screens.options;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import ru.kelcu.windows.screens.components.LabelWidget;
import ru.kelcu.windows.utils.WindowUtils;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.screens.ConfigScreenBuilder;

import java.util.ArrayList;
import java.util.List;

public class SystemOptionsScreen extends Screen {
    public ConfigScreenBuilder builder;
    public SystemOptionsScreen(ConfigScreenBuilder screenBuilder) {
        super(screenBuilder.title);
        this.builder = screenBuilder;
    }

    public ConfigureScrolWidget scroller;
    @Override
    protected void init() {
        super.init();
        for(AbstractWidget widget : builder.widgets) widget.setWidth(width-140);
        this.scroller = addRenderableWidget(new ConfigureScrolWidget(width-4, 1, 3, this.height-2, Component.empty(), scroller -> {
            scroller.innerHeight = 5;
            for (AbstractWidget widget : builder.widgets) {
                if (widget.visible) {
                    widget.setPosition(130, (int) (scroller.innerHeight - scroller.scrollAmount()));
                    scroller.innerHeight += (widget.getHeight() + 5);
                } else widget.setY(-widget.getHeight());
            }
            scroller.innerHeight-=8;
        }));
        addRenderableWidget(scroller);
        addRenderableWidgets$scroller(scroller, builder.widgets);
    }

    protected void addRenderableWidgets$scroller(ConfigureScrolWidget scroller, @NotNull List<AbstractWidget> widgets){
        scroller.addWidgets(widgets);
        for(AbstractWidget widget : widgets) addWidget(widget);
    }

    @Override
    public void tick() {
        super.tick();
        if(scroller != null) scroller.onScroll.accept(scroller);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        boolean st = true;
        GuiEventListener selected = null;
        for (GuiEventListener guiEventListener : this.children()) {
            if (scroller != null && scroller.widgets.contains(guiEventListener)) {
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
        if (i == 0) {
            this.setDragging(true);
        }

        return st;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean scr = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            if (!scr && scroller != null) {
                scr = scroller.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            }
        return scr;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        guiGraphics.enableScissor(1, 1, width-1, height-1);
        if (scroller != null) for (AbstractWidget widget : scroller.widgets) widget.render(guiGraphics, i, j, f);
        guiGraphics.disableScissor();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        super.renderBackground(guiGraphics, i, j, f);
        WindowUtils.welcomeToWhiteSpace(guiGraphics, 0, 0, width, height);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, builder.textureIcon == null ? GuiUtils.getResourceLocation("windows", "textures/options/background.png") : builder.textureIcon, 1, 1, 0, 0, 100, 150, 100, 150);
        int fruik =90;
        int x = 10;
        int y = 10;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/start/icons/options.png"), x, y, 0, 0, 35, 35, 35, 35);
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
        Component description = builder.description != null ? builder.description : Component.translatable(builder.widgets.isEmpty() ? "minedows.control.description.empty" : "minedows.control.description");
        for(FormattedCharSequence formattedCharSequence : font.split(description, fruik)){
            guiGraphics.drawString(font, formattedCharSequence, x, y, 0xFF000000, false);
            y += font.lineHeight + 3;
        }
    }
}
