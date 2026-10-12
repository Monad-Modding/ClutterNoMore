package dev.tazer.clutternomore.fabric;

import dev.tazer.clutternomore.platform.condition.ConditionalMixinPlugin;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.HashSet;
import java.util.Set;

public final class FabricMixinPlugin extends ConditionalMixinPlugin {

    @Override
    protected Set<String> findModIds() {
        Set<String> ids = new HashSet<>();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            ids.add(mod.getMetadata().getId());
        }
        return ids;
    }

    @Override
    protected boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
