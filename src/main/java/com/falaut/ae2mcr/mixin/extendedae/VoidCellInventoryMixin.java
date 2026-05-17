package com.falaut.ae2mcr.mixin.extendedae;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falaut.ae2mcr.VoidCellSelectionState;

import appeng.api.stacks.AEItemKey;
import net.minecraft.world.item.ItemStack;

@Mixin(targets = "com.glodblock.github.extendedae.common.inventory.VoidCellInventory", remap = false)
public abstract class VoidCellInventoryMixin {
    @Shadow
    private ItemStack stack;

    @Shadow
    private double voidEnergy;

    @Shadow
    protected abstract it.unimi.dsi.fastutil.objects.Object2LongMap<appeng.api.stacks.AEKey> getCellItems();

    @Inject(method = "fillOutput", at = @At("HEAD"), cancellable = true)
    private void ae2mcr$fillOutputFromCondenserRecipe(CallbackInfo ci) {
        int requiredPower = VoidCellSelectionState.readRequiredPower(stack);
        if (requiredPower <= 0) {
            this.voidEnergy = 0;
            ci.cancel();
            return;
        }

        var outputStack = VoidCellSelectionState.readOutputStack(stack);
        if (outputStack.isEmpty()) {
            this.voidEnergy = 0;
            ci.cancel();
            return;
        }

        var output = AEItemKey.of(outputStack);
        long amt = (long) (this.voidEnergy / requiredPower);
        if (output != null && amt > 0) {
            long produced = amt * Math.max(1, outputStack.getCount());
            if (produced < 0) {
                produced = Long.MAX_VALUE;
            }
            var cellItems = this.getCellItems();
            var cur = cellItems.getLong(output);
            cellItems.put(output, cur + produced);
            this.voidEnergy -= amt * requiredPower;
        }

        ci.cancel();
    }
}
