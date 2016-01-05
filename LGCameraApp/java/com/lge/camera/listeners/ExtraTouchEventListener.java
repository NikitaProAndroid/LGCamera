package com.lge.camera.listeners;

import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.Mediator;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.BarView;
import com.lge.camera.components.MultiDirectionSlidingDrawer;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.filterpacks.DualRecorderFilter;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class ExtraTouchEventListener {
    private static final int BASE_SPAN = 900;
    private static final float SCALE_SIZE = 0.4f;
    private static final int STEP_LIMIT = 8;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private static final int ZERO = 0;
    private int convX;
    private int convY;
    private int downTouchX;
    private int downTouchY;
    private int initialX;
    private int initialY;
    private boolean mActionDetected;
    private final long mDualRec_TouchSensitivity;
    private GestureDetector mGestureDetector;
    private boolean mIsDualRecSubWindowMoving;
    private boolean mIsSmartZoomFocusMoving;
    private long mLastTouchedAFTime;
    private int mMoveDirection;
    private boolean mPingPong;
    private boolean mResizingHandler;
    private ScaleGestureDetector mScaleDetector;
    private long mTouchDownTime;
    private long mTouchUpTime;
    private int prevX;
    private int prevY;
    private int previewTopMargin;
    int startX;
    int startY;
    private int x;
    private int y;

    private class GestureListener extends SimpleOnGestureListener {
        private Mediator mediator;

        public GestureListener(Mediator mediator) {
            this.mediator = mediator;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!(e1 == null || e2 == null)) {
                try {
                    if (Math.abs(e1.getY() - e2.getY()) <= 250.0f && ((e1.getX() - e2.getX() <= CameraConstants.SMARTZOOM_ZOOM_AREA_WIDTH_UVGA || Math.abs(velocityX) <= 200.0f) && ((e2.getX() - e1.getX() <= CameraConstants.SMARTZOOM_ZOOM_AREA_WIDTH_UVGA || Math.abs(velocityX) <= 200.0f) && ((e1.getY() - e2.getY() <= CameraConstants.SMARTZOOM_ZOOM_AREA_WIDTH_UVGA || Math.abs(velocityY) <= 200.0f) && e2.getY() - e1.getY() > CameraConstants.SMARTZOOM_ZOOM_AREA_WIDTH_UVGA && Math.abs(velocityY) > 200.0f)))) {
                    }
                } catch (Exception e) {
                    CamLog.e(FaceDetector.TAG, "GestureListener exception : ", e);
                }
            }
            return false;
        }

        public void onLongPress(MotionEvent event) {
            try {
                CamLog.d(FaceDetector.TAG, "GestureListener - onLongPress");
                if (this.mediator != null) {
                    if (this.mediator.getApplicationMode() != 0 || "0".equals(this.mediator.getSettingValue(Setting.KEY_CAMERA_TIMER)) || !this.mediator.isTimerShotCountdown()) {
                        if (ExtraTouchEventListener.this.checkIgnoreTouchEvent(ExtraTouchEventListener.this.startX, this.mediator)) {
                            CamLog.d(FaceDetector.TAG, "Ignore touch event");
                        } else if (!this.mediator.getPIPController().isInSubWindow(ExtraTouchEventListener.this.x - ExtraTouchEventListener.this.previewTopMargin, ExtraTouchEventListener.this.y) || (this.mediator.getPIPController().isInSmartZoomFocusViewArea(ExtraTouchEventListener.this.x, ExtraTouchEventListener.this.y) && this.mediator.isSmartZoomRecordingActive())) {
                            if (this.mediator.getApplicationMode() == 1 && MultimediaProperties.isSmartZoomSupported() && CameraConstants.TYPE_RECORDMODE_SMART_ZOOM.equals(this.mediator.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE)) && MultimediaProperties.SMARTZOOM_FOCUS_MODE == 3 && this.mediator.getCurrentPIPMask() != 0 && ExtraTouchEventListener.this.x >= ExtraTouchEventListener.this.previewTopMargin && ExtraTouchEventListener.this.x <= this.mediator.getFocusAreaHeight() + ExtraTouchEventListener.this.previewTopMargin) {
                                this.mediator.getPIPController().enableObjectTrackingForSmartZoom();
                                ExtraTouchEventListener.this.setSmartZoomFocusViewPosition(ExtraTouchEventListener.this.x, ExtraTouchEventListener.this.y, this.mediator);
                                int revertX = ExtraTouchEventListener.this.x;
                                int revertY = ExtraTouchEventListener.this.y;
                                if (!this.mediator.isConfigureLandscape()) {
                                    revertX = ExtraTouchEventListener.this.y;
                                    revertY = CameraConstants.LCD_SIZE_HEIGHT - ExtraTouchEventListener.this.x;
                                }
                                CamLog.d(FaceDetector.TAG, "onLongPress revertX = " + revertX + ", revertY = " + revertY);
                                this.mediator.doTouchbyAF(revertX, revertY);
                                this.mediator.toast((int) R.string.smart_zoom_object_tracking_focus_mode);
                            }
                        } else if (((this.mediator.getApplicationMode() == 1 && ((MultimediaProperties.isDualRecordingSupported() && CameraConstants.TYPE_RECORDMODE_DUAL.equals(this.mediator.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE))) || (MultimediaProperties.isSmartZoomSupported() && CameraConstants.TYPE_RECORDMODE_SMART_ZOOM.equals(this.mediator.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE))))) || (this.mediator.getApplicationMode() == 0 && this.mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA))) && !ExtraTouchEventListener.this.isPIPFrameSplitView(this.mediator) && this.mediator.getCurrentPIPMask() != 0) {
                            float mInitialX = event.getX();
                            float mInitialY = event.getY();
                            if (this.mediator.getPIPController().isResizeHandlerShown()) {
                                this.mediator.doCommand(Command.HIDE_PIP_SUBWINDOW_RESIZE_HANDLER);
                                return;
                            }
                            this.mediator.doCommand(Command.SHOW_PIP_SUBWINDOW_RESIZE_HANDLER, Float.valueOf(mInitialX), Float.valueOf(mInitialY));
                            if (this.mediator.getPIPController().isResizeHandlerShown()) {
                                this.mediator.getPIPController().setMode(1);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "GestureListener exception : ", e);
            }
        }
    }

    private class ScaleListener extends SimpleOnScaleGestureListener {
        private float mBlockJitterStep;
        private float mOneStepBeforeSpan;
        private Mediator mediator;

        public ScaleListener(Mediator mediator) {
            this.mBlockJitterStep = 4.0f;
            this.mOneStepBeforeSpan = 0.0f;
            this.mediator = mediator;
        }

        public boolean onScale(ScaleGestureDetector detector) {
            CamLog.d(FaceDetector.TAG, "onScale!!!");
            int mGapSpan = (int) (detector.getCurrentSpan() - this.mOneStepBeforeSpan);
            if (((float) Math.abs(mGapSpan)) <= this.mBlockJitterStep) {
                return super.onScale(detector);
            }
            int gapSpan = Math.abs(mGapSpan) * ProjectVariables.getGestureZoomFactor();
            int zoomStep = ((gapSpan * gapSpan) / ExtraTouchEventListener.BASE_SPAN) + 1;
            if (zoomStep >= ExtraTouchEventListener.STEP_LIMIT) {
                zoomStep = ExtraTouchEventListener.STEP_LIMIT;
            }
            CamLog.d(FaceDetector.TAG, "ScaleGestureDetector : gapSpan = " + gapSpan + ", zoomStep = " + zoomStep);
            if (mGapSpan != 0) {
                ExtraTouchEventListener.this.zoomInOut(mGapSpan > 0 ? BarView.CURSOR_ONE_STEP_PLUS : BarView.CURSOR_ONE_STEP_MINUS, zoomStep, false, this.mediator);
            }
            this.mOneStepBeforeSpan = detector.getCurrentSpan();
            return super.onScale(detector);
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            CamLog.v(FaceDetector.TAG, "onScaleBegin");
            this.mOneStepBeforeSpan = detector.getCurrentSpan();
            return super.onScaleBegin(detector);
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
            CamLog.v(FaceDetector.TAG, "onScaleEnd```````````````````");
            this.mOneStepBeforeSpan = 0.0f;
            ExtraTouchEventListener.this.zoomInOut(BarView.CURSOR_ONE_STEP_PLUS, 0, true, this.mediator);
            super.onScaleEnd(detector);
        }
    }

    public ExtraTouchEventListener() {
        this.mScaleDetector = null;
        this.mGestureDetector = null;
        this.mActionDetected = false;
        this.mLastTouchedAFTime = 0;
        this.mIsDualRecSubWindowMoving = false;
        this.mResizingHandler = false;
        this.initialX = 0;
        this.initialY = 0;
        this.downTouchX = 0;
        this.downTouchY = 0;
        this.mPingPong = false;
        this.prevX = 0;
        this.prevY = 0;
        this.mTouchDownTime = 0;
        this.mTouchUpTime = 0;
        this.mDualRec_TouchSensitivity = 300;
        this.previewTopMargin = 0;
        this.mIsSmartZoomFocusMoving = false;
        this.startX = 0;
        this.startY = 0;
    }

    public void executeTouchEvent(MotionEvent event, Mediator mediator) {
        this.x = (int) event.getX();
        this.y = (int) event.getY();
        boolean bInPreviewScreen = true;
        this.previewTopMargin = (CameraConstants.LCD_SIZE_HEIGHT - mediator.getFocusAreaHeight()) / 2;
        if (!mediator.isPausing()) {
            if (!Common.isQuickWindowCameraMode()) {
                View v;
                if (this.mScaleDetector == null || event.getPointerCount() <= 1) {
                    try {
                        if (!mediator.isQuickFunctionDragControllerVisible() && this.mGestureDetector.onTouchEvent(event)) {
                            CamLog.d(FaceDetector.TAG, "PinchZoom : detect success");
                            this.mActionDetected = true;
                            v = mediator.findViewById(R.id.focus_touch_move);
                            if (v != null && v.getVisibility() == 0) {
                                v.setVisibility(STEP_LIMIT);
                                return;
                            }
                            return;
                        }
                    } catch (IllegalArgumentException ex) {
                        CamLog.e(FaceDetector.TAG, "mScaleDetector fail", ex);
                    }
                } else {
                    LayoutParams previewParam = (LayoutParams) mediator.findViewById(R.id.preview).getLayoutParams();
                    int panelWidth = Common.getPixelFromDimens(mediator.getApplicationContext(), R.dimen.preview_panel_width);
                    if ((ModelProperties.isXGAmodel() || ModelProperties.isUVGAmodel()) && this.x > previewParam.width - panelWidth) {
                        bInPreviewScreen = false;
                    }
                    if (this.x < previewParam.leftMargin || this.x > previewParam.width + previewParam.leftMargin || this.y < previewParam.bottomMargin || this.y > previewParam.height + previewParam.bottomMargin) {
                        bInPreviewScreen = false;
                    } else {
                        try {
                            if (!mediator.isQuickFunctionDragControllerVisible()) {
                                if (this.mScaleDetector.onTouchEvent(event)) {
                                    this.mActionDetected = true;
                                } else if ((!(mediator.getApplicationMode() != 0 || mediator.isSnapOnFinish() || mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) || (mediator.getApplicationMode() == 1 && !mediator.isEffectsCamcorderActive())) && this.mGestureDetector.onTouchEvent(event)) {
                                    this.mActionDetected = true;
                                }
                                if (this.mActionDetected) {
                                    CamLog.d(FaceDetector.TAG, "Action Detected.");
                                    v = mediator.findViewById(R.id.focus_touch_move);
                                    if (v != null && v.getVisibility() == 0) {
                                        v.setVisibility(STEP_LIMIT);
                                        return;
                                    }
                                    return;
                                }
                            }
                            return;
                        } catch (IllegalArgumentException ex2) {
                            CamLog.e(FaceDetector.TAG, "mScaleDetector fail", ex2);
                        }
                    }
                }
                if (mediator.isDualRecordingActive() || mediator.isDualCameraActive() || mediator.isSmartZoomRecordingActive()) {
                    this.convX = this.x;
                    this.convY = this.y;
                    if (this.convX < 0) {
                        this.convX = 0;
                    }
                } else if (mediator.isConfigureLandscape()) {
                    this.convX = this.x;
                    this.convY = this.y;
                } else {
                    this.convX = this.y;
                    this.convY = CameraConstants.LCD_SIZE_HEIGHT - this.x;
                }
                this.x = this.convX;
                this.y = this.convY;
                doTouchAction(event, this.x, this.y, bInPreviewScreen, mediator);
            } else if (!isTouchSmartCoverView() || event.getAction() != 1) {
            }
        }
    }

    private void doTouchAction(MotionEvent event, int x, int y, boolean bInPreviewScreen, Mediator mediator) {
        switch (event.getActionMasked() & Ola_ShotParam.AnimalMask_Random) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                CamLog.d(FaceDetector.TAG, "PinchZoom : ACTION_DOWN");
                this.mActionDetected = false;
                this.mIsDualRecSubWindowMoving = false;
                this.mResizingHandler = false;
                this.mTouchDownTime = SystemClock.uptimeMillis();
                this.downTouchX = x;
                this.downTouchY = y;
                this.startX = x;
                this.startY = y;
                if (mediator.isControllerInitialized()) {
                    CamLog.d(FaceDetector.TAG, "checkMenuInTouchActionDown : ACTION_DOWN");
                    checkMenuInTouchActionDown(mediator);
                }
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                CamLog.d(FaceDetector.TAG, "PinchZoom : ACTION_UP");
                this.mTouchUpTime = SystemClock.uptimeMillis();
                doExeTouchActionUp(event, x, y, mediator);
                this.startX = 0;
                this.startY = 0;
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                CamLog.d(FaceDetector.TAG, "PinchZoom : ACTION_MOVE");
                if (mediator.checkTouchFocusArea(x)) {
                    doExeTouchActionMove(event, x, y, bInPreviewScreen, mediator);
                }
            case LGKeyRec.EVENT_STARTED /*3*/:
                CamLog.d(FaceDetector.TAG, "ACTION_CANCEL");
                ImageView v = (ImageView) mediator.findViewById(R.id.focus_touch_move);
                if (v != null) {
                    v.setVisibility(STEP_LIMIT);
                }
            default:
        }
    }

    private void checkMenuInTouchActionDown(Mediator mediator) {
        if (mediator.isQuickFunctionDragControllerVisible()) {
            this.mActionDetected = true;
            mediator.doCommand(Command.HIDE_QUICK_FUNCTION_DRAG_DROP);
        } else if (mediator.isQuickFunctionSettingControllerShowing()) {
            this.mActionDetected = true;
            mediator.doCommand(Command.HIDE_QUICK_FUNCTION_SETTING_MENU);
        } else if (!mediator.isNullSettingView()) {
            mediator.setSubMenuMode(0);
            mediator.doCommandUi(Command.REMOVE_SETTING_MENU);
            this.mActionDetected = true;
        } else if (mediator.getSubMenuMode() == 18) {
            if (mediator.findPreference(Setting.KEY_VIDEO_RECORD_MODE) == null) {
                mediator.restoreLiveEffectSubMenu();
            }
            mediator.clearSubMenu();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void doExeTouchActionMove(android.view.MotionEvent r21, int r22, int r23, boolean r24, com.lge.camera.Mediator r25) {
        /*
        r20 = this;
        r4 = 0;
        r5 = 0;
        r15 = r25.getCameraId();
        r16 = 1;
        r0 = r16;
        if (r15 != r0) goto L_0x001f;
    L_0x000c:
        r15 = r25.isDualRecordingActive();
        if (r15 != 0) goto L_0x001f;
    L_0x0012:
        r15 = r25.isSmartZoomRecordingActive();
        if (r15 != 0) goto L_0x001f;
    L_0x0018:
        r15 = com.lge.camera.properties.FunctionProperties.isFrontTouchAESupported();
        if (r15 != 0) goto L_0x001f;
    L_0x001e:
        return;
    L_0x001f:
        r0 = r20;
        r1 = r25;
        r15 = r0.checkTouchMoveFocus(r1);
        if (r15 == 0) goto L_0x001e;
    L_0x0029:
        r15 = r25.isFocusViewVisible();
        if (r15 == 0) goto L_0x0032;
    L_0x002f:
        r25.hideFocus();
    L_0x0032:
        r15 = r25.getApplicationMode();
        r16 = 1;
        r0 = r16;
        if (r15 == r0) goto L_0x0050;
    L_0x003c:
        r15 = r25.getApplicationMode();
        if (r15 != 0) goto L_0x02cf;
    L_0x0042:
        r15 = "key_camera_shot_mode";
        r16 = "shotmode_dual_camera";
        r0 = r25;
        r1 = r16;
        r15 = r0.checkSettingValue(r15, r1);
        if (r15 == 0) goto L_0x02cf;
    L_0x0050:
        r15 = r25.isDualRecordingActive();
        if (r15 != 0) goto L_0x0062;
    L_0x0056:
        r15 = r25.isDualCameraActive();
        if (r15 != 0) goto L_0x0062;
    L_0x005c:
        r15 = r25.isSmartZoomRecordingActive();
        if (r15 == 0) goto L_0x001e;
    L_0x0062:
        r15 = r25.getPIPController();
        if (r15 == 0) goto L_0x001e;
    L_0x0068:
        r0 = r20;
        r15 = r0.mIsDualRecSubWindowMoving;
        if (r15 != 0) goto L_0x00f8;
    L_0x006e:
        r0 = r20;
        r15 = r0.mResizingHandler;
        if (r15 != 0) goto L_0x00f8;
    L_0x0074:
        r0 = r22;
        r1 = r20;
        r1.initialX = r0;
        r0 = r23;
        r1 = r20;
        r1.initialY = r0;
        r0 = r20;
        r15 = r0.initialX;
        r0 = r20;
        r0.prevX = r15;
        r0 = r20;
        r15 = r0.initialY;
        r0 = r20;
        r0.prevY = r15;
        r15 = r25.getPIPController();
        r0 = r20;
        r0 = r0.initialX;
        r16 = r0;
        r0 = r20;
        r0 = r0.previewTopMargin;
        r17 = r0;
        r16 = r16 - r17;
        r0 = r20;
        r0 = r0.initialY;
        r17 = r0;
        r15 = r15.checkResizeDirection(r16, r17);
        r0 = r20;
        r0.mMoveDirection = r15;
        r15 = r25.getPIPController();
        r0 = r20;
        r0 = r0.mMoveDirection;
        r16 = r0;
        r0 = r20;
        r0 = r0.initialX;
        r17 = r0;
        r0 = r20;
        r0 = r0.previewTopMargin;
        r18 = r0;
        r17 = r17 - r18;
        r0 = r20;
        r0 = r0.initialY;
        r18 = r0;
        r19 = r25.getOrientation();
        r15 = r15.setDirectionForRatio(r16, r17, r18, r19);
        r0 = r20;
        r0.mMoveDirection = r15;
        r15 = "CameraApp";
        r16 = new java.lang.StringBuilder;
        r16.<init>();
        r17 = "mMoveDirection = ";
        r16 = r16.append(r17);
        r0 = r20;
        r0 = r0.mMoveDirection;
        r17 = r0;
        r16 = r16.append(r17);
        r16 = r16.toString();
        com.lge.camera.util.CamLog.d(r15, r16);
    L_0x00f8:
        r0 = r20;
        r15 = r0.prevX;
        r4 = r22 - r15;
        r0 = r20;
        r15 = r0.prevY;
        r5 = r23 - r15;
        r0 = r20;
        r15 = r0.startX;
        r0 = r20;
        r1 = r25;
        r15 = r0.checkIgnoreTouchEvent(r15, r1);
        if (r15 == 0) goto L_0x011b;
    L_0x0112:
        r15 = "CameraApp";
        r16 = "Ignore touch event";
        com.lge.camera.util.CamLog.d(r15, r16);
        goto L_0x001e;
    L_0x011b:
        r15 = r25.getPIPController();
        r15 = r15.getMode();
        r16 = 1;
        r0 = r16;
        if (r15 != r0) goto L_0x0134;
    L_0x0129:
        r0 = r20;
        r15 = r0.mMoveDirection;
        if (r15 == 0) goto L_0x0134;
    L_0x012f:
        r15 = 1;
        r0 = r20;
        r0.mResizingHandler = r15;
    L_0x0134:
        r15 = r25.getPIPController();
        r0 = r20;
        r0 = r0.previewTopMargin;
        r16 = r0;
        r16 = r22 - r16;
        r0 = r16;
        r1 = r23;
        r15 = r15.isInSubWindow(r0, r1);
        if (r15 == 0) goto L_0x016d;
    L_0x014a:
        r0 = r20;
        r15 = r0.mIsSmartZoomFocusMoving;
        if (r15 != 0) goto L_0x016d;
    L_0x0150:
        r15 = r25.getPIPController();
        r0 = r22;
        r1 = r23;
        r15 = r15.isInSmartZoomFocusViewArea(r0, r1);
        if (r15 != 0) goto L_0x016d;
    L_0x015e:
        r0 = r20;
        r1 = r25;
        r15 = r0.isPIPFrameSplitView(r1);
        if (r15 != 0) goto L_0x016d;
    L_0x0168:
        r15 = 1;
        r0 = r20;
        r0.mIsDualRecSubWindowMoving = r15;
    L_0x016d:
        r15 = r25.getPIPController();
        r15 = r15.getMode();
        if (r15 != 0) goto L_0x024a;
    L_0x0177:
        r15 = com.lge.camera.properties.MultimediaProperties.PIP_MOVE_ALLOWED_ONLY_IN_EDIT_MODE;
        if (r15 != 0) goto L_0x01cd;
    L_0x017b:
        r15 = r25.getPIPController();
        r0 = r20;
        r0 = r0.previewTopMargin;
        r16 = r0;
        r16 = r22 - r16;
        r0 = r16;
        r1 = r23;
        r15 = r15.isInSubWindow(r0, r1);
        if (r15 == 0) goto L_0x01cd;
    L_0x0191:
        r15 = r25.getPIPController();
        r0 = r22;
        r1 = r23;
        r15 = r15.isInSmartZoomFocusViewArea(r0, r1);
        if (r15 == 0) goto L_0x01a5;
    L_0x019f:
        r0 = r20;
        r15 = r0.mIsDualRecSubWindowMoving;
        if (r15 == 0) goto L_0x01cd;
    L_0x01a5:
        r0 = r20;
        r15 = r0.mIsSmartZoomFocusMoving;
        if (r15 != 0) goto L_0x01cd;
    L_0x01ab:
        r15 = r25.getCurrentPIPMask();
        if (r15 == 0) goto L_0x01cd;
    L_0x01b1:
        r15 = r25.getPIPController();
        r15.moveSubWindow(r4, r5);
        r15 = r25.getPIPController();
        r15.confirmSubWindow();
    L_0x01bf:
        r0 = r22;
        r1 = r20;
        r1.prevX = r0;
        r0 = r23;
        r1 = r20;
        r1.prevY = r0;
        goto L_0x001e;
    L_0x01cd:
        r15 = r25.isSmartZoomRecordingActive();
        if (r15 == 0) goto L_0x01bf;
    L_0x01d3:
        r15 = r25.getPIPController();
        r15 = r15.isObjectTrackingEnabledForSmartZoom();
        if (r15 != 0) goto L_0x01bf;
    L_0x01dd:
        r0 = r20;
        r15 = r0.previewTopMargin;
        r0 = r22;
        if (r0 < r15) goto L_0x001e;
    L_0x01e5:
        r15 = r25.getFocusAreaHeight();
        r0 = r20;
        r0 = r0.previewTopMargin;
        r16 = r0;
        r15 = r15 + r16;
        r0 = r22;
        if (r0 > r15) goto L_0x001e;
    L_0x01f5:
        r0 = r20;
        r15 = r0.downTouchX;
        r15 = r15 - r22;
        r0 = r20;
        r0 = r0.downTouchX;
        r16 = r0;
        r16 = r16 - r22;
        r15 = r15 * r16;
        r0 = r20;
        r0 = r0.downTouchY;
        r16 = r0;
        r16 = r16 - r23;
        r0 = r20;
        r0 = r0.downTouchY;
        r17 = r0;
        r17 = r17 - r23;
        r16 = r16 * r17;
        r15 = r15 + r16;
        r16 = 1600; // 0x640 float:2.242E-42 double:7.905E-321;
        r0 = r16;
        if (r15 >= r0) goto L_0x0225;
    L_0x021f:
        r0 = r20;
        r15 = r0.mIsSmartZoomFocusMoving;
        if (r15 == 0) goto L_0x0235;
    L_0x0225:
        r0 = r20;
        r1 = r22;
        r2 = r23;
        r3 = r25;
        r0.setSmartZoomFocusViewPosition(r1, r2, r3);
        r15 = 1;
        r0 = r20;
        r0.mIsSmartZoomFocusMoving = r15;
    L_0x0235:
        r15 = r25.getPIPController();
        r15 = r15.isSmartZoomFocusViewShown();
        if (r15 != 0) goto L_0x01bf;
    L_0x023f:
        r15 = r25.getCurrentPIPMask();
        if (r15 == 0) goto L_0x01bf;
    L_0x0245:
        r25.showSmartZoomFocusView();
        goto L_0x01bf;
    L_0x024a:
        r15 = r25.getPIPController();
        r15 = r15.getMode();
        r16 = 1;
        r0 = r16;
        if (r15 != r0) goto L_0x02ad;
    L_0x0258:
        r15 = "com.lge.camera.command.HidePIPResizeHandler";
        r0 = r25;
        r0.removeScheduledCommand(r15);
        r0 = r20;
        r15 = r0.mMoveDirection;
        r16 = 15;
        r0 = r16;
        if (r15 == r0) goto L_0x028d;
    L_0x0269:
        r0 = r20;
        r15 = r0.mMoveDirection;
        if (r15 == 0) goto L_0x028d;
    L_0x026f:
        r15 = r25.getPIPController();
        r0 = r20;
        r0 = r0.mMoveDirection;
        r16 = r0;
        r0 = r16;
        r15.resizeSubWindowByDiff(r4, r5, r0);
        r15 = r25.getPIPController();
        r0 = r20;
        r0 = r0.mMoveDirection;
        r16 = r0;
        r15.resizeSubWindowResizeHandler(r16);
        goto L_0x01bf;
    L_0x028d:
        r0 = r20;
        r15 = r0.mMoveDirection;
        r16 = 15;
        r0 = r16;
        if (r15 != r0) goto L_0x01bf;
    L_0x0297:
        r15 = r25.getPIPController();
        r15.moveSubWindow(r4, r5);
        r15 = r25.getPIPController();
        r0 = r20;
        r0 = r0.mMoveDirection;
        r16 = r0;
        r15.resizeSubWindowResizeHandler(r16);
        goto L_0x01bf;
    L_0x02ad:
        r15 = "CameraApp";
        r16 = new java.lang.StringBuilder;
        r16.<init>();
        r17 = "Invalid mode : ";
        r16 = r16.append(r17);
        r17 = r25.getPIPController();
        r17 = r17.getMode();
        r16 = r16.append(r17);
        r16 = r16.toString();
        com.lge.camera.util.CamLog.d(r15, r16);
        goto L_0x01bf;
    L_0x02cf:
        if (r24 == 0) goto L_0x001e;
    L_0x02d1:
        r0 = r20;
        r15 = r0.startY;
        r0 = r20;
        r1 = r25;
        r15 = r0.checkIgnoreTouchEvent(r15, r1);
        if (r15 == 0) goto L_0x02e8;
    L_0x02df:
        r15 = "CameraApp";
        r16 = "Ignore touch event!";
        com.lge.camera.util.CamLog.d(r15, r16);
        goto L_0x001e;
    L_0x02e8:
        r15 = 2131558777; // 0x7f0d0179 float:1.874288E38 double:1.053129964E-314;
        r0 = r25;
        r13 = r0.findViewById(r15);
        r13 = (android.widget.ImageView) r13;
        if (r13 == 0) goto L_0x001e;
    L_0x02f5:
        r15 = r13.getVisibility();
        if (r15 == 0) goto L_0x0305;
    L_0x02fb:
        r15 = 0;
        r13.setVisibility(r15);
        r15 = 1060320051; // 0x3f333333 float:0.7 double:5.23867711E-315;
        r13.setAlpha(r15);
    L_0x0305:
        r6 = r13.getLayoutParams();
        r6 = (android.widget.RelativeLayout.LayoutParams) r6;
        r14 = r13.getMeasuredWidth();
        r7 = r13.getMeasuredHeight();
        r11 = r25.getFocusAreaWidth();
        r9 = r25.getFocusAreaHeight();
        r10 = r25.getFocusAreaLeftMargin();
        r15 = r10 + r11;
        r16 = r14 / 2;
        r15 = r15 - r16;
        r0 = r22;
        if (r0 <= r15) goto L_0x032f;
    L_0x0329:
        r15 = r10 + r11;
        r16 = r14 / 2;
        r22 = r15 - r16;
    L_0x032f:
        r15 = r7 / 2;
        r15 = r9 - r15;
        r0 = r23;
        if (r0 <= r15) goto L_0x033b;
    L_0x0337:
        r15 = r7 / 2;
        r23 = r9 - r15;
    L_0x033b:
        r15 = r22 - r10;
        r16 = r14 / 2;
        r8 = r15 - r16;
        r15 = r7 / 2;
        r12 = r23 - r15;
        if (r8 >= 0) goto L_0x0348;
    L_0x0347:
        r8 = 0;
    L_0x0348:
        if (r12 >= 0) goto L_0x034b;
    L_0x034a:
        r12 = 0;
    L_0x034b:
        r15 = r25.isConfigureLandscape();
        if (r15 == 0) goto L_0x035a;
    L_0x0351:
        r6.leftMargin = r8;
        r6.topMargin = r12;
    L_0x0355:
        r13.setLayoutParams(r6);
        goto L_0x001e;
    L_0x035a:
        r6.topMargin = r8;
        r6.rightMargin = r12;
        goto L_0x0355;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.listeners.ExtraTouchEventListener.doExeTouchActionMove(android.view.MotionEvent, int, int, boolean, com.lge.camera.Mediator):void");
    }

    private boolean checkTouchMoveFocus(Mediator mediator) {
        if (!mediator.isControllerInitialized() || mediator.isPausing() || mediator.getInCaptureProgress() || this.mScaleDetector == null || this.mScaleDetector.isInProgress() || ModelProperties.isFixedFocusModel() || !FunctionProperties.isTouchAfSupported(mediator.getApplicationMode())) {
            return false;
        }
        if ((mediator.getSubMenuMode() != 0 && mediator.getSubMenuMode() != 15) || this.mActionDetected) {
            return false;
        }
        if (mediator.getApplicationMode() == 0) {
            if ((!"0".equals(mediator.getSettingValue(Setting.KEY_CAMERA_TIMER)) && mediator.isTimerShotCountdown()) || mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PANORAMA) || mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) || mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA)) {
                return false;
            }
            if (mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_CONTINUOUS) && mediator.isCurrnetModuleRunning()) {
                return false;
            }
            if (mediator.checkSettingValue(Setting.KEY_LIGHT, CameraConstants.SMART_MODE_ON) && !mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA)) {
                return false;
            }
        }
        if (mediator.findViewById(R.id.gallery_quick_window).getVisibility() == 0) {
            CamLog.d(FaceDetector.TAG, "block touch during Quickview is visible");
            return false;
        } else if (mediator.snapshotOnContinuousFocus() || SystemClock.uptimeMillis() - this.mLastTouchedAFTime <= 600) {
            return false;
        } else {
            if ((!mediator.isCafSupported() && Setting.HELP_FACE_TRACKING_LED.equals(mediator.getSettingValue(Setting.KEY_FOCUS))) || CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(mediator.getSettingValue(Setting.KEY_FOCUS)) || !mediator.checkFocusController() || mediator.getFocusState() == 1 || mediator.getFocusState() == 5 || mediator.getFocusState() == 2) {
                return false;
            }
            return true;
        }
    }

    public void doExeTouchActionUp(MotionEvent event, int x, int y, Mediator mediator) {
        View v = mediator.findViewById(R.id.focus_touch_move);
        if (v != null) {
            v.setVisibility(STEP_LIMIT);
        }
        if (!mediator.isControllerInitialized()) {
            return;
        }
        if (mediator.findViewById(R.id.gallery_quick_window).getVisibility() == 0) {
            CamLog.d(FaceDetector.TAG, "block touch during Quickview is visible");
            return;
        }
        if (mediator.isNullSettingView()) {
            if (mediator.getSubMenuMode() == 0 || mediator.getSubMenuMode() == 5 || mediator.getSubMenuMode() == 16) {
                if (!this.mActionDetected) {
                    if (CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(mediator.getSettingValue(Setting.KEY_FOCUS))) {
                        checkBarMenuOnTouchActionUp(x, y, false, mediator);
                        if (mediator.isManualFocusBarVisible()) {
                            mediator.showManualFocusController(false);
                            return;
                        }
                        mediator.showManualFocusController(true);
                        mediator.setSubMenuMode(25);
                        return;
                    }
                }
                int frontCameraID = 1;
                long current_time = SystemClock.uptimeMillis();
                CamLog.d(FaceDetector.TAG, "PinchZoom : touched time: " + current_time + "-" + this.mLastTouchedAFTime + "=" + (current_time - this.mLastTouchedAFTime));
                long touchTime = this.mTouchUpTime - this.mTouchDownTime;
                if ((mediator.isDualRecordingActive() || mediator.isDualCameraActive() || mediator.isSmartZoomRecordingActive()) && mediator.getPIPController() != null) {
                    if (checkIgnoreTouchEvent(this.startX, mediator)) {
                        CamLog.d(FaceDetector.TAG, "Ignore touch event!");
                        return;
                    }
                    if (((MultiDirectionSlidingDrawer) mediator.findViewById(R.id.pip_frame_sliding_drawer_menu_slide)).isOpened() && mediator.getPIPController().getMode() != 1 && !mediator.isSmartZoomRecordingActive() && ((!mediator.isDualCameraActive() && !mediator.isDualRecordingActive()) || !this.mIsDualRecSubWindowMoving)) {
                        ((MultiDirectionSlidingDrawer) mediator.findViewById(R.id.pip_frame_sliding_drawer_menu_slide)).animateClose();
                    } else if (mediator.getPIPController().getMode() == 0) {
                        if ((mediator.getPIPController().isInSubWindow(x - this.previewTopMargin, y) || isPIPFrameSplitView(mediator)) && !this.mActionDetected && mediator.getCurrentPIPMask() != 0) {
                            if (mediator.getApplicationMode() == 0) {
                                if (!"0".equals(mediator.getSettingValue(Setting.KEY_CAMERA_TIMER)) && mediator.isTimerShotCountdown()) {
                                    this.mActionDetected = false;
                                    return;
                                }
                            }
                            if (touchTime < 300 && ((this.downTouchX - x) * (this.downTouchX - x)) + ((this.downTouchY - y) * (this.downTouchY - y)) < 100) {
                                if (mediator.isDualRecordingActive() || mediator.isDualCameraActive()) {
                                    boolean z;
                                    if (!this.mPingPong) {
                                        frontCameraID = 0;
                                    }
                                    DualRecorderFilter.selectPIP(frontCameraID);
                                    if (this.mPingPong) {
                                        z = false;
                                    } else {
                                        z = true;
                                    }
                                    this.mPingPong = z;
                                } else if (mediator.isSmartZoomRecordingActive()) {
                                    if (mediator.getPIPController().isObjectTrackingEnabledForSmartZoom()) {
                                        mediator.getPIPController().disableObjectTrackingForSmartZoom();
                                        setSmartZoomFocusViewPosition(x, y, mediator);
                                        mediator.unregisterObjectCallback();
                                        mediator.toast((int) R.string.smart_zoom_manual_focus_mode);
                                    } else {
                                        setSmartZoomFocusViewPosition(x, y, mediator);
                                    }
                                }
                            }
                        } else if (mediator.isSmartZoomRecordingActive() && !this.mActionDetected) {
                            if (x >= this.previewTopMargin && x <= mediator.getFocusAreaHeight() + this.previewTopMargin) {
                                if (touchTime < 300) {
                                    if (!mediator.getPIPController().isObjectTrackingEnabledForSmartZoom()) {
                                        setSmartZoomFocusViewPosition(x, y, mediator);
                                    } else if (MultimediaProperties.SMARTZOOM_FOCUS_MODE != 3) {
                                        CamLog.i(FaceDetector.TAG, "For SmartZoom Recording, object tracking is started");
                                        int revertX = x;
                                        int revertY = y;
                                        if (!mediator.isConfigureLandscape()) {
                                            revertX = y;
                                            revertY = CameraConstants.LCD_SIZE_HEIGHT - x;
                                        }
                                        CamLog.d(FaceDetector.TAG, "doExeTouchActionUp revertX = " + revertX + ", revertY = " + revertY);
                                        mediator.doTouchbyAF(revertX, revertY);
                                    } else {
                                        mediator.getPIPController().disableObjectTrackingForSmartZoom();
                                        setSmartZoomFocusViewPosition(x, y, mediator);
                                        mediator.unregisterObjectCallback();
                                        mediator.toast((int) R.string.smart_zoom_manual_focus_mode);
                                    }
                                }
                            } else {
                                return;
                            }
                        }
                        mediator.doCommand(Command.HIDE_PIP_SUBWINDOW_RESIZE_HANDLER);
                    } else if (mediator.getPIPController().getMode() == 1) {
                        mediator.getPIPController().confirmSubWindow();
                        if (mediator.getPIPController().isInSubWindow(x - this.previewTopMargin, y) || this.mIsDualRecSubWindowMoving || this.mResizingHandler) {
                            if (MultimediaProperties.PIP_TOGGLE_ALLOWED_IN_EDIT_MODE && touchTime < 300 && ((this.downTouchX - x) * (this.downTouchX - x)) + ((this.downTouchY - y) * (this.downTouchY - y)) < 100 && (mediator.isDualRecordingActive() || mediator.isDualCameraActive())) {
                                if (!this.mPingPong) {
                                    frontCameraID = 0;
                                }
                                DualRecorderFilter.selectPIP(frontCameraID);
                                this.mPingPong = !this.mPingPong;
                            }
                            if (!MultimediaProperties.PIP_SUPPORT_REALTIME_WINDOW_UPDATE) {
                                mediator.getPIPController().drawSubWindow();
                            }
                            mediator.getPIPController().resizeSubWindowResizeHandler(15);
                            mediator.doCommandDelayed(Command.HIDE_PIP_SUBWINDOW_RESIZE_HANDLER, CameraConstants.TOAST_LENGTH_LONG);
                        } else {
                            mediator.doCommand(Command.HIDE_PIP_SUBWINDOW_RESIZE_HANDLER);
                        }
                    } else {
                        CamLog.d(FaceDetector.TAG, "KMIN - Invalid SubWindow mode");
                    }
                    this.mIsDualRecSubWindowMoving = false;
                    this.mResizingHandler = false;
                    this.mIsSmartZoomFocusMoving = false;
                } else if (!mediator.isLiveEffectActive() || mediator.getVideoState() == 3) {
                    if (checkIgnoreTouchEvent(this.startY, mediator)) {
                        CamLog.d(FaceDetector.TAG, "Ignore touch event!");
                        return;
                    } else if (ProjectVariables.isSupportClearView()) {
                        if (!this.mActionDetected) {
                            CamLog.d(FaceDetector.TAG, "call toggleClearView");
                            mediator.toggleClearView();
                        }
                    } else if (!(this.mActionDetected || mediator.getInCaptureProgress() || current_time - this.mLastTouchedAFTime <= 600)) {
                        this.mLastTouchedAFTime = SystemClock.uptimeMillis();
                        mediator.doTouchbyAF(x, y);
                    }
                } else if (((MultiDirectionSlidingDrawer) mediator.findViewById(R.id.live_effect_sliding_drawer_menu_slide)).isOpened()) {
                    ((MultiDirectionSlidingDrawer) mediator.findViewById(R.id.live_effect_sliding_drawer_menu_slide)).animateClose();
                }
            } else {
                checkBarMenuOnTouchActionUp(x, y, true, mediator);
            }
        } else if (!mediator.isSettingControllerVisible()) {
            mediator.setSubMenuMode(0);
            mediator.doCommandUi(Command.REMOVE_SETTING_MENU);
        } else {
            return;
        }
        this.mActionDetected = false;
    }

    private void checkBarMenuOnTouchActionUp(int x, int y, boolean includeManualFocus, Mediator mediator) {
        int zoom_brightness_touch_area_start_y = Common.getPixelFromDimens(mediator.getApplicationContext(), R.dimen.zoom_brightness_touch_area_start_y);
        if (((mediator.getSubMenuMode() != 7 && mediator.getSubMenuMode() != 6 && ((mediator.getSubMenuMode() != 15 || mediator.getSubMenuMode() == 15) && mediator.getSubMenuMode() != 25)) || y < zoom_brightness_touch_area_start_y) && !this.mActionDetected) {
            if (mediator.getSubMenuMode() == 15) {
                long current_time = SystemClock.uptimeMillis();
                if (!mediator.getInCaptureProgress() && current_time - this.mLastTouchedAFTime > 600) {
                    this.mLastTouchedAFTime = SystemClock.uptimeMillis();
                    mediator.doTouchbyAF(x, y);
                    return;
                }
                return;
            }
            if (!mediator.isShotModeMenuVisible()) {
                mediator.setSubMenuMode(0);
                mediator.clearSubMenu(includeManualFocus);
                if (mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_MAIN_BEAUTY)) {
                    mediator.showBeautyshotController(true);
                }
            }
            mediator.showIndicatorController();
            if (mediator.checkFocusController()) {
                String focus = mediator.getSettingValue(Setting.KEY_FOCUS);
                if (LGT_Limit.ISP_AUTOMODE_AUTO.equals(focus) || CameraConstants.FOCUS_SETTING_VALUE_MULTIWINDOWAF.equals(focus) || mediator.getApplicationMode() == 1 || ModelProperties.isFixedFocusModel()) {
                    mediator.showFocus();
                }
            }
            mediator.removeScheduledCommand(Command.EXIT_ZOOM_BRIGHTNESS_INTERACTION);
        }
    }

    public void zoomInOut(int cursorStep, int factor, boolean scaleEnd, Mediator mediator) {
        try {
            if (mediator.isControllerInitialized() && !mediator.isPressedShutterButton() && !mediator.getInCaptureProgress() && mediator.checkFocusController() && mediator.getFocusState() != 1 && mediator.getFocusState() != 5 && mediator.getFocusState() != 2) {
                if (mediator.getCameraId() == 1 || !mediator.checkShotModeForZoomInOut() || mediator.checkSlowMotionMode()) {
                    if (mediator.checkSlowMotionMode()) {
                        mediator.toastConstant(mediator.getString(R.string.volume_key_zoom_disable_resolution));
                    } else if (!mediator.isToastControllerShowing()) {
                        mediator.toastConstant(mediator.getString(R.string.volume_key_zoom_disable));
                    }
                } else if (mediator.getCameraDevice() != null && mediator.getParameters() != null) {
                    if (FunctionProperties.isSupportZoomOnRecord()) {
                        if (mediator.getVideoState() == 0 || mediator.getVideoState() == 3 || mediator.getVideoState() == 4) {
                            if (!(mediator.getSubMenuMode() == 6 || mediator.getSubMenuMode() == 5 || mediator.getSubMenuMode() == 16)) {
                                mediator.doCommand(Command.SHOW_ZOOM);
                            }
                            mediator.updateZoomBar(cursorStep, factor, scaleEnd);
                        }
                    } else if (mediator.getVideoState() == 0) {
                        if (!(mediator.getSubMenuMode() == 6 || mediator.getSubMenuMode() == 5 || mediator.getSubMenuMode() == 16)) {
                            mediator.doCommand(Command.SHOW_ZOOM);
                        }
                        mediator.updateZoomBar(cursorStep, factor, scaleEnd);
                    }
                }
            }
        } catch (NullPointerException e) {
            CamLog.w(FaceDetector.TAG, "zoomInOut NullPointerException:", e);
        }
    }

    public void setScaleDetectorListener(Mediator mediator) {
        this.mScaleDetector = new ScaleGestureDetector(mediator.getApplicationContext(), new ScaleListener(mediator));
    }

    public void releaseScaleDetectorListener() {
        this.mScaleDetector = null;
    }

    public void setGestureDetectorListener(Mediator mediator) {
        this.mGestureDetector = new GestureDetector(mediator.getApplicationContext(), new GestureListener(mediator));
    }

    public void releaseGestureDetectorListener() {
        this.mGestureDetector = null;
    }

    public boolean isPIPFrameSplitView(Mediator mediator) {
        int currentPIPMask = mediator.getCurrentPIPMask();
        return currentPIPMask == STEP_LIMIT || currentPIPMask == 9;
    }

    public boolean isTouchSmartCoverView() {
        int mCover_width = CameraConstants.smartCoverSizeHeight;
        int mCover_x = (CameraConstants.LCD_SIZE_HEIGHT - mCover_width) / 2;
        return this.x > mCover_x && this.x < mCover_width + mCover_x && this.y < CameraConstants.smartCoverSizeWidth;
    }

    private boolean checkIgnoreTouchEvent(int coordinate, Mediator mediator) {
        int margin = (int) (((float) mediator.getResources().getDrawable(R.drawable.focus_succeed_taf).getMinimumWidth()) * SCALE_SIZE);
        if (coordinate < margin || coordinate > CameraConstants.LCD_SIZE_HEIGHT - margin) {
            return true;
        }
        return false;
    }

    private boolean checkSmartZoomFocusViewInScreen(int x, int y, Mediator mediator) {
        int halfWidth = getFocusViewHalfWidth(mediator);
        if (x < halfWidth || x > CameraConstants.LCD_SIZE_HEIGHT - halfWidth || y < halfWidth || y > CameraConstants.LCD_SIZE_WIDTH - halfWidth) {
            return false;
        }
        return true;
    }

    private void setSmartZoomFocusViewPosition(int x, int y, Mediator mediator) {
        int halfWidth = getFocusViewHalfWidth(mediator);
        if (!checkSmartZoomFocusViewInScreen(x, y, mediator)) {
            if (x < halfWidth) {
                x = halfWidth;
            } else if (x > CameraConstants.LCD_SIZE_HEIGHT - halfWidth) {
                x = CameraConstants.LCD_SIZE_HEIGHT - halfWidth;
            }
            if (y < halfWidth) {
                y = halfWidth;
            } else if (y > CameraConstants.LCD_SIZE_WIDTH - halfWidth) {
                y = CameraConstants.LCD_SIZE_WIDTH - halfWidth;
            }
        }
        mediator.setSmartZoomFocusViewPosition(x, y);
    }

    private int getFocusViewHalfWidth(Mediator mediator) {
        return mediator.getResources().getDrawable(R.drawable.focus_smart_zoom).getMinimumWidth() / 2;
    }
}
