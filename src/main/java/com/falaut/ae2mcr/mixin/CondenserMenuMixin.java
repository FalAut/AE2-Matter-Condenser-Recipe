package com.falaut.ae2mcr.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.falaut.ae2mcr.CondenserSelectionState;
import com.falaut.ae2mcr.api.CondenserMenuBridge;
import com.falaut.ae2mcr.api.CondenserSelectionHost;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import appeng.blockentity.misc.CondenserBlockEntity;
import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;

@Mixin(targets = "appeng.menu.implementations.CondenserMenu")
public abstract class CondenserMenuMixin extends AEBaseMenu implements CondenserMenuBridge {
    @Shadow
    private CondenserBlockEntity condenser;

    @GuiSync(3)
    public ResourceLocation ae2mcr$selectedRecipeId = CondenserSelectionState.TRASH_ID;

    @GuiSync(4)
    public String ae2mcr$availableRecipeIdsCsv = "";

    @Unique
    private static final String AE2MCR_ACTION_SELECT_RECIPE = "ae2mcr_select_recipe";
    @Unique
    private static final String AE2MCR_ACTION_CYCLE_RECIPE = "ae2mcr_cycle_recipe";

    protected CondenserMenuMixin() {
        super(null, -1, null, null);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void ae2mcr$initAction(int id, net.minecraft.world.entity.player.Inventory ip, CondenserBlockEntity condenser,
            CallbackInfo ci) {
        registerClientAction(AE2MCR_ACTION_SELECT_RECIPE, String.class, this::ae2mcr$handleSelectRecipe);
        registerClientAction(AE2MCR_ACTION_CYCLE_RECIPE, Boolean.class, this::ae2mcr$handleCycleRecipe);
    }

    @Inject(method = "broadcastChanges", at = @At("HEAD"))
    private void ae2mcr$broadcastRecipeData(CallbackInfo ci) {
        if (!isServerSide()) {
            return;
        }

        if (!(condenser instanceof CondenserSelectionHost host)) {
            return;
        }

        ae2mcr$selectedRecipeId = host.ae2mcr$getSelectedCondenserRecipeId();
        ae2mcr$availableRecipeIdsCsv = String.join(",",
                host.ae2mcr$getAvailableCondenserRecipeIds().stream().map(ResourceLocation::toString).toList());
    }

    @Unique
    private void ae2mcr$handleSelectRecipe(String recipeIdString) {
        if (!(condenser instanceof CondenserSelectionHost host)) {
            return;
        }

        var id = recipeIdString == null || recipeIdString.isBlank()
                ? CondenserSelectionState.TRASH_ID
                : ResourceLocation.tryParse(recipeIdString);
        if (id == null) {
            id = CondenserSelectionState.TRASH_ID;
        }
        host.ae2mcr$setSelectedCondenserRecipeId(id);
    }

    @Unique
    private void ae2mcr$handleCycleRecipe(Boolean backwardsArg) {
        if (!(condenser instanceof CondenserSelectionHost host)) {
            return;
        }

        List<ResourceLocation> ids = host.ae2mcr$getAvailableCondenserRecipeIds();
        if (ids.isEmpty()) {
            host.ae2mcr$setSelectedCondenserRecipeId(CondenserSelectionState.TRASH_ID);
            return;
        }

        ResourceLocation current = host.ae2mcr$getSelectedCondenserRecipeId();
        int index = ids.indexOf(current);
        if (index < 0) {
            index = 0;
        }

        boolean backwards = backwardsArg != null && backwardsArg;
        int nextIndex = backwards ? (index - 1 + ids.size()) % ids.size() : (index + 1) % ids.size();
        host.ae2mcr$setSelectedCondenserRecipeId(ids.get(nextIndex));
    }

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
