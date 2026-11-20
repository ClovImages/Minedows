package ru.kelcu.windows;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import ru.kelcu.windows.components.Action;
import ru.kelcu.windows.components.Window;
import ru.kelcu.windows.components.builders.WindowBuilder;
import ru.kelcu.windows.mods.ModMenuActions;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcu.windows.screens.apps.ExplorerScreen;
import ru.kelcu.windows.screens.components.LabelConfWidget;
import ru.kelcu.windows.style.MinedowsStyle;
import ru.kelcu.windows.utils.*;
import ru.kelcu.windows.utils.overlay.OverlayHandler;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.api.events.client.ClientLifecycleEvents;
import ru.kelcuprum.alinlib.api.events.client.GuiRenderEvents;
import ru.kelcuprum.alinlib.api.events.client.ScreenEvents;
import ru.kelcuprum.alinlib.config.Config;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.info.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.sleep;
import static ru.kelcu.windows.screens.DesktopScreen.getLabelActions;
import static ru.kelcuprum.alinlib.gui.GuiUtils.interpolate;

public class Windows implements ClientModInitializer {
    public static boolean gameStarted = false;
    public static Config config = new Config("config/minedows/conf.json");
    public static Thread perlinNoises;
    public static MinedowsStyle minedowsStyle = new MinedowsStyle();
    private volatile boolean perlinLoop;
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register((s) -> {
            WallpaperUtil.registerDefaultWallpapers();
            WallpaperUtil.loadFiledWallpapers();
            ThemeManager.registerDefaultThemes();
            ThemeManager.loadThemesFromFolder();
            try {
                String file = Windows.config.getString("WALLPAPER.FILE", "city");
                if(!file.isEmpty()) {
                    if(Windows.config.getNumber("WALLPAPER.TYPE", 3).intValue() == 4){
                        for(WallpaperUtil.FiledWallpaperData fwd : WallpaperUtil.filedWallpapers){
                            if(fwd.file().toPath().toString().equals(file)){
                                WallpaperUtil.setFiledWallpaper(fwd);
                                return;
                            }
                        }
                    }
                    if(file.equals("fluffy_forever")) WallpaperUtil.fluffy();
                    else if(WallpaperUtil.getWallpaperData(file) != null) WallpaperUtil.loadWallpaper(WallpaperUtil.getWallpaperData(file));
                    else WallpaperUtil.loadFileWallpaper(Path.of(file));
                }
                if(WallpaperUtil.width == 0 && WallpaperUtil.height == 0) Windows.config.setNumber("WALLPAPER.TYPE", 0);
            } catch (Exception ex){
                ex.printStackTrace();
            }
            try {
                generatePerlin(Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2, Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//        ModManager.registerModAction(new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.explorer"), GuiUtils.getResourceLocation("windows", "textures/start/icons/folder1.png"), new WindowBuilder().setScreen(new ExplorerScreen()).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/folder1.png"))), false);
//        ModManager.registerModAction(new Action(Action.Type.OPEN_SCREEN, Component.translatable("minedows.explorer"), GuiUtils.getResourceLocation("windows", "textures/start/icons/folder1.png"), new WindowBuilder().setScreen(new ExplorerScreen()).setIcon(GuiUtils.getResourceLocation("windows", "textures/start/icons/folder1.png"))), true);
        });
        GuiRenderEvents.RENDER.register((guiGraphics, tick) -> {
            if(Minecraft.getInstance().gui.screen() instanceof ChatScreen) return;
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
            perlinLoop = true;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("windows", "windows_startup")), 1.0F));
            perlinNoises = new Thread(() -> {
                while (perlinLoop) {
                        try {
                            if(config.getNumber("WALLPAPER.TYPE", 3).intValue() == 1 && Minecraft.getInstance().level == null) {
                                generatePerlin(Minecraft.getInstance().gui.screen().width / 2, Minecraft.getInstance().gui.screen().height / 2);
                                xPerlin++;
                            } else sleep(500);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                        catch (Exception ex) {
                        }
                }
            });
            perlinNoises.start();
            for(Action action : getLabelActions()){
                Config config = new Config(Windows.config.getJsonObject("auto_run."+action.title.toString(), new JsonObject()));
                if(config.getBoolean("enable", false)) executeAction(action);
            }
            if(Windows.isModMenuInstalled()){
                ArrayList<ModMenuActions.ModInfo> mods = ModMenuActions.getMods();
                for(ModMenuActions.ModInfo mod : mods){
                    Config config = new Config(Windows.config.getJsonObject("auto_run."+mod.id(), new JsonObject()));
                    if(config.getBoolean("enable", false))
                        executeAction(new Action(Action.Type.OPEN_SCREEN, Component.literal(mod.name()), mod.icon(), mod.screen()));
                }
            }
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register((s) -> {
            perlinLoop = false;
            if (perlinNoises != null) perlinNoises.interrupt();
            if(s.gui.screen() instanceof DesktopScreen) {
                isStoppedShitGame = true;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("windows", "windows_shutdown")), 1.0F));
                long t = System.currentTimeMillis() + 2500;
                while(System.currentTimeMillis() < t){} // не задавайте лишних вопросов, я типо умни
            }
        });
        OverlayHandler hud = new OverlayHandler();
        ScreenEvents.SCREEN_RENDER.register(hud);
        GuiRenderEvents.RENDER.register(hud);
    }
    public static boolean isStoppedShitGame = false;

    public static void executeAction(Action action){
        switch (action.type){
            case STOP_GAME -> Minecraft.getInstance().stop();
            case UNPAUSE_GAME -> Minecraft.getInstance().gui.setScreen(null);
            case DISCONNECT ->
                //#if MC < 12110
                //$$PauseScreen.disconnectFromWorld(Minecraft.getInstance(), ClientLevel.DEFAULT_QUIT_MESSAGE);
                //#else
                    Minecraft.getInstance().getReportingContext().draftReportHandled(Minecraft.getInstance(), Minecraft.getInstance().gui.screen(), () -> Minecraft.getInstance().disconnectFromWorld(ClientLevel.DEFAULT_QUIT_MESSAGE), true);
                //#endif
            case OPEN_SCREEN -> {
                if(Minecraft.getInstance().gui.screen() instanceof DesktopScreen)
                    Minecraft.getInstance().gui.screen().rebuildWidgets();
                DesktopScreen.addWindow(action.getWindow());
            }
            case EXECUTE_ACTION -> action.execute.execute();
        }
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
