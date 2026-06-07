package com.falaut.ae2mcr;

import java.util.List;

import com.falaut.ae2mcr.recipe.CondenserRecipeSelectionService;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class CondenserSelectionState {
    public static final ResourceLocation TRASH_ID = CondenserRecipeSelectionService.TRASH_ID;

    private CondenserSelectionState() {
    }

    public static List<ResourceLocation> listSelectableIds(Level level) {
        return CondenserRecipeSelectionService.listSelectableIds(level);
    }

    public static boolean isTrash(ResourceLocation id) {
        return CondenserRecipeSelectionService.isTrash(id);
    }

    public static ItemStack preview(Level level, ResourceLocation id) {
        return CondenserRecipeSelectionService.preview(level, id);
    }

    public static int requiredPower(Level level, ResourceLocation id) {
        return CondenserRecipeSelectionService.requiredPower(level, id);
    }

    public static ResourceLocation normalizeSelected(Level level, ResourceLocation current) {
        return CondenserRecipeSelectionService.normalizeSelected(level, current);
    }
}
