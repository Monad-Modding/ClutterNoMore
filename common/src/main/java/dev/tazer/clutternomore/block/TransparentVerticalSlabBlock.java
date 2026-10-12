package dev.tazer.clutternomore.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
//? if >=1.20.5 && <26.3 {
import com.mojang.serialization.MapCodec;
//?}

public class TransparentVerticalSlabBlock extends VerticalSlabBlock {

    //? if >=1.20.5 && <26.3 {
    public static final MapCodec<TransparentVerticalSlabBlock> CODEC = simpleCodec(TransparentVerticalSlabBlock::new);
    //?}

    public TransparentVerticalSlabBlock(Properties properties) {
        super(properties);
    }

    //? if >=1.20.5 && <26.3 {
    @Override
    protected MapCodec<? extends VerticalSlabBlock> codec() {
        return CODEC;
    }
    //?}

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    //? if >=26 {
    /*@Override
    public boolean propagatesSkylightDown(BlockState state) {
        return !state.getValue(WATERLOGGED);
    }
    *///?} else {
    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return !state.getValue(WATERLOGGED);
    }
    //?}

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacent, Direction direction) {
        return TransparentShapes.isCulledBy(state, adjacent, direction) || super.skipRendering(state, adjacent, direction);
    }
}
