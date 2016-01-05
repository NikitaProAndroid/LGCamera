package com.lge.camera.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.ControllerFunction;
import com.lge.camera.EffectsBase;
import com.lge.camera.EffectsBase.EffectBaseInterface;
import com.lge.camera.EffectsBase.EffectsListener;
import com.lge.camera.EffectsCamera;
import com.lge.camera.EffectsRecorder;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.CameraPreview;
import com.lge.camera.components.CameraPreview.OnDeviceListener;
import com.lge.camera.components.ImageButtonEx;
import com.lge.camera.components.OpenGLSurfaceView;
import com.lge.camera.components.OpenGLSurfaceView.GLSurfaceListener;
import com.lge.camera.components.RotateImageButton;
import com.lge.camera.components.ShutterButton;
import com.lge.camera.controller.PIPController.PIPControllerFunction;
import com.lge.camera.controller.camcorder.PIPRecordingController;
import com.lge.camera.controller.camera.PIPCameraController;
import com.lge.camera.module.Module;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.PreferenceGroup;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CameraHardwareException;
import com.lge.camera.util.CameraHolder;
import com.lge.camera.util.Common;
import com.lge.camera.util.SecureImageUtil;
import com.lge.camera.util.SharedPreferenceUtil;
import com.lge.camera.util.Util;
import com.lge.filterpacks.DualRecorderFilter;
import com.lge.hardware.LGCamera;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.media.CamcorderProfileEx;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Limit;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.AutoPanorama;
import com.lge.olaworks.library.BaseEngine;
import com.lge.olaworks.library.EngineProcessor;
import com.lge.olaworks.library.FaceBeauty;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class PreviewController extends Controller implements Callback, GLSurfaceListener, PIPControllerFunction, OnDeviceListener, EffectBaseInterface {
    protected static final int HEIGHT_INDEX = 1;
    protected static final int WIDTH_INDEX = 0;
    protected boolean bRendered;
    private CountDownLatch deviceOpenLatch;
    private final int divider;
    private boolean isBeautyshotProgress;
    protected boolean isPlayRingVideoSizeSet;
    protected boolean isReadyEngineProcessor;
    private boolean mBeginStartPreview;
    protected Camera mCameraDevice;
    protected OpenGLSurfaceView mCameraGLPreview;
    protected SurfaceView mCameraGLPreviewExtra;
    protected int mCameraMode;
    protected CameraPreview mCameraPreview;
    protected boolean mCameraReleaseOnGoing;
    private boolean mChangeMode;
    private boolean mChangedAutoReviewToDefault;
    private boolean mChangedManualFocusToDefault;
    private int mCheckCountOf_CAMERA_ERROR_SERVER_DIED;
    protected String mCurrentEffect;
    protected EffectCameraListener mEffectCameraListener;
    protected ArrayList<String> mEffectList;
    protected Object mEffectParameter;
    protected EffectRecorderListener mEffectRecorderListener;
    protected int mEffectType;
    public EffectsCamera mEffectsCamera;
    public EffectsRecorder mEffectsRecorder;
    public Thread mEnableInputThread;
    protected EngineProcessor mEngineProcessor;
    private CameraErrorCallback mErrorCallback;
    private ImageButtonEx mExitBtn;
    private boolean mFaceDetectionHasUI;
    private boolean mFaceDetectionStarted;
    public Runnable mGestureEngineRunable;
    private boolean mIsSensorSupportBackdropper;
    protected boolean mIsStartPreviewEffectOnGoing;
    protected LGCamera mLGCamera;
    private CountDownLatch mLatch;
    private boolean mLockScreenPreventPreview;
    public PreviewCallback mOneShotPreviewCallback;
    protected PIPCameraController mPIPCameraController;
    protected PIPRecordingController mPIPRecordingController;
    private String mPrevAutoReviewVal;
    public Runnable mPreviewCallbackRunnable;
    private boolean mPreviewing;
    protected String mPreviousResolutionBack;
    protected String mPreviousResolutionFront;
    protected CamcorderProfileEx mProfile;
    protected int mProfileType;
    private OnTouchListener mQuickShutterButtonClickListener;
    private OnLongClickListener mQuickShutterButtonLongClickListener;
    private OnTouchListener mQuickWindowExitButtonTouchListener;
    private RotateImageButton mRotateExitBtn;
    private RotateImageButton mRotateShutterBtn;
    private boolean mSetPreviewDisplayCheck;
    private CountDownLatch mSetPreviewDisplayLatch;
    private Object mSetPreviewDisplayLock;
    private ShutterButton mShutterBtn;
    private boolean mStartPreviewFail;
    protected boolean mStartPreviewOnGoing;
    private Runnable mStartPreviewRunnable;
    private Thread mStartPreviewThread;
    protected int mSurfaceHeight;
    protected SurfaceHolder mSurfaceHolder;
    protected int mSurfaceWidth;
    private Runnable mThreadStartRunnable;
    private boolean mUseOnResume;
    protected Object previousEffectParameter;
    protected int previousEffectType;
    protected String previousResolution;
    private final int ruleEnable;
    private final int shiftQuickWindow;

    class CameraErrorCallback implements ErrorCallback {
        CameraErrorCallback() {
        }

        public void onError(int error, Camera camera) {
            Log.e(FaceDetector.TAG, "ErrorCallback() : " + error);
            if (PreviewController.this.checkMediator()) {
                switch (error) {
                    case PreviewController.HEIGHT_INDEX /*1*/:
                        PreviewController.this.cameraErrorCallbackForUnKnown();
                    case Limit.OLA_FIFO_DATA_MAX_SIZE_SHORT /*100*/:
                        PreviewController.this.cameraErrorCallbackForServerDied();
                    default:
                }
            }
        }
    }

    private final class EffectCameraListener implements EffectsListener {
        private EffectCameraListener() {
        }

        public void onEffectsUpdate(int effectId, int effectMsg) {
            PreviewController.this.doOnEffectsUpdate(effectId, effectMsg);
        }

        public synchronized void onEffectsError(Exception exception, String fileName) {
            PreviewController.this.doOnEffectesError(exception, fileName);
        }
    }

    private final class EffectRecorderListener implements EffectsListener {
        private EffectRecorderListener() {
        }

        public void onEffectsUpdate(int effectId, int effectMsg) {
            PreviewController.this.doOnEffectsUpdate(effectId, effectMsg);
        }

        public synchronized void onEffectsError(Exception exception, String fileName) {
            PreviewController.this.doOnEffectesError(exception, fileName);
        }
    }

    public abstract String getPreviewSizeOnDevice();

    public abstract String getPreviewSizeOnScreen();

    public boolean isSensorSupportBackdropper() {
        return this.mIsSensorSupportBackdropper;
    }

    public PreviewController(ControllerFunction function) {
        super(function);
        this.mStartPreviewOnGoing = false;
        this.mCameraReleaseOnGoing = false;
        this.mSurfaceHolder = null;
        this.bRendered = false;
        this.mCameraMode = 0;
        this.mPreviewing = false;
        this.mBeginStartPreview = true;
        this.mStartPreviewFail = false;
        this.mEngineProcessor = new EngineProcessor();
        this.isReadyEngineProcessor = false;
        this.isPlayRingVideoSizeSet = false;
        this.mErrorCallback = new CameraErrorCallback();
        this.mEffectsRecorder = null;
        this.mEffectsCamera = null;
        this.mEffectType = 0;
        this.mEffectParameter = null;
        this.mProfileType = 4;
        this.mSurfaceWidth = 0;
        this.mSurfaceHeight = 0;
        this.mEffectRecorderListener = new EffectRecorderListener();
        this.mEffectCameraListener = new EffectCameraListener();
        this.previousEffectType = 0;
        this.previousEffectParameter = CameraConstants.SMART_MODE_OFF;
        this.previousResolution = null;
        this.mPreviousResolutionBack = null;
        this.mPreviousResolutionFront = null;
        this.shiftQuickWindow = -1;
        this.ruleEnable = HEIGHT_INDEX;
        this.divider = 2;
        this.mExitBtn = null;
        this.mRotateExitBtn = null;
        this.mShutterBtn = null;
        this.mRotateShutterBtn = null;
        this.mChangeMode = false;
        this.mChangedManualFocusToDefault = false;
        this.mChangedAutoReviewToDefault = false;
        this.mPrevAutoReviewVal = null;
        this.mIsSensorSupportBackdropper = true;
        this.mPIPRecordingController = null;
        this.mPIPCameraController = null;
        this.mLatch = null;
        this.mIsStartPreviewEffectOnGoing = false;
        this.mCameraGLPreview = null;
        this.mCameraGLPreviewExtra = null;
        this.mLockScreenPreventPreview = false;
        this.mSetPreviewDisplayLatch = null;
        this.mSetPreviewDisplayLock = new Object();
        this.mSetPreviewDisplayCheck = false;
        this.deviceOpenLatch = null;
        this.mStartPreviewThread = null;
        this.mUseOnResume = false;
        this.mStartPreviewRunnable = new Runnable() {
            public void run() {
                try {
                    CamLog.d(FaceDetector.TAG, "mStartPreviewThread start");
                    PreviewController.this.mStartPreviewFail = false;
                    if (PreviewController.this.mUseOnResume) {
                        PreviewController.this.startPreview(null, false);
                        return;
                    }
                    PreviewController.this.ensureCameraDevice();
                    try {
                        if (PreviewController.this.mLatch != null) {
                            PreviewController.this.mLatch.await();
                        }
                    } catch (InterruptedException ie) {
                        CamLog.e(FaceDetector.TAG, "InterruptedException : ", ie);
                        PreviewController.this.mStartPreviewFail = true;
                    }
                    if (!PreviewController.this.bRendered) {
                        PreviewController.this.startPreview(null, true);
                    }
                    CamLog.d(FaceDetector.TAG, "mStartPreviewThread end");
                } catch (CameraHardwareException e) {
                    PreviewController.this.mStartPreviewFail = true;
                    PreviewController.this.mGet.runOnUiThread(new Runnable() {
                        public void run() {
                            PreviewController.this.mGet.removePostRunnable(this);
                            PreviewController.this.mGet.showCameraErrorAndFinish();
                        }
                    });
                    CamLog.e(FaceDetector.TAG, "CameraHardwareException : ", e);
                }
            }
        };
        this.mThreadStartRunnable = new Runnable() {
            public void run() {
                PreviewController.this.mGet.removePostRunnable(this);
                if (!PreviewController.this.mGet.isPausing() && !PreviewController.this.mGet.isFinishingActivity()) {
                    PreviewController.this.mStartPreviewThread = new Thread(PreviewController.this.mStartPreviewRunnable);
                    PreviewController.this.mStartPreviewThread.start();
                }
            }
        };
        this.isBeautyshotProgress = false;
        this.mQuickWindowExitButtonTouchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (!PreviewController.this.mGet.isPausing()) {
                    PreviewController.this.mRotateExitBtn = (RotateImageButton) PreviewController.this.mGet.findViewById(R.id.smart_cover_window_exit_button);
                    if (event.getAction() == 0 && PreviewController.this.mRotateExitBtn != null) {
                        PreviewController.this.mRotateExitBtn.setPressed(true);
                    }
                    if (!(event.getAction() != 2 || v.isPressed() || PreviewController.this.mRotateExitBtn == null)) {
                        PreviewController.this.mRotateExitBtn.setPressed(false);
                    }
                    if (event.getAction() == PreviewController.HEIGHT_INDEX) {
                        if (PreviewController.this.mRotateExitBtn != null) {
                            PreviewController.this.mRotateExitBtn.setPressed(false);
                        }
                        if (v.isPressed()) {
                            PreviewController.this.mGet.getApplicationContext().sendBroadcast(new Intent(CameraConstants.INTENT_ACTION_CAMERA_FINISH));
                        }
                    }
                }
                return false;
            }
        };
        this.mQuickShutterButtonClickListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (!PreviewController.this.mGet.isPausing()) {
                    if (event.getAction() == 0) {
                        PreviewController.this.mGet.findViewById(R.id.smart_cover_window_shutter_button).setPressed(true);
                    }
                    if (event.getAction() == 2 && !v.isPressed()) {
                        PreviewController.this.mGet.findViewById(R.id.smart_cover_window_shutter_button).setPressed(false);
                        PreviewController.this.mGet.getPreviewPanelController().onShutterButtonFocus(PreviewController.this.mShutterBtn, false);
                    }
                    if (event.getAction() == PreviewController.HEIGHT_INDEX) {
                        PreviewController.this.mGet.findViewById(R.id.smart_cover_window_shutter_button).setPressed(false);
                        if (v.isPressed()) {
                            PreviewController.this.mGet.getPreviewPanelController().onShutterButtonFocus(PreviewController.this.mShutterBtn, true);
                            PreviewController.this.mGet.doCommand(Command.TAKE_PICTURE);
                            PreviewController.this.mGet.getPreviewPanelController().onShutterButtonFocus(PreviewController.this.mShutterBtn, false);
                        }
                    }
                }
                return false;
            }
        };
        this.mQuickShutterButtonLongClickListener = new OnLongClickListener() {
            public boolean onLongClick(View v) {
                PreviewController.this.mGet.getPreviewPanelController().onShutterButtonLongPressed(PreviewController.this.mShutterBtn);
                return false;
            }
        };
        this.mGestureEngineRunable = new Runnable() {
            public void run() {
                PreviewController.this.mGet.removePostRunnable(this);
                if (CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(PreviewController.this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
                    PreviewController.this.mGet.runGestureEngine(false);
                } else {
                    PreviewController.this.mGet.runGestureEngine(true);
                }
            }
        };
        this.mPreviewCallbackRunnable = new Runnable() {
            public void run() {
                CamLog.d(FaceDetector.TAG, "##### below onPreviewFrame is just a fake..it is not from sensor callback.");
                PreviewController.this.checkStartPreviewCallback();
            }
        };
        this.mOneShotPreviewCallback = new PreviewCallback() {
            public void onPreviewFrame(byte[] data, Camera camera) {
                PreviewController.this.checkStartPreviewCallback();
            }
        };
        this.mCheckCountOf_CAMERA_ERROR_SERVER_DIED = 0;
        this.mFaceDetectionStarted = false;
        this.mFaceDetectionHasUI = false;
        this.mCurrentEffect = CameraConstants.SMART_MODE_OFF;
    }

    public void initController() {
        this.mGet.inflateStub(R.id.stub_preview);
        this.mCameraPreview = (CameraPreview) this.mGet.findViewById(R.id.preview_holder_surface);
        this.mCameraPreview.setDeviceListener(this);
        this.mCameraGLPreview = (OpenGLSurfaceView) this.mGet.findViewById(R.id.preview_render_surface);
        this.mCameraGLPreview.initOpenGLSurfaceView(this);
        this.mCameraGLPreviewExtra = (SurfaceView) this.mGet.findViewById(R.id.preview_render_surface_extra);
        View previewLayout = this.mGet.findViewById(R.id.preview);
        this.mGet.inflateStub(R.id.stub_quick_window);
        this.mExitBtn = (ImageButtonEx) this.mGet.findViewById(R.id.smart_cover_window_exit_button_bg);
        this.mRotateExitBtn = (RotateImageButton) this.mGet.findViewById(R.id.smart_cover_window_exit_button);
        this.mShutterBtn = (ShutterButton) this.mGet.findViewById(R.id.smart_cover_window_shutter_button_bg);
        this.mRotateShutterBtn = (RotateImageButton) this.mGet.findViewById(R.id.smart_cover_window_shutter_button);
        if (26 == ModelProperties.getProjectCode()) {
            CameraConstants.setSmartCoverSize(378, Tag.Y_RESOLUTION, 503);
            this.mGet.findViewById(R.id.smart_cover_window_exit_view).setTranslationX(-10.0f);
            this.mGet.findViewById(R.id.smart_cover_window_exit_view).setTranslationY(-10.0f);
            this.mShutterBtn.setTranslationY(-35.0f);
            this.mRotateShutterBtn.setTranslationY(-35.0f);
        }
        enableQuickWindowButton(false);
        if (!(this.mExitBtn == null || this.mShutterBtn == null)) {
            this.mExitBtn.setOnTouchListener(this.mQuickWindowExitButtonTouchListener);
            this.mShutterBtn.setOnTouchListener(this.mQuickShutterButtonClickListener);
            this.mShutterBtn.setOnLongClickListener(this.mQuickShutterButtonLongClickListener);
        }
        if (this.mGet.getApplicationMode() == 0 && (CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)) || CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)))) {
            this.bRendered = true;
        } else {
            this.bRendered = false;
        }
        show();
        this.mChangeMode = false;
        if (this.mLatch != null) {
            this.mLatch.countDown();
            this.mLatch = null;
        }
        setupHolder(this.bRendered);
        CamLog.d(FaceDetector.TAG, "previewLayout initController [" + previewLayout + "]");
    }

    public Camera getCameraDevice() {
        if (this.mCameraDevice == null) {
            CamLog.i(FaceDetector.TAG, String.format("Camera ref is null. getCameraDevice() return null.", new Object[0]));
        }
        return this.mCameraDevice;
    }

    public LGCamera getLG() {
        if (this.mLGCamera == null) {
            CamLog.i(FaceDetector.TAG, String.format("LGCamera ref is null. getLG() return null.", new Object[0]));
        }
        return this.mLGCamera;
    }

    public EngineProcessor getEngineProcessor() {
        if (this.mEngineProcessor == null) {
            this.mEngineProcessor = new EngineProcessor();
        }
        return this.mEngineProcessor;
    }

    public void setLockScreenPreventPreview(boolean flag) {
        this.mLockScreenPreventPreview = flag;
    }

    public synchronized void startPreview(LGParameters lgParameter, boolean useCallback) {
        CamLog.d(FaceDetector.TAG, "# startpreview [start] : , mPreviewing = " + this.mPreviewing + ", mStartPreviewOnGoing = " + this.mStartPreviewOnGoing);
        if (this.mGet.isPausing() || this.mGet.isFinishingActivity()) {
            CamLog.d(FaceDetector.TAG, "startPreview()- ongoing finish");
        } else if (Common.isScreenLocked()) {
            CamLog.d(FaceDetector.TAG, "startpreview return, isScreenLock=" + Common.isScreenLocked());
            this.mStartPreviewOnGoing = false;
            this.mLockScreenPreventPreview = true;
            this.mGet.doCommandDelayed(Command.START_PREVIEW, 200);
        } else {
            this.mLockScreenPreventPreview = false;
            if (this.mStartPreviewOnGoing) {
                CamLog.d(FaceDetector.TAG, "startPreview()- ongoing return");
            } else {
                LGParameters lgParams;
                this.mStartPreviewOnGoing = true;
                CamLog.d(FaceDetector.TAG, "startPreview()-start mStartPreviewOnGoing=" + this.mStartPreviewOnGoing + ",mEffectType=" + this.mEffectType + ",mEffectParameter=" + this.mEffectParameter);
                CamLog.d(FaceDetector.TAG, "startPreview(): parameter = " + lgParameter + ", useCallback = " + useCallback);
                if (this.mGet.getApplicationMode() == 0 && (CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)) || CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)))) {
                    this.mGet.swapPreviewEffect(this.mGet.getFaceBeauty());
                }
                this.isReadyEngineProcessor = false;
                if (this.mGet.getCameraMode() != this.mGet.getCameraId()) {
                    this.mCameraMode = this.mGet.getCameraId();
                    CamLog.d(FaceDetector.TAG, "startPreview mCameraMode [" + this.mCameraMode + "] getCameraId() [" + this.mGet.getCameraId() + "]");
                    closeCamera();
                }
                this.mGet.removePostRunnable(this.mGestureEngineRunable);
                try {
                    ensureCameraDevice();
                } catch (CameraHardwareException e) {
                    this.mStartPreviewFail = true;
                    this.mGet.runOnUiThread(new Runnable() {
                        public void run() {
                            PreviewController.this.mGet.removePostRunnable(this);
                            PreviewController.this.mGet.showCameraErrorAndFinish();
                        }
                    });
                    CamLog.e(FaceDetector.TAG, "CameraHardwareException : ", e);
                } catch (Exception e2) {
                    CamLog.e(FaceDetector.TAG, "Exception occured in ensureCameraDevice()", e2);
                }
                if (this.mPreviewing) {
                    stopPreview();
                }
                this.mStartPreviewOnGoing = true;
                setCameraDisplayOrientation(this.mGet.getActivity(), this.mGet.getCameraId(), this.mCameraDevice);
                if (lgParameter != null) {
                    lgParams = lgParameter;
                } else {
                    try {
                        lgParams = this.mLGCamera.getLGParameters();
                    } catch (Exception e22) {
                        CamLog.e(FaceDetector.TAG, "startPreview getParameters Exception");
                        e22.printStackTrace();
                        this.mStartPreviewOnGoing = false;
                    }
                }
                if (ModelProperties.isOMAP4Chipset()) {
                    boolean z = lgParams.getParameters().isAutoExposureLockSupported() && lgParams.getParameters().isAutoWhiteBalanceLockSupported();
                    this.mIsSensorSupportBackdropper = z;
                }
                if (this.mCameraReleaseOnGoing) {
                    this.mStartPreviewOnGoing = false;
                } else {
                    this.mGet.doCommand(Command.APPLY_ALL_SETTINGS, lgParams);
                    if (effectsCamcorderActive()) {
                        this.mGet.setLiveeffectLimit();
                    }
                    if (this.mGet.isPlayRingMode() && !this.isPlayRingVideoSizeSet) {
                        ListPreference pref = this.mGet.getSettingListPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
                        String playRingResolution = "640x480";
                        if (pref != null) {
                            if (pref.findIndexOfValue("640x480") != -1) {
                                playRingResolution = "640x480";
                            } else if (pref.findIndexOfValue("720x480") != -1) {
                                playRingResolution = "720x480";
                            } else if (pref.findIndexOfValue("1280x720") != -1) {
                                playRingResolution = "1280x720";
                            } else {
                                playRingResolution = pref.getValue();
                            }
                            this.mGet.setSetting(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, playRingResolution);
                            this.mGet.doCommand(pref.getEntryCommand(), lgParams);
                        }
                        this.isPlayRingVideoSizeSet = true;
                    }
                    this.mGet.enableInput(false);
                    if (this.mGet.getApplicationMode() == HEIGHT_INDEX) {
                        if (this.mEffectsRecorder == null && CameraHolder.instance().getOneShotSetPreviewNull() != null) {
                            CamLog.d(FaceDetector.TAG, "startPreview creates EffectsRecorder");
                            this.mEffectsRecorder = new EffectsRecorder(this.mGet.getApplicationContext(), this);
                            CameraHolder.instance().setOneShotSetPreviewNullLatchCountDown();
                        }
                    } else if (this.mEffectsCamera == null && CameraHolder.instance().getOneShotSetPreviewNull() != null) {
                        CamLog.d(FaceDetector.TAG, "startPreview creates EffectsCamera");
                        this.mEffectsCamera = new EffectsCamera(this.mGet.getApplicationContext(), this);
                        CameraHolder.instance().setOneShotSetPreviewNullLatchCountDown();
                    }
                    if (this.mGet.isCafSupported()) {
                        if (this.mGet.getApplicationMode() == HEIGHT_INDEX) {
                            lgParams.getParameters().setFocusMode("continuous-video");
                            CamLog.d(FaceDetector.TAG, "### setFocusMode-conti");
                        } else if (CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
                            if (CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
                                lgParams.getParameters().setFocusMode("normal");
                                lgParams.getParameters().set("manualfocus_step", this.mGet.getManualFocusValue());
                                CamLog.d(FaceDetector.TAG, "### setFocusMode-manual");
                            }
                        } else if (this.mGet.isShutterButtonLongKey() || getCameraDevice() == null) {
                            lgParams.getParameters().setFocusMode(LGT_Limit.ISP_AUTOMODE_AUTO);
                            CamLog.d(FaceDetector.TAG, "### setFocusMode-auto");
                        } else {
                            if (FunctionProperties.isSupportAFonCAF() && FunctionProperties.isAutoFocusNullSettingNeededInStartPreview()) {
                                getCameraDevice().autoFocus(null);
                                CamLog.d(FaceDetector.TAG, "###getCameraDevice().autoFocus(null)");
                            }
                            if ("continuous-picture".equals(lgParams.getParameters().getFocusMode()) || CameraConstants.FOCUS_MODE_MULTIWINDOWAF.equals(lgParams.getParameters().getFocusMode())) {
                                getCameraDevice().cancelAutoFocus();
                                CamLog.d(FaceDetector.TAG, "###mCameraDevice.cancelAutoFocus()");
                            }
                            String defaultFocusMode = this.mGet.getDefaultFocusModeParameterForMultiWindowAF(lgParams);
                            lgParams.getParameters().setFocusMode(defaultFocusMode);
                            CamLog.d(FaceDetector.TAG, "### setFocusMode-" + defaultFocusMode);
                        }
                    } else if (!(ModelProperties.isNVIDIAChipset() || ModelProperties.isOMAP4Chipset() || this.mGet.getApplicationMode() != HEIGHT_INDEX)) {
                        lgParams.getParameters().setFocusMode("infinity");
                        CamLog.d(FaceDetector.TAG, "### setFocusMode-infinity");
                    }
                    lgParams.getParameters().set("lge-camera", HEIGHT_INDEX);
                    String shotMode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
                    if (this.mGet.getApplicationMode() == 0) {
                        if (FunctionProperties.isZslSupported()) {
                            String currentSceneMode = this.mGet.getSettingValue(Setting.KEY_SCENE_MODE);
                            if (this.mGet.isTimeMachineModeOn()) {
                                CamLog.d(FaceDetector.TAG, "#### Set ZSL on if camera shot mode is Time machine.");
                                lgParams.getParameters().set("zsl", CameraConstants.SMART_MODE_ON);
                            } else if (CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS.equals(shotMode)) {
                                CamLog.d(FaceDetector.TAG, "#### Set ZSL on if camera shot mode is Full frame CONTINUOUS.");
                                if (ModelProperties.isRenesasISP()) {
                                    CamLog.d(FaceDetector.TAG, "#### param set zsl off");
                                    lgParams.getParameters().set("zsl", CameraConstants.SMART_MODE_OFF);
                                } else {
                                    CamLog.d(FaceDetector.TAG, "#### param set zsl on");
                                    lgParams.getParameters().set("zsl", CameraConstants.SMART_MODE_ON);
                                }
                            } else if (CameraConstants.TYPE_SHOTMODE_PANORAMA.equals(shotMode)) {
                                CamLog.d(FaceDetector.TAG, "#### Set ZSL off if camera shot mode is PANORAMA.");
                                lgParams.getParameters().set("zsl", CameraConstants.SMART_MODE_OFF);
                            } else if (CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA.equals(shotMode)) {
                                CamLog.d(FaceDetector.TAG, "#### Set ZSL on if camera shot mode is PLANE PANORAMA.");
                                lgParams.getParameters().set("zsl", CameraConstants.SMART_MODE_ON);
                            } else if (CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA.equals(shotMode)) {
                                CamLog.d(FaceDetector.TAG, "#### Set ZSL off if camera shot mode is FREE PANORAMA.");
                                lgParams.getParameters().set("zsl", CameraConstants.SMART_MODE_OFF);
                                this.mGet.showFreePanoramaBlackBg();
                            } else if (CameraConstants.TYPE_SHOTMODE_HDR.equals(shotMode)) {
                                if (ModelProperties.isRenesasISP() || ModelProperties.getProjectCode() == 14) {
                                    CamLog.d(FaceDetector.TAG, "#### Set ZSL off if camera shot mode is HDR");
                                    lgParams.getParameters().set("zsl", CameraConstants.SMART_MODE_OFF);
                                } else {
                                    CamLog.d(FaceDetector.TAG, "#### Set ZSL on if camera shot mode is HDR");
                                    lgParams.getParameters().set("zsl", CameraConstants.SMART_MODE_ON);
                                }
                            } else if (CameraConstants.TYPE_SHOTMODE_CONTINUOUS.equals(shotMode)) {
                                CamLog.d(FaceDetector.TAG, "#### Set ZSL off if camera shot mode is CONTINUOUS.");
                                lgParams.getParameters().set("zsl", CameraConstants.SMART_MODE_OFF);
                            } else if (CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotMode) && CameraConstants.SCENE_MODE_SMART_SHUTTER.equals(currentSceneMode) && !this.mGet.isTimeMachineModeOn()) {
                                CamLog.d(FaceDetector.TAG, "#### Set ZSL off if camera shot mode is normal and scene mode is smart shutter.");
                                lgParams.getParameters().set("zsl", CameraConstants.SMART_MODE_OFF);
                            } else if (CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT.equals(shotMode)) {
                                CamLog.d(FaceDetector.TAG, "#### Set ZSL on if camera shot mode is CLEAR SHOT.");
                                lgParams.getParameters().set("zsl", CameraConstants.SMART_MODE_ON);
                            } else if (CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(shotMode)) {
                                CamLog.d(FaceDetector.TAG, "#### Set ZSL off if camera shot mode is DUAL CAMERA.");
                                lgParams.getParameters().set("zsl", CameraConstants.SMART_MODE_OFF);
                            } else if (FunctionProperties.isSupportSmartMode() && CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_SMART_MODE)) && !ModelProperties.isRenesasISP()) {
                                lgParams.getParameters().set("zsl", CameraConstants.SMART_MODE_ON);
                            } else if (FunctionProperties.isNonZSLMode()) {
                                CamLog.d(FaceDetector.TAG, "#### param set zsl off");
                                lgParams.getParameters().set("zsl", CameraConstants.SMART_MODE_OFF);
                            } else {
                                CamLog.d(FaceDetector.TAG, "#### param set zsl on");
                                lgParams.getParameters().set("zsl", CameraConstants.SMART_MODE_ON);
                            }
                        }
                        if (ModelProperties.isMTKChipset()) {
                            CamLog.d(FaceDetector.TAG, "#### param set cam-mode : 1");
                            lgParams.getParameters().set("cam-mode", HEIGHT_INDEX);
                        }
                        if (FunctionProperties.isVideoStabilizationSupported()) {
                            CamLog.d(FaceDetector.TAG, "param set video-stabilization false");
                            lgParams.getParameters().set("video-stabilization", CameraConstants.ONEKEY_CONTROL_DISABLE_STRING);
                        }
                        if (CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(shotMode)) {
                            CamLog.d(FaceDetector.TAG, "param set beauty-shot on");
                            lgParams.getParameters().set("beauty-shot", CameraConstants.SMART_MODE_ON);
                        } else {
                            CamLog.d(FaceDetector.TAG, "param set beauty-shot off");
                            lgParams.getParameters().set("beauty-shot", CameraConstants.SMART_MODE_OFF);
                        }
                    }
                    try {
                        Log.i(FaceDetector.TAG, "[Time Info][4] App Camera Param setting End : Camera Parameter setting " + Common.interimCheckTime(true) + " ms");
                        Log.i(FaceDetector.TAG, "[Time Info][5] Device Param setting Start : Device setting " + Common.interimCheckTime(false));
                        if (!(this.mGet.isCafSupported() || !ModelProperties.isMTKChipset() || ModelProperties.isFixedFocusModel())) {
                            lgParams.getParameters().setFocusAreas(null);
                            lgParams.getParameters().setMeteringAreas(null);
                        }
                        lgParams.setParameters(lgParams.getParameters());
                        Log.i(FaceDetector.TAG, "[Time Info][5] Device Param setting End : Device setting " + Common.interimCheckTime(true) + " ms");
                    } catch (Exception e222) {
                        CamLog.e(FaceDetector.TAG, "startPreview setParameters Exception");
                        e222.printStackTrace();
                    }
                    setPreviewDisplay(this.mSurfaceHolder);
                    if (this.mCameraDevice != null) {
                        this.mCameraDevice.setErrorCallback(this.mErrorCallback);
                    }
                    this.mBeginStartPreview = true;
                    try {
                        if (this.mCameraDevice != null) {
                            if (useCallback) {
                                this.mCameraDevice.setOneShotPreviewCallback(this.mOneShotPreviewCallback);
                            }
                            Log.i(FaceDetector.TAG, "[Time Info][6] Device StartPreview Start : Camera Driver Preview Operation " + Common.interimCheckTime(false));
                            if (ProjectVariables.bEnterSetting && this.mSurfaceHolder == null) {
                                this.mSetPreviewDisplayLatch = new CountDownLatch(HEIGHT_INDEX);
                                this.mSetPreviewDisplayLatch.await(500, TimeUnit.MILLISECONDS);
                            }
                            if (!(isDualCameraActive() || effectsCamcorderActive())) {
                                Log.e(FaceDetector.TAG, "##### TIME_CHECK startPreview-start");
                                this.mCameraDevice.startPreview();
                                Log.e(FaceDetector.TAG, "##### TIME_CHECK startPreview-end");
                            }
                            String currentPreviewSizeOnScreen;
                            if (this.mGet.getApplicationMode() == HEIGHT_INDEX) {
                                currentPreviewSizeOnScreen = "";
                                if (this.mCameraPreview != null) {
                                    currentPreviewSizeOnScreen = this.mCameraPreview.getHeight() + "x" + this.mCameraPreview.getWidth();
                                }
                                if (getPreviewSizeOnScreen().equals(currentPreviewSizeOnScreen) && (this.mGet.isDualRecordingActive() || this.mGet.isSmartZoomRecordingActive() || this.mGet.isLiveEffectActive())) {
                                    CamLog.d(FaceDetector.TAG, "startPreviewEffect() is called in *startPreview()* for dual recording");
                                    startPreviewEffect();
                                }
                            } else if (this.mGet.getApplicationMode() == 0) {
                                currentPreviewSizeOnScreen = "";
                                if (this.mCameraPreview != null) {
                                    if (this.mGet.isConfigureLandscape()) {
                                        currentPreviewSizeOnScreen = this.mCameraPreview.getWidth() + "x" + this.mCameraPreview.getHeight();
                                    } else {
                                        currentPreviewSizeOnScreen = this.mCameraPreview.getHeight() + "x" + this.mCameraPreview.getWidth();
                                    }
                                }
                                if (getPreviewSizeOnScreen().equals(currentPreviewSizeOnScreen) && CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
                                    CamLog.d(FaceDetector.TAG, "startPreviewEffect() is called in *startPreview()* for dual camera");
                                    startPreviewEffect();
                                } else {
                                    CamLog.d(FaceDetector.TAG, "startPreviewEffect() is NOT called in *startPreview()*");
                                }
                            }
                        }
                        this.mBeginStartPreview = false;
                        this.mPreviewing = true;
                        if (!useCallback) {
                            this.mPreviewCallbackRunnable.run();
                        }
                        CamLog.d(FaceDetector.TAG, "startPreview()-end mStartPreviewOnGoing =" + this.mStartPreviewOnGoing);
                    } catch (Throwable ex) {
                        CamLog.e(FaceDetector.TAG, "startPreview failed : ", ex);
                        closeCamera();
                        this.mGet.runOnUiThread(new Runnable() {
                            public void run() {
                                PreviewController.this.mGet.removePostRunnable(this);
                                PreviewController.this.mGet.showCameraErrorAndFinish();
                            }
                        });
                    }
                }
            }
        }
    }

    public void setPreviewDisplayLatchCountDown() {
        if (this.mSetPreviewDisplayLatch != null) {
            this.mSetPreviewDisplayLatch.countDown();
            this.mSetPreviewDisplayLatch = null;
        }
    }

    public void setEngineProcessor() {
        if (!getEngineProcessor().isEmptyEngine()) {
            if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) || Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)) || CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)) || CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
                if (this.bRendered) {
                    this.mCameraGLPreview.setEngineProcessor(getEngineProcessor(), this.mCameraDevice);
                } else {
                    this.mCameraPreview.setEngineProcessor(getEngineProcessor(), this.mCameraDevice);
                }
                this.isReadyEngineProcessor = true;
            }
        }
    }

    public void removePreviewCallback() {
        if (this.mCameraDevice != null && isReadyEngineProcessor()) {
            CamLog.d(FaceDetector.TAG, "removePreviewCallback call");
            this.mCameraDevice.setPreviewCallback(null);
            this.mCameraDevice.addCallbackBuffer(null);
            this.isReadyEngineProcessor = false;
        }
    }

    public boolean isReadyEngineProcessor() {
        return this.isReadyEngineProcessor;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (checkMediator()) {
            CamLog.d(FaceDetector.TAG, String.format("#### surfaceChanged %dx%d, mode:%s", new Object[]{Integer.valueOf(width), Integer.valueOf(height), this.mGet.getApplicationModeString()}));
            this.mSurfaceWidth = width;
            this.mSurfaceHeight = height;
            if (holder.getSurface() == null) {
                CamLog.w(FaceDetector.TAG, String.format("surfaceChanged return", new Object[0]));
                CamLog.w(FaceDetector.TAG, "holder.getSurface() == null");
                return;
            }
            this.mSurfaceHolder = holder;
            if (this.mCameraDevice == null || this.mGet.isPausing() || this.mGet.isFinishingActivity()) {
                CamLog.w(FaceDetector.TAG, String.format("surfaceChanged return", new Object[0]));
                CamLog.w(FaceDetector.TAG, String.format("mCameraDevice:%s pausing:%b finishing:%b", new Object[]{this.mCameraDevice, Boolean.valueOf(this.mGet.isPausing()), Boolean.valueOf(this.mGet.isFinishingActivity())}));
                return;
            }
            CamLog.d(FaceDetector.TAG, String.format("mPreviewing:%b holder.isCreating():%b mStartPreviewOnGoing:%b", new Object[]{Boolean.valueOf(this.mPreviewing), Boolean.valueOf(holder.isCreating()), Boolean.valueOf(this.mStartPreviewOnGoing)}));
            try {
                if (holder.isCreating()) {
                    setPreviewDisplay(holder);
                    setPreviewDisplayLatchCountDown();
                    if (this.mGet.getApplicationMode() == HEIGHT_INDEX && effectsCamcorderActive()) {
                        if (this.previousResolution == null) {
                            ListPreference videoSizePref = this.mGet.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
                            if (videoSizePref != null) {
                                this.previousResolution = videoSizePref.getValue();
                                CamLog.d(FaceDetector.TAG, "backup previous resolution:" + this.previousResolution);
                                CamLog.d(FaceDetector.TAG, "set to live effect limit");
                                this.mGet.setLiveeffectLimit();
                            } else {
                                CamLog.d(FaceDetector.TAG, "videoSizePref is null");
                            }
                        }
                        CamLog.d(FaceDetector.TAG, "startPreviewEffect is called in *surfaceChanged*");
                        startPreviewEffect();
                    } else if (effectsCameraActive()) {
                        if (CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(this.mGet.getPreviousShotModeString()) || CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(this.mGet.getPreviousShotModeString())) {
                            CamLog.d(FaceDetector.TAG, "Because previous shot mode is beauty shot, so startPreview is not called in surfaceChanged");
                            return;
                        } else {
                            CamLog.d(FaceDetector.TAG, "startPreviewEffect is called in *surfaceChanged*");
                            startPreviewEffect();
                        }
                    }
                } else if (this.mLockScreenPreventPreview) {
                    String str = FaceDetector.TAG;
                    Object[] objArr = new Object[HEIGHT_INDEX];
                    objArr[0] = Boolean.valueOf(this.mLockScreenPreventPreview);
                    CamLog.i(str, String.format("lockScreen:%b", objArr));
                    CamLog.i(FaceDetector.TAG, String.format("Not start preview in surfaceChanged()", new Object[0]));
                } else {
                    if (this.mGet.getApplicationMode() == HEIGHT_INDEX) {
                        switch (this.mGet.getVideoState()) {
                            case HEIGHT_INDEX /*1*/:
                            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                                return;
                            case LGKeyRec.EVENT_STARTED /*3*/:
                            case LGKeyRec.EVENT_STOPPED /*4*/:
                                this.mGet.setVideoState(2);
                                if (!this.mGet.isPausing()) {
                                    this.mGet.stopRecording(false);
                                    break;
                                } else {
                                    this.mGet.stopRecordingByPausing();
                                    break;
                                }
                            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                                CamLog.d(FaceDetector.TAG, "isStopRecordingByMountedAction=" + this.mGet.isStopRecordingByMountedAction());
                                if ((this.mGet.isAttachIntent() || this.mGet.isMMSIntent()) && this.mGet.isStopRecordingByMountedAction()) {
                                    this.mGet.setVideoStateOnly(5);
                                    this.mGet.doCommandDelayed(Command.DISPLAY_CAMCORDER_POSTVIEW, 1000);
                                    return;
                                } else if (CameraConstants.SMART_MODE_OFF.equals(this.mGet.getSettingValue(Setting.KEY_VIDEO_AUTO_REVIEW)) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(this.mGet.getSettingValue(Setting.KEY_VIDEO_AUTO_REVIEW))) {
                                    String strUri = SharedPreferenceUtil.getLastVideoUri(this.mGet.getApplicationContext());
                                    if (strUri != null) {
                                        this.mGet.setLastThumb(Uri.parse(strUri), false);
                                    }
                                    this.mGet.doCommandUi(Command.ON_DELAY_OFF);
                                    break;
                                } else {
                                    this.mGet.setVideoStateOnly(5);
                                    this.mGet.doCommandDelayed(Command.DISPLAY_CAMCORDER_POSTVIEW, 1000);
                                    return;
                                }
                        }
                    } else if (this.mGet.isPanoramaStarted()) {
                        this.mGet.stopPanorama();
                        this.mGet.playRecordingSound(false);
                        return;
                    } else if (this.mGet.getFreePanoramaStatus() == 3) {
                        this.mGet.stopFreePanorama();
                        return;
                    }
                    if (effectsCamcorderActive() || effectsCameraActive()) {
                        CamLog.d(FaceDetector.TAG, "startPreviewEffect is called in *surfaceChanged()* mEffectType:" + this.mEffectType);
                        startPreviewEffect();
                    } else {
                        CamLog.d(FaceDetector.TAG, "startPreview - *surfaceChanged()*");
                        startPreview(null, true);
                    }
                }
            } catch (Exception e) {
                CamLog.w(FaceDetector.TAG, "surfaceChanged Exception:", e);
                stopPreview();
                startPreview(null, true);
                this.mGet.doCommandDelayed(Command.DISPLAY_PREVIEW, 1000);
            }
            this.mGet.updateNavigationBarShape();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (checkMediator()) {
            String str = FaceDetector.TAG;
            Object[] objArr = new Object[HEIGHT_INDEX];
            objArr[0] = this.mGet.getApplicationModeString();
            CamLog.d(str, String.format("#### surfaceCreated, mode:%s", objArr));
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (!checkMediator()) {
            return;
        }
        if (this.mGet.getApplicationMode() == HEIGHT_INDEX && (this.mGet.getVideoState() == 3 || this.mGet.getVideoState() == 4)) {
            this.mSurfaceHolder = null;
            CamLog.d(FaceDetector.TAG, "To prevent mCameraDevice.stopPreview() before MediaRecorder.stop()");
            return;
        }
        stopPreview();
        this.mSurfaceHolder = null;
        String str = FaceDetector.TAG;
        Object[] objArr = new Object[HEIGHT_INDEX];
        objArr[0] = this.mGet.getApplicationModeString();
        CamLog.d(str, String.format("##### surfaceDestroyed, mode:%s", objArr));
    }

    private void setPreviewDisplay(SurfaceHolder holder) {
        CamLog.d(FaceDetector.TAG, String.format("##### mCameraDevice.setPreviewDisplay device:%s, holder:%s", new Object[]{this.mCameraDevice, holder}));
        if (this.mSetPreviewDisplayCheck) {
            CamLog.d(FaceDetector.TAG, "return setPreviewDisplay : " + this.mSetPreviewDisplayCheck);
            return;
        }
        if (holder != null) {
            this.mSetPreviewDisplayCheck = true;
        }
        synchronized (this.mSetPreviewDisplayLock) {
            try {
                this.mCameraDevice.setPreviewDisplay(holder);
            } catch (Throwable ex) {
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        PreviewController.this.mGet.removePostRunnable(this);
                        PreviewController.this.mGet.showCameraStoppedAndFinish();
                    }
                });
                ex.printStackTrace();
            }
        }
        this.mGet.updateNavigationBarShape();
        CamLog.d(FaceDetector.TAG, String.format("##### mCameraDevice.setPreviewDisplay end", new Object[0]));
    }

    public synchronized void stopPreview() {
        CamLog.d(FaceDetector.TAG, "StopPreview()-start, mPreviewing=" + this.mPreviewing + "/mBeginStartPreview=" + this.mBeginStartPreview);
        if (!(this.mCameraDevice == null || !this.mPreviewing || this.mBeginStartPreview)) {
            try {
                if (this.mGet.getApplicationMode() == HEIGHT_INDEX) {
                    boolean bWait = false;
                    boolean bChangeParameters = false;
                    Parameters params = this.mCameraDevice.getParameters();
                    if (ModelProperties.isOMAP4Chipset() && this.mGet.getSettingValue(Setting.KEY_FLASH).equals(CameraConstants.FLASH_TORCH)) {
                        params.setFlashMode(CameraConstants.SMART_MODE_OFF);
                        bWait = true;
                        bChangeParameters = true;
                    }
                    if (bChangeParameters) {
                        this.mCameraDevice.setParameters(params);
                    }
                    if (bWait && ModelProperties.isOMAP4Chipset()) {
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (ModelProperties.isRenesasISP()) {
                    this.mLGCamera.setISPDataCallbackMode(null);
                } else {
                    if (FunctionProperties.isSupportSmartMode()) {
                        this.mLGCamera.setISPDataCallbackMode(null);
                    }
                    if (FunctionProperties.isSupportObjectTracking()) {
                        this.mLGCamera.setOBTDataCallbackMode(null);
                    }
                }
                if (FunctionProperties.beSupportMoveCallbackFromSensor()) {
                    this.mGet.unregisterCAFCallback();
                }
                CamLog.d(FaceDetector.TAG, "### mCameraDevice.stopPreview()");
                this.mCameraDevice.setPreviewCallbackWithBuffer(null);
                this.mCameraDevice.setPreviewCallback(null);
                this.mGet.releaseGestureEngine();
                if (this.mGet.isDualRecordingActive() || this.mGet.isDualCameraActive()) {
                    DualRecorderFilter.manualStopPreview();
                }
                this.mCameraDevice.stopPreview();
            } catch (Exception e2) {
                CamLog.d(FaceDetector.TAG, "Warn: stopPreview() : ", e2);
            }
        }
        this.mPreviewing = false;
        this.mBeginStartPreview = false;
        this.mStartPreviewOnGoing = false;
        this.mSetPreviewDisplayCheck = false;
        if (this.mFaceDetectionStarted) {
            this.mGet.stopFaceDetection();
        }
        CamLog.d(FaceDetector.TAG, "stopPreview()-end");
    }

    private void ensureCameraDevice() throws CameraHardwareException {
        CamLog.d(FaceDetector.TAG, "ensureCameraDevice() deviceOpenLatch:" + this.deviceOpenLatch + " device:" + this.mCameraDevice);
        if (this.deviceOpenLatch != null) {
            if (!this.mStartPreviewFail) {
                int trial = HEIGHT_INDEX;
                while (trial < 4) {
                    try {
                        Object[] objArr;
                        if (this.mCameraDevice == null) {
                            objArr = new Object[HEIGHT_INDEX];
                            objArr[0] = Integer.valueOf(trial);
                            CamLog.d("TAG", String.format(" Camera device is opening in another thread, wait for done %d...", objArr));
                            objArr = new Object[HEIGHT_INDEX];
                            objArr[0] = this.mCameraDevice;
                            CamLog.d("TAG", String.format(" currently camera device:%s", objArr));
                        }
                        if (this.deviceOpenLatch != null) {
                            if (this.deviceOpenLatch.await(1000, TimeUnit.MILLISECONDS)) {
                                objArr = new Object[HEIGHT_INDEX];
                                objArr[0] = this.mCameraDevice;
                                CamLog.d("TAG", String.format(" Opened. Camera device:%s", objArr));
                                break;
                            }
                            objArr = new Object[HEIGHT_INDEX];
                            objArr[0] = Boolean.valueOf(this.mStartPreviewFail);
                            CamLog.d("TAG", String.format(" Device open latch timeout! startPreviewFail:%b", objArr));
                        }
                        trial += HEIGHT_INDEX;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                return;
            }
        }
        if (this.mCameraDevice == null) {
            this.deviceOpenLatch = new CountDownLatch(HEIGHT_INDEX);
            try {
                if (CameraHolder.instance().getUsers() == 0) {
                    int ui_cam_id = this.mGet.getCameraId();
                    CamLog.i(FaceDetector.TAG, "call CameraHolder.open(" + ui_cam_id + ")");
                    int old_device_cam_id = CameraHolder.instance().getRealCameraId();
                    this.mCameraDevice = CameraHolder.instance().open(ui_cam_id);
                    this.mLGCamera = CameraHolder.instance().getLG();
                    int device_cam_id = CameraHolder.instance().getRealCameraId();
                    if (old_device_cam_id != ui_cam_id && old_device_cam_id == device_cam_id) {
                        this.mGet.setCameraId(device_cam_id);
                        this.mGet.setCameraMode(device_cam_id);
                        Setting.writePreferredCameraId(this.mGet.getActivity().getSharedPreferences(Setting.SETTING_PRIMARY, 0), device_cam_id);
                        CamLog.d(FaceDetector.TAG, "reset ui menu for device number:" + device_cam_id);
                        this.mGet.doCommandUi(Command.SET_CAMERA_ID_BEFORE_START_INIT);
                    }
                    CamLog.d(FaceDetector.TAG, "open routine done readlCamId:" + device_cam_id);
                } else {
                    CamLog.d(FaceDetector.TAG, String.format("Camera device user != 0 SOMETHING WRONG!!", new Object[0]));
                }
                CamLog.d(FaceDetector.TAG, String.format("Camera device opening done.", new Object[0]));
                this.deviceOpenLatch.countDown();
                this.deviceOpenLatch = null;
                CamLog.d(FaceDetector.TAG, String.format("latch countDown called", new Object[0]));
            } catch (Throwable th) {
                this.deviceOpenLatch.countDown();
                this.deviceOpenLatch = null;
                CamLog.d(FaceDetector.TAG, String.format("latch countDown called", new Object[0]));
            }
        }
    }

    public void closeCamera() {
        CamLog.d(FaceDetector.TAG, "closeCamera()-start, mCameraDevice is null?:" + (this.mCameraDevice == null));
        if (this.mCameraDevice != null) {
            if (this.mEffectsRecorder != null) {
                this.mEffectsRecorder.release();
                this.mEffectsRecorder = null;
            }
            if (this.mEffectsCamera != null) {
                this.mEffectsCamera.release();
                this.mEffectsCamera = null;
            }
            this.mEffectType = 0;
            this.mEffectParameter = null;
            this.mCameraReleaseOnGoing = true;
            try {
                this.mCameraDevice.setZoomChangeListener(null);
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "closeCamera setZoomChangeListener exception : ", e);
            }
            if (ProjectVariables.isSupportManualAntibanding()) {
                CamLog.d(FaceDetector.TAG, "closeCamera()-zoom reset");
                try {
                    if (this.mGet.getCameraId() == 0) {
                        this.mGet.resetZoomController();
                    }
                } catch (Exception e2) {
                    CamLog.e(FaceDetector.TAG, "closeCamera zoom reset exception : ", e2);
                }
            }
            this.mCameraDevice = null;
            this.mLGCamera = null;
            this.mPreviewing = false;
            CameraHolder.instance().release();
            this.mCameraReleaseOnGoing = false;
            this.mFaceDetectionStarted = false;
        }
        CamLog.d(FaceDetector.TAG, "closeCamera()-end");
    }

    public void restartPreview(LGParameters lgParameter, boolean useCallBack) {
        CamLog.d(FaceDetector.TAG, "restartPreview()-useCallBack : " + useCallBack);
        if (!isPreviewing() || this.mStartPreviewOnGoing) {
            CamLog.w(FaceDetector.TAG, "Not previewing now, do nothing.");
            return;
        }
        this.mGet.enableInput(false);
        if (this.mGet.getApplicationMode() == 0) {
            this.mGet.setApplicationMode(0);
        } else {
            this.mGet.setApplicationMode(HEIGHT_INDEX);
        }
        try {
            startPreview(lgParameter, useCallBack);
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    PreviewController.this.mGet.removePostRunnable(this);
                    PreviewController.this.mGet.enableCommand(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    PreviewController.this.mGet.removePostRunnable(this);
                    PreviewController.this.mGet.showCameraErrorAndFinish();
                }
            });
        }
    }

    public boolean isPreviewing() {
        return this.mPreviewing;
    }

    public void setPreviewing(boolean state) {
        this.mPreviewing = state;
    }

    public void setStartPreviewOnGoing(boolean state) {
        this.mStartPreviewOnGoing = state;
    }

    public boolean isPreviewOnGoing() {
        return this.mStartPreviewOnGoing;
    }

    public SurfaceHolder getSurfaceHolder() {
        return this.mSurfaceHolder;
    }

    public void onCreate() {
        int i = 4;
        CamLog.d(FaceDetector.TAG, "onCreate-start");
        this.mEffectsRecorder = null;
        if (this.mGet.getApplicationMode() == HEIGHT_INDEX) {
            this.mEffectsRecorder = new EffectsRecorder(this.mGet.getApplicationContext(), this);
            if (MultimediaProperties.isLiveEffectSupported() || MultimediaProperties.isDualRecordingSupported()) {
                if (!CamcorderProfileEx.hasProfile(this.mGet.getCameraId(), 4)) {
                    i = 10;
                }
                this.mProfileType = i;
                this.mProfile = CamcorderProfileEx.get(this.mGet.getCameraId(), this.mProfileType);
                CamLog.v(FaceDetector.TAG, "profile : " + this.mProfile.videoFrameWidth + "x" + this.mProfile.videoFrameHeight);
            }
        }
        this.mEffectsCamera = null;
        if (this.mGet.getApplicationMode() == 0) {
            this.mEffectsCamera = new EffectsCamera(this.mGet.getApplicationContext(), this);
        }
        this.mLatch = new CountDownLatch(HEIGHT_INDEX);
        if (!Common.isFaceUnlock()) {
            CamLog.d(FaceDetector.TAG, "startPreviewThread start-onCreate");
            this.mUseOnResume = false;
            this.mThreadStartRunnable.run();
        }
        CamLog.d(FaceDetector.TAG, "onCreate-end");
    }

    public void onResume() {
        int i;
        int maxScreenSizeX;
        int maxScreenSizeY;
        CamLog.d(FaceDetector.TAG, "onResume-start appmode:" + this.mGet.getApplicationMode());
        this.mEffectType = readEffectType();
        this.mEffectParameter = readEffectParameter();
        this.previousEffectType = 0;
        this.previousEffectParameter = CameraConstants.SMART_MODE_OFF;
        int readEffectType = readEffectType();
        Object readEffectParameter = readEffectParameter();
        CamLog.d(FaceDetector.TAG, "mEffectType : " + i + " mEffectParameter : " + r23);
        if (this.mGet.isConfigureLandscape()) {
            maxScreenSizeX = CameraConstants.LCD_SIZE_WIDTH;
            maxScreenSizeY = CameraConstants.LCD_SIZE_HEIGHT;
        } else {
            maxScreenSizeX = CameraConstants.LCD_SIZE_HEIGHT;
            maxScreenSizeY = CameraConstants.LCD_SIZE_WIDTH;
        }
        int subWindowX1 = maxScreenSizeX - Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.preview_panel_width);
        int subWindowY1 = maxScreenSizeY - 0;
        int subWindowX0 = subWindowX1 - ((int) TypedValue.applyDimension(HEIGHT_INDEX, 270.0f, this.mGet.getResources().getDisplayMetrics()));
        int subWindowY0 = subWindowY1 - ((int) TypedValue.applyDimension(HEIGHT_INDEX, CameraConstants.PIP_SUBWINDOW_INIT_HEIGHT_UVGA, this.mGet.getResources().getDisplayMetrics()));
        if (this.mGet.getApplicationMode() == HEIGHT_INDEX) {
            if (this.mPIPRecordingController == null) {
                this.mPIPRecordingController = new PIPRecordingController(this, subWindowX0, subWindowY0, subWindowX1, subWindowY1);
            } else {
                this.mPIPRecordingController.setSubWindowPosition(subWindowX0, subWindowY0, subWindowX1, subWindowY1);
            }
            if (this.mEffectsRecorder == null) {
                this.mEffectsRecorder = new EffectsRecorder(this.mGet.getApplicationContext(), this);
            }
        } else {
            if (this.mPIPCameraController == null) {
                this.mPIPCameraController = new PIPCameraController(this, subWindowX0, subWindowY0, subWindowX1, subWindowY1);
            } else {
                this.mPIPCameraController.setSubWindowPosition(subWindowX0, subWindowY0, subWindowX1, subWindowY1);
            }
            if (this.mEffectsCamera == null) {
                this.mEffectsCamera = new EffectsCamera(this.mGet.getApplicationContext(), this);
            }
        }
        setEffectRecorderPausing(false);
        if (this.mGet.getApplicationMode() == HEIGHT_INDEX) {
            if (MultimediaProperties.isLiveEffectSupported()) {
                this.mProfileType = CamcorderProfileEx.hasProfile(this.mGet.getCameraId(), 4) ? 4 : 10;
                this.mProfile = CamcorderProfileEx.get(this.mGet.getCameraId(), this.mProfileType);
                i = this.mProfile.videoFrameWidth;
                i = this.mProfile.videoFrameHeight;
                CamLog.d(FaceDetector.TAG, "profile : " + i + "x" + i);
            }
            this.mGet.recordingControllerShow();
            if (this.mGet.getApplicationMode() == HEIGHT_INDEX) {
                int indexOfPreviousResolution;
                int indexOfPreviousResolutionSwap;
                PreferenceGroup prefGroupSwap;
                if (this.mGet.getCameraMode() == 0) {
                    indexOfPreviousResolution = SharedPreferenceUtil.getVideoSizeIndexAtPrimaryNormalMode(this.mGet.getApplicationContext());
                    indexOfPreviousResolutionSwap = SharedPreferenceUtil.getVideoSizeIndexAtSecondaryNormalMode(this.mGet.getApplicationContext());
                } else {
                    indexOfPreviousResolution = SharedPreferenceUtil.getVideoSizeIndexAtSecondaryNormalMode(this.mGet.getApplicationContext());
                    indexOfPreviousResolutionSwap = SharedPreferenceUtil.getVideoSizeIndexAtPrimaryNormalMode(this.mGet.getApplicationContext());
                }
                ListPreference pref = this.mGet.getSettingListPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
                if (pref == null || indexOfPreviousResolution == -1) {
                    CamLog.e(FaceDetector.TAG, "Error: pref = " + pref + ", indexOfPreviousResolution = " + indexOfPreviousResolution);
                } else {
                    this.previousResolution = pref.getEntryValues()[indexOfPreviousResolution].toString();
                }
                if (this.mGet.getCameraMode() == 0) {
                    prefGroupSwap = this.mGet.getFrontPreferenceGroup();
                } else {
                    prefGroupSwap = this.mGet.getBackPreferenceGroup();
                }
                if (prefGroupSwap != null) {
                    ListPreference listPrefVideoSize = prefGroupSwap.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
                    if (!(listPrefVideoSize == null || indexOfPreviousResolutionSwap == -1)) {
                        if (this.mGet.getCameraMode() == 0) {
                            this.mPreviousResolutionFront = listPrefVideoSize.getEntryValues()[indexOfPreviousResolutionSwap].toString();
                        } else {
                            this.mPreviousResolutionBack = listPrefVideoSize.getEntryValues()[indexOfPreviousResolutionSwap].toString();
                        }
                    }
                }
            }
        }
        int video_state = this.mGet.getVideoState();
        boolean z = this.mPreviewing;
        z = this.mStartPreviewOnGoing;
        String str = ", video_state: ";
        CamLog.d(FaceDetector.TAG, "onResume : mPreviewing:" + z + ", mStartPreviewOnGoing:" + z + r23 + video_state);
        boolean needStartPreview = false;
        long delayTime = 0;
        if (this.mPreviewing || this.mStartPreviewOnGoing || ProjectVariables.bEnterSetting || video_state == 6 || video_state == 5 || this.mGet.getStatus() == 2) {
            this.mUseOnResume = false;
        } else {
            show();
            this.mCameraGLPreview.onResume();
            needStartPreview = true;
            this.mUseOnResume = true;
        }
        if (Common.isFaceUnlock()) {
            delayTime = 300;
            needStartPreview = true;
        }
        if (needStartPreview && (this.mStartPreviewThread == null || !this.mStartPreviewThread.isAlive())) {
            str = " delayTime is = ";
            CamLog.d(FaceDetector.TAG, "mStartPreviewThread : OnResume - mUseOnResume = " + this.mUseOnResume + r23 + delayTime);
            this.mGet.removePostRunnable(this.mThreadStartRunnable);
            this.mGet.postOnUiThread(this.mThreadStartRunnable, delayTime);
        }
        CamLog.d(FaceDetector.TAG, "onResume-end");
    }

    public void onPause() {
        CamLog.d(FaceDetector.TAG, "onPause-start ");
        this.mSetPreviewDisplayCheck = false;
        ProjectVariables.bEnterSetting = false;
        Common.setQuickWindowCameraMode(false);
        if (this.mChangedManualFocusToDefault) {
            this.mGet.setSetting(Setting.KEY_FOCUS, CameraConstants.FOCUS_SETTING_VALUE_MANUAL);
            this.mGet.doCommand(Command.CAMERA_FOCUS_MODE);
            this.mChangedManualFocusToDefault = false;
        }
        if (this.mChangedAutoReviewToDefault) {
            this.mGet.setSetting(Setting.KEY_CAMERA_AUTO_REVIEW, this.mPrevAutoReviewVal);
            this.mGet.doCommand(Command.CAMERA_AUTO_REVIEW);
            this.mChangedAutoReviewToDefault = false;
        }
        this.mCheckCountOf_CAMERA_ERROR_SERVER_DIED = 0;
        setPreviewDisplayLatchCountDown();
        if (this.mCameraGLPreview != null) {
            this.mCameraGLPreview.onPause();
        }
        releaseEngine(true);
        waitStartPreviewThreadDone();
        if (this.mGet.getApplicationMode() == HEIGHT_INDEX) {
            String previousResolutionBeforeSwap;
            PreferenceGroup prefGroupBeforeSwap;
            ListPreference pref = this.mGet.getSettingListPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
            int indexOfPreviousResolution = 0;
            if (pref != null) {
                indexOfPreviousResolution = pref.findIndexOfValue(this.previousResolution);
                if (indexOfPreviousResolution == -1) {
                    CamLog.e(FaceDetector.TAG, "indexOfPreviousResolution has wrong value");
                }
            }
            ListPreference listPrefVideoSizeBeforeSwap = null;
            ListPreference listPrefVideoModeBeforeSwap = null;
            if (this.mGet.getCameraMode() == 0) {
                previousResolutionBeforeSwap = this.mPreviousResolutionFront;
            } else {
                previousResolutionBeforeSwap = this.mPreviousResolutionBack;
            }
            if (this.mGet.getCameraMode() == 0) {
                prefGroupBeforeSwap = this.mGet.getFrontPreferenceGroup();
            } else {
                prefGroupBeforeSwap = this.mGet.getBackPreferenceGroup();
            }
            if (prefGroupBeforeSwap != null) {
                listPrefVideoSizeBeforeSwap = prefGroupBeforeSwap.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
                listPrefVideoModeBeforeSwap = prefGroupBeforeSwap.findPreference(Setting.KEY_VIDEO_RECORD_MODE);
            }
            if (this.mGet.isBackKeyPressed()) {
                if (effectsCamcorderActive() && this.previousResolution != null) {
                    this.mGet.setSelectedChild(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, this.previousResolution, true);
                }
                if (!(previousResolutionBeforeSwap == null || listPrefVideoSizeBeforeSwap == null || listPrefVideoModeBeforeSwap == null)) {
                    String videoModeBeforeSwap = listPrefVideoModeBeforeSwap.getValue();
                    if (CameraConstants.TYPE_RECORDMODE_DUAL.equals(videoModeBeforeSwap) || CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(videoModeBeforeSwap) || CameraConstants.TYPE_RECORDMODE_SMART_ZOOM.equals(videoModeBeforeSwap)) {
                        listPrefVideoSizeBeforeSwap.setValue(previousResolutionBeforeSwap);
                    }
                }
                this.mGet.setBackKeyPressed(false);
            }
            int index = 0;
            if (!(listPrefVideoSizeBeforeSwap == null || previousResolutionBeforeSwap == null)) {
                index = listPrefVideoSizeBeforeSwap.findIndexOfValue(previousResolutionBeforeSwap);
            }
            if (this.mGet.getCameraMode() == 0) {
                SharedPreferenceUtil.saveVideoSizeIndexAtPrimaryNormalMode(this.mGet.getApplicationContext(), indexOfPreviousResolution);
                SharedPreferenceUtil.saveVideoSizeIndexAtSecondaryNormalMode(this.mGet.getApplicationContext(), index);
            } else {
                SharedPreferenceUtil.saveVideoSizeIndexAtSecondaryNormalMode(this.mGet.getApplicationContext(), indexOfPreviousResolution);
                SharedPreferenceUtil.saveVideoSizeIndexAtPrimaryNormalMode(this.mGet.getApplicationContext(), index);
            }
            if (isLiveEffectActive()) {
                int effectIndex = this.mGet.getLiveEffectList().indexOf(this.mGet.getLiveEffect());
                if (this.mGet.getCameraMode() == 0) {
                    SharedPreferenceUtil.saveLiveEffectFaceIndex(this.mGet.getApplicationContext(), effectIndex);
                } else {
                    SharedPreferenceUtil.saveFrontLiveEffectFaceIndex(this.mGet.getApplicationContext(), effectIndex);
                }
            }
            if (isDualRecordingActive()) {
                int DualRecordingPipIndex = this.mGet.getCurrentPIPMask();
                if (this.mGet.getCameraMode() == 0) {
                    SharedPreferenceUtil.saveDualCamcorderPIPIndex(this.mGet.getApplicationContext(), DualRecordingPipIndex);
                } else {
                    SharedPreferenceUtil.saveFrontDualCamcorderPIPIndex(this.mGet.getApplicationContext(), DualRecordingPipIndex);
                }
            }
            if (isSmartZoomRecordingActive()) {
                int SmartZoomPipIndex = this.mGet.getCurrentPIPMask();
                if (this.mGet.getCameraMode() == 0) {
                    SharedPreferenceUtil.saveSmartZoomPIPIndex(this.mGet.getApplicationContext(), SmartZoomPipIndex);
                } else {
                    SharedPreferenceUtil.saveFrontSmartZoomPIPIndex(this.mGet.getApplicationContext(), SmartZoomPipIndex);
                }
            }
        } else {
            CamLog.d(FaceDetector.TAG, "isDualCamera : " + isDualCameraActive());
            if (isDualCameraActive()) {
                int DualCameraPipIndex = this.mGet.getCurrentPIPMask();
                if (this.mGet.getCameraMode() == 0) {
                    SharedPreferenceUtil.saveDualCameraPIPIndex(this.mGet.getApplicationContext(), DualCameraPipIndex);
                } else if (this.mGet.getCameraMode() == HEIGHT_INDEX) {
                    SharedPreferenceUtil.saveFrontDualCameraPIPIndex(this.mGet.getApplicationContext(), DualCameraPipIndex);
                }
            }
        }
        if (FunctionProperties.isFaceDetectionAuto()) {
            this.mGet.stopFaceDetection();
        }
        if (this.mGet.getApplicationMode() == HEIGHT_INDEX) {
            if (isLiveEffectActive()) {
                this.mGet.doCommandUi(Command.HIDE_LIVEEFFECT_SUBMENU_DRAWER);
            } else if (isDualRecordingActive()) {
                this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
            } else if (this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE).equals(CameraConstants.TYPE_RECORDMODE_WDR)) {
                this.mGet.setSetting(Setting.KEY_VIDEO_RECORD_MODE, CameraConstants.TYPE_RECORDMODE_NORMAL);
            } else if (isSmartZoomRecordingActive()) {
            }
        } else if (this.mGet.getApplicationMode() == 0) {
            if (this.mGet.getCameraId() == 0 && Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
                this.mGet.stopFaceDetection();
            } else if (this.mGet.getCameraId() == HEIGHT_INDEX && FunctionProperties.isFaceDetectionAuto()) {
                this.mGet.stopFaceDetection();
            }
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
        }
        if (this.mGet.isChangingToOtherActivity()) {
            if (!this.mGet.isAttachIntent()) {
                CameraHolder.instance().keep();
            }
            try {
                CamLog.d(FaceDetector.TAG, "check closeCamera");
                closeCamera();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.mGet.setChangingToOtherActivity(false);
        } else {
            try {
                if (!(effectsCamcorderActive() || effectsCameraActive())) {
                    if (this.mCameraPreview != null) {
                        this.mCameraPreview.setVisibility(8);
                    }
                    if (this.mCameraGLPreview != null) {
                        this.mCameraGLPreview.setVisibility(8);
                    }
                    if (this.mCameraGLPreviewExtra != null) {
                        this.mCameraGLPreviewExtra.setVisibility(8);
                    }
                    stopPreview();
                    if (this.bRendered) {
                        if (this.mCameraGLPreview != null) {
                            this.mCameraGLPreview.close(this.mCameraDevice);
                        }
                    } else if (this.mCameraPreview != null) {
                        this.mCameraPreview.close(this.mCameraDevice);
                    }
                }
            } catch (NullPointerException e2) {
                CamLog.e(FaceDetector.TAG, "NullPointerException!", e2);
            }
            if (!this.mGet.isConfigurationChanging()) {
                closeCamera();
            }
        }
        if (!(this.mCameraGLPreview == null || getCameraDevice() == null)) {
            this.mCameraGLPreview.releasePreviewCallback(getCameraDevice());
            this.mCameraGLPreview.clearData(false);
            releaseEngine(false);
        }
        this.mGet.swapPreviewEffect(null);
        if (this.mPIPRecordingController != null) {
            this.mPIPRecordingController.unbind();
            this.mPIPRecordingController = null;
        }
        if (this.mPIPCameraController != null) {
            this.mPIPCameraController.unbind();
            this.mPIPCameraController = null;
        }
        if (this.mEffectsRecorder != null) {
            this.mEffectsRecorder.close();
            this.mEffectsRecorder.setEffectsListener(null);
            this.mEffectsRecorder.setOnInfoListener(null);
            this.mEffectsRecorder.setOnErrorListener(null);
            this.mEffectsRecorder = null;
        }
        if (this.mEffectsCamera != null) {
            this.mEffectsCamera.close();
            this.mEffectsCamera.setEffectsListener(null);
            this.mEffectsCamera.setOnInfoListener(null);
            this.mEffectsCamera.setOnErrorListener(null);
            this.mEffectsCamera = null;
        }
        this.mIsStartPreviewEffectOnGoing = false;
        CamLog.d(FaceDetector.TAG, "onPause-end ");
    }

    public void onDestroy() {
        CamLog.d(FaceDetector.TAG, "onDestroy-start");
        if (!this.mChangeMode) {
            SecureImageUtil.get().release();
        }
        this.previousEffectType = 0;
        this.previousEffectParameter = CameraConstants.SMART_MODE_OFF;
        this.previousResolution = null;
        this.mPreviousResolutionBack = null;
        this.mPreviousResolutionFront = null;
        if (this.mCameraGLPreview != null && this.bRendered) {
            this.mCameraGLPreview.onDestroy();
        }
        closeCamera();
        releaseEngine(false);
        this.mSurfaceHolder = null;
        this.mCameraPreview = null;
        this.mCameraGLPreview = null;
        this.mCameraGLPreviewExtra = null;
        this.mStartPreviewThread = null;
        this.mErrorCallback = null;
        super.onDestroy();
        CamLog.d(FaceDetector.TAG, "onDestroy-end");
    }

    public void setBeautyshotProgress(boolean set) {
        this.isBeautyshotProgress = set;
    }

    public boolean getBeautyshotProgress() {
        return this.isBeautyshotProgress;
    }

    public void releaseEngine(boolean checkEngineTag) {
        if (this.mEngineProcessor == null) {
            return;
        }
        if (checkEngineTag) {
            if (!this.mEngineProcessor.checkEngineTag(AutoPanorama.ENGINE_TAG) && !this.mEngineProcessor.checkEngineTag(FaceBeauty.ENGINE_TAG)) {
                this.mEngineProcessor.stop();
                this.mEngineProcessor.releaseAllEngine();
                this.mEngineProcessor.destroy();
                this.mEngineProcessor = null;
            }
        } else if (!this.isBeautyshotProgress) {
            this.mEngineProcessor.stop();
            this.mEngineProcessor.releaseAllEngine();
            this.mEngineProcessor.destroy();
            this.mEngineProcessor = null;
            CamLog.d(FaceDetector.TAG, "release engine complete.");
        }
    }

    public boolean CamDeviceOpen() throws CameraHardwareException {
        boolean ret = false;
        try {
            if (CameraHolder.instance().getUsers() == 0) {
                String str = FaceDetector.TAG;
                Object[] objArr = new Object[HEIGHT_INDEX];
                objArr[0] = Integer.valueOf(this.mGet.getCameraId());
                CamLog.d(str, String.format("CamDeviceOpen call CameraHolder.open(%d)", objArr));
                this.mCameraDevice = CameraHolder.instance().open(this.mGet.getCameraId());
                this.mLGCamera = CameraHolder.instance().getLG();
                ret = true;
                CamLog.d(FaceDetector.TAG, String.format("CamDeviceOpen open routine done", new Object[0]));
            } else {
                CamLog.e(FaceDetector.TAG, String.format("CamDeviceOpen Camera device user != 0 SOMETHING WRONG!!", new Object[0]));
            }
            CamLog.d(FaceDetector.TAG, String.format("CamDeviceOpen Camera device opening done.", new Object[0]));
        } catch (Exception e) {
            e.printStackTrace();
            CamLog.e(FaceDetector.TAG, String.format("CamDeviceOpen latch countDown called", new Object[0]));
        }
        return ret;
    }

    public void waitStartPreviewThreadDone() {
        try {
            if (this.mGet.isErrorOccuredAndFinish()) {
                CamLog.w(FaceDetector.TAG, "We don't wait this case that mErrorOccuredAndFinish is true, " + (this.mStartPreviewThread == null));
                if (this.mStartPreviewThread != null && this.mStartPreviewThread.isAlive()) {
                    this.mStartPreviewThread.interrupt();
                }
                this.mStartPreviewThread = null;
            } else if (this.mStartPreviewThread != null && this.mStartPreviewThread.isAlive()) {
                this.mStartPreviewThread.join();
                this.mStartPreviewThread = null;
            }
        } catch (InterruptedException e) {
            CamLog.e(FaceDetector.TAG, "Failed to join startPreview thread!", e);
        }
    }

    public void show() {
        if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
            this.mGet.showFreePanoramaBlackBg();
        }
        this.mGet.findViewById(R.id.preview).setVisibility(0);
        if (this.bRendered) {
            this.mGet.findViewById(R.id.preview_holder_surface).setVisibility(8);
            this.mGet.findViewById(R.id.preview_render_surface).setVisibility(0);
            this.mGet.findViewById(R.id.preview_render_surface_extra).setVisibility(0);
            return;
        }
        this.mGet.findViewById(R.id.preview_holder_surface).setVisibility(0);
        this.mGet.findViewById(R.id.preview_render_surface).setVisibility(8);
        this.mGet.findViewById(R.id.preview_render_surface_extra).setVisibility(8);
    }

    public void hide() {
        this.mGet.findViewById(R.id.preview).setVisibility(8);
        this.mGet.findViewById(R.id.preview_holder_surface).setVisibility(8);
        this.mGet.findViewById(R.id.preview_render_surface).setVisibility(8);
        this.mGet.findViewById(R.id.preview_render_surface_extra).setVisibility(8);
    }

    public void setBackgroundColorBlack() {
        View initView = this.mGet.findViewById(R.id.light_frame);
        View preview = this.mGet.findViewById(R.id.preview);
        initView.setVisibility(4);
        preview.setBackgroundColor(-16777216);
    }

    public void setBackgroundColorWhite() {
        View initView = this.mGet.findViewById(R.id.light_frame);
        View preview = this.mGet.findViewById(R.id.preview);
        ((ImageView) this.mGet.findViewById(R.id.light_frame_image)).setBackgroundResource(R.drawable.lightframe_bg);
        initView.setVisibility(0);
        preview.setBackgroundColor(0);
    }

    public CameraPreview getCameraPreview() {
        return this.mCameraPreview;
    }

    public OpenGLSurfaceView getCameraGLPreview() {
        return this.mCameraGLPreview;
    }

    public SurfaceView getCameraGLPreviewExtra() {
        return this.mCameraGLPreviewExtra;
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        try {
            int result;
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            int degrees = Util.getDisplayRotation(activity);
            if (info.facing == HEIGHT_INDEX) {
                result = (360 - ((info.orientation + degrees) % CameraConstants.DEGREE_360)) % CameraConstants.DEGREE_360;
            } else {
                result = ((info.orientation - degrees) + CameraConstants.DEGREE_360) % CameraConstants.DEGREE_360;
            }
            CamLog.i(FaceDetector.TAG, "degrees = " + degrees + ", info.orientation = " + info.orientation + ", result = " + result);
            camera.setDisplayOrientation(result);
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "setCameraDisplayOrientation exception : ", e);
        }
    }

    public boolean isCameraReleaseOnGoing() {
        return this.mCameraReleaseOnGoing;
    }

    public void setupHolder(boolean bRender) {
        CamLog.i(FaceDetector.TAG, "setupHolder(): bRender = " + bRender);
        this.bRendered = bRender;
        if (this.mCameraPreview != null && this.mCameraGLPreviewExtra != null && this.mCameraPreview.getHolder() != null && this.mCameraGLPreviewExtra.getHolder() != null) {
            SurfaceHolder holder;
            if (bRender) {
                holder = this.mCameraGLPreviewExtra.getHolder();
            } else {
                holder = this.mCameraPreview.getHolder();
            }
            holder.addCallback(this);
        }
    }

    public boolean beRendered() {
        return this.bRendered;
    }

    public void setRendered(boolean render) {
        this.bRendered = render;
    }

    public void swapPreviewEffect(BaseEngine engine) {
        if (engine != null) {
            CamLog.d(FaceDetector.TAG, "swapPreviewEffect engine.needRenderMode() [" + engine.needRenderMode() + "]");
        }
        if (engine == null) {
            setupHolder(false);
        } else if (engine.needRenderMode()) {
            setupHolder(engine.needRenderMode());
            getEngineProcessor().setEngine(engine);
            CamLog.d(FaceDetector.TAG, "swapPreviewEffect setEngine");
        } else if (!engine.needRenderMode()) {
            setupHolder(engine.needRenderMode());
        }
    }

    public void changePreviewModeOnUiThread(final int width, final int height) {
        if (this.bRendered) {
            stopPreview();
            this.mCameraGLPreview.releasePreviewCallback(this.mCameraDevice);
            this.mCameraGLPreview.clearData(true);
        }
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                PreviewController.this.mGet.removePostRunnable(this);
                PreviewController.this.changePreviewMode(width, height);
            }
        });
    }

    public void changeQuickPreviewMode(int width, int height) {
        int wideMargin;
        LayoutParams surfaceParam;
        View previewLayout = this.mGet.findViewById(R.id.preview);
        if (((double) width) / ((double) height) > CameraConstants.SCREEN_RATIO_STANDARD) {
            width = CameraConstants.smartCoverWideSizeWidth;
            height = CameraConstants.smartCoverSizeHeight;
            wideMargin = (width - CameraConstants.smartCoverSizeWidth) / 2;
        } else {
            width = CameraConstants.smartCoverSizeWidth;
            height = CameraConstants.smartCoverSizeHeight;
            wideMargin = 0;
        }
        LayoutParams previewParam = (LayoutParams) previewLayout.getLayoutParams();
        if (this.bRendered) {
            surfaceParam = (LayoutParams) this.mCameraGLPreview.getLayoutParams();
        } else {
            surfaceParam = (LayoutParams) this.mCameraPreview.getLayoutParams();
        }
        previewParam.width = getPixelFromDimens(R.dimen.layout_preview_height);
        previewParam.height = getPixelFromDimens(R.dimen.layout_preview_width);
        previewParam.topMargin = 0;
        surfaceParam.width = -1;
        surfaceParam.height = -1;
        if (previewParam.width >= height && previewParam.height >= width) {
            CamLog.d(FaceDetector.TAG, String.format("surface not scaled %dx%d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)}));
            surfaceParam.width = height;
            surfaceParam.height = width;
            surfaceParam.topMargin = 0;
        }
        surfaceParam.topMargin = wideMargin * -1;
        surfaceParam.addRule(13, 0);
        surfaceParam.addRule(14, HEIGHT_INDEX);
        setOtherLayoutParam(width, height, 0, previewParam, surfaceParam);
        previewLayout.setLayoutParams(previewParam);
        if (this.bRendered) {
            this.mCameraGLPreview.setLayoutParams(surfaceParam);
            return;
        }
        this.mCameraPreview.setLayoutParams(surfaceParam);
        this.mCameraPreview.setAspectRatio(width, height);
    }

    public void showQuickWindowCamera(boolean enable) {
        if (enable) {
            this.mGet.hideIndicatorController();
            this.mGet.setModeMenuVisibility(4);
            this.mGet.hidePreviewPanelController();
            this.mGet.clearSubMenu();
            this.mGet.setQuickButtonVisible(100, 4, false);
            this.mGet.setModeMenuVisibility(0);
            if (CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
                this.mGet.setSetting(Setting.KEY_FOCUS, this.mGet.findPreference(Setting.KEY_FOCUS).getDefaultValue());
                this.mGet.doCommand(Command.CAMERA_FOCUS_MODE);
                this.mChangedManualFocusToDefault = true;
            }
            if (ProjectVariables.isSupportedAutoReview() && !CameraConstants.SMART_MODE_OFF.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_AUTO_REVIEW))) {
                this.mPrevAutoReviewVal = this.mGet.getSettingValue(Setting.KEY_CAMERA_AUTO_REVIEW);
                this.mGet.setSetting(Setting.KEY_CAMERA_AUTO_REVIEW, CameraConstants.SMART_MODE_OFF);
                this.mGet.doCommand(Command.CAMERA_AUTO_REVIEW);
                this.mChangedAutoReviewToDefault = true;
                return;
            }
            return;
        }
        this.mGet.showIndicatorController();
        this.mGet.setModeMenuVisibility(0);
        this.mGet.updateModeMenuIndicator();
        this.mGet.showPreviewPanelController();
        this.mGet.hideFocus();
        if (Common.isQuickWindowCameraMode()) {
            this.mGet.showQuickMenuEnteringGuide(true);
        }
        this.mGet.setQuickButtonVisible(100, 0, false);
        if (this.mChangedManualFocusToDefault) {
            this.mGet.setSetting(Setting.KEY_FOCUS, CameraConstants.FOCUS_SETTING_VALUE_MANUAL);
            this.mGet.doCommand(Command.CAMERA_FOCUS_MODE);
            this.mChangedManualFocusToDefault = false;
        }
        if (this.mChangedAutoReviewToDefault) {
            this.mGet.setSetting(Setting.KEY_CAMERA_AUTO_REVIEW, this.mPrevAutoReviewVal);
            this.mGet.doCommand(Command.CAMERA_AUTO_REVIEW);
            this.mChangedAutoReviewToDefault = false;
        }
    }

    public void enableQuickWindowButton(boolean visible) {
        CamLog.d(FaceDetector.TAG, "enableQuickWindowButton=" + visible);
        if (this.mExitBtn != null && this.mShutterBtn != null && this.mRotateExitBtn != null && this.mRotateShutterBtn != null) {
            if (visible) {
                this.mExitBtn.setVisibility(0);
                this.mShutterBtn.setVisibility(0);
                this.mRotateExitBtn.setVisibility(0);
                this.mRotateShutterBtn.setVisibility(0);
                return;
            }
            this.mExitBtn.setVisibility(8);
            this.mShutterBtn.setVisibility(8);
            this.mRotateExitBtn.setVisibility(8);
            this.mRotateShutterBtn.setVisibility(8);
        }
    }

    public void changePreviewMode(int width, int height) {
        View previewLayout = this.mGet.findViewById(R.id.preview);
        if (Common.isQuickWindowCameraMode()) {
            showQuickWindowCamera(true);
            changeQuickPreviewMode(width, height);
            return;
        }
        LayoutParams surfaceParam;
        showQuickWindowCamera(false);
        enableQuickWindowButton(false);
        CamLog.d(FaceDetector.TAG, "changePreviewMode previewLayout [" + previewLayout + "]" + width + "x" + height);
        LayoutParams previewParam = (LayoutParams) previewLayout.getLayoutParams();
        if (this.bRendered) {
            surfaceParam = (LayoutParams) this.mCameraGLPreview.getLayoutParams();
        } else {
            surfaceParam = (LayoutParams) this.mCameraPreview.getLayoutParams();
        }
        int previewWidth = getPixelFromDimens(R.dimen.layout_preview_width);
        int previewHeight = getPixelFromDimens(R.dimen.layout_preview_height);
        int lcdWidth = getPixelFromDimens(R.dimen.lcd_width);
        int lcdHeight = getPixelFromDimens(R.dimen.lcd_height);
        int leftMargin = getPixelFromDimens(R.dimen.layout_preview_marginLeft);
        if (this.mGet.isConfigureLandscape()) {
            previewParam.width = previewWidth;
            previewParam.height = previewHeight;
        } else {
            previewParam.width = previewHeight;
            previewParam.height = previewWidth;
        }
        surfaceParam.width = -1;
        surfaceParam.height = -1;
        surfaceParam.addRule(13, HEIGHT_INDEX);
        if (previewWidth < width) {
            this.mGet.setMainBarAlpha(30);
            if (this.mGet.isConfigureLandscape()) {
                previewParam.width = lcdWidth;
                previewParam.height = lcdHeight;
            } else {
                previewParam.width = lcdHeight;
                previewParam.height = lcdWidth;
            }
            previewParam.leftMargin = 0;
            previewParam.topMargin = 0;
            leftMargin = 0;
            if (this.mGet.isConfigureLandscape()) {
                if (previewParam.width < width || previewParam.height < height) {
                    surfaceParam.width = lcdWidth;
                    surfaceParam.height = (lcdWidth * height) / width;
                } else {
                    CamLog.d(FaceDetector.TAG, String.format("surface not scaled %dx%d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)}));
                    surfaceParam.width = width;
                    surfaceParam.height = height;
                }
            } else if (previewParam.width < height || previewParam.height < width) {
                surfaceParam.height = lcdWidth;
                surfaceParam.width = (lcdWidth * height) / width;
            } else {
                CamLog.d(FaceDetector.TAG, String.format("surface not scaled %dx%d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)}));
                surfaceParam.height = width;
                surfaceParam.width = height;
            }
        } else {
            this.mGet.setMainBarAlpha(Ola_ShotParam.AnimalMask_Random);
            if (previewWidth - leftMargin < width) {
                if (this.mGet.isConfigureLandscape()) {
                    previewParam.width = previewWidth;
                    surfaceParam.width = width;
                    surfaceParam.height = height;
                } else {
                    previewParam.height = previewWidth;
                    surfaceParam.height = width;
                    surfaceParam.width = height;
                }
                if (!ModelProperties.isXGAmodel() && !ModelProperties.isUVGAmodel()) {
                    previewParam.leftMargin = 0;
                    previewParam.topMargin = 0;
                    leftMargin = 0;
                } else if (this.mGet.isConfigureLandscape()) {
                    previewParam.leftMargin = leftMargin;
                } else {
                    previewParam.topMargin = leftMargin;
                }
            } else {
                if (this.mGet.isConfigureLandscape()) {
                    previewParam.width = previewWidth - leftMargin;
                    previewParam.leftMargin = leftMargin;
                    previewParam.topMargin = 0;
                } else {
                    previewParam.height = previewWidth - leftMargin;
                    previewParam.leftMargin = 0;
                    previewParam.topMargin = leftMargin;
                }
                if (this.mGet.isConfigureLandscape()) {
                    if (previewParam.width >= width && previewParam.height >= height) {
                        CamLog.d(FaceDetector.TAG, String.format("surface not scaled %dx%d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)}));
                        surfaceParam.width = width;
                        surfaceParam.height = height;
                    }
                } else if (previewParam.width >= height && previewParam.height >= width) {
                    CamLog.d(FaceDetector.TAG, String.format("surface not scaled %dx%d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)}));
                    surfaceParam.width = height;
                    surfaceParam.height = width;
                }
            }
        }
        setOtherLayoutParam(width, height, leftMargin, previewParam, surfaceParam);
        previewLayout.setLayoutParams(previewParam);
        if (this.bRendered) {
            this.mCameraGLPreview.setLayoutParams(surfaceParam);
            if (this.mGet.checkSettingValue(Setting.KEY_LIGHT, CameraConstants.SMART_MODE_ON)) {
                this.mCameraGLPreview.setBackgroundResource(R.drawable.lightframe_img_box);
                return;
            } else {
                this.mCameraGLPreview.setBackgroundColor(0);
                return;
            }
        }
        this.mCameraPreview.setLayoutParams(surfaceParam);
        this.mCameraPreview.setAspectRatio(width, height);
        if (this.mGet.checkSettingValue(Setting.KEY_LIGHT, CameraConstants.SMART_MODE_ON)) {
            this.mCameraPreview.setBackgroundResource(R.drawable.lightframe_img_box);
        } else {
            this.mCameraPreview.setBackgroundColor(0);
        }
    }

    private void setOtherLayoutParam(int width, int height, int leftMargin, LayoutParams previewParam, LayoutParams surfaceParam) {
        if (this.mGet.getApplicationMode() == HEIGHT_INDEX) {
            int recIconLeftMargin = leftMargin;
            if (this.mGet.isConfigureLandscape()) {
                if (previewParam.width >= width) {
                    recIconLeftMargin += (previewParam.width - width) / 2;
                }
            } else if (previewParam.height >= width) {
                recIconLeftMargin += (previewParam.height - width) / 2;
            }
            if (ModelProperties.isUVGAmodel() || ModelProperties.isXGAmodel() || ModelProperties.isUWXGAmodel() || ModelProperties.isHVGAmodel() || ModelProperties.getProjectCode() == 6 || ModelProperties.getProjectCode() == 21) {
                this.mGet.setRecIndicatorLayout(CameraConstants.LCD_SIZE_WIDTH, CameraConstants.LCD_SIZE_HEIGHT, 0);
            } else {
                this.mGet.setRecIndicatorLayout(width, height, recIconLeftMargin);
            }
            if (FunctionProperties.isSupportAudiozoom()) {
                this.mGet.setAudioZoomGuideViewLayout(width, height, leftMargin);
                if (ModelProperties.isUVGAmodel()) {
                    this.mGet.setAudioZoomGuideViewLayout(CameraConstants.LCD_SIZE_WIDTH, CameraConstants.LCD_SIZE_HEIGHT, leftMargin);
                } else {
                    this.mGet.setAudioZoomGuideViewLayout(width, height, leftMargin);
                }
            }
        }
        final int[] previewSizeOnScreen = Util.SizeString2WidthHeight(this.mGet.getPreviewSizeOnScreen());
        if (CameraConstants.FOCUS_SETTING_VALUE_MULTIWINDOWAF.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    PreviewController.this.mGet.removePostRunnable(this);
                    PreviewController.this.mGet.setMultiWindowAFView(previewSizeOnScreen);
                }
            });
        }
        PIPController pip = getPIPController();
        if (pip != null) {
            pip.setSmartZoomLayoutSize(width, height, leftMargin);
        }
        this.mGet.setFocusAreaWindow(width, height, leftMargin);
        if (this.mGet.findViewById(R.id.on_delay_off_ani) != null) {
            this.mGet.findViewById(R.id.on_delay_off_ani).setLayoutParams(surfaceParam);
        }
        this.mGet.setIndicatorLayout(leftMargin);
    }

    public void setChangeMode() {
        this.mChangeMode = true;
    }

    public boolean isChangeMode() {
        return this.mChangeMode;
    }

    public boolean setEnable3ALocks(LGParameters lgParameters, boolean lock) {
        if (!checkMediator() || getCameraDevice() == null || getLG() == null) {
            return false;
        }
        CamLog.d(FaceDetector.TAG, "#### setEnable3ALocks : " + lock);
        if (lgParameters == null) {
            try {
                lgParameters = getLG().getLGParameters();
                if (!(lgParameters == null || lgParameters.getParameters() == null)) {
                    if (lgParameters.getParameters().isAutoExposureLockSupported()) {
                        lgParameters.getParameters().setAutoExposureLock(lock);
                    }
                    if (lgParameters.getParameters().isAutoWhiteBalanceLockSupported()) {
                        lgParameters.getParameters().setAutoWhiteBalanceLock(lock);
                    }
                    lgParameters.setParameters(lgParameters.getParameters());
                    return true;
                }
            } catch (RuntimeException e) {
                CamLog.e(FaceDetector.TAG, "RuntimeException-setEnable3ALocks: ", e);
            }
        } else if (lgParameters.getParameters() == null) {
            return true;
        } else {
            if (lgParameters.getParameters().isAutoExposureLockSupported()) {
                lgParameters.getParameters().setAutoExposureLock(lock);
            }
            if (!lgParameters.getParameters().isAutoWhiteBalanceLockSupported()) {
                return true;
            }
            lgParameters.getParameters().setAutoWhiteBalanceLock(lock);
            return true;
        }
        return false;
    }

    public void checkStartPreviewCallback() {
        Log.i(FaceDetector.TAG, "[Time Info][6] Device StartPreview End : Camera Driver Preview Operation " + Common.interimCheckTime(true) + " ms");
        Log.e(FaceDetector.TAG, "##### TIME_CHECK previewStart onPreviewFrame!!!!!!!");
        if (checkMediator()) {
            if (this.mGet.getApplicationMode() == 0 && this.mGet.getCameraId() == 0) {
                this.mGet.setShutterButtonClicked(false);
            }
            Common.checkEnteringTime(true);
            if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        PreviewController.this.mGet.removePostRunnable(this);
                        if (PreviewController.this.mGet.getCameraDevice() != null && PreviewController.this.mGet.getLGParam() != null) {
                            PreviewController.this.mGet.startFreePanoramaEngine(PreviewController.this.mGet.getParameters());
                            PreviewController.this.mGet.showFreePanoramaView();
                        }
                    }
                });
            } else {
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        PreviewController.this.mGet.removePostRunnable(this);
                        PreviewController.this.mGet.removeFreePanoramaView();
                    }
                });
            }
            if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        PreviewController.this.mGet.removePostRunnable(this);
                        PreviewController.this.mGet.startPlanePanoramaEngine();
                    }
                });
            } else {
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        PreviewController.this.mGet.removePostRunnable(this);
                        PreviewController.this.mGet.removePlanePanoramaView();
                    }
                });
            }
            if (this.mGet.getApplicationMode() == 0 && this.mGet.getCameraId() == HEIGHT_INDEX && !CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
                this.mGet.postOnUiThread(this.mGestureEngineRunable);
            }
            if (this.mGet.getApplicationMode() == 0) {
                CamLog.i(FaceDetector.TAG, "startPreview OneShotPreviewCallback onPreviewFrame [start]");
                setEngineProcessor();
                if (this.mGet.getCameraId() == HEIGHT_INDEX) {
                    this.mCameraGLPreview.setFlipHorizontal(true);
                } else if (CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
                    this.mCameraGLPreview.setFlipHorizontal(false);
                } else {
                    this.mCameraPreview.setFlipHorizontal(false);
                }
                CamLog.i(FaceDetector.TAG, "startPreview OneShotPreviewCallback onPreviewFrame [end]");
            }
            this.mSetPreviewDisplayCheck = false;
            this.mStartPreviewOnGoing = false;
            this.mGet.setInCaptureProgress(false);
            ProjectVariables.bEnterSetting = false;
            CamLog.d(FaceDetector.TAG, "bEnterSetting =  " + ProjectVariables.bEnterSetting);
            if (MultimediaProperties.isLiveEffectSupported() && this.mGet.getApplicationMode() == HEIGHT_INDEX && !this.mGet.isSettingControllerVisible()) {
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        PreviewController.this.mGet.removePostRunnable(this);
                        PreviewController.this.mGet.quickFunctionControllerRefresh(true);
                    }
                });
            }
            if (!(this.mGet.getEnableInput() || this.mGet.getGoingAutoReviewForQuickView() || this.mGet.isDualCameraActive())) {
                this.mGet.enableInput(true);
                if (Common.isQuickWindowCameraMode()) {
                    this.mGet.runOnUiThread(new Runnable() {
                        public void run() {
                            PreviewController.this.mGet.removePostRunnable(this);
                            PreviewController.this.enableQuickWindowButton(true);
                        }
                    });
                }
            }
            if (this.mGet.getApplicationMode() == 0) {
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        PreviewController.this.mGet.removePostRunnable(this);
                        PreviewController.this.mGet.initMultiWindowAFView();
                        String focusMode = PreviewController.this.mGet.getSettingValue(Setting.KEY_FOCUS);
                        if (!(Setting.HELP_FACE_TRACKING_LED.equals(focusMode) || CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(focusMode))) {
                            PreviewController.this.mGet.showFocus();
                            if (CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(focusMode)) {
                                PreviewController.this.mGet.setEnable3ALocks(null, false);
                            }
                        }
                        if (!FunctionProperties.useFaceDetectionFromHal()) {
                            return;
                        }
                        if (Setting.HELP_FACE_TRACKING_LED.equals(focusMode)) {
                            PreviewController.this.mGet.stopFaceDetection();
                            PreviewController.this.mGet.startFaceDetection(true);
                        } else if (FunctionProperties.isFaceDetectionAuto() && !PreviewController.this.mGet.checkFaceDetectionNoUI()) {
                            PreviewController.this.mGet.stopFaceDetection();
                            PreviewController.this.mGet.startFaceDetection(false);
                        }
                    }
                });
                try {
                    if (this.mCameraDevice != null) {
                        Parameters parameter = this.mCameraDevice.getParameters();
                        if (parameter != null) {
                            this.mGet.setFocalLength(parameter.getFocalLength());
                        }
                    }
                } catch (RuntimeException e) {
                    CamLog.e(FaceDetector.TAG, "RuntimeException : ", e);
                }
            } else {
                this.mGet.unregisterObjectCallback();
            }
            if (this.mGet.checkPreviewPanelController() && this.mGet.snapshotOnIdle() && this.mGet.getHandler() != null) {
                this.mGet.getHandler().post(this.mGet.getSnapshotRunnable());
                return;
            }
            return;
        }
        CamLog.d(FaceDetector.TAG, "exit checkStartPreviewCallback by (!checkMediator())");
    }

    public void stopPreviewForMediatorOnPause() {
        if (getCameraGLPreview() != null) {
            getCameraGLPreview().setActivityPausing(true);
        }
        if (!this.mGet.isChangingToOtherActivity()) {
            waitStartPreviewThreadDone();
            if (isPreviewing()) {
                stopPreview();
            }
        }
    }

    public void cameraErrorCallbackForServerDied() {
        CamLog.e(FaceDetector.TAG, "media server died");
        if (this.mGet == null || this.mGet.getApplicationMode() != 0) {
            CamLog.d(FaceDetector.TAG, "mediator or Controller is null");
        } else if (this.mGet.checkFsWritable() || this.mCheckCountOf_CAMERA_ERROR_SERVER_DIED < 3) {
            CamLog.e(FaceDetector.TAG, String.format("Try restart preview", new Object[0]));
            this.mCheckCountOf_CAMERA_ERROR_SERVER_DIED += HEIGHT_INDEX;
            stopPreview();
            closeCamera();
            hide();
            show();
            try {
                CamLog.e(FaceDetector.TAG, "wait 5 second for restart MediaServer");
                Thread.sleep(CameraConstants.TOAST_LENGTH_LONG);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            startPreview(null, false);
        } else {
            CamLog.e(FaceDetector.TAG, String.format("Storage is not available. Or media server never wake up. finish app.", new Object[0]));
            if (!this.mGet.getShowCameraErrorPopup()) {
                this.mGet.setShowCameraErrorPopup(true);
                this.mGet.showCameraStoppedAndFinish();
            }
        }
    }

    public void cameraErrorCallbackForUnKnown() {
        CamLog.e(FaceDetector.TAG, "Camera Driver Error, bShowCameraErrorPopup = " + this.mGet.getShowCameraErrorPopup());
        if (!this.mGet.getShowCameraErrorPopup()) {
            this.mGet.setShowCameraErrorPopup(true);
            this.mGet.showCameraStoppedAndFinish();
        }
    }

    public boolean checkFaceDetectionNoUI() {
        if (!this.mFaceDetectionStarted || this.mFaceDetectionHasUI) {
            return false;
        }
        return true;
    }

    public void startFaceDetectionFromHal(boolean bHasUI) {
        if (!this.mFaceDetectionStarted) {
            if ((!CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)) && !CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)) && !CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) || bHasUI) {
                this.mFaceDetectionHasUI = bHasUI;
                if (this.mCameraDevice != null && this.mCameraDevice.getParameters() != null) {
                    CamLog.d(FaceDetector.TAG, "[HAL FACE] getMaxNumDetectedFaces : " + this.mCameraDevice.getParameters().getMaxNumDetectedFaces());
                    if (this.mCameraDevice.getParameters().getMaxNumDetectedFaces() > 0) {
                        if (this.mGet.getCameraMode() == 0 && bHasUI) {
                            this.mCameraDevice.setFaceDetectionListener(new FaceDetectionListener() {
                                public void onFaceDetection(Face[] faces, Camera camera) {
                                    PreviewController.this.mGet.onFaceDetectionFromHal(faces);
                                }
                            });
                        } else {
                            this.mCameraDevice.setFaceDetectionListener(null);
                        }
                        CamLog.d(FaceDetector.TAG, "FaceDetection Start UI = " + bHasUI);
                        this.mCameraDevice.startFaceDetection();
                        this.mFaceDetectionStarted = true;
                    }
                }
            }
        }
    }

    public void stopFaceDetectionFromHal() {
        if (this.mCameraDevice != null && this.mCameraDevice.getParameters() != null && this.mCameraDevice.getParameters().getMaxNumDetectedFaces() > 0 && this.mFaceDetectionStarted) {
            CamLog.d(FaceDetector.TAG, "FaceDetection Stop UI  = " + this.mFaceDetectionHasUI);
            this.mFaceDetectionStarted = false;
            this.mCameraDevice.setFaceDetectionListener(null);
            this.mCameraDevice.stopFaceDetection();
        }
    }

    public View findViewById(int resId) {
        return this.mGet.findViewById(resId);
    }

    public View inflateStub(int resId) {
        return this.mGet.inflateStub(resId);
    }

    public void doCommandDelayed(String msg, long delay) {
        this.mGet.doCommandDelayed(msg, delay);
    }

    public Context getApplicationContext() {
        return this.mGet.getApplicationContext();
    }

    public void onLearningDoneProcess() {
        if (this.mGet.getApplicationMode() != HEIGHT_INDEX) {
            this.mEffectsCamera.sendMessage(6, HEIGHT_INDEX);
        } else if (this.mGet.isDualRecordingActive()) {
            this.mEffectsRecorder.sendMessage(5, HEIGHT_INDEX);
        } else if (this.mGet.isSmartZoomRecordingActive()) {
            this.mEffectsRecorder.sendMessage(7, HEIGHT_INDEX);
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    PreviewController.this.mGet.removePostRunnable(this);
                    if (PreviewController.this.mGet.getCurrentPIPMask() != 0) {
                        PreviewController.this.mGet.showSmartZoomFocusView();
                        PreviewController.this.mGet.initSmartZoomFocusView();
                    }
                }
            });
        }
    }

    public void onSetBitmapDoneProcess() {
        getPIPController().setBitmapDone(true);
    }

    public void setQuickFunctionAllMenuEnabled(boolean enabled, boolean dimByEnable) {
        this.mGet.setQuickFunctionAllMenuEnabled(enabled, dimByEnable);
    }

    public void quickFunctionControllerRefresh(boolean enabled) {
        this.mGet.quickFunctionControllerRefresh(enabled);
    }

    public void setQuickButtonMenuEnable(boolean enable, boolean dimByEnable) {
        this.mGet.setQuickButtonMenuEnable(enable, dimByEnable);
    }

    public void setButtonRemainRefresh() {
        this.mGet.setButtonRemainRefresh();
    }

    public void setQuickButtonForcedDisable(boolean set) {
        this.mGet.setQuickButtonForcedDisable(set);
    }

    public int getVideoState() {
        return this.mGet.getVideoState();
    }

    public void setMainButtonVisible(boolean visible) {
        this.mGet.setMainButtonVisible(visible);
    }

    public void setSwitcherVisible(boolean visible) {
        this.mGet.setSwitcherVisible(visible);
    }

    public void setThumbnailButtonVisibility(int visible) {
        this.mGet.setThumbnailButtonVisibility(visible);
    }

    public Module getCurrentModule() {
        return this.mGet.getCurrentModule();
    }

    public boolean isCamcorderRotation(boolean checkWithPause) {
        return this.mGet.isCamcorderRotation(checkWithPause);
    }

    public boolean isCameraDeviceClosed() {
        return this.mCameraDevice == null;
    }

    public void jpegCallbackOnDualCameraPictureTaken(byte[] previewFrameJpeg) {
        final byte[] frameJpeg = previewFrameJpeg;
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                PreviewController.this.mGet.removePostRunnable(this);
                if (!PreviewController.this.mGet.isPausing() && PreviewController.this.mGet.getCurrentModule() != null) {
                    PreviewController.this.mGet.getCurrentModule().jpegCallbackOnDualCameraPictureTaken(frameJpeg, null);
                }
            }
        });
    }

    public boolean isConfigureLandscape() {
        return this.mGet.isConfigureLandscape();
    }

    public Resources getResources() {
        return this.mGet.getResources();
    }

    public Activity getActivity() {
        return this.mGet.getActivity();
    }

    public int getFocusAreaHeight() {
        return this.mGet.getFocusAreaHeight();
    }

    public void restoreSubWindow() {
        PIPController pip = getPIPController();
        if (pip != null) {
            pip.restoreSubWindow();
        }
    }

    public void setEffectRecorderPausing(boolean set) {
        if (this.mEffectsRecorder != null) {
            this.mEffectsRecorder.isOnPausing(set);
        }
    }

    public boolean getEffectRecorderPausing() {
        if (this.mEffectsRecorder != null) {
            return this.mEffectsRecorder.getIsOnPausing();
        }
        return false;
    }

    public void startPreviewEffect() {
    }

    public void startPreviewEffect(EffectsBase mEffects, boolean checkCondition) {
        if (!this.mGet.isPausing() && !this.mGet.isFinishingActivity() && mEffects != null && this.mCameraDevice != null) {
            if (this.mIsStartPreviewEffectOnGoing) {
                CamLog.d(FaceDetector.TAG, "startPreviewEffect is OnGoing; Do nothing");
            } else if (checkCondition) {
                this.mIsStartPreviewEffectOnGoing = true;
                this.mEffectType = readEffectType();
                this.mEffectParameter = readEffectParameter();
                onSetBitmapDoneProcess();
                this.mGet.enableInput(false);
                CamLog.d(FaceDetector.TAG, "enableInput(false) in startPreviewEffect");
                CamLog.d(FaceDetector.TAG, "startPreviewEffect() mEffectType:" + this.mEffectType);
                initializeEffectsPreview();
                mEffects.startPreview();
            }
        }
    }

    public void stopPreviewEffect() {
    }

    public void stopPreviewEffect(EffectsBase mEffects) {
        if (mEffects != null) {
            mEffects.stopPreview(100);
            CamLog.d(FaceDetector.TAG, "stopPreviewEffect");
        }
    }

    protected void makeEffectList() {
    }

    public ArrayList<String> getLiveEffectList() {
        return this.mEffectList;
    }

    public void setLiveEffect(String effect) {
    }

    public String getLiveEffect() {
        return this.mCurrentEffect;
    }

    protected void initPIPSubWindow(String videoSize) {
    }

    public boolean updateEffectSelection() {
        return true;
    }

    protected int readEffectType() {
        return 0;
    }

    protected Object readEffectParameter() {
        return null;
    }

    public boolean effectsCamcorderActive() {
        return false;
    }

    public boolean effectsCameraActive() {
        return false;
    }

    public boolean isDualRecordingActive() {
        return false;
    }

    public boolean isDualCameraActive() {
        return false;
    }

    public boolean isSmartZoomRecordingActive() {
        return false;
    }

    public boolean isLiveEffectActive() {
        return false;
    }

    public void showSubWindowResizeHandler(float x, float y) {
    }

    public void hideSubWindowResizeHandler() {
    }

    public void showSmartZoomFocusView() {
    }

    public void hideSmartZoomFocusView() {
    }

    public void setSmartZoomFocusViewPosition(int x, int y) {
    }

    public void initSmartZoomFocusView() {
    }

    public int getOrientationDegree() {
        return this.mGet.getOrientationDegree();
    }

    public PIPRecordingController getPIPRecordingController() {
        return this.mPIPRecordingController;
    }

    public PIPCameraController getPIPCameraController() {
        return this.mPIPCameraController;
    }

    public PIPController getPIPController() {
        if (this.mGet.getApplicationMode() == HEIGHT_INDEX) {
            return this.mPIPRecordingController;
        }
        return this.mPIPCameraController;
    }

    public void initializeRecordingEffect(String filepath_name, long mMaxFileSize, int mMaxDurationTime, long freeSpace) {
    }

    protected void initializeEffectsPreview() {
    }

    public void startRecordingEffect() {
    }

    public void pauseAndResumeRecording(boolean pause) {
    }

    public void stopRecordingEffect() {
    }

    public void waitStopRecordingEffectThreadDone() {
    }

    protected void checkQualityAndStartPreview() {
    }

    public int getPreviousEffectType() {
        return 0;
    }

    public String getPreviousResolution() {
        return this.previousResolution;
    }

    public void setPreviousResolution(String resolution) {
    }

    public void storePreviousResolution(String resolution) {
    }

    public void setPrevResolutionWithStoredValue() {
    }

    protected void doOnEffectsUpdate(int effectId, int effectMsg) {
    }

    protected void doOnEffectesError(Exception exception, String fileName) {
    }

    public void startPIPFrameSubMenuRotation(int degree) {
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
    }
}
