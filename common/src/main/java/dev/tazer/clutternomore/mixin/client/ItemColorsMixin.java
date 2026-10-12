//? if <1.21.4 {
package dev.tazer.clutternomore.mixin.client;

import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if <1.20.5 {
/*import net.minecraft.nbt.CompoundTag;
*///?}

@Mixin(ItemColors.class)
public class ItemColorsMixin {

    @Inject(method = "getColor(Lnet/minecraft/world/item/ItemStack;I)I", at = @At("HEAD"), cancellable = true)
    private void cnm$parentColor(ItemStack stack, int tintIndex, CallbackInfoReturnable<Integer> cir) {
        Item item = stack.getItem();
        if (!ShapeMap.isShape(item)) return;
        Item parent = ShapeMap.getParent(item);
        //? if >=1.20.5 {
        ItemStack parentStack = stack.transmuteCopy(parent, stack.getCount());
        //?} else {
        /*ItemStack parentStack = new ItemStack(parent, stack.getCount());
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            parentStack.setTag(tag.copy());
        }
        *///?}
        int color = ((ItemColors) (Object) this).getColor(parentStack, tintIndex);
        if (color != -1) {
            cir.setReturnValue(color);
        }
    }
}
//?}
