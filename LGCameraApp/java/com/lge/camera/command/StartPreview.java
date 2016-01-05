package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;

public class StartPreview extends Command {
    public StartPreview(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        if (Common.isScreenLocked()) {
            CamLog.d(FaceDetector.TAG, String.format("Wait for lockscreen uncovered...", new Object[0]));
            this.mGet.doCommandDelayed(Command.START_PREVIEW, 200);
            return;
        }
        CamLog.i(FaceDetector.TAG, String.format("Lockscreen uncovered. Start preview.", new Object[0]));
        this.mGet.setLockScreenPreventPreview(false);
        this.mGet.startPreview(null, true);
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                if (StartPreview.this.mGet != null) {
                    StartPreview.this.mGet.removePostRunnable(this);
                    StartPreview.this.mGet.keepScreenOnAwhile();
                }
            }
        });
    }
}
