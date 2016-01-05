package com.lge.camera.components;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.lge.camera.properties.CameraConstants;

public class AudiozoomBar extends ImageView {
    public static final int FORWARD = 0;
    private float mCurs;
    private int mHeight;
    private int mWidth;

    public AudiozoomBar(Context context) {
        super(context);
        this.mCurs = 0.0f;
    }

    public AudiozoomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCurs = 0.0f;
    }

    public void initRecProgressBar(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public void setProgress(int value) {
        if (value > 100) {
            value = 100;
        } else if (value < 0) {
            value = 0;
        }
        this.mCurs = ((float) value) * CameraConstants.AUDIOZOOM_BAR_RATIO;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        canvas.clipRect(0, this.mHeight - ((int) (((float) this.mHeight) * this.mCurs)), this.mWidth, this.mHeight);
        super.onDraw(canvas);
    }
}
