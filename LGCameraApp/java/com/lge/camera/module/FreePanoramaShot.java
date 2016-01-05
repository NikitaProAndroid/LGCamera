package com.lge.camera.module;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class FreePanoramaShot extends Module {
    public FreePanoramaShot(ControllerFunction function) {
        super(function);
        CamLog.d(FaceDetector.TAG, "Free Panorama Module Create !!");
    }

    public boolean takePicture() {
        CamLog.d(FaceDetector.TAG, "Free Panorama Module takePicture....");
        if (this.mGet.getFreePanoramaEngineStatus() == 0) {
            CamLog.d(FaceDetector.TAG, "cannot start free panorama takePicture....");
            this.mGet.setInCaptureProgress(false);
            this.mGet.enableCommand(true);
            return false;
        }
        this.mGet.setMainButtonDisable();
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                FreePanoramaShot.this.mGet.removePostRunnable(this);
                FreePanoramaShot.this.mGet.startFreePanorama();
            }
        });
        return true;
    }

    public boolean checkCurrentShotMode() {
        return this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA);
    }

    public boolean isRunning() {
        return false;
    }

    public void stopByUserAction() {
    }
}
