package dev.tazer.clutternomore.network;

import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record ChangeShape(int containerId, int slot, int shapeIndex) {

    public static final int HELD = -1;

    public static ChangeShape read(FriendlyByteBuf buf) {
        return new ChangeShape(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void write(FriendlyByteBuf buf, ChangeShape packet) {
        buf.writeInt(packet.containerId);
        buf.writeInt(packet.slot);
        buf.writeInt(packet.shapeIndex);
    }

    public static void handle(ChangeShape packet, ServerPlayer player) {
        if (packet.shapeIndex < 0 || player.isSpectator()) return;
        if (packet.slot == HELD) {
            ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (!ShapeMap.contains(held.getItem())) return;
            ItemStack next = ShapeMap.transferStack(held, packet.shapeIndex);
            if (ShapeMap.isUnchanged(held, next)) return;
            player.setItemInHand(InteractionHand.MAIN_HAND, next);
            return;
        }
        AbstractContainerMenu menu = findMenu(player, packet.containerId);
        if (menu == null || packet.slot < 0 || packet.slot >= menu.slots.size() || !menu.stillValid(player)) return;
        Slot slot = menu.getSlot(packet.slot);
        ItemStack current = slot.getItem();
        if (!ShapeMap.contains(current.getItem()) || !slot.mayPickup(player)) return;
        ItemStack next = ShapeMap.transferStack(current, packet.shapeIndex);
        if (ShapeMap.isUnchanged(current, next) || !slot.mayPlace(next)) return;
        slot.setByPlayer(next);
        menu.sendAllDataToRemote();
    }

    @Nullable
    private static AbstractContainerMenu findMenu(ServerPlayer player, int containerId) {
        if (player.containerMenu != null && player.containerMenu.containerId == containerId) return player.containerMenu;
        if (player.inventoryMenu.containerId == containerId) return player.inventoryMenu;
        return null;
    }
}
