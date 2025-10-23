package ru.kelcu.windows.mods;

import com.moulberry.flashback.Flashback;
import com.moulberry.flashback.combo_options.RecordingControlsLocation;
import com.moulberry.flashback.screen.select_replay.SelectReplayScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;
import ru.kelcu.windows.components.Action;
import ru.kelcu.windows.utils.WinColors;
import ru.kelcuprum.alinlib.AlinLib;

import java.util.ArrayList;
import java.util.List;

public class FlashbackActions {
    public static boolean isRecord(){
        return Flashback.RECORDER != null;
    }
    public static boolean isPaused(){
        return Flashback.RECORDER.isPaused();
    }
    public static boolean isShow(){
        return !(Flashback.getConfig().recordingControls.controlsLocation == RecordingControlsLocation.BELOW) && !Flashback.isInReplay();
    }

    public static int buttonsCount(){
        return AlinLib.MINECRAFT.level == null || !isShow() ? 0 : isRecord() ? 3 : 1;
    }

    public static ArrayList<Action> getActions(){
        ArrayList<Action> actions = new ArrayList<>();
        int buttons = buttonsCount();
        if(buttons == 0) {
            actions.add(new Action(() -> {
                List<String> incompatibleMods = Screen.hasShiftDown() ? List.of() : Flashback.getReplayIncompatibleMods();
                if (incompatibleMods.isEmpty()) {
                    AlinLib.MINECRAFT.setScreen(new SelectReplayScreen(null));
                } else {
                    String mods = StringUtils.join(incompatibleMods, ", ");
                    Component description = Component.translatable("flashback.incompatible_with_viewing_description").append(Component.literal(mods).withStyle(ChatFormatting.RED));
                    AlinLib.MINECRAFT.setScreen(new AlertScreen(() -> Minecraft.getInstance().setScreen(null), Component.translatable("flashback.incompatible_with_viewing"), description));
                }

            }, Component.translatable("flashback.open_replays"), WinColors.getLightIcon(String.format("textures/start/flashback/start"))));
        }
        if(buttons == 3){
            actions.add(new Action(FlashbackActions::cancelRecord,
                    Component.translatable("minedows.flashback.cancel"),
                    WinColors.getLightIcon("textures/window/close")));
            actions.add(new Action(FlashbackActions::pauseRecord,
                    Component.translatable(isPaused() ? "minedows.flashback.unpause" : "minedows.flashback.pause"),
                    WinColors.getLightIcon(String.format("textures/start/flashback/%s", isPaused() ? "pause" : "play"))));
        }
        if(buttons >= 1) {
            actions.add(new Action(FlashbackActions::stateRecord,
                    Component.translatable(isRecord() ? "minedows.flashback.finish" : "minedows.flashback.start"),
                    WinColors.getLightIcon(String.format("textures/start/flashback/%s", isRecord() ? "finish" : "start"))));
        }
        return actions;
    }

    public static void pauseRecord(){
        if(isRecord()){
            Flashback.pauseRecordingReplay(!isPaused());
        }
    }
    public static void stateRecord(){
        if(isRecord())
            Flashback.finishRecordingReplay();
        else Flashback.startRecordingReplay();
        AlinLib.MINECRAFT.setScreen(null);
    }
    public static void cancelRecord(){
        AlinLib.MINECRAFT.setScreen(new ConfirmScreen(value -> {
            if (value) {
                Flashback.cancelRecordingReplay();
                AlinLib.MINECRAFT.setScreen(null);
            } else {
                AlinLib.MINECRAFT.setScreen(new PauseScreen(true));
            }
        }, Component.literal("Confirm Cancel Recording"),
                Component.literal("Are you sure you want to cancel the recording? You won't be able to recover it")));
    }
}
