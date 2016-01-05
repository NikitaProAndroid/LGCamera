package com.lge.camera.command;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AudioUtil;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;

public class DisplayPreview extends Command {
    public DisplayPreview(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        execute(new Bundle());
    }

    public void execute(Object arg) {
        CamLog.d(FaceDetector.TAG, "DisplayPreview - start");
        if (this.mGet.isPausing()) {
            CamLog.d(FaceDetector.TAG, "DisplayPreview - pausing state, so return");
            return;
        }
        boolean fromJpegCallback = ((Bundle) arg).getBoolean("fromJpegCallback", false);
        if (this.mGet.getApplicationMode() == 0 && this.mGet.getSettingValue(Setting.KEY_VOICESHUTTER).equals(CameraConstants.SMART_MODE_OFF) && !this.mGet.isCameraKeyLongPressed() && !this.mGet.isShutterButtonLongKey()) {
            CamLog.d(FaceDetector.TAG, "setAudioFocus  abandon in displayPreview");
            AudioUtil.setAudioFocus(this.mGet.getApplicationContext(), false);
        }
        this.mGet.showPreview();
        if (!Common.isQuickWindowCameraMode()) {
            this.mGet.showIndicatorController();
            this.mGet.quickFunctionControllerRefresh(true);
            this.mGet.setQuickButtonMenuEnable(true, false);
            this.mGet.setMainButtonVisible(true);
            this.mGet.enableCommand(true);
            this.mGet.showSubButtonInit(false);
            this.mGet.showDefaultQuickButton(true);
        }
        if (this.mGet.isAttachIntent()) {
            this.mGet.setSwitcherVisible(false);
        } else {
            this.mGet.setSwitcherVisible(true);
            if (ModelProperties.is3dSupportedModel()) {
                this.mGet.set3DSwitchVisible(true);
            }
            if (fromJpegCallback) {
                this.mGet.setMainButtonEnable(CameraConstants.STORAGECONTROLLER_LOCKKEY);
            } else {
                CamLog.d(FaceDetector.TAG, "DisplayPreview thumbnail update");
                this.mGet.doCommandDelayed(Command.UPDATE_THUMBNAIL_BUTTON, 200);
                if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CONTINUOUS)) {
                    this.mGet.setMainButtonEnable(CameraConstants.STORAGECONTROLLER_LOCKKEY);
                }
                this.mGet.doCommandUi(Command.ROTATE);
            }
        }
        if (this.mGet.getApplicationMode() == 0) {
            if (!(Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)) || CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)))) {
                this.mGet.showFocus();
            }
            if (!(this.mGet.isTimeMachineModeOn() || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_REFOCUS))) {
                this.mGet.removeAllImageList();
            }
        } else {
            this.mGet.showFocus();
        }
        this.mGet.keepScreenOnAwhile();
        CamLog.d(FaceDetector.TAG, "DisplayPreview - end");
    }
}
