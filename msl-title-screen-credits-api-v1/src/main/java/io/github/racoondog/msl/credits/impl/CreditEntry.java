package io.github.racoondog.msl.credits.impl;

import io.github.racoondog.msl.credits.api.Credit;
import io.github.racoondog.msl.credits.api.DrawFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CreditEntry {
    public final DrawFunction drawFunction;
    public final boolean updatesY;
    public Credit credit;

    public CreditEntry(DrawFunction drawFunction, boolean updatesY) {
        this.drawFunction = drawFunction;
        this.updatesY = updatesY;
    }
}
