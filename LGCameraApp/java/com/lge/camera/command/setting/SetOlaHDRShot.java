package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.module.HDRShot;
import com.lge.camera.module.Module;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.hardware.LGCamera.LGParameters;

public class SetOlaHDRShot extends SettingCommand {
    public SetOlaHDRShot(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        if (!(this.mGet.getCurrentModule() instanceof HDRShot)) {
            lgParameters.getParameters().set(CameraConstants.PARAM_KEY_DUAL_RECORDER, 0);
            this.mGet.setModule(Module.HDR_SHOT);
            this.mGet.setSetting(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_HDR);
            this.mGet.setTimerAndSceneSmartShutterEnable(lgParameters.getParameters(), false, true, false);
        }
    }
}
