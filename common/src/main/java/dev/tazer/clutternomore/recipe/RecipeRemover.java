package dev.tazer.clutternomore.recipe;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.Nullable;
//? if >=1.21 {
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.tazer.clutternomore.mixin.recipe.RecipeManagerAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.crafting.RecipeHolder;
//?} else {
/*import dev.tazer.clutternomore.mixin.recipe.SingleItemRecipeAccessor;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
*///?}
//? if >=26 {
/*import dev.tazer.clutternomore.platform.Services;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
*///?}
//? if >=26.3 {
/*import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
*///?}

import java.util.ArrayList;
import java.util.List;
//? if >=1.21 {
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
//?}

public final class RecipeRemover {

    //? if >=26.3 {
    /*@Nullable
    private static HolderLookup.Provider registries;
    *///?}

    private RecipeRemover() {
    }

    //? if >=26.3 {
    /*public static void setRegistries(HolderLookup.Provider provider) {
        registries = provider;
    }
    *///?}

    //? if >=1.21 {
    public static void removeShapeRecipes(RecipeManager manager) {
        if (!ClutterNoMore.STARTUP_CONFIG.recipeFixes.value() || ShapeMap.getParentByShape().isEmpty()) return;
        RecipeManagerAccessor accessor = (RecipeManagerAccessor) manager;
        Map<String, JsonElement> shapeToParent = new HashMap<>();
        for (Map.Entry<Item, Item> entry : ShapeMap.getParentByShape().entrySet()) {
            shapeToParent.put(BuiltInRegistries.ITEM.getKey(entry.getKey()).toString(), new JsonPrimitive(BuiltInRegistries.ITEM.getKey(entry.getValue()).toString()));
        }
        Map<String, TagInfo> tags = buildTagCache();
        //? if >=26.3 {
        /*if (registries == null) return;
        RegistryOps<JsonElement> ops = registries.createSerializationContext(JsonOps.INSTANCE);
        ContextMap context = ContextMap.builder().buildAndValidate(SlotDisplayContext.CONTEXT);
        Iterable<RecipeHolder<?>> source = accessor.cnm$getRecipes().values();
        *///?} else if >=26 {
        /*RegistryOps<JsonElement> ops = accessor.cnm$getRegistries().createSerializationContext(JsonOps.INSTANCE);
        ContextMap context = new ContextMap.Builder().create(SlotDisplayContext.CONTEXT);
        Iterable<RecipeHolder<?>> source = accessor.cnm$getRecipes().values();
        *///?} else {
        RegistryOps<JsonElement> ops = accessor.cnm$getRegistries().createSerializationContext(JsonOps.INSTANCE);
        Iterable<RecipeHolder<?>> source = manager.getRecipes();
        //?}
        List<RecipeHolder<?>> kept = new ArrayList<>();
        Map<Item, Set<String>> dangerousTags = new HashMap<>();
        boolean detailedLogs = ClutterNoMore.STARTUP_CONFIG.detailedLogs.value();
        int encodeFailures = 0;
        for (RecipeHolder<?> holder : source) {
            try {
                Recipe<?> recipe = holder.value();
                //? if >=26 {
                /*Item result = resultOf(recipe, context);
                *///?} else {
                Item result = recipe.getResultItem(accessor.cnm$getRegistries()).getItem();
                //?}
                if (result != null && ShapeMap.isShape(result)) {
                    continue;
                }
                if (!mayUseShapes(recipe)) {
                    kept.add(holder);
                    continue;
                }
                //? if >=26.3 {
                /*DataResult<JsonElement> encoded = Recipe.CODEC.encodeStart(ops, new Holder.Direct<>(recipe, DataComponentMap.EMPTY));
                *///?} else {
                DataResult<JsonElement> encoded = Recipe.CODEC.encodeStart(ops, recipe);
                //?}
                Optional<JsonElement> json = encoded.result();
                if (json.isEmpty()) {
                    if (encoded.error().isPresent()) {
                        encodeFailures++;
                        if (detailedLogs) {
                            ClutterNoMore.LOGGER.warn("[CNM] encode fail for {}: {}", holder.id(), encoded.error().get().message());
                        }
                    }
                    kept.add(holder);
                    continue;
                }
                if (result != null && containsAny(json.get(), dangerousIds(result), dangerousTags.computeIfAbsent(result, item -> dangerousTags(item, tags)))) {
                    continue;
                }
                if (mutate(json.get(), shapeToParent, tags)) {
                    //? if >=26.3 {
                    /*Optional<Holder<Recipe<?>>> decoded = Recipe.CODEC.parse(ops, json.get()).result();
                    if (decoded.isPresent()) {
                        kept.add(new RecipeHolder<>(holder.id(), decoded.get().value()));
                        continue;
                    }
                    *///?} else {
                    Optional<Recipe<?>> decoded = Recipe.CODEC.parse(ops, json.get()).result();
                    if (decoded.isPresent()) {
                        kept.add(new RecipeHolder<>(holder.id(), decoded.get()));
                        continue;
                    }
                    //?}
                }
                kept.add(holder);
            } catch (Exception exception) {
                ClutterNoMore.LOGGER.error("Error processing recipe {}: {}", holder.id(), exception.getMessage());
                kept.add(holder);
            }
        }
        if (encodeFailures > 0) {
            ClutterNoMore.LOGGER.info("[CNM] {} recipes could not be re-encoded for shape processing and were kept unchanged (enable detailed logs to list them)", encodeFailures);
        }
        //? if >=26 {
        /*accessor.cnm$setRecipes(Services.PLATFORM.createRecipeMap(kept));
        *///?} else {
        manager.replaceRecipes(kept);
        //?}
    }

    private static boolean mayUseShapes(Recipe<?> recipe) {
        //? if >=26 {
        /*List<Ingredient> ingredients = recipe.placementInfo().ingredients();
        *///?} else {
        List<Ingredient> ingredients = recipe.getIngredients();
        //?}
        if (ingredients.isEmpty()) return true;
        for (Ingredient ingredient : ingredients) {
            if (hasShape(ingredient)) return true;
        }
        return false;
    }

    private static boolean hasShape(Ingredient ingredient) {
        //? if >=26 {
        /*return ingredient.items().anyMatch(holder -> ShapeMap.isShape(holder.value()));
        *///?} else {
        for (ItemStack stack : ingredient.getItems()) {
            if (ShapeMap.isShape(stack.getItem())) return true;
        }
        return false;
        //?}
    }

    private static Set<String> dangerousIds(Item result) {
        Set<String> ids = new HashSet<>();
        for (Item shape : ShapeMap.getShapes(result)) {
            if (ShapeMap.inSameShapeSet(shape, result)) {
                ids.add(BuiltInRegistries.ITEM.getKey(shape).toString());
            }
        }
        return ids;
    }

    private static Set<String> dangerousTags(Item result, Map<String, TagInfo> tags) {
        Set<String> dangerous = new HashSet<>();
        String resultId = BuiltInRegistries.ITEM.getKey(result).toString();
        for (Map.Entry<String, TagInfo> entry : tags.entrySet()) {
            JsonArray expanded = entry.getValue().expandedItems();
            if (expanded.isEmpty()) {
                continue;
            }
            boolean collapses = true;
            for (JsonElement element : expanded) {
                if (!element.getAsJsonObject().get("item").getAsString().equals(resultId)) {
                    collapses = false;
                    break;
                }
            }
            if (collapses) {
                dangerous.add(entry.getKey());
            }
        }
        return dangerous;
    }

    private static Map<String, TagInfo> buildTagCache() {
        Map<String, TagInfo> result = new HashMap<>();
        //? if >=26 {
        /*BuiltInRegistries.ITEM.getTags().forEach(tag -> processTag(tag, result));
        *///?} else {
        BuiltInRegistries.ITEM.getTags().forEach(pair -> processTag(pair.getSecond(), result));
        //?}
        return result;
    }

    private static void processTag(HolderSet.Named<Item> tag, Map<String, TagInfo> out) {
        Set<Item> shapes = new HashSet<>();
        Set<String> seen = new LinkedHashSet<>();
        JsonArray expanded = new JsonArray();
        for (Holder<Item> holder : tag) {
            Item item = holder.value();
            String id;
            if (ShapeMap.isShape(item)) {
                shapes.add(item);
                id = BuiltInRegistries.ITEM.getKey(ShapeMap.getParent(item)).toString();
            } else {
                id = BuiltInRegistries.ITEM.getKey(item).toString();
            }
            if (seen.add(id)) {
                JsonObject entry = new JsonObject();
                entry.addProperty("item", id);
                expanded.add(entry);
            }
        }
        if (!shapes.isEmpty()) {
            out.put(tag.key().location().toString(), new TagInfo(expanded, shapes));
        }
    }

    //? if >=26 {
    /*@Nullable
    private static Item resultOf(Recipe<?> recipe, ContextMap context) {
        try {
            for (RecipeDisplay display : recipe.display()) {
                for (ItemStack stack : display.result().resolveForStacks(context)) {
                    if (!stack.isEmpty()) return stack.getItem();
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }
    *///?}

    @Nullable
    private static String tagIdOf(JsonElement element) {
        if (element.isJsonObject()) {
            JsonElement tag = element.getAsJsonObject().get("tag");
            if (tag != null && tag.isJsonPrimitive() && tag.getAsJsonPrimitive().isString()) return tag.getAsString();
        } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            String text = element.getAsString();
            if (text.startsWith("#")) return text.substring(1);
        }
        return null;
    }

    private static boolean isSkippedKey(String key) {
        return "type".equals(key) || "result".equals(key) || "results".equals(key);
    }

    private static boolean containsAny(JsonElement element, Set<String> literals, Set<String> tags) {
        String tagId = tagIdOf(element);
        if (tagId != null && tags.contains(tagId)) return true;
        if (element.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                if (!isSkippedKey(entry.getKey()) && containsAny(entry.getValue(), literals, tags)) return true;
            }
        } else if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                if (containsAny(child, literals, tags)) return true;
            }
        } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            return literals.contains(element.getAsString());
        }
        return false;
    }

    @Nullable
    private static JsonElement replacementFor(JsonElement value, Map<String, JsonElement> replacements, Map<String, TagInfo> tags) {
        String tagId = tagIdOf(value);
        if (tagId != null) {
            TagInfo info = tags.get(tagId);
            return info == null ? null : info.expandedItems().deepCopy();
        }
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
            JsonElement replacement = replacements.get(value.getAsString());
            if (replacement != null) return replacement.deepCopy();
        }
        return null;
    }

    private static boolean mutate(JsonElement element, Map<String, JsonElement> replacements, Map<String, TagInfo> tags) {
        boolean changed = false;
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                if (isSkippedKey(entry.getKey())) {
                    continue;
                }
                JsonElement replacement = replacementFor(entry.getValue(), replacements, tags);
                if (replacement != null) {
                    object.add(entry.getKey(), replacement);
                    changed = true;
                } else {
                    changed |= mutate(entry.getValue(), replacements, tags);
                }
            }
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                JsonElement replacement = replacementFor(array.get(i), replacements, tags);
                if (replacement != null) {
                    array.set(i, replacement);
                    changed = true;
                } else {
                    changed |= mutate(array.get(i), replacements, tags);
                }
            }
        }
        return changed;
    }

    private record TagInfo(JsonArray expandedItems, Set<Item> shapes) {
    }
    //?} else {
    /*public static void removeShapeRecipes(RecipeManager manager, RegistryAccess registries) {
        if (!ClutterNoMore.STARTUP_CONFIG.recipeFixes.value() || ShapeMap.getParentByShape().isEmpty()) return;
        boolean removed = false;
        List<Recipe<?>> kept = new ArrayList<>();
        for (Recipe<?> recipe : manager.getRecipes()) {
            if (process(recipe, registries)) {
                removed = true;
            } else {
                kept.add(recipe);
            }
        }
        if (removed) {
            manager.replaceRecipes(kept);
        }
    }

    private static boolean process(Recipe<?> recipe, RegistryAccess registries) {
        Item result = resultOf(recipe, registries);
        if (result != null && ShapeMap.isShape(result)) return true;
        if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe) {
            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            for (int i = 0; i < ingredients.size(); i++) {
                if (producesOwnParent(ingredients.get(i), result)) return true;
                Ingredient rewritten = rewrite(ingredients.get(i));
                if (rewritten != null) {
                    ingredients.set(i, rewritten);
                }
            }
        } else if (recipe instanceof SingleItemRecipe single) {
            SingleItemRecipeAccessor accessor = (SingleItemRecipeAccessor) single;
            if (producesOwnParent(accessor.cnm$getIngredient(), result)) return true;
            Ingredient rewritten = rewrite(accessor.cnm$getIngredient());
            if (rewritten != null) {
                accessor.cnm$setIngredient(rewritten);
            }
        }
        return false;
    }

    @Nullable
    private static Item resultOf(Recipe<?> recipe, RegistryAccess registries) {
        try {
            ItemStack stack = recipe.getResultItem(registries);
            if (stack != null && !stack.isEmpty()) return stack.getItem();
        } catch (Throwable throwable) {
            ClutterNoMore.LOGGER.debug("getResultItem failed for {}: {}", recipe.getClass().getSimpleName(), throwable.getMessage());
        }
        return null;
    }

    private static boolean producesOwnParent(Ingredient ingredient, @Nullable Item result) {
        if (result == null) return false;
        for (ItemStack stack : ingredient.getItems()) {
            if (ShapeMap.isShape(stack.getItem()) && ShapeMap.getParent(stack.getItem()) == result) return true;
        }
        return false;
    }

    @Nullable
    private static Ingredient rewrite(Ingredient ingredient) {
        boolean changed = false;
        List<ItemStack> stacks = new ArrayList<>();
        for (ItemStack stack : ingredient.getItems()) {
            if (ShapeMap.isShape(stack.getItem())) {
                stacks.add(ShapeMap.transferStack(stack, 0));
                changed = true;
            } else {
                stacks.add(stack);
            }
        }
        if (!changed || stacks.isEmpty()) return null;
        return Ingredient.of(stacks.stream());
    }
    *///?}
}
