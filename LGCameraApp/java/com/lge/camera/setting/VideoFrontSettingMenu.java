package com.lge.camera.setting;

import android.content.Context;
import com.lge.camera.setting.SettingMenu.SettingMenuFunction;

public class VideoFrontSettingMenu extends VideoSettingMenu {
    public VideoFrontSettingMenu(SettingMenuFunction function, Context context, VideoSetting setting) {
        super(function, context, setting);
        setSettingPreferenceGroupForVideo(this.mSetting.getPreferenceGroup());
        initMenu();
    }

    public void initSettingMenu() {
        initMenu();
    }
}
