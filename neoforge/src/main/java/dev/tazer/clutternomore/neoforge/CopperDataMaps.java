package dev.tazer.clutternomore.neoforge;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.data.RuntimeData;
import dev.tazer.clutternomore.data.TagFiles;
import dev.tazer.clutternomore.registry.ShapeBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class CopperDataMaps {

    private static final String OXIDIZABLES = "oxidizables";
    private static final String WAXABLES = "waxables";
    private static final String NEXT_STAGE = "next_oxidation_stage";
    private static final String WAXED = "waxed";

    private CopperDataMaps() {
    }

    public static void writeDefaults() {
        RuntimeData.writeServerData(file(OXIDIZABLES), dataMap(ShapeBlocks.getOxidationStages(), NEXT_STAGE));
        RuntimeData.writeServerData(file(WAXABLES), dataMap(ShapeBlocks.getWaxedBlocks(), WAXED));
    }

    public static void generate(ResourceManager manager) {
        if (ShapeBlocks.getSources().isEmpty()) return;
        TagFiles blockTags = new TagFiles(manager, RuntimeData.getBlockTagFolder());
        Map<Block, Block> oxidation = new LinkedHashMap<>(ShapeBlocks.getOxidationStages());
        for (Map.Entry<Block, Block> pair : read(manager, OXIDIZABLES, NEXT_STAGE, blockTags).entrySet()) {
            Block shape = ShapeBlocks.getShape(pair.getKey());
            Block next = ShapeBlocks.getShape(pair.getValue());
            if (shape instanceof WeatheringCopper && next != null && next != shape) {
                oxidation.put(shape, next);
            }
        }
        Map<Block, Block> waxing = new LinkedHashMap<>(ShapeBlocks.getWaxedBlocks());
        for (Map.Entry<Block, Block> pair : read(manager, WAXABLES, WAXED, blockTags).entrySet()) {
            Block shape = ShapeBlocks.getShape(pair.getKey());
            Block waxed = ShapeBlocks.getShape(pair.getValue());
            if (shape != null && waxed != null && waxed != shape) {
                waxing.put(shape, waxed);
            }
        }
        ClutterNoMore.RESOURCES.addReloadedJson(PackType.SERVER_DATA, file(OXIDIZABLES), dataMap(oxidation, NEXT_STAGE));
        ClutterNoMore.RESOURCES.addReloadedJson(PackType.SERVER_DATA, file(WAXABLES), dataMap(waxing, WAXED));
    }

    private static Map<Block, Block> read(ResourceManager manager, String name, String field, TagFiles blockTags) {
        Map<ResourceLocation, ResourceLocation> pairs = new LinkedHashMap<>();
        for (Resource resource : manager.getResourceStack(file(name))) {
            JsonObject json = TagFiles.read(resource);
            if (json == null) {
                continue;
            }
            if (GsonHelper.getAsBoolean(json, "replace", false)) {
                pairs.clear();
            }
            if (json.get("values") instanceof JsonObject values) {
                for (Map.Entry<String, JsonElement> entry : values.entrySet()) {
                    ResourceLocation target = target(entry.getValue(), field);
                    if (target == null) {
                        continue;
                    }
                    for (ResourceLocation key : blockTags.resolveEntry(entry.getKey())) {
                        pairs.put(key, target);
                    }
                }
            }
            if (json.get("remove") instanceof JsonArray removals) {
                for (JsonElement removal : removals) {
                    String key = removal.isJsonObject() ? GsonHelper.getAsString(removal.getAsJsonObject(), "key", "") : removal.isJsonPrimitive() ? removal.getAsString() : "";
                    for (ResourceLocation id : blockTags.resolveEntry(key)) {
                        pairs.remove(id);
                    }
                }
            }
        }
        Map<Block, Block> blocks = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, ResourceLocation> pair : pairs.entrySet()) {
            Optional<Block> from = BuiltInRegistries.BLOCK.getOptional(pair.getKey());
            Optional<Block> to = BuiltInRegistries.BLOCK.getOptional(pair.getValue());
            if (from.isPresent() && to.isPresent()) {
                blocks.put(from.get(), to.get());
            }
        }
        return blocks;
    }

    @Nullable
    private static ResourceLocation target(JsonElement entry, String field) {
        JsonElement value = entry.isJsonObject() && entry.getAsJsonObject().has("value") ? entry.getAsJsonObject().get("value") : entry;
        JsonElement id = value.isJsonObject() ? value.getAsJsonObject().get(field) : value;
        return id != null && id.isJsonPrimitive() ? ResourceLocation.tryParse(id.getAsString()) : null;
    }

    private static JsonObject dataMap(Map<Block, Block> pairs, String field) {
        JsonObject values = new JsonObject();
        for (Map.Entry<Block, Block> pair : pairs.entrySet()) {
            JsonObject value = new JsonObject();
            value.addProperty(field, BuiltInRegistries.BLOCK.getKey(pair.getValue()).toString());
            values.add(BuiltInRegistries.BLOCK.getKey(pair.getKey()).toString(), value);
        }
        JsonObject map = new JsonObject();
        map.add("values", values);
        return map;
    }

    private static ResourceLocation file(String name) {
        return ClutterNoMore.id("neoforge", "data_maps/block/" + name + ".json");
    }
}
