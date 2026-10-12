//? if >=26.3 {
/*package dev.tazer.clutternomore.mixin.recipe;

import dev.tazer.clutternomore.recipe.RecipeRemover;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cnm$storeRegistries(HolderLookup.Provider registries, CallbackInfo ci) {
        RecipeRemover.setRegistries(registries);
    }
}
*///?}
