package ru.kelcu.windows;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;
import ru.kelcuprum.alinlib.AlinLib;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class SoundLoader {
    private static final Path SOUND = FabricLoader.getInstance().getConfigDir().resolve("minedows/crash.wav");
    private static Clip clip;

    public static void init() {
        getSound().ifPresent(it -> {
            try {
                AudioInputStream is = AudioSystem.getAudioInputStream(it);
                clip = AudioSystem.getClip();
                clip.open(is);
            } catch (Exception e) {
                AlinLib.LOG.log("Couldn't load clip %s: %s".formatted(SOUND, e.getMessage()), Level.ERROR);
            }
        });
    }

    public static void tryPlay() {
        if (clip != null) {
            clip.start();
            try {
                Thread.sleep(clip.getMicrosecondLength() / 1000);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private static Optional<File> getSound() {
        try {
            if (!Files.exists(SOUND)) {
                Files.createDirectories(SOUND.getParent());
                moveResource();
            }
            return Optional.of(SOUND.toFile().getAbsoluteFile());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static void moveResource() {
        try (InputStream is = SoundLoader.class.getClassLoader().getResourceAsStream("assets/windows/sounds/crash.wav")) {
            if (is == null) throw new IllegalStateException("Sound resource not present");
            Files.copy(is, SOUND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
