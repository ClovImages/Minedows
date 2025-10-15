package ru.kelcu.windows.screens.options.lib;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcu.windows.Windows;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.screens.ConfigScreenBuilder;

public class MinedowsConfigScreen {
    public static Screen build(Screen parent){
        ConfigScreenBuilder builder = new ConfigScreenBuilder(parent);
        builder.setTitle(Component.literal("Minedows 98"));
        builder.addWidget(new ButtonBooleanBuilder(Component.translatable("minedows.config.custom_lables_pos"), false).setConfig(Windows.config, "LABEL.CUSTOM_POS"));
        return builder.build();
    }
}
