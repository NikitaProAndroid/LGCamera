package com.lge.camera.command.setting;

import android.os.Bundle;
import android.provider.Settings.System;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.module.Module;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.DialogCreater;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;

public class SetCameraFocusMode extends SettingCommand {
    public SetCameraFocusMode(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, new Bundle());
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetCameraFocusMode");
        boolean allSetting = ((Bundle) arg).getBoolean("allSetting", false);
        if (this.mGet.getCameraId() != 1) {
            updateIndicator();
            String focusSetting = this.mGet.getSettingValue(Setting.KEY_FOCUS);
            String shotMode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
            CamLog.i(FaceDetector.TAG, "Focus setting value : " + focusSetting);
            if (LGT_Limit.ISP_AUTOMODE_AUTO.equals(focusSetting) || CameraConstants.FOCUS_SETTING_VALUE_MULTIWINDOWAF.equals(focusSetting)) {
                setFocusAuto(lgParameters, allSetting);
            } else if (CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(focusSetting)) {
                setFocusManual(lgParameters, allSetting);
            } else if (Setting.HELP_FACE_TRACKING_LED.equals(focusSetting)) {
                setFocusFaceTracking(lgParameters, shotMode, allSetting);
            }
            if (!(Module.CLEAR_SHOT.equals(this.mGet.getCurrentModuleName()) && this.mGet.isCurrnetModuleRunning())) {
                this.mGet.setEnable3ALocks(lgParameters, false);
            }
            setFocusMenuEnableForShotMode(shotMode);
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    SetCameraFocusMode.this.mGet.removePostRunnable(this);
                    SetCameraFocusMode.this.mGet.setBlockingFaceTrFocusing(false);
                }
            });
        }
    }

    private void setFocusFaceTracking(LGParameters lgParameters, String shotMode, final boolean allSetting) {
        if (!(CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_COLOREFFECT)) || Setting.HELP_OTHER.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_COLOREFFECT)))) {
            this.mGet.setSetting(Setting.KEY_CAMERA_COLOREFFECT, Setting.HELP_OTHER);
            lgParameters.getParameters().setColorEffect(Setting.HELP_OTHER);
            this.mGet.toast((int) R.string.note_color_effect_change_none);
        }
        if (!(CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotMode) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(shotMode) || this.mGet.isTimeMachineModeOn())) {
            this.mGet.toast((int) R.string.note_shot_mode_change_normal);
            this.mGet.setModule(Module.DEFAULT_NORMAL_SHOT);
            this.mGet.setSetting(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_NORMAL);
            this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_PICTURESIZE, true, false);
            this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_TIMER, true, false);
            this.mGet.doCommand(Command.CAMERA_IMAGE_SIZE, lgParameters.getParameters());
        }
        if (FunctionProperties.useFaceDetectionFromHal()) {
            this.mGet.registerFaceTrackingCallback();
        }
        lgParameters.getParameters().set(CameraConstants.FOCUS_MODE_MANUAL, 1);
        if (this.mGet.isCafSupported()) {
            if (this.mGet.isShutterButtonLongKey()) {
                lgParameters.getParameters().setFocusMode(LGT_Limit.ISP_AUTOMODE_AUTO);
                CamLog.d(FaceDetector.TAG, "### setFocusMode-auto");
            } else {
                String focusMode = lgParameters.getParameters().getSupportedFocusModes().contains(CameraConstants.FOCUS_MODE_MULTIWINDOWAF) ? CameraConstants.FOCUS_MODE_MULTIWINDOWAF : "continuous-picture";
                lgParameters.getParameters().setFocusMode(focusMode);
                CamLog.d(FaceDetector.TAG, "### setFocusMode-" + focusMode);
            }
        }
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                SetCameraFocusMode.this.mGet.removePostRunnable(this);
                SetCameraFocusMode.this.mGet.facePreviewInitController();
                if (!(allSetting && FunctionProperties.useFaceDetectionFromHal())) {
                    if (FunctionProperties.isFaceDetectionAuto()) {
                        SetCameraFocusMode.this.mGet.stopFaceDetection();
                    }
                    SetCameraFocusMode.this.mGet.startFaceDetection(true);
                }
                SetCameraFocusMode.this.mGet.setFocusRectangleInitialize();
                SetCameraFocusMode.this.mGet.showManualFocusController(false);
                SetCameraFocusMode.this.mGet.hideFocus();
                if (!SetCameraFocusMode.this.mGet.isReadyEngineProcessor() && !SetCameraFocusMode.this.mGet.isPreviewOnGoing()) {
                    SetCameraFocusMode.this.mGet.setEngineProcessor();
                }
            }
        });
        if (FunctionProperties.isSupportEmotionalLED()) {
            int faceLedEnabled = System.getInt(this.mGet.getContentResolver(), "emotional_led_back_camera_face_detecting_noti", 1);
            if (!allSetting && faceLedEnabled == 1) {
                this.mGet.showHelpGuidePopup(Setting.HELP_FACE_TRACKING_LED, DialogCreater.DIALOG_ID_HELP_FACE_TRACKING_LED, true);
            }
        }
    }

    private void setFocusMenuEnableForShotMode(String shotMode) {
        if (shotMode.equals(CameraConstants.TYPE_SHOTMODE_NORMAL) || shotMode.equals(CameraConstants.TYPE_PREFERENCE_NOT_FOUND) || this.mGet.isTimeMachineModeOn()) {
            this.mGet.setPreferenceMenuEnable(Setting.KEY_FOCUS, true, false);
        } else {
            this.mGet.setPreferenceMenuEnable(Setting.KEY_FOCUS, false, false);
        }
    }

    private void setFocusAuto(LGParameters lgParameters, final boolean allSetting) {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                SetCameraFocusMode.this.mGet.removePostRunnable(this);
                String smartMode = SetCameraFocusMode.this.mGet.getSettingValue(Setting.KEY_SMART_MODE);
                if (SetCameraFocusMode.this.mGet.isfacePreviewInitialized() && ((CameraConstants.SMART_MODE_OFF.equals(smartMode) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(smartMode)) && !FunctionProperties.isFaceDetectionAuto())) {
                    SetCameraFocusMode.this.mGet.stopFaceDetection();
                }
                String shotMode = SetCameraFocusMode.this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
                if (shotMode.equals(CameraConstants.TYPE_SHOTMODE_NORMAL) || shotMode.equals(CameraConstants.TYPE_PREFERENCE_NOT_FOUND) || SetCameraFocusMode.this.mGet.isTimeMachineModeOn()) {
                    SetCameraFocusMode.this.mGet.releaseAllEngine();
                    SetCameraFocusMode.this.mGet.removePreviewCallback();
                }
                SetCameraFocusMode.this.mGet.setFocusRectangleInitialize();
                if (allSetting) {
                    SetCameraFocusMode.this.showFocusForSetFocusMode();
                }
                SetCameraFocusMode.this.mGet.showManualFocusController(false);
                if (FunctionProperties.isFaceDetectionAuto() && !SetCameraFocusMode.this.mGet.checkFaceDetectionNoUI()) {
                    SetCameraFocusMode.this.mGet.stopFaceDetection();
                    SetCameraFocusMode.this.mGet.startFaceDetection(false);
                }
            }
        });
        lgParameters.getParameters().set(CameraConstants.FOCUS_MODE_MANUAL, 0);
        if (!allSetting) {
            lgParameters.getParameters().setFocusAreas(null);
            lgParameters.getParameters().setMeteringAreas(null);
        }
        if (!this.mGet.isCafSupported()) {
            return;
        }
        if (this.mGet.isShutterButtonLongKey()) {
            CamLog.d(FaceDetector.TAG, "###setFocusMode-auto");
            lgParameters.getParameters().setFocusMode(LGT_Limit.ISP_AUTOMODE_AUTO);
            return;
        }
        String focusMode = this.mGet.getDefaultFocusModeParameterForMultiWindowAF(lgParameters);
        lgParameters.getParameters().setFocusMode(focusMode);
        CamLog.d(FaceDetector.TAG, "### setFocusMode-" + focusMode);
    }

    private void setFocusManual(LGParameters lgParameters, final boolean allSetting) {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                SetCameraFocusMode.this.mGet.removePostRunnable(this);
                if (SetCameraFocusMode.this.mGet.isfacePreviewInitialized() && !FunctionProperties.isFaceDetectionAuto()) {
                    SetCameraFocusMode.this.mGet.stopFaceDetection();
                }
                String shotMode = SetCameraFocusMode.this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
                if (CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotMode) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(shotMode) || SetCameraFocusMode.this.mGet.isTimeMachineModeOn()) {
                    SetCameraFocusMode.this.mGet.releaseAllEngine();
                    SetCameraFocusMode.this.mGet.removePreviewCallback();
                }
                SetCameraFocusMode.this.mGet.setFocusRectangleInitialize();
                if (!allSetting) {
                    if (!(SetCameraFocusMode.this.mGet.getSubMenuMode() == 0 || SetCameraFocusMode.this.mGet.isNullSettingView())) {
                        SetCameraFocusMode.this.mGet.doCommandUi(Command.REMOVE_SETTING_MENU);
                    }
                    SetCameraFocusMode.this.mGet.clearSubMenu(false);
                    SetCameraFocusMode.this.mGet.showManualFocusController(true);
                    SetCameraFocusMode.this.mGet.setSubMenuMode(25);
                } else if (SetCameraFocusMode.this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) || SetCameraFocusMode.this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) || SetCameraFocusMode.this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
                    SetCameraFocusMode.this.mGet.hideFocus();
                } else if (SetCameraFocusMode.this.mGet.isManualFocusBarVisible()) {
                    SetCameraFocusMode.this.mGet.showManualFocusController(true);
                    SetCameraFocusMode.this.mGet.setSubMenuMode(25);
                }
                if (FunctionProperties.isFaceDetectionAuto() && !SetCameraFocusMode.this.mGet.checkFaceDetectionNoUI()) {
                    SetCameraFocusMode.this.mGet.stopFaceDetection();
                    SetCameraFocusMode.this.mGet.startFaceDetection(false);
                }
            }
        });
        if (!allSetting) {
            lgParameters.getParameters().setFocusAreas(null);
            lgParameters.getParameters().setMeteringAreas(null);
        }
        lgParameters.getParameters().set(CameraConstants.FOCUS_MODE_MANUAL, 0);
        lgParameters.getParameters().setFocusMode("normal");
        lgParameters.getParameters().set("manualfocus_step", this.mGet.getManualFocusValue());
        CamLog.d(FaceDetector.TAG, "### setFocusMode-manual");
    }

    private void updateIndicator() {
        if (this.mGet.isIndicatorControllerInitialized()) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    SetCameraFocusMode.this.mGet.removePostRunnable(this);
                    SetCameraFocusMode.this.mGet.updateFocusIndicator();
                }
            });
        }
    }

    private void showFocusForSetFocusMode() {
        if (this.mGet.getCameraId() == 0 && !Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)) && !CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
            if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
                this.mGet.hideFocus();
            } else {
                this.mGet.showFocus();
            }
        }
    }

    protected void onExecuteAlone() {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                SetCameraFocusMode.this.mGet.removePostRunnable(this);
                SetCameraFocusMode.this.showFocusForSetFocusMode();
                if (FunctionProperties.useFaceDetectionFromHal() && Setting.HELP_FACE_TRACKING_LED.equals(SetCameraFocusMode.this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
                    SetCameraFocusMode.this.mGet.restartPreview(null, false);
                }
            }
        });
    }
}
