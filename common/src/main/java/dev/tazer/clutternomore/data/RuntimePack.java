package dev.tazer.clutternomore.data;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.platform.Services;
import dev.tazer.clutternomore.recipe.RecipeFixes;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.List;

public final class RuntimePack {

    private static final ThreadLocal<Boolean> DETACHED = ThreadLocal.withInitial(() -> false);

    private RuntimePack() {
    }

    public static List<PackResources> attach(PackType type, List<PackResources> packs) {
        if (DETACHED.get() || packs.contains(ClutterNoMore.RESOURCES)) return packs;
        if (type == PackType.SERVER_DATA) {
            ResourceManager manager = detached(type, packs);
            ClutterNoMore.RESOURCES.clearReloaded(PackType.SERVER_DATA);
            InheritedTags.generate(manager);
            Services.PLATFORM.generateServerData(manager);
            RecipeFixes.generate(manager);
            if (ClutterNoMore.STARTUP_CONFIG.runtimeDataGeneration.value()) {
                ClutterNoMore.RESOURCES.dump(PackType.SERVER_DATA, ClutterNoMore.RUNTIME_PACK);
            }
        }
        List<PackResources> attached = new ArrayList<>(packs);
        attached.add(insertionIndex(packs), ClutterNoMore.RESOURCES);
        return attached;
    }

    public static ResourceManager detached(PackType type, List<PackResources> packs) {
        DETACHED.set(true);
        try {
            return new MultiPackResourceManager(type, packs);
        } finally {
            DETACHED.set(false);
        }
    }

    private static int insertionIndex(List<PackResources> packs) {
        for (int i = 0; i < packs.size(); i++) {
            if (packs.get(i) instanceof VanillaPackResources) return i + 1;
        }
        return 0;
    }
}
