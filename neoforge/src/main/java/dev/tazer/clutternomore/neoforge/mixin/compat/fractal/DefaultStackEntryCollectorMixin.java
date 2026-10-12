package dev.tazer.clutternomore.neoforge.mixin.compat.fractal;

import de.dafuqs.fractal.api.DefaultStackEntryCollector;
import dev.tazer.clutternomore.platform.condition.IfModPresent;
import dev.tazer.clutternomore.shape.ShapeHooks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@IfModPresent("fractal")
@Mixin(DefaultStackEntryCollector.class)
public abstract class DefaultStackEntryCollectorMixin {

    @Inject(method = "accept(Lnet/minecraft/world/level/ItemLike;Lnet/minecraft/world/item/CreativeModeTab$TabVisibility;)V", at = @At("HEAD"), cancellable = true)
    private void cnm$hideShapeItem(ItemLike item, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (ShapeHooks.denyItem(item.asItem())) {
            ci.cancel();
        }
    }

    @Inject(method = "accept(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/CreativeModeTab$TabVisibility;)V", at = @At("HEAD"), cancellable = true)
    private void cnm$hideShapeStack(ItemStack stack, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (ShapeHooks.denyItem(stack.getItem())) {
            ci.cancel();
        }
    }
}
