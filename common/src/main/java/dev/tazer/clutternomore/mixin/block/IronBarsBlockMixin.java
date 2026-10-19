package dev.tazer.clutternomore.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tazer.clutternomore.block.VerticalSlabBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IronBarsBlock.class)
public abstract class IronBarsBlockMixin {

    @WrapOperation(method = {"getStateForPlacement", "updateShape"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isFaceSturdy(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"))
    private boolean cnm$touchesVerticalSlab(BlockState neighbor, BlockGetter level, BlockPos pos, Direction face, Operation<Boolean> original) {
        if (neighbor.getBlock() instanceof VerticalSlabBlock && !neighbor.getValue(VerticalSlabBlock.DOUBLE)) {
            return neighbor.getValue(VerticalSlabBlock.FACING) != face.getOpposite();
        }
        return original.call(neighbor, level, pos, face);
    }
}
