package com.lge.camera;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.lge.camera.command.Command;
import com.lge.camera.components.CameraPreview;
import com.lge.camera.components.OpenGLSurfaceView;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.controller.BubblePopupController;
import com.lge.camera.controller.GestureShutterController;
import com.lge.camera.controller.GestureShutterController.OnGestureRecogListener;
import com.lge.camera.controller.camera.CameraFocusController;
import com.lge.camera.controller.camera.CameraIndicatorController;
import com.lge.camera.controller.camera.CameraPreviewController;
import com.lge.camera.controller.camera.CameraQuickFunctionController;
import com.lge.camera.controller.camera.CameraShotModeMenuController;
import com.lge.camera.controller.camera.CameraStorageController;
import com.lge.camera.controller.camera.FacePreviewController;
import com.lge.camera.controller.camera.FreePanoramaController;
import com.lge.camera.controller.camera.PanoramaController;
import com.lge.camera.controller.camera.PlanePanoramaController;
import com.lge.camera.controller.camera.TimerController;
import com.lge.camera.module.ContinuousShot;
import com.lge.camera.module.FullFrameContinuousShot;
import com.lge.camera.module.Module;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.CameraSettingMenu;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AppControlUtil;
import com.lge.camera.util.BitmapManager;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.ExifUtil;
import com.lge.camera.util.FileNamer;
import com.lge.camera.util.ImageManager;
import com.lge.camera.util.ImageSavers;
import com.lge.camera.util.ImageSavers.ImageSaverCallback;
import com.lge.camera.util.SaveRequest;
import com.lge.camera.util.SecureImageUtil;
import com.lge.camera.util.SharedPreferenceUtil;
import com.lge.camera.util.Util;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.FaceBeauty;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class CameraMediator extends Mediator implements ImageSaverCallback {
    private boolean isCaptureIntent;
    private boolean mBurstShotStop;
    private boolean mContinuousShotAlived;
    private String mCropValue;
    private int mCurrentIAMode;
    protected FacePreviewController mFacePreviewController;
    private final int[][] mFaceValues;
    private float mFocalLength;
    protected FreePanoramaController mFreePanoramaController;
    protected OnGestureRecogListener mGestureRecogListener;
    private ArrayList<Integer> mImageListRotation;
    private ArrayList<Uri> mImageListUri;
    private ImageSavers mImageSaver;
    private String mIntentFrom;
    protected PanoramaController mPanoramaController;
    protected PlanePanoramaController mPlanePanoramaController;
    private String mPreSmartModePicSize;
    private String mPreviousCameraAutoReviewValue;
    private String mPreviousPictureSize;
    private String mPreviousShotMode;
    private boolean mRefocusShotHasPictures;
    private boolean mRequestSingleImage;
    private boolean mTimemachineHasPictures;
    protected TimerController mTimerController;

    public CameraMediator(CameraApp activity) {
        super(activity);
        this.mPreviousShotMode = CameraConstants.TYPE_SHOTMODE_NORMAL;
        this.mBurstShotStop = false;
        this.mImageListUri = new ArrayList();
        this.mImageListRotation = new ArrayList();
        this.mImageSaver = null;
        this.mTimemachineHasPictures = false;
        this.mRefocusShotHasPictures = false;
        this.mPreSmartModePicSize = null;
        this.mCurrentIAMode = 0;
        this.mPreviousCameraAutoReviewValue = null;
        this.mRequestSingleImage = false;
        this.isCaptureIntent = false;
        this.mFaceValues = new int[][]{new int[]{0, 0}, new int[]{1, 8}, new int[]{2, 16}, new int[]{2, 24}, new int[]{3, 32}, new int[]{3, 36}, new int[]{4, 40}, new int[]{5, 44}, new int[]{5, 48}};
        this.mGestureRecogListener = new OnGestureRecogListener() {
            public void doTimershotByGestureRecog() {
                if (CameraMediator.this.mPreviewPanelController != null) {
                    CameraMediator.this.mTimerController.setGestureShotActivated(true);
                    CameraMediator.this.mPreviewPanelController.gestureCallbackTakePicture();
                }
            }
        };
        setApplicationMode(0);
    }

    public PanoramaController getPanoramaController() {
        return this.mPanoramaController;
    }

    public FreePanoramaController getFreePanoramaController() {
        return this.mFreePanoramaController;
    }

    public PlanePanoramaController getPlanePanoramaController() {
        return this.mPlanePanoramaController;
    }

    public CameraFocusController getFocusController() {
        return (CameraFocusController) this.mFocusController;
    }

    public CameraIndicatorController getIndicatorController() {
        return (CameraIndicatorController) this.mIndicatorController;
    }

    public CameraPreviewController getPreviewController() {
        return (CameraPreviewController) this.mPreviewController;
    }

    public CameraStorageController getStorageController() {
        return (CameraStorageController) this.mStorageController;
    }

    public TimerController getTimerController() {
        return this.mTimerController;
    }

    public FacePreviewController getFacePreviewController() {
        return this.mFacePreviewController;
    }

    public CameraQuickFunctionController getQuickFunctionController() {
        return (CameraQuickFunctionController) this.mQuickFunctionController;
    }

    protected void createPreviewController() {
        this.mPreviewController = new CameraPreviewController(this);
    }

    public CameraShotModeMenuController getShotModeMenuController() {
        return (CameraShotModeMenuController) this.mShotModeMenuController;
    }

    protected void createControllers() {
        if (this.mPreviewController == null) {
            this.mPreviewController = new CameraPreviewController(this);
        }
        this.mControllers.add(this.mPreviewController);
        this.mQuickFunctionController = new CameraQuickFunctionController(this);
        this.mControllers.add(this.mQuickFunctionController);
        this.mIndicatorController = new CameraIndicatorController(this);
        this.mControllers.add(this.mIndicatorController);
        this.mFocusController = new CameraFocusController(this);
        this.mControllers.add(this.mFocusController);
        this.mStorageController = new CameraStorageController(this);
        this.mControllers.add(this.mStorageController);
        this.mPanoramaController = new PanoramaController(this);
        this.mControllers.add(this.mPanoramaController);
        this.mFreePanoramaController = new FreePanoramaController(this);
        this.mControllers.add(this.mFreePanoramaController);
        this.mPlanePanoramaController = new PlanePanoramaController(this);
        this.mControllers.add(this.mPlanePanoramaController);
        this.mTimerController = new TimerController(this);
        this.mControllers.add(this.mTimerController);
        this.mFacePreviewController = new FacePreviewController(this);
        this.mControllers.add(this.mFacePreviewController);
        this.mShotModeMenuController = new CameraShotModeMenuController(this);
        this.mControllers.add(this.mShotModeMenuController);
        this.mGestureShutterController = new GestureShutterController(this);
        this.mControllers.add(this.mGestureShutterController);
        this.mGestureShutterController.setGestureRecogEngineListener(this.mGestureRecogListener);
        super.createControllers();
    }

    public void initControllers() {
        super.initControllers();
    }

    public void onCreate() {
        CamLog.d(FaceDetector.TAG, "onCreate()-start");
        this.mModule = this.mModuleFactory.getModule(Module.DEFAULT_NORMAL_SHOT);
        createControllers();
        super.onCreate();
        setupCaptureIntent();
        CamLog.d(FaceDetector.TAG, "onCreate()-end");
    }

    public void onResume() {
        CamLog.d(FaceDetector.TAG, "onResume()-start");
        this.mImageSaver = new ImageSavers(this, 40);
        if (isTimeMachineModeOn()) {
            setPreviousShotModeString(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE);
            deleteTimeMachineImages();
            getImageListUri().clear();
        } else {
            String shotMode = getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
            if (CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(shotMode)) {
                setPreviousShotModeString(CameraConstants.TYPE_SHOTMODE_NORMAL);
            } else {
                if (CameraConstants.TYPE_SHOTMODE_REFOCUS.equals(shotMode)) {
                    deleteRefocusShotImages();
                    getImageListUri().clear();
                }
                setPreviousShotModeString(shotMode);
            }
        }
        if (getCameraId() == 1 && checkSettingValue(Setting.KEY_LIGHT, CameraConstants.SMART_MODE_ON)) {
            setBackgroundColorWhite();
            Common.setBacklightToMax(getActivity());
        } else if (checkSettingValue(Setting.KEY_LIGHT, CameraConstants.SMART_MODE_OFF) || getCameraId() == 0) {
            setBackgroundColorBlack();
        }
        super.onResume();
        if ("com.lge.pa.action.CAMVOICE".equals(getActivity().getIntent().getAction())) {
            CamLog.d(FaceDetector.TAG, "Camera is called by QVOICE, So voice shutter on!! ");
            setSetting(Setting.KEY_VOICESHUTTER, CameraConstants.SMART_MODE_ON);
            doCommandUi(Command.SET_VOICE_SHUTTER);
            getActivity().getIntent().setAction(null);
        }
        if (getCameraId() == 0 && CameraConstants.SMART_MODE_ON.equals(getSettingValue(Setting.KEY_SMART_MODE))) {
            setQuickButtonMode(true);
        }
        if (this.mActivity.getPostviewRequestCode() == 2) {
            doAttach();
            this.mActivity.setPostviewRequestInitCode();
            return;
        }
        postOnUiThread(new Runnable() {
            public void run() {
                CameraMediator.this.removePostRunnable(this);
                if (!CameraMediator.this.isPausing()) {
                    int requestCode = CameraMediator.this.mActivity.getPostviewRequestCode();
                    CameraMediator.this.mActivity.setPostviewRequestInitCode();
                    switch (requestCode) {
                        case LGKeyRec.EVENT_NO_MATCH /*1*/:
                            CameraMediator.this.toast(CameraMediator.this.getString(R.string.popup_delete_done));
                        case LGKeyRec.EVENT_STOPPED /*4*/:
                            CameraMediator.this.toast(CameraMediator.this.getString(R.string.sp_saved_NORMAL));
                        default:
                    }
                }
            }
        });
        if (Common.isQuickWindowCameraMode()) {
            if (!CameraConstants.TYPE_SHOTMODE_NORMAL.equals(getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
                setSetting(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_NORMAL);
            }
            if (!LGT_Limit.ISP_AUTOMODE_AUTO.equals(getSettingValue(Setting.KEY_SCENE_MODE))) {
                setSetting(Setting.KEY_SCENE_MODE, LGT_Limit.ISP_AUTOMODE_AUTO);
            }
            if (!CameraConstants.SMART_MODE_OFF.equals(getSettingValue(Setting.KEY_SMART_MODE))) {
                setSetting(Setting.KEY_SMART_MODE, CameraConstants.SMART_MODE_OFF);
            }
            if (CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(getPreviousShotModeString())) {
                setPreviewEffectForBeautyShotMode(null, true);
            }
        }
        if (getCameraId() == 0) {
            setPreviousPictureSize(SharedPreferenceUtil.getMainPreviousPictureSize(getApplicationContext()));
        }
        CamLog.d(FaceDetector.TAG, "onResume()-end");
    }

    public void onPause() {
        CamLog.d(FaceDetector.TAG, "onPause()-start");
        this.mActivity.setPostviewRequestInitCode();
        stopShotModeOnPause();
        finishImageSaver();
        if (isTimeMachineModeOn() && getImageListUri().size() < 5 && getImageListUri().size() > 0) {
            getImageListUri().clear();
            deleteTimeMachineImages();
        }
        if (checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_REFOCUS) && getImageListUri().size() < 7 && getImageListUri().size() > 0) {
            getImageListUri().clear();
            deleteRefocusShotImages();
        }
        if (getCameraId() == 0 && CameraConstants.SMART_MODE_ON.equals(getSettingValue(Setting.KEY_SMART_MODE))) {
            this.mQuickButtonController.setSmartCameraMode(null, false);
        }
        stopContinuousShotOnPause();
        stopReceivingLocationUpdates();
        if (this.mFocusController != null) {
            this.mFocusController.onPause();
        }
        if (this.mPanoramaController != null) {
            stopPanorama();
        }
        if (getCameraId() == 0) {
            if (!isAttachIntent() && checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
                SharedPreferenceUtil.saveMainPreviousPictureSize(getApplicationContext(), getPreviousPictureSize());
            }
            if (checkSettingValue(Setting.KEY_LIGHT, CameraConstants.SMART_MODE_ON)) {
                setBackgroundColorBlack();
            }
        }
        if (getCameraId() == 1 && this.mGestureShutterController != null) {
            this.mGestureShutterController.releaseGestureEngine();
        }
        super.onPause();
        CamLog.d(FaceDetector.TAG, "onPause()-end");
    }

    private void stopContinuousShotOnPause() {
        boolean autoReview = false;
        if (checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CONTINUOUS) && getCurrentModule().isRunning()) {
            CamLog.d(FaceDetector.TAG, String.format("Continuous shot is running, stop it.", new Object[0]));
            try {
                stopByUserAction();
                getSoundController().stopSoundContinuous();
                ((ContinuousShot) getCurrentModule()).waitForSaveDone();
                if (CameraConstants.SMART_MODE_ON.equals(getSettingValue(Setting.KEY_CAMERA_AUTO_REVIEW)) || "on_delay_2sec".equals(getSettingValue(Setting.KEY_CAMERA_AUTO_REVIEW)) || "on_delay_5sec".equals(getSettingValue(Setting.KEY_CAMERA_AUTO_REVIEW))) {
                    autoReview = true;
                }
                if (getImageListUri().size() <= 0) {
                    doCommand(Command.DISPLAY_PREVIEW);
                } else if (getStorageController().isStorageAvailable() && autoReview) {
                    doCommand(Command.DISPLAY_CAMERA_POSTVIEW);
                } else {
                    doCommand(Command.DISPLAY_PREVIEW);
                }
            } catch (ClassCastException e) {
                CamLog.e(FaceDetector.TAG, "ClassCastException : ", e);
            } catch (NullPointerException e2) {
                CamLog.e(FaceDetector.TAG, "NullPointerException : ", e2);
            }
        }
    }

    private void stopShotModeOnPause() {
        if ((FunctionProperties.isFullFrameContinuousShotSupported() || FunctionProperties.isSupportBurstShot()) && checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS)) {
            CamLog.d(FaceDetector.TAG, "stopContinuousShotFullFrame");
            ((FullFrameContinuousShot) getCurrentModule()).stopByOnPause();
        }
        if (FunctionProperties.isClearShotSupported() && checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT)) {
            getCurrentModule().stopByUserAction();
        }
        if (FunctionProperties.isRefocusShotSupported() && checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_REFOCUS)) {
            getCurrentModule().stopByUserAction();
        }
    }

    public void onDestroy() {
        CamLog.d(FaceDetector.TAG, "onDestroy()-start");
        if (this.mImageListUri != null) {
            this.mImageListUri.clear();
            this.mImageListUri = null;
        }
        if (this.mImageListRotation != null) {
            this.mImageListRotation.clear();
            this.mImageListRotation = null;
        }
        this.mSavedImageUri = null;
        this.mCaptureData = null;
        this.mPanoramaController = null;
        this.mPlanePanoramaController = null;
        this.mFreePanoramaController = null;
        this.mTimerController = null;
        this.mFacePreviewController = null;
        this.mGestureShutterController = null;
        super.onDestroy();
        CamLog.d(FaceDetector.TAG, "onDestroy()-end");
    }

    public void setContinuousShotAlived(boolean alived) {
        this.mContinuousShotAlived = alived;
    }

    public boolean isContinuousShotAlived() {
        return this.mContinuousShotAlived;
    }

    public void setBurstShotStop(boolean stop) {
        this.mBurstShotStop = stop;
    }

    public boolean isBurstShotStop() {
        return this.mBurstShotStop;
    }

    public byte[] getCaptureData() {
        return this.mCaptureData;
    }

    public void setCaptureData(byte[] data) {
        this.mCaptureData = data;
    }

    public ArrayList<Uri> getImageListUri() {
        if (this.mImageListUri != null) {
            CamLog.i(FaceDetector.TAG, "mImageListUri = " + this.mImageListUri.size());
        }
        return this.mImageListUri;
    }

    public ArrayList<Integer> getImageListRotation() {
        return this.mImageListRotation;
    }

    public void removeAllImageList() {
        if (getImageListUri().size() > 0) {
            getImageListUri().removeAll(getImageListUri());
        }
        if (getImageListRotation().size() > 0) {
            getImageListRotation().removeAll(getImageListRotation());
        }
    }

    private String[] getFileNameAndExt() {
        String[] fileNameAndExt = new String[]{".tmp", CameraConstants.TIME_MACHINE_TEMPFILE_EXT};
        if (hasSaveURI() && !"content".equals(this.mSaveUri.getScheme())) {
            fileNameAndExt[0] = new File(this.mSaveUri.getPath()).getName();
            if (fileNameAndExt[0] != null) {
                fileNameAndExt[1] = fileNameAndExt[0].replaceAll("^[^.]+", "");
                fileNameAndExt[0] = fileNameAndExt[0].replaceFirst("\\.[a-zA-Z]+$", "");
            }
        }
        return fileNameAndExt;
    }

    public boolean saveTimeMachinePicture(byte[] data, int timeMachineTempFileCount) {
        CamLog.d(FaceDetector.TAG, "saveTimeMachinePicture()-start ");
        String fileExt = CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
        String timeMachineFilename = CameraConstants.TIME_MACHINE_TEMPFILE + Integer.toString(timeMachineTempFileCount);
        this.mImageRotationDegree = getDeviceDegree();
        String path = this.mStorageController.getTimeMachineStorageDirectory();
        boolean ret = ImageManager.saveTempFileForTimeMachineShot(data, path, timeMachineFilename, fileExt);
        Uri saveUri = Uri.fromFile(new File(path + timeMachineFilename + fileExt));
        if (ret) {
            this.mSavedFileName = timeMachineFilename;
            this.mImageListUri.add(saveUri);
        }
        CamLog.d(FaceDetector.TAG, "saveTimeMachinePicture()-end, return " + ret);
        return ret;
    }

    public boolean saveClearShotPicture(byte[] data, int clearShotTempFileCount) {
        CamLog.d(FaceDetector.TAG, "saveClearShotPicture()-start ");
        String fileExt = CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
        String clearShotFilename = CameraConstants.CLEAR_SHOT_TEMPFILE + Integer.toString(clearShotTempFileCount);
        this.mImageRotationDegree = getDeviceDegree();
        String path = this.mStorageController.getTimeMachineStorageDirectory();
        boolean ret = ImageManager.saveTempFileForTimeMachineShot(data, path, clearShotFilename, fileExt);
        Uri saveUri = Uri.fromFile(new File(path + clearShotFilename + fileExt));
        if (ret) {
            this.mSavedFileName = clearShotFilename;
            this.mImageListUri.add(saveUri);
        }
        CamLog.d(FaceDetector.TAG, "saveClearShotPicture()-end, return " + ret);
        return ret;
    }

    public boolean saveRefocusShotPicture(byte[] data, int refocusShotTempFileCount) {
        CamLog.d(FaceDetector.TAG, "saveRefocusPicture()-start ");
        String fileExt = CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
        String refocusFilename = CameraConstants.REFOCUS_SHOT_TEMPFILE + Integer.toString(refocusShotTempFileCount);
        this.mImageRotationDegree = getDeviceDegree();
        String path = this.mStorageController.getTimeMachineStorageDirectory();
        boolean ret = ImageManager.saveTempFileForTimeMachineShot(data, path, refocusFilename, fileExt);
        Uri saveUri = Uri.fromFile(new File(path + refocusFilename + fileExt));
        if (ret) {
            this.mSavedFileName = refocusFilename;
            CamLog.d(FaceDetector.TAG, "mSavedFileName = " + this.mSavedFileName);
            this.mImageListUri.add(saveUri);
        }
        CamLog.d(FaceDetector.TAG, "saveRefocusPicture()-end, return " + ret);
        return ret;
    }

    public void saveRefocusShotMap(byte[] data) {
        FileNotFoundException ex;
        Throwable th;
        IOException ex2;
        this.mImageRotationDegree = getDeviceDegree();
        String path = this.mStorageController.getTimeMachineStorageDirectory();
        CamLog.i(FaceDetector.TAG, "saveRefocusShotMap-start:DepthMapImage.y");
        OutputStream outputStream = null;
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            OutputStream outputStream2 = new FileOutputStream(new File(path, CameraConstants.REFOCUS_MAP_FILE));
            try {
                outputStream2.write(data);
                if (outputStream2 != null) {
                    try {
                        outputStream2.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                CamLog.d(FaceDetector.TAG, "saveRefocusShotMap-end");
            } catch (FileNotFoundException e2) {
                ex = e2;
                outputStream = outputStream2;
                try {
                    CamLog.w(FaceDetector.TAG, ex.toString(), ex);
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e32) {
                            e32.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (IOException e4) {
                ex2 = e4;
                outputStream = outputStream2;
                CamLog.w(FaceDetector.TAG, ex2.toString(), ex2);
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e322) {
                        e322.printStackTrace();
                    }
                }
            } catch (Throwable th3) {
                th = th3;
                outputStream = outputStream2;
                if (outputStream != null) {
                    outputStream.close();
                }
                throw th;
            }
        } catch (FileNotFoundException e5) {
            ex = e5;
            CamLog.w(FaceDetector.TAG, ex.toString(), ex);
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e6) {
            ex2 = e6;
            CamLog.w(FaceDetector.TAG, ex2.toString(), ex2);
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public void setRefocusShotPreviewGuideVisibility(final boolean show) {
        runOnUiThread(new Runnable() {
            public void run() {
                CameraMediator.this.removePostRunnable(this);
                View mGuideViewLayout = CameraMediator.this.findViewById(R.id.refocus_guide_layout);
                if (mGuideViewLayout != null) {
                    TextView mGuideTextView = (TextView) CameraMediator.this.findViewById(R.id.refocus_guide_text);
                    LayoutParams lp = (LayoutParams) mGuideTextView.getLayoutParams();
                    Common.resetLayoutParameter(lp);
                    lp.width = Common.getPixelFromDimens(CameraMediator.this.getApplicationContext(), R.dimen.refocus_preview_guide_text_width);
                    if (show) {
                        if (Util.isEqualDegree(CameraMediator.this.getResources(), CameraMediator.this.getOrientationDegree(), 0)) {
                            lp.addRule(12, 1);
                            lp.bottomMargin = Common.getPixelFromDimens(CameraMediator.this.getApplicationContext(), R.dimen.refocus_preview_guide_text_marginLeft);
                        } else if (Util.isEqualDegree(CameraMediator.this.getResources(), CameraMediator.this.getOrientationDegree(), 90)) {
                            lp.addRule(12, 1);
                            lp.bottomMargin = (Common.getPixelFromDimens(CameraMediator.this.getApplicationContext(), R.dimen.preview_panel_width) + Common.getPixelFromDimens(CameraMediator.this.getApplicationContext(), R.dimen.preview_panel_marginBottom)) + Common.getPixelFromDimens(CameraMediator.this.getApplicationContext(), R.dimen.bubble_popup_width_bottom_margin_portrait_marginBottom);
                        } else if (Util.isEqualDegree(CameraMediator.this.getResources(), CameraMediator.this.getOrientationDegree(), MediaProviderUtils.ROTATION_180)) {
                            lp.addRule(10, 1);
                            lp.topMargin = Common.getPixelFromDimens(CameraMediator.this.getApplicationContext(), R.dimen.refocus_preview_guide_text_marginLeft);
                        } else {
                            lp.addRule(10, 1);
                            lp.topMargin = (Common.getPixelFromDimens(CameraMediator.this.getApplicationContext(), R.dimen.preview_panel_width) + Common.getPixelFromDimens(CameraMediator.this.getApplicationContext(), R.dimen.preview_panel_marginBottom)) + Common.getPixelFromDimens(CameraMediator.this.getApplicationContext(), R.dimen.bubble_popup_width_bottom_margin_portrait_marginBottom);
                        }
                        lp.addRule(14, 1);
                        mGuideTextView.setLayoutParams(lp);
                        ((RotateLayout) mGuideViewLayout.findViewById(R.id.refocus_guide_rotate)).rotateLayout(CameraMediator.this.getOrientationDegree());
                        mGuideViewLayout.setVisibility(0);
                        return;
                    }
                    mGuideViewLayout.setVisibility(8);
                }
            }
        });
    }

    public boolean savePicture(byte[] data, Bitmap bitmap) {
        String fileName;
        String fileExt;
        long dateTaken = System.currentTimeMillis();
        CamLog.d(FaceDetector.TAG, "savePicture()-start ");
        this.mImageRotationDegree = getDeviceDegree();
        byte[] exifData = setExifData(data);
        String fileExt2 = CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
        FileNamer.get().markTakeTime(getSettingValue(Setting.KEY_CAMERA_SHOT_MODE), getSettingValue(Setting.KEY_SCENE_MODE));
        if (hasSaveURI()) {
            fileName = getFileNameAndExt()[0];
            fileExt = getFileNameAndExt()[1];
        } else {
            fileName = FileNamer.get().getFileNewName(getApplicationContext(), getApplicationMode(), getCurrentStorage(), getCurrentStorageDirectory(), false, getSettingValue(Setting.KEY_CAMERA_SHOT_MODE), false);
            fileExt = fileExt2;
        }
        CamLog.i(FaceDetector.TAG, "savePicture >  fileName :" + fileName);
        String path = getPath(fileName);
        boolean ret = addImageToImageHandler(exifData, data, fileName, fileExt, dateTaken, path, bitmap, this.mImageRotationDegree);
        getStorageController().checkStorage(false);
        if (ret) {
            getPreviewPanelController().setLastPictureThumb(data, this.mSavedImageUri, true);
            getPreviewPanelController().updateThumbnailButton();
            this.mSavedFileName = fileName;
            if (!isAttachMode()) {
                Util.broadcastNewPicture(getActivity(), this.mSavedImageUri);
            }
            this.mImageListUri.add(this.mSavedImageUri);
            if (Common.useSecureLockImage() || AppControlUtil.checkGuestModeAndAppDisabled(getContentResolver(), true, 1)) {
                SecureImageUtil.get().addSecureLockImageUri(this.mSavedImageUri);
            }
            Util.requestUpBoxBackupPhoto(getActivity(), path + fileName + fileExt, CameraConstants.SMART_MODE_ON.equals(getSettingValue(Setting.KEY_UPLUS_BOX)));
        }
        CamLog.d(FaceDetector.TAG, "savePicture()-end, return " + ret);
        return ret;
    }

    private byte[] setExifData(byte[] data) {
        byte[] exifData = null;
        if (checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA)) {
            exifData = ExifUtil.setNewExifInformation(data, 0, getCurrentLocation(), this.mFocalLength);
        }
        if (isAttachMode()) {
            if (exifData != null) {
                this.mCaptureData = exifData;
            } else {
                this.mCaptureData = data;
            }
        }
        return exifData;
    }

    private String getPath(String fileName) {
        if (!hasSaveURI()) {
            return this.mStorageController.getCurrentStorageDirectory();
        }
        if ("content".equals(this.mSaveUri.getScheme())) {
            return this.mStorageController.getCurrentStorageDirectory();
        }
        String returnPath = this.mSaveUri.getPath();
        if (returnPath != null) {
            returnPath = returnPath.replaceFirst("/" + fileName + "\\.[a-zA-Z]+$", "/");
        }
        if (returnPath == null) {
            return this.mStorageController.getCurrentStorageDirectory();
        }
        return returnPath;
    }

    private boolean addImageToImageHandler(byte[] exifData, byte[] data, String fileName, String fileExt, long dateTaken, String path, Bitmap bitmap, int imageRotationDegree) {
        boolean returnValue = false;
        try {
            CamLog.d(FaceDetector.TAG, "imageHandler.addImage-start ");
            if (fileName == null) {
                CamLog.d(FaceDetector.TAG, "error get file name!");
                return false;
            }
            byte[] imageData;
            ContentResolver cr = null;
            if (!hasSaveURI()) {
                cr = getContentResolver();
            }
            if (exifData != null) {
                imageData = exifData;
            } else {
                imageData = data;
            }
            this.mSavedImageUri = this.imageHandler.addImage(cr, fileName, dateTaken, getCurrentLocation(), path, fileName + fileExt, bitmap, imageData, imageRotationDegree, getSettingValue(Setting.KEY_CAMERA_SHOT_MODE).equals(CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS));
            if (hasSaveURI()) {
                if ("content".equals(this.mSaveUri.getScheme())) {
                    this.mSavedImageUri = Uri.fromFile(new File(path + fileName + fileExt));
                } else {
                    this.mSavedImageUri = this.mSaveUri;
                }
            }
            if (this.mSavedImageUri != null) {
                returnValue = true;
            }
            CamLog.d(FaceDetector.TAG, "imageHandler.addImage-end " + this.mSavedImageUri);
            return returnValue;
        } catch (Exception ex) {
            CamLog.e(FaceDetector.TAG, "Exception while compressing image.", ex);
            returnValue = false;
        }
    }

    public void finishImageSaver() {
        if (this.mImageSaver != null) {
            this.mImageSaver.finish();
            this.mImageSaver = null;
        }
    }

    public void waitSaveImageThreadDone() {
        if (this.mImageSaver != null) {
            this.mImageSaver.waitDone();
        }
    }

    public void waitAvailableQueueCount(int availableCount) {
        if (this.mImageSaver != null) {
            this.mImageSaver.waitAvailableQueueCount(availableCount);
        }
    }

    public boolean saveImageSavers(byte[] finalJpegData, Bitmap bitmap, int degree, boolean isSetLastThumb, boolean isBurstFirst) {
        if (this.mImageSaver != null) {
            return this.mImageSaver.addImage(finalJpegData, bitmap, degree, isSetLastThumb, isBurstFirst);
        }
        CamLog.w(FaceDetector.TAG, "ImageSave is null!");
        return false;
    }

    public int getQueueCount() {
        if (this.mImageSaver != null) {
            return this.mImageSaver.getCount();
        }
        return 0;
    }

    public void setSaveRequest(SaveRequest sr, byte[] data, Bitmap bitmap, int degree, boolean isSetLastThumb, boolean isBurstFirst) {
        sr.exifData = setExifData(data);
        sr.data = data;
        sr.bitmap = bitmap;
        sr.dateTaken = System.currentTimeMillis();
        sr.degree = degree;
        sr.isSetLastThumb = isSetLastThumb;
        sr.isBurstFirst = isBurstFirst;
    }

    public void saveAndAddImageForImageSavers(SaveRequest sr) {
        String fileName;
        String fileExt;
        String fileExt2 = CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
        FileNamer.get().markTakeTime(getSettingValue(Setting.KEY_CAMERA_SHOT_MODE), getSettingValue(Setting.KEY_SCENE_MODE));
        if (hasSaveURI()) {
            fileName = getFileNameAndExt()[0];
            fileExt = getFileNameAndExt()[1];
        } else {
            fileName = FileNamer.get().getFileNewName(getApplicationContext(), getApplicationMode(), getCurrentStorage(), getCurrentStorageDirectory(), false, getSettingValue(Setting.KEY_CAMERA_SHOT_MODE), sr.isBurstFirst);
            fileExt = fileExt2;
        }
        CamLog.i(FaceDetector.TAG, "savePicture >  sr.fileName :" + fileName);
        if (fileName != null) {
            String path = getPath(fileName);
            if (addImageToImageHandler(sr.exifData, sr.data, fileName, fileExt, sr.dateTaken, path, sr.bitmap, sr.degree)) {
                this.mSavedFileName = fileName;
                Util.broadcastNewPicture(getActivity(), this.mSavedImageUri);
                Util.requestUpBoxBackupPhoto(getActivity(), path + fileName + fileExt, CameraConstants.SMART_MODE_ON.equals(getSettingValue(Setting.KEY_UPLUS_BOX)));
                if (!(checkAutoReviewOff(false) || isTimeMachineModeOn() || checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_REFOCUS))) {
                    this.mImageListUri.add(this.mSavedImageUri);
                    CamLog.d(FaceDetector.TAG, "mImageListUri.add" + this.mSavedImageUri);
                }
                if (Common.useSecureLockImage() || AppControlUtil.checkGuestModeAndAppDisabled(getContentResolver(), true, 1)) {
                    SecureImageUtil.get().addSecureLockImageUri(this.mSavedImageUri);
                    CamLog.d(FaceDetector.TAG, "SecureImageUtil.get().addSecureLockImageUri" + this.mSavedImageUri);
                }
            }
            if (sr.isSetLastThumb) {
                getPreviewPanelController().setLastPictureThumb(sr.data, this.mSavedImageUri, true);
                getPreviewPanelController().updateThumbnailButton();
            }
            getStorageController().checkStorage(false);
        }
    }

    public void doAfterSaveImageSavers() {
        if (getQueueCount() == 0 && isExitIgnoreDuringSaving()) {
            setExitIgnoreDuringSaving(false);
            deleteSavingProgressDialog();
        }
    }

    public boolean hasSaveURI() {
        if (!this.mRequestSingleImage || this.mSaveUri == null) {
            return false;
        }
        return true;
    }

    public Uri getSaveURI() {
        if (this.mSavedImageUri != null) {
            return this.mSavedImageUri;
        }
        return null;
    }

    public boolean beDirectlyGoingToCropGallery() {
        return this.mSaveUri == null && this.mCropValue != null && this.mIntentFrom == null;
    }

    public void setupCaptureParams() {
        Bundle myExtras = getActivity().getIntent().getExtras();
        if (myExtras != null) {
            this.mSaveUri = (Uri) myExtras.getParcelable("output");
            this.mRequestSingleImage = myExtras.getBoolean("sigleimage", false);
            this.mCropValue = myExtras.getString("crop");
            this.mIntentFrom = myExtras.getString("intentFrom");
            CamLog.i(FaceDetector.TAG, String.format("mSaveUri: %s, crop:%s, intentFrom:%s", new Object[]{this.mSaveUri, this.mCropValue, this.mIntentFrom}));
            return;
        }
        CamLog.i(FaceDetector.TAG, String.format("no extra values", new Object[0]));
    }

    public void doAttachNoCrop() {
        CamLog.d(FaceDetector.TAG, "doAttachNoCrop()");
        if (this.mSaveUri != null) {
            doAttachSaveUri();
        } else if (this.mCaptureData == null || this.mSavedImageUri == null) {
            CamLog.d(FaceDetector.TAG, "doAttach mCaptureData is Null");
            if (this.mSavedImageUri == null) {
                CamLog.d(FaceDetector.TAG, "doAttach mSavedImageUri null!");
                setResultAndFinish(0);
                return;
            }
            Bitmap Orgbmp = ImageManager.loadBitmap(getContentResolver(), this.mSavedImageUri.toString(), false, 16);
            if (Orgbmp == null) {
                CamLog.d(FaceDetector.TAG, "LoadBitmap fail!");
                setResultAndFinish(0);
                return;
            }
            intent = new Intent("inline-data").putExtra("data", Orgbmp);
            intent.setData(this.mSavedImageUri);
            CamLog.d(FaceDetector.TAG, "doAttach OK");
            setResultAndFinish(-1, intent);
            Orgbmp.recycle();
        } else {
            Bitmap bitmap = Util.createCaptureBitmap(this.mCaptureData, 0);
            intent = new Intent("inline-data").putExtra("data", bitmap);
            intent.setData(this.mSavedImageUri);
            CamLog.d(FaceDetector.TAG, "doAttach OK");
            setResultAndFinish(-1, intent);
            bitmap.recycle();
        }
    }

    @SuppressLint({"WorldReadableFiles", "WorldWriteableFiles"})
    public void doAttachCrop() {
        FileOutputStream tempStream = null;
        CamLog.d(FaceDetector.TAG, "doAttachCrop()");
        try {
            File path = getActivity().getFileStreamPath("crop-temp");
            if (path == null) {
                CamLog.d(FaceDetector.TAG, "doAttach\tcrop-temp file path is null");
                setResultAndFinish(0);
                Common.closeSilently(tempStream);
                return;
            }
            path.delete();
            tempStream = getActivity().openFileOutput("crop-temp", 3);
            if (tempStream != null) {
                if (this.mCaptureData != null) {
                    tempStream.write(this.mCaptureData);
                    tempStream.close();
                } else {
                    CamLog.d(FaceDetector.TAG, "doAttach tempStream is null!!!");
                    setResultAndFinish(0);
                    tempStream.close();
                    Common.closeSilently(tempStream);
                    return;
                }
            }
            CamLog.d(FaceDetector.TAG, "tempUri = " + Uri.fromFile(path));
            Common.closeSilently(null);
            Bundle newExtras = new Bundle();
            if (this.mCropValue.equals("circle")) {
                newExtras.putString("circleCrop", CameraConstants.ONEKEY_CONTROL_ENABLE_STRING);
            }
            if (this.mSaveUri != null) {
                CamLog.d(FaceDetector.TAG, "mSaveUri");
                newExtras.putParcelable("output", this.mSaveUri);
            } else {
                CamLog.d(FaceDetector.TAG, "return-data");
                newExtras.putBoolean("return-data", true);
                Bundle InExtra = getActivity().getIntent().getExtras();
                if (InExtra != null) {
                    CamLog.d(FaceDetector.TAG, "input extra = " + InExtra);
                    newExtras.putInt("aspectX", InExtra.getInt("aspectX"));
                    newExtras.putInt("aspectY", InExtra.getInt("aspectY"));
                    newExtras.putInt("outputX", InExtra.getInt("outputX"));
                    newExtras.putInt("outputY", InExtra.getInt("outputY"));
                }
            }
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(this.mSavedImageUri, "image/*");
            cropIntent.putExtras(newExtras);
            getActivity().startActivityForResult(cropIntent, 1);
        } catch (FileNotFoundException ex) {
            try {
                CamLog.e(FaceDetector.TAG, "FileNotFoundException", ex);
                setResultAndFinish(0);
            } finally {
                Common.closeSilently(tempStream);
            }
        } catch (IOException ex2) {
            CamLog.e(FaceDetector.TAG, "IOException", ex2);
            setResultAndFinish(0);
            Common.closeSilently(tempStream);
        } catch (Throwable th) {
            tempStream.close();
        }
    }

    public void doAttach() {
        CamLog.d(FaceDetector.TAG, "doAttach(),mSaveUri:" + this.mSaveUri + ",mCropValue:" + this.mCropValue + ",mIntentFrom:" + this.mIntentFrom + "mSavedImageUri:" + this.mSavedImageUri);
        if (!this.mPausing) {
            if (this.mCropValue == null) {
                doAttachNoCrop();
            } else if (AppControlUtil.checkGuestModeAndAppDisabled(getContentResolver(), true, 1)) {
                doAttachNoCrop();
            } else {
                doAttachCrop();
            }
        }
    }

    public void showOsd() {
        if (getApplicationMode() == 0 && CameraConstants.SMART_MODE_ON.equals(getSettingValue(Setting.KEY_SMART_MODE))) {
            setQuickFunctionControllerVisible(false);
        } else {
            setQuickFunctionControllerVisible(true);
        }
        showIndicatorController();
    }

    public void hideOsd() {
        this.mQuickFunctionController.hide();
        getFocusController().hideFocus();
    }

    public boolean deleteTimeMachineImages() {
        CamLog.d(FaceDetector.TAG, "deleteTimeMachineImages-start");
        String path = getStorageController().getTimeMachineStorageDirectory();
        int i = 0;
        while (i < 5) {
            try {
                String fullFilePath = path + (CameraConstants.TIME_MACHINE_TEMPFILE + Integer.toString(i + 1)) + CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
                if (!Common.isFileExist(fullFilePath)) {
                    CamLog.d(FaceDetector.TAG, "file is not exist : " + fullFilePath);
                } else if (!new File(fullFilePath).delete()) {
                    CamLog.d(FaceDetector.TAG, "TMS temp file delete fail.");
                }
                i++;
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "deleteTimeMachineImages fail!:", e);
            }
        }
        CamLog.d(FaceDetector.TAG, "deleteTimeMachineImages-end");
        return true;
    }

    public boolean deleteClearShotImages() {
        CamLog.d(FaceDetector.TAG, "deleteClearShotImages-start");
        String path = getStorageController().getTimeMachineStorageDirectory();
        int i = 0;
        while (i < 6) {
            try {
                String fullFilePath = path + (CameraConstants.CLEAR_SHOT_TEMPFILE + Integer.toString(i + 1)) + CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
                if (!Common.isFileExist(fullFilePath)) {
                    CamLog.d(FaceDetector.TAG, "file is not exist : " + fullFilePath);
                } else if (!new File(fullFilePath).delete()) {
                    CamLog.d(FaceDetector.TAG, "clear shot temp file delete fail.");
                }
                i++;
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "deleteClearShotImages fail!:", e);
            }
        }
        CamLog.d(FaceDetector.TAG, "deleteClearShotImages-end");
        return true;
    }

    public int deleteImage(String filename, Uri uri) {
        String path = getStorageController().getCurrentStorageDirectory();
        if (Common.isFileExist(path + filename + CameraConstants.TIME_MACHINE_TEMPFILE_EXT) && new File(path + filename + CameraConstants.TIME_MACHINE_TEMPFILE_EXT).delete()) {
            boolean equal;
            ImageManager.deleteImage(getContentResolver(), uri);
            if (this.mImageListUri.size() == this.mImageListRotation.size()) {
                equal = true;
            } else {
                equal = false;
            }
            int index = this.mImageListUri.indexOf(uri);
            if (this.mImageListUri.remove(uri)) {
                if (equal) {
                    this.mImageListRotation.remove(index);
                }
                CamLog.d(FaceDetector.TAG, "deleted uri");
            } else {
                CamLog.w(FaceDetector.TAG, "failure to delete uri!");
            }
            getStorageController().checkStorage(false);
            return this.mImageListUri.size();
        }
        CamLog.w(FaceDetector.TAG, "failure delete image file (return -1)");
        return -1;
    }

    public CameraSettingMenu getCurrentSettingMenu() {
        return (CameraSettingMenu) super.getCurrentSettingMenu();
    }

    public int getSelectedChildIndex() {
        return getCurrentSettingMenu().getSelectedChildIndex();
    }

    public long getAvailablePictureCount() {
        return getStorageController().getAvailablePictureCount();
    }

    public long getAvailablePictureCount(int storageType) {
        return getStorageController().getAvailablePictureCount(storageType);
    }

    public boolean isAttachMode() {
        return isAttachIntent();
    }

    public void setupCaptureIntent() {
        String action = getActivity().getIntent().getAction();
        boolean z = "android.media.action.IMAGE_CAPTURE".equals(action) || CameraConstants.ACTION_IMAGE_CAPTURE_SECURE.equals(action);
        this.isCaptureIntent = z;
    }

    public boolean isAttachIntent() {
        return this.isCaptureIntent;
    }

    public Bitmap getLastThumbnail(Uri uri) {
        if (this.mActivity == null) {
            return null;
        }
        long id = -1;
        if (uri != null) {
            id = Util.getIdFromUri(getActivity(), uri);
            CamLog.d(FaceDetector.TAG, String.format("GET PICTURE THUMB start id is %d, and uri is %s", new Object[]{Long.valueOf(id), uri.toString()}));
        }
        if (id == -1) {
            CamLog.w(FaceDetector.TAG, String.format("GET PICTURE THUMB end: uri not valid", new Object[0]));
            return null;
        }
        int degree = ExifUtil.getExifOrientationDegree(BitmapManager.getRealPathFromURI(getActivity(), uri));
        Bitmap thumbnail = BitmapManager.instance().getThumbnail(getContentResolver(), id, 1, null, false);
        if (this.imageHandler != null) {
            thumbnail = this.imageHandler.getImage(thumbnail, degree, false);
        } else {
            thumbnail = null;
        }
        CamLog.d(FaceDetector.TAG, String.format("GET PICTURE THUMB end", new Object[0]));
        return thumbnail;
    }

    public boolean isMMSIntent() {
        boolean isMMScalling = false;
        Intent intent = getActivity().getIntent();
        String callingPackage = getActivity().getCallingPackage();
        if (intent != null) {
            if (intent.getIntExtra("MMSAttach", 0) == 1) {
                isMMScalling = true;
            }
            String intentFrom = intent.getStringExtra("intentFrom");
            if (intentFrom != null && intentFrom.equals("MMSAttach")) {
                isMMScalling = true;
            }
        }
        if (callingPackage == null || !callingPackage.equals("com.android.mms")) {
            return isMMScalling;
        }
        return true;
    }

    public boolean isPlayRingMode() {
        return false;
    }

    public int[] getFaceValues(int mValue) {
        CamLog.d(FaceDetector.TAG, "FaceBeauty : mFaceValues.length = " + this.mFaceValues.length + ", mValue = " + mValue);
        if (this.mFaceValues.length <= mValue || mValue < 0) {
            CamLog.w(FaceDetector.TAG, "Error : mValue must be a smaller than mFaceValues.length");
            mValue = 4;
        }
        return this.mFaceValues[mValue];
    }

    public void setFaceBeutyShotParameter(int mValue) {
        if (getEngineProcessor().checkEngineTag(FaceBeauty.ENGINE_TAG)) {
            CamLog.d(FaceDetector.TAG, "setFaceBeutyShotParameter");
            FaceBeauty beautyEngine = (FaceBeauty) getEngineProcessor().getEngine(FaceBeauty.ENGINE_TAG);
            int[] value = getFaceValues(mValue);
            CamLog.d(FaceDetector.TAG, "FaceBeauty White Value [" + value[0] + "] Strength Value [" + value[1] + "] ");
            if (beautyEngine != null) {
                beautyEngine.setParameter(value[0], value[1]);
            }
        }
    }

    public String getPreviousShotModeString() {
        return this.mPreviousShotMode;
    }

    public void setPreviousShotModeString(String strShotMode) {
        this.mPreviousShotMode = strShotMode;
    }

    public String getPreviousPictureSize() {
        CamLog.d(FaceDetector.TAG, "get previous PictireSize=" + this.mPreviousPictureSize);
        return this.mPreviousPictureSize;
    }

    public void setPreviousPictureSize(String size) {
        CamLog.d(FaceDetector.TAG, "set previous PictireSize=" + size);
        this.mPreviousPictureSize = size;
    }

    public void setTimeMachineShot(Parameters parameters, int zsl_buffer) {
        if (FunctionProperties.isTimeMachinShotSupported() || FunctionProperties.isFullFrameContinuousShotSupported()) {
            CamLog.v(FaceDetector.TAG, "setTimeMachineShot zsl-burst-count = " + zsl_buffer);
            parameters.set("zsl-burst-count", zsl_buffer);
        }
    }

    public boolean getTimeMachinePictures() {
        if (!this.mTimemachineHasPictures || !isTimeMachineModeOn()) {
            return false;
        }
        postOnUiThread(new Runnable() {
            public void run() {
                CameraMediator.this.removePostRunnable(this);
                Bundle bundle = new Bundle();
                bundle.putBoolean("useTimeMachinePostview", true);
                CameraMediator.this.doCommandUi(Command.DISPLAY_CAMERA_POSTVIEW, bundle);
            }
        });
        return true;
    }

    public void setTimemachineHasPictures(boolean has) {
        if (FunctionProperties.isTimeMachinShotSupported()) {
            long duration;
            long autoReviewDuration;
            this.mTimemachineHasPictures = has;
            getPreviewPanelController().setTimeMachineReviewIconVisible(has);
            String autoReview = getSettingValue(Setting.KEY_CAMERA_AUTO_REVIEW);
            if ("on_delay_2sec".equals(autoReview)) {
                duration = CameraConstants.TOAST_LENGTH_SHORT;
            } else {
                duration = 0;
            }
            if ("on_delay_5sec".equals(autoReview)) {
                autoReviewDuration = CameraConstants.TOAST_LENGTH_LONG;
            } else {
                autoReviewDuration = duration;
            }
            if (autoReviewDuration == 0) {
                showBubblePopupVisibility(0, CameraConstants.TOAST_LENGTH_VERY_LONG, has);
            }
        }
    }

    public boolean isTimemachineHasPictures() {
        return this.mTimemachineHasPictures;
    }

    public boolean deleteRefocusShotImages() {
        String fullFilePath;
        CamLog.d(FaceDetector.TAG, "deleteRefocusShotImages-start");
        String path = getStorageController().getTimeMachineStorageDirectory();
        int i = 0;
        while (i < 7) {
            try {
                fullFilePath = path + (CameraConstants.REFOCUS_SHOT_TEMPFILE + Integer.toString(i + 1)) + CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
                if (!Common.isFileExist(fullFilePath)) {
                    CamLog.d(FaceDetector.TAG, "file is not exist : " + fullFilePath);
                } else if (!new File(fullFilePath).delete()) {
                    CamLog.d(FaceDetector.TAG, "Refocus temp file delete fail.");
                }
                i++;
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "deleteRefocusShotImages fail!:", e);
            }
        }
        fullFilePath = path + CameraConstants.REFOCUS_MAP_FILE;
        if (!Common.isFileExist(fullFilePath)) {
            CamLog.d(FaceDetector.TAG, "file is not exist : " + fullFilePath);
        } else if (!new File(fullFilePath).delete()) {
            CamLog.d(FaceDetector.TAG, "Refocus temp file delete fail.");
        }
        CamLog.d(FaceDetector.TAG, "deleteRefocusShotImages-end");
        return true;
    }

    public void setRefocusShotHasPictures(boolean has) {
        if (FunctionProperties.isRefocusShotSupported()) {
            long duration;
            long autoReviewDuration;
            this.mRefocusShotHasPictures = has;
            getPreviewPanelController().setRefocusReviewIconVisible(has);
            String autoReview = getSettingValue(Setting.KEY_CAMERA_AUTO_REVIEW);
            if ("on_delay_2sec".equals(autoReview)) {
                duration = CameraConstants.TOAST_LENGTH_SHORT;
            } else {
                duration = 0;
            }
            if ("on_delay_5sec".equals(autoReview)) {
                autoReviewDuration = CameraConstants.TOAST_LENGTH_LONG;
            } else {
                autoReviewDuration = duration;
            }
            if (autoReviewDuration == 0) {
                showBubblePopupVisibility(1, CameraConstants.TOAST_LENGTH_VERY_LONG, has);
            }
        }
    }

    public boolean isRefocusShotHasPictures() {
        return this.mRefocusShotHasPictures;
    }

    public boolean getRefocusPictures() {
        if (!this.mRefocusShotHasPictures || !CameraConstants.TYPE_SHOTMODE_REFOCUS.equals(getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
            return false;
        }
        postOnUiThread(new Runnable() {
            public void run() {
                CameraMediator.this.removePostRunnable(this);
                Bundle bundle = new Bundle();
                bundle.putBoolean("useRefocusPostview", true);
                CameraMediator.this.doCommandUi(Command.DISPLAY_CAMERA_POSTVIEW, bundle);
            }
        });
        return true;
    }

    public void showBubblePopupVisibility(int popupType, long duration, boolean show) {
        if (show) {
            BubblePopupController.get().showBubblePopup(getMediator(), popupType, duration);
        } else {
            BubblePopupController.get().removeBubblePopup(getMediator(), 0);
        }
    }

    public void setFullFrameContinuousShot(Parameters parameters, int bufferNum) {
        if (FunctionProperties.isFullFrameContinuousShotSupported()) {
            CamLog.v(FaceDetector.TAG, "setContinuousShot buffer-count = " + bufferNum);
            parameters.set("zsl-burst-count", bufferNum);
        }
    }

    public void setTimerAndSceneSmartShutterEnable(Parameters parameters, boolean timer, boolean scene, boolean enable) {
        String currentTimerValue = getSettingValue(Setting.KEY_CAMERA_TIMER);
        String currentSceneMode = getSettingValue(Setting.KEY_SCENE_MODE);
        if (timer && !currentTimerValue.equals("0") && (checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS) || checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) || checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) || checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA))) {
            setSetting(Setting.KEY_CAMERA_TIMER, "0");
            if (getIndicatorController() != null && isIndicatorControllerInitialized()) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        CameraMediator.this.removePostRunnable(this);
                        CameraMediator.this.getIndicatorController().updateTimerIndicator();
                    }
                });
            }
        }
        if (scene) {
            if (currentSceneMode.equals(CameraConstants.SCENE_MODE_SMART_SHUTTER)) {
                setSetting(Setting.KEY_SCENE_MODE, LGT_Limit.ISP_AUTOMODE_AUTO);
                CamLog.i(FaceDetector.TAG, "####### scene mode set to off, because of smart shutter");
                setSceneModeForAdvanced(parameters, LGT_Limit.ISP_AUTOMODE_AUTO);
            }
            smartShutterEnable(enable);
        }
    }

    public void smartShutterEnable(boolean enable) {
        ListPreference pref = getPreferenceGroup().findPreference(Setting.KEY_SCENE_MODE);
        if (pref != null) {
            CharSequence[] sceneEntryValues = pref.getEntryValues();
            if (sceneEntryValues != null) {
                for (Object equals : sceneEntryValues) {
                    if (equals.equals(CameraConstants.SCENE_MODE_SMART_SHUTTER)) {
                        setCurrentSettingMenuEnable(Setting.KEY_SCENE_MODE, CameraConstants.SCENE_MODE_SMART_SHUTTER, enable);
                        return;
                    }
                }
            }
        }
    }

    public void setSceneModeForAdvanced(Parameters parameters, String sceneMode) {
        if (parameters != null) {
            parameters.setSceneMode(sceneMode);
            String[] fpsValues;
            if (getCameraId() == 1) {
                fpsValues = SystemProperties.get("persist.data.front.minfps", MultimediaProperties.getFrontCameraFrameRateNormalRangeMin() + "," + MultimediaProperties.getFrontCameraFrameRateNightModeRangeMin()).split(",");
                if (!Setting.HELP_NIGHT.equals(sceneMode)) {
                    parameters.setPreviewFpsRange(Integer.parseInt(fpsValues[0]), MultimediaProperties.getCameraFrameRateNormalRangeMax());
                } else if (ModelProperties.getProjectCode() != 6) {
                    parameters.setPreviewFpsRange(Integer.parseInt(fpsValues[1]), MultimediaProperties.getCameraFrameRateNormalRangeMax());
                }
            } else if (CameraConstants.SMART_MODE_ON.equals(getSettingValue(Setting.KEY_SMART_MODE))) {
                parameters.setPreviewFpsRange(MultimediaProperties.getCameraFrameRateIAModeRangeMin(), MultimediaProperties.getCameraFrameRateNormalRangeMax());
            } else if (checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS)) {
                parameters.setPreviewFpsRange(MultimediaProperties.getCameraFrameRateBurstShotModeRangeMin(), MultimediaProperties.getCameraFrameRateNormalRangeMax());
            } else {
                fpsValues = SystemProperties.get("persist.data.rear.minfps", MultimediaProperties.getCameraFrameRateNormalRangeMin() + "," + MultimediaProperties.getCameraFrameRateNightModeRangeMin()).split(",");
                if (Setting.HELP_NIGHT.equals(sceneMode)) {
                    parameters.setPreviewFpsRange(Integer.parseInt(fpsValues[1]), MultimediaProperties.getCameraFrameRateNormalRangeMax());
                } else {
                    parameters.setPreviewFpsRange(Integer.parseInt(fpsValues[0]), MultimediaProperties.getCameraFrameRateNormalRangeMax());
                }
            }
        }
    }

    public void doTouchbyAF(int x, int y) {
        if (getApplicationMode() == 0 && getCameraId() == 0) {
            if (this.mFreePanoramaController != null && checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
                return;
            }
            if (this.mPanoramaController != null && checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA)) {
                return;
            }
            if (this.mPlanePanoramaController != null && checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
                return;
            }
            if (!checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CONTINUOUS) || !getCurrentModule().isRunning()) {
                if (("0".equals(getSettingValue(Setting.KEY_CAMERA_TIMER)) || !isTimerShotCountdown()) && !getPreviewPanelController().snapshotOnContinuousFocus() && !getInCaptureProgress()) {
                    this.mFocusController.startFocusByTouchPress(x, y);
                }
            }
        } else if (getApplicationMode() == 0 && getCameraId() == 1 && FunctionProperties.isFrontTouchAESupported() && !getInCaptureProgress()) {
            this.mFocusController.startAEByTouchPress(x, y);
        }
    }

    public void switchCameraId(int cameraId) {
        if (!this.mPausing) {
            CamLog.d(FaceDetector.TAG, "switchCameraId()-start, " + cameraId);
            if (checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
                showFreePanoramaBlackBg();
            }
            setCameraId(cameraId);
            Setting.writePreferredCameraId(getActivity().getSharedPreferences(Setting.SETTING_PRIMARY, 0), cameraId);
            stopPreview();
            if (getFreePanoramaController().isPanoramaUIShown()) {
                stopFreePanoramaEngine(getParameters());
            }
            getPreviewController().closeCamera();
            if (ModelProperties.isOMAP4Chipset()) {
                this.imageHandler.resetRotation();
            }
            enableInput(false);
            if (getCameraMode() == 1) {
                setModule(Module.DEFAULT_NORMAL_SHOT);
            }
            CameraPreview sPreview = this.mPreviewController.getCameraPreview();
            sPreview.releasePreviewCallback(getCameraDevice());
            sPreview.setVisibility(4);
            if (FunctionProperties.getFunctionFrontCameraBeautyShot() == 1) {
                try {
                    OpenGLSurfaceView gPreview;
                    SurfaceView gPreviewExtra;
                    if (CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)) || CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
                        gPreview = this.mPreviewController.getCameraGLPreview();
                        gPreviewExtra = this.mPreviewController.getCameraGLPreviewExtra();
                        gPreview.releasePreviewCallback(getCameraDevice());
                        gPreview.setVisibility(4);
                        gPreview.clearData(true);
                        gPreview.setVisibility(0);
                        gPreviewExtra.setVisibility(0);
                        if (!isPreviewing()) {
                            this.mPreviewController.startPreview(null, true);
                        }
                    } else {
                        gPreviewExtra = this.mPreviewController.getCameraGLPreviewExtra();
                        gPreview = this.mPreviewController.getCameraGLPreview();
                        gPreview.releasePreviewCallback(getCameraDevice());
                        gPreview.setVisibility(8);
                        gPreviewExtra.setVisibility(8);
                        getEngineProcessor().releaseAllEngine();
                        swapPreviewEffect(null);
                        sPreview.setVisibility(0);
                        this.mPreviewController.startPreview(null, true);
                    }
                } catch (NullPointerException e) {
                    CamLog.e(FaceDetector.TAG, "NullPointerException:", e);
                    if (this.mPreviewController != null) {
                        this.mPreviewController.startPreview(null, true);
                    }
                }
            } else {
                sPreview.setVisibility(0);
                this.mPreviewController.startPreview(null, true);
            }
            CamLog.d(FaceDetector.TAG, "switchCameraId()-end, " + cameraId);
        }
    }

    public void setFocalLength(float focalLength) {
        this.mFocalLength = focalLength;
    }

    public void setSmartModeForPictureSize(String PictureSize) {
        this.mPreSmartModePicSize = PictureSize;
    }

    public String getSmartModeForPictureSize() {
        return this.mPreSmartModePicSize;
    }

    public void showControllerForHideSettingMenu(boolean checkShowAll, boolean showAll) {
        if (!checkShowAll || showAll) {
            if (!(this.mFocusController == null || this.mManualFocusController == null || Setting.HELP_FACE_TRACKING_LED.equals(getSettingValue(Setting.KEY_FOCUS)) || CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(getSettingValue(Setting.KEY_FOCUS)))) {
                showFocus();
            }
            if (isDualRecordingActive() || isDualCameraActive() || isSmartZoomRecordingActive()) {
                doCommandUi(Command.SHOW_PIP_FRAME_SUB_MENU);
            }
            showBeautyShotBarForNewUx(true);
        }
    }

    public boolean postviewRequestInit() {
        if (this.mActivity.getPostviewRequestCode() != 3) {
            return false;
        }
        this.mActivity.setPostviewRequestInitCode();
        return true;
    }

    public boolean checkShotModeForZoomInOut() {
        if (isTimerShotCountdown() || isContinuousShotAlived() || isPanoramaStarted() || checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) || checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA) || checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) || checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA) || checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_REFOCUS)) {
            return false;
        }
        return true;
    }

    public void setPanoramaEngine() {
        getPanoramaController().setEngine();
    }

    public void showPanoramaView() {
        getPanoramaController().showPanoramaView();
    }

    public void removePanoramaView() {
        getPanoramaController().removePanoramaView();
    }

    public boolean isPanoramaStarted() {
        return getPanoramaController().isPanoramaStarted();
    }

    public boolean isSynthesisInProgress() {
        return getPanoramaController().isSynthesisInProgress();
    }

    public boolean isPanoramaUIShown() {
        return getPanoramaController().isPanoramaUIShown();
    }

    public void startPanorama() {
        getPanoramaController().startPanorama();
    }

    public void stopPanorama() {
        getPanoramaController().stopPanorama();
    }

    public boolean isPanoramaUpdatebutton() {
        return getPanoramaController().isPanoramaUpdatebutton();
    }

    public void showFreePanoramaBlackBg() {
        runOnUiThread(new Runnable() {
            public void run() {
                float lcdHeight;
                CameraMediator.this.removePostRunnable(this);
                if (Util.isConfigureLandscape(CameraMediator.this.getResources())) {
                    lcdHeight = (float) Common.getPixelFromDimens(CameraMediator.this.getApplicationContext(), R.dimen.lcd_height);
                } else {
                    lcdHeight = (float) Common.getPixelFromDimens(CameraMediator.this.getApplicationContext(), R.dimen.lcd_width);
                }
                CameraMediator.this.getPreviewController().getCameraPreview().setTranslationY(lcdHeight);
                CameraMediator.this.findViewById(R.id.free_panorama_black_background).setVisibility(0);
            }
        });
    }

    public void removeFreePanoramaBlackBg() {
        runOnUiThread(new Runnable() {
            public void run() {
                CameraMediator.this.removePostRunnable(this);
                CameraMediator.this.getPreviewController().getCameraPreview().setTranslationY(0.0f);
                CameraMediator.this.findViewById(R.id.free_panorama_black_background).setVisibility(8);
            }
        });
    }

    public void showFreePanoramaView() {
        getFreePanoramaController().showPanoramaView();
    }

    public void removeFreePanoramaView() {
        getFreePanoramaController().removePanoramaView();
    }

    public void startFreePanorama() {
        getFreePanoramaController().startPanorama();
    }

    public void stopFreePanorama() {
        getFreePanoramaController().stopPanorama();
    }

    public void startFreePanoramaEngine(Parameters parameters) {
        getFreePanoramaController().startEngine(parameters);
    }

    public void stopFreePanoramaEngine(Parameters parameters) {
        getFreePanoramaController().stopEngine(parameters);
    }

    public void restartFreePanorama() {
        getFreePanoramaController().restartToStartupPreview();
    }

    public void hideFreePanoramaTakingGuide() {
        getFreePanoramaController().setVisibleTakingGuide(false);
    }

    public int[] getFreePanoramaResultSize() {
        return getFreePanoramaController().getResultSize();
    }

    public int getFreePanoramaStatus() {
        return getFreePanoramaController().getPanoramaState();
    }

    public int getFreePanoramaEngineStatus() {
        return getFreePanoramaController().getPanoramaEngineState();
    }

    public void setRemoveFreePanoramaBlackBg(boolean remove) {
        getFreePanoramaController().setRemoveFreePanoramaBlackBg(remove);
    }

    public void startFreePanoramaRotation(int degree, boolean animation) {
        if (this.mFreePanoramaController != null) {
            this.mFreePanoramaController.startRotation(degree, animation);
        }
    }

    public void startPlanePanoramaEngine() {
        getPlanePanoramaController().startEngine();
    }

    public void stopPlanePanoramaEngine() {
        getPlanePanoramaController().stopEngine();
    }

    public void removePlanePanoramaView() {
        getPlanePanoramaController().hide();
    }

    public void startPlanePanorama() {
        getPlanePanoramaController().startPanorama();
    }

    public void stopPlanePanorama() {
        getPlanePanoramaController().stopPanorama();
    }

    public int[] getPlanePanoramaResultSize() {
        return getPlanePanoramaController().getResultSize();
    }

    public void startPlanePanoramaRotation(int degree, boolean animation) {
        if (this.mPlanePanoramaController != null) {
            this.mPlanePanoramaController.startRotation(degree, animation);
        }
    }

    public int getPlanePanoramaStatus() {
        return getPlanePanoramaController().getStatus();
    }

    public void updateVoiceShutterIndicator(boolean show) {
        if (getIndicatorController() != null) {
            getIndicatorController().updateVoiceIndicator(show);
        }
    }

    public void doVoiceShutterIndicatorUpdateHandler() {
        if (getApplicationMode() == 0 && FunctionProperties.isVoiceShutter()) {
            updateVoiceShutterIndicator(false);
        }
    }

    public boolean stopByUserAction() {
        if (!getCurrentModule().isRunning()) {
            return false;
        }
        getCurrentModule().stopByUserAction();
        return true;
    }

    public void facePreviewInitController() {
        getFacePreviewController().initController();
    }

    public void initFaceDetectInfo() {
        getFacePreviewController().initFaceDetectInfo();
    }

    public void startFaceDetection(boolean bHasUI) {
        getFacePreviewController().startFaceDetection(bHasUI);
    }

    public void stopFaceDetection() {
        getFacePreviewController().stopFaceDetection();
    }

    public boolean isfacePreviewInitialized() {
        return getFacePreviewController().isInitialized();
    }

    public void onFaceDetectionFromHal(Face[] faces) {
        getFacePreviewController().onFaceDetectionFromHal(faces);
    }

    public void startFaceDetectionFromHal(boolean bHasUI) {
        getPreviewController().startFaceDetectionFromHal(bHasUI);
    }

    public boolean checkFaceDetectionNoUI() {
        return getPreviewController().checkFaceDetectionNoUI();
    }

    public void stopFaceDetectionFromHal() {
        getPreviewController().stopFaceDetectionFromHal();
    }

    public boolean checkUpdateThumbnail() {
        if (this.mActivity.getPostviewRequestCode() != 3) {
            return true;
        }
        CamLog.i(FaceDetector.TAG, "UpdateThumbnailButton() return");
        return false;
    }

    public void setTimerSetting(int value) {
        getTimerController().setTimerSetting(value);
    }

    public boolean stopTimerShot() {
        return getTimerController().stopTimerShot();
    }

    public int getTimerCaptureDelay() {
        return getTimerController().getTimerCaptureDelay();
    }

    public void startTimerShot() {
        getTimerController().startTimerShot();
    }

    public boolean isTimerShotCountdown() {
        return getTimerController().isTimerShotCountdown();
    }

    public void setTimerShotCountdown(boolean value) {
        getTimerController().setTimerShotCountdown(value);
    }

    public void setSmartCameraMode(LGParameters lgParams, boolean enable) {
        getQuickButtonController().setSmartCameraMode(lgParams, enable);
    }

    public void doSmartCameraModeCallback(int[] data) {
        int ASDScene = (data[2] & -16777216) >>> 24;
        int ASDMove = data[3] & Ola_ShotParam.AnimalMask_Random;
        CamLog.e(FaceDetector.TAG, "SmartCameraModeCallback, object callback data is  ASDStatus : " + ((data[2] & 16711680) >>> 16) + ", ASDScene : " + ASDScene + ", ASDMove : " + ASDMove);
        setCurrentIAMode(ASDScene);
    }

    public void setCurrentIAMode(int currentIAMode) {
        this.mCurrentIAMode = currentIAMode;
    }

    public int getCurrentIAMode() {
        return this.mCurrentIAMode;
    }

    public void hideAudiozoomready() {
    }

    public boolean checkSupportVideoSize(Uri uri) {
        return false;
    }

    public int[] getDualCameraPictureSize() {
        if (getPreviewController() != null) {
            String sizeOnDevice = getPreviewController().getPreviewSizeOnDevice();
            if (sizeOnDevice != null) {
                CamLog.d(FaceDetector.TAG, "=========== size : " + sizeOnDevice);
                return Util.SizeString2WidthHeight(sizeOnDevice);
            }
        }
        return null;
    }

    public void setPreviousAutoReviewValue() {
        if (ProjectVariables.isSupportedAutoReview() && AppControlUtil.checkGuestModeAndAppDisabled(getContentResolver(), true, 1)) {
            this.mPreviousCameraAutoReviewValue = getSettingValue(Setting.KEY_CAMERA_AUTO_REVIEW);
            setSetting(Setting.KEY_CAMERA_AUTO_REVIEW, CameraConstants.SMART_MODE_OFF);
            CamLog.d(FaceDetector.TAG, "Because of guest mode and gallery not exist, so previous AutoReview value is saved. Saved value : " + this.mPreviousCameraAutoReviewValue);
        }
    }

    public void restoreAutoReviewValue() {
        if (ProjectVariables.isSupportedAutoReview() && AppControlUtil.checkGuestModeAndAppDisabled(getContentResolver(), true, 1)) {
            CamLog.d(FaceDetector.TAG, "Restore previous AutoReviewValue : " + this.mPreviousCameraAutoReviewValue);
            if (this.mPreviousCameraAutoReviewValue != null) {
                this.mSettingController.setSetting(Setting.KEY_CAMERA_AUTO_REVIEW, this.mPreviousCameraAutoReviewValue);
            }
        }
    }

    public boolean getEffectRecorderPausing() {
        return this.mPreviewController.getEffectRecorderPausing();
    }

    public void runGestureEngine(boolean useCallback) {
        if (this.mGestureShutterController != null) {
            this.mGestureShutterController.runGestureEngine(useCallback);
        }
    }

    public void releaseGestureEngine() {
        if (this.mGestureShutterController != null) {
            this.mGestureShutterController.releaseGestureEngine();
        }
    }

    public void showGestureGuide() {
        if (this.mGestureShutterController != null) {
            this.mGestureShutterController.showGestureGuide();
        }
    }

    public void hideGestureGuide() {
        if (this.mGestureShutterController != null) {
            this.mGestureShutterController.hideGestureGuide();
        }
    }

    public void putPreviewFrameForGesture(byte[] data, Camera camera) {
        if (this.mGestureShutterController != null) {
            this.mGestureShutterController.onPreviewFrame(data, camera);
        }
    }

    public void setGestureShotActivated(boolean set) {
        if (this.mTimerController != null) {
            this.mTimerController.setGestureShotActivated(set);
        }
    }

    public boolean isGestureShotActivated() {
        return this.mTimerController != null ? this.mTimerController.isGestureShotActivated() : false;
    }

    public void startGestureEngine() {
        if (this.mGestureShutterController != null && this.mGestureShutterController.isAvailableGestureShutterStarted()) {
            this.mGestureShutterController.startGestureEngine();
        }
    }

    public void stopGestureEngine() {
        if (this.mGestureShutterController != null) {
            this.mGestureShutterController.stopGestureEngine();
        }
    }
}
