package com.lge.camera.module;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class PlanePanoramaShot extends Module {
    public PlanePanoramaShot(ControllerFunction function) {
        super(function);
        CamLog.d(FaceDetector.TAG, "plane Panorama Module Create !!");
    }

    public boolean takePicture() {
        CamLog.d(FaceDetector.TAG, "plane Panorama Module takePicture....");
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                PlanePanoramaShot.this.mGet.removePostRunnable(this);
                PlanePanoramaShot.this.mGet.startPlanePanorama();
            }
        });
        return true;
    }

    public boolean checkCurrentShotMode() {
        return this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA);
    }

    public boolean isRunning() {
        return false;
    }

    public void stopByUserAction() {
    }
}
