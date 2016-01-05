package com.lge.camera.command;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AppControlUtil;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;

public class StartInit extends Command {
    private int mCheckCount;
    private boolean mFirstInitController;

    public StartInit(ControllerFunction function) {
        super(function);
        this.mFirstInitController = false;
        this.mCheckCount = 0;
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "initialize UI-start");
        if (!this.mFirstInitController) {
            this.mFirstInitController = true;
            Bundle bundle = new Bundle();
            bundle.putBoolean("animation", false);
            this.mGet.doCommandUi(Command.ROTATE, bundle);
            AppControlUtil.UnblockAlarmInRecording(this.mGet.getActivity());
            this.mGet.getActivity().getWindow().setBackgroundDrawable(null);
        }
        if (this.mGet.isPreviewing() && !this.mGet.isPreviewOnGoing()) {
            doInit();
        } else if (this.mCheckCount >= 50) {
            CamLog.e(FaceDetector.TAG, "Preview fail");
            doInit();
        } else if (!this.mGet.isErrorOccuredAndFinish()) {
            this.mGet.removeScheduledCommand(Command.START_INIT);
            this.mGet.doCommandDelayed(Command.START_INIT, 100);
            CamLog.d(FaceDetector.TAG, "initialize UI-restart checkCount:" + this.mCheckCount);
            this.mCheckCount++;
            return;
        }
        CamLog.d(FaceDetector.TAG, "initialize UI-end");
    }

    private void doInit() {
        this.mCheckCount = 0;
        this.mFirstInitController = false;
        this.mGet.initControllers();
        CamLog.d(FaceDetector.TAG, "mIsFromMountedAction=" + this.mGet.isStopRecordingByMountedAction());
        if (!ProjectVariables.isSupportRecordingModePopUp() || this.mGet.getApplicationMode() != 1 || this.mGet.isAttachIntent() || this.mGet.isStopRecordingByMountedAction()) {
            this.mGet.checkStorage(false);
            this.mGet.quickFunctionControllerInitMenu();
            this.mGet.quickFunctionControllerRefresh(true);
            this.mGet.enableCommand(true);
        } else {
            this.mGet.setVideoState(1);
            this.mGet.enableCommand(false);
            this.mGet.doCommand(Command.SELECT_VIDEO_LENGTH);
        }
        this.mGet.showBeautyShotBarForNewUx(true);
        if (this.mGet.getApplicationMode() == 0) {
            if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA) || Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
                this.mGet.hideFocus();
            }
            if (CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
                this.mGet.showManualFocusController(true);
                this.mGet.setSubMenuMode(25);
            }
        }
        if (ProjectVariables.useTurnOffAnimation()) {
            this.mGet.getHandler().postDelayed(new Runnable() {
                public void run() {
                    Common.turnOnAnimation();
                }
            }, 300);
        }
        this.mGet.showQuickMenuEnteringGuide(true);
        if (ProjectVariables.isSupportClearView()) {
            this.mGet.removeScheduledCommand(Command.CLEAR_SCREEN);
            this.mGet.doCommandDelayed(Command.CLEAR_SCREEN, CameraConstants.TOAST_LENGTH_LONG);
        }
    }
}
