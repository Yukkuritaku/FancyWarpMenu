package com.github.yukkuritaku.modernwarpmenu.compat.modmenu;

import com.github.yukkuritaku.modernwarpmenu.ModernWarpMenu;
import com.terraformersmc.modmenu.ModMenu;

public class UpdateDetection {

    public boolean hasUpdateAvailable(){
        return ModMenu.MODS.get(ModernWarpMenu.MOD_ID).hasUpdate();
    }
}
