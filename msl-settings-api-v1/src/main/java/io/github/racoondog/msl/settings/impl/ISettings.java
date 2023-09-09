package io.github.racoondog.msl.settings.impl;

import meteordevelopment.meteorclient.settings.SettingGroup;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ISettings {
    void msl$addSettingGroup(SettingGroup settingGroup);
}
