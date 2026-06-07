package com.falaut.ae2mcr.integration;

import java.util.ArrayList;
import java.util.List;

import com.falaut.ae2mcr.config.DefaultCatalystConfig;
import com.falaut.ae2mcr.recipe.CondenserRecipe;
import com.falaut.ae2mcr.recipe.CondenserRecipeSelectionService;

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
        return level == null ? List.of(trashRecipe()) : list(level.getRecipeManager(), true);
    }

    public static List<CondenserViewerRecipe> list(RecipeManager recipeManager) {
        return list(recipeManager, true);
    }

    public static List<CondenserViewerRecipe> listWithoutTrash(Level level) {
        if (level == null) {
            return List.of();
        }
        return listWithoutTrash(level.getRecipeManager());
    }

    public static List<CondenserViewerRecipe> listWithoutTrash(RecipeManager recipeManager) {
        return list(recipeManager, false);
    }

    public static CondenserViewerRecipe find(Level level, ResourceLocation id) {
        if (CondenserRecipeSelectionService.isTrash(id)) {
            return trashRecipe();
        }

        if (level == null) {
            return null;
        }

        CondenserRecipe recipe = CondenserRecipeSelectionService.findRecipe(level, id);
        if (recipe == null) {
            return null;
        }

        return toViewerRecipe(id, recipe);
    }

    public static Component displayName(CondenserViewerRecipe recipe) {
        if (recipe == null || CondenserRecipeSelectionService.isTrash(recipe.id())) {
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

    private static List<CondenserViewerRecipe> list(RecipeManager recipeManager, boolean includeTrash) {
        var out = new ArrayList<CondenserViewerRecipe>();
        if (includeTrash) {
            out.add(trashRecipe());
        }
        for (var holder : CondenserRecipeSelectionService.listRecipeHolders(recipeManager)) {
            out.add(toViewerRecipe(holder.id(), holder.value()));
        }
        return List.copyOf(out);
    }

    private static CondenserViewerRecipe toViewerRecipe(ResourceLocation id, CondenserRecipe recipe) {
        return new CondenserViewerRecipe(
                id,
                recipe.getOutputCopy(),
                recipe.getRequiredPower(),
                recipe.getCatalystPreviewStacks());
    }

    private static CondenserViewerRecipe trashRecipe() {
        return new CondenserViewerRecipe(
                CondenserRecipeSelectionService.TRASH_ID,
                ItemStack.EMPTY,
                CondenserOutput.TRASH.requiredPower,
                viableStorageComponents(CondenserOutput.TRASH.requiredPower));
    }
}
