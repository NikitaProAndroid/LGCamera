package com.lge.camera.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;

public class RotateDragView extends RotateImageView {
    private static final int DIFF_OFFSET = 1;
    private static final int DRAG_SCALE = 0;
    int mB;
    private int mHeight;
    int mL;
    private ViewGroup mLayout;
    int mR;
    private int mRegistrationX;
    private int mRegistrationY;
    int mT;
    private int mWidth;

    public RotateDragView(Context context) {
        super(context);
    }

    public RotateDragView(Context context, ViewGroup vg, int regX, int regY, int left, int top, int width, int height) {
        super(context);
        this.mLayout = vg;
        float scaleFactor = (float) width;
        scaleFactor = (0.0f + scaleFactor) / scaleFactor;
        new Matrix().setScale(scaleFactor, scaleFactor);
        this.mWidth = width;
        this.mHeight = height;
        this.mRegistrationX = regX + 0;
        this.mRegistrationY = regY + 0;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(this.mWidth, this.mHeight);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            checkDiffOffset(left, top, right, bottom);
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void show(int touchX, int touchY) {
        this.mL = touchX - this.mRegistrationX;
        this.mT = touchY - this.mRegistrationY;
        this.mR = (touchX - this.mRegistrationX) + this.mWidth;
        this.mB = (touchY - this.mRegistrationY) + this.mHeight;
        this.mLayout.addView(this);
        MarginLayoutParams mp = (MarginLayoutParams) getLayoutParams();
        mp.leftMargin = this.mL;
        mp.topMargin = this.mT;
        mp.rightMargin = this.mR;
        mp.bottomMargin = this.mB;
        setLayoutParams(mp);
    }

    public void move(int touchX, int touchY) {
        this.mL = touchX - this.mRegistrationX;
        this.mT = touchY - this.mRegistrationY;
        this.mR = (touchX - this.mRegistrationX) + getWidth();
        this.mB = (touchY - this.mRegistrationY) + getHeight();
        layout(this.mL, this.mT, this.mR, this.mB);
    }

    public void remove() {
        this.mLayout.removeView(this);
    }

    private void checkDiffOffset(int l, int t, int r, int b) {
        if (l - 1 <= this.mL && l + DIFF_OFFSET >= this.mL) {
            return;
        }
        if (t - 1 <= this.mT && t + DIFF_OFFSET >= this.mT) {
            return;
        }
        if (r - 1 <= this.mR && r + DIFF_OFFSET >= this.mR) {
            return;
        }
        if (b - 1 > this.mB || b + DIFF_OFFSET < this.mB) {
            layout(this.mL, this.mT, this.mR, this.mB);
        }
    }
}
