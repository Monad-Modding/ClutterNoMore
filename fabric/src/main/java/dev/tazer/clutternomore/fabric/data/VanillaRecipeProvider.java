package dev.tazer.clutternomore.fabric.data;

import com.google.common.hash.HashCode;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.recipe.RecipeFixes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
//? if >=1.21 {
import net.minecraft.core.HolderLookup;
//?}
//? if >=26 {
/*import net.minecraft.data.recipes.RecipeProvider;
*///?}
//? if >=26 && <26.3 {
/*import net.minecraft.data.recipes.RecipeOutput;
*///?} else if <26 && >=1.21 {
import net.minecraft.data.recipes.RecipeOutput;
//?}
//? if >=26.2 {
/*import net.minecraft.world.level.block.WeatheringCopperCollection;
*///?}
//? if >=26.3 {
/*import net.minecraft.advancements.Advancement;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.crafting.Recipe;
*///?}
//? if <1.21 {
/*import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;
*///?}

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class VanillaRecipeProvider extends FabricRecipeProvider {

    //? if >=1.21 {
    public VanillaRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }
    //?} else {
    /*public VanillaRecipeProvider(FabricDataOutput output) {
        super(output);
    }
    *///?}

    private static void build(ShapedFactory factory, Consumer<ShapedRecipeBuilder> save) {
        save.accept(factory.shaped(RecipeCategory.DECORATIONS, Items.BARREL, 1).pattern("#T#").pattern("# #").pattern("###").define('#', ItemTags.PLANKS).define('T', ItemTags.WOODEN_TRAPDOORS));
        save.accept(factory.shaped(RecipeCategory.BUILDING_BLOCKS, Items.CHISELED_BOOKSHELF, 1).define('#', ItemTags.PLANKS).define('P', Items.PAPER).pattern("###").pattern("PPP").pattern("###"));
        for (ItemLike[] pair : chiseledPairs()) {
            save.accept(factory.shaped(RecipeCategory.BUILDING_BLOCKS, pair[0], 4).define('#', pair[1]).pattern(" # ").pattern("# #").pattern(" # "));
        }
    }

    private static List<ItemLike[]> chiseledPairs() {
        List<ItemLike[]> pairs = new ArrayList<>();
        pairs.add(new ItemLike[]{Items.CHISELED_DEEPSLATE, Items.COBBLED_DEEPSLATE});
        pairs.add(new ItemLike[]{Items.CHISELED_NETHER_BRICKS, Items.NETHER_BRICKS});
        pairs.add(new ItemLike[]{Items.CHISELED_POLISHED_BLACKSTONE, Items.POLISHED_BLACKSTONE});
        pairs.add(new ItemLike[]{Items.CHISELED_QUARTZ_BLOCK, Items.QUARTZ_BLOCK});
        pairs.add(new ItemLike[]{Items.CHISELED_RED_SANDSTONE, Items.RED_SANDSTONE});
        pairs.add(new ItemLike[]{Items.CHISELED_SANDSTONE, Items.SANDSTONE});
        pairs.add(new ItemLike[]{Items.CHISELED_STONE_BRICKS, Items.STONE_BRICKS});
        //? if >=26.2 {
        /*WeatheringCopperCollection.zipApply(Items.CHISELED_COPPER, Items.CUT_COPPER, (chiseled, cut) -> pairs.add(new ItemLike[]{chiseled, cut}));
        *///?} else if >=1.21 {
        pairs.add(new ItemLike[]{Items.CHISELED_COPPER, Items.CUT_COPPER});
        pairs.add(new ItemLike[]{Items.EXPOSED_CHISELED_COPPER, Items.EXPOSED_CUT_COPPER});
        pairs.add(new ItemLike[]{Items.WEATHERED_CHISELED_COPPER, Items.WEATHERED_CUT_COPPER});
        pairs.add(new ItemLike[]{Items.OXIDIZED_CHISELED_COPPER, Items.OXIDIZED_CUT_COPPER});
        pairs.add(new ItemLike[]{Items.WAXED_CHISELED_COPPER, Items.WAXED_CUT_COPPER});
        pairs.add(new ItemLike[]{Items.WAXED_EXPOSED_CHISELED_COPPER, Items.WAXED_EXPOSED_CUT_COPPER});
        pairs.add(new ItemLike[]{Items.WAXED_WEATHERED_CHISELED_COPPER, Items.WAXED_WEATHERED_CUT_COPPER});
        pairs.add(new ItemLike[]{Items.WAXED_OXIDIZED_CHISELED_COPPER, Items.WAXED_OXIDIZED_CUT_COPPER});
        //?}
        //? if >=1.21 {
        pairs.add(new ItemLike[]{Items.CHISELED_TUFF, Items.TUFF});
        pairs.add(new ItemLike[]{Items.CHISELED_TUFF_BRICKS, Items.TUFF_BRICKS});
        //?}
        //? if >=1.21.4 {
        /*pairs.add(new ItemLike[]{Items.CHISELED_RESIN_BRICKS, Items.RESIN_BRICK_SLAB});
        *///?}
        return pairs;
    }

    //? if >=26.3 {
    /*@Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
        return new RecipeProvider(recipes, advancements) {
            @Override
            public void buildRecipes() {
                build(this::shaped, builder -> builder.save(this.output));
            }
        };
    }
    *///?} else if >=26 {
    /*@Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
                build(this::shaped, builder -> builder.save(this.output));
            }
        };
    }
    *///?} else if >=1.21 {
    @Override
    public void buildRecipes(RecipeOutput output) {
        build(ShapedRecipeBuilder::shaped, builder -> builder.save(output));
    }
    //?} else {
    /*@Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        build(ShapedRecipeBuilder::shaped, builder -> builder.save(recipe -> exporter.accept(new RecipeOnly(recipe))));
    }
    *///?}

    @Override
    public String getName() {
        return "Vanilla Overrides";
    }

    //? if >=1.21 && <26 {
    @Override
    public CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider registries) {
        return super.run((path, data, hash) -> relocate(output, path, data, hash), registries);
    }
    //?} else {
    /*@Override
    public CompletableFuture<?> run(CachedOutput output) {
        return super.run((path, data, hash) -> relocate(output, path, data, hash));
    }
    *///?}

    private static void relocate(CachedOutput output, Path path, byte[] data, HashCode hash) throws IOException {
        for (int i = 0; i + 3 < path.getNameCount(); i++) {
            if (!path.getName(i).toString().equals("data")) {
                continue;
            }
            String folder = path.getName(i + 2).toString();
            if (folder.startsWith("advancement")) return;
            if (folder.startsWith("recipe")) {
                Path fixes = path.getRoot().resolve(path.subpath(0, i + 1)).resolve(ClutterNoMore.MOD_ID).resolve(RecipeFixes.DIRECTORY).resolve(path.getName(i + 1).toString());
                output.writeIfNeeded(fixes.resolve(path.subpath(i + 3, path.getNameCount()).toString()), data, hash);
                return;
            }
        }
        output.writeIfNeeded(path, data, hash);
    }

    @Override
    protected ResourceLocation getRecipeIdentifier(ResourceLocation identifier) {
        return ClutterNoMore.id("minecraft", identifier.getPath());
    }

    @FunctionalInterface
    private interface ShapedFactory {

        ShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result, int count);
    }

    //? if <1.21 {
    /*private record RecipeOnly(FinishedRecipe recipe) implements FinishedRecipe {

        @Override
        public void serializeRecipeData(JsonObject json) {
            this.recipe.serializeRecipeData(json);
        }

        @Override
        public ResourceLocation getId() {
            return this.recipe.getId();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return this.recipe.getType();
        }

        @Override
        @Nullable
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        @Nullable
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
    *///?}
}
