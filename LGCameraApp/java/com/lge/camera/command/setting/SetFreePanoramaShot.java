package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.module.Module;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetFreePanoramaShot extends CameraSettingCommand {
    private LGParameters mLGParameters;

    public SetFreePanoramaShot(ControllerFunction function) {
        super(function);
        this.mLGParameters = null;
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetFreePanoramaShot");
        lgParameters.getParameters().set(CameraConstants.PARAM_KEY_DUAL_RECORDER, 0);
        this.mLGParameters = lgParameters;
        this.mGet.setModule(Module.FREE_PANORAMA_SHOT);
        this.mGet.setSetting(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA);
        this.mGet.setTimerAndSceneSmartShutterEnable(this.mLGParameters.getParameters(), true, true, false);
    }
}
