package com.lge.camera.setting;

import android.content.Context;
import com.lge.camera.setting.Setting.SettingFunction;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Util;
import com.lge.olaworks.library.FaceDetector;

public class VideoSetting extends Setting {
    public VideoSetting(SettingFunction function, Context context, String configName, PreferenceGroup prefGroup) {
        super(function, context, configName, prefGroup);
        loadSetting(context);
    }

    public void loadSetting(Context context) {
        CamLog.d(FaceDetector.TAG, "Load configuration.");
    }

    public void saveSetting(Context context) {
        CamLog.d(FaceDetector.TAG, "Save configuration.");
    }

    public int[] getPreviewSizeOnDevice(int index) {
        int[] iArr = null;
        if (getPreferenceGroup() != null) {
            ListPreference listPref = getPreferenceGroup().findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
            if (listPref != null) {
                iArr = null;
                try {
                    iArr = Util.SizeString2WidthHeight(listPref.getEntryValues()[index].toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    CamLog.e(FaceDetector.TAG, "listPref is null");
                }
            }
        }
        return iArr;
    }
}
