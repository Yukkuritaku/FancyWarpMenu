package com.github.yukkuritaku.modernwarpmenu.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class InputEvents {

    public static final Event<KeyPressed> KEY_PRESSED = EventFactory.createArrayBacked(KeyPressed.class, callbacks -> (key, scanCode, action, modifiers) -> {
        for (KeyPressed keyPressed : callbacks){
            keyPressed.onKeyPressed(key, scanCode, action, modifiers);
        }
    });

    @FunctionalInterface
    public interface KeyPressed {
        void onKeyPressed(int key, int scanCode, int action, int modifiers);
    }
}