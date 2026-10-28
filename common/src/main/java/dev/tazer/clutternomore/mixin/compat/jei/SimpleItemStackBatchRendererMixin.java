//? if >=26 {
/*package dev.tazer.clutternomore.mixin.compat.jei;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tazer.clutternomore.client.RenderHelper;
import dev.tazer.clutternomore.platform.condition.IfModPresent;
import mezz.jei.library.render.batch.SimpleItemStackBatchRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@IfModPresent("jei")
@Mixin(SimpleItemStackBatchRenderer.class)
public class SimpleItemStackBatchRendererMixin {

    @WrapOperation(method = "renderBatch", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fakeItem(Lnet/minecraft/world/item/ItemStack;II)V"))
    private void cnm$badge(GuiGraphics graphics, ItemStack stack, int x, int y, Operation<Void> original) {
        RenderHelper.shapeBadge(graphics, stack, x, y);
        original.call(graphics, stack, x, y);
    }
}
*///?}
