package com.falaut.ae2mcr.api;

import java.util.List;

import com.falaut.ae2mcr.recipe.CondenserRecipeSelectionService;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface VoidCellMenuBridge {
    void ae2mcr$selectRecipe(ResourceLocation id);

    void ae2mcr$cycleRecipe(boolean backwards);

    ResourceLocation ae2mcr$getSelectedRecipeId();

    Level ae2mcr$getMenuLevel();

    default List<ResourceLocation> ae2mcr$getAvailableRecipeIds() {
        return CondenserRecipeSelectionService.listSelectableIds(ae2mcr$getMenuLevel());
    }

    default ItemStack ae2mcr$getPreview(ResourceLocation id) {
        return CondenserRecipeSelectionService.preview(ae2mcr$getMenuLevel(), id);
    }

    default int ae2mcr$getRequiredPower(ResourceLocation id) {
        return CondenserRecipeSelectionService.requiredPower(ae2mcr$getMenuLevel(), id);
    }
}
