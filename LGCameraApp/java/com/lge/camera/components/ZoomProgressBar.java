package com.lge.camera.components;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ZoomProgressBar extends ImageView {
    private int mCurs;
    private boolean mInit;
    private int mMax;

    public ZoomProgressBar(Context context) {
        super(context);
        this.mMax = 100;
        this.mCurs = 0;
        this.mInit = false;
    }

    public ZoomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMax = 100;
        this.mCurs = 0;
        this.mInit = false;
    }

    public void initZoomProgressBar(int maxCount) {
        this.mMax = maxCount;
        this.mInit = true;
    }

    public int getMax() {
        return this.mMax;
    }

    public void setProgress(int value) {
        if (this.mInit) {
            if (value > this.mMax) {
                value = this.mMax;
            }
            this.mCurs = value;
            invalidate();
        }
    }

    protected void onDraw(Canvas canvas) {
        if (this.mInit) {
            float height = (float) getHeight();
            float width = (float) getWidth();
            if (height > 0.0f) {
                float step = ((float) this.mMax) / height;
                if (step > 0.0f) {
                    canvas.clipRect(0.0f, height - ((float) ((int) (((float) this.mCurs) / step))), width, height);
                }
            }
            super.onDraw(canvas);
        }
    }
}
