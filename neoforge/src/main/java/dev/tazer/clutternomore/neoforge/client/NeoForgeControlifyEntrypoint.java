package dev.tazer.clutternomore.neoforge.client;

import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint;
import dev.isxander.controlify.api.entrypoint.InitContext;
import dev.isxander.controlify.api.entrypoint.PreInitContext;
import dev.tazer.clutternomore.compat.ControlifyCompat;

public final class NeoForgeControlifyEntrypoint implements ControlifyEntrypoint {

    @Override
    public void onControllersDiscovered(ControlifyApi controlify) {
    }

    @Override
    public void onControlifyInit(InitContext context) {
    }

    @Override
    public void onControlifyPreInit(PreInitContext context) {
        ControlifyCompat.register(context.bindings());
    }
}
