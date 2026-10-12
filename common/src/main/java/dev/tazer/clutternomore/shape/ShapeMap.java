package dev.tazer.clutternomore.shape;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.network.ClutterNoMoreNetwork;
import dev.tazer.clutternomore.network.ShapeMapSync;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
//? if >=26 {
/*import dev.tazer.clutternomore.compat.RrvCompat;
import dev.tazer.clutternomore.platform.Services;
import net.minecraft.world.item.ItemStackTemplate;
*///?}
//? if <1.20.5 {
/*import net.minecraft.nbt.CompoundTag;
*///?}

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
//? if <1.20.5 {
/*import java.util.Objects;
*///?}

public final class ShapeMap {

    private static volatile Snapshot snapshot = new Snapshot(Map.of(), Map.of());

    private ShapeMap() {
    }

    public static void setShapeMaps(Map<Item, List<Item>> shapes, Map<Item, Item> parents) {
        Map<Item, List<Item>> copied = new HashMap<>();
        for (Map.Entry<Item, List<Item>> entry : shapes.entrySet()) {
            copied.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        snapshot = new Snapshot(Collections.unmodifiableMap(copied), Map.copyOf(parents));
    }

    public static Map<Item, List<Item>> getShapesByParent() {
        return snapshot.shapesByParent();
    }

    public static Map<Item, Item> getParentByShape() {
        return snapshot.parentByShape();
    }

    public static boolean hasShapes(Item item) {
        return snapshot.shapesByParent().containsKey(item);
    }

    //? if >=26 {
    /*public static boolean isShape(ItemStackTemplate stack) {
        return snapshot.parentByShape().containsKey(stack.item().value());
    }
    *///?}

    public static boolean isShape(ItemStack stack) {
        return snapshot.parentByShape().containsKey(stack.getItem());
    }

    public static boolean isShape(Item item) {
        return snapshot.parentByShape().containsKey(item);
    }

    public static boolean contains(Item item) {
        return hasShapes(item) || isShape(item);
    }

    public static Item getParent(Item item) {
        return snapshot.parentByShape().getOrDefault(item, item);
    }

    public static boolean inSameShapeSet(Item item, Item other) {
        if (!isShape(item) && !isShape(other)) return false;
        return getParent(item) == getParent(other);
    }

    public static List<Item> getShapes(Item item) {
        Snapshot current = snapshot;
        return current.shapesByParent().getOrDefault(current.parentByShape().getOrDefault(item, item), List.of());
    }

    public static List<String> getSearchAliases(Item item) {
        List<Item> shapes = snapshot.shapesByParent().get(item);
        if (shapes == null || shapes.isEmpty()) return List.of();
        List<String> aliases = new ArrayList<>(shapes.size());
        for (Item shape : shapes) {
            aliases.add(Component.translatable(shape.getDescriptionId()).getString());
        }
        return aliases;
    }

    public static int getShapeIndex(ItemStack stack) {
        return getShapes(stack.getItem()).indexOf(stack.getItem());
    }

    public static ItemStack transferStack(ItemStack stack, int index) {
        List<Item> shapes = getShapes(stack.getItem());
        if (index < 0 || index >= shapes.size()) return stack.copy();
        Item target = shapes.get(index);
        //? if >=1.20.5 {
        return stack.transmuteCopy(target, stack.getCount());
        //?} else {
        /*ItemStack result = new ItemStack(target, stack.getCount());
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            result.setTag(tag.copy());
        }
        return result;
        *///?}
    }

    public static boolean isUnchanged(ItemStack before, ItemStack after) {
        if (after.getItem() != before.getItem()) return false;
        //? if >=1.20.5 {
        return after.getComponentsPatch().equals(before.getComponentsPatch());
        //?} else {
        /*return Objects.equals(after.getTag(), before.getTag());
        *///?}
    }

    public static void sendShapeMap(@Nullable ServerPlayer player) {
        if (player == null) return;
        ClutterNoMoreNetwork.sendToPlayer(player, ClutterNoMoreNetwork.SHAPE_MAP, ShapeMapSync.capture());
    }

    public static void setMappings(List<Mapping> mappings, boolean detailedLogs) {
        Map<Item, List<Item>> shapesByParent = new HashMap<>();
        Map<Item, Item> parentByShape = new HashMap<>();
        Map<Item, Item> roots = new HashMap<>();
        Set<Item> nodes = new LinkedHashSet<>();
        for (Mapping mapping : mappings) {
            nodes.add(mapping.parent());
            nodes.add(mapping.shape());
            union(roots, mapping.parent(), mapping.shape());
        }
        Map<Item, List<Item>> sets = new LinkedHashMap<>();
        for (Item item : nodes) {
            sets.computeIfAbsent(find(roots, item), key -> new ArrayList<>()).add(item);
        }
        Map<Item, List<Mapping>> mappingsByGroup = new HashMap<>();
        for (Mapping mapping : mappings) {
            mappingsByGroup.computeIfAbsent(find(roots, mapping.parent()), key -> new ArrayList<>()).add(mapping);
        }
        for (Map.Entry<Item, List<Item>> entry : sets.entrySet()) {
            List<Item> members = entry.getValue();
            if (members.size() < 2) {
                continue;
            }
            List<Mapping> groupMappings = mappingsByGroup.getOrDefault(entry.getKey(), List.of());
            Item parent = pickParent(groupMappings);
            if (parent == null || !members.contains(parent)) {
                parent = lexicographicallyFirst(members);
            }
            List<Item> shapes = order(parent, members, groupMappings);
            for (Item member : shapes) {
                if (member == parent) {
                    continue;
                }
                parentByShape.put(member, parent);
                //? if >=26 {
                /*if (Services.PLATFORM.isModLoaded("rrv") && Services.PLATFORM.isClient()) {
                    RrvCompat.hide(member);
                }
                *///?}
            }
            shapesByParent.put(parent, shapes);
            if (detailedLogs && groupMappings.size() > members.size() - 1) {
                List<ResourceLocation> ids = new ArrayList<>(members.size());
                for (Item member : members) {
                    ids.add(BuiltInRegistries.ITEM.getKey(member));
                }
                ClutterNoMore.LOGGER.info("[ShapeMap] circular shape set resolved: parent={} shapes={}", BuiltInRegistries.ITEM.getKey(parent), ids);
            }
        }
        setShapeMaps(shapesByParent, parentByShape);
    }

    @Nullable
    private static Item pickParent(List<Mapping> mappings) {
        Set<Item> shapes = new HashSet<>();
        for (Mapping mapping : mappings) {
            shapes.add(mapping.shape());
        }
        Mapping best = null;
        Mapping bestRoot = null;
        for (Mapping mapping : mappings) {
            if (outranks(mapping, best)) {
                best = mapping;
            }
            if (!shapes.contains(mapping.parent()) && outranks(mapping, bestRoot)) {
                bestRoot = mapping;
            }
        }
        Mapping chosen = bestRoot != null ? bestRoot : best;
        return chosen == null ? null : chosen.parent();
    }

    private static boolean outranks(Mapping mapping, @Nullable Mapping best) {
        return best == null || mapping.priority() > best.priority() || mapping.priority() == best.priority() && mapping.source().compareTo(best.source()) < 0;
    }

    private static List<Item> order(Item parent, List<Item> members, List<Mapping> mappings) {
        List<Mapping> ranked = new ArrayList<>(mappings);
        ranked.sort(Comparator.comparingInt(Mapping::priority).reversed());
        Map<Item, List<Item>> children = new HashMap<>();
        for (Mapping mapping : ranked) {
            children.computeIfAbsent(mapping.parent(), key -> new ArrayList<>()).add(mapping.shape());
        }
        List<Item> ordered = new ArrayList<>(members.size());
        Set<Item> seen = new HashSet<>();
        ordered.add(parent);
        seen.add(parent);
        for (int i = 0; i < ordered.size(); i++) {
            for (Item child : children.getOrDefault(ordered.get(i), List.of())) {
                if (seen.add(child)) {
                    ordered.add(child);
                }
            }
        }
        for (Item member : members) {
            if (seen.add(member)) {
                ordered.add(member);
            }
        }
        return ordered;
    }

    private static Item lexicographicallyFirst(List<Item> items) {
        Item first = items.get(0);
        ResourceLocation firstId = BuiltInRegistries.ITEM.getKey(first);
        for (int i = 1; i < items.size(); i++) {
            Item candidate = items.get(i);
            ResourceLocation candidateId = BuiltInRegistries.ITEM.getKey(candidate);
            if (candidateId.compareTo(firstId) < 0) {
                first = candidate;
                firstId = candidateId;
            }
        }
        return first;
    }

    private static <T> T find(Map<T, T> roots, T value) {
        T root = roots.getOrDefault(value, value);
        if (root.equals(value)) return value;
        T found = find(roots, root);
        roots.put(value, found);
        return found;
    }

    private static <T> void union(Map<T, T> roots, T first, T second) {
        T firstRoot = find(roots, first);
        T secondRoot = find(roots, second);
        if (!firstRoot.equals(secondRoot)) {
            roots.put(firstRoot, secondRoot);
        }
    }

    public record Mapping(Item parent, Item shape, int priority, ResourceLocation source) {
    }

    private record Snapshot(Map<Item, List<Item>> shapesByParent, Map<Item, Item> parentByShape) {
    }
}
