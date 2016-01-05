package com.lge.camera.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ImageButtonEx extends ImageButton implements OnTouchListener {
    public static final int BUTTON_STATE_LONG_PRESSED = 2;
    public static final int BUTTON_STATE_NOT_PRESSED = 0;
    public static final int BUTTON_STATE_PRESSED = 1;
    public static final int BUTTON_STATE_RELEASED = 3;
    public static final int IBE_BUTTON_MIN = 0;
    public static final int IBE_BUTTON_PLUS = 1;
    private static final long LONG_PRESS_EVENT_DELAY = 200;
    private static final long LONG_PRESS_JUDGE_TIMEOUT = 1000;
    private Timer mButtonCheckTimer;
    private ImageButtonExStateListener mButtonStateListener;
    private int mButtonStatus;
    public Context mContext;
    private ArrayList<ReleaseArea> mReleaseArea;

    public interface ImageButtonExStateListener {
        void onChange(int i);
    }

    private class ReleaseArea {
        public int mBottom;
        public int mLeft;
        public int mRight;
        public int mTop;

        public ReleaseArea(int left, int top, int right, int bottom) {
            this.mLeft = left;
            this.mTop = top;
            this.mRight = right;
            this.mBottom = bottom;
        }
    }

    public ImageButtonEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = null;
        this.mButtonStateListener = null;
        this.mButtonStatus = IBE_BUTTON_MIN;
        this.mContext = context;
    }

    public ImageButtonEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = null;
        this.mButtonStateListener = null;
        this.mButtonStatus = IBE_BUTTON_MIN;
        this.mContext = context;
        setOnTouchListener(this);
        setFocusable(false);
        initReleaseArea();
    }

    public ImageButtonEx(Context context) {
        super(context);
        this.mContext = null;
        this.mButtonStateListener = null;
        this.mButtonStatus = IBE_BUTTON_MIN;
        this.mContext = context;
    }

    public void initReleaseArea() {
        this.mReleaseArea = new ArrayList();
        this.mReleaseArea.add(IBE_BUTTON_MIN, new ReleaseArea(10, 10, 10, 10));
        this.mReleaseArea.add(IBE_BUTTON_PLUS, new ReleaseArea(10, 10, 10, 10));
    }

    private void startTimerTask(TimerTask t, long judge, long interval) {
        if (this.mButtonCheckTimer == null) {
            this.mButtonCheckTimer = new Timer("timer_long_press_check");
            this.mButtonCheckTimer.scheduleAtFixedRate(t, judge, interval);
        }
    }

    private void stopTimerTask() {
        if (this.mButtonCheckTimer != null) {
            this.mButtonCheckTimer.cancel();
            this.mButtonCheckTimer.purge();
            this.mButtonCheckTimer = null;
        }
    }

    private boolean checkOutRange(View v, int index, float x, float y) {
        if (y >= ((float) (-((ReleaseArea) this.mReleaseArea.get(index)).mTop))) {
            if (y <= ((float) (((ReleaseArea) this.mReleaseArea.get(index)).mBottom + v.getHeight()))) {
                if (x >= ((float) (-((ReleaseArea) this.mReleaseArea.get(index)).mLeft))) {
                    if (x <= ((float) (((ReleaseArea) this.mReleaseArea.get(index)).mRight + v.getWidth()))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return true;
    }

    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case IBE_BUTTON_MIN /*0*/:
                doTouchActionDown(v);
                return true;
            case IBE_BUTTON_PLUS /*1*/:
                doTouchActionUp(v);
                break;
            case BUTTON_STATE_LONG_PRESSED /*2*/:
                doTouchActionMove(v, x, y);
                break;
        }
        return false;
    }

    private void doTouchActionUp(View v) {
        if (this.mButtonStatus != 0) {
            this.mButtonStatus = IBE_BUTTON_MIN;
            stopTimerTask();
            this.mButtonStateListener.onChange(BUTTON_STATE_RELEASED);
        }
    }

    private void doTouchActionMove(View v, float x, float y) {
        if (this.mButtonStatus == BUTTON_STATE_LONG_PRESSED && checkOutRange(v, IBE_BUTTON_PLUS, x, y)) {
            this.mButtonStatus = IBE_BUTTON_MIN;
            stopTimerTask();
            this.mButtonStateListener.onChange(BUTTON_STATE_RELEASED);
        }
    }

    private void doTouchActionDown(View v) {
        this.mButtonStatus = IBE_BUTTON_PLUS;
        startTimerTask(new TimerTask() {
            public void run() {
                switch (ImageButtonEx.this.mButtonStatus) {
                    case ImageButtonEx.IBE_BUTTON_PLUS /*1*/:
                        ImageButtonEx.this.mButtonStatus = ImageButtonEx.BUTTON_STATE_LONG_PRESSED;
                        if (ImageButtonEx.this.mButtonStateListener != null) {
                            ImageButtonEx.this.mButtonStateListener.onChange(ImageButtonEx.BUTTON_STATE_LONG_PRESSED);
                        }
                    case ImageButtonEx.BUTTON_STATE_LONG_PRESSED /*2*/:
                        if (ImageButtonEx.this.mButtonStateListener != null) {
                            ImageButtonEx.this.mButtonStateListener.onChange(ImageButtonEx.BUTTON_STATE_LONG_PRESSED);
                        }
                    default:
                }
            }
        }, LONG_PRESS_JUDGE_TIMEOUT, LONG_PRESS_EVENT_DELAY);
    }

    public void setImageButtonExStatusListener(ImageButtonExStateListener listener) {
        this.mButtonStateListener = listener;
    }

    public void unbind() {
        stopTimerTask();
        this.mButtonStateListener = null;
        getDrawable().setCallback(null);
        setImageDrawable(null);
        getBackground().setCallback(null);
        setBackground(null);
    }
}
