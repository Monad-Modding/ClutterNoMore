package dev.tazer.clutternomore.client.asset;

import com.google.gson.JsonObject;
import dev.tazer.clutternomore.block.StepBlock;
import dev.tazer.clutternomore.registry.ShapeBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.List;
import java.util.Map;

public final class StepAssets {

    private StepAssets() {
    }

    public static void generate(ResourceManager manager) {
        for (Map.Entry<Item, Item> entry : ShapeBlocks.getSourceItems().entrySet()) {
            if (entry.getKey() instanceof BlockItem shape && shape.getBlock() instanceof StepBlock && entry.getValue() instanceof BlockItem source) {
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
        model.addProperty("parent", templates + "step" + suffix);
        model.add("textures", textures);
        AssetGenerator.write("models/block/%s.json".formatted(shape.getPath()), model);
        model.addProperty("parent", templates + "step_double" + suffix);
        AssetGenerator.write("models/block/%s_double.json".formatted(shape.getPath()), model);
        model.addProperty("parent", templates + "step_top" + suffix);
        AssetGenerator.write("models/block/%s_top.json".formatted(shape.getPath()), model);
        String modelId = "clutternomore:block/" + shape.getPath().replace("waxed_", "");
        JsonObject variants = new JsonObject();
        List<Property.Value<Direction>> facings = StepBlock.FACING.getAllValues().toList();
        List<Property.Value<SlabType>> types = StepBlock.SLAB_TYPE.getAllValues().toList();
        for (Property.Value<Direction> facing : facings) {
            for (Property.Value<SlabType> type : types) {
                JsonObject variant = new JsonObject();
                variant.addProperty("uvlock", true);
                variant.addProperty("model", switch (type.value()) {
                    case DOUBLE -> modelId + "_double";
                    case TOP -> modelId + "_top";
                    default -> modelId;
                });
                int rotation = VerticalSlabAssets.rotation(facing.value());
                if (rotation != 0) {
                    variant.addProperty("y", rotation);
                }
                variants.add(facing + "," + type, variant);
            }
        }
        JsonObject blockState = new JsonObject();
        blockState.add("variants", variants);
        AssetGenerator.write("blockstates/%s.json".formatted(shape.getPath()), blockState);
    }
}
