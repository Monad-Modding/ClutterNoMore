package dev.tazer.clutternomore.neoforge;

import dev.tazer.clutternomore.platform.condition.ConditionalMixinPlugin;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModInfo;

import java.util.HashSet;
import java.util.Set;

public final class NeoForgeMixinPlugin extends ConditionalMixinPlugin {

    @Override
    protected Set<String> findModIds() {
        Set<String> ids = new HashSet<>();
        //? if >=26 {
        /*for (ModInfo mod : FMLLoader.getCurrent().getLoadingModList().getMods()) {
        *///?} else {
        for (ModInfo mod : FMLLoader.getLoadingModList().getMods()) {
        //?}
            ids.add(mod.getModId());
        }
        return ids;
    }

    @Override
    protected boolean isDevelopmentEnvironment() {
        //? if >=26 {
        /*return !FMLLoader.getCurrent().isProduction();
        *///?} else {
        return !FMLLoader.isProduction();
        //?}
    }
}
