//? if >=26.3 {
/*package dev.tazer.clutternomore.mixin.item;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DataComponentMap.Builder.class)
public interface DataComponentMapBuilderAccessor {

    @Accessor("map")
    Reference2ObjectMap<DataComponentType<?>, Object> cnm$getMap();
}
*///?}
