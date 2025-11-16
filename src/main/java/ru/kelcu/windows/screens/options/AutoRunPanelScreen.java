package ru.kelcu.windows.screens.options;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.components.Action;
import ru.kelcu.windows.mods.ModMenuActions;
import ru.kelcu.windows.screens.components.LabelConfWidget;
import ru.kelcu.windows.screens.components.alinlib.ButtonBoolean;
import ru.kelcu.windows.style.NothingStyle;
import ru.kelcu.windows.utils.WindowUtils;
import ru.kelcuprum.alinlib.config.Config;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.components.ConfigureScrolWidget;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;

import java.util.ArrayList;
import java.util.List;

import static ru.kelcu.windows.screens.DesktopScreen.getLabelActions;

public class AutoRunPanelScreen extends Screen {
    public ArrayList<LabelConfWidget> widgets = new ArrayList<>();
    public static NothingStyle nothingStyle = new NothingStyle();
    public AutoRunPanelScreen() {
        super(Component.translatable("minedows.control.auto_run"));
    }
    public ConfigureScrolWidget scroller;
    public void initLabels(){
        widgets = new ArrayList<>();
        for(Action action : getLabelActions()){
            Config config = new Config(Windows.config.getJsonObject("auto_run."+action.title.toString(), new JsonObject()));
                LabelConfWidget label = new LabelConfWidget(-50, 0, 40,
                        action);
                label.setConfig("auto_run."+action.title.toString(), config);
                label.setDefaultValue(false);
                widgets.add(label);
        }
        if(Windows.isModMenuInstalled()){
            ArrayList<ModMenuActions.ModInfo> mods = ModMenuActions.getMods();
            for(ModMenuActions.ModInfo mod : mods){
                Config config = new Config(Windows.config.getJsonObject("auto_run."+mod.id(), new JsonObject()));
                    LabelConfWidget label = new LabelConfWidget(-50, 0, 40,
                            new Action(Action.Type.OPEN_SCREEN, Component.literal(mod.name()), mod.icon(), mod.screen()));
                    label.setConfig("auto_run."+mod.id(), config);
                    label.setDefaultValue(false);
                    widgets.add(label);
            }
        }
    }

    public ArrayList<AbstractWidget> widgetss = new ArrayList<>();

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
        for(LabelConfWidget labelWidget : widgets){
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
        y = 60;
        for(FormattedCharSequence formattedCharSequence : font.split(Component.empty().append(getTitle()).withStyle(Style.EMPTY.withBold(true)), 90)){
            y += font.lineHeight + 3;
        }
        y+=5;
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
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/start/icons/empty.png"), x, y, 0, 0, 35, 35, 35, 35);
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
        for(FormattedCharSequence formattedCharSequence : font.split(Component.translatable(widgets.isEmpty() ? "minedows.control.auto_run.description.empty" : "minedows.control.auto_run.description"), fruik)){
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
//    @Override
//    public boolean mouseClicked(double d, double e, int i) {
//        int size = width-135;
//        int x = 135;
//        boolean st = true;
//        GuiEventListener selected = null;
//        for (GuiEventListener guiEventListener : this.children()) {
//            if (scroller != null && scroller.widgets.contains(guiEventListener)) {
//                for(AbstractWidget widget : scroller.widgets){
//                    if (widget.getX() < d && d < widget.getRight() && widget.getY() < e && e < widget.getBottom()){
//                        if (guiEventListener.mouseClicked(d, e, i)) {
//                            st = false;
//                            selected = guiEventListener;
//                            break;
//                        }
//                    }
//                }
//            } else if (guiEventListener.mouseClicked(d, e, i)) {
//                st = false;
//                selected = guiEventListener;
//                break;
//            }
//        }
//
//        this.setFocused(selected);
//        if (i == 0)
//            this.setDragging(true);

//        return st;
//    }
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
