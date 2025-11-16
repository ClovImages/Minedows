package ru.kelcu.windows.screens;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractWindowedScreen extends Screen {
    protected AbstractWindowedScreen(Component component) {
        super(component);
    }

    public abstract ResourceLocation icon();
    public abstract boolean resizable();
    public abstract int getWindowType();
    public abstract int maxHeight();
    public abstract int minHeight();
    public abstract int minWidth();
    public abstract int maxWidth();
    public abstract int height();
    public abstract int width();
}
