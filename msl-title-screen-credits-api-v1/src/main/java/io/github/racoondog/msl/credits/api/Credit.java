package io.github.racoondog.msl.credits.api;

import meteordevelopment.meteorclient.addons.MeteorAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class Credit {
    public final MeteorAddon addon;
    public final List<Section> sections = new ArrayList<>();
    public int width = 0;
    public boolean outdated = false;

    public Credit(MeteorAddon addon) {
        this.addon = addon;
    }

    public void calculateWidth() {
        width = 0;
        for (var section : sections) width += section.width;
    }

    public void append(Section section) {
        sections.add(section);
        calculateWidth();
    }

    public void append(Text text) {
        append(new Section(text));
    }

    public void append(String text, int color) {
        append(new Section(text, color));
    }
}
