package com.lge.camera.setting;

import android.content.Context;
import com.lge.camera.setting.Setting.SettingFunction;

public class CameraSetting extends Setting {
    public CameraSetting(SettingFunction function, Context context, String configName, PreferenceGroup prefGroup) {
        super(function, context, configName, prefGroup);
        loadSetting(context);
    }

    public void loadSetting(Context context) {
        if (!this.mGet.isAttachIntent()) {
        }
    }

    public void saveSetting(Context context) {
        if (!this.mGet.isAttachIntent()) {
        }
    }
}
