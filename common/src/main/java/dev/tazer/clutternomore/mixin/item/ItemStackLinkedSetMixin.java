//? if <1.20.5 {
/*package dev.tazer.clutternomore.mixin.item;

import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemStackLinkedSet.class)
public class ItemStackLinkedSetMixin {

    @ModifyVariable(method = "hashStackAndTag", at = @At("HEAD"), argsOnly = true)
    private static ItemStack cnm$hashAsParent(ItemStack stack) {
        return stack != null && ShapeMap.isShape(stack) ? ShapeMap.transferStack(stack, 0) : stack;
    }
}
*///?}
