package com.lge.camera.components;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.lge.camera.properties.CameraConstants;

public class PanoProgressBar extends ImageView {
    public static final int FORWARD = 0;
    public static final int REVERSE = 1;
    public final String TAG;
    private int mCurs;
    private int mDirection;
    private int mDrawWidth;
    private int mHeight;
    private int mMax;
    private int mPadding;
    private float mStep;
    private int mWidth;

    public PanoProgressBar(Context context) {
        super(context);
        this.TAG = "PanoProgressBar";
        this.mMax = CameraConstants.PIP_SUBWINDOW_RESIZE_HANDLER_DURATION;
        this.mPadding = FORWARD;
        this.mCurs = FORWARD;
    }

    public PanoProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = "PanoProgressBar";
        this.mMax = CameraConstants.PIP_SUBWINDOW_RESIZE_HANDLER_DURATION;
        this.mPadding = FORWARD;
        this.mCurs = FORWARD;
    }

    public void initPanoProgressBar(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        this.mDrawWidth = this.mWidth - (this.mPadding * 2);
        this.mStep = ((float) this.mMax) / ((float) this.mDrawWidth);
    }

    public int getMax() {
        return this.mMax;
    }

    public void setDirection(int direction) {
        this.mDirection = direction;
    }

    public void setProgress(int value) {
        if (value > this.mMax) {
            value = this.mMax;
        }
        this.mCurs = value;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        int value = (int) (((float) this.mCurs) / this.mStep);
        if (this.mDirection == 0) {
            canvas.clipRect(FORWARD, FORWARD, value, this.mHeight);
        } else {
            canvas.clipRect(this.mDrawWidth - value, FORWARD, this.mDrawWidth, this.mHeight);
        }
        super.onDraw(canvas);
    }
}
