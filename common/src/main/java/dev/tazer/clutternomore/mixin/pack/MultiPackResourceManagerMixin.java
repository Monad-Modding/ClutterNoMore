package dev.tazer.clutternomore.mixin.pack;

import dev.tazer.clutternomore.data.RuntimePack;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(MultiPackResourceManager.class)
public class MultiPackResourceManagerMixin {

    @ModifyVariable(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;copyOf(Ljava/util/Collection;)Ljava/util/List;", ordinal = 0), argsOnly = true)
    private List<PackResources> cnm$attachRuntimePack(List<PackResources> packs, PackType type) {
        return RuntimePack.attach(type, packs);
    }
}
