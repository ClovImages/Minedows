package ru.kelcu.windows.utils;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;

import static ru.kelcu.windows.Windows.config;

public class ThemeManager {
    public static HashMap<String, Theme> themes = new HashMap<>();
    public static ArrayList<Theme> themesList = new ArrayList<>();

    public static Theme getSelectedTheme(){
        return themes.getOrDefault(config.getString("THEME", "default"), getUserTheme());
    }
    public static void registerDefaultThemes(){
        registerTheme(new Theme("default", Component.translatable("minedows.theme.default"),0xFFcbcbcb, 0xFFcbcbcb, -16777088, -14644786, -14644786, -16777088));
        registerTheme(new Theme("dark", Component.translatable("minedows.theme.dark"),0xff1e1e1e, 0xff0f6573, -16777088, -14644786, -14644786, -16777088));
        registerTheme(new Theme("city", Component.translatable("minedows.theme.city"),0xff0e1835, 0xff0f6573, 0xff65ceb7, 0xff0f6573, 0xff0f6573, 0xff65ceb7));
        registerTheme(new Theme("sky", Component.translatable("minedows.theme.sky"),0xff4c1e7e, 0xffa745ff, 0xffa13eff, 0xfff3b8ff, 0xffa13eff, 0xfff3b8ff));
    }

    public static String[] getThemesID(){
        String[] strings = new String[themesList.size()+1];
        int i =0;
        for(Theme theme : themesList){
            strings[i] = theme.id;
            i++;
        }
        strings[i] = "user-theme";
        return strings;
    }

    public static int getThemePosition(Theme theme){
        return themesList.indexOf(theme);
    }

    public static String[] getThemesNames(){
        String[] strings = new String[themesList.size()+1];
        int i =0;
        for(Theme theme : themesList){
            strings[i] = theme.title.getString();
            i++;
        }
        strings[i] = Component.translatable("minedows.theme.user").getString();
        return strings;
    }

    public static void registerTheme(Theme theme){
        themesList.add(theme);
        themes.put(theme.id, theme);
    }
    public static Theme getUserTheme(){
        return new Theme("user-theme", Component.translatable("minedows.theme.user"),
                config.getNumber("THEME.MAIN_COLOR", 0xFFcbcbcb).intValue(),
                config.getNumber("THEME.ACTIVE_COLOR", 0xFFcbcbcb).intValue(),
                config.getNumber("TITLE.GRADIENT.START", -16777088).intValue(), config.getNumber("TITLE.GRADIENT.END", -14644786).intValue(),
                config.getNumber("START.GRADIENT.START", -14644786).intValue(), config.getNumber("START.GRADIENT.END", -16777088).intValue());
    }

    public record Theme(String id, Component title, int mainColor, int activeColor, int startTitle, int endTitle, int startStartMenu, int endStartMenu){}
}
