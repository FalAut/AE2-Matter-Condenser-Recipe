package com.falaut.ae2mcr.recipe;

import java.util.Arrays;
import java.util.List;

import com.falaut.ae2mcr.config.DefaultCatalystConfig;
import com.mojang.datafixers.util.Either;
import com.falaut.ae2mcr.registry.ModRecipeSerializers;
import com.falaut.ae2mcr.registry.ModRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import appeng.api.ids.AEComponents;

public class CondenserRecipe implements Recipe<RecipeInput> {
    private static final Codec<Integer> NON_NEGATIVE_INT = Codec.INT.flatXmap(
            value -> value <= 0
                    ? DataResult.error(() -> "value must be > 0")
                    : DataResult.success(value),
            DataResult::success);
    private static final MapCodec<CondenserCatalystEntry> CATALYST_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CondenserCatalystEntry::ingredient))
                    .apply(instance, CondenserCatalystEntry::new));
    private static final Codec<Ingredient> CATALYST_ENTRY_CODEC = Codec.either(
            Ingredient.CODEC_NONEMPTY,
            CATALYST_CODEC.codec())
            .xmap(
                    either -> either.map(ingredient -> ingredient, CondenserCatalystEntry::ingredient),
                    Either::left);

    private final ItemStack result;
    private final int requiredPower;
    private final List<Ingredient> catalysts;

    private static final Codec<List<Ingredient>> CATALYST_LIST_CODEC = CATALYST_ENTRY_CODEC.listOf();

    public static final MapCodec<CondenserRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ItemStack.CODEC.fieldOf("result").forGetter(CondenserRecipe::getResultTemplate),
            NON_NEGATIVE_INT.fieldOf("required_power").forGetter(CondenserRecipe::getRequiredPower),
            CATALYST_LIST_CODEC.optionalFieldOf("catalyst", List.of()).forGetter(CondenserRecipe::catalysts))
            .apply(builder, CondenserRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CondenserRecipe> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            CondenserRecipe::getResultTemplate,
            net.minecraft.network.codec.ByteBufCodecs.VAR_INT,
            CondenserRecipe::getRequiredPower,
            ByteBufCodecs.collection(java.util.ArrayList::new, Ingredient.CONTENTS_STREAM_CODEC),
            CondenserRecipe::catalysts,
            CondenserRecipe::new);

    public CondenserRecipe(
            ItemStack result,
            int requiredPower,
            List<Ingredient> catalysts) {
        this.result = result.copy();
        this.requiredPower = requiredPower;
        this.catalysts = catalysts == null ? List.of() : List.copyOf(catalysts);
        validateCatalysts();
    }

    public ItemStack getResultTemplate() {
        return result.copy();
    }

    public int getRequiredPower() {
        return requiredPower;
    }

    public List<Ingredient> catalysts() {
        return this.catalysts;
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
        var copy = result.copy();
        if ("data_energistics:data_capture_ball".equals(BuiltInRegistries.ITEM.getKey(copy.getItem()).toString())
                && !copy.has(AEComponents.STORED_ENERGY)) {
            copy.set(AEComponents.STORED_ENERGY, 5_000.0D);
        }
        return copy;
    }

    public boolean acceptsCatalyst(ItemStack stack) {
        if (this.catalysts.isEmpty()) {
            return DefaultCatalystConfig.storageFor(stack) >= this.requiredPower;
        }
        return this.catalysts.stream().anyMatch(entry -> !stack.isEmpty() && entry.test(stack));
    }

    public boolean restrictsStorage(ItemStack stack) {
        return !acceptsCatalyst(stack);
    }

    public int getCatalystStorage(ItemStack stack) {
        return acceptsCatalyst(stack) ? this.requiredPower : 0;
    }

    public List<ItemStack> getCatalystPreviewStacks() {
        if (this.catalysts.isEmpty()) {
            return DefaultCatalystConfig.previewStacks(this.requiredPower);
        }
        return this.catalysts.stream()
                .flatMap(entry -> Arrays.stream(entry.getItems()))
                .map(ItemStack::copy)
                .filter(stack -> !stack.isEmpty())
                .toList();
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
        var all = CondenserRecipeSelectionService.listRecipeHolders(level);
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
        return CondenserRecipeSelectionService.findRecipe(level, id);
    }

    public static CondenserRecipe findById(RecipeManager recipeManager, ResourceLocation id) {
        return CondenserRecipeSelectionService.findRecipe(recipeManager, id);
    }

    public static List<ResourceLocation> listIds(Level level) {
        return level == null ? List.of() : listIds(level.getRecipeManager());
    }

    public static List<ResourceLocation> listIds(RecipeManager recipeManager) {
        return CondenserRecipeSelectionService.listRecipeHolders(recipeManager)
                .stream()
                .map(holder -> holder.id())
                .toList();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CONDENSER_RECIPE.get();
    }

    private record CondenserCatalystEntry(Ingredient ingredient) {
    }

    private void validateCatalysts() {
        for (var catalyst : this.catalysts) {
            validateCatalyst(catalyst);
        }
    }

    private void validateCatalyst(Ingredient catalyst) {
        var items = catalyst.getItems();
        if (items.length == 0) {
            throw new IllegalArgumentException(
                    "Condenser catalyst must resolve to at least one item.");
        }
    }
}
