//? fabric && >26.2 {
/*package dev.tazer.clutternomore.fabric;

import com.google.common.collect.Multimap;
import net.fabricmc.fabric.impl.recipe.sync.SyncedSerializerAwarePreparedRecipe;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.*;
import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class FabricRecipeMap extends RecipeMap implements SyncedSerializerAwarePreparedRecipe {

	private final Map<RecipeSerializer<?>, List<RecipeHolder<?>>> bySyncedSerializer;

	public FabricRecipeMap(Multimap<RecipeType<?>, RecipeHolder<?>> byType, Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey, IdentityHashMap<RecipeSerializer<?>, List<RecipeHolder<?>>> map) {
		super(byType, byKey);
		this.bySyncedSerializer = map;
	}

	@Override
	public @Nullable List<RecipeHolder<?>> fabric_getRecipesBySyncedSerializer(RecipeSerializer<?> serializer) {
		return bySyncedSerializer.get(serializer);
	}
}
*///?}