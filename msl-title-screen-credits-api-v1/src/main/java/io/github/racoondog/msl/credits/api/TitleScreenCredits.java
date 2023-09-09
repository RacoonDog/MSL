package io.github.racoondog.msl.credits.api;

import io.github.racoondog.msl.credits.impl.TitleScreenCreditsImpl;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public interface TitleScreenCredits {
    int WHITE = Color.fromRGBA(255, 255, 255, 255);
    int GRAY = Color.fromRGBA(175, 175, 175, 255);
    int RED = Color.fromRGBA(255, 25, 25, 255);

    static void customDrawFunction(MeteorAddon addon, DrawFunction function, boolean shouldUpdateY) {
        TitleScreenCreditsImpl.registerCustomDrawFunction(addon, function, shouldUpdateY);
    }

    static void consumerDrawFunction(MeteorAddon addon, Consumer<DrawContext> consumer, boolean shouldUpdateY) {
        TitleScreenCreditsImpl.registerConsumerDrawFunction(addon, consumer, shouldUpdateY);
    }

    static void defaultDrawFunction(MeteorAddon addon) {
        TitleScreenCreditsImpl.registerDefaultDrawFunction(addon);
    }

    static void emptyDrawFunction(MeteorAddon addon) {
        TitleScreenCreditsImpl.registerEmptyDrawFunction(addon);
    }

    static void modifyCredit(MeteorAddon addon, Consumer<Credit> consumer) {
        TitleScreenCreditsImpl.registerCreditModification(addon, consumer);
    }

    static void modifyAllCredits(Consumer<Credit> consumer) {
        TitleScreenCreditsImpl.registerGlobalCreditModification(consumer);
    }
}
