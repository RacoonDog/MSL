package io.github.racoondog.msl.settings.impl.mixin;

import io.github.racoondog.msl.settings.impl.ISettings;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(Settings.class)
public abstract class SettingsMixin implements ISettings {
    @Shadow @Final public List<SettingGroup> groups;

    @Override
    public void msl$addSettingGroup(SettingGroup settingGroup) {
        groups.add(settingGroup);
    }
}
