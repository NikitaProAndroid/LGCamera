package com.lge.camera.command.setting;

import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.command.Command;
import com.lge.camera.controller.BubblePopupController;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.DialogCreater;
import com.lge.camera.util.SharedPreferenceUtil;
import com.lge.camera.util.TelephonyUtil;
import com.lge.camera.util.Util;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.AutoPanorama;
import com.lge.olaworks.library.FaceDetector;
import java.util.List;

public class SetCameraShotMode extends SettingCommand {
    private boolean isFromGridView;
    private boolean isNormalShot;
    private boolean isSettingSelected;
    private String pictureSizeString;

    public SetCameraShotMode(ControllerFunction function) {
        super(function);
        this.pictureSizeString = null;
        this.isNormalShot = true;
        this.isFromGridView = false;
        this.isSettingSelected = false;
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, new Bundle());
    }

    public void execute(LGParameters lgParameters, Object arg) {
        Bundle bundle = (Bundle) arg;
        boolean allSetting = bundle.getBoolean("allSetting", false);
        boolean isModeMenuCommand = bundle.getBoolean(CameraConstants.MODE_MENU_COMMAND, false);
        boolean fromResetSetting = bundle.getBoolean("isFromResetMenu", false);
        if (!allSetting) {
            this.isFromGridView = bundle.getBoolean("fromGridView", false);
        }
        String shotMode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
        String strPreviousShotMode = this.mGet.getPreviousShotModeString();
        CamLog.d(FaceDetector.TAG, "SetCameraShotMode-start: currentshotmode:" + shotMode + " previousshotmode:" + strPreviousShotMode);
        if (this.mGet.getCameraId() == 1) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    SetCameraShotMode.this.mGet.removePostRunnable(this);
                    String shotMode = SetCameraShotMode.this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
                    if (!SetCameraShotMode.this.mGet.checkSettingValue(Setting.KEY_LIGHT, CameraConstants.SMART_MODE_ON) || CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(shotMode)) {
                        Common.setBacklightToSystemSetting(SetCameraShotMode.this.mGet.getActivity());
                    } else {
                        Common.setBacklightToMax(SetCameraShotMode.this.mGet.getActivity());
                    }
                }
            });
        }
        this.pictureSizeString = null;
        shotMode = isTimeMachineShotModeOn(shotMode);
        if (isModeMenuCommand && !allSetting) {
            if (CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_SMART_MODE))) {
                this.mGet.setSetting(Setting.KEY_SMART_MODE, CameraConstants.SMART_MODE_OFF);
                if (CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(shotMode)) {
                    Bundle smart = new Bundle();
                    smart.putBoolean(CameraConstants.SHOT_MODE_SMART, true);
                    this.mGet.doCommand(Command.SET_SMART_MODE, lgParameters, smart);
                    final LGParameters params = lgParameters;
                    this.mGet.postOnUiThread(new Runnable() {
                        public void run() {
                            SetCameraShotMode.this.mGet.removePostRunnable(this);
                            SetCameraShotMode.this.mGet.doCommand(Command.CAMERA_SHOT_MODE, params);
                        }
                    });
                    return;
                }
                this.mGet.doCommand(Command.SET_SMART_MODE, lgParameters);
                return;
            }
            this.mGet.checkSceneMode(lgParameters, false, null);
            if (!(CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(shotMode))) {
                CamLog.e(FaceDetector.TAG, "beautyshot controll false!");
                this.mGet.showBeautyshotController(false);
            }
        }
        if (CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotMode) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(shotMode)) {
            executeNormalOrNotFoundShotMode(lgParameters, allSetting, fromResetSetting, strPreviousShotMode);
        } else if (CameraConstants.TYPE_SHOTMODE_CONTINUOUS.equals(shotMode)) {
            executeContinuousShotMode(lgParameters, allSetting, fromResetSetting, strPreviousShotMode);
        } else if (CameraConstants.TYPE_SHOTMODE_PANORAMA.equals(shotMode)) {
            executePanoramaShotMode(lgParameters, allSetting, fromResetSetting, strPreviousShotMode);
        } else if (CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA.equals(shotMode)) {
            executePlanePanoramaShotMode(lgParameters, allSetting, fromResetSetting, strPreviousShotMode);
        } else if (CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA.equals(shotMode)) {
            executeFreePanoramaShotMode(lgParameters, allSetting, fromResetSetting, strPreviousShotMode);
        } else if (CameraConstants.TYPE_SHOTMODE_HDR.equals(shotMode)) {
            executeHdrShotMode(lgParameters, allSetting, fromResetSetting, strPreviousShotMode);
        } else if (CameraConstants.TYPE_SHOTMODE_TIMEMACHINE.equals(shotMode)) {
            executeTimemachineShotMode(lgParameters, allSetting, fromResetSetting, strPreviousShotMode);
        } else if (CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS.equals(shotMode)) {
            executeFullFrameContinuousShotMode(lgParameters, allSetting, fromResetSetting, strPreviousShotMode);
        } else if (CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(shotMode)) {
            executeMainBeautyShotMode(lgParameters, allSetting, fromResetSetting, strPreviousShotMode);
        } else if (CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(shotMode)) {
            executeFrontBeautyShotMode(lgParameters, allSetting, fromResetSetting, strPreviousShotMode);
        } else if (CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT.equals(shotMode)) {
            executeClearShotMode(lgParameters, allSetting, fromResetSetting, strPreviousShotMode);
        } else if (CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(shotMode)) {
            executeDualCameraShotMode(lgParameters, allSetting, fromResetSetting, strPreviousShotMode);
        } else if (CameraConstants.TYPE_SHOTMODE_REFOCUS.equals(shotMode)) {
            executeRefocusMode(lgParameters, allSetting, fromResetSetting, strPreviousShotMode);
        }
        if (this.mGet.isAttachMode() && this.mGet.getCameraId() != 1 && ModelProperties.isSupportShotModeModel()) {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_SHOT_MODE, false);
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_TIME_MACHINE, false);
        }
        CamLog.i(FaceDetector.TAG, "pictureSizeString [" + this.pictureSizeString + "]");
        setPictureSizeMenuEnable();
        setPictureSize(lgParameters.getParameters());
        if (!(this.mGet.isTimeMachineModeOn() || CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS.equals(shotMode))) {
            this.mGet.setTimeMachineShot(lgParameters.getParameters(), 1);
        }
        this.mGet.doCommand(Command.SET_SUPER_ZOOM, lgParameters);
        CamLog.d(FaceDetector.TAG, "SetCameraShotMode-end");
    }

    private void executeNormalOrNotFoundShotMode(LGParameters lgParameters, boolean allSetting, boolean fromResetSetting, String strPreviousShotMode) {
        if (this.mGet.getCameraId() == 0) {
            if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_HDR)) {
                Bundle useHDR = new Bundle();
                useHDR.putBoolean("useHDR", true);
                this.mGet.doCommandUi(Command.RESTORE_OPTIONAL_PARAMETERS, lgParameters, useHDR);
                this.mGet.restartPreview(lgParameters, false);
            } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE)) {
                removeTimeMachineShotImage();
                this.mGet.restartPreview(lgParameters, false);
                this.mGet.setTimeMachineLimit(false);
            } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
                this.mGet.stopPlanePanoramaEngine();
                setPictureSizeWithPreviousValue(lgParameters);
                this.mGet.restartPreview(lgParameters, false);
            } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
                this.mGet.removeFreePanoramaView();
                this.mGet.stopFreePanoramaEngine(lgParameters.getParameters());
                if (!allSetting) {
                    setImageSizeAndRestartPreview(lgParameters);
                }
                this.mGet.restartPreview(lgParameters, false);
            } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS)) {
                if (FunctionProperties.isSupportBurstShot()) {
                    CamLog.d(FaceDetector.TAG, "burst shot off");
                    lgParameters.getParameters().set("burst-shot", CameraConstants.SMART_MODE_OFF);
                }
                this.mGet.restartPreview(lgParameters, false);
            } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) {
                this.mGet.setSettingForDualCamera(true);
                setVideoSizeFullHD(lgParameters);
                this.mGet.updateDualRecordingSelection();
                this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
            } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY)) {
                if (!allSetting) {
                    this.mGet.setPreviewRendered(false);
                    setImageSizeAndRestartPreviewForBeautyShot(lgParameters, false);
                }
            } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_REFOCUS)) {
                resetRefocusShotModeSetting(lgParameters, fromResetSetting);
            } else if (!allSetting) {
                setImageSizeAndRestartPreview(lgParameters);
            }
            this.mGet.doCommand(Command.NORMAL_SHOT, lgParameters);
            if (TelephonyUtil.phoneInCall(this.mGet.getApplicationContext())) {
                this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, false);
            } else {
                this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, true);
            }
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_TIMER, true);
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SHUTTER_SOUND, true);
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_TIME_MACHINE, true);
            if (this.mGet.isAttachMode()) {
                this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, false);
            } else {
                this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, true);
            }
            ListPreference pref = this.mGet.findPreference(Setting.KEY_SCENE_MODE);
            if (!(pref == null || pref.findIndexOfValue(Setting.HELP_NIGHT) == -1)) {
                this.mGet.setCurrentSettingMenuEnable(Setting.KEY_SCENE_MODE, Setting.HELP_NIGHT, true);
            }
            this.mGet.smartShutterEnable(true);
            int pictureSizeIndex = this.mGet.getSettingIndex(Setting.KEY_CAMERA_PICTURESIZE);
            ListPreference pictureSizePref = this.mGet.findPreference(Setting.KEY_CAMERA_PICTURESIZE);
            if (pictureSizeIndex == -1) {
                pictureSizeIndex = 0;
                this.mGet.setSetting(this.mGet.findPreferenceIndex(Setting.KEY_CAMERA_PICTURESIZE), 0);
            }
            if (pictureSizePref != null) {
                this.pictureSizeString = pictureSizePref.getEntries()[pictureSizeIndex].toString();
            }
            this.isNormalShot = true;
            if (!allSetting) {
                this.mGet.doCommand(Command.CAMERA_FLASH_MODE, lgParameters);
                this.mGet.doCommand(Command.CAMERA_FOCUS_MODE, lgParameters);
            }
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY)) {
            if (!allSetting) {
                this.mGet.setPreviewRendered(false);
                setImageSizeAndRestartPreviewForBeautyShot(lgParameters, false);
            }
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) {
            this.mGet.setSettingForDualCamera(true);
            setVideoSizeFullHD(lgParameters);
            this.mGet.updateDualRecordingSelection();
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
            this.mGet.doCommand(Command.NORMAL_SHOT, lgParameters);
        }
    }

    public void resetRefocusShotModeSetting(LGParameters lgParameters, boolean fromResetSetting) {
        removeRefocusShotImage();
        lgParameters.getParameters().set(CameraConstants.REFOCUS_PARAM, CameraConstants.REFOCUS_PARAM_VALUE_OFF);
        this.mGet.setRefocusShotPreviewGuideVisibility(false);
        if (!this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY)) {
            this.mGet.restartPreview(lgParameters, false);
        }
    }

    private void executeContinuousShotMode(LGParameters lgParameters, boolean allSetting, boolean fromResetSetting, String strPreviousShotMode) {
        if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_HDR)) {
            Bundle useHDR = new Bundle();
            useHDR.putBoolean("useHDR", true);
            this.mGet.doCommand(Command.RESTORE_OPTIONAL_PARAMETERS, lgParameters, useHDR);
            this.mGet.restartPreview(lgParameters, false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE)) {
            removeTimeMachineShotImage();
            this.mGet.restartPreview(lgParameters, false);
            this.mGet.setTimeMachineLimit(false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
            this.mGet.stopPlanePanoramaEngine();
            setPictureSizeWithPreviousValue(lgParameters);
            this.mGet.restartPreview(lgParameters, false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
            this.mGet.removeFreePanoramaView();
            this.mGet.stopFreePanoramaEngine(lgParameters.getParameters());
            setImageSizeAndRestartPreview(lgParameters);
            this.mGet.removePreviewCallback();
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) {
            this.mGet.setSettingForDualCamera(true);
            setVideoSizeFullHD(lgParameters);
            this.mGet.updateDualRecordingSelection();
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY)) {
            if (!allSetting) {
                this.mGet.setPreviewRendered(false);
                setImageSizeAndRestartPreviewForBeautyShot(lgParameters, false);
            }
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_REFOCUS)) {
            resetRefocusShotModeSetting(lgParameters, fromResetSetting);
        } else {
            setImageSizeAndRestartPreview(lgParameters);
            this.mGet.removePreviewCallback();
        }
        this.mGet.setPreviousShotModeString(strPreviousShotMode);
        this.mGet.setSetting(Setting.KEY_VOICESHUTTER, CameraConstants.SMART_MODE_OFF);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_TIMER, true);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SHUTTER_SOUND, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, true);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_TIME_MACHINE, false);
        this.mGet.setCurrentSettingMenuEnable(Setting.KEY_SCENE_MODE, Setting.HELP_NIGHT, true);
        this.mGet.doCommand(Command.CONTINUOUS_SHOT, lgParameters);
        checkFocusMode(lgParameters.getParameters());
        ListPreference shotModePref = this.mGet.findPreference(Setting.KEY_CAMERA_SHOT_MODE);
        if (shotModePref != null) {
            this.pictureSizeString = shotModePref.getExtraInfo3();
        }
        this.isNormalShot = false;
        if (allSetting) {
            this.mGet.setPreviousShotModeString(CameraConstants.TYPE_SHOTMODE_CONTINUOUS);
        }
    }

    private void executePanoramaShotMode(LGParameters lgParameters, boolean allSetting, boolean fromResetSetting, String strPreviousShotMode) {
        if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_HDR)) {
            Bundle useHDR = new Bundle();
            useHDR.putBoolean("useHDR", true);
            this.mGet.doCommand(Command.RESTORE_OPTIONAL_PARAMETERS, lgParameters, useHDR);
            this.mGet.restartPreview(lgParameters, false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE)) {
            removeTimeMachineShotImage();
            this.mGet.restartPreview(lgParameters, false);
            this.mGet.setTimeMachineLimit(false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS)) {
            if (FunctionProperties.isSupportBurstShot()) {
                CamLog.d(FaceDetector.TAG, "burst shot off");
                lgParameters.getParameters().set("burst-shot", CameraConstants.SMART_MODE_OFF);
            }
            this.mGet.restartPreview(lgParameters, false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
            this.mGet.stopPlanePanoramaEngine();
            setPictureSizeWithPreviousValue(lgParameters);
            this.mGet.restartPreview(lgParameters, false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
            this.mGet.removeFreePanoramaView();
            this.mGet.stopFreePanoramaEngine(lgParameters.getParameters());
            setImageSizeAndRestartPreview(lgParameters);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) {
            this.mGet.setSettingForDualCamera(true);
            setVideoSizeFullHD(lgParameters);
            this.mGet.updateDualRecordingSelection();
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY)) {
            if (!allSetting) {
                this.mGet.setPreviewRendered(false);
                setImageSizeAndRestartPreviewForBeautyShot(lgParameters, false);
            }
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_REFOCUS)) {
            resetRefocusShotModeSetting(lgParameters, fromResetSetting);
        } else {
            setImageSizeAndRestartPreview(lgParameters);
        }
        this.mGet.setPreviousShotModeString(strPreviousShotMode);
        this.mGet.setSetting(Setting.KEY_VOICESHUTTER, CameraConstants.SMART_MODE_OFF);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_TIMER, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SHUTTER_SOUND, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, true);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_TIME_MACHINE, false);
        this.mGet.setCurrentSettingMenuEnable(Setting.KEY_SCENE_MODE, Setting.HELP_NIGHT, true);
        this.mGet.doCommand(Command.PANORAMA_SHOT, lgParameters);
        checkFocusMode(lgParameters.getParameters());
        ListPreference shotModePref = this.mGet.findPreference(Setting.KEY_CAMERA_SHOT_MODE);
        if (shotModePref != null) {
            this.pictureSizeString = shotModePref.getExtraInfo3();
        }
        this.isNormalShot = false;
        if (allSetting) {
            this.mGet.setPreviousShotModeString(CameraConstants.TYPE_SHOTMODE_PANORAMA);
            return;
        }
        this.mGet.doCommand(Command.CAMERA_FLASH_MODE, lgParameters);
        this.mGet.doCommand(Command.CAMERA_FOCUS_MODE, lgParameters);
    }

    private void checkPreviousModeBeforePlanePanorama(LGParameters lgParameters, boolean allSetting, boolean fromResetSetting, String strPreviousShotMode) {
        if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_HDR)) {
            Bundle useHDR = new Bundle();
            useHDR.putBoolean("useHDR", true);
            this.mGet.doCommand(Command.RESTORE_OPTIONAL_PARAMETERS, lgParameters, useHDR);
            this.mGet.restartPreview(lgParameters, false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE)) {
            removeTimeMachineShotImage();
            this.mGet.restartPreview(lgParameters, false);
            this.mGet.setTimeMachineLimit(false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS)) {
            if (FunctionProperties.isSupportBurstShot()) {
                lgParameters.getParameters().set("burst-shot", CameraConstants.SMART_MODE_OFF);
            }
            this.mGet.restartPreview(lgParameters, false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) {
            this.mGet.setSettingForDualCamera(true);
            setVideoSizeFullHD(lgParameters);
            this.mGet.updateDualRecordingSelection();
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
            this.mGet.removeFreePanoramaView();
            this.mGet.stopFreePanoramaEngine(lgParameters.getParameters());
            setImageSizeAndRestartPreview(lgParameters);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY)) {
            if (!allSetting) {
                this.mGet.setPreviewRendered(false);
                setImageSizeAndRestartPreviewForBeautyShot(lgParameters, false);
            }
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_REFOCUS)) {
            resetRefocusShotModeSetting(lgParameters, fromResetSetting);
        } else {
            setImageSizeAndRestartPreview(lgParameters);
        }
    }

    private void executePlanePanoramaShotMode(LGParameters lgParameters, boolean allSetting, boolean fromResetSetting, String strPreviousShotMode) {
        checkPreviousModeBeforePlanePanorama(lgParameters, allSetting, fromResetSetting, strPreviousShotMode);
        this.mGet.setPreviousShotModeString(strPreviousShotMode);
        this.mGet.setSetting(Setting.KEY_VOICESHUTTER, CameraConstants.SMART_MODE_OFF);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_TIMER, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SHUTTER_SOUND, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, true);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_FLASH, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_ZOOM, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_FOCUS, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_PICTURESIZE, false);
        lgParameters.getParameters().setZoom(0);
        this.mGet.resetZoomController();
        this.mGet.doCommand(Command.PLANE_PANORAMA_SHOT, lgParameters);
        checkFocusMode(lgParameters.getParameters());
        this.isNormalShot = false;
        if (allSetting) {
            if (!(this.mGet.isSwapCameraProcessing() || CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA.equals(strPreviousShotMode))) {
                Size paramSize = lgParameters.getParameters().getPictureSize();
                this.mGet.setPreviousPictureSize(Util.size2String(paramSize.width, paramSize.height));
            }
            this.mGet.setPreviousShotModeString(CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA);
            ListPreference shotModePref = this.mGet.findPreference(Setting.KEY_CAMERA_SHOT_MODE);
            if (shotModePref != null) {
                this.pictureSizeString = shotModePref.getExtraInfo3();
                this.mGet.setSetting(Setting.KEY_CAMERA_PICTURESIZE, this.pictureSizeString, false);
                return;
            }
            return;
        }
        this.mGet.doCommand(Command.CAMERA_FLASH_MODE, lgParameters);
        this.mGet.doCommand(Command.CAMERA_FOCUS_MODE, lgParameters);
    }

    private void executeFreePanoramaShotMode(LGParameters lgParameters, boolean allSetting, boolean fromResetSetting, String strPreviousShotMode) {
        this.mGet.showFreePanoramaBlackBg();
        if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_HDR)) {
            Bundle useHDR = new Bundle();
            useHDR.putBoolean("useHDR", true);
            this.mGet.doCommand(Command.RESTORE_OPTIONAL_PARAMETERS, lgParameters, useHDR);
            this.mGet.restartPreview(lgParameters, false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE)) {
            removeTimeMachineShotImage();
            this.mGet.restartPreview(lgParameters, false);
            this.mGet.setTimeMachineLimit(false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
            this.mGet.stopPlanePanoramaEngine();
            setPictureSizeWithPreviousValue(lgParameters);
            this.mGet.restartPreview(lgParameters, false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS)) {
            if (FunctionProperties.isSupportBurstShot()) {
                lgParameters.getParameters().set("burst-shot", CameraConstants.SMART_MODE_OFF);
            }
            this.mGet.restartPreview(lgParameters, false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) {
            this.mGet.setSettingForDualCamera(true);
            setVideoSizeFullHD(lgParameters);
            this.mGet.updateDualRecordingSelection();
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY)) {
            if (!allSetting) {
                this.mGet.setPreviewRendered(false);
                setImageSizeAndRestartPreviewForBeautyShot(lgParameters, false);
            }
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_REFOCUS)) {
            resetRefocusShotModeSetting(lgParameters, fromResetSetting);
        } else {
            setImageSizeAndRestartPreview(lgParameters);
        }
        this.mGet.setPreviousShotModeString(strPreviousShotMode);
        this.mGet.setSetting(Setting.KEY_VOICESHUTTER, CameraConstants.SMART_MODE_OFF);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_TIMER, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SHUTTER_SOUND, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, true);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_FLASH, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_ZOOM, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_FOCUS, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_PICTURESIZE, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_TIME_MACHINE, false);
        this.mGet.setCurrentSettingMenuEnable(Setting.KEY_SCENE_MODE, Setting.HELP_NIGHT, true);
        lgParameters.getParameters().setZoom(0);
        this.mGet.resetZoomController();
        this.mGet.doCommand(Command.FREE_PANORAMA_SHOT, lgParameters);
        checkFocusMode(lgParameters.getParameters());
        ListPreference shotModePref = this.mGet.getPreferenceGroup().findPreference(Setting.KEY_CAMERA_SHOT_MODE);
        if (shotModePref != null) {
            this.pictureSizeString = shotModePref.getExtraInfo3();
        }
        this.isNormalShot = false;
        if (allSetting) {
            this.mGet.setPreviousShotModeString(CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA);
            return;
        }
        this.mGet.doCommand(Command.CAMERA_FLASH_MODE, lgParameters);
        this.mGet.doCommand(Command.CAMERA_FOCUS_MODE, lgParameters);
    }

    private void executeHdrShotMode(LGParameters lgParameters, boolean allSetting, boolean fromResetSetting, String strPreviousShotMode) {
        if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_PANORAMA) || strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_CONTINUOUS)) {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_TIMER, true);
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SHUTTER_SOUND, true);
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, true);
            int pictureSizeIndex = this.mGet.getSettingIndex(Setting.KEY_CAMERA_PICTURESIZE);
            ListPreference pictureSizePref = this.mGet.findPreference(Setting.KEY_CAMERA_PICTURESIZE);
            if (pictureSizePref != null) {
                this.pictureSizeString = pictureSizePref.getEntries()[pictureSizeIndex].toString();
            }
            this.isNormalShot = true;
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_TIMER, true);
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SHUTTER_SOUND, true);
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, true);
            this.isNormalShot = true;
            this.mGet.stopPlanePanoramaEngine();
            setPictureSizeWithPreviousValue(lgParameters);
            this.mGet.restartPreview(lgParameters, false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_TIMER, true);
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SHUTTER_SOUND, true);
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, true);
            this.isNormalShot = true;
            this.mGet.removeFreePanoramaView();
            this.mGet.stopFreePanoramaEngine(lgParameters.getParameters());
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE)) {
            removeTimeMachineShotImage();
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, true);
            this.mGet.setTimeMachineLimit(false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS)) {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_TIMER, true);
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SHUTTER_SOUND, true);
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, true);
            if (FunctionProperties.isSupportBurstShot()) {
                lgParameters.getParameters().set("burst-shot", CameraConstants.SMART_MODE_OFF);
            }
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) {
            this.mGet.setSettingForDualCamera(true);
            this.isNormalShot = true;
            setVideoSizeFullHD(lgParameters);
            this.mGet.updateDualRecordingSelection();
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY)) {
            if (!allSetting) {
                this.mGet.setPreviewRendered(false);
                setImageSizeAndRestartPreviewForBeautyShot(lgParameters, false);
            }
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_REFOCUS)) {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SHUTTER_SOUND, true);
            resetRefocusShotModeSetting(lgParameters, fromResetSetting);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT)) {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SHUTTER_SOUND, true);
        }
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_TIME_MACHINE, false);
        if (TelephonyUtil.phoneInCall(this.mGet.getApplicationContext())) {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, false);
        } else {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, true);
        }
        if (!(strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA) || strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY))) {
            this.mGet.restartPreview(lgParameters, false);
        }
        lgParameters.getParameters().set(CameraConstants.HDR_MODE, "1");
        this.mGet.doCommand(Command.HDR_SHOT, lgParameters);
    }

    private void executeFullFrameContinuousShotMode(LGParameters lgParameters, boolean allSetting, boolean fromResetSetting, String strPreviousShotMode) {
        if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_HDR)) {
            Bundle useHDR = new Bundle();
            useHDR.putBoolean("useHDR", true);
            this.mGet.doCommand(Command.RESTORE_OPTIONAL_PARAMETERS, lgParameters, useHDR);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
            this.mGet.stopPlanePanoramaEngine();
            setPictureSizeWithPreviousValue(lgParameters);
            this.mGet.restartPreview(lgParameters, false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
            this.mGet.removeFreePanoramaView();
            this.mGet.stopFreePanoramaEngine(lgParameters.getParameters());
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE)) {
            removeTimeMachineShotImage();
            this.mGet.setTimeMachineLimit(false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) {
            this.mGet.setSettingForDualCamera(true);
            setVideoSizeFullHD(lgParameters);
            this.mGet.updateDualRecordingSelection();
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_REFOCUS)) {
            resetRefocusShotModeSetting(lgParameters, fromResetSetting);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY) && !allSetting) {
            this.mGet.setPreviewRendered(false);
            setImageSizeAndRestartPreviewForBeautyShot(lgParameters, false);
        }
        if (!(strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA) || strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY))) {
            this.mGet.restartPreview(lgParameters, false);
        }
        this.mGet.setPreviousShotModeString(strPreviousShotMode);
        this.mGet.setSetting(Setting.KEY_VOICESHUTTER, CameraConstants.SMART_MODE_OFF);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_TIMER, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SHUTTER_SOUND, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, true);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_TIME_MACHINE, false);
        this.mGet.setCurrentSettingMenuEnable(Setting.KEY_SCENE_MODE, Setting.HELP_NIGHT, false);
        if (FunctionProperties.isSupportBurstShot()) {
            lgParameters.getParameters().set("burst-shot", CameraConstants.SMART_MODE_ON);
        } else {
            this.mGet.setFullFrameContinuousShot(lgParameters.getParameters(), 6);
        }
        this.mGet.doCommand(Command.FULL_FRAME_CONTINUOUS_SHOT, lgParameters);
        checkFocusMode(lgParameters.getParameters());
        int pictureSizeIndex = this.mGet.getSettingIndex(Setting.KEY_CAMERA_PICTURESIZE);
        ListPreference pictureSizePref = this.mGet.findPreference(Setting.KEY_CAMERA_PICTURESIZE);
        if (pictureSizePref != null) {
            this.pictureSizeString = pictureSizePref.getEntries()[pictureSizeIndex].toString();
        }
        this.isNormalShot = true;
        if (allSetting) {
            this.mGet.setPreviousShotModeString(CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS);
        }
    }

    private void executeTimemachineShotMode(LGParameters lgParameters, boolean allSetting, boolean fromResetSetting, String strPreviousShotMode) {
        if (CameraConstants.TYPE_SHOTMODE_HDR.equals(strPreviousShotMode)) {
            Bundle useHDR = new Bundle();
            useHDR.putBoolean("useHDR", true);
            this.mGet.doCommand(Command.RESTORE_OPTIONAL_PARAMETERS, lgParameters, useHDR);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
            this.mGet.stopPlanePanoramaEngine();
            setPictureSizeWithPreviousValue(lgParameters);
            this.mGet.restartPreview(lgParameters, false);
        } else if (CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA.equals(strPreviousShotMode)) {
            this.mGet.removeFreePanoramaView();
            this.mGet.stopFreePanoramaEngine(lgParameters.getParameters());
        } else if (CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS.equals(strPreviousShotMode)) {
            if (FunctionProperties.isSupportBurstShot()) {
                lgParameters.getParameters().set("burst-shot", CameraConstants.SMART_MODE_OFF);
            }
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) {
            this.mGet.setSettingForDualCamera(true);
            setVideoSizeFullHD(lgParameters);
            this.mGet.updateDualRecordingSelection();
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_REFOCUS)) {
            resetRefocusShotModeSetting(lgParameters, fromResetSetting);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY) && !allSetting) {
            this.mGet.setPreviewRendered(false);
            setImageSizeAndRestartPreviewForBeautyShot(lgParameters, false);
        }
        if (!(strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA) || strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY))) {
            this.mGet.restartPreview(lgParameters, false);
        }
        this.mGet.setPreviousShotModeString(strPreviousShotMode);
        this.mGet.setTimeMachineLimit(true);
        this.mGet.doCommand(Command.TIME_MACHINE_SHOT, lgParameters);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_TIMER, true);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SHUTTER_SOUND, true);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, true);
        this.mGet.setPreviousShotModeString(strPreviousShotMode);
        if (TelephonyUtil.phoneInCall(this.mGet.getApplicationContext())) {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, false);
        } else {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, true);
        }
        this.mGet.setCurrentSettingMenuEnable(Setting.KEY_SCENE_MODE, Setting.HELP_NIGHT, false);
        int pictureSizeIndex = this.mGet.getSettingIndex(Setting.KEY_CAMERA_PICTURESIZE);
        ListPreference pictureSizePref = this.mGet.findPreference(Setting.KEY_CAMERA_PICTURESIZE);
        if (pictureSizePref != null) {
            this.pictureSizeString = pictureSizePref.getEntries()[pictureSizeIndex].toString();
        }
        this.isNormalShot = true;
        this.mGet.setTimeMachineShot(lgParameters.getParameters(), 5);
        if (allSetting) {
            this.mGet.setPreviousShotModeString(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE);
            return;
        }
        this.mGet.doCommand(Command.CAMERA_FLASH_MODE, lgParameters);
        this.mGet.doCommand(Command.CAMERA_FOCUS_MODE, lgParameters);
    }

    private void executeMainBeautyShotMode(LGParameters lgParameters, boolean allSetting, boolean fromResetSetting, String strPreviousShotMode) {
        if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_HDR)) {
            Bundle useHDR = new Bundle();
            useHDR.putBoolean("useHDR", true);
            this.mGet.doCommand(Command.RESTORE_OPTIONAL_PARAMETERS, lgParameters, useHDR);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE)) {
            removeTimeMachineShotImage();
            this.mGet.setTimeMachineLimit(false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
            this.mGet.stopPlanePanoramaEngine();
            setPictureSizeWithPreviousValue(lgParameters);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
            this.mGet.removeFreePanoramaView();
            this.mGet.stopFreePanoramaEngine(lgParameters.getParameters());
        } else if (CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS.equals(strPreviousShotMode)) {
            if (FunctionProperties.isSupportBurstShot()) {
                lgParameters.getParameters().set("burst-shot", CameraConstants.SMART_MODE_OFF);
            }
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) {
            if (!this.mGet.isPreviewRendered()) {
                this.mGet.setPreviewRendered(true);
            }
            this.mGet.setSettingForDualCamera(true);
            setVideoSizeFullHD(lgParameters);
            this.mGet.updateDualRecordingSelection();
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_REFOCUS)) {
            resetRefocusShotModeSetting(lgParameters, fromResetSetting);
        }
        if (!(strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA) || this.mGet.isPreviewRendered())) {
            this.mGet.setPreviewRendered(true);
            setImageSizeAndRestartPreviewForBeautyShot(lgParameters, true);
            this.mGet.removePreviewCallback();
        }
        this.mGet.setPreviousShotModeString(strPreviousShotMode);
        if (TelephonyUtil.phoneInCall(this.mGet.getApplicationContext())) {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, false);
        } else {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, true);
        }
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_TIMER, true);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SHUTTER_SOUND, true);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, true);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_TIME_MACHINE, false);
        this.mGet.setCurrentSettingMenuEnable(Setting.KEY_SCENE_MODE, Setting.HELP_NIGHT, true);
        this.mGet.doCommand(Command.MAIN_BEAUTY_SHOT, lgParameters);
        checkFocusMode(lgParameters.getParameters());
        int pictureSizeIndex = this.mGet.getSettingIndex(Setting.KEY_CAMERA_PICTURESIZE);
        ListPreference pictureSizePref = this.mGet.findPreference(Setting.KEY_CAMERA_PICTURESIZE);
        if (pictureSizePref != null) {
            this.pictureSizeString = pictureSizePref.getEntries()[pictureSizeIndex].toString();
        }
        this.isNormalShot = true;
        if (allSetting) {
            this.mGet.setPreviousShotModeString(CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY);
        }
        CamLog.e(FaceDetector.TAG, " mGet.getSubMenuMode() : " + this.mGet.getSubMenuMode());
        if (this.mGet.getSubMenuMode() != 5 && this.mGet.getSubMenuMode() != 16 && this.mGet.getSubMenuMode() != 27) {
            CamLog.e(FaceDetector.TAG, "shot beautyshot controll true!");
            this.mGet.showBeautyshotController(true);
        }
    }

    private void executeFrontBeautyShotMode(LGParameters lgParameters, boolean allSetting, boolean fromResetSetting, String strPreviousShotMode) {
        if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) {
            if (!this.mGet.isPreviewRendered()) {
                this.mGet.setPreviewRendered(true);
            }
            this.mGet.setSettingForDualCamera(true);
            setVideoSizeFullHD(lgParameters);
            this.mGet.updateDualRecordingSelection();
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
            this.mGet.doCommand(Command.NORMAL_SHOT, lgParameters);
        } else if (!this.mGet.isPreviewRendered()) {
            this.mGet.setPreviewRendered(true);
            setImageSizeAndRestartPreviewForBeautyShot(lgParameters, true);
            this.mGet.removePreviewCallback();
        }
        this.mGet.doCommand(Command.SET_BEAUTYSHOT, lgParameters);
        int pictureSizeIndex = this.mGet.getSettingIndex(Setting.KEY_CAMERA_PICTURESIZE);
        ListPreference pictureSizePref = this.mGet.findPreference(Setting.KEY_CAMERA_PICTURESIZE);
        if (pictureSizePref != null) {
            this.pictureSizeString = pictureSizePref.getEntries()[pictureSizeIndex].toString();
        }
        if (allSetting) {
            this.mGet.setPreviousShotModeString(CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY);
        }
        if (this.mGet.getSubMenuMode() != 5 && this.mGet.getSubMenuMode() != 16 && this.mGet.getSubMenuMode() != 27) {
            this.mGet.showBeautyshotController(true);
        }
    }

    private void executeClearShotMode(LGParameters lgParameters, boolean allSetting, boolean fromResetSetting, String strPreviousShotMode) {
        if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_HDR)) {
            Bundle useHDR = new Bundle();
            useHDR.putBoolean("useHDR", true);
            this.mGet.doCommand(Command.RESTORE_OPTIONAL_PARAMETERS, lgParameters, useHDR);
            this.mGet.restartPreview(lgParameters, false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE)) {
            removeTimeMachineShotImage();
            this.mGet.restartPreview(lgParameters, false);
            this.mGet.setTimeMachineLimit(false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
            this.mGet.stopPlanePanoramaEngine();
            setPictureSizeWithPreviousValue(lgParameters);
            this.mGet.restartPreview(lgParameters, false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
            this.mGet.removeFreePanoramaView();
            this.mGet.stopFreePanoramaEngine(lgParameters.getParameters());
            setImageSizeAndRestartPreview(lgParameters);
            this.mGet.removePreviewCallback();
        } else if (CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS.equals(strPreviousShotMode)) {
            if (FunctionProperties.isSupportBurstShot()) {
                lgParameters.getParameters().set("burst-shot", CameraConstants.SMART_MODE_OFF);
            }
            this.mGet.restartPreview(lgParameters, false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) {
            this.mGet.setSettingForDualCamera(true);
            setVideoSizeFullHD(lgParameters);
            this.mGet.updateDualRecordingSelection();
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY)) {
            if (!allSetting) {
                this.mGet.setPreviewRendered(false);
                setImageSizeAndRestartPreviewForBeautyShot(lgParameters, false);
            }
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_REFOCUS)) {
            resetRefocusShotModeSetting(lgParameters, fromResetSetting);
        } else {
            setImageSizeAndRestartPreview(lgParameters);
            this.mGet.removePreviewCallback();
        }
        this.mGet.setPreviousShotModeString(strPreviousShotMode);
        if (TelephonyUtil.phoneInCall(this.mGet.getApplicationContext())) {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, false);
        } else {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, true);
        }
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_TIMER, true);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SHUTTER_SOUND, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_FLASH, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_TIME_MACHINE, false);
        this.mGet.setCurrentSettingMenuEnable(Setting.KEY_SCENE_MODE, Setting.HELP_NIGHT, false);
        this.mGet.doCommand(Command.CLEAR_SHOT, lgParameters);
        checkFocusMode(lgParameters.getParameters());
        int pictureSizeIndex = this.mGet.getSettingIndex(Setting.KEY_CAMERA_PICTURESIZE);
        ListPreference pictureSizePref = this.mGet.findPreference(Setting.KEY_CAMERA_PICTURESIZE);
        if (pictureSizePref != null) {
            this.pictureSizeString = pictureSizePref.getEntries()[pictureSizeIndex].toString();
        }
        this.isNormalShot = true;
        if (allSetting) {
            this.mGet.setPreviousShotModeString(CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT);
        }
    }

    private void executeDualCameraShotMode(LGParameters lgParameters, boolean allSetting, boolean fromResetSetting, String strPreviousShotMode) {
        int pipMaskIndex = 1;
        if (allSetting && !this.isSettingSelected) {
            if (this.mGet.getCameraMode() == 0) {
                pipMaskIndex = SharedPreferenceUtil.getDualCameraPIPIndex(this.mGet.getApplicationContext());
                CamLog.d(FaceDetector.TAG, "restore - DualCamera Primary getIndex =  " + pipMaskIndex);
            } else if (this.mGet.getCameraMode() == 1) {
                pipMaskIndex = SharedPreferenceUtil.getFrontDualCameraPIPIndex(this.mGet.getApplicationContext());
                CamLog.d(FaceDetector.TAG, "restore - FrontDualCamera Primary getIndex =  " + pipMaskIndex);
            }
            if (pipMaskIndex == -1) {
                pipMaskIndex = 1;
            }
        } else if (!allSetting) {
            this.isSettingSelected = true;
        }
        this.mGet.setCurrentPIPMask(pipMaskIndex);
        if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_HDR)) {
            Bundle useHDR = new Bundle();
            useHDR.putBoolean("useHDR", true);
            this.mGet.doCommand(Command.RESTORE_OPTIONAL_PARAMETERS, lgParameters, useHDR);
            this.mGet.restartPreview(lgParameters, false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
            this.mGet.stopPlanePanoramaEngine();
            if (this.mGet.getCameraMode() == 0) {
                setPictureSizeWithPreviousValue(lgParameters);
                setImageSizeAndRestartPreview(lgParameters);
            }
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE)) {
            removeTimeMachineShotImage();
            this.mGet.restartPreview(lgParameters, false);
            this.mGet.setTimeMachineLimit(false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS)) {
            if (FunctionProperties.isSupportBurstShot()) {
                lgParameters.getParameters().set("burst-shot", CameraConstants.SMART_MODE_OFF);
            }
            this.mGet.restartPreview(lgParameters, false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY)) {
            if (!allSetting) {
                this.mGet.setPreviewRendered(false);
                setImageSizeAndRestartPreviewForBeautyShot(lgParameters, false);
            }
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY)) {
            if (!allSetting) {
                this.mGet.setPreviewRendered(false);
                setImageSizeAndRestartPreviewForBeautyShot(lgParameters, false);
            }
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
            this.mGet.removeFreePanoramaView();
            setImageSizeAndRestartPreview(lgParameters);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_REFOCUS)) {
            resetRefocusShotModeSetting(lgParameters, fromResetSetting);
        } else {
            setImageSizeAndRestartPreview(lgParameters);
        }
        this.mGet.setPreviousShotModeString(strPreviousShotMode);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, true);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SHUTTER_SOUND, true);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_TIMER, true);
        if (!Common.useSecureLockImage()) {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_TAG_LOCATION, true);
        }
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_TIME_MACHINE, false);
        this.mGet.setSettingForDualCamera(false);
        lgParameters.getParameters().setZoom(0);
        this.mGet.resetZoomController();
        this.mGet.doCommand(Command.DUAL_CAMERA_SHOT, lgParameters);
        this.mGet.doCommandUi(Command.SHOW_PIP_FRAME_SUB_MENU);
        checkFocusMode(lgParameters.getParameters());
        ListPreference shotModePref = this.mGet.getPreferenceGroup().findPreference(Setting.KEY_CAMERA_SHOT_MODE);
        if (shotModePref != null) {
            this.pictureSizeString = shotModePref.getExtraInfo3();
        }
        this.isNormalShot = false;
        this.mGet.setQuickFunctionAllMenuEnabled(false, true);
        this.mGet.setQuickButtonForcedDisable(true);
        this.mGet.setQuickButtonMenuEnable(false, false);
        if (this.isSettingSelected && !allSetting) {
            this.isSettingSelected = false;
        }
    }

    private void checkPreviousShotModeForRefocus(LGParameters lgParameters, boolean allSetting, boolean fromResetSetting, String strPreviousShotMode) {
        if (CameraConstants.TYPE_SHOTMODE_HDR.equals(strPreviousShotMode)) {
            Bundle useHDR = new Bundle();
            useHDR.putBoolean("useHDR", true);
            this.mGet.doCommand(Command.RESTORE_OPTIONAL_PARAMETERS, lgParameters, useHDR);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA)) {
            this.mGet.stopPlanePanoramaEngine();
            setPictureSizeWithPreviousValue(lgParameters);
            this.mGet.restartPreview(lgParameters, false);
        } else if (CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA.equals(strPreviousShotMode)) {
            this.mGet.removeFreePanoramaView();
            this.mGet.stopFreePanoramaEngine(lgParameters.getParameters());
        } else if (CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS.equals(strPreviousShotMode)) {
            if (FunctionProperties.isSupportBurstShot()) {
                lgParameters.getParameters().set("burst-shot", CameraConstants.SMART_MODE_OFF);
            }
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE)) {
            removeTimeMachineShotImage();
            this.mGet.restartPreview(lgParameters, false);
            this.mGet.setTimeMachineLimit(false);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) {
            this.mGet.setSettingForDualCamera(true);
            setVideoSizeFullHD(lgParameters);
            this.mGet.updateDualRecordingSelection();
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
        } else if (strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY) && !allSetting) {
            this.mGet.setPreviewRendered(false);
            setImageSizeAndRestartPreviewForBeautyShot(lgParameters, false);
        }
        if (!strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA) && !strPreviousShotMode.equals(CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY)) {
            this.mGet.restartPreview(lgParameters, false);
        }
    }

    private void executeRefocusMode(LGParameters lgParameters, boolean allSetting, boolean fromResetSetting, String strPreviousShotMode) {
        checkPreviousShotModeForRefocus(lgParameters, allSetting, fromResetSetting, strPreviousShotMode);
        lgParameters.getParameters().setZoom(0);
        this.mGet.resetZoomController();
        checkFocusMode(lgParameters.getParameters());
        this.mGet.setPreviousShotModeString(strPreviousShotMode);
        this.mGet.doCommand(Command.REFOCUS_SHOT, lgParameters);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_TIMER, true);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_SHUTTER_SOUND, false);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_AUTO_REVIEW, true);
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_FLASH, false);
        if (TelephonyUtil.phoneInCall(this.mGet.getApplicationContext())) {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, false);
        } else {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VOICESHUTTER, true);
        }
        this.mGet.setCurrentSettingMenuEnable(Setting.KEY_SCENE_MODE, Setting.HELP_NIGHT, false);
        int pictureSizeIndex = this.mGet.getSettingIndex(Setting.KEY_CAMERA_PICTURESIZE);
        ListPreference pictureSizePref = this.mGet.findPreference(Setting.KEY_CAMERA_PICTURESIZE);
        if (pictureSizePref != null) {
            this.pictureSizeString = pictureSizePref.getEntries()[pictureSizeIndex].toString();
        }
        this.isNormalShot = true;
        this.mGet.setRefocusShotPreviewGuideVisibility(true);
        if (allSetting) {
            this.mGet.setPreviousShotModeString(CameraConstants.TYPE_SHOTMODE_REFOCUS);
            lgParameters.getParameters().set(CameraConstants.REFOCUS_PARAM, CameraConstants.REFOCUS_PARAM_VALUE_ON);
        }
    }

    private void removeRefocusShotImage() {
        if (FunctionProperties.isRefocusShotSupported()) {
            boolean z = false;
            try {
                z = this.mGet.deleteRefocusShotImages();
                this.mGet.setRefocusShotHasPictures(false);
                BubblePopupController.get().initializeNotiComplete();
            } catch (Exception e) {
                CamLog.w(FaceDetector.TAG, "Exception:", e);
            } finally {
                CamLog.i(FaceDetector.TAG, "refocusShotTempFileDeleted ? = " + z);
            }
        }
    }

    protected void onExecuteAlone() {
        CamLog.d(FaceDetector.TAG, String.format("SetCameraShotMode onExecuteAlone()", new Object[0]));
        try {
            if (!this.mGet.getInCaptureProgress()) {
                String shotMode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
                if (this.mGet.isTimeMachineModeOn() || CameraConstants.TYPE_SHOTMODE_TIMEMACHINE.equals(shotMode)) {
                    showHelpGuideDialog(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE);
                    this.mGet.setPreviousShotModeString(CameraConstants.TYPE_SHOTMODE_TIMEMACHINE);
                } else {
                    showHelpGuideDialog(shotMode);
                    this.mGet.setPreviousShotModeString(shotMode);
                }
                if (this.mGet.isTimeMachineModeOn() || !CameraConstants.TYPE_SHOTMODE_PANORAMA.equals(shotMode)) {
                    this.mGet.removePanoramaView();
                }
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        SetCameraShotMode.this.mGet.removePostRunnable(this);
                        SetCameraShotMode.this.mGet.setQuickFunctionControllerAllMenuIcons();
                        SetCameraShotMode.this.mGet.checkStorage(false);
                        SetCameraShotMode.this.mGet.updateSizeIndicator();
                    }
                });
            }
        } catch (NullPointerException e) {
            CamLog.w(FaceDetector.TAG, "NullPointerException:", e);
        }
    }

    private boolean showHelpGuideDialog(String shotMode) {
        boolean retVal = true;
        if (this.mGet.isRotateDialogVisible()) {
            this.mGet.onDismissRotateDialog();
        }
        if (this.isFromGridView) {
            if (CameraConstants.TYPE_SHOTMODE_CONTINUOUS.equals(shotMode)) {
                retVal = this.mGet.showHelpGuidePopup(Setting.HELP_CONTINUOUS_SHOT, DialogCreater.DIALOG_ID_HELP_CONTINUOUS_SHOT, true);
            } else if (CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS.equals(shotMode)) {
                retVal = FunctionProperties.isSupportBurstShot() ? this.mGet.showHelpGuidePopup(Setting.HELP_BURST_SHOT, DialogCreater.DIALOG_ID_HELP_BURST_SHOT, true) : this.mGet.showHelpGuidePopup(Setting.HELP_CONTINUOUS_SHOT, DialogCreater.DIALOG_ID_HELP_CONTINUOUS_SHOT, true);
            } else if (CameraConstants.TYPE_SHOTMODE_HDR.equals(shotMode)) {
                retVal = this.mGet.showHelpGuidePopup(Setting.HELP_HDR, DialogCreater.DIALOG_ID_HELP_HDR, true);
            } else if (CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_FRONT_BEAUTY.equals(shotMode)) {
                retVal = this.mGet.showHelpGuidePopup(Setting.HELP_BEAUTY_SHOT, DialogCreater.DIALOG_ID_HELP_BEAUTY_SHOT, true);
            }
        }
        if (CameraConstants.TYPE_SHOTMODE_PANORAMA.equals(shotMode)) {
            return this.mGet.showHelpGuidePopup(Setting.HELP_PANORAMA, DialogCreater.DIALOG_ID_HELP_PANORAMA, true);
        }
        if (CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA.equals(shotMode)) {
            return this.mGet.showHelpGuidePopup(Setting.HELP_PLANE_PANORAMA, DialogCreater.DIALOG_ID_HELP_PLANE_PANORAMA, true);
        }
        if (CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA.equals(shotMode)) {
            return this.mGet.showHelpGuidePopup(Setting.HELP_FREE_PANORAMA, DialogCreater.DIALOG_ID_HELP_FREE_PANORAMA, true);
        }
        if (CameraConstants.TYPE_SHOTMODE_TIMEMACHINE.equals(shotMode)) {
            return this.mGet.showHelpGuidePopup(Setting.HELP_TIMEMACHINE, DialogCreater.DIALOG_ID_HELP_TIMEMACHINE, true);
        }
        if (CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT.equals(shotMode)) {
            return this.mGet.showHelpGuidePopup(Setting.HELP_CLEAR_SHOT, DialogCreater.DIALOG_ID_HELP_CLEAR_SHOT, true);
        }
        if (CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(shotMode)) {
            return this.mGet.showHelpGuidePopup(Setting.HELP_DUAL_CAMERA, DialogCreater.DIALOG_ID_HELP_DUAL_CAMERA, true);
        }
        if (CameraConstants.TYPE_SHOTMODE_REFOCUS.equals(shotMode)) {
            return this.mGet.showHelpGuidePopup(Setting.HELP_REFOCUS, DialogCreater.DIALOG_ID_HELP_REFOCUS, true);
        }
        return retVal;
    }

    private String isTimeMachineShotModeOn(String shotMode) {
        if (!FunctionProperties.isTimeMachinShotSupported()) {
            return shotMode;
        }
        if (this.mGet.isAttachMode()) {
            this.mGet.setSetting(Setting.KEY_TIME_MACHINE, CameraConstants.SMART_MODE_OFF);
            shotMode = CameraConstants.TYPE_SHOTMODE_NORMAL;
        }
        if (CameraConstants.TYPE_SHOTMODE_TIMEMACHINE.equals(shotMode)) {
            return CameraConstants.TYPE_SHOTMODE_TIMEMACHINE;
        }
        if (this.mGet.isTimeMachineModeOn() && (CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotMode) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(shotMode))) {
            CamLog.d(FaceDetector.TAG, "SetCameraShotMode-start: Time machine On:" + shotMode);
            return CameraConstants.TYPE_SHOTMODE_TIMEMACHINE;
        }
        this.mGet.setSetting(Setting.KEY_TIME_MACHINE, CameraConstants.SMART_MODE_OFF);
        return shotMode;
    }

    private void removeTimeMachineShotImage() {
        if (FunctionProperties.isTimeMachinShotSupported()) {
            boolean z = false;
            try {
                z = this.mGet.deleteTimeMachineImages();
                this.mGet.setTimemachineHasPictures(false);
                BubblePopupController.get().initializeNotiComplete();
            } catch (Exception e) {
                CamLog.w(FaceDetector.TAG, "Exception:", e);
            } finally {
                CamLog.i(FaceDetector.TAG, "timeMachineTempFileDeleted ? = " + z);
            }
        }
    }

    private void setPictureSizeWithPreviousValue(LGParameters lgParameters) {
        String size = this.mGet.getPreviousPictureSize();
        if (size != null) {
            this.pictureSizeString = size;
            this.mGet.setSetting(Setting.KEY_CAMERA_PICTURESIZE, this.pictureSizeString, false);
        }
    }

    private void setPictureSize(Parameters parameters) {
        Size newPictureSize = parameters.getPictureSize();
        if (newPictureSize != null && !this.mGet.isEffectsCamcorderActive() && !this.mGet.isEffectsCameraActive()) {
            ListPreference listPref = this.mGet.findPreference(Setting.KEY_CAMERA_PICTURESIZE);
            if (listPref != null) {
                CharSequence[] imageSizeArray = listPref.getEntryValues();
                for (Object valueOf : imageSizeArray) {
                    int[] size = Util.SizeString2WidthHeight(String.valueOf(valueOf));
                    if (size != null && newPictureSize.width == size[0] && newPictureSize.height == size[1]) {
                        parameters.setPictureSize(newPictureSize.width, newPictureSize.height);
                        CamLog.i(FaceDetector.TAG, "setPictureSize " + newPictureSize.width + "x" + newPictureSize.height);
                        return;
                    }
                }
            }
        }
    }

    private void setPictureSizeMenuEnable() {
        String shotMode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
        if (this.mGet.getCameraMode() == 1 && !CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA.equals(shotMode)) {
            this.isNormalShot = true;
        }
        if (this.isNormalShot) {
            if (this.mGet.getCurrentSettingMenu() != null) {
                this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_PICTURESIZE, true);
            }
            CamLog.d(FaceDetector.TAG, "releaseEngine call in SetCameraShotMode");
            if (this.mGet.getEngineProcessor().checkEngineTag(AutoPanorama.ENGINE_TAG)) {
                this.mGet.releaseEngine(AutoPanorama.ENGINE_TAG);
            }
        } else if (this.mGet.getCurrentSettingMenu() != null) {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_CAMERA_PICTURESIZE, false);
        }
    }

    private void checkFocusMode(Parameters parameters) {
        if (this.mGet.getCameraId() != 1) {
            String focusSetting = this.mGet.getSettingValue(Setting.KEY_FOCUS);
            if (!LGT_Limit.ISP_AUTOMODE_AUTO.equals(focusSetting) || !CameraConstants.FOCUS_SETTING_VALUE_MULTIWINDOWAF.equals(focusSetting)) {
                ListPreference pref = this.mGet.findPreference(Setting.KEY_FOCUS);
                if (pref != null) {
                    this.mGet.setSetting(Setting.KEY_FOCUS, pref.getDefaultValue());
                }
                this.mGet.setQuickFunctionControllerMmsLimit();
                if (!FunctionProperties.isFaceDetectionAuto()) {
                    this.mGet.stopFaceDetection();
                }
                parameters.set(CameraConstants.FOCUS_MODE_MANUAL, 0);
                if (this.mGet.isIndicatorControllerInitialized()) {
                    this.mGet.runOnUiThread(new Runnable() {
                        public void run() {
                            SetCameraShotMode.this.mGet.removePostRunnable(this);
                            SetCameraShotMode.this.mGet.setFocusRectangleInitialize();
                            SetCameraShotMode.this.mGet.showFocus();
                            SetCameraShotMode.this.mGet.updateFocusIndicator();
                            SetCameraShotMode.this.mGet.showManualFocusController(false);
                        }
                    });
                }
            } else if (CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(focusSetting)) {
                String shotMode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
                if (shotMode.equals(CameraConstants.TYPE_SHOTMODE_PANORAMA) || shotMode.equals(CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) || shotMode.equals(CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
                    this.mGet.hideFocus();
                }
            }
        }
    }

    private void setImageSizeAndRestartPreview(LGParameters lgParameters) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("doNotRestartPreview", true);
        this.mGet.doCommand(Command.CAMERA_IMAGE_SIZE, lgParameters, bundle);
        this.mGet.restartPreview(lgParameters, false);
    }

    private void setImageSizeAndRestartPreviewForBeautyShot(LGParameters lgParameters, boolean changeMode) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("doNotRestartPreview", true);
        bundle.putBoolean("doChangePrevieMode", changeMode);
        this.mGet.doCommand(Command.CAMERA_IMAGE_SIZE, lgParameters, bundle);
        this.mGet.setPreviewEffectForBeautyShotMode(lgParameters, changeMode);
    }

    private void setVideoSizeFullHD(LGParameters lgParameters) {
        String videoSize = null;
        if (lgParameters != null) {
            List<Size> sizeList = lgParameters.getParameters().getSupportedVideoSizes();
            if (((Size) sizeList.get(0)).width >= 1920) {
                for (Size size : sizeList) {
                    if (size.width == 1920) {
                        videoSize = size.width + "x" + size.height;
                    }
                }
            } else {
                videoSize = ((Size) sizeList.get(0)).width + "x" + ((Size) sizeList.get(0)).height;
            }
            CamLog.d(FaceDetector.TAG, "videoSize = " + videoSize);
            lgParameters.getParameters().set("video-size", videoSize);
        }
    }
}
