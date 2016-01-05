package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.module.Module;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetPlanePanoramaShot extends CameraSettingCommand {
    public SetPlanePanoramaShot(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetPlanePanoramaShot");
        lgParameters.getParameters().set(CameraConstants.PARAM_KEY_DUAL_RECORDER, 0);
        this.mGet.setModule(Module.PLANE_PANORAMA_SHOT);
        this.mGet.setSetting(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA);
        this.mGet.setTimerAndSceneSmartShutterEnable(lgParameters.getParameters(), true, true, false);
        this.mGet.updateFlashIndicator(false, null);
    }
}
