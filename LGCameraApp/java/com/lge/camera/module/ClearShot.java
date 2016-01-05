package com.lge.camera.module;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.listeners.JpegPictureCallback;
import com.lge.camera.listeners.JpegPictureCallback.JpegCallbackFunction;
import com.lge.camera.listeners.ShutterCallback;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.FileNamer;
import com.lge.olaworks.library.FaceDetector;

public class ClearShot extends Module implements JpegCallbackFunction {
    private int iDegree;
    private boolean isCaptureFailed;
    private long mCheckShotTimeInterval;
    private int mClearCount;
    private View mGuideImageView;
    private boolean mIsClearShotTaking;
    private boolean mStopByUser;

    public ClearShot(ControllerFunction function) {
        super(function);
        this.mClearCount = 0;
        this.mCheckShotTimeInterval = 0;
        this.mStopByUser = false;
        this.mIsClearShotTaking = false;
        this.iDegree = 0;
        this.isCaptureFailed = false;
        this.mGuideImageView = null;
    }

    public boolean checkCurrentShotMode() {
        return this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT);
    }

    public boolean isRunning() {
        return this.mIsClearShotTaking;
    }

    public void stopByUserAction() {
        this.mStopByUser = true;
        this.mGet.stopClearShotSound();
        removewClearShotView();
        this.mIsClearShotTaking = false;
    }

    public boolean takePicture() {
        CamLog.d(FaceDetector.TAG, "TIME_CHECK [Module]ClearShot::takePicture-start");
        this.mClearCount = 0;
        this.mStopByUser = false;
        this.mIsClearShotTaking = true;
        this.mGet.removeAllImageList();
        this.mGet.getCameraDevice().setPreviewCallback(null);
        CamLog.d(FaceDetector.TAG, "#### Device().takePicture()");
        this.mGet.setEnable3ALocks(null, true);
        showClearShotView();
        deviceTakePicture(0);
        this.isCaptureFailed = false;
        this.mGet.setPreviewing(false);
        this.mGet.setInCaptureProgress(true);
        this.mGet.setMainButtonDisable();
        CamLog.d(FaceDetector.TAG, "[Module]ClearShot::takePicture-end");
        return true;
    }

    private void deviceTakePicture(long delay) {
        this.mGet.postOnUiThread(new Runnable() {
            public void run() {
                ClearShot.this.mGet.removePostRunnable(this);
                try {
                    ClearShot.this.mCheckShotTimeInterval = System.currentTimeMillis();
                    if (!ClearShot.this.mGet.isPausing() && ClearShot.this.mGet.getCameraDevice() != null) {
                        if (ClearShot.this.mClearCount == 0) {
                            ClearShot.this.iDegree = ClearShot.this.mGet.getDeviceDegree();
                        } else if (ClearShot.this.iDegree != ClearShot.this.mGet.getDeviceDegree()) {
                            ClearShot.this.isCaptureFailed = true;
                        }
                        ClearShot.this.playSound();
                        ClearShot.this.mGet.getCameraDevice().takePicture(new ShutterCallback(ClearShot.this.mGet), null, null, new JpegPictureCallback(ClearShot.this));
                    }
                } catch (Exception e) {
                    CamLog.e(FaceDetector.TAG, "ClearShot-takePicture  failed :" + e);
                    ClearShot.this.handleTakePictureError(R.string.error_occurred);
                }
            }
        }, delay);
    }

    public void jpegCallbackOnPictureTaken(byte[] jpegData, Camera camera) {
        CamLog.d(FaceDetector.TAG, "#### TIME_CHECK JpegPictureCallbackClearShot()-start");
        if (checkMediator()) {
            boolean isSuccessSave;
            this.mGet.setCaptureData(null);
            this.mGet.setImageRotationDegree(this.mGet.getDeviceDegree());
            if (jpegData == null) {
                CamLog.e(FaceDetector.TAG, "error!! ClearShot-onPictureTaken (jpegData == null)");
                isSuccessSave = false;
                if (ProjectVariables.useJpegPictureCallbackError()) {
                    jpegPictureCallbackError();
                    return;
                }
            }
            CamLog.i(FaceDetector.TAG, "finalJpegData size [" + jpegData.length + "]");
            if (((long) jpegData.length) < this.mGet.getFreeSpace()) {
                this.mGet.waitSaveImageThreadDone();
                if (this.mClearCount == 0) {
                    CamLog.i(FaceDetector.TAG, "ClearShotFileDeleted ? = " + this.mGet.deleteClearShotImages());
                }
                this.mClearCount++;
                isSuccessSave = this.mGet.saveClearShotPicture(jpegData, this.mClearCount);
            } else {
                CamLog.e(FaceDetector.TAG, "savePicture() not enough memory!");
                handleTakePictureError(R.string.sp_not_enough_memory_NORMAL);
                return;
            }
            if (isSuccessSave) {
                if (this.isCaptureFailed) {
                    this.mStopByUser = true;
                }
                processJpegCallbackAfter();
                CamLog.i(FaceDetector.TAG, "mClearCount = " + this.mClearCount);
                CamLog.d(FaceDetector.TAG, "JpegPictureCallbackmClearCount()-end");
                return;
            }
            CamLog.e(FaceDetector.TAG, "savePicture() fail!");
            handleTakePictureError(R.string.error_write_file);
        }
    }

    private void processJpegCallbackAfter() {
        if (this.mStopByUser) {
            this.mGet.setQuickFunctionMenuForcedDisable(false);
            this.mGet.setQuickButtonForcedDisable(false);
            removewClearShotView();
            this.mGet.stopClearShotSound();
            if (this.isCaptureFailed) {
                this.mGet.toast((int) R.string.sp_clear_shot_capture_failed);
            }
            this.mGet.waitSaveImageThreadDone();
            this.mGet.deleteClearShotImages();
            this.mStopByUser = false;
            this.mGet.startPreview(null, true);
            this.mGet.doCommandUi(Command.DISPLAY_PREVIEW);
            this.mGet.checkStorage(false);
            this.mGet.setEnable3ALocks(null, false);
            this.mClearCount = 0;
            this.mIsClearShotTaking = false;
            this.mGet.setInCaptureProgress(false);
        } else if (this.mClearCount >= 6) {
            removewClearShotView();
            this.mGet.waitSaveImageThreadDone();
            Bundle bundle = new Bundle();
            bundle.putBoolean("useClearShotPostview", true);
            this.mGet.doCommandUi(Command.DISPLAY_CAMERA_POSTVIEW, bundle);
            this.mGet.setEnable3ALocks(null, false);
            this.mClearCount = 0;
            this.mIsClearShotTaking = false;
        } else if (!this.mGet.isPausing()) {
            long currentInterval = System.currentTimeMillis() - this.mCheckShotTimeInterval;
            long delayTime = 0;
            if (currentInterval <= 650) {
                delayTime = 650 - currentInterval;
            }
            CamLog.d(FaceDetector.TAG, "delayTime = " + delayTime);
            deviceTakePicture(delayTime);
        }
    }

    protected void handleTakePictureError(int resource) {
        this.mGet.setInCaptureProgress(false);
        this.mGet.toast(resource);
        CamLog.d(FaceDetector.TAG, "Current mClearCount count : " + this.mClearCount);
        this.mClearCount = 0;
        this.mIsClearShotTaking = false;
        FileNamer.get().setErrorFeedback(this.mGet.getApplicationMode());
        removewClearShotView();
        this.mGet.startPreview(null, true);
        this.mGet.doCommandUi(Command.DISPLAY_PREVIEW);
        this.mGet.checkStorage(false);
    }

    public void playSound() {
        if (this.mClearCount == 0) {
            this.mGet.playClearShotShutterSound(true);
        } else if (this.mClearCount == 5) {
            this.mGet.playClearShotShutterSound(false);
        }
    }

    public void showClearShotView() {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                ClearShot.this.mGet.removePostRunnable(this);
                if (ClearShot.this.mGuideImageView == null) {
                    ClearShot.this.mGuideImageView = ClearShot.this.mGet.inflateView(R.layout.clearshot_guide);
                    ViewGroup vg = (ViewGroup) ClearShot.this.mGet.findViewById(R.id.preview);
                    if (vg != null && vg.indexOfChild(ClearShot.this.mGuideImageView) == -1) {
                        vg.invalidate();
                        vg.addView(ClearShot.this.mGuideImageView);
                        ClearShot.this.startRotation(ClearShot.this.mGet.getOrientationDegree());
                    }
                }
            }
        });
    }

    public void removewClearShotView() {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                ClearShot.this.mGet.removePostRunnable(this);
                if (ClearShot.this.mGuideImageView != null) {
                    ViewGroup vg = (ViewGroup) ClearShot.this.mGet.findViewById(R.id.preview);
                    if (!(vg == null || vg.indexOfChild(ClearShot.this.mGuideImageView) == -1)) {
                        vg.removeView(ClearShot.this.mGuideImageView);
                    }
                    ClearShot.this.mGuideImageView = null;
                }
            }
        });
    }

    public void startRotation(int degree) {
        if (FunctionProperties.isClearShotSupported() && this.mGuideImageView != null) {
            ((RotateLayout) this.mGuideImageView.findViewById(R.id.clearshot_guide_rotate)).rotateLayout(this.mGet.getOrientationDegree());
        }
    }
}
