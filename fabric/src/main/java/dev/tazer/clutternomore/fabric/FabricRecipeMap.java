//? if >=26.3 {
/*package dev.tazer.clutternomore.fabric;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.impl.recipe.sync.RecipeSyncImpl;
import net.fabricmc.fabric.impl.recipe.sync.SyncedSerializerAwarePreparedRecipe;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class FabricRecipeMap extends RecipeMap implements SyncedSerializerAwarePreparedRecipe {

    private final Map<RecipeSerializer<?>, List<RecipeHolder<?>>> bySyncedSerializer;

    private FabricRecipeMap(Multimap<RecipeType<?>, RecipeHolder<?>> byType, Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey, Map<RecipeSerializer<?>, List<RecipeHolder<?>>> bySyncedSerializer) {
        super(byType, byKey);
        this.bySyncedSerializer = bySyncedSerializer;
    }

    public static FabricRecipeMap create(List<RecipeHolder<?>> recipes) {
        ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> byType = ImmutableMultimap.builder();
        ImmutableMap.Builder<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey = ImmutableMap.builder();
        for (RecipeHolder<?> recipe : recipes) {
            byType.put(recipe.value().getType(), recipe);
            byKey.put(recipe.id(), recipe);
        }
        Map<RecipeSerializer<?>, List<RecipeHolder<?>>> bySerializer = new IdentityHashMap<>();
        for (RecipeSerializer<?> serializer : RecipeSyncImpl.getSyncedSerializers()) {
            bySerializer.put(serializer, new ArrayList<>());
        }
        for (RecipeHolder<?> recipe : recipes) {
            List<RecipeHolder<?>> list = bySerializer.get(recipe.value().getSerializer());
            if (list != null) {
                list.add(recipe);
            }
        }
        return new FabricRecipeMap(byType.build(), byKey.build(), bySerializer);
    }

    @Override
    @Nullable
    public List<RecipeHolder<?>> fabric_getRecipesBySyncedSerializer(RecipeSerializer<?> serializer) {
        return this.bySyncedSerializer.get(serializer);
    }
}
*///?}
