package dev.tazer.clutternomore.forge;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.forge.client.ClutterNoMoreForgeClient;
import dev.tazer.clutternomore.registry.ShapeBlocks;
import dev.tazer.clutternomore.shape.ShapeMap;
import dev.tazer.clutternomore.shape.ShapeMapReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;

@Mod(ClutterNoMore.MOD_ID)
public final class ClutterNoMoreForge {

    public ClutterNoMoreForge(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        ClutterNoMore.init();
        ForgeNetwork.register();
        modBus.addListener(ClutterNoMoreForge::onCommonSetup);
        modBus.addListener(ClutterNoMoreForge::onRegister);
        MinecraftForge.EVENT_BUS.addListener(ClutterNoMoreForge::onReloadListeners);
        MinecraftForge.EVENT_BUS.addListener(ClutterNoMoreForge::onDatapackSync);
        MinecraftForge.EVENT_BUS.addListener(ClutterNoMoreForge::onEntityJoin);
        MinecraftForge.EVENT_BUS.addListener(ClutterNoMoreForge::onToolModification);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, ClutterNoMoreForge::onFuelBurnTime);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClutterNoMoreForgeClient.init(context);
        }
    }

    @SuppressWarnings("deprecation")
    private static void onCommonSetup(FMLCommonSetupEvent event) {
        BuiltInRegistries.BLOCK.freeze();
        BuiltInRegistries.ITEM.freeze();
    }

    private static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.POTION)) {
            ShapeBlocks.registerVariants();
        }
    }

    private static void onReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new ShapeMapReloadListener());
    }

    private static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        if (event.getBurnTime() > 0) return;
        Item source = ShapeBlocks.getSourceItem(event.getItemStack().getItem());
        if (source == null) return;
        int burnTime = ForgeHooks.getBurnTime(new ItemStack(source), event.getRecipeType());
        if (burnTime > 0) {
            event.setBurnTime(burnTime);
        }
    }

    private static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) {
            ShapeMap.sendShapeMap(event.getPlayer());
            return;
        }
        for (ServerPlayer player : event.getPlayerList().getPlayers()) {
            ShapeMap.sendShapeMap(player);
        }
    }

    private static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ShapeMap.sendShapeMap(player);
        }
    }

    private static void onToolModification(BlockEvent.BlockToolModificationEvent event) {
        ToolAction action = event.getToolAction();
        if (!event.getHeldItemStack().canPerformAction(action)) return;
        BlockState state = event.getState();
        Block modified = null;
        if (action == ToolActions.AXE_SCRAPE) {
            modified = ShapeBlocks.getPreviousOxidation(state.getBlock());
        } else if (action == ToolActions.AXE_WAX_OFF) {
            modified = ShapeBlocks.getUnwaxed(state.getBlock());
        }
        if (modified != null) {
            event.setFinalState(modified.withPropertiesOf(state));
        }
    }
}
