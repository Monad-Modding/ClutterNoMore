package dev.tazer.clutternomore.client;

import com.google.common.collect.ImmutableList;
import dev.tazer.clutternomore.ClutterNoMoreConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
//? if >=1.20.5 {
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
//?} else {
/*import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
*///?}

public final class ShapeSwitcherOptionsScreen extends OptionsSubScreen {

    public static final Component TITLE = Component.translatable("options.clutternomore.shape_switcher.title");
    public static final OptionInstance.Enum<ClutterNoMoreConfig.InputType> INPUT_TYPES = new OptionInstance.Enum<>(ImmutableList.of(ClutterNoMoreConfig.InputType.HOLD, ClutterNoMoreConfig.InputType.TOGGLE, ClutterNoMoreConfig.InputType.PRESS), ClutterNoMoreConfig.InputType.CODEC);

    public ShapeSwitcherOptionsScreen(Screen lastScreen, Options options) {
        super(lastScreen, options, TITLE);
    }

    public ShapeSwitcherOptionsScreen(Screen lastScreen) {
        this(lastScreen, Minecraft.getInstance().options);
    }

    private static OptionInstance<?>[] options() {
        ClutterNoMoreConfig.Client config = ClutterNoMoreClient.CONFIG;
        OptionInstance<Boolean> menuType = new OptionInstance<>("key.clutternomore.menu_type", OptionInstance.noTooltip(), (caption, value) -> Component.translatable(value ? "key.clutternomore.menu_scrolling" : "key.clutternomore.menu_static"), OptionInstance.BOOLEAN_VALUES, config.scrolling.value(), config.scrolling::setValue);
        OptionInstance<ClutterNoMoreConfig.InputType> openMenu = new OptionInstance<>("key.clutternomore.open_menu", OptionInstance.noTooltip(), (caption, value) -> Component.translatable("key.clutternomore.menu_" + value), INPUT_TYPES, config.hold.value(), config.hold::setValue);
        return new OptionInstance<?>[]{menuType, openMenu, toggle("key.clutternomore.wrap_scrolling", config.wrapScrolling), toggle("key.clutternomore.look_to_switch", config.lookToSwitch), toggle("key.clutternomore.shape_indicator", config.shapeIndicator), toggle("key.clutternomore.detailed_tooltips", config.detailedTooltips)};
    }

    private static OptionInstance<Boolean> toggle(String key, TrackedValue<Boolean> value) {
        return new OptionInstance<>(key, OptionInstance.noTooltip(), (caption, enabled) -> Component.translatable(enabled ? "key.clutternomore.enabled" : "key.clutternomore.disabled"), OptionInstance.BOOLEAN_VALUES, value.value(), value::setValue);
    }

    //? if >=1.20.5 {
    @Override
    protected void addOptions() {
        if (this.list != null) {
            this.list.addSmall(options());
        }
    }
    //?} else {
    /*@Override
    protected void init() {
        OptionsList list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        list.addSmall(options());
        this.addRenderableWidget(list);
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> {
            this.minecraft.options.save();
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }
    *///?}
}
