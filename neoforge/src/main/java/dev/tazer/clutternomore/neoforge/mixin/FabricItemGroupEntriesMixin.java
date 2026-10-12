package dev.tazer.clutternomore.neoforge.mixin;

import dev.tazer.clutternomore.shape.ShapeHooks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >=26 {
/*import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
*///?} else {
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
//?}

@Pseudo
//? if >=26 {
/*@Mixin(FabricCreativeModeTabOutput.class)
*///?} else {
@Mixin(FabricItemGroupEntries.class)
//?}
public abstract class FabricItemGroupEntriesMixin {

    @Inject(method = "accept", at = @At("HEAD"), cancellable = true)
    private void cnm$hideShapes(ItemStack stack, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (ShapeHooks.denyItem(stack.getItem())) {
            ci.cancel();
        }
    }

    @Inject(method = "isEnabled", remap = false, at = @At("RETURN"), cancellable = true)
    private void cnm$disableShapes(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (ShapeHooks.denyItem(stack.getItem())) {
            cir.setReturnValue(false);
        }
    }
}
