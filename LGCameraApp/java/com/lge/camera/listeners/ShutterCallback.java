package com.lge.camera.listeners;

import android.util.Log;
import com.lge.camera.ControllerFunction;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ShutterCallback implements android.hardware.Camera.ShutterCallback {
    private ControllerFunction mGet;

    public ShutterCallback(ControllerFunction function) {
        this.mGet = null;
        this.mGet = function;
    }

    public void onShutter() {
        if (this.mGet != null) {
            CamLog.d(FaceDetector.TAG, "SHOT ShutterCallback()");
            if (this.mGet.isPausing()) {
                CamLog.d(FaceDetector.TAG, "ShutterCallback():: (mMediator.isPausing() == true)");
            } else if (this.mGet.getApplicationMode() == 1) {
                CamLog.d(FaceDetector.TAG, "ShutterCallback(): (mMediator.getApplicationMode() == MODE_CAMCORDER)");
            } else {
                if (ProjectVariables.mCaptureStartTime > 0) {
                    Log.d(FaceDetector.TAG, "[SHOT TIME] Shutter Lag = " + (System.currentTimeMillis() - ProjectVariables.mCaptureStartTime) + "ms");
                    ProjectVariables.mCaptureStartTime = System.currentTimeMillis();
                }
                if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS)) {
                    CamLog.d(FaceDetector.TAG, "ShutterCallback():: Full Frame Continuous shot");
                    this.mGet.postOnUiThread(new Runnable() {
                        public void run() {
                            ShutterCallback.this.mGet.removePostRunnable(this);
                            if (!FunctionProperties.isSupportBurstShot()) {
                                ShutterCallback.this.mGet.playContinuousShutterSound();
                            }
                            ShutterCallback.this.mGet.clearFocusState();
                            ShutterCallback.this.mGet.hideFocus();
                        }
                    });
                    return;
                }
                if (!(this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_REFOCUS))) {
                    this.mGet.postOnUiThread(new Runnable() {
                        public void run() {
                            ShutterCallback.this.mGet.removePostRunnable(this);
                            ShutterCallback.this.mGet.setShutterButtonImage(true, ShutterCallback.this.mGet.getOrientationDegree());
                        }
                    });
                    this.mGet.playShutterSound();
                    this.mGet.postOnUiThread(new Runnable() {
                        public void run() {
                            ShutterCallback.this.mGet.removePostRunnable(this);
                            if (!ShutterCallback.this.mGet.checkAutoReviewOff(false) || ShutterCallback.this.mGet.getCameraMode() != 0) {
                                return;
                            }
                            if (ShutterCallback.this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_NORMAL) || ShutterCallback.this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY) || ShutterCallback.this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_PREFERENCE_NOT_FOUND)) {
                                ShutterCallback.this.mGet.doCommandUi(Command.SNAPSHOT_EFFECT);
                            }
                        }
                    });
                }
                this.mGet.postOnUiThread(new Runnable() {
                    public void run() {
                        ShutterCallback.this.mGet.removePostRunnable(this);
                        ShutterCallback.this.mGet.clearFocusState();
                        ShutterCallback.this.mGet.hideFocus();
                    }
                });
            }
        }
    }
}
