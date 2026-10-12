package dev.tazer.clutternomore.mixin.recipe;

import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
//? if >=1.21 && <26.3 {
import net.minecraft.core.HolderLookup;
//?}
//? if >=1.21 {
import org.spongepowered.asm.mixin.gen.Accessor;
//?}
//? if >=26 {
/*import net.minecraft.world.item.crafting.RecipeMap;
import org.spongepowered.asm.mixin.Mutable;
*///?}

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {

    //? if >=26 {
    /*@Accessor("recipes")
    RecipeMap cnm$getRecipes();

    @Accessor("recipes")
    @Mutable
    void cnm$setRecipes(RecipeMap recipes);
    *///?}

    //? if >=1.21 && <26.3 {
    @Accessor("registries")
    HolderLookup.Provider cnm$getRegistries();
    //?}
}
