//? if <1.21.2 {
package dev.tazer.clutternomore.mixin.recipe;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tazer.clutternomore.recipe.RecipeFixes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Inventory.class)
public abstract class RecipeBookInventoryMixin {

    //? if >=1.20.5 {
    @WrapOperation(method = "findSlotMatchingUnusedItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    //?} else {
    /*@WrapOperation(method = "findSlotMatchingUnusedItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameTags(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    *///?}
    private boolean cnm$splitShapes(ItemStack wanted, ItemStack candidate, Operation<Boolean> original) {
        return original.call(wanted, candidate) && (RecipeFixes.isEnabled() || wanted.getItem() == candidate.getItem());
    }
}
//?}
