package com.falaut.ae2mcr.recipe;

import java.util.List;

import com.falaut.ae2mcr.registry.ModRecipeSerializers;
import com.falaut.ae2mcr.registry.ModRecipeTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class CondenserRecipe implements Recipe<RecipeInput> {
    private final ItemStack result;
    private final int requiredPower;

    public static final MapCodec<CondenserRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ItemStack.CODEC.fieldOf("result").forGetter(CondenserRecipe::getResultTemplate),
            com.mojang.serialization.Codec.INT.fieldOf("required_power").forGetter(CondenserRecipe::getRequiredPower))
            .apply(builder, CondenserRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CondenserRecipe> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            CondenserRecipe::getResultTemplate,
            net.minecraft.network.codec.ByteBufCodecs.VAR_INT,
            CondenserRecipe::getRequiredPower,
            CondenserRecipe::new);

    public CondenserRecipe(ItemStack result, int requiredPower) {
        this.result = result;
        this.requiredPower = requiredPower;
    }

    public ItemStack getResultTemplate() {
        return result;
    }

    public int getRequiredPower() {
        return requiredPower;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return result.copy();
    }

    public ItemStack getOutputCopy() {
        return result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CONDENSER_RECIPE.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CONDENSER_RECIPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static CondenserRecipe pickByIdOrFirst(Level level, ResourceLocation id) {
        var all = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.CONDENSER_RECIPE.get());
        if (all.isEmpty()) {
            return null;
        }

        if (id != null) {
            for (var holder : all) {
                if (holder.id().equals(id)) {
                    return holder.value();
                }
            }
        }

        return all.getFirst().value();
    }

    public static CondenserRecipe findById(Level level, ResourceLocation id) {
        return level == null ? null : findById(level.getRecipeManager(), id);
    }

    public static CondenserRecipe findById(RecipeManager recipeManager, ResourceLocation id) {
        if (id == null) {
            return null;
        }
        for (var holder : recipeManager.getAllRecipesFor(ModRecipeTypes.CONDENSER_RECIPE.get())) {
            if (holder.id().equals(id)) {
                return holder.value();
            }
        }
        return null;
    }

    public static List<ResourceLocation> listIds(Level level) {
        return level == null ? List.of() : listIds(level.getRecipeManager());
    }

    public static List<ResourceLocation> listIds(RecipeManager recipeManager) {
        return recipeManager.getAllRecipesFor(ModRecipeTypes.CONDENSER_RECIPE.get())
                .stream()
                .map(holder -> holder.id())
                .toList();
    }
}
