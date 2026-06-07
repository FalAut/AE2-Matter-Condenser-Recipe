package com.falaut.ae2mcr.mixin.integration.dataenergistics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;

@Pseudo
@Mixin(targets = "com.fish_dan_.data_energistics.client.emi.DataEnergisticsEmiPlugin", remap = false)
public abstract class DataEnergisticsEmiPluginMixin {
    @Redirect(
            method = "register",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/emi/emi/api/EmiRegistry;addRecipe(Ldev/emi/emi/api/recipe/EmiRecipe;)V"),
            require = 0)
    private void ae2mcr$skipCondenserRecipe(EmiRegistry registry, EmiRecipe recipe) {
        if (!"data_energistics:condenser/data_capture_ball".equals(recipe.getId().toString())) {
            registry.addRecipe(recipe);
        }
    }
}
