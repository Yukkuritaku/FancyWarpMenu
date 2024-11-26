package com.github.yukkuritaku.modernwarpmenu.compat.modmenu;

import com.github.yukkuritaku.modernwarpmenu.data.settings.SettingsManager;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuApiImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SettingsManager::createSettingsScreen;
    }

}
