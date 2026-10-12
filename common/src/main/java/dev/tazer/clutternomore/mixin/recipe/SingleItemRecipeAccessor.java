//? if <1.21 {
/*package dev.tazer.clutternomore.mixin.recipe;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SingleItemRecipe.class)
public interface SingleItemRecipeAccessor {

    @Accessor("ingredient")
    Ingredient cnm$getIngredient();

    @Accessor("ingredient")
    @Mutable
    void cnm$setIngredient(Ingredient ingredient);
}
*///?}
