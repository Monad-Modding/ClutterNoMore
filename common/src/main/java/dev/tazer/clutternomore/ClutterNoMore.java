package dev.tazer.clutternomore;

import dev.tazer.clutternomore.data.RuntimePackResources;
import dev.tazer.clutternomore.platform.Services;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public final class ClutterNoMore {

    public static final String MOD_ID = "clutternomore";
    public static final Logger LOGGER = LogManager.getLogger("ClutterNoMore");
    public static final ClutterNoMoreConfig.Startup STARTUP_CONFIG = ClutterNoMoreConfig.Startup.createToml(Services.PLATFORM.getConfigDirectory(), MOD_ID, "startup", ClutterNoMoreConfig.Startup.class);
    public static final RuntimePackResources RESOURCES = new RuntimePackResources();
    public static final Path RUNTIME_PACK = Services.PLATFORM.getGameDirectory().resolve("resourcepacks").resolve(MOD_ID);

    private ClutterNoMore() {
    }

    public static void init() {
        LOGGER.info("Initializing {} on {}", MOD_ID, Services.PLATFORM.getName());
    }

    public static ResourceLocation id(String path) {
        return id(MOD_ID, path);
    }

    public static ResourceLocation id(String namespace, String path) {
        //? if >=1.21 {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
        //?} else {
        /*return new ResourceLocation(namespace, path);
        *///?}
    }

    public static ResourceLocation parse(String id) {
        //? if >=1.21 {
        return ResourceLocation.parse(id);
        //?} else {
        /*return new ResourceLocation(id);
        *///?}
    }
}
