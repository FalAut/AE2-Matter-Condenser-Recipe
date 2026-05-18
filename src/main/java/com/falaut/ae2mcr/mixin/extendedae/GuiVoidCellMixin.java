package com.falaut.ae2mcr.mixin.extendedae;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falaut.ae2mcr.api.VoidCellMenuBridge;
import com.falaut.ae2mcr.client.VoidCellModeButton;
import com.falaut.ae2mcr.client.VoidCellRecipeSelectionScreen;
import com.glodblock.github.extendedae.container.ContainerVoidCell;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.widgets.AE2Button;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@Mixin(targets = "com.glodblock.github.extendedae.client.gui.GuiVoidCell", remap = false)
public abstract class GuiVoidCellMixin extends AEBaseScreen<ContainerVoidCell> {
    @Shadow
    private com.glodblock.github.extendedae.client.button.ActionEPPButton trash;
    @Shadow
    private com.glodblock.github.extendedae.client.button.ActionEPPButton matterBall;
    @Shadow
    private com.glodblock.github.extendedae.client.button.ActionEPPButton singularity;

    @Unique
    private VoidCellModeButton ae2mcr$modeButton;
    @Unique
    private AE2Button ae2mcr$openSelectorButton;

    @SuppressWarnings("unchecked")
    protected GuiVoidCellMixin(ContainerVoidCell menu, Inventory playerInventory, Component title,
            appeng.client.gui.style.ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "init", at = @At("TAIL"), remap = false)
    private void ae2mcr$hideLegacyButtons(CallbackInfo ci) {
        if (trash != null) {
            trash.visible = false;
            trash.active = false;
        }
        if (matterBall != null) {
            matterBall.visible = false;
            matterBall.active = false;
        }
        if (singularity != null) {
            singularity.visible = false;
            singularity.active = false;
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void ae2mcr$initButtons(ContainerVoidCell menu, Inventory playerInventory, Component title,
            appeng.client.gui.style.ScreenStyle style, CallbackInfo ci) {
        ae2mcr$modeButton = new VoidCellModeButton((VoidCellMenuBridge) menu);
        ae2mcr$openSelectorButton = new AE2Button(
                Component.translatable("gui.ae2mcr.condenser.open_selector"),
                btn -> switchToScreen(new VoidCellRecipeSelectionScreen((AEBaseScreen<ContainerVoidCell>) (Object) this)));
        ae2mcr$openSelectorButton.setTooltip(
                net.minecraft.client.gui.components.Tooltip.create(
                        Component.translatable("gui.ae2mcr.condenser.select_output_button")));
    }

    @Inject(method = "drawFG", at = @At("HEAD"), remap = false)
    private void ae2mcr$updateButtonsBeforeRender(
            net.minecraft.client.gui.GuiGraphics guiGraphics,
            int offsetX,
            int offsetY,
            int mouseX,
            int mouseY,
            CallbackInfo ci) {
        if (ae2mcr$modeButton == null || ae2mcr$openSelectorButton == null) {
            return;
        }

        ae2mcr$modeButton.refreshFromMenu();
        if (!this.children().contains(ae2mcr$modeButton)) {
            addRenderableWidget(ae2mcr$modeButton);
        }
        if (!this.children().contains(ae2mcr$openSelectorButton)) {
            addRenderableWidget(ae2mcr$openSelectorButton);
        }

        ae2mcr$modeButton.setPosition(this.leftPos + 54, this.topPos + 20);
        ae2mcr$modeButton.setSize(16, 16);
        ae2mcr$openSelectorButton.setPosition(this.leftPos + 76, this.topPos + 20);
        ae2mcr$openSelectorButton.setSize(18, 18);
    }

    @Redirect(
            method = "drawFG",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I"),
            remap = false)
    private int ae2mcr$hideLegacyModeText(
            GuiGraphics guiGraphics,
            Font font,
            Component text,
            int x,
            int y,
            int color,
            boolean dropShadow) {
        return 0;
    }
}
