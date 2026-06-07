package com.falaut.ae2mcr.api;

import java.util.List;

import com.falaut.ae2mcr.recipe.CondenserRecipeSelectionService;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface CondenserSelectionHost {
    ResourceLocation ae2mcr$getSelectedCondenserRecipeId();

    void ae2mcr$setSelectedCondenserRecipeId(ResourceLocation id);

    Level ae2mcr$getCondenserLevel();

    default List<ResourceLocation> ae2mcr$getAvailableCondenserRecipeIds() {
        return CondenserRecipeSelectionService.listSelectableIds(ae2mcr$getCondenserLevel());
    }

    default ItemStack ae2mcr$getCondenserRecipeOutputPreview(ResourceLocation id) {
        return CondenserRecipeSelectionService.preview(ae2mcr$getCondenserLevel(), id);
    }

    default int ae2mcr$getCondenserRequiredPower(ResourceLocation id) {
        return CondenserRecipeSelectionService.requiredPower(ae2mcr$getCondenserLevel(), id);
    }
}
