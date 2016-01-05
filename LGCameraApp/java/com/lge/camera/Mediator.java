package com.lge.camera;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.Parameters;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.lge.camera.CheckTemperature.CheckTemperatureFunction;
import com.lge.camera.LocationInfo.LocationInfoFunction;
import com.lge.camera.OrientationInfo.OrientationRotateFunction;
import com.lge.camera.command.Command;
import com.lge.camera.command.CommandManager;
import com.lge.camera.components.CameraPreview;
import com.lge.camera.components.MultiDirectionSlidingDrawer;
import com.lge.camera.components.OpenGLSurfaceView;
import com.lge.camera.components.RotateImageButton;
import com.lge.camera.components.RotateImageView;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.components.ShutterButton;
import com.lge.camera.components.Switcher;
import com.lge.camera.components.SwitcherLever;
import com.lge.camera.components.SwitcherLeverHorizon;
import com.lge.camera.components.SwitcherLeverVertical;
import com.lge.camera.controller.BeautyshotController;
import com.lge.camera.controller.BrightnessController;
import com.lge.camera.controller.BrightnessControllerExpand;
import com.lge.camera.controller.BubblePopupController;
import com.lge.camera.controller.Camera3dDepthController;
import com.lge.camera.controller.Controller;
import com.lge.camera.controller.DialogController;
import com.lge.camera.controller.EnteringViewController;
import com.lge.camera.controller.FocusController;
import com.lge.camera.controller.GestureShutterController;
import com.lge.camera.controller.IndicatorController;
import com.lge.camera.controller.ManualFocusController;
import com.lge.camera.controller.OptionMenuController;
import com.lge.camera.controller.PIPController;
import com.lge.camera.controller.PreviewController;
import com.lge.camera.controller.PreviewPanel2DController;
import com.lge.camera.controller.PreviewPanel3DController;
import com.lge.camera.controller.PreviewPanelController;
import com.lge.camera.controller.QuickButtonController;
import com.lge.camera.controller.QuickFunctionController;
import com.lge.camera.controller.QuickFunctionDragController;
import com.lge.camera.controller.QuickFunctionSettingController;
import com.lge.camera.controller.SettingController;
import com.lge.camera.controller.SettingRotatableExpandableController;
import com.lge.camera.controller.ShotModeMenuController;
import com.lge.camera.controller.SoundController;
import com.lge.camera.controller.StorageController;
import com.lge.camera.controller.ToastController;
import com.lge.camera.controller.ZoomController;
import com.lge.camera.listeners.AudioRecogEngineCallback;
import com.lge.camera.listeners.AudioRecogEngineCallback.AudioCallbackFunction;
import com.lge.camera.listeners.LocationListener;
import com.lge.camera.listeners.LocationListener.LocationListenerFunction;
import com.lge.camera.module.Module;
import com.lge.camera.module.ModuleFactory;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.receiver.BroadCastReceiverDefine;
import com.lge.camera.receiver.CameraBroadCastReceiver.ReceiverMediatorBridge;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.PreferenceGroup;
import com.lge.camera.setting.Setting;
import com.lge.camera.setting.SettingMenu;
import com.lge.camera.setting.SettingMenuItem;
import com.lge.camera.util.AppControlUtil;
import com.lge.camera.util.AudioUtil;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.DialogCreater;
import com.lge.camera.util.FileNamer;
import com.lge.camera.util.ImageHandler;
import com.lge.camera.util.ImageManager;
import com.lge.camera.util.ImageRotationOff;
import com.lge.camera.util.ImageRotationOn;
import com.lge.camera.util.MainHandler;
import com.lge.camera.util.MainHandler.HandlerFunction;
import com.lge.camera.util.SecureImageUtil;
import com.lge.camera.util.Util;
import com.lge.hardware.LGCamera;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Limit;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.BaseEngine;
import com.lge.olaworks.library.EngineProcessor;
import com.lge.olaworks.library.FaceBeauty;
import com.lge.olaworks.library.FaceDetector;
import com.lge.systemservice.core.LGContext;
import com.lge.systemservice.core.LGPowerManagerHelper;
import com.lge.voiceshutter.library.AudioRecogEngine;
import com.lge.voiceshutter.library.LGKeyRec;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public abstract class Mediator implements ControllerFunction, HandlerFunction, AudioCallbackFunction, LocationListenerFunction, ReceiverMediatorBridge, OrientationRotateFunction, LocationInfoFunction, CheckTemperatureFunction {
    private boolean bShowCameraErrorPopup;
    protected ImageHandler imageHandler;
    protected ImageHandler imageHandlerRotationOff;
    private boolean isCameraKeyLongPressed;
    private boolean isEventIgnoreDuringSaving;
    private boolean isNeedProgressDuringCapture;
    protected ActivityBridge mActivity;
    private int mActualBatteryLevel;
    protected int mApplicationMode;
    private AudioRecogEngine mAudioRecogEngine;
    private PreferenceGroup mBack3dPreferenceGroup;
    private PreferenceGroup mBackPreferenceGroup;
    private int mBatteryLevel;
    protected BeautyshotController mBeautyshotController;
    private boolean mBlockTouchByCallPopUp;
    protected BrightnessController mBrightnessController;
    protected BrightnessControllerExpand mBrightnessControllerExpand;
    protected BroadCastReceiverDefine mBroadCastReceiver;
    protected Camera3dDepthController mCamera3dDepthController;
    private int mCameraId;
    protected int mCameraMode;
    protected byte[] mCaptureData;
    private boolean mChangingToOtherActivity;
    public boolean mCharging;
    protected CheckTemperature mCheckTemperature;
    protected CommandManager mCommandManager;
    private boolean mControllerInitialized;
    public ArrayList<Controller> mControllers;
    private long mCurrentRecordingTime;
    protected DialogController mDialogController;
    protected boolean mDidRegister;
    private boolean mErrorOccuredAndFinish;
    private FaceBeauty mFaceBeauty;
    protected FocusController mFocusController;
    protected int mFocusMode;
    private PreferenceGroup mFrontPreferenceGroup;
    protected GestureShutterController mGestureShutterController;
    private boolean mGoingAutoQuickReview;
    protected MainHandler mHandler;
    protected int mImageRotationDegree;
    private boolean mInCaptureProgress;
    protected IndicatorController mIndicatorController;
    private boolean mIsBackKeyPressed;
    public boolean mIsClearView;
    private boolean mIsClearViewBeforeRecording;
    private boolean mIsEnableInput;
    private boolean mIsFlashOffByHighTemperature;
    private boolean mIsIAFlashOn;
    private boolean mIsSendBroadcastIntent;
    private boolean mIsSwapCameraProcessing;
    protected LocationInfo mLocationInfo;
    protected int mMainCameraDimension;
    protected ManualFocusController mManualFocusController;
    protected Module mModule;
    public ModuleFactory mModuleFactory;
    private boolean mOpenLBSSetting;
    protected OptionMenuController mOptionMenuController;
    protected OrientationInfo mOrientationInfo;
    private int mParameteredRotation;
    protected boolean mPausing;
    private Object mPostRunnableLock;
    private ArrayList<Runnable> mPostRunnables;
    protected PreviewController mPreviewController;
    protected PreviewPanelController mPreviewPanelController;
    protected QuickButtonController mQuickButtonController;
    protected QuickFunctionController mQuickFunctionController;
    protected QuickFunctionDragController mQuickFunctionDragController;
    protected QuickFunctionSettingController mQuickFunctionSettingController;
    private Runnable mRefreshMenuRunnable;
    private boolean mRotateBatteryIndicatorwithHint;
    protected Uri mSaveUri;
    protected String mSavedFileName;
    protected Uri mSavedImageUri;
    protected Uri mSavedVideoUri;
    private boolean mSetCameraMode;
    private boolean mSetTimeMachineComplete;
    protected SettingController mSettingController;
    protected ShotModeMenuController mShotModeMenuController;
    protected SoundController mSoundController;
    protected int mStatus;
    protected StorageController mStorageController;
    protected int mSubMenuMode;
    private Toast mToast;
    protected ToastController mToastController;
    protected Thread mUiThread;
    private int mVideoState;
    protected ZoomController mZoomController;
    private String strLanguage;

    public interface ActivityBridge {
        CameraActivity getActivity();

        String getPostviewRename();

        int getPostviewRequestCode();

        Uri getPostviewUri();

        void setPostviewRequestInitCode();
    }

    public abstract void doAttach();

    public abstract void doTouchbyAF(int i, int i2);

    public abstract Bitmap getLastThumbnail(Uri uri);

    public abstract void hideOsd();

    public abstract boolean isAttachIntent();

    public abstract boolean isAttachMode();

    public abstract boolean isMMSIntent();

    public abstract boolean isPlayRingMode();

    public abstract boolean postviewRequestInit();

    public abstract void restoreAutoReviewValue();

    public abstract void setPreviousAutoReviewValue();

    public abstract void showControllerForHideSettingMenu(boolean z, boolean z2);

    public abstract void showOsd();

    public abstract void switchCameraId(int i);

    public SoundController getSoundController() {
        return this.mSoundController;
    }

    public PreviewController getPreviewController() {
        return this.mPreviewController;
    }

    public PreviewPanelController getPreviewPanelController() {
        return this.mPreviewPanelController;
    }

    public QuickFunctionController getQuickFunctionController() {
        return this.mQuickFunctionController;
    }

    public QuickFunctionDragController getQuickFunctionDragController() {
        return this.mQuickFunctionDragController;
    }

    public QuickFunctionSettingController getQuickFunctionSettingController() {
        return this.mQuickFunctionSettingController;
    }

    public IndicatorController getIndicatorController() {
        return this.mIndicatorController;
    }

    public BrightnessController getBrightnessController() {
        return this.mBrightnessController;
    }

    public BrightnessControllerExpand getBrightnessControllerExpand() {
        return this.mBrightnessControllerExpand;
    }

    public BeautyshotController getBeautyshotController() {
        return this.mBeautyshotController;
    }

    public Camera3dDepthController getCamera3dDepthController() {
        return this.mCamera3dDepthController;
    }

    public SettingController getSettingController() {
        return this.mSettingController;
    }

    public ZoomController getZoomController() {
        return this.mZoomController;
    }

    public DialogController getDialogController() {
        return this.mDialogController;
    }

    public FocusController getFocusController() {
        return this.mFocusController;
    }

    public StorageController getStorageController() {
        return this.mStorageController;
    }

    public ToastController getToastController() {
        return this.mToastController;
    }

    public ManualFocusController getManualFocusController() {
        return this.mManualFocusController;
    }

    public QuickButtonController getQuickButtonController() {
        return this.mQuickButtonController;
    }

    public OptionMenuController getOptionMenuController() {
        return this.mOptionMenuController;
    }

    public ShotModeMenuController getShotModeMenuController() {
        return this.mShotModeMenuController;
    }

    public GestureShutterController getGestureShutterController() {
        return this.mGestureShutterController;
    }

    public Mediator(ActivityBridge activity) {
        this.mModuleFactory = null;
        this.mControllers = new ArrayList();
        this.mModule = null;
        this.mSaveUri = null;
        this.mCaptureData = null;
        this.mVideoState = 0;
        this.mApplicationMode = 0;
        this.mCameraMode = 0;
        this.mMainCameraDimension = 0;
        this.mStatus = 0;
        this.mBroadCastReceiver = null;
        this.mDidRegister = false;
        this.mOrientationInfo = new OrientationInfo(this);
        this.mLocationInfo = new LocationInfo(this);
        this.mRotateBatteryIndicatorwithHint = false;
        this.mSetCameraMode = false;
        this.mGoingAutoQuickReview = false;
        this.imageHandler = new ImageRotationOn();
        this.imageHandlerRotationOff = new ImageRotationOff();
        this.mIsEnableInput = true;
        this.mCommandManager = new CommandManager(this);
        this.mHandler = new MainHandler(this);
        this.mCheckTemperature = new CheckTemperature(this);
        this.mControllerInitialized = false;
        this.mIsSwapCameraProcessing = false;
        this.mErrorOccuredAndFinish = false;
        this.mUiThread = null;
        this.mCharging = false;
        this.mPostRunnables = new ArrayList();
        this.mPostRunnableLock = new Object();
        this.mOpenLBSSetting = false;
        this.mAudioRecogEngine = null;
        this.mActivity = null;
        this.mBlockTouchByCallPopUp = false;
        this.mIsSendBroadcastIntent = true;
        this.mIsIAFlashOn = false;
        this.isEventIgnoreDuringSaving = false;
        this.mCurrentRecordingTime = 0;
        this.mSetTimeMachineComplete = false;
        this.isCameraKeyLongPressed = false;
        this.isNeedProgressDuringCapture = false;
        this.mToast = null;
        this.mIsBackKeyPressed = false;
        this.mFrontPreferenceGroup = null;
        this.mBackPreferenceGroup = null;
        this.mBack3dPreferenceGroup = null;
        this.mIsClearView = false;
        this.mIsClearViewBeforeRecording = false;
        this.mChangingToOtherActivity = false;
        this.strLanguage = "en";
        this.mRefreshMenuRunnable = new Runnable() {
            public void run() {
                if (!Mediator.this.isPausing() && Mediator.this.mQuickFunctionController != null && Mediator.this.mQuickButtonController != null && Mediator.this.getPlanePanoramaStatus() <= 1 && Mediator.this.getFreePanoramaStatus() <= 1 && !Mediator.this.isTimerShotCountdown()) {
                    if (!Mediator.this.getInCaptureProgress() || !Mediator.this.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT)) {
                        Mediator.this.mQuickFunctionController.quickFunctionControllerRefresh(!CameraConstants.SMART_MODE_ON.equals(Mediator.this.getSettingValue(Setting.KEY_SMART_MODE)));
                        if (Mediator.this.isPIPFrameDrawerOpened()) {
                            Mediator.this.mQuickButtonController.refreshQuickButton();
                        } else {
                            Mediator.this.mQuickButtonController.setAllMenuEnable(true, true);
                        }
                    }
                }
            }
        };
        this.bShowCameraErrorPopup = false;
        this.mFaceBeauty = new FaceBeauty();
        this.mIsFlashOffByHighTemperature = false;
        this.mActivity = activity;
        this.mModuleFactory = new ModuleFactory(CameraConstants.MODULE_PACKAGE_NAME, this);
        this.mUiThread = Thread.currentThread();
    }

    public boolean checkActivity() {
        return this.mActivity != null;
    }

    public Mediator getMediator() {
        return this;
    }

    public CameraActivity getActivity() {
        return this.mActivity.getActivity();
    }

    public boolean isFinishingActivity() {
        return getActivity().isFinishing();
    }

    public void activityFinish() {
        try {
            getActivity().finish();
        } catch (Exception e) {
            CamLog.d(FaceDetector.TAG, "Exception:", e);
        }
    }

    public ContentResolver getContentResolver() {
        return getActivity().getContentResolver();
    }

    public View findViewById(int id) {
        return getActivity().findViewById(id);
    }

    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        getActivity().startActivityForResult(intent, requestCode);
    }

    public void gotoHelpActivity(String keyString) {
        getActivity().gotoHelpActivity(keyString);
    }

    public View inflateView(int resource) {
        return ((LayoutInflater) getActivity().getSystemService("layout_inflater")).inflate(resource, null);
    }

    public View inflateStub(int id) {
        ViewStub viewStub = (ViewStub) findViewById(id);
        if (viewStub != null) {
            View view = viewStub.inflate();
            CamLog.i(FaceDetector.TAG, "inflated view: " + view);
            return view;
        }
        CamLog.i(FaceDetector.TAG, "inflated stubView is null.");
        return null;
    }

    public Resources getResources() {
        return getActivity().getResources();
    }

    public String getString(int resId) {
        return getActivity().getString(resId);
    }

    public float getDimension(int resId) {
        return getActivity().getResources().getDimension(resId);
    }

    public Drawable getDrawable(int resId) {
        return getActivity().getResources().getDrawable(resId);
    }

    public Locale getLocale() {
        return getActivity().getResources().getConfiguration().locale;
    }

    public void setResultCancelForAttachMode() {
        Activity activity = getActivity();
        if (isAttachMode() && activity != null) {
            activity.setResult(0);
        }
    }

    public void inflateMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.camera_preview_menu, menu);
    }

    public boolean isConfigurationChanging() {
        return getActivity().isConfigurationChanging();
    }

    public void setLockChangeConfiguration(boolean lock) {
    }

    public boolean setVisible(int resId, boolean visible) {
        int i = 0;
        View v = findViewById(resId);
        if (v != null) {
            if (!visible) {
                i = 4;
            }
            v.setVisibility(i);
            return true;
        }
        CamLog.d(FaceDetector.TAG, "View not found in setVisible resId: " + resId);
        return false;
    }

    public CommandManager getCommandManager() {
        return this.mCommandManager;
    }

    public void doCommandNoneParameter(String msg) {
        this.mCommandManager.doCommandNoneParameter(msg);
    }

    public void doCommandNoneParameter(String msg, Object arg1) {
        this.mCommandManager.doCommandNoneParameter(msg, arg1);
    }

    public void doCommand(String msg) {
        this.mCommandManager.doCommand(msg);
    }

    public void doCommandUi(String msg) {
        this.mCommandManager.doCommandUi(msg);
    }

    public void doCommand(String msg, Object arg1) {
        this.mCommandManager.doCommand(msg, arg1);
    }

    public void doCommand(String msg, Object arg1, Object arg2) {
        this.mCommandManager.doCommand(msg, arg1, arg2);
    }

    public void doCommandUi(String msg, Object arg1) {
        this.mCommandManager.doCommandUi(msg, arg1);
    }

    public void doCommandUi(String msg, Object arg1, Object arg2) {
        this.mCommandManager.doCommandUi(msg, arg1, arg2);
    }

    public void postOnUiThread(Runnable action) {
        if (this.mHandler != null) {
            synchronized (this.mPostRunnableLock) {
                this.mPostRunnables.add(action);
            }
            getHandler().post(action);
        }
    }

    public void postOnUiThread(Runnable action, long delay) {
        if (this.mHandler != null) {
            synchronized (this.mPostRunnableLock) {
                this.mPostRunnables.add(action);
            }
            getHandler().postDelayed(action, delay);
        }
    }

    public void runOnUiThread(Runnable action) {
        if (this.mUiThread == null) {
            return;
        }
        if (Thread.currentThread() != this.mUiThread) {
            postOnUiThread(action);
        } else {
            action.run();
        }
    }

    public void doCommandDelayed(String msg, long delay) {
        doCommandWithFixedRate(msg, delay, 0);
    }

    public void doCommandDelayed(String msg, Object obj, long delay) {
        doCommandWithFixedRate(msg, obj, delay, 0);
    }

    public void doCommandWithFixedRate(String msg, long delay, long period) {
        this.mCommandManager.doCommandWithFixedRate(msg, delay, period);
    }

    public void doCommandWithFixedRate(String msg, Object obj, long delay, long period) {
        this.mCommandManager.doCommandWithFixedRate(msg, obj, delay, period);
    }

    public void removeScheduledCommand(String msg) {
        this.mCommandManager.removeScheduledCommand(msg);
    }

    public void removeScheduledAllCommand() {
        this.mCommandManager.removeScheduledAllCommand();
    }

    public void removePostRunnable(Object object) {
        if (this.mPostRunnables == null) {
            CamLog.d(FaceDetector.TAG, "mPostRunnables is null");
        } else if (this.mPostRunnables.size() > 0) {
            synchronized (this.mPostRunnableLock) {
                int index = this.mPostRunnables.indexOf(object);
                if (index >= 0) {
                    removeCallbacks((Runnable) this.mPostRunnables.get(index));
                    this.mPostRunnables.remove(index);
                }
            }
        }
    }

    public void removePostAllRunnables() {
        if (this.mPostRunnables == null) {
            CamLog.d(FaceDetector.TAG, "mPostRunnables is null");
            return;
        }
        Iterator i$ = this.mPostRunnables.iterator();
        while (i$.hasNext()) {
            removeCallbacks((Runnable) i$.next());
        }
        synchronized (this.mPostRunnableLock) {
            this.mPostRunnables.clear();
        }
    }

    public void removeCallbacks(Runnable r) {
        if (this.mHandler != null) {
            this.mHandler.removeCallbacks(r);
        }
    }

    public boolean findScheduledCommand(String msg) {
        return this.mCommandManager.findScheduledCommand(msg);
    }

    public MainHandler getHandler() {
        return this.mHandler;
    }

    public PreferenceGroup getPreferenceGroup() {
        if (getCameraMode() != 0) {
            return this.mFrontPreferenceGroup;
        }
        if (getCameraDimension() == 0) {
            return this.mBackPreferenceGroup;
        }
        return this.mBack3dPreferenceGroup;
    }

    public PreferenceGroup getFrontPreferenceGroup() {
        return this.mFrontPreferenceGroup;
    }

    public PreferenceGroup getBackPreferenceGroup() {
        return this.mBackPreferenceGroup;
    }

    public PreferenceGroup getBack3dPreferenceGroup() {
        return this.mBack3dPreferenceGroup;
    }

    public void setFrontPreference(PreferenceGroup pref) {
        this.mFrontPreferenceGroup = pref;
    }

    public void setBackPreference(PreferenceGroup pref) {
        this.mBackPreferenceGroup = pref;
    }

    public void setBack3dPreference(PreferenceGroup pref) {
        this.mBack3dPreferenceGroup = pref;
    }

    public ListPreference findPreference(String key) {
        return getPreferenceGroup().findPreference(key);
    }

    public ListPreference getListPreference(int keyIndex) {
        return getPreferenceGroup().getListPreference(keyIndex);
    }

    public int findPreferenceIndex(String key) {
        return getPreferenceGroup().findPreferenceIndex(key);
    }

    public void setStatus(int state) {
        this.mStatus = state;
    }

    public void setIsCharging(boolean bState) {
        this.mCharging = bState;
    }

    public boolean getIsCharging() {
        return this.mCharging;
    }

    public int getStatus() {
        return this.mStatus;
    }

    protected void createControllers() {
        if (ModelProperties.is3dSupportedModel()) {
            this.mPreviewPanelController = new PreviewPanel3DController(this);
        } else {
            this.mPreviewPanelController = new PreviewPanel2DController(this);
        }
        this.mControllers.add(this.mPreviewPanelController);
        this.mBrightnessController = new BrightnessController(this);
        this.mControllers.add(this.mBrightnessController);
        this.mBrightnessControllerExpand = new BrightnessControllerExpand(this);
        this.mControllers.add(this.mBrightnessControllerExpand);
        this.mBeautyshotController = new BeautyshotController(this);
        this.mControllers.add(this.mBeautyshotController);
        this.mSettingController = new SettingRotatableExpandableController(this);
        this.mControllers.add(this.mSettingController);
        this.mZoomController = new ZoomController(this);
        this.mControllers.add(this.mZoomController);
        this.mManualFocusController = new ManualFocusController(this);
        this.mControllers.add(this.mManualFocusController);
        this.mDialogController = new DialogController(this);
        this.mControllers.add(this.mDialogController);
        this.mSoundController = new SoundController(this);
        this.mControllers.add(this.mSoundController);
        this.mToastController = new ToastController(this);
        this.mControllers.add(this.mToastController);
        this.mQuickFunctionDragController = new QuickFunctionDragController(this);
        this.mControllers.add(this.mQuickFunctionDragController);
        this.mQuickFunctionSettingController = new QuickFunctionSettingController(this);
        this.mControllers.add(this.mQuickFunctionSettingController);
        this.mQuickButtonController = new QuickButtonController(this);
        this.mControllers.add(this.mQuickButtonController);
        this.mOptionMenuController = new OptionMenuController(this);
        this.mControllers.add(this.mOptionMenuController);
        if (ModelProperties.is3dSupportedModel()) {
            this.mCamera3dDepthController = new Camera3dDepthController(this);
            this.mControllers.add(this.mCamera3dDepthController);
        }
    }

    public boolean getInCaptureProgress() {
        return this.mInCaptureProgress;
    }

    public void setInCaptureProgress(boolean state) {
        this.mInCaptureProgress = state;
    }

    public void setGoingAutoReviewForQuickView(boolean isGoing) {
        CamLog.d(FaceDetector.TAG, "setGoingAutoReviewForQuickView :" + isGoing);
        this.mGoingAutoQuickReview = isGoing;
    }

    public boolean getGoingAutoReviewForQuickView() {
        CamLog.d(FaceDetector.TAG, "getGoingAutoReviewForQuickView :" + this.mGoingAutoQuickReview);
        return this.mGoingAutoQuickReview;
    }

    public int getParameteredRotation() {
        return this.mParameteredRotation;
    }

    public void setParameteredRotation(int value) {
        this.mParameteredRotation = value;
    }

    public ImageHandler getImageHandler() {
        return getImageHandler(true);
    }

    public ImageHandler getImageHandler(boolean isRotationOn) {
        if (isRotationOn) {
            return this.imageHandler;
        }
        return this.imageHandlerRotationOff;
    }

    public Bitmap getImage(Bitmap bmp, int degree, boolean mirror) {
        return getImageHandler().getImage(bmp, degree, mirror);
    }

    public int getImageRotationDegree() {
        return this.mImageRotationDegree;
    }

    public void setImageRotationDegree(int degree) {
        CamLog.d(FaceDetector.TAG, "mImageRotationDegree [" + this.mImageRotationDegree + "]");
        this.mImageRotationDegree = degree;
    }

    public Uri getSavedImageUri() {
        return this.mSavedImageUri;
    }

    public void setSavedImageUri(Uri uri) {
        this.mSavedImageUri = uri;
    }

    public String getSavedFileName() {
        return this.mSavedFileName;
    }

    public void setSavedFileName(String name) {
        this.mSavedFileName = name;
    }

    public void setCameraId(int cameraId) {
        this.mCameraId = cameraId;
    }

    public int getCameraId() {
        return this.mCameraId;
    }

    public void enableInput(boolean enable) {
        CamLog.v(FaceDetector.TAG, "enableInput(" + enable + ")");
        this.mIsEnableInput = enable;
    }

    public boolean getEnableInput() {
        return this.mIsEnableInput;
    }

    public void debugStackTrace(String from) {
        Util.debugStackTrace(from);
    }

    public void clearScreen() {
        clearSubMenu();
        this.mQuickFunctionController.hide();
        this.mIndicatorController.hideIndicator();
    }

    public void clearSubMenu() {
        clearSubMenu(true);
    }

    public boolean isClearView() {
        return this.mIsClearView;
    }

    public boolean isClearViewBeforeRecording() {
        return this.mIsClearViewBeforeRecording;
    }

    public void clearViewOn(boolean useAnimation) {
        if ((ProjectVariables.isSupportClearView() || ProjectVariables.isSupportKDDICleanView()) && !this.mIsClearView) {
            removeScheduledCommand(Command.CLEAR_SCREEN);
            this.mQuickFunctionController.slideQFLOut(useAnimation);
            this.mQuickButtonController.setVisible(100, 4, true);
            this.mIndicatorController.slideIndicatorOut(useAnimation);
            if (!ProjectVariables.isSupportKDDICleanView()) {
                this.mPreviewPanelController.slidePanelOut();
            }
            this.mIsClearView = true;
        }
    }

    public void clearViewOff(boolean useAnimation) {
        if ((ProjectVariables.isSupportClearView() || ProjectVariables.isSupportKDDICleanView()) && this.mIsClearView) {
            this.mIsClearView = false;
            this.mQuickFunctionController.slideQFLIn(useAnimation);
            this.mIndicatorController.slideIndicatorIn(useAnimation);
            this.mQuickButtonController.setVisible(100, 0, true);
            if (!ProjectVariables.isSupportKDDICleanView()) {
                this.mPreviewPanelController.slidePanelIn();
                resetTimeClearScreen();
            }
        }
    }

    public void clearViewOnAtStartRec() {
        if (ProjectVariables.isSupportClearView() || ProjectVariables.isSupportKDDICleanView()) {
            this.mIsClearViewBeforeRecording = this.mIsClearView;
            clearViewOn(true);
        }
    }

    public void clearViewRestoreAtStopRec() {
        if (!ProjectVariables.isSupportClearView() && !ProjectVariables.isSupportKDDICleanView()) {
            return;
        }
        if (this.mIsClearViewBeforeRecording) {
            clearViewOn(true);
        } else {
            clearViewOff(true);
        }
    }

    public void toggleClearView() {
        if (!ProjectVariables.isSupportClearView() && !ProjectVariables.isSupportKDDICleanView()) {
            return;
        }
        if (getQuickFunctionController().isSliding() || getPreviewPanelController().isSliding() || getQuickButtonController().isSliding()) {
            CamLog.d(FaceDetector.TAG, "return toggleClearView because on sliding");
            return;
        }
        CamLog.d(FaceDetector.TAG, "toggleClearView");
        if (getApplicationMode() == 0) {
            if (isTimerShotCountdown() || isContinuousShotAlived() || isPanoramaStarted() || this.mInCaptureProgress) {
                return;
            }
        } else if (getVideoState() != 0) {
            return;
        }
        if (this.mIsClearView) {
            clearViewOff(true);
        } else {
            clearViewOn(true);
        }
        if (ProjectVariables.isSupportKDDICleanView()) {
            SharedPreferences pref = getActivity().getSharedPreferences(Setting.SETTING_PRIMARY, 0);
            if (pref != null) {
                Editor edit = pref.edit();
                edit.putBoolean(CameraConstants.CLEAN_VIEW_ON, this.mIsClearView);
                edit.apply();
            }
        }
    }

    public void resetTimeClearScreen() {
        if (ProjectVariables.isSupportClearView()) {
            removeScheduledCommand(Command.CLEAR_SCREEN);
            doCommandDelayed(Command.CLEAR_SCREEN, CameraConstants.TOAST_LENGTH_LONG);
        }
    }

    public void clearSubMenu(boolean includeManualFocus) {
        removeScheduledCommand(Command.EXIT_ZOOM_BRIGHTNESS_INTERACTION);
        this.mZoomController.showControl(false);
        this.mBrightnessController.showControl(false);
        this.mBeautyshotController.showControl(false);
        if (ModelProperties.is3dSupportedModel()) {
            this.mCamera3dDepthController.showControl(false);
        }
        clearQuickFunctionSubMenu();
        if (isQuickFunctionSettingControllerShowing()) {
            removeQuickFunctionSettingView();
        }
        if (isShotModeMenuVisible()) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("immediately", true);
            doCommandUi(Command.HIDE_MODE_MENU, bundle);
        }
        if (isSettingControllerVisible()) {
            removeSettingView();
        }
        setSubMenuMode(0);
        if (!(isPausing() || isTimerShotCountdown())) {
            updateThumbnailButtonVisibility();
        }
        if (includeManualFocus) {
            this.mManualFocusController.showControl(false);
        }
    }

    public int getApplicationMode() {
        return this.mApplicationMode;
    }

    public String getApplicationModeString() {
        if (this.mApplicationMode == 1) {
            return "Camcorder";
        }
        return "Camera";
    }

    public void setApplicationMode(int mode) {
        this.mApplicationMode = mode;
    }

    public int getCameraMode() {
        return this.mCameraMode;
    }

    public void setCameraMode(int mode) {
        this.mCameraMode = mode;
    }

    public void setMainCameraDimension(int mode) {
        this.mMainCameraDimension = mode;
    }

    public int getMainCameraDimension() {
        return this.mMainCameraDimension;
    }

    public int getCameraDimension() {
        if (!ModelProperties.is3dSupportedModel()) {
            return 0;
        }
        if (this.mCameraMode == 0 && this.mMainCameraDimension == 1) {
            return 1;
        }
        return 0;
    }

    public int getOrientation() {
        if (this.mOrientationInfo == null) {
            return 0;
        }
        return this.mOrientationInfo.getOrientation();
    }

    public void setOrientation(int orientation) {
        if (this.mOrientationInfo != null) {
            this.mOrientationInfo.setOrientation(orientation);
        }
    }

    public int getOrientationDegree() {
        if (this.mOrientationInfo == null) {
            return 0;
        }
        return this.mOrientationInfo.getOrientationDegree();
    }

    public int getDeviceDegree() {
        return this.mOrientationInfo.getDeviceDegree(this.mCameraId);
    }

    public int getRecordingDegree() {
        int degree = VideoRecorder.getOrientationHint();
        if (isConfigureLandscape()) {
            return (degree + MediaProviderUtils.ROTATION_180) % CameraConstants.DEGREE_360;
        }
        if (getCameraId() == 1 && (degree == 90 || degree == Tag.IMAGE_DESCRIPTION)) {
            return (degree + 90) % CameraConstants.DEGREE_360;
        }
        return (degree + Tag.IMAGE_DESCRIPTION) % CameraConstants.DEGREE_360;
    }

    public void setOrientationListener() {
        CamLog.v(FaceDetector.TAG, "setOrientationListener");
        if (this.mOrientationInfo != null) {
            this.mOrientationInfo.setOrientationListener();
        }
    }

    public void setOrientationForced(int orientation) {
        CamLog.v(FaceDetector.TAG, "setOrientationForced");
        if (orientation == -1) {
            if (this.mOrientationInfo.getOrientation() == 0) {
                orientation = 1;
            } else if (this.mOrientationInfo.getOrientation() == 1) {
                orientation = 0;
            } else {
                return;
            }
        }
        if (this.mOrientationInfo != null) {
            this.mOrientationInfo.setOrientationForced(orientation);
        }
    }

    public void setSlimPortDegree(int degree) {
        if (this.mOrientationInfo != null) {
            this.mOrientationInfo.setSlimPortDegree(degree);
        }
    }

    public boolean isConfigureLandscape() {
        return Util.isConfigureLandscape(getResources());
    }

    public void setOrientationListenerRotate(int orientation) {
        if (isControllerInitialized()) {
            doCommandUi(Command.ROTATE);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("animation", false);
        doCommandUi(Command.ROTATE, bundle);
    }

    public void setEffectRecorderOrientationHint() {
        if (this.mPreviewController != null && this.mPreviewController.mEffectsRecorder != null && isEffectsCamcorderActive()) {
            int orientationCompensation = isConfigureLandscape() ? getOrientationDegree() : (getOrientationDegree() + 90) % CameraConstants.DEGREE_360;
            if (this.mPreviewController.mEffectsRecorder.getOrientationHint() != orientationCompensation) {
                CamLog.v(FaceDetector.TAG, "orientationCompensation:" + orientationCompensation);
                this.mPreviewController.mEffectsRecorder.setOrientationHint(orientationCompensation);
            }
        }
    }

    public void setEffectCameraOrientationHint() {
        if (this.mPreviewController != null && this.mPreviewController.mEffectsCamera != null && isEffectsCameraActive()) {
            int orientationCompensation = isConfigureLandscape() ? getOrientationDegree() : (getOrientationDegree() + 90) % CameraConstants.DEGREE_360;
            if (this.mPreviewController.mEffectsCamera.getOrientationHint() != orientationCompensation) {
                CamLog.v(FaceDetector.TAG, "orientationCompensation:" + orientationCompensation);
                this.mPreviewController.mEffectsCamera.setOrientationHint(orientationCompensation);
            }
        }
    }

    private void setPreviewEffectForBeautyShot() {
        if (getApplicationMode() != 0) {
            return;
        }
        if ((CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)) || CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) && !Common.isQuickWindowCameraMode() && this.mPreviewController != null) {
            try {
                OpenGLSurfaceView gPreview = this.mPreviewController.getCameraGLPreview();
                SurfaceView gPreviewExtra = this.mPreviewController.getCameraGLPreviewExtra();
                swapPreviewEffect(this.mFaceBeauty);
                if (gPreview != null) {
                    gPreview.setVisibility(0);
                }
                if (gPreviewExtra != null) {
                    gPreviewExtra.setVisibility(0);
                }
            } catch (NullPointerException e) {
                CamLog.e(FaceDetector.TAG, "NullPointerException!", e);
            }
        }
    }

    public void setPreviewEffectForBeautyShotMode(LGParameters lgParameters, boolean isRendered) {
        CamLog.e(FaceDetector.TAG, "setPreviewEffectForBeautyShotMode - start");
        try {
            CameraPreview sPreview = this.mPreviewController.getCameraPreview();
            sPreview.releasePreviewCallback(getCameraDevice());
            sPreview.setVisibility(4);
            OpenGLSurfaceView gPreview;
            SurfaceView gPreviewExtra;
            if (isRendered) {
                CamLog.e(FaceDetector.TAG, "setPreviewEffectForBeautyShotMode - shot mode is beautyshot");
                gPreview = this.mPreviewController.getCameraGLPreview();
                gPreviewExtra = this.mPreviewController.getCameraGLPreviewExtra();
                gPreview.releasePreviewCallback(getCameraDevice());
                gPreview.setVisibility(4);
                gPreview.clearData(true);
                gPreview.setVisibility(0);
                gPreviewExtra.setVisibility(0);
                this.mPreviewController.startPreview(lgParameters, true);
            } else {
                gPreviewExtra = this.mPreviewController.getCameraGLPreviewExtra();
                gPreview = this.mPreviewController.getCameraGLPreview();
                gPreview.releasePreviewCallback(getCameraDevice());
                gPreview.setVisibility(8);
                gPreviewExtra.setVisibility(8);
                getEngineProcessor().releaseAllEngine();
                swapPreviewEffect(null);
                sPreview.setVisibility(0);
                this.mPreviewController.startPreview(lgParameters, true);
            }
        } catch (NullPointerException e) {
            CamLog.e(FaceDetector.TAG, "NullPointerException:", e);
            if (this.mPreviewController != null) {
                this.mPreviewController.startPreview(null, true);
            }
        }
        CamLog.e(FaceDetector.TAG, "setPreviewEffectForBeautyShotMode - end");
    }

    public void setSubMenuMode(int mode) {
        this.mSubMenuMode = mode;
    }

    public int getSubMenuMode() {
        return this.mSubMenuMode;
    }

    public void setSubCameraModeRunning(boolean mode) {
        this.mSetCameraMode = mode;
    }

    public boolean getSubCameraModeRunning() {
        return this.mSetCameraMode;
    }

    public int getBatteryLevel() {
        return this.mBatteryLevel;
    }

    public void setBatteryLevel(int level) {
        this.mBatteryLevel = level;
    }

    public int getActualBatteryLevel() {
        return this.mActualBatteryLevel;
    }

    public void setActualBatteryLevel(int level) {
        this.mActualBatteryLevel = level;
    }

    public void setBatteryTemper(int temper) {
        if (this.mCheckTemperature != null) {
            this.mCheckTemperature.setBatteryTemper(temper);
        }
    }

    public boolean getRotateBatteryIndicatorwithHint() {
        return this.mRotateBatteryIndicatorwithHint;
    }

    public void setRotateBatteryIndicatorwithHint(boolean mode) {
        this.mRotateBatteryIndicatorwithHint = mode;
    }

    public boolean isPausing() {
        return this.mPausing;
    }

    public void setPausing(boolean state) {
        this.mPausing = state;
    }

    public int getVideoState() {
        return this.mVideoState;
    }

    public void setVideoState(int state) {
        CamLog.d(FaceDetector.TAG, "Set video state from " + this.mVideoState + " to " + state);
        this.mVideoState = state;
        doCommandUi(Command.UPDATE_CAPTURE_BUTTON);
        doCommandUi(Command.UPDATE_REC_INDICATOR);
    }

    public void setVideoStateOnly(int state) {
        CamLog.d(FaceDetector.TAG, "Set video state from " + this.mVideoState + " to " + state);
        this.mVideoState = state;
    }

    public Module getCurrentModule() {
        return this.mModule;
    }

    public String getCurrentModuleName() {
        if (this.mModuleFactory == null) {
            return null;
        }
        return this.mModuleFactory.getCurrentModuleName();
    }

    public boolean isCurrnetModuleRunning() {
        return getCurrentModule().isRunning();
    }

    public void setModule(String name) {
        CamLog.d(FaceDetector.TAG, "mediator setModule : " + name);
        this.mModule = this.mModuleFactory.getModule(name);
    }

    public boolean checkCurrentShotModeForModule() {
        if (CameraConstants.TYPE_PREFERENCE_NOT_FOUND.endsWith(getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)) || getCameraId() == 1) {
            return true;
        }
        if (this.mModule != null) {
            return this.mModule.checkCurrentShotMode();
        }
        return false;
    }

    public boolean doTakePictureCommand() {
        return getCurrentModule().takePicture();
    }

    public void setNeedProgressDuringCapture(boolean set) {
        this.isNeedProgressDuringCapture = set;
    }

    public boolean getNeedProgressDuringCapture() {
        return this.isNeedProgressDuringCapture;
    }

    public boolean isErrorOccuredAndFinish() {
        return this.mErrorOccuredAndFinish;
    }

    public void setErrorOccuredAndFinish(boolean error) {
        this.mErrorOccuredAndFinish = error;
    }

    public boolean isSendBroadcastIntent() {
        return this.mIsSendBroadcastIntent;
    }

    public void setIsSendBroadcastIntent(boolean changed) {
        this.mIsSendBroadcastIntent = changed;
    }

    public void resetScreenTimeout() {
        CamLog.v(FaceDetector.TAG, "resetScreenTimeout");
        if (this.mActivity != null) {
            Activity activity = getActivity();
            if (activity == null) {
                CamLog.w(FaceDetector.TAG, "resetScreenTimeout : getActivity is null!!");
            } else {
                activity.getWindow().clearFlags(128);
            }
        }
    }

    public void keepScreenOnAwhile() {
        CamLog.v(FaceDetector.TAG, "keepScreenOnAwhile");
        if (this.mActivity == null) {
            CamLog.w(FaceDetector.TAG, "keepScreenOnAwhile : mActivity is null!!");
            return;
        }
        Activity activity = getActivity();
        if (activity == null) {
            CamLog.w(FaceDetector.TAG, "keepScreenOnAwhile : getActivity is null!!");
            return;
        }
        removeScheduledCommand(Command.EXIT_INTERACTION);
        activity.getWindow().addFlags(128);
        doCommandDelayed(Command.EXIT_INTERACTION, 120000);
    }

    public void setKeepScreenOn() {
        CamLog.v(FaceDetector.TAG, "setKeepScreenOn");
        removeScheduledCommand(Command.EXIT_INTERACTION);
        getActivity().getWindow().addFlags(128);
    }

    public void onUserInteraction() {
        if (getVideoState() != 3 && getVideoState() != 4 && isControllerInitialized() && !isPausing()) {
            keepScreenOnAwhile();
        }
    }

    public void releaseControllerForReInitialize() {
        Iterator i$ = this.mControllers.iterator();
        while (i$.hasNext()) {
            ((Controller) i$.next()).onPause();
        }
        i$ = this.mControllers.iterator();
        while (i$.hasNext()) {
            ((Controller) i$.next()).onStop();
        }
        if (this.mOrientationInfo != null) {
            this.mOrientationInfo.setOrientationListenerEnable(false);
            this.mOrientationInfo.releaseOrientationListener();
        }
    }

    public void resetControllerForReInitialize() {
        if (!(this.mOrientationInfo == null || this.mOrientationInfo.getOrientationListenerEnable())) {
            CamLog.d(FaceDetector.TAG, "onResume() mOrientationChangeEnabled = " + this.mOrientationInfo.getOrientationListenerEnable());
            this.mOrientationInfo.setOrientationListener();
            this.mOrientationInfo.setOrientationListenerEnable(true);
        }
        getPreviewController().onCreate();
        Iterator i$ = this.mControllers.iterator();
        while (i$.hasNext()) {
            Controller control = (Controller) i$.next();
            control.reInitialize();
            control.initController();
        }
        i$ = this.mControllers.iterator();
        while (i$.hasNext()) {
            ((Controller) i$.next()).onStart();
        }
        i$ = this.mControllers.iterator();
        while (i$.hasNext()) {
            ((Controller) i$.next()).onResume();
        }
        showDefaultQuickButton(true);
        doCommandUi(Command.ROTATE);
    }

    public void initControllers() {
        CamLog.d(FaceDetector.TAG, "initController start");
        this.mFocusController.initController();
        this.mZoomController.initController();
        this.mBrightnessController.initController();
        this.mManualFocusController.initController();
        this.mBeautyshotController.initController();
        this.mStorageController.initController();
        this.mSoundController.initController();
        this.mQuickFunctionDragController.initController();
        this.mQuickFunctionSettingController.initController();
        this.mShotModeMenuController.initController();
        if (ModelProperties.is3dSupportedModel()) {
            this.mCamera3dDepthController.initController();
        }
        this.mControllerInitialized = true;
        if (this.mLocationInfo != null) {
            this.mLocationInfo.setLocationManager((LocationManager) getActivity().getSystemService("location"));
            if (CameraConstants.SMART_MODE_ON.equals(this.mSettingController.getSettingValue(Setting.KEY_CAMERA_TAG_LOCATION))) {
                this.mLocationInfo.setRecordLocation(true);
                startReceivingLocationUpdates();
            } else {
                this.mLocationInfo.setRecordLocation(false);
            }
        }
        CamLog.d(FaceDetector.TAG, "initController end");
    }

    public boolean isControllerInitialized() {
        return this.mControllerInitialized;
    }

    public void resetControllerInitialized() {
        this.mControllerInitialized = false;
    }

    public void onCreate() {
        this.mBroadCastReceiver = new BroadCastReceiverDefine(this, Common.isSecureCamera());
        registerReceiver();
        if (this.mOrientationInfo != null) {
            this.mOrientationInfo.initOsManager();
            this.mOrientationInfo.setOrientationListener();
            this.mOrientationInfo.setOrientationListenerEnable(true);
            if (ModelProperties.getProjectCode() == 33 && this.mOrientationInfo.getOrientation() == -1) {
                this.mOrientationInfo.initailizeOrientation();
            }
        }
        if (this.mLocationInfo != null) {
            this.mLocationInfo.setLocationListener(new LocationListener[]{new LocationListener(this, "gps"), new LocationListener(this, "network")});
        }
        initAudioShutterEngine();
    }

    public void onStart() {
        boolean z;
        CamLog.d(FaceDetector.TAG, "onStart()-start");
        if (!getActivity().isFinishing() && AppControlUtil.isSecureCameraIntent(getActivity().getIntent())) {
            Common.setSecureCamera(true);
        } else if (AppControlUtil.checkGuestModeAndAppDisabled(getContentResolver(), true, 1)) {
            Common.setSecureCamera(false);
        } else {
            Common.setSecureCamera(false);
            SecureImageUtil.get().release();
        }
        Window window = getActivity().getWindow();
        AppControlUtil.setQuickWindowCameraFromIntent(getActivity().getIntent());
        if (Common.isQuickWindowCameraMode() || Common.isSecureCamera()) {
            z = true;
        } else {
            z = false;
        }
        Common.configureWindowFlag(window, false, z);
        if (getActivity().getRequestedOrientation() != 1) {
            getActivity().setRequestedOrientation(1);
        }
        if (ModelProperties.getProjectCode() == 33) {
            doCommandUi(Command.ROTATE);
        }
        Iterator i$ = this.mControllers.iterator();
        while (i$.hasNext()) {
            ((Controller) i$.next()).onStart();
        }
        CamLog.d(FaceDetector.TAG, "onStart()-end");
    }

    public void onResume() {
        CamLog.d(FaceDetector.TAG, "onResume()-start");
        CameraConstants.MEDIA_RECEIVER_FINISHED = false;
        this.mPausing = false;
        setBackKeyPressed(false);
        this.isEventIgnoreDuringSaving = false;
        setPreviousAutoReviewValue();
        showDefaultQuickButton(true);
        updateModeMenuIndicator();
        if (this.mPreviewController != null) {
            String sizeOnScreenString = getPreviewSizeOnScreen();
            if (sizeOnScreenString != null) {
                int[] previewSizeOnScreen = Util.SizeString2WidthHeight(sizeOnScreenString);
                this.mPreviewController.changePreviewMode(previewSizeOnScreen[0], previewSizeOnScreen[1]);
            }
            if (this.mPreviewController.getCameraGLPreview() != null) {
                this.mPreviewController.getCameraGLPreview().setActivityPausing(false);
            }
        }
        if (this.mCheckTemperature != null) {
            this.mCheckTemperature.checkTemperatureForKddi();
        }
        this.imageHandler.resetRotation();
        if (!(this.mOrientationInfo == null || this.mOrientationInfo.getOrientationListenerEnable())) {
            CamLog.d(FaceDetector.TAG, "onResume() mOrientationChangeEnabled = " + this.mOrientationInfo.getOrientationListenerEnable());
            this.mOrientationInfo.initOsManager();
            this.mOrientationInfo.setOrientationListener();
            this.mOrientationInfo.setOrientationListenerEnable(true);
        }
        FileNamer.get().startFileNamer(getApplicationContext(), getApplicationMode(), getCurrentStorage(), getCurrentStorageDirectory(), true);
        setPreviewEffectForBeautyShot();
        initAudioShutterEngine();
        Iterator i$ = this.mControllers.iterator();
        while (i$.hasNext()) {
            ((Controller) i$.next()).onResume();
        }
        if (this.mControllerInitialized) {
            showQuickMenuEnteringGuide(true);
        } else if (!findScheduledCommand(Command.START_INIT)) {
            CamLog.d(FaceDetector.TAG, "!findScheduledCommand(Command.START_INIT)");
            doCommand(Command.START_INIT);
        }
        if (!Common.isScreenLocked()) {
            keepScreenOnAwhile();
        }
        if (getSubMenuMode() == 5 || getSubMenuMode() == 16) {
            hideOsd();
        } else {
            showOsd();
        }
        setBlockTouchByCallPopUp(false);
        setQuickFunctionMenuForcedDisable(false);
        doCommandDelayed(Command.EXIT_ZOOM_BRIGHTNESS_INTERACTION, ProjectVariables.keepDuration);
        this.mChangingToOtherActivity = false;
        CamLog.d(FaceDetector.TAG, "onResume()-end");
    }

    public void onPause() {
        CamLog.d(FaceDetector.TAG, "onPause-start");
        restoreAutoReviewValue();
        this.mPausing = true;
        this.mErrorOccuredAndFinish = false;
        this.isEventIgnoreDuringSaving = false;
        if (this.mCheckTemperature != null) {
            this.mCheckTemperature.releaseCheckTemperature();
        }
        if (this.mPreviewController != null) {
            this.mPreviewController.stopPreviewForMediatorOnPause();
        }
        if (FunctionProperties.isVoiceShutter() && this.mAudioRecogEngine != null) {
            this.mAudioRecogEngine.stop();
            this.mAudioRecogEngine = null;
        }
        if (this.mOrientationInfo != null) {
            this.mOrientationInfo.setOrientationListenerEnable(false);
            this.mOrientationInfo.releaseOrientationListener();
        }
        Iterator i$ = this.mControllers.iterator();
        while (i$.hasNext()) {
            ((Controller) i$.next()).onPause();
        }
        BubblePopupController.get().removeBubblePopup(this, 0);
        BubblePopupController.get().unbind();
        FileNamer.get().close(getApplicationContext(), getCurrentStorage());
        EnteringViewController.get().hideOnPauseEnteringGuide(getActivity());
        EnteringViewController.get().unbind();
        resetScreenTimeout();
        removeScheduledAllCommand();
        this.mInCaptureProgress = false;
        this.mGoingAutoQuickReview = false;
        AudioUtil.setStreamMute(getApplicationContext(), false);
        AudioUtil.setVibrationMute(getApplicationContext(), false);
        CamLog.d(FaceDetector.TAG, "onPause-end");
    }

    public void onStop() {
        toastControllerHide(true);
        Iterator i$ = this.mControllers.iterator();
        while (i$.hasNext()) {
            ((Controller) i$.next()).onStop();
        }
    }

    public void onDestroy() {
        if (this.mOrientationInfo != null) {
            this.mOrientationInfo.setOrientationListenerEnable(false);
            this.mOrientationInfo.releaseOrientationListener();
            this.mOrientationInfo.unbind();
            this.mOrientationInfo = null;
        }
        if (this.mLocationInfo != null) {
            this.mLocationInfo.setLocationListener(null);
            this.mLocationInfo.setLocationManager(null);
            this.mLocationInfo.unbind();
            this.mLocationInfo = null;
        }
        if (this.mCheckTemperature != null) {
            this.mCheckTemperature = null;
        }
        removeScheduledAllCommand();
        removePostAllRunnables();
        unregisterReceivers();
        if (this.mBroadCastReceiver != null) {
            this.mBroadCastReceiver.unbindReceiver();
            this.mBroadCastReceiver = null;
        }
        Iterator i$ = this.mControllers.iterator();
        while (i$.hasNext()) {
            ((Controller) i$.next()).onDestroy();
        }
        if (this.mModuleFactory != null) {
            this.mModuleFactory.unbind();
            this.mModuleFactory = null;
        }
        this.mModule = null;
        this.mControllers = null;
        this.mActivity = null;
        this.mModuleFactory = null;
        this.imageHandler = null;
        this.imageHandlerRotationOff = null;
        this.mSoundController = null;
        this.mPreviewController = null;
        this.mPreviewPanelController = null;
        this.mQuickFunctionController = null;
        this.mQuickFunctionDragController = null;
        this.mQuickFunctionSettingController = null;
        this.mIndicatorController = null;
        this.mBrightnessController = null;
        this.mBeautyshotController = null;
        this.mSettingController = null;
        this.mZoomController = null;
        this.mManualFocusController = null;
        this.mDialogController = null;
        this.mFocusController = null;
        this.mStorageController = null;
        this.mToastController = null;
        this.mOptionMenuController = null;
        this.mShotModeMenuController = null;
        this.mPostRunnables = null;
        this.mRefreshMenuRunnable = null;
        if (this.mHandler != null) {
            this.mHandler.unbind();
            this.mHandler = null;
        }
        this.mUiThread = null;
        this.mCommandManager = null;
        this.mFaceBeauty = null;
        if (this.mFrontPreferenceGroup != null) {
            this.mFrontPreferenceGroup.close();
            this.mFrontPreferenceGroup = null;
        }
        if (this.mBackPreferenceGroup != null) {
            this.mBackPreferenceGroup.close();
            this.mBackPreferenceGroup = null;
        }
        if (this.mBack3dPreferenceGroup != null) {
            this.mBack3dPreferenceGroup.close();
            this.mBack3dPreferenceGroup = null;
        }
    }

    protected void registerReceiver() {
        if (!this.mDidRegister) {
            this.mBroadCastReceiver.registerReceiver();
            this.mDidRegister = true;
        }
    }

    protected void unregisterReceivers() {
        if (this.mDidRegister) {
            this.mBroadCastReceiver.unregisterReceivers();
            this.mDidRegister = false;
        }
    }

    public boolean isSwapCameraProcessing() {
        return this.mIsSwapCameraProcessing;
    }

    public void setIsSwapCameraProcessing(boolean state) {
        this.mIsSwapCameraProcessing = state;
    }

    public void setKeepLastCameraMode() {
        CamLog.d(FaceDetector.TAG, "setKeepLastCameraMode()-start");
        Intent intent = getActivity().getIntent();
        SharedPreferences pref = getActivity().getSharedPreferences(Setting.SETTING_PRIMARY, 0);
        int cameraModeOfCamera = Setting.readPreferredCameraId(pref);
        int mainCameraDimension = intent.getIntExtra("mainCameraDimension", 0);
        intent.removeExtra("mainCameraDimension");
        CamLog.i(FaceDetector.TAG, "setKeepLastCameraMode CameraID : " + cameraModeOfCamera);
        if (Common.isQuickWindowCameraMode()) {
            CamLog.i(FaceDetector.TAG, "setKeepLastCameraMode SmartCoverClose! CameraID : 0");
            cameraModeOfCamera = 0;
        }
        setCameraMode(cameraModeOfCamera);
        setCameraId(cameraModeOfCamera);
        Setting.writePreferredCameraId(pref, cameraModeOfCamera);
        setMainCameraDimension(mainCameraDimension);
        if (AppControlUtil.isSecureCameraIntent(intent)) {
            Common.setSecureCamera(true);
        } else if (AppControlUtil.checkGuestModeAndAppDisabled(getContentResolver(), true, 1)) {
            Common.setSecureCamera(false);
        } else {
            Common.setSecureCamera(false);
            SecureImageUtil.get().release();
        }
        CamLog.d(FaceDetector.TAG, "setKeepLastCameraMode()-end, cammode = " + getCameraMode());
    }

    public void setKeepLastValue() {
        Intent intent = getActivity().getIntent();
        boolean keepLastValue = intent.getBooleanExtra("keepLastValue", false);
        if (!intent.getBooleanExtra("isFirst", true) && keepLastValue) {
            int i;
            ListPreference listPref;
            int pSize = this.mBackPreferenceGroup.size();
            for (i = 0; i < pSize; i++) {
                listPref = this.mBackPreferenceGroup.getListPreference(i);
                if (listPref != null) {
                    listPref.keepLastValue();
                }
            }
            pSize = this.mFrontPreferenceGroup.size();
            for (i = 0; i < pSize; i++) {
                listPref = this.mFrontPreferenceGroup.getListPreference(i);
                if (listPref != null) {
                    listPref.keepLastValue();
                }
            }
        }
    }

    public boolean checkAutoReviewOff(boolean needToCheckMore) {
        if ((!CameraConstants.SMART_MODE_OFF.equals(getSettingValue(Setting.KEY_CAMERA_AUTO_REVIEW)) && !CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(getSettingValue(Setting.KEY_CAMERA_AUTO_REVIEW))) || isAttachIntent()) {
            return false;
        }
        if (!needToCheckMore) {
            return true;
        }
        String shotMode = getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
        if ("0".equals(getSettingValue(Setting.KEY_CAMERA_TIMER)) && (CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_HDR.equals(shotMode) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(shotMode))) {
            return true;
        }
        return false;
    }

    public boolean isCafSupported() {
        if (!FunctionProperties.isCafSupported(getApplicationMode(), getCameraId())) {
            return false;
        }
        if (this.mPreviewPanelController == null || !this.mPreviewPanelController.isShutterFocusLongKey()) {
            return true;
        }
        return false;
    }

    public boolean getOpenLBSSetting() {
        return this.mOpenLBSSetting;
    }

    public void setOpenLBSSetting(boolean setting) {
        this.mOpenLBSSetting = setting;
    }

    public void showPopupAnimation(int ViewId) {
        Util.startAlphaAnimation((RotateLayout) findViewById(ViewId), 0, 1, Limit.OLA_FIFO_DATA_MAX_SIZE_BYTE, null);
    }

    public void hidePopupAnimation(int ViewId) {
        Util.startAlphaAnimation((RotateLayout) findViewById(ViewId), 1, 0, Limit.OLA_FIFO_DATA_MAX_SIZE_BYTE, null);
    }

    public boolean isChangingToOtherActivity() {
        return this.mChangingToOtherActivity;
    }

    public void setChangingToOtherActivity(boolean value) {
        this.mChangingToOtherActivity = value;
    }

    public String getLanguageType() {
        return this.strLanguage;
    }

    public void setLanguageType(String mlanguage) {
        this.strLanguage = mlanguage;
    }

    public void refreshMenuForVolumeShutterPress() {
        if (getApplicationMode() == 0 && this.mRefreshMenuRunnable != null && getHandler() != null) {
            getHandler().removeCallbacks(this.mRefreshMenuRunnable);
            getHandler().postDelayed(this.mRefreshMenuRunnable, 700);
        }
    }

    public void initAudioShutterEngine() {
        if (FunctionProperties.isVoiceShutter() && this.mAudioRecogEngine == null) {
            this.mAudioRecogEngine = new AudioRecogEngine(new AudioRecogEngineCallback(this), FunctionProperties.voiceShutterKind());
        }
    }

    public void setAudioRecogEngineStop() {
        if (FunctionProperties.isVoiceShutter()) {
            CamLog.d(FaceDetector.TAG, "setAudioRecogEngineStop");
            if (this.mAudioRecogEngine != null) {
                this.mAudioRecogEngine.stop();
            }
        }
    }

    public void setAudioRecogEngineStart() {
        if (FunctionProperties.isVoiceShutter() && this.mAudioRecogEngine != null && getSettingValue(Setting.KEY_VOICESHUTTER).equals(CameraConstants.SMART_MODE_ON)) {
            CamLog.d(FaceDetector.TAG, "setAudioRecogEngineStart");
            this.mAudioRecogEngine.start();
        }
    }

    public void audioCallbackRestartEngine() {
        if (FunctionProperties.isVoiceShutter() && getApplicationMode() == 0 && getDialogID() != DialogCreater.DIALOG_ID_HELP_VOICE_PHOTO) {
            postOnUiThread(new Runnable() {
                public void run() {
                    Mediator.this.removePostRunnable(this);
                    Mediator.this.updateVoiceShutterIndicator(false);
                    Mediator.this.setAudioRecogEngineStart();
                }
            }, 500);
        }
    }

    public void audioCallbackTakePicture() {
        if (this.mPreviewPanelController != null) {
            this.mPreviewPanelController.audioCallbackTakePicture();
        }
    }

    public boolean isTimeMachineModeOn() {
        if (!FunctionProperties.isTimeMachinShotSupported() || getApplicationMode() == 1) {
            return false;
        }
        if (CameraConstants.TYPE_SHOTMODE_TIMEMACHINE.equals(getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)) || CameraConstants.SMART_MODE_ON.equals(getSettingValue(Setting.KEY_TIME_MACHINE))) {
            return true;
        }
        return false;
    }

    public void setPreferenceMenuEnable(String menu, boolean enable, boolean onlySetIcon) {
        if (this.mQuickFunctionController != null && this.mSettingController != null && getPreferenceGroup() != null) {
            if (AppControlUtil.checkGuestModeAndAppDisabled(getContentResolver(), true, 1) && (Setting.KEY_CAMERA_AUTO_REVIEW.equals(menu) || Setting.KEY_VIDEO_AUTO_REVIEW.equals(menu))) {
                enable = false;
            }
            int menuIndex = getQfIndex(menu);
            if (!onlySetIcon) {
                if (isQuickFunctionList(menuIndex)) {
                    setQFLMenuEnabled(menuIndex, enable);
                }
                if (getCurrentSettingMenu() != null) {
                    setCurrentSettingMenuEnable(menu, enable);
                }
                if (Setting.KEY_FLASH.equals(menu)) {
                    setButtonRemainEnabled(10, enable, enable);
                }
                if (Setting.KEY_LIGHT.equals(menu)) {
                    setButtonRemainEnabled(11, enable, enable);
                }
                setQuickFunctionControllerAllMenuIcons();
            } else if (isQuickFunctionList(menuIndex)) {
                setQuickFunctionControllerMenuIcon(menuIndex, this.mSettingController.getSetting(menu));
            }
        }
    }

    public void setPreferenceMenuOnlyEnable(String menu, boolean enable) {
        if (this.mQuickFunctionController != null && this.mSettingController != null && getPreferenceGroup() != null) {
            if (AppControlUtil.checkGuestModeAndAppDisabled(getContentResolver(), true, 1) && (Setting.KEY_CAMERA_AUTO_REVIEW.equals(menu) || Setting.KEY_VIDEO_AUTO_REVIEW.equals(menu))) {
                enable = false;
            }
            int menuIndex = getQfIndex(menu);
            if (isQuickFunctionList(menuIndex)) {
                setQFLMenuEnabled(menuIndex, enable);
            }
            if (getCurrentSettingMenu() != null) {
                setCurrentSettingMenuEnable(menu, enable);
            }
            if (Setting.KEY_FLASH.equals(menu)) {
                setButtonRemainEnabled(10, enable, enable);
            }
        }
    }

    public boolean checkSettingValue(String key, String shotMode) {
        if ((!Setting.KEY_CAMERA_SHOT_MODE.equals(key) || !isTimeMachineModeOn()) && shotMode.equals(getSettingValue(key))) {
            return true;
        }
        return false;
    }

    public void showQuickMenuEnteringGuide(boolean isShow) {
        if (!Common.isQuickWindowCameraMode() && FunctionProperties.isSupportHelpSetting()) {
            EnteringViewController.get().showQuickMenuEnteringGuide(getActivity(), this.mQuickFunctionController, isShow, getOrientationDegree());
        }
    }

    public boolean isEnteringViewShowing() {
        return EnteringViewController.get().isEnteringViewShowing();
    }

    public void setQuickButtonMode(boolean isIAon) {
        boolean addFlash;
        boolean needModeButton = true;
        if ((getApplicationMode() == 0 && CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) || (getApplicationMode() == 1 && CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(getSettingValue(Setting.KEY_VIDEO_RECORD_MODE)))) {
            needModeButton = false;
        }
        if (getCameraId() != 0 || FunctionProperties.isNoneFlashModel()) {
            addFlash = false;
        } else {
            addFlash = true;
        }
        SparseIntArray buttonDefine = getButtonDefine(needModeButton, isIAon, addFlash);
        if (buttonDefine != null) {
            int i = 0;
            while (i < 12) {
                int butLoc = buttonDefine.get(i, -1);
                if (butLoc > -1) {
                    addQuickButton(getApplicationContext(), i, butLoc);
                    if (isIAon) {
                        if (ModelProperties.getCarrierCode() != 6) {
                            setButtonRemainEnabled(i, true, true);
                        } else if (i == 8) {
                            setButtonRemainEnabled(i, false, true);
                        } else {
                            setButtonRemainEnabled(i, true, true);
                        }
                    } else if (ModelProperties.getCarrierCode() == 6 && i == 8) {
                        setButtonRemainEnabled(i, true, true);
                    }
                    setQuickButtonVisible(i, 0, false);
                } else {
                    removeQuickButton(i);
                }
                i++;
            }
            buttonDefine.clear();
        }
    }

    public boolean getIAFlashStatus() {
        return this.mIsIAFlashOn;
    }

    public void setIAFlashStatus(boolean flashStatus) {
        this.mIsIAFlashOn = flashStatus;
        Bundle bundle = new Bundle();
        bundle.putBoolean("isIAFlashOn", this.mIsIAFlashOn);
        doCommand(Command.CAMERA_IA_FLASH_MODE, null, bundle);
    }

    public void setShowCameraErrorPopup(boolean set) {
        this.bShowCameraErrorPopup = set;
    }

    public boolean getShowCameraErrorPopup() {
        return this.bShowCameraErrorPopup;
    }

    public int getRecentMessageType() {
        return this.mBroadCastReceiver.getRecentMessageType();
    }

    public void doSettingFlashHandler() {
        doCommand(Command.CAMERA_FLASH_MODE);
    }

    public void startReceivingLocationUpdates() {
        CamLog.d(FaceDetector.TAG, "startReceivingLocationUpdates()");
        if (this.mLocationInfo != null) {
            this.mLocationInfo.startReceivingLocationUpdates();
        }
    }

    public void stopReceivingLocationUpdates() {
        CamLog.d(FaceDetector.TAG, "stopReceivingLocationUpdates");
        if (this.mLocationInfo != null) {
            this.mLocationInfo.stopReceivingLocationUpdates();
        }
    }

    public Location getCurrentLocation() {
        if (this.mLocationInfo != null) {
            return this.mLocationInfo.getCurrentLocation();
        }
        return null;
    }

    public boolean checkGPSSettingValue() {
        if (CameraConstants.SMART_MODE_OFF.equals(getSettingValue(Setting.KEY_CAMERA_TAG_LOCATION))) {
            return false;
        }
        return true;
    }

    public boolean getRecordLocation() {
        return this.mLocationInfo.getRecordLocation();
    }

    public void setRecordLocation(boolean state) {
        this.mLocationInfo.setRecordLocation(state);
    }

    public boolean getLocationOn() {
        return this.mLocationInfo.getLocationOn();
    }

    public void setLocationOn(boolean setting) {
        this.mLocationInfo.setLocationOn(setting);
    }

    public void removeSettingView() {
        this.mSettingController.removeSettingView();
    }

    public void removeSettingViewAll() {
        this.mSettingController.removeSettingViewAll();
    }

    public ListPreference getSettingListPreference(String key) {
        return this.mSettingController.getSetting().getSettingListPreference(key);
    }

    public String getSettingValue(String key) {
        return this.mSettingController.getSettingValue(key);
    }

    public int getSettingIndex(String key) {
        return this.mSettingController.getSetting(key);
    }

    public int getSettingIndex(int settingIndex) {
        return this.mSettingController.getSetting(settingIndex);
    }

    public SettingMenu getCurrentSettingMenu() {
        return this.mSettingController.getCurrentSettingMenu();
    }

    public int getSelectedChildIndex() {
        return getCurrentSettingMenu().getSelectedChildIndex();
    }

    public int getCurrentSettingMenuIndex(String key) {
        return this.mSettingController.getCurrentSettingMenu().getMenuIndex(key);
    }

    public void allSettingMenuSelectedChild(String key, boolean useCurrentSettingValue) {
        int chlidSettingIndex = useCurrentSettingValue ? getSettingIndex(key) : getCurrentSettingMenu().getSelectedChildIndex();
        if (getApplicationMode() == 0) {
            this.mSettingController.getCameraMainSettingMenu().setSelectedChild(key, chlidSettingIndex);
            if (ModelProperties.isSupportFrontCameraModel()) {
                this.mSettingController.getCameraFrontSettingMenu().setSelectedChild(key, chlidSettingIndex);
                return;
            }
            return;
        }
        this.mSettingController.getVideoMainSettingMenu().setSelectedChild(key, chlidSettingIndex);
        if (ModelProperties.isSupportFrontCameraModel()) {
            this.mSettingController.getVideoFrontSettingMenu().setSelectedChild(key, chlidSettingIndex);
        }
    }

    public int getCurrentSettingMenuIndex() {
        return this.mSettingController.getCurrentSettingMenu().getCurrentMenuIndex();
    }

    public int getSettingMenuCount() {
        return getCurrentSettingMenu().getMenuCount();
    }

    public int getSelectedChildCount(int currentSelectIndex) {
        return getCurrentSettingMenu().getMenu(currentSelectIndex).getChildCount();
    }

    public SettingMenuItem getSettingMenuItem(int index) {
        return getCurrentSettingMenu().getMenu(index);
    }

    public SettingMenuItem getSettingMenuItem(String key) {
        return getCurrentSettingMenu().getMenu(key);
    }

    public boolean getSettingMenuEnable(int index) {
        return getCurrentSettingMenu().getMenu(index).enable;
    }

    public boolean setSetting(int settingIndex, int settingValue) {
        return this.mSettingController.setSetting(settingIndex, settingValue);
    }

    public boolean setSetting(String key, String value) {
        return this.mSettingController.setSetting(key, value);
    }

    public boolean setSetting(String key, String value, boolean needSave) {
        return this.mSettingController.setSetting(key, value, needSave);
    }

    public void initSettingMenu() {
        this.mSettingController.initSettingMenu();
    }

    public int getQfIndex(String key) {
        return this.mSettingController.getSetting().getQfIndex(key);
    }

    public String getCurrentMenuKey() {
        return this.mSettingController.getCurrentSettingMenu().getCurrentMenu().getKey();
    }

    public String getIndexMenuKey(int menuPosition) {
        return this.mSettingController.getCurrentSettingMenu().getMenu(menuPosition).getKey();
    }

    public String setLiveeffectLimit() {
        return this.mSettingController.setLiveeffectLimit();
    }

    public void setSettingForDualCamera(boolean value) {
        this.mSettingController.setSettingForDualCamera(value);
    }

    public boolean isQuickFunctionList(int index) {
        return index >= 0 && index < 5;
    }

    public void resetQFIndex() {
        this.mSettingController.getSetting().clearQFIndex();
    }

    public void setTimeMachineLimit(boolean set) {
        this.mSettingController.setTimeMachineLimit(set);
    }

    public void setFocusMode(String value) {
        this.mSettingController.setSetting(Setting.KEY_FOCUS, value);
    }

    public void setSettingParameter(int key, int value) {
        this.mSettingController.setSetting(key, value);
    }

    public void applyCameraChange() {
        this.mSettingController.applyCameraChange();
    }

    public boolean checkAndCloseChildView() {
        return this.mSettingController.checkAndCloseChildView();
    }

    public void setAllPreferenceApply(int which, String key, String value) {
        if (this.mSettingController != null) {
            this.mSettingController.setAllPreferenceApply(which, key, value);
        }
    }

    public boolean isStorageControllerInitialized() {
        if (this.mStorageController == null || !this.mStorageController.isInitialized()) {
            return false;
        }
        return true;
    }

    public boolean isStorageFull() {
        if (this.mStorageController == null || this.mStorageController.getStorageState() == 2) {
            return true;
        }
        return false;
    }

    public int getStorageState() {
        return this.mStorageController.getStorageState();
    }

    public void setCurrentStorage(int storageType) {
        this.mStorageController.setCurrentStorage(storageType);
    }

    public int getCurrentStorage() {
        return this.mStorageController.getCurrentStorage();
    }

    public String getCurrentStorageDirectory() {
        return this.mStorageController.getCurrentStorageDirectory();
    }

    public String getTimeMachineStorageDirectory() {
        return this.mStorageController.getTimeMachineStorageDirectory();
    }

    public boolean isExternalStorageRemoved() {
        return this.mStorageController.isExternalStorageRemoved();
    }

    public void showStorageHint(int storageState) {
        this.mStorageController.showStorageHint(storageState);
    }

    public long getFreeSpace() {
        return this.mStorageController.getFreeSpace();
    }

    public long getAvailablePictureCount() {
        return this.mStorageController.getAvailablePictureCount();
    }

    public String getStorageBucketId() {
        return this.mStorageController == null ? null : this.mStorageController.getBucketId();
    }

    public String getStorageBucketId(int storageType) {
        return this.mStorageController == null ? null : this.mStorageController.getBucketId(storageType);
    }

    public void addQuickButton(Context context, int buttonType, int location) {
        this.mQuickButtonController.addQuickButton(context, buttonType, location);
    }

    public void addQuickButton(Context context, int buttonType, int orientation, int location) {
        this.mQuickButtonController.addQuickButton(context, buttonType, orientation, location);
    }

    public void setQuickButtonVisible(int type, int visible, boolean animation) {
        this.mQuickButtonController.setVisible(type, visible, animation);
    }

    public void removeQuickButtonAll() {
        this.mQuickButtonController.removeQuickButtonAll();
    }

    public void removeQuickButton(int buttonType) {
        this.mQuickButtonController.removeQuickButton(buttonType);
    }

    public void setQuickButtonMenuEnable(boolean enable, boolean dimByEnable) {
        this.mQuickButtonController.setAllMenuEnable(enable, dimByEnable);
    }

    public void setQuickButtonMenuEnable(int buttonType, boolean enable, boolean dimByEnable) {
        this.mQuickButtonController.setMenuEnable(buttonType, enable, dimByEnable);
    }

    public void setQuickButtonForcedDisable(boolean set) {
        this.mQuickButtonController.setQuickButtonForcedDisable(set);
    }

    public void changeButtonResource(int type, int resId) {
        this.mQuickButtonController.changeButtonResource(type, resId);
    }

    public void changeButtonResource(int type, int resId, int descriptionId) {
        this.mQuickButtonController.changeButtonResource(type, resId, descriptionId);
    }

    public void setButtonRemainRefresh() {
        this.mQuickButtonController.setButtonRemainRefresh();
    }

    public void setButtonRemainEnabled(int buttonType, boolean enable) {
        this.mQuickButtonController.setButtonRemainEnabled(buttonType, enable);
    }

    public void setButtonRemainEnabled(int buttonType, boolean enable, boolean useDefaultIcon) {
        this.mQuickButtonController.setButtonRemainEnabled(buttonType, enable, useDefaultIcon);
    }

    public void refreshQuickButton() {
        this.mQuickButtonController.refreshQuickButton();
    }

    public void showDefaultQuickButton(boolean show) {
        if (show) {
            boolean addFlash;
            boolean needModeButton = true;
            if ((getApplicationMode() == 0 && CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) || (getApplicationMode() == 1 && CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(getSettingValue(Setting.KEY_VIDEO_RECORD_MODE)))) {
                needModeButton = false;
            }
            boolean isIAon = CameraConstants.SMART_MODE_ON.equals(getSettingValue(Setting.KEY_SMART_MODE));
            if (getCameraId() != 0 || FunctionProperties.isNoneFlashModel()) {
                addFlash = false;
            } else {
                addFlash = true;
            }
            SparseIntArray buttonDefine = getButtonDefine(needModeButton, isIAon, addFlash);
            if (buttonDefine != null) {
                int i = 0;
                while (i < 12) {
                    int butLoc = buttonDefine.get(i, -1);
                    if (butLoc > -1) {
                        addQuickButton(getApplicationContext(), i, butLoc);
                        if (addFlash && i == 10) {
                            setPreferenceMenuOnlyEnable(Setting.KEY_FLASH, getFlashEnableForShotMode());
                        }
                    } else {
                        removeQuickButton(i);
                    }
                    i++;
                }
                if ((getApplicationMode() == 0 && isAttachMode()) || isMMSIntent()) {
                    setQuickButtonMenuEnable(7, false, true);
                    setButtonRemainEnabled(7, false);
                }
                setQuickButtonVisible(100, 0, false);
                buttonDefine.clear();
                return;
            }
            return;
        }
        removeQuickButtonAll();
    }

    private SparseIntArray getButtonDefine(boolean needModeButton, boolean isIAon, boolean addFlash) {
        SparseIntArray buttonDefine = new SparseIntArray();
        if (needModeButton && isIAon && addFlash) {
            if (ModelProperties.getCarrierCode() == 6) {
                buttonDefine.put(10, 0);
                buttonDefine.put(9, 1);
                buttonDefine.put(7, 2);
                buttonDefine.put(8, 3);
            } else {
                buttonDefine.put(10, 5);
                buttonDefine.put(9, 6);
                buttonDefine.put(7, 7);
            }
        } else if (needModeButton && isIAon && !addFlash) {
            if (getApplicationMode() == 0 && FunctionProperties.isSupportLightFrame()) {
                buttonDefine.put(11, 5);
                buttonDefine.put(9, 6);
                buttonDefine.put(7, 7);
            } else {
                buttonDefine.put(9, 1);
                buttonDefine.put(7, 2);
            }
        } else if (needModeButton && !isIAon && addFlash) {
            buttonDefine.put(10, 0);
            buttonDefine.put(9, 1);
            buttonDefine.put(7, 2);
            buttonDefine.put(8, 3);
        } else if (!needModeButton || isIAon || addFlash) {
            if (!needModeButton && isIAon && addFlash) {
                buttonDefine.put(10, 1);
                buttonDefine.put(9, 2);
            } else if (!needModeButton && isIAon && !addFlash) {
                buttonDefine.put(9, 6);
            } else if (!needModeButton && !isIAon && addFlash) {
                buttonDefine.put(10, 5);
                buttonDefine.put(9, 6);
                buttonDefine.put(8, 7);
            } else if (!(needModeButton || isIAon || addFlash)) {
                if (getApplicationMode() == 0 && FunctionProperties.isSupportLightFrame()) {
                    buttonDefine.put(11, 5);
                    buttonDefine.put(9, 6);
                    buttonDefine.put(8, 7);
                } else {
                    buttonDefine.put(9, 1);
                    buttonDefine.put(8, 2);
                }
            }
        } else if (getApplicationMode() == 0 && FunctionProperties.isSupportLightFrame()) {
            buttonDefine.put(11, 0);
            buttonDefine.put(9, 1);
            buttonDefine.put(7, 2);
            buttonDefine.put(8, 3);
        } else {
            buttonDefine.put(9, 5);
            buttonDefine.put(7, 6);
            buttonDefine.put(8, 7);
        }
        if (!ModelProperties.isSupportFrontCameraModel()) {
            int i;
            int startIndex;
            if (buttonDefine.get(9) != 0) {
                buttonDefine.removeAt(buttonDefine.indexOfKey(9));
            }
            int tempArraySize = buttonDefine.size();
            int arraySize = buttonDefine.size();
            int[] tempKey = new int[tempArraySize];
            int[] tempValue = new int[tempArraySize];
            for (i = 0; i < arraySize; i++) {
                tempKey[i] = buttonDefine.keyAt(i);
                tempValue[i] = buttonDefine.get(tempKey[i]);
            }
            Arrays.sort(tempValue);
            if (arraySize % 2 != 0) {
                startIndex = ((5 - arraySize) / 2) + 4;
            } else {
                startIndex = ((4 - arraySize) / 2) + 0;
            }
            i = 0;
            int startIndex2 = startIndex;
            while (i < arraySize) {
                for (int j = 0; j < arraySize; j++) {
                    if (tempValue[i] == buttonDefine.valueAt(j)) {
                        startIndex = startIndex2 + 1;
                        buttonDefine.put(buttonDefine.keyAt(j), startIndex2);
                        break;
                    }
                }
                startIndex = startIndex2;
                i++;
                startIndex2 = startIndex;
            }
        }
        return buttonDefine;
    }

    public boolean checkPreviewPanelController() {
        return this.mPreviewPanelController != null;
    }

    public void setMainButtonEnable() {
        this.mPreviewPanelController.setMainButtonEnable();
    }

    public void setMainButtonEnable(String lockKey) {
        this.mPreviewPanelController.setMainButtonEnable(lockKey);
    }

    public void setMainButtonDisable() {
        this.mPreviewPanelController.setMainButtonDisable();
    }

    public void setMainButtonVisible(boolean visible) {
        this.mPreviewPanelController.setMainButtonVisible(visible);
    }

    public void enableCommand(boolean enable) {
        this.mPreviewPanelController.enableCommand(enable);
    }

    public boolean isShutterButtonEnable() {
        return this.mPreviewPanelController.isShutterButtonEnable();
    }

    public boolean snapshotOnIdle() {
        return this.mPreviewPanelController.snapshotOnIdle();
    }

    public Runnable getSnapshotRunnable() {
        return this.mPreviewPanelController.mDoSnapRunnable;
    }

    public void setShutterButtonImage(boolean buttonEnable, int degree) {
        this.mPreviewPanelController.setShutterButtonImage(buttonEnable, degree);
    }

    public void setMainBarAlpha(int value) {
        this.mPreviewPanelController.setMainBarAlpha(value);
    }

    public int getMainBarAlphaValue() {
        return this.mPreviewPanelController.getMainBarAlphaValue();
    }

    public boolean isShutterButtonLongKey() {
        return this.mPreviewPanelController.isShutterButtonLongKey();
    }

    public boolean isCameraKeyLongPressed() {
        return this.isCameraKeyLongPressed;
    }

    public void setCameraKeyLongPressed(boolean set) {
        this.isCameraKeyLongPressed = set;
    }

    public boolean isPressedShutterButton() {
        return this.mPreviewPanelController.isPressedShutterButton();
    }

    public boolean isShutterFocusLongKey() {
        return this.mPreviewPanelController.isShutterFocusLongKey();
    }

    public void setShutterFocusLongKey(boolean set) {
        this.mPreviewPanelController.setShutterFocusLongKey(set);
    }

    public void setSwitcherVisible(boolean visible) {
        this.mPreviewPanelController.setSwitcherVisible(visible);
    }

    public void set3DSwitchVisible(boolean visible) {
        this.mPreviewPanelController.set3DSwitchVisible(visible);
    }

    public void set3DSwitchImage() {
        this.mPreviewPanelController.set3DSwitchImage();
    }

    public boolean isSwitcherLeverPressed() {
        return this.mPreviewPanelController.isSwitcherLeverPressed();
    }

    public void setLastThumb(Uri uri, boolean isPicture) {
        this.mPreviewPanelController.setLastThumb(uri, isPicture);
    }

    public void updateThumbnailButtonVisibility() {
        this.mPreviewPanelController.updateThumbnailButtonVisibility();
    }

    public void setThumbnailButtonVisibility(int visible) {
        this.mPreviewPanelController.setThumbnailButtonVisibility(visible);
    }

    public void updateThumbnailButton() {
        this.mPreviewPanelController.updateThumbnailButton();
    }

    public Uri getMostRecentThumbnailUri(boolean isUseLinkedThumbnailList, int inclusion) {
        return this.mPreviewPanelController.getMostRecentThumbnailUri(isUseLinkedThumbnailList, inclusion);
    }

    public boolean snapshotOnContinuousFocus() {
        return this.mPreviewPanelController.snapshotOnContinuousFocus();
    }

    public boolean getCafOnGoing() {
        return this.mFocusController.getCafOnGoing();
    }

    public void setCafOnGoing(boolean cafOnGoing) {
        this.mFocusController.setCafOnGoing(cafOnGoing);
    }

    public void showSubButtonInit(boolean updateThumb) {
        this.mPreviewPanelController.showSubButtonInit(updateThumb);
    }

    public void setSubButton(int loc, int resId) {
        this.mPreviewPanelController.setSubButton(loc, resId);
    }

    public void setGalleryLaunching(boolean isLaunching) {
        this.mPreviewPanelController.setGalleryLaunching(isLaunching);
    }

    public boolean isGalleryLaunching() {
        return this.mPreviewPanelController.isGalleryLaunching();
    }

    public Uri getThumbnailControllerUri() {
        return this.mPreviewPanelController.getThumbController().getUri();
    }

    public void getThumbnailAndUpdateButton() {
        this.mPreviewPanelController.getThumbnailAndUpdateButton();
    }

    public void setTimeMachineComplete(boolean set) {
        this.mSetTimeMachineComplete = set;
    }

    public boolean getTimeMachineComplete() {
        return this.mSetTimeMachineComplete;
    }

    public void showPreviewPanelLiveSnapShotButton() {
        this.mPreviewPanelController.showLiveSnapshotButton();
    }

    public void hidePreviewPanelLiveSnapshotButton() {
        this.mPreviewPanelController.hideLiveSnapshotButton();
    }

    public void setLastPictureThumb(byte[] data, Uri uri, boolean isPicture) {
        this.mPreviewPanelController.setLastPictureThumb(data, uri, isPicture);
    }

    public void onShutterButtonClick(ShutterButton button) {
        if (this.mPreviewPanelController != null) {
            this.mPreviewPanelController.onShutterButtonClick(button);
        }
    }

    public void hidePreviewPanelController() {
        if (this.mPreviewPanelController != null) {
            this.mPreviewPanelController.setPreviewPanelVisibility(false);
        }
    }

    public void showPreviewPanelController() {
        if (this.mPreviewPanelController != null) {
            this.mPreviewPanelController.setPreviewPanelVisibility(true);
        }
    }

    public void clearSettingMenuAndSubMenu() {
        if (this.mPreviewPanelController != null) {
            this.mPreviewPanelController.clearSettingMenuAndSubMenu();
        }
    }

    public void closeGalleryQuickView(boolean deleteImage) {
        if (this.mPreviewPanelController != null) {
            this.mPreviewPanelController.closeGalleryQuickView(deleteImage);
        }
    }

    public boolean isShowingQuickView() {
        long duration;
        long autoReviewDuration;
        String autoReview = CameraConstants.SMART_MODE_OFF;
        if (getApplicationMode() == 0) {
            autoReview = getSettingValue(Setting.KEY_CAMERA_AUTO_REVIEW);
        } else if (getApplicationMode() == 1) {
            autoReview = getSettingValue(Setting.KEY_VIDEO_AUTO_REVIEW);
        }
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
        if (!(autoReviewDuration == 0 || isAttachIntent())) {
            View mQuickViewThumbLayout = findViewById(R.id.gallery_quick_window);
            if (mQuickViewThumbLayout != null && mQuickViewThumbLayout.getVisibility() == 0) {
                removeCallbacks(this.mPreviewPanelController.mHideQuickViewRunable);
                this.mPreviewPanelController.mAutoReviewBlockTouch = false;
                this.mPreviewPanelController.showGalleryQuickViewAnimation(false, false);
                return true;
            }
        }
        return false;
    }

    public boolean checkAutoReviewForQuickView() {
        long duration;
        long autoReviewDuration;
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
        if (autoReviewDuration == 0 || isAttachIntent()) {
            return false;
        }
        setGoingAutoReviewForQuickView(true);
        setInCaptureProgress(false);
        enableInput(false);
        String shotMode = getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
        Bundle bundle;
        if (isTimeMachineModeOn()) {
            startPreview(null, false);
            bundle = new Bundle();
            bundle.putBoolean("fromJpegCallback", true);
            doCommandDelayed(Command.DISPLAY_PREVIEW, bundle, 0);
        } else if (CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS.equals(shotMode)) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Mediator.this.removePostRunnable(this);
                    Mediator.this.setShutterButtonImage(true, Mediator.this.getOrientationDegree());
                }
            });
            startPreview(null, false);
            bundle = new Bundle();
            bundle.putBoolean("from JpegCallback Full Frame Continuous shot", true);
            doCommandDelayed(Command.DISPLAY_PREVIEW, bundle, 0);
            ArrayList<Uri> imageListUri = getImageListUri();
            if (imageListUri != null && imageListUri.size() > 0) {
                imageListUri.clear();
            }
        } else {
            doCommandUi(Command.ON_DELAY_OFF);
        }
        postOnUiThread(new Runnable() {
            public void run() {
                Mediator.this.removePostRunnable(this);
                if (Mediator.this.mPreviewPanelController != null) {
                    Mediator.this.setGoingAutoReviewForQuickView(false);
                    Mediator.this.mPreviewPanelController.showGalleryQuickViewWindow(false, autoReviewDuration);
                }
            }
        });
        return true;
    }

    public boolean showGalleryQuickViewWindow(boolean useLongKey, long duration) {
        if (this.mPreviewPanelController != null) {
            return this.mPreviewPanelController.showGalleryQuickViewWindow(useLongKey, duration);
        }
        return false;
    }

    public boolean isSwitcherLeverEnable() {
        if (this.mPreviewPanelController != null) {
            return this.mPreviewPanelController.isSwitcherLeverEnable();
        }
        return false;
    }

    public void setSwithcerEnable(boolean enable) {
        if (this.mPreviewPanelController != null) {
            this.mPreviewPanelController.setSwithcerEnable(enable);
        }
    }

    public void resetSwitcherLever() {
        if (this.mPreviewPanelController != null) {
            this.mPreviewPanelController.resetSwitcherLever();
        }
    }

    public void updateAllBars(int mBarType, int value) {
        switch (mBarType) {
            case LGKeyRec.EVENT_INVALID /*0*/:
            case LGKeyRec.EVENT_STOPPED /*4*/:
                if (this.mZoomController != null) {
                    this.mZoomController.updateAllBars(value);
                }
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
                if (this.mBrightnessController != null) {
                    this.mBrightnessController.updateAllBars(value);
                }
                if (this.mBrightnessControllerExpand != null) {
                    this.mBrightnessControllerExpand.updateAllBars(value);
                }
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
                if (this.mBeautyshotController != null) {
                    this.mBeautyshotController.updateAllBars(value);
                }
            case LGKeyRec.EVENT_STARTED /*3*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
                if (this.mCamera3dDepthController != null) {
                    this.mCamera3dDepthController.updateAllBars(value);
                }
            case LGKeyRec.EVENT_NEED_MORE_AUDIO /*8*/:
                if (this.mManualFocusController != null) {
                    this.mManualFocusController.updateAllBars(value);
                }
            default:
        }
    }

    public void rotateSettingBar(int mBarType, int degree) {
        switch (mBarType) {
            case LGKeyRec.EVENT_STOPPED /*4*/:
                this.mSettingController.rotateSettingZoom(degree);
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                this.mSettingController.rotateSettingBrightness(degree);
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
                this.mSettingController.rotateSettingBeautyShot(degree);
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
                this.mSettingController.rotateSettingCamera3dDepth(degree);
            default:
        }
    }

    public void resetBarController(int mBarType) {
        switch (mBarType) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                if (getCameraId() == 0) {
                    this.mZoomController.reset();
                }
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                this.mBrightnessController.reset();
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                if (FunctionProperties.getFunctionFrontCameraBeautyShot() == 1 && getApplicationMode() == 0) {
                    this.mBeautyshotController.reset();
                }
            case LGKeyRec.EVENT_STARTED /*3*/:
                if (ModelProperties.is3dSupportedModel() && getCameraDimension() == 1) {
                    this.mCamera3dDepthController.reset();
                }
            case LGKeyRec.EVENT_NEED_MORE_AUDIO /*8*/:
                this.mManualFocusController.reset();
            default:
        }
    }

    public void resetController() {
        resetZoomController();
        resetBrightnessController();
        resetBeautyshotController();
        resetManualFocusController();
        reset3dDepthController();
        if (MultimediaProperties.isLiveEffectSupported() && getApplicationMode() == 1) {
            this.mQuickFunctionController.setLimitationToLiveeffect(true);
        }
        this.mQuickFunctionController.reset();
    }

    public void resetZoomController() {
        resetBarController(0);
    }

    public void resetBrightnessController() {
        resetBarController(1);
    }

    public void resetBeautyshotController() {
        resetBarController(2);
    }

    public void resetManualFocusController() {
        resetBarController(8);
    }

    public void reset3dDepthController() {
        resetBarController(3);
    }

    public void refreshZoomSettingBars() {
        this.mZoomController.refreshSettingBars();
    }

    public void refreshBrightnessSettingBars() {
        this.mBrightnessController.refreshSettingBars();
    }

    public void refreshBrightnessExpandSettingBars() {
        this.mBrightnessControllerExpand.refreshSettingBars();
    }

    public void refreshBeautyshotSettingBars() {
        this.mBeautyshotController.refreshSettingBars();
    }

    public void refresh3dDepthSettingBars() {
        this.mCamera3dDepthController.refreshSettingBars();
    }

    public void showSettingBrightnessControl(boolean useAnim) {
        this.mBrightnessController.showSettingBrightnessControl(useAnim);
    }

    public void showSettingBeautyShotControl(boolean useAnim) {
        this.mBeautyshotController.showSettingBeautyShotControl(useAnim);
    }

    public void showSettingZoomControl(boolean useAnim) {
        this.mZoomController.showSettingZoomControl(useAnim);
    }

    public void showSetting3dDepthControl(boolean useAnim) {
        this.mCamera3dDepthController.showSetting3dDepthControl(useAnim);
    }

    public void showZoomController(final boolean show) {
        runOnUiThread(new Runnable() {
            public void run() {
                Mediator.this.removePostRunnable(this);
                Mediator.this.mZoomController.showControl(show);
            }
        });
    }

    public void showBrightnessController(final boolean show) {
        runOnUiThread(new Runnable() {
            public void run() {
                Mediator.this.removePostRunnable(this);
                Mediator.this.mBrightnessController.showControl(show);
            }
        });
    }

    public void showBeautyshotController(final boolean show) {
        runOnUiThread(new Runnable() {
            public void run() {
                Mediator.this.removePostRunnable(this);
                Mediator.this.mBeautyshotController.showControl(show);
                if (show) {
                    Mediator.this.setSubMenuMode(15);
                }
            }
        });
    }

    public void showManualFocusController(final boolean show) {
        runOnUiThread(new Runnable() {
            public void run() {
                Mediator.this.removePostRunnable(this);
                Mediator.this.mManualFocusController.showControl(show);
            }
        });
    }

    public void show3dDepthController(boolean show) {
        this.mCamera3dDepthController.showControl(show);
    }

    public void resetDisplayTimeoutZoom() {
        this.mZoomController.resetDisplayTimeout();
    }

    public void resetDisplayTimeoutBrightness() {
        this.mBrightnessController.resetDisplayTimeout();
    }

    public void resetDisplayTimeoutBeautyshot() {
        this.mBeautyshotController.resetDisplayTimeout();
    }

    public void resetDisplayTimeoutManualFocus() {
        this.mManualFocusController.resetDisplayTimeout();
    }

    public void resetDisplayTimeout3dDepth() {
        this.mCamera3dDepthController.resetDisplayTimeout();
    }

    public void refreshZoomController() {
        this.mZoomController.refreshController();
    }

    public void refreshBrightnessController() {
        this.mBrightnessController.refreshController();
    }

    public void refreshBeautyshotController() {
        this.mBeautyshotController.refreshController();
    }

    public void refreshManualFocusController() {
        this.mManualFocusController.refreshController();
    }

    public void refresh3dDepthController() {
        this.mCamera3dDepthController.refreshController();
    }

    public int getZoomCursorMaxStep() {
        return this.mZoomController.getCursorMaxStep();
    }

    public float getZoomMaxValue() {
        return this.mZoomController.getZoomMaxValue();
    }

    public void setZoomMaxValue(float value) {
        this.mZoomController.setZoomMaxValue(value);
    }

    public void setZoomRatio(float value) {
        this.mZoomController.setZoomRatio(value);
    }

    public float getZoomRatio() {
        return this.mZoomController.getZoomRatio();
    }

    public int getZoomBarValue() {
        return this.mZoomController.getZoomBarValue();
    }

    public void showChildCustomView(boolean useAnim) {
        CamLog.d(FaceDetector.TAG, "temp showChildCustomView " + useAnim);
        this.mZoomController.showSettingZoomControl(useAnim);
        this.mBrightnessController.showSettingBrightnessControl(useAnim);
        this.mBeautyshotController.showSettingBeautyShotControl(useAnim);
        if (ModelProperties.is3dSupportedModel()) {
            this.mCamera3dDepthController.showSetting3dDepthControl(useAnim);
        }
    }

    public void hideChildCustomView(boolean useAnim) {
        CamLog.d(FaceDetector.TAG, "temp hideChildCustomView " + useAnim);
        this.mZoomController.hideSettingZoomControl(useAnim);
        this.mBrightnessController.hideSettingBrightnessControl(useAnim);
        this.mBeautyshotController.hideSettingBeautyShotControl(useAnim);
        if (ModelProperties.is3dSupportedModel()) {
            this.mCamera3dDepthController.hideSettingDepth3DControl(useAnim);
        }
    }

    public void clearSettingBarControll() {
        if (this.mZoomController != null) {
            this.mZoomController.clearSettingZoom();
        }
        if (this.mBeautyshotController != null) {
            this.mBeautyshotController.clearSettingBeautyshotBar();
        }
        if (this.mBrightnessController != null) {
            this.mBrightnessController.clearSettingBrightnessBar();
        }
        if (this.mCamera3dDepthController != null) {
            this.mCamera3dDepthController.clearSettingDepth3DBar();
        }
    }

    public void updateZoomBar(int cursorStep, int factor, boolean scaleEnd) {
        if (this.mZoomController != null && this.mZoomController.getZoomBar() != null) {
            this.mZoomController.getZoomBar().updateBar(factor * cursorStep, true, false, scaleEnd);
        }
    }

    public void showBeautyShotBarForNewUx(boolean show) {
        if (checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY) || checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY)) {
            showBeautyshotController(show);
        }
    }

    public RelativeLayout initSettingBrightnessBar() {
        return this.mBrightnessControllerExpand.initSettingBrightnessBar();
    }

    public boolean isQuickFunctionSettingControllerShowing() {
        return this.mQuickFunctionSettingController.isVisible();
    }

    public void displayQuickFunctionSettingView(String key) {
        this.mQuickFunctionSettingController.displaySettingView(key);
    }

    public void removeQuickFunctionSettingView() {
        this.mQuickFunctionSettingController.removeSettingView();
    }

    public void qflSettingAnimation(View aniView, boolean show) {
        this.mQuickFunctionSettingController.qflSettingAnimation(aniView, show);
    }

    public boolean checkManualFocusController() {
        return this.mManualFocusController != null;
    }

    public int getManualFocusValue() {
        return this.mManualFocusController.getManualFocusValue();
    }

    public void setManualFocusValue(int value) {
        this.mManualFocusController.setManualFocusValue(value);
    }

    public boolean isManualFocusBarVisible() {
        return this.mManualFocusController.isBarVisible();
    }

    public void quickFunctionControllerInitMenu() {
        this.mQuickFunctionController.initMenu();
    }

    public void quickFunctionAllMenuSelected(boolean selected) {
        this.mQuickFunctionController.setAllMenuSelected(selected);
    }

    public void quickFunctionControllerRefresh(boolean show) {
        this.mQuickFunctionController.quickFunctionControllerRefresh(!CameraConstants.SMART_MODE_ON.equals(getSettingValue(Setting.KEY_SMART_MODE)));
    }

    public void setQuickFunctionAllMenuEnabled(boolean enabled, boolean dimByEnable) {
        this.mQuickFunctionController.setAllMenuEnabled(enabled, dimByEnable);
    }

    public boolean[] getQFLMenuEnable() {
        return this.mQuickFunctionController.getQFLMenuEnable();
    }

    public void setQFLMenuEnabled(int menuIndex, boolean enabled) {
        this.mQuickFunctionController.setMenuEnabled(menuIndex, enabled);
    }

    public void setQFLMenuSelected(int menuIndex, boolean selected) {
        this.mQuickFunctionController.setMenuSelected(menuIndex, selected);
    }

    public void initQuickFunctionEnabled() {
        this.mQuickFunctionController.initEnabled();
    }

    public void clearQuickFunctionSubMenu() {
        this.mQuickFunctionController.clearSubMenu();
    }

    public void showQuickFunctionController() {
        this.mQuickFunctionController.show();
    }

    public void hideQuickFunctionController() {
        this.mQuickFunctionController.hide();
    }

    public ListPreference getQuickFunctionControllerMenuTag(int index) {
        View quickFunctionControllerMenuView = getQuickFunctionControllerMenuView(index);
        if (quickFunctionControllerMenuView == null) {
            return null;
        }
        return (ListPreference) quickFunctionControllerMenuView.getTag();
    }

    public View getQuickFunctionControllerMenuView(int index) {
        return this.mQuickFunctionController.getMenuView(index);
    }

    public String getSelectedQuickFunctionMenuKey() {
        return this.mQuickFunctionController.getSelectedMenuKey();
    }

    public void setQuickFunctionControllerAllMenuIcons() {
        this.mQuickFunctionController.setAllMenuIcons();
    }

    public void startSubMenuRotation(int degree) {
        this.mQuickFunctionController.startSubMenuRotation(degree);
    }

    public void startLiveEffectDrawerSubMenuRotation(int degree) {
        this.mQuickFunctionController.startLiveEffectDrawerSubMenuRotation(degree);
    }

    public void startPIPFrameSubMenuRotation(int degree) {
        this.mQuickFunctionController.startPIPFrameSubMenuRotation(degree);
    }

    public boolean setPIPMask(int pipFrameSelectedMenu) {
        if (this.mPreviewController == null || this.mPreviewController.getPIPController() == null) {
            return false;
        }
        return this.mPreviewController.getPIPController().setPIPMask(pipFrameSelectedMenu);
    }

    public int getCurrentPIPMask() {
        return PIPController.mCurrentMaskMenu;
    }

    public void setCurrentPIPMask(int maskIndex) {
        PIPController.mCurrentMaskMenu = maskIndex;
    }

    public void setDefaultPIPMask() {
        if (this.mPreviewController != null && this.mPreviewController.getPIPController() != null) {
            this.mPreviewController.getPIPController().setDefaultPIPMask();
        }
    }

    public void setPIPRotate(int degree) {
        if (this.mPreviewController != null && this.mPreviewController.getPIPController() != null) {
            this.mPreviewController.getPIPController().setPIPRotate(degree);
        }
    }

    public boolean isPIPFrameDrawerOpened() {
        MultiDirectionSlidingDrawer pipFrameDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.pip_frame_sliding_drawer_menu_slide);
        if (pipFrameDrawer == null) {
            return false;
        }
        return pipFrameDrawer.isOpened();
    }

    public boolean isPIPFrameDrawerShown() {
        return ((RelativeLayout) findViewById(R.id.pip_frame_sliding_drawer_menu_view)).isShown();
    }

    public boolean isLiveEffectDrawerOpened() {
        MultiDirectionSlidingDrawer liveEffectDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.live_effect_sliding_drawer_menu_slide);
        if (liveEffectDrawer == null) {
            return false;
        }
        return liveEffectDrawer.isOpened();
    }

    public boolean isLiveEffectDrawerShown() {
        return ((RelativeLayout) findViewById(R.id.live_effect_sliding_drawer_menu_view)).isShown();
    }

    public void setQuickFunctionControllerMenuIcon(int menuIndex, int iconIndex) {
        this.mQuickFunctionController.setMenuIcon(menuIndex, iconIndex);
    }

    public void setQuickFunctionControllerMenu(int menuIndex) {
        this.mQuickFunctionController.setMenu(menuIndex);
    }

    public void setQuickFunctionControllerMmsLimit() {
        this.mQuickFunctionController.setMmsLimit();
    }

    public void setQuickFunctionControllerMmsLimit(boolean mmsVideo) {
        this.mQuickFunctionController.setMmsLimit(mmsVideo);
    }

    public void qflMenuAnimation(boolean show, int duration, AnimationListener listener) {
        this.mQuickFunctionController.qflMenuAnimation(show, duration, listener);
    }

    public void setQuickFunctionControllerVisible(boolean show) {
        if (show) {
            this.mQuickFunctionController.qflShow();
        } else {
            this.mQuickFunctionController.qflHide();
        }
    }

    public void restoreLiveEffectSubMenu() {
        this.mQuickFunctionController.restoreLiveEffectSubMenu();
    }

    public void setLimitationToLiveeffect(boolean beSet) {
        this.mQuickFunctionController.setLimitationToLiveeffect(beSet);
    }

    public void setCheckClickTime(long time) {
        this.mQuickFunctionController.setCheckClickTime(time);
    }

    public boolean setCheckToggleTime(int usage) {
        return this.mQuickFunctionController.setCheckToggleTime(usage);
    }

    public void setQuickFunctionMenuForcedDisable(boolean set) {
        this.mQuickFunctionController.setQuickFunctionMenuForcedDisable(set);
    }

    public ListPreference getQuickFunctionDragControllerMenuTag(int index) {
        View quickFunctionDragView = this.mQuickFunctionDragController.getDragView(index);
        if (quickFunctionDragView == null) {
            return null;
        }
        return (ListPreference) quickFunctionDragView.getTag();
    }

    public boolean isQuickFunctionDragControllerVisible() {
        return this.mQuickFunctionDragController.isVisible();
    }

    public void showQuickFunctionDragController() {
        this.mQuickFunctionDragController.show();
    }

    public void hideQuickFunctionDragController(boolean animation) {
        this.mQuickFunctionDragController.hide(animation);
    }

    public void setQuickFunctionDragControllerSelectIndex(int index) {
        this.mQuickFunctionDragController.setSelectIndex(index);
    }

    public void playVoiceCommandSound(int soundIndex) {
        if (this.mSoundController != null) {
            this.mSoundController.stopVoiceCommandSound();
            this.mSoundController.playVoiceCommandSound(soundIndex);
        }
    }

    public void stopVoiceCommandSound() {
        if (this.mSoundController != null) {
            this.mSoundController.stopVoiceCommandSound();
        }
    }

    public void playRecordingSound(boolean start) {
        if (this.mSoundController != null) {
            this.mSoundController.playRecordingSound(start);
        }
    }

    public void playAFSound(boolean seccess) {
        if (this.mSoundController != null) {
            this.mSoundController.playAFSound(seccess);
        }
    }

    public void playTimerSound(int time) {
        if (this.mSoundController != null) {
            this.mSoundController.playTimerSound(time);
        }
    }

    public void playShutterSound() {
        if (this.mSoundController != null) {
            this.mSoundController.playShutterSound();
        }
    }

    public void changeShutterSound(int index) {
        if (this.mSoundController != null) {
            this.mSoundController.changeShutterSound(index);
        }
    }

    public void playContinuousShutterSound() {
        if (this.mSoundController != null) {
            this.mSoundController.playContinuousShutterSound();
        }
    }

    public void playBurstShotShutterSound(boolean repeat) {
        if (this.mSoundController != null) {
            this.mSoundController.playBurstShotShutterSound(repeat);
        }
    }

    public void playFreePanoramaShutterSound() {
        if (this.mSoundController != null) {
            this.mSoundController.playFreePanoramaShutterSound();
        }
    }

    public void stopSoundContinuous() {
        if (this.mSoundController != null) {
            this.mSoundController.stopSoundContinuous();
        }
    }

    public void stopBurstShotSound() {
        if (this.mSoundController != null) {
            CamLog.d(FaceDetector.TAG, "stopBurstShotSound");
            this.mSoundController.stopSoundBurstShot();
        }
    }

    public void playClearShotShutterSound(boolean repeat) {
        if (this.mSoundController != null) {
            this.mSoundController.playClearShotShutterSound(repeat);
        }
    }

    public void stopClearShotSound() {
        if (this.mSoundController != null) {
            this.mSoundController.stopClearShotSound();
        }
    }

    public boolean checkCameraShutterSoundLoaded() {
        if (this.mSoundController != null) {
            return this.mSoundController.checkCameraShutterSoundLoaded();
        }
        return false;
    }

    public Dialog onCreateDialog(int id, Bundle args) {
        return this.mDialogController.onCreateDialog(id, args);
    }

    public void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        this.mDialogController.onPrepareDialog(id, dialog, args);
    }

    public void showDialogPopup(final int dialogId) {
        runOnUiThread(new Runnable() {
            public void run() {
                Mediator.this.removePostRunnable(this);
                Mediator.this.mDialogController.showDialogPopup(dialogId);
            }
        });
    }

    public void onDismissRotateDialog() {
        this.mDialogController.onDismissRotateDialog();
    }

    public boolean isRotateDialogVisible() {
        return this.mDialogController.isRotateDialogVisible();
    }

    public Dialog getCurrentDialog() {
        return this.mDialogController.getCurrentDialog();
    }

    public int getDialogID() {
        return this.mDialogController.getDialogID();
    }

    public void showCameraErrorAndFinish() {
        CamLog.e(FaceDetector.TAG, "Error!!! showCameraErrorAndFinish");
        resetScreenTimeout();
        setErrorOccuredAndFinish(true);
        this.mDialogController.showDialogPopup(18);
    }

    public void showCameraStoppedAndFinish() {
        CamLog.e(FaceDetector.TAG, "Error!!! showCameraStoppedAndFinish");
        resetScreenTimeout();
        setErrorOccuredAndFinish(true);
        if (Common.isQuickWindowCameraMode()) {
            if (this.mToast != null) {
                this.mToast.cancel();
            }
            this.mToast = Toast.makeText(getActivity(), R.string.camera_application_stopped, 0);
            this.mToast.setGravity(49, 0, Common.getPixelFromDimens(getApplicationContext(), R.dimen.smart_cover_toast_marginTop));
            this.mToast.show();
            getActivity().finish();
            return;
        }
        this.mDialogController.showDialogPopup(19);
    }

    public void afterOnDismissForSelectVideoLength() {
        quickFunctionControllerInitMenu();
        quickFunctionControllerRefresh(true);
        setVideoState(0);
        enableCommand(true);
        checkStorage(false);
    }

    public boolean showHelpGuidePopup(String shotModeHelp, int dialogId, boolean useCheckBox) {
        if (this.mDialogController != null) {
            return this.mDialogController.showHelpGuidePopup(shotModeHelp, dialogId, useCheckBox);
        }
        return false;
    }

    public void showProgressDialog() {
        CamLog.d(FaceDetector.TAG, "showProgressDialog");
        if (this.mDialogController != null) {
            this.mDialogController.showProgressDialog();
        }
    }

    public void deleteProgressDialog() {
        CamLog.d(FaceDetector.TAG, "deleteProgressDialog");
        if (this.mDialogController != null) {
            this.mDialogController.deleteProgressDialog();
        }
    }

    public void showSavingProgressDialog() {
        CamLog.d(FaceDetector.TAG, "showProgressDialog");
        if (this.mDialogController != null) {
            this.mDialogController.showSavingProgressDialog();
        }
    }

    public void deleteSavingProgressDialog() {
        CamLog.d(FaceDetector.TAG, "deleteProgressDialog");
        if (this.mDialogController != null) {
            this.mDialogController.deleteSavingProgressDialog();
        }
    }

    public void dialogControllerOnDismiss() {
        this.mDialogController.onDismiss();
    }

    public boolean checkStorageController() {
        return this.mStorageController != null;
    }

    public int getStorageMessageId() {
        return this.mStorageController.getMessageId();
    }

    public String getStoragePopupMessage() {
        return this.mStorageController.getMessage();
    }

    public void checkStorage(boolean showToast) {
        this.mStorageController.checkStorage(showToast);
    }

    public boolean checkFsWritable() {
        if (this.mStorageController != null) {
            return this.mStorageController.checkFsWritable();
        }
        return true;
    }

    public boolean isMediaScanning() {
        return this.mStorageController.isMediaScanning();
    }

    public void setMediaScanning(boolean scanning) {
        this.mStorageController.setMediaScanning(scanning);
    }

    public String getExternalStorageDir() {
        return this.mStorageController.getExternalStorageDir();
    }

    public String setStorageInitForFileNamingHelper() {
        return this.mStorageController.setStorageInitForFileNamingHelper();
    }

    public void setSettingControllerMmsLimit(boolean mmsVideo) {
        this.mSettingController.setMmsLimit(mmsVideo);
    }

    public void displaySettingView() {
        this.mSettingController.displaySettingView();
    }

    public boolean isSettingViewRemoving() {
        return this.mSettingController.isSettingViewRemoving();
    }

    public boolean isQuickFunctionSettingRemoving() {
        return this.mQuickFunctionSettingController.isQuickFunctionSettingViewRemoving();
    }

    public boolean isNullSettingView() {
        return this.mSettingController.isNullSettingView();
    }

    public boolean isNullQuickFunctionSettingView() {
        return this.mQuickFunctionSettingController.isNullSettingView();
    }

    public boolean isSettingControllerVisible() {
        return this.mSettingController.isVisible();
    }

    public void resetSettingMenu() {
        if (this.mSettingController != null && this.mPreviewController != null) {
            this.mSettingController.initSettingMenu();
            if ((isEffectsCamcorderActive() || isEffectsCameraActive()) && getPreviousResolution() != null) {
                setAllChildMenuEnabled(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, true);
            }
        }
    }

    public void setAllChildMenuEnabled(String key, boolean enable) {
        getCurrentSettingMenu().setAllChildMenuEnabled(key, enable);
    }

    public void setCurrentSettingMenuEnable(String key, boolean enable) {
        getCurrentSettingMenu().setEnabled(key, enable);
    }

    public void setCurrentSettingMenuEnable(String key, String value, boolean enable) {
        getCurrentSettingMenu().setEnabled(key, value, enable);
    }

    public void setSelectedChild(String key, String value, boolean saveSetting) {
        getCurrentSettingMenu().setSelectedChild(key, value, saveSetting);
    }

    public boolean setSelectedChild(int menuIndex, int index) {
        return this.mSettingController.getCurrentSettingMenu().setSelectedChild(menuIndex, index);
    }

    public boolean setSelectedChild(int index) {
        return this.mSettingController.getCurrentSettingMenu().setSelectedChild(index);
    }

    public SettingMenuItem getSelectedChild() {
        return getCurrentSettingMenu().getSelectedChild();
    }

    public int getChildIndex(String value) {
        return getCurrentSettingMenu().getChildIndex(value);
    }

    public ArrayList<String> getQfIndexList() {
        return this.mSettingController.getSetting().getQfIndexList();
    }

    public String getQFIndexListItem(int menuIndex) {
        return (String) this.mSettingController.getSetting().getQfIndexList().get(menuIndex);
    }

    public String getCurrentSettingMenuName() {
        return getCurrentSettingMenu().getCurrentMenu().name;
    }

    public boolean setCurrentSettingMenu(int index) {
        return this.mSettingController.getCurrentSettingMenu().setCurrentMenu(index);
    }

    public boolean setCurrentSettingMenuOnly(int index) {
        return this.mSettingController.getCurrentSettingMenu().setCurrentMenuOnly(index);
    }

    public String getSettingMenuCommand() {
        return getCurrentSettingMenu().getCurrentMenu().getSettingMenuCommand();
    }

    public String getMenuCommand() {
        return getCurrentSettingMenu().getCurrentMenu().getSelectedChild().getCommand();
    }

    public String getSettingParameterValue() {
        return getCurrentSettingMenu().getCurrentMenu().getSelectedChild().getParameterValue();
    }

    public int getBackupCurrentMenuIndex() {
        return getCurrentSettingMenu().getBackupCurrentMenuIndex();
    }

    public void setBackupCurrentMenuIndex(int index) {
        getCurrentSettingMenu().setBackupCurrentMenuIndex(index);
    }

    public void saveQFLIndex() {
        this.mSettingController.getSetting().saveQFLIndex();
    }

    public FaceBeauty getFaceBeauty() {
        return this.mFaceBeauty;
    }

    public void startPreview(LGParameters lsParameter, boolean useCallback) {
        if (this.mPreviewController != null) {
            this.mPreviewController.startPreview(lsParameter, useCallback);
        } else {
            CamLog.d(FaceDetector.TAG, "mPreviewController is null");
        }
    }

    public void stopPreview() {
        if (this.mPreviewController != null) {
            this.mPreviewController.stopPreview();
        } else {
            CamLog.d(FaceDetector.TAG, "mPreviewController is null");
        }
    }

    public void restartPreview(LGParameters lgParameter, boolean useCallBack) {
        if (this.mPreviewController != null) {
            this.mPreviewController.restartPreview(lgParameter, useCallBack);
        } else {
            CamLog.d(FaceDetector.TAG, "mPreviewController is null");
        }
    }

    public void startPreviewEffect() {
        if (this.mPreviewController != null) {
            this.mPreviewController.startPreviewEffect();
        } else {
            CamLog.d(FaceDetector.TAG, "mPreviewController is null");
        }
    }

    public void closeCamera() {
        if (this.mPreviewController != null) {
            this.mPreviewController.closeCamera();
        } else {
            CamLog.d(FaceDetector.TAG, "mPreviewController is null");
        }
    }

    public Camera getCameraDevice() {
        return this.mPreviewController.getCameraDevice();
    }

    public LGCamera getLG() {
        return this.mPreviewController.getLG();
    }

    public boolean isPreviewing() {
        return this.mPreviewController != null ? this.mPreviewController.isPreviewing() : false;
    }

    public void setPreviewing(boolean state) {
        if (this.mPreviewController != null) {
            this.mPreviewController.setPreviewing(state);
        }
    }

    public boolean checkPreviewController() {
        return this.mPreviewController != null;
    }

    public boolean checkSurfaceHolder() {
        return this.mPreviewController.getSurfaceHolder() != null;
    }

    public SurfaceHolder getSurfaceHolder() {
        return this.mPreviewController.getSurfaceHolder();
    }

    public boolean isCompleteProcessFrame() {
        return this.mPreviewController.getCameraGLPreview().isCompleteProcessFrame();
    }

    public boolean isPreviewOnGoing() {
        return this.mPreviewController.isPreviewOnGoing();
    }

    public boolean isReadyEngineProcessor() {
        return this.mPreviewController.isReadyEngineProcessor();
    }

    public void setEngineProcessor() {
        this.mPreviewController.setEngineProcessor();
    }

    public EngineProcessor getEngineProcessor() {
        return this.mPreviewController.getEngineProcessor();
    }

    public void releaseAllEngine() {
        if (isReadyEngineProcessor()) {
            CamLog.d(FaceDetector.TAG, "releaseAllEngine call...");
            this.mPreviewController.getEngineProcessor().releaseAllEngine();
        }
    }

    public void releaseEngine(String tag) {
        if (isReadyEngineProcessor()) {
            CamLog.d(FaceDetector.TAG, "releaseEngine [" + tag + "] call...");
            this.mPreviewController.getEngineProcessor().releaseEngine(tag);
        }
    }

    public BaseEngine enegineProcessorSetEngine(BaseEngine engine) {
        return getEngineProcessor().setEngine(engine);
    }

    public void enegineProcessorStart() {
        getEngineProcessor().start();
    }

    public boolean updateEffectSelection() {
        return this.mPreviewController.updateEffectSelection();
    }

    public int getPreviousEffectType() {
        return this.mPreviewController.getPreviousEffectType();
    }

    public void swapPreviewEffect(BaseEngine engine) {
        this.mPreviewController.swapPreviewEffect(engine);
    }

    public Parameters getParameters() {
        return this.mPreviewController.getLG().getLGParameters().getParameters();
    }

    public LGParameters getLGParam() {
        if (this.mPreviewController == null || this.mPreviewController.getLG() == null) {
            return null;
        }
        return this.mPreviewController.getLG().getLGParameters();
    }

    public void setParameters(Parameters parameter) {
        if (this.mPreviewController != null && this.mPreviewController.getCameraDevice() != null) {
            Camera cameraDevice = this.mPreviewController.getCameraDevice();
            if (cameraDevice != null) {
                cameraDevice.setParameters(parameter);
            }
        }
    }

    public boolean isEffectsCamcorderActive() {
        return this.mPreviewController.effectsCamcorderActive();
    }

    public boolean isEffectsCameraActive() {
        return this.mPreviewController.effectsCameraActive();
    }

    public boolean isLiveEffectActive() {
        return this.mPreviewController.isLiveEffectActive();
    }

    public void setLiveEffect(String effect) {
        this.mPreviewController.setLiveEffect(effect);
    }

    public String getLiveEffect() {
        return this.mPreviewController.getLiveEffect();
    }

    public ArrayList<String> getLiveEffectList() {
        return this.mPreviewController.getLiveEffectList();
    }

    public boolean isDualRecordingActive() {
        return this.mPreviewController.isDualRecordingActive();
    }

    public boolean isDualCameraActive() {
        return this.mPreviewController.isDualCameraActive();
    }

    public void initializeRecordingDual(String filepath_name, long mMaxFileSize, int mMaxDurationTime, long freeSpace) {
        this.mPreviewController.initializeRecordingEffect(filepath_name, mMaxFileSize, mMaxDurationTime, freeSpace);
    }

    public void showSubWindowResizeHandler(float x, float y) {
        this.mPreviewController.showSubWindowResizeHandler(x, y);
    }

    public PIPController getPIPController() {
        return this.mPreviewController.getPIPController();
    }

    public void hideSubWindowResizeHandler() {
        this.mPreviewController.hideSubWindowResizeHandler();
    }

    public boolean updateDualRecordingSelection() {
        return this.mPreviewController.updateEffectSelection();
    }

    public boolean isSmartZoomRecordingActive() {
        return this.mPreviewController.isSmartZoomRecordingActive();
    }

    public boolean updateSmartZoomRecordingSelection() {
        return this.mPreviewController.updateEffectSelection();
    }

    public void showSmartZoomFocusView() {
        this.mPreviewController.showSmartZoomFocusView();
    }

    public void hideSmartZoomFocusView() {
        this.mPreviewController.hideSmartZoomFocusView();
    }

    public void setSmartZoomFocusViewPosition(int x, int y) {
        this.mPreviewController.setSmartZoomFocusViewPosition(x, y);
    }

    public void initSmartZoomFocusView() {
        this.mPreviewController.initSmartZoomFocusView();
    }

    public void disableObjectTrackingForSmartZoom() {
        getPIPController().disableObjectTrackingForSmartZoom();
    }

    public boolean isObjectTrackingEnabledForSmartZoom() {
        return getPIPController().isObjectTrackingEnabledForSmartZoom();
    }

    public int getSmartZoomFocusViewMode() {
        return getPIPController().getSmartZoomFocusViewMode();
    }

    public void setChangeMode() {
        this.mPreviewController.setChangeMode();
    }

    public boolean isChangeMode() {
        return this.mPreviewController.isChangeMode();
    }

    public String getPreviousResolution() {
        return this.mPreviewController.getPreviousResolution();
    }

    public void setPreviousResolution(String resolution) {
        this.mPreviewController.setPreviousResolution(resolution);
    }

    public void storePreviousResolution(String resolution) {
        this.mPreviewController.storePreviousResolution(resolution);
    }

    public void setPrevResolutionWithStoredValue() {
        this.mPreviewController.setPrevResolutionWithStoredValue();
    }

    public void removePreviewCallback() {
        this.mPreviewController.removePreviewCallback();
    }

    public void startRecordingEffect() {
        this.mPreviewController.startRecordingEffect();
    }

    public void stopRecordingEffect() {
        this.mPreviewController.stopRecordingEffect();
    }

    public void pauseAndResumeRecording(boolean pause) {
        this.mPreviewController.pauseAndResumeRecording(pause);
    }

    public void waitStopRecordingEffectThreadDone() {
        this.mPreviewController.waitStopRecordingEffectThreadDone();
    }

    public void effectRecorderStopPreview() {
        effectRecorderStopPreviewByCallFrom(DialogCreater.DIALOG_ID_HELP_HDR);
    }

    public void effectCameraStopPreview() {
        effectCameraStopPreviewByCallFrom(DialogCreater.DIALOG_ID_HELP_HDR);
    }

    public boolean isSensorSupportBackdropper() {
        return this.mPreviewController.isSensorSupportBackdropper();
    }

    public void effectRecorderStopPreviewByCallFrom(int calledFrom) {
        this.mPreviewController.mEffectsRecorder.stopPreview(calledFrom);
    }

    public void effectCameraStopPreviewByCallFrom(int calledFrom) {
        this.mPreviewController.mEffectsCamera.stopPreview(calledFrom);
    }

    public void initializeRecordingEffect(String filepath_name, long mMaxFileSize, int mMaxDurationTime, long freeSpace) {
        this.mPreviewController.initializeRecordingEffect(filepath_name, mMaxFileSize, mMaxDurationTime, freeSpace);
    }

    public String getPreviewSizeOnDevice() {
        return this.mPreviewController.getPreviewSizeOnDevice();
    }

    public String getPreviewSizeOnScreen() {
        return this.mPreviewController.getPreviewSizeOnScreen();
    }

    public void setPreviewVisibility(int visible) {
        this.mPreviewController.getCameraPreview().setVisibility(visible);
    }

    public void showPreview() {
        this.mPreviewController.show();
    }

    public boolean setEnable3ALocks(LGParameters lgParameters, boolean lock) {
        return this.mPreviewController.setEnable3ALocks(lgParameters, lock);
    }

    public void setLockScreenPreventPreview(boolean flag) {
        this.mPreviewController.setLockScreenPreventPreview(flag);
    }

    public boolean isPreviewRendered() {
        return this.mPreviewController.beRendered();
    }

    public void setPreviewRendered(boolean render) {
        CamLog.e(FaceDetector.TAG, "setPreviewRendered : " + render);
        this.mPreviewController.setRendered(render);
    }

    public void changePreviewModeOnUiThread(int width, int height) {
        this.mPreviewController.changePreviewModeOnUiThread(width, height);
    }

    public void changeQuickPreviewMode(int width, int height) {
        this.mPreviewController.changeQuickPreviewMode(width, height);
    }

    public void setBeautyshotProgress(boolean set) {
        this.mPreviewController.setBeautyshotProgress(set);
    }

    public boolean getBeautyshotProgress() {
        return this.mPreviewController.getBeautyshotProgress();
    }

    public void releaseEngine(boolean checkEngineTag) {
        this.mPreviewController.releaseEngine(checkEngineTag);
    }

    public void restoreSubWindow() {
        this.mPreviewController.restoreSubWindow();
    }

    public void setEffectRecorderPausing(boolean set) {
        this.mPreviewController.setEffectRecorderPausing(set);
    }

    public void setBackgroundColorBlack() {
        this.mPreviewController.setBackgroundColorBlack();
    }

    public void setBackgroundColorWhite() {
        this.mPreviewController.setBackgroundColorWhite();
    }

    public long getPicturesRemaining() {
        return this.mIndicatorController.getPicturesRemaining();
    }

    public void setPicturesRemaining(long remain) {
        this.mIndicatorController.setPicturesRemaining(remain);
    }

    public void showIndicatorController() {
        this.mIndicatorController.showIndicator();
    }

    public void hideIndicatorController() {
        this.mIndicatorController.hideIndicator();
    }

    public void updateSizeIndicator() {
    }

    public void updateStabilizationIndicator() {
        this.mIndicatorController.updateStabilizationIndicator();
    }

    public void updateGpsIndicator() {
        runOnUiThread(new Runnable() {
            public void run() {
                Mediator.this.removePostRunnable(this);
                Mediator.this.mIndicatorController.updateGpsIndicator();
            }
        });
    }

    public void updateGpsIndicator(String provider) {
        if (!getRecordLocation()) {
            return;
        }
        if ("gps".equals(provider) || "network".equals(provider)) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Mediator.this.removePostRunnable(this);
                    Mediator.this.mIndicatorController.updateGpsIndicator();
                }
            });
        }
    }

    public void updateRemainIndicator() {
        this.mIndicatorController.updateRemainIndicator();
    }

    public void updateTimerIndicator() {
        this.mIndicatorController.updateTimerIndicator();
    }

    public void updateSceneIndicator(boolean useLocalSetting, String value) {
    }

    public void updateFlashIndicator(boolean useLocalSetting, String value) {
        this.mIndicatorController.updateFlashIndicator(useLocalSetting, value);
    }

    public void updateStorageIndicator() {
        this.mIndicatorController.updateStorageIndicator();
    }

    public void updateFocusIndicator() {
    }

    public void updateAudioIndicator() {
    }

    public boolean isIndicatorControllerInitialized() {
        return this.mIndicatorController.isInitialized();
    }

    public void setBatteryIndicator(int level) {
        if (this.mIndicatorController != null) {
            this.mIndicatorController.setBatteryIndicator(level);
        }
    }

    public void setBatteryVisibility(int batteryLevel) {
        if (this.mIndicatorController != null) {
            this.mIndicatorController.setBatteryVisibility(batteryLevel);
        }
    }

    public void setMessageIndicatorReceived(int msgReceived, boolean isReadAllMsg) {
        this.mIndicatorController.setMessageIndicatorReceived(msgReceived, isReadAllMsg);
    }

    public void setVoiceMailIndicator(int msgReceived) {
        this.mIndicatorController.setVoiceMailIndicator(msgReceived);
    }

    public void updateModeMenuIndicator() {
        this.mIndicatorController.updateModeMenuIndicator();
    }

    public void setModeMenuVisibility(int visible) {
        this.mIndicatorController.setModeMenuVisibility(visible);
    }

    public void updateModeMenuIndicator(String title) {
        this.mIndicatorController.updateModeMenuIndicator(title);
    }

    public void setIndicatorLayout(int leftMargin) {
        this.mIndicatorController.setIndicatorLayout(leftMargin);
    }

    public void setFocusRectangleInitialize() {
        this.mFocusController.setFocusRectangleInitialize();
    }

    public boolean checkFocusController() {
        return this.mFocusController != null;
    }

    public int getFocusState() {
        return this.mFocusController.getFocusState();
    }

    public void setFocusState(int state) {
        this.mFocusController.setFocusState(state);
    }

    public boolean isFocusViewVisible() {
        return this.mFocusController.isFocusViewVisible();
    }

    public void clearFocusState() {
        this.mFocusController.clearFocusState();
    }

    public void setClearFocusAnimation() {
        this.mFocusController.setClearFocusAnimation();
    }

    public void setShutterButtonClicked(boolean clicked) {
        this.mFocusController.setShutterButtonClicked(clicked);
    }

    public void updateFocusStateIndicator() {
        this.mFocusController.updateFocusStateIndicator();
    }

    public boolean isContinuousFocusActivating() {
        return this.mFocusController.isContinuousFocusActivating();
    }

    public void setContinuousFocusActive(boolean active) {
        this.mFocusController.setContinuousFocusActive(active);
    }

    public void setFocusAreaWindow(int width, int height, int leftMargin) {
        this.mFocusController.setFocusAreaWindow(width, height, leftMargin);
    }

    public void doFocus(boolean pressed) {
        this.mFocusController.doFocus(pressed);
    }

    public void doFocusOnCaf() {
        this.mFocusController.doFocusOnCaf();
    }

    public void cancelAutoFocus() {
        this.mFocusController.cancelAutoFocus();
    }

    public boolean isBlockingFaceTrFocusing() {
        return this.mFocusController.isBlockingFaceTrFocusing();
    }

    public void setBlockingFaceTrFocusing(boolean beBlocked) {
        this.mFocusController.setBlockingFaceTrFocusing(beBlocked);
    }

    public void setFocusPosition(int position) {
        this.mFocusController.setFocusPosition(position);
    }

    public void startFocusByTouchPress(int x, int y, boolean bTouchedAFbyFaceTr) {
        this.mFocusController.startFocusByTouchPress(x, y, bTouchedAFbyFaceTr);
    }

    public void initFocusAreas() {
        this.mFocusController.initFocusAreas();
    }

    public boolean hideFocus() {
        return getFocusController().hideFocus();
    }

    public boolean showFocus() {
        return getFocusController().showFocus();
    }

    public boolean showFocus(boolean justDoIt) {
        return getFocusController().showFocus(justDoIt);
    }

    public boolean checkTouchFocusArea(int x) {
        if (this.mFocusController == null || (x <= this.mFocusController.mFocusAreaWidth + this.mFocusController.mFocusAreaLeftMargin && x >= this.mFocusController.mFocusAreaLeftMargin)) {
            return true;
        }
        return false;
    }

    public int getFocusAreaWidth() {
        return this.mFocusController.mFocusAreaWidth;
    }

    public int getFocusAreaHeight() {
        return this.mFocusController.mFocusAreaHeight;
    }

    public int getFocusAreaLeftMargin() {
        return this.mFocusController.mFocusAreaLeftMargin;
    }

    public int getObjectTrackingState() {
        return this.mFocusController.getObjectTrackingState();
    }

    public void startObjectTrackingFocus(int x, int y, int width, int height, int state) {
        this.mFocusController.startObjectTrackingFocus(x, y, width, height, state);
    }

    public void startObjectTrackingFocusForSmartZoom(int x, int y, int width, int height, int state) {
        this.mFocusController.startObjectTrackingFocusForSmartZoom(x, y, width, height, state);
    }

    public boolean isSnapOnFinish() {
        return this.mFocusController.isSnapOnFinish();
    }

    public void unregisterCAFCallback() {
        this.mFocusController.unregisterCallback();
    }

    public void registerObjectCallback() {
        this.mFocusController.registerObjectCallback();
    }

    public void unregisterObjectCallback() {
        this.mFocusController.unregisterObjectCallback();
    }

    public void registerFaceTrackingCallback() {
        this.mFocusController.registerFaceTrackingCallback();
    }

    public void toast(int resource) {
        toast(getString(resource));
    }

    public void toast(String strString) {
        this.mToastController.showShortToast(strString);
    }

    public void toastConstant(String strString) {
        this.mToastController.showShortToast(strString, false);
    }

    public void toast(String message, boolean immediately) {
        this.mToastController.show(message, immediately);
    }

    public void toastMiddleLong(String strString) {
        this.mToastController.show(strString, (long) ProjectVariables.keepDuration);
    }

    public void toastLong(String strString) {
        this.mToastController.showLongToast(strString);
    }

    public void toastControllerHide(boolean immediately) {
        this.mToastController.hide(immediately);
    }

    public boolean isToastControllerShowing() {
        return this.mToastController.isShowing();
    }

    public void storageToastShow(String message, boolean immediately, boolean shortToast) {
        this.mToastController.storageToastShow(message, immediately, shortToast);
    }

    public void storageToasthide(boolean immediately) {
        this.mToastController.storageToasthide(immediately);
    }

    public boolean isStorageToastShowing() {
        return this.mToastController.isStorageToastShowing();
    }

    public boolean hideForPhotoStory() {
        return this.mToastController.hideForPhotoStory();
    }

    public void onOptionItemViewClick() {
        if (this.mOptionMenuController != null) {
            this.mOptionMenuController.hideOptionMenu();
        }
    }

    public void showOptionMenu(int menuType) {
        if (this.mOptionMenuController != null) {
            this.mOptionMenuController.showOptionMenu(menuType);
        }
    }

    public void hideOptionMenu() {
        if (this.mOptionMenuController != null) {
            this.mOptionMenuController.hideOptionMenu();
        }
    }

    public boolean isOptionMenuShowing() {
        if (this.mOptionMenuController != null) {
            return this.mOptionMenuController.isOptionMenuShowing();
        }
        return false;
    }

    public boolean isShotModeMenuVisible() {
        return this.mShotModeMenuController.isVisible();
    }

    public void showShotModeMenu() {
        this.mShotModeMenuController.show();
    }

    public void hideShotModeMenu(boolean animation) {
        this.mShotModeMenuController.hide(animation);
    }

    public String getCurrentSelectedTitle() {
        return this.mShotModeMenuController.getCurrentSelectedTitle();
    }

    public void rotateAllController(int degree, boolean animation) {
        if (getCameraDimension() == 1 && (degree == 90 || degree == Tag.IMAGE_DESCRIPTION)) {
            toast(getString(R.string.sp_popup_change_view_to_landscape_for_3d_NORMAL));
            return;
        }
        this.mPreviewPanelController.startRotation(degree, animation);
        this.mSettingController.startRotation(degree);
        this.mQuickFunctionController.startSubMenuRotation(degree);
        if (!(!isLiveEffectActive() || findViewById(R.id.live_effect_sliding_drawer_menu_slide) == null || ((MultiDirectionSlidingDrawer) findViewById(R.id.live_effect_sliding_drawer_menu_slide)).isMoving())) {
            this.mQuickFunctionController.startLiveEffectDrawerSubMenuRotation(degree);
        }
        if (!((!isDualCameraActive() && !isDualRecordingActive() && !isSmartZoomRecordingActive()) || findViewById(R.id.pip_frame_sliding_drawer_menu_slide) == null || ((MultiDirectionSlidingDrawer) findViewById(R.id.pip_frame_sliding_drawer_menu_slide)).isMoving())) {
            this.mQuickFunctionController.startPIPFrameSubMenuRotation(degree);
        }
        this.mQuickFunctionController.startAudiozoomMenuRotation(degree);
        if (!(isSettingViewRemoving() || isQuickFunctionSettingRemoving() || (!isDualRecordingActive() && !isDualCameraActive() && !isSmartZoomRecordingActive()))) {
            setPIPRotate(degree);
            doCommand(Command.HIDE_PIP_SUBWINDOW_RESIZE_HANDLER);
        }
        if (!(!isSmartZoomRecordingActive() || this.mPreviewController == null || this.mPreviewController.getPIPRecordingController() == null)) {
            this.mPreviewController.getPIPRecordingController().updateOrientation();
        }
        if (!(this.mPreviewController == null || this.mPreviewController.getCameraGLPreview() == null)) {
            this.mPreviewController.getCameraGLPreview().setOrientation(degree);
        }
        if (!(this.mPreviewController == null || this.mPreviewController.getCameraPreview() == null)) {
            this.mPreviewController.getCameraPreview().setOrientation(degree);
        }
        this.mToastController.rotate();
        this.mDialogController.startRotation(degree);
        setDegree(R.id.osd_btn1, degree, animation);
        setDegree(R.id.osd_btn2, degree, animation);
        setDegree(R.id.osd_btn3, degree, animation);
        setDegree(R.id.osd_btn4, degree, animation);
        setDegree(R.id.osd_btn5, degree, animation);
        setDegree(R.id.icon_resolution, degree, R.id.camera_indicator, animation);
        setDegree(R.id.icon_focus, degree, R.id.camera_indicator, animation);
        setDegree(R.id.icon_scene_mode, degree, R.id.camera_indicator, animation);
        setDegree(R.id.icon_timer, degree, R.id.camera_indicator, animation);
        setDegree(R.id.icon_geo_tag, degree, R.id.camera_indicator, animation);
        setDegree(R.id.icon_storage, degree, R.id.camera_indicator, animation);
        setDegree(R.id.icon_message, degree, R.id.camera_indicator, animation);
        setDegree(R.id.icon_battery, degree, R.id.camera_indicator, animation);
        setDegree(R.id.icon_flash, degree, R.id.camera_indicator, animation);
        setDegree(R.id.icon_voice_shutter, degree, R.id.camera_indicator, animation);
        setDegree(R.id.icon_vvm_message, degree, R.id.camera_indicator, animation);
        if (isCamcorderRotation(true)) {
            setDegree(R.id.icon_video_size, degree, R.id.camcorder_indicator, animation);
            setDegree(R.id.icon_scene_mode, degree, R.id.camcorder_indicator, animation);
            setDegree(R.id.icon_voice, degree, R.id.camcorder_indicator, animation);
            setDegree(R.id.icon_storage, degree, R.id.camcorder_indicator, animation);
            setDegree(R.id.icon_voice_mail, degree, R.id.camcorder_indicator, animation);
            setDegree(R.id.icon_message, degree, R.id.camcorder_indicator, animation);
            setDegree(R.id.icon_battery, degree, R.id.camcorder_indicator, animation);
            setDegree(R.id.icon_flash, degree, R.id.camcorder_indicator, animation);
            setDegree(R.id.icon_geo_tag, degree, R.id.camcorder_indicator, animation);
            setDegree(R.id.icon_stabilization, degree, R.id.camcorder_indicator, animation);
            setDegree(R.id.icon_vvm_message, degree, R.id.camcorder_indicator, animation);
        }
        this.mQuickButtonController.startRotation(degree, animation);
        BubblePopupController.get().startRotation(this, degree, animation);
        EnteringViewController.get().enteringGuideRotate(getActivity(), degree);
        if (!FunctionProperties.isSupportZoomOnRecord() || isCamcorderRotation(false)) {
            this.mZoomController.startRotation(degree, animation);
        }
        this.mBrightnessController.startRotation(degree, animation);
        this.mBeautyshotController.startRotation(degree, animation);
        this.mManualFocusController.startRotation(degree, animation);
        if (ModelProperties.is3dSupportedModel()) {
            this.mCamera3dDepthController.startRotation(degree, animation);
        }
        this.mIndicatorController.startRotation(degree, false);
        if (getApplicationMode() == 0) {
            setDegree(R.id.free_panorama_sensor_correction_guide_view, degree, R.id.free_panorama_view_layout, animation);
            getCurrentModule().startRotation(degree);
            startFreePanoramaRotation(degree, animation);
            startPlanePanoramaRotation(degree, animation);
        } else {
            startRecordingControllerRotation(degree);
            startAudioZoomContollerRotation(degree);
        }
        setDegree(R.id.countdown_bg, degree, animation);
        setDegree(R.id.timer_count, degree, animation);
        this.mQuickFunctionDragController.startRotation(degree, animation);
        this.mQuickFunctionSettingController.startRotation(degree);
        this.mOptionMenuController.startRotation(degree);
        this.mShotModeMenuController.startRotation(degree, animation);
        if (this.mGestureShutterController != null) {
            this.mGestureShutterController.startRotation(degree, animation);
        }
    }

    public void setDegree(int resId, int degree, boolean animation) {
        setDegree(resId, degree, 0, animation);
    }

    public void setDegree(int resId, int degree, int parentResId, boolean animation) {
        View view = findViewById(resId);
        if (view != null) {
            if (parentResId != 0 && findViewById(parentResId) == null) {
                return;
            }
            if (view instanceof RotateImageButton) {
                if (parentResId == 0) {
                    ((RotateImageButton) findViewById(resId)).setDegree(degree, animation);
                } else {
                    ((RotateImageButton) findViewById(parentResId).findViewById(resId)).setDegree(degree, animation);
                }
            } else if (view instanceof RotateImageView) {
                if (parentResId == 0) {
                    ((RotateImageView) findViewById(resId)).setDegree(degree, animation);
                } else {
                    ((RotateImageView) findViewById(parentResId).findViewById(resId)).setDegree(degree, animation);
                }
            } else if (view instanceof SwitcherLever) {
                ((SwitcherLever) view).setDegree(degree, animation);
            } else if (view instanceof Switcher) {
                ((Switcher) view).setDegree(degree, animation);
            } else if (view instanceof SwitcherLeverVertical) {
                ((SwitcherLeverVertical) view).setDegree(degree, animation);
                ((SwitcherLeverVertical) view).startRotation(degree);
            } else if (view instanceof SwitcherLeverHorizon) {
                ((SwitcherLeverHorizon) view).setDegree(degree, animation);
                ((SwitcherLeverHorizon) view).startRotation(degree);
            }
        }
    }

    public boolean isCamcorderRotation(boolean checkWithPause) {
        return true;
    }

    public void setCameraIdBeforeStartInit() {
        CamLog.d(FaceDetector.TAG, "SetCameraIdBeforeStartInit-start");
        if (this.mSettingController != null) {
            this.mSettingController.initController();
        }
        if (getApplicationMode() == 0) {
            setCameraIDForCamera();
        } else {
            setCameraIDForCamcorder();
        }
        if (this.mPreviewPanelController != null) {
            this.mPreviewPanelController.initController();
        }
        if (this.mQuickFunctionController != null) {
            this.mQuickFunctionController.initController();
        }
        if (this.mIndicatorController != null) {
            this.mIndicatorController.initController();
        }
        CamLog.d(FaceDetector.TAG, "SetCameraIdBeforeStartInit-end");
    }

    private void setCameraIDForCamcorder() {
        if (isAttachMode() && isAttachIntent()) {
            readVideoIntentExtras();
            if (!(this.mSettingController == null || getRequestedVideoSizeLimit() == 0)) {
                this.mSettingController.setSetting(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, "320x240");
            }
        }
        if (this.mPreviewController != null) {
            this.mPreviewController.initController();
        }
    }

    private void setCameraIDForCamera() {
        if (this.mPreviewController != null && this.mSettingController != null) {
            this.mPreviewController.initController();
            if (this.mPreviewController.getCameraPreview() == null) {
                return;
            }
            if (!CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
                this.mPreviewController.setupHolder(false);
            }
        }
    }

    public void setBlockTouchByCallPopUp(boolean block) {
        this.mBlockTouchByCallPopUp = block;
    }

    public boolean getBlockTouchByCallPopUp() {
        return this.mBlockTouchByCallPopUp;
    }

    public void setExitIgnoreDuringSaving(boolean isIgnore) {
        CamLog.d(FaceDetector.TAG, "setExitIgnoreDuringSaving :" + isIgnore);
        this.isEventIgnoreDuringSaving = isIgnore;
    }

    public boolean isExitIgnoreDuringSaving() {
        CamLog.d(FaceDetector.TAG, "isExitIgnoreDuringSaving :" + this.isEventIgnoreDuringSaving);
        return this.isEventIgnoreDuringSaving;
    }

    public void setCurrentRecordingTime(long seconds) {
        this.mCurrentRecordingTime = seconds;
    }

    public long getCurrentRecordingTime() {
        return this.mCurrentRecordingTime;
    }

    public void setMultiWindowAFView(int[] previewSizeOnScreen) {
        this.mFocusController.setMultiWindowAFView(previewSizeOnScreen);
    }

    public boolean checkShotModeForMultiWindowAF() {
        return this.mFocusController.checkShotModeForMultiWindowAF();
    }

    public String getDefaultFocusModeParameterForMultiWindowAF(LGParameters lgParam) {
        return this.mFocusController.getDefaultFocusModeParameterForMultiWindowAF(lgParam);
    }

    public void initMultiWindowAFView() {
        this.mFocusController.initMultiWindowAFView();
    }

    public void updateNavigationBarShape() {
        if (ModelProperties.isSoftKeyNavigationBarModel()) {
            postOnUiThread(new Runnable() {
                public void run() {
                    Mediator.this.removePostRunnable(this);
                    if (!Mediator.this.isPausing()) {
                        AppControlUtil.setFullScreen(Mediator.this.getActivity());
                        AppControlUtil.setTransparentNavigationBar(Mediator.this.getActivity(), true);
                        AppControlUtil.setEnableRotateNaviataionBar(Mediator.this.getActivity(), true);
                        AppControlUtil.disableNavigationButton(Mediator.this.getActivity());
                        if (Mediator.this.isCamcorderRotation(true)) {
                            AppControlUtil.rotateNavigationBarIcon(Mediator.this.getActivity(), Mediator.this.getOrientationDegree(), CameraConstants.GALLERY_QUICKVIEW_ANI_INTERVAL);
                        } else {
                            AppControlUtil.rotateNavigationBarIcon(Mediator.this.getActivity(), Mediator.this.getRecordingDegree(), CameraConstants.GALLERY_QUICKVIEW_ANI_INTERVAL);
                        }
                    }
                }
            });
        }
    }

    public void checkSceneMode(LGParameters lgParameters, boolean showToast, String toastMessage) {
        String previeousSceneMode = getSettingValue(Setting.KEY_SCENE_MODE);
        if (!LGT_Limit.ISP_AUTOMODE_AUTO.equals(previeousSceneMode) && !CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(previeousSceneMode)) {
            List<String> supportedScenemode = lgParameters.getParameters().getSupportedSceneModes();
            if (supportedScenemode != null && supportedScenemode.size() > 0) {
                setSetting(Setting.KEY_SCENE_MODE, LGT_Limit.ISP_AUTOMODE_AUTO);
                if (getApplicationMode() == 0) {
                    if (ModelProperties.isRenesasISP()) {
                        setSceneModeForAdvanced(lgParameters.getParameters(), LGT_Limit.ISP_AUTOMODE_AUTO);
                        if (!(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)) || CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)) || CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)) || (!CameraConstants.SCENE_MODE_SMART_SHUTTER.equals(previeousSceneMode) && !Setting.HELP_NIGHT.equals(previeousSceneMode)))) {
                            restartPreview(lgParameters, false);
                        }
                    } else {
                        doCommand(Command.CAMERA_SCENE_MODE, lgParameters);
                    }
                    if (showToast && toastMessage != null) {
                        toast(toastMessage);
                    }
                    setQuickFunctionControllerAllMenuIcons();
                    if (isIndicatorControllerInitialized()) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Mediator.this.removePostRunnable(this);
                                Mediator.this.updateSceneIndicator(false, null);
                                Mediator.this.updateModeMenuIndicator();
                            }
                        });
                    }
                }
            }
        }
    }

    public void setMenuEnableForSceneMode(int setting) {
        if (getApplicationMode() == 0) {
            boolean enable = false;
            if (LGT_Limit.ISP_AUTOMODE_AUTO.equals(getSettingValue(Setting.KEY_SCENE_MODE))) {
                String shotMode = getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
                if (!(CameraConstants.TYPE_SHOTMODE_HDR.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(shotMode))) {
                    enable = true;
                }
            }
            if ((setting & 1) != 0) {
                setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_WHITEBALANCE, enable);
            }
            if ((setting & 2) != 0) {
                setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_COLOREFFECT, enable);
            }
            if ((setting & 4) != 0) {
                setPreferenceMenuOnlyEnable(Setting.KEY_ISO, enable);
            }
        }
    }

    public boolean getFlashEnableForShotMode() {
        if (getApplicationMode() == 0 && !CameraConstants.SMART_MODE_ON.equals(getSettingValue(Setting.KEY_SMART_MODE))) {
            String shotMode = getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
            if (CameraConstants.TYPE_SHOTMODE_PANORAMA.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_CONTINUOUS.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_HDR.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_REFOCUS.equals(shotMode) || getCameraId() == 1 || isTimeMachineModeOn()) {
                return false;
            }
            if ((CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotMode) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(shotMode) || getCameraId() == 0) && Setting.HELP_NIGHT.equals(getSettingValue(Setting.KEY_SCENE_MODE))) {
                return false;
            }
        }
        return true;
    }

    public boolean isFlashOffByHighTemperature() {
        return this.mIsFlashOffByHighTemperature;
    }

    public void setFlashOffByHighTemperature(boolean setFlashOff) {
        this.mIsFlashOffByHighTemperature = setFlashOff;
    }

    public void perfLockAcquire() {
        if (ModelProperties.is8974Chipset()) {
            LGPowerManagerHelper service = (LGPowerManagerHelper) new LGContext(getApplicationContext()).getLGSystemService("lgpowermanagerhelper");
            try {
                CamLog.d(FaceDetector.TAG, "Boost CPU clock!!, ");
                service.boost(11);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isBackKeyPressed() {
        return this.mIsBackKeyPressed;
    }

    public void setBackKeyPressed(boolean backKeyPressed) {
        this.mIsBackKeyPressed = backKeyPressed;
    }

    protected void setResultAndFinish(int resultCode, Intent data) {
        getActivity().setResult(resultCode, data);
        getActivity().finish();
    }

    protected void setResultAndFinish(int resultCode) {
        getActivity().setResult(resultCode);
        getActivity().finish();
    }

    private void preProcessSaveUri() {
        Uri savedUri = getSavedUri();
        if ("content".equals(this.mSaveUri.getScheme())) {
            File tempFile = new File(savedUri.getPath());
            if (tempFile.exists()) {
                CamLog.d(FaceDetector.TAG, "temp file(" + tempFile.getPath() + ") deleted : " + tempFile.delete());
                return;
            }
            return;
        }
        String requestedPath = this.mSaveUri.getPath();
        requestedPath = requestedPath.substring(0, requestedPath.lastIndexOf(47));
        CamLog.d(FaceDetector.TAG, "Requested directory:" + requestedPath);
        File requestedDir = new File(requestedPath);
        if (!requestedDir.exists()) {
            CamLog.d(FaceDetector.TAG, "Requested directory not exist, make it.");
            requestedDir.mkdirs();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void doAttachSaveUri() {
        /*
        r17 = this;
        r10 = 0;
        r4 = "CameraApp";
        r5 = "doAttachSaveUri()";
        com.lge.camera.util.CamLog.d(r4, r5);
        r17.preProcessSaveUri();
        r4 = r17.checkFreespace();
        if (r4 != 0) goto L_0x0048;
    L_0x0011:
        r4 = "CameraApp";
        r5 = "===>Not Enough sotrage space!!!";
        com.lge.camera.util.CamLog.d(r4, r5);
        r14 = r17.getRequestedAttachFileStorage();
        r16 = 0;
        if (r14 != 0) goto L_0x003e;
    L_0x0020:
        r4 = 2131361898; // 0x7f0a006a float:1.8343561E38 double:1.0530326927E-314;
        r0 = r17;
        r16 = r0.getString(r4);
    L_0x0029:
        r4 = r17.getApplicationContext();
        r5 = 1;
        r0 = r16;
        r4 = android.widget.Toast.makeText(r4, r0, r5);
        r4.show();
        r4 = 0;
        r0 = r17;
        r0.setResultAndFinish(r4);
    L_0x003d:
        return;
    L_0x003e:
        r4 = 2131361896; // 0x7f0a0068 float:1.8343557E38 double:1.0530326917E-314;
        r0 = r17;
        r16 = r0.getString(r4);
        goto L_0x0029;
    L_0x0048:
        r12 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Exception -> 0x0174 }
        r0 = r17;
        r4 = r0.mSaveUri;	 Catch:{ Exception -> 0x0174 }
        r4 = r4.equals(r12);	 Catch:{ Exception -> 0x0174 }
        if (r4 == 0) goto L_0x007b;
    L_0x0054:
        r4 = "CameraApp";
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0174 }
        r5.<init>();	 Catch:{ Exception -> 0x0174 }
        r6 = "===>URL Is Not correct we will return URI :";
        r5 = r5.append(r6);	 Catch:{ Exception -> 0x0174 }
        r0 = r17;
        r6 = r0.mSaveUri;	 Catch:{ Exception -> 0x0174 }
        r6 = r6.getPath();	 Catch:{ Exception -> 0x0174 }
        r5 = r5.append(r6);	 Catch:{ Exception -> 0x0174 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0174 }
        com.lge.camera.util.CamLog.d(r4, r5);	 Catch:{ Exception -> 0x0174 }
        r17.sendResultIntent();	 Catch:{ Exception -> 0x0174 }
        com.lge.camera.util.Common.closeSilently(r10);
        goto L_0x003d;
    L_0x007b:
        r4 = r17.getContentResolver();	 Catch:{ Exception -> 0x0174 }
        r0 = r17;
        r5 = r0.mSaveUri;	 Catch:{ Exception -> 0x0174 }
        r4 = r4.openOutputStream(r5);	 Catch:{ Exception -> 0x0174 }
        r0 = r4;
        r0 = (java.io.FileOutputStream) r0;	 Catch:{ Exception -> 0x0174 }
        r10 = r0;
        r4 = "CameraApp";
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0174 }
        r5.<init>();	 Catch:{ Exception -> 0x0174 }
        r6 = "file path = ";
        r5 = r5.append(r6);	 Catch:{ Exception -> 0x0174 }
        r0 = r17;
        r6 = r0.mSaveUri;	 Catch:{ Exception -> 0x0174 }
        r6 = r6.getPath();	 Catch:{ Exception -> 0x0174 }
        r5 = r5.append(r6);	 Catch:{ Exception -> 0x0174 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0174 }
        com.lge.camera.util.CamLog.d(r4, r5);	 Catch:{ Exception -> 0x0174 }
        if (r10 != 0) goto L_0x00c6;
    L_0x00ad:
        r4 = "CameraApp";
        r5 = "===>outputStream null! cancel";
        com.lge.camera.util.CamLog.d(r4, r5);	 Catch:{ Exception -> 0x0174 }
        r4 = "CameraApp";
        r5 = "doAttach CANCELED";
        com.lge.camera.util.CamLog.d(r4, r5);	 Catch:{ Exception -> 0x0174 }
        r4 = 0;
        r0 = r17;
        r0.setResultAndFinish(r4);	 Catch:{ Exception -> 0x0174 }
        com.lge.camera.util.Common.closeSilently(r10);
        goto L_0x003d;
    L_0x00c6:
        r4 = r17.getApplicationMode();	 Catch:{ Exception -> 0x0174 }
        if (r4 != 0) goto L_0x00fa;
    L_0x00cc:
        r0 = r17;
        r4 = r0.mCaptureData;	 Catch:{ Exception -> 0x0174 }
        if (r4 == 0) goto L_0x00fa;
    L_0x00d2:
        r0 = r17;
        r4 = r0.mCaptureData;	 Catch:{ Exception -> 0x0174 }
        r10.write(r4);	 Catch:{ Exception -> 0x0174 }
    L_0x00d9:
        r4 = com.lge.camera.properties.ProjectVariables.isRemoveOrgFile();	 Catch:{ Exception -> 0x0174 }
        if (r4 == 0) goto L_0x00e2;
    L_0x00df:
        r17.removeOrgFile();	 Catch:{ Exception -> 0x0174 }
    L_0x00e2:
        com.lge.camera.util.Common.closeSilently(r10);
        r4 = "CameraApp";
        r5 = "doAttach OK";
        com.lge.camera.util.CamLog.d(r4, r5);
        r4 = r17.getApplicationMode();
        if (r4 != 0) goto L_0x01bb;
    L_0x00f2:
        r4 = -1;
        r0 = r17;
        r0.setResultAndFinish(r4);
        goto L_0x003d;
    L_0x00fa:
        r4 = "CameraApp";
        r5 = "doAttach mCaptureData is null! so we recopy video data to mSaveUri";
        com.lge.camera.util.CamLog.w(r4, r5);	 Catch:{ Exception -> 0x0174 }
        r13 = 0;
        r9 = 0;
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0174 }
        r4.<init>();	 Catch:{ Exception -> 0x0174 }
        r0 = r17;
        r5 = r0.mStorageController;	 Catch:{ Exception -> 0x0174 }
        r5 = r5.getCurrentStorageDirectory();	 Catch:{ Exception -> 0x0174 }
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x0174 }
        r5 = r17.getSavedFileName();	 Catch:{ Exception -> 0x0174 }
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x0174 }
        r5 = r17.getFileExtension();	 Catch:{ Exception -> 0x0174 }
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x0174 }
        r11 = r4.toString();	 Catch:{ Exception -> 0x0174 }
        r13 = new java.io.File;	 Catch:{ Exception -> 0x0174 }
        r13.<init>(r11);	 Catch:{ Exception -> 0x0174 }
        r9 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x0174 }
        r9.<init>(r13);	 Catch:{ Exception -> 0x0174 }
        r3 = r9.getChannel();	 Catch:{ IOException -> 0x0198 }
        if (r3 == 0) goto L_0x016f;
    L_0x0138:
        r4 = 0;
        r6 = r3.size();	 Catch:{ IOException -> 0x0198 }
        r8 = r10.getChannel();	 Catch:{ IOException -> 0x0198 }
        r3.transferTo(r4, r6, r8);	 Catch:{ IOException -> 0x0198 }
        r4 = "CameraApp";
        r5 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x0198 }
        r5.<init>();	 Catch:{ IOException -> 0x0198 }
        r6 = "file copy done - from: ";
        r5 = r5.append(r6);	 Catch:{ IOException -> 0x0198 }
        r5 = r5.append(r11);	 Catch:{ IOException -> 0x0198 }
        r6 = ", to: ";
        r5 = r5.append(r6);	 Catch:{ IOException -> 0x0198 }
        r0 = r17;
        r6 = r0.mSaveUri;	 Catch:{ IOException -> 0x0198 }
        r6 = r6.getPath();	 Catch:{ IOException -> 0x0198 }
        r5 = r5.append(r6);	 Catch:{ IOException -> 0x0198 }
        r5 = r5.toString();	 Catch:{ IOException -> 0x0198 }
        com.lge.camera.util.CamLog.v(r4, r5);	 Catch:{ IOException -> 0x0198 }
    L_0x016f:
        com.lge.camera.util.Common.closeSilently(r9);	 Catch:{ Exception -> 0x0174 }
        goto L_0x00d9;
    L_0x0174:
        r2 = move-exception;
        r4 = "CameraApp";
        r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x01b6 }
        r5.<init>();	 Catch:{ all -> 0x01b6 }
        r6 = "outputStream error";
        r5 = r5.append(r6);	 Catch:{ all -> 0x01b6 }
        r5 = r5.append(r2);	 Catch:{ all -> 0x01b6 }
        r5 = r5.toString();	 Catch:{ all -> 0x01b6 }
        com.lge.camera.util.CamLog.d(r4, r5);	 Catch:{ all -> 0x01b6 }
        r4 = 0;
        r0 = r17;
        r0.setResultAndFinish(r4);	 Catch:{ all -> 0x01b6 }
        com.lge.camera.util.Common.closeSilently(r10);
        goto L_0x003d;
    L_0x0198:
        r2 = move-exception;
        r2.printStackTrace();	 Catch:{ all -> 0x01b1 }
        r4 = 0;
        r0 = r17;
        r0.setResultAndFinish(r4);	 Catch:{ all -> 0x01b1 }
        r4 = "CameraApp";
        r5 = "doAttach CANCELED";
        com.lge.camera.util.CamLog.d(r4, r5);	 Catch:{ all -> 0x01b1 }
        com.lge.camera.util.Common.closeSilently(r9);	 Catch:{ Exception -> 0x0174 }
        com.lge.camera.util.Common.closeSilently(r10);
        goto L_0x003d;
    L_0x01b1:
        r4 = move-exception;
        com.lge.camera.util.Common.closeSilently(r9);	 Catch:{ Exception -> 0x0174 }
        throw r4;	 Catch:{ Exception -> 0x0174 }
    L_0x01b6:
        r4 = move-exception;
        com.lge.camera.util.Common.closeSilently(r10);
        throw r4;
    L_0x01bb:
        r15 = new android.content.Intent;
        r15.<init>();
        r0 = r17;
        r4 = r0.mSaveUri;
        r15.setData(r4);
        r4 = -1;
        r0 = r17;
        r0.setResultAndFinish(r4, r15);
        goto L_0x003d;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.Mediator.doAttachSaveUri():void");
    }

    private void sendResultIntent() {
        Uri savedUri = getSavedUri();
        if (savedUri != null) {
            Bundle newExtras = new Bundle();
            newExtras.putParcelable("output", savedUri);
            CamLog.d(FaceDetector.TAG, "mSavedImageUri: " + savedUri);
            Intent intent = new Intent();
            intent.setData(savedUri);
            intent.putExtras(newExtras);
            setResultAndFinish(-1, intent);
            return;
        }
        setResultAndFinish(0);
    }

    private boolean checkFreespace() {
        CamLog.d(FaceDetector.TAG, "CheckFreespace");
        Uri savedUri = getSavedUri();
        String defaultFileExtension = getFileExtension();
        if (savedUri == null || this.mStorageController == null || savedUri.getPath() == null) {
            return false;
        }
        String checkFilePath = this.mStorageController.getCurrentStorageDirectory() + getSavedFileName() + defaultFileExtension;
        CamLog.d(FaceDetector.TAG, "cameraCaptureFilePath: " + checkFilePath);
        File originalFile = new File(checkFilePath);
        if (this.mSaveUri == null) {
            return false;
        }
        CamLog.d(FaceDetector.TAG, "FilePathFromRequestApp: " + this.mSaveUri.getPath());
        int requestAppStorageID = getRequestedAttachFileStorage();
        if (this.mStorageController == null || originalFile == null) {
            return false;
        }
        CamLog.d(FaceDetector.TAG, "mStorageController.getFreeSpace(RequestAppStorageID = " + requestAppStorageID + "): " + this.mStorageController.getFreeSpace(requestAppStorageID));
        CamLog.d(FaceDetector.TAG, "OriImage.length(): " + originalFile.length());
        if (this.mStorageController.getFreeSpace(requestAppStorageID) > originalFile.length()) {
            return true;
        }
        return false;
    }

    private int getRequestedAttachFileStorage() {
        String requestedFilePath = this.mSaveUri.getPath();
        if (requestedFilePath.contains(getStorageController().EXTERNAL_STORAGE_DIR)) {
            return 0;
        }
        if (requestedFilePath.contains(getStorageController().INTERNAL_STORAGE_DIR)) {
            return 1;
        }
        return 1;
    }

    private void removeOrgFile() {
        CamLog.d(FaceDetector.TAG, "===>removeOrgFile!!");
        Uri savedUri = getSavedUri();
        if (savedUri != null) {
            String fullPath = Common.getFileFullPathFromUri(getContentResolver(), savedUri);
            if (fullPath == null) {
                CamLog.d(FaceDetector.TAG, "FullPath == null");
                return;
            }
            CamLog.d(FaceDetector.TAG, "===>removeOrgFile!! FullPath: " + fullPath);
            try {
                File orgFile = new File(fullPath);
                if (orgFile.exists() && orgFile.delete()) {
                    CamLog.d(FaceDetector.TAG, "===>removeOrgFile!! delete sucess ");
                    ImageManager.deleteImage(getContentResolver(), savedUri);
                }
            } catch (Exception e) {
                CamLog.d(FaceDetector.TAG, "delete error" + e);
            }
        }
    }

    private Uri getSavedUri() {
        if (getApplicationMode() == 0) {
            return this.mSavedImageUri;
        }
        return this.mSavedVideoUri;
    }

    private String getFileExtension() {
        if (getApplicationMode() == 0) {
            return CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
        }
        VideoFile videoFile = getVideoFile();
        if (videoFile != null) {
            return videoFile.getFileExtension();
        }
        return VideoFile.VIDEO_EXTENSION_MP4;
    }

    public boolean checkSlowMotionMode() {
        return false;
    }

    public boolean checkShotModeForZoomInOut() {
        return true;
    }

    public boolean isTimerShotCountdown() {
        return false;
    }

    public void setTimerShotCountdown(boolean value) {
    }

    public void setTimerSetting(int value) {
    }

    public boolean stopTimerShot() {
        return true;
    }

    public void showPanoramaView() {
    }

    public void removePanoramaView() {
    }

    public void startPanorama() {
    }

    public boolean isPanoramaStarted() {
        return false;
    }

    public boolean isSynthesisInProgress() {
        return false;
    }

    public boolean isPanoramaUIShown() {
        return true;
    }

    public void stopPanorama() {
    }

    public boolean isPanoramaUpdatebutton() {
        return false;
    }

    public void showFreePanoramaBlackBg() {
    }

    public void removeFreePanoramaBlackBg() {
    }

    public void showFreePanoramaView() {
    }

    public void removeFreePanoramaView() {
    }

    public void startFreePanorama() {
    }

    public void stopFreePanorama() {
    }

    public void startFreePanoramaEngine(Parameters parameters) {
    }

    public void stopFreePanoramaEngine(Parameters parameters) {
    }

    public void restartFreePanorama() {
    }

    public void hideFreePanoramaTakingGuide() {
    }

    public int[] getFreePanoramaResultSize() {
        return null;
    }

    public int getFreePanoramaStatus() {
        return 0;
    }

    public int getFreePanoramaEngineStatus() {
        return 0;
    }

    public void setRemoveFreePanoramaBlackBg(boolean remove) {
    }

    public void startFreePanoramaRotation(int degree, boolean animation) {
    }

    public void startPlanePanorama() {
    }

    public void stopPlanePanorama() {
    }

    public void startPlanePanoramaEngine() {
    }

    public void stopPlanePanoramaEngine() {
    }

    public void removePlanePanoramaView() {
    }

    public int[] getPlanePanoramaResultSize() {
        return null;
    }

    public void startPlanePanoramaRotation(int degree, boolean animation) {
    }

    public int getPlanePanoramaStatus() {
        return 0;
    }

    public void doCamcorderContinuousFocusCallback(boolean focusedState) {
    }

    public boolean checkCamcorderStop(int repeatCount, boolean useBackKey) {
        return true;
    }

    public void doVoiceShutterIndicatorUpdateHandler() {
    }

    public void updateVoiceShutterIndicator(boolean show) {
    }

    public void showRequestedSizeLimit() {
    }

    public boolean stopByUserAction() {
        return false;
    }

    public ArrayList<Uri> getImageListUri() {
        return null;
    }

    public boolean hasSaveURI() {
        return false;
    }

    public void removeAllImageList() {
    }

    public void setStartrecordingdegree(int degree) {
    }

    public boolean getAudiozoomStart() {
        return true;
    }

    public void setAudiozoomStart(boolean mAudiozoomStart) {
    }

    public boolean getAudiozoomStartInRecording() {
        return false;
    }

    public void setAudiozoomStartInRecording(boolean start) {
    }

    public void startAudiozoom() {
    }

    public void updateAudiozoom(boolean updateangle, int zoomvalue) {
    }

    public void setForced_audiozoom(boolean isEnable) {
    }

    public void resetAudioZoomMenu() {
    }

    public void setAudioZoomGuideViewLayout(int width, int height, int marginLeft) {
    }

    public void setHeadsetstate(int nConnect) {
    }

    public int getHeadsetstate() {
        return 0;
    }

    public void stopAudiozoom() {
    }

    public void setAudiozoombuttonstate() {
    }

    public boolean isRecordingControllerInit() {
        return true;
    }

    public void recordingControllerHide() {
    }

    public void recordingControllerShow() {
    }

    public void startRecordingControllerRotation(int degree) {
    }

    public void startAudioZoomContollerRotation(int degree) {
    }

    public boolean isMMSRecording() {
        return false;
    }

    public void setScaleWidthHeight(float ScaleWidthHeight) {
    }

    public void readVideoIntentExtras() {
    }

    public long getRequestedVideoSizeLimit() {
        return 0;
    }

    public void hideOsdByForce() {
    }

    public boolean getMediaUSBConnectAtStartRecord() {
        return true;
    }

    public void setMediaUSBConnectAtStartRecord(boolean Connect) {
    }

    public boolean isStopRecordingByMountedAction() {
        return false;
    }

    public void startRecording() {
    }

    public void stopRecording() {
    }

    public void stopRecording(boolean isFromMountedAction) {
    }

    public void pauseRecording() {
    }

    public void resumeRecording() {
    }

    public void stopRecordingByPausing() {
    }

    public void setBackKeyRecStop(boolean con) {
    }

    public long getStartTime() {
        return 0;
    }

    public void setEndTime(long endTime) {
    }

    public void resumeUpdateReordingTime() {
    }

    public boolean isAvailableResumeVideo() {
        return true;
    }

    public boolean getIsFileSizeLimitReached() {
        return false;
    }

    public void setIsFileSizeLimitReached(boolean set) {
    }

    public long getRecordingSizeLimit() {
        return 0;
    }

    public long getRecordingDurationLimit() {
        return 0;
    }

    public long getVideoFileSize() {
        return 0;
    }

    public VideoFile getVideoFile() {
        return null;
    }

    public boolean checkUpdateThumbnail() {
        return true;
    }

    public void addMMSTexture(PreferenceGroup pg) {
    }

    public int getMaxVideoDurationInMs() {
        return 0;
    }

    public boolean needProgressBar() {
        return false;
    }

    public void showRecoridngStopButton() {
    }

    public void hideRecoridngStopButton() {
    }

    public boolean getBackKeyRecStop() {
        return false;
    }

    public void setRecIndicatorLayout(int width, int height, int leftMargin) {
    }

    public boolean isRecordedLengthTooShort() {
        return false;
    }

    public void facePreviewInitController() {
    }

    public void initFaceDetectInfo() {
    }

    public void startFaceDetection(boolean bHasUI) {
    }

    public void stopFaceDetection() {
    }

    public boolean isfacePreviewInitialized() {
        return true;
    }

    public boolean savePicture(byte[] data, Bitmap bitmap, boolean useTimeMachine, int timeMachineTempFileCount) {
        return true;
    }

    public boolean saveImageSavers(byte[] jpegData, Bitmap bitmap, int degree, boolean isSetLastThumb, boolean isBurstFirst) {
        return true;
    }

    public int getQueueCount() {
        return 0;
    }

    public void waitSaveImageTreadDone() {
    }

    public void waitAvailableQueueCount(int availableCount) {
    }

    public void setFaceBeutyShotParameter(int mValue) {
    }

    public int getTimerCaptureDelay() {
        return 0;
    }

    public void startTimerShot() {
    }

    public String getPreviousShotModeString() {
        return "";
    }

    public void setPreviousShotModeString(String strShotMode) {
    }

    public String getPreviousPictureSize() {
        return null;
    }

    public void setPreviousPictureSize(String size) {
    }

    public String getPreviousRecordModeString() {
        return "";
    }

    public void setPreviousRecordModeString(String strRecordMode) {
    }

    public void setTimeMachineShot(Parameters parameters, int zsl_buffer) {
    }

    public boolean getTimeMachinePictures() {
        return false;
    }

    public void setTimemachineHasPictures(boolean has) {
    }

    public boolean isTimemachineHasPictures() {
        return false;
    }

    public void showBubblePopupVisibility(int popupType, long duration, boolean show) {
    }

    public boolean deleteTimeMachineImages() {
        return true;
    }

    public boolean deleteClearShotImages() {
        return true;
    }

    public boolean deleteRefocusShotImages() {
        return true;
    }

    public void setRefocusShotHasPictures(boolean has) {
    }

    public boolean isRefocusShotHasPictures() {
        return false;
    }

    public boolean getRefocusPictures() {
        return false;
    }

    public void smartShutterEnable(boolean enable) {
    }

    public void setFullFrameContinuousShot(Parameters parameters, int bufferNum) {
    }

    public boolean isContinuousShotAlived() {
        return false;
    }

    public void setTimerAndSceneSmartShutterEnable(Parameters parameters, boolean timer, boolean scene, boolean enable) {
    }

    public void setPanoramaEngine() {
    }

    public void doModuleAfterCaptureForB2Model() {
    }

    public void startHeatingwarning() {
    }

    public void stopHeatingwarning() {
    }

    public void showHeatingwarning() {
    }

    public boolean beDirectlyGoingToCropGallery() {
        return false;
    }

    public Uri getSaveURI() {
        return null;
    }

    public ArrayList<Integer> getImageListRotation() {
        return null;
    }

    public void setContinuousShotAlived(boolean alived) {
    }

    public boolean saveTimeMachinePicture(byte[] data, int timeMachineTempFileCount) {
        return true;
    }

    public boolean saveClearShotPicture(byte[] data, int timeMachineTempFileCount) {
        return true;
    }

    public boolean saveRefocusShotPicture(byte[] data, int timeMachineTempFileCount) {
        return true;
    }

    public void saveRefocusShotMap(byte[] data) {
    }

    public void setRefocusShotPreviewGuideVisibility(boolean show) {
    }

    public void setRecordingTime_realduration(long duration) {
    }

    public void setSceneModeForAdvanced(Parameters parameters, String sceneMode) {
    }

    public void doSmartCameraModeCallback(int[] data) {
    }

    public void setSmartCameraMode(LGParameters lgParams, boolean enable) {
    }

    public void setBurstShotStop(boolean stop) {
    }

    public boolean isBurstShotStop() {
        return false;
    }

    public void setFocalLength(float focalLength) {
    }

    public void setSmartModeForPictureSize(String PictureSize) {
    }

    public String getSmartModeForPictureSize() {
        return null;
    }

    public String getAudiozoomvalue() {
        return null;
    }

    public void setAudiozoomvalue(String value) {
    }

    public boolean isAudiozoom_ExceptionCase(boolean checkRotation) {
        return false;
    }

    public void setAudiozoom_ExceptionCase(boolean isOccured) {
    }

    public void doAfterSaveImageSavers() {
    }

    public void setCaptureData(byte[] data) {
    }

    public void onFaceDetectionFromHal(Face[] faces) {
    }

    public void startFaceDetectionFromHal(boolean bHasUI) {
    }

    public void stopFaceDetectionFromHal() {
    }

    public int[] getDualCameraPictureSize() {
        return null;
    }

    public void setCurrentIAMode(int currentIAMode) {
    }

    public int getCurrentIAMode() {
        return 0;
    }

    public void setVideoFlash(boolean on) {
    }

    public void changeLiveSnapshotMaxFileSize(long size) {
    }

    public void runGestureEngine(boolean useCallback) {
    }

    public void releaseGestureEngine() {
    }

    public void showGestureGuide() {
    }

    public void hideGestureGuide() {
    }

    public void putPreviewFrameForGesture(byte[] data, Camera camera) {
    }

    public boolean isGestureShotActivated() {
        return false;
    }

    public void setGestureShotActivated(boolean set) {
    }

    public void startGestureEngine() {
    }

    public void stopGestureEngine() {
    }
}
