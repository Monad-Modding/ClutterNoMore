package dev.tazer.clutternomore.fabric.mixin;

import dev.tazer.clutternomore.registry.ShapeBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltInRegistries.class)
public class BuiltInRegistriesMixin {

    @Inject(method = "bootStrap", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/registries/BuiltInRegistries;freeze()V", shift = At.Shift.BEFORE))
    private static void cnm$registerVariants(CallbackInfo ci) {
        ShapeBlocks.registerVariants();
    }
}
