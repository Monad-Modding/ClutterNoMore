//? if <26 {
package dev.tazer.clutternomore.neoforge.compat;

import com.ldtteam.domumornamentum.block.IMateriallyTexturedBlock;
import com.ldtteam.domumornamentum.block.IMateriallyTexturedBlockComponent;
import com.ldtteam.domumornamentum.client.model.data.MaterialTextureData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DomumOrnamentumCompat {

    public static void remap(ItemStack from, ItemStack to) {
        if (!(from.getItem() instanceof BlockItem source) || !(to.getItem() instanceof BlockItem target)) return;
        if (!(source.getBlock() instanceof IMateriallyTexturedBlock sourceBlock) || !(target.getBlock() instanceof IMateriallyTexturedBlock targetBlock)) return;
        MaterialTextureData data = MaterialTextureData.readFromItemStack(from);
        if (data.isEmpty()) return;
        Map<ResourceLocation, Block> textures = data.getTexturedComponents();
        List<IMateriallyTexturedBlockComponent> sourceComponents = new ArrayList<>(sourceBlock.getComponents());
        List<IMateriallyTexturedBlockComponent> targetComponents = new ArrayList<>(targetBlock.getComponents());
        boolean[] used = new boolean[sourceComponents.size()];
        MaterialTextureData.Builder builder = MaterialTextureData.builder();
        for (int i = 0; i < targetComponents.size(); i++) {
            IMateriallyTexturedBlockComponent component = targetComponents.get(i);
            int match = findMatch(sourceComponents, used, component, i);
            Block texture = null;
            if (match >= 0) {
                used[match] = true;
                texture = textures.get(sourceComponents.get(match).getId());
            }
            if (texture == null || !texture.defaultBlockState().is(component.getValidSkins())) {
                texture = component.getDefault();
            }
            builder.setComponent(component.getId(), texture);
        }
        builder.build().writeToItemStack(to);
    }

    private static int findMatch(List<IMateriallyTexturedBlockComponent> sourceComponents, boolean[] used, IMateriallyTexturedBlockComponent component, int index) {
        for (int i = 0; i < sourceComponents.size(); i++) {
            if (!used[i] && sourceComponents.get(i).getValidSkins().equals(component.getValidSkins())) return i;
        }
        if (index < sourceComponents.size() && !used[index]) return index;
        for (int i = 0; i < sourceComponents.size(); i++) {
            if (!used[i] && sourceComponents.get(i).getId().equals(component.getId())) return i;
        }
        return -1;
    }
}
//?}
