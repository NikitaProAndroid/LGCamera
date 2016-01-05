package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;

public class SetCameraWhiteBalance extends SettingCommand {
    public SetCameraWhiteBalance(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetCameraWhiteBalance ");
        String oldValue = lgParameters.getParameters().getWhiteBalance();
        String newValue = this.mGet.getSettingValue(Setting.KEY_CAMERA_WHITEBALANCE);
        if (!this.mGet.getSettingValue(Setting.KEY_CAMERA_WHITEBALANCE).equals(LGT_Limit.ISP_AUTOMODE_AUTO)) {
            if (!(CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_COLOREFFECT)) || Setting.HELP_OTHER.equals(this.mGet.getSettingValue(Setting.KEY_CAMERA_COLOREFFECT)))) {
                this.mGet.setSetting(Setting.KEY_CAMERA_COLOREFFECT, Setting.HELP_OTHER);
                lgParameters.getParameters().setColorEffect(Setting.HELP_OTHER);
                this.mGet.toast((int) R.string.note_color_effect_change_none);
                this.mGet.setQuickFunctionControllerAllMenuIcons();
            }
            this.mGet.checkSceneMode(lgParameters, true, this.mGet.getString(R.string.note_shot_mode_change_normal));
        }
        this.mGet.setMenuEnableForSceneMode(1);
        if (oldValue != null && !oldValue.equals(newValue)) {
            CamLog.i(FaceDetector.TAG, "white balance set to " + newValue);
            this.mGet.setSetting(Setting.KEY_CAMERA_WHITEBALANCE, newValue);
            lgParameters.getParameters().setWhiteBalance(newValue);
        }
    }
}
