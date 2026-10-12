package dev.tazer.clutternomore.data;

import com.google.gson.JsonArray;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.block.StepBlock;
import dev.tazer.clutternomore.block.VerticalSlabBlock;
import dev.tazer.clutternomore.registry.ShapeBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public final class InheritedTags {

    private static final List<String> INHERITED_PREFIXES = List.of("mineable/", "needs_", "incorrect_for_");
    private static final ResourceLocation WOODEN_SLABS = ClutterNoMore.id("minecraft", "wooden_slabs");
    private static final ResourceLocation WOODEN_STAIRS = ClutterNoMore.id("minecraft", "wooden_stairs");
    private static final ResourceLocation WOODEN_VERTICAL_SLABS = ClutterNoMore.id("wooden_vertical_slabs");
    private static final ResourceLocation WOODEN_STEPS = ClutterNoMore.id("wooden_steps");

    private InheritedTags() {
    }

    public static void generate(ResourceManager manager) {
        Map<Block, Block> blocks = ShapeBlocks.getSources();
        Map<Item, Item> items = ShapeBlocks.getSourceItems();
        if (blocks.isEmpty()) return;
        TagFiles blockTags = new TagFiles(manager, RuntimeData.getBlockTagFolder());
        for (ResourceLocation tag : blockTags.listTags()) {
            if (!isInherited(tag)) {
                continue;
            }
            JsonArray shapes = shapesIn(blockTags.resolve(tag), blocks, BuiltInRegistries.BLOCK, block -> true);
            if (!shapes.isEmpty()) {
                write(RuntimeData.blockTagFile(tag), shapes);
            }
        }
        write(RuntimeData.blockTagFile(WOODEN_VERTICAL_SLABS), shapesIn(blockTags.resolve(WOODEN_SLABS), blocks, BuiltInRegistries.BLOCK, block -> block instanceof VerticalSlabBlock));
        write(RuntimeData.blockTagFile(WOODEN_STEPS), shapesIn(blockTags.resolve(WOODEN_STAIRS), blocks, BuiltInRegistries.BLOCK, block -> block instanceof StepBlock));
        TagFiles itemTags = new TagFiles(manager, RuntimeData.getItemTagFolder());
        write(RuntimeData.itemTagFile(WOODEN_VERTICAL_SLABS), shapesIn(itemTags.resolve(WOODEN_SLABS), items, BuiltInRegistries.ITEM, item -> item instanceof BlockItem blockItem && blockItem.getBlock() instanceof VerticalSlabBlock));
        write(RuntimeData.itemTagFile(WOODEN_STEPS), shapesIn(itemTags.resolve(WOODEN_STAIRS), items, BuiltInRegistries.ITEM, item -> item instanceof BlockItem blockItem && blockItem.getBlock() instanceof StepBlock));
    }

    private static boolean isInherited(ResourceLocation tag) {
        for (String prefix : INHERITED_PREFIXES) {
            if (tag.getPath().startsWith(prefix)) return true;
        }
        return false;
    }

    private static <T> JsonArray shapesIn(Set<ResourceLocation> members, Map<T, T> sources, Registry<T> registry, Predicate<T> filter) {
        JsonArray shapes = new JsonArray();
        for (Map.Entry<T, T> entry : sources.entrySet()) {
            if (filter.test(entry.getKey()) && members.contains(registry.getKey(entry.getValue()))) {
                shapes.add(String.valueOf(registry.getKey(entry.getKey())));
            }
        }
        return shapes;
    }

    private static void write(ResourceLocation file, JsonArray shapes) {
        ClutterNoMore.RESOURCES.addReloadedJson(PackType.SERVER_DATA, file, RuntimeData.tagFile(shapes));
    }
}
