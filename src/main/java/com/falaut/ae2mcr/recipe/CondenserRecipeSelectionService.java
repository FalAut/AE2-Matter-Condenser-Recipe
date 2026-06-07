package com.falaut.ae2mcr.recipe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.falaut.ae2mcr.AE2MatterCondenserRecipe;
import com.falaut.ae2mcr.registry.ModRecipeTypes;

import appeng.api.config.CondenserOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

public final class CondenserRecipeSelectionService {
    public static final ResourceLocation TRASH_ID = ResourceLocation
            .fromNamespaceAndPath(AE2MatterCondenserRecipe.MOD_ID, "trash");

    private CondenserRecipeSelectionService() {
    }

    public static List<ResourceLocation> listSelectableIds(Level level) {
        return level == null ? List.of(TRASH_ID) : listSelectableIds(level.getRecipeManager());
    }

    public static List<ResourceLocation> listSelectableIds(RecipeManager recipeManager) {
        var ids = new ArrayList<ResourceLocation>();
        ids.add(TRASH_ID);
        ids.addAll(indexRecipes(recipeManager).keySet());
        return List.copyOf(ids);
    }

    public static boolean isTrash(ResourceLocation id) {
        return id == null || TRASH_ID.equals(id);
    }

    public static ResourceLocation normalizeSelected(Level level, ResourceLocation id) {
        return level == null ? TRASH_ID : normalizeSelected(level.getRecipeManager(), id);
    }

    public static ResourceLocation normalizeSelected(RecipeManager recipeManager, ResourceLocation id) {
        return normalizeSelected(listSelectableIds(recipeManager), id);
    }

    public static CondenserRecipe findRecipe(Level level, ResourceLocation id) {
        return level == null ? null : findRecipe(level.getRecipeManager(), id);
    }

    public static CondenserRecipe findRecipe(RecipeManager recipeManager, ResourceLocation id) {
        if (recipeManager == null || isTrash(id)) {
            return null;
        }
        return indexRecipes(recipeManager).get(id);
    }

    public static List<RecipeHolder<CondenserRecipe>> listRecipeHolders(Level level) {
        return level == null ? List.of() : listRecipeHolders(level.getRecipeManager());
    }

    public static List<RecipeHolder<CondenserRecipe>> listRecipeHolders(RecipeManager recipeManager) {
        return List.copyOf(recipeManager.getAllRecipesFor(ModRecipeTypes.CONDENSER_RECIPE.get()));
    }

    public static ItemStack preview(Level level, ResourceLocation id) {
        return level == null ? ItemStack.EMPTY : preview(level.getRecipeManager(), id);
    }

    public static ItemStack preview(RecipeManager recipeManager, ResourceLocation id) {
        if (isTrash(id)) {
            return ItemStack.EMPTY;
        }
        var recipe = findRecipe(recipeManager, id);
        return recipe == null ? ItemStack.EMPTY : recipe.getOutputCopy();
    }

    public static int requiredPower(Level level, ResourceLocation id) {
        return level == null ? requiredPower((RecipeManager) null, id) : requiredPower(level.getRecipeManager(), id);
    }

    public static int requiredPower(RecipeManager recipeManager, ResourceLocation id) {
        if (isTrash(id)) {
            return CondenserOutput.TRASH.requiredPower;
        }
        if (recipeManager == null) {
            return Integer.MAX_VALUE;
        }
        var recipe = findRecipe(recipeManager, id);
        return recipe == null ? Integer.MAX_VALUE : recipe.getRequiredPower();
    }

    public static ResourceLocation cycleSelection(Level level, ResourceLocation current, boolean backwards) {
        return level == null ? TRASH_ID : cycleSelection(level.getRecipeManager(), current, backwards);
    }

    public static ResourceLocation cycleSelection(RecipeManager recipeManager, ResourceLocation current, boolean backwards) {
        return cycleSelection(listSelectableIds(recipeManager), current, backwards);
    }

    private static Map<ResourceLocation, CondenserRecipe> indexRecipes(RecipeManager recipeManager) {
        var indexed = new LinkedHashMap<ResourceLocation, CondenserRecipe>();
        for (var holder : recipeManager.getAllRecipesFor(ModRecipeTypes.CONDENSER_RECIPE.get())) {
            indexed.putIfAbsent(holder.id(), holder.value());
        }
        return indexed;
    }

    static ResourceLocation normalizeSelected(List<ResourceLocation> availableIds, ResourceLocation id) {
        if (isTrash(id)) {
            return TRASH_ID;
        }
        return availableIds.contains(id) ? id : TRASH_ID;
    }

    static ResourceLocation cycleSelection(List<ResourceLocation> availableIds, ResourceLocation current, boolean backwards) {
        if (availableIds.isEmpty()) {
            return TRASH_ID;
        }

        ResourceLocation normalized = normalizeSelected(availableIds, current);
        int index = availableIds.indexOf(normalized);
        if (index < 0) {
            index = 0;
        }

        int nextIndex = backwards ? (index - 1 + availableIds.size()) % availableIds.size()
                : (index + 1) % availableIds.size();
        return availableIds.get(nextIndex);
    }
}
