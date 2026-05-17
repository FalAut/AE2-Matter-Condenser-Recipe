package com.falaut.ae2mcr.registry;

import com.falaut.ae2mcr.AE2MatterCondenserRecipe;
import com.falaut.ae2mcr.recipe.CondenserRecipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipeTypes {
    private ModRecipeTypes() {
    }

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister
            .create(Registries.RECIPE_TYPE, AE2MatterCondenserRecipe.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CondenserRecipe>> CONDENSER_RECIPE = register(
            "condenser");

    private static <T extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<T>> register(String path) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AE2MatterCondenserRecipe.MOD_ID, path);
        RecipeType<T> type = RecipeType.simple(id);
        return RECIPE_TYPES.register(path, () -> type);
    }
}
