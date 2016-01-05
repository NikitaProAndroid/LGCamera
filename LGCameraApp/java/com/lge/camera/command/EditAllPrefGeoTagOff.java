package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class EditAllPrefGeoTagOff extends Command {
    public EditAllPrefGeoTagOff(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "EditAllPrefGeoTagOn executed");
        this.mGet.setAllPreferenceApply(15, Setting.KEY_CAMERA_TAG_LOCATION, CameraConstants.SMART_MODE_OFF);
    }
}
