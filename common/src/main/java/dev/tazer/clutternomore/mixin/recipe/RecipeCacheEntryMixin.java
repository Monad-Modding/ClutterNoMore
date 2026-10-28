//? if >=1.21 {
package dev.tazer.clutternomore.mixin.recipe;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RecipeCache.Entry.class)
public abstract class RecipeCacheEntryMixin {

    @WrapOperation(method = "matches", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean cnm$exactItem(ItemStack cached, ItemStack current, Operation<Boolean> original) {
        return original.call(cached, current) && cached.getItem() == current.getItem();
    }
}
//?}
