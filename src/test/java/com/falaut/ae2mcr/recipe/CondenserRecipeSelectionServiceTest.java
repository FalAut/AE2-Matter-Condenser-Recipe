package com.falaut.ae2mcr.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.minecraft.resources.ResourceLocation;

class CondenserRecipeSelectionServiceTest {
    private static final ResourceLocation RECIPE_A = ResourceLocation.fromNamespaceAndPath("test", "a");
    private static final ResourceLocation RECIPE_B = ResourceLocation.fromNamespaceAndPath("test", "b");

    @Test
    void normalizeFallsBackToTrashWhenIdMissing() {
        assertEquals(
                CondenserRecipeSelectionService.TRASH_ID,
                CondenserRecipeSelectionService.normalizeSelected(List.of(
                        CondenserRecipeSelectionService.TRASH_ID,
                        RECIPE_A),
                        RECIPE_B));
    }

    @Test
    void cycleSelectionWrapsInBothDirections() {
        var ids = List.of(
                CondenserRecipeSelectionService.TRASH_ID,
                RECIPE_A,
                RECIPE_B);

        assertEquals(RECIPE_A, CondenserRecipeSelectionService.cycleSelection(ids,
                CondenserRecipeSelectionService.TRASH_ID, false));
        assertEquals(RECIPE_B, CondenserRecipeSelectionService.cycleSelection(ids,
                CondenserRecipeSelectionService.TRASH_ID, true));
    }

    @Test
    void nullLevelDefaultsRemainStable() {
        assertEquals(List.of(CondenserRecipeSelectionService.TRASH_ID),
                CondenserRecipeSelectionService.listSelectableIds((net.minecraft.world.level.Level) null));
        assertEquals(CondenserRecipeSelectionService.TRASH_ID,
                CondenserRecipeSelectionService.normalizeSelected((net.minecraft.world.level.Level) null, RECIPE_A));
    }
}
