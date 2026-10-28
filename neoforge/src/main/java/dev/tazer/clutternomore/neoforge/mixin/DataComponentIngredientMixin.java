//? if <26 {
package dev.tazer.clutternomore.neoforge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DataComponentIngredient.class)
public abstract class DataComponentIngredientMixin {

    @WrapOperation(method = "test", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean cnm$exactItem(ItemStack stack, ItemStack expected, Operation<Boolean> original) {
        return original.call(stack, expected) && stack.getItem() == expected.getItem();
    }
}
//?}
