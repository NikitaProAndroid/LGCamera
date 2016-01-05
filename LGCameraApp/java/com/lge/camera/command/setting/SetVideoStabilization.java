package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetVideoStabilization extends SettingCommand {
    public SetVideoStabilization(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        String value = this.mGet.getSettingValue(Setting.KEY_VIDEO_STABILIZATION);
        CamLog.d(FaceDetector.TAG, "SetVideostabilization: " + value);
        if (CameraConstants.SMART_MODE_ON.equals(value)) {
            lgParameters.getParameters().set("video-stabilization", CameraConstants.ONEKEY_CONTROL_ENABLE_STRING);
        } else {
            lgParameters.getParameters().set("video-stabilization", CameraConstants.ONEKEY_CONTROL_DISABLE_STRING);
        }
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                SetVideoStabilization.this.mGet.removePostRunnable(this);
                SetVideoStabilization.this.mGet.updateStabilizationIndicator();
            }
        });
    }

    protected void onExecuteAlone() {
        if (checkMediator()) {
            if (!(ModelProperties.isRenesasISP() && this.mGet.getCameraMode() == 0)) {
                this.mGet.restartPreview(null, true);
            }
            this.mGet.allSettingMenuSelectedChild(Setting.KEY_VIDEO_STABILIZATION, false);
        }
    }
}
