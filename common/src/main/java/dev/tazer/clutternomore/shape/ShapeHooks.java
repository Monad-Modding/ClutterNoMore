package dev.tazer.clutternomore.shape;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.block.VerticalSlabBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
//? if >=26 {
/*import net.minecraft.world.item.ItemInstance;
*///?}

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ShapeHooks {

    private ShapeHooks() {
    }

    public static boolean denyItem(Item item) {
        return ShapeMap.isShape(item);
    }

    public static boolean isDoubled(BlockState state) {
        if (state.getOptionalValue(SlabBlock.TYPE).orElse(null) == SlabType.DOUBLE) return true;
        return state.getOptionalValue(VerticalSlabBlock.DOUBLE).orElse(false);
    }

    //? if >=26 {
    /*public static List<ItemStack> getDrops(List<ItemStack> original, BlockState state, ServerLevel level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemInstance tool) {
    *///?} else {
    public static List<ItemStack> getDrops(List<ItemStack> original, BlockState state, ServerLevel level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack tool) {
    //?}
        if (!ClutterNoMore.STARTUP_CONFIG.inheritLootTables.value()) return original;
        Block block = state.getBlock();
        Map<Item, List<ItemStack>> shapes = new LinkedHashMap<>();
        List<ItemStack> drops = new ArrayList<>();
        for (ItemStack drop : original) {
            if (drop.getItem() instanceof BlockItem item && item.getBlock() == block && ShapeMap.isShape(item)) {
                shapes.computeIfAbsent(item, key -> new ArrayList<>()).add(drop);
            } else {
                drops.add(drop);
            }
        }
        LootParams.Builder params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, tool).withOptionalParameter(LootContextParams.THIS_ENTITY, entity).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
        if (shapes.isEmpty()) {
            Item item = block.asItem();
            if (!ShapeMap.isShape(item) || isUpperPart(state) || parentBlock(item) == Blocks.AIR) return original;
            return parentDrops(state, parentBlock(item), 1, params);
        }
        for (Map.Entry<Item, List<ItemStack>> shape : shapes.entrySet()) {
            Block parent = parentBlock(shape.getKey());
            if (parent == Blocks.AIR) {
                drops.addAll(shape.getValue());
                continue;
            }
            int count = 0;
            for (ItemStack stack : shape.getValue()) {
                count += stack.getCount();
            }
            drops.addAll(parentDrops(state, parent, isDoubled(state) ? 1 : count, params));
        }
        return drops;
    }

    private static Block parentBlock(Item shape) {
        return Block.byItem(ShapeMap.getParent(shape));
    }

    private static List<ItemStack> parentDrops(BlockState state, Block parent, int times, LootParams.Builder params) {
        BlockState parentState = parent.defaultBlockState();
        for (Property<?> property : state.getProperties()) {
            parentState = copyProperty(state, parentState, property);
        }
        List<ItemStack> drops = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            drops.addAll(parentState.getDrops(params));
        }
        return drops;
    }

    private static boolean isUpperPart(BlockState state) {
        for (Property<?> property : state.getProperties()) {
            String name = property.getName();
            if (name.equals("half") || name.equals("third")) {
                String value = valueName(state, property);
                if (value.equals("upper") || value.equals("middle")) return true;
            }
        }
        return false;
    }

    private static <T extends Comparable<T>> String valueName(BlockState state, Property<T> property) {
        return property.getName(state.getValue(property));
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState from, BlockState to, Property<T> property) {
        return to.hasProperty(property) ? to.setValue(property, from.getValue(property)) : to;
    }
}
