package ru.kelcu.windows.mixin.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.kelcu.windows.components.Window;
import ru.kelcu.windows.screens.DesktopScreen;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin {

    @Shadow
    protected boolean isHovered;

    @Shadow
    public abstract int getX();

    @Shadow
    public abstract int getRight();

    @Shadow
    public abstract int getY();

    @Shadow
    public abstract int getBottom();

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;containsPointInScissor(II)Z"))
    public boolean render(GuiGraphics instance, int i, int j) {
        boolean def = instance.containsPointInScissor(i, j);
        if(DesktopScreen.currentRenderedWindow != null){
            Window window = DesktopScreen.currentRenderedWindow;
            if(instance.containsPointInScissor((int) (i+(window.x+3)), (int) (j+(window.y+19))) && window.active && window.visible) return true;
        }
        return def;
    }
}
