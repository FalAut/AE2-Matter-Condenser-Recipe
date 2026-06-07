package com.falaut.ae2mcr.mixin.integration.dataenergistics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.fish_dan_.data_energistics.guideme.DataCaptureBallCondenserGuideRecipeMappings", remap = false)
public abstract class DataCaptureBallCondenserGuideRecipeMappingsMixin {
    @Inject(method = "collect", at = @At("HEAD"), cancellable = true, require = 0)
    private void ae2mcr$skipGuideRecipe(CallbackInfo ci) {
        ci.cancel();
    }
}
