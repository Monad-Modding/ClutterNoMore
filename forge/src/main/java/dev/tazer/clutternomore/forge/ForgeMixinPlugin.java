package dev.tazer.clutternomore.forge;

import dev.tazer.clutternomore.platform.condition.ConditionalMixinPlugin;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import java.util.HashSet;
import java.util.Set;

public final class ForgeMixinPlugin extends ConditionalMixinPlugin {

    @Override
    protected Set<String> findModIds() {
        Set<String> ids = new HashSet<>();
        for (ModInfo mod : FMLLoader.getLoadingModList().getMods()) {
            ids.add(mod.getModId());
        }
        return ids;
    }

    @Override
    protected boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }
}
