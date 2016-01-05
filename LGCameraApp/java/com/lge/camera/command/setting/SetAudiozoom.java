package com.lge.camera.command.setting;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.DialogCreater;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;

public class SetAudiozoom extends SettingCommand {
    private boolean mIsSubMenuCliecked;
    private String newValue;
    private boolean showHelp;

    public SetAudiozoom(ControllerFunction function) {
        super(function);
        this.mIsSubMenuCliecked = false;
        this.newValue = null;
        this.showHelp = false;
    }

    public void execute(LGParameters lgParameters) {
        execute(lgParameters, new Bundle());
    }

    public void execute(LGParameters lgParameters, Object arg) {
        CamLog.d(FaceDetector.TAG, "SetAudiozoom");
        this.mIsSubMenuCliecked = ((Bundle) arg).getBoolean("subMenuClicked", false);
        if (FunctionProperties.isSupportAudiozoom()) {
            CamLog.d(FaceDetector.TAG, "SetAudiozoom ");
            if (!this.mGet.getAudiozoomStart()) {
                this.newValue = this.mGet.getSettingValue(Setting.KEY_CAMCORDER_AUDIOZOOM);
                if (!this.newValue.equals(this.mGet.getAudiozoomvalue())) {
                    if (this.mGet.getSubMenuMode() == 0 && (this.mGet.getVideoState() == 3 || this.mGet.getVideoState() == 4)) {
                        this.mGet.setAudiozoomvalue(this.newValue);
                    }
                    if (CameraConstants.SMART_MODE_ON.equals(this.newValue)) {
                        this.showHelp = true;
                    }
                }
            }
        }
    }

    protected void onExecuteAlone() {
        if (FunctionProperties.isSupportAudiozoom()) {
            if (this.mIsSubMenuCliecked) {
                this.mGet.quickFunctionControllerInitMenu();
                this.mGet.quickFunctionControllerRefresh(true);
            }
            if (this.showHelp && CameraConstants.SMART_MODE_ON.equals(this.newValue)) {
                showHelpPopup();
                this.showHelp = false;
            }
        }
    }

    private void showHelpPopup() {
        if (this.mGet.isRotateDialogVisible()) {
            this.mGet.onDismissRotateDialog();
        }
        this.mGet.showHelpGuidePopup(Setting.HELP_AUDIOZOOM, DialogCreater.DIALOG_ID_HELP_AUDIOZOOM, true);
    }
}
