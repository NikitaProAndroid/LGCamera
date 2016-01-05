package com.lge.camera.command;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.controller.BubblePopupController;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class SetTimeMachineMode extends Command {
    public SetTimeMachineMode(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        execute(new Bundle());
    }

    public void execute(Object arg) {
        boolean subMenuClicked = ((Bundle) arg).getBoolean("subMenuClicked", false);
        if (!checkMediator()) {
            return;
        }
        if (FunctionProperties.isTimeMachinShotSupported()) {
            String value = this.mGet.getSettingValue(Setting.KEY_TIME_MACHINE);
            CamLog.d(FaceDetector.TAG, "## SetTimeMachineMode : " + value + ", subMenuClicked = " + subMenuClicked);
            if (this.mGet.getApplicationMode() == 0 && !this.mGet.isAttachMode() && subMenuClicked) {
                this.mGet.doCommand(Command.CAMERA_SHOT_MODE);
            }
            if (subMenuClicked) {
                onExecuteAlone(value);
                this.mGet.getImageListUri().clear();
                return;
            }
            return;
        }
        CamLog.i(FaceDetector.TAG, "SetTimeMachineMode : model is not supported.");
    }

    protected void onExecuteAlone(String value) {
        CamLog.d(FaceDetector.TAG, "SetTimeMachineMode-onExecuteAlone");
        if (FunctionProperties.isTimeMachinShotSupported()) {
            boolean timeMachineTempFileDelete = false;
            try {
                timeMachineTempFileDelete = this.mGet.deleteTimeMachineImages();
                this.mGet.setTimemachineHasPictures(false);
                BubblePopupController.get().initializeNotiComplete();
            } catch (Exception e) {
                CamLog.w(FaceDetector.TAG, "Exception:", e);
            } finally {
                CamLog.i(FaceDetector.TAG, "timeMachineTempFileDeleted ? = " + timeMachineTempFileDelete);
            }
            this.mGet.quickFunctionControllerInitMenu();
            this.mGet.quickFunctionControllerRefresh(true);
        }
    }
}
