package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.module.MainBeautyShot;
import com.lge.camera.module.Module;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceBeauty;
import com.lge.olaworks.library.FaceDetector;

public class SetOlaMainBeautyShot extends SettingCommand {
    public SetOlaMainBeautyShot(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        if (!(this.mGet.getCurrentModule() instanceof MainBeautyShot) || !this.mGet.getEngineProcessor().checkEngineTag(FaceBeauty.ENGINE_TAG)) {
            lgParameters.getParameters().set(CameraConstants.PARAM_KEY_DUAL_RECORDER, 0);
            this.mGet.setModule(Module.MAIN_BEAUTY_SHOT);
            this.mGet.getEngineProcessor().setEngine(this.mGet.getFaceBeauty());
            int value = 0;
            if (this.mGet.findPreference(Setting.KEY_BEAUTYSHOT) != null) {
                value = Integer.parseInt(this.mGet.getSettingValue(Setting.KEY_BEAUTYSHOT));
                CamLog.e(FaceDetector.TAG, "beautyshot value : " + value);
            }
            if (value < 0) {
                value = 0;
            }
            this.mGet.setFaceBeutyShotParameter(value);
        }
    }
}
