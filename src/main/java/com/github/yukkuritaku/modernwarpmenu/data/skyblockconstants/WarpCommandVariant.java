package com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

public record WarpCommandVariant(String command, WarpCommandType type) {

    public static final MapCodec<WarpCommandVariant> CODEC = RecordCodecBuilder.<WarpCommandVariant>mapCodec(instance ->
            instance.group(
                    ExtraCodecs.NON_EMPTY_STRING.fieldOf("command").forGetter(WarpCommandVariant::command),
                    WarpCommandType.CODEC.fieldOf("type").forGetter(WarpCommandVariant::type)
                    )
                    .apply(instance, WarpCommandVariant::new)
    ).validate(WarpCommandVariant::validate);

    private static DataResult<WarpCommandVariant> validate(WarpCommandVariant variant){
        if (variant.command == null){
            return DataResult.error(() -> "Warp command variant's command cannot be null");
        }
        if (variant.type == null){
            return DataResult.error(() -> "Warp command variant's command type cannot be null");
        }
        return DataResult.success(variant);
    }

    public enum WarpCommandType implements StringRepresentable {
        /**
         * An alias that works like the actual /warp command
         */
        ALIAS("alias"),
        /**
         * A shortcut to teleport to a single warp
         */
        WARP("warp");

        public static final StringRepresentable.EnumCodec<WarpCommandType> CODEC = StringRepresentable.fromEnum(WarpCommandType::values);
        private final String name;

        WarpCommandType(String name){
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public static void validateWarpCommandVariant(WarpCommandVariant warpCommandVariant) {
        if (warpCommandVariant == null) {
            throw new NullPointerException("Warp command variant cannot be null");
        }

        if (warpCommandVariant.command == null) {
            throw new NullPointerException("Warp command variant's command cannot be null");
        }

        if (warpCommandVariant.type == null) {
            throw new NullPointerException("Warp command variant's command type cannot be null");
        }
    }
}
