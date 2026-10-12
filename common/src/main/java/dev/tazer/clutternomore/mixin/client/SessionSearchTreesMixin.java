//? if >=1.20.5 {
package dev.tazer.clutternomore.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(SessionSearchTrees.class)
public class SessionSearchTreesMixin {

    @WrapOperation(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getTooltipLines(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;"))
    private static List<Component> cnm$searchAliases(ItemStack stack, Item.TooltipContext context, Player player, TooltipFlag flag, Operation<List<Component>> original) {
        List<Component> lines = original.call(stack, context, player, flag);
        for (String alias : ShapeMap.getSearchAliases(stack.getItem())) {
            lines.add(Component.literal(alias));
        }
        return lines;
    }
}
//?}
