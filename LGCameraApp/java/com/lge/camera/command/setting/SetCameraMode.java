package com.lge.camera.command.setting;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CheckStatusManager;
import com.lge.camera.util.TelephonyUtil;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetCameraMode extends SettingCommand {
    boolean mLiveEffect;

    public SetCameraMode(ControllerFunction function) {
        super(function);
        this.mLiveEffect = false;
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, new Bundle());
    }

    public void execute(LGParameters lgParameters, Object arg) {
        Bundle bundle = (Bundle) arg;
        final boolean allSetting = bundle.getBoolean("allSetting", false);
        this.mLiveEffect = bundle.getBoolean("liveeffect_mode", false);
        CamLog.d(FaceDetector.TAG, "SetCameraMode-start");
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                SetCameraMode.this.mGet.removePostRunnable(this);
                if (!allSetting && SetCameraMode.this.mGet.isStorageFull()) {
                    SetCameraMode.this.mGet.setMainButtonEnable(CameraConstants.STORAGECONTROLLER_LOCKKEY);
                }
                SetCameraMode.this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SWAP, true);
                if (SetCameraMode.this.mGet.getCameraMode() == 1) {
                    SetCameraMode.this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_ZOOM, false);
                    SetCameraMode.this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_FLASH, false);
                    SetCameraMode.this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_FOCUS, false);
                    if (!CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(SetCameraMode.this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
                        SetCameraMode.this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_BRIGHTNESS, true);
                    }
                    SetCameraMode.this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_BEAUTYSHOT, true);
                } else if (SetCameraMode.this.mGet.getApplicationMode() == 0 && CameraConstants.TYPE_SHOTMODE_TIMEMACHINE.equals(SetCameraMode.this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
                    SetCameraMode.this.mGet.setQuickFunctionAllMenuEnabled(true, false);
                    SetCameraMode.this.mGet.setQuickButtonMenuEnable(true, false);
                } else {
                    SetCameraMode.this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_ZOOM, true);
                }
                if (SetCameraMode.this.mGet.getApplicationMode() == 1 && SetCameraMode.this.mGet.isMMSIntent()) {
                    SetCameraMode.this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_LIVE_EFFECT, false);
                    SetCameraMode.this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VIDEO_RECORD_MODE, false);
                } else {
                    if (TelephonyUtil.phoneInCall(SetCameraMode.this.mGet.getApplicationContext()) || !(SetCameraMode.this.mGet.isTimeMachineModeOn() || CheckStatusManager.checkVoiceShutterEnable(SetCameraMode.this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)))) {
                        SetCameraMode.this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, false);
                    } else {
                        SetCameraMode.this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, true);
                    }
                    if (SetCameraMode.this.mGet.isAttachMode()) {
                        SetCameraMode.this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_SHOT_MODE, false);
                    }
                    if (!SetCameraMode.this.mGet.isSettingControllerVisible()) {
                        SetCameraMode.this.mGet.quickFunctionControllerRefresh(true);
                        SetCameraMode.this.mGet.setQuickButtonMenuEnable(true, false);
                    }
                    if (SetCameraMode.this.mGet.isAttachMode()) {
                        SetCameraMode.this.mGet.doCommandUi(Command.SELECT_DURATION, Boolean.valueOf(true));
                    }
                    SetCameraMode.this.mGet.applyCameraChange();
                    CamLog.d(FaceDetector.TAG, "SetCameraMode-end");
                }
                SetCameraMode.this.mGet.setQuickFunctionControllerAllMenuIcons();
            }
        });
    }

    protected void onExecuteAlone() {
        if (this.mGet.getCameraMode() != this.mGet.getSettingIndex(Setting.KEY_SWAP)) {
            this.mGet.setSubCameraModeRunning(true);
            this.mGet.setSubMenuMode(0);
            this.mGet.doCommandUi(Command.REMOVE_SETTING_MENU);
            this.mGet.postOnUiThread(new Runnable() {
                public void run() {
                    SetCameraMode.this.mGet.removePostRunnable(this);
                    if (SetCameraMode.this.mGet.getCameraMode() == 0) {
                        SetCameraMode.this.mGet.setSetting(Setting.KEY_SWAP, "back");
                    } else {
                        SetCameraMode.this.mGet.setSetting(Setting.KEY_SWAP, "front");
                    }
                }
            });
            this.mGet.setSubCameraModeRunning(false);
            if (!this.mLiveEffect) {
                this.mGet.doCommandDelayed(Command.SWAP_CAMERA, 0);
            }
        }
    }
}
