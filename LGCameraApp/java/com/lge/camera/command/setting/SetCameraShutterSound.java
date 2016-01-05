package com.lge.camera.command.setting;

import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ShutterSoundProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.TelephonyUtil;
import com.lge.hardware.LGCamera.LGParameters;

public class SetCameraShutterSound extends SettingCommand {
    public SetCameraShutterSound(ControllerFunction function) {
        super(function);
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, null);
    }

    public void execute(LGParameters lgParameters, Object arg) {
        if (ShutterSoundProperties.isSupportShutterSoundOff() && CameraConstants.SMART_MODE_OFF.equals(this.mGet.getSettingValue(Setting.KEY_SHUTTER_SOUND))) {
            this.mGet.changeShutterSound(-1);
            return;
        }
        this.mGet.changeShutterSound(Integer.parseInt(this.mGet.getSettingValue(Setting.KEY_SHUTTER_SOUND)));
    }

    protected void onExecuteAlone() {
        this.mGet.allSettingMenuSelectedChild(Setting.KEY_SHUTTER_SOUND, false);
        if ((!ShutterSoundProperties.isSupportShutterSoundOff() || !this.mGet.getSettingValue(Setting.KEY_SHUTTER_SOUND).equals(CameraConstants.SMART_MODE_OFF)) && !TelephonyUtil.phoneInCall(this.mGet.getApplicationContext())) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    SetCameraShutterSound.this.mGet.removePostRunnable(this);
                    SetCameraShutterSound.this.mGet.playShutterSound();
                }
            });
        }
    }
}
