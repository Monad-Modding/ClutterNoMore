package dev.tazer.clutternomore.mixin.compat.mousetweaks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tazer.clutternomore.platform.condition.IfModPresent;
import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import yalter.mousetweaks.Main;

@Pseudo
@IfModPresent("mousetweaks")
@Mixin(Main.class)
public class MainMixin {

    @WrapOperation(method = "areStacksCompatible", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z", remap = true), remap = false)
    private static boolean cnm$sameShapeSet(ItemStack first, ItemStack second, Operation<Boolean> original) {
        return original.call(first, second) || ShapeMap.inSameShapeSet(first.getItem(), second.getItem());
    }
}
