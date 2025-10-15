package ru.kelcu.windows.screens.options;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import ru.kelcu.windows.screens.components.VolumeComponent;
import ru.kelcu.windows.utils.WindowUtils;
import ru.kelcuprum.alinlib.gui.GuiUtils;

public class SoundMixerScreen extends Screen {
    public SoundMixerScreen() {
        super(Component.translatable("minedows.sound_mixer"));
    }

    @Override
    protected void init() {
        super.init();
        int x = 10;
        addRenderableWidget(new VolumeComponent(x, 50, 30, height-75, SoundSource.MASTER));
        x+=40;
        addRenderableWidget(new VolumeComponent(x, 50, 30, height-75, SoundSource.MUSIC));
        x+=40;
        addRenderableWidget(new VolumeComponent(x, 50, 30, height-75, SoundSource.RECORDS));
        x+=40;
        addRenderableWidget(new VolumeComponent(x, 50, 30, height-75, SoundSource.WEATHER));
        x+=40;
        addRenderableWidget(new VolumeComponent(x, 50, 30, height-75, SoundSource.BLOCKS));
        x+=40;
        addRenderableWidget(new VolumeComponent(x, 50, 30, height-75, SoundSource.HOSTILE));
        x+=40;
        addRenderableWidget(new VolumeComponent(x, 50, 30, height-75, SoundSource.NEUTRAL));
        x+=40;
        addRenderableWidget(new VolumeComponent(x, 50, 30, height-75, SoundSource.PLAYERS));
        x+=40;
        addRenderableWidget(new VolumeComponent(x, 50, 30, height-75, SoundSource.AMBIENT));
        x+=40;
        addRenderableWidget(new VolumeComponent(x, 50, 30, height-75, SoundSource.VOICE));
        x+=40;
        addRenderableWidget(new VolumeComponent(x, 50, 30, height-75, SoundSource.UI));
        x+=40;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        WindowUtils.welcomeToWhiteSpace(guiGraphics, 0, 0, width, height);
        int y = 10;
        int x = 10;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/sound_mixer/main.png"), x+5, y+5, 0, 0, 20, 20, 20, 20);
        x+=40;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/sound_mixer/music.png"), x+5, y+5, 0, 0, 20, 20, 20, 20);
        x+=40;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/sound_mixer/music_block.png"), x+5, y+5, 0, 0, 20, 20, 20, 20);
        x+=40;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/sound_mixer/weather.png"), x+5, y+5, 0, 0, 20, 20, 20, 20);
        x+=40;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/sound_mixer/blocks.png"), x+5, y+5, 0, 0, 20, 20, 20, 20);
        x+=40;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/sound_mixer/no_friends.png"), x+5, y+5, 0, 0, 20, 20, 20, 20);
        x+=40;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/sound_mixer/friend.png"), x+5, y+5, 0, 0, 20, 20, 20, 20);
        x+=40;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/sound_mixer/players.png"), x+5, y+5, 0, 0, 20, 20, 20, 20);
        x+=40;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/sound_mixer/ambient.png"), x+5, y+5, 0, 0, 20, 20, 20, 20);
        x+=40;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/sound_mixer/voice.png"), x+5, y+5, 0, 0, 20, 20, 20, 20);
        x+=40;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GuiUtils.getResourceLocation("windows", "textures/sound_mixer/ui.png"), x+5, y+5, 0, 0, 20, 20, 20, 20);
        x+=40;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        Component[] components = {
                Component.translatable("soundCategory.master"),
                Component.translatable("soundCategory.music"),
                Component.translatable("soundCategory.record"),
                Component.translatable("soundCategory.weather"),
                Component.translatable("soundCategory.block"),
                Component.translatable("soundCategory.hostile"),
                Component.translatable("soundCategory.neutral"),
                Component.translatable("soundCategory.player"),
                Component.translatable("soundCategory.ambient"),
                Component.translatable("soundCategory.voice"),
                Component.translatable("soundCategory.ui"),
        };
        int x = 10;
        for(Component component : components){
            if(x<i&&i<x+30&&10<j&&j<40){
                guiGraphics.setTooltipForNextFrame(component, i, j);
                break;
            }
            x+=40;
        }
    }
}
