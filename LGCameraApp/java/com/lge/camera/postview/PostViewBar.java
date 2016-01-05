package com.lge.camera.postview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.R;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class PostViewBar extends RelativeLayout {
    protected static final int sCursorMinStep = 0;
    protected float mCursorHeight;
    protected int mCursorMaxStep;
    protected int mCursorPosHeight;
    protected int mCursorPosWidth;
    protected float mCursorWidth;
    protected boolean mInitial;
    protected int mMaxCursorPos;
    protected int mMinCursorPos;
    public OnTouchListener mOnLineTouchListener;
    protected PostViewBarListener mPostviewBarListener;
    protected int mValue;

    public PostViewBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mCursorMaxStep = 5;
        this.mCursorHeight = 0.0f;
        this.mCursorPosHeight = 0;
        this.mCursorWidth = 0.0f;
        this.mCursorPosWidth = 0;
        this.mMinCursorPos = 0;
        this.mMaxCursorPos = 0;
        this.mInitial = false;
        this.mPostviewBarListener = null;
        this.mOnLineTouchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (!PostViewBar.this.mInitial || PostViewBar.this.getVisibility() != 0) {
                    return false;
                }
                int value = getValueForLineTouchListener(event.getX(), event.getY());
                View cursor = PostViewBar.this.findViewById(R.id.refocus_bar_handler_cursor);
                if (cursor == null) {
                    return false;
                }
                switch (event.getAction()) {
                    case LGKeyRec.EVENT_INVALID /*0*/:
                        cursor.sendAccessibilityEvent(8);
                        cursor.setPressed(true);
                        PostViewBar.this.updateBarWithValue(value, false);
                        break;
                    case LGKeyRec.EVENT_NO_MATCH /*1*/:
                    case LGKeyRec.EVENT_STARTED /*3*/:
                        cursor.setPressed(false);
                        PostViewBar.this.updateBarWithValue(value, true);
                        break;
                    case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                        cursor.sendAccessibilityEvent(4096);
                        cursor.setPressed(true);
                        PostViewBar.this.updateBarWithValue(value, false);
                        break;
                }
                return true;
            }

            private int getValueForLineTouchListener(float x, float y) {
                try {
                    int plusMargin = ((ImageView) PostViewBar.this.findViewById(R.id.refocus_bar_handler_plus)).getMeasuredHeight();
                    int marginDown = (int) (((float) PostViewBar.this.mMaxCursorPos) - (PostViewBar.this.mCursorHeight / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK));
                    if (PostViewBar.this.mCursorMaxStep == 0) {
                        return 0;
                    }
                    float curLineLevel = ((float) PostViewBar.this.mCursorPosHeight) / ((float) PostViewBar.this.mCursorMaxStep);
                    return (int) (Math.round(curLineLevel) == 0 ? 0.0f : ((((float) marginDown) - y) + ((float) plusMargin)) / curLineLevel);
                } catch (Exception e) {
                    CamLog.e(FaceDetector.TAG, "Exception:", e);
                    return 0;
                }
            }
        };
    }

    public PostViewBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCursorMaxStep = 5;
        this.mCursorHeight = 0.0f;
        this.mCursorPosHeight = 0;
        this.mCursorWidth = 0.0f;
        this.mCursorPosWidth = 0;
        this.mMinCursorPos = 0;
        this.mMaxCursorPos = 0;
        this.mInitial = false;
        this.mPostviewBarListener = null;
        this.mOnLineTouchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (!PostViewBar.this.mInitial || PostViewBar.this.getVisibility() != 0) {
                    return false;
                }
                int value = getValueForLineTouchListener(event.getX(), event.getY());
                View cursor = PostViewBar.this.findViewById(R.id.refocus_bar_handler_cursor);
                if (cursor == null) {
                    return false;
                }
                switch (event.getAction()) {
                    case LGKeyRec.EVENT_INVALID /*0*/:
                        cursor.sendAccessibilityEvent(8);
                        cursor.setPressed(true);
                        PostViewBar.this.updateBarWithValue(value, false);
                        break;
                    case LGKeyRec.EVENT_NO_MATCH /*1*/:
                    case LGKeyRec.EVENT_STARTED /*3*/:
                        cursor.setPressed(false);
                        PostViewBar.this.updateBarWithValue(value, true);
                        break;
                    case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                        cursor.sendAccessibilityEvent(4096);
                        cursor.setPressed(true);
                        PostViewBar.this.updateBarWithValue(value, false);
                        break;
                }
                return true;
            }

            private int getValueForLineTouchListener(float x, float y) {
                try {
                    int plusMargin = ((ImageView) PostViewBar.this.findViewById(R.id.refocus_bar_handler_plus)).getMeasuredHeight();
                    int marginDown = (int) (((float) PostViewBar.this.mMaxCursorPos) - (PostViewBar.this.mCursorHeight / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK));
                    if (PostViewBar.this.mCursorMaxStep == 0) {
                        return 0;
                    }
                    float curLineLevel = ((float) PostViewBar.this.mCursorPosHeight) / ((float) PostViewBar.this.mCursorMaxStep);
                    return (int) (Math.round(curLineLevel) == 0 ? 0.0f : ((((float) marginDown) - y) + ((float) plusMargin)) / curLineLevel);
                } catch (Exception e) {
                    CamLog.e(FaceDetector.TAG, "Exception:", e);
                    return 0;
                }
            }
        };
    }

    public PostViewBar(Context context) {
        super(context);
        this.mCursorMaxStep = 5;
        this.mCursorHeight = 0.0f;
        this.mCursorPosHeight = 0;
        this.mCursorWidth = 0.0f;
        this.mCursorPosWidth = 0;
        this.mMinCursorPos = 0;
        this.mMaxCursorPos = 0;
        this.mInitial = false;
        this.mPostviewBarListener = null;
        this.mOnLineTouchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (!PostViewBar.this.mInitial || PostViewBar.this.getVisibility() != 0) {
                    return false;
                }
                int value = getValueForLineTouchListener(event.getX(), event.getY());
                View cursor = PostViewBar.this.findViewById(R.id.refocus_bar_handler_cursor);
                if (cursor == null) {
                    return false;
                }
                switch (event.getAction()) {
                    case LGKeyRec.EVENT_INVALID /*0*/:
                        cursor.sendAccessibilityEvent(8);
                        cursor.setPressed(true);
                        PostViewBar.this.updateBarWithValue(value, false);
                        break;
                    case LGKeyRec.EVENT_NO_MATCH /*1*/:
                    case LGKeyRec.EVENT_STARTED /*3*/:
                        cursor.setPressed(false);
                        PostViewBar.this.updateBarWithValue(value, true);
                        break;
                    case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                        cursor.sendAccessibilityEvent(4096);
                        cursor.setPressed(true);
                        PostViewBar.this.updateBarWithValue(value, false);
                        break;
                }
                return true;
            }

            private int getValueForLineTouchListener(float x, float y) {
                try {
                    int plusMargin = ((ImageView) PostViewBar.this.findViewById(R.id.refocus_bar_handler_plus)).getMeasuredHeight();
                    int marginDown = (int) (((float) PostViewBar.this.mMaxCursorPos) - (PostViewBar.this.mCursorHeight / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK));
                    if (PostViewBar.this.mCursorMaxStep == 0) {
                        return 0;
                    }
                    float curLineLevel = ((float) PostViewBar.this.mCursorPosHeight) / ((float) PostViewBar.this.mCursorMaxStep);
                    return (int) (Math.round(curLineLevel) == 0 ? 0.0f : ((((float) marginDown) - y) + ((float) plusMargin)) / curLineLevel);
                } catch (Exception e) {
                    CamLog.e(FaceDetector.TAG, "Exception:", e);
                    return 0;
                }
            }
        };
    }

    public void unbind() {
        this.mOnLineTouchListener = null;
        setListener(false);
        this.mPostviewBarListener = null;
    }

    public void initBar(int maxStep, PostViewBarListener listener) {
        this.mPostviewBarListener = listener;
        setLayoutDimension();
        findViewById(R.id.refocus_bar_handler_cursor).setFocusable(false);
        this.mInitial = true;
        this.mCursorMaxStep = maxStep;
    }

    public void setLayoutDimension() {
        this.mMinCursorPos = 0;
        this.mMaxCursorPos = this.mPostviewBarListener.getPx(R.dimen.setting_adj_cursor_bg_height);
        this.mCursorHeight = (float) this.mPostviewBarListener.getPx(R.dimen.setting_adj_cursor_height);
        this.mCursorPosHeight = (int) (((float) this.mMaxCursorPos) - this.mCursorHeight);
    }

    public void setListener(boolean set) {
        if (this.mInitial) {
            View barLayout = findViewById(R.id.refocus_bar_handler);
            if (barLayout == null) {
                return;
            }
            if (set) {
                barLayout.setOnTouchListener(this.mOnLineTouchListener);
            } else {
                barLayout.setOnTouchListener(null);
            }
        }
    }

    public void updateBarWithValue(int value, boolean actionEnd) {
        if (this.mPostviewBarListener != null) {
            this.mPostviewBarListener.onCursorMoving(actionEnd);
        }
        if (this.mValue != value && this.mInitial && !actionEnd) {
            if (value > this.mCursorMaxStep) {
                value = this.mCursorMaxStep;
            }
            if (value < 0) {
                value = 0;
            }
            setBarValue(value);
            if (this.mPostviewBarListener != null) {
                this.mPostviewBarListener.onCursorUpdated(value);
                this.mPostviewBarListener.onCursorMoving(actionEnd);
            }
        }
    }

    public void setVisible(int visible) {
        if (this.mInitial) {
            CamLog.d(FaceDetector.TAG, "BarView-showControl:mValue = " + this.mValue + " visible=" + visible);
            findViewById(R.id.refocus_bar_handler_cursor).setPressed(false);
            setVisibility(visible);
        }
    }

    public boolean isBarVisible() {
        return getVisibility() == 0;
    }

    public int getCursorValue() {
        return this.mValue;
    }

    public void setBarValue(int value) {
        this.mValue = value;
        setCursor(this.mValue);
    }

    public void setCursor(final int value) {
        if (this.mInitial && this.mPostviewBarListener != null) {
            this.mPostviewBarListener.runOnUiThread(new Runnable() {
                public void run() {
                    PostViewBar.this.mPostviewBarListener.removePostRunnable(this);
                    int curLevel = 0;
                    try {
                        ImageView cursor = (ImageView) PostViewBar.this.findViewById(R.id.refocus_bar_handler_cursor);
                        LayoutParams param = (LayoutParams) cursor.getLayoutParams();
                        int marginDown = (int) (((float) PostViewBar.this.mMaxCursorPos) - PostViewBar.this.mCursorHeight);
                        if (PostViewBar.this.mCursorMaxStep != 0) {
                            curLevel = (int) (((float) value) * (((float) PostViewBar.this.mCursorPosHeight) / ((float) PostViewBar.this.mCursorMaxStep)));
                        }
                        int position = marginDown - curLevel;
                        if (position > PostViewBar.this.mMaxCursorPos) {
                            position = PostViewBar.this.mMaxCursorPos;
                        }
                        if (position < PostViewBar.this.mMinCursorPos) {
                            position = PostViewBar.this.mMinCursorPos;
                        }
                        param.topMargin = position;
                        cursor.setLayoutParams(param);
                    } catch (Exception e) {
                        CamLog.e(FaceDetector.TAG, "Exception:", e);
                    }
                }
            });
        }
    }
}
