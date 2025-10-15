package ru.kelcu.windows.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import ru.kelcu.windows.Windows;

public class SoundUtils {
    public static void error(){
        if(Windows.config.getBoolean("DISABLE_ERROR_SOUND", false)) return;
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), 1.0F, 0.3F));
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), 1.5F, 0.3F));
    }

    public static void click(){
        if(Windows.config.getBoolean("DISABLE_CLICK_SOUND", false)) return;
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_HAT.value(), 1.25F, 0.3F));
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_HAT.value(), 1.5F, 0.3F));
    }
}
