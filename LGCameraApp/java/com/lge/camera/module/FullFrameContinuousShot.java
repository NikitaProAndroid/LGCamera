package com.lge.camera.module;

import android.hardware.Camera;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.BurstShotProgressBar;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.listeners.JpegPictureCallback;
import com.lge.camera.listeners.JpegPictureCallback.JpegCallbackFunction;
import com.lge.camera.listeners.ShutterCallback;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.FileNamer;
import com.lge.camera.util.Util;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.library.FaceDetector;

public class FullFrameContinuousShot extends Module implements JpegCallbackFunction {
    private long freeSpace;
    private boolean isStorageFull;
    private boolean isViewInitialized;
    private long jpeg_maximum_size;
    private View mBurstShotProgressView;
    private int mCallbackCountFornVidia;
    private int mContinuousShotCount;
    private boolean mContinuousShotStopped;
    private boolean mIsFullFrameContinuousShotRunning;
    private Object mTakePictureLock;
    private int max_count;
    private long safeDamper;

    public FullFrameContinuousShot(ControllerFunction function) {
        super(function);
        this.mContinuousShotCount = 0;
        this.mIsFullFrameContinuousShotRunning = false;
        this.mContinuousShotStopped = false;
        this.isViewInitialized = false;
        this.max_count = FunctionProperties.isSupportBurstShot() ? 20 : 6;
        this.jpeg_maximum_size = 7340032;
        this.freeSpace = 0;
        this.safeDamper = 0;
        this.mBurstShotProgressView = null;
        this.isStorageFull = false;
        this.mCallbackCountFornVidia = 0;
        this.mTakePictureLock = new Object();
        setBurstShotView();
    }

    public void setBurstShotView() {
        if (FunctionProperties.isSupportBurstShot()) {
            this.mBurstShotProgressView = this.mGet.inflateView(R.layout.burstshotprogress);
            LayoutParams lp = this.mBurstShotProgressView.findViewById(R.id.burstshotprogress_bar).getLayoutParams();
            ((BurstShotProgressBar) this.mBurstShotProgressView.findViewById(R.id.burstshotprogress_bar)).initBurstShotProgressBar(lp.width, lp.height, this.max_count);
            String maxCount = " / " + String.format("%d", new Object[]{Integer.valueOf(this.max_count)});
            ((TextView) this.mBurstShotProgressView.findViewById(R.id.burstshot_count)).setText(Integer.toString(this.mContinuousShotCount));
            ((TextView) this.mBurstShotProgressView.findViewById(R.id.burstshot_maxcount)).setText(maxCount);
            this.isViewInitialized = true;
        }
    }

    public void updateUIView() {
        if (FunctionProperties.isSupportBurstShot() && !this.mGet.isPausing()) {
            CamLog.d(FaceDetector.TAG, "Update view - " + this.mContinuousShotCount);
            if (!this.isViewInitialized) {
                setBurstShotView();
            }
            if (this.mBurstShotProgressView != null) {
                ViewGroup vgInit = (ViewGroup) this.mGet.findViewById(R.id.init);
                if (vgInit != null && vgInit.indexOfChild(this.mBurstShotProgressView) == -1) {
                    vgInit.invalidate();
                    vgInit.addView(this.mBurstShotProgressView);
                    setLayoutParams(this.mGet.getOrientationDegree());
                    this.mBurstShotProgressView.requestLayout();
                }
                ((RotateLayout) this.mBurstShotProgressView.findViewById(R.id.burstshotprogress_rotatelayout)).rotateLayout(this.mGet.getOrientationDegree());
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        FullFrameContinuousShot.this.mGet.removePostRunnable(this);
                        BurstShotProgressBar bpb = (BurstShotProgressBar) FullFrameContinuousShot.this.mGet.findViewById(R.id.burstshotprogress_bar);
                        TextView tv = (TextView) FullFrameContinuousShot.this.mBurstShotProgressView.findViewById(R.id.burstshot_count);
                        if (bpb != null && tv != null) {
                            bpb.setProgress(FullFrameContinuousShot.this.mContinuousShotCount);
                            tv.setText(String.format("%d", new Object[]{Integer.valueOf(FullFrameContinuousShot.this.mContinuousShotCount)}));
                        }
                    }
                });
            }
        }
    }

    private void setLayoutParams(int degree) {
        if (this.mBurstShotProgressView != null) {
            int previewPanelWidth = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.preview_panel_width);
            int bottomMargin = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.progress_burst_marginBottom);
            int previewPanelMarginBottom = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.preview_panel_marginBottom);
            RelativeLayout.LayoutParams pp = (RelativeLayout.LayoutParams) this.mBurstShotProgressView.getLayoutParams();
            if (pp != null) {
                Common.resetLayoutParameter(pp);
                if (Util.isEqualDegree(this.mGet.getResources(), degree, 0)) {
                    if (this.mGet.isConfigureLandscape()) {
                        pp.addRule(12, 1);
                        pp.addRule(14, 1);
                        pp.bottomMargin = bottomMargin;
                    } else {
                        pp.addRule(20, 1);
                        pp.addRule(15, 1);
                        pp.leftMargin = bottomMargin;
                    }
                } else if (Util.isEqualDegree(this.mGet.getResources(), degree, 90)) {
                    if (this.mGet.isConfigureLandscape()) {
                        pp.addRule(21, 1);
                        pp.addRule(15, 1);
                        pp.rightMargin = previewPanelWidth + previewPanelMarginBottom;
                    } else {
                        pp.addRule(12, 1);
                        pp.addRule(14, 1);
                        pp.bottomMargin = previewPanelWidth + previewPanelMarginBottom;
                    }
                } else if (Util.isEqualDegree(this.mGet.getResources(), degree, MediaProviderUtils.ROTATION_180)) {
                    if (this.mGet.isConfigureLandscape()) {
                        pp.addRule(12, 1);
                        pp.addRule(14, 1);
                        pp.bottomMargin = bottomMargin;
                    } else {
                        pp.addRule(20, 1);
                        pp.addRule(15, 1);
                        pp.leftMargin = bottomMargin;
                    }
                } else if (this.mGet.isConfigureLandscape()) {
                    pp.addRule(21, 1);
                    pp.addRule(15, 1);
                    pp.rightMargin = previewPanelWidth + previewPanelMarginBottom;
                } else {
                    pp.addRule(12, 1);
                    pp.addRule(14, 1);
                    pp.bottomMargin = previewPanelWidth + previewPanelMarginBottom;
                }
                this.mBurstShotProgressView.setLayoutParams(pp);
            }
        }
    }

    public void removewBurstShotView() {
        if (FunctionProperties.isSupportBurstShot()) {
            this.mGet.postOnUiThread(new Runnable() {
                public void run() {
                    FullFrameContinuousShot.this.mGet.removePostRunnable(this);
                    if (FullFrameContinuousShot.this.mBurstShotProgressView != null) {
                        BurstShotProgressBar bpb = (BurstShotProgressBar) FullFrameContinuousShot.this.mGet.findViewById(R.id.burstshotprogress_bar);
                        TextView tv = (TextView) FullFrameContinuousShot.this.mBurstShotProgressView.findViewById(R.id.burstshot_count);
                        if (!(bpb == null || tv == null)) {
                            bpb.setProgress(0);
                            tv.setText(Integer.toString(0));
                        }
                        ViewGroup vgInit = (ViewGroup) FullFrameContinuousShot.this.mGet.findViewById(R.id.init);
                        if (!(vgInit == null || vgInit.indexOfChild(FullFrameContinuousShot.this.mBurstShotProgressView) == -1)) {
                            vgInit.removeView(FullFrameContinuousShot.this.mBurstShotProgressView);
                        }
                        FullFrameContinuousShot.this.mBurstShotProgressView = null;
                        FullFrameContinuousShot.this.isViewInitialized = false;
                    }
                }
            }, 500);
        }
    }

    public void startRotation(int degree) {
        if (FunctionProperties.isSupportBurstShot()) {
            setLayoutParams(degree);
            if (this.mBurstShotProgressView != null) {
                ((RotateLayout) this.mBurstShotProgressView.findViewById(R.id.burstshotprogress_rotatelayout)).rotateLayout(this.mGet.getOrientationDegree());
            }
        }
    }

    public void setIsRunning(boolean isRunning) {
        this.mIsFullFrameContinuousShotRunning = isRunning;
    }

    public boolean checkCurrentShotMode() {
        return this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS);
    }

    public boolean isRunning() {
        return this.mIsFullFrameContinuousShotRunning;
    }

    public boolean takePicture() {
        CamLog.d(FaceDetector.TAG, "TIME_CHECK [Module]FullFrameContinuousShot::takePicture-start");
        this.mContinuousShotCount = 0;
        this.mCallbackCountFornVidia = 0;
        this.isStorageFull = false;
        this.mGet.clearFocusState();
        this.mGet.hideFocus();
        this.mGet.getCameraDevice().setPreviewCallback(null);
        this.mContinuousShotStopped = false;
        if (this.mGet.findViewById(R.id.main_button).isPressed() || !FunctionProperties.isSupportBurstShot()) {
            this.mGet.setBurstShotStop(false);
        } else {
            this.mGet.setBurstShotStop(true);
        }
        int[] max_resolution = getMaxResolutionWidthHeight();
        this.safeDamper = this.mGet.getCurrentStorage() == 0 ? 1048576 : CameraConstants.INTERNAL_MEMORY_SAFE_FREE_SPACE;
        this.freeSpace = (this.mGet.getFreeSpace() - this.safeDamper) - (((long) this.mGet.getQueueCount()) * ((long) getAverageSpace(max_resolution[0], max_resolution[1])));
        if (this.freeSpace < 0) {
            this.freeSpace = 0;
        }
        CamLog.d(FaceDetector.TAG, "FullFrameContinuous free space :" + this.freeSpace + " Storage : " + this.mGet.getCurrentStorage());
        CamLog.d(FaceDetector.TAG, "FullFrameContinuous #### Device().takePicture()");
        ShutterCallback shutterCallBack = null;
        if (!FunctionProperties.isSupportBurstShot()) {
            shutterCallBack = new ShutterCallback(this.mGet);
        }
        synchronized (this.mTakePictureLock) {
            try {
                this.mGet.getCameraDevice().takePicture(shutterCallBack, null, null, new JpegPictureCallback(this));
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "FullFrameContinuousShot - takePicture  failed :" + e);
                handleTakePictureError(R.string.error_occurred);
            }
        }
        updateUIView();
        setIsRunning(true);
        this.mGet.setPreviewing(false);
        this.mGet.setInCaptureProgress(true);
        this.mGet.setQuickFunctionMenuForcedDisable(true);
        this.mGet.setQuickButtonForcedDisable(true);
        CamLog.d(FaceDetector.TAG, "[Module]FullFrameContinuousShot::takePicture-end");
        return true;
    }

    public void jpegCallbackOnPictureTaken(byte[] jpegData, Camera camera) {
        CamLog.d(FaceDetector.TAG, "#### TIME_CHECK JpegPictureCallback-FullFrameContinuousShot()-start");
        this.mCallbackCountFornVidia++;
        if (!checkMediator()) {
            return;
        }
        if (this.mContinuousShotStopped || this.isStorageFull) {
            CamLog.d(FaceDetector.TAG, "mContinoushShotStopped JpegCallBack return");
            return;
        }
        this.mGet.setCaptureData(null);
        this.mGet.setImageRotationDegree(this.mGet.getDeviceDegree());
        if (jpegData == null) {
            CamLog.e(FaceDetector.TAG, "error!! FullFrameContinuousShot-onPictureTaken (jpegData == null)");
            boolean isSuccessSave = false;
            if (ProjectVariables.useJpegPictureCallbackError()) {
                jpegPictureCallbackError();
                return;
            }
        }
        CamLog.i(FaceDetector.TAG, "finalJpegData size [" + jpegData.length + "]");
        if (!(jpegData[0] == (byte) -1 && jpegData[1] == (byte) -40) && ProjectVariables.useJpegPictureCallbackError()) {
            CamLog.d(FaceDetector.TAG, "error!! FullFrameContinuousShot abnormal jpegData stream");
            jpegPictureCallbackError();
            return;
        } else if (((long) jpegData.length) < this.freeSpace) {
            this.mContinuousShotCount++;
            if (this.mContinuousShotCount == 1 && FunctionProperties.isSupportBurstShot()) {
                if (isRunning() && this.mGet.findViewById(R.id.main_button).isPressed()) {
                    this.mGet.playBurstShotShutterSound(true);
                } else {
                    this.mGet.playBurstShotShutterSound(false);
                }
            }
            updateUIView();
            if (this.mContinuousShotCount == this.max_count || this.mGet.isBurstShotStop()) {
                CamLog.d(FaceDetector.TAG, "FullFrameContinuous add imagesaver make thumbnail");
                if (this.mContinuousShotCount == 1) {
                    isSuccessSave = this.mGet.saveImageSavers(jpegData, null, this.mGet.getDeviceDegree(), true, true);
                } else {
                    isSuccessSave = this.mGet.saveImageSavers(jpegData, null, this.mGet.getDeviceDegree(), true, false);
                }
                this.mContinuousShotStopped = true;
            } else if (this.freeSpace - ((long) jpegData.length) < this.jpeg_maximum_size || this.mContinuousShotCount == 1) {
                CamLog.d(FaceDetector.TAG, "FullFrameContinuous add imagesaver make thumbnail");
                if (this.mContinuousShotCount == 1) {
                    isSuccessSave = this.mGet.saveImageSavers(jpegData, null, this.mGet.getDeviceDegree(), true, true);
                } else {
                    isSuccessSave = this.mGet.saveImageSavers(jpegData, null, this.mGet.getDeviceDegree(), true, false);
                }
            } else if (this.mContinuousShotCount == 1) {
                isSuccessSave = this.mGet.saveImageSavers(jpegData, null, this.mGet.getDeviceDegree(), false, true);
            } else {
                isSuccessSave = this.mGet.saveImageSavers(jpegData, null, this.mGet.getDeviceDegree(), false, false);
            }
            if (isSuccessSave) {
                this.freeSpace -= (long) jpegData.length;
                CamLog.d(FaceDetector.TAG, "FullFrameContinuous free space : " + this.freeSpace);
            }
        } else if (!ModelProperties.isNVIDIAChipset() || this.mCallbackCountFornVidia >= this.max_count) {
            CamLog.e(FaceDetector.TAG, "savePicture() not enough memory!");
            this.isStorageFull = true;
            handleTakePictureError(this.mGet.getCurrentStorage() == 0 ? R.string.sp_storage_full_message_not_on_sd_NORMAL : R.string.sp_storage_full_message_not_on_internal_NORMAL);
            return;
        } else {
            CamLog.d(FaceDetector.TAG, "waiting maxcountTH jpeg callback for nvidia");
            return;
        }
        if (isSuccessSave) {
            if (!(FunctionProperties.isSupportBurstShot() || this.mContinuousShotCount != 1 || "0".equals(this.mGet.getSettingValue(Setting.KEY_ZOOM)))) {
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        FullFrameContinuousShot.this.mGet.removePostRunnable(this);
                        FullFrameContinuousShot.this.mGet.showSavingProgressDialog();
                    }
                });
            }
            processJpegCallbackAfter();
            CamLog.d(FaceDetector.TAG, "JpegPictureCallback()-end");
            return;
        }
        CamLog.e(FaceDetector.TAG, "savePicture() fail!");
        handleTakePictureError(R.string.error_write_file);
    }

    public void stopByUserAction() {
        CamLog.d(FaceDetector.TAG, "stopFullFrameContinuousShotUserAction");
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                FullFrameContinuousShot.this.mGet.removePostRunnable(this);
                FullFrameContinuousShot.this.mGet.enableCommand(false);
            }
        });
        this.mGet.setBurstShotStop(true);
        this.mGet.setQuickFunctionMenuForcedDisable(false);
        this.mGet.setQuickButtonForcedDisable(false);
        setIsRunning(false);
        removewBurstShotView();
    }

    public void stopByOnPause() {
        CamLog.d(FaceDetector.TAG, "stopFullFrameContinuousShotOnPause");
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                FullFrameContinuousShot.this.mGet.removePostRunnable(this);
                FullFrameContinuousShot.this.mGet.enableCommand(false);
            }
        });
        this.mGet.setBurstShotStop(true);
        this.mGet.setQuickFunctionMenuForcedDisable(false);
        this.mGet.setQuickButtonForcedDisable(false);
        this.mGet.stopBurstShotSound();
        if (FunctionProperties.isSupportBurstShot() && isRunning()) {
            this.mGet.setInCaptureProgress(false);
            CamLog.d(FaceDetector.TAG, "BurstShot CancelPicture Start");
            this.mGet.getLG().cancelPicture();
            CamLog.d(FaceDetector.TAG, "BurstShot CancelPicture End");
            removewBurstShotView();
        }
        setIsRunning(false);
    }

    private void processJpegCallbackAfter() {
        CamLog.i(FaceDetector.TAG, "mContinuousShotCount = " + this.mContinuousShotCount);
        if (this.mContinuousShotCount >= this.max_count || this.mGet.isBurstShotStop()) {
            CamLog.i(FaceDetector.TAG, "Continous shot finish process!");
            if (FunctionProperties.isSupportBurstShot()) {
                if (this.mContinuousShotCount < this.max_count) {
                    this.mGet.stopBurstShotSound();
                } else if (ModelProperties.isRenesasISP()) {
                    this.mGet.stopBurstShotSound();
                } else {
                    this.mGet.getHandler().postDelayed(new Runnable() {
                        public void run() {
                            FullFrameContinuousShot.this.mGet.stopBurstShotSound();
                        }
                    }, 100);
                }
            }
            if (this.mContinuousShotCount >= this.max_count) {
                this.mGet.enableCommand(false);
            }
            this.mGet.showSavingProgressDialog();
            setIsRunning(false);
            new Thread(new Runnable() {
                public void run() {
                    if (FunctionProperties.isSupportBurstShot()) {
                        CamLog.d(FaceDetector.TAG, "BurstShot CancelPicture Start");
                        FullFrameContinuousShot.this.mGet.getLG().cancelPicture();
                        CamLog.d(FaceDetector.TAG, "BurstShot CancelPicture End");
                    }
                    if (FullFrameContinuousShot.this.mGet.checkAutoReviewOff(false)) {
                        synchronized (FullFrameContinuousShot.this.mTakePictureLock) {
                            FullFrameContinuousShot.this.mGet.startPreview(null, false);
                        }
                    }
                }
            }).start();
            new Thread(new Runnable() {
                public void run() {
                    if (!FullFrameContinuousShot.this.mGet.checkAutoReviewOff(false)) {
                        FullFrameContinuousShot.this.mGet.waitSaveImageThreadDone();
                    } else if (FunctionProperties.isSupportBurstShot()) {
                        FullFrameContinuousShot.this.mGet.waitAvailableQueueCount(25);
                    } else {
                        FullFrameContinuousShot.this.mGet.waitSaveImageThreadDone();
                    }
                    FullFrameContinuousShot.this.mGet.postOnUiThread(new Runnable() {
                        public void run() {
                            if (FullFrameContinuousShot.this.mGet != null) {
                                FullFrameContinuousShot.this.mGet.removePostRunnable(this);
                                FullFrameContinuousShot.this.mGet.setInCaptureProgress(false);
                                FullFrameContinuousShot.this.mContinuousShotCount = 0;
                                FullFrameContinuousShot.this.removewBurstShotView();
                                FullFrameContinuousShot.this.mGet.setQuickFunctionMenuForcedDisable(false);
                                FullFrameContinuousShot.this.mGet.setQuickButtonForcedDisable(false);
                                if (FullFrameContinuousShot.this.mGet.checkAutoReviewOff(false)) {
                                    FullFrameContinuousShot.this.mGet.deleteSavingProgressDialog();
                                    FullFrameContinuousShot.this.mGet.doCommandDelayed(Command.DISPLAY_PREVIEW, 0);
                                    if (FullFrameContinuousShot.this.mGet.getImageListUri().size() > 0) {
                                        FullFrameContinuousShot.this.mGet.getImageListUri().removeAll(FullFrameContinuousShot.this.mGet.getImageListUri());
                                    }
                                } else if (FullFrameContinuousShot.this.mGet.checkAutoReviewForQuickView()) {
                                    FullFrameContinuousShot.this.mGet.deleteSavingProgressDialog();
                                } else {
                                    FullFrameContinuousShot.this.mGet.doCommandUi(Command.DISPLAY_CAMERA_POSTVIEW);
                                }
                            }
                        }
                    });
                }
            }).start();
        }
    }

    protected void handleTakePictureError(int resource) {
        this.mGet.enableCommand(false);
        this.mGet.stopBurstShotSound();
        this.mGet.setBurstShotStop(true);
        this.mGet.setQuickFunctionMenuForcedDisable(false);
        this.mGet.setQuickButtonForcedDisable(false);
        setIsRunning(false);
        this.mGet.toast(resource);
        removewBurstShotView();
        FileNamer.get().setErrorFeedback(this.mGet.getApplicationMode());
        processJpegCallbackAfter();
        this.mGet.checkStorage(false);
    }

    private double getAverageSpace(int width, int height) {
        return (((double) (width * height)) * 0.3d) * Util.getPictureSizeScale(this.mGet.getCameraId(), this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE), ModelProperties.getProjectCode(), this.mGet.getSettingValue(Setting.KEY_CAMERA_PICTURESIZE));
    }

    private int[] getMaxResolutionWidthHeight() {
        return Util.SizeString2WidthHeight((String) this.mGet.findPreference(Setting.KEY_CAMERA_PICTURESIZE).getEntryValues()[0]);
    }
}
