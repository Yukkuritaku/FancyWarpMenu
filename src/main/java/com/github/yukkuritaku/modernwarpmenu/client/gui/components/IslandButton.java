package com.github.yukkuritaku.modernwarpmenu.client.gui.components;

import com.github.yukkuritaku.modernwarpmenu.client.gui.screens.ModernWarpScreen;
import com.github.yukkuritaku.modernwarpmenu.client.gui.screens.grid.ScaledGrid;
import com.github.yukkuritaku.modernwarpmenu.client.gui.screens.transition.ScaleTransition;
import com.github.yukkuritaku.modernwarpmenu.data.layout.Island;
import com.github.yukkuritaku.modernwarpmenu.data.layout.Warp;
import com.github.yukkuritaku.modernwarpmenu.data.settings.SettingsManager;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

import java.awt.*;

public class IslandButton extends ScaleTransitionButton{

    private static final Color TEXT_GREEN_COLOR = new Color(5635925);
    private static final float HOVERED_SCALE = 1.1F;
    private static final long SCALE_TRANSITION_DURATION = 400;
    public final Island island;
    public final ScaledGrid scaledGrid;

    public IslandButton(ModernWarpScreen parent,
                        Window window,
                        Island island,
                        OnPress onPress,
                        CreateNarration createNarration) {
        super(island.gridX, island.gridY,

                // width, height is not initialized
                island.getWidth(), island.getHeight(),

                Component.literal(island.name),
                island.texture,
                island.hoverEffectTexture,
                onPress, createNarration);
        this.island = island;
        this.island.init(window);
        this.scaledXPosition = parent.getScaledGrid().getActualX(island.gridX);
        this.scaledYPosition = parent.getScaledGrid().getActualY(island.gridY);
        this.setZLevel(island.zLevel);
        this.width = island.getWidth();
        this.height = island.getHeight();
        this.scaledGrid = new ScaledGrid(this.scaledXPosition, this.scaledYPosition,
                this.width, this.height,
                Warp.GRID_UNIT_WIDTH_FACTOR, true);
        this.transition = new ScaleTransition(0, 1, 1);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.visible) {
            int originalZ = this.getZLevel();
            transitionStep(SCALE_TRANSITION_DURATION, HOVERED_SCALE);
            this.scaledGrid.setScaleFactor(this.transition.getCurrentScale());
            this.scaledXPosition = this.scaledGrid.getGridStartX();
            this.scaledYPosition = this.scaledGrid.getGridStartY();
            this.scaledWidth = this.scaledGrid.getScaledDimension(this.width);
            this.scaledHeight = this.scaledGrid.getScaledDimension(this.height);
            if (this.isHovered) {
                this.setZLevel(9);
            }
            renderButtonTexture(guiGraphics, this.backgroundTexture.location());
            if (this.isHovered) {
                renderForegroundLayer(guiGraphics, this.foregroundTexture.location());
            }
            if (SettingsManager.get().general.showIslandLabels) {
                renderMessageString(guiGraphics, this.scaledWidth / 2f, this.scaledHeight, TEXT_GREEN_COLOR);
            }
            if (SettingsManager.get().debug.debugModeEnabled && SettingsManager.get().debug.drawBorders) {
                renderBorder(guiGraphics, ARGB.white(1.0f));
            }
            this.setZLevel(originalZ);
        }
    }
}
