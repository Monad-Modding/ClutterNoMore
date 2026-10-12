//? if <1.21 {
/*package dev.tazer.clutternomore.fabric.mixin.compat.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import dev.tazer.clutternomore.platform.condition.IfModPresent;
import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@IfModPresent("create")
@Mixin(ItemRequirement.StackRequirement.class)
public class StackRequirementMixin {

    @WrapOperation(method = "matches", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean cnm$matchShapeSet(ItemStack first, ItemStack second, Operation<Boolean> original) {
        return original.call(first, second) || ShapeMap.inSameShapeSet(first.getItem(), second.getItem());
    }
}
*///?}
