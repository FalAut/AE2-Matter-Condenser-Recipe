package com.falaut.ae2mcr.mixin.integration.extendedae;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falaut.ae2mcr.VoidCellSelectionState;

import appeng.api.stacks.AEItemKey;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.world.item.ItemStack;

@Mixin(targets = "com.glodblock.github.extendedae.common.inventory.VoidCellInventory", remap = false)
public abstract class VoidCellInventoryMixin {
    @Shadow
    private ItemStack stack;

    @Shadow
    private double voidEnergy;

    /**
     * Extended AE normally initializes this map while loading the cell. An IO
     * port can insert into a freshly-created inventory before that path runs,
     * leaving the field null and causing persist() to crash.
     */
    @Shadow
    @Mutable
    private it.unimi.dsi.fastutil.objects.Object2LongMap<appeng.api.stacks.AEKey> storedAmounts;

    @Shadow
    protected abstract it.unimi.dsi.fastutil.objects.Object2LongMap<appeng.api.stacks.AEKey> getCellItems();

    @Inject(method = "fillOutput", at = @At("HEAD"), cancellable = true)
    private void ae2mcr$fillOutputFromCondenserRecipe(CallbackInfo ci) {
        if (this.storedAmounts == null) {
            this.storedAmounts = new Object2LongOpenHashMap<>();
        }

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
