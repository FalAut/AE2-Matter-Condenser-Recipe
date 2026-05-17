package com.falaut.ae2mcr.mixin.integration.rei;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.falaut.ae2mcr.integration.CondenserReiDisplay;
import com.falaut.ae2mcr.integration.CondenserViewerRecipes;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import appeng.core.AppEng;

@Pseudo
@Mixin(targets = "appeng.integration.modules.rei.CondenserCategory", remap = false)
public abstract class ReiCondenserCategoryMixin {
    @Inject(method = "setupDisplay", at = @At("HEAD"), cancellable = true, remap = false)
    private void ae2mcr$setupCustomDisplay(
            appeng.integration.modules.rei.CondenserOutputDisplay recipeDisplay,
            Rectangle bounds,
            CallbackInfoReturnable<List<Widget>> cir) {
        if (!(recipeDisplay instanceof CondenserReiDisplay display)) {
            return;
        }

        final int padding = 7;
        final int width = 96;
        final int height = 48;
        Point origin = new Point(bounds.x + padding, bounds.y + padding);

        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.wrapRenderer(bounds,
                new appeng.integration.modules.rei.BackgroundRenderer(width + 2 * padding, height + 2 * padding)));

        ResourceLocation condenserTex = AppEng.makeId("textures/guis/condenser.png");
        ResourceLocation statesTex = AppEng.makeId("textures/guis/states.png");
        widgets.add(Widgets.createTexturedWidget(condenserTex, origin.x, origin.y, 48, 25, width, height));
        widgets.add(Widgets.createTexturedWidget(statesTex, origin.x + 4, origin.y + 28, 241, 81, 14, 14));
        widgets.add(Widgets.createTexturedWidget(statesTex, origin.x + 80, origin.y + 28, 240, 240, 16, 16));
        widgets.add(Widgets.wrapRenderer(bounds,
                new appeng.integration.modules.rei.ProgressBarRenderer(condenserTex, origin.x + 72, origin.y, 6, 18,
                        176, 0)));

        widgets.add(Widgets.createDrawableWidget((guiGraphics, mouseX, mouseY, delta) -> {
            if (display.getRecipe() != null && !display.getRecipe().output().isEmpty()) {
                guiGraphics.renderItem(display.getRecipe().output(), origin.x + 81, origin.y + 29);
            }
            Rectangle rect = new Rectangle(origin.x + 80, origin.y + 28, 16, 16);
            if (rect.contains(mouseX, mouseY)) {
                Tooltip.create(CondenserViewerRecipes.tooltip(display.getRecipe())).queue();
            }
        }));

        if (!display.getOutputEntries().isEmpty()) {
            Slot outputSlot = Widgets.createSlot(new Point(origin.x + 57, origin.y + 27))
                    .disableBackground()
                    .markOutput()
                    .entries(display.getOutputEntries().getFirst());
            widgets.add(outputSlot);
        }

        Slot storageSlot = Widgets.createSlot(new Point(origin.x + 53, origin.y + 1))
                .disableBackground()
                .markInput()
                .entries(display.getViableStorageComponents());
        widgets.add(storageSlot);

        cir.setReturnValue(widgets);
    }
}
