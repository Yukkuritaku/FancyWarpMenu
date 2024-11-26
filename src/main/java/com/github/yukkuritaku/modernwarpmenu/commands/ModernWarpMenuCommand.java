package com.github.yukkuritaku.modernwarpmenu.commands;

import com.github.yukkuritaku.modernwarpmenu.ModernWarpMenu;
import com.github.yukkuritaku.modernwarpmenu.data.settings.SettingsManager;
import com.github.yukkuritaku.modernwarpmenu.state.ModernWarpMenuState;
import com.github.yukkuritaku.modernwarpmenu.utils.ChatUtils;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ModernWarpMenuCommand {

    public static void registerCommands(){
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, commandBuildContext) ->
                        dispatcher.register(ClientCommandManager.literal(ModernWarpMenu.MOD_ID)
                                .executes(ctx -> {
                                    ModernWarpMenuState.setOpenConfigMenuRequested(true);
                                    return 0;
                                }).then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                        .executes(ctx -> {
                                            SettingsManager.get().general.warpMenuEnabled = BoolArgumentType.getBool(ctx, "enabled");
                                            SettingsManager.save();
                                            if (SettingsManager.get().general.warpMenuEnabled) {
                                                ChatUtils.sendMessageWithModNamePrefix(Component.translatable(ModernWarpMenu.getFullLanguageKey("messages.modernWarpMenuEnabled"))
                                                        .withStyle(ChatFormatting.GREEN));
                                            }else {
                                                ChatUtils.sendMessageWithModNamePrefix(Component.translatable(ModernWarpMenu.getFullLanguageKey("messages.modernWarpMenuDisabled"))
                                                        .withStyle(ChatFormatting.RED));
                                            }
                                            return 0;
                                        })
                                )
                        )
        );
    }
}
