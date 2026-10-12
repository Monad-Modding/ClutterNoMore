package dev.tazer.clutternomore.shape;

import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
//? if >=26 {
/*import net.minecraft.world.item.ItemInstance;
*///?}

import java.util.List;

public final class ShapeHooks {

    private ShapeHooks() {
    }

    public static boolean denyItem(Item item) {
        return ShapeMap.isShape(item);
    }

    //? if >=26 {
    /*public static List<ItemStack> getDrops(List<ItemStack> original, BlockState state, ServerLevel level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemInstance tool) {
    *///?} else {
    public static List<ItemStack> getDrops(List<ItemStack> original, BlockState state, ServerLevel level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack tool) {
    //?}
        Item item = state.getBlock().asItem();
        if (!ShapeMap.isShape(item) || !ClutterNoMore.STARTUP_CONFIG.inheritLootTables.value()) return original;
        LootParams.Builder params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, tool).withOptionalParameter(LootContextParams.THIS_ENTITY, entity).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
        return Block.byItem(ShapeMap.getParent(item)).defaultBlockState().getDrops(params);
    }
}
