package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetVideoVoice extends SettingCommand {
    public SetVideoVoice(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetVideoVoice: " + this.mGet.getSettingValue(Setting.KEY_VIDEO_AUDIO_RECORDING));
        updateIndicator();
    }

    public void updateIndicator() {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                SetVideoVoice.this.mGet.removePostRunnable(this);
                SetVideoVoice.this.mGet.updateAudioIndicator();
            }
        });
    }

    protected void onExecuteAlone() {
        if (checkMediator()) {
            this.mGet.allSettingMenuSelectedChild(Setting.KEY_VIDEO_AUDIO_RECORDING, false);
        }
    }
}
