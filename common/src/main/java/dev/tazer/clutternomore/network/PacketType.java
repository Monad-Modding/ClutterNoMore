package dev.tazer.clutternomore.network;

import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
//? if >=1.20.5 {
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?}

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public final class PacketType<T> {

    private final ResourceLocation id;
    private final Class<T> type;
    private final boolean serverbound;
    private final BiConsumer<FriendlyByteBuf, T> writer;
    private final Function<FriendlyByteBuf, T> reader;
    //? if >=1.20.5 {
    private final CustomPacketPayload.Type<ClutterNoMorePayload<T>> payloadType;
    private final StreamCodec<RegistryFriendlyByteBuf, ClutterNoMorePayload<T>> streamCodec;
    //?}
    @Nullable
    private final BiConsumer<T, ServerPlayer> serverHandler;

    @Nullable
    private Consumer<T> clientHandler;

    private PacketType(String name, Class<T> type, boolean serverbound, BiConsumer<FriendlyByteBuf, T> writer, Function<FriendlyByteBuf, T> reader, @Nullable BiConsumer<T, ServerPlayer> serverHandler) {
        this.id = ClutterNoMore.id(name);
        this.type = type;
        this.serverbound = serverbound;
        this.writer = writer;
        this.reader = reader;
        this.serverHandler = serverHandler;
        //? if >=1.20.5 {
        this.payloadType = new CustomPacketPayload.Type<>(this.id);
        this.streamCodec = StreamCodec.of((buf, payload) -> this.write(buf, payload.value()), buf -> this.wrap(this.read(buf)));
        //?}
    }

    public static <T> PacketType<T> serverbound(String name, Class<T> type, BiConsumer<FriendlyByteBuf, T> writer, Function<FriendlyByteBuf, T> reader, BiConsumer<T, ServerPlayer> handler) {
        return new PacketType<>(name, type, true, writer, reader, handler);
    }

    public static <T> PacketType<T> clientbound(String name, Class<T> type, BiConsumer<FriendlyByteBuf, T> writer, Function<FriendlyByteBuf, T> reader) {
        return new PacketType<>(name, type, false, writer, reader, null);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public Class<T> getType() {
        return this.type;
    }

    public boolean isServerbound() {
        return this.serverbound;
    }

    //? if >=1.20.5 {
    public CustomPacketPayload.Type<ClutterNoMorePayload<T>> getPayloadType() {
        return this.payloadType;
    }

    public StreamCodec<RegistryFriendlyByteBuf, ClutterNoMorePayload<T>> getStreamCodec() {
        return this.streamCodec;
    }

    public ClutterNoMorePayload<T> wrap(T value) {
        return new ClutterNoMorePayload<>(this, value);
    }
    //?}

    public void setClientHandler(Consumer<T> handler) {
        this.clientHandler = handler;
    }

    public void write(FriendlyByteBuf buf, T value) {
        this.writer.accept(buf, value);
    }

    public T read(FriendlyByteBuf buf) {
        return this.reader.apply(buf);
    }

    public void handleOnServer(T packet, ServerPlayer player) {
        if (this.serverHandler != null) {
            this.serverHandler.accept(packet, player);
        }
    }

    public void handleOnClient(T packet) {
        if (this.clientHandler != null) {
            this.clientHandler.accept(packet);
        }
    }
}
