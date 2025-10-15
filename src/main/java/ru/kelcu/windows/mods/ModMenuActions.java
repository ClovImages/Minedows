package ru.kelcu.windows.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ru.kelcuprum.alinlib.gui.GuiUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModMenuActions {
    public static Screen getScreen(){
        return com.terraformersmc.modmenu.api.ModMenuApi.createModsScreen(null);
    }

    public static Component getModText(){
        return com.terraformersmc.modmenu.api.ModMenuApi.createModsButtonText();
    }
    public static HashMap<String, Boolean> registeredImage = new HashMap<>();
    public static com.terraformersmc.modmenu.util.mod.fabric.FabricIconHandler iconHandler = null;
    public static ArrayList<ModInfo> cacheMods = null;
    public static ArrayList<ModInfo> getMods(){
        if(iconHandler == null) iconHandler = new com.terraformersmc.modmenu.util.mod.fabric.FabricIconHandler();
        Map<String, com.terraformersmc.modmenu.util.mod.Mod> map = com.terraformersmc.modmenu.ModMenu.MODS;
        ArrayList<ModInfo> mods = new ArrayList<>();
        if(cacheMods == null) {
            for (com.terraformersmc.modmenu.util.mod.Mod mod : map.values()) {
                if (mod.isReal()) {
                    if (!mod.getBadges().contains(com.terraformersmc.modmenu.util.mod.Mod.Badge.MINECRAFT)) {
                        if (com.terraformersmc.modmenu.ModMenu.hasConfigScreen(mod.getId())) {
                            try {
                                ResourceLocation icon = GuiUtils.getResourceLocation("minedows", String.format("mods/%s", mod.getId()));
                                if (!registeredImage.getOrDefault(mod.getId(), false)) {
                                    try {
                                        DynamicTexture texture = mod.getIcon(iconHandler, 64 * Minecraft.getInstance().options.guiScale().get());
                                        Minecraft.getInstance().execute(() -> {
                                            Minecraft.getInstance().getTextureManager().register(icon, texture);
                                        });
                                        registeredImage.put(mod.getId(), true);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                                mods.add(new ModInfo(mod.getId(), mod.getName(), com.terraformersmc.modmenu.ModMenu.getConfigScreen(mod.getId(), null), icon, mod.getBadges().contains(com.terraformersmc.modmenu.util.mod.Mod.Badge.LIBRARY)));
                            } catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
            cacheMods = mods;
        } mods = cacheMods;
        return mods;
    }

    public record ModInfo(String id, String name, Screen screen, ResourceLocation icon, boolean isLibrary){}

}
