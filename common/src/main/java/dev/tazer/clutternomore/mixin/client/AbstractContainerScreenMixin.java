package dev.tazer.clutternomore.mixin.client;

import dev.tazer.clutternomore.client.ClutterNoMoreClient;
import dev.tazer.clutternomore.client.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}
