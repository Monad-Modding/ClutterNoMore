package dev.tazer.clutternomore.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.tazer.clutternomore.shape.ShapeHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//? if >=26 {
/*import net.minecraft.world.item.ItemInstance;
*///?}

import java.util.List;

@Mixin(Block.class)
public abstract class BlockMixin {

    @ModifyReturnValue(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)Ljava/util/List;", at = @At("RETURN"))
    private static List<ItemStack> cnm$getDrops(List<ItemStack> original, BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity) {
        return ShapeHooks.getDrops(original, state, level, pos, blockEntity, null, ItemStack.EMPTY);
    }

    //? if >=26 {
    /*@ModifyReturnValue(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemInstance;)Ljava/util/List;", at = @At("RETURN"))
    private static List<ItemStack> cnm$getDrops(List<ItemStack> original, BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity, Entity breaker, ItemInstance tool) {
        return ShapeHooks.getDrops(original, state, level, pos, blockEntity, breaker, tool);
    }
    *///?} else {
    @ModifyReturnValue(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", at = @At("RETURN"))
    private static List<ItemStack> cnm$getDrops(List<ItemStack> original, BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity, Entity breaker, ItemStack tool) {
        return ShapeHooks.getDrops(original, state, level, pos, blockEntity, breaker, tool);
    }
    //?}
}
