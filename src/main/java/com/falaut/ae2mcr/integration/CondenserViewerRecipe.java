package com.falaut.ae2mcr.integration;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record CondenserViewerRecipe(
        ResourceLocation id,
        ItemStack output,
        int requiredPower,
        List<ItemStack> catalysts) {
}
