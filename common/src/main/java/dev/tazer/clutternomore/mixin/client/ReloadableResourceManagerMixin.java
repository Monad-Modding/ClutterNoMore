package dev.tazer.clutternomore.mixin.client;

import dev.tazer.clutternomore.client.asset.AssetGenerator;
import dev.tazer.clutternomore.data.RuntimePack;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManager.class)
public class ReloadableResourceManagerMixin {

    @Shadow
    @Final
    private PackType type;

    @Inject(method = "createReload", at = @At("HEAD"))
    private void cnm$generateAssets(Executor background, Executor game, CompletableFuture<Unit> waiting, List<PackResources> packs, CallbackInfoReturnable<?> cir) {
        if (this.type == PackType.CLIENT_RESOURCES) {
            AssetGenerator.generate(RuntimePack.detached(this.type, packs));
        }
    }
}
