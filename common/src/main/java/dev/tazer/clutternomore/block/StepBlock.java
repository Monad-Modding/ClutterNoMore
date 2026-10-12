package dev.tazer.clutternomore.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
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
*///?} else {
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
//?}

import java.util.EnumMap;
import java.util.Map;

public class StepBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {

    //? if >=1.20.5 && <26.3 {
    public static final MapCodec<? extends StepBlock> CODEC = simpleCodec(StepBlock::new);
    //?}
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<SlabType> SLAB_TYPE = BlockStateProperties.SLAB_TYPE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final Map<Direction, VoxelShape> BOTTOM_SHAPES = createShapes(0);
    private static final Map<Direction, VoxelShape> TOP_SHAPES = createShapes(0.5);
    private static final Map<Direction, VoxelShape> DOUBLE_SHAPES = createDoubleShapes();

    public StepBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SLAB_TYPE, SlabType.BOTTOM).setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    public static VoxelShape createShape(Direction direction, double y) {
        return switch (direction) {
            case NORTH -> Shapes.create(0, y, 0, 1, y + 0.5, 0.5);
            case EAST -> Shapes.create(0.5, y, 0, 1, y + 0.5, 1);
            case SOUTH -> Shapes.create(0, y, 0.5, 1, y + 0.5, 1);
            case WEST -> Shapes.create(0, y, 0, 0.5, y + 0.5, 1);
            default -> Shapes.block();
        };
    }

    private static Map<Direction, VoxelShape> createShapes(double y) {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            shapes.put(direction, createShape(direction, y));
        }
        return shapes;
    }

    private static Map<Direction, VoxelShape> createDoubleShapes() {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            shapes.put(direction, Shapes.or(createShape(direction, 0.5), createShape(direction.getOpposite(), 0)));
        }
        return shapes;
    }

    //? if >=1.20.5 && <26.3 {
    @Override
    protected MapCodec<? extends StepBlock> codec() {
        return CODEC;
    }
    //?}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SLAB_TYPE).add(FACING).add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Vec3 click = context.getClickLocation();
        BlockState replacing = context.getLevel().getBlockState(pos);
        if (replacing.is(this)) {
            if (replacing.getValue(SLAB_TYPE) == SlabType.BOTTOM) {
                replacing = replacing.setValue(FACING, replacing.getValue(FACING).getOpposite());
            }
            return replacing.setValue(SLAB_TYPE, SlabType.DOUBLE);
        }
        BlockState state = this.defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(pos).getType() == Fluids.WATER);
        if (click.y - pos.getY() > 0.5) {
            state = state.setValue(SLAB_TYPE, SlabType.TOP);
        }
        return state.setValue(FACING, facing(context.getHorizontalDirection(), click, pos));
    }

    private static Direction facing(Direction direction, Vec3 click, BlockPos pos) {
        boolean flip = switch (direction) {
            case NORTH -> click.z - pos.getZ() > 0.5;
            case SOUTH -> click.z - pos.getZ() < 0.5;
            case EAST -> click.x - pos.getX() < 0.5;
            case WEST -> click.x - pos.getX() > 0.5;
            default -> false;
        };
        return flip ? direction.getOpposite() : direction;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        if (state.getValue(SLAB_TYPE) == SlabType.DOUBLE || !stack.is(this.asItem())) return false;
        if (!context.replacingClickedOnBlock()) return true;
        Direction face = context.getClickedFace();
        return face == (state.getValue(SLAB_TYPE) == SlabType.BOTTOM ? Direction.UP : Direction.DOWN) || face == state.getValue(FACING).getOpposite();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return switch (state.getValue(SLAB_TYPE)) {
            case TOP -> TOP_SHAPES.get(direction);
            case DOUBLE -> DOUBLE_SHAPES.get(direction);
            default -> BOTTOM_SHAPES.get(direction);
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
