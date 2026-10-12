package dev.tazer.clutternomore.fabric.mixin;

import dev.tazer.clutternomore.platform.condition.IfDevEnvironment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
//? if >=26 {
/*import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
*///?} else if >=1.20.5 {
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
//?} else {
/*import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?}

@IfDevEnvironment
//? if >=26 {
/*@Mixin(RecipeUnlockAdvancementBuilder.class)
*///?} else {
@Mixin(ShapedRecipeBuilder.class)
//?}
public class ShapedRecipeBuilderMixin {

    //? if >=26 {
    /*@Shadow
    @Final
    private Map<String, Criterion<?>> criteria;

    @Inject(method = "build", at = @At(value = "INVOKE", target = "Ljava/util/Map;isEmpty()Z"), cancellable = true)
    private void cnm$skipVanillaAdvancements(RecipeOutput output, ResourceKey<Recipe<?>> id, RecipeCategory category, CallbackInfoReturnable<AdvancementHolder> cir) {
        if (this.criteria.isEmpty() && output.getRecipeIdentifier(id.identifier()).getNamespace().equals("minecraft")) {
            cir.setReturnValue(null);
        }
    }
    *///?} else if >=1.20.5 {
    @Shadow
    @Final
    private Map<String, Criterion<?>> criteria;

    @Shadow
    @Final
    private List<String> rows;

    @Shadow
    @Final
    private Map<Character, Ingredient> key;

    @Inject(method = "ensureValid", at = @At(value = "INVOKE", target = "Ljava/util/Map;isEmpty()Z"), cancellable = true)
    private void cnm$skipVanillaAdvancements(ResourceLocation id, CallbackInfoReturnable<ShapedRecipePattern> cir) {
        if (this.criteria.isEmpty()) {
            cir.setReturnValue(ShapedRecipePattern.of(this.key, this.rows));
        }
    }
    //?} else {
    /*@Shadow
    @Final
    private Advancement.Builder advancement;

    @Inject(method = "ensureValid", at = @At("HEAD"), cancellable = true)
    private void cnm$skipVanillaAdvancements(ResourceLocation id, CallbackInfo ci) {
        if (this.advancement.getCriteria().isEmpty()) {
            ci.cancel();
        }
    }
    *///?}
}
