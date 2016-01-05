package com.lge.camera.command.setting;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetSmartCameraFlashMode extends SettingCommand {
    public SetSmartCameraFlashMode(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, new Bundle());
    }

    public void execute(LGParameters lgParameters, Object arg) {
        boolean flashStatues = ((Bundle) arg).getBoolean("isIAFlashOn", false);
        CamLog.d(FaceDetector.TAG, "SetSmartCameraFlashMode flastatus" + flashStatues);
        if (flashStatues) {
            lgParameters.getParameters().setFlashMode(CameraConstants.SMART_MODE_ON);
        } else {
            lgParameters.getParameters().setFlashMode(CameraConstants.SMART_MODE_OFF);
        }
    }
}
