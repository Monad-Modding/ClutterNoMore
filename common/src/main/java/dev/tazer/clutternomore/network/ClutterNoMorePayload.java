package dev.tazer.clutternomore.network;

//? if >=1.20.5 {
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClutterNoMorePayload<T>(PacketType<T> packet, T value) implements CustomPacketPayload {

    @Override
    public CustomPacketPayload.Type<ClutterNoMorePayload<T>> type() {
        return this.packet.getPayloadType();
    }

    public void handleOnClient() {
        this.packet.handleOnClient(this.value);
    }
}
//?}
