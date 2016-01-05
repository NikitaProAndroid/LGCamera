package com.lge.camera.module;

import android.hardware.Camera;
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
import com.lge.olaworks.define.Ola_ImageFormat;
import com.lge.olaworks.library.FaceDetector;

public class MainBeautyShot extends Module implements JpegCallbackFunction {
    public MainBeautyShot(ControllerFunction function) {
        super(function);
    }

    public boolean checkCurrentShotMode() {
        return this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY);
    }

    public boolean isRunning() {
        return false;
    }

    public void stopByUserAction() {
    }

    public boolean takePicture() {
        CamLog.d(FaceDetector.TAG, "TIME_CHECK [Module]MainBeautyShot::takePicture-start");
        this.mGet.getCameraDevice().setPreviewCallback(null);
        CamLog.d(FaceDetector.TAG, "#### Device().takePicture()");
        try {
            this.mGet.getCameraDevice().takePicture(new ShutterCallback(this.mGet), null, null, new JpegPictureCallback(this));
            this.mGet.setPreviewing(false);
            CamLog.d(FaceDetector.TAG, "[Module]MainBeautyShot::takePicture-end");
        } catch (IllegalStateException e) {
            CamLog.e(FaceDetector.TAG, "takePicture  failed :" + e);
            handleTakePictureError(R.string.error_occurred);
        } catch (RuntimeException e2) {
            CamLog.e(FaceDetector.TAG, "takePicture  failed :" + e2);
            handleTakePictureError(R.string.error_occurred);
        }
        return true;
    }

    public void jpegCallbackOnPictureTaken(final byte[] jpegData, Camera camera) {
        CamLog.d(FaceDetector.TAG, "#### TIME_CHECK JpegPictureCallback()-start");
        if (checkMediator()) {
            this.mGet.setBeautyshotProgress(true);
            checkShotTime();
            checkAutoReview();
            this.mGet.setCaptureData(null);
            this.mGet.setImageRotationDegree(this.mGet.getDeviceDegree());
            doAfterCapture();
            this.mGet.showProgressDialog();
            Thread beautyShotThread = new Thread(new Runnable() {
                public void run() {
                    final byte[] finalJpegData = MainBeautyShot.this.processFinalJpegData(jpegData);
                    if (jpegData == null || finalJpegData == null) {
                        CamLog.e(FaceDetector.TAG, "error!! MainBeautyShot-onPictureTaken (jpegData == null)");
                        if (jpegData == null && ProjectVariables.useJpegPictureCallbackError()) {
                            MainBeautyShot.this.jpegPictureCallbackError();
                            MainBeautyShot.this.releaseBeautyshotJpegCallback();
                            return;
                        }
                        MainBeautyShot.this.processJpegCallbackAfter(false);
                        return;
                    }
                    CamLog.i(FaceDetector.TAG, "finalJpegData size [" + finalJpegData.length + "], jpegData size [" + jpegData.length + "]");
                    if (!(jpegData[0] == (byte) -1 && jpegData[1] == (byte) -40) && ProjectVariables.useJpegPictureCallbackError()) {
                        CamLog.d(FaceDetector.TAG, "error!! MainBeautyShot abnormal jpegData stream");
                        MainBeautyShot.this.jpegPictureCallbackError();
                        return;
                    }
                    MainBeautyShot.this.mGet.runOnUiThread(new Runnable() {
                        public void run() {
                            MainBeautyShot.this.mGet.removePostRunnable(this);
                            if (((long) jpegData.length) < MainBeautyShot.this.mGet.getFreeSpace()) {
                                MainBeautyShot.this.processJpegCallbackAfter(MainBeautyShot.this.savePictureInJpegCallback(finalJpegData));
                                return;
                            }
                            CamLog.e(FaceDetector.TAG, "savePicture() not enough memory!");
                            MainBeautyShot.this.mGet.toast((int) R.string.sp_not_enough_memory_NORMAL);
                            MainBeautyShot.this.mGet.setInCaptureProgress(false);
                            FileNamer.get().setErrorFeedback(MainBeautyShot.this.mGet.getApplicationMode());
                            if (MainBeautyShot.this.mGet.checkAutoReviewOff(false)) {
                                MainBeautyShot.this.mGet.checkStorage(true);
                            } else {
                                MainBeautyShot.this.mGet.startPreview(null, true);
                                MainBeautyShot.this.mGet.doCommandUi(Command.DISPLAY_PREVIEW);
                                MainBeautyShot.this.mGet.checkStorage(false);
                            }
                            MainBeautyShot.this.releaseBeautyshotJpegCallback();
                        }
                    });
                }
            });
            beautyShotThread.start();
            beautyShotThread.setPriority(10);
            CamLog.d(FaceDetector.TAG, "JpegPictureCallback()-end");
            return;
        }
        CamLog.v(FaceDetector.TAG, "JpegPictureCallback()-end, null -> return or pausing.");
    }

    protected byte[] processFinalJpegData(byte[] jpegData) {
        int ola_Orientation = this.mGet.getParameteredRotation() == 0 ? 0 : 3;
        if (jpegData == null || this.mGet.getApplicationMode() != 0) {
            return null;
        }
        return makeBeautyShotImage(jpegData, Ola_ImageFormat.RGB_LABEL, ola_Orientation, false);
    }

    protected boolean savePictureInJpegCallback(byte[] finalJpegData) {
        return super.savePictureInJpegCallback(finalJpegData);
    }

    protected void processJpegCallbackAfter(boolean isSuccessSave) {
        this.mGet.deleteProgressDialog();
        super.processJpegCallbackAfter(isSuccessSave);
        releaseBeautyshotJpegCallback();
    }

    public void releaseBeautyshotJpegCallback() {
        CamLog.d(FaceDetector.TAG, "MainBeautyShot - release engine");
        this.mGet.setBeautyshotProgress(false);
        if (this.mGet.isPausing()) {
            this.mGet.releaseEngine(false);
        }
    }
}
