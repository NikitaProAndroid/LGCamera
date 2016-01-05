package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.module.Module;
import com.lge.camera.module.PanoramaShot;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetOlaPanoramaShot extends SettingCommand {
    public SetOlaPanoramaShot(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetOlaPanoramaShot");
        if (!(this.mGet.getCurrentModule() instanceof PanoramaShot)) {
            lgParameters.getParameters().setZoom(0);
            this.mGet.resetZoomController();
            lgParameters.getParameters().set(CameraConstants.PARAM_KEY_DUAL_RECORDER, 0);
            this.mGet.setModule(Module.PANORAMA_SHOT);
            this.mGet.setSetting(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA);
            this.mGet.setTimerAndSceneSmartShutterEnable(lgParameters.getParameters(), true, true, false);
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    SetOlaPanoramaShot.this.mGet.removePostRunnable(this);
                    SetOlaPanoramaShot.this.mGet.setPanoramaEngine();
                }
            });
            if (!this.mGet.isReadyEngineProcessor() && !this.mGet.isPreviewOnGoing()) {
                this.mGet.setEngineProcessor();
            }
        } else if (!this.mGet.isReadyEngineProcessor()) {
            this.mGet.setPanoramaEngine();
        }
    }
}
