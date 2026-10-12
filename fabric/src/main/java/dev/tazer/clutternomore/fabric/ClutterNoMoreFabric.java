package dev.tazer.clutternomore.fabric;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.shape.ShapeMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.packs.PackType;
//? if >=1.21.9 {
/*import dev.tazer.clutternomore.shape.ShapeMapReloadListener;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
*///?} else {
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
//?}

public final class ClutterNoMoreFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ClutterNoMore.init();
        FabricNetwork.register();
        //? if >=1.21.9 {
        /*ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(ClutterNoMore.id(ShapeMapReloadListener.DIRECTORY), new ShapeMapReloadListener());
        *///?} else {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new FabricShapeMapReloadListener());
        //?}
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> ShapeMap.sendShapeMap(player));
    }
}
