package dev.tazer.clutternomore.mixin.compat.controlify;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.controlify.ingame.InGameInputHandler;
import dev.tazer.clutternomore.client.ClutterNoMoreClient;
import dev.tazer.clutternomore.platform.condition.IfModPresent;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@IfModPresent("controlify")
@Mixin(InGameInputHandler.class)
public class InGameInputHandlerMixin {

    //? if >=26 {
    /*@WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlot(I)V", ordinal = 0, remap = true), remap = false)
    private void cnm$previousShape(Inventory inventory, int index, Operation<Void> original) {
        if (!ClutterNoMoreClient.previousSlot()) {
            original.call(inventory, index);
        }
    }

    @WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlot(I)V", ordinal = 1, remap = true), remap = false)
    private void cnm$nextShape(Inventory inventory, int index, Operation<Void> original) {
        if (!ClutterNoMoreClient.nextSlot()) {
            original.call(inventory, index);
        }
    }
    *///?} else {
    @WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;swapPaint(D)V", ordinal = 0, remap = true), remap = false)
    private void cnm$previousShape(Inventory inventory, double direction, Operation<Void> original) {
        if (!ClutterNoMoreClient.previousSlot()) {
            original.call(inventory, direction);
        }
    }

    @WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;swapPaint(D)V", ordinal = 1, remap = true), remap = false)
    private void cnm$nextShape(Inventory inventory, double direction, Operation<Void> original) {
        if (!ClutterNoMoreClient.nextSlot()) {
            original.call(inventory, direction);
        }
    }
    //?}
}
