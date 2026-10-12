package dev.tazer.clutternomore.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;
//? if <26.3 {
import net.minecraft.server.packs.AbstractPackResources;
//?}
//? if >=1.21 {
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.repository.PackSource;
//?}
//? if >=1.21.9 {
/*import com.google.gson.Strictness;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.util.InclusiveRange;
*///?} else {
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
//?}

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
//? if >=1.21 {
import java.util.Optional;
//?}

//? if >=26.3 {
/*public final class RuntimePackResources implements PackResources {
*///?} else {
public final class RuntimePackResources extends AbstractPackResources {
//?}

    //? if >=1.21.9 {
    /*public static final InclusiveRange<PackFormat> RESOURCE_PACK_VERSION = SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES).minorRange();
    public static final InclusiveRange<PackFormat> DATA_PACK_VERSION = SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA).minorRange();
    *///?} else {
    public static final int RESOURCE_PACK_VERSION = SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES);
    public static final int DATA_PACK_VERSION = SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA);
    //?}
    //? if >=1.21 {
    private static final PackLocationInfo INFO = new PackLocationInfo(ClutterNoMore.MOD_ID + "-runtime", Component.literal("ClutterNoMore"), PackSource.BUILT_IN, Optional.empty());
    //?} else {
    /*private static final String INFO = ClutterNoMore.MOD_ID + "-runtime";
    *///?}

    private static final int MIN_PACK_FORMAT = 15;
    private static final int MAX_PACK_FORMAT = 95;

    private final Map<ResourceLocation, byte[]> clientResources = new ConcurrentHashMap<>();
    private final Map<ResourceLocation, byte[]> serverData = new ConcurrentHashMap<>();
    private final Map<ResourceLocation, byte[]> reloadedClientResources = new ConcurrentHashMap<>();
    private final Map<ResourceLocation, byte[]> reloadedServerData = new ConcurrentHashMap<>();
    private final PackMetadataSection clientMetadata;
    private final PackMetadataSection serverMetadata;

    public RuntimePackResources() {
        //? if <1.21 {
        /*super(INFO, true);
        *///?} else if <26.3 {
        super(INFO);
        //?}
        //? if >=1.21 && <1.21.9 {
        this.clientMetadata = new PackMetadataSection(Component.literal("ClutterNoMore Runtime Client Resources"), RESOURCE_PACK_VERSION, Optional.empty());
        this.serverMetadata = new PackMetadataSection(Component.literal("ClutterNoMore Runtime Server Data"), DATA_PACK_VERSION, Optional.empty());
        //?} else {
        /*this.clientMetadata = new PackMetadataSection(Component.literal("ClutterNoMore Runtime Client Resources"), RESOURCE_PACK_VERSION);
        this.serverMetadata = new PackMetadataSection(Component.literal("ClutterNoMore Runtime Server Data"), DATA_PACK_VERSION);
        *///?}
    }

    public static String serializeJson(JsonElement json) {
        try (StringWriter output = new StringWriter(); JsonWriter writer = new JsonWriter(output)) {
            //? if >=1.21.9 {
            /*writer.setStrictness(Strictness.LENIENT);
            *///?} else {
            writer.setLenient(true);
            //?}
            writer.setIndent("  ");
            Streams.write(json, writer);
            return output.toString();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void addJson(PackType type, ResourceLocation id, JsonElement json) {
        this.resources(type).put(id, serializeJson(json).getBytes(StandardCharsets.UTF_8));
    }

    public void clearReloaded(PackType type) {
        this.reloaded(type).clear();
    }

    public void addReloaded(PackType type, ResourceLocation id, byte[] contents) {
        this.reloaded(type).put(id, contents);
    }

    public void addReloadedJson(PackType type, ResourceLocation id, JsonElement json) {
        this.addReloaded(type, id, serializeJson(json).getBytes(StandardCharsets.UTF_8));
    }

    public void dump(PackType type, Path root) {
        Path folder = root.resolve(type.getDirectory());
        try {
            if (Files.isDirectory(folder)) {
                List<Path> stale;
                try (Stream<Path> files = Files.walk(folder)) {
                    stale = files.sorted(Comparator.reverseOrder()).toList();
                }
                for (Path path : stale) {
                    Files.delete(path);
                }
            }
            Map<ResourceLocation, byte[]> contents = new HashMap<>(this.resources(type));
            contents.putAll(this.reloaded(type));
            for (Map.Entry<ResourceLocation, byte[]> entry : contents.entrySet()) {
                Path file = folder.resolve(entry.getKey().getNamespace()).resolve(entry.getKey().getPath());
                Files.createDirectories(file.getParent());
                Files.write(file, entry.getValue());
            }
            writePackMeta(root);
        } catch (IOException exception) {
            ClutterNoMore.LOGGER.error("Failed to write the runtime pack copy to {}", folder, exception);
        }
    }

    private static void writePackMeta(Path root) throws IOException {
        JsonObject pack = new JsonObject();
        pack.add("description", new JsonPrimitive("Generated resources for ClutterNoMore"));
        //? if <1.21.9 {
        pack.add("pack_format", new JsonPrimitive(RESOURCE_PACK_VERSION));
        //?} else {
        /*pack.add("pack_format", new JsonPrimitive(MIN_PACK_FORMAT));
        *///?}
        pack.add("min_format", new JsonPrimitive(MIN_PACK_FORMAT));
        pack.add("max_format", new JsonPrimitive(MAX_PACK_FORMAT));
        JsonArray supported = new JsonArray(2);
        supported.add(MIN_PACK_FORMAT);
        supported.add(MAX_PACK_FORMAT);
        pack.add("supported_formats", supported);
        JsonObject meta = new JsonObject();
        meta.add("pack", pack);
        Files.createDirectories(root);
        Files.writeString(root.resolve("pack.mcmeta"), serializeJson(meta));
    }

    private Map<ResourceLocation, byte[]> resources(PackType type) {
        return type == PackType.CLIENT_RESOURCES ? this.clientResources : this.serverData;
    }

    private Map<ResourceLocation, byte[]> reloaded(PackType type) {
        return type == PackType.CLIENT_RESOURCES ? this.reloadedClientResources : this.reloadedServerData;
    }

    //? if >=26.3 {
    /*@Override
    public PackLocationInfo location() {
        return INFO;
    }
    *///?}

    @Override
    public Set<String> getNamespaces(PackType type) {
        Set<String> namespaces = new HashSet<>();
        namespaces.add(ClutterNoMore.MOD_ID);
        for (ResourceLocation id : this.resources(type).keySet()) {
            namespaces.add(id.getNamespace());
        }
        for (ResourceLocation id : this.reloaded(type).keySet()) {
            namespaces.add(id.getNamespace());
        }
        return Set.copyOf(namespaces);
    }

    //? if >=1.21.9 {
    /*@Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getMetadataSection(MetadataSectionType<T> type) {
        if (type == PackMetadataSection.CLIENT_TYPE) return (T) this.clientMetadata;
        if (type == PackMetadataSection.SERVER_TYPE) return (T) this.serverMetadata;
        return null;
    }
    *///?} else {
    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) {
        return deserializer == PackMetadataSection.TYPE ? (T) this.clientMetadata : null;
    }
    //?}

    @Override
    @Nullable
    public IoSupplier<InputStream> getRootResource(String... path) {
        return null;
    }

    @Override
    @Nullable
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation id) {
        byte[] reloaded = this.reloaded(type).get(id);
        byte[] resource = reloaded == null ? this.resources(type).get(id) : reloaded;
        return resource == null ? null : () -> new ByteArrayInputStream(resource);
    }

    @Override
    public void listResources(PackType type, String namespace, String path, PackResources.ResourceOutput output) {
        Map<ResourceLocation, byte[]> reloaded = this.reloaded(type);
        for (Map.Entry<ResourceLocation, byte[]> entry : reloaded.entrySet()) {
            list(entry.getKey(), entry.getValue(), namespace, path, output);
        }
        for (Map.Entry<ResourceLocation, byte[]> entry : this.resources(type).entrySet()) {
            if (!reloaded.containsKey(entry.getKey())) {
                list(entry.getKey(), entry.getValue(), namespace, path, output);
            }
        }
    }

    private static void list(ResourceLocation id, byte[] resource, String namespace, String path, PackResources.ResourceOutput output) {
        if (id.getNamespace().equals(namespace) && id.getPath().startsWith(path)) {
            output.accept(id, () -> new ByteArrayInputStream(resource));
        }
    }

    @Override
    public void close() {
    }
}
