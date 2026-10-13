package dev.tazer.clutternomore.forge.client;

import dev.tazer.clutternomore.client.ClientShapeTooltip;
import dev.tazer.clutternomore.client.ClutterNoMoreClient;
import dev.tazer.clutternomore.client.ShapeSwitcherOptionsScreen;
import dev.tazer.clutternomore.shape.ShapeTooltip;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class ClutterNoMoreForgeClient {

    private ClutterNoMoreForgeClient() {
    }

    public static void init(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        ClutterNoMoreClient.init();
        context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, parent) -> new ShapeSwitcherOptionsScreen(parent, minecraft.options)));
        modBus.addListener(ClutterNoMoreForgeClient::onKeyMappings);
        modBus.addListener(ClutterNoMoreForgeClient::onTooltipComponents);
        MinecraftForge.EVENT_BUS.addListener(ClutterNoMoreForgeClient::onItemTooltip);
        MinecraftForge.EVENT_BUS.addListener(ClutterNoMoreForgeClient::onKey);
        MinecraftForge.EVENT_BUS.addListener(ClutterNoMoreForgeClient::onMouseButton);
        MinecraftForge.EVENT_BUS.addListener(ClutterNoMoreForgeClient::onMouseScrolling);
        MinecraftForge.EVENT_BUS.addListener(ClutterNoMoreForgeClient::onScreenScroll);
        MinecraftForge.EVENT_BUS.addListener(ClutterNoMoreForgeClient::onScreenKeyPressed);
        MinecraftForge.EVENT_BUS.addListener(ClutterNoMoreForgeClient::onScreenMousePressed);
        MinecraftForge.EVENT_BUS.addListener(ClutterNoMoreForgeClient::onScreenKeyReleased);
        MinecraftForge.EVENT_BUS.addListener(ClutterNoMoreForgeClient::onScreenMouseReleased);
        MinecraftForge.EVENT_BUS.addListener(ClutterNoMoreForgeClient::onRenderGui);
        MinecraftForge.EVENT_BUS.addListener(ClutterNoMoreForgeClient::onLoggingOut);
        MinecraftForge.EVENT_BUS.addListener(ClutterNoMoreForgeClient::onClientTick);
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
        if (ClutterNoMoreClient.onMouseScrolled(event.getScrollDelta())) {
            event.setCanceled(true);
        }
    }

    private static void onScreenScroll(ScreenEvent.MouseScrolled.Pre event) {
        if (!ClutterNoMoreClient.allowScreenScroll(event.getScreen(), event.getMouseX(), event.getMouseY(), 0, event.getScrollDelta())) {
            event.setCanceled(true);
        }
    }

    private static void onScreenKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        ClutterNoMoreClient.onKeyPress(event.getScreen(), event.getKeyCode());
    }

    private static void onScreenMousePressed(ScreenEvent.MouseButtonPressed.Pre event) {
        ClutterNoMoreClient.onKeyPress(event.getScreen(), event.getButton());
    }

    private static void onScreenKeyReleased(ScreenEvent.KeyReleased.Pre event) {
        ClutterNoMoreClient.onKeyReleased(event.getKeyCode());
    }

    private static void onScreenMouseReleased(ScreenEvent.MouseButtonReleased.Pre event) {
        ClutterNoMoreClient.onKeyReleased(event.getButton());
    }

    private static void onRenderGui(RenderGuiEvent.Post event) {
        ClutterNoMoreClient.renderOverlay(event.getGuiGraphics(), event.getPartialTick());
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ClutterNoMoreClient.onClientTick(Minecraft.getInstance());
        }
    }

    private static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClutterNoMoreClient.onDisconnect();
    }
}
