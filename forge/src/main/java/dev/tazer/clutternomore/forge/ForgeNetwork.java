package dev.tazer.clutternomore.forge;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.network.ClutterNoMoreNetwork;
import dev.tazer.clutternomore.network.PacketType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;
import java.util.function.Supplier;

public final class ForgeNetwork {

    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(ClutterNoMore.id("main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    private ForgeNetwork() {
    }

    public static void register() {
        List<PacketType<?>> types = ClutterNoMoreNetwork.all();
        for (int index = 0; index < types.size(); index++) {
            register(types.get(index), index);
        }
    }

    private static <T> void register(PacketType<T> type, int index) {
        CHANNEL.registerMessage(index, type.getType(), (packet, buf) -> type.write(buf, packet), type::read, (packet, context) -> handle(type, packet, context));
    }

    private static <T> void handle(PacketType<T> type, T packet, Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context network = context.get();
        network.enqueueWork(() -> {
            ServerPlayer sender = network.getSender();
            if (type.isServerbound()) {
                if (sender != null) {
                    type.handleOnServer(packet, sender);
                }
            } else if (sender == null) {
                type.handleOnClient(packet);
            }
        });
        network.setPacketHandled(true);
    }
}
