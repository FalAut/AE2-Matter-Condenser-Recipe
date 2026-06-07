package com.falaut.ae2mcr.recipe;

import java.util.Arrays;
import java.util.List;

import com.falaut.ae2mcr.config.DefaultCatalystConfig;
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
    private static final int DEFAULT_STORAGE = -1;
    private static final Codec<Integer> NON_NEGATIVE_INT = Codec.INT.flatXmap(
            value -> value < 0
                    ? DataResult.error(() -> "value must be >= 0")
                    : DataResult.success(value),
            DataResult::success);
    private static final Codec<Integer> STORAGE_CODEC = Codec.INT.flatXmap(
            value -> value < DEFAULT_STORAGE
                    ? DataResult.error(() -> "storage must be >= -1")
                    : DataResult.success(value),
            DataResult::success);

    private final ItemStack result;
    private final int requiredPower;
    private final List<CondenserCatalystEntry> catalysts;

    private static final Codec<List<CondenserCatalystEntry>> CATALYST_LIST_CODEC = CondenserCatalystEntry.CODEC.codec()
            .listOf()
            .validate(entries -> entries.isEmpty()
                    ? DataResult.error(() -> "catalyst list must not be empty")
                    : DataResult.success(entries));

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
            ByteBufCodecs.collection(java.util.ArrayList::new, CondenserCatalystEntry.STREAM_CODEC),
            CondenserRecipe::catalysts,
            CondenserRecipe::new);

    public CondenserRecipe(
            ItemStack result,
            int requiredPower,
            List<CondenserCatalystEntry> catalysts) {
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

    public List<CondenserCatalystEntry> catalysts() {
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
        return this.catalysts.stream().anyMatch(entry -> entry.matchesItem(stack));
    }

    public boolean restrictsStorage(ItemStack stack) {
        return !acceptsCatalyst(stack);
    }

    public int getCatalystStorage(ItemStack stack) {
        if (!acceptsCatalyst(stack)) {
            return 0;
        }

        if (this.catalysts.isEmpty()) {
            return DefaultCatalystConfig.storageFor(stack);
        }

        for (var entry : this.catalysts) {
            if (entry.matchesItem(stack)) {
                return entry.storageValue(stack, this.requiredPower);
            }
        }

        return 0;
    }

    public List<ItemStack> getCatalystPreviewStacks() {
        if (this.catalysts.isEmpty()) {
            return DefaultCatalystConfig.previewStacks(this.requiredPower);
        }
        return this.catalysts.stream()
                .flatMap(entry -> entry.previewStacks(this.requiredPower).stream())
                .map(ItemStack::copy)
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

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CONDENSER_RECIPE.get();
    }

    public record CondenserCatalystEntry(Ingredient ingredient, int storage) {
        public static final MapCodec<CondenserCatalystEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CondenserCatalystEntry::ingredient),
                STORAGE_CODEC.optionalFieldOf("storage", DEFAULT_STORAGE).forGetter(CondenserCatalystEntry::storage))
                .apply(instance, CondenserCatalystEntry::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CondenserCatalystEntry> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC,
                CondenserCatalystEntry::ingredient,
                ByteBufCodecs.INT,
                CondenserCatalystEntry::storage,
                CondenserCatalystEntry::new);

        public CondenserCatalystEntry {
            ingredient = ingredient == null ? Ingredient.EMPTY : ingredient;
            storage = storage < DEFAULT_STORAGE ? DEFAULT_STORAGE : storage;
        }

        public boolean matchesItem(ItemStack stack) {
            return !stack.isEmpty() && this.ingredient.test(stack);
        }

        public int storageValue(ItemStack stack, int requiredPower) {
            return this.storage == DEFAULT_STORAGE ? DefaultCatalystConfig.storageFor(stack) : this.storage;
        }

        public List<ItemStack> previewStacks(int requiredPower) {
            return Arrays.stream(this.ingredient.getItems())
                    .map(ItemStack::copy)
                    .filter(this::matchesItem)
                    .toList();
        }
    }

    private void validateCatalysts() {
        for (var catalyst : this.catalysts) {
            validateCatalyst(catalyst);
        }
    }

    private void validateCatalyst(CondenserCatalystEntry catalyst) {
        if (catalyst.storage() != DEFAULT_STORAGE) {
            if (catalyst.storage() < this.requiredPower) {
                throw new IllegalArgumentException(
                        "Condenser catalyst storage must be >= required_power: storage=" + catalyst.storage()
                                + ", required_power=" + this.requiredPower);
            }
            return;
        }

        var items = catalyst.ingredient().getItems();
        if (items.length == 0) {
            throw new IllegalArgumentException(
                    "Condenser catalyst without storage must resolve to at least one item.");
        }

        for (var stack : items) {
            int defaultStorage = DefaultCatalystConfig.storageFor(stack);
            String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            if (defaultStorage <= 0) {
                throw new IllegalArgumentException(
                        "Item '" + itemId
                                + "' must define catalyst.storage explicitly because it has no default catalyst storage.");
            }
            if (defaultStorage < this.requiredPower) {
                throw new IllegalArgumentException(
                        "Default catalyst storage for item '" + itemId + "' must be >= required_power: storage="
                                + defaultStorage + ", required_power=" + this.requiredPower
                                + ". Set catalyst.storage explicitly or raise the default value.");
            }
        }
    }
}
