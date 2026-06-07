package com.falaut.ae2mcr.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;

public final class ConfigHolder {
    private ConfigHolder() {
    }

    public static void init(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, DefaultCatalystConfig.SPEC, "ae2mcr.toml");
    }
}
