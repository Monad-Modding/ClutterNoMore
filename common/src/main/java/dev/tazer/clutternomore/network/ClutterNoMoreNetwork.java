package dev.tazer.clutternomore.network;

import dev.tazer.clutternomore.platform.Services;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public final class ClutterNoMoreNetwork {

    public static final PacketType<ChangeShape> CHANGE_SHAPE = PacketType.serverbound("player_change_stack", ChangeShape.class, ChangeShape::write, ChangeShape::read, ChangeShape::handle);
    public static final PacketType<ShapeMapSync> SHAPE_MAP = PacketType.clientbound("shapes", ShapeMapSync.class, ShapeMapSync::write, ShapeMapSync::read);

    private ClutterNoMoreNetwork() {
    }

    public static List<PacketType<?>> all() {
        return List.of(CHANGE_SHAPE, SHAPE_MAP);
    }

    public static <T> void sendToServer(PacketType<T> type, T packet) {
        Services.PLATFORM.sendToServer(type, packet);
    }

    public static <T> void sendToPlayer(ServerPlayer player, PacketType<T> type, T packet) {
        Services.PLATFORM.sendToPlayer(player, type, packet);
    }
}
