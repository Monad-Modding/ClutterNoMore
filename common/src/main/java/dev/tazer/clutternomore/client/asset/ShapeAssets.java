package dev.tazer.clutternomore.client.asset;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.block.StepBlock;
import dev.tazer.clutternomore.block.VerticalSlabBlock;
import dev.tazer.clutternomore.block.VerticalStairBlock;
import dev.tazer.clutternomore.block.VerticalStepBlock;
import dev.tazer.clutternomore.registry.ShapeBlocks;
import dev.tazer.clutternomore.registry.ShapeType;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class ShapeAssets {

    private static final String ROTATEABLE_DIRECTORY = "rotateable_vertical_slabs";
    private static final String ROTATEABLE_TEMPLATE = "clutternomore:block/templates/rotateable_vertical_slab";
    private static final List<String> ROTATEABLE_PARTS = List.of("north", "east", "south", "west", "double_z", "double_x");

    private ShapeAssets() {
    }

    public static void generate(ResourceManager manager) {
        Map<ResourceLocation, JsonObject> rotateable = readRotateable(manager);
        for (ShapeType type : ShapeType.values()) {
            for (Map.Entry<Item, Item> entry : ShapeBlocks.getSourceItems().entrySet()) {
                if (entry.getKey() instanceof BlockItem shape && ShapeBlocks.getType(shape.getBlock()) == type && entry.getValue() instanceof BlockItem source) {
                    ResourceLocation id = BuiltInRegistries.ITEM.getKey(shape);
                    JsonObject settings = type == ShapeType.VERTICAL_SLAB ? rotateable.get(BuiltInRegistries.BLOCK.getKey(source.getBlock())) : null;
                    if (settings != null && generateRotateable(source.getBlock(), id, settings, manager)) {
                        AssetGenerator.generateItem(BuiltInRegistries.ITEM.getKey(source), id, AssetGenerator.modelId(id) + "_north", manager);
                        continue;
                    }
                    generateBlock(type, source.getBlock(), id, manager);
                    AssetGenerator.generateItem(BuiltInRegistries.ITEM.getKey(source), id, manager);
                }
            }
        }
    }

    private static Map<ResourceLocation, JsonObject> readRotateable(ResourceManager manager) {
        Map<ResourceLocation, JsonObject> entries = new LinkedHashMap<>();
        Map<ResourceLocation, List<Resource>> files = new TreeMap<>(manager.listResourceStacks(ROTATEABLE_DIRECTORY, id -> id.getPath().endsWith(".json")));
        for (Map.Entry<ResourceLocation, List<Resource>> file : files.entrySet()) {
            Map<ResourceLocation, JsonObject> values = new LinkedHashMap<>();
            for (Resource resource : file.getValue()) {
                try (Reader reader = resource.openAsReader()) {
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    if (json.get("replace") instanceof JsonPrimitive replace && replace.isBoolean() && replace.getAsBoolean()) {
                        values.clear();
                    }
                    if (json.get("values") instanceof JsonArray array) {
                        for (JsonElement value : array) {
                            readValue(value, values);
                        }
                    }
                } catch (Exception exception) {
                    ClutterNoMore.LOGGER.warn("Failed to read rotateable vertical slabs {} from {}", file.getKey(), resource.sourcePackId());
                }
            }
            entries.putAll(values);
        }
        return entries;
    }

    private static void readValue(JsonElement value, Map<ResourceLocation, JsonObject> values) {
        if (value instanceof JsonPrimitive primitive && primitive.isString()) {
            values.put(ClutterNoMore.parse(primitive.getAsString()), new JsonObject());
        } else if (value instanceof JsonObject object && object.get("id") instanceof JsonPrimitive id && id.isString()) {
            values.put(ClutterNoMore.parse(id.getAsString()), object);
        } else {
            ClutterNoMore.LOGGER.warn("Ignoring invalid rotateable vertical slab entry {}", value);
        }
    }

    private static boolean generateRotateable(Block source, ResourceLocation shape, JsonObject settings, ResourceManager manager) {
        if (manager.getResource(shape.withPrefix("blockstates/").withSuffix(".json")).isPresent()) return false;
        ResourceLocation sourceModel = AssetGenerator.findModel(manager, source.defaultBlockState());
        if (sourceModel == null) return false;
        JsonObject sourceTextures = AssetGenerator.getTextures(manager, sourceModel);
        if (sourceTextures == null || sourceTextures.has("is_thatch")) return false;
        JsonElement side = texture(settings, "side");
        boolean rotate = settings.get("rotate") instanceof JsonPrimitive primitive && primitive.isBoolean() ? primitive.getAsBoolean() : side == null;
        JsonObject textures = new JsonObject();
        textures.add("top", orDefault(texture(settings, "top"), sourceTextures.get("top")));
        textures.add("bottom", orDefault(texture(settings, "bottom"), sourceTextures.get("bottom")));
        textures.add("side", orDefault(side, sourceTextures.get("side")));
        if (!rotate) {
            textures.add("slab", orDefault(texture(settings, "slab"), sourceTextures.get("side")));
        }
        String template = ROTATEABLE_TEMPLATE + (rotate ? "_unrotated" : "");
        String suffix = AssetGenerator.hasTint(manager, sourceModel) ? "_tinted" : "";
        JsonObject model = new JsonObject();
        String renderType = AssetGenerator.getRenderType(manager, sourceModel);
        if (renderType != null) {
            model.addProperty("render_type", renderType);
        }
        model.addProperty("parent", template + "_" + ROTATEABLE_PARTS.get(0) + suffix);
        model.add("textures", textures);
        for (String part : ROTATEABLE_PARTS) {
            model.addProperty("parent", template + "_" + part + suffix);
            AssetGenerator.write("models/block/%s_%s.json".formatted(shape.getPath(), part), model);
        }
        String modelId = AssetGenerator.modelId(shape);
        JsonObject variants = new JsonObject();
        for (Property.Value<Direction> facing : VerticalSlabBlock.FACING.getAllValues().toList()) {
            for (Property.Value<Boolean> isDouble : VerticalSlabBlock.DOUBLE.getAllValues().toList()) {
                String part = isDouble.value() ? (facing.value().getAxis() == Direction.Axis.Z ? "double_z" : "double_x") : facing.value().getSerializedName();
                JsonObject variant = new JsonObject();
                variant.addProperty("model", modelId + "_" + part);
                variants.add(facing + "," + isDouble, variant);
            }
        }
        JsonObject blockState = new JsonObject();
        blockState.add("variants", variants);
        AssetGenerator.write("blockstates/%s.json".formatted(shape.getPath()), blockState);
        return true;
    }

    @Nullable
    private static JsonElement texture(JsonObject settings, String key) {
        return settings.get(key) instanceof JsonPrimitive primitive && primitive.isString() ? primitive : null;
    }

    private static JsonElement orDefault(@Nullable JsonElement value, JsonElement fallback) {
        return value != null ? value : fallback;
    }

    private static void generateBlock(ShapeType type, Block source, ResourceLocation shape, ResourceManager manager) {
        if (manager.getResource(shape.withPrefix("blockstates/").withSuffix(".json")).isPresent()) return;
        ResourceLocation sourceModel = AssetGenerator.findModel(manager, source.defaultBlockState());
        if (sourceModel == null) return;
        JsonObject textures = AssetGenerator.getTextures(manager, sourceModel);
        if (textures == null) return;
        boolean thatch = textures.has("is_thatch");
        textures.remove("is_thatch");
        String templates = "clutternomore:block/templates/" + (thatch ? "thatch/" : "");
        String suffix = AssetGenerator.hasTint(manager, sourceModel) ? "_tinted" : "";
        List<Model> models = getModels(type);
        JsonObject model = new JsonObject();
        String renderType = AssetGenerator.getRenderType(manager, sourceModel);
        if (renderType != null) {
            model.addProperty("render_type", renderType);
        }
        model.addProperty("parent", templates + models.get(0).template() + suffix);
        model.add("textures", textures);
        for (Model entry : models) {
            model.addProperty("parent", templates + entry.template() + suffix);
            AssetGenerator.write("models/block/%s%s.json".formatted(shape.getPath(), entry.suffix()), model);
        }
        String modelId = AssetGenerator.modelId(shape);
        JsonObject blockState = new JsonObject();
        blockState.add("variants", getVariants(type, modelId));
        AssetGenerator.write("blockstates/%s.json".formatted(shape.getPath()), blockState);
    }

    private static List<Model> getModels(ShapeType type) {
        return switch (type) {
            case VERTICAL_SLAB -> List.of(new Model("", "vertical_slab"), new Model("_double", "vertical_slab_double"));
            case STEP -> List.of(new Model("", "step"), new Model("_double", "step_double"), new Model("_top", "step_top"));
            case VERTICAL_STAIRS -> Arrays.stream(VerticalStairBlock.Shape.values()).map(shape -> new Model(stairSuffix(shape), "vertical_stairs" + stairSuffix(shape))).toList();
            case VERTICAL_STEP -> List.of(new Model("", "vertical_step"), new Model("_double", "vertical_step_double"));
        };
    }

    private static JsonObject getVariants(ShapeType type, String modelId) {
        JsonObject variants = new JsonObject();
        switch (type) {
            case VERTICAL_SLAB -> {
                for (Property.Value<Direction> facing : VerticalSlabBlock.FACING.getAllValues().toList()) {
                    for (Property.Value<Boolean> isDouble : VerticalSlabBlock.DOUBLE.getAllValues().toList()) {
                        variants.add(facing + "," + isDouble, variant(isDouble.value() ? modelId + "_double" : modelId, isDouble.value() ? 0 : rotation(facing.value())));
                    }
                }
            }
            case STEP -> {
                for (Property.Value<Direction> facing : StepBlock.FACING.getAllValues().toList()) {
                    for (Property.Value<SlabType> slabType : StepBlock.SLAB_TYPE.getAllValues().toList()) {
                        String model = switch (slabType.value()) {
                            case DOUBLE -> modelId + "_double";
                            case TOP -> modelId + "_top";
                            default -> modelId;
                        };
                        variants.add(facing + "," + slabType, variant(model, rotation(facing.value())));
                    }
                }
            }
            case VERTICAL_STAIRS -> {
                for (Property.Value<Direction> facing : VerticalStairBlock.FACING.getAllValues().toList()) {
                    for (Property.Value<VerticalStairBlock.Shape> shape : VerticalStairBlock.SHAPE.getAllValues().toList()) {
                        variants.add(facing + "," + shape, variant(modelId + stairSuffix(shape.value()), rotation(facing.value())));
                    }
                }
            }
            case VERTICAL_STEP -> {
                for (Property.Value<Direction> facing : VerticalStepBlock.FACING.getAllValues().toList()) {
                    for (Property.Value<Boolean> isDouble : VerticalStepBlock.DOUBLE.getAllValues().toList()) {
                        variants.add(facing + "," + isDouble, variant(isDouble.value() ? modelId + "_double" : modelId, rotation(facing.value())));
                    }
                }
            }
        }
        return variants;
    }

    private static String stairSuffix(VerticalStairBlock.Shape shape) {
        return shape == VerticalStairBlock.Shape.STRAIGHT ? "" : "_" + shape.getSerializedName();
    }

    private static JsonObject variant(String model, int rotation) {
        JsonObject variant = new JsonObject();
        variant.addProperty("uvlock", true);
        variant.addProperty("model", model);
        if (rotation != 0) {
            variant.addProperty("y", rotation);
        }
        return variant;
    }

    public static int rotation(Direction facing) {
        return switch (facing) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }

    private record Model(String suffix, String template) {
    }
}
