package ru.kelcu.windows.screens.apps;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.sdl.SDL;
import org.lwjgl.sdl.SDLDialog;
import org.lwjgl.sdl.SDL_DialogFileCallbackI;
import org.lwjgl.sdl.SDL_DialogFileFilter;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import ru.kelcu.windows.Windows;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.MultilineEditBoxBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.sdl.SDLError.SDL_GetError;
import static ru.kelcu.windows.Windows.minedowsStyle;

public class NotepadScreen extends Screen {
    public String value = "";
    public Path file;
    public NotepadScreen() {
        this(null);
    }
    public NotepadScreen(Path file){
        super(Component.translatable("minedows.notepad"));
        this.file = file;
        if(file != null && file.toFile().exists()){
            try{
                this.value = Files.readString(file);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        this.lastValue = this.value;
    }

    @Override
    protected void init() {
        addRenderableWidget(new ButtonBuilder(Component.translatable("windows.paint.save"), (s) -> {
            try {
                saveText();
            } catch (Exception exception){
                exception.printStackTrace();
            }
        })
                .setSize(75, 16).setPosition(0, 1)
                .setStyle(minedowsStyle).build());
        addRenderableWidget(new MultilineEditBoxBuilder(Component.empty()).setValue(value).setResponder((string) -> value = string)
                .setSize(width, height-19).setPosition(0, 19).setStyle(Windows.minedowsStyle).build());
    }

    @Override
    public @NotNull Component getTitle() {
        MutableComponent title;
        if(file != null && file.toFile().exists()){
            title = Component.empty().append(super.getTitle()).append(" - ").append(file.toFile().getName());
        } else {
            title = Component.empty().append(super.getTitle()).append(" - Untitled file");
        }
        if(!lastValue.equals(value)) title.append("*");
        return title;
    }
    public String lastValue = "";
    public void saveText() throws IOException {
        if(file == null) {
            // Никакого new Thread()! Вызов строго с главного потока
            File defaultPath = new File(System.getProperty("user.home")).getAbsoluteFile();
            String defaultString = defaultPath.getAbsolutePath();
            if (defaultPath.isDirectory() && !defaultString.endsWith(File.separator)) {
                defaultString += File.separator;
            }

            SDL_DialogFileFilter.Buffer filters = SDL_DialogFileFilter.calloc(1);
            ByteBuffer filterName = MemoryUtil.memASCII(
                    Component.translatable("minedows.notepad.save.filter_description").getString());
            ByteBuffer filterPattern = MemoryUtil.memASCII("txt;md");
            filters.get(0).name(filterName).pattern(filterPattern);

            SDL_DialogFileCallbackI pendingFileDialog = (userdata, filelist, filter) -> {
                try {
                    if (filelist == MemoryUtil.NULL) {
                        // Вот здесь и живёт твоя «пустая» причина — читаем её
                        AlinLib.LOG.error("SDL file dialog failed: {}", SDL_GetError());
                        minecraft.execute(() -> {
                            // покажешь пользователю тост об ошибке, если хочешь
                        });
                        return;
                    }

                    List<String> picked = new ArrayList<>();
                    for (long p = filelist; p != 0L; p += PointerBuffer.POINTER_SIZE) {
                        long s = MemoryUtil.memGetAddress(p);
                        if (s == MemoryUtil.NULL) break;
                        picked.add(MemoryUtil.memUTF8(s));
                    }
                    if (picked.isEmpty()) {
                        AlinLib.LOG.log("Dialog cancelled by user");
                        return;
                    }

                    String path = picked.get(0);
                    file = new File(path).toPath();
                } finally {
                    filters.free();
                    MemoryUtil.memFree(filterName);
                    MemoryUtil.memFree(filterPattern);
                }
            };

            // 0 вместо указателя окна — на время диагностики
            SDLDialog.SDL_ShowSaveFileDialog(pendingFileDialog, 0L, 0L, filters, defaultString);
        }
        if(file == null) return;
        Files.createDirectories(file.getParent());
        Files.writeString(file, value, StandardCharsets.UTF_8);
        lastValue = value;
    }
}
