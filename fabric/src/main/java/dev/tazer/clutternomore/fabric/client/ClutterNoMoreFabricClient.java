package dev.tazer.clutternomore.fabric.client;

import dev.tazer.clutternomore.client.ClientShapeTooltip;
import dev.tazer.clutternomore.client.ClutterNoMoreClient;
import dev.tazer.clutternomore.network.ClutterNoMoreNetwork;
import dev.tazer.clutternomore.network.PacketType;
import dev.tazer.clutternomore.shape.ShapeTooltip;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.Nullable;
//? if >=26 {
/*import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
*///?} else {
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
//?}
//? if <26 {
import dev.tazer.clutternomore.registry.ShapeBlocks;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
//?}
//? if >=1.21.6 {
/*import dev.tazer.clutternomore.ClutterNoMore;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
*///?} else {
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
//?}

//? if <26 {
import java.util.Map;
//?}

public final class ClutterNoMoreFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClutterNoMoreClient.init();
        for (PacketType<?> type : ClutterNoMoreNetwork.all()) {
            if (!type.isServerbound()) {
                registerReceiver(type);
            }
        }
        //? if >=26 {
        /*KeyMappingHelper.registerKeyMapping(ClutterNoMoreClient.SHAPE_KEY);
        ClientTooltipComponentCallback.EVENT.register(ClutterNoMoreFabricClient::tooltip);
        *///?} else {
        KeyBindingHelper.registerKeyBinding(ClutterNoMoreClient.SHAPE_KEY);
        TooltipComponentCallback.EVENT.register(ClutterNoMoreFabricClient::tooltip);
        //?}
        //? if >=1.20.5 {
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> ClutterNoMoreClient.onItemTooltip(stack, lines));
        //?} else {
        /*ItemTooltipCallback.EVENT.register((stack, flag, lines) -> ClutterNoMoreClient.onItemTooltip(stack, lines));
        *///?}
        //? if >=1.21.6 {
        /*HudElementRegistry.addLast(ClutterNoMore.id("overlay"), ClutterNoMoreClient::onRenderGui);
        *///?} else if >=1.21 {
        HudRenderCallback.EVENT.register(ClutterNoMoreClient::onRenderGui);
        //?} else {
        /*HudRenderCallback.EVENT.register(ClutterNoMoreClient::renderOverlay);
        *///?}
        ClientTickEvents.START_CLIENT_TICK.register(ClutterNoMoreClient::onClientTick);
        ScreenEvents.AFTER_INIT.register(ClutterNoMoreFabricClient::afterScreenInit);
        //? if <26 {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> copyRenderLayers());
        //?}
    }

    //? if <26 {
    private static void copyRenderLayers() {
        for (Map.Entry<Block, Block> entry : ShapeBlocks.getSources().entrySet()) {
            RenderType layer = ItemBlockRenderTypes.getChunkRenderType(entry.getValue().defaultBlockState());
            if (layer != RenderType.solid()) {
                BlockRenderLayerMap.INSTANCE.putBlock(entry.getKey(), layer);
            }
        }
    }
    //?}

    private static <T> void registerReceiver(PacketType<T> type) {
        //? if >=1.20.5 {
        ClientPlayNetworking.registerGlobalReceiver(type.getPayloadType(), (payload, context) -> payload.handleOnClient());
        //?} else {
        /*ClientPlayNetworking.registerGlobalReceiver(type.getId(), (client, listener, buf, sender) -> {
            T value = type.read(buf);
            client.execute(() -> type.handleOnClient(value));
        });
        *///?}
    }

    @Nullable
    private static ClientTooltipComponent tooltip(TooltipComponent component) {
        return component instanceof ShapeTooltip shapes ? new ClientShapeTooltip(shapes) : null;
    }

    private static void afterScreenInit(Minecraft minecraft, Screen screen, int width, int height) {
        if (!(screen instanceof AbstractContainerScreen<?>)) return;
        //? if >=1.21.9 {
        /*ScreenKeyboardEvents.afterKeyPress(screen).register((current, event) -> ClutterNoMoreClient.onKeyPress(current, event.input()));
        ScreenKeyboardEvents.afterKeyRelease(screen).register((current, event) -> ClutterNoMoreClient.onKeyReleased(event.input()));
        ScreenMouseEvents.afterMouseClick(screen).register((current, event, doubleClick) -> {
            ClutterNoMoreClient.onKeyPress(current, event.button());
            return false;
        });
        ScreenMouseEvents.afterMouseRelease(screen).register((current, event, consumed) -> {
            ClutterNoMoreClient.onKeyReleased(event.button());
            return false;
        });
        *///?} else {
        ScreenKeyboardEvents.afterKeyPress(screen).register((current, key, scancode, modifiers) -> ClutterNoMoreClient.onKeyPress(current, key));
        ScreenKeyboardEvents.afterKeyRelease(screen).register((current, key, scancode, modifiers) -> ClutterNoMoreClient.onKeyReleased(key));
        ScreenMouseEvents.afterMouseClick(screen).register((current, mouseX, mouseY, button) -> ClutterNoMoreClient.onKeyPress(current, button));
        ScreenMouseEvents.afterMouseRelease(screen).register((current, mouseX, mouseY, button) -> ClutterNoMoreClient.onKeyReleased(button));
        //?}
        ScreenMouseEvents.allowMouseScroll(screen).register(ClutterNoMoreClient::allowScreenScroll);
    }
}
