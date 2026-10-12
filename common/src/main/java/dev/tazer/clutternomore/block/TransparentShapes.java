package dev.tazer.clutternomore.block;

import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TransparentShapes {

    public static final TagKey<Block> GLASS = TagKey.create(Registries.BLOCK, ClutterNoMore.id("glass"));
    private static final Map<BlockState, Boolean> GLASS_LIKE = new ConcurrentHashMap<>();

    private TransparentShapes() {
    }

    public static boolean isGlassLike(BlockState state) {
        return GLASS_LIKE.computeIfAbsent(state, TransparentShapes::computeGlassLike);
    }

    public static boolean isCulledBy(BlockState state, BlockState adjacent, Direction direction) {
        if (!adjacent.is(GLASS) && !isGlassLike(adjacent)) return false;
        return Shapes.blockOccudes(state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO), adjacent.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO), direction);
    }

    private static boolean computeGlassLike(BlockState state) {
        try {
            return !state.canOcclude() && state.getVisualShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CollisionContext.empty()).isEmpty() && !state.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).isEmpty();
        } catch (RuntimeException exception) {
            return false;
        }
    }
}
