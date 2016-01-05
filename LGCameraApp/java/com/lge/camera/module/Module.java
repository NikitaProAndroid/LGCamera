package com.lge.camera.module;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.FileNamer;
import com.lge.camera.util.SharedPreferenceUtil;
import com.lge.camera.util.Util;
import com.lge.olaworks.define.Ola_ImageFormat;
import com.lge.olaworks.library.Exif;
import com.lge.olaworks.library.FaceDetector;
import java.io.ByteArrayOutputStream;

public abstract class Module {
    public static final String CLEAR_SHOT = "ClearShot";
    public static final String CONTINUOUS_SHOT = "ContinuousShot";
    public static final String DEFAULT_NORMAL_SHOT = "DefaultNormalShot";
    public static final String DUAL_CAMERA_SHOT = "DualCameraShot";
    public static final String FACE_TRACKING = "FaceTracking";
    public static final String FREE_PANORAMA_SHOT = "FreePanoramaShot";
    public static final String FULLFRAME_CONTINUOUS_SHOT = "FullFrameContinuousShot";
    public static final String HDR_SHOT = "HDRShot";
    public static final String MAIN_BEAUTY_SHOT = "MainBeautyShot";
    public static final String PANORAMA_SHOT = "PanoramaShot";
    public static final String PLANE_PANORAMA_SHOT = "PlanePanoramaShot";
    public static final String REFOCUS_SHOT = "RefocusShot";
    public static final String SMILE_SHOT = "SmileShot";
    public static final String TIMER_SHOT = "TimerShot";
    public static final String TIME_MACHINE_SHOT = "TimeMachineShot";
    private static long afterTime;
    private static long beforeTime;
    protected ControllerFunction mGet;

    public abstract boolean checkCurrentShotMode();

    public abstract boolean isRunning();

    public abstract void stopByUserAction();

    public abstract boolean takePicture();

    static {
        beforeTime = 0;
        afterTime = 0;
    }

    public Module(ControllerFunction function) {
        this.mGet = null;
        this.mGet = function;
    }

    public void startRotation(int degree) {
    }

    public void doAfterCapture() {
    }

    public void jpegCallbackOnDualCameraPictureTaken(byte[] jpegData, Camera camera) {
        jpegCallbackOnPictureTaken(jpegData, camera);
    }

    protected boolean checkMediator() {
        if (this.mGet == null || this.mGet.isPausing()) {
            return false;
        }
        return true;
    }

    protected void checkShotTime() {
        if (beforeTime == afterTime) {
            beforeTime = System.currentTimeMillis();
            return;
        }
        afterTime = System.currentTimeMillis() - beforeTime;
        beforeTime = System.currentTimeMillis();
        Log.d(FaceDetector.TAG, "[SHOT TIME] JPEG callback lag : " + (System.currentTimeMillis() - ProjectVariables.mCaptureStartTime) + "ms");
        Log.d(FaceDetector.TAG, "[SHOT TIME] ShotToShot time : " + afterTime + "ms");
        afterTime = 0;
    }

    protected void checkAutoReview() {
        if (this.mGet.checkAutoReviewOff(false)) {
            if (!CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
                this.mGet.startPreview(null, false);
            }
            Bundle bundle = new Bundle();
            bundle.putBoolean("fromJpegCallback", true);
            this.mGet.doCommandUi(Command.DISPLAY_PREVIEW, bundle);
        }
    }

    protected void checkProgressdialog() {
        if (this.mGet.getNeedProgressDuringCapture()) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    if (Module.this.mGet != null) {
                        Module.this.mGet.removePostRunnable(this);
                        Module.this.mGet.deleteProgressDialog();
                        Module.this.mGet.setNeedProgressDuringCapture(false);
                    }
                }
            });
        }
    }

    protected void jpegCallbackOnPictureTaken(byte[] jpegData, Camera camera) {
        CamLog.d(FaceDetector.TAG, "#### TIME_CHECK JpegPictureCallback()-start");
        if (checkMediator()) {
            boolean isSuccessSave;
            checkShotTime();
            checkProgressdialog();
            checkAutoReview();
            this.mGet.setCaptureData(null);
            this.mGet.setImageRotationDegree(this.mGet.getDeviceDegree());
            doAfterCapture();
            byte[] finalJpegData = processFinalJpegData(jpegData);
            if (jpegData == null || finalJpegData == null) {
                CamLog.e(FaceDetector.TAG, "error!! Module-onPictureTaken (jpegData == null)");
                isSuccessSave = false;
                if (jpegData == null && ProjectVariables.useJpegPictureCallbackError()) {
                    jpegPictureCallbackError();
                    return;
                }
            }
            CamLog.i(FaceDetector.TAG, "finalJpegData size [" + finalJpegData.length + "], jpegData size [" + jpegData.length + "]");
            if (!(jpegData[0] == (byte) -1 && jpegData[1] == (byte) -40) && ProjectVariables.useJpegPictureCallbackError()) {
                CamLog.d(FaceDetector.TAG, "error!! Module abnormal jpegData stream");
                jpegPictureCallbackError();
                return;
            } else if (((long) jpegData.length) < this.mGet.getFreeSpace()) {
                isSuccessSave = savePictureInJpegCallback(finalJpegData);
            } else {
                CamLog.e(FaceDetector.TAG, "savePicture() not enough memory!");
                this.mGet.toast((int) R.string.sp_not_enough_memory_NORMAL);
                this.mGet.setInCaptureProgress(false);
                FileNamer.get().setErrorFeedback(this.mGet.getApplicationMode());
                if (this.mGet.checkAutoReviewOff(false)) {
                    this.mGet.checkStorage(true);
                    return;
                }
                this.mGet.startPreview(null, true);
                this.mGet.doCommandUi(Command.DISPLAY_PREVIEW);
                this.mGet.checkStorage(false);
                return;
            }
            processJpegCallbackAfter(isSuccessSave);
            CamLog.d(FaceDetector.TAG, "JpegPictureCallback()-end");
            return;
        }
        CamLog.v(FaceDetector.TAG, "JpegPictureCallback()-end, null -> return or pausing.");
    }

    protected byte[] processFinalJpegData(byte[] jpegData) {
        byte[] finalJpegData = null;
        int ola_Orientation = this.mGet.getParameteredRotation() == 0 ? 0 : 3;
        if (jpegData != null) {
            if (this.mGet.getApplicationMode() != 0 || this.mGet.getCameraMode() != 1) {
                finalJpegData = jpegData;
            } else if (CameraConstants.SMART_MODE_OFF.equals(this.mGet.getSettingValue(Setting.KEY_SAVE_DIRECTION))) {
                if (CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
                    finalJpegData = makeBeautyShotImage(jpegData, Ola_ImageFormat.RGB_LABEL, ola_Orientation, true);
                } else {
                    finalJpegData = makeNormalShotImage(jpegData, true);
                }
            } else if (FunctionProperties.getFunctionFrontCameraBeautyShot() == 1) {
                finalJpegData = makeBeautyShotImage(jpegData, Ola_ImageFormat.RGB_LABEL, ola_Orientation, false);
            } else {
                finalJpegData = jpegData;
            }
            if (finalJpegData == null) {
                CamLog.e(FaceDetector.TAG, "error!! onPictureTaken (finalJpegData == null)");
            }
        }
        return finalJpegData;
    }

    protected boolean savePictureInJpegCallback(byte[] finalJpegData) {
        if (!this.mGet.checkAutoReviewOff(false)) {
            this.mGet.waitSaveImageThreadDone();
            return this.mGet.savePicture(finalJpegData, null);
        } else if (this.mGet.getAvailablePictureCount() > 1) {
            boolean isSuccessSave = this.mGet.saveImageSavers(finalJpegData, null, this.mGet.getDeviceDegree(), true, false);
            CamLog.d(FaceDetector.TAG, "ImageSaver Queue count is : " + this.mGet.getQueueCount());
            return isSuccessSave;
        } else {
            this.mGet.waitSaveImageThreadDone();
            return this.mGet.savePicture(finalJpegData, null);
        }
    }

    protected void processJpegCallbackAfter(boolean isSuccessSave) {
        if (isSuccessSave) {
            CamLog.i(FaceDetector.TAG, "mImageListUri size = " + this.mGet.getImageListUri().size());
            if (!this.mGet.checkAutoReviewOff(false)) {
                this.mGet.setInCaptureProgress(false);
                this.mGet.waitSaveImageThreadDone();
                SharedPreferenceUtil.saveLastPicture(this.mGet.getActivity(), this.mGet.getSavedImageUri());
                if (this.mGet.beDirectlyGoingToCropGallery()) {
                    this.mGet.doAttach();
                    return;
                } else if (!this.mGet.checkAutoReviewForQuickView()) {
                    this.mGet.doCommandUi(Command.DISPLAY_CAMERA_POSTVIEW);
                    return;
                } else if (this.mGet.getImageListUri().size() > 0) {
                    this.mGet.getImageListUri().removeAll(this.mGet.getImageListUri());
                    return;
                } else {
                    return;
                }
            } else if (this.mGet.getImageListUri().size() > 0) {
                this.mGet.getImageListUri().removeAll(this.mGet.getImageListUri());
                return;
            } else {
                return;
            }
        }
        CamLog.e(FaceDetector.TAG, "savePicture() fail!");
        if (!this.mGet.isPausing()) {
            this.mGet.toast((int) R.string.error_write_file);
        }
        this.mGet.setInCaptureProgress(false);
        FileNamer.get().setErrorFeedback(this.mGet.getApplicationMode());
        this.mGet.startPreview(null, true);
        this.mGet.doCommandUi(Command.DISPLAY_PREVIEW);
    }

    protected void jpegPictureCallbackError() {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                Module.this.mGet.removePostRunnable(this);
                Module.this.mGet.showCameraStoppedAndFinish();
            }
        });
    }

    protected void handleTakePictureError(int resource) {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                if (Module.this.mGet != null) {
                    Module.this.mGet.removePostRunnable(this);
                    Module.this.mGet.setShutterButtonImage(true, Module.this.mGet.getOrientationDegree());
                }
            }
        });
        this.mGet.getCameraDevice().stopPreview();
        this.mGet.startPreview(null, false);
        Bundle bundle = new Bundle();
        bundle.putBoolean("fromJpegCallback", true);
        this.mGet.doCommandDelayed(Command.DISPLAY_PREVIEW, bundle, 0);
        this.mGet.toast((int) R.string.error_occurred);
    }

    protected byte[] makeBeautyShotImage(byte[] jpegData, int format, int orientation, boolean isFlip) {
        Bitmap bitmap = Util.makeBitmap(jpegData, isFlip);
        if (bitmap != null) {
            return this.mGet.getEngineProcessor().processCapture(jpegData, bitmap, format, orientation);
        }
        return jpegData;
    }

    protected byte[] makeNormalShotImage(byte[] jpegData, boolean isFlip) {
        Bitmap bitmap = Util.makeBitmap(jpegData, isFlip);
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        if (bitmap == null) {
            return jpegData;
        }
        bitmap.compress(CompressFormat.JPEG, 95, ostream);
        byte[] finalJpegData = Exif.processLoadExif(jpegData, ostream.toByteArray(), bitmap);
        bitmap.recycle();
        return finalJpegData;
    }
}
