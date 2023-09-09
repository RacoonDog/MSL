package io.github.racoondog.msl.settings.impl;

import io.github.racoondog.msl.settings.api.MslSettingsApi;
import io.github.racoondog.msl.settings.impl.mixin.SettingGroupMixin;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public class MslSettingsApiImpl implements MslSettingsApi {
    private MslSettingsApiImpl() {}

    public static SettingGroup createSettingGroup(String name, boolean sectionExpanded) {
        return SettingGroupMixin.msl$invokeCtor(name, sectionExpanded);
    }

    public static void addSettingGroup(Module module, SettingGroup settingGroup) {
        ((ISettings) module.settings).msl$addSettingGroup(settingGroup);
    }
}
