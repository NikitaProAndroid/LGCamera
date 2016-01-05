package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetCameraTimer extends SettingCommand {
    public SetCameraTimer(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetCameraTimer ");
        String stringValue = this.mGet.getSettingValue(Setting.KEY_CAMERA_TIMER);
        CamLog.i(FaceDetector.TAG, "getSettingValue(key_camera_timer) = " + stringValue);
        if (stringValue.equals(CameraConstants.TYPE_PREFERENCE_NOT_FOUND)) {
            CamLog.d(FaceDetector.TAG, "Need to check string whether it is convertable or not.");
            return;
        }
        this.mGet.setTimerSetting(Integer.parseInt(stringValue));
        this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_TIMER, true, true);
    }

    public void updateIndicator() {
        if (this.mGet.isIndicatorControllerInitialized()) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    SetCameraTimer.this.mGet.removePostRunnable(this);
                    SetCameraTimer.this.mGet.updateTimerIndicator();
                }
            });
        }
    }
}
