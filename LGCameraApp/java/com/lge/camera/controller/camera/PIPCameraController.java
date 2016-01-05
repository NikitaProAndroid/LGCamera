package com.lge.camera.controller.camera;

import com.lge.camera.controller.PIPController;
import com.lge.camera.controller.PIPController.PIPControllerFunction;

public class PIPCameraController extends PIPController {
    public PIPCameraController(PIPControllerFunction function) {
        super(function);
    }

    public PIPCameraController(PIPControllerFunction function, int x0, int y0, int x1, int y1) {
        super(function, x0, y0, x1, y1);
    }
}
