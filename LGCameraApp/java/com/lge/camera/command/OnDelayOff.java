package com.lge.camera.command;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AudioUtil;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class OnDelayOff extends Command {
    public OnDelayOff(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        execute(new Bundle());
    }

    public void execute(Object arg) {
        CamLog.d(FaceDetector.TAG, "OnDelayOff-start");
        excuteDisplayPreviewPanel();
        excuteDisplayPreview();
        afterDisplayPreview();
        if (this.mGet.getApplicationMode() == 0 && this.mGet.getSettingValue(Setting.KEY_VOICESHUTTER).equals(CameraConstants.SMART_MODE_OFF) && !this.mGet.isCameraKeyLongPressed() && !this.mGet.isShutterButtonLongKey()) {
            CamLog.d(FaceDetector.TAG, "setAudioFocus  abandon in OnDelayOff");
            AudioUtil.setAudioFocus(this.mGet.getApplicationContext(), false);
        }
    }

    private void excuteDisplayPreview() {
        if (checkMediator()) {
            String shotMode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
            if ((this.mGet.getApplicationMode() == 0 && (CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotMode) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_HDR.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_FULL_CONTINUOUS.equals(shotMode) || (this.mGet.getCameraId() == 1 && !this.mGet.isDualCameraActive()))) || this.mGet.isTimeMachineModeOn()) {
                this.mGet.startPreview(null, true);
            }
            this.mGet.showPreview();
            this.mGet.showIndicatorController();
            this.mGet.showQuickFunctionController();
            this.mGet.quickFunctionControllerRefresh(true);
            this.mGet.enableCommand(true);
            this.mGet.setMainButtonEnable();
            this.mGet.showDefaultQuickButton(true);
            if (this.mGet.getApplicationMode() == 1) {
                if (this.mGet.isEffectsCamcorderActive()) {
                    this.mGet.setLimitationToLiveeffect(false);
                } else {
                    this.mGet.setLimitationToLiveeffect(true);
                }
            }
            this.mGet.setQuickButtonForcedDisable(false);
            this.mGet.setButtonRemainRefresh();
            if (this.mGet.isPIPFrameDrawerOpened()) {
                this.mGet.setQuickButtonMenuEnable(false, true);
            } else {
                this.mGet.setQuickButtonMenuEnable(true, false);
            }
            if (this.mGet.getApplicationMode() == 1) {
                this.mGet.recordingControllerShow();
                if (this.mGet.isMMSIntent()) {
                    int mCarrierCode = ModelProperties.getCarrierCode();
                    if (mCarrierCode == 5 || mCarrierCode == 21) {
                        this.mGet.showRequestedSizeLimit();
                    }
                }
            }
        }
    }

    private void excuteDisplayPreviewPanel() {
        this.mGet.setMainButtonVisible(true);
        this.mGet.setMainButtonDisable();
        this.mGet.showSubButtonInit(true);
        if (this.mGet.isAttachIntent()) {
            this.mGet.setSwitcherVisible(false);
        } else {
            this.mGet.setSwitcherVisible(true);
        }
        if (ModelProperties.is3dSupportedModel()) {
            this.mGet.set3DSwitchVisible(true);
        }
    }

    private void afterDisplayPreview() {
        this.mGet.doCommandUi(Command.ROTATE);
        if (this.mGet.getApplicationMode() == 0) {
            if (!(Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)) || CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)))) {
                this.mGet.showFocus();
            }
            if (this.mGet.isTimeMachineModeOn() && !this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_REFOCUS)) {
                this.mGet.removeAllImageList();
            }
        } else {
            this.mGet.showFocus();
            this.mGet.setVideoState(0);
        }
        this.mGet.checkStorage(false);
        this.mGet.keepScreenOnAwhile();
    }
}
