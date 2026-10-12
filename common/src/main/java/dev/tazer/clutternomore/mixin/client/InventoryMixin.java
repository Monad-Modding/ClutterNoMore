//? if <1.21.2 {
package dev.tazer.clutternomore.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.tazer.clutternomore.network.ChangeShape;
import dev.tazer.clutternomore.network.ClutterNoMoreNetwork;
import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Inventory.class)
public abstract class InventoryMixin {

    @WrapMethod(method = "findSlotMatchingItem")
    private int cnm$pickShape(ItemStack target, Operation<Integer> original) {
        int index = original.call(target);
        if (index == -1) return index;
        Inventory inventory = (Inventory) (Object) this;
        ItemStack current = inventory.items.get(index);
        if (!ShapeMap.inSameShapeSet(target.getItem(), current.getItem())) return index;
        int shape = ShapeMap.getShapes(current.getItem()).indexOf(target.getItem());
        LocalPlayer player = Minecraft.getInstance().player;
        if (shape < 0 || player == null) return index;
        inventory.items.set(index, target.copyWithCount(current.getCount()));
        ClutterNoMoreNetwork.sendToServer(ClutterNoMoreNetwork.CHANGE_SHAPE, new ChangeShape(player.inventoryMenu.containerId, index < 9 ? index + 36 : index, shape));
        return index;
    }
}
//?}
