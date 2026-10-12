package dev.tazer.clutternomore.client.asset;

import com.google.gson.JsonObject;
import dev.tazer.clutternomore.block.VerticalSlabBlock;
import dev.tazer.clutternomore.registry.ShapeBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;
import java.util.Map;

public final class VerticalSlabAssets {

    private VerticalSlabAssets() {
    }

    public static void generate(ResourceManager manager) {
        for (Map.Entry<Item, Item> entry : ShapeBlocks.getSourceItems().entrySet()) {
            if (entry.getKey() instanceof BlockItem shape && shape.getBlock() instanceof VerticalSlabBlock && entry.getValue() instanceof BlockItem source) {
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(shape);
                generateBlock(source.getBlock(), id, manager);
                AssetGenerator.generateItem(BuiltInRegistries.ITEM.getKey(source), id, manager);
            }
        }
    }

    private static void generateBlock(Block source, ResourceLocation shape, ResourceManager manager) {
        if (manager.getResource(shape.withPrefix("blockstates/").withSuffix(".json")).isPresent()) return;
        ResourceLocation sourceModel = AssetGenerator.findModel(manager, source.defaultBlockState());
        if (sourceModel == null) return;
        JsonObject textures = AssetGenerator.getTextures(manager, sourceModel);
        if (textures == null) return;
        boolean thatch = textures.has("is_thatch");
        textures.remove("is_thatch");
        String templates = "clutternomore:block/templates/" + (thatch ? "thatch/" : "");
        String suffix = AssetGenerator.hasTint(manager, sourceModel) ? "_tinted" : "";
        JsonObject model = new JsonObject();
        String renderType = AssetGenerator.getRenderType(manager, sourceModel);
        if (renderType != null) {
            model.addProperty("render_type", renderType);
        }
        model.addProperty("parent", templates + "vertical_slab" + suffix);
        model.add("textures", textures);
        AssetGenerator.write("models/block/%s.json".formatted(shape.getPath()), model);
        model.addProperty("parent", templates + "vertical_slab_double" + suffix);
        AssetGenerator.write("models/block/%s_double.json".formatted(shape.getPath()), model);
        String modelId = "clutternomore:block/" + shape.getPath().replace("waxed_", "");
        JsonObject variants = new JsonObject();
        List<Property.Value<Direction>> facings = VerticalSlabBlock.FACING.getAllValues().toList();
        List<Property.Value<Boolean>> doubles = VerticalSlabBlock.DOUBLE.getAllValues().toList();
        for (Property.Value<Direction> facing : facings) {
            for (Property.Value<Boolean> isDouble : doubles) {
                JsonObject variant = new JsonObject();
                variant.addProperty("uvlock", true);
                if (isDouble.value()) {
                    variant.addProperty("model", modelId + "_double");
                } else {
                    variant.addProperty("model", modelId);
                    int rotation = rotation(facing.value());
                    if (rotation != 0) {
                        variant.addProperty("y", rotation);
                    }
                }
                variants.add(facing + "," + isDouble, variant);
            }
        }
        JsonObject blockState = new JsonObject();
        blockState.add("variants", variants);
        AssetGenerator.write("blockstates/%s.json".formatted(shape.getPath()), blockState);
    }

    public static int rotation(Direction facing) {
        return switch (facing) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }
}
