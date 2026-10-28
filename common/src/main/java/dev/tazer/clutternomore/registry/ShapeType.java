package dev.tazer.clutternomore.registry;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreConfig;
import dev.tazer.clutternomore.block.StepBlock;
import dev.tazer.clutternomore.block.TransparentShapes;
import dev.tazer.clutternomore.block.TransparentStepBlock;
import dev.tazer.clutternomore.block.TransparentVerticalSlabBlock;
import dev.tazer.clutternomore.block.TransparentVerticalStairBlock;
import dev.tazer.clutternomore.block.TransparentVerticalStepBlock;
import dev.tazer.clutternomore.block.VerticalSlabBlock;
import dev.tazer.clutternomore.block.VerticalStairBlock;
import dev.tazer.clutternomore.block.VerticalStepBlock;
import dev.tazer.clutternomore.block.WeatheringStepBlock;
import dev.tazer.clutternomore.block.WeatheringVerticalSlabBlock;
import dev.tazer.clutternomore.block.WeatheringVerticalStairBlock;
import dev.tazer.clutternomore.block.WeatheringVerticalStepBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public enum ShapeType {
    VERTICAL_SLAB("vertical_slabs", "wooden_vertical_slabs", "wooden_slabs"),
    STEP("steps", "wooden_steps", "wooden_stairs"),
    VERTICAL_STAIRS("vertical_stairs", "wooden_vertical_stairs", "wooden_stairs"),
    VERTICAL_STEP("vertical_steps", "wooden_vertical_steps", "wooden_stairs");

    private final String tag;
    private final ResourceLocation woodenTag;
    private final ResourceLocation woodenSource;

    ShapeType(String tag, String woodenTag, String woodenSource) {
        this.tag = tag;
        this.woodenTag = ClutterNoMore.id(woodenTag);
        this.woodenSource = ClutterNoMore.id("minecraft", woodenSource);
    }

    public String getTag() {
        return this.tag;
    }

    public ResourceLocation getWoodenTag() {
        return this.woodenTag;
    }

    public ResourceLocation getWoodenSource() {
        return this.woodenSource;
    }

    public boolean isEnabled(ClutterNoMoreConfig.Startup config) {
        return switch (this) {
            case VERTICAL_SLAB -> config.verticalSlabs.value();
            case STEP -> config.steps.value();
            case VERTICAL_STAIRS -> config.verticalStairs.value();
            case VERTICAL_STEP -> config.verticalSteps.value();
        };
    }

    public boolean accepts(Block block) {
        return switch (this) {
            case VERTICAL_SLAB -> block instanceof SlabBlock && propertyCount(block.defaultBlockState()) == 2;
            case STEP, VERTICAL_STAIRS, VERTICAL_STEP -> block instanceof StairBlock && propertyCount(block.defaultBlockState()) == 4;
        };
    }

    public ResourceLocation idOf(ResourceLocation source) {
        return switch (this) {
            case VERTICAL_SLAB -> ShapeBlocks.verticalSlabOf(source);
            case STEP -> ShapeBlocks.stepOf(source);
            case VERTICAL_STAIRS -> ShapeBlocks.verticalStairsOf(source);
            case VERTICAL_STEP -> ShapeBlocks.verticalStepOf(source);
        };
    }

    public Function<BlockBehaviour.Properties, Block> factory(Block source) {
        if (source instanceof WeatheringCopper weathering) {
            WeatheringCopper.WeatherState age = weathering.getAge();
            return switch (this) {
                case VERTICAL_SLAB -> properties -> new WeatheringVerticalSlabBlock(age, properties);
                case STEP -> properties -> new WeatheringStepBlock(age, properties);
                case VERTICAL_STAIRS -> properties -> new WeatheringVerticalStairBlock(age, properties);
                case VERTICAL_STEP -> properties -> new WeatheringVerticalStepBlock(age, properties);
            };
        }
        if (TransparentShapes.isGlassLike(source.defaultBlockState())) {
            return switch (this) {
                case VERTICAL_SLAB -> TransparentVerticalSlabBlock::new;
                case STEP -> TransparentStepBlock::new;
                case VERTICAL_STAIRS -> TransparentVerticalStairBlock::new;
                case VERTICAL_STEP -> TransparentVerticalStepBlock::new;
            };
        }
        return switch (this) {
            case VERTICAL_SLAB -> VerticalSlabBlock::new;
            case STEP -> StepBlock::new;
            case VERTICAL_STAIRS -> VerticalStairBlock::new;
            case VERTICAL_STEP -> VerticalStepBlock::new;
        };
    }

    private static int propertyCount(BlockState state) {
        //? if >=26 {
        /*return state.getValues().toList().size();
        *///?} else {
        return state.getValues().size();
        //?}
    }
}
