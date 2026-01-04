package ru.kelcu.windows.mixin.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.OutOfMemoryScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
//#if MC >= 12110
import net.minecraft.client.input.KeyEvent;
//#endif
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.kelcu.windows.screens.DesktopScreen;

import java.util.ArrayList;

@Mixin(OutOfMemoryScreen.class)
public class OutOfMemoryScreenMixin extends Screen {

    @Shadow
    @Final
    private static Component TITLE;

    @Shadow
    @Final
    private static Component MESSAGE;

    protected OutOfMemoryScreenMixin(Component component) {
        super(component);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        guiGraphics.fill(0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), 0xFF0000FF);
        int w = (int) (guiGraphics.guiWidth() * 0.6);
        ArrayList<FormattedCharSequence> messages = new ArrayList<>();
        messages.addAll(font.split(TITLE, w));
        messages.addAll(font.split(MESSAGE, w));
        int h = 30 + (font.lineHeight*2) + (font.lineHeight*messages.size()) + (2*(messages.size()-1));
        int x = guiGraphics.guiWidth() / 2 - w / 2;
        int y = guiGraphics.guiHeight() / 2 - h / 2;
        guiGraphics.fill(guiGraphics.guiWidth()/2-(font.width(Component.literal("Minedows"))/2)-6, y-3, guiGraphics.guiWidth()/2+(font.width(Component.literal("Minedows"))/2)+6, y+ font.lineHeight+3, -1);
        guiGraphics.drawString(font, Component.literal("Minedows"), guiGraphics.guiWidth()/2-(font.width(Component.literal("Minedows"))/2), y, 0xFF0000FF, false);
        y+=15+font.lineHeight;
        for(FormattedCharSequence formattedCharSequence : messages){
            guiGraphics.drawString(font, formattedCharSequence, x, y, -1, false);
            y+=2+font.lineHeight;
        }
        y+=13;
        guiGraphics.drawString(font, Component.translatable("minedows.bsod.back", System.currentTimeMillis() % 1000 <= 500 ? "  " : " _"),
                guiGraphics.guiWidth()/2-(font.width(Component.translatable("minedows.bsod.back", ""))/2), y, -1, false);
    }

    @Override
    //#if MC < 12110
    //$$public boolean keyPressed(int i, int j, int k) {
    //#else
    public boolean keyPressed(KeyEvent keyEvent) {
        //#endif
        onClose();
        return super.keyPressed(
                //#if MC < 12110
                //$$i, j, k
                //#else
                keyEvent
                //#endif
        );
    }

    @Override
    public void onClose() {
        minecraft.setScreen(new DesktopScreen());
    }

    @Inject(method = "init", at=@At("HEAD"), cancellable = true)
    public void init(CallbackInfo ci){
        ci.cancel();
    }
}
