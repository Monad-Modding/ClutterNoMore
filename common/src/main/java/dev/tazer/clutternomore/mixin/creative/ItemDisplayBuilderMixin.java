package dev.tazer.clutternomore.mixin.creative;

import dev.tazer.clutternomore.shape.ShapeHooks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeTab.ItemDisplayBuilder.class)
public class ItemDisplayBuilderMixin {

    @Inject(method = "accept", at = @At("HEAD"), cancellable = true)
    private void cnm$hideShapes(ItemStack stack, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (ShapeHooks.denyItem(stack.getItem())) {
            ci.cancel();
        }
    }
}
