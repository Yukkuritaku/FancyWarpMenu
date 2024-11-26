package com.github.yukkuritaku.modernwarpmenu.state;


import com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants.menu.Menu;
import com.github.yukkuritaku.modernwarpmenu.data.settings.SettingsManager;

public class GameState {

    /**
     * Whether the player is currently on SkyBlock
     */
    private static boolean onSkyBlock;
    /**
     * The current stage of the in-game season, can be "Early", mid (null), or "Late".
     */
    private static String seasonStage;
    /**
     * The current in-game season
     */
    private static String season;
    /**
     * Current in-game menu the player has open
     */
    private static Menu currentMenu;

    public static boolean isOnSkyBlock() {
        return onSkyBlock || SettingsManager.get().debug.skipSkyBlockCheck;
    }

    public static void setOnSkyBlock(boolean onSkyBlock) {
        GameState.onSkyBlock = onSkyBlock;
    }

    public static String getSeasonStage() {
        return seasonStage;
    }

    public static void setSeasonStage(String seasonStage) {
        GameState.seasonStage = seasonStage;
    }

    public static String getSeason() {
        return season;
    }

    public static void setSeason(String season) {
        GameState.season = season;
    }

    public static Menu getCurrentMenu() {
        return currentMenu;
    }

    public static void setCurrentMenu(Menu currentMenu) {
        GameState.currentMenu = currentMenu;
    }
}
