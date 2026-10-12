//? if >=1.21.2 {
/*package dev.tazer.clutternomore.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @WrapOperation(method = "tryPickItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;findSlotMatchingItem(Lnet/minecraft/world/item/ItemStack;)I"))
    private int cnm$pickShape(Inventory inventory, ItemStack target, Operation<Integer> original) {
        int index = original.call(inventory, target);
        if (index == -1) return index;
        ItemStack current = inventory.getNonEquipmentItems().get(index);
        if (ShapeMap.inSameShapeSet(target.getItem(), current.getItem())) {
            ItemStack replaced = target.copyWithCount(current.getCount());
            inventory.setItem(index, replaced);
            ((ServerGamePacketListenerImpl) (Object) this).send(new ClientboundSetPlayerInventoryPacket(index, replaced));
        }
        return index;
    }
}
*///?}
