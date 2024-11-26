package com.github.yukkuritaku.modernwarpmenu.client.gui.components;

import com.github.yukkuritaku.modernwarpmenu.client.gui.screens.grid.GridRectangle;
import com.github.yukkuritaku.modernwarpmenu.client.gui.screens.transition.ScaleTransition;
import com.github.yukkuritaku.modernwarpmenu.data.layout.texture.LayoutTexture;
import com.github.yukkuritaku.modernwarpmenu.data.settings.SettingsManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.joml.Matrix4f;

import java.awt.*;

public class ScaleTransitionButton extends CustomContainerButton{
    private static final float HOVERED_BRIGHTNESS = 1f;
    private static final float UN_HOVERED_BRIGHTNESS = 0.9F;

    /** This rectangle determines the button's placement on its {@code GuiScreen}'s {@code ScaledGrid} */
    protected GridRectangle buttonRectangle;
    protected ScaleTransition transition;
    protected float scaledXPosition;
    protected float scaledYPosition;
    protected float scaledWidth;
    protected float scaledHeight;

    public ScaleTransitionButton(int x, int y, int width, int height, Component message, LayoutTexture backgroundTexture, LayoutTexture foregroundTexture, OnPress onPress, CreateNarration createNarration) {
        super(x, y, width, height, message, backgroundTexture, foregroundTexture, onPress, createNarration);
    }

    /**
     * Button hover calculations adapted for float values instead of int
     */
    public void calculateHoverState(int mouseX, int mouseY) {
        this.isHovered =
                        mouseX >= this.scaledXPosition &&
                        mouseY >= this.scaledYPosition &&
                        mouseX < this.scaledXPosition + this.scaledWidth &&
                        mouseY < this.scaledYPosition + this.scaledHeight;
    }

    /**
     * Recalculates the progress of {@code transition} towards its end time and reverses the direction of transition if
     * this button's hover state changes
     *
     * @param scaleTransitionDuration duration from transition start to finish
     * @param hoveredScale final scale when the transition when the button is hovered is finished
     */
    public void transitionStep(long scaleTransitionDuration, float hoveredScale) {
        this.transition.step();
        if (this.isHovered) {
            if (this.transition.getEndScale() == 1) {
                this.transition = new ScaleTransition((long) (this.transition.getProgress() * scaleTransitionDuration), this.transition.getCurrentScale(), hoveredScale);
            }
        } else {
            if (this.transition.getEndScale() == hoveredScale) {
                this.transition = new ScaleTransition((long) (this.transition.getProgress() * scaleTransitionDuration), this.transition.getCurrentScale(), 1);
            }
        }
    }

    /**
     * Draw a border around this button with the given color. This stutters due to using int instead of float.
     *
     * @param color color of the border
     */
    public void renderBorder(GuiGraphics guiGraphics, int color){
        PoseStack stack = guiGraphics.pose();
        stack.pushPose();
        stack.translate(0, 0, this.getZLevel());
        guiGraphics.hLine((int) this.scaledXPosition, (int) (this.scaledXPosition + this.scaledWidth), (int) this.scaledYPosition, color);
        guiGraphics.vLine((int) this.scaledXPosition, (int) this.scaledYPosition, (int) (this.scaledYPosition + this.scaledHeight), color);
        guiGraphics.hLine((int) this.scaledXPosition, (int) (this.scaledXPosition + this.scaledWidth), (int) (this.scaledYPosition + this.scaledHeight), color);
        guiGraphics.vLine((int) (this.scaledXPosition + this.scaledWidth), (int) this.scaledYPosition, (int) (this.scaledYPosition + this.scaledHeight), color);
        stack.popPose();
    }
    /**
     * Draws the provided texture at ({@code this.scaledXPosition}, {@code this.scaledYPosition}, {@code this.zLevel}) at a size of ({@code this.scaledWidth})x({@code this.scaledHeight})
     *
     * @param texture location of texture to draw
     */
    protected void renderButtonTexture(GuiGraphics guiGraphics, ResourceLocation texture) {
        PoseStack stack = guiGraphics.pose();
        Matrix4f pose = stack.last().pose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int color;
        if (this.isHovered){
            color = new Color(HOVERED_BRIGHTNESS, HOVERED_BRIGHTNESS, HOVERED_BRIGHTNESS, 1f).getRGB();
        }else {
            color = new Color(UN_HOVERED_BRIGHTNESS, UN_HOVERED_BRIGHTNESS, UN_HOVERED_BRIGHTNESS, 1f).getRGB();
        }
        VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.guiTextured(texture));
        consumer.addVertex(pose, this.scaledXPosition, this.scaledYPosition + this.scaledHeight, this.getZLevel()).setUv(0, 1).setColor(color);
        consumer.addVertex(pose, this.scaledXPosition + this.scaledWidth, this.scaledYPosition + this.scaledHeight, this.getZLevel()).setUv(1, 1).setColor(color);
        consumer.addVertex(pose, this.scaledXPosition + this.scaledWidth, this.scaledYPosition, this.getZLevel()).setUv(1, 0).setColor(color);
        consumer.addVertex(pose, this.scaledXPosition, this.scaledYPosition, this.getZLevel()).setUv(0, 0).setColor(color);
        RenderSystem.disableBlend();
    }


    /**
     * Draws the display string for this button aligned to centre. The centre of the string is given by {@code xOffset}
     * and {@code yOffset} relative to its top-left corner. The offsets should be pre-scaled. This method does not scale
     * the offsets.
     *
     * @param xOffset x-offset from button left
     * @param yOffset y-offset from button top
     */
    public void renderMessageString(GuiGraphics guiGraphics, float xOffset, float yOffset) {
        PoseStack stack = guiGraphics.pose();
        int color;
        if (this.isHovered){
            color = ARGB.colorFromFloat(HOVERED_BRIGHTNESS, HOVERED_BRIGHTNESS, HOVERED_BRIGHTNESS, 1.0f);
        }else {
            color = ARGB.colorFromFloat(UN_HOVERED_BRIGHTNESS, UN_HOVERED_BRIGHTNESS, UN_HOVERED_BRIGHTNESS, 1.0f);
        }
        stack.pushPose();
        stack.translate(this.scaledXPosition + xOffset, this.scaledYPosition + yOffset, this.getZLevel() + 1);
        stack.scale(this.transition.getCurrentScale(), this.transition.getCurrentScale(), 1);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, this.getMessage(), 0, Minecraft.getInstance().font.lineHeight, color);
        stack.popPose();
    }

    protected void renderForegroundLayer(GuiGraphics guiGraphics, ResourceLocation foregroundTexture){
        if (foregroundTexture != null)
            renderButtonTexture(guiGraphics, foregroundTexture);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.visible){
            this.buttonRectangle.scale(this.transition.getCurrentScale());
            this.scaledXPosition = this.buttonRectangle.getXPosition();
            this.scaledYPosition = this.buttonRectangle.getYPosition();
            this.scaledWidth = this.buttonRectangle.getWidth();
            this.scaledHeight = this.buttonRectangle.getHeight();
            renderButtonTexture(guiGraphics, this.backgroundTexture.location());
            if (SettingsManager.get().debug.debugModeEnabled && SettingsManager.get().debug.drawBorders) {
                renderBorder(guiGraphics, ARGB.white(1.0f));
            }
        }
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return this.active && this.visible &&
                mouseX >= this.scaledXPosition &&
                mouseY >= this.scaledYPosition &&
                mouseX < this.scaledXPosition + this.scaledWidth &&
                mouseY < this.scaledYPosition + this.scaledHeight;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible &&
                mouseX >= this.scaledXPosition &&
                mouseY >= this.scaledYPosition &&
                mouseX < this.scaledXPosition + this.scaledWidth &&
                mouseY < this.scaledYPosition + this.scaledHeight;
    }

    @Override
    public int getX() {
        return (int) this.scaledXPosition;
    }

    @Override
    public void setX(int x) {
        this.scaledXPosition = x;
    }

    @Override
    public int getY() {
        return (int) this.scaledYPosition;
    }
    @Override
    public void setY(int y) {
        this.scaledYPosition = y;
    }

    @Override
    public int getWidth() {
        return (int) this.scaledWidth;
    }
    @Override
    public void setWidth(int width) {
        this.scaledWidth = width;
    }
    @Override
    public int getHeight() {
        return (int) this.scaledHeight;
    }

    @Override
    public void setHeight(int height) {
        this.scaledHeight = height;
    }

    @Override
    public void setSize(int width, int height) {
        this.scaledWidth = width;
        this.scaledHeight = height;
    }

    @Override
    public int getRight() {
        return this.getX() + this.getWidth();
    }

    @Override
    public int getBottom() {
        return this.getY() + this.getHeight();
    }
}
