package com.lge.camera.setting;

import android.content.Context;
import com.lge.camera.setting.SettingMenu.SettingMenuFunction;

public class VideoMainSettingMenu extends VideoSettingMenu {
    public VideoMainSettingMenu(SettingMenuFunction function, Context context, VideoSetting setting) {
        super(function, context, setting);
        setSettingPreferenceGroupForVideo(this.mSetting.getPreferenceGroup());
        initMenu();
    }

    public void initSettingMenu() {
        initMenu();
    }
}
