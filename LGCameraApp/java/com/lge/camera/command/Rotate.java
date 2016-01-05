package com.lge.camera.command;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.AppControlUtil;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class Rotate extends Command {
    private boolean animation;

    public Rotate(ControllerFunction function) {
        super(function);
        this.animation = true;
    }

    public void execute() {
        execute(new Bundle());
    }

    public void execute(Object arg) {
        Bundle bundle = (Bundle) arg;
        this.animation = bundle.getBoolean("animation", true);
        int degree = bundle.getInt("degrees", this.mGet.getOrientationDegree());
        if (!this.mGet.isControllerInitialized()) {
            bundle.putBoolean("animation", false);
            this.mGet.doCommandDelayed(Command.ROTATE, bundle, 500);
        }
        CamLog.d(FaceDetector.TAG, "Rotate to degree: " + degree);
        if (this.mGet.getApplicationMode() != 0 || !this.mGet.isPanoramaStarted()) {
            if (this.mGet.getApplicationMode() == 0 && this.mGet.getPlanePanoramaStatus() >= 1 && this.mGet.getPlanePanoramaStatus() <= 3) {
                CamLog.d(FaceDetector.TAG, "block rotation because plane panorama is taking.");
            } else if (this.mGet.isCamcorderRotation(true)) {
                this.mGet.rotateAllController(degree, this.animation);
                if (!this.mGet.isChangeMode()) {
                    this.mGet.postOnUiThread(new Runnable() {
                        public void run() {
                            Rotate.this.mGet.removePostRunnable(this);
                            AppControlUtil.rotateNavigationBarIcon(Rotate.this.mGet.getActivity(), Rotate.this.mGet.getOrientationDegree(), Rotate.this.animation ? CameraConstants.GALLERY_QUICKVIEW_ANI_INTERVAL : 0);
                        }
                    });
                }
            }
        }
    }
}
