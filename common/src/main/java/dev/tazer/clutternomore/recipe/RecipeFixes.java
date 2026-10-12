package dev.tazer.clutternomore.recipe;

import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public final class RecipeFixes {

    public static final String DIRECTORY = "recipe_fixes";

    private static volatile boolean enabled = ClutterNoMore.STARTUP_CONFIG.recipeFixes.value();

    private RecipeFixes() {
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static void generate(ResourceManager manager) {
        enabled = ClutterNoMore.STARTUP_CONFIG.recipeFixes.value();
        if (!enabled) return;
        for (Map.Entry<ResourceLocation, Resource> entry : manager.listResources(DIRECTORY, id -> id.getNamespace().equals(ClutterNoMore.MOD_ID) && id.getPath().endsWith(".json")).entrySet()) {
            ResourceLocation target = target(entry.getKey());
            if (target == null) {
                continue;
            }
            try (InputStream input = entry.getValue().open()) {
                ClutterNoMore.RESOURCES.addReloaded(PackType.SERVER_DATA, target, input.readAllBytes());
            } catch (IOException exception) {
                ClutterNoMore.LOGGER.error("Failed to read recipe fix {}", entry.getKey(), exception);
            }
        }
    }

    @Nullable
    private static ResourceLocation target(ResourceLocation file) {
        String path = file.getPath().substring(DIRECTORY.length() + 1);
        int separator = path.indexOf('/');
        if (separator <= 0) return null;
        return ClutterNoMore.id(path.substring(0, separator), getRecipeFolder() + "/" + path.substring(separator + 1));
    }

    public static String getRecipeFolder() {
        //? if >=1.21 {
        return "recipe";
        //?} else {
        /*return "recipes";
        *///?}
    }
}
