package dev.tazer.clutternomore.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class TagFiles {

    private static final List<String> REMOVAL_KEYS = List.of("remove", "fabric:remove");

    private final ResourceManager manager;
    private final String folder;
    private final Map<ResourceLocation, Set<ResourceLocation>> resolved = new HashMap<>();
    private final Set<ResourceLocation> resolving = new HashSet<>();

    public TagFiles(ResourceManager manager, String folder) {
        this.manager = manager;
        this.folder = folder;
    }

    public Set<ResourceLocation> listTags() {
        Set<ResourceLocation> tags = new LinkedHashSet<>();
        int start = this.folder.length() + 1;
        for (ResourceLocation file : this.manager.listResources(this.folder, id -> id.getPath().endsWith(".json")).keySet()) {
            String path = file.getPath();
            tags.add(ClutterNoMore.id(file.getNamespace(), path.substring(start, path.length() - ".json".length())));
        }
        return tags;
    }

    public Set<ResourceLocation> resolve(ResourceLocation tag) {
        Set<ResourceLocation> known = this.resolved.get(tag);
        if (known != null) return known;
        if (!this.resolving.add(tag)) return Set.of();
        List<TagReference> values = new ArrayList<>();
        List<TagReference> removed = new ArrayList<>();
        for (Resource resource : this.manager.getResourceStack(ClutterNoMore.id(tag.getNamespace(), this.folder + "/" + tag.getPath() + ".json"))) {
            JsonObject json = read(resource);
            if (json == null) {
                continue;
            }
            if (GsonHelper.getAsBoolean(json, "replace", false)) {
                values.clear();
                removed.clear();
            }
            collect(json.get("values"), values);
            for (String key : REMOVAL_KEYS) {
                collect(json.get(key), removed);
            }
        }
        Set<ResourceLocation> members = new HashSet<>();
        for (TagReference value : values) {
            if (value.tag()) {
                members.addAll(this.resolve(value.id()));
            } else {
                members.add(value.id());
            }
        }
        for (TagReference value : removed) {
            if (value.tag()) {
                members.removeAll(this.resolve(value.id()));
            } else {
                members.remove(value.id());
            }
        }
        this.resolving.remove(tag);
        this.resolved.put(tag, members);
        return members;
    }

    public Set<ResourceLocation> resolveEntry(String entry) {
        if (entry.isEmpty()) return Set.of();
        boolean tag = entry.startsWith("#");
        ResourceLocation id = ResourceLocation.tryParse(tag ? entry.substring(1) : entry);
        if (id == null) return Set.of();
        return tag ? this.resolve(id) : Set.of(id);
    }

    @Nullable
    public static JsonObject read(Resource resource) {
        try (Reader reader = resource.openAsReader()) {
            JsonElement json = JsonParser.parseReader(reader);
            return json.isJsonObject() ? json.getAsJsonObject() : null;
        } catch (Exception exception) {
            ClutterNoMore.LOGGER.debug("Skipping unreadable file from {}", resource.sourcePackId());
            return null;
        }
    }

    private static void collect(@Nullable JsonElement element, List<TagReference> output) {
        if (element == null || !element.isJsonArray()) return;
        for (JsonElement entry : element.getAsJsonArray()) {
            String id = entry.isJsonObject() ? GsonHelper.getAsString(entry.getAsJsonObject(), "id", "") : entry.isJsonPrimitive() ? entry.getAsString() : "";
            if (id.isEmpty()) {
                continue;
            }
            boolean tag = id.startsWith("#");
            ResourceLocation location = ResourceLocation.tryParse(tag ? id.substring(1) : id);
            if (location != null) {
                output.add(new TagReference(location, tag));
            }
        }
    }

    private record TagReference(ResourceLocation id, boolean tag) {
    }
}
