package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetCameraAntibanding extends SettingCommand {
    public SetCameraAntibanding(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        String antiBanding = this.mGet.getSettingValue(Setting.KEY_CAMERA_ANTI_BANDING);
        CamLog.i(FaceDetector.TAG, "antiBanding = " + antiBanding);
        lgParameters.getParameters().setAntibanding(antiBanding);
    }
}
