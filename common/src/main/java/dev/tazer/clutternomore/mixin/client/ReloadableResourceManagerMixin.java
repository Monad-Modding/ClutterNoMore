package dev.tazer.clutternomore.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.client.asset.AssetGenerator;
import dev.tazer.clutternomore.data.RuntimePack;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(value = ReloadableResourceManager.class, priority = 900)
public class ReloadableResourceManagerMixin {

    @Shadow
    @Final
    private PackType type;

    @WrapOperation(method = "createReload", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/SimpleReloadInstance;create(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Z)Lnet/minecraft/server/packs/resources/ReloadInstance;"))
    private ReloadInstance cnm$generateAssets(ResourceManager manager, List<PreparableReloadListener> listeners, Executor background, Executor game, CompletableFuture<Unit> waiting, boolean profiled, Operation<ReloadInstance> original) {
        if (this.type == PackType.CLIENT_RESOURCES) {
            List<PackResources> packs = manager.listPacks().filter(pack -> pack != ClutterNoMore.RESOURCES).toList();
            AssetGenerator.generate(RuntimePack.detached(this.type, packs));
        }
        return original.call(manager, listeners, background, game, waiting, profiled);
    }
}
