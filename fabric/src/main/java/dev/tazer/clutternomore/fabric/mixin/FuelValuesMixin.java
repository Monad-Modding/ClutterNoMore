//? if >=1.21.2 && <26.3 {
/*package dev.tazer.clutternomore.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.tazer.clutternomore.registry.ShapeBlocks;
import it.unimi.dsi.fastutil.objects.Object2IntSortedMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.FuelValues;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FuelValues.class)
public abstract class FuelValuesMixin {

    @Shadow
    @Final
    private Object2IntSortedMap<Item> values;

    @ModifyReturnValue(method = "vanillaBurnTimes(Lnet/minecraft/core/HolderLookup$Provider;Lnet/minecraft/world/flag/FeatureFlagSet;I)Lnet/minecraft/world/level/block/entity/FuelValues;", at = @At("RETURN"))
    private static FuelValues cnm$inheritShapeFuel(FuelValues fuel) {
        ShapeBlocks.inheritFuel(((FuelValuesMixin) (Object) fuel).values);
        return fuel;
    }
}
*///?}
