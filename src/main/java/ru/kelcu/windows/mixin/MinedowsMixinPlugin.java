package ru.kelcu.windows.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MinedowsMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if(mixinClassName.startsWith("ru.kelcu.windows.mixin.mcef") && !FabricLoader.getInstance().isModLoaded("mcef")){
            return false;
        }
        if(mixinClassName.startsWith("ru.kelcu.windows.mixin.lamb") && !FabricLoader.getInstance().isModLoaded("lambdynlights")){
            return false;
        }
        if(mixinClassName.startsWith("ru.kelcu.windows.mixin.figura") && !FabricLoader.getInstance().isModLoaded("figura")){
            return false;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
