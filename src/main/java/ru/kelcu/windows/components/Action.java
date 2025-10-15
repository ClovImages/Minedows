package ru.kelcu.windows.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.kelcu.windows.components.builders.WindowBuilder;
import ru.kelcuprum.alinlib.gui.components.buttons.Button;

import java.util.UUID;

public class Action {
    public Type type;
    public Screen screen;
    @NotNull
    public Component title;
    @NotNull
    public ResourceLocation icon;
    public WindowBuilder windowBuilder;
    public onExecute execute;

    public Action(Type type, Component title, ResourceLocation icon){
        this(type, title, icon, null, null);
    }
    public Action(Type type, Component title, ResourceLocation icon, Screen screen){
        this(type, title, icon, screen, null);
    }
    public Action(Type type, Component title, ResourceLocation icon, WindowBuilder builder){
        this(type, title, icon, null, builder);
    }
    public Action(onExecute onExecute, Component title, ResourceLocation icon) {
        this(Type.EXECUTE_ACTION, title, icon, null, null);
        this.execute = onExecute;
    }
    public Action(Type type, Component title, ResourceLocation icon, Screen screen, WindowBuilder builder){
        this.type = type;
        this.screen = screen;
        this.title = title;
        this.icon = icon;
        this.windowBuilder = builder;
    }

    public Window getWindow(){
        if(windowBuilder != null) return windowBuilder.build();
        else {
            int w = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int h = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            int width = (int) (w*0.75);
            int height = (int) (h*0.75);
            return new Window(UUID.randomUUID(), ((w-width)/2), ((h-height)/2), width, height, true, screen);
        }
    }

    public enum Type {
        OPEN_SCREEN,
        STOP_GAME,
        UNPAUSE_GAME,
        DISCONNECT,
        EXECUTE_ACTION
    }

    public interface onExecute {
        void execute();
    }
}
