package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;

public class SetCameraColorEffect extends SettingCommand {
    public SetCameraColorEffect(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetCameraColorEffect");
        if (CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_COLOREFFECT))) {
            CamLog.d(FaceDetector.TAG, "Color effect doesn't supported. Return.");
            return;
        }
        String olaValue = lgParameters.getParameters().getColorEffect();
        String newValue = this.mGet.getSettingValue(Setting.KEY_CAMERA_COLOREFFECT);
        CamLog.i(FaceDetector.TAG, "oldValue : " + olaValue + ", newValue : " + newValue);
        if (!this.mGet.getSettingValue(Setting.KEY_CAMERA_COLOREFFECT).equals(Setting.HELP_OTHER)) {
            if (!this.mGet.getSettingValue(Setting.KEY_CAMERA_WHITEBALANCE).equals(LGT_Limit.ISP_AUTOMODE_AUTO)) {
                this.mGet.setSetting(Setting.KEY_CAMERA_WHITEBALANCE, LGT_Limit.ISP_AUTOMODE_AUTO);
                lgParameters.getParameters().setWhiteBalance(LGT_Limit.ISP_AUTOMODE_AUTO);
                this.mGet.toast((int) R.string.note_white_balance_change);
                this.mGet.setQuickFunctionControllerAllMenuIcons();
            }
            this.mGet.checkSceneMode(lgParameters, true, this.mGet.getString(R.string.note_shot_mode_change_normal));
            checkFocusMode(lgParameters);
        }
        if (!(olaValue == null || olaValue.equals(newValue))) {
            CamLog.i(FaceDetector.TAG, "color effect set to " + newValue);
            this.mGet.setSetting(Setting.KEY_CAMERA_COLOREFFECT, newValue);
            lgParameters.getParameters().setColorEffect(newValue);
        }
        this.mGet.setMenuEnableForSceneMode(2);
    }

    private void checkFocusMode(LGParameters lgParameters) {
        if (this.mGet.getApplicationMode() == 0 && this.mGet.getCameraId() == 0 && Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
            ListPreference pref = this.mGet.findPreference(Setting.KEY_FOCUS);
            if (pref != null) {
                this.mGet.setSetting(Setting.KEY_FOCUS, pref.getDefaultValue());
            }
            this.mGet.stopFaceDetection();
            lgParameters.getParameters().set(CameraConstants.FOCUS_MODE_MANUAL, 0);
            if (this.mGet.isIndicatorControllerInitialized()) {
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        SetCameraColorEffect.this.mGet.removePostRunnable(this);
                        SetCameraColorEffect.this.mGet.showFocus();
                        SetCameraColorEffect.this.mGet.updateFocusIndicator();
                        SetCameraColorEffect.this.mGet.showManualFocusController(false);
                    }
                });
            }
            this.mGet.toast((int) R.string.note_focus_mode_set_to_auto);
            this.mGet.setQuickFunctionControllerAllMenuIcons();
        }
    }
}
