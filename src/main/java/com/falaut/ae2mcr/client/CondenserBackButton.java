package com.falaut.ae2mcr.client;

import java.util.List;

import net.minecraft.network.chat.Component;
import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;

public class CondenserBackButton extends IconButton {
    public CondenserBackButton(OnPress onPress) {
        super(onPress);
    }

    @Override
    protected Icon getIcon() {
        return Icon.BACK;
    }

    @Override
    public List<Component> getTooltipMessage() {
        return List.of(Component.translatable("gui.ae2_matter_condenser_recipe.common.back"));
    }
}
