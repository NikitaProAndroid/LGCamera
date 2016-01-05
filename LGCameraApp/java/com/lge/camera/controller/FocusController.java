package com.lge.camera.controller;

import android.graphics.Rect;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.AutoFocusMoveCallback;
import android.hardware.Camera.Size;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.CameraFocusView;
import com.lge.camera.components.CameraMultiWindowAFView;
import com.lge.camera.components.RotateImageButton;
import com.lge.camera.components.RotateView;
import com.lge.camera.listeners.ObjectTrackingFocusCallback;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.Util;
import com.lge.hardware.LGCamera;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Limit;
import com.lge.olaworks.define.Ola_Exif.ThumbNailSize;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.BaseEngine;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.util.List;

public abstract class FocusController extends Controller {
    public static final int FOCUS_POSITION_DEFAULT = 0;
    public static final int FOCUS_POSITION_FACE = 2;
    public static final int FOCUS_POSITION_TOUCH = 1;
    public final int FOCUS_GUIDE_HEIGHT;
    public final int FOCUS_GUIDE_WIDTH;
    public final int PREVIEW_MARGINE_LEFT;
    public final int RECTANGLE_HEIGHT;
    public final int RECTANGLE_MARGINE_LEFT;
    public final int RECTANGLE_MARGINE_TOP;
    public final int RECTANGLE_WIDTH;
    private Runnable continuousFocus;
    private boolean isCafOnGoing;
    protected AutoFocusCallback mAutoFocusCallback;
    protected AutoFocusMoveCallback mAutoFocusMoveCallback;
    protected AutoFocusCallback mAutoFocusOnCafCallback;
    protected CameraFocusView mCameraFocusView;
    protected CameraMultiWindowAFView mCameraMultiWindowAFView;
    protected AutoFocusCallback mContinuousFocusCallback;
    boolean mFailShowFocusBeforeInit;
    public int mFocusAreaHeight;
    public int mFocusAreaLeftMargin;
    public int mFocusAreaTopMargin;
    public int mFocusAreaWidth;
    protected int mFocusPosition;
    protected Rect mFocusRect;
    protected int mFocusState;
    private boolean mIsRegisterObjectCallback;
    private int mObjectState;
    protected ObjectTrackingFocusCallback mObjectTrackingFocusCallback;
    public boolean mRegister;
    protected boolean mTouchedAFbyFaceTr;
    private float scaleRatio;

    public abstract void doFocus(boolean z);

    public FocusController(ControllerFunction function) {
        super(function);
        this.RECTANGLE_WIDTH = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.focus_rectangle_width);
        this.RECTANGLE_HEIGHT = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.focus_rectangle_height);
        this.RECTANGLE_MARGINE_LEFT = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.focus_rectangle_marginLeft);
        this.RECTANGLE_MARGINE_TOP = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.focus_rectangle_marginTop);
        this.PREVIEW_MARGINE_LEFT = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.layout_preview_marginLeft);
        this.FOCUS_GUIDE_WIDTH = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.focus_guide_width);
        this.FOCUS_GUIDE_HEIGHT = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.focus_guide_height);
        this.mFocusState = FOCUS_POSITION_DEFAULT;
        this.mFailShowFocusBeforeInit = false;
        this.mFocusPosition = FOCUS_POSITION_DEFAULT;
        this.mFocusAreaWidth = FOCUS_POSITION_DEFAULT;
        this.mFocusAreaHeight = FOCUS_POSITION_DEFAULT;
        this.mFocusAreaLeftMargin = FOCUS_POSITION_DEFAULT;
        this.mFocusAreaTopMargin = FOCUS_POSITION_DEFAULT;
        this.mFocusRect = new Rect();
        this.mTouchedAFbyFaceTr = false;
        this.mRegister = false;
        this.isCafOnGoing = false;
        this.continuousFocus = new Runnable() {
            public void run() {
                if (FocusController.this.checkMediator()) {
                    FocusController.this.mGet.removePostRunnable(this);
                    String focus = FocusController.this.mGet.getSettingValue(Setting.KEY_FOCUS);
                    if ((LGT_Limit.ISP_AUTOMODE_AUTO.equals(focus) || (CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(focus) && FocusController.this.mGet.isCafSupported())) && FocusController.this.mCameraFocusView != null) {
                        FocusController.this.mFocusState = 9;
                        FocusController.this.updateFocusStateIndicator();
                    }
                }
            }
        };
        this.mObjectState = FOCUS_POSITION_DEFAULT;
        this.mIsRegisterObjectCallback = false;
    }

    public void initController() {
        CamLog.d(FaceDetector.TAG, "FocusController init-start:" + this.mGet.getApplicationMode());
        this.mGet.inflateStub(R.id.stub_focus);
        this.mCameraFocusView = (CameraFocusView) this.mGet.findViewById(R.id.camera_focus_view);
        this.mCameraMultiWindowAFView = (CameraMultiWindowAFView) this.mGet.findViewById(R.id.multifocus_af_parent);
        initMultiWindowAFView();
        if (ModelProperties.isFixedFocusModel() || FunctionProperties.isCafAnimationSupported(FOCUS_POSITION_DEFAULT, FOCUS_POSITION_DEFAULT) || this.mGet.getApplicationMode() == FOCUS_POSITION_TOUCH) {
            this.mCameraFocusView.setVisibility(4);
        }
        CamLog.i(FaceDetector.TAG, "visible? " + this.mCameraFocusView.getVisibility());
        setFocusView(FOCUS_POSITION_DEFAULT);
        this.mInit = true;
        if (this.mFailShowFocusBeforeInit) {
            if (!(this.mGet.getSubMenuMode() != 0 || Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)) || CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)) || !showFocus() || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA))) {
                CamLog.d(FaceDetector.TAG, "### showFocus & register ");
                registerCallback();
            }
            this.mFailShowFocusBeforeInit = false;
        }
        CamLog.d(FaceDetector.TAG, "FocusController init-end");
    }

    public void reInitialize() {
        super.reInitialize();
        if (this.mCameraMultiWindowAFView != null) {
            this.mCameraMultiWindowAFView.destroyMultiWindowAFGuide();
            this.mCameraMultiWindowAFView = null;
        }
    }

    public void setFocusAreaWindow(int width, int height, int leftMargin) {
        this.mFocusAreaWidth = width;
        this.mFocusAreaHeight = height;
        this.mFocusAreaLeftMargin = leftMargin;
        this.mFocusAreaTopMargin = (CameraConstants.LCD_SIZE_HEIGHT - this.mFocusAreaHeight) / FOCUS_POSITION_FACE;
        if (FunctionProperties.isFrontTouchAESupported() && this.mGet.getApplicationMode() == 0) {
            ImageView v = (ImageView) this.mGet.findViewById(R.id.focus_touch_move);
            if (this.mGet.getCameraId() == FOCUS_POSITION_TOUCH) {
                v.setBackgroundResource(R.drawable.focus_touch_ae);
            } else {
                v.setBackgroundResource(R.drawable.focus_guide);
            }
        }
    }

    public void setFocusView(int state) {
        if (this.mInit) {
            if (FunctionProperties.isFrontTouchAESupported() && this.mGet.getApplicationMode() == 0 && this.mGet.getCameraId() == FOCUS_POSITION_TOUCH) {
                state = 9;
            }
            this.mCameraFocusView.setState(state);
        }
    }

    public void removeContinuousFocusRunnable() {
        this.mGet.removePostRunnable(this.continuousFocus);
    }

    public void setFocusState(int state) {
        if (FunctionProperties.beSupportMoveCallbackFromSensor()) {
            this.mFocusState = state;
        } else if (state == 9) {
            this.mGet.removeScheduledCommand(Command.RELEASE_TOUCH_FOCUS);
            this.mGet.doCommandDelayed(Command.RELEASE_TOUCH_FOCUS, ProjectVariables.keepDuration);
            this.mGet.removePostRunnable(this.continuousFocus);
            String focus = this.mGet.getSettingValue(Setting.KEY_FOCUS);
            if (LGT_Limit.ISP_AUTOMODE_AUTO.equals(focus) || (CameraConstants.TYPE_PREFERENCE_NOT_FOUND.equals(focus) && this.mGet.isCafSupported())) {
                this.mGet.postOnUiThread(this.continuousFocus, 800);
                updateFocusStateIndicator(8, null);
            }
        } else {
            this.mGet.removePostRunnable(this.continuousFocus);
            this.mFocusState = state;
        }
    }

    public int getFocusState() {
        if (ModelProperties.isFixedFocusModel()) {
            return 4;
        }
        return this.mFocusState;
    }

    public boolean showFocus() {
        CamLog.d(FaceDetector.TAG, "showFocus");
        if (ModelProperties.isFixedFocusModel()) {
            CamLog.d(FaceDetector.TAG, "showFocus, return: is fixed focus");
            return false;
        } else if (this.mGet.getVideoState() == 3 || this.mGet.getVideoState() == 4) {
            CamLog.d(FaceDetector.TAG, "showFocus, return: " + this.mGet.getVideoState());
            return false;
        } else if (this.mGet.getApplicationMode() == FOCUS_POSITION_TOUCH && !FunctionProperties.isCafAnimationSupported(FOCUS_POSITION_TOUCH, this.mGet.getCameraId())) {
            CamLog.d(FaceDetector.TAG, "showFocus, return: mode is camcorder");
            return false;
        } else if (!this.mGet.isNullSettingView() && !this.mGet.isSettingViewRemoving()) {
            CamLog.d(FaceDetector.TAG, "settingview is not null && is not Removing");
            return false;
        } else if (!this.mInit) {
            CamLog.i(FaceDetector.TAG, "mInit is false, return");
            this.mFailShowFocusBeforeInit = true;
            return false;
        } else if (this.mGet.getCameraMode() == FOCUS_POSITION_TOUCH) {
            CamLog.d(FaceDetector.TAG, "front camera, return");
            return false;
        } else if (this.mGet.getApplicationMode() == 0 && (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA))) {
            CamLog.d(FaceDetector.TAG, "dual camera / panorama mode, return");
            return false;
        } else {
            CamLog.i(FaceDetector.TAG, "showFocus - visible, mFocusPosition = " + this.mFocusPosition);
            if (this.mCameraMultiWindowAFView == null || this.mCameraFocusView == null) {
                CamLog.d(FaceDetector.TAG, "mCameraMultiWindowAFView or mCameraFocusView is null!");
            } else {
                CamLog.d(FaceDetector.TAG, "shotMode = " + this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE));
                if (checkMultiWindowAFCondition()) {
                    this.mCameraMultiWindowAFView.setVisibility(FOCUS_POSITION_DEFAULT);
                    this.mCameraFocusView.clearAnimation();
                    this.mCameraFocusView.setVisibility(8);
                } else {
                    this.mCameraMultiWindowAFView.setBackgroundDefault();
                    this.mCameraMultiWindowAFView.clearAnimation();
                    this.mCameraMultiWindowAFView.setVisibility(8);
                    this.mCameraFocusView.setVisibility(FOCUS_POSITION_DEFAULT);
                    updateFocusStateIndicator();
                }
            }
            return true;
        }
    }

    public boolean isFocusViewVisible() {
        if ((this.mCameraFocusView == null || this.mCameraFocusView.getVisibility() != 0) && (this.mCameraMultiWindowAFView == null || this.mCameraMultiWindowAFView.getVisibility() != 0)) {
            return false;
        }
        return true;
    }

    public boolean hideFocus() {
        CamLog.d(FaceDetector.TAG, "hideFocus-init");
        if (this.mInit) {
            CamLog.d(FaceDetector.TAG, "hideFocus-gone");
            if (!(this.mCameraMultiWindowAFView == null || this.mCameraFocusView == null)) {
                this.mCameraMultiWindowAFView.setBackgroundDefault();
                this.mCameraMultiWindowAFView.clearAnimation();
                this.mCameraMultiWindowAFView.setVisibility(8);
                this.mCameraFocusView.clearAnimation();
                this.mCameraFocusView.setVisibility(8);
            }
            return true;
        }
        CamLog.i(FaceDetector.TAG, "mInit is false, return");
        return false;
    }

    public void updateFocusStateIndicator(List<Area> areaList) {
        updateFocusStateIndicator(getFocusState(), areaList);
    }

    public void updateFocusStateIndicator() {
        updateFocusStateIndicator(getFocusState(), null);
    }

    public void updateFocusStateIndicator(int focusState, List<Area> areaList) {
        CamLog.d(FaceDetector.TAG, "updateFocusIndicator() : " + getFocusState());
        if (isFocusIndicatorUpdateAvailable()) {
            this.mFocusState = focusState;
            switch (focusState) {
                case FOCUS_POSITION_DEFAULT /*0*/:
                case FOCUS_POSITION_FACE /*2*/:
                    setFocusView(FOCUS_POSITION_DEFAULT);
                case FOCUS_POSITION_TOUCH /*1*/:
                    if (checkMultiWindowAFCondition()) {
                        this.mCameraMultiWindowAFView.clearAnimation();
                        this.mCameraMultiWindowAFView.setCenterWindowVisibility(FOCUS_POSITION_DEFAULT);
                    } else if (this.mFocusPosition == FOCUS_POSITION_TOUCH) {
                        setFocusView(6);
                    } else {
                        setFocusView(FOCUS_POSITION_DEFAULT);
                    }
                case LGKeyRec.EVENT_STARTED /*3*/:
                    if (checkMultiWindowAFCondition()) {
                        this.mCameraMultiWindowAFView.setCenterWindowStatus(12);
                    } else if (this.mFocusPosition == FOCUS_POSITION_TOUCH) {
                        setFocusView(7);
                    } else {
                        setFocusView(FOCUS_POSITION_TOUCH);
                    }
                case LGKeyRec.EVENT_STOPPED /*4*/:
                    if (checkMultiWindowAFCondition()) {
                        this.mCameraMultiWindowAFView.setCenterWindowStatus(13);
                    } else if (this.mFocusPosition == FOCUS_POSITION_TOUCH) {
                        setFocusView(8);
                    } else {
                        setFocusView(FOCUS_POSITION_FACE);
                    }
                case LGKeyRec.EVENT_NEED_MORE_AUDIO /*8*/:
                    setMoveNormalFocusRectCenter();
                    setFocusView(3);
                case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
                    setFocusView(4);
                case BaseEngine.DEFAULT_PRIORITY /*10*/:
                    if (FunctionProperties.beSupportMoveCallbackFromSensor()) {
                        setClearFocusAnimation();
                    }
                    setFocusView(5);
                case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
                case Ola_ShotParam.ImageEffect_Solarize /*12*/:
                    if (checkMultiWindowAFCondition() && areaList != null) {
                        updateMultiWindowAFGuide(focusState, areaList);
                    }
                default:
                    CamLog.d(FaceDetector.TAG, "Wrong focus state or cannot update indicator!");
            }
        }
    }

    public boolean isFocusIndicatorUpdateAvailable() {
        if (this.mGet.getApplicationMode() == FOCUS_POSITION_TOUCH && FunctionProperties.isCafAnimationSupported(FOCUS_POSITION_TOUCH, this.mGet.getCameraId())) {
            return false;
        }
        if (this.mGet.getApplicationMode() == 0 && this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) {
            CamLog.d(FaceDetector.TAG, "updateFocusStateIndicator - dual camera mode, return");
            return false;
        } else if (this.mCameraFocusView == null || this.mCameraMultiWindowAFView == null) {
            return false;
        } else {
            return true;
        }
    }

    public void updateMultiWindowAFGuide(int focusState, List<Area> areaList) {
        if (focusState == 12) {
            this.mCameraMultiWindowAFView.clearAnimation();
            this.mCameraMultiWindowAFView.setList(areaList);
            this.mCameraMultiWindowAFView.update();
            if (areaList != null && ((Area) areaList.get(areaList.size() - 1)).weight == FOCUS_POSITION_FACE) {
                this.mGet.removeScheduledCommand(Command.RELEASE_TOUCH_FOCUS);
                this.mGet.doCommandDelayed(Command.RELEASE_TOUCH_FOCUS, CameraConstants.FREE_PANO_TIME_REMOVE_GUIDE);
            }
        } else if (focusState != 11) {
        } else {
            if (((Integer) this.mGet.getParameters().getZoomRatios().get(this.mGet.getParameters().getZoom())).intValue() >= Limit.OLA_FIFO_DATA_MAX_SIZE_BYTE) {
                this.mCameraMultiWindowAFView.clearAnimation();
                this.mCameraMultiWindowAFView.setCenterWindowVisibility(FOCUS_POSITION_DEFAULT);
                return;
            }
            this.mCameraMultiWindowAFView.setBackgroundDefault();
        }
    }

    public void registerCallback() {
        CamLog.d(FaceDetector.TAG, "mRegister = " + this.mRegister);
        if (checkFocusResigerCallBack()) {
            try {
                if (!ModelProperties.isSamsungModel() && FunctionProperties.beSupportCafCallbackFromSensor() && this.mGet.getCameraDevice() != null) {
                    String focusMode = this.mGet.getParameters().getFocusMode();
                    if (!this.mGet.getInCaptureProgress() && focusMode != null) {
                        if (!focusMode.equals("continuous-video") && !focusMode.equals("continuous-picture") && !focusMode.equals(CameraConstants.FOCUS_MODE_MULTIWINDOWAF)) {
                            return;
                        }
                        if (!FunctionProperties.beSupportMoveCallbackFromSensor()) {
                            CamLog.d(FaceDetector.TAG, "### CameraDevice().autoFocus(callback) - for registerCallback caf");
                            this.mGet.getCameraDevice().autoFocus(this.mContinuousFocusCallback);
                        } else if (!this.mRegister) {
                            this.mGet.getCameraDevice().setAutoFocusMoveCallback(this.mAutoFocusMoveCallback);
                            CamLog.d(FaceDetector.TAG, "### CameraDevice().setAutoFocusMoveCallback(callback) - for registerCallback caf");
                            this.mRegister = true;
                        }
                    }
                }
            } catch (NoSuchMethodError e) {
                CamLog.e(FaceDetector.TAG, String.format("Continuous focus not supported!", new Object[FOCUS_POSITION_DEFAULT]));
                e.printStackTrace();
            } catch (RuntimeException e2) {
                CamLog.e(FaceDetector.TAG, String.format("Continuous focus not supported!", new Object[FOCUS_POSITION_DEFAULT]));
                e2.printStackTrace();
            }
        }
    }

    public void unregisterCallback() {
        CamLog.d(FaceDetector.TAG, "mRegister = " + this.mRegister);
        if (this.mRegister) {
            if (this.mGet.getCameraDevice() != null) {
                CamLog.d(FaceDetector.TAG, "### CameraDevice().setAutoFocusMoveCallback(null) - for unRegisterCallback caf");
                this.mGet.getCameraDevice().setAutoFocusMoveCallback(null);
            }
            this.mRegister = false;
            setCafOnGoing(false);
        }
        if (checkFocusResigerCallBack()) {
            try {
                if (!ModelProperties.isSamsungModel() && FunctionProperties.beSupportCafCallbackFromSensor()) {
                    String focusMode = this.mGet.getParameters().getFocusMode();
                    if (!this.mGet.getInCaptureProgress() && focusMode != null) {
                        if (!focusMode.equals("continuous-video") && !focusMode.equals("continuous-picture") && !focusMode.equals(CameraConstants.FOCUS_MODE_MULTIWINDOWAF)) {
                            return;
                        }
                        if (!FunctionProperties.beSupportMoveCallbackFromSensor()) {
                            CamLog.d(FaceDetector.TAG, "### CameraDevice().autoFocus(null) - for unRegisterCallback caf");
                            this.mGet.getCameraDevice().autoFocus(null);
                        } else if (this.mRegister) {
                            this.mGet.getCameraDevice().setAutoFocusMoveCallback(null);
                            CamLog.d(FaceDetector.TAG, "### CameraDevice().setAutoFocusMoveCallback(null) - for unRegisterCallback caf");
                            this.mRegister = false;
                            setCafOnGoing(false);
                        }
                    }
                }
            } catch (NoSuchMethodError e) {
                CamLog.e(FaceDetector.TAG, String.format("Continuous focus not supported!", new Object[FOCUS_POSITION_DEFAULT]));
                e.printStackTrace();
            } catch (RuntimeException e2) {
                CamLog.e(FaceDetector.TAG, String.format("Continuous focus not supported!", new Object[FOCUS_POSITION_DEFAULT]));
                e2.printStackTrace();
            }
        }
    }

    public boolean checkFocusResigerCallBack() {
        if (checkMediator()) {
            try {
                if (!this.mGet.checkPreviewController()) {
                    CamLog.d(FaceDetector.TAG, "previewcontroller is null, return");
                    return false;
                } else if (this.mGet.isPreviewOnGoing()) {
                    CamLog.d(FaceDetector.TAG, "Preview is not started yet, return");
                    return false;
                } else if (this.mGet.getCameraDevice() != null && this.mGet.getParameters() != null) {
                    return true;
                } else {
                    CamLog.d(FaceDetector.TAG, "device or parameter is null, return");
                    return false;
                }
            } catch (RuntimeException e) {
                CamLog.e(FaceDetector.TAG, "RuntimeException : ", e);
                return false;
            }
        }
        CamLog.d(FaceDetector.TAG, "mediator is null, return");
        return false;
    }

    public void setClearFocusAnimation() {
        if (this.mCameraFocusView != null) {
            this.mCameraFocusView.clearAnimation();
        }
    }

    public void setMoveNormalFocusRect(int x, int y) {
        int left = x - (this.RECTANGLE_WIDTH / FOCUS_POSITION_FACE);
        int top = y - (this.RECTANGLE_HEIGHT / FOCUS_POSITION_FACE);
        int right = x + (this.RECTANGLE_WIDTH / FOCUS_POSITION_FACE);
        int bottom = y + (this.RECTANGLE_HEIGHT / FOCUS_POSITION_FACE);
        if (left <= this.mFocusAreaLeftMargin) {
            left = this.mFocusAreaLeftMargin;
            right = this.mFocusAreaLeftMargin + this.RECTANGLE_WIDTH;
        }
        if (top <= 0) {
            top = FOCUS_POSITION_DEFAULT;
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
        if (this.mTouchedAFbyFaceTr) {
            CamLog.i(FaceDetector.TAG, "setMoveNormalFocusRect - No need to start animation");
            return;
        }
        float pivotX;
        float pivotY;
        if (this.mGet.isConfigureLandscape()) {
            pivotX = (float) (x - left);
            pivotY = (float) (y - top);
        } else {
            pivotX = (float) (bottom - y);
            pivotY = (float) (x - left);
        }
        startGuideViewAnimation(pivotX, pivotY);
        CamLog.d(FaceDetector.TAG, "x = " + x + " / y = " + y + " / left = " + left + " / top = " + top + " / x - left = " + (x - left) + " / y - top  = " + (y - top));
    }

    public void startGuideViewAnimation(float pivotX, float pivotY) {
        float scaleStart;
        if (getFocusState() == 8 || getFocusState() == 11) {
            scaleStart = 1.5f;
        } else {
            scaleStart = RotateView.DEFAULT_TEXT_SCALE_X;
        }
        float scaleEnd = (getFocusState() == 8 || getFocusState() == 11) ? RotateView.DEFAULT_TEXT_SCALE_X : 0.59f;
        ScaleAnimation mAniFocusScale = new ScaleAnimation(scaleStart, scaleEnd, scaleStart, scaleEnd, pivotX, pivotY);
        AlphaAnimation mAniFocusAlpha = new AlphaAnimation(0.25f, RotateView.DEFAULT_TEXT_SCALE_X);
        AnimationSet aniSet = new AnimationSet(true);
        aniSet.addAnimation(mAniFocusScale);
        aniSet.addAnimation(mAniFocusAlpha);
        aniSet.setFillAfter(true);
        aniSet.setDuration(300);
        aniSet.setInterpolator(new AccelerateDecelerateInterpolator());
        if (checkMultiWindowAFCondition()) {
            this.mCameraMultiWindowAFView.startAnimation(aniSet);
        } else {
            this.mCameraFocusView.startAnimation(aniSet);
        }
    }

    public void setMoveNormalFocusRectCenter() {
        int x;
        int[] previewSizeOnScreen = Util.SizeString2WidthHeight(this.mGet.getPreviewSizeOnScreen());
        if (previewSizeOnScreen[FOCUS_POSITION_DEFAULT] > Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.layout_preview_width)) {
            x = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.lcd_width) / FOCUS_POSITION_FACE;
        } else {
            x = getPreviewLeftMargin() + (previewSizeOnScreen[FOCUS_POSITION_DEFAULT] / FOCUS_POSITION_FACE);
        }
        setMoveNormalFocusRect(x, this.RECTANGLE_MARGINE_TOP + (this.RECTANGLE_HEIGHT / FOCUS_POSITION_FACE));
    }

    private int getPreviewLeftMargin() {
        LayoutParams previewParam = (LayoutParams) this.mGet.findViewById(R.id.preview).getLayoutParams();
        if (this.mGet.isConfigureLandscape()) {
            return previewParam.leftMargin;
        }
        return previewParam.topMargin;
    }

    public void clearFocusState() {
        CamLog.d(FaceDetector.TAG, "clearFocusState");
        this.mFocusState = FOCUS_POSITION_DEFAULT;
        setFocusPosition(FOCUS_POSITION_DEFAULT);
        updateFocusStateIndicator();
        this.mGet.removeScheduledCommand(Command.RELEASE_TOUCH_FOCUS);
        if (FunctionProperties.beSupportMoveCallbackFromSensor()) {
            setCafOnGoing(false);
            setContinuousFocusActive(false);
        }
    }

    public void setFocusRectangleInitialize() {
        setFocusRectangleInitialize(true);
    }

    public void setFocusRectangleInitialize(boolean isRemoveCafRunable) {
        CamLog.d(FaceDetector.TAG, "setFocusRectangleInitialize isRemoveCAFrunable=" + isRemoveCafRunable);
        if (isRemoveCafRunable) {
            removeContinuousFocusRunnable();
        }
        clearFocusState();
        setClearFocusAnimation();
        int marginLeft = this.RECTANGLE_MARGINE_LEFT;
        int marginTop = this.RECTANGLE_MARGINE_TOP;
        int lcdWidth = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.lcd_width);
        int leftMargin = getPixelFromDimens(R.dimen.layout_preview_marginLeft);
        int previewWidth = getPixelFromDimens(R.dimen.layout_preview_width);
        int[] previewSizeOnScreen = Util.SizeString2WidthHeight(this.mGet.getPreviewSizeOnScreen());
        if (previewSizeOnScreen[FOCUS_POSITION_DEFAULT] > previewWidth) {
            marginLeft = (lcdWidth - this.RECTANGLE_WIDTH) / FOCUS_POSITION_FACE;
        } else if (previewSizeOnScreen[FOCUS_POSITION_DEFAULT] > previewWidth - leftMargin) {
            leftMargin = (previewSizeOnScreen[FOCUS_POSITION_DEFAULT] - this.RECTANGLE_WIDTH) / FOCUS_POSITION_FACE;
        } else {
            leftMargin += (previewSizeOnScreen[FOCUS_POSITION_DEFAULT] - this.RECTANGLE_WIDTH) / FOCUS_POSITION_FACE;
        }
        setFocusRectangle(marginLeft, marginTop, this.RECTANGLE_WIDTH + marginLeft, this.RECTANGLE_HEIGHT + marginTop);
        if (!FunctionProperties.isFrontTouchAESupported() || this.mGet.getCameraId() != FOCUS_POSITION_TOUCH || this.mGet.getActivity() == null) {
            return;
        }
        if (this.mGet.getCameraDevice() != null || this.mGet.getLG() != null) {
            initFocusAreas();
        }
    }

    public void setFocusRectangle(int left, int top, int right, int bottom) {
        if (this.mFocusRect != null && this.mCameraFocusView != null) {
            LayoutParams rl = (LayoutParams) this.mCameraFocusView.getLayoutParams();
            if (!Common.isQuickWindowCameraMode()) {
                rl.width = this.RECTANGLE_WIDTH;
                rl.height = this.RECTANGLE_HEIGHT;
            } else if (LGT_Limit.ISP_AUTOMODE_AUTO.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) {
                top = (CameraConstants.LCD_SIZE_HEIGHT - (this.RECTANGLE_HEIGHT / FOCUS_POSITION_FACE)) / FOCUS_POSITION_FACE;
                left = (CameraConstants.smartCoverSizeWidth - (this.RECTANGLE_WIDTH / FOCUS_POSITION_FACE)) / FOCUS_POSITION_FACE;
                rl.width = this.RECTANGLE_WIDTH / FOCUS_POSITION_FACE;
                rl.height = this.RECTANGLE_HEIGHT / FOCUS_POSITION_FACE;
            } else {
                left = (CameraConstants.smartCoverSizeWidth - this.RECTANGLE_WIDTH) / FOCUS_POSITION_FACE;
                rl.width = this.RECTANGLE_WIDTH;
                rl.height = this.RECTANGLE_HEIGHT;
            }
            if (this.mGet.isConfigureLandscape()) {
                rl.leftMargin = left;
                rl.topMargin = top;
            } else {
                rl.topMargin = left;
                rl.rightMargin = top;
            }
            this.mCameraFocusView.setLayoutParams(rl);
            if (left - this.mFocusAreaLeftMargin < 0) {
                left = FOCUS_POSITION_DEFAULT;
                right = this.RECTANGLE_WIDTH;
            }
            this.mFocusRect.set(left - this.mFocusAreaLeftMargin, top, right - this.mFocusAreaLeftMargin, bottom);
        }
    }

    public void setFocusRectangle(Rect rect) {
        setFocusRectangle(rect.left, rect.top, rect.right, rect.bottom);
    }

    public void setFocusPosition(int position) {
        this.mFocusPosition = position;
    }

    public int getObjectTrackingState() {
        return this.mObjectState;
    }

    public void setObjectFocusState(int state) {
        this.mObjectState = state;
    }

    public boolean isRegisterObjectCallback() {
        return this.mIsRegisterObjectCallback;
    }

    public boolean checkAvailablePreviewSize() {
        int[] previewSizeOnDevice = Util.SizeString2WidthHeight(this.mGet.getPreviewSizeOnDevice());
        String sVideoFps = CameraConstants.SMART_MODE_OFF;
        ListPreference pref = this.mGet.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
        if (pref != null) {
            sVideoFps = pref.getExtraInfo3();
        }
        if ((previewSizeOnDevice[FOCUS_POSITION_DEFAULT] > ThumbNailSize.width || this.mGet.isLiveEffectActive()) && CameraConstants.SMART_MODE_OFF.equals(sVideoFps) && !"3840x2160".equalsIgnoreCase(this.mGet.getSettingValue(Setting.KEY_PREVIEW_SIZE_ON_DEVICE))) {
            return true;
        }
        return false;
    }

    public void calculateObjectScaleRatio() {
        Size previewSizeOnDevice = this.mGet.getLGParam().getParameters().getPreviewSize();
        this.scaleRatio = ((float) this.mFocusAreaWidth) / ((float) previewSizeOnDevice.width);
        CamLog.d(FaceDetector.TAG, "Object scale ratio : " + this.scaleRatio + "(" + this.mFocusAreaWidth + " / " + previewSizeOnDevice.width + ")");
    }

    public void startObjectTrackingFocus(int[] data) {
        int status;
        int left;
        int top;
        int right;
        int bottom;
        if (ModelProperties.isRenesasISP()) {
            status = data[FOCUS_POSITION_DEFAULT] & Ola_ShotParam.AnimalMask_Random;
            left = (data[FOCUS_POSITION_DEFAULT] & -65536) >>> 16;
            top = data[FOCUS_POSITION_TOUCH] & 65535;
            right = (data[FOCUS_POSITION_TOUCH] & -65536) >>> 16;
            bottom = data[FOCUS_POSITION_FACE] & 65535;
        } else {
            status = data[FOCUS_POSITION_DEFAULT];
            left = data[FOCUS_POSITION_TOUCH];
            top = data[FOCUS_POSITION_FACE];
            right = data[3];
            bottom = data[4];
        }
        if (status != 0 && status <= 4) {
            int x = left + ((right - left) / FOCUS_POSITION_FACE);
            int y = top + ((bottom - top) / FOCUS_POSITION_FACE);
            CamLog.d(FaceDetector.TAG, "Object callback values, x : " + x + ", y : " + y + ", object status : " + status + ", coordinate left : " + left + ", top : " + top + ", right : " + right + ", bottom : " + bottom);
            if (this.mGet.isSmartZoomRecordingActive()) {
                startObjectTrackingFocusForSmartZoom((int) (((float) x) * this.scaleRatio), (int) ((((float) y) * this.scaleRatio) + ((float) this.mFocusAreaTopMargin)), (int) (((float) (right - left)) * this.scaleRatio), (int) (((float) (bottom - top)) * this.scaleRatio), status);
                return;
            }
            startObjectTrackingFocus((int) (((float) x) * this.scaleRatio), (int) ((((float) y) * this.scaleRatio) + ((float) this.mFocusAreaTopMargin)), (int) (((float) (right - left)) * this.scaleRatio), (int) (((float) (bottom - top)) * this.scaleRatio), status);
        }
    }

    public void startObjectTrackingFocus(int x, int y, int width, int height, int state) {
        if (FunctionProperties.isSupportObjectTracking()) {
            if (!(this.mGet.isSmartZoomRecordingActive() || (this.mGet.getApplicationMode() == FOCUS_POSITION_TOUCH && (this.mGet.getVideoState() == 3 || this.mGet.getVideoState() == 4)))) {
                state = FOCUS_POSITION_DEFAULT;
            }
            RotateImageButton objectTracking = (RotateImageButton) this.mGet.findViewById(R.id.object_tracking_focus);
            if (objectTracking != null) {
                if (FunctionProperties.isSupportAudiozoom() && this.mGet.getAudiozoomStart()) {
                    state = FOCUS_POSITION_DEFAULT;
                }
                this.mObjectState = state;
                switch (state) {
                    case FOCUS_POSITION_DEFAULT /*0*/:
                    case LGKeyRec.EVENT_STOPPED /*4*/:
                        unregisterObjectCallback();
                        objectTracking.setBackgroundResource(R.drawable.camera_preview_object_focusing);
                        objectTracking.setVisibility(8);
                        this.mGet.removeQuickButton(3);
                        return;
                    case FOCUS_POSITION_TOUCH /*1*/:
                        this.mGet.addQuickButton(this.mGet.getApplicationContext(), 3, 6);
                        this.mGet.setQuickButtonVisible(3, FOCUS_POSITION_DEFAULT, true);
                        objectTracking.setBackgroundResource(R.drawable.camera_preview_object_focusing);
                        break;
                    case FOCUS_POSITION_FACE /*2*/:
                        this.mGet.addQuickButton(this.mGet.getApplicationContext(), 3, 6);
                        this.mGet.setQuickButtonVisible(3, FOCUS_POSITION_DEFAULT, true);
                        objectTracking.setBackgroundResource(R.drawable.camera_preview_object_focused);
                        break;
                    case LGKeyRec.EVENT_STARTED /*3*/:
                        unregisterObjectCallback();
                        objectTracking.setBackgroundResource(R.drawable.camera_preview_object_failed);
                        objectTracking.setVisibility(8);
                        this.mGet.removeQuickButton(3);
                        break;
                    default:
                        return;
                }
                setLayoutForObjectFocus(x, y, width, height, objectTracking);
            }
        }
    }

    public void startObjectTrackingFocusForSmartZoom(int x, int y, int width, int height, int state) {
        if (FunctionProperties.isSupportObjectTracking()) {
            if (!(this.mGet.isSmartZoomRecordingActive() || (this.mGet.getApplicationMode() == FOCUS_POSITION_TOUCH && this.mGet.getVideoState() == 3))) {
                state = FOCUS_POSITION_DEFAULT;
            }
            RotateImageButton objectTracking = (RotateImageButton) this.mGet.findViewById(R.id.object_tracking_focus);
            if (objectTracking != null) {
                this.mObjectState = state;
                switch (state) {
                    case FOCUS_POSITION_DEFAULT /*0*/:
                        unregisterObjectCallback();
                        objectTracking.setBackgroundResource(R.drawable.camera_preview_object_focusing);
                        objectTracking.setVisibility(8);
                        return;
                    case FOCUS_POSITION_TOUCH /*1*/:
                        objectTracking.setBackgroundResource(R.drawable.camera_preview_object_focusing);
                        break;
                    case FOCUS_POSITION_FACE /*2*/:
                        objectTracking.setBackgroundResource(R.drawable.camera_preview_object_focused);
                        break;
                    case LGKeyRec.EVENT_STARTED /*3*/:
                        unregisterObjectCallback();
                        objectTracking.setBackgroundResource(R.drawable.camera_preview_object_failed);
                        objectTracking.setVisibility(8);
                        break;
                    case LGKeyRec.EVENT_STOPPED /*4*/:
                        this.mGet.disableObjectTrackingForSmartZoom();
                        unregisterObjectCallback();
                        break;
                }
                int convX = x;
                int convY = y;
                if (state == 4) {
                    this.mGet.initSmartZoomFocusView();
                    CamLog.d(FaceDetector.TAG, "startObjectTrackingFocusForSmartZoom : focus view moves to default position");
                    return;
                }
                if (!this.mGet.isConfigureLandscape()) {
                    DisplayMetrics outMetrics = new DisplayMetrics();
                    ((WindowManager) this.mGet.getActivity().getSystemService("window")).getDefaultDisplay().getMetrics(outMetrics);
                    convX = outMetrics.widthPixels - y;
                    convY = x;
                }
                CamLog.d(FaceDetector.TAG, "startObjectTrackingFocusForSmartZoom : convX = " + convX + ", convY = " + convY);
                this.mGet.setSmartZoomFocusViewPosition(convX, convY);
            }
        }
    }

    private void setLayoutForObjectFocus(int x, int y, int width, int height, ImageView objectTracking) {
        int i = FOCUS_POSITION_DEFAULT;
        if (objectTracking.getVisibility() != 0) {
            objectTracking.setVisibility(FOCUS_POSITION_DEFAULT);
            objectTracking.setAlpha(0.7f);
        }
        LayoutParams otp = (LayoutParams) objectTracking.getLayoutParams();
        if (x > (this.mFocusAreaLeftMargin + this.mFocusAreaWidth) - (width / FOCUS_POSITION_FACE)) {
            x = (this.mFocusAreaLeftMargin + this.mFocusAreaWidth) - (width / FOCUS_POSITION_FACE);
        }
        if (y > (this.mFocusAreaHeight + this.mFocusAreaTopMargin) - (height / FOCUS_POSITION_FACE)) {
            y = (this.mFocusAreaHeight + this.mFocusAreaTopMargin) - (height / FOCUS_POSITION_FACE);
        }
        int left = (x - this.mFocusAreaLeftMargin) - (width / FOCUS_POSITION_FACE);
        int top = y - (height / FOCUS_POSITION_FACE);
        if (this.mGet.isConfigureLandscape()) {
            if (left < 0) {
                left = FOCUS_POSITION_DEFAULT;
            }
            otp.leftMargin = left;
            if (top >= 0) {
                i = top;
            }
            otp.topMargin = i;
            otp.width = width;
            otp.height = height;
        } else {
            if (top < 0) {
                top = FOCUS_POSITION_DEFAULT;
            }
            otp.rightMargin = top;
            if (left >= 0) {
                i = left;
            }
            otp.topMargin = i;
            otp.width = height;
            otp.height = width;
        }
        objectTracking.setLayoutParams(otp);
    }

    public void registerObjectCallback() {
        if (FunctionProperties.isSupportObjectTracking()) {
            try {
                LGCamera lgCamera = this.mGet.getLG();
                if (lgCamera != null) {
                    CamLog.d(FaceDetector.TAG, "Register object callback");
                    if (ModelProperties.isRenesasISP()) {
                        lgCamera.setISPDataCallbackMode(this.mObjectTrackingFocusCallback);
                    } else {
                        lgCamera.setOBTDataCallbackMode(this.mObjectTrackingFocusCallback);
                    }
                    this.mIsRegisterObjectCallback = true;
                }
            } catch (RuntimeException e) {
                CamLog.e(FaceDetector.TAG, "registerObjectCallback:RuntimeException : ", e);
            }
        }
    }

    public void unregisterObjectCallback() {
        if (FunctionProperties.isSupportObjectTracking()) {
            try {
                LGCamera lgCamera = this.mGet.getLG();
                if (lgCamera != null) {
                    CamLog.d(FaceDetector.TAG, "Unregister object callback");
                    LGParameters lgParameters = lgCamera.getLGParameters();
                    lgParameters.setObjectTracking(CameraConstants.SMART_MODE_OFF);
                    lgParameters.setParameters(lgParameters.getParameters());
                    this.mGet.getLG().runObjectTracking();
                    if (ModelProperties.isRenesasISP()) {
                        this.mGet.getLG().setISPDataCallbackMode(null);
                    } else {
                        this.mGet.getLG().setOBTDataCallbackMode(null);
                    }
                    this.mIsRegisterObjectCallback = false;
                }
            } catch (RuntimeException e) {
                CamLog.e(FaceDetector.TAG, "unregisterObjectCallback-setParameters fail:RuntimeException : ", e);
            }
        }
    }

    public boolean getCafOnGoing() {
        return this.isCafOnGoing;
    }

    public void setCafOnGoing(boolean CafOnGoing) {
        this.isCafOnGoing = CafOnGoing;
    }

    public void initMultiWindowAFView() {
        if (this.mCameraMultiWindowAFView != null && !this.mCameraMultiWindowAFView.isInitialize()) {
            LGCamera lgCamera = this.mGet.getLG();
            if (lgCamera != null) {
                LGParameters lgParameters = lgCamera.getLGParameters();
                if (lgParameters != null) {
                    int[] previewSizeOnScreen = Util.SizeString2WidthHeight(this.mGet.getPreviewSizeOnScreen());
                    if (lgParameters.getMultiWindowFocusAreas() != null) {
                        this.mCameraMultiWindowAFView.init(this.mGet.getApplicationContext(), lgParameters.getMultiWindowFocusAreas(), previewSizeOnScreen);
                    }
                }
            }
        }
    }

    public void setMultiWindowAFView(int[] previewSizeOnScreen) {
        if (this.mCameraMultiWindowAFView != null) {
            this.mCameraMultiWindowAFView.refresh(previewSizeOnScreen);
        }
    }

    public boolean checkShotModeForMultiWindowAF() {
        if (this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) || this.mGet.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
            return false;
        }
        return true;
    }

    public boolean checkMultiWindowAFCondition() {
        if ((CameraConstants.FOCUS_SETTING_VALUE_MULTIWINDOWAF.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)) || Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) && checkShotModeForMultiWindowAF() && this.mFocusPosition == 0) {
            return true;
        }
        return false;
    }

    public String getDefaultFocusModeParameterForMultiWindowAF(LGParameters lgParam) {
        if (lgParam == null) {
            return null;
        }
        if ((CameraConstants.FOCUS_SETTING_VALUE_MULTIWINDOWAF.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS)) || Setting.HELP_FACE_TRACKING_LED.equals(this.mGet.getSettingValue(Setting.KEY_FOCUS))) && lgParam.getParameters().getSupportedFocusModes().contains(CameraConstants.FOCUS_MODE_MULTIWINDOWAF) && checkShotModeForMultiWindowAF()) {
            return CameraConstants.FOCUS_MODE_MULTIWINDOWAF;
        }
        return "continuous-picture";
    }

    public void setBlockingFaceTrFocusing(boolean beBlocked) {
    }

    public boolean isBlockingFaceTrFocusing() {
        return false;
    }

    public void cancelAutoFocus() {
    }

    public boolean isSnapOnFinish() {
        return true;
    }

    public void startAEByTouchPress(int x, int y) {
    }

    public void startFocusByTouchPress(int x, int y) {
    }

    public void startFocusByTouchPress(int x, int y, boolean bTouchedAFbyFaceTr) {
    }

    public boolean isShutterButtonClicked() {
        return false;
    }

    public void setShutterButtonClicked(boolean clicked) {
    }

    public boolean isContinuousFocusActivating() {
        return false;
    }

    public void setContinuousFocusActive(boolean active) {
    }

    public boolean showFocus(boolean justDoIt) {
        return true;
    }

    public void initFocusAreas() {
    }

    public void registerFaceTrackingCallback() {
    }

    public void doFocusOnCaf() {
    }
}
