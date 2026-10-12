//? if >=26 {
/*package dev.tazer.clutternomore.mixin.compat.jei;

import com.llamalad7.mixinextras.sugar.Local;
import dev.tazer.clutternomore.client.RenderHelper;
import dev.tazer.clutternomore.platform.condition.IfModPresent;
import mezz.jei.api.ingredients.rendering.BatchRenderElement;
import mezz.jei.library.render.ItemStackRenderer;
import mezz.jei.library.render.batch.SimpleItemStackBatchRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Pseudo
@IfModPresent("jei")
@Mixin(SimpleItemStackBatchRenderer.class)
public class SimpleItemStackBatchRendererMixin {

    @Inject(method = "renderBatch", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fakeItem(Lnet/minecraft/world/item/ItemStack;II)V"))
    private void cnm$badge(GuiGraphics graphics, ItemStackRenderer renderer, List<BatchRenderElement<ItemStack>> elements, CallbackInfo ci, @Local(name = "element") BatchRenderElement<ItemStack> element) {
        RenderHelper.shapeBadge(graphics, element.ingredient(), element.x(), element.y());
    }
}
*///?}
