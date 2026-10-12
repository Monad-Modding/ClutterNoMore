package dev.tazer.clutternomore.fabric.mixin.client;

import dev.tazer.clutternomore.client.ClutterNoMoreClient;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >=1.21.9 {
/*import net.minecraft.client.input.KeyEvent;
*///?}

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    //? if >=1.21.9 {
    /*@Inject(method = "keyPress", at = @At("HEAD"))
    private void cnm$getShapeKey(long window, int action, KeyEvent event, CallbackInfo ci) {
        ClutterNoMoreClient.onKeyInput(event.key(), action);
    }
    *///?} else {
    @Inject(method = "keyPress", at = @At("HEAD"))
    private void cnm$getShapeKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        ClutterNoMoreClient.onKeyInput(key, action);
    }
    //?}
}
