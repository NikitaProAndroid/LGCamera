package com.lge.camera.components;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Util;
import com.lge.morpho.app.morphopanorama.PanoramaApplication;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.util.Timer;
import java.util.TimerTask;

public abstract class BarView extends RelativeLayout {
    public static int CURSOR_MIN_STEP = 0;
    public static int CURSOR_ONE_STEP_MINUS = 0;
    public static final int CURSOR_ONE_STEP_MINUS_BUTTON = -1;
    public static int CURSOR_ONE_STEP_PLUS = 0;
    public static final int CURSOR_ONE_STEP_PLUS_BUTTON = 1;
    public static final String NONE_SETTING_KEY = "none";
    public static final int TYPE_3D_DEPTH_BAR = 3;
    public static final int TYPE_BEAUTYSHOT_BAR = 2;
    public static final int TYPE_BRIGHTNESS_BAR = 1;
    public static final int TYPE_MANUAL_FOCUS_BAR = 8;
    public static final int TYPE_SETTING_3D_DEPTH_BAR = 7;
    public static final int TYPE_SETTING_BEAUTYSHOT_BAR = 6;
    public static final int TYPE_SETTING_BRIGHTNESS_BAR = 5;
    public static final int TYPE_SETTING_BRIGHTNESS_BAR_EXPAND = 9;
    public static final int TYPE_SETTING_ZOOM_BAR = 4;
    public static final int TYPE_ZOOM_BAR = 0;
    protected float CURSOR_HEIGHT;
    protected float CURSOR_HEIGHT_PORT;
    protected int CURSOR_POS_HEIGHT;
    protected int CURSOR_POS_HEIGHT_PORT;
    protected int CURSOR_POS_WIDTH;
    protected float CURSOR_WIDTH;
    protected long LONG_PRESS_EVENT_DELAY;
    protected long LONG_PRESS_JUDGE_TIMEOUT;
    protected int MAX_CURSOR_POS;
    protected int MAX_CURSOR_POS_PORT;
    protected int MIN_CURSOR_POS;
    protected int RELEASE_EXPAND_BOTTOM;
    protected int RELEASE_EXPAND_LEFT;
    protected int RELEASE_EXPAND_RIGHT;
    protected int RELEASE_EXPAND_TOP;
    protected String barSettingCommand;
    protected String barSettingKey;
    protected int cursorBGResId;
    protected int cursorResId;
    protected BarAction mBarAction;
    public int mBarType;
    protected Timer mButtonCheckTimer;
    protected int mCursorMaxStep;
    protected boolean mInitial;
    public OnTouchListener mOnButtonTouchListener;
    public OnTouchListener mOnLineTouchListener;
    protected int mValue;
    protected int minusButtonResId;
    protected int minusButtonViewResId;
    protected int plusButtonResId;
    protected int plusButtonViewResId;

    protected abstract RotateLayout getBarLayout();

    protected abstract View getBarParentLayout();

    public abstract void getBarSettingValue();

    public abstract void releaseBar();

    public abstract void setLayoutDimension();

    static {
        CURSOR_MIN_STEP = 0;
        CURSOR_ONE_STEP_MINUS = CURSOR_ONE_STEP_MINUS_BUTTON;
        CURSOR_ONE_STEP_PLUS = TYPE_BRIGHTNESS_BAR;
    }

    public int getCursorValue() {
        return this.mValue;
    }

    public void setCursorValue(int mValue) {
        this.mValue = mValue;
    }

    public BarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mBarType = 0;
        this.LONG_PRESS_JUDGE_TIMEOUT = 1000;
        this.LONG_PRESS_EVENT_DELAY = 200;
        this.mCursorMaxStep = 12;
        this.RELEASE_EXPAND_LEFT = 0;
        this.RELEASE_EXPAND_TOP = 0;
        this.RELEASE_EXPAND_RIGHT = 0;
        this.RELEASE_EXPAND_BOTTOM = 0;
        this.cursorResId = R.id.adj_cursor;
        this.cursorBGResId = R.id.adj_cursor_bg;
        this.minusButtonResId = R.id.adj_minus_button;
        this.minusButtonViewResId = R.id.adj_minus_button_view;
        this.plusButtonResId = R.id.adj_plus_button;
        this.plusButtonViewResId = R.id.adj_plus_button_view;
        this.barSettingKey = Setting.KEY_BRIGHTNESS;
        this.barSettingCommand = Command.SET_BRIGHTNESS;
        this.mInitial = false;
        this.mBarAction = null;
        this.mOnLineTouchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (!BarView.this.mInitial || BarView.this.getVisibility() != 0 || BarView.this.mBarAction.isSettingViewRemoving()) {
                    return false;
                }
                int value = getValueForLineTouchListener(event.getX(), event.getY());
                switch (event.getAction()) {
                    case LGKeyRec.EVENT_INVALID /*0*/:
                        BarView.this.disallowTouchInParentView(v);
                        BarView.this.findViewById(BarView.this.cursorResId).sendAccessibilityEvent(BarView.TYPE_MANUAL_FOCUS_BAR);
                        BarView.this.findViewById(BarView.this.cursorResId).setPressed(true);
                        BarView.this.updateBarWithValue(value, false);
                        break;
                    case BarView.TYPE_BRIGHTNESS_BAR /*1*/:
                    case BarView.TYPE_3D_DEPTH_BAR /*3*/:
                        BarView.this.findViewById(BarView.this.cursorResId).setPressed(false);
                        BarView.this.updateBarWithValue(value, true);
                        break;
                    case BarView.TYPE_BEAUTYSHOT_BAR /*2*/:
                        BarView.this.findViewById(BarView.this.cursorResId).sendAccessibilityEvent(4096);
                        BarView.this.findViewById(BarView.this.cursorResId).setPressed(true);
                        BarView.this.updateBarWithValue(value, false);
                        break;
                }
                BarView.this.resetDisplayTimeout();
                return true;
            }

            private int getValueForLineTouchListener(float x, float y) {
                try {
                    switch (BarView.this.mBarType) {
                        case LGKeyRec.EVENT_INVALID /*0*/:
                        case BarView.TYPE_BRIGHTNESS_BAR /*1*/:
                        case BarView.TYPE_BEAUTYSHOT_BAR /*2*/:
                        case BarView.TYPE_3D_DEPTH_BAR /*3*/:
                        case BarView.TYPE_SETTING_ZOOM_BAR /*4*/:
                        case BarView.TYPE_SETTING_BRIGHTNESS_BAR /*5*/:
                        case BarView.TYPE_SETTING_BEAUTYSHOT_BAR /*6*/:
                        case BarView.TYPE_SETTING_3D_DEPTH_BAR /*7*/:
                        case BarView.TYPE_MANUAL_FOCUS_BAR /*8*/:
                            int marginDown;
                            float curLineLevel;
                            if (BarView.this.isLandscape()) {
                                marginDown = (int) (((float) BarView.this.MAX_CURSOR_POS) - (BarView.this.CURSOR_HEIGHT / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK));
                                curLineLevel = ((float) BarView.this.CURSOR_POS_HEIGHT) / ((float) BarView.this.getCursorMaxStep());
                            } else {
                                marginDown = (int) (((float) BarView.this.MAX_CURSOR_POS_PORT) - (BarView.this.CURSOR_HEIGHT_PORT / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK));
                                curLineLevel = ((float) BarView.this.CURSOR_POS_HEIGHT_PORT) / ((float) BarView.this.getCursorMaxStep());
                            }
                            return (int) ((((float) marginDown) - y) / curLineLevel);
                        case BarView.TYPE_SETTING_BRIGHTNESS_BAR_EXPAND /*9*/:
                            return (int) ((x - (BarView.this.CURSOR_WIDTH / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK)) / (((float) BarView.this.CURSOR_POS_WIDTH) / ((float) BarView.this.getCursorMaxStep())));
                        default:
                            return 0;
                    }
                } catch (ArithmeticException e) {
                    CamLog.d(FaceDetector.TAG, "getCursorMaxStep() = " + BarView.this.getCursorMaxStep());
                    CamLog.e(FaceDetector.TAG, "ArithmeticException:", e);
                    return 0;
                }
            }
        };
        this.mOnButtonTouchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return BarView.this.onButtonTouch(v, event);
            }
        };
    }

    public BarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mBarType = 0;
        this.LONG_PRESS_JUDGE_TIMEOUT = 1000;
        this.LONG_PRESS_EVENT_DELAY = 200;
        this.mCursorMaxStep = 12;
        this.RELEASE_EXPAND_LEFT = 0;
        this.RELEASE_EXPAND_TOP = 0;
        this.RELEASE_EXPAND_RIGHT = 0;
        this.RELEASE_EXPAND_BOTTOM = 0;
        this.cursorResId = R.id.adj_cursor;
        this.cursorBGResId = R.id.adj_cursor_bg;
        this.minusButtonResId = R.id.adj_minus_button;
        this.minusButtonViewResId = R.id.adj_minus_button_view;
        this.plusButtonResId = R.id.adj_plus_button;
        this.plusButtonViewResId = R.id.adj_plus_button_view;
        this.barSettingKey = Setting.KEY_BRIGHTNESS;
        this.barSettingCommand = Command.SET_BRIGHTNESS;
        this.mInitial = false;
        this.mBarAction = null;
        this.mOnLineTouchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (!BarView.this.mInitial || BarView.this.getVisibility() != 0 || BarView.this.mBarAction.isSettingViewRemoving()) {
                    return false;
                }
                int value = getValueForLineTouchListener(event.getX(), event.getY());
                switch (event.getAction()) {
                    case LGKeyRec.EVENT_INVALID /*0*/:
                        BarView.this.disallowTouchInParentView(v);
                        BarView.this.findViewById(BarView.this.cursorResId).sendAccessibilityEvent(BarView.TYPE_MANUAL_FOCUS_BAR);
                        BarView.this.findViewById(BarView.this.cursorResId).setPressed(true);
                        BarView.this.updateBarWithValue(value, false);
                        break;
                    case BarView.TYPE_BRIGHTNESS_BAR /*1*/:
                    case BarView.TYPE_3D_DEPTH_BAR /*3*/:
                        BarView.this.findViewById(BarView.this.cursorResId).setPressed(false);
                        BarView.this.updateBarWithValue(value, true);
                        break;
                    case BarView.TYPE_BEAUTYSHOT_BAR /*2*/:
                        BarView.this.findViewById(BarView.this.cursorResId).sendAccessibilityEvent(4096);
                        BarView.this.findViewById(BarView.this.cursorResId).setPressed(true);
                        BarView.this.updateBarWithValue(value, false);
                        break;
                }
                BarView.this.resetDisplayTimeout();
                return true;
            }

            private int getValueForLineTouchListener(float x, float y) {
                try {
                    switch (BarView.this.mBarType) {
                        case LGKeyRec.EVENT_INVALID /*0*/:
                        case BarView.TYPE_BRIGHTNESS_BAR /*1*/:
                        case BarView.TYPE_BEAUTYSHOT_BAR /*2*/:
                        case BarView.TYPE_3D_DEPTH_BAR /*3*/:
                        case BarView.TYPE_SETTING_ZOOM_BAR /*4*/:
                        case BarView.TYPE_SETTING_BRIGHTNESS_BAR /*5*/:
                        case BarView.TYPE_SETTING_BEAUTYSHOT_BAR /*6*/:
                        case BarView.TYPE_SETTING_3D_DEPTH_BAR /*7*/:
                        case BarView.TYPE_MANUAL_FOCUS_BAR /*8*/:
                            int marginDown;
                            float curLineLevel;
                            if (BarView.this.isLandscape()) {
                                marginDown = (int) (((float) BarView.this.MAX_CURSOR_POS) - (BarView.this.CURSOR_HEIGHT / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK));
                                curLineLevel = ((float) BarView.this.CURSOR_POS_HEIGHT) / ((float) BarView.this.getCursorMaxStep());
                            } else {
                                marginDown = (int) (((float) BarView.this.MAX_CURSOR_POS_PORT) - (BarView.this.CURSOR_HEIGHT_PORT / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK));
                                curLineLevel = ((float) BarView.this.CURSOR_POS_HEIGHT_PORT) / ((float) BarView.this.getCursorMaxStep());
                            }
                            return (int) ((((float) marginDown) - y) / curLineLevel);
                        case BarView.TYPE_SETTING_BRIGHTNESS_BAR_EXPAND /*9*/:
                            return (int) ((x - (BarView.this.CURSOR_WIDTH / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK)) / (((float) BarView.this.CURSOR_POS_WIDTH) / ((float) BarView.this.getCursorMaxStep())));
                        default:
                            return 0;
                    }
                } catch (ArithmeticException e) {
                    CamLog.d(FaceDetector.TAG, "getCursorMaxStep() = " + BarView.this.getCursorMaxStep());
                    CamLog.e(FaceDetector.TAG, "ArithmeticException:", e);
                    return 0;
                }
            }
        };
        this.mOnButtonTouchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return BarView.this.onButtonTouch(v, event);
            }
        };
    }

    public BarView(Context context) {
        super(context);
        this.mBarType = 0;
        this.LONG_PRESS_JUDGE_TIMEOUT = 1000;
        this.LONG_PRESS_EVENT_DELAY = 200;
        this.mCursorMaxStep = 12;
        this.RELEASE_EXPAND_LEFT = 0;
        this.RELEASE_EXPAND_TOP = 0;
        this.RELEASE_EXPAND_RIGHT = 0;
        this.RELEASE_EXPAND_BOTTOM = 0;
        this.cursorResId = R.id.adj_cursor;
        this.cursorBGResId = R.id.adj_cursor_bg;
        this.minusButtonResId = R.id.adj_minus_button;
        this.minusButtonViewResId = R.id.adj_minus_button_view;
        this.plusButtonResId = R.id.adj_plus_button;
        this.plusButtonViewResId = R.id.adj_plus_button_view;
        this.barSettingKey = Setting.KEY_BRIGHTNESS;
        this.barSettingCommand = Command.SET_BRIGHTNESS;
        this.mInitial = false;
        this.mBarAction = null;
        this.mOnLineTouchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (!BarView.this.mInitial || BarView.this.getVisibility() != 0 || BarView.this.mBarAction.isSettingViewRemoving()) {
                    return false;
                }
                int value = getValueForLineTouchListener(event.getX(), event.getY());
                switch (event.getAction()) {
                    case LGKeyRec.EVENT_INVALID /*0*/:
                        BarView.this.disallowTouchInParentView(v);
                        BarView.this.findViewById(BarView.this.cursorResId).sendAccessibilityEvent(BarView.TYPE_MANUAL_FOCUS_BAR);
                        BarView.this.findViewById(BarView.this.cursorResId).setPressed(true);
                        BarView.this.updateBarWithValue(value, false);
                        break;
                    case BarView.TYPE_BRIGHTNESS_BAR /*1*/:
                    case BarView.TYPE_3D_DEPTH_BAR /*3*/:
                        BarView.this.findViewById(BarView.this.cursorResId).setPressed(false);
                        BarView.this.updateBarWithValue(value, true);
                        break;
                    case BarView.TYPE_BEAUTYSHOT_BAR /*2*/:
                        BarView.this.findViewById(BarView.this.cursorResId).sendAccessibilityEvent(4096);
                        BarView.this.findViewById(BarView.this.cursorResId).setPressed(true);
                        BarView.this.updateBarWithValue(value, false);
                        break;
                }
                BarView.this.resetDisplayTimeout();
                return true;
            }

            private int getValueForLineTouchListener(float x, float y) {
                try {
                    switch (BarView.this.mBarType) {
                        case LGKeyRec.EVENT_INVALID /*0*/:
                        case BarView.TYPE_BRIGHTNESS_BAR /*1*/:
                        case BarView.TYPE_BEAUTYSHOT_BAR /*2*/:
                        case BarView.TYPE_3D_DEPTH_BAR /*3*/:
                        case BarView.TYPE_SETTING_ZOOM_BAR /*4*/:
                        case BarView.TYPE_SETTING_BRIGHTNESS_BAR /*5*/:
                        case BarView.TYPE_SETTING_BEAUTYSHOT_BAR /*6*/:
                        case BarView.TYPE_SETTING_3D_DEPTH_BAR /*7*/:
                        case BarView.TYPE_MANUAL_FOCUS_BAR /*8*/:
                            int marginDown;
                            float curLineLevel;
                            if (BarView.this.isLandscape()) {
                                marginDown = (int) (((float) BarView.this.MAX_CURSOR_POS) - (BarView.this.CURSOR_HEIGHT / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK));
                                curLineLevel = ((float) BarView.this.CURSOR_POS_HEIGHT) / ((float) BarView.this.getCursorMaxStep());
                            } else {
                                marginDown = (int) (((float) BarView.this.MAX_CURSOR_POS_PORT) - (BarView.this.CURSOR_HEIGHT_PORT / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK));
                                curLineLevel = ((float) BarView.this.CURSOR_POS_HEIGHT_PORT) / ((float) BarView.this.getCursorMaxStep());
                            }
                            return (int) ((((float) marginDown) - y) / curLineLevel);
                        case BarView.TYPE_SETTING_BRIGHTNESS_BAR_EXPAND /*9*/:
                            return (int) ((x - (BarView.this.CURSOR_WIDTH / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK)) / (((float) BarView.this.CURSOR_POS_WIDTH) / ((float) BarView.this.getCursorMaxStep())));
                        default:
                            return 0;
                    }
                } catch (ArithmeticException e) {
                    CamLog.d(FaceDetector.TAG, "getCursorMaxStep() = " + BarView.this.getCursorMaxStep());
                    CamLog.e(FaceDetector.TAG, "ArithmeticException:", e);
                    return 0;
                }
            }
        };
        this.mOnButtonTouchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return BarView.this.onButtonTouch(v, event);
            }
        };
    }

    public void unbind() {
        this.mOnButtonTouchListener = null;
        this.mOnLineTouchListener = null;
        setListener(false);
        this.mBarAction = null;
    }

    public void setBarValueInitialization(long longPressTime, long longPressDelay, int maxStep, int minStep, int onStepMinus, int oneStepPlus) {
        this.LONG_PRESS_JUDGE_TIMEOUT = longPressTime;
        this.LONG_PRESS_EVENT_DELAY = longPressDelay;
        this.mCursorMaxStep = maxStep;
        CURSOR_MIN_STEP = minStep;
        CURSOR_ONE_STEP_MINUS = onStepMinus;
        CURSOR_ONE_STEP_PLUS = oneStepPlus;
    }

    public void setBarResources(int barType, int cursor, int cursorBG, int minus, int minusView, int plus, int plusView) {
        this.mBarType = barType;
        this.cursorResId = cursor;
        this.cursorBGResId = cursorBG;
        this.minusButtonResId = minus;
        this.minusButtonViewResId = minusView;
        this.plusButtonResId = plus;
        this.plusButtonViewResId = plusView;
    }

    public void setBarSettingCommand(String key, String command) {
        this.barSettingKey = key;
        this.barSettingCommand = command;
    }

    public void initBar(BarAction barAction) {
        setVisibility(TYPE_SETTING_ZOOM_BAR);
        this.mBarAction = barAction;
        setLayoutDimension();
        findViewById(this.cursorResId).setFocusable(false);
        getBarSettingValue();
        this.mInitial = true;
        int degree = this.mBarAction.getDegreeInBarAction();
        this.mBarAction.setDegreeInBarAction(this.minusButtonResId, degree, false);
        this.mBarAction.setDegreeInBarAction(this.plusButtonResId, degree, false);
        startRotation(degree, false);
        updateAllBars();
    }

    public void setListener(boolean set) {
        if (!this.mInitial) {
            return;
        }
        if (set) {
            if (this.mBarType == TYPE_SETTING_BRIGHTNESS_BAR_EXPAND) {
                findViewById(this.plusButtonViewResId).setOnTouchListener(this.mOnButtonTouchListener);
                findViewById(this.minusButtonViewResId).setOnTouchListener(this.mOnButtonTouchListener);
            }
            findViewById(this.cursorBGResId).setOnTouchListener(this.mOnLineTouchListener);
            return;
        }
        if (this.mBarType == TYPE_SETTING_BRIGHTNESS_BAR_EXPAND) {
            findViewById(this.plusButtonViewResId).setOnTouchListener(null);
            findViewById(this.minusButtonViewResId).setOnTouchListener(null);
        }
        findViewById(this.cursorBGResId).setOnTouchListener(null);
    }

    public void updateAllBars() {
        if (this.mInitial && this.mBarAction != null) {
            this.mBarAction.updateAllBars(this.mBarType, this.mValue);
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        disallowTouchInParentView(this);
        return super.dispatchTouchEvent(ev);
    }

    public void updateBar(int step, boolean others, boolean isLongTouch, boolean actionEnd) {
        int lValue = this.mValue;
        if (this.mInitial && step != 0 && !actionEnd && this.mBarAction != null && this.mBarAction.isPreviewing()) {
            int updatedValue = lValue + step;
            if (updatedValue > this.mCursorMaxStep) {
                updatedValue = this.mCursorMaxStep;
            }
            if (updatedValue < 0) {
                updatedValue = 0;
            }
            if (updatedValue != lValue) {
                setCursorValue(updatedValue);
                this.mBarAction.runOnUiThread(new Runnable() {
                    public void run() {
                        BarView.this.mBarAction.removePostRunnable(this);
                        BarView.this.setCursor(BarView.this.getCursorValue());
                    }
                });
                if (isLongTouch) {
                    setBarSettingValue(this.barSettingKey, updatedValue);
                    if (this.mBarType == TYPE_SETTING_BEAUTYSHOT_BAR) {
                        this.mBarAction.doCommandNoneParameter(this.barSettingCommand);
                    } else {
                        this.mBarAction.doCommand(this.barSettingCommand);
                    }
                } else {
                    this.mBarAction.postOnUiThread(new Runnable() {
                        public void run() {
                            BarView.this.mBarAction.removePostRunnable(this);
                            BarView.this.setBarSettingValue(BarView.this.barSettingKey, BarView.this.getCursorValue());
                            if (BarView.this.mBarType == BarView.TYPE_SETTING_BEAUTYSHOT_BAR) {
                                BarView.this.mBarAction.doCommandNoneParameter(BarView.this.barSettingCommand);
                            } else {
                                BarView.this.mBarAction.doCommand(BarView.this.barSettingCommand);
                            }
                        }
                    });
                }
                resetDisplayTimeout();
                updateAllBars();
            }
        }
    }

    public void updateBarWithValue(int value, boolean actionEnd) {
        int lValue = this.mValue;
        if (!this.mInitial || !this.mBarAction.isPreviewing()) {
            return;
        }
        if (actionEnd) {
            setBarSettingValue(this.barSettingKey, lValue);
            return;
        }
        lValue = value;
        if (lValue > this.mCursorMaxStep) {
            lValue = this.mCursorMaxStep;
        }
        if (lValue < CURSOR_MIN_STEP) {
            lValue = CURSOR_MIN_STEP;
        }
        setCursor(lValue);
        setCursorValue(lValue);
        Bundle bundle = new Bundle();
        bundle.putInt("mValue", lValue);
        if (this.mBarType != TYPE_SETTING_BRIGHTNESS_BAR_EXPAND && lValue == 0) {
            setBarSettingValue(this.barSettingKey, lValue);
        }
        if (this.mBarType == TYPE_SETTING_BEAUTYSHOT_BAR || this.mBarType == TYPE_BEAUTYSHOT_BAR) {
            this.mBarAction.doCommandNoneParameter(this.barSettingCommand, bundle);
        } else {
            this.mBarAction.doCommand(this.barSettingCommand, null, bundle);
        }
        resetDisplayTimeout();
        updateAllBars();
    }

    public void updateBarWithTimer(View v, int step, MotionEvent event, boolean others, boolean isLongTouch, boolean actionEnd) {
        if (this.mInitial) {
            updateBar(step, others, isLongTouch, actionEnd);
            if (!isLongTouch && !actionEnd) {
                final View view = v;
                final int i = step;
                final MotionEvent motionEvent = event;
                final boolean z = others;
                final boolean z2 = actionEnd;
                TimerTask anonymousClass3 = new TimerTask() {
                    public void run() {
                        BarView.this.updateBarWithTimer(view, i, motionEvent, z, true, z2);
                    }
                };
                startTimerTask(r0, this.LONG_PRESS_JUDGE_TIMEOUT, this.LONG_PRESS_EVENT_DELAY, v);
            }
        }
    }

    public void startTimerTask(TimerTask t, long judge, long interval, View v) {
        if (!this.mInitial || this.mButtonCheckTimer != null) {
            return;
        }
        if (v == null || v.isPressed()) {
            this.mButtonCheckTimer = new Timer("timer_long_press_check");
            this.mButtonCheckTimer.scheduleAtFixedRate(t, judge, interval);
        }
    }

    public void stopTimerTask() {
        if (this.mButtonCheckTimer != null) {
            this.mButtonCheckTimer.cancel();
            this.mButtonCheckTimer.purge();
            this.mButtonCheckTimer = null;
        }
    }

    public void showControl(boolean visible) {
        int i = 0;
        if (this.mInitial) {
            CamLog.d(FaceDetector.TAG, "BarView-showControl:mValue = " + this.mValue + " visible=" + visible);
            if (visible) {
                setDisplayTimeout();
            } else {
                findViewById(this.cursorResId).setPressed(false);
                findViewById(this.plusButtonViewResId).setPressed(false);
                findViewById(this.minusButtonViewResId).setPressed(false);
                stopTimerTask();
                if (getVisibility() == 0) {
                    releaseBar();
                }
            }
            if (!visible) {
                i = TYPE_MANUAL_FOCUS_BAR;
            }
            setVisibility(i);
        }
    }

    public boolean isBarVisible() {
        return getVisibility() == 0;
    }

    public void setDisplayTimeout() {
        if (this.mInitial) {
            this.mBarAction.doCommandDelayed(Command.EXIT_ZOOM_BRIGHTNESS_INTERACTION, this.mBarType == TYPE_MANUAL_FOCUS_BAR ? CameraConstants.PIP_SUBWINDOW_RESIZE_HANDLER_DURATION : PanoramaApplication.SENSOR_CORRECTION_TIME_EVERYTIME);
        }
    }

    public void resetDisplayTimeout() {
        if (!this.mBarAction.isPausing()) {
            this.mBarAction.removeScheduledCommand(Command.EXIT_ZOOM_BRIGHTNESS_INTERACTION);
            setDisplayTimeout();
        }
    }

    public void setBarValue(int value) {
        setCursorValue(value);
        refreshBar();
    }

    public void refreshBar() {
        setCursor(this.mValue);
    }

    public void setCursor(final int value) {
        if (this.mInitial) {
            this.mBarAction.runOnUiThread(new Runnable() {
                public void run() {
                    BarView.this.mBarAction.removePostRunnable(this);
                    try {
                        ImageView cursor = (ImageView) BarView.this.findViewById(BarView.this.cursorResId);
                        LayoutParams param = (LayoutParams) cursor.getLayoutParams();
                        int position;
                        switch (BarView.this.mBarType) {
                            case LGKeyRec.EVENT_INVALID /*0*/:
                            case BarView.TYPE_BRIGHTNESS_BAR /*1*/:
                            case BarView.TYPE_BEAUTYSHOT_BAR /*2*/:
                            case BarView.TYPE_3D_DEPTH_BAR /*3*/:
                            case BarView.TYPE_SETTING_ZOOM_BAR /*4*/:
                            case BarView.TYPE_SETTING_BRIGHTNESS_BAR /*5*/:
                            case BarView.TYPE_SETTING_BEAUTYSHOT_BAR /*6*/:
                            case BarView.TYPE_SETTING_3D_DEPTH_BAR /*7*/:
                            case BarView.TYPE_MANUAL_FOCUS_BAR /*8*/:
                                int marginDown;
                                int curLevel;
                                if (BarView.this.isLandscape()) {
                                    marginDown = (int) (((float) (BarView.this.MAX_CURSOR_POS - BarView.this.MIN_CURSOR_POS)) - BarView.this.CURSOR_HEIGHT);
                                    curLevel = (int) (((float) value) * (((float) BarView.this.CURSOR_POS_HEIGHT) / ((float) BarView.this.getCursorMaxStep())));
                                } else {
                                    marginDown = (int) (((float) (BarView.this.MAX_CURSOR_POS_PORT - BarView.this.MIN_CURSOR_POS)) - BarView.this.CURSOR_HEIGHT_PORT);
                                    curLevel = (int) (((float) value) * (((float) BarView.this.CURSOR_POS_HEIGHT_PORT) / ((float) BarView.this.getCursorMaxStep())));
                                }
                                position = marginDown - curLevel;
                                if (position > (BarView.this.isLandscape() ? BarView.this.MAX_CURSOR_POS : BarView.this.MAX_CURSOR_POS_PORT)) {
                                    if (BarView.this.isLandscape()) {
                                        position = BarView.this.MAX_CURSOR_POS;
                                    } else {
                                        position = BarView.this.MAX_CURSOR_POS_PORT;
                                    }
                                }
                                if (position < BarView.this.MIN_CURSOR_POS) {
                                    position = BarView.this.MIN_CURSOR_POS;
                                }
                                param.topMargin = position;
                                break;
                            case BarView.TYPE_SETTING_BRIGHTNESS_BAR_EXPAND /*9*/:
                                position = BarView.this.MIN_CURSOR_POS + ((int) (((float) value) * (((float) BarView.this.CURSOR_POS_WIDTH) / ((float) BarView.this.getCursorMaxStep()))));
                                if (position > BarView.this.MAX_CURSOR_POS) {
                                    position = BarView.this.MAX_CURSOR_POS;
                                }
                                if (position < BarView.this.MIN_CURSOR_POS) {
                                    position = BarView.this.MIN_CURSOR_POS;
                                }
                                param.leftMargin = position;
                                break;
                        }
                        cursor.setLayoutParams(param);
                    } catch (ArithmeticException e) {
                        CamLog.d(FaceDetector.TAG, "getCursorMaxStep() = " + BarView.this.getCursorMaxStep());
                        CamLog.e(FaceDetector.TAG, "ArithmeticException:", e);
                    }
                }
            });
        }
    }

    public void resetCursor(int value) {
        setCursor(value);
        updateAllBars();
    }

    public void resetValue(int value) {
        setCursorValue(value);
    }

    public int getValue() {
        return this.mValue;
    }

    protected boolean onButtonTouch(View v, MotionEvent event) {
        if (!this.mInitial || getVisibility() != 0 || this.mBarAction.isSettingViewRemoving()) {
            return false;
        }
        switch (event.getAction()) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                disallowTouchInParentView(v);
                v.setPressed(true);
                if (!isTouchUpAreaOfButton(v, event)) {
                    v.setPressed(false);
                    stopTimerTask();
                    break;
                }
                v.sendAccessibilityEvent(TYPE_MANUAL_FOCUS_BAR);
                if (!isPlusButton(v)) {
                    updateBarWithTimer(v, CURSOR_ONE_STEP_MINUS_BUTTON, event, false, false, false);
                    break;
                }
                updateBarWithTimer(v, TYPE_BRIGHTNESS_BAR, event, false, false, false);
                break;
            case TYPE_BRIGHTNESS_BAR /*1*/:
            case TYPE_3D_DEPTH_BAR /*3*/:
                v.playSoundEffect(0);
                v.setPressed(false);
                stopTimerTask();
                updateBar(0, false, true, true);
                break;
            case TYPE_BEAUTYSHOT_BAR /*2*/:
                if (!isTouchUpAreaOfButton(v, event)) {
                    v.setPressed(false);
                    stopTimerTask();
                    break;
                }
                break;
        }
        resetDisplayTimeout();
        return true;
    }

    protected boolean isPlusButton(View v) {
        return v.getId() == this.plusButtonViewResId;
    }

    public boolean isTouchUpAreaOfButton(View v, MotionEvent event) {
        if (event.getX() <= ((float) (-this.RELEASE_EXPAND_LEFT)) || event.getX() >= ((float) (v.getWidth() + this.RELEASE_EXPAND_RIGHT)) || event.getY() <= ((float) (-this.RELEASE_EXPAND_TOP)) || event.getY() >= ((float) (v.getWidth() + this.RELEASE_EXPAND_BOTTOM))) {
            return false;
        }
        return true;
    }

    protected void disallowTouchInParentView(View view) {
    }

    public void setCursorMaxStep(int maxStep) {
        this.mCursorMaxStep = maxStep;
    }

    public int getCursorMaxStep() {
        return this.mCursorMaxStep;
    }

    public boolean isLandscape() {
        if (this.mBarAction.getOrientation() == 0 || this.mBarAction.getOrientation() == TYPE_BEAUTYSHOT_BAR) {
            return true;
        }
        return (this.mBarAction.getOrientation() == TYPE_BRIGHTNESS_BAR || this.mBarAction.getOrientation() != TYPE_3D_DEPTH_BAR) ? false : false;
    }

    public void setBarSettingValue(String key, int value) {
        if (!NONE_SETTING_KEY.equals(key)) {
            this.mBarAction.setSetting(key, Integer.toString(value));
        }
    }

    protected boolean isEqualsDegree(int current, int input) {
        return Util.isEqualDegree(getResources(), current, input);
    }

    protected int convertLayoutDegree(int degree) {
        return Util.convertLayoutDegree(getResources(), degree);
    }

    protected int convertDegree(int degree) {
        return Util.convertDegree(getResources(), degree);
    }

    public void startRotation(int degree, boolean animation) {
        RotateLayout rl = getBarLayout();
        if (rl != null) {
            rl.rotateLayout(degree);
            rl.requestLayout();
            rl.invalidate();
            View parent = getBarParentLayout();
            if (parent != null) {
                LayoutParams param = (LayoutParams) parent.getLayoutParams();
                if (isEqualsDegree(degree, 0)) {
                    param.rightMargin = 0;
                    param.leftMargin = 0;
                    param.bottomMargin = this.mBarAction.getPixelFromDimens(R.dimen.zoom_control_marginRight);
                    param.topMargin = 0;
                } else if (isEqualsDegree(degree, 90)) {
                    param.rightMargin = this.mBarAction.getPixelFromDimens(R.dimen.zoom_control_margin_indi_height);
                    param.leftMargin = 0;
                    param.bottomMargin = 0;
                    param.topMargin = 0;
                } else if (isEqualsDegree(degree, MediaProviderUtils.ROTATION_180)) {
                    param.rightMargin = 0;
                    param.leftMargin = 0;
                    param.bottomMargin = 0;
                    param.topMargin = this.mBarAction.getPixelFromDimens(R.dimen.zoom_control_marginLeft);
                } else if (isEqualsDegree(degree, Tag.IMAGE_DESCRIPTION)) {
                    param.rightMargin = 0;
                    param.leftMargin = this.mBarAction.getPixelFromDimens(R.dimen.zoom_control_margin_indi_height);
                    param.bottomMargin = 0;
                    param.topMargin = 0;
                }
                parent.setLayoutParams(param);
            }
        }
    }
}
