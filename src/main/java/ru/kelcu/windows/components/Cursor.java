package ru.kelcu.windows.components;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class Cursor {
    private static boolean isDragging;

    public static void setDragging() {
        if (isDragging) return;
        isDragging = true;
        GLFW.glfwSetCursor(Minecraft.getInstance().getWindow().getWindow(), GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_NWSE_CURSOR));
    }

    public static void reset() {
        if (!isDragging) return;
        isDragging = false;
        GLFW.glfwSetCursor(Minecraft.getInstance().getWindow().getWindow(), GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR));
    }
}
