//? if <1.21.4 {
package dev.tazer.clutternomore.mixin.compat.jei;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.tazer.clutternomore.client.RenderHelper;
import dev.tazer.clutternomore.platform.condition.IfModPresent;
import mezz.jei.library.render.batch.ItemStackBatchRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@IfModPresent("jei")
@Mixin(ItemStackBatchRenderer.class)
public class ItemStackBatchRendererMixin {

    @WrapMethod(method = "renderItem", remap = false)
    private void cnm$badge(GuiGraphics graphics, ItemRenderer renderer, BakedModel model, ItemStack stack, int x, int y, Operation<Void> original) {
        original.call(graphics, renderer, model, stack, x, y);
        RenderHelper.shapeBadge(graphics, stack, x, y);
    }
}
//?}
