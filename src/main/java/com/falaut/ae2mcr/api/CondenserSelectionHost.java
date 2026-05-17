package com.falaut.ae2mcr.api;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface CondenserSelectionHost {
    ResourceLocation ae2mcr$getSelectedCondenserRecipeId();

    void ae2mcr$setSelectedCondenserRecipeId(ResourceLocation id);

    List<ResourceLocation> ae2mcr$getAvailableCondenserRecipeIds();

    ItemStack ae2mcr$getCondenserRecipeOutputPreview(ResourceLocation id);

    int ae2mcr$getCondenserRequiredPower(ResourceLocation id);
}
