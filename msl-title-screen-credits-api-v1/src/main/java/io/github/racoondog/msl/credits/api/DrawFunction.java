package io.github.racoondog.msl.credits.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface DrawFunction {
    void accept(DrawContext context, Credit credit, int y);
}
