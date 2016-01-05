package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.module.ContinuousShot;
import com.lge.camera.module.Module;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetOlaContinuousShot extends SettingCommand {
    public SetOlaContinuousShot(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetOlaContinuousShot");
        if (!(this.mGet.getCurrentModule() instanceof ContinuousShot)) {
            lgParameters.getParameters().set(CameraConstants.PARAM_KEY_DUAL_RECORDER, 0);
            this.mGet.setModule(Module.CONTINUOUS_SHOT);
            this.mGet.setTimerAndSceneSmartShutterEnable(lgParameters.getParameters(), true, true, false);
            this.mGet.setSetting(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CONTINUOUS);
        }
    }
}
