//? if >=26 {
/*package dev.tazer.clutternomore.mixin.compat.jei;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.tazer.clutternomore.client.RenderHelper;
import dev.tazer.clutternomore.platform.condition.IfModPresent;
import mezz.jei.api.ingredients.rendering.BatchRenderElement;
import mezz.jei.library.render.ItemStackRenderer;
import mezz.jei.library.render.batch.SimpleItemStackBatchRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import java.util.List;

@Pseudo
@IfModPresent("jei")
@Mixin(SimpleItemStackBatchRenderer.class)
public class SimpleItemStackBatchRendererMixin {

    @WrapMethod(method = "renderBatch", remap = false)
    private void cnm$badge(GuiGraphics graphics, ItemStackRenderer renderer, List<BatchRenderElement<ItemStack>> elements, Operation<Void> original) {
        for (BatchRenderElement<ItemStack> element : elements) {
            RenderHelper.shapeBadge(graphics, element.ingredient(), element.x(), element.y());
            original.call(graphics, renderer, List.of(element));
        }
    }
}
*///?}
