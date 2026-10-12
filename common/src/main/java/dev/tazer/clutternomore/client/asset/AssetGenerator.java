package dev.tazer.clutternomore.client.asset;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.client.ClutterNoMoreClient;
import dev.tazer.clutternomore.registry.ShapeBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class AssetGenerator {

    private static final List<String> LOWERCASE_WORDS = List.of("of", "and", "with");
    private static final List<String> TOP_FALLBACKS = List.of("side", "bottom", "all", "texture", "particle");

    private AssetGenerator() {
    }

    public static void generate(ResourceManager manager) {
        ClutterNoMore.RESOURCES.clearReloaded(PackType.CLIENT_RESOURCES);
        List<String> paths = ShapeBlocks.getShapePaths();
        if (paths.isEmpty()) return;
        Set<String> translated = translatedKeys(manager);
        JsonObject lang = new JsonObject();
        for (String path : paths) {
            String key = "block.clutternomore." + path.replace("/", ".");
            if (!translated.contains(key)) {
                lang.addProperty(key, langName(path));
            }
        }
        write("lang/en_us.json", lang);
        VerticalSlabAssets.generate(manager);
        StepAssets.generate(manager);
        if (ClutterNoMoreClient.CONFIG.runtimeAssetGeneration.value()) {
            ClutterNoMore.RESOURCES.dump(PackType.CLIENT_RESOURCES, ClutterNoMore.RUNTIME_PACK);
        }
    }

    public static void write(String file, JsonElement contents) {
        ClutterNoMore.RESOURCES.addReloadedJson(PackType.CLIENT_RESOURCES, ClutterNoMore.id(file), contents);
    }

    private static Set<String> translatedKeys(ResourceManager manager) {
        Set<String> translated = new HashSet<>();
        for (Resource resource : manager.getResourceStack(ClutterNoMore.id("lang/en_us.json"))) {
            try (Reader reader = resource.openAsReader()) {
                translated.addAll(JsonParser.parseReader(reader).getAsJsonObject().keySet());
            } catch (Exception exception) {
                ClutterNoMore.LOGGER.warn("Failed to read existing translations from pack {}", resource.sourcePackId());
            }
        }
        return translated;
    }

    public static String langName(String path) {
        String name = path.contains("/") ? path.substring(path.lastIndexOf("/") + 1) : path;
        StringBuilder result = new StringBuilder();
        for (String word : name.replace("_", " ").split(" ")) {
            if (word.isEmpty()) {
                continue;
            }
            result.append(LOWERCASE_WORDS.contains(word) ? word.charAt(0) : Character.toUpperCase(word.charAt(0)));
            result.append(word.substring(1)).append(" ");
        }
        return result.toString().trim();
    }

    @Nullable
    public static ResourceLocation findModel(ResourceManager manager, BlockState state) {
        JsonObject blockState = readJson(manager, BuiltInRegistries.BLOCK.getKey(state.getBlock()).withPrefix("blockstates/").withSuffix(".json"));
        if (blockState == null) return null;
        JsonElement first = null;
        if (blockState.get("variants") instanceof JsonObject variants) {
            for (Map.Entry<String, JsonElement> variant : variants.entrySet()) {
                if (matchesVariant(variant.getKey(), state)) return modelOf(variant.getValue());
                if (first == null) {
                    first = variant.getValue();
                }
            }
        } else if (blockState.get("multipart") instanceof JsonArray parts) {
            for (JsonElement part : parts) {
                if (!(part instanceof JsonObject object)) {
                    continue;
                }
                if (!object.has("when") || matchesCondition(object.get("when"), state)) return modelOf(object.get("apply"));
                if (first == null) {
                    first = object.get("apply");
                }
            }
        }
        return modelOf(first);
    }

    @Nullable
    public static JsonObject getTextures(ResourceManager manager, ResourceLocation model) {
        JsonObject textures = new JsonObject();
        Set<ResourceLocation> visited = new HashSet<>();
        ResourceLocation current = model;
        while (current != null && visited.add(current)) {
            JsonObject json = readModel(manager, current);
            if (json == null) {
                break;
            }
            if (json.get("textures") instanceof JsonObject own) {
                for (Map.Entry<String, JsonElement> texture : own.entrySet()) {
                    if (!textures.has(texture.getKey())) {
                        textures.add(texture.getKey(), texture.getValue());
                    }
                }
            }
            current = parentOf(json);
        }
        resolveReferences(textures);
        if (textures.has("thatch") && textures.has("extrudes")) {
            textures.addProperty("is_thatch", true);
            return textures;
        }
        if (textures.get("top") == null) {
            for (String fallback : TOP_FALLBACKS) {
                if (textures.get(fallback) != null) {
                    textures.add("top", textures.get(fallback));
                    break;
                }
            }
        }
        if (textures.get("side") == null) {
            textures.add("side", textures.get("top"));
        }
        if (textures.get("bottom") == null) {
            textures.add("bottom", textures.get("top"));
        }
        return textures.get("top") == null ? null : textures;
    }

    public static void generateItem(ResourceLocation parent, ResourceLocation shape, ResourceManager manager) {
        String model = "clutternomore:block/" + shape.getPath().replace("waxed_", "");
        //? if >=1.21.4 {
        /*if (manager.getResource(shape.withPrefix("items/").withSuffix(".json")).isPresent()) return;
        JsonObject definition = new JsonObject();
        definition.addProperty("type", "minecraft:model");
        definition.addProperty("model", model);
        Optional<Resource> parentItem = manager.getResource(parent.withPrefix("items/").withSuffix(".json"));
        if (parentItem.isPresent()) {
            try (Reader reader = parentItem.get().openAsReader()) {
                JsonObject parentJson = JsonParser.parseReader(reader).getAsJsonObject();
                if (parentJson.has("model") && parentJson.getAsJsonObject("model").has("tints")) {
                    definition.add("tints", parentJson.getAsJsonObject("model").getAsJsonArray("tints"));
                }
            } catch (Exception exception) {
                ClutterNoMore.LOGGER.warn("Failed to extract item tints from parent {} for shape {}", parent, shape, exception);
            }
        }
        JsonObject item = new JsonObject();
        item.add("model", definition);
        write("items/%s.json".formatted(shape.getPath()), item);
        *///?} else {
        if (manager.getResource(shape.withPrefix("models/item/").withSuffix(".json")).isPresent()) return;
        JsonObject item = new JsonObject();
        item.addProperty("parent", model);
        write("models/item/%s.json".formatted(shape.getPath()), item);
        //?}
    }

    public static boolean hasTint(ResourceManager manager, ResourceLocation model) {
        Set<ResourceLocation> visited = new HashSet<>();
        ResourceLocation current = model;
        while (current != null && visited.add(current)) {
            JsonObject json = readModel(manager, current);
            if (json == null) return false;
            if (json.get("elements") instanceof JsonArray elements) {
                for (JsonElement element : elements) {
                    if (element instanceof JsonObject object && object.get("faces") instanceof JsonObject faces) {
                        for (Map.Entry<String, JsonElement> face : faces.entrySet()) {
                            if (face.getValue() instanceof JsonObject faceObject && faceObject.has("tintindex")) return true;
                        }
                    }
                }
            }
            current = parentOf(json);
        }
        return false;
    }

    @Nullable
    public static String getRenderType(ResourceManager manager, ResourceLocation model) {
        Set<ResourceLocation> visited = new HashSet<>();
        ResourceLocation current = model;
        while (current != null && visited.add(current)) {
            JsonObject json = readModel(manager, current);
            if (json == null) return null;
            if (json.get("render_type") instanceof JsonPrimitive renderType) return renderType.getAsString();
            current = parentOf(json);
        }
        return null;
    }

    private static boolean matchesVariant(String key, BlockState state) {
        if (key.isEmpty()) return true;
        for (String condition : key.split(",")) {
            int separator = condition.indexOf('=');
            if (separator < 0 || !hasValue(state, condition.substring(0, separator), condition.substring(separator + 1))) return false;
        }
        return true;
    }

    private static boolean matchesCondition(@Nullable JsonElement when, BlockState state) {
        if (!(when instanceof JsonObject condition)) return false;
        for (Map.Entry<String, JsonElement> entry : condition.entrySet()) {
            String key = entry.getKey();
            if (key.equals("OR") || key.equals("AND")) {
                if (!(entry.getValue() instanceof JsonArray terms)) return false;
                boolean any = false;
                boolean all = true;
                for (JsonElement term : terms) {
                    boolean matched = matchesCondition(term, state);
                    any |= matched;
                    all &= matched;
                }
                if (key.equals("OR") ? !any : !all) return false;
            } else if (!(entry.getValue() instanceof JsonPrimitive value) || !hasValue(state, key, value.getAsString())) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasValue(BlockState state, String name, String values) {
        Property<?> property = state.getBlock().getStateDefinition().getProperty(name);
        if (property == null) return false;
        boolean negated = values.startsWith("!");
        String current = valueName(state, property);
        boolean found = false;
        for (String value : (negated ? values.substring(1) : values).split("\\|")) {
            found |= value.equals(current);
        }
        return found != negated;
    }

    private static <T extends Comparable<T>> String valueName(BlockState state, Property<T> property) {
        return property.getName(state.getValue(property));
    }

    @Nullable
    private static ResourceLocation modelOf(@Nullable JsonElement spec) {
        JsonElement model = spec instanceof JsonArray array ? (array.isEmpty() ? null : array.get(0)) : spec;
        if (!(model instanceof JsonObject object) || !(object.get("model") instanceof JsonPrimitive id)) return null;
        return modelId(id.getAsString());
    }

    private static void resolveReferences(JsonObject textures) {
        for (String key : new ArrayList<>(textures.keySet())) {
            JsonElement value = textures.get(key);
            for (int depth = 0; depth < textures.size() && isReference(value); depth++) {
                value = textures.get(value.getAsString().substring(1));
            }
            if (value == null || isReference(value)) {
                textures.remove(key);
            } else {
                textures.add(key, value);
            }
        }
    }

    private static boolean isReference(@Nullable JsonElement value) {
        return value instanceof JsonPrimitive primitive && primitive.isString() && primitive.getAsString().startsWith("#");
    }

    @Nullable
    private static ResourceLocation parentOf(JsonObject model) {
        return model.get("parent") instanceof JsonPrimitive parent ? modelId(parent.getAsString()) : null;
    }

    @Nullable
    private static JsonObject readModel(ResourceManager manager, ResourceLocation model) {
        return readJson(manager, model.withPrefix("models/").withSuffix(".json"));
    }

    @Nullable
    private static JsonObject readJson(ResourceManager manager, ResourceLocation file) {
        Optional<Resource> resource = manager.getResource(file);
        if (resource.isEmpty()) return null;
        try (Reader reader = resource.get().openAsReader()) {
            JsonElement json = JsonParser.parseReader(reader);
            return json.isJsonObject() ? json.getAsJsonObject() : null;
        } catch (Exception exception) {
            ClutterNoMore.LOGGER.warn("Failed to read {} from {}", file, resource.get().sourcePackId());
            return null;
        }
    }

    private static ResourceLocation modelId(String model) {
        String[] parts = model.split(":");
        return parts.length == 1 ? ClutterNoMore.id("minecraft", parts[0]) : ClutterNoMore.id(parts[0], parts[1]);
    }
}
