package dev.tazer.clutternomore.forge.mixin.compat.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import dev.tazer.clutternomore.platform.condition.IfModPresent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@IfModPresent("create")
@Mixin(ItemRequirement.class)
public class ItemRequirementMixin {

    @WrapOperation(method = "defaultOf", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;hasProperty(Lnet/minecraft/world/level/block/state/properties/Property;)Z", remap = true), remap = false)
    private static boolean cnm$slabsCostOne(BlockState state, Property<?> property, Operation<Boolean> original) {
        return false;
    }
}
