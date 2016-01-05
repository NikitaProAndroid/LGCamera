package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;

public class DoCapture extends Command {
    public DoCapture(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "DoCapture-start");
        int focusState = this.mGet.getFocusState();
        if (this.mGet.getStorageState() != 0) {
            CamLog.d(FaceDetector.TAG, "doCapture return : NOT STORAGE_AVAILABLE");
            this.mGet.enableCommand(false);
            this.mGet.startGestureEngine();
            return;
        }
        this.mGet.enableCommand(false);
        ProjectVariables.mCaptureStartTime = System.currentTimeMillis();
        if (this.mGet.getCameraId() == 0) {
            CamLog.i(FaceDetector.TAG, "DoCapture focusState = " + focusState);
            if (focusState == 3 || focusState == 4 || focusState == 7 || focusState == 6 || CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
                doTakePictureInDoCapture();
            } else if (focusState == 1 || focusState == 5) {
                this.mGet.setFocusState(2);
            } else if (this.mGet.isCafSupported() && (focusState == 0 || focusState == 8 || focusState == 9 || focusState == 10 || focusState == 11 || focusState == 12 || focusState == 13)) {
                doTakePictureInDoCapture();
            } else if (focusState == 0) {
                this.mGet.enableCommand(true);
            }
        } else {
            doTakePictureInDoCapture();
        }
        CamLog.d(FaceDetector.TAG, "DoCapture-end");
    }

    private void doTakePictureInDoCapture() {
        if (("0".equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_TIMER)) || this.mGet.getTimerCaptureDelay() <= 0 || Common.isQuickWindowCameraMode() || this.mGet.isTimerShotCountdown()) && !this.mGet.isGestureShotActivated()) {
            this.mGet.startGestureEngine();
            this.mGet.doCommand(Command.TAKE_PICTURE);
            this.mGet.setTimerShotCountdown(false);
            return;
        }
        if (!("0".equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_TIMER)) || this.mGet.isGestureShotActivated())) {
            this.mGet.stopGestureEngine();
        }
        this.mGet.startTimerShot();
    }
}
