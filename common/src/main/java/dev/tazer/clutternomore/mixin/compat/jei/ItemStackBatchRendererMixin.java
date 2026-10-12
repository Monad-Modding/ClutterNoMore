//? if <1.21.4 {
package dev.tazer.clutternomore.mixin.compat.jei;

import dev.tazer.clutternomore.client.RenderHelper;
import dev.tazer.clutternomore.platform.condition.IfModPresent;
import mezz.jei.library.render.batch.ItemStackBatchRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@IfModPresent("jei")
@Mixin(ItemStackBatchRenderer.class)
public class ItemStackBatchRendererMixin {

    @Inject(method = "renderItem", at = @At("RETURN"), remap = false)
    private void cnm$badge(GuiGraphics graphics, ItemRenderer renderer, BakedModel model, ItemStack stack, int x, int y, CallbackInfo ci) {
        RenderHelper.shapeBadge(graphics, stack, x, y);
    }
}
//?}
