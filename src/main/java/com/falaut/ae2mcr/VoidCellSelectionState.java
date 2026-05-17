package com.falaut.ae2mcr;

import java.util.ArrayList;
import java.util.List;

import com.falaut.ae2mcr.recipe.CondenserRecipe;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeManager;

public final class VoidCellSelectionState {
    private static final String TAG_SELECTED_RECIPE = "ae2mcrVoidCellSelectedRecipe";
    private static final String TAG_REQUIRED_POWER = "ae2mcrVoidCellRequiredPower";
    private static final String TAG_OUTPUT_ITEM = "ae2mcrVoidCellOutputItem";
    private static final String TAG_OUTPUT_COUNT = "ae2mcrVoidCellOutputCount";

    private VoidCellSelectionState() {
    }

    public static ResourceLocation readSelectedRecipeId(ItemStack stack) {
        var customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        var tag = customData.copyTag();
        if (!tag.contains(TAG_SELECTED_RECIPE)) {
            return CondenserSelectionState.TRASH_ID;
        }

        var id = ResourceLocation.tryParse(tag.getString(TAG_SELECTED_RECIPE));
        return id == null ? CondenserSelectionState.TRASH_ID : id;
    }

    public static void writeSelectedRecipeId(ItemStack stack, ResourceLocation id) {
        ResourceLocation safeId = id == null ? CondenserSelectionState.TRASH_ID : id;
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putString(TAG_SELECTED_RECIPE, safeId.toString());
        });
    }

    public static void writeResolvedSelection(ItemStack stack, RecipeManager recipeManager, ResourceLocation id) {
        ResourceLocation normalized = normalizeSelected(recipeManager, id);
        CondenserRecipe recipe = CondenserSelectionState.isTrash(normalized)
                ? null
                : CondenserRecipe.findById(recipeManager, normalized);
        writeResolvedSelection(stack, normalized, recipe);
    }

    public static void writeResolvedSelection(ItemStack stack, ResourceLocation selectedId, CondenserRecipe recipe) {
        ResourceLocation safeId = selectedId == null ? CondenserSelectionState.TRASH_ID : selectedId;
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putString(TAG_SELECTED_RECIPE, safeId.toString());
            if (recipe == null || recipe.getRequiredPower() <= 0 || recipe.getOutputCopy().isEmpty()) {
                tag.putInt(TAG_REQUIRED_POWER, 0);
                tag.remove(TAG_OUTPUT_ITEM);
                tag.remove(TAG_OUTPUT_COUNT);
                return;
            }

            ItemStack output = recipe.getOutputCopy();
            var itemId = BuiltInRegistries.ITEM.getKey(output.getItem());
            if (itemId == null) {
                tag.putInt(TAG_REQUIRED_POWER, 0);
                tag.remove(TAG_OUTPUT_ITEM);
                tag.remove(TAG_OUTPUT_COUNT);
                return;
            }

            tag.putInt(TAG_REQUIRED_POWER, recipe.getRequiredPower());
            tag.putString(TAG_OUTPUT_ITEM, itemId.toString());
            tag.putInt(TAG_OUTPUT_COUNT, Math.max(1, output.getCount()));
        });
    }

    public static int readRequiredPower(ItemStack stack) {
        var customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        var tag = customData.copyTag();
        return Math.max(0, tag.getInt(TAG_REQUIRED_POWER));
    }

    public static ItemStack readOutputStack(ItemStack stack) {
        var customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        var tag = customData.copyTag();
        if (!tag.contains(TAG_OUTPUT_ITEM)) {
            return ItemStack.EMPTY;
        }

        var itemId = ResourceLocation.tryParse(tag.getString(TAG_OUTPUT_ITEM));
        if (itemId == null) {
            return ItemStack.EMPTY;
        }
        var item = BuiltInRegistries.ITEM.get(itemId);
        if (item == null) {
            return ItemStack.EMPTY;
        }

        int count = Math.max(1, tag.getInt(TAG_OUTPUT_COUNT));
        return new ItemStack(item, count);
    }

    public static List<ResourceLocation> listSelectableIds(RecipeManager recipeManager) {
        var ids = new ArrayList<ResourceLocation>();
        ids.add(CondenserSelectionState.TRASH_ID);
        ids.addAll(CondenserRecipe.listIds(recipeManager));
        return ids;
    }

    public static ResourceLocation normalizeSelected(RecipeManager recipeManager, ResourceLocation id) {
        if (CondenserSelectionState.isTrash(id)) {
            return CondenserSelectionState.TRASH_ID;
        }
        return CondenserRecipe.findById(recipeManager, id) == null ? CondenserSelectionState.TRASH_ID : id;
    }
}
