package com.lge.camera;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import com.lge.camera.Mediator.ActivityBridge;
import com.lge.camera.command.Command;
import com.lge.camera.components.CameraCoverView;
import com.lge.camera.controller.EnteringViewController;
import com.lge.camera.listeners.ExtraTouchEventListener;
import com.lge.camera.listeners.OnKeyEventListener;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.properties.ShutterSoundProperties;
import com.lge.camera.properties.StorageProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AppControlUtil;
import com.lge.camera.util.AudioUtil;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CheckStatusManager;
import com.lge.camera.util.Common;
import com.lge.camera.util.TelephonyUtil;
import com.lge.olaworks.library.FaceDetector;

public abstract class CameraActivity extends Activity implements ActivityBridge {
    public static Activity mCameraAppAct;
    private boolean configurationChanging;
    protected ExtraTouchEventListener mExtraTouchEventListener;
    protected Uri mFileUri;
    protected int mFromVolumeKey;
    private int mOldHardKeyboardHidden;
    protected OnKeyEventListener mOnKeyEventListener;
    private PhoneStateListener mPhoneStateListener;
    protected boolean mPostviewRequestDeleteDone;
    protected boolean mPostviewRequestDoAttach;
    protected boolean mPostviewRequestReturn;
    protected boolean mPostviewRequestSaveDone;
    protected String mRename;
    protected WakeLock mWakeLock;

    protected abstract void changeLocalSetting();

    public abstract void doPhoneStateListenerAction(int i);

    public abstract CameraActivity getActivity();

    public abstract Mediator getMediator();

    protected abstract void releaseEachMode();

    protected abstract void setLocalSetting();

    protected abstract void setStatusForAttach(boolean z);

    protected abstract boolean setThumbnailForPostviewReturn(Bundle bundle);

    public CameraActivity() {
        this.mWakeLock = null;
        this.mFromVolumeKey = 0;
        this.configurationChanging = false;
        this.mOldHardKeyboardHidden = 0;
        this.mRename = null;
        this.mFileUri = null;
        this.mPostviewRequestSaveDone = false;
        this.mPostviewRequestDeleteDone = false;
        this.mPostviewRequestDoAttach = false;
        this.mPostviewRequestReturn = false;
        this.mPhoneStateListener = new PhoneStateListener() {
            public void onCallStateChanged(int state, String incomingNumber) {
                if (CameraActivity.this.getMediator() == null) {
                    CamLog.d(FaceDetector.TAG, "mMediator = null");
                } else {
                    CameraActivity.this.doPhoneStateListenerAction(state);
                }
            }
        };
        Log.i(FaceDetector.TAG, "[Time Info][1] Please check the Time besides CameraApp : Info Touch Recognition, Launcher, Memory Allocation Layout");
        Log.i(FaceDetector.TAG, "[Time Info][2] Camloading Activity Start : Camera UI Initialization " + Common.interimCheckTime(false));
        Common.checkEnteringTime(false);
        CamLog.setUiThreadHashCode(Thread.currentThread().hashCode());
        CamLog.i(FaceDetector.TAG, "construct CameraActivity");
    }

    public void onCreate(Bundle savedInstanceState) {
        CheckStatusManager.setCheckEnterOutSecure(0);
        ProjectVariables.bEnterSetting = true;
        if (ProjectVariables.useTurnOffAnimation()) {
            Common.turnOffAnimation();
        }
        super.onCreate(savedInstanceState);
        initOnCreate();
        mCameraAppAct = this;
        Intent svcIntent = new Intent();
        svcIntent.setClassName("com.lge.appbox.client", "com.lge.appbox.service.AppBoxCommonService");
        svcIntent.putExtra("packagename", getPackageName());
        svcIntent.putExtra("type", "update");
        startService(svcIntent);
        CamLog.d(FaceDetector.TAG, "onCreate()-end ");
    }

    private void initOnCreate() {
        String version_name = null;
        CamLog.d(FaceDetector.TAG, "bEnterSetting =  " + ProjectVariables.bEnterSetting);
        try {
            version_name = getPackageManager().getPackageInfo(getApplicationInfo().packageName, 0).versionName;
        } catch (NameNotFoundException e) {
            CamLog.e(FaceDetector.TAG, "VersionName is not found, ", e);
        }
        Log.d(FaceDetector.TAG, "TIME_CHECK onCreate()-start,Model:" + ModelProperties.readModelName() + ",Version:" + version_name + CameraConstants.RELEASE_DATE);
        handleFromVolumeKeyIntent(true);
        CameraConstants.setLcdSize(Common.getPixelFromDimens(getApplicationContext(), R.dimen.lcd_width), Common.getPixelFromDimens(getApplicationContext(), R.dimen.lcd_height));
        CameraConstants.setSmartCoverSize(Common.getPixelFromDimens(getApplicationContext(), R.dimen.smart_cover_window_width), Common.getPixelFromDimens(getApplicationContext(), R.dimen.smart_cover_window_height), Common.getPixelFromDimens(getApplicationContext(), R.dimen.smart_cover_window_wide_width));
        ModelProperties.setProjectCode();
        ModelProperties.setCarrierCode();
        CamLog.setLogOn(ProjectVariables.isDebugNotSupported());
        ShutterSoundProperties.setShutterSound();
        ShutterSoundProperties.setShutterSoundOff();
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!CameraCoverView.isCoverOpen() || CheckStatusManager.getCheckEnterOutSecure() == 1 || CheckStatusManager.getCheckEnterOutSecure() == 2) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (CheckStatusManager.getCheckEnterOutSecure() == 1 || CheckStatusManager.getCheckEnterOutSecure() == 2) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mExtraTouchEventListener != null) {
            this.mExtraTouchEventListener.executeTouchEvent(event, getMediator());
        }
        return super.onTouchEvent(event);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this.mOnKeyEventListener == null || this.mOnKeyEventListener.onKeyDown(keyCode, event, getMediator())) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (this.mOnKeyEventListener == null || this.mOnKeyEventListener.onKeyUp(keyCode, event, getMediator())) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        if (getMediator() != null) {
            getMediator().onPrepareDialog(id, dialog, args);
        }
        super.onPrepareDialog(id, dialog, args);
    }

    protected Dialog onCreateDialog(int id, Bundle args) {
        if (getMediator() != null) {
            return getMediator().onCreateDialog(id, args);
        }
        return null;
    }

    protected void onStart() {
        CamLog.d(FaceDetector.TAG, "onStart()-start");
        if (getMediator() != null) {
            getMediator().onStart();
            this.mOldHardKeyboardHidden = getResources().getConfiguration().hardKeyboardHidden;
        }
        super.onStart();
        CamLog.d(FaceDetector.TAG, "onStart()-end ");
    }

    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        CamLog.d(FaceDetector.TAG, String.format("start with new intent", new Object[0]));
        if (getMediator() != null) {
            getMediator().setKeepLastCameraMode();
            getMediator().applyCameraChange();
        }
        super.onNewIntent(intent);
    }

    protected void onResume() {
        CamLog.d(FaceDetector.TAG, "onResume() - start");
        if (CheckStatusManager.getCheckEnterOutSecure() != 0 || Common.IS_ENTER_CONDITION) {
            getMediator().updateNavigationBarShape();
            Common.backlightControl(this);
            handleFromVolumeKeyIntent(true);
            getWakeLock();
            AppControlUtil.checkCurrentCoverStatus(getActivity());
            AppControlUtil.setQuickWindowCameraFromIntent(getIntent());
            if (Common.isSecureCamera() || Common.isQuickWindowCameraMode()) {
                getWindow().addFlags(2621440);
            } else if (checkFromVolumeKey()) {
                getWindow().addFlags(4194304);
            } else {
                Common.setWakeLock(this.mWakeLock, true);
            }
            AppControlUtil.setFmRadioOff(getApplicationContext());
            AppControlUtil.setQuickClipScreenCaptureLimit(this);
            setCleanViewAndNavigationBar(false, false);
            ((TelephonyManager) getSystemService("phone")).listen(this.mPhoneStateListener, 32);
            if (this.mExtraTouchEventListener != null) {
                this.mExtraTouchEventListener.setScaleDetectorListener(getMediator());
                this.mExtraTouchEventListener.setGestureDetectorListener(getMediator());
            }
            checkOnResume(getMediator());
            sendBroadcastIntentCameraStarted();
            super.onResume();
            findViewById(R.id.init).getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    CameraActivity.this.findViewById(R.id.init).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    if (CameraActivity.this.checkFromVolumeKey()) {
                        Common.setWakeLock(CameraActivity.this.mWakeLock, true);
                        Common.setWakeLock(CameraActivity.this.mWakeLock, false);
                        PowerManager powerManager = (PowerManager) CameraActivity.this.getSystemService("power");
                        CameraActivity.this.mWakeLock = powerManager.newWakeLock(1, getClass().getName());
                        CameraActivity.this.mWakeLock.setReferenceCounted(false);
                        Common.setWakeLock(CameraActivity.this.mWakeLock, true);
                    }
                }
            });
            Common.galleryCacheDuringCameraApp(getApplicationContext(), true);
            CamLog.d(FaceDetector.TAG, "onResume() - end");
            return;
        }
        Common.IS_ENTER_CONDITION = true;
        super.onResume();
        CamLog.d(FaceDetector.TAG, "not available by checkEnterApplication");
    }

    protected void onPause() {
        CamLog.d(FaceDetector.TAG, "onPause()-start ");
        if (checkFromVolumeKey()) {
            getWindow().clearFlags(4718592);
            handleFromVolumeKeyIntent(false);
        }
        setCleanViewAndNavigationBar(false, true);
        if (getMediator() != null) {
            getMediator().getPreviewController().setEffectRecorderPausing(true);
            getMediator().deleteProgressDialog();
            getMediator().doCommand(Command.HIDE_PIP_SUBWINDOW_RESIZE_HANDLER);
            getMediator().hideSmartZoomFocusView();
            Common.galleryCacheDuringCameraApp(getApplicationContext(), false);
            if (!getMediator().isPausing()) {
                getMediator().onPause();
            }
        }
        if (ProjectVariables.useTurnOffAnimation()) {
            Common.turnOnAnimation();
        }
        if (FunctionProperties.isSupportQClipCustomization()) {
            AppControlUtil.resetQuickClipScreenCaptureLimit(this);
            AppControlUtil.resetQClipHotkeyFlag();
        }
        CheckStatusManager.setEnterCheckComplete(false);
        AudioUtil.checkAudioFocus(getApplicationContext());
        Common.setWakeLock(this.mWakeLock, false);
        releaseEachMode();
        ((TelephonyManager) getSystemService("phone")).listen(this.mPhoneStateListener, 0);
        if (this.mExtraTouchEventListener != null) {
            this.mExtraTouchEventListener.releaseScaleDetectorListener();
            this.mExtraTouchEventListener.releaseGestureDetectorListener();
        }
        sendBroadcastIntentCameraEnded();
        super.onPause();
        AppControlUtil.setEnableRotateNaviataionBar(getActivity(), false);
        Common.printRunningTask(getApplicationContext());
        if (!getMediator().isChangeMode()) {
            getMediator().setSlimPortDegree(0);
            CamLog.d(FaceDetector.TAG, "set property done: 0");
        }
        CamLog.d(FaceDetector.TAG, "onPause()-end ");
    }

    private void sendBroadcastIntentCameraStarted() {
        if (!CameraConstants.IS_CHANGE_MODE_STATUS) {
            if (Common.isQuickWindowCameraMode() && getMediator().isChangingToOtherActivity()) {
                CamLog.i(FaceDetector.TAG, "Because QuickCover is closed, Do not send broadcast ENTER intent.");
            } else {
                CamLog.i(FaceDetector.TAG, "Send broadcast : com.lge.intent.action.FLOATING_WINDOW_ENTER_LOWPROFILE, Extra value : hide is true");
                Intent intent = new Intent("com.lge.intent.action.FLOATING_WINDOW_ENTER_LOWPROFILE");
                intent.putExtra("hide", true);
                intent.putExtra("package", getPackageName());
                sendBroadcast(intent);
                sendBroadcast(new Intent("com.lge.camera.action.START_CAMERA_APP"));
            }
        }
        CameraConstants.IS_CHANGE_MODE_STATUS = false;
    }

    private void sendBroadcastIntentCameraEnded() {
        if (getMediator() != null) {
            if (getMediator().isSendBroadcastIntent()) {
                if (Common.isSmartCoverClosed() && getMediator().getApplicationMode() == 1 && getMediator().getVideoState() != 5) {
                    CamLog.i(FaceDetector.TAG, "Because QuickCover is closed, Do not send broadcast EXIT intent.");
                } else {
                    CamLog.i(FaceDetector.TAG, "Send broadcast : com.lge.intent.action.FLOATING_WINDOW_EXIT_LOWPROFILE");
                    Intent intent = new Intent("com.lge.intent.action.FLOATING_WINDOW_EXIT_LOWPROFILE");
                    intent.putExtra("package", getPackageName());
                    sendBroadcast(intent);
                    sendBroadcast(new Intent("com.lge.camera.action.STOP_CAMERA_APP"));
                }
                CameraConstants.IS_CHANGE_MODE_STATUS = false;
            }
            getMediator().setIsSendBroadcastIntent(true);
        }
    }

    protected void onStop() {
        CamLog.d(FaceDetector.TAG, "onStop()-start ");
        if (ProjectVariables.useTurnOffAnimation()) {
            Common.turnOnAnimation();
        }
        if (getMediator() != null) {
            getMediator().onStop();
        }
        super.onStop();
        CamLog.d(FaceDetector.TAG, "onStop()-end ");
    }

    protected void onDestroy() {
        CamLog.d(FaceDetector.TAG, "onDestroy()-start ");
        if (ProjectVariables.useTurnOffAnimation()) {
            Common.turnOnAnimation();
        }
        if (getMediator() != null) {
            getMediator().onDestroy();
        }
        Common.setWakeLock(this.mWakeLock, false);
        this.mWakeLock = null;
        super.onDestroy();
        this.mOnKeyEventListener = null;
        this.mExtraTouchEventListener = null;
        mCameraAppAct = null;
        CamLog.d(FaceDetector.TAG, "onDestroy()-end ");
    }

    public void onUserInteraction() {
        super.onUserInteraction();
        if (getMediator() != null) {
            getMediator().onUserInteraction();
        }
    }

    public boolean isConfigurationChanging() {
        return this.configurationChanging;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        CamLog.d(FaceDetector.TAG, "onConfigurationChanged() START " + newConfig.orientation);
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == 2) {
            CamLog.d(FaceDetector.TAG, "RETURN by orientation " + newConfig.orientation);
            return;
        }
        boolean doNotChange = false;
        if (!(this.mOldHardKeyboardHidden == newConfig.hardKeyboardHidden || getMediator().isPreviewOnGoing())) {
            doNotChange = true;
            this.mOldHardKeyboardHidden = newConfig.hardKeyboardHidden;
        }
        if (getMediator() != null) {
            if ((doNotChange && getMediator().isControllerInitialized() && getMediator().isPreviewing()) || getMediator().getInCaptureProgress()) {
                CamLog.d(FaceDetector.TAG, "RETURN " + newConfig.orientation + " hardKeyboardHidden=" + newConfig.hardKeyboardHidden + " isControllerInitialized=" + getMediator().isControllerInitialized() + " isPreviewing=" + getMediator().isPreviewing() + ", getInCaptureProgress()=" + getMediator().getInCaptureProgress());
                return;
            }
            this.configurationChanging = true;
            getMediator().removePostAllRunnables();
            getMediator().releaseControllerForReInitialize();
            setContentView(R.layout.init);
            getMediator().resetControllerForReInitialize();
            this.configurationChanging = false;
        }
        CamLog.d(FaceDetector.TAG, "onConfigurationChanged() END " + newConfig.orientation);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        Mediator mediator = getMediator();
        CamLog.d(FaceDetector.TAG, "onWindowFocusChanged() hasFocus ? =" + hasFocus);
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mediator.updateNavigationBarShape();
            String value = mediator.getSettingValue(Setting.KEY_VOICESHUTTER);
            CamLog.d(FaceDetector.TAG, "## SetVoiceShutterMode : " + value);
            if (FunctionProperties.isVoiceShutter()) {
                AudioManager am = (AudioManager) getSystemService("audio");
                if (mediator.getApplicationMode() == 0 && value.equals(CameraConstants.SMART_MODE_ON) && !TelephonyUtil.phoneInCall(mediator.getApplicationContext()) && am.isMusicActive() && mediator.isPreviewing()) {
                    mediator.setSetting(Setting.KEY_VOICESHUTTER, CameraConstants.SMART_MODE_OFF);
                    mediator.doCommandUi(Command.SET_VOICE_SHUTTER);
                }
            }
        }
    }

    protected void checkOnResume(Mediator mediator) {
        if (mediator != null) {
            mediator.onResume();
            mediator.setLanguageType(getBaseContext().getResources().getConfiguration().locale.getLanguage());
        }
    }

    protected boolean resultForPostview(Intent data) {
        if (!(data == null || getMediator() == null)) {
            Bundle extras = data.getExtras();
            if (extras != null && (resultForSaveDone(extras) || resultForDeleteDone(extras) || resultForAttach(extras) || resultForPostviewReturn(extras))) {
                setZoomForManualAntiBanding(extras);
                setMemoryCheck(extras);
                return true;
            }
        }
        return false;
    }

    protected boolean resultForSaveDone(Bundle extras) {
        return false;
    }

    protected boolean resultForDeleteDone(Bundle extras) {
        if (extras == null || !extras.getBoolean("delete_done")) {
            return false;
        }
        this.mPostviewRequestDeleteDone = true;
        CamLog.d(FaceDetector.TAG, "REQUEST_CODE_POSTVIEW delete done");
        return true;
    }

    protected boolean resultForAttach(Bundle extras) {
        if (extras != null) {
            boolean postview_mode = extras.getBoolean("postview_mode");
            if (extras.getBoolean("doAttach")) {
                this.mPostviewRequestDoAttach = true;
                setStatusForAttach(postview_mode);
                this.mFileUri = (Uri) extras.getParcelable("recent_uri");
                getMediator().setSavedImageUri(this.mFileUri);
                getMediator().setSavedFileName(extras.getString("file_name"));
                CamLog.d(FaceDetector.TAG, "REQUEST_CODE_POSTVIEW doAttach uri:" + this.mFileUri);
                return true;
            }
        }
        return false;
    }

    protected boolean resultForPostviewReturn(Bundle extras) {
        if (extras == null || !extras.getBoolean("postview_return")) {
            return false;
        }
        this.mPostviewRequestReturn = true;
        CamLog.d(FaceDetector.TAG, "REQUEST_CODE_POSTVIEW postview_return");
        if (setThumbnailForPostviewReturn(extras)) {
            return true;
        }
        this.mPostviewRequestReturn = false;
        getMediator().enableInput(false);
        return true;
    }

    protected void setZoomForManualAntiBanding(Bundle extras) {
        if (ProjectVariables.isSupportManualAntibanding() && getMediator() != null && getMediator().getZoomController().getZoomBar() != null && getMediator().getCameraId() == 0 && extras != null) {
            String[] curzoom = extras.getStringArray("currentZoom");
            if (curzoom != null) {
                CamLog.d(FaceDetector.TAG, "===> Return zoom: " + curzoom[0]);
                getMediator().setSetting(Setting.KEY_ZOOM, curzoom[0]);
                getMediator().getZoomController().setZoomCursorMaxStep(Integer.parseInt(curzoom[1]));
                getMediator().getZoomController().reset(Integer.parseInt(curzoom[2]));
                return;
            }
            CamLog.w(FaceDetector.TAG, "extras.getStringArray('currentZoom') is NULL Value, plz check it up ASAP !!!");
        }
    }

    private void setMemoryCheck(Bundle extras) {
        if (extras.getBoolean("insert_sdcard") && StorageProperties.isAllMemorySupported()) {
            getMediator().showDialogPopup(26);
            getMediator().setPreferenceMenuEnable(Setting.KEY_STORAGE, true, false);
        }
    }

    public int getPostviewRequestCode() {
        if (this.mPostviewRequestSaveDone) {
            return 4;
        }
        if (this.mPostviewRequestDeleteDone) {
            return 1;
        }
        if (this.mPostviewRequestDoAttach) {
            return 2;
        }
        if (this.mPostviewRequestReturn) {
            return 3;
        }
        return -1;
    }

    public String getPostviewRename() {
        return this.mRename;
    }

    public Uri getPostviewUri() {
        return this.mFileUri;
    }

    public void setPostviewRequestInitCode() {
        this.mPostviewRequestSaveDone = false;
        this.mPostviewRequestDeleteDone = false;
        this.mPostviewRequestDoAttach = false;
        this.mPostviewRequestReturn = false;
        this.mRename = null;
        this.mFileUri = null;
    }

    public void finish() {
        CamLog.d(FaceDetector.TAG, "pre-finish-start");
        if (AppControlUtil.isInLockTask(this)) {
            CamLog.d(FaceDetector.TAG, "return pre-finish-start : because is in lock task mode");
            super.finish();
            return;
        }
        if (!(!ProjectVariables.hasWrongPreviewWhilePauseBug() || getMediator() == null || getMediator().isPausing())) {
            getMediator().onPause();
        }
        super.finish();
        CheckStatusManager.setCheckEnterOutSecure(0);
        if (!(getMediator() == null || getMediator().isChangeMode())) {
            Common.setSecureCamera(false);
        }
        CamLog.d(FaceDetector.TAG, "pre-finish-end");
    }

    public void onEnteringViewClick(View v) {
        if (getMediator() != null) {
            CamLog.d(FaceDetector.TAG, "onEnteringViewClick");
            getMediator().showQuickMenuEnteringGuide(false);
        }
    }

    public void onEnteringDoNotShow(View v) {
        CamLog.d(FaceDetector.TAG, "onEnteringDoNotShow clicked");
        if (getMediator() != null) {
            EnteringViewController.get().mDoNotShowAgain = true;
            getMediator().showQuickMenuEnteringGuide(false);
        }
    }

    public void gotoHelpActivity(String keyString) {
        CamLog.d(FaceDetector.TAG, "display helpContext = " + keyString);
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), CameraHelp.class);
        intent.putExtra(CameraConstants.KEY_STRING_HELP_MENU, keyString);
        intent.putExtra(CameraConstants.KEY_STRING_CAMERA_ID, getMediator().getCameraId());
        intent.putExtra(CameraConstants.KEY_STRING_HELP_MODE, getMediator().getApplicationMode());
        intent.putExtra(Setting.KEY_CAMERA_TIMER, getMediator().getSettingValue(Setting.KEY_CAMERA_TIMER));
        intent.putExtra(CameraConstants.SECURE_CAMERA, Common.isSecureCamera());
        getMediator().setChangingToOtherActivity(true);
        if (getMediator().checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
            getMediator().setRemoveFreePanoramaBlackBg(false);
        }
        startActivityForResult(intent, 4);
    }

    public void onOptionItemViewClick(View v) {
        if (getMediator() != null) {
            CamLog.d(FaceDetector.TAG, "onEnteringViewClick");
            getMediator().onOptionItemViewClick();
        }
    }

    public void setCleanViewAndNavigationBar(boolean inputKeyAction, boolean release) {
        if (ProjectVariables.isSupportKDDICleanView()) {
            SharedPreferences pref = getSharedPreferences(Setting.SETTING_PRIMARY, 0);
            if (pref != null) {
                Intent intent = new Intent(CameraConstants.INTENT_ACTION_SYSTEMUI_CLEAN_VIEW_BUTTON);
                boolean cleanViewSettingOn = pref.getBoolean(CameraConstants.CLEAN_VIEW_ON, true);
                if (release) {
                    intent.putExtra(CameraConstants.CLEAN_VIEW_ENABLE, false);
                } else {
                    intent.putExtra(CameraConstants.CLEAN_VIEW_ENABLE, true);
                    if (inputKeyAction) {
                        getMediator().toggleClearView();
                    } else if (cleanViewSettingOn) {
                        getMediator().clearViewOn(false);
                    } else {
                        getMediator().clearViewOff(false);
                    }
                    Intent i = new Intent();
                    i.setAction(CameraConstants.INTENT_ACTION_CLEAN_VIEW_RECEIVER);
                    intent.putExtra(CameraConstants.CLEAN_VIEW_PENDING_ACTION, PendingIntent.getBroadcast(this, 0, i, 0));
                }
                sendBroadcast(intent);
            }
        }
    }

    public void getWakeLock() {
        this.mWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(checkFromVolumeKey() ? 805306394 : 1, getClass().getName());
        this.mWakeLock.setReferenceCounted(false);
    }

    public void handleFromVolumeKeyIntent(boolean get) {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        if (get) {
            this.mFromVolumeKey = intent.getIntExtra(CameraConstants.USE_WAKELOCK_ONWIDOWFOCUSCHANGED, 0);
            return;
        }
        intent.putExtra(CameraConstants.USE_WAKELOCK_ONWIDOWFOCUSCHANGED, 0);
        this.mFromVolumeKey = 0;
    }

    public boolean checkFromVolumeKey() {
        return this.mFromVolumeKey == 4;
    }
}
