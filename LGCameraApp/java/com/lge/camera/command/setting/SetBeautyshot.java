package com.lge.camera.command.setting;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetBeautyshot extends SettingCommand {
    public SetBeautyshot(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, new Bundle());
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetBeautyshot");
        if (this.mGet.getApplicationMode() == 0) {
            int value = ((Bundle) arg).getInt("mValue", 0);
            if (value == 0 && this.mGet.findPreference(Setting.KEY_BEAUTYSHOT) != null) {
                value = Integer.parseInt(this.mGet.getSettingValue(Setting.KEY_BEAUTYSHOT));
            }
            if (value < 0) {
                value = 0;
            }
            if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY)) {
                this.mGet.setFaceBeutyShotParameter(value);
            } else {
                this.mGet.setFaceBeutyShotParameter(0);
            }
        }
    }
}
