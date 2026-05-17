package com.falaut.ae2mcr.mixin.integration.jei;

import java.util.List;
import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.falaut.ae2mcr.integration.CondenserViewerRecipe;
import com.falaut.ae2mcr.integration.CondenserViewerRecipes;

import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;

@Pseudo
@Mixin(targets = "tamaized.ae2jeiintegration.integration.modules.jei.JEIPlugin", remap = false)
public abstract class JeiPluginMixin {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Redirect(
            method = "registerRecipes",
            at = @At(
                    value = "INVOKE",
                    target = "Lmezz/jei/api/registration/IRecipeRegistration;addRecipes(Lmezz/jei/api/recipe/RecipeType;Ljava/util/List;)V"),
            require = 0)
    private void ae2mcr$replaceCondenserRecipeList(
            IRecipeRegistration registration,
            RecipeType recipeType,
            List recipes) {
        if ("ae2:condenser".equals(recipeType.getUid().toString())) {
            var level = Objects.requireNonNull(Minecraft.getInstance().level);
            List<CondenserViewerRecipe> viewerRecipes = CondenserViewerRecipes.listWithoutTrash(level);
            registration.addRecipes((RecipeType) recipeType, (List) viewerRecipes);
            return;
        }
        registration.addRecipes(recipeType, recipes);
    }
}
