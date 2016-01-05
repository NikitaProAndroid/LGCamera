package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ShowPIPResizeHandler extends Command {
    public ShowPIPResizeHandler(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "ShowDualRecSubWindowHandler executed");
        execute(Integer.valueOf(0));
    }

    public void execute(Object arg) {
        float v1 = ((Float) arg).floatValue();
        CamLog.d(FaceDetector.TAG, "ShowDualRecSubWindowHandler executed");
        execute(Float.valueOf(v1), Float.valueOf(v1));
    }

    public void execute(Object arg1, Object arg2) {
        float x = ((Float) arg1).floatValue();
        float y = ((Float) arg2).floatValue();
        CamLog.d(FaceDetector.TAG, "ShowDualRecSubWindowHandler executed");
        this.mGet.showSubWindowResizeHandler(x, y);
    }
}
