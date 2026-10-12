package dev.tazer.clutternomore.mixin.creative;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeModeTabs.class)
public interface CreativeModeTabsAccessor {

    @Accessor("CACHED_PARAMETERS")
    static void cnm$setCachedParameters(@Nullable CreativeModeTab.ItemDisplayParameters parameters) {
        throw new AssertionError();
    }
}
