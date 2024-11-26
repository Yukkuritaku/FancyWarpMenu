package com.github.yukkuritaku.modernwarpmenu.data.settings.categories;

import com.github.yukkuritaku.modernwarpmenu.data.settings.Settings;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.network.chat.Component;

public class DebugCategory {


    public static ConfigCategory create(Settings defaults, Settings config) {
        return ConfigCategory.createBuilder()
                .name(Component.translatable("modernwarpmenu.config.categories.debug"))
                .tooltip(Component.translatable("modernwarpmenu.config.categories.debug.tooltip"))
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("modernwarpmenu.config.debug.debugModeEnabled"))
                        .description(OptionDescription.of(Component.translatable("modernwarpmenu.config.debug.debugModeEnabled.tooltip")))
                        .binding(defaults.debug.debugModeEnabled,
                                () -> config.debug.debugModeEnabled,
                                value -> config.debug.debugModeEnabled = value)
                        .controller(option -> BooleanControllerBuilder.create(option).yesNoFormatter().coloured(true))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("modernwarpmenu.config.debug.showDebugOverlay"))
                        .description(OptionDescription.of(Component.translatable("modernwarpmenu.config.debug.debugModeEnabled.tooltip")))
                        .binding(defaults.debug.showDebugOverlay,
                                () -> config.debug.showDebugOverlay,
                                value -> config.debug.showDebugOverlay = value)
                        .controller(option -> BooleanControllerBuilder.create(option).yesNoFormatter().coloured(true))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("modernwarpmenu.config.debug.drawBorders"))
                        .description(OptionDescription.of(Component.translatable("modernwarpmenu.config.debug.debugModeEnabled.tooltip")))
                        .binding(defaults.debug.drawBorders,
                                () -> config.debug.drawBorders,
                                value -> config.debug.drawBorders = value)
                        .controller(option -> BooleanControllerBuilder.create(option).yesNoFormatter().coloured(true))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("modernwarpmenu.config.debug.skipSkyBlockCheck"))
                        .description(OptionDescription.of(Component.translatable("modernwarpmenu.config.debug.debugModeEnabled.tooltip")))
                        .binding(defaults.debug.skipSkyBlockCheck,
                                () -> config.debug.skipSkyBlockCheck,
                                value -> config.debug.skipSkyBlockCheck = value)
                        .controller(option -> BooleanControllerBuilder.create(option).yesNoFormatter().coloured(true))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("modernwarpmenu.config.debug.alwaysShowJerryIsland"))
                        .description(OptionDescription.of(Component.translatable("modernwarpmenu.config.debug.debugModeEnabled.tooltip")))
                        .binding(defaults.debug.alwaysShowJerryIsland,
                                () -> config.debug.alwaysShowJerryIsland,
                                value -> config.debug.alwaysShowJerryIsland = value)
                        .controller(option -> BooleanControllerBuilder.create(option).yesNoFormatter().coloured(true))
                        .build())
                .build();
    }
}
