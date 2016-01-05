package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AppControlUtil;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetVideoShowRecordedVideo extends SettingCommand {
    public SetVideoShowRecordedVideo(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetVideoShowRecordedVideo executed");
        String recordMode = this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE);
        if (AppControlUtil.checkGuestModeAndAppDisabled(this.mGet.getContentResolver(), true, 1) || this.mGet.isAttachIntent()) {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VIDEO_AUTO_REVIEW, false);
        } else if (CameraConstants.TYPE_RECORDMODE_DUAL.equals(recordMode) || CameraConstants.TYPE_RECORDMODE_SMART_ZOOM.equals(recordMode)) {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VIDEO_AUTO_REVIEW, false);
        } else {
            this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_VIDEO_AUTO_REVIEW, true);
        }
    }

    protected void onExecuteAlone() {
        if (checkMediator()) {
            this.mGet.allSettingMenuSelectedChild(Setting.KEY_VIDEO_AUTO_REVIEW, false);
        }
    }
}
