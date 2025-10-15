package ru.kelcu.windows.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.kelcu.windows.components.Window;
import ru.kelcu.windows.screens.DesktopScreen;

@Mixin(PictureInPictureRenderState.class)
public class GuiEntityRenderStateMixin {
//    @Redirect(method = "x0", at = @At("HEAD"))
//    private void init(CallbackInfoReturnable<Integer> cir){
//        if(Minecraft.getInstance().screen instanceof DesktopScreen){
//            for(Window window : DesktopScreen.windows){
//                if(window.active && window.visible) {
//                    pictureInPictureRenderState
//                    i = (int) (window.x+3+i);
//                    j = (int) (window.y+19+j);
//                    k = (int) (window.x+3+k);
//                    l = (int) (window.y+19+l);
//                    args.set(4, +(int)args.get(4));
//                    args.set(5, window.y+19+(int)args.get(5));
//                    args.set(6, window.x+3+(int)args.get(6));
//                    args.set(7, window.y+19+(int)args.get(7));
//                    ci.cancel();
//                    break;
//                }
//            }
//        }
//        return value;
//    }

}
