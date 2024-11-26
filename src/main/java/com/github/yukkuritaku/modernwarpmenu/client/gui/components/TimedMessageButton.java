package com.github.yukkuritaku.modernwarpmenu.client.gui.components;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;


public class TimedMessageButton extends Button {
    private Component originalMessage;
    private long timedMessageExpiryTime;

    public TimedMessageButton(int x, int y, Component message, OnPress onPress, CreateNarration createNarration) {
        super(x, y, 0, 0, message, onPress, createNarration);
    }
    protected TimedMessageButton(int x, int y, int width, int height, net.minecraft.network.chat.Component message, OnPress onPress, CreateNarration createNarration) {
        super(x, y, width, height, message, onPress, createNarration);
    }


    /**
     * Set a temporary label text for this button that will revert to the original label text after the given
     * time has elapsed.
     *
     * @param message the temporary label text
     * @param time the time in milliseconds this label text should be shown for
     */
    public void setTimedMessage(Component message, int time) {
        this.timedMessageExpiryTime = Util.getMillis() + time;
        this.originalMessage = this.getMessage();
        this.setMessage(message);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        if (this.timedMessageExpiryTime > 0 && Util.getMillis() > this.timedMessageExpiryTime) {
            this.timedMessageExpiryTime = -1;
            this.setMessage(this.originalMessage);
        }
    }
}
