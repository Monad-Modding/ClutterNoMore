package dev.tazer.clutternomore.platform;

import dev.tazer.clutternomore.mixin.access.FireBlockAccessor;
import dev.tazer.clutternomore.network.PacketType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
//? if >=26 {
/*import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
*///?}

import java.nio.file.Path;
import java.util.Optional;
//? if >=26 {
/*import java.util.List;
*///?}

public interface Platform {

    String getName();

    boolean isModLoaded(String id);

    boolean isClient();

    Path getGameDirectory();

    Path getConfigDirectory();

    <T> void register(Registry<T> registry, ResourceLocation id, T value);

    void beforeShapeRegistration();

    void afterShapeRegistration();

    default void generateServerData(ResourceManager manager) {
    }

    default Optional<Block> getNextOxidation(Block block) {
        return WeatheringCopper.getNext(block);
    }

    default int getFlammability(BlockState state) {
        return ((FireBlockAccessor) Blocks.FIRE).cnm$getBurnOdds(state);
    }

    default int getFireSpreadSpeed(BlockState state) {
        return ((FireBlockAccessor) Blocks.FIRE).cnm$getIgniteOdds(state);
    }

    <T> void sendToServer(PacketType<T> type, T packet);

    <T> void sendToPlayer(ServerPlayer player, PacketType<T> type, T packet);

    //? if >=26 {
    /*RecipeMap createRecipeMap(List<RecipeHolder<?>> recipes);
    *///?}
}
