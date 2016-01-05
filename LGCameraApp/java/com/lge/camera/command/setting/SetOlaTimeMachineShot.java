package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.controller.BubblePopupController;
import com.lge.camera.module.Module;
import com.lge.camera.module.TimeMachineShot;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;

public class SetOlaTimeMachineShot extends SettingCommand {
    public SetOlaTimeMachineShot(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetOlaTimeMachineShot");
        if (!(this.mGet.getCurrentModule() instanceof TimeMachineShot)) {
            lgParameters.getParameters().set(CameraConstants.PARAM_KEY_DUAL_RECORDER, 0);
            if (LGParameters.SCENE_MODE_NIGHT.equals(this.mGet.getSettingValue(Setting.KEY_SCENE_MODE))) {
                this.mGet.setSetting(Setting.KEY_SCENE_MODE, LGT_Limit.ISP_AUTOMODE_AUTO);
                this.mGet.setSceneModeForAdvanced(lgParameters.getParameters(), LGT_Limit.ISP_AUTOMODE_AUTO);
            }
            this.mGet.setModule(Module.TIME_MACHINE_SHOT);
            this.mGet.setTimerAndSceneSmartShutterEnable(lgParameters.getParameters(), false, true, false);
            boolean timeMachineTempFileDelete = false;
            try {
                timeMachineTempFileDelete = this.mGet.deleteTimeMachineImages();
                this.mGet.setTimemachineHasPictures(false);
                BubblePopupController.get().initializeNotiComplete();
            } catch (Exception e) {
                CamLog.w(FaceDetector.TAG, "Exception:", e);
            } finally {
                CamLog.i(FaceDetector.TAG, "timeMachineTempFileDeleted ? = " + timeMachineTempFileDelete);
            }
        }
    }
}
