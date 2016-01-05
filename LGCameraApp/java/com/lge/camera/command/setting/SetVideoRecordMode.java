package com.lge.camera.command.setting;

import android.os.Bundle;
import android.os.SystemProperties;
import com.lge.camera.ControllerFunction;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.DialogCreater;
import com.lge.camera.util.SharedPreferenceUtil;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.morpho.app.morphopanorama.PanoramaApplication;
import com.lge.olaworks.library.FaceDetector;

public class SetVideoRecordMode extends SettingCommand {
    private boolean isFromGridView;
    private String mRequestedRecordMode;

    public SetVideoRecordMode(ControllerFunction function) {
        super(function);
        this.mRequestedRecordMode = null;
        this.isFromGridView = false;
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, new Bundle());
    }

    public void execute(LGParameters lgParameters, Object arg) {
        String recordMode;
        boolean argValue = false;
        this.mRequestedRecordMode = null;
        if (arg instanceof String) {
            recordMode = (String) arg;
            this.mRequestedRecordMode = recordMode;
        } else {
            argValue = ((Bundle) arg).getBoolean("allSetting", false);
            recordMode = this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE);
        }
        boolean allSetting = argValue;
        if (arg instanceof Bundle) {
            Bundle bundle = (Bundle) arg;
            if (!allSetting) {
                this.isFromGridView = bundle.getBoolean("fromGridView", false);
            }
        }
        String strPreviousRecordMode = this.mGet.getPreviousRecordModeString();
        CamLog.d(FaceDetector.TAG, "SetVideoRecordMode-start: CurrentRecordmode:" + recordMode + ", PreviousRecordmode:" + strPreviousRecordMode);
        if (CameraConstants.TYPE_RECORDMODE_NORMAL.equals(recordMode) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(recordMode)) {
            executeNormalRecordMode(lgParameters, strPreviousRecordMode, allSetting);
        } else if (CameraConstants.TYPE_RECORDMODE_WDR.equals(recordMode)) {
            executeWDRRecordMode(lgParameters, strPreviousRecordMode, allSetting);
        } else if (CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(recordMode)) {
            this.mGet.enableInput(false);
            executeLiveEffectRecordMode(lgParameters, strPreviousRecordMode, allSetting);
        } else if (CameraConstants.TYPE_RECORDMODE_DUAL.equals(recordMode)) {
            this.mGet.enableInput(false);
            executeDualRecordMode(lgParameters, strPreviousRecordMode, allSetting);
        } else if (CameraConstants.TYPE_RECORDMODE_SMART_ZOOM.equals(recordMode)) {
            this.mGet.enableInput(false);
            executeSmartZoomMode(lgParameters, strPreviousRecordMode, allSetting);
        }
        CamLog.d(FaceDetector.TAG, "SetVideoRecordMode-end");
    }

    public void executeNormalRecordMode(LGParameters lgParameters, String strPreviousRecordMode, boolean allSetting) {
        if (CameraConstants.TYPE_RECORDMODE_WDR.equals(strPreviousRecordMode)) {
            String sVideoFps = CameraConstants.SMART_MODE_OFF;
            ListPreference pref = this.mGet.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
            if (pref != null) {
                sVideoFps = pref.getExtraInfo3();
            }
            if (MultimediaProperties.isHighFramRateVideoSupported() && this.mGet.getCameraId() == 0 && !CameraConstants.SMART_MODE_OFF.equals(sVideoFps)) {
                String[] fpsRange = SystemProperties.get("hw.camcorder.fpsrange", "15000,30000").split(",");
                lgParameters.getParameters().set("video-hfr", sVideoFps);
                lgParameters.getParameters().set("preview-format", "nv12-venus");
                int iVideoFps = Integer.parseInt(sVideoFps) * PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME;
                lgParameters.getParameters().setPreviewFpsRange(Integer.parseInt(fpsRange[0]), iVideoFps);
                CamLog.d(FaceDetector.TAG, "HFR mode Min FPS is set to " + fpsRange[0] + " Max FPS is set to " + iVideoFps);
            }
            if (FunctionProperties.isHDRRecordingNameUsed()) {
                lgParameters.getParameters().set("video-hdr", CameraConstants.SMART_MODE_OFF);
            } else {
                lgParameters.getParameters().set("video-wdr", CameraConstants.SMART_MODE_OFF);
            }
        } else if (CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(strPreviousRecordMode)) {
            this.mGet.enableInput(false);
            this.mGet.setLiveEffect(CameraConstants.SMART_MODE_OFF);
            this.mGet.doCommand(Command.SET_LIVE_EFFECT, lgParameters);
        } else if (CameraConstants.TYPE_RECORDMODE_DUAL.equals(strPreviousRecordMode)) {
            this.mGet.enableInput(false);
            this.mGet.setSetting(Setting.KEY_DUAL_RECORDING, CameraConstants.SMART_MODE_OFF);
            this.mGet.doCommand(Command.SET_DUAL_RECORDING, lgParameters);
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
        } else if (CameraConstants.TYPE_RECORDMODE_SMART_ZOOM.equals(strPreviousRecordMode)) {
            this.mGet.enableInput(false);
            this.mGet.setSetting(Setting.KEY_SMART_ZOOM_RECORDING, CameraConstants.SMART_MODE_OFF);
            this.mGet.doCommand(Command.SET_SMART_ZOOM_RECORDING, lgParameters);
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
            this.mGet.hideSmartZoomFocusView();
        }
        this.mGet.doCommandUi(Command.HIDE_LIVEEFFECT_SUBMENU_DRAWER);
        lgParameters.getParameters().set(CameraConstants.PARAM_KEY_DUAL_RECORDER, 0);
        this.mGet.setPreferenceMenuEnable(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, true, false);
        this.mGet.setPreviousRecordModeString(CameraConstants.TYPE_RECORDMODE_NORMAL);
    }

    public void executeWDRRecordMode(LGParameters lgParameters, String strPreviousRecordMode, boolean allSetting) {
        if (CameraConstants.TYPE_RECORDMODE_DUAL.equals(strPreviousRecordMode)) {
            this.mGet.setSetting(Setting.KEY_DUAL_RECORDING, CameraConstants.SMART_MODE_OFF);
            this.mGet.doCommand(Command.SET_DUAL_RECORDING, lgParameters);
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
        } else if (CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(strPreviousRecordMode)) {
            this.mGet.setLiveEffect(CameraConstants.SMART_MODE_OFF);
            this.mGet.doCommand(Command.SET_LIVE_EFFECT, lgParameters);
        } else if (CameraConstants.TYPE_RECORDMODE_SMART_ZOOM.equals(strPreviousRecordMode)) {
            this.mGet.setSetting(Setting.KEY_SMART_ZOOM_RECORDING, CameraConstants.SMART_MODE_OFF);
            this.mGet.doCommand(Command.SET_SMART_ZOOM_RECORDING, lgParameters);
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
            this.mGet.hideSmartZoomFocusView();
        }
        this.mGet.doCommandUi(Command.HIDE_LIVEEFFECT_SUBMENU_DRAWER);
        this.mGet.doCommand(Command.SET_WDR_RECORDING, lgParameters);
        this.mGet.setPreviousRecordModeString(CameraConstants.TYPE_RECORDMODE_WDR);
    }

    public void executeLiveEffectRecordMode(LGParameters lgParameters, String strPreviousRecordMode, boolean allSetting) {
        if (!CameraConstants.TYPE_RECORDMODE_WDR.equals(strPreviousRecordMode)) {
            if (CameraConstants.TYPE_RECORDMODE_DUAL.equals(strPreviousRecordMode)) {
                this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
            } else if (CameraConstants.TYPE_RECORDMODE_SMART_ZOOM.equals(strPreviousRecordMode)) {
                this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
            }
        }
        if (!(CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(strPreviousRecordMode) && CameraConstants.SMART_MODE_OFF.equals(this.mGet.getLiveEffect()) && allSetting)) {
            int faceIndex = 1;
            if (allSetting) {
                if (this.mGet.getCameraMode() == 0) {
                    faceIndex = SharedPreferenceUtil.getLiveEffectFaceIndex(this.mGet.getApplicationContext());
                } else {
                    faceIndex = SharedPreferenceUtil.getFrontLiveEffectFaceIndex(this.mGet.getApplicationContext());
                }
                if (faceIndex == -1) {
                    faceIndex = 1;
                }
            }
            String value = (String) this.mGet.getLiveEffectList().get(faceIndex);
            int menuIndex = this.mGet.getQfIndex(Setting.KEY_VIDEO_RECORD_MODE);
            this.mGet.setLiveEffect(value);
            ListPreference pref = this.mGet.findPreference(Setting.KEY_VIDEO_RECORD_MODE);
            if (pref != null) {
                if (this.mGet.isQuickFunctionList(menuIndex)) {
                    this.mGet.setSelectedChild(this.mGet.getChildIndex(CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT));
                    this.mGet.setQuickFunctionControllerAllMenuIcons();
                } else {
                    this.mGet.setSelectedChild(this.mGet.getCurrentSettingMenuIndex(pref.getKey()), pref.findIndexOfValue(CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT));
                }
            }
            this.mGet.setPreviousRecordModeString(CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT);
            Bundle isOpen = new Bundle();
            isOpen.putBoolean("menu_open", true);
            this.mGet.doCommandUi(Command.SHOW_LIVEEFFECT_SUBMENU_DRAWER, isOpen);
            this.mGet.doCommandUi(Command.SET_LIVE_EFFECT, lgParameters);
        }
        this.mGet.setPreferenceMenuEnable(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, false, false);
    }

    public void executeDualRecordMode(LGParameters lgParameters, String strPreviousRecordMode, boolean allSetting) {
        if (CameraConstants.TYPE_RECORDMODE_WDR.equals(strPreviousRecordMode)) {
            if (FunctionProperties.isHDRRecordingNameUsed()) {
                lgParameters.getParameters().set("video-hdr", CameraConstants.SMART_MODE_OFF);
            } else {
                lgParameters.getParameters().set("video-wdr", CameraConstants.SMART_MODE_OFF);
            }
        } else if (CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(strPreviousRecordMode)) {
            this.mGet.setLiveEffect(CameraConstants.SMART_MODE_OFF);
        } else if (CameraConstants.TYPE_RECORDMODE_SMART_ZOOM.equals(strPreviousRecordMode)) {
            this.mGet.hideSmartZoomFocusView();
        }
        this.mGet.doCommandUi(Command.HIDE_LIVEEFFECT_SUBMENU_DRAWER);
        this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
        int pipMaskIndex = 1;
        if (allSetting) {
            if (this.mGet.getCameraMode() == 0) {
                pipMaskIndex = SharedPreferenceUtil.getDualCamcorderPIPIndex(this.mGet.getApplicationContext());
            } else {
                pipMaskIndex = SharedPreferenceUtil.getFrontDualCamcorderPIPIndex(this.mGet.getApplicationContext());
            }
            if (pipMaskIndex == -1) {
                pipMaskIndex = 1;
            }
        }
        this.mGet.setCurrentPIPMask(pipMaskIndex);
        this.mGet.setPreferenceMenuEnable(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, false, false);
        this.mGet.setSetting(Setting.KEY_DUAL_RECORDING, CameraConstants.SMART_MODE_ON);
        if (ProjectVariables.isSupportedAutoReview()) {
            this.mGet.setPreferenceMenuEnable(Setting.KEY_VIDEO_AUTO_REVIEW, false, false);
            String settingValue = Setting.KEY_VIDEO_AUTO_REVIEW;
            this.mGet.setSetting(settingValue, this.mGet.findPreference(settingValue).getDefaultValue());
        }
        this.mGet.doCommand(Command.SET_DUAL_RECORDING, lgParameters);
        this.mGet.doCommandUi(Command.SHOW_PIP_FRAME_SUB_MENU);
        this.mGet.setPreviousRecordModeString(CameraConstants.TYPE_RECORDMODE_DUAL);
        this.mGet.setQuickFunctionAllMenuEnabled(false, true);
        this.mGet.setQuickButtonForcedDisable(true);
        this.mGet.setQuickButtonMenuEnable(false, false);
    }

    public void executeSmartZoomMode(LGParameters lgParameters, String strPreviousRecordMode, boolean allSetting) {
        if (CameraConstants.TYPE_RECORDMODE_WDR.equals(strPreviousRecordMode)) {
            if (FunctionProperties.isHDRRecordingNameUsed()) {
                lgParameters.getParameters().set("video-hdr", CameraConstants.SMART_MODE_OFF);
            } else {
                lgParameters.getParameters().set("video-wdr", CameraConstants.SMART_MODE_OFF);
            }
        } else if (CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(strPreviousRecordMode)) {
            this.mGet.setLiveEffect(CameraConstants.SMART_MODE_OFF);
        } else if (CameraConstants.TYPE_RECORDMODE_DUAL.equals(strPreviousRecordMode)) {
        }
        this.mGet.doCommandUi(Command.HIDE_LIVEEFFECT_SUBMENU_DRAWER);
        this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
        this.mGet.setPreferenceMenuEnable(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, false, false);
        int pipMaskIndex = 1;
        if (allSetting) {
            if (this.mGet.getCameraMode() == 0) {
                pipMaskIndex = SharedPreferenceUtil.getSmartZoomPIPIndex(this.mGet.getApplicationContext());
            } else {
                pipMaskIndex = SharedPreferenceUtil.getFrontSmartZoomPIPIndex(this.mGet.getApplicationContext());
            }
            if (pipMaskIndex == -1) {
                pipMaskIndex = 1;
            }
        }
        this.mGet.setCurrentPIPMask(pipMaskIndex);
        if (ProjectVariables.isSupportedAutoReview()) {
            this.mGet.setPreferenceMenuEnable(Setting.KEY_VIDEO_AUTO_REVIEW, false, false);
            String settingValue = Setting.KEY_VIDEO_AUTO_REVIEW;
            this.mGet.setSetting(settingValue, this.mGet.findPreference(settingValue).getDefaultValue());
        }
        this.mGet.setSetting(Setting.KEY_SMART_ZOOM_RECORDING, CameraConstants.SMART_MODE_ON);
        this.mGet.doCommand(Command.SET_SMART_ZOOM_RECORDING, lgParameters);
        this.mGet.doCommandUi(Command.SHOW_PIP_FRAME_SUB_MENU);
        this.mGet.setPreviousRecordModeString(CameraConstants.TYPE_RECORDMODE_SMART_ZOOM);
        this.mGet.setQuickFunctionAllMenuEnabled(false, true);
        this.mGet.setQuickButtonForcedDisable(true);
        this.mGet.setQuickButtonMenuEnable(false, false);
    }

    protected void onExecuteAlone() {
        String recordingMode;
        CamLog.d(FaceDetector.TAG, String.format("SetVideoRecordMode onExecuteAlone()", new Object[0]));
        if (this.mRequestedRecordMode == null) {
            recordingMode = this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE);
        } else {
            recordingMode = this.mRequestedRecordMode;
        }
        showHelpPopup(recordingMode);
    }

    private boolean showHelpPopup(String recordingMode) {
        boolean retVal = true;
        if (this.mGet.isRotateDialogVisible()) {
            this.mGet.onDismissRotateDialog();
        }
        if (this.isFromGridView && CameraConstants.TYPE_RECORDMODE_WDR.equals(recordingMode)) {
            retVal = FunctionProperties.isHDRRecordingNameUsed() ? this.mGet.showHelpGuidePopup(Setting.HELP_HDR_MOVIE, DialogCreater.DIALOG_ID_HELP_HDR_MOVIE, true) : this.mGet.showHelpGuidePopup(Setting.HELP_WDR_MOVIE, DialogCreater.DIALOG_ID_HELP_WDR_MOVIE, true);
        }
        if (CameraConstants.TYPE_RECORDMODE_DUAL.equals(recordingMode)) {
            return this.mGet.showHelpGuidePopup(Setting.HELP_DUAL_RECORDING, DialogCreater.DIALOG_ID_HELP_DUAL_RECORDING, true);
        }
        if (CameraConstants.TYPE_RECORDMODE_SMART_ZOOM.equals(recordingMode)) {
            return this.mGet.showHelpGuidePopup(Setting.HELP_SMART_ZOOM_RECORDING, DialogCreater.DIALOG_ID_HELP_SMART_ZOOM_RECORDING, true);
        }
        if (CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(recordingMode)) {
            return this.mGet.showHelpGuidePopup(Setting.HELP_LIVE_EFFECT, DialogCreater.DIALOG_ID_HELP_LIVE_EFFECT, true);
        }
        return retVal;
    }
}
