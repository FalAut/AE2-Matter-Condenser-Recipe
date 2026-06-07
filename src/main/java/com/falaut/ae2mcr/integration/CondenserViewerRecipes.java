package com.falaut.ae2mcr.integration;

import java.util.ArrayList;
import java.util.List;

import com.falaut.ae2mcr.CondenserSelectionState;
import com.falaut.ae2mcr.config.DefaultCatalystConfig;
import com.falaut.ae2mcr.recipe.CondenserRecipe;
import com.falaut.ae2mcr.registry.ModRecipeTypes;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import appeng.api.config.CondenserOutput;

public final class CondenserViewerRecipes {
    private CondenserViewerRecipes() {
    }

    public static List<CondenserViewerRecipe> list(Level level) {
        var out = new ArrayList<CondenserViewerRecipe>();
        out.add(new CondenserViewerRecipe(
                CondenserSelectionState.TRASH_ID,
                ItemStack.EMPTY,
                CondenserOutput.TRASH.requiredPower,
                viableStorageComponents(CondenserOutput.TRASH.requiredPower)));

        if (level == null) {
            return out;
        }

        for (var holder : level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.CONDENSER_RECIPE.get())) {
            var recipe = holder.value();
            out.add(new CondenserViewerRecipe(
                    holder.id(),
                    recipe.getOutputCopy(),
                    recipe.getRequiredPower(),
                    recipe.getCatalystPreviewStacks()));
        }
        return out;
    }

    public static List<CondenserViewerRecipe> list(RecipeManager recipeManager) {
        var out = new ArrayList<CondenserViewerRecipe>();
        out.add(new CondenserViewerRecipe(
                CondenserSelectionState.TRASH_ID,
                ItemStack.EMPTY,
                CondenserOutput.TRASH.requiredPower,
                viableStorageComponents(CondenserOutput.TRASH.requiredPower)));
        for (var holder : recipeManager.getAllRecipesFor(ModRecipeTypes.CONDENSER_RECIPE.get())) {
            var recipe = holder.value();
            out.add(new CondenserViewerRecipe(
                    holder.id(),
                    recipe.getOutputCopy(),
                    recipe.getRequiredPower(),
                    recipe.getCatalystPreviewStacks()));
        }
        return out;
    }

    public static List<CondenserViewerRecipe> listWithoutTrash(Level level) {
        if (level == null) {
            return List.of();
        }
        return listWithoutTrash(level.getRecipeManager());
    }

    public static List<CondenserViewerRecipe> listWithoutTrash(RecipeManager recipeManager) {
        var out = new ArrayList<CondenserViewerRecipe>();
        for (var holder : recipeManager.getAllRecipesFor(ModRecipeTypes.CONDENSER_RECIPE.get())) {
            var recipe = holder.value();
            out.add(new CondenserViewerRecipe(
                    holder.id(),
                    recipe.getOutputCopy(),
                    recipe.getRequiredPower(),
                    recipe.getCatalystPreviewStacks()));
        }
        return out;
    }

    public static CondenserViewerRecipe find(Level level, ResourceLocation id) {
        if (CondenserSelectionState.isTrash(id)) {
            return new CondenserViewerRecipe(
                    CondenserSelectionState.TRASH_ID,
                    ItemStack.EMPTY,
                    CondenserOutput.TRASH.requiredPower,
                    viableStorageComponents(CondenserOutput.TRASH.requiredPower));
        }

        if (level == null) {
            return null;
        }

        CondenserRecipe recipe = CondenserRecipe.findById(level, id);
        if (recipe == null) {
            return null;
        }

        return new CondenserViewerRecipe(id, recipe.getOutputCopy(), recipe.getRequiredPower(),
                recipe.getCatalystPreviewStacks());
    }

    public static Component displayName(CondenserViewerRecipe recipe) {
        if (recipe == null || CondenserSelectionState.isTrash(recipe.id())) {
            return Component.translatable("gui.ae2mcr.condenser.trash");
        }

        return recipe.output().isEmpty() ? Component.literal(recipe.id().toString()) : recipe.output().getHoverName();
    }

    public static List<Component> tooltip(CondenserViewerRecipe recipe) {
        return List.of(
                displayName(recipe),
                Component.translatable(
                        "gui.ae2mcr.condenser.required_power",
                        recipe == null ? 0 : recipe.requiredPower()));
    }

    public static List<ItemStack> viableStorageComponents(int requiredPower) {
        return DefaultCatalystConfig.previewStacks(requiredPower);
    }
}
