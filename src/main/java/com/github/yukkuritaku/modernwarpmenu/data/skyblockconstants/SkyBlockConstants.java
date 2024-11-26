package com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants;

import com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants.menu.ItemMatchCondition;
import com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants.menu.Menu;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Map;

public record SkyBlockConstants(Map<Menu, List<ItemMatchCondition>> menuMatchingMap,
                                WarpMessages warpMessages,
                                List<WarpCommandVariant> warpCommandVariants,
                                String skyBlockJoinMessage
                                ) {

    private static final Codec<Map<Menu, List<ItemMatchCondition>>> MENU_MATCHING_MAP_CODEC = Codec.unboundedMap(Menu.CODEC, ItemMatchCondition.CODEC.codec().listOf());
    public static final MapCodec<SkyBlockConstants> CODEC = RecordCodecBuilder.<SkyBlockConstants>mapCodec(instance ->
            instance.group(
                    MENU_MATCHING_MAP_CODEC.fieldOf("menu_matching").forGetter(SkyBlockConstants::menuMatchingMap),
                    WarpMessages.CODEC.fieldOf("warp_messages").forGetter(SkyBlockConstants::warpMessages),
                    WarpCommandVariant.CODEC.codec().listOf().fieldOf("warp_command_variants").forGetter(SkyBlockConstants::warpCommandVariants),
                    Codec.STRING.optionalFieldOf("skyblock_join_message", null).forGetter(SkyBlockConstants::skyBlockJoinMessage)
            ).apply(instance, SkyBlockConstants::new)).validate(SkyBlockConstants::validate);

    public static final String WARP_COMMAND_BASE = "/warp";

    private static DataResult<SkyBlockConstants> validate(SkyBlockConstants constants){
        if (constants.warpCommandVariants == null || constants.warpCommandVariants.isEmpty()) {
            return DataResult.error(() -> "Warp command variant list cannot be empty");
        }
        return DataResult.success(constants);
    }

    /**
     * Returns the inventory slot index of the last {@link ItemMatchCondition} for the given {@link Menu}.
     *
     * @param menu the {@code Menu} to get the inventory slot index from
     * @return the inventory slot index of the last {@code ItemMatchCondition} for the given {@code Menu}
     */
    public int getLastMatchConditionInventorySlotIndex(Menu menu) {
        List<ItemMatchCondition> matchConditions = menuMatchingMap.get(menu);
        return matchConditions.getLast().inventorySlot();
    }
}
