package ru.kelcu.windows.mods;

import com.mrcrayfish.catalogue.client.screen.CatalogueModListScreen;
import net.minecraft.client.gui.screens.Screen;
import ru.kelcuprum.alinlib.AlinLib;

public class CatalogueActions {
    public static Screen getScreen(){
        return new CatalogueModListScreen(null);
    }
}
