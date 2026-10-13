package dev.tazer.clutternomore.shape;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
//? if >=1.21.2 {
/*import net.minecraft.resources.FileToIdConverter;
import net.minecraft.util.ExtraCodecs;
*///?} else {
import com.google.gson.Gson;
//?}

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//? if >=1.21.2 {
/*public class ShapeMapReloadListener extends SimpleJsonResourceReloadListener<JsonElement> {
*///?} else {
public class ShapeMapReloadListener extends SimpleJsonResourceReloadListener {
//?}

    public static final String DIRECTORY = "shape_map";
    private static final Pattern NAMED_GROUP = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>");

    private static volatile List<LoadedFile> pending = List.of();

    public ShapeMapReloadListener() {
        //? if >=1.21.2 {
        /*super(ExtraCodecs.JSON, FileToIdConverter.json(DIRECTORY));
        *///?} else {
        super(new Gson(), DIRECTORY);
        //?}
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager manager, ProfilerFiller profiler) {
        List<LoadedFile> loaded = new ArrayList<>();
        if (ClutterNoMore.STARTUP_CONFIG.shapeMaps.value()) {
            for (Map.Entry<ResourceLocation, JsonElement> entry : files.entrySet()) {
                DataResult<ShapeMapFile> parsed = ShapeMapFile.CODEC.parse(JsonOps.INSTANCE, entry.getValue());
                parsed.error().ifPresent(error -> ClutterNoMore.LOGGER.error("[ShapeMap] failed to parse '{}': {}", entry.getKey(), error.message()));
                parsed.result().ifPresent(file -> loaded.add(new LoadedFile(entry.getKey(), file)));
            }
            loaded.sort(Comparator.comparingInt((LoadedFile file) -> file.contents().priority()).thenComparing(file -> file.id().toString()));
        }
        pending = List.copyOf(loaded);
    }

    public static void resolve() {
        boolean detailed = ClutterNoMore.STARTUP_CONFIG.detailedLogs.value();
        ShapeMapFile.ScanIndex index = ShapeMapFile.ScanIndex.get();
        List<ShapeMap.Mapping> mappings = new ArrayList<>();
        for (LoadedFile file : pending) {
            applyRules(file, mappings, detailed, true, index);
            applyRules(file, mappings, detailed, false, index);
        }
        ShapeMap.setMappings(mappings, detailed);
        ClutterNoMore.LOGGER.info("[ShapeMap] Resolved {} shape sets from {} files", ShapeMap.getShapesByParent().size(), pending.size());
    }

    private static void applyRules(LoadedFile file, List<ShapeMap.Mapping> mappings, boolean detailed, boolean adding, ShapeMapFile.ScanIndex index) {
        Map<ShapeMapFile.Key, ShapeMapFile.Rule> rules = adding ? file.contents().add() : file.contents().remove();
        for (Map.Entry<ShapeMapFile.Key, ShapeMapFile.Rule> rule : rules.entrySet()) {
            ShapeMapFile.Key key = rule.getKey();
            List<ShapeMapFile.Template> templates = rule.getValue().shapes();
            ShapeMapFile.Conditions conditions = rule.getValue().conditions().orElse(null);
            if (adding) {
                if (detailed && key instanceof ShapeMapFile.Key.Tag) {
                    warnTagOnTag(key, templates, file.id());
                }
                for (KeyMatch match : resolveKey(key, file.id(), detailed, index)) {
                    if (conditions != null && !conditions.evaluate(match.item(), match.vars(), index)) {
                        continue;
                    }
                    for (ShapeMapFile.Template template : templates) {
                        for (Item shape : resolveValue(template.substitute(match.vars()), template.raw(), file.id(), detailed, index)) {
                            if (shape != match.item()) {
                                mappings.add(new ShapeMap.Mapping(match.item(), shape, file.contents().priority(), file.id()));
                            }
                        }
                    }
                }
            } else {
                Map<Item, List<Item>> parentsByShape = parentsByShape(mappings);
                ShapeMapFile.Selector[] selectors = constantSelectors(templates);
                mappings.removeIf(mapping -> ruleMatches(key, templates, selectors, conditions, mapping, index, parentsByShape));
            }
        }
    }

    private static void warnTagOnTag(ShapeMapFile.Key key, List<ShapeMapFile.Template> templates, ResourceLocation source) {
        for (ShapeMapFile.Template template : templates) {
            if (template.raw().startsWith("#")) {
                ClutterNoMore.LOGGER.info("[ShapeMap] tag-on-tag rule '{}' -> '{}' produces a full cross product shape set in {}. This is not recommended and likely a mistake!", key.getRaw(), template.raw(), source);
                return;
            }
        }
    }

    private static boolean ruleMatches(ShapeMapFile.Key key, List<ShapeMapFile.Template> templates, ShapeMapFile.Selector[] selectors, @Nullable ShapeMapFile.Conditions conditions, ShapeMap.Mapping mapping, ShapeMapFile.ScanIndex index, Map<Item, List<Item>> parentsByShape) {
        Item parent = mapping.parent();
        Item shape = mapping.shape();
        if (candidateMatches(key, templates, selectors, conditions, parent, shape, index)) return true;
        List<Item> immediate = parentsByShape.get(parent);
        if (immediate == null) return false;
        List<Item> queue = new ArrayList<>(immediate);
        Set<Item> visited = new HashSet<>();
        visited.add(parent);
        for (int i = 0; i < queue.size(); i++) {
            Item candidate = queue.get(i);
            if (!visited.add(candidate)) {
                continue;
            }
            if (candidateMatches(key, templates, selectors, conditions, candidate, shape, index)) return true;
            List<Item> up = parentsByShape.get(candidate);
            if (up != null) {
                queue.addAll(up);
            }
        }
        return false;
    }

    private static boolean candidateMatches(ShapeMapFile.Key key, List<ShapeMapFile.Template> templates, ShapeMapFile.Selector[] selectors, @Nullable ShapeMapFile.Conditions conditions, Item candidate, Item shape, ShapeMapFile.ScanIndex index) {
        Map<String, String> vars = matchKey(key, candidate);
        if (vars == null) return false;
        if (conditions != null && !conditions.evaluate(candidate, vars, index)) return false;
        for (int i = 0; i < templates.size(); i++) {
            ShapeMapFile.Selector selector = selectors[i] != null ? selectors[i] : ShapeMapFile.parseSelector(templates.get(i).substitute(vars), null);
            if (ShapeMapFile.matches(selector, shape)) return true;
        }
        return false;
    }

    private static Map<Item, List<Item>> parentsByShape(List<ShapeMap.Mapping> mappings) {
        Map<Item, List<Item>> parents = new HashMap<>();
        for (ShapeMap.Mapping mapping : mappings) {
            parents.computeIfAbsent(mapping.shape(), key -> new ArrayList<>()).add(mapping.parent());
        }
        return parents;
    }

    private static ShapeMapFile.Selector[] constantSelectors(List<ShapeMapFile.Template> templates) {
        ShapeMapFile.Selector[] selectors = new ShapeMapFile.Selector[templates.size()];
        for (int i = 0; i < templates.size(); i++) {
            String raw = templates.get(i).raw();
            if (!raw.contains("${")) {
                selectors[i] = ShapeMapFile.parseSelector(raw, null);
            }
        }
        return selectors;
    }

    @Nullable
    private static Map<String, String> matchKey(ShapeMapFile.Key key, Item parent) {
        ResourceLocation parentId = BuiltInRegistries.ITEM.getKey(parent);
        if (key instanceof ShapeMapFile.Key.Literal literal) return literal.id().equals(parentId) ? defaultVars(parentId) : null;
        if (key instanceof ShapeMapFile.Key.Tag tag) return BuiltInRegistries.ITEM.wrapAsHolder(parent).is(TagKey.create(Registries.ITEM, tag.tagId())) ? defaultVars(parentId) : null;
        if (key instanceof ShapeMapFile.Key.Regex regex) {
            Matcher matcher = regex.pattern().matcher(parentId.toString());
            if (!matcher.matches()) return null;
            return captureVars(parentId, matcher, namedGroups(regex.pattern()));
        }
        return null;
    }

    private static List<KeyMatch> resolveKey(ShapeMapFile.Key key, ResourceLocation source, boolean detailed, ShapeMapFile.ScanIndex index) {
        if (key instanceof ShapeMapFile.Key.Literal literal) {
            Optional<Item> item = BuiltInRegistries.ITEM.getOptional(literal.id());
            return item.map(value -> List.of(new KeyMatch(value, defaultVars(literal.id())))).orElseGet(List::of);
        }
        if (key instanceof ShapeMapFile.Key.Tag tag) {
            List<Item> tagged = itemsInTag(TagKey.create(Registries.ITEM, tag.tagId()));
            if (tagged.isEmpty()) {
                if (detailed) {
                    ClutterNoMore.LOGGER.info("[ShapeMap] missing/empty tag '#{}' (requested in {})", tag.tagId(), source);
                }
                return List.of();
            }
            List<KeyMatch> matches = new ArrayList<>(tagged.size());
            for (Item item : tagged) {
                matches.add(new KeyMatch(item, defaultVars(BuiltInRegistries.ITEM.getKey(item))));
            }
            return matches;
        }
        if (key instanceof ShapeMapFile.Key.Regex regex) {
            Set<String> names = namedGroups(regex.pattern());
            List<KeyMatch> matches = new ArrayList<>();
            for (int i = 0; i < index.ids().length; i++) {
                Matcher matcher = regex.pattern().matcher(index.ids()[i]);
                if (matcher.matches()) {
                    matches.add(new KeyMatch(index.items().get(i), captureVars(index.identifiers()[i], matcher, names)));
                }
            }
            return matches;
        }
        return List.of();
    }

    private static Map<String, String> captureVars(ResourceLocation id, Matcher matcher, Set<String> names) {
        Map<String, String> vars = defaultVars(id);
        for (int group = 1; group <= matcher.groupCount(); group++) {
            String captured = matcher.group(group);
            if (captured != null) {
                vars.put(String.valueOf(group), captured);
            }
        }
        for (String name : names) {
            try {
                String captured = matcher.group(name);
                if (captured != null) {
                    vars.put(name, captured);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        return vars;
    }

    private static List<Item> resolveValue(String resolved, String original, ResourceLocation source, boolean detailed, ShapeMapFile.ScanIndex index) {
        List<Item> items = ShapeMapFile.resolveItems(resolved, message -> ClutterNoMore.LOGGER.error("[ShapeMap] {} (from template '{}') in {}", message, original, source), index);
        if (detailed && resolved.startsWith("#") && items.isEmpty()) {
            ClutterNoMore.LOGGER.info("[ShapeMap] missing/empty tag '{}' (value in {})", resolved, source);
        }
        return items;
    }

    private static List<Item> itemsInTag(TagKey<Item> tag) {
        List<Item> items = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (BuiltInRegistries.ITEM.wrapAsHolder(item).is(tag)) {
                items.add(item);
            }
        }
        return items;
    }

    private static Map<String, String> defaultVars(ResourceLocation id) {
        Map<String, String> vars = new HashMap<>();
        vars.put("ns", id.getNamespace());
        vars.put("path", id.getPath());
        return vars;
    }

    private static Set<String> namedGroups(Pattern pattern) {
        Set<String> names = new LinkedHashSet<>();
        Matcher matcher = NAMED_GROUP.matcher(pattern.pattern());
        while (matcher.find()) {
            names.add(matcher.group(1));
        }
        return names;
    }

    private record LoadedFile(ResourceLocation id, ShapeMapFile contents) {
    }

    private record KeyMatch(Item item, Map<String, String> vars) {
    }
}
