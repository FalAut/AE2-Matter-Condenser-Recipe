package com.falaut.ae2mcr.integration;

import java.util.List;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

import appeng.core.AppEng;

public class CondenserEmiRecipe extends BasicEmiRecipe {
    private final CondenserViewerRecipe recipe;
    private final EmiIngredient viableStorageComponents;

    public CondenserEmiRecipe(EmiRecipeCategory category, CondenserViewerRecipe recipe) {
        super(category, recipe.id(), 96, 48);
        this.recipe = recipe;
        this.viableStorageComponents = EmiIngredient.of(
                CondenserViewerRecipes.viableStorageComponents(recipe.requiredPower())
                        .stream()
                        .map(EmiStack::of)
                        .toList());
        this.catalysts.add(this.viableStorageComponents);
        if (!recipe.output().isEmpty()) {
            this.outputs.add(EmiStack.of(recipe.output()));
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        var background = AppEng.makeId("textures/guis/condenser.png");
        widgets.addTexture(background, 0, 0, 96, 48, 48, 25);

        var states = AppEng.makeId("textures/guis/states.png");
        widgets.addTexture(states, 4, 28, 14, 14, 241, 81);
        widgets.addTexture(states, 80, 28, 16, 16, 240, 240);
        widgets.addAnimatedTexture(background, 72, 0, 6, 18, 176, 0, 2000, false, true, false);

        if (!recipe.output().isEmpty()) {
            var modeSlot = widgets.addSlot(EmiStack.of(recipe.output()), 80, 28).drawBack(false);
            for (var line : CondenserViewerRecipes.tooltip(recipe)) {
                modeSlot.appendTooltip(line);
            }
        }

        widgets.addSlot(this.viableStorageComponents, 52, 0).drawBack(false);
        if (!recipe.output().isEmpty()) {
            widgets.addSlot(EmiStack.of(recipe.output()), 56, 26).drawBack(false);
        }
    }
}
