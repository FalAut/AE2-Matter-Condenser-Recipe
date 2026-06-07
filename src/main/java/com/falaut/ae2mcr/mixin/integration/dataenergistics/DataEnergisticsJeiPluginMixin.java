package com.falaut.ae2mcr.mixin.integration.dataenergistics;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

@Pseudo
@Mixin(targets = "com.fish_dan_.data_energistics.client.jei.DataEnergisticsJeiPlugin", remap = false)
public abstract class DataEnergisticsJeiPluginMixin {
    private static final String DATAE_CONDENSER_RECIPE_TYPE = "data_energistics:condenser_data_capture_ball";

    @Redirect(
            method = "registerCategories",
            at = @At(
                    value = "INVOKE",
                    target = "Lmezz/jei/api/registration/IRecipeCategoryRegistration;addRecipeCategories([Lmezz/jei/api/recipe/category/IRecipeCategory;)V"),
            require = 0)
    private void ae2mcr$skipCondenserCategory(
            IRecipeCategoryRegistration registration,
            IRecipeCategory<?>[] categories) {
        registration.addRecipeCategories(Arrays.stream(categories)
                .filter(category -> !category.getClass().getName()
                        .equals("com.fish_dan_.data_energistics.client.jei.DataCaptureBallCondenserCategory"))
                .toArray(IRecipeCategory[]::new));
    }

    @Redirect(
            method = "registerRecipeCatalysts",
            at = @At(
                    value = "INVOKE",
                    target = "Lmezz/jei/api/registration/IRecipeCatalystRegistration;addRecipeCatalyst(Lnet/minecraft/world/item/ItemStack;Lmezz/jei/api/recipe/RecipeType;)V"),
            require = 0)
    private void ae2mcr$skipCondenserCatalyst(
            IRecipeCatalystRegistration registration,
            net.minecraft.world.item.ItemStack stack,
            RecipeType<?> recipeType) {
        if (!DATAE_CONDENSER_RECIPE_TYPE.equals(recipeType.getUid().toString())) {
            registration.addRecipeCatalyst(stack, recipeType);
        }
    }

    @Redirect(
            method = "registerRecipes",
            at = @At(
                    value = "INVOKE",
                    target = "Lmezz/jei/api/registration/IRecipeRegistration;addRecipes(Lmezz/jei/api/recipe/RecipeType;Ljava/util/List;)V"),
            require = 0)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void ae2mcr$skipCondenserRecipes(
            IRecipeRegistration registration,
            RecipeType<?> recipeType,
            List<?> recipes) {
        if (!DATAE_CONDENSER_RECIPE_TYPE.equals(recipeType.getUid().toString())) {
            registration.addRecipes((RecipeType) recipeType, (List) recipes);
        }
    }
}
