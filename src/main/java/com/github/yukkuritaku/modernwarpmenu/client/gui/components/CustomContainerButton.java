package com.github.yukkuritaku.modernwarpmenu.client.gui.components;

import com.github.yukkuritaku.modernwarpmenu.data.layout.texture.LayoutTexture;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public abstract class CustomContainerButton extends Button {

    public final LayoutTexture backgroundTexture;
    public final LayoutTexture foregroundTexture;
    private int zLevel = 0;

    public CustomContainerButton(int x, int y,
                                 int width, int height,
                                 Component message,
                                 LayoutTexture backgroundTexture,
                                 LayoutTexture foregroundTexture,

                                 OnPress onPress,
                                 CreateNarration createNarration) {
        super(x, y, width, height, message, onPress, createNarration);
        this.backgroundTexture = backgroundTexture;
        this.foregroundTexture = foregroundTexture;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setHovered(boolean hovered){
        this.isHovered = hovered;
    }

    public void setZLevel(int zLevel) {
        this.zLevel = zLevel;
    }

    public int getZLevel() {
        return this.zLevel;
    }

    /**
     * Disable the vanilla button rendering
     */
    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}
}
