package com.lge.camera.command.setting;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.DialogCreater;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;

public class SetSmartCameraMode extends SettingCommand {
    private boolean isFromGridView;
    private boolean mSmartModeValueOn;

    public SetSmartCameraMode(ControllerFunction function) {
        super(function);
        this.mSmartModeValueOn = true;
        this.isFromGridView = false;
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, new Bundle());
    }

    public void execute(LGParameters lgParameters, Object arg) {
        Bundle bundle = (Bundle) arg;
        final boolean allSetting = bundle.getBoolean("allSetting", false);
        boolean isModeMenuCommand = bundle.getBoolean(CameraConstants.MODE_MENU_COMMAND, false);
        final boolean isShotMode = bundle.getBoolean(CameraConstants.SHOT_MODE_SMART, false);
        if (!allSetting) {
            this.isFromGridView = bundle.getBoolean("fromGridView", false);
        }
        if (checkMediator()) {
            String value = this.mGet.getSettingValue(Setting.KEY_SMART_MODE);
            CamLog.d(FaceDetector.TAG, "## SetSmartCameraMode : " + value);
            if (FunctionProperties.isSupportSmartMode()) {
                if (isModeMenuCommand && !allSetting) {
                    if (!CameraConstants.TYPE_SHOTMODE_NORMAL.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE))) {
                        this.mGet.setSetting(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_NORMAL);
                        this.mGet.doCommand(Command.CAMERA_SHOT_MODE, lgParameters);
                        this.mGet.setPreviousShotModeString(CameraConstants.TYPE_SHOTMODE_NORMAL);
                        return;
                    } else if (!LGT_Limit.ISP_AUTOMODE_AUTO.equals(this.mGet.getSettingValue(Setting.KEY_SCENE_MODE))) {
                        this.mGet.setSetting(Setting.KEY_SCENE_MODE, LGT_Limit.ISP_AUTOMODE_AUTO);
                        this.mGet.doCommand(Command.CAMERA_SCENE_MODE, lgParameters);
                        Bundle fromGridView = new Bundle();
                        fromGridView.putBoolean("fromGridView", this.isFromGridView);
                        this.mGet.doCommand(Command.SET_SMART_MODE, lgParameters, fromGridView);
                        return;
                    }
                }
                if (this.mGet.getApplicationMode() == 0) {
                    if (!ModelProperties.isRenesasISP() && CameraConstants.SMART_MODE_ON.equals(value) && Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)) && this.mGet.isfacePreviewInitialized()) {
                        this.mGet.stopFaceDetection();
                    }
                    this.mGet.setSmartCameraMode(lgParameters, CameraConstants.SMART_MODE_ON.equals(value));
                    this.mSmartModeValueOn = CameraConstants.SMART_MODE_ON.equals(value);
                    this.mGet.setSetting(Setting.KEY_SMART_MODE, value);
                    if (!allSetting) {
                        this.mGet.doCommandUi(Command.RELEASE_TOUCH_FOCUS);
                        this.mGet.enableInput(false);
                        final LGParameters lgParams = lgParameters;
                        this.mGet.postOnUiThread(new Runnable() {
                            public void run() {
                                SetSmartCameraMode.this.mGet.removePostRunnable(this);
                                if (!SetSmartCameraMode.this.mGet.getActivity().isFinishing()) {
                                    Bundle useSmartMode = new Bundle();
                                    useSmartMode.putBoolean("useSmartMode", true);
                                    if (SetSmartCameraMode.this.mSmartModeValueOn) {
                                        SetSmartCameraMode.this.mGet.doCommand(Command.SET_OPTIONAL_PARAMETERS, lgParams, useSmartMode);
                                    } else {
                                        SetSmartCameraMode.this.mGet.doCommand(Command.RESTORE_OPTIONAL_PARAMETERS, lgParams, useSmartMode);
                                    }
                                    if (!isShotMode) {
                                        SetSmartCameraMode.this.mGet.restartPreview(lgParams, true);
                                    }
                                }
                            }
                        });
                    }
                }
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        if (SetSmartCameraMode.this.checkMediator()) {
                            SetSmartCameraMode.this.mGet.removePostRunnable(this);
                            if (SetSmartCameraMode.this.mGet.getApplicationMode() == 0) {
                                SetSmartCameraMode.this.mGet.setQuickButtonMode(SetSmartCameraMode.this.mSmartModeValueOn);
                                SetSmartCameraMode.this.setSmartMode(SetSmartCameraMode.this.mSmartModeValueOn, !allSetting ? CameraConstants.GALLERY_QUICKVIEW_ANI_INTERVAL : 0);
                            }
                        }
                    }
                });
                return;
            }
            this.mSmartModeValueOn = false;
            CamLog.i(FaceDetector.TAG, "SetSmartCameraMode : model is not supported.");
        }
    }

    protected void onExecuteAlone() {
        CamLog.d(FaceDetector.TAG, "SetSmartCameraMode : " + this.mSmartModeValueOn);
        if (FunctionProperties.isSupportSmartMode()) {
            if (this.mGet.getSubMenuMode() == 5 || this.mGet.getSubMenuMode() == 16) {
                this.mGet.setSubMenuMode(0);
                this.mGet.doCommandUi(Command.REMOVE_SETTING_MENU);
            } else if (this.mGet.getSubMenuMode() == 22) {
                this.mGet.setSubMenuMode(0);
                this.mGet.doCommandUi(Command.HIDE_QUICK_FUNCTION_DRAG_DROP);
            } else if (this.mGet.getSubMenuMode() == 21) {
                this.mGet.setSubMenuMode(0);
                this.mGet.doCommandUi(Command.HIDE_QUICK_FUNCTION_SETTING_MENU);
            } else if (this.mGet.getSubMenuMode() != 0) {
                this.mGet.clearSubMenu();
            }
            if (CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_SMART_MODE)) && this.isFromGridView) {
                this.mGet.showHelpGuidePopup(Setting.HELP_INTELLIGENT_AUTO_MODE, DialogCreater.DIALOG_ID_HELP_INTELLIGENT_AUTO_MODE, true);
            }
            this.mGet.quickFunctionControllerInitMenu();
            this.mGet.quickFunctionControllerRefresh(true);
            this.mGet.updateSizeIndicator();
            if (ModelProperties.isRenesasISP() && Setting.HELP_NIGHT.equals(this.mGet.getSettingValue(Setting.KEY_SCENE_MODE))) {
                this.mGet.postOnUiThread(new Runnable() {
                    public void run() {
                        SetSmartCameraMode.this.mGet.removePostRunnable(this);
                        SetSmartCameraMode.this.mGet.restartPreview(null, true);
                    }
                }, 650);
            }
        }
    }

    private void setSmartMode(boolean smartMode, int duration) {
        boolean z = true;
        ControllerFunction controllerFunction;
        if (duration == 0) {
            controllerFunction = this.mGet;
            if (smartMode) {
                z = false;
            }
            controllerFunction.setQuickFunctionControllerVisible(z);
        } else {
            controllerFunction = this.mGet;
            if (smartMode) {
                z = false;
            }
            controllerFunction.qflMenuAnimation(z, duration, null);
        }
        this.mGet.setShutterButtonImage(this.mGet.isShutterButtonEnable(), this.mGet.getOrientationDegree());
    }
}
