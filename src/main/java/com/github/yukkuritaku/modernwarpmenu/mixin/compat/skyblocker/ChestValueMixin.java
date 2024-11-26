package com.github.yukkuritaku.modernwarpmenu.mixin.compat.skyblocker;

import com.github.yukkuritaku.modernwarpmenu.client.gui.screens.ModernWarpScreen;
import com.github.yukkuritaku.modernwarpmenu.data.settings.SettingsManager;
import de.hysky.skyblocker.skyblock.ChestValue;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = {
        @Condition("skyblocker")
})
@Mixin(ChestValue.class)
public class ChestValueMixin {

    @Inject(method = "lambda$init$3", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), cancellable = true)
    private static void onInit(Minecraft client, Screen screen, int scaledWidth, int scaledHeight, CallbackInfo ci){
        if (screen instanceof ModernWarpScreen && SettingsManager.get().general.warpMenuEnabled){
            ci.cancel();
        }
    }
}
