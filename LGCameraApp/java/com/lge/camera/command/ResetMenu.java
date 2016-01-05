package com.lge.camera.command;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.module.Module;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.PreferenceGroup;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.FileNamer;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class ResetMenu extends Command {
    public ResetMenu(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        PreferenceGroup prefGroup = this.mGet.getPreferenceGroup();
        if (prefGroup == null) {
            CamLog.d(FaceDetector.TAG, "prefGroup null error");
            return;
        }
        CamLog.v(FaceDetector.TAG, "ResetMenu - start");
        if (this.mGet.getApplicationMode() == 0) {
            this.mGet.cancelAutoFocus();
        }
        this.mGet.resetSettingMenu();
        LGParameters parameters = this.mGet.getLGParam();
        String shotMode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
        if (this.mGet.getApplicationMode() == 0) {
            if (this.mGet.getCameraId() == 0) {
                if (FunctionProperties.isSupportSmartMode() && CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_SMART_MODE))) {
                    this.mGet.setSetting(Setting.KEY_SMART_MODE, CameraConstants.SMART_MODE_OFF);
                    this.mGet.doCommand(Command.SET_SMART_MODE, parameters);
                }
                Bundle bundle;
                if (this.mGet.isTimeMachineModeOn()) {
                    this.mGet.setPreviousShotModeString(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE);
                    this.mGet.setModule(Module.DEFAULT_NORMAL_SHOT);
                    this.mGet.setSetting(Setting.KEY_TIME_MACHINE, CameraConstants.SMART_MODE_OFF);
                    this.mGet.setSetting(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_NORMAL);
                    if (FunctionProperties.isNonZSLMode()) {
                        CamLog.d(FaceDetector.TAG, "#### param set zsl off");
                        parameters.getParameters().set("zsl", CameraConstants.SMART_MODE_OFF);
                    }
                    bundle = new Bundle();
                    bundle.putBoolean("subMenuClicked", true);
                    this.mGet.doCommand(Command.SET_TIMEMACHINE_MODE, bundle);
                    this.mGet.setPreviousShotModeString(CameraConstants.TYPE_SHOTMODE_NORMAL);
                } else if (!(CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotMode) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(shotMode))) {
                    bundle = new Bundle();
                    bundle.putBoolean("isFromResetMenu", true);
                    this.mGet.setPreviousShotModeString(shotMode);
                    this.mGet.setModule(Module.DEFAULT_NORMAL_SHOT);
                    this.mGet.setSetting(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_NORMAL);
                    this.mGet.doCommand(Command.CAMERA_SHOT_MODE, parameters, bundle);
                    this.mGet.setPreviousShotModeString(CameraConstants.TYPE_SHOTMODE_NORMAL);
                    this.mGet.doCommand(Command.CAMERA_FLASH_MODE, parameters);
                    this.mGet.doCommand(Command.CAMERA_FOCUS_MODE, parameters);
                }
            } else {
                if (!(CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotMode) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(shotMode))) {
                    this.mGet.setModule(Module.DEFAULT_NORMAL_SHOT);
                    this.mGet.setSetting(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_NORMAL);
                    this.mGet.doCommand(Command.CAMERA_SHOT_MODE, parameters);
                    this.mGet.setPreviousShotModeString(CameraConstants.TYPE_SHOTMODE_NORMAL);
                }
                this.mGet.setSetting(Setting.KEY_LIGHT, CameraConstants.SMART_MODE_OFF);
                this.mGet.setBackgroundColorBlack();
            }
            this.mGet.hideFocus();
        }
        int prefSize = prefGroup.size();
        for (int i = 0; i < prefSize; i++) {
            ListPreference listPref = prefGroup.getListPreference(i);
            if (!(listPref == null || listPref.getDefaultValue() == null || listPref.getEntryCommand() == null)) {
                this.mGet.setSetting(listPref.getKey(), listPref.getDefaultValue());
            }
        }
        this.mGet.resetController();
        this.mGet.setSubMenuMode(0);
        this.mGet.doCommand(Command.APPLY_ALL_SETTINGS, parameters);
        this.mGet.doCommandUi(Command.REMOVE_SETTING_MENU);
        this.mGet.toast((int) R.string.sp_All_settings_reset_NORMAL);
        try {
            this.mGet.setParameters(parameters.getParameters());
        } catch (RuntimeException e) {
            CamLog.w(FaceDetector.TAG, "SetParam-RunTimeException:", e);
        }
        this.mGet.setCurrentSettingMenu(0);
        this.mGet.updateSizeIndicator();
        this.mGet.updateStorageIndicator();
        this.mGet.updateModeMenuIndicator();
        FileNamer.get().reload(this.mGet.getApplicationContext(), this.mGet.getApplicationMode(), this.mGet.getCurrentStorage(), this.mGet.getCurrentStorageDirectory(), true);
        if (MultimediaProperties.isLiveEffectSupported() && "640x480".equals(MultimediaProperties.getLiveeffectResolutions(this.mGet.getCameraId())) && this.mGet.getCameraId() == 1 && this.mGet.getApplicationMode() == 1) {
            this.mGet.restartPreview(null, true);
        } else if (MultimediaProperties.isDualRecordingSupported() && this.mGet.getApplicationMode() == 1) {
            this.mGet.restartPreview(null, true);
        }
        this.mGet.showFocus();
        CamLog.v(FaceDetector.TAG, "ResetMenu - end");
    }
}
