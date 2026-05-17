package com.falaut.ae2mcr.client;

import java.util.List;
import java.util.ArrayList;

import com.falaut.ae2mcr.CondenserSelectionState;
import com.falaut.ae2mcr.api.CondenserMenuBridge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import appeng.client.Point;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.AESubScreen;
import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.widgets.AE2Button;
import appeng.client.gui.Icon;
import appeng.client.gui.widgets.Scrollbar;
import appeng.menu.implementations.CondenserMenu;

public class CondenserRecipeSelectionScreen extends AESubScreen<CondenserMenu, AEBaseScreen<CondenserMenu>> {
    private static final int ROW_HEIGHT = 20;
    private static final int ROW_PAGE_SIZE = 7;

    private final CondenserMenuBridge menuBridge;
    private final Scrollbar scrollbar = new Scrollbar(Scrollbar.SMALL);
    private final RecipeListWidget recipeListWidget = new RecipeListWidget();

    public CondenserRecipeSelectionScreen(AEBaseScreen<CondenserMenu> parent) {
        super(parent, "/screens/ae2mcr/condenser_selector.json");
        this.menuBridge = (CondenserMenuBridge) parent.getMenu();

        addToLeftToolbar(new CondenserBackButton(btn -> returnToParent()));
        widgets.add("scrollbar", scrollbar);
        widgets.add("recipes", recipeListWidget);

        scrollbar.setCaptureMouseWheel(true);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        setTextContent(AEBaseScreen.TEXT_ID_DIALOG_TITLE,
                Component.translatable("gui.ae2_matter_condenser_recipe.condenser.select_output"));
        recipeListWidget.refreshRows();
    }

    @Override
    protected @Nullable guideme.PageAnchor getHelpTopic() {
        return null;
    }

    private class RecipeListWidget implements ICompositeWidget {
        private Rect2i bounds = new Rect2i(0, 0, 0, 0);
        private final List<RecipeEntryButton> rowButtons = new ArrayList<>();
        private int originX;
        private int originY;
        private int rowWidth;

        @Override
        public void setPosition(Point position) {
            bounds = new Rect2i(position.getX(), position.getY(), bounds.getWidth(), bounds.getHeight());
        }

        @Override
        public void setSize(int width, int height) {
            bounds = new Rect2i(bounds.getX(), bounds.getY(), width, height);
        }

        @Override
        public Rect2i getBounds() {
            return bounds;
        }

        @Override
        public void populateScreen(java.util.function.Consumer<AbstractWidget> addWidget, Rect2i screenBounds,
                AEBaseScreen<?> screen) {
            originX = screenBounds.getX() + bounds.getX();
            originY = screenBounds.getY() + bounds.getY();
            rowWidth = bounds.getWidth();

            if (rowButtons.isEmpty()) {
                for (int i = 0; i < ROW_PAGE_SIZE; i++) {
                    int rowY = originY + i * ROW_HEIGHT;
                    var row = new RecipeEntryButton(originX, rowY, rowWidth, ROW_HEIGHT);
                    rowButtons.add(row);
                }
            } else {
                for (int i = 0; i < rowButtons.size(); i++) {
                    rowButtons.get(i).setPosition(originX, originY + i * ROW_HEIGHT);
                    rowButtons.get(i).setWidth(rowWidth);
                }
            }

            for (var row : rowButtons) {
                addWidget.accept(row);
            }

            refreshRows();
        }

        private void refreshRows() {
            List<ResourceLocation> ids = menuBridge.ae2mcr$getAvailableRecipeIds();
            ResourceLocation selected = menuBridge.ae2mcr$getSelectedRecipeId();

            int maxStart = Math.max(0, ids.size() - ROW_PAGE_SIZE);
            scrollbar.setRange(0, maxStart, 1);
            int start = scrollbar.getCurrentScroll();

            for (int rowIndex = 0; rowIndex < rowButtons.size(); rowIndex++) {
                int i = start + rowIndex;
                var row = rowButtons.get(rowIndex);
                if (i >= ids.size()) {
                    if (ids.isEmpty() && rowIndex == 0) {
                        row.setEmpty(
                                Component.translatable("gui.ae2_matter_condenser_recipe.condenser.no_recipes"));
                    } else {
                        row.setHidden();
                    }
                    continue;
                }

                ResourceLocation id = ids.get(i);
                ItemStack preview = menuBridge.ae2mcr$getPreview(id);
                Component label = CondenserSelectionState.isTrash(id)
                        ? Component.translatable("gui.ae2_matter_condenser_recipe.condenser.trash")
                        : (preview.isEmpty() ? Component.literal(id.toString()) : preview.getHoverName());
                int requiredPower = menuBridge.ae2mcr$getRequiredPower(id);
                boolean active = selected == null || !selected.equals(id);
                row.setEntry(id, preview, label, requiredPower, active);
            }
        }
    }

    private class RecipeEntryButton extends AE2Button {
        private ResourceLocation recipeId;
        private ItemStack iconStack = ItemStack.EMPTY;
        private Component text = Component.empty();
        private int requiredPower = 0;
        private boolean emptyLine = false;

        public RecipeEntryButton(int x, int y, int width, int height) {
            super(x, y, width, height, Component.empty(), btn -> {
            });
            this.recipeId = null;
            this.iconStack = ItemStack.EMPTY;
            this.text = Component.empty();
        }

        public void setEntry(@NotNull ResourceLocation recipeId, @NotNull ItemStack iconStack, @NotNull Component text,
                int requiredPower, boolean active) {
            this.recipeId = recipeId;
            this.iconStack = iconStack.copy();
            this.text = text;
            this.requiredPower = requiredPower;
            this.active = active;
            this.visible = true;
            this.emptyLine = false;
            setTooltip(Tooltip.create(Component.translatable(
                    "gui.ae2_matter_condenser_recipe.condenser.required_power_tooltip",
                    text,
                    requiredPower)));
        }

        public void setEmpty(@NotNull Component text) {
            this.recipeId = null;
            this.iconStack = ItemStack.EMPTY;
            this.text = text;
            this.requiredPower = 0;
            this.active = false;
            this.visible = true;
            this.emptyLine = true;
            setTooltip(null);
        }

        public void setHidden() {
            this.recipeId = null;
            this.iconStack = ItemStack.EMPTY;
            this.text = Component.empty();
            this.requiredPower = 0;
            this.active = false;
            this.visible = false;
            this.emptyLine = false;
            setTooltip(null);
        }

        @Override
        public void onPress() {
            if (recipeId == null || emptyLine) {
                return;
            }
            menuBridge.ae2mcr$selectRecipe(recipeId);
            returnToParent();
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

            int iconX = getX() + 3;
            int iconY = getY() + 2;
            if (!emptyLine && recipeId != null && CondenserSelectionState.isTrash(recipeId)) {
                Icon.CONDENSER_OUTPUT_TRASH.getBlitter().dest(iconX, iconY).blit(guiGraphics);
            } else if (!emptyLine && !iconStack.isEmpty()) {
                guiGraphics.renderItem(iconStack, iconX, iconY);
            }

            int textX = emptyLine ? getX() + 6 : iconX + 19;
            int textY = getY() + (getHeight() - 8) / 2;
            int color = active ? (isHovered() ? 0xE6F3FF : 0xF2F2F2) : 0x8A8A8A;
            guiGraphics.drawString(Minecraft.getInstance().font, text, textX, textY, color, false);
        }
    }
}
