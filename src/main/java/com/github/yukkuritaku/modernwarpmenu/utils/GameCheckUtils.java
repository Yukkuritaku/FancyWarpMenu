/*
 * Copyright (c) 2024. TirelessTraveler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.yukkuritaku.modernwarpmenu.utils;

import com.github.yukkuritaku.modernwarpmenu.ModernWarpMenu;
import com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants.menu.ItemMatchCondition;
import com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants.menu.Menu;
import com.github.yukkuritaku.modernwarpmenu.data.settings.SettingsManager;
import com.github.yukkuritaku.modernwarpmenu.state.GameState;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.scores.*;
import org.slf4j.Logger;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameCheckUtils {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Matcher seasonMatcher =
            Pattern.compile("(?<seasonStage>Late|Early)? ?(?<season>[a-zA-Z]+) \\d{1,2}.*").matcher("");

    /**
     * Checks the current SkyBlock season and saves using {@link GameState#setSeason(String)}.
     */
    public static void checkSeason() {
        // Don't run outside of SB to prevent exceptions
        if (!SettingsManager.get().debug.skipSkyBlockCheck) {
            try {
                if (Minecraft.getInstance().level != null) {
                    Scoreboard sb = Minecraft.getInstance().level.getScoreboard();
                    // SkyBlock sidebar objective
                    Objective scores = sb.getDisplayObjective(DisplaySlot.SIDEBAR);

                    // The date is always near the top (highest score) so we iterate backwards.
                    for (ScoreHolder holder : sb.getTrackedPlayers()) {
                        PlayerTeam team = sb.getPlayersTeam(holder.getScoreboardName());
                        if (team != null) {
                            String scoreboardLine = team.getName().trim();
                            seasonMatcher.reset(scoreboardLine);
                            if (seasonMatcher.matches()) {
                                String seasonStage = seasonMatcher.group("seasonStage");
                                String season = seasonMatcher.group("season");
                                LOGGER.info("Season: {}, SeasonStage: {}", season, seasonStage);
                                GameState.setSeasonStage(seasonStage);
                                if (season != null) {
                                    GameState.setSeason(season);
                                    return;
                                }
                            }
                        }
                    }
                }
                GameState.setSeasonStage(null);
                GameState.setSeason(null);
            } catch (RuntimeException e) {
                LOGGER.warn("Failed to check scoreboard season", e);
            }
        }
    }

    /**
     * Determines which SkyBlock {@code ChestMenu} menu the player is in using the {@link net.minecraft.world.inventory.ChestMenu}
     * display name. This is used for initial checks when the items haven't loaded in yet.
     *
     * @param component the inventory of the chest holding the menu
     * @return a {@code Menu} value representing the current menu the player has open
     */
    public static Menu determineOpenMenu(Component component) {
        String chestTitle = component.getString();

        for (Menu menu : ModernWarpMenu.getInstance().getSkyBlockConstantsManager().getSkyBlockConstants().menuMatchingMap().keySet()) {
            if (chestTitle.equals(menu.getDisplayName())) {
                return menu;
            }
        }

        return Menu.NONE;
    }

    /**
     * Determines if the player is in the given menu by checking whether all the {@link ItemMatchCondition}s for that menu
     * match the given inventory. This should be used after the inventory has loaded the slot index returned by
     * {@link com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants.SkyBlockConstants#getLastMatchConditionInventorySlotIndex(Menu)}.
     * If a match is found, the matched menu is saved using {@link GameState#setCurrentMenu(Menu)}
     *
     * @param menu           the {@code Menu} whose match conditions will be checked
     * @param chestContainer the inventory to check against the match conditions
     * @return {@code true} if all the {@link ItemMatchCondition}s match, {@code false} otherwise
     */
    public static boolean menuItemsMatch(Menu menu, Container chestContainer) {
        List<ItemMatchCondition> matchConditions = ModernWarpMenu.getInstance().getSkyBlockConstantsManager()
                .getSkyBlockConstants().menuMatchingMap().get(menu);

        for (ItemMatchCondition matchCondition : matchConditions) {
            LOGGER.debug("Starting item match on slot {} for menu {}.",
                    matchCondition.inventorySlot(), menu);

            if (!matchCondition.inventoryContainsMatchingItem(chestContainer)) {
                LOGGER.warn("Item match on slot {} failed.", matchCondition.inventorySlot());
                GameState.setCurrentMenu(Menu.NONE);
                return false;
            }
            LOGGER.debug("Finished item match on slot {} for menu {}.",
                    matchCondition.inventorySlot(), menu);
        }

        GameState.setCurrentMenu(menu);
        return true;
    }
}
