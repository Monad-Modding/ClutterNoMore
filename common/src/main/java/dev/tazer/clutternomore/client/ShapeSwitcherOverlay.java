package dev.tazer.clutternomore.client;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreConfig;
import dev.tazer.clutternomore.network.ChangeShape;
import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if <1.21.2 {
import com.mojang.blaze3d.systems.RenderSystem;
//?}

import java.util.List;
import java.util.Objects;

public final class ShapeSwitcherOverlay {

    private static final float LOOK_DEGREES_PER_SWITCH = 25.0F;
    private static final int SPACING = 22;
    private static final ResourceLocation BACKGROUND = ClutterNoMore.id("textures/gui/shape_background.png");
    private static final ResourceLocation SELECTED = ClutterNoMore.id("textures/gui/selected_shape.png");

    private final Minecraft minecraft;
    private final boolean visible;
    private final int hotbarSlot;
    private final List<Item> shapes;

    private int selectedIndex;
    private float displayedIndex;
    private float lastYaw;
    private float accumulatedYaw;

    public ShapeSwitcherOverlay(Minecraft minecraft, ItemStack held, boolean visible) {
        LocalPlayer player = Objects.requireNonNull(minecraft.player);
        this.minecraft = minecraft;
        this.visible = visible;
        this.hotbarSlot = ClutterNoMoreClient.selectedSlot(player);
        this.shapes = ShapeMap.getShapes(held.getItem());
        this.selectedIndex = Math.max(ShapeMap.getShapeIndex(held), 0);
        this.displayedIndex = this.selectedIndex;
        this.lastYaw = player.getYRot();
    }

    public boolean isVisible() {
        return this.visible;
    }

    public int getShapeCount() {
        return this.shapes.size();
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    public void render(GuiGraphics graphics, float partialTick) {
        int y = graphics.guiHeight() / 2 + 20;
        int centreX = graphics.guiWidth() / 2 - 8;
        float smoothing = 1 - (float) Math.exp(-5 * partialTick);
        this.displayedIndex = Mth.lerp(smoothing, this.displayedIndex, this.selectedIndex);
        ItemStack held = this.player().getItemInHand(InteractionHand.MAIN_HAND);
        if (ClutterNoMoreClient.CONFIG.scrolling.value()) {
            int startX = Mth.floor(centreX - this.displayedIndex * SPACING);
            RenderHelper.blit(graphics, SELECTED, centreX - 3, y - 3, 22, 22);
            this.renderShapes(graphics, held, startX, y);
        } else {
            int startX = Mth.floor(centreX - (float) this.shapes.size() / 2 * SPACING) + SPACING / 2;
            int selectedX = Mth.floor(startX + this.displayedIndex * SPACING) - 3;
            //? if >=26 {
            /*RenderHelper.blit(graphics, SELECTED, selectedX, y - 3, 22, 22);
            this.renderShapes(graphics, held, startX, y);
            *///?} else {
            this.renderShapes(graphics, held, startX, y);
            RenderHelper.blit(graphics, SELECTED, selectedX, y - 3, 22, 22);
            //?}
        }
        //? if <1.21.2 {
        RenderSystem.disableBlend();
        //?}
    }

    private void renderShapes(GuiGraphics graphics, ItemStack held, int startX, int y) {
        for (int index = 0; index < this.shapes.size(); index++) {
            int x = startX + index * SPACING;
            RenderHelper.blit(graphics, BACKGROUND, x, y, 16, 16);
            RenderHelper.item(graphics, ShapeMap.transferStack(held, index), x, y);
        }
    }

    public void onMouseScrolled(int direction) {
        this.changeSlot(this.selectedIndex - direction);
    }

    public void tickLook() {
        float yaw = this.player().getYRot();
        if (!ClutterNoMoreClient.CONFIG.lookToSwitch.value()) {
            this.lastYaw = yaw;
            this.accumulatedYaw = 0;
            return;
        }
        this.accumulatedYaw += Mth.wrapDegrees(yaw - this.lastYaw);
        this.lastYaw = yaw;
        while (this.accumulatedYaw >= LOOK_DEGREES_PER_SWITCH) {
            this.accumulatedYaw -= LOOK_DEGREES_PER_SWITCH;
            this.changeSlot(this.selectedIndex + 1);
        }
        while (this.accumulatedYaw <= -LOOK_DEGREES_PER_SWITCH) {
            this.accumulatedYaw += LOOK_DEGREES_PER_SWITCH;
            this.changeSlot(this.selectedIndex - 1);
        }
    }

    public void changeSlot(int index) {
        int max = this.shapes.size() - 1;
        int previous = this.selectedIndex;
        boolean wrap = ClutterNoMoreClient.CONFIG.wrapScrolling.value() || ClutterNoMoreClient.CONFIG.hold.value() == ClutterNoMoreConfig.InputType.PRESS;
        if (wrap) {
            this.selectedIndex = index < 0 ? max : index > max ? 0 : index;
        } else {
            this.selectedIndex = Math.min(Math.max(index, 0), max);
        }
        if (this.selectedIndex == previous) return;
        LocalPlayer player = this.player();
        player.setItemInHand(InteractionHand.MAIN_HAND, ShapeMap.transferStack(player.getItemInHand(InteractionHand.MAIN_HAND), this.selectedIndex));
        ClutterNoMoreClient.playSwitchSound(player);
        ClutterNoMoreClient.sendChangeShape(ChangeShape.HELD, ChangeShape.HELD, this.selectedIndex);
    }

    public boolean shouldStayOpen() {
        LocalPlayer player = this.player();
        return ShapeMap.contains(player.getItemInHand(InteractionHand.MAIN_HAND).getItem()) && ClutterNoMoreClient.selectedSlot(player) == this.hotbarSlot;
    }

    private LocalPlayer player() {
        return Objects.requireNonNull(this.minecraft.player);
    }
}
