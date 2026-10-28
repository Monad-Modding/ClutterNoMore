package dev.tazer.clutternomore.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.registry.ShapeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.EnumMap;
import java.util.Map;

public final class RuntimeData {

    private static final Map<ShapeType, JsonArray> TAGS = new EnumMap<>(ShapeType.class);

    private RuntimeData() {
    }

    public static void addToTag(ShapeType tag, ResourceLocation id) {
        TAGS.computeIfAbsent(tag, key -> new JsonArray()).add(id.toString());
    }

    public static void generate() {
        for (ShapeType tag : ShapeType.values()) {
            JsonObject file = tagFile(TAGS.getOrDefault(tag, new JsonArray()));
            writeServerData(ClutterNoMore.id(getBlockTagFolder() + "/" + tag.getTag() + ".json"), file);
            writeServerData(ClutterNoMore.id(getItemTagFolder() + "/" + tag.getTag() + ".json"), file);
        }
    }

    public static String getBlockTagFolder() {
        //? if >=1.21 {
        return "tags/block";
        //?} else {
        /*return "tags/blocks";
        *///?}
    }

    public static String getItemTagFolder() {
        //? if >=1.21 {
        return "tags/item";
        //?} else {
        /*return "tags/items";
        *///?}
    }

    public static void writeServerData(ResourceLocation file, JsonElement contents) {
        ClutterNoMore.RESOURCES.addJson(PackType.SERVER_DATA, file, contents);
    }

    public static ResourceLocation blockTagFile(ResourceLocation tag) {
        return ClutterNoMore.id(tag.getNamespace(), getBlockTagFolder() + "/" + tag.getPath() + ".json");
    }

    public static ResourceLocation itemTagFile(ResourceLocation tag) {
        return ClutterNoMore.id(tag.getNamespace(), getItemTagFolder() + "/" + tag.getPath() + ".json");
    }

    public static JsonObject tagFile(JsonArray ids) {
        JsonArray values = new JsonArray();
        for (JsonElement id : ids) {
            JsonObject value = new JsonObject();
            value.add("id", id);
            value.addProperty("required", false);
            values.add(value);
        }
        JsonObject tag = new JsonObject();
        tag.add("values", values);
        return tag;
    }
}
