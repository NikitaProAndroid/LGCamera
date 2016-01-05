package com.lge.camera.command.setting;

import android.hardware.Camera.Parameters;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;

public class SetCameraIso extends SettingCommand {
    public SetCameraIso(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetCameraIso ");
        if ((!this.mGet.getSettingValue(Setting.KEY_ISO).equals(LGT_Limit.ISP_AUTOMODE_AUTO) || this.mGet.getSettingValue(Setting.KEY_ISO).equals(CameraConstants.TYPE_PREFERENCE_NOT_FOUND)) && this.mGet.getCameraId() == 0) {
            this.mGet.checkSceneMode(lgParameters, true, this.mGet.getString(R.string.note_shot_mode_change_normal));
        }
        String configValue = this.mGet.getSettingValue(Setting.KEY_ISO);
        CamLog.i(FaceDetector.TAG, "configValue [" + configValue + "] ");
        this.mGet.setMenuEnableForSceneMode(4);
        try {
            lgParameters.getParameters().set(LGT_Limit.ISP_ISO, configValue);
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
            CamLog.e(FaceDetector.TAG, String.format("ISO NOT SUPPORTED!", new Object[0]));
        }
    }

    private void setBrightnessDefualtForNVIDIA(Parameters parameters, String configValue) {
        if (ModelProperties.isNVIDIAChipset()) {
            int BRsettingValue = Integer.parseInt(this.mGet.getSettingValue(Setting.KEY_BRIGHTNESS));
            CamLog.d(FaceDetector.TAG, "BRValue [" + BRsettingValue + "] ");
            try {
                if (checkMediator() && this.mGet.getPreferenceGroup() != null && !configValue.equals(LGT_Limit.ISP_AUTOMODE_AUTO)) {
                    ListPreference preference = this.mGet.getPreferenceGroup().findPreference(Setting.KEY_BRIGHTNESS);
                    if (preference != null && BRsettingValue != Integer.parseInt(preference.getDefaultValue())) {
                        parameters.setExposureCompensation(0);
                        this.mGet.resetBrightnessController();
                        this.mGet.toast((int) R.string.sp_note_brightness_set_to_zero_NORMAL);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                CamLog.e(FaceDetector.TAG, String.format("ISO changing brightness error!", new Object[0]));
            }
        }
    }
}
