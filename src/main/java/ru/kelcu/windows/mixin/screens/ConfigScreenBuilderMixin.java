package ru.kelcu.windows.mixin.screens;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.screens.AbstractConfigScreen;
import ru.kelcuprum.alinlib.gui.screens.ConfigScreenBuilder;

@Mixin(ConfigScreenBuilder.class)
public class ConfigScreenBuilderMixin {

    @Inject(method = "build", at=@At("HEAD"), cancellable = true, remap = false)
    public void build(CallbackInfoReturnable<AbstractConfigScreen> cir){
        if(!(AlinLib.MINECRAFT.screen instanceof DesktopScreen)) return;
//        cir.setReturnValue(new AlinLibConfigScreen((ConfigScreenBuilder) (Object) this));
    }
}
