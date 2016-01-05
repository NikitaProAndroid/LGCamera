package com.lge.camera.command.setting;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.RotateImageView;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;

public class RestoreOptionalParameters extends SettingCommand {
    public RestoreOptionalParameters(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, new Bundle());
    }

    public void execute(LGParameters lgParameters, Object arg) {
        Bundle bundle = (Bundle) arg;
        boolean useSmartMode = bundle.getBoolean("useSmartMode", false);
        boolean useHDR = bundle.getBoolean("useHDR", false);
        if (useSmartMode) {
            restoreShotMode(lgParameters, true);
        }
        if (useHDR) {
            restoreHDR(lgParameters, true);
        }
        if (useHDR || useSmartMode) {
            restoreBrightness(lgParameters, true);
        }
        if (useHDR || useSmartMode) {
            restoreFlash(lgParameters, true);
        }
        if (useHDR || useSmartMode) {
            restoreSceneMode(lgParameters, true);
        }
        if (useHDR || useSmartMode) {
            restoreISO(lgParameters, true);
        }
        if (useHDR || useSmartMode) {
            restoreWhiteBalance(lgParameters, true);
        }
        if (useHDR || useSmartMode) {
            restoreColorEffect(lgParameters, true);
        }
        if (useHDR || useSmartMode) {
            restoreFocus(lgParameters, true);
        }
        if (useSmartMode) {
            restoreTimerSetting(lgParameters, true);
        }
    }

    private void restoreShotMode(LGParameters lgParameters, boolean enable) {
        if (!this.mGet.isAttachMode()) {
            setMenuControlEnable(Setting.KEY_CAMERA_SHOT_MODE, enable);
            setMenuControlEnable(Setting.KEY_TIME_MACHINE, enable);
        }
    }

    private void restoreHDR(LGParameters lgParameters, boolean enable) {
        lgParameters.getParameters().set(CameraConstants.HDR_MODE, "0");
    }

    private void restoreBrightness(LGParameters lgParameters, boolean enable) {
        String stringValue = getPreferenceStringValue(Setting.KEY_BRIGHTNESS);
        if (CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(stringValue)) {
            CamLog.d(FaceDetector.TAG, "Need to check string whether it is convertable or not.");
            return;
        }
        int valueForParameter = Common.scaleParameter(Integer.parseInt(stringValue), lgParameters.getParameters());
        CamLog.i(FaceDetector.TAG, "KEY_BRIGHTNESS value to parameter: " + valueForParameter);
        lgParameters.getParameters().setExposureCompensation(valueForParameter);
        setMenuControlEnable(Setting.KEY_BRIGHTNESS, enable);
    }

    private void restoreFlash(LGParameters lgParameters, boolean enable) {
        String stringValue = getPreferenceStringValue(Setting.KEY_FLASH);
        if (CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(stringValue)) {
            CamLog.d(FaceDetector.TAG, "Need to check string whether it is convertable or not.");
            return;
        }
        CamLog.i(FaceDetector.TAG, "KEY_FLASH value to parameter: " + stringValue);
        lgParameters.getParameters().setFlashMode(stringValue);
        ((RotateImageView) this.mGet.findViewById(R.id.icon_flash)).setVisibility(0);
        this.mGet.updateFlashIndicator(false, null);
        setMenuControlEnable(Setting.KEY_FLASH, enable);
    }

    private void restoreSceneMode(LGParameters lgParameters, boolean enable) {
        String stringValue = getPreferenceStringValue(Setting.KEY_SCENE_MODE);
        if (CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(stringValue)) {
            CamLog.d(FaceDetector.TAG, "Need to check string whether it is convertable or not.");
            return;
        }
        CamLog.i(FaceDetector.TAG, "KEY_SCENE_MODE value to parameter: " + stringValue);
        this.mGet.setSceneModeForAdvanced(lgParameters.getParameters(), stringValue);
        this.mGet.updateSceneIndicator(false, null);
        setMenuControlEnable(Setting.KEY_SCENE_MODE, enable);
    }

    private void restoreISO(LGParameters lgParameters, boolean enable) {
        String stringValue = getPreferenceStringValue(Setting.KEY_ISO);
        if (CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(stringValue)) {
            CamLog.d(FaceDetector.TAG, "Need to check string whether it is convertable or not.");
            return;
        }
        CamLog.i(FaceDetector.TAG, "KEY_ISO value to parameter: " + stringValue);
        lgParameters.getParameters().set(LGT_Limit.ISP_ISO, stringValue);
        setMenuControlEnable(Setting.KEY_ISO, enable);
    }

    private void restoreWhiteBalance(LGParameters lgParameters, boolean enable) {
        String stringValue = getPreferenceStringValue(Setting.KEY_CAMERA_WHITEBALANCE);
        if (CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(stringValue)) {
            CamLog.d(FaceDetector.TAG, "Need to check string whether it is convertable or not.");
            return;
        }
        CamLog.i(FaceDetector.TAG, "KEY_CAMERA_WHITEBALANCE value to parameter: " + stringValue);
        lgParameters.getParameters().setWhiteBalance(stringValue);
        setMenuControlEnable(Setting.KEY_CAMERA_WHITEBALANCE, enable);
    }

    private void restoreColorEffect(LGParameters lgParameters, boolean enable) {
        String stringValue = getPreferenceStringValue(Setting.KEY_CAMERA_COLOREFFECT);
        if (CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(stringValue)) {
            CamLog.d(FaceDetector.TAG, "Need to check string whether it is convertable or not.");
            return;
        }
        CamLog.i(FaceDetector.TAG, "KEY_CAMERA_COLOREFFECT value to parameter: " + stringValue);
        lgParameters.getParameters().setColorEffect(stringValue);
        setMenuControlEnable(Setting.KEY_CAMERA_COLOREFFECT, enable);
    }

    private void restoreFocus(LGParameters lgParameters, boolean enable) {
        if (CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(getPreferenceStringValue(Setting.KEY_FOCUS))) {
            CamLog.d(FaceDetector.TAG, "Need to check string whether it is convertable or not.");
            return;
        }
        this.mGet.updateFocusIndicator();
        setMenuControlEnable(Setting.KEY_FOCUS, enable);
    }

    private void restoreTimerSetting(LGParameters lgParameters, boolean enable) {
        if (CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(getPreferenceStringValue(Setting.KEY_CAMERA_TIMER))) {
            CamLog.d(FaceDetector.TAG, "Need to check string whether it is convertable or not.");
        } else {
            this.mGet.doCommandUi(Command.CAMERA_TIMER);
        }
    }

    private boolean checkPreferencePresents(String key) {
        if (this.mGet.getPreferenceGroup() == null) {
            CamLog.d(FaceDetector.TAG, "prefGroup null error");
            return false;
        } else if (this.mGet.findPreference(key) != null) {
            return true;
        } else {
            CamLog.d(FaceDetector.TAG, "listPref null error");
            return false;
        }
    }

    private void setMenuControlEnable(String key, boolean menuEnable) {
        if (checkPreferencePresents(key)) {
            this.mGet.setPreferenceMenuEnable(key, menuEnable, false);
        }
    }

    private String getPreferenceStringValue(String key) {
        if (!checkPreferencePresents(key)) {
            return CameraConstants.TYPE_PREFERENCE_NOT_FOUND;
        }
        String stringValue = this.mGet.findPreference(key).getValue();
        CamLog.d(FaceDetector.TAG, "string listPref value = " + stringValue);
        return stringValue;
    }
}
