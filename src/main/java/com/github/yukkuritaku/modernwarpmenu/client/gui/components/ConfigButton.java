package com.github.yukkuritaku.modernwarpmenu.client.gui.components;

import com.github.yukkuritaku.modernwarpmenu.ModernWarpMenu;
import com.github.yukkuritaku.modernwarpmenu.client.gui.screens.grid.GridRectangle;
import com.github.yukkuritaku.modernwarpmenu.client.gui.screens.grid.ScaledGrid;
import com.github.yukkuritaku.modernwarpmenu.client.gui.screens.transition.ScaleTransition;
import com.github.yukkuritaku.modernwarpmenu.data.layout.Button;
import com.github.yukkuritaku.modernwarpmenu.data.layout.Island;
import com.github.yukkuritaku.modernwarpmenu.data.layout.Layout;
import com.github.yukkuritaku.modernwarpmenu.data.settings.SettingsManager;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ConfigButton extends ScaleTransitionButton{

    private static final float HOVERED_SCALE = 1.2F;
    private static final long SCALE_TRANSITION_DURATION = 500;

    /** This button uses its own grid instead of the grid of the GuiScreen it belongs to since it's also attached to vanilla screens, which don't have grids */
    public final ScaledGrid scaledGrid;
    // Far right edge
    public final int gridX;
    // Bottom edge
    public final int gridY;

    public ConfigButton(Layout layout, Window window, OnPress onPress, CreateNarration createNarration) {
        super(layout.configButton().gridX, layout.configButton().gridY,
                // width, height is not initialized
                layout.configButton().getWidth(), layout.configButton().getHeight(),
                Component.translatable("modernwarpmenu.gui.buttons.config").withStyle(ChatFormatting.GREEN),
                layout.configButton().texture, null, onPress, createNarration);
        Button configLayoutButton = layout.configButton();
        configLayoutButton.init(window);
        this.scaledGrid = new ScaledGrid(0, 0, window.getGuiScaledWidth(), window.getGuiScaledHeight(), Island.GRID_UNIT_HEIGHT_FACTOR, Island.GRID_UNIT_WIDTH_FACTOR, false);
        this.gridX = configLayoutButton.gridX;
        this.gridY = configLayoutButton.gridY;
        this.width = configLayoutButton.getWidth();
        this.height = configLayoutButton.getHeight();
        // Above islands and warps
        this.setZLevel(20);
        this.buttonRectangle = new GridRectangle(this.scaledGrid, this.gridX, this.gridY, this.width, this.height, false, true);
        this.scaledGrid.addRectangle("configButton", this.buttonRectangle);
        this.transition = new ScaleTransition(0, 1, 1);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.visible){
            calculateHoverState(mouseX, mouseY);
            transitionStep(SCALE_TRANSITION_DURATION, HOVERED_SCALE);
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            if (SettingsManager.get().general.enableUpdateNotification && ModernWarpMenu.updateAvailable()) {
                renderForegroundLayer(guiGraphics, Button.NOTIFICATION);
            }
        }
    }
}
