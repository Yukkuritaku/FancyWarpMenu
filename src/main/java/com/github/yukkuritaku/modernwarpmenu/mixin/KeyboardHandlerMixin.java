package com.github.yukkuritaku.modernwarpmenu.mixin;

import com.github.yukkuritaku.modernwarpmenu.event.InputEvents;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("TAIL"))
    private void onKeyPress(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci){
        if (windowPointer == this.minecraft.getWindow().getWindow()) {
            InputEvents.KEY_PRESSED.invoker().onKeyPressed(key, scanCode, action, modifiers);
        }
    }
}
