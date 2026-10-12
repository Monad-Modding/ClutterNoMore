package dev.tazer.clutternomore.fabric.client;

import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint;
import dev.tazer.clutternomore.compat.ControlifyCompat;
//? if >=1.21 {
import dev.isxander.controlify.api.entrypoint.InitContext;
import dev.isxander.controlify.api.entrypoint.PreInitContext;
//?} else {
/*import dev.isxander.controlify.api.bind.ControlifyBindApi;
*///?}

public final class FabricControlifyEntrypoint implements ControlifyEntrypoint {

    @Override
    public void onControllersDiscovered(ControlifyApi controlify) {
    }

    //? if >=1.21 {
    @Override
    public void onControlifyInit(InitContext context) {
    }

    @Override
    public void onControlifyPreInit(PreInitContext context) {
        ControlifyCompat.register(context.bindings());
    }
    //?} else {
    /*@Override
    public void onControlifyPreInit(ControlifyApi controlify) {
        ControlifyCompat.register(ControlifyBindApi.get());
    }
    *///?}
}
