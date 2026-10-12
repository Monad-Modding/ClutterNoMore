package dev.tazer.clutternomore.compat;

//? if <1.21.4 {
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmiStackInteraction;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Map;

@EmiEntrypoint
public final class EmiCompat implements EmiPlugin {

    public static boolean isHoveringIngredient() {
        EmiStackInteraction interaction = EmiApi.getHoveredStack(false);
        return interaction != null && !interaction.isEmpty();
    }

    @Override
    public void register(EmiRegistry registry) {
        registry.removeEmiStacks(stack -> stack.getId().getNamespace().equals(ClutterNoMore.MOD_ID) || ShapeMap.isShape(stack.getItemStack().getItem()));
        for (Map.Entry<Item, List<Item>> entry : ShapeMap.getShapesByParent().entrySet()) {
            EmiStack parent = EmiStack.of(entry.getKey().getDefaultInstance());
            for (Item shape : entry.getValue()) {
                registry.addAlias(parent, Component.translatable(shape.getDescriptionId()));
            }
        }
    }
}
//?}
