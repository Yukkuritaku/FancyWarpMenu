package com.github.yukkuritaku.modernwarpmenu.client.gui.components;

import com.github.yukkuritaku.modernwarpmenu.client.gui.screens.grid.GridRectangle;
import com.github.yukkuritaku.modernwarpmenu.client.gui.screens.grid.ScaledGrid;
import com.github.yukkuritaku.modernwarpmenu.client.gui.screens.transition.ScaleTransition;
import com.github.yukkuritaku.modernwarpmenu.data.layout.Button;
import com.github.yukkuritaku.modernwarpmenu.data.layout.Layout;
import com.github.yukkuritaku.modernwarpmenu.data.layout.texture.LayoutTexture;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class RegularWarpMenuButton extends ScaleTransitionButton{

    private static final float HOVERED_SCALE = 1.2F;
    private static final long SCALE_TRANSITION_DURATION = 500;
    // Far right edge
    public final int gridX;
    // Bottom edge
    public final int gridY;

    public RegularWarpMenuButton(Layout layout,
                                 Window window,
                                 ScaledGrid grid,
                                 OnPress onPress,
                                 CreateNarration createNarration) {
        super(layout.regularWarpMenuButton().gridX, layout.regularWarpMenuButton().gridY,
                // width, height is not initialized
                layout.regularWarpMenuButton().getWidth(), layout.regularWarpMenuButton().getHeight(),
                Component.translatable("modernwarpmenu.gui.buttons.regularWarpMenu").withStyle(ChatFormatting.GREEN),
                layout.regularWarpMenuButton().texture, null,
                onPress, createNarration);
        Button regularWarpMenu = layout.regularWarpMenuButton();
        regularWarpMenu.init(window);
        this.gridX = regularWarpMenu.gridX;
        this.gridY = regularWarpMenu.gridY;
        this.width = regularWarpMenu.getWidth();
        this.height = regularWarpMenu.getHeight();
        this.setZLevel(20);
        this.buttonRectangle = new GridRectangle(grid, this.gridX, this.gridY,
                this.width, this.height, false, true);
        grid.addRectangle("regularWarpMenuButton", this.buttonRectangle);
        this.transition = new ScaleTransition(0, 1, 1);
        this.setMessage(Component.literal(
                String.join("\n", formattedToStr(Minecraft.getInstance().font.split(this.getMessage(), this.width * 3)))
        ));
    }

    private List<String> formattedToStr(List<FormattedCharSequence> charSequences){
        List<String> stringList = new ArrayList<>();
        for (FormattedCharSequence charSequence : charSequences){
            StringBuilder builder = new StringBuilder();
            charSequence.accept((x, style, character) -> {
                builder.append(Character.toChars(character));
                return true;
            });
            stringList.add(builder.toString());
        }
        return stringList;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.visible) {
            calculateHoverState(mouseX, mouseY);
            transitionStep(SCALE_TRANSITION_DURATION, HOVERED_SCALE);
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
}
