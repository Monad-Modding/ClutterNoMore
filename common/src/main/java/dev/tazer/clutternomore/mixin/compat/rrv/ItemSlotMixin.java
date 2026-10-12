//? if >=26 {
/*package dev.tazer.clutternomore.mixin.compat.rrv;

import cc.cassian.rrv.common.overlay.ItemSlot;
import com.llamalad7.mixinextras.sugar.Local;
import dev.tazer.clutternomore.client.RenderHelper;
import dev.tazer.clutternomore.platform.condition.IfModPresent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@IfModPresent("rrv")
@Mixin(ItemSlot.class)
public abstract class ItemSlotMixin {

    @Shadow
    @Final
    private int x;

    @Shadow
    @Final
    private int y;

    @Shadow
    public abstract ItemStack getStack();

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void cnm$badge(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci, @Local(name = "recipe") String recipe) {
        if (recipe == null) {
            RenderHelper.shapeBadge(graphics, this.getStack(), this.x + 3, this.y + 2);
        }
    }
}
*///?}
