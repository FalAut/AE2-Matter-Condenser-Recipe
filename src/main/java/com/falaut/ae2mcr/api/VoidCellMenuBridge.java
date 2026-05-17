package com.falaut.ae2mcr.api;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface VoidCellMenuBridge {
    void ae2mcr$selectRecipe(ResourceLocation id);

    void ae2mcr$cycleRecipe(boolean backwards);

    ResourceLocation ae2mcr$getSelectedRecipeId();

    List<ResourceLocation> ae2mcr$getAvailableRecipeIds();

    ItemStack ae2mcr$getPreview(ResourceLocation id);

    int ae2mcr$getRequiredPower(ResourceLocation id);

    Level ae2mcr$getMenuLevel();
}

