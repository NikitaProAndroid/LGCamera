package com.lge.camera.command.setting;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.command.Command;
import com.lge.camera.controller.BubblePopupController;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.StorageProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;

public class SetOptionalParameters extends SettingCommand {
    public SetOptionalParameters(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, new Bundle());
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetOptionalParameters - start");
        Bundle bundle = (Bundle) arg;
        boolean useSmartMode = bundle.getBoolean("useSmartMode", false);
        boolean useHDR = bundle.getBoolean("useHDR", false);
        if (useSmartMode) {
            setShotModeSetting(lgParameters, false);
        }
        if (useHDR) {
            setHDRSetting(lgParameters, false);
        }
        if (useHDR || useSmartMode) {
            setBrightnessSetting(lgParameters, false);
        }
        if (useSmartMode) {
            setZoomSetting(lgParameters, true);
        }
        if (useHDR) {
            setFlashSetting(lgParameters, CameraConstants.SMART_MODE_OFF, false, true);
        }
        if (useHDR || useSmartMode) {
            setSceneModeSetting(lgParameters, false);
        }
        if (useHDR || useSmartMode) {
            setISOsetting(lgParameters, false);
        }
        if (useHDR || useSmartMode) {
            setWhiteBalanceSetting(lgParameters, false);
        }
        if (useHDR || useSmartMode) {
            setColorEffectSetting(lgParameters, false);
        }
        if (useHDR || useSmartMode) {
            setFocusSetting(lgParameters, false);
        }
        if (useHDR || useSmartMode) {
            setVoiceShutterSetting();
        }
        if (useHDR || useSmartMode) {
            setStorageSetting();
        }
        if (useSmartMode) {
            setTimerSetting();
        }
        if (useSmartMode) {
            setAutoReviewSetting(lgParameters);
        }
        if (useSmartMode) {
            setCameraModeSetting();
        }
    }

    private void setCameraModeSetting() {
        if (this.mGet.getCameraMode() == 0) {
            this.mGet.setSetting(Setting.KEY_SWAP, "back");
        } else {
            this.mGet.setSetting(Setting.KEY_SWAP, "front");
        }
    }

    private void setShotModeSetting(LGParameters lgParameters, boolean menuEnable) {
        if (!this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_NORMAL)) {
            if (this.mGet.isTimeMachineModeOn()) {
                try {
                    this.mGet.deleteTimeMachineImages();
                    this.mGet.setTimemachineHasPictures(false);
                    BubblePopupController.get().initializeNotiComplete();
                } catch (Exception e) {
                    CamLog.w(FaceDetector.TAG, "Exception:", e);
                }
                this.mGet.setSetting(Setting.KEY_TIME_MACHINE, CameraConstants.SMART_MODE_OFF);
            } else if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_REFOCUS)) {
                try {
                    this.mGet.deleteRefocusShotImages();
                    this.mGet.setRefocusShotHasPictures(false);
                    BubblePopupController.get().initializeNotiComplete();
                } catch (Exception e2) {
                    CamLog.w(FaceDetector.TAG, "Exception:", e2);
                }
            }
            this.mGet.setSetting(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_NORMAL);
            this.mGet.doCommand(Command.CAMERA_SHOT_MODE, lgParameters);
        }
        this.mGet.setPreferenceMenuEnable(Setting.KEY_TIME_MACHINE, menuEnable, false);
        this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_SHOT_MODE, menuEnable, false);
    }

    private void setHDRSetting(LGParameters lgParameters, boolean menuEnable) {
        lgParameters.getParameters().set(CameraConstants.HDR_MODE, "1");
    }

    private void setBrightnessSetting(LGParameters lgParameters, boolean menuEnable) {
        ListPreference preference = this.mGet.findPreference(Setting.KEY_BRIGHTNESS);
        if (!(preference == null || "6".equals(this.mGet.getSettingValue(Setting.KEY_BRIGHTNESS)))) {
            int valueForParameter = Common.scaleParameter(Integer.parseInt(preference.getDefaultValue()), lgParameters.getParameters());
            CamLog.i(FaceDetector.TAG, "brightness value to parameter: " + valueForParameter);
            lgParameters.getParameters().setExposureCompensation(valueForParameter);
        }
        this.mGet.setPreferenceMenuEnable(Setting.KEY_BRIGHTNESS, menuEnable, false);
    }

    private void setZoomSetting(LGParameters lgParameters, boolean menuEnable) {
        this.mGet.doCommand(Command.SET_ZOOM, lgParameters);
    }

    private void setFlashSetting(LGParameters lgParameters, final String value, boolean menuEnable, boolean changSettingValue) {
        lgParameters.getParameters().setFlashMode(value);
        this.mGet.setPreferenceMenuEnable(Setting.KEY_FLASH, menuEnable, false);
        this.mGet.postOnUiThread(new Runnable() {
            public void run() {
                SetOptionalParameters.this.mGet.removePostRunnable(this);
                SetOptionalParameters.this.mGet.updateFlashIndicator(true, value);
            }
        });
    }

    private void setSceneModeSetting(LGParameters lgParameters, boolean menuEnable) {
        if (!LGT_Limit.ISP_AUTOMODE_AUTO.equals(this.mGet.getSettingValue(Setting.KEY_SCENE_MODE))) {
            String settingValue = LGT_Limit.ISP_AUTOMODE_AUTO;
            CamLog.i(FaceDetector.TAG, "KEY_SCENE_MODE [" + settingValue + "] ");
            this.mGet.setSceneModeForAdvanced(lgParameters.getParameters(), settingValue);
        }
        this.mGet.setPreferenceMenuEnable(Setting.KEY_SCENE_MODE, menuEnable, false);
        this.mGet.postOnUiThread(new Runnable() {
            public void run() {
                SetOptionalParameters.this.mGet.removePostRunnable(this);
                SetOptionalParameters.this.mGet.updateSceneIndicator(true, LGT_Limit.ISP_AUTOMODE_AUTO);
            }
        });
    }

    private void setISOsetting(LGParameters lgParameters, boolean menuEnable) {
        if (!LGT_Limit.ISP_AUTOMODE_AUTO.equals(this.mGet.getSettingValue(Setting.KEY_ISO))) {
            String settingValue = LGT_Limit.ISP_AUTOMODE_AUTO;
            CamLog.i(FaceDetector.TAG, "configValue [" + settingValue + "] ");
            lgParameters.getParameters().set(LGT_Limit.ISP_ISO, settingValue);
        }
        this.mGet.setPreferenceMenuEnable(Setting.KEY_ISO, menuEnable, false);
    }

    private void setWhiteBalanceSetting(LGParameters lgParameters, boolean menuEnable) {
        if (!LGT_Limit.ISP_AUTOMODE_AUTO.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_WHITEBALANCE))) {
            String settingValue = LGT_Limit.ISP_AUTOMODE_AUTO;
            CamLog.i(FaceDetector.TAG, "KEY_CAMERA_WHITEBALANCE [" + settingValue + "] ");
            lgParameters.getParameters().setWhiteBalance(settingValue);
        }
        this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_WHITEBALANCE, menuEnable, false);
    }

    private void setColorEffectSetting(LGParameters lgParameters, boolean menuEnable) {
        if (CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_COLOREFFECT))) {
            CamLog.d(FaceDetector.TAG, "Need to check string whether it is convertable or not.");
            return;
        }
        if (!Setting.HELP_OTHER.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_COLOREFFECT))) {
            String settingValue = Setting.HELP_OTHER;
            CamLog.i(FaceDetector.TAG, "KEY_CAMERA_COLOREFFECT [" + settingValue + "] ");
            lgParameters.getParameters().setColorEffect(settingValue);
        }
        this.mGet.setPreferenceMenuEnable(Setting.KEY_CAMERA_COLOREFFECT, menuEnable, false);
    }

    private void setFocusSetting(LGParameters lgParameters, boolean menuEnable) {
        String focusValue = null;
        ListPreference pref = this.mGet.findPreference(Setting.KEY_FOCUS);
        if (pref != null) {
            focusValue = pref.getDefaultValue();
        }
        if (!(focusValue == null || focusValue.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) || this.mGet.getIAFlashStatus()) {
            this.mGet.setSetting(Setting.KEY_FOCUS, focusValue);
            CamLog.i(FaceDetector.TAG, "KEY_FOCUS - " + focusValue);
            this.mGet.doCommand(Command.CAMERA_FOCUS_MODE, lgParameters);
        }
        this.mGet.setEnable3ALocks(lgParameters, false);
        this.mGet.setPreferenceMenuEnable(Setting.KEY_FOCUS, menuEnable, false);
    }

    private void setStorageSetting() {
        if (this.mGet.isExternalStorageRemoved()) {
            this.mGet.setCurrentStorage(1);
            this.mGet.setPreferenceMenuEnable(Setting.KEY_STORAGE, false, false);
        } else {
            if (StorageProperties.getEmmcName().equals(this.mGet.getSettingValue(Setting.KEY_STORAGE)) || StorageProperties.isInternalMemoryOnly()) {
                this.mGet.setCurrentStorage(1);
            } else {
                this.mGet.setCurrentStorage(0);
            }
            this.mGet.setPreferenceMenuEnable(Setting.KEY_STORAGE, true, false);
        }
        this.mGet.postOnUiThread(new Runnable() {
            public void run() {
                SetOptionalParameters.this.mGet.removePostRunnable(this);
                SetOptionalParameters.this.mGet.updateStorageIndicator();
            }
        });
    }

    private void setVoiceShutterSetting() {
        if (FunctionProperties.isVoiceShutter()) {
            if (CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_VOICESHUTTER))) {
                this.mGet.doCommandUi(Command.SET_VOICE_SHUTTER);
            }
        }
    }

    private void setTimerSetting() {
        if (!"0".equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_TIMER))) {
            this.mGet.setTimerSetting(0);
            this.mGet.postOnUiThread(new Runnable() {
                public void run() {
                    SetOptionalParameters.this.mGet.removePostRunnable(this);
                    SetOptionalParameters.this.mGet.updateTimerIndicator();
                }
            });
        }
    }

    private void setAutoReviewSetting(LGParameters lgParameters) {
        if (!CameraConstants.SMART_MODE_OFF.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_AUTO_REVIEW))) {
            this.mGet.setSetting(Setting.KEY_CAMERA_AUTO_REVIEW, CameraConstants.SMART_MODE_OFF);
            this.mGet.doCommandUi(Command.CAMERA_AUTO_REVIEW, lgParameters);
        }
    }
}
