package dev.tazer.clutternomore.forge.mixin;

import dev.tazer.clutternomore.shape.ShapeHooks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuildCreativeModeTabContentsEvent.class)
public abstract class BuildCreativeModeTabContentsEventMixin {

    @Inject(method = "accept*", at = @At("HEAD"), cancellable = true)
    private void cnm$hideShapes(ItemStack newEntry, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (ShapeHooks.denyItem(newEntry.getItem())) {
            ci.cancel();
        }
    }
}
