package dev.tazer.clutternomore.compat;

//? if >=26 {
/*import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.overlay.AbstractRrvOverlay;
import cc.cassian.rrv.common.overlay.ItemSlot;
import cc.cassian.rrv.common.overlay.OverlayManager;
import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public final class RrvCompat {

    private RrvCompat() {
    }

    public static void hide(Item item) {
        ItemView.excludeItem(item);
        ItemView.addAlias(ShapeMap.getParent(item), Component.translatable(item.getDescriptionId()).getString());
    }

    public static boolean isHoveringIngredient() {
        for (AbstractRrvOverlay overlay : OverlayManager.INSTANCE.screenContextMap().keySet()) {
            for (ItemSlot slot : overlay.itemSlots()) {
                if (slot.isHovered()) return true;
            }
        }
        return false;
    }
}
*///?}
