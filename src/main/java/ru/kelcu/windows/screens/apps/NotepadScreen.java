package ru.kelcu.windows.screens.apps;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import ru.kelcu.windows.Windows;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.MultilineEditBoxBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

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
            MemoryStack stack = MemoryStack.stackPush();
            PointerBuffer filters = stack.mallocPointer(8);
            filters.put(stack.UTF8("*.txt"));
            filters.put(stack.UTF8("*.md"));

            filters.flip();
            File defaultPath = new File(System.getProperty("user.home")).getAbsoluteFile();
            String defaultString = defaultPath.getAbsolutePath();
            if (defaultPath.isDirectory() && !defaultString.endsWith(File.separator)) {
                defaultString += File.separator;
            }

            String result = TinyFileDialogs.tinyfd_saveFileDialog(Component.translatable("minedows.notepad.save").getString(), defaultString, filters, Component.translatable("minedows.notepad.save.filter_description").getString());
            if (result == null) return;
            file = Path.of(result);
        }
        Files.createDirectories(file.getParent());
        Files.writeString(file, value, StandardCharsets.UTF_8);
        lastValue = value;
    }
}
