package com.falaut.ae2mcr;

import com.falaut.ae2mcr.registry.ModRecipeSerializers;
import com.falaut.ae2mcr.registry.ModRecipeTypes;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(AE2MatterCondenserRecipe.MOD_ID)
public class AE2MatterCondenserRecipe {
    public static final String MOD_ID = "ae2_matter_condenser_recipe";

    public AE2MatterCondenserRecipe(IEventBus modBus, ModContainer modContainer) {
        ModRecipeTypes.RECIPE_TYPES.register(modBus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(modBus);
    }
}
