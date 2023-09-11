package io.github.racoondog.msl.credits.impl.mixin;

import io.github.racoondog.msl.credits.impl.TitleScreenCreditsImpl;
import meteordevelopment.meteorclient.utils.player.TitleScreenCredits;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(value = TitleScreenCredits.class, remap = false)
public abstract class TitleScreenCreditsMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private static void injectRender(DrawContext context, CallbackInfo ci) {
        TitleScreenCreditsImpl.render(context);
        ci.cancel();
    }

    @Inject(method = "onClicked", at = @At("HEAD"), cancellable = true)
    private static void injectOnClicked(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(TitleScreenCreditsImpl.onClicked(mouseX, mouseY));
        cir.cancel();
    }
}
