package com.lge.camera.command;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class StartRecording extends Command {
    public StartRecording(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "StartRecording");
        if (ProjectVariables.useHideQFLWhenSettingMenuDisplay()) {
            this.mGet.hideOsdByForce();
        } else {
            this.mGet.hideOsd();
        }
        this.mGet.hideOptionMenu();
        this.mGet.setKeepScreenOn();
        this.mGet.setSwitcherVisible(false);
        if (ModelProperties.is3dSupportedModel()) {
            this.mGet.set3DSwitchVisible(false);
        }
        this.mGet.clearSubMenu();
        this.mGet.enableCommand(false);
        this.mGet.removeScheduledCommand(Command.EXIT_ZOOM_BRIGHTNESS_INTERACTION);
        if (this.mGet.isLiveEffectActive()) {
            this.mGet.doCommandUi(Command.HIDE_LIVEEFFECT_SUBMENU_DRAWER);
        } else if (this.mGet.isDualRecordingActive() || this.mGet.isSmartZoomRecordingActive()) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("only_handle_close", true);
            this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU, bundle);
        }
        this.mGet.startRecording();
        this.mGet.updateThumbnailButtonVisibility();
    }
}
