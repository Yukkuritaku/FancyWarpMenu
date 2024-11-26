package com.github.yukkuritaku.modernwarpmenu.mixin.compat.rei;

import com.github.yukkuritaku.modernwarpmenu.client.gui.screens.ModernWarpScreen;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.shedaniel.rei.RoughlyEnoughItemsCoreClient;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(require = {
        @Condition("roughlyenoughitems")
})
@Mixin(RoughlyEnoughItemsCoreClient.class)
public class RoughlyEnoughItemsCoreClientMixin {

    @Inject(method = "shouldReturn", at = @At("HEAD"), cancellable = true)
    private static void injectShouldReturn(Screen screen, CallbackInfoReturnable<Boolean> cir){
        if (screen instanceof ModernWarpScreen){
            cir.setReturnValue(true);
        }
    }
}
