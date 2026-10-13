package dev.tazer.clutternomore.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreConfig;
import dev.tazer.clutternomore.compat.ControlifyCompat;
import dev.tazer.clutternomore.compat.JeiCompat;
import dev.tazer.clutternomore.mixin.client.CreativeModeInventoryScreenAccessor;
import dev.tazer.clutternomore.mixin.client.KeyMappingAccessor;
import dev.tazer.clutternomore.mixin.client.SlotWrapperAccessor;
import dev.tazer.clutternomore.mixin.screen.AbstractContainerScreenAccessor;
import dev.tazer.clutternomore.network.ChangeShape;
import dev.tazer.clutternomore.network.ClutterNoMoreNetwork;
import dev.tazer.clutternomore.network.ShapeMapSync;
import dev.tazer.clutternomore.platform.Services;
import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
//? if <1.21.4 {
import dev.tazer.clutternomore.compat.EmiCompat;
//?}
//? if >=26 {
/*import dev.tazer.clutternomore.compat.RrvCompat;
*///?}
//? if >=1.21 {
import net.minecraft.client.DeltaTracker;
//?}
//? if <26.3 {
import org.lwjgl.glfw.GLFW;
//?}

import java.util.List;

public final class ClutterNoMoreClient {

    public static final ClutterNoMoreConfig.Client CONFIG = ClutterNoMoreConfig.Client.createToml(Services.PLATFORM.getConfigDirectory(), ClutterNoMore.MOD_ID, "client", ClutterNoMoreConfig.Client.class);
    public static final KeyMapping SHAPE_KEY = new KeyMapping("key.clutternomore.change_block_shape", InputConstants.KEY_LALT,
            //? if >=1.21.9 {
            /*KeyMapping.Category.INVENTORY
            *///?} else {
            "key.categories.inventory"
            //?}
    );
    private static final long SEND_INTERVAL_MS = 50L;
    private static final int PRESSED = 1;
    private static final int RELEASED = 0;

    private static boolean showTooltip;
    private static boolean keyHeld;
    private static long lastSend;
    private static double scrollProgress;

    @Nullable
    private static ShapeSwitcherOverlay overlay;

    @Nullable
    private static ChangeShape pending;

    private ClutterNoMoreClient() {
    }

    public static void init() {
        ClutterNoMoreNetwork.SHAPE_MAP.setClientHandler(ClutterNoMoreClient::onShapeMapSync);
    }

    public static void onDisconnect() {
        ShapeMap.clearRemoteShapeMaps();
    }

    private static void onShapeMapSync(ShapeMapSync packet) {
        ShapeMapSync.apply(packet, !Minecraft.getInstance().hasSingleplayerServer());
    }

    @Nullable
    public static ShapeSwitcherOverlay getOverlay() {
        return overlay;
    }

    public static void closeOverlay() {
        overlay = null;
    }

    public static boolean isCreativeTabSlot(@Nullable Slot slot) {
        return slot != null && slot.container == CreativeModeInventoryScreenAccessor.cnm$getContainer();
    }

    @Nullable
    public static Slot getHoveredSlot() {
        if (!(Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> screen)) return null;
        return ((AbstractContainerScreenAccessor) screen).cnm$getHoveredSlot();
    }

    public static boolean isHoveringSwitchableSlot() {
        Slot slot = getHoveredSlot();
        if (slot == null) return false;
        if (isCreativeTabSlot(slot)) return true;
        Player player = Minecraft.getInstance().player;
        return player != null && slot.allowModification(player);
    }

    public static boolean isHoveringRecipeViewer() {
        try {
            if (Services.PLATFORM.isModLoaded("jei") && JeiCompat.isHoveringIngredient()) return true;
            //? if <1.21.4 {
            if (Services.PLATFORM.isModLoaded("emi") && EmiCompat.isHoveringIngredient()) return true;
            //?}
            //? if >=26 {
            /*if (Services.PLATFORM.isModLoaded("rrv") && RrvCompat.isHoveringIngredient()) return true;
            *///?}
        } catch (Throwable ignored) {
        }
        return false;
    }

    public static boolean isRenderingIcons() {
        return isCreativeTabSlot(getHoveredSlot()) || showTooltip || isHoveringRecipeViewer();
    }

    public static void onItemTooltip(ItemStack stack, List<Component> tooltip) {
        if (!ShapeMap.contains(stack.getItem()) || tooltip.isEmpty()) return;
        if (CONFIG.detailedTooltips.value()) {
            if (Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen && getHoveredSlot() != null) return;
            if (isHoveringSwitchableSlot()) {
                tooltip.add(1, getHint());
                return;
            }
        }
        if (isRenderingIcons()) return;
        tooltip.set(0, tooltip.get(0).copy().append(Component.literal(" [+]").withStyle(ChatFormatting.DARK_GRAY)));
    }

    public static Component getHint() {
        Component hint;
        if (showTooltip) {
            hint = Component.translatable("tooltip.clutternomore.scroll_to_change", Component.translatable("tooltip.clutternomore.scroll").withStyle(ChatFormatting.GRAY));
        } else {
            String key = CONFIG.hold.value() == ClutterNoMoreConfig.InputType.HOLD ? "tooltip.clutternomore.hold_to_change" : "tooltip.clutternomore.press_to_change";
            hint = Component.translatable(key, Component.keybind("key.clutternomore.change_block_shape").copy().withStyle(ChatFormatting.GRAY));
        }
        return hint.copy().withStyle(ChatFormatting.DARK_GRAY);
    }

    public static int getShapeKey() {
        return ((KeyMappingAccessor) SHAPE_KEY).cnm$getKey().getValue();
    }

    public static void onKeyInput(int key, int action) {
        if (key != getShapeKey() || Services.PLATFORM.isModLoaded("controlify") && ControlifyCompat.isControllerActive()) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != null) return;
        if (action == PRESSED) {
            keyHeld = true;
        } else if (action == RELEASED) {
            keyHeld = false;
        }
        ItemStack held = heldShapeStack(minecraft);
        if (held == null) return;
        switch (CONFIG.hold.value()) {
            case HOLD -> {
                if (overlay == null && action == PRESSED) {
                    overlay = new ShapeSwitcherOverlay(minecraft, held, true);
                } else if (action == RELEASED) {
                    overlay = null;
                }
            }
            case TOGGLE -> {
                if (action == PRESSED) {
                    overlay = overlay == null ? new ShapeSwitcherOverlay(minecraft, held, true) : null;
                }
            }
            case PRESS -> {
                if (action == PRESSED) {
                    cycleOnce(minecraft, held);
                }
            }
        }
    }

    public static void onControllerShapeKey(boolean down, boolean justPressed, boolean justReleased, boolean justTapped) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.screen != null) return;
        if (down && !justPressed) {
            keyHeld = true;
        } else if (justReleased) {
            keyHeld = false;
        }
        ItemStack held = heldShapeStack(minecraft);
        if (held == null) return;
        switch (CONFIG.hold.value()) {
            case HOLD -> {
                if (overlay == null && down) {
                    overlay = new ShapeSwitcherOverlay(minecraft, held, true);
                } else if (justReleased) {
                    overlay = null;
                }
            }
            case TOGGLE -> {
                if (justTapped && !keyHeld) {
                    overlay = overlay == null ? new ShapeSwitcherOverlay(minecraft, held, true) : null;
                }
            }
            case PRESS -> {
                if (justTapped && !keyHeld) {
                    cycleOnce(minecraft, held);
                }
            }
        }
    }

    @Nullable
    private static ItemStack heldShapeStack(Minecraft minecraft) {
        if (minecraft.player == null) return null;
        ItemStack held = minecraft.player.getItemInHand(InteractionHand.MAIN_HAND);
        return ShapeMap.contains(held.getItem()) ? held : null;
    }

    private static void cycleOnce(Minecraft minecraft, ItemStack held) {
        ShapeSwitcherOverlay switcher = overlay == null ? new ShapeSwitcherOverlay(minecraft, held, false) : overlay;
        switcher.onMouseScrolled(-1);
        overlay = null;
    }

    public static void onKeyPress(Screen screen, int key) {
        if (key != getShapeKey() || keyHeld) return;
        keyHeld = true;
        switch (CONFIG.hold.value()) {
            case HOLD -> showTooltip = true;
            case TOGGLE -> showTooltip = !showTooltip;
            case PRESS -> {
                if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) return;
                Slot slot = ((AbstractContainerScreenAccessor) containerScreen).cnm$getHoveredSlot();
                if (slot == null || !canSwitch(slot)) return;
                switchShape(slot, containerScreen.getMenu().containerId, slot.getItem(), -1, true);
            }
        }
    }

    public static void onKeyReleased(int key) {
        if (key != getShapeKey()) return;
        keyHeld = false;
        if (CONFIG.hold.value() == ClutterNoMoreConfig.InputType.HOLD) {
            showTooltip = false;
        }
    }

    private static boolean canSwitch(Slot slot) {
        if (!ShapeMap.contains(slot.getItem().getItem())) return false;
        Player player = Minecraft.getInstance().player;
        return isCreativeTabSlot(slot) || player != null && slot.allowModification(player);
    }

    public static int selectedSlot(Player player) {
        //? if >=1.21.5 {
        /*return player.getInventory().getSelectedSlot();
        *///?} else {
        return player.getInventory().selected;
        //?}
    }

    public static void renderOverlay(GuiGraphics graphics, float partialTick) {
        if (overlay == null) return;
        overlay.tickLook();
        if (overlay.isVisible()) {
            overlay.render(graphics, partialTick);
        }
    }

    //? if >=1.21 {
    public static void onRenderGui(GuiGraphics graphics, DeltaTracker tracker) {
        renderOverlay(graphics, tracker.getGameTimeDeltaTicks());
    }
    //?}

    public static boolean selectHoveredShape(int index) {
        if (!showTooltip || !(Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> screen)) return false;
        Slot slot = ((AbstractContainerScreenAccessor) screen).cnm$getHoveredSlot();
        if (slot == null || !canSwitch(slot)) return false;
        if (index < ShapeMap.getShapes(slot.getItem().getItem()).size()) {
            switchShapeTo(slot, screen.getMenu().containerId, slot.getItem(), index);
        }
        return true;
    }

    public static void switchShape(Slot slot, int containerId, ItemStack stack, int direction, boolean wrap) {
        switchShapeTo(slot, containerId, stack, nextShapeIndex(stack, direction, wrap));
    }

    private static void switchShapeTo(Slot slot, int containerId, ItemStack stack, int next) {
        if (next < 0 || next == ShapeMap.getShapeIndex(stack)) return;
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;
        playSwitchSound(player);
        if (isCreativeTabSlot(slot)) {
            slot.set(ShapeMap.transferStack(stack, next));
            return;
        }
        if (minecraft.screen instanceof CreativeModeInventoryScreen) {
            if (slot instanceof SlotWrapperAccessor wrapper && wrapper.cnm$getTarget() != null) {
                sendChangeShape(player.inventoryMenu.containerId, wrapper.cnm$getTarget().index, next);
                return;
            }
            if (slot.container == player.getInventory()) {
                for (Slot candidate : player.inventoryMenu.slots) {
                    if (candidate.container == slot.container && candidate.getContainerSlot() == slot.getContainerSlot()) {
                        sendChangeShape(player.inventoryMenu.containerId, candidate.index, next);
                        return;
                    }
                }
            }
        }
        sendChangeShape(containerId, slot.index, next);
    }

    private static int nextShapeIndex(ItemStack stack, int direction, boolean wrap) {
        List<Item> shapes = ShapeMap.getShapes(stack.getItem());
        if (shapes.isEmpty()) return -1;
        int index = Math.max(ShapeMap.getShapeIndex(stack), 0) - direction;
        int max = shapes.size() - 1;
        if (wrap) {
            if (index < 0) return max;
            if (index > max) return 0;
            return index;
        }
        return Math.min(Math.max(index, 0), max);
    }

    public static void playSwitchSound(Player player) {
        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3F, 1.5F);
    }

    public static void sendChangeShape(int containerId, int slot, int shapeIndex) {
        long now = System.currentTimeMillis();
        if (pending != null && (pending.containerId() != containerId || pending.slot() != slot)) {
            send(pending, now);
        }
        ChangeShape change = new ChangeShape(containerId, slot, shapeIndex);
        if (now - lastSend >= SEND_INTERVAL_MS) {
            send(change, now);
        } else {
            pending = change;
        }
    }

    private static void send(ChangeShape change, long now) {
        ClutterNoMoreNetwork.sendToServer(ClutterNoMoreNetwork.CHANGE_SHAPE, change);
        lastSend = now;
        pending = null;
    }

    public static boolean isShapeKeyPhysicallyDown() {
        int key = getShapeKey();
        if (key < 0) return false;
        //? if >=26.3 {
        /*return InputConstants.isKeyDown(key);
        *///?} else if >=26 {
        /*if (key < InputConstants.KEY_SPACE) return GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().handle(), key) == GLFW.GLFW_PRESS;
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), key);
        *///?} else {
        long window = Minecraft.getInstance().getWindow().getWindow();
        if (key < InputConstants.KEY_SPACE) return GLFW.glfwGetMouseButton(window, key) == GLFW.GLFW_PRESS;
        return InputConstants.isKeyDown(window, key);
        //?}
    }

    public static void onClientTick(Minecraft minecraft) {
        if (Services.PLATFORM.isModLoaded("controlify")) {
            ControlifyCompat.tick();
        }
        boolean physical = isShapeKeyPhysicallyDown();
        boolean rising = physical && !keyHeld;
        keyHeld = physical;
        switch (CONFIG.hold.value()) {
            case HOLD -> showTooltip = physical;
            case TOGGLE -> {
                if (rising) {
                    showTooltip = !showTooltip;
                }
            }
            case PRESS -> showTooltip = false;
        }
        if (overlay != null && !overlay.shouldStayOpen()) {
            overlay = null;
        }
        if (pending != null && System.currentTimeMillis() - lastSend >= SEND_INTERVAL_MS) {
            send(pending, System.currentTimeMillis());
        }
    }

    public static boolean allowScreenScroll(Screen screen, double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!showTooltip || !(screen instanceof AbstractContainerScreen<?> containerScreen)) return true;
        Slot slot = ((AbstractContainerScreenAccessor) containerScreen).cnm$getHoveredSlot();
        if (slot == null || !canSwitch(slot)) return true;
        switchShape(slot, containerScreen.getMenu().containerId, slot.getItem(), direction(scrollY), CONFIG.wrapScrolling.value());
        return false;
    }

    public static boolean onMouseScrolled(double scrollY) {
        if (overlay == null) return false;
        overlay.onMouseScrolled(direction(scrollY));
        return true;
    }

    private static int direction(double scroll) {
        if (scrollProgress != 0 && Math.signum(scroll) != Math.signum(scrollProgress)) {
            scrollProgress = 0;
        }
        scrollProgress += scroll;
        int steps = (int) scrollProgress;
        scrollProgress -= steps;
        return Integer.signum(steps);
    }

    public static boolean changeHotbarSlot(int index) {
        if (overlay == null) return false;
        overlay.changeSlot(Math.min(Math.max(index, 0), overlay.getShapeCount() - 1));
        return true;
    }

    public static boolean previousSlot() {
        return overlay != null && changeHotbarSlot(overlay.getSelectedIndex() + 1);
    }

    public static boolean nextSlot() {
        return overlay != null && changeHotbarSlot(overlay.getSelectedIndex() - 1);
    }
}
