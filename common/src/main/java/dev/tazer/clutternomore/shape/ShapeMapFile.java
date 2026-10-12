package dev.tazer.clutternomore.shape;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public record ShapeMapFile(int priority, Map<ShapeMapFile.Key, ShapeMapFile.Rule> add, Map<ShapeMapFile.Key, ShapeMapFile.Rule> remove) {

    public static final int DEFAULT_PRIORITY = 1000;
    private static final Codec<Map<Key, Rule>> RULES_CODEC = Codec.unboundedMap(Key.CODEC, Rule.CODEC);
    public static final Codec<ShapeMapFile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("priority", DEFAULT_PRIORITY).forGetter(ShapeMapFile::priority),
            RULES_CODEC.optionalFieldOf("add", Map.of()).forGetter(ShapeMapFile::add),
            RULES_CODEC.optionalFieldOf("remove", Map.of()).forGetter(ShapeMapFile::remove)
    ).apply(instance, ShapeMapFile::new));

    @Nullable
    public static Selector parseSelector(String resolved, @Nullable Consumer<String> onError) {
        if (resolved.length() >= 2 && resolved.startsWith("/") && resolved.endsWith("/")) {
            try {
                return new Selector.Regex(Pattern.compile(resolved.substring(1, resolved.length() - 1)));
            } catch (PatternSyntaxException exception) {
                report(onError, "invalid regex '" + resolved + "': " + exception.getMessage());
                return null;
            }
        }
        if (resolved.startsWith("#")) {
            try {
                return new Selector.Tag(TagKey.create(Registries.ITEM, ClutterNoMore.parse(resolved.substring(1))));
            } catch (Exception exception) {
                report(onError, "invalid tag id '" + resolved + "': " + exception.getMessage());
                return null;
            }
        }
        try {
            return new Selector.Literal(ClutterNoMore.parse(resolved));
        } catch (Exception exception) {
            report(onError, "invalid identifier '" + resolved + "': " + exception.getMessage());
            return null;
        }
    }

    private static void report(@Nullable Consumer<String> onError, String message) {
        if (onError != null) {
            onError.accept(message);
        }
    }

    public static boolean matches(@Nullable Selector selector, Item item) {
        if (selector instanceof Selector.Regex regex) return regex.pattern().matcher(BuiltInRegistries.ITEM.getKey(item).toString()).matches();
        if (selector instanceof Selector.Tag tag) return BuiltInRegistries.ITEM.wrapAsHolder(item).is(tag.tagKey());
        if (selector instanceof Selector.Literal literal) return BuiltInRegistries.ITEM.getKey(item).equals(literal.id());
        return false;
    }

    public static List<Item> resolveItems(String resolved, Consumer<String> onError, @Nullable ScanIndex index) {
        Selector selector = parseSelector(resolved, onError);
        if (selector == null) return List.of();
        if (selector instanceof Selector.Literal literal) return BuiltInRegistries.ITEM.getOptional(literal.id()).map(List::of).orElse(List.of());
        if (index != null && selector instanceof Selector.Regex regex) {
            Pattern pattern = regex.pattern();
            String anchor = literalPathAnchor(pattern.pattern());
            List<Item> result = new ArrayList<>();
            if (anchor != null) {
                for (Item item : index.byPath().getOrDefault(anchor, List.of())) {
                    if (pattern.matcher(BuiltInRegistries.ITEM.getKey(item).toString()).matches()) {
                        result.add(item);
                    }
                }
                return result;
            }
            for (int i = 0; i < index.ids().length; i++) {
                if (pattern.matcher(index.ids()[i]).matches()) {
                    result.add(index.items().get(i));
                }
            }
            return result;
        }
        List<Item> items = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (matches(selector, item)) {
                items.add(item);
            }
        }
        return items;
    }

    public static boolean anyMatch(String resolved, @Nullable ScanIndex index) {
        Selector selector = parseSelector(resolved, null);
        if (selector == null) return false;
        if (selector instanceof Selector.Literal literal) return BuiltInRegistries.ITEM.containsKey(literal.id());
        if (index != null && selector instanceof Selector.Regex regex) {
            Pattern pattern = regex.pattern();
            String anchor = literalPathAnchor(pattern.pattern());
            if (anchor != null) {
                for (Item item : index.byPath().getOrDefault(anchor, List.of())) {
                    if (pattern.matcher(BuiltInRegistries.ITEM.getKey(item).toString()).matches()) return true;
                }
                return false;
            }
            for (String id : index.ids()) {
                if (pattern.matcher(id).matches()) return true;
            }
            return false;
        }
        for (Item item : BuiltInRegistries.ITEM) {
            if (matches(selector, item)) return true;
        }
        return false;
    }

    @Nullable
    private static String literalPathAnchor(String regex) {
        int colon = regex.lastIndexOf(':');
        if (colon < 0 || colon == regex.length() - 1) return null;
        for (int i = colon + 1; i < regex.length(); i++) {
            char c = regex.charAt(i);
            boolean literal = c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '_' || c == '-' || c == '/';
            if (!literal) return null;
        }
        return regex.substring(colon + 1);
    }

    public sealed interface Key permits Key.Literal, Key.Tag, Key.Regex {

        Codec<Key> CODEC = Codec.STRING.flatXmap(Key::parse, key -> DataResult.success(key.getRaw()));

        static DataResult<Key> parse(String raw) {
            StringBuilder error = new StringBuilder();
            Selector selector = parseSelector(raw, error::append);
            if (selector instanceof Selector.Literal literal) return DataResult.success(new Literal(literal.id()));
            if (selector instanceof Selector.Tag tag) return DataResult.success(new Tag(tag.tagKey().location()));
            if (selector instanceof Selector.Regex regex) return DataResult.success(new Regex(regex.pattern()));
            return DataResult.error(() -> "Invalid shape map key '" + raw + "': " + error);
        }

        String getRaw();

        record Literal(ResourceLocation id) implements Key {

            @Override
            public String getRaw() {
                return this.id.toString();
            }
        }

        record Tag(ResourceLocation tagId) implements Key {

            @Override
            public String getRaw() {
                return "#" + this.tagId;
            }
        }

        record Regex(Pattern pattern) implements Key {

            @Override
            public String getRaw() {
                return "/" + this.pattern.pattern() + "/";
            }
        }
    }

    public record Template(String raw) {

        public static final Codec<Template> CODEC = Codec.STRING.flatXmap(Template::validate, template -> DataResult.success(template.raw()));

        private static DataResult<Template> validate(String raw) {
            int index = 0;
            while ((index = raw.indexOf("${", index)) != -1) {
                int end = raw.indexOf('}', index);
                if (end == -1) return DataResult.error(() -> "Unclosed ${ in shape map template '" + raw + "'");
                index = end + 1;
            }
            return DataResult.success(new Template(raw));
        }

        public String substitute(Map<String, String> vars) {
            if (!this.raw.contains("${")) return this.raw;
            StringBuilder result = new StringBuilder(this.raw.length());
            int index = 0;
            while (index < this.raw.length()) {
                int dollar = this.raw.indexOf("${", index);
                if (dollar < 0) {
                    result.append(this.raw, index, this.raw.length());
                    break;
                }
                result.append(this.raw, index, dollar);
                int end = this.raw.indexOf('}', dollar);
                result.append(vars.getOrDefault(this.raw.substring(dollar + 2, end), ""));
                index = end + 1;
            }
            return result.toString();
        }
    }

    public record Conditions(List<Template> exists, List<String> modLoaded, boolean isBlockItem, List<Conditions> allOf, List<Conditions> anyOf, List<Conditions> noneOf, Optional<Conditions> not) {

        private static final Codec<Boolean> IS_BLOCK_ITEM_CODEC = Codec.BOOL.flatXmap(value -> value ? DataResult.success(true) : DataResult.error(() -> "is_blockitem must be `true` (use `not` to invert)"), DataResult::success);
        public static final Codec<Conditions> CODEC = new Codec<>() {

            @Override
            public <T> DataResult<Pair<Conditions, T>> decode(DynamicOps<T> ops, T input) {
                return DELEGATE.decode(ops, input);
            }

            @Override
            public <T> DataResult<T> encode(Conditions input, DynamicOps<T> ops, T prefix) {
                return DELEGATE.encode(input, ops, prefix);
            }
        };
        private static final Codec<Conditions> DELEGATE = RecordCodecBuilder.create(instance -> instance.group(
                Template.CODEC.listOf().optionalFieldOf("exists", List.of()).forGetter(Conditions::exists),
                Codec.STRING.listOf().optionalFieldOf("mod_loaded", List.of()).forGetter(Conditions::modLoaded),
                IS_BLOCK_ITEM_CODEC.optionalFieldOf("is_blockitem", false).forGetter(Conditions::isBlockItem),
                CODEC.listOf().optionalFieldOf("all_of", List.of()).forGetter(Conditions::allOf),
                CODEC.listOf().optionalFieldOf("any_of", List.of()).forGetter(Conditions::anyOf),
                CODEC.listOf().optionalFieldOf("none_of", List.of()).forGetter(Conditions::noneOf),
                CODEC.optionalFieldOf("not").forGetter(Conditions::not)
        ).apply(instance, Conditions::new));

        public boolean evaluate(Item parent, Map<String, String> vars, ScanIndex index) {
            for (Template template : this.exists) {
                if (!anyMatch(template.substitute(vars), index)) return false;
            }
            for (String mod : this.modLoaded) {
                if (!Services.PLATFORM.isModLoaded(mod)) return false;
            }
            if (this.isBlockItem && !(parent instanceof BlockItem)) return false;
            for (Conditions condition : this.allOf) {
                if (!condition.evaluate(parent, vars, index)) return false;
            }
            if (!this.anyOf.isEmpty() && !this.anyMatches(parent, vars, index)) return false;
            for (Conditions condition : this.noneOf) {
                if (condition.evaluate(parent, vars, index)) return false;
            }
            return this.not.isEmpty() || !this.not.get().evaluate(parent, vars, index);
        }

        private boolean anyMatches(Item parent, Map<String, String> vars, ScanIndex index) {
            for (Conditions condition : this.anyOf) {
                if (condition.evaluate(parent, vars, index)) return true;
            }
            return false;
        }
    }

    public record Rule(List<Template> shapes, Optional<Conditions> conditions) {

        private static final Codec<Rule> OBJECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Template.CODEC.listOf().fieldOf("shapes").forGetter(Rule::shapes),
                Conditions.CODEC.optionalFieldOf("conditions").forGetter(Rule::conditions)
        ).apply(instance, Rule::new));
        public static final Codec<Rule> CODEC = Codec.either(Template.CODEC.listOf(), OBJECT_CODEC).xmap(either -> either.map(list -> new Rule(list, Optional.empty()), rule -> rule), rule -> rule.conditions().isEmpty() ? Either.left(rule.shapes()) : Either.right(rule));
    }

    public record ScanIndex(List<Item> items, ResourceLocation[] identifiers, String[] ids, Map<String, List<Item>> byPath) {

        @Nullable
        private static ScanIndex cached;

        public static ScanIndex get() {
            ScanIndex local = cached;
            if (local == null || local.items().size() != BuiltInRegistries.ITEM.size()) {
                local = build();
                cached = local;
            }
            return local;
        }

        private static ScanIndex build() {
            List<Item> items = new ArrayList<>();
            for (Item item : BuiltInRegistries.ITEM) {
                items.add(item);
            }
            ResourceLocation[] identifiers = new ResourceLocation[items.size()];
            String[] ids = new String[items.size()];
            Map<String, List<Item>> byPath = new HashMap<>();
            for (int i = 0; i < items.size(); i++) {
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(items.get(i));
                identifiers[i] = id;
                ids[i] = id.toString();
                byPath.computeIfAbsent(id.getPath(), key -> new ArrayList<>()).add(items.get(i));
            }
            return new ScanIndex(items, identifiers, ids, byPath);
        }
    }

    public sealed interface Selector permits Selector.Regex, Selector.Tag, Selector.Literal {

        record Regex(Pattern pattern) implements Selector {
        }

        record Tag(TagKey<Item> tagKey) implements Selector {
        }

        record Literal(ResourceLocation id) implements Selector {
        }
    }
}
