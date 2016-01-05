package com.lge.camera.components;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.lge.camera.properties.CameraConstants;

public class AudiozoomBarLevelMeter extends ImageView {
    public static final int FORWARD = 0;
    private int mBox;
    private float mCurs;
    private int mHeight;
    private int mStep;
    private int mWidth;

    public AudiozoomBarLevelMeter(Context context) {
        super(context);
        this.mCurs = 0.0f;
        this.mStep = 0;
        this.mBox = 0;
    }

    public AudiozoomBarLevelMeter(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCurs = 0.0f;
        this.mStep = 0;
        this.mBox = 0;
    }

    public void initRecProgressBar(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        this.mBox = (int) (((double) this.mHeight) * 0.3d);
    }

    public void setStep(int step) {
        this.mStep = (int) (((float) (this.mHeight * step)) * CameraConstants.AUDIOZOOM_BAR_RATIO);
    }

    private boolean isOutOfLevel() {
        if (this.mStep >= this.mHeight) {
            return true;
        }
        return false;
    }

    public void setProgress(int value) {
        if (!isOutOfLevel()) {
            if (value > 100) {
                value = 100;
            } else if (value < 0) {
                value = 0;
            }
            this.mCurs = ((float) value) * CameraConstants.AUDIOZOOM_BAR_RATIO;
            invalidate();
        }
    }

    protected void onDraw(Canvas canvas) {
        int value = (int) (((float) this.mStep) + (((float) this.mBox) * this.mCurs));
        if (value > this.mHeight) {
            value = this.mHeight;
        } else if (value < 0) {
            value = 0;
        }
        canvas.clipRect(0, this.mHeight - value, this.mWidth, this.mHeight);
        super.onDraw(canvas);
    }
}
