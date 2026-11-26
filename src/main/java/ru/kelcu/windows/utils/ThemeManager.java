package ru.kelcu.windows.utils;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import ru.kelcuprum.alinlib.utils.GsonHelper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

import static ru.kelcu.windows.Windows.config;

public class ThemeManager {
    public static HashMap<String, Theme> themes = new HashMap<>();
    public static ArrayList<Theme> themesList = new ArrayList<>();

    public static Theme getSelectedTheme(){
        return themes.getOrDefault(config.getString("THEME", "city"), getUserTheme());
    }
    public final static Path path = Path.of("./config/minedows/themes");
    public static void loadThemesFromFolder(){
        if(!path.toFile().exists()) return;
        for(File file : path.toFile().listFiles()){
            if(file.isDirectory()) continue;
            try {
                JsonObject jsonObject = net.minecraft.util.GsonHelper.parse(Files.readString(file.toPath()));
                registerTheme(getThemeFromJSON(jsonObject));
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
    public static void registerDefaultThemes(){
        registerTheme(new Theme(null, "default", Component.translatable("minedows.theme.default"),0xFFcbcbcb, 0xFFcbcbcb, -16777088, -14644786, -14644786, -16777088, false, -1, false));
        registerTheme(new Theme(null, "dark", Component.translatable("minedows.theme.dark"),0xff1e1e1e, 0xff0f6573, -16777088, -14644786, -14644786, -16777088, false, -1, false));
        registerTheme(new Theme(null, "city", Component.translatable("minedows.theme.city"),0xff0e1835, 0xff0f6573, 0xff65ceb7, 0xff0f6573, 0xff0f6573, 0xff65ceb7, false, -1, false));
        registerTheme(new Theme(null, "sky", Component.translatable("minedows.theme.sky"),0xff4c1e7e, 0xffa745ff, 0xffa13eff, 0xfff3b8ff, 0xffa13eff, 0xfff3b8ff, false, -1, false));
        registerTheme(new Theme(null, "catppuccin_mocha", Component.translatable("minedows.theme.catppuccin_mocha"),0xff1e1e2e, 0xff313244, 0xff585b70, 0xff6c7086, 0xff6c7086, 0xff585b70, false, -1, false));
        registerTheme(new Theme(null, "luna", Component.translatable("minedows.theme.luna"),0xff255fdc, 0xff6696FE, -16777088, -14644786, -14644786, -16777088, false, -1, true));
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
        int pos = themesList.indexOf(theme);
        if(pos == -1) pos = themesList.size();
        return pos;
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
        return new Theme(null, "user-theme", Component.translatable("minedows.theme.user"),
                config.getNumber("THEME.MAIN_COLOR", 0xFFcbcbcb).intValue(),
                config.getNumber("THEME.ACTIVE_COLOR", 0xFFcbcbcb).intValue(),
                config.getNumber("TITLE.GRADIENT.START", -16777088).intValue(), config.getNumber("TITLE.GRADIENT.END", -14644786).intValue(),
                config.getNumber("START.GRADIENT.START", -14644786).intValue(), config.getNumber("START.GRADIENT.END", -16777088).intValue(), false, -1,
                config.getBoolean("THEME.OVERWRITE_LIGHT", false));
    }

    public static Theme getThemeFromJSON(JsonObject jsonObject){
        return getThemeFromJSON(null, jsonObject);
    }
    public static Theme getThemeFromJSON(Path path, JsonObject jsonObject){
        return new Theme(path, GsonHelper.getStringInJSON("id", jsonObject, "user-theme"), Component.translatable(GsonHelper.getStringInJSON("id", jsonObject, "title")),
                GsonHelper.getNumberInJSON("main_color", jsonObject, 0xFFcbcbcb).intValue(),
                GsonHelper.getNumberInJSON("active_color", jsonObject, 0xFFcbcbcb).intValue(),

                GsonHelper.getNumberInJSON("title_gradient_start", jsonObject, -16777088).intValue(),
                GsonHelper.getNumberInJSON("title_gradient_end", jsonObject, -14644786).intValue(),

                GsonHelper.getNumberInJSON("start_gradient_start", jsonObject, -14644786).intValue(),
                GsonHelper.getNumberInJSON("start_gradient_end", jsonObject, -16777088).intValue(),
                GsonHelper.getBooleanInJSON("colored_font", jsonObject, false), GsonHelper.getNumberInJSON("color_font", jsonObject, -1).intValue(),
                GsonHelper.getBooleanInJSON("overwrite_light", jsonObject, false));
    }

    public record Theme(Path path, String id, Component title, int mainColor, int activeColor, int startTitle, int endTitle, int startStartMenu, int endStartMenu, boolean isColoredFont, int fontColor, boolean overwrite_light){
        public JsonObject toJSON(){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", id);
            jsonObject.addProperty("title", title.tryCollapseToString());

            jsonObject.addProperty("main_color", mainColor);
            jsonObject.addProperty("active_color", activeColor);

            jsonObject.addProperty("title_gradient_start", startTitle);
            jsonObject.addProperty("title_gradient_end", endTitle);

            jsonObject.addProperty("start_gradient_start", startStartMenu);
            jsonObject.addProperty("start_gradient_end", endStartMenu);


            jsonObject.addProperty("colored_font", isColoredFont);
            jsonObject.addProperty("color_font", fontColor);
            jsonObject.addProperty("overwrite_light", overwrite_light);
            return jsonObject;
        }

        @Override
        public @NonNull String toString() {
            return toJSON().toString();
        }
    }
}
