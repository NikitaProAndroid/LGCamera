package com.lge.camera.setting;

import android.content.Context;
import com.lge.camera.setting.SettingMenu.SettingMenuFunction;

public class VideoMain3dSettingMenu extends VideoSettingMenu {
    public VideoMain3dSettingMenu(SettingMenuFunction function, Context context, VideoSetting setting) {
        super(function, context, setting);
        setSettingPreferenceGroupForVideo(this.mSetting.getPreferenceGroup());
        initMenu();
    }

    public void initSettingMenu() {
        initMenu();
    }
}
