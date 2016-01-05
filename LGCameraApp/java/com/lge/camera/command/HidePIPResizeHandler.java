package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class HidePIPResizeHandler extends Command {
    public HidePIPResizeHandler(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "HideDualRecSubWindowHandler executed");
        if (this.mGet != null) {
            this.mGet.removeScheduledCommand(Command.HIDE_PIP_SUBWINDOW_RESIZE_HANDLER);
            this.mGet.hideSubWindowResizeHandler();
        }
    }
}
