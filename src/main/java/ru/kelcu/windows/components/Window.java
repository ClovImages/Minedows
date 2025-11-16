package ru.kelcu.windows.components;

import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ru.kelcu.windows.screens.AbstractWindowedScreen;
import ru.kelcuprum.alinlib.gui.GuiUtils;

import java.util.UUID;

import static java.lang.Integer.MAX_VALUE;

public class Window {
    public double x;
    public double y;
    public double width;
    public double height;
    public boolean active;
    public Screen screen;
    public UUID uuid;
    public boolean resizable = true;
    public boolean maximize = false;
    public ResourceLocation icon;
    public Component title;

    public boolean isDragging;
    public boolean isResized;
    public boolean isScreenDragging;
    public boolean pinned = false;

    public int buttons = 0;
    public boolean visible = true;
    public Window(UUID uuid, int x, int y, int width, int height, boolean active, Screen screen){
        this(uuid, x, y, width, height, null, active, 0, true, screen, GuiUtils.getResourceLocation("windows", "textures/start/icons/cmd.png"));
    }
    public Window(UUID uuid, int x, int y, int width, int height, boolean active, boolean visible, Screen screen){
        this(uuid, x, y, width, height, null, active, 0, visible, screen, GuiUtils.getResourceLocation("windows", "textures/start/icons/cmd.png"));
    }
    public Window(UUID uuid, int x, int y, int width, int height, boolean active, int buttons, Screen screen){
        this(uuid, x, y, width, height, null, active, buttons, true, screen, GuiUtils.getResourceLocation("windows", "textures/start/icons/cmd.png"));
    }
    public Window(UUID uuid, int x, int y, int width, int height, Component title, boolean active, int buttons, boolean visible, Screen screen, ResourceLocation icon){
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.active = active;
        this.screen = screen;
        this.buttons = buttons;
        this.visible = visible;
        this.icon = icon;
        this.title = title;
        if(screen instanceof AbstractWindowedScreen){
            this.resizable = ((AbstractWindowedScreen) screen).resizable();
            if(!this.resizable){
                setSize(((AbstractWindowedScreen) screen).width(), ((AbstractWindowedScreen) screen).height());
            }
        }
    }
    public void setResizable(boolean resizable){
        this.resizable = resizable;
    }
    public double lastWidth;
    public double lastHeight;
    public double lastX;
    public double lastY;
    public void changeMax(int maxWidth, int maxHeight){
        if(maximize) {
            setSize(lastWidth, lastHeight);
            setPosition(lastX, lastY);
        }
        else{
            lastHeight = height;
            lastWidth = width;
            lastX = x;
            lastY = y;
            setSize(maxWidth, maxHeight);
            setPosition(0, 0);
        }
        maximize = !maximize;
    }

    public void setDragging(boolean isDragging){
        this.isDragging = isDragging;
    }
    public void setResized(boolean isResized){
        this.isResized = isResized;
    }
    public void setScreenDragging(boolean isScreenDragging){
        this.isScreenDragging = isScreenDragging;
    }
    public boolean isResizable(){
        if(screen instanceof AbstractWindowedScreen){
            return ((AbstractWindowedScreen) screen).resizable();
        }
        return resizable;
    }
    public void changePin(){
        this.pinned = !this.pinned;
    }

    public void setPosition(double x, double y){
        this.x = x; this.y = y;
    }
    public void setSize(double width, double height){
        int minW = 200;
        int minH = 50;
        int maxW = MAX_VALUE;
        int maxH = MAX_VALUE;
        if(screen instanceof AbstractWindowedScreen){
            minW = ((AbstractWindowedScreen) screen).minWidth(); minH = ((AbstractWindowedScreen) screen).minHeight();
            maxW = ((AbstractWindowedScreen) screen).maxWidth(); maxH = ((AbstractWindowedScreen) screen).maxHeight();
        }
        if(width <= minW) width = minW; if(maxW <= width) width = maxW;
        if(height <= minH) height = minH; if(maxH <= height) height = maxH;
        this.width = width; this.height = height;
    }
    public int getButtons(){
        if(screen instanceof AbstractWindowedScreen) return ((AbstractWindowedScreen) screen).getWindowType();
        return buttons;
    }

    public Screen lastScreen = null;

    public void setScreen(Screen screen){
        lastScreen = screen instanceof ReceivingLevelScreen ? null : this.screen;
        this.screen = screen;
    }
}
