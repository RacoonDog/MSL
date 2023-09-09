package io.github.racoondog.msl.settings.impl.mixin;

import meteordevelopment.meteorclient.settings.SettingGroup;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(SettingGroup.class)
public interface SettingGroupMixin {
    @Invoker("<init>")
    static SettingGroup msl$invokeCtor(String name, boolean sectionExpanded) {
        throw new AssertionError();
    }
}
