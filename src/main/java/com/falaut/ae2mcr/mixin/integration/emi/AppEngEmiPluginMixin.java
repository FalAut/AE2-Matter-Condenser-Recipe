package com.falaut.ae2mcr.mixin.integration.emi;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Unique;

import com.falaut.ae2mcr.integration.CondenserEmiRecipe;
import com.falaut.ae2mcr.integration.CondenserViewerRecipes;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import net.minecraft.resources.ResourceLocation;

@Pseudo
@Mixin(targets = "appeng.integration.modules.emi.AppEngEmiPlugin", remap = false)
public abstract class AppEngEmiPluginMixin {
    private static final ResourceLocation AE2_CONDENSER_CATEGORY = ResourceLocation
            .fromNamespaceAndPath("ae2", "condenser");
    @Unique
    private EmiRecipeCategory ae2mcr$condenserCategory;

    @Inject(method = "register", at = @At("HEAD"), remap = false)
    private void ae2mcr$resetCondenserCategory(EmiRegistry registry, CallbackInfo ci) {
        this.ae2mcr$condenserCategory = null;
    }

    @Redirect(
            method = "register",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/emi/emi/api/EmiRegistry;addRecipe(Ldev/emi/emi/api/recipe/EmiRecipe;)V"),
            remap = false)
    private void ae2mcr$replaceVanillaCondenserRecipes(EmiRegistry registry, EmiRecipe recipe) {
        EmiRecipeCategory category = recipe.getCategory();
        if (category != null
                && AE2_CONDENSER_CATEGORY.equals(category.getId())
                && "appeng.integration.modules.emi.EmiCondenserRecipe".equals(recipe.getClass().getName())) {
            this.ae2mcr$condenserCategory = category;
            return;
        }
        registry.addRecipe(recipe);
    }

    @Inject(method = "register", at = @At("TAIL"), remap = false)
    private void ae2mcr$addCustomCondenserRecipes(EmiRegistry registry, CallbackInfo ci) {
        if (this.ae2mcr$condenserCategory == null) {
            return;
        }
        for (var recipe : CondenserViewerRecipes.listWithoutTrash(registry.getRecipeManager())) {
            registry.addRecipe(new CondenserEmiRecipe(this.ae2mcr$condenserCategory, recipe));
        }
    }
}
