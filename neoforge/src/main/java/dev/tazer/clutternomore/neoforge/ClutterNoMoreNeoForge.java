package dev.tazer.clutternomore.neoforge;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.network.ClutterNoMoreNetwork;
import dev.tazer.clutternomore.network.PacketType;
import dev.tazer.clutternomore.registry.ShapeBlocks;
import dev.tazer.clutternomore.shape.ShapeMap;
import dev.tazer.clutternomore.shape.ShapeMapReloadListener;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;
//? if >=26 {
/*import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
*///?} else {
import net.neoforged.neoforge.event.AddReloadListenerEvent;
//?}
//? if <26.3 {
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
//?}

@Mod(ClutterNoMore.MOD_ID)
public final class ClutterNoMoreNeoForge {

    private static final String PROTOCOL = "1";

    public ClutterNoMoreNeoForge(IEventBus modBus) {
        ClutterNoMore.init();
        modBus.addListener(ClutterNoMoreNeoForge::onPayloads);
        modBus.addListener(ClutterNoMoreNeoForge::onRegister);
        NeoForge.EVENT_BUS.addListener(ClutterNoMoreNeoForge::onDatapackSync);
        NeoForge.EVENT_BUS.addListener(ClutterNoMoreNeoForge::onReloadListeners);
        //? if <26.3 {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, ClutterNoMoreNeoForge::onFuelBurnTime);
        //?}
    }

    private static void onPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL);
        for (PacketType<?> type : ClutterNoMoreNetwork.all()) {
            register(registrar, type);
        }
    }

    private static <T> void register(PayloadRegistrar registrar, PacketType<T> type) {
        if (type.isServerbound()) {
            registrar.playToServer(type.getPayloadType(), type.getStreamCodec(), (payload, context) -> {
                if (context.player() instanceof ServerPlayer player) {
                    type.handleOnServer(payload.value(), player);
                }
            });
        } else {
            registrar.playToClient(type.getPayloadType(), type.getStreamCodec(), (payload, context) -> payload.handleOnClient());
        }
    }

    private static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.POTION)) {
            ShapeBlocks.registerVariants();
        }
    }

    private static void onDatapackSync(OnDatapackSyncEvent event) {
        event.getRelevantPlayers().forEach(ShapeMap::sendShapeMap);
    }

    //? if <26.3 {
    private static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        if (event.getBurnTime() > 0) return;
        Item source = ShapeBlocks.getSourceItem(event.getItemStack().getItem());
        if (source == null) return;
        //? if >=26 {
        /*int burnTime = new ItemStack(source).getBurnTime(event.getRecipeType(), event.getFuelValues());
        *///?} else {
        int burnTime = new ItemStack(source).getBurnTime(event.getRecipeType());
        //?}
        if (burnTime > 0) {
            event.setBurnTime(burnTime);
        }
    }
    //?}

    //? if >=26 {
    /*private static void onReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(ClutterNoMore.id(ShapeMapReloadListener.DIRECTORY), new ShapeMapReloadListener());
    }
    *///?} else {
    private static void onReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new ShapeMapReloadListener());
    }
    //?}
}
