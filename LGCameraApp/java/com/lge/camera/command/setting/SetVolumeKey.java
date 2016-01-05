package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetVolumeKey extends SettingCommand {
    public SetVolumeKey(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        if (checkMediator()) {
            String value = this.mGet.getSettingValue(Setting.KEY_VOLUME);
            CamLog.d(FaceDetector.TAG, "## SetVolumeKey : " + value);
            this.mGet.setAllPreferenceApply(15, Setting.KEY_VOLUME, value);
        }
    }

    protected void onExecuteAlone() {
        CamLog.d(FaceDetector.TAG, "SetVolumeKey");
        this.mGet.quickFunctionControllerInitMenu();
        this.mGet.quickFunctionControllerRefresh(true);
    }
}
