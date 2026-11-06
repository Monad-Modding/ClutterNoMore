package dev.tazer.clutternomore.neoforge;

import dev.tazer.clutternomore.network.PacketType;
import dev.tazer.clutternomore.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;
//? if <26 {
import dev.tazer.clutternomore.neoforge.compat.DomumOrnamentumCompat;
import net.minecraft.world.item.ItemStack;
//?}
//? if >=26 {
/*import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
*///?}

import java.nio.file.Path;
//? if >=26 {
/*import java.util.List;
*///?}

public final class NeoForgePlatform implements Platform {

    //? if <26 {
    private final boolean domumOrnamentum = ModList.get().isLoaded("domum_ornamentum");
    //?}

    @Override
    public String getName() {
        return "neoforge";
    }

    @Override
    public boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    @Override
    public boolean isClient() {
        //? if >=26 {
        /*return FMLEnvironment.getDist().isClient();
        *///?} else {
        return FMLEnvironment.dist.isClient();
        //?}
    }

    //? if <26 {
    @Override
    public void afterShapeTransfer(ItemStack from, ItemStack to) {
        if (this.domumOrnamentum) {
            DomumOrnamentumCompat.remap(from, to);
        }
    }
    //?}

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
        Registry.register(registry, id, value);
    }

    @Override
    public <T> void alias(Registry<T> registry, ResourceLocation from, ResourceLocation to) {
        registry.addAlias(from, to);
    }

    @Override
    public void beforeShapeRegistration() {
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
    public void afterShapeRegistration() {
        CopperDataMaps.writeDefaults();
    }

    @Override
    public void generateServerData(ResourceManager manager) {
        CopperDataMaps.generate(manager);
    }

    @Override
    public <T> void sendToServer(PacketType<T> type, T packet) {
        //? if >=26 {
        /*ClientPacketDistributor.sendToServer(type.wrap(packet));
        *///?} else {
        PacketDistributor.sendToServer(type.wrap(packet));
        //?}
    }

    @Override
    public <T> void sendToPlayer(ServerPlayer player, PacketType<T> type, T packet) {
        PacketDistributor.sendToPlayer(player, type.wrap(packet));
    }

    //? if >=26.3 {
    /*@Override
    public RecipeMap createRecipeMap(List<RecipeHolder<?>> recipes) {
        return RecipeMap.createClient(recipes);
    }
    *///?} else if >=26 {
    /*@Override
    public RecipeMap createRecipeMap(List<RecipeHolder<?>> recipes) {
        return RecipeMap.create(recipes);
    }
    *///?}
}
