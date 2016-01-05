package com.lge.camera.controller.camera;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.util.Log;
import android.widget.ImageView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.CameraFocusView;
import com.lge.camera.controller.FocusController;
import com.lge.camera.listeners.CameraAutoFocusCallback;
import com.lge.camera.listeners.CameraAutoFocusCallback.CameraAutoFocusCallbackFunction;
import com.lge.camera.listeners.CameraAutoFocusMoveCallback;
import com.lge.camera.listeners.CameraAutoFocusMoveCallback.CameraAutoFocusMoveCallbackFunction;
import com.lge.camera.listeners.CameraAutoFocusOnCafCallback;
import com.lge.camera.listeners.CameraAutoFocusOnCafCallback.CameraAutoFocusOnCafCallbackFunction;
import com.lge.camera.listeners.CameraContinuousFocusCallback;
import com.lge.camera.listeners.CameraContinuousFocusCallback.CameraCAFCallbackFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Util;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.util.ArrayList;
import java.util.List;

public class CameraFocusController extends FocusController implements CameraAutoFocusCallbackFunction, CameraAutoFocusMoveCallbackFunction, CameraCAFCallbackFunction, CameraAutoFocusOnCafCallbackFunction {
    private static int mPreviousFocusedState;
    protected boolean mBlockingFaceTrFocusing;
    private List<Area> mFocusArea;
    private boolean mIsContinuousFocusActive;
    private boolean mShutterButtonClicked;

    public CameraFocusController(ControllerFunction function) {
        super(function);
        this.mShutterButtonClicked = false;
        this.mBlockingFaceTrFocusing = false;
        this.mFocusArea = null;
        this.mIsContinuousFocusActive = false;
        this.mAutoFocusCallback = new CameraAutoFocusCallback(this);
        this.mAutoFocusOnCafCallback = new CameraAutoFocusOnCafCallback(this);
        this.mAutoFocusMoveCallback = new CameraAutoFocusMoveCallback(this);
        this.mContinuousFocusCallback = new CameraContinuousFocusCallback(this);
    }

    public void onPause() {
        super.onPause();
        this.mIsContinuousFocusActive = false;
        this.mGet.doCommandUi(Command.RELEASE_TOUCH_FOCUS);
        hideFocus();
        setClearFocusAnimation();
        if (FunctionProperties.isCafAnimationSupported(this.mGet.getApplicationMode(), this.mGet.getCameraId())) {
            unregisterCallback();
        }
        ImageView v = (ImageView) this.mGet.findViewById(R.id.focus_touch_move);
        if (v != null) {
            v.setVisibility(8);
        }
    }

    public void doFocus(boolean pressed) {
        CamLog.d(FaceDetector.TAG, "doFocus pressed = " + pressed);
        if (!pressed) {
            cancelAutoFocus();
        } else if (this.mGet.getCameraId() == 1 || ModelProperties.isFixedFocusModel()) {
            this.mFocusState = 0;
        } else {
            autoFocus();
        }
    }

    public void doFocusOnCaf() {
        if (this.mGet.isPreviewing()) {
            CamLog.d(FaceDetector.TAG, "## Register AutoFocus on CAF");
            try {
                this.mGet.getCameraDevice().autoFocus(this.mAutoFocusOnCafCallback);
                if (Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
                    this.mGet.stopFaceDetection();
                }
            } catch (RuntimeException e) {
                CamLog.d(FaceDetector.TAG, "CameraDevice.autoFocus() RuntimeException: " + e);
            }
        }
    }

    public void cancelAutoFocus() {
        CamLog.i(FaceDetector.TAG, "---- TIME_CHECK cancelAutoFocus(): mFocusState:" + getFocusState());
        switch (getFocusState()) {
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
                doCancelFocusingState();
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                CamLog.d(FaceDetector.TAG, "Cancel autofocus : FOCUSING_SNAP_ON_FINISH : break");
            default:
                setFocusRectangleInitialize(false);
        }
    }

    private void doCancelFocusingState() {
        if (this.mGet.getApplicationMode() == 0) {
            if (!ModelProperties.isFixedFocusModel()) {
                setBlockingFaceTrFocusing(false);
            }
            if (this.mGet.getCameraDevice() != null) {
                String focusMode = this.mGet.getParameters().getFocusMode();
                if (CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
                    setCameraFocusMode("fixed");
                } else if (this.mGet.isCafSupported() || !LGT_Limit.ISP_AUTOMODE_AUTO.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
                    String defaultFocusValue = this.mGet.getDefaultFocusModeParameterForMultiWindowAF(this.mGet.getLGParam());
                    if (!(!this.mGet.isCafSupported() || focusMode == null || focusMode.equals(defaultFocusValue))) {
                        setCameraFocusMode(defaultFocusValue);
                    }
                } else {
                    setCameraFocusMode(LGT_Limit.ISP_AUTOMODE_AUTO);
                }
            }
            cancelAutoFocusAfterInitFocusArea();
        }
        setFocusRectangleInitialize();
        if (!isShutterButtonClicked()) {
            this.mGet.showIndicatorController();
            showFocus();
        }
        if (this.mGet.getApplicationMode() == 0 && !this.mGet.isSettingControllerVisible() && !this.mGet.isQuickFunctionSettingControllerShowing() && !this.mGet.isQuickFunctionDragControllerVisible()) {
            this.mGet.showQuickFunctionController();
        }
    }

    private void cancelAutoFocusAfterInitFocusArea() {
        try {
            if (this.mGet.getCameraDevice() != null && this.mGet.getLG() != null) {
                CamLog.d(FaceDetector.TAG, "#####  TIME_CHECK call getCameraDevice().cancelAutoFocus() : others, states = " + getFocusState());
                this.mGet.getCameraDevice().cancelAutoFocus();
            }
        } catch (Exception e) {
            CamLog.w(FaceDetector.TAG, "cancelAutoFocus Exception:", e);
        }
    }

    private void setCameraFocusMode(String param) {
        if (this.mGet.getCameraDevice() != null && this.mGet.getLGParam() != null) {
            LGParameters lgParameters = this.mGet.getLGParam();
            lgParameters.getParameters().setFocusMode(param);
            CamLog.d(FaceDetector.TAG, "### setFocusMode-" + param);
            this.mGet.setEnable3ALocks(lgParameters, false);
            this.mFocusArea = null;
            try {
                lgParameters.setParameters(lgParameters.getParameters());
            } catch (RuntimeException e) {
                CamLog.e(FaceDetector.TAG, "setParameters failed: " + e);
            }
        }
    }

    public boolean isShutterButtonClicked() {
        return this.mShutterButtonClicked;
    }

    public void setShutterButtonClicked(boolean clicked) {
        this.mShutterButtonClicked = clicked;
    }

    public boolean isBlockingFaceTrFocusing() {
        return this.mBlockingFaceTrFocusing;
    }

    public void setBlockingFaceTrFocusing(boolean beBlocked) {
        CamLog.d(FaceDetector.TAG, "### mBlockingFaceTrFocusing:" + this.mBlockingFaceTrFocusing + " ," + beBlocked);
        if (Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
            this.mBlockingFaceTrFocusing = beBlocked;
            if (beBlocked) {
                showFocus(false);
                return;
            } else {
                hideFocus();
                return;
            }
        }
        this.mBlockingFaceTrFocusing = false;
    }

    private void autoFocus() {
        Log.d(FaceDetector.TAG, "TIME CHECK : Touch AF [START] - autoFocus()");
        CamLog.d(FaceDetector.TAG, "autoFocus()");
        if (this.mCameraFocusView == null) {
            this.mCameraFocusView = (CameraFocusView) this.mGet.findViewById(R.id.camera_focus_view);
        }
        if (canTakePicture() && this.mFocusRect != null) {
            CamLog.d(FaceDetector.TAG, "Start autofocus.");
            if (!Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
                this.mFocusState = 1;
            } else if (this.mTouchedAFbyFaceTr) {
                this.mFocusState = 5;
            } else {
                setBlockingFaceTrFocusing(true);
                this.mFocusState = 1;
            }
            setFocusWindow(this.mFocusRect);
            updateFocusStateIndicator();
            CamLog.d(FaceDetector.TAG, "#### TIME_CHECK call CameraDevice().autoFocus().");
            try {
                this.mGet.getCameraDevice().autoFocus(this.mAutoFocusCallback);
            } catch (RuntimeException e) {
                CamLog.d(FaceDetector.TAG, "CameraDevice.autoFocus() RuntimeException: " + e);
            }
        }
    }

    public void setFocusWindow(Rect rect) {
        String sizeOnScreenString = this.mGet.getPreviewSizeOnScreen();
        int[] previewSizeOnScreen = Util.SizeString2WidthHeight(sizeOnScreenString);
        if (sizeOnScreenString == null) {
            super.onResume();
            CamLog.d(FaceDetector.TAG, "error! sizeOnScreenString is null");
        } else if (this.mGet.getCameraDevice() == null || this.mGet.getLGParam() == null) {
            CamLog.e(FaceDetector.TAG, "mCameraDevice is null");
        } else if (FunctionProperties.isTouchAfSupported(this.mGet.getApplicationMode())) {
            LGParameters lsParameters = this.mGet.getLGParam();
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
                if (this.mGet.getApplicationMode() == 0 && this.mGet.getCameraId() == 1) {
                    Rect tmprect = new Rect(rect);
                    rect.left = tmprect.right * -1;
                    rect.right = tmprect.left * -1;
                }
                if (ModelProperties.isNVIDIAChipset() && (rect.width() == 0 || rect.height() == 0)) {
                    rect.left = (this.RECTANGLE_WIDTH * -1) / 2;
                    rect.right = this.RECTANGLE_WIDTH / 2;
                    rect.top = (this.RECTANGLE_HEIGHT * -1) / 2;
                    rect.bottom = this.RECTANGLE_HEIGHT / 2;
                }
                ((Area) this.mFocusArea.get(0)).rect.left = rect.left;
                ((Area) this.mFocusArea.get(0)).rect.right = rect.right;
                ((Area) this.mFocusArea.get(0)).rect.top = rect.top;
                ((Area) this.mFocusArea.get(0)).rect.bottom = rect.bottom;
                ((Area) this.mFocusArea.get(0)).weight = 1;
                if (this.mGet.getCameraId() == 0) {
                    this.mGet.setEnable3ALocks(lsParameters, false);
                    lsParameters.getParameters().setFocusAreas(this.mFocusArea);
                    String focusMode = LGT_Limit.ISP_AUTOMODE_AUTO;
                    lsParameters.getParameters().setFocusMode(focusMode);
                    CamLog.d(FaceDetector.TAG, "### setFocusMode-" + focusMode);
                }
                lsParameters.getParameters().setMeteringAreas(this.mFocusArea);
                if (FunctionProperties.beSupportMoveCallbackFromSensor()) {
                    setContinuousFocusActive(false);
                }
                CamLog.i(FaceDetector.TAG, "setFocusWindow : left " + rect.left + " top " + rect.top);
                CamLog.i(FaceDetector.TAG, "setFocusWindow : right " + rect.right + " bottom " + rect.bottom);
                lsParameters.setParameters(lsParameters.getParameters());
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "setTouchIndexAf setParameter exception : ", e);
            }
        }
    }

    private boolean canTakePicture() {
        CamLog.i(FaceDetector.TAG, "canTakePicture() " + getFocusState());
        return isFocusStateIdle() && this.mGet.isPreviewing();
    }

    private boolean isFocusStateIdle() {
        return getFocusState() == 0;
    }

    public boolean showFocus() {
        return showFocus(true);
    }

    public boolean showFocus(boolean justDoIt) {
        CamLog.d(FaceDetector.TAG, "showFocus : " + justDoIt);
        String focus = this.mGet.getSettingValue(Setting.KEY_FOCUS);
        if (Setting.HELP_FACE_TRACKING_LED.equals(focus) && this.mGet.getSubMenuMode() == 0) {
            if (isBlockingFaceTrFocusing()) {
                super.showFocus();
            } else {
                unregisterCallback();
            }
        } else if (CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(focus)) {
            CamLog.v(FaceDetector.TAG, "showFocus : manual focus.");
            unregisterCallback();
            super.showFocus();
            return true;
        } else if (this.mGet.getSubMenuMode() == 0 || this.mGet.getSubMenuMode() == 15) {
            if (!this.mGet.isCafSupported()) {
                super.showFocus();
                return true;
            } else if (!((!justDoIt && !super.showFocus()) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA) || !FunctionProperties.isCafAnimationSupported(this.mGet.getApplicationMode(), this.mGet.getCameraId()))) {
                CamLog.v(FaceDetector.TAG, "showFocus & register ");
                registerCallback();
                return true;
            }
        }
        return false;
    }

    public boolean isTouchMoveFocusVisible() {
        if (((ImageView) this.mGet.findViewById(R.id.focus_touch_move)).getVisibility() == 0) {
            return true;
        }
        return false;
    }

    public boolean hideFocus() {
        CamLog.d(FaceDetector.TAG, "hideFocus");
        if (super.hideFocus() && ((this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) && FunctionProperties.isCafAnimationSupported(this.mGet.getApplicationMode(), this.mGet.getCameraId()))) {
            unregisterCallback();
        }
        return true;
    }

    public void startFocusByTouchPress(int x, int y, boolean bTouchedAFbyFaceTr) {
        this.mTouchedAFbyFaceTr = bTouchedAFbyFaceTr;
        startFocusByTouchPress(x, y);
    }

    public void startFocusByTouchPress(int x, int y) {
        if (checkStartFocusByTouchPress() && checkFocusPosition(x, y)) {
            cancelAutoFocusAfterInitFocusArea();
            super.setFocusState(0);
            doFocus(false);
            if (!this.mTouchedAFbyFaceTr && FunctionProperties.isFaceDetectionAuto()) {
                this.mGet.stopFaceDetection();
            }
            setFocusPosition(1);
            showFocus(false);
            setMoveNormalFocusRect(x, y);
            doFocus(true);
            this.mTouchedAFbyFaceTr = false;
            CamLog.d(FaceDetector.TAG, "------startFocusByTouchPress : x = " + x + ", y = " + y);
        }
    }

    public void startAEByTouchPress(int x, int y) {
        if (!checkStartFocusByTouchPress() || !checkFocusPosition(x, y)) {
            return;
        }
        if (this.mCameraFocusView != null) {
            this.mCameraFocusView.setVisibility(0);
            setFocusView(9);
            if (FunctionProperties.isFaceDetectionAuto()) {
                this.mGet.stopFaceDetection();
            }
            super.setFocusState(0);
            showFocus(false);
            setFocusRectangleInitialize();
            setFocusPosition(1);
            setMoveNormalFocusRect(x, y);
            setFocusWindow(this.mFocusRect);
            updateFocusStateIndicator();
            this.mGet.removeScheduledCommand(Command.RELEASE_TOUCH_FOCUS);
            this.mGet.doCommandDelayed(Command.RELEASE_TOUCH_FOCUS, ProjectVariables.keepDuration);
            CamLog.d(FaceDetector.TAG, "------startAEByTouchPress : x = " + x + ", y = " + y);
            return;
        }
        CamLog.i(FaceDetector.TAG, "TAE mCameraFocusView is null");
    }

    private boolean checkStartFocusByTouchPress() {
        if (!this.mGet.isPreviewing() || !FunctionProperties.isTouchAfSupported(this.mGet.getApplicationMode()) || ModelProperties.isFixedFocusModel() || this.mGet.checkSettingValue(Setting.KEY_LIGHT, CameraConstants.SMART_MODE_ON) || this.mGet.getApplicationMode() != 0) {
            return false;
        }
        if ((getFocusState() == 1 && !ModelProperties.is8974Chipset()) || getFocusState() == 5 || getFocusState() == 2 || this.mCameraFocusView == null) {
            return false;
        }
        return true;
    }

    private boolean checkFocusPosition(int x, int y) {
        int baseX = this.PREVIEW_MARGINE_LEFT;
        int width = baseX + this.FOCUS_GUIDE_WIDTH;
        int height = 0 + this.FOCUS_GUIDE_HEIGHT;
        if (ModelProperties.isFixedFocusModel() || !Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
            if (x < baseX || x > width || y < 0 || y > height) {
                return false;
            }
        } else if (x < baseX || x > width || y < 0 || y > height) {
            CamLog.i(FaceDetector.TAG, String.format("startFocusByTouchPress() return: %d, %d", new Object[]{Integer.valueOf(x), Integer.valueOf(y)}));
            return false;
        }
        return true;
    }

    public void initFocusAreas() {
        CamLog.d(FaceDetector.TAG, "InitFocusAreas");
        LGParameters lgParameters = this.mGet.getLGParam();
        if (ModelProperties.isJBPlusModel()) {
            if (this.mGet.getCameraId() == 0) {
                lgParameters.getParameters().setFocusAreas(null);
            }
            lgParameters.getParameters().setMeteringAreas(null);
        } else {
            if (this.mFocusArea == null) {
                this.mFocusArea = new ArrayList();
                this.mFocusArea.add(new Area(this.mFocusRect, 1));
            }
            ((Area) this.mFocusArea.get(0)).rect.left = 0;
            ((Area) this.mFocusArea.get(0)).rect.right = 0;
            ((Area) this.mFocusArea.get(0)).rect.top = 0;
            ((Area) this.mFocusArea.get(0)).rect.bottom = 0;
            ((Area) this.mFocusArea.get(0)).weight = 0;
            if (this.mGet.getCameraId() == 0) {
                lgParameters.getParameters().setFocusAreas(this.mFocusArea);
            }
            lgParameters.getParameters().setMeteringAreas(this.mFocusArea);
        }
        if (this.mGet.getCameraId() == 0) {
            this.mGet.setEnable3ALocks(lgParameters, false);
        }
        try {
            lgParameters.setParameters(lgParameters.getParameters());
        } catch (RuntimeException e) {
            CamLog.i(FaceDetector.TAG, "RuntimeException:", e);
        }
    }

    public boolean isSnapOnFinish() {
        return getFocusState() == 2;
    }

    public boolean isContinuousFocusActivating() {
        return this.mIsContinuousFocusActive;
    }

    public void setContinuousFocusActive(boolean active) {
        this.mIsContinuousFocusActive = active;
    }

    public void registerFaceTrackingCallback() {
        if (this.mGet.getCameraDevice() == null) {
            return;
        }
        if (FunctionProperties.beSupportMoveCallbackFromSensor()) {
            CamLog.d(FaceDetector.TAG, "### CameraDevice().setAutoFocusMoveCallback(callback) - for registerFaceTrackingCallback caf");
            this.mGet.getCameraDevice().setAutoFocusMoveCallback(this.mAutoFocusMoveCallback);
            return;
        }
        CamLog.d(FaceDetector.TAG, "### CameraDevice().autoFocus(callback) - for registerFaceTrackingCallback caf");
        this.mGet.getCameraDevice().autoFocus(this.mContinuousFocusCallback);
    }

    public void onDestroy() {
        if (this.mFocusArea != null) {
            this.mFocusArea.clear();
            this.mFocusArea = null;
        }
        if (this.mCameraFocusView != null) {
            this.mCameraFocusView = null;
        }
        if (this.mCameraMultiWindowAFView != null) {
            this.mCameraMultiWindowAFView.destroyMultiWindowAFGuide();
            this.mCameraMultiWindowAFView = null;
        }
        this.mFocusRect = null;
        this.mAutoFocusCallback = null;
        this.mAutoFocusMoveCallback = null;
        this.mContinuousFocusCallback = null;
        this.mAutoFocusOnCafCallback = null;
        super.onDestroy();
    }

    public void callbackOnAutoFocus(boolean focused, Camera camera) {
        CamLog.d(FaceDetector.TAG, "##### AutoFocusCallback():" + focused);
        Log.d(FaceDetector.TAG, "TIME CHECK : Touch AF [END] - callbackOnAutoFocus ()? " + (focused ? "FOCUS_SUCCESS" : "FOCUS_FAIL"));
        if (checkMediator() && this.mGet.isPreviewing() && this.mGet.getCameraDevice() != null && !this.mGet.isPausing()) {
            int focusState = getFocusState();
            String shotMode = this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE);
            if (this.mGet.getApplicationMode() == 0 && !checkFlashModeOff()) {
                if (CameraConstants.TYPE_SHOTMODE_NORMAL.equals(shotMode) || CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(shotMode) || CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY.equals(shotMode) || this.mGet.isTimeMachineModeOn()) {
                    setFlashMode(this.mGet.getFlashEnableForShotMode() ? this.mGet.getSettingValue(Setting.KEY_FLASH) : CameraConstants.SMART_MODE_OFF);
                } else {
                    setFlashMode(CameraConstants.SMART_MODE_OFF);
                }
            }
            this.mGet.removeScheduledCommand(Command.RELEASE_TOUCH_FOCUS);
            if (!this.mGet.isShutterFocusLongKey()) {
                this.mGet.doCommandDelayed(Command.RELEASE_TOUCH_FOCUS, ProjectVariables.keepDuration);
            }
            if (focusState == 2) {
                doFocusing(focused);
                if ((shotMode.equals(CameraConstants.TYPE_SHOTMODE_NORMAL) || shotMode.equals(CameraConstants.TYPE_PREFERENCE_NOT_FOUND) || this.mGet.isTimeMachineModeOn()) && (this.mGet.getSettingValue(Setting.KEY_CAMERA_TIMER).equals("0") || this.mGet.getTimerCaptureDelay() <= 0)) {
                    this.mGet.doCommand(Command.DO_CAPTURE);
                } else {
                    this.mGet.doCommandDelayed(Command.DO_CAPTURE, 100);
                }
            } else if (focusState == 1 || focusState == 5) {
                doFocusing(focused);
            } else if (focusState == 0) {
                updateFocusStateIndicator();
            }
            CamLog.d(FaceDetector.TAG, "AutoFocusCallback ------------------");
            CamLog.d(FaceDetector.TAG, "getFocusState() [" + getFocusState() + "]");
        }
    }

    private void doFocusing(boolean focused) {
        int i = 3;
        boolean isFaceTracking = Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS));
        if (focused) {
            CamLog.i(FaceDetector.TAG, "FOCUS_SUCCESS");
            if (!this.mGet.isTimerShotCountdown()) {
                this.mGet.playAFSound(true);
            }
            if (isBlockingFaceTrFocusing()) {
                setFocusState(3);
            } else {
                if (isFaceTracking) {
                    i = 6;
                }
                setFocusState(i);
            }
            if (this.mGet.isShutterFocusLongKey()) {
                this.mGet.setEnable3ALocks(null, true);
            }
        } else {
            CamLog.i(FaceDetector.TAG, "FOCUS_FAIL");
            if (!this.mGet.isTimerShotCountdown()) {
                this.mGet.playAFSound(false);
            }
            if (isBlockingFaceTrFocusing()) {
                setFocusState(4);
            } else {
                setFocusState(isFaceTracking ? 7 : 4);
            }
        }
        CamLog.d(FaceDetector.TAG, "mFocusPosition = " + this.mFocusPosition);
        updateFocusStateIndicator();
    }

    public void setFlashMode(String value) {
        if (this.mGet.getCameraDevice() != null) {
            LGParameters lgParameters = this.mGet.getLGParam();
            lgParameters.getParameters().set("flash-mode", value);
            try {
                lgParameters.setParameters(lgParameters.getParameters());
            } catch (RuntimeException e) {
                CamLog.e(FaceDetector.TAG, "setParameters failed: " + e);
            }
        }
    }

    private boolean checkFlashModeOff() {
        String currentSetting = this.mGet.getSettingValue(Setting.KEY_FLASH);
        return currentSetting.equals(CameraConstants.SMART_MODE_OFF) || currentSetting.equals(CameraConstants.TYPE_PREFERENCE_NOT_FOUND) || this.mGet.getSettingValue(Setting.KEY_SMART_MODE).equals(CameraConstants.SMART_MODE_ON);
    }

    static {
        mPreviousFocusedState = 0;
    }

    public void callbackOnCAFocus(boolean focusedState, Camera camera) {
        CamLog.d(FaceDetector.TAG, "onContinuousFocus()");
        if (checkMediator() && !this.mGet.isPausing() && this.mGet.getCameraDevice() != null && this.mGet.getLG() != null && this.mGet.isPreviewing() && isInitialized() && this.mGet.getSubMenuMode() == 0 && !isTouchMoveFocusVisible()) {
            CamLog.d(FaceDetector.TAG, "### onContinuousFocus():" + focusedState);
            if (!ModelProperties.isMTKChipset() || (!(focusedState && mPreviousFocusedState == 9) && (focusedState || mPreviousFocusedState != 8))) {
                if (focusedState) {
                    setFocusState(9);
                    if (ModelProperties.isMTKChipset()) {
                        mPreviousFocusedState = 9;
                    }
                } else {
                    setFocusState(8);
                    if (ModelProperties.isMTKChipset()) {
                        mPreviousFocusedState = 8;
                    }
                }
                setContinuousFocusActive(true);
                showFocus(false);
                updateFocusStateIndicator();
                this.mGet.postOnUiThread(new Runnable() {
                    public void run() {
                        if (CameraFocusController.this.checkMediator()) {
                            CamLog.d(FaceDetector.TAG, "snapshotOnContinuousFocus mDoSnapRunnable!");
                            CameraFocusController.this.mGet.removePostRunnable(this);
                            CameraFocusController.this.setContinuousFocusActive(false);
                            if (CameraFocusController.this.mGet.checkPreviewPanelController() && CameraFocusController.this.mGet.snapshotOnContinuousFocus() && CameraFocusController.this.mGet.getHandler() != null) {
                                CameraFocusController.this.mGet.getHandler().post(CameraFocusController.this.mGet.getSnapshotRunnable());
                            }
                        }
                    }
                }, 800);
                return;
            }
            mPreviousFocusedState = 0;
            CamLog.v(FaceDetector.TAG, "### onContinuousFocus(): skipped");
        }
    }

    public void callbackOnAutoFocusMove(final boolean start, Camera camera) {
        CamLog.d(FaceDetector.TAG, "onAutoFocusMoving + " + start + " called. Could be returned without any action.");
        if (checkMediator() && !this.mGet.isPausing() && this.mGet.getCameraDevice() != null && this.mGet.getLG() != null && this.mGet.isPreviewing() && isInitialized()) {
            if (this.mGet.getSubMenuMode() == 0 || this.mGet.snapshotOnContinuousFocus()) {
                String focusMode = this.mGet.getLGParam().getParameters().getFocusMode();
                if (Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
                    if (start) {
                        initFocusAreas();
                    }
                } else if ((!start || !getCafOnGoing()) && !isTouchMoveFocusVisible()) {
                    if (focusMode.equals(LGT_Limit.ISP_AUTOMODE_AUTO)) {
                        CamLog.d(FaceDetector.TAG, "focus mode is FOCUS_MODE_AUTO");
                        setCafOnGoing(false);
                        setContinuousFocusActive(false);
                        return;
                    }
                    this.mGet.removeScheduledCommand(Command.RELEASE_TOUCH_FOCUS);
                    if (start) {
                        initFocusAreas();
                        setCafOnGoing(true);
                        if (checkMultiWindowAFCondition()) {
                            setFocusState(11);
                        } else {
                            setFocusState(8);
                        }
                        showFocus(false);
                        setContinuousFocusActive(true);
                    } else {
                        setCafOnGoing(false);
                        if (checkMultiWindowAFCondition()) {
                            setFocusState(12);
                        } else {
                            setFocusState(9);
                        }
                        this.mGet.doCommandDelayed(Command.RELEASE_TOUCH_FOCUS, ProjectVariables.keepDuration);
                        setContinuousFocusActive(false);
                    }
                    CamLog.d(FaceDetector.TAG, "### onAutoFocusMoving(): " + (start ? "START" : "END"));
                    updateFocusStateIndicator(this.mGet.getLGParam().getMultiWindowFocusAreas());
                    if (!FunctionProperties.isSupportAFonCAF()) {
                        this.mGet.postOnUiThread(new Runnable() {
                            public void run() {
                                if (CameraFocusController.this.checkMediator()) {
                                    CameraFocusController.this.mGet.removePostRunnable(this);
                                    if (CameraFocusController.this.mGet.checkPreviewPanelController() && CameraFocusController.this.mGet.snapshotOnContinuousFocus() && !start && CameraFocusController.this.mGet.getHandler() != null) {
                                        CamLog.d(FaceDetector.TAG, "snapshotOnContinuousFocus mDoSnapRunnable!");
                                        CameraFocusController.this.mGet.getHandler().post(CameraFocusController.this.mGet.getSnapshotRunnable());
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    public void callbackAutoFocusOnCaf(boolean success, Camera camera) {
        CamLog.d(FaceDetector.TAG, "##### AutoFocusOnCafCallback():" + success);
        if (checkMediator() && this.mGet.isPreviewing() && this.mGet.getCameraDevice() != null && this.mGet.getLG() != null && !this.mGet.isPausing()) {
            if (Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
                this.mGet.startFaceDetection(true);
            }
            this.mGet.postOnUiThread(new Runnable() {
                public void run() {
                    if (CameraFocusController.this.checkMediator()) {
                        CameraFocusController.this.mGet.removePostRunnable(this);
                        if (CameraFocusController.this.mGet.checkPreviewPanelController() && CameraFocusController.this.mGet.snapshotOnContinuousFocus() && CameraFocusController.this.mGet.getHandler() != null) {
                            CamLog.d(FaceDetector.TAG, "mDoSnapRunnable by AutoFocusOnCaf Callback!");
                            CameraFocusController.this.mGet.getHandler().post(CameraFocusController.this.mGet.getSnapshotRunnable());
                        }
                    }
                }
            });
        }
    }
}
