package dev.tazer.clutternomore.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
//? if >=1.20.5 && <26.3 {
import com.mojang.serialization.MapCodec;
//?}
//? if >=1.21.2 {
/*import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.properties.EnumProperty;
*///?} else {
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
//?}

import java.util.EnumMap;
import java.util.Map;

public class VerticalStepBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {

    //? if >=1.20.5 && <26.3 {
    public static final MapCodec<? extends VerticalStepBlock> CODEC = simpleCodec(VerticalStepBlock::new);
    //?}
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty DOUBLE = VerticalSlabBlock.DOUBLE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final Map<Direction, VoxelShape> SINGLE_SHAPES = createShapes(false);
    private static final Map<Direction, VoxelShape> DOUBLE_SHAPES = createShapes(true);

    public VerticalStepBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DOUBLE, false).setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    public static VoxelShape createShape(Direction direction) {
        return switch (direction) {
            case NORTH -> Shapes.create(0.5, 0, 0, 1, 1, 0.5);
            case EAST -> Shapes.create(0.5, 0, 0.5, 1, 1, 1);
            case SOUTH -> Shapes.create(0, 0, 0.5, 0.5, 1, 1);
            case WEST -> Shapes.create(0, 0, 0, 0.5, 1, 0.5);
            default -> Shapes.block();
        };
    }

    public static Direction corner(boolean east, boolean south) {
        if (south) return east ? Direction.EAST : Direction.SOUTH;
        return east ? Direction.NORTH : Direction.WEST;
    }

    private static Map<Direction, VoxelShape> createShapes(boolean doubled) {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            shapes.put(direction, doubled ? Shapes.or(createShape(direction), createShape(direction.getOpposite())) : createShape(direction));
        }
        return shapes;
    }

    //? if >=1.20.5 && <26.3 {
    @Override
    protected MapCodec<? extends VerticalStepBlock> codec() {
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
        if (replacing.is(this)) return replacing.setValue(DOUBLE, true);
        BlockState state = this.defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(pos).getType() == Fluids.WATER);
        return state.setValue(FACING, corner(click.x - pos.getX() > 0.5, click.z - pos.getZ() > 0.5));
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        if (state.getValue(DOUBLE) || !stack.is(this.asItem())) return false;
        if (!context.replacingClickedOnBlock()) return true;
        Direction face = context.getClickedFace();
        Direction facing = state.getValue(FACING);
        return face == facing.getOpposite() || face == facing.getCounterClockWise();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return (state.getValue(DOUBLE) ? DOUBLE_SHAPES : SINGLE_SHAPES).get(state.getValue(FACING));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE) return state;
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)).getCounterClockWise());
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
        return true;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.getValue(WATERLOGGED)) return Fluids.WATER.getSource(false);
        return state.getFluidState();
    }

    @Override
    //? if >=1.20.5 {
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
    //?} else {
    /*public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
    *///?}
        return type == PathComputationType.WATER && state.getFluidState().is(FluidTags.WATER);
    }
}
