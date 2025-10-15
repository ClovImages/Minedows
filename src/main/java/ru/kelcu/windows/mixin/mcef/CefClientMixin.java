package ru.kelcu.windows.mixin.mcef;

import net.fabricmc.loader.api.FabricLoader;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefBeforeDownloadCallback;
import org.cef.callback.CefDownloadItem;
import org.cef.callback.CefDownloadItemCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.kelcu.windows.Windows;
import ru.kelcu.windows.components.Window;
import ru.kelcu.windows.screens.DesktopScreen;
import ru.kelcu.windows.screens.apps.BrowserScreen;

@Pseudo
@Mixin(CefClient.class)
public class CefClientMixin {
    @Inject(at = @At("HEAD"), method = "onAddressChange", remap = false)
    public void onAddressChange(CefBrowser browser, CefFrame frame, String url, CallbackInfo ci) {
        Window window = getWindowByBrowser(browser);
        if(window != null) ((BrowserScreen) window.screen).editBox.setValue(url);
//        if (url != null && !(browser instanceof BrowserTabIcon) && (browser instanceof BrowserImpl)) {
//            BrowserUtil.onUrlChange();
//        }
//        BrowserCaches.urlCache.put(browser.getIdentifier(), url);
    }

    @Inject(at = @At("HEAD"), method = "onTooltip", remap = false)
    public void onTooltip(CefBrowser browser, String text, CallbackInfoReturnable<Boolean> cir) {
        Window window = getWindowByBrowser(browser);
        if(window != null) ((BrowserScreen) window.screen).tooltipHell = text;
    }

    @Inject(at = @At("HEAD"), method = "onConsoleMessage", remap = false, cancellable = true)
    public void onLoadingStateChange(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line, CallbackInfoReturnable<Boolean> cir) {
        if(!Windows.config.getBoolean("BROWSER.LOGS_ENABLES", false))// && !FabricLoader.getInstance().isDevelopmentEnvironment())
            cir.setReturnValue(false);
//        BrowserUtil.instance.updateWidgets();
//        BrowserCaches.isLoadingCache.put(browser.getIdentifier(), isLoading);
    }

    @Inject(at = @At("HEAD"), method = "onTitleChange", remap = false)
    public void onTitleChange(CefBrowser browser, String title, CallbackInfo ci) {
        Window window = getWindowByBrowser(browser);
        if(window != null) ((BrowserScreen) window.screen).titleTab = title;
    }

//    @Inject(at = @At("HEAD"), method = "onBeforeDownload", remap = false)
//    public void onBeforeDownload(CefBrowser browser, CefDownloadItem downloadItem, String suggestedName, CefBeforeDownloadCallback callback, CallbackInfo ci){
//        if(MCBrowser.getConfig().allowDownloads){
//            MCBrowser.sendToastMessage(Text.translatable("mcbrowser.download.toast.started"), Text.translatable("mcbrowser.download.toast.started.description"));
//            callback.Continue(suggestedName, true);
//        }else{
//            MCBrowser.sendToastMessage(Text.translatable("mcbrowser.download.toast.disabled"), Text.translatable("mcbrowser.download.toast.disabled.description"));
//        }
//    }
//
//    @Inject(at = @At("HEAD"), method = "onDownloadUpdated", remap = false)
//    public void onDownloadUpdated(CefBrowser browser, CefDownloadItem downloadItem, CefDownloadItemCallback callback, CallbackInfo ci){
//        if(MCBrowser.isShuttingDown){
//            callback.cancel();
//        }
//        System.out.println("Downloading " + downloadItem.getSuggestedFileName() + " (" + downloadItem.getPercentComplete() + "% Complete  (" + downloadItem.getCurrentSpeed() + " bytes/s))");
//        if(downloadItem.isComplete()){
//            MCBrowser.sendToastMessage(Text.translatable("mcbrowser.download.toast.complete"), Text.translatable("mcbrowser.download.toast.completed.description", downloadItem.getSuggestedFileName()));
//        }
//    }

    @Unique
    public Window getWindowByBrowser(CefBrowser browser){
        for(Window window : DesktopScreen.windows){
            if(window.screen instanceof BrowserScreen){
                if(((BrowserScreen) window.screen).browser.getIdentifier() == browser.getIdentifier()) return window;
            }
        }
        return null;
    }

}
