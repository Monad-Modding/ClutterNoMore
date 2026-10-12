package dev.tazer.clutternomore.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
//? if >=1.20.5 {
import org.jetbrains.annotations.Nullable;
//?}
//? if >=1.20.5 && <26.3 {
import com.mojang.serialization.MapCodec;
//?}
//? if >=1.21.2 {
/*import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.properties.EnumProperty;
*///?} else {
import net.minecraft.world.level.block.state.properties.DirectionProperty;
//?}
//? if >=1.20.5 && <1.21.2 {
import net.minecraft.world.entity.player.Player;
//?}

public class VerticalSlabBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {

    //? if >=1.20.5 && <26.3 {
    public static final MapCodec<? extends VerticalSlabBlock> CODEC = simpleCodec(VerticalSlabBlock::new);
    //?}
    public static final BooleanProperty DOUBLE = BooleanProperty.create("double");
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape NORTH_SHAPE = Shapes.create(0, 0, 0, 1, 1, 0.5);
    private static final VoxelShape EAST_SHAPE = Shapes.create(0.5, 0, 0, 1, 1, 1);
    private static final VoxelShape SOUTH_SHAPE = Shapes.create(0, 0, 0.5, 1, 1, 1);
    private static final VoxelShape WEST_SHAPE = Shapes.create(0, 0, 0, 0.5, 1, 1);

    public VerticalSlabBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DOUBLE, false).setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    //? if >=1.20.5 && <26.3 {
    @Override
    protected MapCodec<? extends VerticalSlabBlock> codec() {
        return CODEC;
    }
    //?}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DOUBLE).add(FACING).add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Vec3 click = context.getClickLocation();
        BlockState replacing = context.getLevel().getBlockState(pos);
        if (replacing.is(this)) return replacing.setValue(DOUBLE, true).setValue(WATERLOGGED, false);
        BlockState state = this.defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(pos).getType() == Fluids.WATER);
        Direction direction = context.getHorizontalDirection();
        boolean flip = switch (direction) {
            case NORTH -> click.z - pos.getZ() > 0.5;
            case SOUTH -> click.z - pos.getZ() < 0.5;
            case EAST -> click.x - pos.getX() < 0.5;
            case WEST -> click.x - pos.getX() > 0.5;
            default -> false;
        };
        return state.setValue(FACING, flip ? direction.getOpposite() : direction);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        if (state.getValue(DOUBLE) || !stack.is(this.asItem())) return false;
        if (!context.replacingClickedOnBlock()) return true;
        double x = context.getClickLocation().x - context.getClickedPos().getX();
        double z = context.getClickLocation().z - context.getClickedPos().getZ();
        return switch (state.getValue(FACING)) {
            case NORTH -> z >= 0.5;
            case EAST -> x <= 0.5;
            case SOUTH -> z <= 0.5;
            case WEST -> x >= 0.5;
            default -> false;
        };
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(DOUBLE)) return Shapes.block();
        return switch (state.getValue(FACING)) {
            case NORTH -> NORTH_SHAPE;
            case EAST -> EAST_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> Shapes.block();
        };
    }

    //? if >=1.21.2 {
    /*@Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }
    *///?} else if >=1.20.5 {
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }
    //?} else {
    /*@Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }
    *///?}

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return !state.getValue(DOUBLE);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.getValue(WATERLOGGED)) return Fluids.WATER.getSource(false);
        return state.getFluidState();
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (state.getValue(DOUBLE)) return false;
        return SimpleWaterloggedBlock.super.placeLiquid(level, pos, state, fluidState);
    }

    //? if >=1.21.2 {
    /*@Override
    public boolean canPlaceLiquid(@Nullable LivingEntity entity, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        if (state.getValue(DOUBLE)) return false;
        return SimpleWaterloggedBlock.super.canPlaceLiquid(entity, level, pos, state, fluid);
    }
    *///?} else if >=1.20.5 {
    @Override
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        if (state.getValue(DOUBLE)) return false;
        return SimpleWaterloggedBlock.super.canPlaceLiquid(player, level, pos, state, fluid);
    }
    //?} else {
    /*@Override
    public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        if (state.getValue(DOUBLE)) return false;
        return SimpleWaterloggedBlock.super.canPlaceLiquid(level, pos, state, fluid);
    }
    *///?}

    @Override
    //? if >=1.20.5 {
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
    //?} else {
    /*public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
    *///?}
        return type == PathComputationType.WATER && state.getFluidState().is(FluidTags.WATER);
    }
}
