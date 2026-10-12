package dev.tazer.clutternomore.fabric.mixin.client;

import dev.tazer.clutternomore.client.ClutterNoMoreClient;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonInfo;
*///?}

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    //? if >=1.21.9 {
    /*@Inject(method = "onButton", at = @At("HEAD"))
    private void cnm$shapeButton(long window, MouseButtonInfo info, int action, CallbackInfo ci) {
        ClutterNoMoreClient.onKeyInput(info.button(), action);
    }
    *///?} else {
    @Inject(method = "onPress", at = @At("HEAD"))
    private void cnm$shapeButton(long window, int button, int action, int modifiers, CallbackInfo ci) {
        ClutterNoMoreClient.onKeyInput(button, action);
    }
    //?}

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void cnm$scrollShapes(long window, double scrollX, double scrollY, CallbackInfo ci) {
        if (ClutterNoMoreClient.onMouseScrolled(scrollY)) {
            ci.cancel();
        }
    }
}
