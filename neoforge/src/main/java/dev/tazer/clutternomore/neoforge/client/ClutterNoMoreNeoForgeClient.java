package dev.tazer.clutternomore.neoforge.client;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.client.ClientShapeTooltip;
import dev.tazer.clutternomore.client.ClutterNoMoreClient;
import dev.tazer.clutternomore.client.ShapeSwitcherOptionsScreen;
import dev.tazer.clutternomore.shape.ShapeTooltip;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@Mod(value = ClutterNoMore.MOD_ID, dist = Dist.CLIENT)
public final class ClutterNoMoreNeoForgeClient {

    public ClutterNoMoreNeoForgeClient(IEventBus modBus, ModContainer container) {
        ClutterNoMoreClient.init();
        container.registerExtensionPoint(IConfigScreenFactory.class, (mod, parent) -> new ShapeSwitcherOptionsScreen(parent));
        modBus.addListener(ClutterNoMoreNeoForgeClient::onKeyMappings);
        modBus.addListener(ClutterNoMoreNeoForgeClient::onTooltipComponents);
        NeoForge.EVENT_BUS.addListener(ClutterNoMoreNeoForgeClient::onItemTooltip);
        NeoForge.EVENT_BUS.addListener(ClutterNoMoreNeoForgeClient::onKey);
        NeoForge.EVENT_BUS.addListener(ClutterNoMoreNeoForgeClient::onMouseButton);
        NeoForge.EVENT_BUS.addListener(ClutterNoMoreNeoForgeClient::onMouseScrolling);
        NeoForge.EVENT_BUS.addListener(ClutterNoMoreNeoForgeClient::onScreenScroll);
        NeoForge.EVENT_BUS.addListener(ClutterNoMoreNeoForgeClient::onScreenKeyPressed);
        NeoForge.EVENT_BUS.addListener(ClutterNoMoreNeoForgeClient::onScreenMousePressed);
        NeoForge.EVENT_BUS.addListener(ClutterNoMoreNeoForgeClient::onScreenKeyReleased);
        NeoForge.EVENT_BUS.addListener(ClutterNoMoreNeoForgeClient::onScreenMouseReleased);
        NeoForge.EVENT_BUS.addListener(ClutterNoMoreNeoForgeClient::onRenderGui);
        NeoForge.EVENT_BUS.addListener(ClutterNoMoreNeoForgeClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(ClutterNoMoreNeoForgeClient::onLoggingOut);
    }

    private static void onKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ClutterNoMoreClient.SHAPE_KEY);
    }

    private static void onTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ShapeTooltip.class, ClientShapeTooltip::new);
    }

    private static void onItemTooltip(ItemTooltipEvent event) {
        ClutterNoMoreClient.onItemTooltip(event.getItemStack(), event.getToolTip());
    }

    private static void onKey(InputEvent.Key event) {
        ClutterNoMoreClient.onKeyInput(event.getKey(), event.getAction());
    }

    private static void onMouseButton(InputEvent.MouseButton.Post event) {
        ClutterNoMoreClient.onKeyInput(event.getButton(), event.getAction());
    }

    private static void onMouseScrolling(InputEvent.MouseScrollingEvent event) {
        if (ClutterNoMoreClient.onMouseScrolled(event.getScrollDeltaY())) {
            event.setCanceled(true);
        }
    }

    private static void onScreenScroll(ScreenEvent.MouseScrolled.Pre event) {
        if (!ClutterNoMoreClient.allowScreenScroll(event.getScreen(), event.getMouseX(), event.getMouseY(), event.getScrollDeltaX(), event.getScrollDeltaY())) {
            event.setCanceled(true);
        }
    }

    private static void onScreenKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        //? if >=26.3 {
        /*ClutterNoMoreClient.onKeyPress(event.getScreen(), event.getKey());
        *///?} else {
        ClutterNoMoreClient.onKeyPress(event.getScreen(), event.getKeyCode());
        //?}
    }

    private static void onScreenMousePressed(ScreenEvent.MouseButtonPressed.Pre event) {
        ClutterNoMoreClient.onKeyPress(event.getScreen(), event.getButton());
    }

    private static void onScreenKeyReleased(ScreenEvent.KeyReleased.Pre event) {
        //? if >=26.3 {
        /*ClutterNoMoreClient.onKeyReleased(event.getKey());
        *///?} else {
        ClutterNoMoreClient.onKeyReleased(event.getKeyCode());
        //?}
    }

    private static void onScreenMouseReleased(ScreenEvent.MouseButtonReleased.Pre event) {
        ClutterNoMoreClient.onKeyReleased(event.getButton());
    }

    private static void onRenderGui(RenderGuiEvent.Post event) {
        ClutterNoMoreClient.onRenderGui(event.getGuiGraphics(), event.getPartialTick());
    }

    private static void onClientTick(ClientTickEvent.Pre event) {
        ClutterNoMoreClient.onClientTick(Minecraft.getInstance());
    }

    private static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClutterNoMoreClient.onDisconnect();
    }
}
