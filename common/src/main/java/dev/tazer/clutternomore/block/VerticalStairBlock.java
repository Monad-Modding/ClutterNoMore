package dev.tazer.clutternomore.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
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

public class VerticalStairBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {

    //? if >=1.20.5 && <26.3 {
    public static final MapCodec<? extends VerticalStairBlock> CODEC = simpleCodec(VerticalStairBlock::new);
    //?}
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape[] SHAPES = createShapes();

    public VerticalStairBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(SHAPE, Shape.STRAIGHT).setValue(WATERLOGGED, false));
    }

    public static VoxelShape createShape(Direction facing, Shape shape) {
        return SHAPES[facing.get2DDataValue() * Shape.values().length + shape.ordinal()];
    }

    private static VoxelShape[] createShapes() {
        VoxelShape[] shapes = new VoxelShape[4 * Shape.values().length];
        BlockState stairs = Blocks.STONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            VoxelShape straight = standUp(stairs.setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.SHAPE, StairsShape.STRAIGHT).getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO), facing);
            for (Shape shape : Shape.values()) {
                VoxelShape result = straight;
                if (shape.getLeft() != null) {
                    result = Shapes.join(result, standUp(stairs.setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.SHAPE, shape.getLeft()).getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO), facing), BooleanOp.AND);
                }
                if (shape.getRight() != null) {
                    result = Shapes.join(result, standUp(stairs.setValue(StairBlock.HALF, Half.BOTTOM).setValue(StairBlock.SHAPE, shape.getRight()).getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO), facing.getClockWise()), BooleanOp.AND);
                }
                shapes[facing.get2DDataValue() * Shape.values().length + shape.ordinal()] = result;
            }
        }
        return shapes;
    }

    private static VoxelShape standUp(VoxelShape north, Direction facing) {
        VoxelShape result = Shapes.empty();
        for (AABB box : north.toAabbs()) {
            double minX = box.minY;
            double maxX = box.maxY;
            double minY = 1 - box.maxX;
            double maxY = 1 - box.minX;
            VoxelShape rotated = switch (facing) {
                case EAST -> Shapes.create(1 - box.maxZ, minY, minX, 1 - box.minZ, maxY, maxX);
                case SOUTH -> Shapes.create(1 - maxX, minY, 1 - box.maxZ, 1 - minX, maxY, 1 - box.minZ);
                case WEST -> Shapes.create(box.minZ, minY, 1 - maxX, box.maxZ, maxY, 1 - minX);
                default -> Shapes.create(minX, minY, box.minZ, maxX, maxY, box.maxZ);
            };
            result = Shapes.or(result, rotated);
        }
        return result;
    }

    private static Direction toWorld(Direction facing, Direction direction) {
        if (direction == Direction.UP) return facing.getClockWise();
        if (direction == Direction.DOWN) return facing.getCounterClockWise();
        if (direction == facing.getClockWise()) return Direction.DOWN;
        if (direction == facing.getCounterClockWise()) return Direction.UP;
        return direction;
    }

    private static Direction toFrame(Direction facing, Direction direction) {
        if (direction == facing.getClockWise()) return Direction.UP;
        if (direction == facing.getCounterClockWise()) return Direction.DOWN;
        if (direction == Direction.DOWN) return facing.getClockWise();
        if (direction == Direction.UP) return facing.getCounterClockWise();
        return direction;
    }

    @Nullable
    private static Stair inFrame(BlockState state, Direction facing) {
        Direction side;
        Direction vertical;
        if (state.getBlock() instanceof StairBlock) {
            side = state.getValue(StairBlock.FACING).getOpposite();
            vertical = state.getValue(StairBlock.HALF) == Half.BOTTOM ? Direction.UP : Direction.DOWN;
        } else if (state.getBlock() instanceof VerticalStairBlock) {
            side = state.getValue(FACING).getOpposite();
            vertical = state.getValue(FACING).getCounterClockWise();
        } else {
            return null;
        }
        Direction first = toFrame(facing, side);
        Direction second = toFrame(facing, vertical);
        if (first.getAxis() == Direction.Axis.Y && second.getAxis() != Direction.Axis.Y) return new Stair(second.getOpposite(), first == Direction.UP ? Half.BOTTOM : Half.TOP);
        if (second.getAxis() == Direction.Axis.Y && first.getAxis() != Direction.Axis.Y) return new Stair(first.getOpposite(), second == Direction.UP ? Half.BOTTOM : Half.TOP);
        return null;
    }

    private static Shape getStairsShape(BlockState state, BlockGetter level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        return Shape.of(getOuterShape(level, pos, facing, Half.TOP), getOuterShape(level, pos, facing.getClockWise(), Half.BOTTOM));
    }

    @Nullable
    private static StairsShape getOuterShape(BlockGetter level, BlockPos pos, Direction facing, Half half) {
        Stair front = inFrame(level.getBlockState(pos.relative(facing)), facing);
        if (front == null || front.half() != half) return null;
        Direction frontFacing = front.facing();
        if (frontFacing.getAxis() == facing.getAxis() || !canTakeShape(level, pos, facing, half, frontFacing.getOpposite())) return null;
        return frontFacing == facing.getCounterClockWise() ? StairsShape.OUTER_LEFT : StairsShape.OUTER_RIGHT;
    }

    private static boolean canTakeShape(BlockGetter level, BlockPos pos, Direction facing, Half half, Direction face) {
        Stair neighbour = inFrame(level.getBlockState(pos.relative(toWorld(facing, face))), facing);
        return neighbour == null || neighbour.facing() != facing || neighbour.half() != half;
    }

    //? if >=1.20.5 && <26.3 {
    @Override
    protected MapCodec<? extends VerticalStairBlock> codec() {
        return CODEC;
    }
    //?}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(SHAPE).add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Vec3 click = context.getClickLocation();
        Direction direction = context.getHorizontalDirection();
        Direction clockwise = direction.getClockWise();
        Direction face = context.getClickedFace();
        double offset = clockwise.getAxis() == Direction.Axis.X ? click.x - pos.getX() : click.z - pos.getZ();
        double lateral = clockwise.getAxisDirection() == Direction.AxisDirection.POSITIVE ? offset : 1 - offset;
        boolean bottom = face != direction.getCounterClockWise() && (face == clockwise || !(lateral > 0.5));
        BlockState state = this.defaultBlockState().setValue(FACING, bottom ? direction.getCounterClockWise() : direction).setValue(WATERLOGGED, context.getLevel().getFluidState(pos).getType() == Fluids.WATER);
        return state.setValue(SHAPE, getStairsShape(state, context.getLevel(), pos));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return createShape(state.getValue(FACING), state.getValue(SHAPE));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE) return state;
        VoxelShape mirrored = Shapes.empty();
        for (AABB box : this.getShape(state, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CollisionContext.empty()).toAabbs()) {
            mirrored = Shapes.or(mirrored, mirror == Mirror.LEFT_RIGHT ? Shapes.create(box.minX, box.minY, 1 - box.maxZ, box.maxX, box.maxY, 1 - box.minZ) : Shapes.create(1 - box.maxX, box.minY, box.minZ, 1 - box.minX, box.maxY, box.maxZ));
        }
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            for (Shape shape : Shape.values()) {
                if (!Shapes.joinIsNotEmpty(createShape(facing, shape), mirrored, BooleanOp.NOT_SAME)) return state.setValue(FACING, facing).setValue(SHAPE, shape);
            }
        }
        return state;
    }

    //? if >=1.21.2 {
    /*@Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random).setValue(SHAPE, getStairsShape(state, level, pos));
    }
    *///?} else if >=1.20.5 {
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos).setValue(SHAPE, getStairsShape(state, level, pos));
    }
    //?} else {
    /*@Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos).setValue(SHAPE, getStairsShape(state, level, pos));
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

    private record Stair(Direction facing, Half half) {
    }

    public enum Shape implements StringRepresentable {
        STRAIGHT("straight", null, null),
        OUTER_LEFT_TOP("outer_left_top", StairsShape.OUTER_LEFT, null),
        OUTER_LEFT_BOTTOM("outer_left_bottom", StairsShape.OUTER_RIGHT, null),
        OUTER_RIGHT_TOP("outer_right_top", null, StairsShape.OUTER_LEFT),
        OUTER_RIGHT_BOTTOM("outer_right_bottom", null, StairsShape.OUTER_RIGHT),
        OUTER_BOTH_TOP_TOP("outer_both_top_top", StairsShape.OUTER_LEFT, StairsShape.OUTER_LEFT),
        OUTER_BOTH_TOP_BOTTOM("outer_both_top_bottom", StairsShape.OUTER_LEFT, StairsShape.OUTER_RIGHT),
        OUTER_BOTH_BOTTOM_TOP("outer_both_bottom_top", StairsShape.OUTER_RIGHT, StairsShape.OUTER_LEFT),
        OUTER_BOTH_BOTTOM_BOTTOM("outer_both_bottom_bottom", StairsShape.OUTER_RIGHT, StairsShape.OUTER_RIGHT);

        private final String name;
        @Nullable
        private final StairsShape left;
        @Nullable
        private final StairsShape right;

        private Shape(String name, @Nullable StairsShape left, @Nullable StairsShape right) {
            this.name = name;
            this.left = left;
            this.right = right;
        }

        public static Shape of(@Nullable StairsShape left, @Nullable StairsShape right) {
            for (Shape shape : values()) {
                if (shape.left == left && shape.right == right) return shape;
            }
            return STRAIGHT;
        }

        @Nullable
        public StairsShape getLeft() {
            return this.left;
        }

        @Nullable
        public StairsShape getRight() {
            return this.right;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
