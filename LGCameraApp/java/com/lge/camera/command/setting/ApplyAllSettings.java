package com.lge.camera.command.setting;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.command.Command;
import com.lge.camera.module.Module;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.PreferenceGroup;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CheckStatusManager;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class ApplyAllSettings extends SettingCommand {
    public ApplyAllSettings(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        CamLog.d(FaceDetector.TAG, "ApplyAllSettings-start");
        PreferenceGroup prefGroup = this.mGet.getPreferenceGroup();
        if (prefGroup == null) {
            CamLog.d(FaceDetector.TAG, "prefGroup null error");
        } else if (FunctionProperties.isSupportSmartMode() && this.mGet.getApplicationMode() == 0 && CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_SMART_MODE))) {
            CamLog.d(FaceDetector.TAG, "Set SET_OPTIONAL_PARAMETERS again for The Intelligent Auto Shot when apply all settings.");
            bundle = new Bundle();
            bundle.putBoolean("useSmartMode", true);
            bundle.putBoolean("allSetting", true);
            this.mGet.doCommand(Command.CAMERA_IMAGE_SIZE, lgParameters);
            this.mGet.doCommand(Command.CAMERA_FLASH_MODE, lgParameters);
            this.mGet.doCommand(Command.SET_OPTIONAL_PARAMETERS, lgParameters, bundle);
            this.mGet.doCommand(Command.SET_SMART_MODE, lgParameters, bundle);
        } else {
            if (this.mGet.getApplicationMode() == 0) {
                if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_HDR)) {
                    CamLog.d(FaceDetector.TAG, "Set CAMERA_IMAGE_SIZE and SET_OPTIONAL_PARAMETERS again when apply all settings.");
                    this.mGet.setModule(Module.HDR_SHOT);
                    this.mGet.doCommand(Command.CAMERA_IMAGE_SIZE, lgParameters);
                    this.mGet.doCommand(Command.SET_ZOOM, lgParameters);
                    this.mGet.doCommand(Command.CAMERA_TIMER, lgParameters);
                    if (ProjectVariables.isSupportedAutoReview()) {
                        this.mGet.doCommand(Command.CAMERA_AUTO_REVIEW, lgParameters);
                    }
                    this.mGet.setEnable3ALocks(lgParameters, false);
                    bundle = new Bundle();
                    bundle.putBoolean("useHDR", true);
                    this.mGet.doCommand(Command.SET_OPTIONAL_PARAMETERS, lgParameters, bundle);
                    return;
                } else if (!(this.mGet.isTimeMachineModeOn() || CheckStatusManager.checkVoiceShutterEnable(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)))) {
                    this.mGet.setSetting(Setting.KEY_VOICESHUTTER, CameraConstants.SMART_MODE_OFF);
                }
            }
            bundle = new Bundle();
            bundle.putBoolean("allSetting", true);
            int prefSize = prefGroup.size();
            for (int i = 0; i < prefSize; i++) {
                ListPreference listPref = prefGroup.getListPreference(i);
                if (listPref != null) {
                    String command = listPref.getEntryCommand();
                    if (Setting.KEY_CAMERA_ANTI_BANDING.equals(listPref.getKey()) && ModelProperties.getCarrierCode() != 4 && ModelProperties.getCarrierCode() != 7 && ModelProperties.getCarrierCode() != 32) {
                        CamLog.d(FaceDetector.TAG, "kddi ApplyAllSettings");
                    } else if (command != null) {
                        if (this.mGet.getCommandManager().getCommand(command) instanceof SettingCommand) {
                            this.mGet.doCommand(command, lgParameters, bundle);
                        } else {
                            this.mGet.doCommand(command, bundle);
                        }
                    }
                }
            }
            CamLog.d(FaceDetector.TAG, "ApplyAllSettings-end");
        }
    }
}
