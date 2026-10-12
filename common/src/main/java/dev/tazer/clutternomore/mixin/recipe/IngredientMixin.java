package dev.tazer.clutternomore.mixin.recipe;

import dev.tazer.clutternomore.recipe.RecipeFixes;
import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >=26 {
/*import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
*///?}

@Mixin(Ingredient.class)
public class IngredientMixin {

    //? if >=26 {
    /*@Shadow
    @Final
    private HolderSet<Item> values;
    *///?}

    @Inject(method = "test(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"), cancellable = true)
    private void cnm$matchShapeSet(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() || stack == null || stack.isEmpty() || !RecipeFixes.isEnabled()) return;
        //? if >=26 {
        /*for (Holder<Item> item : this.values) {
            if (ShapeMap.inSameShapeSet(item.value(), stack.getItem())) {
                cir.setReturnValue(true);
                return;
            }
        }
        *///?} else {
        for (ItemStack candidate : ((Ingredient) (Object) this).getItems()) {
            if (ShapeMap.inSameShapeSet(candidate.getItem(), stack.getItem())) {
                cir.setReturnValue(true);
                return;
            }
        }
        //?}
    }
}
