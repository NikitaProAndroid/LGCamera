package com.lge.camera;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemProperties;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.lge.camera.postview.PostViewBatteryReceiver;
import com.lge.camera.postview.PostViewMediaBroadCastReceiver;
import com.lge.camera.postview.PostViewParameters;
import com.lge.camera.postview.PostViewScreenOffReceiver;
import com.lge.camera.postview.PostViewToast;
import com.lge.camera.postview.PostviewDialog;
import com.lge.camera.postview.PostviewMenu;
import com.lge.camera.postview.PostviewMenuAdapter;
import com.lge.camera.postview.PostviewOrientationInfo;
import com.lge.camera.postview.PostviewOrientationInfo.PostviewOrientationInfoFunction;
import com.lge.camera.postview.ReceiverFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.PreferenceProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.PreferenceGroup;
import com.lge.camera.setting.PreferenceInflater;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AppControlUtil;
import com.lge.camera.util.BitmapManager;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CameraHolder;
import com.lge.camera.util.CheckStatusManager;
import com.lge.camera.util.Common;
import com.lge.camera.util.ExifUtil;
import com.lge.camera.util.ImageHandler;
import com.lge.camera.util.ImageManager;
import com.lge.camera.util.ImageRotationOn;
import com.lge.camera.util.MainHandler;
import com.lge.camera.util.SecureImageUtil;
import com.lge.camera.util.Util;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.ISTAudioRecorder;
import com.lge.voiceshutter.library.LGKeyRec;
import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class ShotPostviewActivity extends Activity implements PostviewOrientationInfoFunction, ReceiverFunction {
    public static final int SET_AS_MENU = 1;
    public static final int SHARE_MENU = 0;
    private boolean bConnectedDevice;
    private PostViewBatteryReceiver batteryReceiver;
    protected boolean isFromCreateProcess;
    protected PreferenceGroup mBack3dCamcorderPreferenceGroup;
    protected PreferenceGroup mBack3dCameraPreferenceGroup;
    protected PreferenceGroup mBackCamcorderPreferenceGroup;
    protected PreferenceGroup mBackCameraPreferenceGroup;
    private Camera mCamera;
    private Runnable mCameraOpenRunnable;
    private Thread mCameraOpenThread;
    protected Bitmap mCapturedBitmap;
    protected int mCurrentSelectedIndex;
    private Dialog mDialog;
    OnDismissListener mDismissClickListener;
    protected Runnable mExitInteraction;
    protected PreferenceGroup mFrontCamcorderPreferenceGroup;
    protected PreferenceGroup mFrontCameraPreferenceGroup;
    private MainHandler mHandler;
    protected ImageHandler mImageHandler;
    protected PostviewOrientationInfo mOrientationInfo;
    protected boolean mPause;
    private ArrayList<Runnable> mPostRunnables;
    protected PostViewParameters mPostViewParameters;
    private boolean mSDCardsetting;
    private PostViewScreenOffReceiver mScreenOffReceiver;
    private Runnable mThreadStartRunnable;
    protected PostViewToast mToast;
    private PostViewMediaBroadCastReceiver mediaReceiver;
    protected ArrayList<PostviewMenu> postviewMenuSetAs;
    protected ArrayList<PostviewMenu> postviewMenuShare;

    protected abstract void doPreProcessOnCreate();

    protected abstract void doProcessOnCreate();

    protected abstract void doProcessOnDestroy();

    protected abstract void doProcessOnPause();

    protected abstract void doProcessOnResume();

    protected abstract void postviewShow();

    protected abstract void reloadedPostview();

    protected abstract void setupLayout();

    public ShotPostviewActivity() {
        this.mPostViewParameters = new PostViewParameters();
        this.mediaReceiver = null;
        this.batteryReceiver = null;
        this.mScreenOffReceiver = null;
        this.mToast = new PostViewToast();
        this.mDialog = null;
        this.mHandler = new MainHandler();
        this.mPostRunnables = new ArrayList();
        this.mPause = false;
        this.mSDCardsetting = false;
        this.mFrontCameraPreferenceGroup = null;
        this.mBackCameraPreferenceGroup = null;
        this.mFrontCamcorderPreferenceGroup = null;
        this.mBackCamcorderPreferenceGroup = null;
        this.mBack3dCameraPreferenceGroup = null;
        this.mBack3dCamcorderPreferenceGroup = null;
        this.mCapturedBitmap = null;
        this.mImageHandler = new ImageRotationOn();
        this.mOrientationInfo = new PostviewOrientationInfo(this);
        this.mCurrentSelectedIndex = 0;
        this.isFromCreateProcess = false;
        this.mExitInteraction = new Runnable() {
            public void run() {
                ShotPostviewActivity.this.removePostRunnable(this);
                ShotPostviewActivity.this.finish();
            }
        };
        this.mCamera = null;
        this.mCameraOpenThread = null;
        this.bConnectedDevice = false;
        this.mCameraOpenRunnable = new Runnable() {
            public void run() {
                while (ShotPostviewActivity.this.mCamera == null && !Thread.interrupted()) {
                    CamLog.d(FaceDetector.TAG, "mCameraId: " + ShotPostviewActivity.this.mPostViewParameters.getCameraId());
                    ShotPostviewActivity.this.mCamera = CameraHolder.instance().tryOpen(ShotPostviewActivity.this.mPostViewParameters.getCameraId());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        this.mThreadStartRunnable = new Runnable() {
            public void run() {
                ShotPostviewActivity.this.removePostRunnable(this);
                if (!ShotPostviewActivity.this.isPausing() && !ShotPostviewActivity.this.isFinishing()) {
                    ShotPostviewActivity.this.mCameraOpenThread = new Thread(ShotPostviewActivity.this.mCameraOpenRunnable);
                    ShotPostviewActivity.this.mCameraOpenThread.start();
                }
            }
        };
        this.mDismissClickListener = new OnDismissListener() {
            public void onDismiss(DialogInterface arg0) {
                CamLog.i(FaceDetector.TAG, "mDismissClickListener");
                ShotPostviewActivity.this.mDialog = null;
            }
        };
        this.postviewMenuShare = new ArrayList();
        this.postviewMenuSetAs = new ArrayList();
        Common.APP_SHOTMODE_POSTVIEW_INSTANCE_COUNT += SET_AS_MENU;
        CamLog.i(FaceDetector.TAG, "construct POSTVIEW app_instance_cnt = " + Common.APP_SHOTMODE_POSTVIEW_INSTANCE_COUNT);
    }

    protected void finalize() throws Throwable {
        Common.APP_SHOTMODE_POSTVIEW_INSTANCE_COUNT--;
        CamLog.i(FaceDetector.TAG, "destroy POSTVIEW app_instance_cnt = " + Common.APP_SHOTMODE_POSTVIEW_INSTANCE_COUNT);
        super.finalize();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String str = FaceDetector.TAG;
        StringBuilder append = new StringBuilder().append("Postview onCreate()-start ");
        Object[] objArr = new Object[SET_AS_MENU];
        objArr[0] = ModelProperties.readModelName();
        CamLog.d(str, append.append(String.format("Model name:%s", objArr)).toString());
        configureWindow();
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((WindowManager) getSystemService("window")).getDefaultDisplay().getMetrics(outMetrics);
        CameraConstants.setLcdSize(outMetrics.widthPixels, outMetrics.heightPixels);
        ModelProperties.setProjectCode();
        ModelProperties.setCarrierCode();
        this.mPostViewParameters.setPostViewParameters(getApplicationContext(), getIntent());
        boolean secureCamera = this.mPostViewParameters.isSecureCamera();
        if (secureCamera) {
            this.mScreenOffReceiver = new PostViewScreenOffReceiver(this);
            registerScreenOffReceiver(this.mScreenOffReceiver);
        }
        Common.configureWindowFlag(getWindow(), true, secureCamera);
        this.mOrientationInfo.setOrientationByWindowOrientation();
        this.mOrientationInfo.setOrientationByPreview(this.mPostViewParameters.getPreviewOrientation());
        this.mOrientationInfo.setOrientationListener();
        this.mOrientationInfo.enableOrientationListener(true);
        this.mediaReceiver = new PostViewMediaBroadCastReceiver(this);
        registerMediaReceiver(this.mediaReceiver);
        this.batteryReceiver = new PostViewBatteryReceiver(this);
        registerBatteryReceiver(this.batteryReceiver);
        PreferenceInflater inflater = new PreferenceInflater(this);
        this.mFrontCameraPreferenceGroup = (PreferenceGroup) inflater.inflate(PreferenceProperties.getFrontCameraPreferenceResource());
        this.mBackCameraPreferenceGroup = (PreferenceGroup) inflater.inflate(PreferenceProperties.getBackCameraPreferenceResource());
        this.mFrontCamcorderPreferenceGroup = (PreferenceGroup) inflater.inflate(PreferenceProperties.getFrontCamcorderPreferenceResource());
        this.mBackCamcorderPreferenceGroup = (PreferenceGroup) inflater.inflate(PreferenceProperties.getBackCamcorderPreferenceResource());
        if (ModelProperties.is3dSupportedModel()) {
            this.mBack3dCameraPreferenceGroup = (PreferenceGroup) inflater.inflate(PreferenceProperties.getBack3dCameraPreferenceResource());
            this.mBack3dCamcorderPreferenceGroup = (PreferenceGroup) inflater.inflate(PreferenceProperties.getBack3dCamcorderPreferenceResource());
        }
        setContentView(R.layout.postview_shotmode_main);
        doPreProcessOnCreate();
        setActionBar();
        setupLayout();
        postviewShow();
        setFileName();
        doProcessOnCreate();
        AppControlUtil.disableNavigationButton(getActivity());
        CamLog.d(FaceDetector.TAG, "Postview onCreate()-end ");
    }

    protected void onRestart() {
        CamLog.d(FaceDetector.TAG, "Postview onRestart()-start ");
        CheckStatusManager.setEnterCheckComplete(false);
        super.onRestart();
        CamLog.d(FaceDetector.TAG, "Postview onRestart()-end");
    }

    protected void onResume() {
        CamLog.d(FaceDetector.TAG, "Postview onResume() - start");
        if (!CheckStatusManager.checkEnterApplication(this, true)) {
            super.onResume();
            CamLog.d(FaceDetector.TAG, "onResume()-end, checkEnterApplication");
            CheckStatusManager.checkCameraOut(this, null);
        } else if (CameraConstants.MEDIA_RECEIVER_FINISHED) {
            CamLog.d(FaceDetector.TAG, "Destroy Postview when media ejected");
            CameraConstants.MEDIA_RECEIVER_FINISHED = false;
            super.onResume();
            finish();
        } else {
            if (!this.isFromCreateProcess) {
                Common.configureWindowFlag(getWindow(), true, this.mPostViewParameters.isSecureCamera());
            }
            Common.backlightControl(this);
            AppControlUtil.setFmRadioOff(getApplicationContext());
            this.mPause = false;
            if (this.mOrientationInfo != null) {
                try {
                    int displayOrientationSetting = System.getInt(getContentResolver(), "accelerometer_rotation");
                    this.mOrientationInfo.setDisplayOrientationSettingValue(displayOrientationSetting);
                    CamLog.i(FaceDetector.TAG, "DisplayOrientationSetting is " + displayOrientationSetting);
                } catch (SettingNotFoundException e) {
                    CamLog.e(FaceDetector.TAG, "SettingNotFoundException:", e);
                }
                if (!this.mOrientationInfo.getOrientationListenerEnable()) {
                    this.mOrientationInfo.setOrientationListener();
                    this.mOrientationInfo.enableOrientationListener(true);
                }
            }
            connectCameraDevice();
            super.onResume();
            doProcessOnResume();
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    protected void onPause() {
        CamLog.d(FaceDetector.TAG, String.format("Postview onPause() - start", new Object[0]));
        this.mPause = true;
        Common.configureWindowFlag(getWindow(), true, false);
        doProcessOnPause();
        if (this.mOrientationInfo != null) {
            this.mOrientationInfo.enableOrientationListener(false);
            this.mOrientationInfo.releaseOrientationListener();
        }
        removeExitInteraction();
        removePostAllRunnables();
        checkAndDisconnectCameraDevice();
        super.onPause();
        CamLog.d(FaceDetector.TAG, String.format("Postview onPause() - end", new Object[0]));
    }

    protected void onDestroy() {
        CamLog.d(FaceDetector.TAG, String.format("Postview onDestroy()", new Object[0]));
        if (this.mOrientationInfo != null) {
            this.mOrientationInfo.enableOrientationListener(false);
            this.mOrientationInfo.releaseOrientationListener();
            this.mOrientationInfo = null;
        }
        doProcessOnDestroy();
        removePostAllRunnables();
        if (this.mPostViewParameters != null) {
            this.mPostViewParameters.clearParameters();
            this.mPostViewParameters = null;
        }
        if (this.mediaReceiver != null) {
            unregisterReceiver(this.mediaReceiver);
            this.mediaReceiver.unbind();
            this.mediaReceiver = null;
        }
        if (this.batteryReceiver != null) {
            unregisterReceiver(this.batteryReceiver);
            this.batteryReceiver.unbind();
            this.batteryReceiver = null;
        }
        if (this.mScreenOffReceiver != null) {
            unregisterReceiver(this.mScreenOffReceiver);
            this.mScreenOffReceiver.unbind();
            this.mScreenOffReceiver = null;
        }
        if (this.mToast != null) {
            this.mToast.unbind();
            this.mToast = null;
        }
        this.bConnectedDevice = false;
        this.mHandler = null;
        this.mPostRunnables = null;
        this.mExitInteraction = null;
        this.mCamera = null;
        if (this.mFrontCameraPreferenceGroup != null) {
            this.mFrontCameraPreferenceGroup.close();
            this.mFrontCameraPreferenceGroup = null;
        }
        if (this.mBackCameraPreferenceGroup != null) {
            this.mBackCameraPreferenceGroup.close();
            this.mBackCameraPreferenceGroup = null;
        }
        if (this.mFrontCamcorderPreferenceGroup != null) {
            this.mFrontCamcorderPreferenceGroup.close();
            this.mFrontCamcorderPreferenceGroup = null;
        }
        if (this.mBackCamcorderPreferenceGroup != null) {
            this.mBackCamcorderPreferenceGroup.close();
            this.mBackCamcorderPreferenceGroup = null;
        }
        if (this.mBack3dCameraPreferenceGroup != null) {
            this.mBack3dCameraPreferenceGroup.close();
            this.mBack3dCameraPreferenceGroup = null;
        }
        if (this.mBack3dCamcorderPreferenceGroup != null) {
            this.mBack3dCamcorderPreferenceGroup.close();
            this.mBack3dCamcorderPreferenceGroup = null;
        }
        if (!(this.mCapturedBitmap == null || this.mCapturedBitmap.isRecycled())) {
            this.mCapturedBitmap.recycle();
        }
        this.mCapturedBitmap = null;
        this.mImageHandler = null;
        this.mDialog = null;
        if (PostviewDialog.getPostviewDialog() != null) {
            PostviewDialog.getPostviewDialog().unbind();
        }
        Util.recursiveRecycle(getWindow().getDecorView());
        super.onDestroy();
    }

    public void finish() {
        boolean z = false;
        CamLog.d(FaceDetector.TAG, "Postview finish()");
        Intent intent = getIntent();
        intent.putExtra("postview_return", true);
        if (Common.isFaceUnlock()) {
            checkAndDisconnectCameraDevice();
        }
        if (this.mPostViewParameters != null) {
            try {
                if (!(this.mPostViewParameters.getUriList() == null || this.mPostViewParameters.getUriList().size() == 0)) {
                    intent.putExtra("recent_uri", (Parcelable) this.mPostViewParameters.getUriList().get(this.mPostViewParameters.getUriList().size() - 1));
                    intent.putExtra("file_name", this.mPostViewParameters.getSaveFileName());
                    CamLog.i(FaceDetector.TAG, "Postview intent:file_name = " + this.mPostViewParameters.getSaveFileName());
                }
                if (ProjectVariables.isSupportManualAntibanding() && this.mPostViewParameters.getCurrentZoom() != null) {
                    intent.putExtra("currentZoom", this.mPostViewParameters.getCurrentZoom());
                    CamLog.d(FaceDetector.TAG, "===> mcurzoom : " + this.mPostViewParameters.getCurrentZoom()[0]);
                }
                if (this.mSDCardsetting) {
                    intent.putExtra("insert_sdcard", true);
                }
                this.bConnectedDevice = !this.mPostViewParameters.isIsAttachIntent();
                setResult(100, intent);
                super.finish();
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "Exception!", e);
            } finally {
                if (ProjectVariables.isSupportManualAntibanding() && this.mPostViewParameters.getCurrentZoom() != null) {
                    intent.putExtra("currentZoom", this.mPostViewParameters.getCurrentZoom());
                    CamLog.d(FaceDetector.TAG, "===> mcurzoom : " + this.mPostViewParameters.getCurrentZoom()[0]);
                }
                if (this.mSDCardsetting) {
                    intent.putExtra("insert_sdcard", true);
                }
                if (!this.mPostViewParameters.isIsAttachIntent()) {
                    z = true;
                }
                this.bConnectedDevice = z;
                setResult(100, intent);
                super.finish();
            }
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case Tag.GPS_DEST_LAT_REF /*19*/:
            case Tag.GPS_DEST_LAT /*20*/:
            case Tag.GPS_DEST_LON_REF /*21*/:
            case Tag.GPS_DEST_LON /*22*/:
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        CamLog.d(FaceDetector.TAG, "onKeyDown - keyCode : " + keyCode + ", event : " + event);
        if (event != null && event.getRepeatCount() != 0 && keyCode != 24 && keyCode != 25) {
            return false;
        }
        switch (keyCode) {
            case LGKeyRec.EVENT_STOPPED /*4*/:
                doBackKeyInPostview();
                return true;
            case Tag.GPS_DEST_LAT_REF /*19*/:
            case Tag.GPS_DEST_LAT /*20*/:
            case Tag.GPS_DEST_LON_REF /*21*/:
            case Tag.GPS_DEST_LON /*22*/:
            case Tag.GPS_DEST_BEAR_REF /*23*/:
            case Tag.GPS_PROCESS_METHOD /*27*/:
            case ISTAudioRecorder.NUM_JAVA_BUFFER /*80*/:
            case 84:
                return true;
            case Tag.GPS_DEST_BEAR /*24*/:
            case Tag.GPS_DEST_DIST_REF /*25*/:
                doVolumeKey(event);
                return true;
            case 79:
                return false;
            case 82:
                CamLog.d(FaceDetector.TAG, "KEYCODE_MENU");
                return true;
            default:
                return false;
        }
    }

    protected void doVolumeKey(KeyEvent event) {
        if (this.mPostViewParameters != null && CameraConstants.VOLUME_SHUTTER.equals(this.mPostViewParameters.getVolumeKey()) && event != null && event.getRepeatCount() == 0 && !getActivity().isFinishing()) {
            removeExitInteraction();
            finish();
        }
    }

    protected void doBackKeyInPostview() {
        CamLog.d(FaceDetector.TAG, "KEYCODE_BACK");
        if (this.mPause || getActivity().isFinishing() || this.mDialog != null) {
            CamLog.d(FaceDetector.TAG, "KEYCODE_BACK - return...");
        } else {
            finish();
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case 82:
                CamLog.d(FaceDetector.TAG, "KEYCODE_MENU or KEYCODE_BACK keyup");
                return false;
            case Tag.GPS_DEST_BEAR /*24*/:
            case Tag.GPS_DEST_DIST_REF /*25*/:
                return true;
            default:
                return false;
        }
    }

    protected void setActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    protected void configureWindow() {
        requestWindowFeature(9);
    }

    private void registerMediaReceiver(PostViewMediaBroadCastReceiver receiver) {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MEDIA_EJECT");
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_SCANNER_STARTED");
        intentFilter.addAction("android.intent.action.MEDIA_SCANNER_FINISHED");
        intentFilter.addAction("android.intent.action.MEDIA_CHECKING");
        intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
        intentFilter.addAction("android.intent.action.MEDIA_NOFS");
        intentFilter.addAction("android.intent.action.MEDIA_REMOVED");
        intentFilter.addAction("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        intentFilter.addAction("android.intent.action.MEDIA_SHARED");
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTABLE");
        intentFilter.addDataScheme("file");
        registerReceiver(receiver, intentFilter);
    }

    private void registerBatteryReceiver(PostViewBatteryReceiver receiver) {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
        intentFilter.addAction("android.intent.action.BATTERY_LOW");
        registerReceiver(receiver, intentFilter);
    }

    private void registerScreenOffReceiver(PostViewScreenOffReceiver receiver) {
        registerReceiver(receiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
    }

    public void setSDCardSetting(boolean isInsertSD) {
        this.mSDCardsetting = isInsertSD;
    }

    public boolean getSDCardSetting() {
        return this.mSDCardsetting;
    }

    public boolean isPausing() {
        return this.mPause;
    }

    private void connectCameraDevice() {
        if (ProjectVariables.isPostviewDeviceOpenModel() && !Common.isScreenLocked()) {
            CamLog.d(FaceDetector.TAG, "connectCameraDevice : mCamera = " + this.mCamera);
            if (this.mPostViewParameters == null) {
                CamLog.w(FaceDetector.TAG, "Postview : postview parameters get fail.");
                return;
            }
            if (this.mPostViewParameters.getCameraId() == -1) {
                this.mPostViewParameters.setCameraId(Setting.readPreferredCameraId(getActivity().getSharedPreferences(Setting.SETTING_PRIMARY, 0)));
            }
            if (this.mCamera != null) {
                return;
            }
            if (this.mCameraOpenThread == null || !this.mCameraOpenThread.isAlive()) {
                removePostRunnable(this.mThreadStartRunnable);
                postOnUiThread(this.mThreadStartRunnable, Common.isFaceUnlock() ? 300 : 0);
            }
        }
    }

    private void checkAndDisconnectCameraDevice() {
        if (ProjectVariables.isPostviewDeviceOpenModel()) {
            CamLog.d(FaceDetector.TAG, "checkAndDisconnectCameraDevice : mCamera = " + this.mCamera);
            removePostRunnable(this.mThreadStartRunnable);
            if (this.mCameraOpenThread != null && this.mCameraOpenThread.isAlive()) {
                try {
                    this.mCameraOpenThread.interrupt();
                    this.mCameraOpenThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.mCameraOpenThread = null;
            }
            if (this.mCamera != null) {
                if (this.bConnectedDevice) {
                    CameraHolder.instance().keep();
                }
                CameraHolder.instance().release();
                this.mCamera = null;
            }
        }
    }

    public Activity getActivity() {
        return this;
    }

    public void toast(final String msg) {
        if (this.mToast != null) {
            this.mToast.cancel(true);
            runOnUiThread(new Runnable() {
                public void run() {
                    if (ShotPostviewActivity.this.mToast != null && ShotPostviewActivity.this.mOrientationInfo != null) {
                        int orientation;
                        if (ShotPostviewActivity.this.mOrientationInfo.getOrientation() == 0 || ShotPostviewActivity.this.mOrientationInfo.getOrientation() == 2) {
                            orientation = 0;
                        } else {
                            orientation = ShotPostviewActivity.SET_AS_MENU;
                        }
                        ShotPostviewActivity.this.mToast.show(ShotPostviewActivity.this.getActivity(), msg, 0, orientation);
                    }
                }
            });
        }
    }

    public void removeExitInteraction() {
        if (this.mPostViewParameters == null) {
            return;
        }
        if (("on_delay_2sec".equals(this.mPostViewParameters.getAutoReview()) || "on_delay_5sec".equals(this.mPostViewParameters.getAutoReview())) && this.mHandler != null) {
            removePostRunnable(this.mExitInteraction);
            this.mHandler.removeCallbacks(this.mExitInteraction);
        }
    }

    public void postOnUiThread(Runnable action) {
        if (this.mHandler != null) {
            this.mPostRunnables.add(action);
            this.mHandler.post(action);
        }
    }

    public void postOnUiThread(Runnable action, long delay) {
        if (this.mHandler != null) {
            this.mPostRunnables.add(action);
            this.mHandler.postDelayed(action, delay);
        }
    }

    public void removePostRunnable(Object object) {
        if (this.mPostRunnables == null) {
            CamLog.d(FaceDetector.TAG, "mPostRunnables is null");
            return;
        }
        int index = this.mPostRunnables.indexOf(object);
        if (index >= 0) {
            this.mHandler.removeCallbacks((Runnable) this.mPostRunnables.get(index));
            this.mPostRunnables.remove(index);
        }
    }

    public void removePostAllRunnables() {
        if (this.mPostRunnables == null || this.mHandler == null) {
            CamLog.d(FaceDetector.TAG, "mPostRunnables is null");
            return;
        }
        int postRunnableSize = this.mPostRunnables.size();
        for (int i = 0; i < postRunnableSize; i += SET_AS_MENU) {
            this.mHandler.removeCallbacks((Runnable) this.mPostRunnables.get(i));
        }
        this.mPostRunnables.clear();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        CamLog.d(FaceDetector.TAG, "onConfigurationChanged : newConfig = " + newConfig.orientation);
        if (this.mOrientationInfo != null) {
            this.mOrientationInfo.setOrientationByWindowOrientation();
        }
        super.onConfigurationChanged(newConfig);
    }

    protected void setLayoutParams(LayoutParams params, int width, int height, int leftMargin, int topMargin, int bottomMargin) {
        if (params != null) {
            params.width = width;
            params.height = height;
            params.leftMargin = leftMargin;
            params.topMargin = topMargin;
            params.bottomMargin = bottomMargin;
        }
    }

    protected PreferenceGroup getPreferenceGroup() {
        if (this.mPostViewParameters == null) {
            CamLog.w(FaceDetector.TAG, "Postview : postview parameters get fail.");
            return null;
        }
        this.mPostViewParameters.getApplicationMode();
        if (this.mPostViewParameters.getApplicationMode() == 0) {
            if (this.mPostViewParameters.getCameraId() == SET_AS_MENU) {
                return this.mFrontCameraPreferenceGroup;
            }
            if (this.mPostViewParameters.getCameraDimension() == 0) {
                return this.mBackCameraPreferenceGroup;
            }
            return this.mBack3dCameraPreferenceGroup;
        } else if (this.mPostViewParameters.getCameraId() == SET_AS_MENU) {
            return this.mFrontCamcorderPreferenceGroup;
        } else {
            if (this.mPostViewParameters.getCameraDimension() == 0) {
                return this.mBackCamcorderPreferenceGroup;
            }
            return this.mBack3dCamcorderPreferenceGroup;
        }
    }

    protected String getSettingValue(String key) {
        PreferenceGroup preferenceGroup = getPreferenceGroup();
        if (preferenceGroup == null) {
            return CameraConstants.TYPE_PREFERENCE_NOT_FOUND;
        }
        ListPreference pref = preferenceGroup.findPreference(key);
        if (pref != null) {
            return pref.getValue();
        }
        return CameraConstants.TYPE_PREFERENCE_NOT_FOUND;
    }

    protected int[] getThumbnailSize(boolean landscape) {
        int[] thumbSize = new int[2];
        if (landscape) {
            thumbSize[0] = Common.getPixelFromDimens(getActivity(), R.dimen.contshot_thumb_image_width);
            thumbSize[SET_AS_MENU] = Common.getPixelFromDimens(getActivity(), R.dimen.contshot_thumb_image_height);
        } else {
            thumbSize[0] = Common.getPixelFromDimens(getActivity(), R.dimen.contshot_thumb_image_width_port);
            thumbSize[SET_AS_MENU] = Common.getPixelFromDimens(getActivity(), R.dimen.contshot_thumb_image_height_port);
        }
        return thumbSize;
    }

    protected Bitmap getLastThumbnail(Uri uri, int applicationMode) {
        Bitmap thumbnail = null;
        if (applicationMode == SET_AS_MENU && ModelProperties.isOMAP4Chipset()) {
            String filePath = BitmapManager.getRealPathFromURI(this, uri);
            if (filePath != null) {
                thumbnail = ThumbnailUtils.createVideoThumbnail(filePath, SET_AS_MENU);
            }
        } else {
            long id = -1;
            if (uri != null) {
                id = Util.getIdFromUri(this, uri);
                CamLog.d(FaceDetector.TAG, String.format("GET THUMB start id is %d, and uri is %s", new Object[]{Long.valueOf(id), uri.toString()}));
            }
            if (id == -1) {
                CamLog.w(FaceDetector.TAG, String.format("GET THUMB end: uri not valid", new Object[0]));
                return null;
            } else if (applicationMode == 0) {
                int degree = ExifUtil.getExifOrientationDegree(BitmapManager.getRealPathFromURI(this, uri));
                thumbnail = this.mImageHandler.getImage(BitmapManager.instance().getThumbnail(getContentResolver(), id, SET_AS_MENU, null, false), degree, false);
                CamLog.d(FaceDetector.TAG, String.format("GET PICTURE THUMB end", new Object[0]));
            } else {
                thumbnail = BitmapManager.instance().getThumbnail(getContentResolver(), id, SET_AS_MENU, null, true);
                CamLog.d(FaceDetector.TAG, String.format("GET VIDEO THUMB end", new Object[0]));
            }
        }
        return thumbnail;
    }

    protected Bitmap checkFlipCapturedImage(Bitmap bmp, int applicationMode) {
        boolean z = false;
        if (this.mPostViewParameters == null) {
            CamLog.w(FaceDetector.TAG, "Postview : postview parameters get fail.");
            return null;
        }
        if (this.mPostViewParameters.getCameraId() == SET_AS_MENU) {
            z = true;
        }
        if (applicationMode == 0) {
            try {
                if (this.mPostViewParameters.getCameraId() == SET_AS_MENU && CameraConstants.SMART_MODE_OFF.equals(this.mPostViewParameters.getFlip())) {
                    z = false;
                }
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "Exception : ", e);
                return null;
            }
        }
        if (!z) {
            return bmp;
        }
        CamLog.v(FaceDetector.TAG, "bmp is flipped..");
        return this.mImageHandler.getImage(bmp, 0, z);
    }

    protected void adjustFilenameForView(final TextView tv, String filename, String thumbInfo) {
        final String tFilename = filename + thumbInfo;
        tv.post(new Runnable() {
            public void run() {
                tv.setText(tFilename);
            }
        });
    }

    protected View inflateStub(int id) {
        ViewStub stub = (ViewStub) findViewById(id);
        if (stub != null) {
            return stub.inflate();
        }
        return null;
    }

    protected int deleteImage(String filename, Uri uri) {
        String fullPath = this.mPostViewParameters.getCurrentStorageDirectory() + filename + CameraConstants.TIME_MACHINE_TEMPFILE_EXT;
        CamLog.d(FaceDetector.TAG, "try to delete " + fullPath);
        if (Common.isFileExist(fullPath)) {
            if (new File(fullPath).delete()) {
                ImageManager.deleteImage(getContentResolver(), uri);
                CamLog.d(FaceDetector.TAG, "deleted.");
                if (this.mPostViewParameters.getUriList().remove(uri)) {
                    CamLog.d(FaceDetector.TAG, "deleted uri");
                } else {
                    CamLog.w(FaceDetector.TAG, "failure to delete uri!");
                }
                return this.mPostViewParameters.getUriList().size();
            }
            CamLog.d(FaceDetector.TAG, "delete failed");
        }
        CamLog.w(FaceDetector.TAG, "failure delete image file (return -1)");
        return -1;
    }

    protected boolean deleteVideo(String filename, Uri uri) {
        String fullPath = this.mPostViewParameters.getCurrentStorageDirectory() + filename + this.mPostViewParameters.getVideoExtension();
        if (Common.isFileExist(fullPath)) {
            if (new File(fullPath).delete()) {
                ContentResolver c = getContentResolver();
                CamLog.d(FaceDetector.TAG, "videoFileUri : " + this.mPostViewParameters.getSavedUri());
                if (this.mPostViewParameters.getSavedUri() == null) {
                    CamLog.w(FaceDetector.TAG, "videoFileUri is null!");
                    return false;
                }
                c.delete(this.mPostViewParameters.getSavedUri(), null, null);
            } else {
                CamLog.w(FaceDetector.TAG, "videoFile delete fail");
                return false;
            }
        }
        return true;
    }

    protected boolean checkPauseAndAutoReview() {
        if (this.mPause || getActivity().isFinishing()) {
            return false;
        }
        if (!(this.mCamera == null && ProjectVariables.isPostviewDeviceOpenModel())) {
            removeExitInteraction();
        }
        return true;
    }

    protected void saveFinished() {
        Intent intent = getIntent();
        intent.putExtra("save_done", true);
        setResult(100, intent);
        if (!isFinishing()) {
            finish();
        }
    }

    protected void deleteFinished() {
        Intent intent = getIntent();
        intent.putExtra("delete_done", true);
        setResult(100, intent);
        finish();
    }

    protected void makePostviewMenuItems(int menuType) {
        Intent intent = new Intent();
        ArrayList<PostviewMenu> postviewMenuList = null;
        clearShareAndSetAsMenuList(true, true);
        if (SET_AS_MENU == menuType) {
            postviewMenuList = this.postviewMenuSetAs;
        } else if (menuType == 0) {
            postviewMenuList = this.postviewMenuShare;
        }
        Intent smartShare = new Intent();
        PackageManager packageManager = getPackageManager();
        smartShare.setAction("android.intent.action.LGSMARTSHARE");
        ResolveInfo ri = packageManager.resolveActivity(smartShare, 0);
        List<ResolveInfo> activities = readMenuActivity(menuType, intent);
        if (activities != null && activities.size() > 0) {
            int numActivities = activities.size();
            if (postviewMenuList != null) {
                int i;
                for (i = 0; i != numActivities; i += SET_AS_MENU) {
                    ResolveInfo info = (ResolveInfo) activities.get(i);
                    if (info != null) {
                        postviewMenuList.add(new PostviewMenu(info.loadLabel(packageManager).toString(), info.activityInfo.applicationInfo.loadLabel(packageManager).toString(), info.activityInfo.packageName, info.loadIcon(packageManager)));
                    }
                }
                if (!(ProjectVariables.isSupportPushContorl() || menuType != 0 || ri == null)) {
                    postviewMenuList.add(new PostviewMenu(ri.loadLabel(packageManager).toString(), ri.activityInfo.applicationInfo.loadLabel(packageManager).toString(), ri.activityInfo.packageName, ri.loadIcon(packageManager)));
                }
                sortPostviewMenuList(postviewMenuList, packageManager);
                for (i = 0; i < numActivities; i += SET_AS_MENU) {
                    if ("U+Box".equals(((PostviewMenu) postviewMenuList.get(i)).getLabelImage())) {
                        PostviewMenu uplusBoxMenu = (PostviewMenu) postviewMenuList.get(i);
                        postviewMenuList.remove(i);
                        postviewMenuList.add(0, uplusBoxMenu);
                        break;
                    }
                }
                removeLabelAppByCompareLableImage(postviewMenuList);
            }
        }
    }

    protected void removeLabelAppByCompareLableImage(ArrayList<PostviewMenu> postviewMenuList) {
        String compareString1 = ((PostviewMenu) postviewMenuList.get(0)).getLabelImage();
        for (int i = SET_AS_MENU; i < postviewMenuList.size(); i += SET_AS_MENU) {
            String compareString2 = ((PostviewMenu) postviewMenuList.get(i)).getLabelImage();
            if (!compareString1.equals(compareString2)) {
                ((PostviewMenu) postviewMenuList.get(i - 1)).setLabelApp(null);
                if (i == postviewMenuList.size() - 1) {
                    ((PostviewMenu) postviewMenuList.get(i)).setLabelApp(null);
                }
            }
            compareString1 = compareString2;
        }
    }

    protected void sortPostviewMenuList(ArrayList<PostviewMenu> postviewMenuList, PackageManager packageManager) {
        String strCountry = SystemProperties.get("ro.build.target_country");
        if ("CLR".equals(SystemProperties.get("ro.build.target_operator")) && ("COM".equals(strCountry) || "BR".equals(strCountry) || "CO".equals(strCountry))) {
            ApplicationInfo applicationInfo = null;
            ApplicationInfo mmsInfo = null;
            try {
                applicationInfo = packageManager.getApplicationInfo("com.lge.plugger", 0);
                mmsInfo = packageManager.getApplicationInfo("com.android.mms", 0);
            } catch (NameNotFoundException e) {
                CamLog.w(FaceDetector.TAG, "NameNotFoundException:", e);
            }
            final String pluggerName = applicationInfo != null ? applicationInfo.loadLabel(packageManager).toString() : "";
            final String mmsName = mmsInfo != null ? mmsInfo.loadLabel(packageManager).toString() : "";
            Collections.sort(postviewMenuList, new Comparator<PostviewMenu>() {
                public int compare(PostviewMenu lhs, PostviewMenu rhs) {
                    if (pluggerName.equals(lhs.getLabelImage())) {
                        return -1;
                    }
                    if (pluggerName.equals(rhs.getLabelImage())) {
                        return ShotPostviewActivity.SET_AS_MENU;
                    }
                    if (mmsName.equals(lhs.getLabelImage())) {
                        return -1;
                    }
                    if (mmsName.equals(rhs.getLabelImage())) {
                        return ShotPostviewActivity.SET_AS_MENU;
                    }
                    return lhs.getLabelImage().compareTo(rhs.getLabelImage());
                }
            });
            return;
        }
        Collections.sort(postviewMenuList, new Comparator<PostviewMenu>() {
            private final Collator collator;

            {
                this.collator = Collator.getInstance();
            }

            public int compare(PostviewMenu lhs, PostviewMenu rhs) {
                return this.collator.compare(lhs.getLabelImage(), rhs.getLabelImage());
            }
        });
    }

    protected List<ResolveInfo> readMenuActivity(int menuType, Intent intent) {
        PackageManager packageManager = getPackageManager();
        if (this.mPostViewParameters.getApplicationMode() == 0) {
            intent.setType(MultimediaProperties.IMAGE_MIME_TYPE);
        } else {
            intent.setType("video/*");
        }
        if (menuType == 0) {
            intent.setAction("android.intent.action.SEND");
            return packageManager.queryIntentActivities(intent, 0);
        }
        intent.setAction("android.intent.action.ATTACH_DATA");
        return packageManager.queryIntentActivities(intent, 0);
    }

    protected void startPostviewMenuItems(int menuType, PostviewMenu selectedMenu) {
        Intent intent = new Intent();
        PackageManager packageManager = getPackageManager();
        if (selectedMenu == null || selectedMenu.getLabelImage() == null) {
            toast(getString(R.string.error_not_exist_app));
            return;
        }
        Intent smartShare = new Intent();
        if (menuType == 0) {
            smartShare.setAction("android.intent.action.LGSMARTSHARE");
            ResolveInfo ri = packageManager.resolveActivity(smartShare, 0);
            if (ri != null && ri.loadLabel(packageManager).toString().equals(selectedMenu.getLabelImage())) {
                ArrayList<Uri> uri = new ArrayList();
                uri.add(this.mPostViewParameters.getUriList().get(0));
                smartShare.putExtra("android.intent.extra.STREAM", uri);
                smartShare.putExtra("smartshare.type", "Camera");
                smartShare.putExtra("smartshare.package.name", "com.lge.camera.CameraApp");
                startResolvedActivity(menuType, smartShare, ri);
                return;
            }
        }
        List<ResolveInfo> activities = readMenuActivity(menuType, intent);
        if (activities != null && activities.size() > 0) {
            int numActivities = activities.size();
            for (int i = 0; i < numActivities; i += SET_AS_MENU) {
                ResolveInfo infoImage = (ResolveInfo) activities.get(i);
                String packageName = infoImage.activityInfo.packageName;
                String menuLabel = infoImage.loadLabel(getPackageManager()).toString();
                if (selectedMenu != null && selectedMenu.getPackageName().equals(packageName) && selectedMenu.getLabelImage().equals(menuLabel)) {
                    CamLog.i(FaceDetector.TAG, "Calling Package/App = " + packageName + "/" + menuLabel);
                    startResolvedActivity(menuType, intent, infoImage);
                    return;
                }
            }
        }
    }

    protected void startResolvedActivity(final int menuType, Intent intent, ResolveInfo info) {
        Uri capturedImageUri;
        final Intent resolvedIntent = new Intent(intent);
        ActivityInfo ai = info.activityInfo;
        resolvedIntent.setComponent(new ComponentName(ai.applicationInfo.packageName, ai.name));
        if (this.mPostViewParameters.getUriList().size() > SET_AS_MENU) {
            capturedImageUri = (Uri) this.mPostViewParameters.getUriList().get(this.mCurrentSelectedIndex);
        } else {
            capturedImageUri = (Uri) this.mPostViewParameters.getUriList().get(0);
        }
        if (menuType == 0) {
            resolvedIntent.putExtra("android.intent.extra.STREAM", capturedImageUri);
        } else {
            resolvedIntent.setDataAndType(capturedImageUri, MultimediaProperties.IMAGE_MIME_TYPE);
            resolvedIntent.putExtra("mimeType", MultimediaProperties.IMAGE_MIME_TYPE);
        }
        this.mHandler.post(new Runnable() {
            public void run() {
                ShotPostviewActivity.this.startActivityForResult(resolvedIntent, menuType == 0 ? 2 : 3);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CamLog.d(FaceDetector.TAG, "onActivityResult requestCode=" + requestCode + "/resultCode=" + resultCode);
    }

    protected void clearShareAndSetAsMenuList(boolean share, boolean setas) {
        if (share && this.postviewMenuSetAs != null && this.postviewMenuSetAs.size() > 0) {
            this.postviewMenuSetAs.clear();
        }
        if (setas && this.postviewMenuShare != null && this.postviewMenuShare.size() > 0) {
            this.postviewMenuShare.clear();
        }
    }

    protected void onCreateDialog(int dialogId, int applicationMode) {
        PostviewDialog mDialog = PostviewDialog.getPostviewDialog(dialogId, applicationMode);
        switch (dialogId) {
            case SET_AS_MENU /*1*/:
                makePostviewMenuItems(0);
                mDialog.setSharedListDialogAdater(new PostviewMenuAdapter(getApplicationContext(), R.layout.dialog_item, this.postviewMenuShare));
                break;
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                makePostviewMenuItems(SET_AS_MENU);
                mDialog.setSharedListDialogAdater(new PostviewMenuAdapter(getApplicationContext(), R.layout.dialog_item, this.postviewMenuSetAs));
                break;
        }
        mDialog.show(getFragmentManager(), CameraConstants.TAG_DIALOG_POSTVIEW);
    }

    protected void showProgressDialog(int dialogId, int applicationMode) {
        if (dialogId == 9 || dialogId == 10) {
            PostviewDialog.getPostviewDialog(dialogId, applicationMode).show(getFragmentManager(), CameraConstants.TAG_DIALOG_POSTVIEW);
        }
    }

    protected void dismissProgressDialog() {
        try {
            PostviewDialog mDialog = PostviewDialog.getPostviewDialog();
            if (mDialog == null) {
                return;
            }
            if (mDialog.getCurrentDialogId() == 9 || mDialog.getCurrentDialogId() == 10) {
                mDialog.dismiss();
            }
        } catch (IllegalStateException e) {
            CamLog.w(FaceDetector.TAG, "dismissProgressDialog-IllegalStateException : ", e);
        }
    }

    public void adapterPositiveClick(int dialogId, int i) {
        if (dialogId == SET_AS_MENU) {
            startPostviewMenuItems(0, (PostviewMenu) this.postviewMenuShare.get(i));
        } else {
            startPostviewMenuItems(SET_AS_MENU, (PostviewMenu) this.postviewMenuSetAs.get(i));
        }
    }

    protected void setSecureImageList(Uri uri, boolean add) {
        if (!this.mPostViewParameters.useSecureLockImage() && !AppControlUtil.checkGuestModeAndAppDisabled(getContentResolver(), true, SET_AS_MENU)) {
            return;
        }
        if (add) {
            SecureImageUtil.get().addSecureLockImageUri(uri);
        } else {
            SecureImageUtil.get().removeSecureLockUri(uri, this.mPostViewParameters.getApplicationMode());
        }
    }

    public PostViewParameters getPostViewParameters() {
        return this.mPostViewParameters;
    }

    public void doDeletePositiveClick() {
    }

    public void doDeleteMultiPositiveClick() {
    }

    public void doEnableGalleryPositiveClick() {
    }

    public void doTimeMachineWarningPositiveClick(CheckBox checkBox) {
    }

    public void doTimeMachineWarningNegativeClick(CheckBox checkBox) {
    }

    public void doTimeMachineWarningDismiss() {
    }

    public void doClearShotWarningPositiveClick(CheckBox checkBox) {
    }

    public void doClearShotWarningNegativeClick(CheckBox checkBox) {
    }

    public void doClearShotWarningDismiss() {
    }

    public void doRefocusWarningPositiveClick(CheckBox checkBox) {
    }

    public void doRefocusWarningNegativeClick(CheckBox checkBox) {
    }

    public void doRefocusWarningDismiss() {
    }

    public void doBurstShotWarningPositiveClick(CheckBox checkBox) {
    }

    public void doWarningNegativeClick() {
    }

    protected boolean loadSingleCapturedImages() {
        return false;
    }

    protected Bitmap loadCapturedImage(Uri uri, int degrees) {
        return null;
    }

    protected void setFileNameLayout() {
    }

    protected void setFileName() {
    }
}
