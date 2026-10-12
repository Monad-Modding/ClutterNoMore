package dev.tazer.clutternomore.fabric.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.tazer.clutternomore.client.ShapeSwitcherOptionsScreen;

public final class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ShapeSwitcherOptionsScreen::new;
    }
}
