package dev.tazer.clutternomore.forge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.tazer.clutternomore.registry.ShapeBlocks;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(HoneycombItem.class)
public class HoneycombItemMixin {

    @ModifyReturnValue(method = "getWaxed", at = @At("RETURN"))
    private static Optional<BlockState> cnm$waxShapes(Optional<BlockState> original, BlockState state) {
        if (original.isPresent()) return original;
        Block waxed = ShapeBlocks.getWaxed(state.getBlock());
        return waxed == null ? original : Optional.of(waxed.withPropertiesOf(state));
    }
}
