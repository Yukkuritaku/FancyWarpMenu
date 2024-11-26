package com.github.yukkuritaku.modernwarpmenu.mixin.compat.architectury;

import com.github.yukkuritaku.modernwarpmenu.client.gui.screens.CustomContainerScreen;
import dev.architectury.event.events.client.ClientGuiEvent;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require =
        {
                @Condition("architectury")
        })
@Mixin(CustomContainerScreen.class)
public class CustomContainerScreenMixin {

    @Inject(method = "renderArchitecturyContainerBackground", at = @At("HEAD"))
    private void injectBackgroundEvent(AbstractContainerScreen<?> screen,
                                       GuiGraphics guiGraphics, int mouseX, int mouseY,
                                       float partialTick, CallbackInfo ci) {
        ClientGuiEvent.RENDER_CONTAINER_BACKGROUND.invoker().render(screen, guiGraphics, mouseX, mouseY, partialTick);
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE",
            target = "Lcom/github/yukkuritaku/modernwarpmenu/client/gui/screens/CustomContainerScreen;renderCustomUI(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            ordinal = 0, shift = At.Shift.AFTER))
    private void injectForegroundEvent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci){
        ClientGuiEvent.RENDER_CONTAINER_FOREGROUND.invoker().render((AbstractContainerScreen<?>) (Object)this, guiGraphics, mouseX, mouseY, partialTick);
    }

}
