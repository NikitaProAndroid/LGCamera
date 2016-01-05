package com.lge.camera.command.setting;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;

public class SetFlashMode extends SettingCommand {
    private boolean mIsSubMenuCliecked;

    public SetFlashMode(ControllerFunction function) {
        super(function);
        this.mIsSubMenuCliecked = false;
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, new Bundle());
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetFlashMode");
        Bundle bundle = (Bundle) arg;
        this.mIsSubMenuCliecked = bundle.getBoolean("subMenuClicked", false);
        boolean fromQuickButton = bundle.getBoolean("fromQuickButton", false);
        String originFlashMode = this.mGet.getSettingValue(Setting.KEY_FLASH);
        boolean enable = this.mGet.getFlashEnableForShotMode();
        String flashMode = enable ? originFlashMode : CameraConstants.SMART_MODE_OFF;
        this.mGet.setPreferenceMenuOnlyEnable(Setting.KEY_FLASH, enable);
        CamLog.i(FaceDetector.TAG, String.format("Set flash mode to [%s]", new Object[]{flashMode}));
        if (Common.isSupported(lgParameters.getParameters(), flashMode) && !CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(flashMode)) {
            if (FunctionProperties.isSupportedVideoFlashAuto()) {
                this.mGet.setAllPreferenceApply(15, Setting.KEY_FLASH, originFlashMode);
            }
            if (this.mGet.getApplicationMode() != 1) {
                lgParameters.getParameters().setFlashMode(flashMode);
            } else if (fromQuickButton) {
                if (CameraConstants.SMART_MODE_ON.equals(flashMode)) {
                    lgParameters.getParameters().setFlashMode(CameraConstants.FLASH_TORCH);
                } else if (!FunctionProperties.isSupportedVideoFlashAuto() || !LGT_Limit.ISP_AUTOMODE_AUTO.equals(flashMode)) {
                    lgParameters.getParameters().setFlashMode(CameraConstants.SMART_MODE_OFF);
                } else if (Common.isLowLuminance(lgParameters.getParameters(), false)) {
                    lgParameters.getParameters().setFlashMode(CameraConstants.FLASH_TORCH);
                } else {
                    lgParameters.getParameters().setFlashMode(CameraConstants.SMART_MODE_OFF);
                }
            } else if (CameraConstants.FLASH_TORCH.equals(flashMode)) {
            }
        }
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                SetFlashMode.this.mGet.removePostRunnable(this);
                SetFlashMode.this.mGet.updateFlashIndicator(false, null);
            }
        });
        this.mGet.doCommand(Command.SET_SUPER_ZOOM, lgParameters);
    }

    protected void onExecuteAlone() {
        if (this.mIsSubMenuCliecked) {
            this.mGet.quickFunctionControllerInitMenu();
            this.mGet.quickFunctionControllerRefresh(true);
        }
    }
}
