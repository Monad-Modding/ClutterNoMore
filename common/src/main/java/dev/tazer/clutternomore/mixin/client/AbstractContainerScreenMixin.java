package dev.tazer.clutternomore.mixin.client;

import dev.tazer.clutternomore.client.ClutterNoMoreClient;
import dev.tazer.clutternomore.client.RenderHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >=1.21.9 {
/*import net.minecraft.client.input.KeyEvent;
*///?}

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    //? if >=26 {
    /*@Inject(method = "extractSlot", at = @At("RETURN"))
    private void cnm$shapeIndicator(GuiGraphics graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
    *///?} else {
    @Inject(method = "renderSlot", at = @At("RETURN"))
    private void cnm$shapeIndicator(GuiGraphics graphics, Slot slot, CallbackInfo ci) {
    //?}
        if (ClutterNoMoreClient.isCreativeTabSlot(slot)) {
            RenderHelper.shapeBadge(graphics, slot.getItem(), slot.x, slot.y);
        }
    }

    //? if >=1.21.9 {
    /*@Inject(method = "checkHotbarKeyPressed", at = @At("HEAD"), cancellable = true)
    private void cnm$hotbarSelectsShape(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        KeyMapping[] hotbar = Minecraft.getInstance().options.keyHotbarSlots;
        for (int i = 0; i < hotbar.length; i++) {
            if (hotbar[i].matches(event) && ClutterNoMoreClient.selectHoveredShape(i)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
    *///?} else {
    @Inject(method = "checkHotbarKeyPressed", at = @At("HEAD"), cancellable = true)
    private void cnm$hotbarSelectsShape(int keyCode, int scanCode, CallbackInfoReturnable<Boolean> cir) {
        KeyMapping[] hotbar = Minecraft.getInstance().options.keyHotbarSlots;
        for (int i = 0; i < hotbar.length; i++) {
            if (hotbar[i].matches(keyCode, scanCode) && ClutterNoMoreClient.selectHoveredShape(i)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
    //?}
}
