package ru.kelcu.windows.screens.components;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.PauseScreen;
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

import java.util.List;

public class LabelWidget extends AbstractWidget {
    public Action action;
    public LabelWidget(int x, int y, int width, Action action) {
        super(x, y, width, width, action.title);
        yD = y; xD = x;
        dX = x; dY = y;
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
            guiGraphics.renderOutline(getX()-3, getY()-3, getWidth()+6, yA+3, 0x3e0000F3);
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
    }
    long lastClick = 0;
    boolean isDragged = false;
    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if(System.currentTimeMillis() - lastClick < 300 && !isDragged){
            switch (action.type){
                case STOP_GAME -> Minecraft.getInstance().stop();
                case UNPAUSE_GAME -> Minecraft.getInstance().setScreen(null);
                case DISCONNECT -> PauseScreen.disconnectFromWorld(AlinLib.MINECRAFT, ClientLevel.DEFAULT_QUIT_MESSAGE);
                case OPEN_SCREEN -> {
                    if(AlinLib.MINECRAFT.screen instanceof DesktopScreen)
                        AlinLib.MINECRAFT.screen.rebuildWidgets();
                    DesktopScreen.addWindow(action.getWindow());
                }
                case EXECUTE_ACTION -> action.execute.execute();
            }
            setFocused(false);
            return true;
        }
        lastClick = System.currentTimeMillis();
        return super.mouseClicked(d, e, i);
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

    double dY;
    double dX;

    @Override
    protected void onDrag(double d, double e, double f, double g) {
        if(!isBigDuck || !Windows.config.getBoolean("LABEL.CUSTOM_POS", false)) return;
        isDragged = true;
        dY+=g;dX+=f;
        setPosition((int) dX, (int) dY);
        if (!id.isEmpty() && config != null) {
            config.setNumber("x", getX());
            config.setNumber("y", getY());
            Windows.config.setJsonObject(id, config.toJSON());
        }
        super.onDrag(d, e, f, g);
    }

    @Override
    public void setPosition(int i, int j) {
        if(!isDragged){
            dX=i;
            dY=j;
        }
        super.setPosition(i, j);
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        if(i == 0) isDragged = false;
        return super.mouseReleased(d, e, i);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        SoundUtils.click();
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if(i == GLFW.GLFW_KEY_DELETE && !id.isEmpty() && config != null && Windows.config.getBoolean("LABEL.CUSTOM_POS", false)){
            config = new Config(new JsonObject());
            Windows.config.setJsonObject(id, config.toJSON());
            setPosition(xD, yD);
        }
        return super.keyPressed(i, j, k);
    }

    String id = "";
    Config config = null;
    public void setConfig(String id, Config config){
        this.id = id; this.config = config;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
