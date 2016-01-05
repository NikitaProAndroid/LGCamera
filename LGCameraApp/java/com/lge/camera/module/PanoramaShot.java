package com.lge.camera.module;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class PanoramaShot extends Module {
    public PanoramaShot(ControllerFunction function) {
        super(function);
        CamLog.d(FaceDetector.TAG, "Panorama Module Create !!");
    }

    public boolean takePicture() {
        CamLog.d(FaceDetector.TAG, "Panorama Module takePicture....");
        this.mGet.setMainButtonDisable();
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                PanoramaShot.this.mGet.removePostRunnable(this);
                if (!PanoramaShot.this.mGet.isPanoramaUIShown()) {
                    PanoramaShot.this.mGet.showPanoramaView();
                }
                PanoramaShot.this.mGet.startPanorama();
            }
        });
        return true;
    }

    public boolean checkCurrentShotMode() {
        return this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA);
    }

    public boolean isRunning() {
        return false;
    }

    public void stopByUserAction() {
    }
}
