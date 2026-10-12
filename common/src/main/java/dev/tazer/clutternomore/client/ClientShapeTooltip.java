package dev.tazer.clutternomore.client;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.shape.ShapeTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
//? if <1.21.2 {
import com.mojang.blaze3d.systems.RenderSystem;
//?}

import java.util.List;

public final class ClientShapeTooltip implements ClientTooltipComponent {

    private static final int SPACING = 22;
    private static final ResourceLocation SELECTED = ClutterNoMore.id("textures/gui/selected_shape_inventory.png");

    private final List<ItemStack> stacks;
    private final int selectedIndex;

    public ClientShapeTooltip(ShapeTooltip tooltip) {
        this.stacks = tooltip.stacks();
        this.selectedIndex = tooltip.selectedIndex();
    }

    @Override
    //? if >=1.21.2 {
    /*public int getHeight(Font font) {
    *///?} else {
    public int getHeight() {
    //?}
        return ClutterNoMoreClient.isRenderingIcons() ? SPACING : 0;
    }

    @Override
    public int getWidth(Font font) {
        return ClutterNoMoreClient.isRenderingIcons() ? this.stacks.size() * SPACING : 0;
    }

    @Override
    //? if >=26 {
    /*public void extractImage(Font font, int x, int y, int width, int height, GuiGraphics graphics) {
    *///?} else {
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
    //?}
        if (!ClutterNoMoreClient.isRenderingIcons()) return;
        int startX = x + 2;
        for (int index = 0; index < this.stacks.size(); index++) {
            RenderHelper.item(graphics, this.stacks.get(index), startX + index * SPACING, y);
        }
        RenderHelper.blit(graphics, SELECTED, startX + this.selectedIndex * SPACING - 3, y - 3, SPACING, SPACING);
        //? if <1.21.2 {
        RenderSystem.disableBlend();
        //?}
    }
}
