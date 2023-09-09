package io.github.racoondog.msl.settings.api;

import io.github.racoondog.msl.settings.impl.MslSettingsApiImpl;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface MslSettingsApi {
    static SettingGroup createSettingGroup(String name, boolean sectionExpanded) {
        return MslSettingsApiImpl.createSettingGroup(name, sectionExpanded);
    }

    static void addSettingGroup(Module module, SettingGroup settingGroup) {
        MslSettingsApiImpl.addSettingGroup(module, settingGroup);
    }
}
