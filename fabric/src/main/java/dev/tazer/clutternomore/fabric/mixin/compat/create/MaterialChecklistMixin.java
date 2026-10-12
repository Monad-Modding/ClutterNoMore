//? if <1.21 {
/*package dev.tazer.clutternomore.fabric.mixin.compat.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.schematics.cannon.MaterialChecklist;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import dev.tazer.clutternomore.platform.condition.IfModPresent;
import dev.tazer.clutternomore.shape.ShapeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Pseudo
@IfModPresent("create")
@Mixin(MaterialChecklist.class)
public class MaterialChecklistMixin {

    @WrapOperation(method = "require", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/schematics/requirement/ItemRequirement;getRequiredItems()Ljava/util/List;"))
    private List<ItemRequirement.StackRequirement> cnm$requireParents(ItemRequirement requirement, Operation<List<ItemRequirement.StackRequirement>> original) {
        List<ItemRequirement.StackRequirement> items = original.call(requirement);
        List<ItemRequirement.StackRequirement> rewritten = new ArrayList<>(items.size());
        for (ItemRequirement.StackRequirement item : items) {
            if (item.usage == ItemRequirement.ItemUseType.DAMAGE || item instanceof ItemRequirement.StrictNbtStackRequirement) {
                rewritten.add(item);
            } else {
                rewritten.add(new ItemRequirement.StackRequirement(ShapeMap.transferStack(item.stack, 0), item.usage));
            }
        }
        return rewritten;
    }
}
*///?}
