package com.lge.camera.command.setting;

import android.os.SystemProperties;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetWDRRecording extends SettingCommand {
    public SetWDRRecording(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetWDRRecording");
        if (FunctionProperties.isHDRRecordingNameUsed()) {
            lgParameters.getParameters().set("video-hdr", CameraConstants.SMART_MODE_ON);
        } else {
            lgParameters.getParameters().set("video-wdr", CameraConstants.SMART_MODE_ON);
        }
        lgParameters.getParameters().set(CameraConstants.PARAM_KEY_DUAL_RECORDER, 0);
        if (MultimediaProperties.isHighFramRateVideoSupported()) {
            String[] fpsRange = SystemProperties.get("hw.camcorder.fpsrange", "15000,30000").split(",");
            lgParameters.getParameters().set("video-hfr", CameraConstants.SMART_MODE_OFF);
            lgParameters.getParameters().set("preview-format", "yuv420sp");
            lgParameters.getParameters().setPreviewFpsRange(Integer.parseInt(fpsRange[0]), Integer.parseInt(fpsRange[1]));
        }
        this.mGet.setSetting(Setting.KEY_VIDEO_RECORD_MODE, CameraConstants.TYPE_RECORDMODE_WDR);
    }
}
