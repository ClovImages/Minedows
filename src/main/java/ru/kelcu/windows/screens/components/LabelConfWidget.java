package ru.kelcu.windows.screens.components;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.PauseScreen;
//#if MC >= 12110
import net.minecraft.client.input.MouseButtonEvent;
//#endif
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.glfw.GLFW;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.components.Action;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcu.windows.utils.SoundUtils;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.config.Config;
import ru.kelcuprum.alinlib.gui.GuiUtils;

import java.util.List;

public class LabelConfWidget extends AbstractWidget {
    public Action action;
    public LabelConfWidget(int x, int y, int width, Action action) {
        super(x, y, width, width, action.title);
        yD = y; xD = x;
        this.action = action;
    }

    @Override
    public int getHeight() {
        return (int) (width + (AlinLib.MINECRAFT.font.lineHeight*1.65) +6);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        List<FormattedCharSequence> list = AlinLib.MINECRAFT.font.split(action.title, (int) (width*1.65));
        int yA = getWidth()+3;
        int max /* не мессенжер */ = isHovered() ? 100 : 2;
        int pw = 0;
        for(FormattedCharSequence formattedCharSequence : list){
            if(pw == max){
                pw = 0;
                break;
            }
            yA += (int) (AlinLib.MINECRAFT.font.lineHeight*1.25);
            pw++;
        }
        pw=0;
        if(isHoveredOrFocused()){
            guiGraphics.fill(getX()-3, getY()-3, getX()+getWidth()+3, getY()+yA, 0x3e0000F3);
            guiGraphics.
                    //#if MC < 12110
                    //$$renderOutline
                    //#else
                            submitOutline
                    //#endif
                            (getX()-3, getY()-3, getWidth()+6, yA+3, 0x3e0000F3);
        }
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, action.icon, getX()+6, getY()+6, 0, 0, getWidth()-12, getWidth()-12, getWidth()-12, getWidth()-12);
        // -=-=-=-
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(getX(), getY()+getWidth());
        guiGraphics.pose().scale(0.75f);
        int y = 0;
        int color = Windows.config.getBoolean("LABEL.DARK_TEXT", false) || isBlackDuck ? 0xFF000000 : -1;
        for(FormattedCharSequence formattedCharSequence : list){
            if(pw == max) break;
            guiGraphics.drawString(AlinLib.MINECRAFT.font, formattedCharSequence, (int) ((getWidth()/1.5)-(AlinLib.MINECRAFT.font.width(formattedCharSequence)*0.5)), y, color, false);
            y+= (6+AlinLib.MINECRAFT.font.lineHeight);
            pw++;
        }
        guiGraphics.pose().popMatrix();
        if(config != null){
            if(config.getBoolean("enable", defaultValue))
                guiGraphics.blit(
                        //#if MC >= 12106
                        RenderPipelines.GUI_TEXTURED,
                        //#elseif MC >= 12102
                        //$$ RenderType::guiTextured,
                        //#endif
                        GuiUtils.getResourceLocation("textures/gui/sprites/icon/checkmark.png"), getRight()-14, getY()+5, 0f, 0f, 9, 8, 9, 8);
        }
    }
    long lastClick = 0;
    boolean isDragged = false;
    @Override
    public boolean mouseClicked(
            //#if MC < 12110
            //$$ double d, double e, int i){
            //#else
            MouseButtonEvent mouseButtonEvent, boolean b
    ) {
        double d = mouseButtonEvent.x();
        double e = mouseButtonEvent.y();
        double i = mouseButtonEvent.button();
        //#endif
        if(config != null){
            config.setBoolean("enable", !config.getBoolean("enable", defaultValue));
            Windows.config.setJsonObject(id, config.toJSON());
        }
        if(AlinLib.MINECRAFT.screen instanceof DesktopScreen)
            AlinLib.MINECRAFT.screen.rebuildWidgets();
        lastClick = System.currentTimeMillis();
        return super.mouseClicked(
                //#if MC >= 12110
                mouseButtonEvent, b
                //#else
                //$$ d, e, i
                //#endif
        );
    }

    public boolean isBigDuck = true;
    public boolean isBlackDuck = false;
    public void setBigDuck(boolean isBigDuck){
        this.isBigDuck = isBigDuck;
    }
    public void setBlackDuck(boolean isBlackDuck){
        this.isBlackDuck = isBlackDuck;
    }

    public int xD = 0;
    public int yD = 0;
    public void setDefaultPosition(int xD, int yD){
        this.yD = yD; this.xD = xD;
    }

    @Override
    //#if MC < 12110
    //$$public boolean mouseReleased(double d, double e, int i) {
    //#else
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        int i = mouseButtonEvent.button();
        //#endif
        if(i == 0) isDragged = false;
        return super.mouseReleased(
                //#if MC < 12110
                //$$d, e, i
                //#else
                mouseButtonEvent
                //#endif
        );
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        SoundUtils.click();
    }

    String id = "";
    Config config = null;
    boolean defaultValue = true;
    public void setDefaultValue(Boolean b){
        defaultValue = b;
    }
    public void setConfig(String id, Config config){
        this.id = id; this.config = config;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
