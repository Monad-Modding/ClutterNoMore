package dev.tazer.clutternomore.mixin.access;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FireBlock.class)
public interface FireBlockAccessor {

    @Invoker("setFlammable")
    void cnm$setFlammable(Block block, int igniteOdds, int burnOdds);

    @Invoker("getBurnOdds")
    int cnm$getBurnOdds(BlockState state);

    @Invoker("getIgniteOdds")
    int cnm$getIgniteOdds(BlockState state);
}
