package com.lge.camera.module;

import android.hardware.Camera;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.listeners.JpegPictureCallback;
import com.lge.camera.listeners.JpegPictureCallback.JpegCallbackFunction;
import com.lge.camera.listeners.ShutterCallback;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class HDRShot extends Module implements JpegCallbackFunction {
    public HDRShot(ControllerFunction function) {
        super(function);
    }

    public boolean checkCurrentShotMode() {
        return this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_HDR);
    }

    public boolean isRunning() {
        return false;
    }

    public void stopByUserAction() {
    }

    public boolean takePicture() {
        CamLog.d(FaceDetector.TAG, "TIME_CHECK [Module]HDRShot::takePicture-start");
        this.mGet.getCameraDevice().setPreviewCallback(null);
        CamLog.d(FaceDetector.TAG, "#### Device().takePicture()");
        try {
            this.mGet.getCameraDevice().takePicture(new ShutterCallback(this.mGet), null, null, new JpegPictureCallback(this));
            this.mGet.setPreviewing(false);
            if (this.mGet.getNeedProgressDuringCapture()) {
                this.mGet.postOnUiThread(new Runnable() {
                    public void run() {
                        if (HDRShot.this.mGet != null) {
                            HDRShot.this.mGet.removePostRunnable(this);
                            if (HDRShot.this.mGet.getActivity() != null && !HDRShot.this.mGet.isPreviewing() && HDRShot.this.mGet.getInCaptureProgress()) {
                                HDRShot.this.mGet.showProgressDialog();
                            }
                        }
                    }
                }, 1000);
            }
            CamLog.d(FaceDetector.TAG, "[Module]HDRShot::takePicture-end");
        } catch (IllegalStateException e) {
            CamLog.e(FaceDetector.TAG, "takePicture  failed :" + e);
            handleTakePictureError(R.string.error_occurred);
        } catch (RuntimeException e2) {
            CamLog.e(FaceDetector.TAG, "takePicture  failed :" + e2);
            handleTakePictureError(R.string.error_occurred);
        }
        return true;
    }

    public void jpegCallbackOnPictureTaken(byte[] jpegData, Camera camera) {
        super.jpegCallbackOnPictureTaken(jpegData, camera);
    }

    protected byte[] processFinalJpegData(byte[] jpegData) {
        return super.processFinalJpegData(jpegData);
    }

    protected boolean savePictureInJpegCallback(byte[] finalJpegData) {
        return super.savePictureInJpegCallback(finalJpegData);
    }

    protected void processJpegCallbackAfter(boolean isSuccessSave) {
        super.processJpegCallbackAfter(isSuccessSave);
    }
}
