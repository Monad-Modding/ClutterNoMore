package dev.tazer.clutternomore.platform.condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public abstract class ConditionalMixinPlugin implements IMixinConfigPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger("ClutterNoMore Mixin Plugin");
    private static final String MOD_PRESENT = Type.getDescriptor(IfModPresent.class);
    private static final String MOD_PRESENT_LIST = Type.getDescriptor(IfModPresent.List.class);
    private static final String DEV_ENVIRONMENT = Type.getDescriptor(IfDevEnvironment.class);

    private Set<String> modIds = Set.of();

    @Nullable
    private String mixinPackage;

    protected abstract Set<String> findModIds();

    protected abstract boolean isDevelopmentEnvironment();

    @Override
    public void onLoad(String mixinPackage) {
        this.mixinPackage = mixinPackage;
        this.modIds = this.findModIds();
    }

    @Override
    @Nullable
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (this.mixinPackage != null && !mixinClassName.startsWith(this.mixinPackage)) return true;
        try {
            ClassNode mixin = MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName);
            return this.evaluate(mixin, mixinClassName);
        } catch (ClassNotFoundException | IOException exception) {
            LOGGER.warn("Could not load mixin class for condition evaluation: {}", mixinClassName);
            return true;
        }
    }

    private boolean evaluate(ClassNode mixin, String mixinClassName) {
        if (mixin.visibleAnnotations == null) return true;
        for (AnnotationNode annotation : mixin.visibleAnnotations) {
            if (annotation.desc.equals(DEV_ENVIRONMENT) && !this.isDevelopmentEnvironment()) {
                LOGGER.debug("Skipping dev-only mixin: {}", mixinClassName);
                return false;
            }
            if (annotation.desc.equals(MOD_PRESENT) && !this.isPresent(annotation, mixinClassName)) return false;
            if (annotation.desc.equals(MOD_PRESENT_LIST)) {
                for (AnnotationNode inner : value(annotation, List.<AnnotationNode>of())) {
                    if (!this.isPresent(inner, mixinClassName)) return false;
                }
            }
        }
        return true;
    }

    private boolean isPresent(AnnotationNode annotation, String mixinClassName) {
        String modId = value(annotation, "");
        if (modId.isEmpty()) {
            throw new IllegalArgumentException("modid must not be empty");
        }
        if (this.modIds.contains(modId)) return true;
        LOGGER.debug("Skipping mixin {}; mod '{}' is not present", mixinClassName, modId);
        return false;
    }

    @SuppressWarnings("unchecked")
    private static <T> T value(AnnotationNode annotation, T fallback) {
        if (annotation.values == null) return fallback;
        for (int i = 0; i < annotation.values.size(); i += 2) {
            if ("value".equals(annotation.values.get(i))) return (T) annotation.values.get(i + 1);
        }
        return fallback;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    @Nullable
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
