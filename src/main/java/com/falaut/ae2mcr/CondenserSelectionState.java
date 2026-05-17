package com.falaut.ae2mcr;

import java.util.ArrayList;
import java.util.List;

import com.falaut.ae2mcr.recipe.CondenserRecipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import appeng.api.config.CondenserOutput;

public final class CondenserSelectionState {
    public static final ResourceLocation TRASH_ID = ResourceLocation
            .fromNamespaceAndPath(AE2MatterCondenserRecipe.MOD_ID, "trash");

    private CondenserSelectionState() {
    }

    public static List<ResourceLocation> listSelectableIds(Level level) {
        var ids = new ArrayList<ResourceLocation>();
        ids.add(TRASH_ID);
        ids.addAll(CondenserRecipe.listIds(level));
        return ids;
    }

    public static boolean isTrash(ResourceLocation id) {
        return id == null || TRASH_ID.equals(id);
    }

    public static ItemStack preview(Level level, ResourceLocation id) {
        if (level == null) {
            return ItemStack.EMPTY;
        }
        if (isTrash(id)) {
            return ItemStack.EMPTY;
        }
        var recipe = CondenserRecipe.findById(level, id);
        return recipe == null ? ItemStack.EMPTY : recipe.getOutputCopy();
    }

    public static int requiredPower(Level level, ResourceLocation id) {
        if (isTrash(id)) {
            return CondenserOutput.TRASH.requiredPower;
        }
        if (level == null) {
            return Integer.MAX_VALUE;
        }
        var recipe = CondenserRecipe.findById(level, id);
        return recipe == null ? Integer.MAX_VALUE : recipe.getRequiredPower();
    }

    public static ResourceLocation normalizeSelected(Level level, ResourceLocation current) {
        if (isTrash(current)) {
            return TRASH_ID;
        }
        if (CondenserRecipe.findById(level, current) == null) {
            return TRASH_ID;
        }
        return current;
    }
}
