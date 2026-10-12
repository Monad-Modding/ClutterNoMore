package dev.tazer.clutternomore.compat;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.shape.ShapeMap;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
//? if >=1.20.5 {
import mezz.jei.api.registration.IIngredientAliasRegistration;
//?}

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JeiPlugin
public final class JeiCompat implements IModPlugin {

    @Nullable
    private static IJeiRuntime runtime;

    public static boolean isHoveringIngredient() {
        if (runtime == null) return false;
        if (runtime.getIngredientListOverlay().getIngredientUnderMouse().isPresent()) return true;
        return runtime.getRecipesGui().getIngredientUnderMouse(VanillaTypes.ITEM_STACK).isPresent();
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ClutterNoMore.id("jei");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    @Override
    public void onRuntimeUnavailable() {
        runtime = null;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<ItemStack> hidden = new ArrayList<>();
        for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            if (ShapeMap.isShape(entry.getValue()) || entry.getKey().location().getNamespace().equals(ClutterNoMore.MOD_ID)) {
                hidden.add(entry.getValue().getDefaultInstance());
            }
        }
        registration.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hidden);
    }

    //? if >=1.20.5 {
    @Override
    public void registerIngredientAliases(IIngredientAliasRegistration registration) {
        for (Map.Entry<Item, List<Item>> entry : ShapeMap.getShapesByParent().entrySet()) {
            List<String> aliases = ShapeMap.getSearchAliases(entry.getKey());
            if (!aliases.isEmpty()) {
                registration.addAliases(VanillaTypes.ITEM_STACK, entry.getKey().getDefaultInstance(), aliases);
            }
        }
    }
    //?}
}
