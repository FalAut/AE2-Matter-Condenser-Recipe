package com.falaut.ae2mcr.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.falaut.ae2mcr.CondenserSelectionState;
import com.falaut.ae2mcr.api.CondenserSelectionHost;
import com.falaut.ae2mcr.recipe.CondenserRecipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(targets = "appeng.blockentity.misc.CondenserBlockEntity")
public abstract class CondenserBlockEntityMixin implements CondenserSelectionHost {
    @Shadow
    public abstract double getStorage();

    @Shadow
    public abstract double getStoredPower();

    @Shadow
    public abstract void addPower(double rawPower);

    @Unique
    private static final String AE2MCR_SELECTED_RECIPE_NBT = "ae2mcrSelectedCondenserRecipe";

    @Unique
    private ResourceLocation ae2mcr$selectedRecipeId = CondenserSelectionState.TRASH_ID;

    @Unique
    private Level ae2mcr$level() {
        return ((BlockEntity) (Object) this).getLevel();
    }

    @Unique
    private ResourceLocation ae2mcr$getNormalizedSelectedId() {
        Level level = ae2mcr$level();
        if (level == null) {
            return CondenserSelectionState.TRASH_ID;
        }
        ae2mcr$selectedRecipeId = CondenserSelectionState.normalizeSelected(level, ae2mcr$selectedRecipeId);
        return ae2mcr$selectedRecipeId;
    }

    @Unique
    private CondenserRecipe ae2mcr$getSelectedRecipeStrict() {
        Level level = ((BlockEntity) (Object) this).getLevel();
        if (level == null || CondenserSelectionState.isTrash(ae2mcr$getNormalizedSelectedId())) {
            return null;
        }
        return CondenserRecipe.findById(level, ae2mcr$selectedRecipeId);
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void ae2mcr$saveSelectedRecipe(CompoundTag data, HolderLookup.Provider registries, CallbackInfo ci) {
        if (ae2mcr$selectedRecipeId != null) {
            data.putString(AE2MCR_SELECTED_RECIPE_NBT, ae2mcr$selectedRecipeId.toString());
        }
    }

    @Inject(method = "loadTag", at = @At("TAIL"))
    private void ae2mcr$loadSelectedRecipe(CompoundTag data, HolderLookup.Provider registries, CallbackInfo ci) {
        if (data.contains(AE2MCR_SELECTED_RECIPE_NBT)) {
            var parsed = ResourceLocation.tryParse(data.getString(AE2MCR_SELECTED_RECIPE_NBT));
            ae2mcr$selectedRecipeId = parsed == null ? CondenserSelectionState.TRASH_ID : parsed;
        } else {
            ae2mcr$selectedRecipeId = CondenserSelectionState.TRASH_ID;
        }
    }

    @Inject(method = "getOutput", at = @At("HEAD"), cancellable = true)
    private void ae2mcr$getOutputFromRecipe(CallbackInfoReturnable<ItemStack> cir) {
        var recipe = ae2mcr$getSelectedRecipeStrict();
        cir.setReturnValue(recipe == null ? ItemStack.EMPTY : recipe.getOutputCopy());
    }

    @Inject(method = "getRequiredPower", at = @At("HEAD"), cancellable = true)
    private void ae2mcr$getRequiredPowerFromRecipe(CallbackInfoReturnable<Double> cir) {
        Level level = ae2mcr$level();
        if (level == null) {
            cir.setReturnValue((double) CondenserSelectionState.requiredPower(level, CondenserSelectionState.TRASH_ID));
            return;
        }
        cir.setReturnValue((double) CondenserSelectionState.requiredPower(level, ae2mcr$getNormalizedSelectedId()));
    }

    @Override
    public ResourceLocation ae2mcr$getSelectedCondenserRecipeId() {
        return ae2mcr$getNormalizedSelectedId();
    }

    @Override
    public void ae2mcr$setSelectedCondenserRecipeId(ResourceLocation id) {
        Level level = ae2mcr$level();
        if (level != null) {
            ae2mcr$selectedRecipeId = CondenserSelectionState.normalizeSelected(level, id);
        } else {
            ae2mcr$selectedRecipeId = CondenserSelectionState.TRASH_ID;
        }
        ((BlockEntity) (Object) this).setChanged();
        var storage = getStorage();
        if (getStoredPower() > storage) {
            addPower(storage - getStoredPower());
        }
        addPower(0);
    }

    @Override
    public List<ResourceLocation> ae2mcr$getAvailableCondenserRecipeIds() {
        Level level = ae2mcr$level();
        if (level == null) {
            return List.of(CondenserSelectionState.TRASH_ID);
        }
        return CondenserSelectionState.listSelectableIds(level);
    }

    @Override
    public ItemStack ae2mcr$getCondenserRecipeOutputPreview(ResourceLocation id) {
        Level level = ae2mcr$level();
        if (level == null) {
            return ItemStack.EMPTY;
        }
        return CondenserSelectionState.preview(level, id);
    }

    @Override
    public int ae2mcr$getCondenserRequiredPower(ResourceLocation id) {
        Level level = ae2mcr$level();
        return CondenserSelectionState.requiredPower(level, id);
    }
}
