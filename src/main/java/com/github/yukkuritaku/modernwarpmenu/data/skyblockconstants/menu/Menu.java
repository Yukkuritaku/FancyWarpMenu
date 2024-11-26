package com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants.menu;

import net.minecraft.util.StringRepresentable;

/**
 * In-game menus, not serialized
 */
public enum Menu implements StringRepresentable {

    /** Value used when player is not in a menu or in an unknown or irrelevant menu */
    NONE("none", ""),
    SKYBLOCK_MENU("skyblock_menu", "SkyBlock Menu"),
    FAST_TRAVEL("fast_travel", "Fast Travel"),
    PORHTAL("porhtal", "Porthal");

    public static final StringRepresentable.EnumCodec<Menu> CODEC = StringRepresentable.fromEnum(Menu::values);

    private final String serializedName;
    private final String displayName;

    Menu(String serializedName, String displayName){
        this.serializedName = serializedName;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }
}
