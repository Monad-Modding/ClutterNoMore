package dev.tazer.clutternomore.client;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
//? if >=1.21.6 {
/*import net.minecraft.client.renderer.RenderPipelines;
*///?}
//? if <1.21.2 {
import com.mojang.blaze3d.systems.RenderSystem;
//?}

public final class RenderHelper {

    private static final ResourceLocation SHAPE_INDICATOR = ClutterNoMore.id("textures/gui/shape_indicator.png");

    private RenderHelper() {
    }

    public static void shapeBadge(GuiGraphics graphics, @Nullable ItemStack stack, int x, int y) {
        if (stack == null || stack.isEmpty() || !ClutterNoMoreClient.CONFIG.shapeIndicator.value() || !ShapeMap.contains(stack.getItem())) return;
        //? if <1.21.6 {
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 200.0F);
        blit(graphics, SHAPE_INDICATOR, x - 4, y - 4, 24, 24);
        graphics.pose().popPose();
        //?} else {
        /*blit(graphics, SHAPE_INDICATOR, x - 4, y - 4, 24, 24);
        *///?}
    }

    public static void item(GuiGraphics graphics, ItemStack stack, int x, int y) {
        //? if >=26 {
        /*graphics.fakeItem(stack, x, y);
        *///?} else {
        graphics.renderFakeItem(stack, x, y);
        //?}
    }

    public static void blit(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height) {
        //? if >=1.21.6 {
        /*graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, width, height, width, height);
        *///?} else {
        RenderSystem.enableBlend();
        graphics.blit(texture, x, y, 0, 0, width, height, width, height);
        //?}
    }
}
