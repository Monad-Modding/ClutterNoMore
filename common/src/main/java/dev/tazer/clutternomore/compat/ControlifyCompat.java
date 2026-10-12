package dev.tazer.clutternomore.compat;

import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.bind.ControlifyBindApi;
import dev.isxander.controlify.api.bind.InputBinding;
import dev.isxander.controlify.api.bind.InputBindingSupplier;
import dev.isxander.controlify.controller.ControllerEntity;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.client.ClutterNoMoreClient;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
//? if >=1.21.9 {
/*import net.minecraft.client.KeyMapping;
*///?}

import java.util.Optional;

public final class ControlifyCompat {

    @Nullable
    private static InputBindingSupplier shapeBinding;

    public static void register(ControlifyBindApi bindings) {
        shapeBinding = bindings.registerBinding(builder -> builder.id(ClutterNoMore.id("change_block_shape")).name(Component.translatable("key.clutternomore.change_block_shape")).addKeyCorrelation(ClutterNoMoreClient.SHAPE_KEY).category(category()));
    }

    public static boolean isControllerActive() {
        return ControlifyApi.get().currentInputMode().isController();
    }

    public static void tick() {
        if (shapeBinding == null || !isControllerActive()) return;
        Optional<ControllerEntity> controller = ControlifyApi.get().getCurrentController();
        if (controller.isEmpty()) return;
        InputBinding binding = shapeBinding.on(controller.get());
        ClutterNoMoreClient.onControllerShapeKey(binding.digitalNow(), binding.justPressed(), binding.justReleased(), binding.justTapped());
    }

    private static Component category() {
        //? if >=1.21.9 {
        /*return KeyMapping.Category.INVENTORY.label();
        *///?} else {
        return Component.translatable("key.categories.inventory");
        //?}
    }
}
