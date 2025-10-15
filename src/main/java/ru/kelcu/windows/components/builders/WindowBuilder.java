package ru.kelcu.windows.components.builders;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ru.kelcu.windows.components.Window;
import ru.kelcuprum.alinlib.gui.GuiUtils;

import java.util.UUID;

public class WindowBuilder {
    public double x;
    public double y;

    public double width;
    public double height;
    public boolean active = true;
    public boolean visible = true;
    public ResourceLocation icon = GuiUtils.getResourceLocation("windows", "textures/start/icons/cmd.png");
    public Component title;
    public Screen screen;
    public UUID uuid = UUID.randomUUID();

    public boolean resizable = true;
    public boolean maximize = false;
    public int buttons = 0;

    public WindowBuilder(){ this((Component) null);}
    public WindowBuilder(String title){
        this(Component.translatable(title));
    }
    public WindowBuilder(Component title){
        this.title = title;
        int w = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int h = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        width = (int) (w*0.75);
        height = (int) (h*0.75);
        x = Math.max(0,((w-width)/2)); y = Math.max(0,((h-height)/2));
    }

    public WindowBuilder setTitle(String title){
        return setTitle(Component.translatable(title));
    }
    public WindowBuilder setTitle(Component title){
        this.title = title;
        return this;
    }
    public Component getTitle(){
        return this.screen.getTitle().equals(Component.empty()) ? title == null ? Component.literal(String.format("%s.exe", this.screen.getClass().getSimpleName())) : title : this.screen.getTitle();
    }
    protected boolean isCustomPos = false;
    public WindowBuilder setPosition(double x, double y){
        this.x = x; this.y = y;
        isCustomPos=true;
        return this;
    }
    public WindowBuilder setSize(double width, double height){
        this.width = width; this.height = height;
        if(!isCustomPos){
            int w = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int h = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            x = Math.max(0, ((w-width)/2)); y = Math.max(0,((h-height)/2));
        }
        return this;
    }

    public WindowBuilder setActive(boolean active){
        this.active = active;
        return this;
    }

    public boolean getActive(){
        return this.active;
    }

    public WindowBuilder setVisible(boolean visible){
        this.visible = visible;
        if(!visible) this.active = false;
        return this;
    }

    public boolean getVisible(){
        return this.visible;
    }

    public Screen lastScreen = null;
    public WindowBuilder setScreen(Screen screen){
        lastScreen = this.screen;
        this.screen = screen;
        return this;
    }
    public Screen getScreen(){
        return this.screen;
    }
    public Screen getLastScreen(){
        return this.lastScreen;
    }

    public WindowBuilder setButtons(int buttons){
        this.buttons = buttons;
        return this;
    }
    public WindowBuilder setResizable(boolean resizable){
        this.resizable = resizable;
        return this;
    }

    public WindowBuilder setIcon(ResourceLocation icon){
        this.icon = icon;
        return this;
    }

    public Window build(){
        Window window = new Window(uuid, (int) x, (int) y, (int) width, (int) height, title, active, buttons, visible, screen, icon);
        window.resizable = resizable;
        return window;
    }
}
