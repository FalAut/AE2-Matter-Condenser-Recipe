package com.falaut.ae2mcr.integration;

import java.util.List;

import net.minecraft.world.item.ItemStack;

import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;

import appeng.api.config.CondenserOutput;

public class CondenserReiDisplay extends appeng.integration.modules.rei.CondenserOutputDisplay {
    private final CondenserViewerRecipe recipe;
    private final List<EntryStack<ItemStack>> viableStorageComponents;

    public CondenserReiDisplay(CondenserViewerRecipe recipe) {
        super(CondenserOutput.TRASH);
        this.recipe = recipe;
        this.viableStorageComponents = CondenserViewerRecipes.viableStorageComponents(recipe.requiredPower())
                .stream()
                .map(EntryStacks::of)
                .toList();
    }

    public CondenserViewerRecipe getRecipe() {
        return recipe;
    }

    @Override
    public List<EntryStack<ItemStack>> getViableStorageComponents() {
        return viableStorageComponents;
    }

    @Override
    public List<me.shedaniel.rei.api.common.entry.EntryIngredient> getOutputEntries() {
        if (recipe.output().isEmpty()) {
            return List.of();
        }
        return List.of(me.shedaniel.rei.api.common.util.EntryIngredients.of(recipe.output()));
    }
}
