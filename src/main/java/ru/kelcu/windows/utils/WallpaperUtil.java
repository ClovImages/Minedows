package ru.kelcu.windows.utils;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class WallpaperUtil {
    public static final WallpaperData CITY = new WallpaperData("city", Component.translatable("minedows.wallpaper.city"), GuiUtils.getResourceLocation("windows", "textures/wallpaper/city.png"), 1920, 1080);
    public static final WallpaperData SKY = new WallpaperData("sky", Component.translatable("minedows.wallpaper.sky"), GuiUtils.getResourceLocation("windows", "textures/wallpaper/sky.png"), 1920, 1080);
    public static final WallpaperData BLISS = new WallpaperData("bliss", Component.translatable("minedows.wallpaper.bliss"), GuiUtils.getResourceLocation("windows", "textures/wallpaper/bliss.png"), 300, 241);
    public static final WallpaperData FACTORY_TOWN = new WallpaperData("factory_town", Component.translatable("minedows.wallpaper.factory_town"), GuiUtils.getResourceLocation("windows", "textures/wallpaper/factory_town.png"), 1751, 512);
    public static final WallpaperData ERROR = new WallpaperData("error", Component.translatable("minedows.wallpaper.error"), GuiUtils.getResourceLocation("windows", "textures/wallpaper/error.png"), 1920, 1080);
    public static final WallpaperData FLUFFY = new WallpaperData("fluffy_forever", Component.translatable("minedows.wallpaper.fluffy"), GuiUtils.getResourceLocation("windows", "textures/wallpaper/fluffy.png"), 1280, 834);
    public static ResourceLocation location = GuiUtils.getResourceLocation("minedows", "wallpaper");
    public static int width = 0;
    public static int height = 0;
    public static void loadFileWallpaper(Path hellYeah) throws IOException {
        if(hellYeah.toFile().exists()){
            location = GuiUtils.getResourceLocation("minedows", "wallpaper");
            BufferedImage bufferedImage = ImageIO.read(hellYeah.toFile());
            if(bufferedImage != null){
                width = bufferedImage.getWidth();
                height = bufferedImage.getHeight();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                InputStream is = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                NativeImage image = NativeImage.read(is);
                AtomicReference<DynamicTexture> texture = new AtomicReference<>();
                Minecraft.getInstance().execute(() -> {
                    texture.set(new DynamicTexture(() -> "wallmax", image));
                    Minecraft.getInstance().getTextureManager().register(location, texture.get());
                });
            }
        }
    }

    public static void loadWallpaper(WallpaperData wallpaperData){
        location = wallpaperData.location;
        width = wallpaperData.width;
        height = wallpaperData.height;
    }

    public static void fluffy(){
        loadWallpaper(FLUFFY);
        AlinLib.MINECRAFT.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.CAT_AMBIENT, 1f, 1f));
    }

    public static HashMap<String, WallpaperData> wallpapers = new HashMap<>();
    public static void registerDefaultWallpapers(){
        registerWallpaper(CITY);
        registerWallpaper(SKY);
        registerWallpaper(BLISS);
        registerWallpaper(FACTORY_TOWN);
        registerWallpaper(ERROR);
    }
    public static void registerWallpaper(WallpaperData wallpaperData){
        wallpapers.put(wallpaperData.id, wallpaperData);
    }
    public static WallpaperData getWallpaperData(String id){
        return wallpapers.getOrDefault(id, null);
    }


    public record WallpaperData(String id, Component name, ResourceLocation location, int width, int height){};
}
