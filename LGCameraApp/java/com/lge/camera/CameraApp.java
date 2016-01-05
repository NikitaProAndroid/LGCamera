package com.lge.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import com.lge.camera.command.Command;
import com.lge.camera.listeners.ExtraTouchEventListener;
import com.lge.camera.listeners.OnKeyEventListener;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.PreferenceProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.PreferenceGroup;
import com.lge.camera.setting.PreferenceInflater;
import com.lge.camera.setting.Setting;
import com.lge.camera.setting.SettingVariant;
import com.lge.camera.util.AppControlUtil;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CheckStatusManager;
import com.lge.camera.util.Common;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Limit;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class CameraApp extends CameraActivity {
    private CameraMediator mCameraMediator;

    public CameraApp() {
        this.mCameraMediator = null;
        Common.APP_CAMERA_INSTANCE_COUNT++;
        CamLog.i(FaceDetector.TAG, "construct CAMERA app_instance_cnt = " + Common.APP_CAMERA_INSTANCE_COUNT);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!CheckStatusManager.checkEnterApplication(this, false)) {
            CamLog.d(FaceDetector.TAG, "onCreate()-end, checkEnterApplication fail.");
            if (AppControlUtil.isSecureCameraIntent(getIntent())) {
                CheckStatusManager.setCheckEnterOutSecure(1);
                ProjectVariables.bEnterSetting = false;
            } else {
                try {
                    Thread.currentThread();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    CamLog.e(FaceDetector.TAG, "onCreate() InterruptedException ", e);
                }
                ProjectVariables.bEnterSetting = false;
                CheckStatusManager.checkCameraOut(this, null);
                return;
            }
        }
        AppControlUtil.setQuickWindowCameraFromIntent(getIntent());
        this.mCameraMediator = new CameraMediator(this);
        this.mCameraMediator.setKeepLastCameraMode();
        this.mCameraMediator.createPreviewController();
        this.mCameraMediator.getPreviewController().onCreate();
        this.mCameraMediator.onCreate();
        PreferenceInflater inflater = new PreferenceInflater(getApplicationContext());
        PreferenceGroup prefGroup = (PreferenceGroup) inflater.inflate(PreferenceProperties.getBackCameraPreferenceResource());
        SettingVariant settingVariant = new SettingVariant();
        if (prefGroup != null) {
            this.mCameraMediator.setBackPreference(prefGroup);
            settingVariant.makePreferenceVariant(getApplicationContext(), prefGroup);
        }
        prefGroup = (PreferenceGroup) inflater.inflate(PreferenceProperties.getFrontCameraPreferenceResource());
        if (prefGroup != null) {
            this.mCameraMediator.setFrontPreference(prefGroup);
            settingVariant.makePreferenceVariant(getApplicationContext(), prefGroup);
        }
        changeLocalSetting();
        if (ModelProperties.is3dSupportedModel()) {
            this.mCameraMediator.setBack3dPreference((PreferenceGroup) inflater.inflate(PreferenceProperties.getBack3dCameraPreferenceResource()));
        }
        this.mOnKeyEventListener = new OnKeyEventListener();
        this.mExtraTouchEventListener = new ExtraTouchEventListener();
        setContentView(R.layout.init);
        this.mCameraMediator.setKeepLastValue();
        cameraControllerInit();
        if (CheckStatusManager.getCheckEnterOutSecure() == 0) {
            this.mCameraMediator.doCommandDelayed(Command.START_INIT, 100);
        }
        AppControlUtil.setQuickClipScreenCaptureLimit(this);
        if (ModelProperties.isSoftKeyNavigationBarModel()) {
            AppControlUtil.setFullScreen(getActivity());
            AppControlUtil.setTransparentNavigationBar(getActivity(), true);
            AppControlUtil.setTranslucentStatusBar(this);
            AppControlUtil.setEnableRotateNaviataionBar(getActivity(), true);
            AppControlUtil.disableNavigationButton(getActivity());
        }
        getWindow().setBackgroundDrawable(null);
        CamLog.d(FaceDetector.TAG, "onCreate()-end ");
    }

    protected void onResume() {
        CameraConstants.setLcdSize(Common.getPixelFromDimens(this, R.dimen.lcd_width), Common.getPixelFromDimens(this, R.dimen.lcd_height));
        CameraConstants.setSmartCoverSize(Common.getPixelFromDimens(this, R.dimen.smart_cover_window_width), Common.getPixelFromDimens(this, R.dimen.smart_cover_window_height), Common.getPixelFromDimens(this, R.dimen.smart_cover_window_wide_width));
        if (CheckStatusManager.getCheckEnterOutSecure() == 0 && !CheckStatusManager.checkEnterApplication(this, true)) {
            CamLog.d(FaceDetector.TAG, "onResume()-end, checkEnterApplication");
            if (AppControlUtil.isSecureCameraIntent(getIntent())) {
                CheckStatusManager.setCheckEnterOutSecure(1);
            } else {
                super.onResume();
                CheckStatusManager.checkCameraOut(this, null);
                return;
            }
        }
        if (CheckStatusManager.getCheckEnterOutSecure() == 1) {
            CheckStatusManager.setCheckEnterOutSecure(2);
            if (CheckStatusManager.checkVTCallStatus(getActivity())) {
                CheckStatusManager.checkCameraOut(getActivity(), this.mCameraMediator.getHandler());
            } else {
                CheckStatusManager.checkCameraOut(getActivity(), null);
            }
        }
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
        this.mCameraMediator = null;
    }

    private void cameraControllerInit() {
        if (this.mCameraMediator.getSettingController() != null) {
            this.mCameraMediator.getSettingController().initController();
            setLocalSetting();
        }
        if (this.mCameraMediator.getPreviewController() != null) {
            this.mCameraMediator.getPreviewController().initController();
        }
        cameraUiControllerInit();
        this.mCameraMediator.setupCaptureParams();
    }

    private void cameraUiControllerInit() {
        if (this.mCameraMediator.getPreviewPanelController() != null) {
            this.mCameraMediator.getPreviewPanelController().initController();
        }
        if (this.mCameraMediator.getQuickFunctionController() != null) {
            this.mCameraMediator.getQuickFunctionController().initController();
        }
        if (this.mCameraMediator.getIndicatorController() != null) {
            this.mCameraMediator.getIndicatorController().initController();
        }
        if (this.mCameraMediator.getQuickButtonController() != null) {
            this.mCameraMediator.getQuickButtonController().initController();
        }
    }

    protected void changeLocalSetting() {
        if (this.mCameraMediator != null && this.mCameraMediator.getFrontPreferenceGroup() != null && this.mCameraMediator.getBackPreferenceGroup() != null) {
            String simOperator = ((TelephonyManager) getApplicationContext().getSystemService("phone")).getSimOperator();
            String currentMCC = "0";
            if (simOperator != null && simOperator.length() > 2) {
                if ("466".equals(simOperator.substring(0, 3))) {
                    ListPreference listPref = this.mCameraMediator.getFrontPreferenceGroup().findPreference(Setting.KEY_CAMERA_AUTO_REVIEW);
                    if (listPref != null) {
                        listPref.setDefaultValue(CameraConstants.SMART_MODE_ON);
                    }
                    listPref = this.mCameraMediator.getBackPreferenceGroup().findPreference(Setting.KEY_CAMERA_AUTO_REVIEW);
                    if (listPref != null) {
                        listPref.setDefaultValue(CameraConstants.SMART_MODE_ON);
                    }
                }
            }
        }
    }

    protected void setLocalSetting() {
        if (this.mCameraMediator != null && this.mCameraMediator.getFrontPreferenceGroup() != null && this.mCameraMediator.getBackPreferenceGroup() != null && ModelProperties.NAME_J1_TELUS.equals(SystemProperties.get("ro.product.model"))) {
            this.mCameraMediator.setSetting(Setting.KEY_FLASH, LGT_Limit.ISP_AUTOMODE_AUTO);
        }
    }

    protected void finalize() throws Throwable {
        Common.APP_CAMERA_INSTANCE_COUNT--;
        CamLog.d(FaceDetector.TAG, "destroy CAMERA app_instance_cnt = " + Common.APP_CAMERA_INSTANCE_COUNT);
        super.finalize();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CamLog.d(FaceDetector.TAG, "onActivityResult requestCode=" + requestCode + "/resultCode=" + resultCode);
        switch (requestCode) {
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                resultCodeCrop(resultCode, data);
                break;
            case LGKeyRec.EVENT_STOPPED /*4*/:
                resultCodeHelp(data);
                break;
            case Limit.OLA_FIFO_DATA_MAX_SIZE_SHORT /*100*/:
                resultForPostview(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void resultCodeCrop(int resultCode, Intent data) {
        if (this.mCameraMediator == null) {
            return;
        }
        if (!this.mCameraMediator.beDirectlyGoingToCropGallery() || resultCode != 0) {
            Intent intent = new Intent();
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    intent.putExtras(extras);
                }
            }
            setResult(resultCode, intent);
            finish();
            getFileStreamPath("crop-temp").delete();
        }
    }

    private void resultCodeHelp(Intent data) {
        if (data != null && this.mCameraMediator != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                String strValue = extras.getString(Setting.KEY_CAMERA_TIMER);
                if (this.mCameraMediator != null) {
                    this.mCameraMediator.setSetting(Setting.KEY_CAMERA_TIMER, strValue);
                }
            }
        }
    }

    protected boolean resultForSaveDone(Bundle extras) {
        if (extras == null || !extras.getBoolean("save_done")) {
            return false;
        }
        this.mPostviewRequestSaveDone = true;
        CamLog.d(FaceDetector.TAG, "REQUEST_CODE_POSTVIEW save done");
        return true;
    }

    protected boolean setThumbnailForPostviewReturn(Bundle extras) {
        if (extras != null) {
            try {
                Bitmap bmp = (Bitmap) extras.getParcelable("thumb_data");
                if (!(this.mCameraMediator == null || this.mCameraMediator.getPreviewPanelController() == null)) {
                    this.mFileUri = (Uri) extras.getParcelable("recent_uri");
                    if (this.mFileUri != null) {
                        this.mCameraMediator.getPreviewPanelController().setThumbBitmapAndUpdate(bmp, this.mFileUri);
                    } else {
                        this.mCameraMediator.getPreviewPanelController().setThumbBitmapAndUpdate(bmp, this.mCameraMediator.getSavedImageUri());
                    }
                    return true;
                }
            } catch (NullPointerException e) {
                CamLog.w(FaceDetector.TAG, "NullPointerException:", e);
                return false;
            }
        }
        return false;
    }

    protected void releaseEachMode() {
    }

    protected void setStatusForAttach(boolean postview_mode) {
        if (postview_mode) {
            this.mCameraMediator.setStatus(2);
        }
    }

    public Mediator getMediator() {
        return this.mCameraMediator;
    }

    public CameraActivity getActivity() {
        return this;
    }

    public void doPhoneStateListenerAction(int state) {
        if (state == 0 && CheckStatusManager.checkVoiceShutterEnable(getMediator().getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
            getMediator().doCommandUi(Command.SET_VOICE_SHUTTER);
            getMediator().setPreferenceMenuEnable(Setting.KEY_VOICESHUTTER, true, false);
        }
    }
}
