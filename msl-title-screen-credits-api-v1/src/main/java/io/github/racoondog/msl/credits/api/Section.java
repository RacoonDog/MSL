package io.github.racoondog.msl.credits.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Environment(EnvType.CLIENT)
public class Section {
    public final Text text;
    public final int width;

    public Section(Text text) {
        this.text = text;
        this.width = mc.textRenderer.getWidth(text);
    }

    public Section(String text, int color) {
        this(Text.literal(text).styled(s -> s.withColor(color)));
    }
}
