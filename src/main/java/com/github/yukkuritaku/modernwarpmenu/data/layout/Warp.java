package com.github.yukkuritaku.modernwarpmenu.data.layout;

import com.github.yukkuritaku.modernwarpmenu.data.layout.texture.LayoutTexture;
import com.mojang.blaze3d.platform.Window;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringUtil;

import java.util.List;
import java.util.regex.Pattern;

public record Warp(int gridX,
                   int gridY,
                   String displayName,
                   String commandName,
                   List<String> tags,
                   int slotIndex,
                   boolean hideButton) {

    public Warp(int gridX,
                int gridY,
                String displayName,
                String commandName){
        this(gridX, gridY, displayName, commandName, List.of(), -1, false);
    }
    public Warp(int gridX,
                int gridY,
                String displayName,
                String commandName,
                List<String> tags){
        this(gridX, gridY, displayName, commandName, tags, -1, false);
    }
    public Warp(int gridX,
                int gridY,
                String displayName,
                int slotIndex){
        this(gridX, gridY, displayName, "", List.of(), slotIndex, false);
    }


    public static final MapCodec<Warp> CODEC = RecordCodecBuilder.<Warp>mapCodec(instance ->
            instance.group(
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("grid_x").forGetter(warp -> warp.gridX),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("grid_y").forGetter(warp -> warp.gridY),
                    ExtraCodecs.NON_EMPTY_STRING.fieldOf("display_name").forGetter(warp -> warp.displayName),
                    Codec.STRING.optionalFieldOf("command_name", "").forGetter(warp -> warp.commandName),
                    Codec.STRING.listOf().optionalFieldOf("tags", List.of()).forGetter(warp -> warp.tags),
                    Codec.INT.optionalFieldOf("slot_index", -1).forGetter(warp -> warp.slotIndex),
                    Codec.BOOL.optionalFieldOf("hide_button", false).forGetter(warp -> warp.hideButton)
                    ).apply(instance, Warp::new))
            .validate(Warp::validate);
    // Height scale is the same as width
    /** Grid unit width is islandWidth / widthFactor */
    public static final int GRID_UNIT_WIDTH_FACTOR = 40;
    /** Pattern used to validate tags in {@link Warp#validate(Warp)} */
    private static final Pattern TAG_VALIDATION_PATTERN = Pattern.compile("[a-z\\d-]");
    /** Warp button texture, shared between all warp buttons */
    private static WarpIcon warpIcon;
    /** Warp button width in pixels, see {@link this#initDefaults(Window)} */
    private static int width;
    /** Warp button height in pixels, see {@link this#initDefaults(Window)} */
    private static int height;

    public static void initDefaults(Window window){float scaleFactor;
        width = (int) (window.getGuiScaledWidth() * warpIcon.widthPercentage());
        scaleFactor = (float) width / warpIcon.texture().width();
        height = (int) (warpIcon.texture().height() * scaleFactor);
    }

    private static DataResult<Warp> validate(Warp warp){
        if (StringUtil.isNullOrEmpty(warp.commandName) && warp.commandName.length() > 1 && warp.slotIndex < 0){
            return DataResult.error(() -> "Warp " + warp.displayName + " must have a command name or a slot index");
        }
        if (StringUtil.isNullOrEmpty(warp.commandName) && warp.commandName.length() > 1 &&
                !warp.commandName.matches("(?i)/?[a-z]+")){
            return DataResult.error(() -> "Warp " + warp.displayName + "'s command name contains invalid characters.");
        }
        if (!warp.tags.isEmpty()){
            for (String tag : warp.tags){
                if (!TAG_VALIDATION_PATTERN.asPredicate().test(tag)){
                    return DataResult.error(() -> tag + " is not a valid warp tag.");
                }
            }
        }
        if (warp.gridX < 0 || warp.gridX > GRID_UNIT_WIDTH_FACTOR) {
            return DataResult.error(() -> "Warp " + warp.displayName + " grid_x is outside island");
        }

        if (warp.gridY < 0 || warp.gridY > GRID_UNIT_WIDTH_FACTOR) {
            return DataResult.error(() -> "Warp " + warp.displayName + " grid_y is outside island");
        }
        return DataResult.success(warp);
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }


    /**
     * Returns the command the player has to send to use this warp.
     * If the {@code commandName} doesn't start with a '/', "/warp " is prepended.
     */
    public String getWarpCommand() {
        // hardcoded to prevent command injection
        return this.commandName.equals("/garry") ? this.commandName : "/warp " + this.commandName;
    }


    public LayoutTexture getWarpTextureLocation() {
        return warpIcon.texture();
    }

    public LayoutTexture getWarpHoverEffectTextureLocation() {
        return warpIcon.hoverEffectTexture();
    }

    public static void setWarpIcon(WarpIcon warpIcon) {
        Warp.warpIcon = warpIcon;
    }

}
