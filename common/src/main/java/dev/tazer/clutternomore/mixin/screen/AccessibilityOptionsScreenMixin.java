package dev.tazer.clutternomore.mixin.screen;

import dev.tazer.clutternomore.client.ShapeSwitcherOptionsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >=1.20.5 {
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;

import java.util.List;
//?} else {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
*///?}

@Mixin(AccessibilityOptionsScreen.class)
public abstract class AccessibilityOptionsScreenMixin extends OptionsSubScreen {

    public AccessibilityOptionsScreenMixin(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title);
    }

    private Button cnm$shapeSwitcherButton(int x, int y) {
        return Button.builder(Component.translatable("key.clutternomore.shape_switcher"), button -> Minecraft.getInstance().setScreen(new ShapeSwitcherOptionsScreen(this))).bounds(x, y, 150, 20).build();
    }

    //? if >=1.20.5 {
    @Inject(method = "addOptions", at = @At("RETURN"))
    private void cnm$addShapeSwitcherButton(CallbackInfo ci) {
        if (this.list != null) {
            this.list.addSmall(List.of(this.cnm$shapeSwitcherButton(0, 0)));
        }
    }
    //?} else {
    /*@WrapOperation(method = "createFooter", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/Button$Builder;bounds(IIII)Lnet/minecraft/client/gui/components/Button$Builder;"))
    private Button.Builder cnm$shiftDoneButton(Button.Builder builder, int x, int y, int width, int height, Operation<Button.Builder> original) {
        return original.call(builder, x - 80, y, width, height);
    }

    @Inject(method = "createFooter", at = @At("RETURN"))
    private void cnm$addShapeSwitcherButton(CallbackInfo ci) {
        this.addRenderableWidget(this.cnm$shapeSwitcherButton(this.width / 2 + 80, this.height - 27));
    }
    *///?}
}
