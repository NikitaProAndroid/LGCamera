package com.lge.camera.command.setting;

import android.hardware.Camera.Parameters;
import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.DialogCreater;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;

public class SetSceneMode extends SettingCommand {
    private boolean isFromGridView;
    private String mBackupSceneMode;
    private String newSelectedSceneMode;
    private boolean noNeedRestartPreview;
    private String oldSceneModeValue;

    public SetSceneMode(ControllerFunction function) {
        super(function);
        this.oldSceneModeValue = "";
        this.newSelectedSceneMode = "";
        this.mBackupSceneMode = LGT_Limit.ISP_AUTOMODE_AUTO;
        this.isFromGridView = false;
        this.noNeedRestartPreview = false;
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, new Bundle());
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetSceneMode");
        Bundle bundle = (Bundle) arg;
        boolean allSetting = bundle.getBoolean("allSetting", false);
        boolean isModeMenuCommand = bundle.getBoolean(CameraConstants.MODE_MENU_COMMAND, false);
        this.noNeedRestartPreview = false;
        if (!allSetting) {
            this.isFromGridView = bundle.getBoolean("fromGridView", false);
        }
        this.oldSceneModeValue = lgParameters.getParameters().getSceneMode();
        this.newSelectedSceneMode = this.mGet.getSettingValue(Setting.KEY_SCENE_MODE);
        if (this.mGet.isIndicatorControllerInitialized()) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    SetSceneMode.this.mGet.removePostRunnable(this);
                    SetSceneMode.this.mGet.updateSceneIndicator(false, null);
                }
            });
        }
        checkOtherSettings(lgParameters.getParameters(), allSetting);
        String shotMode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
        if (isModeMenuCommand && !allSetting) {
            if (CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_SMART_MODE))) {
                this.mGet.setSetting(Setting.KEY_SMART_MODE, CameraConstants.SMART_MODE_OFF);
                this.mGet.doCommandUi(Command.SET_SMART_MODE);
                this.noNeedRestartPreview = true;
                return;
            } else if (!(CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotMode) || LGT_Limit.ISP_AUTOMODE_AUTO.equals(this.newSelectedSceneMode))) {
                if (CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(shotMode) && ModelProperties.isRenesasISP()) {
                    this.noNeedRestartPreview = true;
                }
                this.mBackupSceneMode = this.newSelectedSceneMode;
                this.mGet.setSetting(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_NORMAL);
                this.mGet.doCommand(Command.CAMERA_SHOT_MODE, lgParameters);
                this.mGet.setPreviousShotModeString(CameraConstants.TYPE_SHOTMODE_NORMAL);
                shotMode = CameraConstants.TYPE_SHOTMODE_NORMAL;
                this.mGet.showBeautyshotController(false);
                this.mGet.setSubMenuMode(0);
            }
        }
        if (this.mGet.getCameraId() == 1) {
            setLightFrameMenu(shotMode);
        }
        if (!(this.oldSceneModeValue != null && this.oldSceneModeValue.equals(this.newSelectedSceneMode) && LGT_Limit.ISP_AUTOMODE_AUTO.equals(this.mBackupSceneMode))) {
            CamLog.i(FaceDetector.TAG, "####### scene mode set to " + this.newSelectedSceneMode);
            if (this.mGet.isTimeMachineModeOn() || CameraConstants.TYPE_SHOTMODE_PANORAMA.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_CONTINUOUS.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_HDR.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_REFOCUS.equals(shotMode) || this.mGet.getCameraId() == 1 || Setting.HELP_NIGHT.equals(this.newSelectedSceneMode)) {
                this.mGet.setPreferenceMenuEnable(Setting.KEY_FLASH, false, false);
            } else {
                this.mGet.setPreferenceMenuEnable(Setting.KEY_FLASH, true, false);
            }
            if (LGT_Limit.ISP_AUTOMODE_AUTO.equals(this.mBackupSceneMode)) {
                this.mGet.setSetting(Setting.KEY_SCENE_MODE, this.newSelectedSceneMode);
                this.mGet.setSceneModeForAdvanced(lgParameters.getParameters(), this.newSelectedSceneMode);
            } else {
                this.mGet.setSetting(Setting.KEY_SCENE_MODE, this.mBackupSceneMode);
                this.mGet.setSceneModeForAdvanced(lgParameters.getParameters(), this.mBackupSceneMode);
                this.mBackupSceneMode = LGT_Limit.ISP_AUTOMODE_AUTO;
            }
            this.mGet.setMenuEnableForSceneMode(7);
            this.mGet.setPreferenceMenuEnable(Setting.KEY_SCENE_MODE, true, true);
            this.mGet.setQuickFunctionControllerAllMenuIcons();
        }
        this.mGet.doCommand(Command.SET_SUPER_ZOOM, lgParameters);
    }

    private void setLightFrameMenu(String shotMode) {
        if (CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(shotMode)) {
            this.mGet.setPreferenceMenuEnable(Setting.KEY_LIGHT, false, false);
        } else {
            this.mGet.setPreferenceMenuEnable(Setting.KEY_LIGHT, true, false);
        }
    }

    private void checkOtherSettings(Parameters parameters, boolean allSetting) {
        if (!this.mGet.getSettingValue(Setting.KEY_SCENE_MODE).equals(LGT_Limit.ISP_AUTOMODE_AUTO) && !this.mGet.getSettingValue(Setting.KEY_SCENE_MODE).equals(CameraConstants.TYPE_PREFERENCE_NOT_FOUND)) {
            if (!LGT_Limit.ISP_AUTOMODE_AUTO.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_WHITEBALANCE))) {
                this.mGet.setSetting(Setting.KEY_CAMERA_WHITEBALANCE, LGT_Limit.ISP_AUTOMODE_AUTO);
                parameters.setWhiteBalance(LGT_Limit.ISP_AUTOMODE_AUTO);
            }
            if (!(CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_COLOREFFECT)) || Setting.HELP_OTHER.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_COLOREFFECT)))) {
                this.mGet.setSetting(Setting.KEY_CAMERA_COLOREFFECT, Setting.HELP_OTHER);
                parameters.setColorEffect(Setting.HELP_OTHER);
            }
            if (!LGT_Limit.ISP_AUTOMODE_AUTO.equals(this.mGet.getSettingValue(Setting.KEY_ISO)) && this.mGet.getCameraId() == 0) {
                this.mGet.setSetting(Setting.KEY_ISO, LGT_Limit.ISP_AUTOMODE_AUTO);
                parameters.set(LGT_Limit.ISP_ISO, LGT_Limit.ISP_AUTOMODE_AUTO);
            }
            String flashMode = this.mGet.getSettingValue(Setting.KEY_FLASH);
            if (!CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(flashMode) && this.mGet.getCameraId() == 0) {
                if (Setting.HELP_NIGHT.equals(this.newSelectedSceneMode)) {
                    flashMode = CameraConstants.SMART_MODE_OFF;
                }
                parameters.setFlashMode(flashMode);
                this.mGet.postOnUiThread(new Runnable() {
                    public void run() {
                        SetSceneMode.this.mGet.removePostRunnable(this);
                        SetSceneMode.this.mGet.updateFlashIndicator(false, null);
                    }
                });
            }
        }
    }

    private boolean showHelpGuideDialog(String sceneMode) {
        if (!this.isFromGridView) {
            return true;
        }
        if (this.mGet.isRotateDialogVisible()) {
            this.mGet.onDismissRotateDialog();
        }
        if (Setting.HELP_NIGHT.equals(sceneMode)) {
            return this.mGet.showHelpGuidePopup(Setting.HELP_NIGHT, DialogCreater.DIALOG_ID_HELP_NIGHT, true);
        }
        if (Setting.HELP_SPORTS.equals(sceneMode)) {
            return this.mGet.showHelpGuidePopup(Setting.HELP_SPORTS, DialogCreater.DIALOG_ID_HELP_SPORTS, true);
        }
        return true;
    }

    protected void onExecuteAlone() {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                SetSceneMode.this.mGet.removePostRunnable(this);
                String currentSceneMode = SetSceneMode.this.mGet.getSettingValue(Setting.KEY_SCENE_MODE);
                if (SetSceneMode.this.mGet.getApplicationMode() == 0) {
                    if (CameraConstants.TYPE_SHOTMODE_NORMAL.equals(SetSceneMode.this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE)) && !SetSceneMode.this.mGet.isTimeMachineModeOn()) {
                        if (!SetSceneMode.this.oldSceneModeValue.equals(currentSceneMode) && (CameraConstants.SCENE_MODE_SMART_SHUTTER.equals(currentSceneMode) || (CameraConstants.SCENE_MODE_SMART_SHUTTER.equals(SetSceneMode.this.oldSceneModeValue) && !CameraConstants.SCENE_MODE_SMART_SHUTTER.equals(currentSceneMode)))) {
                            SetSceneMode.this.mGet.restartPreview(null, true);
                            if (CameraConstants.SCENE_MODE_SMART_SHUTTER.equals(currentSceneMode) && ModelProperties.getCarrierCode() == 4) {
                                SetSceneMode.this.mGet.toast(SetSceneMode.this.mGet.getString(R.string.sp_help_scene_mode_menu_action_desc_NORMAL));
                            }
                        } else if (ModelProperties.isRenesasISP() && !SetSceneMode.this.oldSceneModeValue.equals(currentSceneMode) && ((Setting.HELP_NIGHT.equals(currentSceneMode) || (Setting.HELP_NIGHT.equals(SetSceneMode.this.oldSceneModeValue) && !Setting.HELP_NIGHT.equals(currentSceneMode))) && !SetSceneMode.this.noNeedRestartPreview)) {
                            SetSceneMode.this.mGet.restartPreview(null, true);
                        }
                    }
                    SetSceneMode.this.showHelpGuideDialog(currentSceneMode);
                }
            }
        });
    }
}
