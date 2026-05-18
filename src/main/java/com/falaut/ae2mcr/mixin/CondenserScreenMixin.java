package com.falaut.ae2mcr.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falaut.ae2mcr.api.CondenserMenuBridge;
import com.falaut.ae2mcr.client.CondenserModeButton;
import com.falaut.ae2mcr.client.CondenserRecipeSelectionScreen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AE2Button;
import appeng.menu.implementations.CondenserMenu;

@Mixin(targets = "appeng.client.gui.implementations.CondenserScreen")
public abstract class CondenserScreenMixin extends AEBaseScreen<CondenserMenu> {
    @Unique
    private CondenserModeButton ae2mcr$modeButton;

    private AE2Button ae2mcr$openSelectorButton;

    @SuppressWarnings("unchecked")
    protected CondenserScreenMixin(CondenserMenu menu, net.minecraft.world.entity.player.Inventory playerInventory,
            Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lappeng/client/gui/WidgetContainer;add(Ljava/lang/String;Lnet/minecraft/client/gui/components/AbstractWidget;)V"))
    private void ae2mcr$replaceModeButton(WidgetContainer widgetContainer, String id,
            net.minecraft.client.gui.components.AbstractWidget widget) {
        if ("mode".equals(id)) {
            return;
        }
        widgetContainer.add(id, widget);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void ae2mcr$initSelectorButton(CondenserMenu menu, Inventory playerInventory,
            Component title, ScreenStyle style, CallbackInfo ci) {
        ae2mcr$modeButton = new CondenserModeButton((CondenserMenuBridge) menu);
        ae2mcr$openSelectorButton = new AE2Button(
                Component.translatable("gui.ae2mcr.condenser.open_selector"),
                btn -> switchToScreen(new CondenserRecipeSelectionScreen((AEBaseScreen<CondenserMenu>) (Object) this)));
        ae2mcr$openSelectorButton.setTooltip(
                net.minecraft.client.gui.components.Tooltip.create(
                        Component.translatable("gui.ae2mcr.condenser.select_output_button")));
    }

    @Inject(method = "updateBeforeRender", at = @At("TAIL"))
    private void ae2mcr$addSelectorButton(CallbackInfo ci) {
        if (ae2mcr$openSelectorButton == null || ae2mcr$modeButton == null) {
            return;
        }

        ae2mcr$modeButton.refreshFromMenu();

        if (!this.children().contains(ae2mcr$modeButton)) {
            addRenderableWidget(ae2mcr$modeButton);
        }
        if (!this.children().contains(ae2mcr$openSelectorButton)) {
            addRenderableWidget(ae2mcr$openSelectorButton);
        }

        ae2mcr$modeButton.setPosition(this.leftPos + 129, this.topPos + 53);
        ae2mcr$modeButton.setSize(16, 16);
        ae2mcr$openSelectorButton.setPosition(this.leftPos + 151, this.topPos + 53);
        ae2mcr$openSelectorButton.setSize(18, 18);
    }
}
