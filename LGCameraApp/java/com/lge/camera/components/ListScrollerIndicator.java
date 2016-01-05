package com.lge.camera.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.lge.camera.properties.CameraConstants;

public class ListScrollerIndicator extends ImageView {
    private static final int DRAW_NONE = 0;
    private static final int DRAW_START = 1;
    private float dx;
    private float dy;
    private int mCurValue;
    private int mDrawState;
    private int mMaxValue;
    private int saveCount;
    private float viewHeight;
    private float viewWidth;

    public ListScrollerIndicator(Context context) {
        super(context);
        this.mMaxValue = DRAW_NONE;
        this.mCurValue = DRAW_NONE;
        this.saveCount = DRAW_NONE;
        this.viewWidth = 0.0f;
        this.viewHeight = 0.0f;
        this.dx = 0.0f;
        this.dy = 0.0f;
        this.mDrawState = DRAW_NONE;
    }

    public ListScrollerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMaxValue = DRAW_NONE;
        this.mCurValue = DRAW_NONE;
        this.saveCount = DRAW_NONE;
        this.viewWidth = 0.0f;
        this.viewHeight = 0.0f;
        this.dx = 0.0f;
        this.dy = 0.0f;
        this.mDrawState = DRAW_NONE;
    }

    public void initListScrollerIndicator(int maxValue) {
        this.mMaxValue = maxValue;
    }

    public void setCursor(int curValue) {
        if (this.mCurValue != curValue) {
            this.mCurValue = curValue;
            postInvalidate();
        }
    }

    public void setDraw(boolean draw) {
        this.mDrawState = draw ? DRAW_START : DRAW_NONE;
    }

    protected void onDraw(Canvas canvas) {
        Drawable cursor = getDrawable();
        if (cursor != null && this.mMaxValue != 0 && this.mDrawState != 0) {
            this.saveCount = canvas.getSaveCount();
            canvas.save();
            float cursorWidth = (float) cursor.getIntrinsicWidth();
            float cursorHeight = (float) cursor.getIntrinsicHeight();
            this.viewWidth = (float) getWidth();
            this.viewHeight = (float) getHeight();
            this.dx = (this.viewWidth - cursorWidth) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK;
            if (this.mMaxValue == DRAW_START) {
                this.dy = 0.0f;
            } else {
                float step = (this.viewHeight - cursorHeight) / ((float) (this.mMaxValue - 1));
                if (this.mCurValue == 0) {
                    this.dy = 0.0f;
                } else if (this.mCurValue == this.mMaxValue - 1) {
                    this.dy = this.viewHeight - cursorHeight;
                } else {
                    this.dy = ((float) this.mCurValue) * step;
                }
            }
            canvas.translate(this.dx, this.dy);
            cursor.draw(canvas);
            canvas.translate(-this.dx, -this.dy);
            canvas.restoreToCount(this.saveCount);
        }
    }
}
