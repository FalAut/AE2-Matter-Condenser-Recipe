package com.falaut.ae2mcr.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.components.AbstractWidget;
import appeng.client.gui.WidgetContainer;

@Mixin(WidgetContainer.class)
public interface WidgetContainerAccessor {
    @Accessor("widgets")
    Map<String, AbstractWidget> ae2mcr$getWidgets();
}
