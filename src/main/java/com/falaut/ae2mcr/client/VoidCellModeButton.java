package com.falaut.ae2mcr.client;

import java.util.ArrayList;
import java.util.List;

import com.falaut.ae2mcr.CondenserSelectionState;
import com.falaut.ae2mcr.api.VoidCellMenuBridge;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class VoidCellModeButton extends IconButton {
    private final VoidCellMenuBridge menuBridge;

    private ResourceLocation selectedId = CondenserSelectionState.TRASH_ID;
    private ItemStack preview = ItemStack.EMPTY;
    private int requiredPower = 0;

    public VoidCellModeButton(VoidCellMenuBridge menuBridge) {
        super(VoidCellModeButton::onPress);
        this.menuBridge = menuBridge;
    }

    public void refreshFromMenu() {
        selectedId = menuBridge.ae2mcr$getSelectedRecipeId();
        preview = menuBridge.ae2mcr$getPreview(selectedId);
        requiredPower = menuBridge.ae2mcr$getRequiredPower(selectedId);
    }

    private static void onPress(net.minecraft.client.gui.components.Button btn) {
        if (!(btn instanceof VoidCellModeButton self)) {
            return;
        }

        boolean backwards = false;
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof AEBaseScreen<?> aeScreen) {
            backwards = aeScreen.isHandlingRightClick();
        }

        self.menuBridge.ae2mcr$cycleRecipe(backwards);
    }

    @Override
    protected Icon getIcon() {
        if (CondenserSelectionState.isTrash(selectedId)) {
            return Icon.CONDENSER_OUTPUT_TRASH;
        }
        return preview.isEmpty() ? Icon.INVALID : Icon.TOOLBAR_BUTTON_BACKGROUND;
    }

    @Override
    protected Item getItemOverlay() {
        return preview.isEmpty() ? null : preview.getItem();
    }

    @Override
    public List<Component> getTooltipMessage() {
        var lines = new ArrayList<Component>();
        lines.add(Component.translatable("gui.extendedae.void_cell"));
        lines.add(getSelectedDisplayName());
        lines.add(Component.translatable("gui.ae2_matter_condenser_recipe.condenser.required_power", requiredPower));
        return lines;
    }

    public Component getSelectedDisplayName() {
        if (CondenserSelectionState.isTrash(selectedId)) {
            return Component.translatable("gui.ae2_matter_condenser_recipe.condenser.trash");
        }
        return preview.isEmpty() ? Component.literal(selectedId.toString()) : preview.getHoverName();
    }
}
