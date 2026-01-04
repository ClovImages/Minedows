package ru.kelcu.windows.components;

//#if MC < 12110
//$$import net.minecraft.client.Minecraft;
//#else
import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
//#endif
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.gui.GuiGraphics;
public class Cursor {
    private static boolean isDragging;


    //#if MC >= 12110
    public static CursorType RESIZE_NWSE = CursorType.createStandardCursor(GLFW.GLFW_RESIZE_NWSE_CURSOR, "resize_nwse", CursorType.DEFAULT);
    //#endif

    public static void setDragging(GuiGraphics guiGraphics) {
        if (isDragging) return;
        isDragging = true;
        //#if MC < 12110
        //$$ GLFW.glfwSetCursor(Minecraft.getInstance().getWindow().getWindow(), GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_NWSE_CURSOR));
        //#else
        guiGraphics.requestCursor(RESIZE_NWSE);
        //#endif

    }

    public static void reset(GuiGraphics guiGraphics) {
        if (!isDragging) return;
        isDragging = false;
        //#if MC < 12110
        //$$ GLFW.glfwSetCursor(Minecraft.getInstance().getWindow().getWindow(), GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR));
        //#else
        guiGraphics.requestCursor(CursorTypes.CROSSHAIR);
        //#endif
    }
}
