package com.lge.camera.module;

import android.hardware.Camera;
import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.listeners.JpegPictureCallback;
import com.lge.camera.listeners.JpegPictureCallback.JpegCallbackFunction;
import com.lge.camera.listeners.ShutterCallback;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.FileNamer;
import com.lge.camera.util.SharedPreferenceUtil;
import com.lge.olaworks.library.FaceDetector;

public class RefocusShot extends Module implements JpegCallbackFunction {
    private boolean mIsRefocusShotRunning;
    private int mRefocusShotCount;

    public RefocusShot(ControllerFunction function) {
        super(function);
        this.mRefocusShotCount = 0;
        this.mIsRefocusShotRunning = false;
    }

    public boolean checkCurrentShotMode() {
        return this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_REFOCUS);
    }

    public void stopByUserAction() {
        this.mGet.stopClearShotSound();
    }

    public boolean takePicture() {
        CamLog.d(FaceDetector.TAG, "TIME_CHECK [Module]RefocusShot::takePicture-start");
        this.mRefocusShotCount = 0;
        this.mGet.removeAllImageList();
        this.mGet.getCameraDevice().setPreviewCallback(null);
        CamLog.d(FaceDetector.TAG, "#### Device().takePicture()");
        this.mGet.setRefocusShotPreviewGuideVisibility(false);
        this.mGet.hideFocus();
        playSound();
        try {
            this.mGet.postOnUiThread(new Runnable() {
                public void run() {
                    RefocusShot.this.mGet.removePostRunnable(this);
                    if (!RefocusShot.this.mGet.isPausing() && RefocusShot.this.mGet.getCameraDevice() != null) {
                        RefocusShot.this.mGet.getCameraDevice().takePicture(new ShutterCallback(RefocusShot.this.mGet), null, null, new JpegPictureCallback(RefocusShot.this));
                    }
                }
            });
            setIsModuleRunning(true);
            this.mGet.setPreviewing(false);
            CamLog.d(FaceDetector.TAG, "[Module]RefocusShot::takePicture-end");
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "RefocusShot-takePicture  failed :" + e);
            handleTakePictureError(R.string.error_occurred);
        }
        return true;
    }

    public void jpegCallbackOnPictureTaken(byte[] jpegData, Camera camera) {
        CamLog.d(FaceDetector.TAG, "#### TIME_CHECK JpegPictureCallbackRefocus()-start");
        if (checkMediator()) {
            this.mGet.setCaptureData(null);
            this.mGet.setImageRotationDegree(this.mGet.getDeviceDegree());
            boolean isSuccessSave = false;
            if (jpegData == null) {
                CamLog.e(FaceDetector.TAG, "error!! RefocusShot-onPictureTaken (jpegData == null)");
                isSuccessSave = false;
                if (ProjectVariables.useJpegPictureCallbackError()) {
                    jpegPictureCallbackError();
                    return;
                }
            }
            CamLog.i(FaceDetector.TAG, "finalJpegData size [" + jpegData.length + "]");
            if (!(jpegData[0] == (byte) -1 && jpegData[1] == (byte) -40) && ProjectVariables.useJpegPictureCallbackError()) {
                CamLog.d(FaceDetector.TAG, "error!! RefocusShot abnormal jpegData stream");
                jpegPictureCallbackError();
                return;
            } else if (((long) jpegData.length) < this.mGet.getFreeSpace()) {
                this.mGet.waitSaveImageThreadDone();
                if (this.mRefocusShotCount == 0) {
                    CamLog.i(FaceDetector.TAG, "refocusShotTempFileDelete ? = " + this.mGet.deleteRefocusShotImages());
                }
                CamLog.d(FaceDetector.TAG, "JPEG Callback data index = " + this.mRefocusShotCount);
                this.mRefocusShotCount++;
                playSound();
                if (this.mRefocusShotCount == 6) {
                    CamLog.d(FaceDetector.TAG, "Map data has received");
                    this.mGet.saveRefocusShotMap(jpegData);
                    isSuccessSave = true;
                } else if (this.mRefocusShotCount != 7) {
                    CamLog.d(FaceDetector.TAG, "Focal length containing image no = " + (this.mRefocusShotCount - 1));
                    isSuccessSave = this.mGet.saveRefocusShotPicture(jpegData, this.mRefocusShotCount);
                }
                if (this.mRefocusShotCount == 7) {
                    CamLog.d(FaceDetector.TAG, "This picture is All-in focus image  so will also be saved in /DCIM/Camera dir.");
                    isSuccessSave = this.mGet.saveImageSavers(jpegData, null, this.mGet.getDeviceDegree(), true, false);
                }
            } else {
                CamLog.e(FaceDetector.TAG, "savePicture() not enough memory!");
                handleTakePictureError(R.string.sp_not_enough_memory_NORMAL);
                return;
            }
            if (isSuccessSave) {
                processJpegCallbackAfter();
                CamLog.i(FaceDetector.TAG, "mRefocusShotCount = " + this.mRefocusShotCount);
                CamLog.d(FaceDetector.TAG, "JpegPictureCallbackRefocus()-end");
                return;
            }
            CamLog.e(FaceDetector.TAG, "savePicture() fail!");
            handleTakePictureError(R.string.error_write_file);
        }
    }

    private void processJpegCallbackAfter() {
        if (this.mRefocusShotCount >= 7) {
            this.mGet.setRefocusShotHasPictures(true);
            this.mGet.waitSaveImageThreadDone();
            SharedPreferenceUtil.saveLastPicture(this.mGet.getActivity(), this.mGet.getSavedImageUri());
            Bundle bundle;
            if (this.mGet.checkAutoReviewOff(false)) {
                this.mGet.startPreview(null, false);
                bundle = new Bundle();
                bundle.putBoolean("fromJpegCallback", true);
                this.mGet.doCommandDelayed(Command.DISPLAY_PREVIEW, bundle, 0);
            } else if (!this.mGet.checkAutoReviewForQuickView()) {
                bundle = new Bundle();
                bundle.putBoolean("useRefocusPostview", true);
                this.mGet.doCommandUi(Command.DISPLAY_CAMERA_POSTVIEW, bundle);
            }
            this.mGet.setRefocusShotPreviewGuideVisibility(true);
            this.mRefocusShotCount = 0;
            setIsModuleRunning(false);
        }
    }

    protected void handleTakePictureError(int resource) {
        this.mGet.setInCaptureProgress(false);
        this.mGet.toast(resource);
        CamLog.d(FaceDetector.TAG, "Current Refocus count : " + this.mRefocusShotCount);
        this.mRefocusShotCount = 0;
        FileNamer.get().setErrorFeedback(this.mGet.getApplicationMode());
        this.mGet.startPreview(null, true);
        this.mGet.doCommandUi(Command.DISPLAY_PREVIEW);
        this.mGet.checkStorage(false);
        this.mGet.setRefocusShotPreviewGuideVisibility(true);
        setIsModuleRunning(false);
    }

    public void startRotation(int degree) {
        super.startRotation(degree);
        if (this.mGet.findViewById(R.id.refocus_guide_layout).getVisibility() == 0) {
            this.mGet.setRefocusShotPreviewGuideVisibility(true);
        }
    }

    private void setIsModuleRunning(boolean isRunning) {
        this.mIsRefocusShotRunning = isRunning;
    }

    public boolean isRunning() {
        return this.mIsRefocusShotRunning;
    }

    public void playSound() {
        CamLog.d(FaceDetector.TAG, "HSAND playSound mRefocusShotCount = " + this.mRefocusShotCount);
        if (this.mRefocusShotCount == 0) {
            this.mGet.playClearShotShutterSound(true);
        } else if (this.mRefocusShotCount == 6) {
            this.mGet.playClearShotShutterSound(false);
        }
    }
}
