package com.lge.camera.controller.camcorder;

import com.lge.camera.controller.PIPController;
import com.lge.camera.controller.PIPController.PIPControllerFunction;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.filterpacks.ObjectFilter;

public class PIPRecordingController extends PIPController {
    private boolean mIsObjectTrackingEnabledForSmartZoom;

    public PIPRecordingController(PIPControllerFunction function) {
        super(function);
        this.mIsObjectTrackingEnabledForSmartZoom = MultimediaProperties.SMARTZOOM_FOCUS_MODE == 2;
        initSmartZoomFocusViewMode();
    }

    public PIPRecordingController(PIPControllerFunction function, int x0, int y0, int x1, int y1) {
        super(function, x0, y0, x1, y1);
        this.mIsObjectTrackingEnabledForSmartZoom = MultimediaProperties.SMARTZOOM_FOCUS_MODE == 2;
        initSmartZoomFocusViewMode();
    }

    private void initSmartZoomFocusViewMode() {
        if (this.mSmartZoomFocusView == null) {
            return;
        }
        if (this.mIsObjectTrackingEnabledForSmartZoom) {
            this.mSmartZoomFocusView.setSmartZoomFocusViewMode(2);
        } else {
            this.mSmartZoomFocusView.setSmartZoomFocusViewMode(0);
        }
    }

    public boolean isObjectTrackingEnabledForSmartZoom() {
        return this.mIsObjectTrackingEnabledForSmartZoom;
    }

    public void enableObjectTrackingForSmartZoom() {
        this.mIsObjectTrackingEnabledForSmartZoom = true;
        this.mSmartZoomFocusView.setSmartZoomFocusViewMode(2);
        ObjectFilter.setObjectTrackingMode(true);
    }

    public void disableObjectTrackingForSmartZoom() {
        this.mIsObjectTrackingEnabledForSmartZoom = false;
        this.mSmartZoomFocusView.setSmartZoomFocusViewMode(0);
        ObjectFilter.setObjectTrackingMode(false);
    }
}
