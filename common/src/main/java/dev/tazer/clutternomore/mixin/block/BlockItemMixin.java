package dev.tazer.clutternomore.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.tazer.clutternomore.shape.ShapeHooks;
import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//? if >=1.20.5 {
import net.minecraft.world.entity.LivingEntity;
//?}

@Mixin(BlockItem.class)
public class BlockItemMixin {

    //? if >=1.20.5 {
    @WrapOperation(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;consume(ILnet/minecraft/world/entity/LivingEntity;)V"))
    private void cnm$keepDoubledShape(ItemStack stack, int amount, LivingEntity entity, Operation<Void> original, @Local(ordinal = 0) BlockState placed) {
        if (cnm$consumes(stack, placed)) {
            original.call(stack, amount, entity);
        }
    }
    //?} else {
    /*@WrapOperation(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private void cnm$keepDoubledShape(ItemStack stack, int amount, Operation<Void> original, @Local(ordinal = 0) BlockState placed) {
        if (cnm$consumes(stack, placed)) {
            original.call(stack, amount);
        }
    }
    *///?}

    private static boolean cnm$consumes(ItemStack stack, BlockState placed) {
        return !ShapeMap.isShape(stack.getItem()) || !ShapeHooks.isDoubled(placed);
    }
}
