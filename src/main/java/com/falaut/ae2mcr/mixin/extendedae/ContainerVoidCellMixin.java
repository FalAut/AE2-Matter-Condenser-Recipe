package com.falaut.ae2mcr.mixin.extendedae;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falaut.ae2mcr.CondenserSelectionState;
import com.falaut.ae2mcr.VoidCellSelectionState;
import com.falaut.ae2mcr.api.VoidCellMenuBridge;
import com.falaut.ae2mcr.recipe.CondenserRecipe;

import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(targets = "com.glodblock.github.extendedae.container.ContainerVoidCell", remap = false)
public abstract class ContainerVoidCellMixin extends AEBaseMenu implements VoidCellMenuBridge {
    @Shadow
    private ItemStack stack;

    @GuiSync(11)
    public ResourceLocation ae2mcr$selectedRecipeId = CondenserSelectionState.TRASH_ID;

    @GuiSync(12)
    public String ae2mcr$availableRecipeIdsCsv = "";

    @Unique
    private static final String AE2MCR_ACTION_SELECT_RECIPE = "ae2mcr_select_recipe";
    @Unique
    private static final String AE2MCR_ACTION_CYCLE_RECIPE = "ae2mcr_cycle_recipe";

    protected ContainerVoidCellMixin() {
        super(null, -1, null, null);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void ae2mcr$registerActions(CallbackInfo ci) {
        registerClientAction(AE2MCR_ACTION_SELECT_RECIPE, String.class, this::ae2mcr$handleSelectRecipe);
        registerClientAction(AE2MCR_ACTION_CYCLE_RECIPE, Boolean.class, this::ae2mcr$handleCycleRecipe);
    }

    @Inject(method = "broadcastChanges", at = @At("TAIL"))
    private void ae2mcr$syncSelection(CallbackInfo ci) {
        if (!isServerSide()) {
            return;
        }

        Level level = ae2mcr$getMenuLevel();
        if (level == null) {
            ae2mcr$selectedRecipeId = CondenserSelectionState.TRASH_ID;
            ae2mcr$availableRecipeIdsCsv = CondenserSelectionState.TRASH_ID.toString();
            return;
        }

        ResourceLocation selected = VoidCellSelectionState.normalizeSelected(
                level.getRecipeManager(),
                VoidCellSelectionState.readSelectedRecipeId(stack));
        VoidCellSelectionState.writeResolvedSelection(stack, level.getRecipeManager(), selected);
        ae2mcr$selectedRecipeId = selected;

        ae2mcr$availableRecipeIdsCsv = String.join(",",
                VoidCellSelectionState.listSelectableIds(level.getRecipeManager())
                        .stream()
                        .map(ResourceLocation::toString)
                        .toList());
    }

    @Unique
    private void ae2mcr$handleSelectRecipe(String recipeIdString) {
        Level level = ae2mcr$getMenuLevel();
        if (level == null) {
            return;
        }

        var id = recipeIdString == null || recipeIdString.isBlank()
                ? CondenserSelectionState.TRASH_ID
                : ResourceLocation.tryParse(recipeIdString);
        if (id == null) {
            id = CondenserSelectionState.TRASH_ID;
        }

        id = VoidCellSelectionState.normalizeSelected(level.getRecipeManager(), id);
        VoidCellSelectionState.writeResolvedSelection(stack, level.getRecipeManager(), id);
        broadcastChanges();
    }

    @Unique
    private void ae2mcr$handleCycleRecipe(Boolean backwardsArg) {
        Level level = ae2mcr$getMenuLevel();
        if (level == null) {
            return;
        }

        List<ResourceLocation> ids = VoidCellSelectionState.listSelectableIds(level.getRecipeManager());
        if (ids.isEmpty()) {
            return;
        }

        ResourceLocation current = VoidCellSelectionState.normalizeSelected(
                level.getRecipeManager(),
                VoidCellSelectionState.readSelectedRecipeId(stack));
        int index = ids.indexOf(current);
        if (index < 0) {
            index = 0;
        }

        boolean backwards = backwardsArg != null && backwardsArg;
        int nextIndex = backwards ? (index - 1 + ids.size()) % ids.size() : (index + 1) % ids.size();
        VoidCellSelectionState.writeResolvedSelection(stack, level.getRecipeManager(), ids.get(nextIndex));
        broadcastChanges();
    }

    @Override
    public void ae2mcr$selectRecipe(ResourceLocation id) {
        if (isClientSide()) {
            sendClientAction(AE2MCR_ACTION_SELECT_RECIPE,
                    (id == null ? CondenserSelectionState.TRASH_ID : id).toString());
        }
    }

    @Override
    public void ae2mcr$cycleRecipe(boolean backwards) {
        if (isClientSide()) {
            sendClientAction(AE2MCR_ACTION_CYCLE_RECIPE, backwards);
        }
    }

    @Override
    public ResourceLocation ae2mcr$getSelectedRecipeId() {
        return ae2mcr$selectedRecipeId == null ? CondenserSelectionState.TRASH_ID : ae2mcr$selectedRecipeId;
    }

    @Override
    public List<ResourceLocation> ae2mcr$getAvailableRecipeIds() {
        if (ae2mcr$availableRecipeIdsCsv == null || ae2mcr$availableRecipeIdsCsv.isBlank()) {
            return List.of(CondenserSelectionState.TRASH_ID);
        }

        List<ResourceLocation> out = new ArrayList<>();
        for (var raw : ae2mcr$availableRecipeIdsCsv.split(",")) {
            var id = ResourceLocation.tryParse(raw);
            if (id != null) {
                out.add(id);
            }
        }
        return out;
    }

    @Override
    public ItemStack ae2mcr$getPreview(ResourceLocation id) {
        return CondenserSelectionState.preview(ae2mcr$getMenuLevel(), id);
    }

    @Override
    public int ae2mcr$getRequiredPower(ResourceLocation id) {
        return CondenserSelectionState.requiredPower(ae2mcr$getMenuLevel(), id);
    }

    @Override
    public Level ae2mcr$getMenuLevel() {
        return getPlayer().level();
    }
}
