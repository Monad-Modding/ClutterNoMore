//? if >=26.3 {
/*package dev.tazer.clutternomore.mixin.item;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.tazer.clutternomore.registry.ShapeBlocks;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CookingFuel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(DataComponentInitializers.class)
public class DataComponentInitializersMixin {

    @ModifyReturnValue(method = "runInitializers", at = @At("RETURN"))
    private Map<ResourceKey<?>, DataComponentMap.Builder> cnm$inheritShapeFuel(Map<ResourceKey<?>, DataComponentMap.Builder> builders) {
        for (Map.Entry<Item, Item> entry : ShapeBlocks.getSourceItems().entrySet()) {
            DataComponentMap.Builder source = builders.get(BuiltInRegistries.ITEM.getResourceKey(entry.getValue()).orElse(null));
            DataComponentMap.Builder shape = builders.get(BuiltInRegistries.ITEM.getResourceKey(entry.getKey()).orElse(null));
            if (source == null || shape == null) {
                continue;
            }
            Object fuel = ((DataComponentMapBuilderAccessor) source).cnm$getMap().get(DataComponents.COOKING_FUEL);
            if (fuel instanceof CookingFuel cookingFuel && !((DataComponentMapBuilderAccessor) shape).cnm$getMap().containsKey(DataComponents.COOKING_FUEL)) {
                shape.set(DataComponents.COOKING_FUEL, cookingFuel);
            }
        }
        return builders;
    }
}
*///?}
