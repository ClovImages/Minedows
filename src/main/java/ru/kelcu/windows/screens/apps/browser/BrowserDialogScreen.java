package ru.kelcu.windows.screens.apps.browser;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.cef.callback.CefJSDialogCallback;
import org.cef.handler.CefJSDialogHandler;
import org.cef.misc.BoolRef;
import ru.kelcu.windows.screens.components.alinlib.EditBox;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.text.TextBuilder;
import ru.kelcuprum.alinlib.gui.components.text.TextBox;

public class BrowserDialogScreen extends Screen {
    public CefJSDialogHandler.JSDialogType jsDialogType;
    public String message;
    public String default_prompt;
    public CefJSDialogCallback callback;
    public BoolRef suppress_message;
    public BrowserDialogScreen(CefJSDialogHandler.JSDialogType jsDialogType, String message, String default_prompt, CefJSDialogCallback callback, BoolRef suppress_message) {
        super(Component.literal("CloviumDialog.exe"));
        this.jsDialogType = jsDialogType;
        this.message = message;
        this.default_prompt = default_prompt;
        this.callback = callback;
        this.suppress_message = suppress_message;
    }

    EditBox editBox;

    @Override
    protected void init() {
        addRenderableWidget(new TextBuilder().setType(TextBuilder.TYPE.MESSAGE).setTitle(message).setWidth(width-6).setHeight(height-(jsDialogType == CefJSDialogHandler.JSDialogType.JSDIALOGTYPE_PROMPT ? 50 : 28)).setPosition(3, 3).build());
        if(jsDialogType == CefJSDialogHandler.JSDialogType.JSDIALOGTYPE_PROMPT){
            if(editBox == null) editBox = new EditBox(new EditBoxBuilder().setValue(default_prompt).setWidth(width-6).setPosition(3, height-44));
            else {
                editBox.setPosition(3, height-44);
                editBox.setWidth(width-6);
            }
            addRenderableWidget(editBox);
        }
        addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.browser.alert.confirm"), (s) -> {
            suppress_message.set(true);
            callback.Continue(true, editBox == null ? "" : editBox.getValue());
            onClose();
        }).setWidth(100).setPosition(width-103, height-22).build());
        addRenderableWidget(new ButtonBuilder(Component.translatable("minedows.browser.alert.cancel"), (s) -> {
            suppress_message.set(false);
            callback.Continue(false, editBox == null ? "" : editBox.getValue());
            onClose();
        }).setWidth(100).setPosition(width-205, height-22).build());
    }
}
