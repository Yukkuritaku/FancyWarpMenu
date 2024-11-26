package com.github.yukkuritaku.modernwarpmenu.data.layout;

import com.github.yukkuritaku.modernwarpmenu.data.layout.texture.LayoutTexture;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record WarpIcon(LayoutTexture texture,
                       LayoutTexture hoverEffectTexture,
                       float widthPercentage) {

    public WarpIcon(LayoutTexture texture, float widthPercentage){
        this(texture, texture, widthPercentage);
    }

    public static final MapCodec<WarpIcon> CODEC = RecordCodecBuilder.<WarpIcon>mapCodec(instance ->
            instance.group(
                            LayoutTexture.CODEC.fieldOf("texture").forGetter(warpIcon -> warpIcon.texture),
                            LayoutTexture.CODEC.codec().fieldOf("hover_effect_texture").forGetter(warpIcon -> warpIcon.hoverEffectTexture),
                            Codec.FLOAT.fieldOf("width_percentage").forGetter(warpIcon -> warpIcon.widthPercentage))
                    .apply(instance, WarpIcon::new)
    ).validate(WarpIcon::validate);


    private static DataResult<WarpIcon> validate(WarpIcon warpIcon){
        return warpIcon.widthPercentage < 0 || warpIcon.widthPercentage > 1 ?
                DataResult.error(() -> "Warp icon width_percentage must be between 0 and 1, but got " + warpIcon.widthPercentage) :
                DataResult.success(warpIcon);
    }
}
