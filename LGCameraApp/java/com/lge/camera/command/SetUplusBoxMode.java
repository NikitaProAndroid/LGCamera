package com.lge.camera.command;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.DialogCreater;
import com.lge.olaworks.library.FaceDetector;

public class SetUplusBoxMode extends Command {
    private boolean mUPlusBoxValueOn;

    public SetUplusBoxMode(ControllerFunction function) {
        super(function);
        this.mUPlusBoxValueOn = true;
    }

    public void execute() {
        execute(new Bundle());
    }

    public void execute(Object arg) {
        boolean subMenuClicked = ((Bundle) arg).getBoolean("subMenuClicked", false);
        if (checkMediator()) {
            String value = this.mGet.getSettingValue(Setting.KEY_UPLUS_BOX);
            CamLog.d(FaceDetector.TAG, "## SetUplusBoxMode : " + value);
            if (FunctionProperties.isUPlusBox()) {
                if (checkMediator()) {
                    if (this.mGet.getApplicationMode() == 0) {
                        if (value.equals(CameraConstants.SMART_MODE_ON)) {
                            this.mUPlusBoxValueOn = true;
                        } else {
                            this.mUPlusBoxValueOn = false;
                        }
                        this.mGet.setSetting(Setting.KEY_UPLUS_BOX, value);
                    }
                    this.mGet.runOnUiThread(new Runnable() {
                        public void run() {
                            if (SetUplusBoxMode.this.checkMediator()) {
                                SetUplusBoxMode.this.mGet.removePostRunnable(this);
                                if (SetUplusBoxMode.this.mGet.getApplicationMode() == 0) {
                                    SetUplusBoxMode.this.mGet.setPreferenceMenuEnable(Setting.KEY_UPLUS_BOX, true, true);
                                }
                            }
                        }
                    });
                }
                if (subMenuClicked) {
                    onExecuteAlone();
                    return;
                }
                return;
            }
            this.mUPlusBoxValueOn = false;
            CamLog.i(FaceDetector.TAG, "SetUplusBoxMode : model is not supported.");
        }
    }

    protected void onExecuteAlone() {
        CamLog.d(FaceDetector.TAG, "SetUplusBoxMode - Show Toast Message : SetUplusBoxMode is = ");
        if (FunctionProperties.isUPlusBox()) {
            if (this.mUPlusBoxValueOn && !this.mGet.showHelpGuidePopup(Setting.HELP_UPLUS_BOX, DialogCreater.DIALOG_ID_HELP_UPLUS_BOX, true)) {
                this.mGet.toast(this.mGet.getString(R.string.sp_uplusbox_NORMAL_desc_on));
            }
            this.mGet.quickFunctionControllerInitMenu();
            this.mGet.quickFunctionControllerRefresh(true);
        }
    }
}
