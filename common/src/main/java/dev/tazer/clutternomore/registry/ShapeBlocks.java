package dev.tazer.clutternomore.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreConfig;
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
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class ShapeBlocks {

    private static final String SLAB_PREFIX = "slab_";
    private static final String STAIRS_PREFIX = "stairs_";
    private static final String STAIR_PREFIX = "stair_";

    private static final BiMap<Block, Block> NEXT_OXIDATION = HashBiMap.create();
    private static final BiMap<Block, Block> WAXED_BLOCKS = HashBiMap.create();
    private static final List<String> SHAPE_PATHS = new ArrayList<>();
    private static final Map<Block, Block> SOURCES = new LinkedHashMap<>();
    private static final Map<ShapeType, Map<Block, Block>> SHAPES = new EnumMap<>(ShapeType.class);
    private static final Map<Block, ShapeType> TYPES = new LinkedHashMap<>();
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
    public static Block getShape(ShapeType type, Block source) {
        Map<Block, Block> shapes = SHAPES.get(type);
        return shapes == null ? null : shapes.get(source);
    }

    @Nullable
    public static ShapeType getType(Block shape) {
        return TYPES.get(shape);
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
        String path = slab.getPath();
        String named = path.startsWith(SLAB_PREFIX) ? path.substring(SLAB_PREFIX.length()) + "_slab" : path;
        return ClutterNoMore.id(shapePrefix(slab) + "vertical_" + named);
    }

    public static ResourceLocation stepOf(ResourceLocation stairs) {
        String stem = prefixedStairStem(stairs.getPath());
        if (stem != null) return ClutterNoMore.id(shapePrefix(stairs) + stem + "_step");
        return ClutterNoMore.id(shapePrefix(stairs) + stairs.getPath().replace("stairs", "step"));
    }

    public static ResourceLocation verticalStairsOf(ResourceLocation stairs) {
        String stem = prefixedStairStem(stairs.getPath());
        if (stem != null) return ClutterNoMore.id(shapePrefix(stairs) + "vertical_" + stem + "_stairs");
        return ClutterNoMore.id(shapePrefix(stairs) + "vertical_" + stairs.getPath());
    }

    @Nullable
    private static String legacyPathOf(ShapeType type, ResourceLocation source) {
        String path = source.getPath();
        String legacy = switch (type) {
            case VERTICAL_SLAB -> path.startsWith(SLAB_PREFIX) ? "vertical_" + path : null;
            case STEP -> prefixedStairStem(path) != null ? path.replace("stairs", "step") : null;
            case VERTICAL_STAIRS -> prefixedStairStem(path) != null ? "vertical_" + path : null;
            case VERTICAL_STEP -> prefixedStairStem(path) != null ? "vertical_" + legacyStepPath(path) : null;
        };
        return legacy == null ? null : shapePrefix(source) + legacy;
    }

    private static String legacyStepPath(String path) {
        String step = path.replace("stairs", "step");
        if (step.equals(path)) {
            step = path.replace("stair", "step");
        }
        return step.equals(path) ? path + "_step" : step;
    }

    @Nullable
    private static String prefixedStairStem(String path) {
        if (path.startsWith(STAIRS_PREFIX)) return path.substring(STAIRS_PREFIX.length());
        if (path.startsWith(STAIR_PREFIX)) return path.substring(STAIR_PREFIX.length());
        return null;
    }

    public static ResourceLocation verticalStepOf(ResourceLocation stairs) {
        String prefixed = prefixedStairStem(stairs.getPath());
        if (prefixed != null) return ClutterNoMore.id(shapePrefix(stairs) + "vertical_" + prefixed + "_step");
        String path = stairs.getPath();
        String step = path.replace("stairs", "step");
        if (step.equals(path)) {
            step = path.replace("stair", "step");
        }
        if (step.equals(path)) {
            step = path + "_step";
        }
        return ClutterNoMore.id(shapePrefix(stairs) + "vertical_" + step);
    }

    public static void registerVariants() {
        Services.PLATFORM.beforeShapeRegistration();
        ClutterNoMoreConfig.Startup config = ClutterNoMore.STARTUP_CONFIG;
        List<ShapeType> types = new ArrayList<>();
        for (ShapeType type : ShapeType.values()) {
            if (type.isEnabled(config)) {
                types.add(type);
            }
        }
        if (types.isEmpty()) return;
        Map<String, Function<BlockBehaviour.Properties, Block>> pending = new LinkedHashMap<>();
        Map<String, BlockItem> parents = new LinkedHashMap<>();
        Map<String, ShapeType> pendingTypes = new LinkedHashMap<>();
        Map<String, String> legacies = new LinkedHashMap<>();
        List<Map.Entry<ResourceKey<Item>, Item>> items = new ArrayList<>(BuiltInRegistries.ITEM.entrySet());
        items.sort(Comparator.comparing(entry -> entry.getKey().location()));
        for (Map.Entry<ResourceKey<Item>, Item> entry : items) {
            if (!(entry.getValue() instanceof BlockItem blockItem)) {
                continue;
            }
            ResourceLocation parentId = entry.getKey().location();
            Block block = blockItem.getBlock();
            for (ShapeType type : types) {
                if (!type.accepts(block)) {
                    continue;
                }
                String path = type.idOf(parentId).getPath();
                String legacy = legacyPathOf(type, parentId);
                if (legacy != null && !legacy.equals(path)) {
                    legacies.put(path, legacy);
                }
                pending.put(path, type.factory(block));
                parents.put(path, blockItem);
                pendingTypes.put(path, type);
                RuntimeData.addToTag(type, ClutterNoMore.id(path));
            }
        }
        for (Map.Entry<String, Function<BlockBehaviour.Properties, Block>> entry : pending.entrySet()) {
            register(entry.getKey(), entry.getValue(), parents.get(entry.getKey()), pendingTypes.get(entry.getKey()));
            String legacy = legacies.get(entry.getKey());
            if (legacy != null) {
                Services.PLATFORM.alias(BuiltInRegistries.BLOCK, ClutterNoMore.id(legacy), ClutterNoMore.id(entry.getKey()));
                Services.PLATFORM.alias(BuiltInRegistries.ITEM, ClutterNoMore.id(legacy), ClutterNoMore.id(entry.getKey()));
            }
        }
        SHAPE_PATHS.addAll(pending.keySet());
        resolveCopper();
        RuntimeData.generate();
        Services.PLATFORM.afterShapeRegistration();
    }

    private static void register(String path, Function<BlockBehaviour.Properties, Block> factory, BlockItem parent, ShapeType type) {
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
        SHAPES.computeIfAbsent(type, key -> new LinkedHashMap<>()).put(parent.getBlock(), block);
        TYPES.put(block, type);
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
            ShapeType type = TYPES.get(shape);
            if (shape instanceof WeatheringCopper && entry.getValue() instanceof WeatheringCopper copper) {
                pair(NEXT_OXIDATION, type, shape, copper.getNext(source));
            }
            pair(WAXED_BLOCKS, type, shape, HoneycombItem.getWaxed(source));
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

    private static void pair(BiMap<Block, Block> pairs, ShapeType type, Block shape, Optional<BlockState> target) {
        Block paired = target.map(state -> getShape(type, state.getBlock())).orElse(null);
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
}
