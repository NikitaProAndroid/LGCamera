package com.lge.camera.module;

import android.hardware.Camera;
import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.listeners.JpegPictureCallback;
import com.lge.camera.listeners.JpegPictureCallback.JpegCallbackFunction;
import com.lge.camera.listeners.ShutterCallback;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.FileNamer;
import com.lge.camera.util.SharedPreferenceUtil;
import com.lge.olaworks.library.FaceDetector;

public class TimeMachineShot extends Module implements JpegCallbackFunction {
    private int mTimeMachineCount;

    public TimeMachineShot(ControllerFunction function) {
        super(function);
        this.mTimeMachineCount = 0;
    }

    public boolean checkCurrentShotMode() {
        return this.mGet.isTimeMachineModeOn();
    }

    public boolean isRunning() {
        return false;
    }

    public void stopByUserAction() {
    }

    public boolean takePicture() {
        CamLog.d(FaceDetector.TAG, "TIME_CHECK [Module]TimeMachineShot::takePicture-start");
        this.mTimeMachineCount = 0;
        this.mGet.removeAllImageList();
        this.mGet.getCameraDevice().setPreviewCallback(null);
        CamLog.d(FaceDetector.TAG, "#### Device().takePicture()");
        try {
            this.mGet.getCameraDevice().takePicture(new ShutterCallback(this.mGet), null, null, new JpegPictureCallback(this));
            this.mGet.setPreviewing(false);
            CamLog.d(FaceDetector.TAG, "[Module]TimeMachineShot::takePicture-end");
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "TimeMachineShot-takePicture  failed :" + e);
            handleTakePictureError(R.string.error_occurred);
        }
        return true;
    }

    public void jpegCallbackOnPictureTaken(byte[] jpegData, Camera camera) {
        CamLog.d(FaceDetector.TAG, "#### TIME_CHECK JpegPictureCallbackTimeMachine()-start");
        if (checkMediator()) {
            boolean isSuccessSave;
            this.mGet.setCaptureData(null);
            this.mGet.setImageRotationDegree(this.mGet.getDeviceDegree());
            if (jpegData == null) {
                CamLog.e(FaceDetector.TAG, "error!! TimeMachineShot-onPictureTaken (jpegData == null)");
                isSuccessSave = false;
                if (ProjectVariables.useJpegPictureCallbackError()) {
                    jpegPictureCallbackError();
                    return;
                }
            }
            CamLog.i(FaceDetector.TAG, "finalJpegData size [" + jpegData.length + "]");
            if (!(jpegData[0] == (byte) -1 && jpegData[1] == (byte) -40) && ProjectVariables.useJpegPictureCallbackError()) {
                CamLog.d(FaceDetector.TAG, "error!! TimeMachineShot abnormal jpegData stream");
                jpegPictureCallbackError();
                return;
            } else if (((long) jpegData.length) < this.mGet.getFreeSpace()) {
                this.mGet.waitSaveImageThreadDone();
                if (this.mTimeMachineCount == 0) {
                    CamLog.i(FaceDetector.TAG, "timeMachineTempFileDeleted ? = " + this.mGet.deleteTimeMachineImages());
                }
                this.mTimeMachineCount++;
                isSuccessSave = this.mGet.saveTimeMachinePicture(jpegData, this.mTimeMachineCount);
                if (this.mTimeMachineCount == 5) {
                    this.mGet.saveImageSavers(jpegData, null, this.mGet.getDeviceDegree(), true, false);
                }
            } else {
                CamLog.e(FaceDetector.TAG, "savePicture() not enough memory!");
                handleTakePictureError(R.string.sp_not_enough_memory_NORMAL);
                return;
            }
            if (isSuccessSave) {
                processJpegCallbackAfter();
                CamLog.i(FaceDetector.TAG, "mTimeMachineCount = " + this.mTimeMachineCount);
                CamLog.d(FaceDetector.TAG, "JpegPictureCallbackTimeMachine()-end");
                return;
            }
            CamLog.e(FaceDetector.TAG, "savePicture() fail!");
            handleTakePictureError(R.string.error_write_file);
        }
    }

    private void processJpegCallbackAfter() {
        if (this.mTimeMachineCount >= 5) {
            this.mGet.setTimemachineHasPictures(true);
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
                bundle.putBoolean("useTimeMachinePostview", true);
                this.mGet.doCommandUi(Command.DISPLAY_CAMERA_POSTVIEW, bundle);
            }
            this.mTimeMachineCount = 0;
        }
    }

    protected void handleTakePictureError(int resource) {
        this.mGet.setInCaptureProgress(false);
        this.mGet.toast(resource);
        CamLog.d(FaceDetector.TAG, "Current TimeMachine count : " + this.mTimeMachineCount);
        this.mTimeMachineCount = 0;
        FileNamer.get().setErrorFeedback(this.mGet.getApplicationMode());
        this.mGet.startPreview(null, true);
        this.mGet.doCommandUi(Command.DISPLAY_PREVIEW);
        this.mGet.checkStorage(false);
    }
}
