package ru.kelcu.windows.mixin.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OutOfMemoryScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import ru.kelcu.windows.utils.SoundUtils;
import ru.kelcuprum.alinlib.AlinLib;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {

    @ModifyArgs(method = "init", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/TitleScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;"))
    public void init(Args args){
        if(args.get(0) instanceof Button){
            Button guiEventListener = args.get(0);
            if(guiEventListener.getMessage().equals(Component.translatable("menu.quit"))){
                args.set(0, Button.builder(Component.translatable("menu.quit"), (button) -> {
                    if(Math.random() < 0.5) {
                        button.setPosition((int) (button.getX() - (button.getWidth() * Math.random() * (Math.random() < 0.5 ? -1 : 1))), (int) (button.getY() - (button.getHeight() * Math.random() * (Math.random() < 0.5 ? -1 : 1))));
                        SoundUtils.error();
                    } else {
                        AlinLib.MINECRAFT.setScreen(new OutOfMemoryScreen());
                    }
                }).bounds(guiEventListener.getX(), guiEventListener.getY(), 98, 20).build());
            }
        }
    }
}
