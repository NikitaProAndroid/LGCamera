package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.module.FullFrameContinuousShot;
import com.lge.camera.module.Module;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;

public class SetFullFrameContinuousShot extends SettingCommand {
    public SetFullFrameContinuousShot(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetFullFrameContinuousShot");
        if (!(this.mGet.getCurrentModule() instanceof FullFrameContinuousShot)) {
            if (Setting.HELP_NIGHT.equals(this.mGet.getSettingValue(Setting.KEY_SCENE_MODE))) {
                this.mGet.setSetting(Setting.KEY_SCENE_MODE, LGT_Limit.ISP_AUTOMODE_AUTO);
                this.mGet.setSceneModeForAdvanced(lgParameters.getParameters(), LGT_Limit.ISP_AUTOMODE_AUTO);
            }
            lgParameters.getParameters().set(CameraConstants.PARAM_KEY_DUAL_RECORDER, 0);
            this.mGet.setModule(Module.FULLFRAME_CONTINUOUS_SHOT);
            this.mGet.setSetting(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS);
            this.mGet.setTimerAndSceneSmartShutterEnable(lgParameters.getParameters(), true, true, false);
        }
    }
}
