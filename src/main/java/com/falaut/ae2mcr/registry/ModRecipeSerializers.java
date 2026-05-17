package com.falaut.ae2mcr.registry;

import com.falaut.ae2mcr.AE2MatterCondenserRecipe;
import com.falaut.ae2mcr.recipe.CondenserRecipe;
import com.falaut.ae2mcr.recipe.CondenserRecipeSerializer;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipeSerializers {
    private ModRecipeSerializers() {
    }

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
            .create(Registries.RECIPE_SERIALIZER, AE2MatterCondenserRecipe.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CondenserRecipe>> CONDENSER_RECIPE = RECIPE_SERIALIZERS
            .register("condenser", CondenserRecipeSerializer::new);
}
