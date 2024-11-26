package com.github.yukkuritaku.modernwarpmenu.listeners;

import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;

import java.util.function.Consumer;

public class InventoryChangeListener implements ContainerListener {

    private final Consumer<Container> callback;

    public InventoryChangeListener(Consumer<Container> callback){
        this.callback = callback;
    }
    @Override
    public void containerChanged(Container container) {
        this.callback.accept(container);
    }
}
