package com.lge.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.System;
import android.view.View;
import com.lge.camera.command.Command;
import com.lge.camera.listeners.ExtraTouchEventListener;
import com.lge.camera.listeners.OnKeyEventListener;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.PreferenceProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.PreferenceGroup;
import com.lge.camera.setting.PreferenceInflater;
import com.lge.camera.setting.SettingVariant;
import com.lge.camera.util.AppControlUtil;
import com.lge.camera.util.AudioUtil;
import com.lge.camera.util.BitmapManager;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CheckStatusManager;
import com.lge.camera.util.Common;
import com.lge.camera.util.SharedPreferenceUtil;
import com.lge.olaworks.define.Limit;
import com.lge.olaworks.library.FaceDetector;

public class Camcorder extends CameraActivity {
    private CamcorderMediator mCamcorderMediator;

    public Camcorder() {
        this.mCamcorderMediator = null;
        Common.APP_CAMCORDER_INSTANCE_COUNT++;
        CamLog.d(FaceDetector.TAG, "construct VIDEO app_instance_cnt = " + Common.APP_CAMCORDER_INSTANCE_COUNT);
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
        this.mCamcorderMediator = new CamcorderMediator(this);
        this.mCamcorderMediator.setKeepLastCameraMode();
        this.mCamcorderMediator.createPreviewController();
        this.mCamcorderMediator.getPreviewController().onCreate();
        this.mCamcorderMediator.onCreate();
        PreferenceInflater inflater = new PreferenceInflater(getApplicationContext());
        PreferenceGroup prefGroup = (PreferenceGroup) inflater.inflate(PreferenceProperties.getBackCamcorderPreferenceResource());
        SettingVariant settingVariant = new SettingVariant();
        if (prefGroup != null) {
            this.mCamcorderMediator.setBackPreference(prefGroup);
            settingVariant.makePreferenceVariant(getApplicationContext(), prefGroup);
        }
        prefGroup = (PreferenceGroup) inflater.inflate(PreferenceProperties.getFrontCamcorderPreferenceResource());
        if (prefGroup != null) {
            this.mCamcorderMediator.setFrontPreference(prefGroup);
            settingVariant.makePreferenceVariant(getApplicationContext(), prefGroup);
        }
        if (ModelProperties.is3dSupportedModel()) {
            this.mCamcorderMediator.setBack3dPreference((PreferenceGroup) inflater.inflate(PreferenceProperties.getBack3dCamcorderPreferenceResource()));
        }
        this.mOnKeyEventListener = new OnKeyEventListener();
        this.mExtraTouchEventListener = new ExtraTouchEventListener();
        setContentView(R.layout.init);
        this.mCamcorderMediator.setKeepLastValue();
        camcorderControllerInit();
        if (CheckStatusManager.getCheckEnterOutSecure() == 0) {
            this.mCamcorderMediator.doCommandDelayed(Command.START_INIT, 100);
        }
        if (ModelProperties.isSoftKeyNavigationBarModel()) {
            AppControlUtil.setFullScreen(getActivity());
            AppControlUtil.setTransparentNavigationBar(getActivity(), true);
            AppControlUtil.setTranslucentStatusBar(this);
            AppControlUtil.setEnableRotateNaviataionBar(getActivity(), true);
            AppControlUtil.disableNavigationButton(getActivity());
        }
        getWindow().setBackgroundDrawable(null);
        CamLog.d(FaceDetector.TAG, "onCreate()-end");
    }

    protected void onResume() {
        CameraConstants.setLcdSize(Common.getPixelFromDimens(this, R.dimen.lcd_width), Common.getPixelFromDimens(this, R.dimen.lcd_height));
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
                CheckStatusManager.checkCameraOut(getActivity(), this.mCamcorderMediator.getHandler());
            } else {
                CheckStatusManager.checkCameraOut(getActivity(), null);
            }
        }
        super.onResume();
        Common.reduceBrightnessMode(getApplicationContext(), true);
    }

    protected void onPause() {
        super.onPause();
        Common.reduceBrightnessMode(getApplicationContext(), false);
    }

    protected void onDestroy() {
        super.onDestroy();
        this.mCamcorderMediator = null;
    }

    private void camcorderControllerInit() {
        if (this.mCamcorderMediator.getSettingController() != null) {
            this.mCamcorderMediator.getSettingController().initController();
            setLocalSetting();
        }
        if (this.mCamcorderMediator.getPreviewController() != null) {
            this.mCamcorderMediator.getPreviewController().initController();
        }
        camcorderUiControllerInit();
    }

    protected void changeLocalSetting() {
    }

    protected void setLocalSetting() {
        if (this.mCamcorderMediator != null && this.mCamcorderMediator.getFrontPreferenceGroup() != null && this.mCamcorderMediator.getBackPreferenceGroup() != null && this.mCamcorderMediator.isAttachMode() && this.mCamcorderMediator.isAttachIntent()) {
            this.mCamcorderMediator.readVideoIntentExtras();
            if (this.mCamcorderMediator.getRequestedVideoSizeLimit() != 0) {
                String mmsResolution = System.getString(this.mCamcorderMediator.getContentResolver(), "android.msg.camera.max.video.resolution");
                if (ModelProperties.getCarrierCode() == 24 && mmsResolution.equals("640x480")) {
                    this.mCamcorderMediator.getSettingController().setAttatchModeDefaultVideoSize("640x480");
                } else {
                    this.mCamcorderMediator.getSettingController().setAttatchModeDefaultVideoSize("320x240");
                }
            }
        }
    }

    private void camcorderUiControllerInit() {
        if (this.mCamcorderMediator.getPreviewPanelController() != null) {
            this.mCamcorderMediator.getPreviewPanelController().initController();
        }
        if (this.mCamcorderMediator.getQuickFunctionController() != null) {
            this.mCamcorderMediator.getQuickFunctionController().initController();
        }
        if (this.mCamcorderMediator.getIndicatorController() != null) {
            this.mCamcorderMediator.getIndicatorController().initController();
        }
        if (this.mCamcorderMediator.getQuickButtonController() != null) {
            this.mCamcorderMediator.getQuickButtonController().initController();
        }
    }

    protected void finalize() throws Throwable {
        Common.APP_CAMCORDER_INSTANCE_COUNT--;
        CamLog.d(FaceDetector.TAG, "destroy VIDEO app_instance_cnt = " + Common.APP_CAMCORDER_INSTANCE_COUNT);
        super.finalize();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CamLog.d(FaceDetector.TAG, "onActivityResult requestCode=" + requestCode + "/resultCode=" + resultCode);
        switch (requestCode) {
            case Limit.OLA_FIFO_DATA_MAX_SIZE_SHORT /*100*/:
                resultForPostview(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onProtectiveCurtainClick(View v) {
    }

    protected boolean setThumbnailForPostviewReturn(Bundle extras) {
        if (extras == null) {
            return false;
        }
        try {
            Bitmap bmp;
            Uri videoUri = this.mCamcorderMediator.getVideoFile().getUri();
            String savedPath = SharedPreferenceUtil.getLastVideoPath(getApplicationContext());
            String pathFromUri = BitmapManager.getRealPathFromURI(this, videoUri);
            if (pathFromUri == null || !pathFromUri.equals(savedPath)) {
                CamLog.d(FaceDetector.TAG, String.format("Saved uri is not valid. Find most recent uri.", new Object[0]));
                videoUri = this.mCamcorderMediator.getPreviewPanelController().getMostRecentThumbnailUri(true, 4);
                bmp = this.mCamcorderMediator.getLastThumbnail(videoUri);
            } else {
                bmp = (Bitmap) extras.getParcelable("thumb_data");
            }
            this.mCamcorderMediator.getPreviewPanelController().setThumbBitmapAndUpdate(bmp, videoUri);
            return true;
        } catch (NullPointerException e) {
            CamLog.w(FaceDetector.TAG, "NullPointerException:", e);
            return false;
        }
    }

    protected void releaseEachMode() {
        AppControlUtil.UnblockAlarmInRecording(this.mCamcorderMediator.getActivity());
        AudioUtil.setMuteNotificationStream(getApplicationContext(), false);
        AudioUtil.setMuteSystemStream(getApplicationContext(), false);
        this.mCamcorderMediator.getHandler().removeMessages(4);
    }

    protected void setStatusForAttach(boolean postview_mode) {
        if (!postview_mode) {
            this.mCamcorderMediator.setVideoState(6);
        }
    }

    public Mediator getMediator() {
        return this.mCamcorderMediator;
    }

    public CameraActivity getActivity() {
        return this;
    }

    public void doPhoneStateListenerAction(int state) {
        if (state != 2) {
            return;
        }
        if (this.mCamcorderMediator.getVideoState() == 3 || this.mCamcorderMediator.getVideoState() == 4) {
            this.mCamcorderMediator.doCommandUi(Command.STOP_RECORDING);
        }
    }
}
