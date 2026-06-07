package com.falaut.ae2mcr.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.falaut.ae2mcr.api.CondenserMenuBridge;
import com.falaut.ae2mcr.recipe.CondenserRecipe;

import net.minecraft.world.item.ItemStack;
import appeng.menu.slot.RestrictedInputSlot;

@Mixin(RestrictedInputSlot.class)
public abstract class RestrictedInputSlotMixin {

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void ae2mcr$allowCustomCondenserCatalyst(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        var menu = ((AppEngSlotAccessor) (Object) this).ae2mcr$invokeGetMenu();
        if (!(menu instanceof CondenserMenuBridge bridge)) {
            return;
        }

        var level = bridge.ae2mcr$getMenuLevel();
        var recipe = CondenserRecipe.findById(level, bridge.ae2mcr$getSelectedRecipeId());
        if (recipe != null && recipe.acceptsCatalyst(stack)) {
            cir.setReturnValue(true);
        }
    }
}
