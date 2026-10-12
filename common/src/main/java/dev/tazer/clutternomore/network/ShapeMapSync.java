package dev.tazer.clutternomore.network;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.mixin.creative.CreativeModeTabsAccessor;
import dev.tazer.clutternomore.recipe.RecipeFixes;
import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record ShapeMapSync(Map<ResourceLocation, List<ResourceLocation>> shapes, Map<ResourceLocation, ResourceLocation> parents, boolean recipeFixes) {

    public static ShapeMapSync read(FriendlyByteBuf buf) {
        int shapeCount = buf.readVarInt();
        Map<ResourceLocation, List<ResourceLocation>> shapes = new HashMap<>(shapeCount);
        for (int i = 0; i < shapeCount; i++) {
            ResourceLocation parent = buf.readResourceLocation();
            shapes.put(parent, readIds(buf));
        }
        int parentCount = buf.readVarInt();
        Map<ResourceLocation, ResourceLocation> parents = new HashMap<>(parentCount);
        for (int i = 0; i < parentCount; i++) {
            ResourceLocation shape = buf.readResourceLocation();
            parents.put(shape, buf.readResourceLocation());
        }
        return new ShapeMapSync(shapes, parents, buf.readBoolean());
    }

    private static List<ResourceLocation> readIds(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        List<ResourceLocation> ids = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ids.add(buf.readResourceLocation());
        }
        return ids;
    }

    public static void write(FriendlyByteBuf buf, ShapeMapSync packet) {
        buf.writeVarInt(packet.shapes.size());
        for (Map.Entry<ResourceLocation, List<ResourceLocation>> entry : packet.shapes.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            buf.writeVarInt(entry.getValue().size());
            for (ResourceLocation id : entry.getValue()) {
                buf.writeResourceLocation(id);
            }
        }
        buf.writeVarInt(packet.parents.size());
        for (Map.Entry<ResourceLocation, ResourceLocation> entry : packet.parents.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            buf.writeResourceLocation(entry.getValue());
        }
        buf.writeBoolean(packet.recipeFixes);
    }

    public static ShapeMapSync capture() {
        Map<ResourceLocation, List<ResourceLocation>> shapes = new HashMap<>();
        for (Map.Entry<Item, List<Item>> entry : ShapeMap.getShapesByParent().entrySet()) {
            List<ResourceLocation> ids = new ArrayList<>(entry.getValue().size());
            for (Item shape : entry.getValue()) {
                ids.add(BuiltInRegistries.ITEM.getKey(shape));
            }
            shapes.put(BuiltInRegistries.ITEM.getKey(entry.getKey()), ids);
        }
        Map<ResourceLocation, ResourceLocation> parents = new HashMap<>();
        for (Map.Entry<Item, Item> entry : ShapeMap.getParentByShape().entrySet()) {
            parents.put(BuiltInRegistries.ITEM.getKey(entry.getKey()), BuiltInRegistries.ITEM.getKey(entry.getValue()));
        }
        return new ShapeMapSync(shapes, parents, ClutterNoMore.STARTUP_CONFIG.recipeFixes.value());
    }

    public static void apply(ShapeMapSync packet) {
        Map<Item, List<Item>> shapesByParent = new HashMap<>();
        for (Map.Entry<ResourceLocation, List<ResourceLocation>> entry : packet.shapes.entrySet()) {
            Optional<Item> parent = BuiltInRegistries.ITEM.getOptional(entry.getKey());
            if (parent.isPresent()) {
                List<Item> shapes = new ArrayList<>(entry.getValue().size());
                for (ResourceLocation id : entry.getValue()) {
                    BuiltInRegistries.ITEM.getOptional(id).ifPresent(shapes::add);
                }
                shapesByParent.put(parent.get(), shapes);
            }
        }
        Map<Item, Item> parentByShape = new HashMap<>();
        for (Map.Entry<ResourceLocation, ResourceLocation> entry : packet.parents.entrySet()) {
            Optional<Item> shape = BuiltInRegistries.ITEM.getOptional(entry.getKey());
            Optional<Item> parent = BuiltInRegistries.ITEM.getOptional(entry.getValue());
            if (shape.isPresent() && parent.isPresent()) {
                parentByShape.put(shape.get(), parent.get());
            }
        }
        ShapeMap.setShapeMaps(shapesByParent, parentByShape);
        RecipeFixes.setEnabled(packet.recipeFixes);
        CreativeModeTabsAccessor.cnm$setCachedParameters(null);
    }
}
