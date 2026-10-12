package dev.tazer.clutternomore.neoforge.mixin;

import dev.tazer.clutternomore.shape.ShapeHooks;
import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.InsertableLinkedOpenCustomHashSet;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuildCreativeModeTabContentsEvent.class)
public abstract class BuildCreativeModeTabContentsEventMixin {

    @Shadow
    public abstract void insertBefore(ItemStack existingEntry, ItemStack newEntry, CreativeModeTab.TabVisibility visibility);

    @Shadow
    public abstract void insertAfter(ItemStack existingEntry, ItemStack newEntry, CreativeModeTab.TabVisibility visibility);

    @Inject(method = "accept", at = @At("HEAD"), cancellable = true)
    private void cnm$hideShapes(ItemStack newEntry, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (ShapeHooks.denyItem(newEntry.getItem())) {
            ci.cancel();
        }
    }

    @Inject(method = "insertAfter", at = @At("HEAD"), cancellable = true)
    private void cnm$insertAfterParent(ItemStack existingEntry, ItemStack newEntry, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (ShapeHooks.denyItem(newEntry.getItem())) {
            ci.cancel();
            return;
        }
        if (ShapeMap.isShape(existingEntry.getItem())) {
            this.insertAfter(ShapeMap.getParent(existingEntry.getItem()).getDefaultInstance(), newEntry, visibility);
            ci.cancel();
        }
    }

    @Inject(method = "insertBefore", at = @At("HEAD"), cancellable = true)
    private void cnm$insertBeforeParent(ItemStack existingEntry, ItemStack newEntry, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (ShapeHooks.denyItem(newEntry.getItem())) {
            ci.cancel();
            return;
        }
        if (ShapeMap.isShape(existingEntry.getItem())) {
            this.insertBefore(ShapeMap.getParent(existingEntry.getItem()).getDefaultInstance(), newEntry, visibility);
            ci.cancel();
        }
    }

    @Inject(method = "assertTargetExists", at = @At("HEAD"), cancellable = true)
    private void cnm$allowMissingTargets(InsertableLinkedOpenCustomHashSet<ItemStack> entries, ItemStack existingEntry, CallbackInfo ci) {
        ci.cancel();
    }
}
