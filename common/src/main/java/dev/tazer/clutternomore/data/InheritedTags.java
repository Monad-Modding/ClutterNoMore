package dev.tazer.clutternomore.data;

import com.google.gson.JsonArray;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.registry.ShapeBlocks;
import dev.tazer.clutternomore.registry.ShapeType;
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
        for (ShapeType type : ShapeType.values()) {
            write(RuntimeData.blockTagFile(type.getWoodenTag()), shapesIn(blockTags.resolve(type.getWoodenSource()), blocks, BuiltInRegistries.BLOCK, block -> ShapeBlocks.getType(block) == type));
        }
        TagFiles itemTags = new TagFiles(manager, RuntimeData.getItemTagFolder());
        for (ShapeType type : ShapeType.values()) {
            write(RuntimeData.itemTagFile(type.getWoodenTag()), shapesIn(itemTags.resolve(type.getWoodenSource()), items, BuiltInRegistries.ITEM, item -> item instanceof BlockItem blockItem && ShapeBlocks.getType(blockItem.getBlock()) == type));
        }
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
