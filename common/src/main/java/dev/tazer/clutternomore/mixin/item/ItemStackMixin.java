package dev.tazer.clutternomore.mixin.item;

import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >=1.20.5 {
import org.spongepowered.asm.mixin.injection.ModifyVariable;
//?}

@Mixin(ItemStack.class)
public class ItemStackMixin {

    //? if >=1.20.5 {
    @Inject(method = "isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"), cancellable = true)
    //?} else {
    /*@Inject(method = "isSameItemSameTags", at = @At("RETURN"), cancellable = true)
    *///?}
    private static void cnm$sameShapeSet(ItemStack stack, ItemStack other, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && ShapeMap.inSameShapeSet(stack.getItem(), other.getItem())) {
            //? if >=1.20.5 {
            cir.setReturnValue(ItemStack.isSameItemSameComponents(ShapeMap.transferStack(stack, 0), ShapeMap.transferStack(other, 0)));
            //?} else {
            /*cir.setReturnValue(ItemStack.isSameItemSameTags(ShapeMap.transferStack(stack, 0), ShapeMap.transferStack(other, 0)));
            *///?}
        }
    }

    //? if >=1.20.5 {
    @ModifyVariable(method = "hashItemAndComponents", at = @At("HEAD"), argsOnly = true)
    private static ItemStack cnm$hashAsParent(ItemStack stack) {
        return stack != null && ShapeMap.isShape(stack) ? ShapeMap.transferStack(stack, 0) : stack;
    }
    //?}
}
