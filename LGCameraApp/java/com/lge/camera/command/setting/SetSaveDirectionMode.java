package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetSaveDirectionMode extends SettingCommand {
    public SetSaveDirectionMode(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.i(FaceDetector.TAG, "SetSaveDirectionMode");
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                SetSaveDirectionMode.this.mGet.removePostRunnable(this);
                if (SetSaveDirectionMode.this.mGet.getCameraId() != 0) {
                    return;
                }
                if (SetSaveDirectionMode.this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) || SetSaveDirectionMode.this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
                    SetSaveDirectionMode.this.mGet.hideFocus();
                } else {
                    SetSaveDirectionMode.this.mGet.showFocus();
                }
            }
        });
        this.mGet.setPreferenceMenuEnable(Setting.KEY_SAVE_DIRECTION, true, true);
    }
}
