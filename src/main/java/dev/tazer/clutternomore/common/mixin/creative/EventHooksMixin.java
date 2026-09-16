package dev.tazer.clutternomore.common.mixin.creative;

import net.minecraft.world.item.CreativeModeTab;
//? neoforge {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.tazer.clutternomore.common.CHooks;
import net.neoforged.neoforge.event.EventHooks;
import net.minecraft.world.item.ItemStack;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
//? if neoforge {
/*@Mixin(EventHooks.class)
*///?} else {
@Mixin(CreativeModeTab.class)
 //?}
public class EventHooksMixin {

	//? if >26 && neoforge {
	/*@WrapOperation(method = "lambda$onCreativeModeTabBuildContents$0", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/BuildCreativeModeTabContentsEvent;isParentTab(Lnet/minecraft/world/item/CreativeModeTab$TabVisibility;)Z"))
	private static boolean cnm$preventAdd(CreativeModeTab.TabVisibility visibility, Operation<Boolean> original, @Local(name = "stack") ItemStack stack) {
		if (CHooks.denyItem(stack.getItem())) {
			return false;
		} else {
			return original.call(visibility);
		}
	}
	*///?}
}
