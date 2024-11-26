package com.github.yukkuritaku.modernwarpmenu.data.layout;

import com.github.yukkuritaku.modernwarpmenu.ModernWarpMenu;
import com.github.yukkuritaku.modernwarpmenu.data.layout.texture.LayoutTexture;
import com.mojang.blaze3d.platform.Window;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public class Button {

    public static final MapCodec<Button> CODEC = RecordCodecBuilder.<Button>mapCodec(instance ->
            instance.group(
                    LayoutTexture.CODEC.fieldOf("texture").forGetter(button -> button.texture),

                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("grid_x").forGetter(button -> button.gridX),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("grid_y").forGetter(button -> button.gridY),
                    ExtraCodecs.NON_NEGATIVE_FLOAT.fieldOf("width_percentage").forGetter(button -> button.widthPercentage)
                    ).apply(instance, Button::new)
    ).validate(Button::validate);

    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID, "textures/gui/icon.png");
    /** Overlay texture rendered when mod is outdated */
    public static final ResourceLocation NOTIFICATION = ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID, "textures/gui/notification.png");

    public static final ResourceLocation REGULAR_WARP_MENU = ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID, "textures/gui/regular_warp_menu.png");


    public final LayoutTexture texture;
    public final int gridX;
    public final int gridY;
    /** Width to render the button texture at as a percentage of the screen width. Texture height is set automatically. */
    public final float widthPercentage;
    /** Width of the button in pixels */
    private transient int width;
    /** Height of the button in pixels */
    private transient int height;

    Button(LayoutTexture texture, int gridX, int gridY, float widthPercentage){
        this.texture = texture;
        this.gridX = gridX;
        this.gridY = gridY;
        this.widthPercentage = widthPercentage;
    }

    private static DataResult<Button> validate(Button button){
        if (button.gridX < 0 || button.gridX > Island.GRID_UNIT_WIDTH_FACTOR) {
            return DataResult.error(() -> "Button grid_x must be between 0 and " + Island.GRID_UNIT_WIDTH_FACTOR + " inclusive");
        }

        if (button.gridY < 0 || button.gridY > Island.GRID_UNIT_HEIGHT_FACTOR) {
            return DataResult.error(() -> "Button grid_y must be between 0 and " + Island.GRID_UNIT_HEIGHT_FACTOR + " inclusive");
        }

        // A button width of zero causes a stack overflow
        if (button.widthPercentage <= 0 || button.widthPercentage > 1) {
            return DataResult.error(() -> "Button icon width_percentage must be within the interval (0,1]");
        }
        return DataResult.success(button);
    }


    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void init(Window window){
        float scaleFactor;
        this.width = (int) (window.getGuiScaledWidth() * this.widthPercentage);
        scaleFactor = (float) this.width / this.texture.width();
        this.height = (int) (this.texture.height() * scaleFactor);
    }

}
