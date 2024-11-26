package com.github.yukkuritaku.modernwarpmenu.data.skyblockconstants;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Map;

/** key: chat message, value: translation key of message to show in warp menu */
public record WarpMessages(List<String> warpSuccessMessages,
                           Map<String, String> warpFailMessages) {

    private static final Codec<Map<String, String>> WARP_FAIL_MESSAGES_CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING);
    public static final MapCodec<WarpMessages> CODEC = RecordCodecBuilder.<WarpMessages>mapCodec(instance ->
            instance.group(
                    Codec.STRING.listOf().fieldOf("warp_success_messages").forGetter(WarpMessages::warpSuccessMessages),
                    WARP_FAIL_MESSAGES_CODEC.fieldOf("warp_fail_messages").forGetter(WarpMessages::warpFailMessages)
                    ).apply(instance, WarpMessages::new))
            .validate(WarpMessages::validate);

    private static DataResult<WarpMessages> validate(WarpMessages warpMessages){
        if (warpMessages.warpSuccessMessages == null || warpMessages.warpSuccessMessages.isEmpty()) {
            return DataResult.error(() -> "Warp success message list cannot be empty");
        }
        if (warpMessages.warpFailMessages == null || warpMessages.warpFailMessages.isEmpty()){
            return DataResult.error(() -> "Warp fail message list cannot be empty");
        }
        return DataResult.success(warpMessages);
    }
}
