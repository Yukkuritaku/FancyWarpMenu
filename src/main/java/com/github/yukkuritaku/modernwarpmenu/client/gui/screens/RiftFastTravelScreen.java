package com.github.yukkuritaku.modernwarpmenu.client.gui.screens;

import com.github.yukkuritaku.modernwarpmenu.ModernWarpMenu;
import com.github.yukkuritaku.modernwarpmenu.client.gui.components.IslandButton;
import com.github.yukkuritaku.modernwarpmenu.client.gui.components.WarpButton;
import com.github.yukkuritaku.modernwarpmenu.data.layout.Layout;
import com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants.menu.Menu;
import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import org.slf4j.Logger;

public class RiftFastTravelScreen extends ModernWarpScreen{
    private static final Logger LOGGER = LogUtils.getLogger();
    public RiftFastTravelScreen(ChestMenu menu, Inventory playerInventory, Layout layout) {
        super(Menu.PORHTAL, menu, playerInventory, layout);
        this.lastSlotIndexToCheck = ModernWarpMenu.getInstance().getSkyBlockConstantsManager()
                .getSkyBlockConstants().getLastMatchConditionInventorySlotIndex(this.warpMenu);
    }

    @Override
    protected void warpButtonHandler(WarpButton button) {
        if (Util.getMillis() > this.warpFailCoolDownExpiryTime) {
            // Don't click twice for islands with only one warp
            if (button.getIsland().warpList.size() > 1) {
                clickSlot(button.getWarpSlotIndex());
            }
        }
    }

    @Override
    protected void islandButtonHandler(IslandButton button) {
        if (Util.getMillis() > this.warpFailCoolDownExpiryTime) {
            if (button.island.warpList.size() == 1) {
                clickSlot(button.island.warpList.getFirst().slotIndex());
            }
        }
    }
}
