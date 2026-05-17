package com.falaut.ae2mcr.mixin.extendedae;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falaut.ae2mcr.CondenserSelectionState;
import com.falaut.ae2mcr.VoidCellSelectionState;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

@Mixin(targets = "com.glodblock.github.extendedae.common.items.ItemVoidCell", remap = false)
public abstract class ItemVoidCellMixin {
    @Inject(method = "appendHoverText", at = @At("HEAD"), cancellable = true, remap = false)
    private void ae2mcr$replaceTooltip(
            ItemStack stack,
            Item.TooltipContext ctx,
            List<Component> lines,
            TooltipFlag flag,
            CallbackInfo ci) {
        ResourceLocation selectedId = VoidCellSelectionState.readSelectedRecipeId(stack);
        ItemStack output = VoidCellSelectionState.readOutputStack(stack);
        int requiredPower = VoidCellSelectionState.readRequiredPower(stack);
        Component modeName;

        if (CondenserSelectionState.isTrash(selectedId)) {
            modeName = Component.translatable("gui.ae2_matter_condenser_recipe.condenser.trash");
        } else if (!output.isEmpty()) {
            modeName = output.getHoverName().copy();
        } else {
            modeName = Component.literal(selectedId.toString());
        }
        lines.add(Component.translatable("gui.ae2_matter_condenser_recipe.void_cell.mode", modeName)
                .withStyle(ChatFormatting.GREEN));
        if (!CondenserSelectionState.isTrash(selectedId) && !output.isEmpty()) {
            lines.add(Component.translatable("gui.ae2_matter_condenser_recipe.condenser.required_power", requiredPower)
                    .withStyle(ChatFormatting.GRAY));
        }

        ci.cancel();
    }
}
