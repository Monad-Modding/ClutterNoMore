package dev.tazer.clutternomore.neoforge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.EventHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EventHooks.class)
public class EventHooksMixin {

    @WrapOperation(method = "lambda$onCreativeModeTabBuildContents$0", at = {
            @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/BuildCreativeModeTabContentsEvent;isParentTab(Lnet/minecraft/world/item/CreativeModeTab$TabVisibility;)Z"),
            @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/BuildCreativeModeTabContentsEvent;isSearchTab(Lnet/minecraft/world/item/CreativeModeTab$TabVisibility;)Z")
    })
    private static boolean cnm$hideShapes(CreativeModeTab.TabVisibility visibility, Operation<Boolean> original, @Local(argsOnly = true) ItemStack stack) {
        return !ShapeMap.isShape(stack.getItem()) && original.call(visibility);
    }
}
