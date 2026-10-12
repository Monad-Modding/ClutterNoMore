package dev.tazer.clutternomore.forge;

import dev.tazer.clutternomore.network.PacketType;
import dev.tazer.clutternomore.platform.Platform;
import dev.tazer.clutternomore.registry.ShapeBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import java.nio.file.Path;
import java.util.Optional;

public final class ForgePlatform implements Platform {

    @Override
    public String getName() {
        return "forge";
    }

    @Override
    public boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    @Override
    public boolean isClient() {
        return FMLEnvironment.dist.isClient();
    }

    @Override
    public Path getGameDirectory() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public <T> void register(Registry<T> registry, ResourceLocation id, T value) {
        ForgeRegistry<T> forgeRegistry = RegistryManager.ACTIVE.getRegistry(registry.key());
        forgeRegistry.register(id, value);
    }

    @Override
    public void beforeShapeRegistration() {
        RegistryManager.ACTIVE.getRegistry(Registries.BLOCK).unfreeze();
        RegistryManager.ACTIVE.getRegistry(Registries.ITEM).unfreeze();
    }

    @Override
    public void afterShapeRegistration() {
    }

    @Override
    public Optional<Block> getNextOxidation(Block block) {
        Optional<Block> next = WeatheringCopper.getNext(block);
        return next.isPresent() ? next : Optional.ofNullable(ShapeBlocks.getNextOxidation(block));
    }

    @Override
    public int getFlammability(BlockState state) {
        return state.getFlammability(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, Direction.UP);
    }

    @Override
    public int getFireSpreadSpeed(BlockState state) {
        return state.getFireSpreadSpeed(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, Direction.UP);
    }

    @Override
    public <T> void sendToServer(PacketType<T> type, T packet) {
        ForgeNetwork.CHANNEL.sendToServer(packet);
    }

    @Override
    public <T> void sendToPlayer(ServerPlayer player, PacketType<T> type, T packet) {
        ForgeNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
