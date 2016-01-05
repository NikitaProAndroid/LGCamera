package com.lge.camera.command;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class StopRecording extends Command {
    public StopRecording(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        execute(new Bundle());
    }

    public void execute(Object arg) {
        Bundle bundle = (Bundle) arg;
        boolean isBackKeyRecStop = bundle.getBoolean("isBackKeyRecStop", false);
        boolean isFromMountedAction = bundle.getBoolean("isFromMountedAction", false);
        CamLog.d(FaceDetector.TAG, "StopRecording isBackKeyRecStop=" + isBackKeyRecStop + " isFromMountedAction=" + isFromMountedAction);
        if (isBackKeyRecStop) {
            this.mGet.setBackKeyRecStop(true);
        } else {
            this.mGet.setBackKeyRecStop(false);
        }
        this.mGet.setVideoState(2);
        this.mGet.getActivity().getWindow().clearFlags(128);
        this.mGet.keepScreenOnAwhile();
        if (this.mGet.getSubMenuMode() != 0) {
            this.mGet.clearQuickFunctionSubMenu();
            this.mGet.clearSubMenu();
        }
        this.mGet.stopRecording(isFromMountedAction);
        this.mGet.enableCommand(true);
        if (this.mGet.isLiveEffectActive()) {
            Bundle isOpen = new Bundle();
            isOpen.putBoolean("menu_open", false);
            this.mGet.doCommandDelayed(Command.SHOW_LIVEEFFECT_SUBMENU_DRAWER, isOpen, 1000);
        }
    }
}
