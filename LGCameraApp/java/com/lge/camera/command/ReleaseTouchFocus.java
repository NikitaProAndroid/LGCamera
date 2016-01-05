package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ReleaseTouchFocus extends Command {
    public ReleaseTouchFocus(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "ReleaseTouchFocus");
        if (this.mGet.getApplicationMode() != 0) {
            return;
        }
        if (this.mGet.isCafSupported() && this.mGet.getInCaptureProgress()) {
            CamLog.d(FaceDetector.TAG, " captured progress!!, so return");
            return;
        }
        if (!(!FunctionProperties.isFaceDetectionAuto() || this.mGet.isChangeMode() || this.mGet.isPausing() || this.mGet.isFinishingActivity())) {
            if (this.mGet.getSettingValue(Setting.KEY_FOCUS).equals(Setting.HELP_FACE_TRACKING_LED)) {
                this.mGet.startFaceDetection(true);
            } else {
                this.mGet.startFaceDetection(false);
            }
        }
        if (this.mGet.getCameraId() == 0) {
            this.mGet.cancelAutoFocus();
            this.mGet.setFocusRectangleInitialize();
            if (this.mGet.isCafSupported()) {
                this.mGet.hideFocus();
            }
        } else if (this.mGet.getCameraId() == 1) {
            this.mGet.setFocusRectangleInitialize();
            this.mGet.hideFocus();
        }
    }
}
