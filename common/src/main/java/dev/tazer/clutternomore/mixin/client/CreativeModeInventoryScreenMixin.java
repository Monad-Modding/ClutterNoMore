package dev.tazer.clutternomore.mixin.client;

import dev.tazer.clutternomore.client.ClutterNoMoreClient;
import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeModeInventoryScreenMixin {

    @Inject(method = "getTooltipFromContainerItem", at = @At("RETURN"))
    private void cnm$insertHint(ItemStack stack, CallbackInfoReturnable<List<Component>> cir) {
        if (!ClutterNoMoreClient.CONFIG.detailedTooltips.value() || !ShapeMap.contains(stack.getItem())) return;
        List<Component> tooltip = cir.getReturnValue();
        if (tooltip != null && !tooltip.isEmpty()) {
            tooltip.add(1, ClutterNoMoreClient.getHint());
        }
    }
}
