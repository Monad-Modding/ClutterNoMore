package dev.tazer.clutternomore.mixin.item;

import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    //? if >=1.20.5 {
    @Inject(method = "isSameItemSameComponents", at = @At("RETURN"), cancellable = true)
    //?} else {
    /*@Inject(method = "isSameItemSameTags", at = @At("RETURN"), cancellable = true)
    *///?}
    private static void cnm$sameShapeSet(ItemStack stack, ItemStack other, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            cir.setReturnValue(ShapeMap.inSameShapeSet(stack.getItem(), other.getItem()));
        }
    }
}
