//? if >=26 {
/*package dev.tazer.clutternomore.mixin.recipe;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.TransmuteRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TransmuteRecipe.class)
public abstract class TransmuteRecipeMixin {

    @WrapOperation(method = "matches", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean cnm$exactItem(ItemStack input, ItemStack result, Operation<Boolean> original) {
        return original.call(input, result) && input.getItem() == result.getItem();
    }
}
*///?}
