package com.falaut.ae2mcr.mixin.integration.jei;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.falaut.ae2mcr.integration.CondenserViewerRecipe;
import com.falaut.ae2mcr.integration.CondenserViewerRecipes;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

@Pseudo
@Mixin(targets = "tamaized.ae2jeiintegration.integration.modules.jei.categories.CondenserCategory", remap = false)
public abstract class JeiCondenserCategoryMixin {
    @Shadow
    @Final
    private IDrawable background;

    @Shadow
    @Final
    private IDrawableAnimated progress;

    @Shadow
    @Final
    private IDrawable backgroundTrash;

    @Shadow
    @Final
    private IDrawable toolbarButtonBackground;

    @Inject(method = "isHandled(Ljava/lang/Object;)Z", at = @At("HEAD"), cancellable = true, require = 0)
    private void ae2mcr$isHandled(Object recipe, CallbackInfoReturnable<Boolean> cir) {
        if (recipe instanceof CondenserViewerRecipe) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Ljava/lang/Object;Lmezz/jei/api/recipe/IFocusGroup;)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void ae2mcr$setRecipe(
            IRecipeLayoutBuilder builder,
            Object recipeObj,
            IFocusGroup focuses,
            CallbackInfo ci) {
        if (!(recipeObj instanceof CondenserViewerRecipe recipe)) {
            return;
        }

        var output = recipe.output();
        if (!output.isEmpty()) {
            builder.addOutputSlot(57, 27).addItemStack(output);
        }

        List<net.minecraft.world.item.ItemStack> catalysts = CondenserViewerRecipes
                .viableStorageComponents(recipe.requiredPower());
        builder.addSlot(RecipeIngredientRole.CATALYST, 53, 1).addItemStacks(catalysts);
        ci.cancel();
    }

    @Inject(method = "draw(Ljava/lang/Object;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphics;DD)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void ae2mcr$draw(
            Object recipeObj,
            IRecipeSlotsView recipeSlotsView,
            GuiGraphics guiGraphics,
            double mouseX,
            double mouseY,
            CallbackInfo ci) {
        if (!(recipeObj instanceof CondenserViewerRecipe recipe)) {
            return;
        }

        this.background.draw(guiGraphics);
        this.progress.draw(guiGraphics);
        this.backgroundTrash.draw(guiGraphics, 3, 27);
        this.toolbarButtonBackground.draw(guiGraphics, 80, 26);
        if (!recipe.output().isEmpty()) {
            guiGraphics.renderItem(recipe.output(), 81, 27);
        }
        ci.cancel();
    }

    @Inject(method = "getTooltip(Lmezz/jei/api/gui/builder/ITooltipBuilder;Ljava/lang/Object;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;DD)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void ae2mcr$getTooltip(
            ITooltipBuilder tooltip,
            Object recipeObj,
            IRecipeSlotsView recipeSlotsView,
            double mouseX,
            double mouseY,
            CallbackInfo ci) {
        if (!(recipeObj instanceof CondenserViewerRecipe recipe)) {
            return;
        }

        if (mouseX >= 80 && mouseX < 96 && mouseY >= 26 && mouseY < 42) {
            for (var line : CondenserViewerRecipes.tooltip(recipe)) {
                tooltip.add(line);
            }
        }
        ci.cancel();
    }

    @Inject(method = "getRegistryName(Ljava/lang/Object;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 0)
    private void ae2mcr$getRegistryName(Object recipeObj, CallbackInfoReturnable<ResourceLocation> cir) {
        if (recipeObj instanceof CondenserViewerRecipe recipe) {
            cir.setReturnValue(recipe.id());
        }
    }
}
