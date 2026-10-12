//? if <1.21.2 {
package dev.tazer.clutternomore.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.tazer.clutternomore.registry.ShapeBlocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(value = AbstractFurnaceBlockEntity.class, priority = 1100)
public class AbstractFurnaceBlockEntityMixin {

    @ModifyReturnValue(method = "getFuel", at = @At("RETURN"))
    private static Map<Item, Integer> cnm$inheritShapeFuel(Map<Item, Integer> fuel) {
        ShapeBlocks.inheritFuel(fuel);
        return fuel;
    }
}
//?}
