package com.lge.camera.controller.camcorder;

import android.graphics.Rect;
import android.hardware.Camera.Area;
import com.lge.camera.ControllerFunction;
import com.lge.camera.controller.FocusController;
import com.lge.camera.listeners.CamcorderAutoFocusCallback;
import com.lge.camera.listeners.CamcorderContinuousFocusCallback;
import com.lge.camera.listeners.CamcorderContinuousFocusCallback.CamcorderCAFCallbackFunction;
import com.lge.camera.listeners.ObjectTrackingFocusCallback;
import com.lge.camera.listeners.ObjectTrackingFocusCallback.ObjectTrackingFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Util;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;
import java.util.List;

public class CamcorderFocusController extends FocusController implements CamcorderCAFCallbackFunction, ObjectTrackingFunction {
    protected CamcorderAutoFocusCallback mAutoFocusCallback;
    private List<Area> mFocusArea;

    public CamcorderFocusController(ControllerFunction function) {
        super(function);
        this.mFocusArea = null;
        this.mAutoFocusCallback = new CamcorderAutoFocusCallback();
        this.mContinuousFocusCallback = new CamcorderContinuousFocusCallback(this);
        this.mObjectTrackingFocusCallback = new ObjectTrackingFocusCallback(this);
    }

    public void doFocus(boolean pressed) {
    }

    public void onPause() {
        super.onPause();
        if (FunctionProperties.isCafAnimationSupported(this.mGet.getApplicationMode(), this.mGet.getCameraId())) {
            unregisterCallback();
        }
        unregisterObjectCallback();
    }

    public void onDestroy() {
        if (this.mFocusArea != null) {
            this.mFocusArea.clear();
            this.mFocusArea = null;
        }
        this.mAutoFocusCallback = null;
        this.mContinuousFocusCallback = null;
        this.mObjectTrackingFocusCallback = null;
    }

    public boolean showFocus(boolean justDoIt) {
        if ((!justDoIt && !super.showFocus()) || !FunctionProperties.isCafAnimationSupported(this.mGet.getApplicationMode(), this.mGet.getCameraId())) {
            return false;
        }
        CamLog.d(FaceDetector.TAG, "### showFocus & register");
        registerCallback();
        return true;
    }

    public boolean showFocus() {
        return showFocus(false);
    }

    public boolean hideFocus() {
        if (super.hideFocus() && FunctionProperties.isCafAnimationSupported(this.mGet.getApplicationMode(), this.mGet.getCameraId())) {
            CamLog.d(FaceDetector.TAG, "### hideFocus & unregister");
            unregisterCallback();
        }
        return true;
    }

    public void doCamcorderContinuousFocusCallback(boolean focusedState) {
        this.mGet.doCamcorderContinuousFocusCallback(focusedState);
    }

    public void startFocusByTouchPress(int x, int y) {
        CamLog.d(FaceDetector.TAG, "startFocusByTouchPress");
        if (FunctionProperties.isSupportObjectTracking() && checkAvailablePreviewSize() && !this.mGet.getAudiozoomStart() && y >= this.mFocusAreaTopMargin && y <= this.mFocusAreaTopMargin + this.mFocusAreaHeight) {
            y -= this.mFocusAreaTopMargin;
            setObjectFocusState(0);
            if (!isRegisterObjectCallback()) {
                calculateObjectScaleRatio();
                registerObjectCallback();
            }
            setFocusRectangleInitialize();
            setObjectFocusRect(x, y);
            setFocusWindow(this.mFocusRect);
            this.mGet.getLG().runObjectTracking();
            CamLog.d(FaceDetector.TAG, "run object tracking");
        }
    }

    public void setObjectFocusRect(int x, int y) {
        int left = x - (this.RECTANGLE_WIDTH / 2);
        int top = y - (this.RECTANGLE_HEIGHT / 2);
        int right = x + (this.RECTANGLE_WIDTH / 2);
        int bottom = y + (this.RECTANGLE_HEIGHT / 2);
        if (left <= this.mFocusAreaLeftMargin) {
            left = this.mFocusAreaLeftMargin;
            right = this.mFocusAreaLeftMargin + this.RECTANGLE_WIDTH;
        }
        if (top <= 0) {
            top = 0;
            bottom = this.RECTANGLE_HEIGHT;
        }
        if (right >= this.mFocusAreaLeftMargin + this.mFocusAreaWidth) {
            right = this.mFocusAreaLeftMargin + this.mFocusAreaWidth;
            left = (this.mFocusAreaLeftMargin + this.mFocusAreaWidth) - this.RECTANGLE_WIDTH;
        }
        if (bottom >= this.mFocusAreaHeight) {
            top = this.mFocusAreaHeight - this.RECTANGLE_HEIGHT;
            bottom = this.mFocusAreaHeight;
        }
        CamLog.i(FaceDetector.TAG, "move to top = " + top + ", left = " + left + ", right = " + right + ", bottom = " + bottom);
        setFocusRectangle(left, top, right, bottom);
    }

    public void setFocusWindow(Rect rect) {
        String sizeOnScreenString = this.mGet.getPreviewSizeOnScreen();
        int[] previewSizeOnScreen = Util.SizeString2WidthHeight(sizeOnScreenString);
        if (sizeOnScreenString == null) {
            super.onResume();
            CamLog.d(FaceDetector.TAG, "error! sizeOnScreenString is null");
        } else if (this.mGet.getCameraDevice() == null || this.mGet.getLGParam() == null) {
            CamLog.e(FaceDetector.TAG, "mCameraDevice is null");
        } else {
            LGParameters lgParameters = this.mGet.getLGParam();
            if (this.mFocusArea == null) {
                this.mFocusArea = new ArrayList();
                this.mFocusArea.add(new Area(this.mFocusRect, 1));
            }
            try {
                float center_x = ((float) previewSizeOnScreen[0]) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK;
                float center_y = ((float) previewSizeOnScreen[1]) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK;
                rect.left = Math.round(((((float) rect.left) - center_x) * 2000.0f) / ((float) previewSizeOnScreen[0]));
                rect.top = Math.round(((((float) rect.top) - center_y) * 2000.0f) / ((float) previewSizeOnScreen[1]));
                rect.right = Math.round(((((float) rect.right) - center_x) * 2000.0f) / ((float) previewSizeOnScreen[0]));
                rect.bottom = Math.round(((((float) rect.bottom) - center_y) * 2000.0f) / ((float) previewSizeOnScreen[1]));
                ((Area) this.mFocusArea.get(0)).rect.left = rect.left;
                ((Area) this.mFocusArea.get(0)).rect.right = rect.right;
                ((Area) this.mFocusArea.get(0)).rect.top = rect.top;
                ((Area) this.mFocusArea.get(0)).rect.bottom = rect.bottom;
                this.mGet.setEnable3ALocks(lgParameters, false);
                lgParameters.getParameters().setFocusAreas(this.mFocusArea);
                lgParameters.setObjectTracking(CameraConstants.SMART_MODE_ON);
                CamLog.d(FaceDetector.TAG, "###setFocusMode-ObjectTracking");
                lgParameters.setParameters(lgParameters.getParameters());
                CamLog.i(FaceDetector.TAG, "setFocusWindow : left " + rect.left + " top " + rect.top);
                CamLog.i(FaceDetector.TAG, "setFocusWindow : right " + rect.right + " bottom " + rect.bottom);
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "setTouchIndexAf setParameter exception : ", e);
            }
        }
    }

    public void doObjectTrackingFocusCallback(int[] data) {
        startObjectTrackingFocus(data);
    }
}
