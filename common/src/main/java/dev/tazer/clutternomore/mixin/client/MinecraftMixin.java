package dev.tazer.clutternomore.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tazer.clutternomore.client.ClutterNoMoreClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//? if >=1.21.9 {
/*import net.minecraft.world.entity.player.Inventory;
*///?} else {
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.KeyMapping;
//?}

@Mixin(Minecraft.class)
public class MinecraftMixin {

    //? if >=1.21.9 {
    /*@WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlot(I)V"))
    private void cnm$hotbarSelectsShape(Inventory inventory, int index, Operation<Void> original) {
        if (!ClutterNoMoreClient.changeHotbarSlot(index)) {
            original.call(inventory, index);
        }
    }
    *///?} else {
    @WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 2))
    private boolean cnm$hotbarSelectsShape(KeyMapping key, Operation<Boolean> original, @Local int index) {
        boolean clicked = original.call(key);
        return clicked && !ClutterNoMoreClient.changeHotbarSlot(index);
    }
    //?}
}
