package dev.tazer.clutternomore.mixin.recipe;

import dev.tazer.clutternomore.recipe.RecipeRemover;
import dev.tazer.clutternomore.registry.ShapeBlocks;
import dev.tazer.clutternomore.shape.ShapeMapReloadListener;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if <1.21 {
/*import net.minecraft.core.RegistryAccess;
*///?}

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {

    @Shadow
    @Final
    private RecipeManager recipes;

    //? if >=26 {
    /*@Inject(method = "updateComponentsAndStaticRegistryTags", at = @At("RETURN"))
    private void cnm$applyShapeMaps(CallbackInfo ci) {
        ShapeMapReloadListener.resolve();
        ShapeBlocks.applyFlammability();
        RecipeRemover.removeShapeRecipes(this.recipes);
    }
    *///?} else if >=1.21 {
    @Inject(method = "updateRegistryTags()V", at = @At("RETURN"))
    private void cnm$applyShapeMaps(CallbackInfo ci) {
        ShapeMapReloadListener.resolve();
        ShapeBlocks.applyFlammability();
        RecipeRemover.removeShapeRecipes(this.recipes);
    }
    //?} else {
    /*@Inject(method = "updateRegistryTags(Lnet/minecraft/core/RegistryAccess;)V", at = @At("RETURN"))
    private void cnm$applyShapeMaps(RegistryAccess registryAccess, CallbackInfo ci) {
        ShapeMapReloadListener.resolve();
        ShapeBlocks.applyFlammability();
        RecipeRemover.removeShapeRecipes(this.recipes, registryAccess);
    }
    *///?}
}
