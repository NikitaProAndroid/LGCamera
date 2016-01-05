package com.lge.camera.command;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class DoAfterFullFrameContinous extends Command {
    public DoAfterFullFrameContinous(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.i(FaceDetector.TAG, "DoAfterBurstShot!");
        this.mGet.setInCaptureProgress(false);
        this.mGet.waitSaveImageThreadDone();
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                if (DoAfterFullFrameContinous.this.mGet != null) {
                    DoAfterFullFrameContinous.this.mGet.removePostRunnable(this);
                    DoAfterFullFrameContinous.this.mGet.deleteProgressDialog();
                }
            }
        });
        if (this.mGet.checkAutoReviewOff(false)) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    if (DoAfterFullFrameContinous.this.mGet != null) {
                        DoAfterFullFrameContinous.this.mGet.removePostRunnable(this);
                        DoAfterFullFrameContinous.this.mGet.setShutterButtonImage(true, DoAfterFullFrameContinous.this.mGet.getOrientationDegree());
                    }
                }
            });
            this.mGet.startPreview(null, false);
            Bundle bundle = new Bundle();
            bundle.putBoolean("from JpegCallback Full Frame Continuous shot", true);
            this.mGet.doCommandDelayed(Command.DISPLAY_PREVIEW, bundle, 0);
            return;
        }
        this.mGet.setInCaptureProgress(false);
        if (!this.mGet.checkAutoReviewForQuickView()) {
            this.mGet.doCommandUi(Command.DISPLAY_CAMERA_POSTVIEW);
        }
    }
}
