package dev.tazer.clutternomore.fabric;

import dev.tazer.clutternomore.network.PacketType;
import dev.tazer.clutternomore.platform.Platform;
import dev.tazer.clutternomore.registry.ShapeBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
//? if <1.20.5 {
/*import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
*///?}
//? if >=26 {
/*import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
*///?}

import java.nio.file.Path;
import java.util.Map;
//? if >=26 {
/*import java.util.List;
*///?}

public final class FabricPlatform implements Platform {

    @Override
    public String getName() {
        return "fabric";
    }

    @Override
    public boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public Path getGameDirectory() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public <T> void register(Registry<T> registry, ResourceLocation id, T value) {
        Registry.register(registry, id, value);
    }

    @Override
    public void beforeShapeRegistration() {
    }

    @Override
    public void afterShapeRegistration() {
        for (Map.Entry<Block, Block> stage : ShapeBlocks.getOxidationStages().entrySet()) {
            //? if >=26 {
            /*OxidizableBlocksRegistry.registerNextStage(stage.getKey(), stage.getValue());
            *///?} else {
            OxidizableBlocksRegistry.registerOxidizableBlockPair(stage.getKey(), stage.getValue());
            //?}
        }
        for (Map.Entry<Block, Block> waxed : ShapeBlocks.getWaxedBlocks().entrySet()) {
            //? if >=26 {
            /*OxidizableBlocksRegistry.registerWaxable(waxed.getKey(), waxed.getValue());
            *///?} else {
            OxidizableBlocksRegistry.registerWaxableBlockPair(waxed.getKey(), waxed.getValue());
            //?}
        }
    }

    @Override
    public <T> void sendToServer(PacketType<T> type, T packet) {
        //? if >=1.20.5 {
        ClientPlayNetworking.send(type.wrap(packet));
        //?} else {
        /*FriendlyByteBuf buf = PacketByteBufs.create();
        type.write(buf, packet);
        ClientPlayNetworking.send(type.getId(), buf);
        *///?}
    }

    @Override
    public <T> void sendToPlayer(ServerPlayer player, PacketType<T> type, T packet) {
        //? if >=1.20.5 {
        ServerPlayNetworking.send(player, type.wrap(packet));
        //?} else {
        /*FriendlyByteBuf buf = PacketByteBufs.create();
        type.write(buf, packet);
        ServerPlayNetworking.send(player, type.getId(), buf);
        *///?}
    }

    //? if >=26.3 {
    /*@Override
    public RecipeMap createRecipeMap(List<RecipeHolder<?>> recipes) {
        return FabricRecipeMap.create(recipes);
    }
    *///?} else if >=26 {
    /*@Override
    public RecipeMap createRecipeMap(List<RecipeHolder<?>> recipes) {
        return RecipeMap.create(recipes);
    }
    *///?}
}
