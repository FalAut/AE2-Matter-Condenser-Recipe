package com.falaut.ae2mcr.mixin.integration.rei;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falaut.ae2mcr.integration.CondenserReiDisplay;
import com.falaut.ae2mcr.integration.CondenserViewerRecipes;

import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.display.Display;

@Pseudo
@Mixin(targets = "appeng.integration.modules.rei.ReiPlugin", remap = false)
public abstract class ReiPluginMixin {
    @Redirect(
            method = "registerDisplays",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/shedaniel/rei/api/client/registry/display/DisplayRegistry;add(Lme/shedaniel/rei/api/common/display/Display;)V"),
            remap = false,
            require = 0)
    private void ae2mcr$skipVanillaCondenserDisplays(DisplayRegistry registry, Display display) {
        if ("appeng.integration.modules.rei.CondenserOutputDisplay".equals(display.getClass().getName())) {
            return;
        }
        registry.add(display);
    }

    @Inject(method = "registerDisplays", at = @At("TAIL"), remap = false)
    private void ae2mcr$addCustomCondenserDisplays(DisplayRegistry registry, CallbackInfo ci) {
        for (var recipe : CondenserViewerRecipes.listWithoutTrash(registry.getRecipeManager())) {
            registry.add(new CondenserReiDisplay(recipe));
        }
    }
}
