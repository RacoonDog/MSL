package io.github.racoondog.msl.settings.api;

import com.google.common.collect.ImmutableList;
import meteordevelopment.meteorclient.settings.IVisible;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;

import java.util.List;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class StyleSetting extends Setting<Style> {
    private static final List<String> SUGGESTIONS = ImmutableList.of("bold,italic,255,255,255", "0,0,0", "obfuscated,70,70,225");

    public StyleSetting(String name, String description, Style defaultValue, Consumer<Style> onChanged, Consumer<Setting<Style>> onModuleActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    @Override
    protected Style parseImpl(String str) {
        Style style = Style.EMPTY
                .withBold(str.contains("bold"))
                .withItalic(str.contains("italic"))
                .withUnderline(str.contains("underlined"))
                .withStrikethrough(str.contains("strikethrough"))
                .withObfuscated(str.contains("obfuscated"));

        String[] tokens = str.split(",");

        style.withColor(new Color(
                Integer.parseInt(tokens[tokens.length - 3]),
                Integer.parseInt(tokens[tokens.length - 2]),
                Integer.parseInt(tokens[tokens.length - 1]),
                255
        ).getPacked());

        return style;
    }

    @Override
    public List<String> getSuggestions() {
        return SUGGESTIONS;
    }

    @Override
    protected boolean isValueValid(Style value) {
        return true;
    }

    @Override
    protected NbtCompound save(NbtCompound tag) {
        if (value.isEmpty()) return tag;
        if (value.isBold()) tag.putBoolean("bold", true);
        if (value.isItalic()) tag.putBoolean("italic", true);
        if (value.isUnderlined()) tag.putBoolean("underlined", true);
        if (value.isStrikethrough()) tag.putBoolean("strikethrough", true);
        if (value.isObfuscated()) tag.putBoolean("obfuscated", true);

        TextColor color = value.getColor();
        if (color != null) tag.putInt("color", color.getRgb());

        return tag;
    }

    @Override
    protected Style load(NbtCompound tag) {
        Style style = Style.EMPTY
                .withBold(tag.contains("bold"))
                .withItalic(tag.contains("italic"))
                .withUnderline(tag.contains("underlined"))
                .withStrikethrough(tag.contains("strikethrough"))
                .withObfuscated(tag.contains("obfuscated"));

        if (tag.contains("color")) style.withColor(tag.getInt("color"));

        return style;
    }

    public static class Builder extends SettingBuilder<Builder, Style, StyleSetting> {
        public Builder() {
            super(Style.EMPTY);
        }

        @Override
        public StyleSetting build() {
            return new StyleSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
