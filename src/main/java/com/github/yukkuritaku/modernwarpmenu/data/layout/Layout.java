package com.github.yukkuritaku.modernwarpmenu.data.layout;

import com.github.yukkuritaku.modernwarpmenu.ModernWarpMenu;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record Layout(LayoutType layoutType, ResourceLocation backgroundTexture,
                     List<Island> islandList,
                     WarpIcon warpIcon,
                     Button configButton,
                     Button regularWarpMenuButton) {

    public static final ResourceLocation EMPTY = ResourceLocation.fromNamespaceAndPath(ModernWarpMenu.MOD_ID, "background_empty");

    public static final MapCodec<Layout> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    LayoutType.CODEC.fieldOf("type").forGetter(Layout::layoutType),
                    ResourceLocation.CODEC.optionalFieldOf("background", EMPTY).forGetter(layout -> layout.backgroundTexture),
                    Island.CODEC.codec().listOf().fieldOf("island_list").forGetter(layout -> layout.islandList),
                    WarpIcon.CODEC.fieldOf("warp_icon").forGetter(layout -> layout.warpIcon),
                    Button.CODEC.fieldOf("config_button").forGetter(layout -> layout.configButton),
                    Button.CODEC.fieldOf("regular_warp_menu_button").forGetter(layout -> layout.regularWarpMenuButton)
            ).apply(instance, Layout::new)
    );

    private static DataResult<Layout> validate(Layout layout){
        if (layout.islandList == null || layout.islandList.isEmpty()) {
            return DataResult.error(() -> "Island list cannot be empty");
        }
        return DataResult.success(layout);
    }

    public enum LayoutType implements StringRepresentable {
        OVERWORLD("overworld"),
        RIFT("rift")
        ;

        public static final EnumCodec<LayoutType> CODEC = StringRepresentable.fromEnum(LayoutType::values);
        private final String name;
        LayoutType(String name){
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
