package dev.tazer.clutternomore.mixin.item;

import dev.tazer.clutternomore.shape.ShapeMap;
import dev.tazer.clutternomore.shape.ShapeTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
    private void cnm$shapeTooltip(ItemStack stack, CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
        if (!ShapeMap.contains(stack.getItem())) return;
        int count = ShapeMap.getShapes(stack.getItem()).size();
        List<ItemStack> stacks = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            stacks.add(ShapeMap.transferStack(stack, i));
        }
        cir.setReturnValue(Optional.of(new ShapeTooltip(stacks, Math.max(ShapeMap.getShapeIndex(stack), 0))));
    }
}
