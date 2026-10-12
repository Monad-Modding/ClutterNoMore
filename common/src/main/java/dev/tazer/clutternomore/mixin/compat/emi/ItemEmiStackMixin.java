//? if <1.21.4 {
package dev.tazer.clutternomore.mixin.compat.emi;

import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.ItemEmiStack;
import dev.tazer.clutternomore.client.RenderHelper;
import dev.tazer.clutternomore.platform.condition.IfModPresent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@IfModPresent("emi")
@Mixin(ItemEmiStack.class)
public class ItemEmiStackMixin {

    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIFI)V", at = @At("RETURN"), remap = false)
    private void cnm$badge(GuiGraphics graphics, int x, int y, float delta, int flags, CallbackInfo ci) {
        RenderHelper.shapeBadge(graphics, ((EmiStack) (Object) this).getItemStack(), x, y);
    }

    @Inject(method = "renderForBatch(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/GuiGraphics;IIIF)V", at = @At("RETURN"), remap = false)
    private void cnm$batchBadge(MultiBufferSource buffers, GuiGraphics graphics, int x, int y, int z, float delta, CallbackInfo ci) {
        RenderHelper.shapeBadge(graphics, ((EmiStack) (Object) this).getItemStack(), x, y);
    }
}
//?}
