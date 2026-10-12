package dev.tazer.clutternomore.mixin.client;

import dev.tazer.clutternomore.client.ClutterNoMoreClient;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >=26.2 {
/*import net.minecraft.client.gui.Gui;
*///?} else {
import net.minecraft.client.Minecraft;
//?}

//? if >=26.2 {
/*@Mixin(Gui.class)
*///?} else {
@Mixin(Minecraft.class)
//?}
public class ScreenCloseMixin {

    @Inject(method = "setScreen", at = @At("RETURN"))
    private void cnm$closeOverlay(Screen screen, CallbackInfo ci) {
        ClutterNoMoreClient.closeOverlay();
    }
}
