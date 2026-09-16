package dev.tazer.clutternomore.common.mixin.recipe;

import dev.tazer.clutternomore.common.recipe.RecipeRemover;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {


    @Inject(method = "<init>", at = @At(value = "HEAD", target = "Lnet/minecraft/core/HolderLookup;listElements()Ljava/util/stream/Stream;"))
    private static void storeRegistryAccess(HolderLookup.Provider registries, CallbackInfo ci) {
        //? if >26.2
        //RecipeRemover.registries = registries;
    }
}
