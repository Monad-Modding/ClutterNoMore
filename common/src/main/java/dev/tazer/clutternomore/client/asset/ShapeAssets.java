package dev.tazer.clutternomore.client.asset;

import com.google.gson.JsonObject;
import dev.tazer.clutternomore.block.StepBlock;
import dev.tazer.clutternomore.block.VerticalSlabBlock;
import dev.tazer.clutternomore.block.VerticalStairBlock;
import dev.tazer.clutternomore.block.VerticalStepBlock;
import dev.tazer.clutternomore.registry.ShapeBlocks;
import dev.tazer.clutternomore.registry.ShapeType;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class ShapeAssets {

    private ShapeAssets() {
    }

    public static void generate(ResourceManager manager) {
        for (ShapeType type : ShapeType.values()) {
            for (Map.Entry<Item, Item> entry : ShapeBlocks.getSourceItems().entrySet()) {
                if (entry.getKey() instanceof BlockItem shape && ShapeBlocks.getType(shape.getBlock()) == type && entry.getValue() instanceof BlockItem source) {
                    ResourceLocation id = BuiltInRegistries.ITEM.getKey(shape);
                    generateBlock(type, source.getBlock(), id, manager);
                    AssetGenerator.generateItem(BuiltInRegistries.ITEM.getKey(source), id, manager);
                }
            }
        }
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
        String modelId = "clutternomore:block/" + shape.getPath().replace("waxed_", "");
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
