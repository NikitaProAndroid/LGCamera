package com.lge.camera.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import com.lge.voiceshutter.library.LGKeyRec;

public class DragLayout extends RelativeLayout {
    public static final String TAG = "CameraApp";
    private boolean mDragging;
    private float mMotionDownX;
    private float mMotionDownY;

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDragging = false;
    }

    public float getMotionDownX() {
        return this.mMotionDownX;
    }

    public void setMotionDownX(float motionDownX) {
        this.mMotionDownX = motionDownX;
    }

    public float getMotionDownY() {
        return this.mMotionDownY;
    }

    public void setMotionDownY(float motionDownY) {
        this.mMotionDownY = motionDownY;
    }

    public boolean getDrag() {
        return this.mDragging;
    }

    public void setDrag(boolean drag) {
        this.mDragging = drag;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int screenX = (int) ev.getRawX();
        int screenY = (int) ev.getRawY();
        switch (ev.getAction()) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                this.mMotionDownX = (float) screenX;
                this.mMotionDownY = (float) screenY;
                break;
        }
        return this.mDragging;
    }
}
