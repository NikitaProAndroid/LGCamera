package com.lge.camera.controller;

import com.lge.camera.ControllerFunction;
import com.lge.camera.R;

public class PreviewPanel2DController extends PreviewPanelController {
    public PreviewPanel2DController(ControllerFunction function) {
        super(function);
    }

    public void initController() {
        this.mGet.inflateStub(R.id.stub_preview_2d_panel);
        super.initController();
    }
}
