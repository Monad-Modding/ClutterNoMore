package dev.tazer.clutternomore.neoforge.mixin;

import net.neoforged.neoforge.event.EventHooks;
import org.spongepowered.asm.mixin.Mixin;
//? if >=26 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.tazer.clutternomore.shape.ShapeHooks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.At;
*///?}

@Mixin(EventHooks.class)
public class EventHooksMixin {

    //? if >=26 {
    /*@WrapOperation(method = "lambda$onCreativeModeTabBuildContents$0", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/BuildCreativeModeTabContentsEvent;isParentTab(Lnet/minecraft/world/item/CreativeModeTab$TabVisibility;)Z"))
    private static boolean cnm$hideShapes(CreativeModeTab.TabVisibility visibility, Operation<Boolean> original, @Local(name = "stack") ItemStack stack) {
        return !ShapeHooks.denyItem(stack.getItem()) && original.call(visibility);
    }
    *///?}
}
