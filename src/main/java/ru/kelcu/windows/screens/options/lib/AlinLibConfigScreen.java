package ru.kelcu.windows.screens.options.lib;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.components.Description;
import ru.kelcuprum.alinlib.gui.components.Resetable;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.text.CategoryBox;
import ru.kelcuprum.alinlib.gui.components.text.DescriptionBox;
import ru.kelcuprum.alinlib.gui.screens.AbstractConfigScreen;
import ru.kelcuprum.alinlib.gui.screens.ConfigScreenBuilder;
import ru.kelcuprum.alinlib.gui.screens.ConfirmScreen;
import ru.kelcuprum.alinlib.gui.toast.ToastBuilder;

import static ru.kelcuprum.alinlib.gui.Icons.EXIT;
import static ru.kelcuprum.alinlib.gui.Icons.RESET;

public class AlinLibConfigScreen extends AbstractConfigScreen {
    public AlinLibConfigScreen(ConfigScreenBuilder builder) {
        super(builder);
    }

    @Override
    public Component getTitle() {
        if(builder.getCategoryTitle() != null && !builder.getCategoryTitle().equals(Component.empty())){
            return Component.empty().append(super.getTitle()).append(" > ").append(builder.getCategoryTitle());
        } else return super.getTitle();
    }



    @Override
    protected void init() {
        initPanelButtons();
        initCategory();
        super.init();
    }
    int yo = 5;
    public void initPanelButtons() {
        // -=-=-=-=-=-=-=-
//        titleW = addRenderableWidget(new TextBuilder(this.builder.title).setPosition(5 + (builder.itemIcon == null && builder.textureIcon == null ? 0 : 25), 5).setSize(this.builder.panelSize - 10 - (builder.itemIcon == null && builder.textureIcon == null ? 0 : 25), 20).build());
        // -=-=-=-=-=-=-=-
        this.descriptionBox = new DescriptionBox(10, 5, this.builder.panelSize - 20, height - 70, Component.empty());
        this.descriptionBox.visible = false;
        addRenderableWidget(this.descriptionBox);
        // -=-=-=-=-=-=-=-
        // Exit Buttons
        // 85 before reset button
        int heigthScroller = 5;
        for (AbstractWidget widget : builder.panelWidgets) {
            widget.setX(5);
            widget.setWidth(widget.getWidth()+10);
            heigthScroller+=(widget.getHeight()+5);
        }
        this.scroller_panel = addRenderableWidget(new ConfigureScrolWidget(builder.panelSize-9, 5, 3, this.height - 10, Component.empty(), scroller -> {
            scroller.innerHeight = 5;
            for (AbstractWidget widget : builder.panelWidgets) {
                if (widget.visible) {
                    widget.setY((int) (scroller.innerHeight - scroller.scrollAmount()));
                    scroller.innerHeight += (widget.getHeight() + 5);
                } else widget.setY(-widget.getHeight());
            }
            scroller.innerHeight-=8;
        }));
        yo = Math.min(heigthScroller, height-5);
        back = addRenderableWidget(new ButtonBuilder(CommonComponents.GUI_BACK).setOnPress((OnPress) -> {
            assert this.minecraft != null;
            this.minecraft.setScreen(builder.parent);
        }).setIcon(AlinLib.isAprilFool() ? EXIT : null).setPosition(5, yo+5).setSize(this.builder.panelSize - (builder.isResetable ? 35 : 10), 20).build());

        if(builder.isResetable) reset = addRenderableWidget(new ButtonBuilder(Component.translatable("alinlib.component.reset")).setOnPress((OnPress) -> {
            this.minecraft.setScreen(new ConfirmScreen(this, RESET, Component.translatable("alinlib.title.reset"), Component.translatable("alinlib.title.reset.description"), (bl) -> {
                if(bl){
                    for (AbstractWidget widget : builder.widgets)
                        if (widget instanceof Resetable) ((Resetable) widget).resetValue();
                    assert this.minecraft != null;
                    new ToastBuilder()
                            .setTitle(title)
                            .setMessage(Component.translatable("alinlib.component.reset.toast"))
                            .setIcon(RESET)
                            .setIsWhiteIcon(true)
                            .buildAndShow();
                    AlinLib.LOG.log(Component.translatable("alinlib.component.reset.toast"));
                }
            }));
        }).setSprite(RESET).setSize(20, 20).setPosition(this.builder.panelSize - 25, yo+5).build());

        addRenderableWidgets$scroller(scroller_panel, builder.panelWidgets);
    }

    public int yc = 0;
    public void initCategory() {
//        if(builder.categoryTitle != null && !builder.categoryTitle.getString().isBlank()) addRenderableWidget(new TextBuilder(builder.categoryTitle).setPosition(getX(), 10).setWidth(getContentWidth()).build());
        int y = 5;
        yc = y;
        int width = getContentWidth();
        for (AbstractWidget widget : builder.widgets) {
            widget.setWidth(width);
            widget.setX(getX());
        }
        int heigthScroller = y;
        for (AbstractWidget widget : builder.widgets) {
            widget.setY(heigthScroller);
            if(widget.visible) heigthScroller+=(widget.getHeight()+5);
        }
        this.scroller = addRenderableWidget(new ConfigureScrolWidget(getX()+getContentWidth()+1, y, 3, this.height-10-y, Component.empty(), scroller -> {
            scroller.innerHeight = 0;
            int heigthScroller1 = yc;
            boolean descriptionEnable = false;
            CategoryBox lastCategory = null;
            for (AbstractWidget widget : builder.widgets) {
                if (widget.visible) {
                    if (widget instanceof Description) {
                        if (widget.isHoveredOrFocused() && ((Description) widget).getDescription() != null && this.descriptionBox != null) {
                            descriptionEnable = true;
                            this.descriptionBox.setDescription(((Description) widget).getDescription());
                        }
                    }
                    if (widget instanceof CategoryBox) {
                        if (lastCategory != widget && ((CategoryBox) widget).getState())
                            lastCategory = (CategoryBox) widget;
                    }
                    if (lastCategory != null && !(widget instanceof CategoryBox)) {
                        if (!lastCategory.values.contains(widget)) {
                            scroller.innerHeight += 6;
                            lastCategory.setRenderLine(true);
                            lastCategory = null;
                        }
                    }
                    widget.setPosition(getX(), y + (int) (scroller.innerHeight - scroller.scrollAmount()));
                    heigthScroller1+=(widget.getHeight()+5);
                    scroller.innerHeight += (widget.getHeight() + 5);
                } else widget.setY(-widget.getHeight());
            }
            scroller.innerHeight -= 8;
            yc = Math.min(height-5, heigthScroller1);
            if (this.lastCheck != descriptionEnable) {
                lastCheck = descriptionEnable;
                for (AbstractWidget widget : builder.panelWidgets) {
                    widget.visible = !lastCheck;
                }
                this.descriptionBox.visible = lastCheck;
            }
        }));
        yc = Math.min(height-5, heigthScroller);
        addRenderableWidgets$scroller(scroller, builder.widgets);
    }

    public int getContentWidth(){
        return width-15-this.builder.panelSize;
    }
    public int getX(){
        return this.builder.panelSize+5;
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        boolean st = true;
        GuiEventListener selected = null;
        for (GuiEventListener guiEventListener : this.children()) {
            if (scroller_panel != null && scroller_panel.widgets.contains(guiEventListener)) {
                if ((d >= 10 && d <= builder.panelSize-10) && (e >= 5 && e <= height-5)) {
                    if (guiEventListener.mouseClicked(d, e, i)) {
                        st = false;
                        selected = guiEventListener;
                        break;
                    }
                }
            } else if (scroller != null && scroller.widgets.contains(guiEventListener)) {
                if ((d >= builder.panelSize) && (e >= 5 && e <= height-5)) {
                    if (guiEventListener.mouseClicked(d, e, i)) {
                        st = false;
                        selected = guiEventListener;
                        break;
                    }
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

    //#if MC >= 12002
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean scr = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (mouseX <= this.builder.panelSize) {
            if (descriptionBox.visible && (mouseX >= 5 && mouseX <= builder.panelSize - 5) && (mouseY >= 5 && mouseY <= height - 5)) {
                scr = descriptionBox.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            } else if (!scr && scroller_panel != null) {
                scr = scroller_panel.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            }
        } else {
            if (!scr && scroller != null) {
                scr = scroller.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
            }
        }
        return scr;
    }
    //#elseif MC < 12002
    //$$ @Override
    //$$   public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
    //$$      boolean scr = super.mouseScrolled(mouseX, mouseY, scrollY);
    //$$      if(mouseX <= this.builder.panelSize){
    //$$          if(descriptionBox.visible && (mouseX >= 5 && mouseX <= builder.panelSize-5) && (mouseY >= 40 && mouseY <= height - 30)){
    //$$                scr = descriptionBox.mouseScrolled(mouseX, mouseY, scrollY);
    //$$            } else if (!scr && scroller_panel != null) {
    //$$              scr = scroller_panel.mouseScrolled(mouseX, mouseY, scrollY);
    //$$          }
    //$$      } else {
    //$$          if (!scr && scroller != null) {
    //$$              scr = scroller.mouseScrolled(mouseX, mouseY, scrollY);
    //$$          }
    //$$      }
    //$$      return scr;
    //$$  }
    //#endif

    //#if MC >= 12002
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        assert this.minecraft != null;
        super.renderBackground(guiGraphics, i, j, f);
        //#elseif MC < 12002
        //$$  @Override
        //$$  public void renderBackground(GuiGraphics guiGraphics){
        //$$      assert this.minecraft != null;
        //$$      super.renderBackground(guiGraphics);
        //#endif
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        //#if MC < 12002
        //$$ renderBackground(guiGraphics);
        //#endif
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.enableScissor(5, 5, builder.panelSize, yo);
        if (scroller_panel != null) for (AbstractWidget widget : scroller_panel.widgets) widget.render(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.disableScissor();

        guiGraphics.enableScissor(0, 5, width, yc-5);
        if (scroller != null) for (AbstractWidget widget : scroller.widgets) widget.render(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.disableScissor();
    }
}
