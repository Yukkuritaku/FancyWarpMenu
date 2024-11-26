package com.github.yukkuritaku.modernwarpmenu.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;

import java.util.List;

public class ScreenButtonMouseEvents {

    public static final Event<BeforeMouseClick> PRE = EventFactory.createArrayBacked(BeforeMouseClick.class, callbacks ->
            (screen, button, children) -> {
                for (BeforeMouseClick pre : callbacks) {
                    if (!pre.beforeMouseClick(screen, button, children)){
                        return false;
                    }
                }
                return true;
            });
    public static final Event<AfterMouseClick> POST = EventFactory.createArrayBacked(AfterMouseClick.class, callbacks ->
            (screen, button, children) -> {
                for (AfterMouseClick post : callbacks) {
                    post.afterMouseClick(screen, button, children);
                }
            });



    @FunctionalInterface
    public interface BeforeMouseClick {
        boolean beforeMouseClick(Screen screen, Button button, List<? extends GuiEventListener> children);
    }

    @FunctionalInterface
    public interface AfterMouseClick {
        void afterMouseClick(Screen screen, Button button, List<? extends GuiEventListener> children);
    }


}
