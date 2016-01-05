package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.module.DefaultNormalShot;
import com.lge.camera.module.Module;
import com.lge.camera.properties.CameraConstants;
import com.lge.hardware.LGCamera.LGParameters;

public class SetOlaNormalShot extends SettingCommand {
    public SetOlaNormalShot(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        lgParameters.getParameters().set(CameraConstants.PARAM_KEY_DUAL_RECORDER, 0);
        if (!(this.mGet.getCurrentModule() instanceof DefaultNormalShot)) {
            this.mGet.setModule(Module.DEFAULT_NORMAL_SHOT);
        }
    }
}
