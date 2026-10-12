package dev.tazer.clutternomore.mixin.client;

import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >=26 {
/*import net.minecraft.client.color.block.BlockTintSource;

import java.util.List;
*///?} else {
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
//?}

@Mixin(BlockColors.class)
public class BlockColorsMixin {

    //? if >=26 {
    /*@Inject(method = "getTintSources", at = @At("HEAD"), cancellable = true)
    private void cnm$parentTintSources(BlockState state, CallbackInfoReturnable<List<BlockTintSource>> cir) {
        Item item = state.getBlock().asItem();
        if (ShapeMap.isShape(item) && ShapeMap.getParent(item) instanceof BlockItem parent) {
            List<BlockTintSource> sources = ((BlockColors) (Object) this).getTintSources(parent.getBlock().defaultBlockState());
            if (sources != null) {
                cir.setReturnValue(sources);
            }
        }
    }
    *///?} else {
    @Inject(method = "getColor(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;I)I", at = @At("HEAD"), cancellable = true)
    private void cnm$parentColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex, CallbackInfoReturnable<Integer> cir) {
        Item item = state.getBlock().asItem();
        if (ShapeMap.isShape(item) && ShapeMap.getParent(item) instanceof BlockItem parent) {
            int color = ((BlockColors) (Object) this).getColor(parent.getBlock().defaultBlockState(), level, pos, tintIndex);
            if (color != -1) {
                cir.setReturnValue(color);
            }
        }
    }
    //?}
}
