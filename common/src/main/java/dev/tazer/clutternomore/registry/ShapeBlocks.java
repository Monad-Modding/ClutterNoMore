package dev.tazer.clutternomore.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreConfig;
import dev.tazer.clutternomore.block.StepBlock;
import dev.tazer.clutternomore.block.TransparentShapes;
import dev.tazer.clutternomore.block.TransparentStepBlock;
import dev.tazer.clutternomore.block.TransparentVerticalSlabBlock;
import dev.tazer.clutternomore.block.VerticalSlabBlock;
import dev.tazer.clutternomore.block.WeatheringStepBlock;
import dev.tazer.clutternomore.block.WeatheringVerticalSlabBlock;
import dev.tazer.clutternomore.data.RuntimeData;
import dev.tazer.clutternomore.mixin.access.FireBlockAccessor;
import dev.tazer.clutternomore.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
//? if >=1.21.2 {
/*import net.minecraft.core.registries.Registries;
*///?}

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class ShapeBlocks {

    private static final BiMap<Block, Block> NEXT_OXIDATION = HashBiMap.create();
    private static final BiMap<Block, Block> WAXED_BLOCKS = HashBiMap.create();
    private static final List<String> SHAPE_PATHS = new ArrayList<>();
    private static final Map<Block, Block> SOURCES = new LinkedHashMap<>();
    private static final Map<Block, Block> SHAPES = new LinkedHashMap<>();
    private static final Map<Item, Item> SOURCE_ITEMS = new LinkedHashMap<>();

    private ShapeBlocks() {
    }

    public static Map<Block, Block> getOxidationStages() {
        return Collections.unmodifiableMap(NEXT_OXIDATION);
    }

    public static Map<Block, Block> getWaxedBlocks() {
        return Collections.unmodifiableMap(WAXED_BLOCKS);
    }

    @Nullable
    public static Block getNextOxidation(Block block) {
        return NEXT_OXIDATION.get(block);
    }

    @Nullable
    public static Block getPreviousOxidation(Block block) {
        return NEXT_OXIDATION.inverse().get(block);
    }

    @Nullable
    public static Block getWaxed(Block block) {
        return WAXED_BLOCKS.get(block);
    }

    @Nullable
    public static Block getUnwaxed(Block block) {
        return WAXED_BLOCKS.inverse().get(block);
    }

    public static List<String> getShapePaths() {
        return Collections.unmodifiableList(SHAPE_PATHS);
    }

    public static Map<Block, Block> getSources() {
        return Collections.unmodifiableMap(SOURCES);
    }

    public static Map<Item, Item> getSourceItems() {
        return Collections.unmodifiableMap(SOURCE_ITEMS);
    }

    @Nullable
    public static Item getSourceItem(Item shape) {
        return SOURCE_ITEMS.get(shape);
    }

    @Nullable
    public static Block getShape(Block source) {
        return SHAPES.get(source);
    }

    public static void applyFlammability() {
        FireBlockAccessor fire = (FireBlockAccessor) Blocks.FIRE;
        for (Map.Entry<Block, Block> entry : SOURCES.entrySet()) {
            BlockState source = entry.getValue().defaultBlockState();
            BlockState shape = entry.getKey().defaultBlockState();
            int burnOdds = Services.PLATFORM.getFlammability(source);
            int igniteOdds = Services.PLATFORM.getFireSpreadSpeed(source);
            if (burnOdds > 0 || igniteOdds > 0 || fire.cnm$getBurnOdds(shape) > 0 || fire.cnm$getIgniteOdds(shape) > 0) {
                fire.cnm$setFlammable(entry.getKey(), igniteOdds, burnOdds);
            }
        }
    }

    public static void inheritFuel(Map<Item, Integer> fuel) {
        for (Map.Entry<Item, Item> entry : SOURCE_ITEMS.entrySet()) {
            Integer burnTime = fuel.get(entry.getValue());
            if (burnTime != null && !fuel.containsKey(entry.getKey())) {
                fuel.put(entry.getKey(), burnTime);
            }
        }
    }

    public static String shapePrefix(ResourceLocation parent) {
        return parent.getNamespace().equals("minecraft") ? "" : parent.getNamespace() + "/";
    }

    public static ResourceLocation verticalSlabOf(ResourceLocation slab) {
        return ClutterNoMore.id(shapePrefix(slab) + "vertical_" + slab.getPath());
    }

    public static ResourceLocation stepOf(ResourceLocation stairs) {
        return ClutterNoMore.id(shapePrefix(stairs) + stairs.getPath().replace("stairs", "step"));
    }

    public static void registerVariants() {
        Services.PLATFORM.beforeShapeRegistration();
        ClutterNoMoreConfig.Startup config = ClutterNoMore.STARTUP_CONFIG;
        if (!config.verticalSlabs.value() && !config.steps.value()) return;
        Map<String, Function<BlockBehaviour.Properties, Block>> pending = new LinkedHashMap<>();
        Map<String, BlockItem> parents = new LinkedHashMap<>();
        List<Map.Entry<ResourceKey<Item>, Item>> items = new ArrayList<>(BuiltInRegistries.ITEM.entrySet());
        items.sort(Comparator.comparing(entry -> entry.getKey().location()));
        for (Map.Entry<ResourceKey<Item>, Item> entry : items) {
            if (!(entry.getValue() instanceof BlockItem blockItem)) {
                continue;
            }
            ResourceLocation parentId = entry.getKey().location();
            Block block = blockItem.getBlock();
            if (block instanceof SlabBlock slab && propertyCount(slab.defaultBlockState()) == 2 && config.verticalSlabs.value()) {
                String path = verticalSlabOf(parentId).getPath();
                if (slab instanceof WeatheringCopper weathering) {
                    pending.put(path, properties -> new WeatheringVerticalSlabBlock(weathering.getAge(), properties));
                } else if (TransparentShapes.isGlassLike(slab.defaultBlockState())) {
                    pending.put(path, TransparentVerticalSlabBlock::new);
                } else {
                    pending.put(path, VerticalSlabBlock::new);
                }
                parents.put(path, blockItem);
                RuntimeData.addToTag(RuntimeData.ShapeTag.VERTICAL_SLABS, ClutterNoMore.id(path));
            }
            if (block instanceof StairBlock stairs && propertyCount(stairs.defaultBlockState()) == 4 && config.steps.value()) {
                String path = stepOf(parentId).getPath();
                if (stairs instanceof WeatheringCopper weathering) {
                    pending.put(path, properties -> new WeatheringStepBlock(weathering.getAge(), properties));
                } else if (TransparentShapes.isGlassLike(stairs.defaultBlockState())) {
                    pending.put(path, TransparentStepBlock::new);
                } else {
                    pending.put(path, StepBlock::new);
                }
                parents.put(path, blockItem);
                RuntimeData.addToTag(RuntimeData.ShapeTag.STEPS, ClutterNoMore.id(path));
            }
        }
        for (Map.Entry<String, Function<BlockBehaviour.Properties, Block>> entry : pending.entrySet()) {
            register(entry.getKey(), entry.getValue(), parents.get(entry.getKey()));
        }
        SHAPE_PATHS.addAll(pending.keySet());
        resolveCopper();
        RuntimeData.generate();
        Services.PLATFORM.afterShapeRegistration();
    }

    private static void register(String path, Function<BlockBehaviour.Properties, Block> factory, BlockItem parent) {
        ResourceLocation id = ClutterNoMore.id(path);
        Block block = factory.apply(copy(parent.getBlock(), id));
        Services.PLATFORM.register(BuiltInRegistries.BLOCK, id, block);
        Item.Properties properties = new Item.Properties();
        //? if >=1.21.2 {
        /*properties.setId(ResourceKey.create(Registries.ITEM, id)).useBlockDescriptionPrefix();
        *///?}
        BlockItem item = new BlockItem(block, properties);
        Services.PLATFORM.register(BuiltInRegistries.ITEM, id, item);
        SOURCES.put(block, parent.getBlock());
        SHAPES.put(parent.getBlock(), block);
        SOURCE_ITEMS.put(item, parent);
    }

    private static BlockBehaviour.Properties copy(Block block, ResourceLocation id) {
        //? if >=1.21.2 {
        /*return BlockBehaviour.Properties.ofFullCopy(block).setId(ResourceKey.create(Registries.BLOCK, id));
        *///?} else if >=1.20.5 {
        return BlockBehaviour.Properties.ofFullCopy(block);
        //?} else {
        /*return BlockBehaviour.Properties.copy(block);
        *///?}
    }

    private static void resolveCopper() {
        for (Map.Entry<Block, Block> entry : SOURCES.entrySet()) {
            Block shape = entry.getKey();
            BlockState source = entry.getValue().defaultBlockState();
            if (shape instanceof WeatheringCopper && entry.getValue() instanceof WeatheringCopper copper) {
                pair(NEXT_OXIDATION, shape, copper.getNext(source));
            }
            pair(WAXED_BLOCKS, shape, HoneycombItem.getWaxed(source));
        }
        for (Block shape : SOURCES.keySet()) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(shape);
            if (shape instanceof WeatheringCopper) {
                ResourceLocation previous = previousStage(id);
                if (previous != null) {
                    pairByName(NEXT_OXIDATION, previous, shape);
                }
            } else if (id.getPath().contains("waxed_")) {
                pairByName(WAXED_BLOCKS, ClutterNoMore.id(id.getNamespace(), id.getPath().replace("waxed_", "")), shape);
            }
        }
    }

    private static void pair(BiMap<Block, Block> pairs, Block shape, Optional<BlockState> target) {
        Block paired = target.map(state -> SHAPES.get(state.getBlock())).orElse(null);
        if (paired != null && paired != shape && !pairs.containsValue(paired)) {
            pairs.put(shape, paired);
        }
    }

    private static void pairByName(BiMap<Block, Block> pairs, ResourceLocation from, Block to) {
        Optional<Block> shape = BuiltInRegistries.BLOCK.getOptional(from);
        if (shape.isPresent() && shape.get() != to && SOURCES.containsKey(shape.get()) && !pairs.containsKey(shape.get()) && !pairs.containsValue(to)) {
            pairs.put(shape.get(), to);
        }
    }

    @Nullable
    private static ResourceLocation previousStage(ResourceLocation id) {
        String path = id.getPath();
        if (path.contains("oxidized")) return ClutterNoMore.id(id.getNamespace(), path.replace("oxidized", "weathered"));
        if (path.contains("weathered")) return ClutterNoMore.id(id.getNamespace(), path.replace("weathered", "exposed"));
        if (path.contains("exposed_")) return ClutterNoMore.id(id.getNamespace(), path.replace("exposed_", ""));
        return null;
    }

    private static int propertyCount(BlockState state) {
        //? if >=26 {
        /*return state.getValues().toList().size();
        *///?} else {
        return state.getValues().size();
        //?}
    }
}
