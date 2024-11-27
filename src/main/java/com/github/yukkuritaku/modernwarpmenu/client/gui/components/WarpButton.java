package com.github.yukkuritaku.modernwarpmenu.client.gui.components;

import com.github.yukkuritaku.modernwarpmenu.client.gui.screens.grid.GridRectangle;
import com.github.yukkuritaku.modernwarpmenu.client.gui.screens.transition.ScaleTransition;
import com.github.yukkuritaku.modernwarpmenu.data.layout.Island;
import com.github.yukkuritaku.modernwarpmenu.data.layout.Warp;
import com.github.yukkuritaku.modernwarpmenu.data.settings.SettingsManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class WarpButton extends ScaleTransitionButton{
    /** The button of the island this warp belongs to */
    private final IslandButton parent;
    private final Warp warp;
    /** Whether the warp label should be drawn under the button */
    private boolean drawWarpLabel;

    public WarpButton(IslandButton parent, Warp warp,
                      OnPress onPress, CreateNarration createNarration) {
        super(warp.gridX(), warp.gridY(),
                Warp.getWidth(), Warp.getHeight(),
                Component.literal(warp.displayName()),
                warp.getWarpTextureLocation(),
                warp.getWarpHoverEffectTextureLocation(),
                onPress, createNarration);
        this.parent = parent;
        this.warp = warp;
        this.buttonRectangle = new GridRectangle(parent.scaledGrid,
                warp.gridX(), warp.gridY(),
                Warp.getWidth(), Warp.getHeight(),
                true, false);
        parent.scaledGrid.addRectangle(warp.displayName(), this.buttonRectangle);
        this.setZLevel(10);
        this.drawWarpLabel = true;
        this.transition = new ScaleTransition(0, 0, 0);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.visible) {
            int originalZ = this.getZLevel();
            calculateHoverState(mouseX, mouseY);
            this.transition.setCurrentScale(this.parent.scaledGrid.getScaleFactor());
            if (this.isHovered) {
                this.setZLevel(19);
            }
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            if (this.isHovered){
                renderForegroundLayer(guiGraphics, this.foregroundTexture.location());
            }
            if (this.drawWarpLabel && (!SettingsManager.get().general.hideWarpLabelsUntilIslandHovered ||
                    this.parent.isHoveredOrFocused())) {
                renderMessageString(guiGraphics, this.buttonRectangle.getWidth() / 2F, this.buttonRectangle.getHeight(), Color.WHITE);
            }
            this.setZLevel(originalZ);
        }
    }

    public String getWarpCommand() {
        return this.warp.getWarpCommand();
    }

    public int getWarpSlotIndex() {
        return this.warp.slotIndex();
    }

    public Island getIsland() {
        return this.parent.island;
    }

    public Warp getWarp() {
        return this.warp;
    }

    public void setDrawWarpLabel(boolean drawWarpLabel) {
        this.drawWarpLabel = drawWarpLabel;
    }
}
