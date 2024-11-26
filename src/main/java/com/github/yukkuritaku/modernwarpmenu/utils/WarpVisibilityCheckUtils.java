package com.github.yukkuritaku.modernwarpmenu.utils;


import com.github.yukkuritaku.modernwarpmenu.data.layout.Island;
import com.github.yukkuritaku.modernwarpmenu.data.layout.Warp;
import com.github.yukkuritaku.modernwarpmenu.data.settings.SettingsManager;
import com.github.yukkuritaku.modernwarpmenu.state.GameState;

import java.util.List;

/**
 * Checks used to control the visibility of warps on the modern warp menu
 */
public class WarpVisibilityCheckUtils {

    /**
     * Checks if the given island with a singular warp should be shown on the fancy warp menu. Throws
     * {@link IllegalArgumentException} if the island has multiple warps.
     *
     * @param island the island to check
     * @return {@code true} if the island should be visible, {@code false} if it should be hidden
     */
    public static boolean shouldShowSingleWarpIsland(Island island) {
        if (island.warpList.size() > 1) {
            throw new IllegalArgumentException("Island has more than one warp");
        }

        return shouldShowWarp(island.warpList.getFirst());
    }

    /**
     * Checks if a warp should be shown on the fancy warp menu.
     *
     * @param warp the warp to check
     * @return {@code true} if the warp should be visible, {@code false} if it should be hidden
     */
    public static boolean shouldShowWarp(Warp warp) {
        List<String> warpTags = warp.tags();

        if (warpTags != null && !warpTags.isEmpty()) {
            for (String tag : warpTags) {
                if (!shouldShowWarpWithTag(tag)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks if warps with the given tag should be shown on the fancy warp menu
     *
     * @param tag the categorization tag to check
     * @return {@code true} if warps with this tag should be visible, {@code false} if they should be hidden
     */
    private static boolean shouldShowWarpWithTag(String tag) {
        switch (tag) {
            case "bingo":
                return !SettingsManager.get().general.hideUnobtainableWarps;
            case "jerry":
                if (SettingsManager.get().debug.debugModeEnabled && SettingsManager.get().debug.alwaysShowJerryIsland) {
                    return true;
                }

                if (!SettingsManager.get().general.showJerryIsland) {
                    return false;
                }

                String season = GameState.getSeason();
                String seasonStage = GameState.getSeasonStage();

                return season != null && seasonStage != null && season.equals("Winter") && seasonStage.equals("Late");
            default:
                return true;
        }
    }
}
