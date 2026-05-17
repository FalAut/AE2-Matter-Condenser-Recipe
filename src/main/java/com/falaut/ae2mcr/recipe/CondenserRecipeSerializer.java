package com.falaut.ae2mcr.recipe;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CondenserRecipeSerializer implements RecipeSerializer<CondenserRecipe> {
    @Override
    public MapCodec<CondenserRecipe> codec() {
        return CondenserRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CondenserRecipe> streamCodec() {
        return CondenserRecipe.STREAM_CODEC;
    }
}
