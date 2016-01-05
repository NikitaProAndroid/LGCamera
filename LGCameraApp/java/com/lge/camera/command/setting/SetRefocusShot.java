package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.module.Module;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;

public class SetRefocusShot extends SettingCommand {
    public SetRefocusShot(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        if (LGParameters.SCENE_MODE_NIGHT.equals(this.mGet.getSettingValue(Setting.KEY_SCENE_MODE))) {
            this.mGet.setSetting(Setting.KEY_SCENE_MODE, LGT_Limit.ISP_AUTOMODE_AUTO);
            this.mGet.setSceneModeForAdvanced(lgParameters.getParameters(), LGT_Limit.ISP_AUTOMODE_AUTO);
        }
        lgParameters.getParameters().set(CameraConstants.PARAM_KEY_DUAL_RECORDER, 0);
        this.mGet.setModule(Module.REFOCUS_SHOT);
        this.mGet.setSetting(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_REFOCUS);
    }
}
