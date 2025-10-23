package ru.kelcu.windows;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import ru.kelcu.windows.components.Window;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcu.windows.style.MinedowsStyle;
import ru.kelcu.windows.utils.Perlin2D;
import ru.kelcu.windows.utils.ThemeManager;
import ru.kelcu.windows.utils.WallpaperUtil;
import ru.kelcu.windows.utils.WinColors;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.api.events.client.ClientLifecycleEvents;
import ru.kelcuprum.alinlib.api.events.client.GuiRenderEvents;
import ru.kelcuprum.alinlib.config.Config;
import ru.kelcuprum.alinlib.gui.GuiUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.sleep;
import static ru.kelcuprum.alinlib.gui.GuiUtils.interpolate;

public class Windows implements ClientModInitializer {
    public static boolean gameStarted = false;
    public static Config config = new Config("config/minedows/conf.json");
    public static Thread perlinNoises;
    public static MinedowsStyle minedowsStyle = new MinedowsStyle();
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register((s) -> {
            WallpaperUtil.registerDefaultWallpapers();
            ThemeManager.registerDefaultThemes();
            try {
                String file = Windows.config.getString("WALLPAPER.FILE", "");
                if(!file.isEmpty()) {
                    if(file.equals("fluffy_forever")) WallpaperUtil.fluffy();
                    else if(WallpaperUtil.getWallpaperData(file) != null) WallpaperUtil.loadWallpaper(WallpaperUtil.getWallpaperData(file));
                    else WallpaperUtil.loadFileWallpaper(Path.of(file));
                }
                if(WallpaperUtil.width == 0 && WallpaperUtil.height == 0) Windows.config.setNumber("WALLPAPER.TYPE", 0);
            } catch (Exception ex){
                ex.printStackTrace();
            }
            try {
                generatePerlin(AlinLib.MINECRAFT.getWindow().getGuiScaledWidth() / 2, AlinLib.MINECRAFT.getWindow().getGuiScaledHeight() / 2);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        GuiRenderEvents.RENDER.register((guiGraphics, tick) -> {
            for(Window window : DesktopScreen.windows){
                if(window.pinned && window.visible){
                    DesktopScreen.renderWindow(guiGraphics, window, guiGraphics.guiWidth()/2, guiGraphics.guiHeight()/2, tick);
                }
            }
        });
        FabricLoader.getInstance().getModContainer("minedows").ifPresent(container -> {
            ResourceManagerHelper.registerBuiltinResourcePack(GuiUtils.getResourceLocation("minedows","windows_sound"), container, Component.translatable("resourcePack.windows_sound"), ResourcePackActivationType.NORMAL);
        });
        GuiUtils.registerStyle(minedowsStyle);
        ClientLifecycleEvents.CLIENT_FULL_STARTED.register((s) -> {
            gameStarted = true;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("windows", "windows_startup")), 1.0F));
            perlinNoises = new Thread(() -> {
                while(true) {
                        try {
                            if(config.getNumber("WALLPAPER.TYPE", 0).intValue() == 1 && AlinLib.MINECRAFT.level == null) {
                                generatePerlin(AlinLib.MINECRAFT.screen.width / 2, AlinLib.MINECRAFT.screen.height / 2);
                                xPerlin++;
                            } else sleep(500);
                        } catch (Exception ex) {
                        }
                }
            });
            perlinNoises.start();
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register((s) -> {if(perlinNoises != null) perlinNoises.interrupt();});
    }

    public static Perlin2D perlin = null;
    public static int xPerlin = 0;

    public static void generatePerlin(int width, int height) throws IOException {
        if(perlin == null) perlin = new Perlin2D((long) (Long.MAX_VALUE*((Math.random() > 0.5 ? -1 : 1)*Math.random())));
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int xd = 0; xd < width; xd++) {
            for (int yd = 0; yd < height; yd++) {
                float value = perlin.getNoise((xd + xPerlin) / 100f, yd / 100f, 8, 0.2f) + .5f;
                int[] colors = WinColors.getPerlinColors();//Colors.getWinTitleGradientColor();
                bufferedImage.setRGB(xd, yd, interpolate(colors[0], colors[1], value));
            }
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        InputStream is = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        NativeImage image = NativeImage.read(is);
        AtomicReference<DynamicTexture> texture = new AtomicReference<>();
        Minecraft.getInstance().execute(() -> {
            texture.set(new DynamicTexture(() -> "perlin", image));
            Minecraft.getInstance().getTextureManager().register(GuiUtils.getResourceLocation("minedows", "perlin"), texture.get());
        });
    }

    public static boolean isModMenuInstalled() {
        return FabricLoader.getInstance().isModLoaded("modmenu") || FabricLoader.getInstance().isModLoaded("menulogue");
    }

    public static boolean isCatalogueInstalled() {
        return FabricLoader.getInstance().isModLoaded("catalogue");
    }

    public static boolean isDeveloperPreview(){
        String version = FabricLoader.getInstance().getModContainer("minedows").get().getMetadata().getVersion().getFriendlyString();
        return FabricLoader.getInstance().isDevelopmentEnvironment() || (version.contains("alpha") || version.contains("beta") || version.contains("dev") || version.contains("rc"));
    }
}
