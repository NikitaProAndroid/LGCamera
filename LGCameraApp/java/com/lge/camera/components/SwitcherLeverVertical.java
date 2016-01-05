package com.lge.camera.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import com.lge.camera.R;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;

public class SwitcherLeverVertical extends SwitcherLever implements OnTouchListener {
    private Drawable mModeCamDrawable;
    private Drawable mModeDrawable;
    private Drawable mModeVideoDrawable;

    public SwitcherLeverVertical(Context context) {
        super(context);
        this.mModeDrawable = null;
        this.mModeCamDrawable = null;
        this.mModeVideoDrawable = null;
        initModeDrawable();
    }

    public SwitcherLeverVertical(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mModeDrawable = null;
        this.mModeCamDrawable = null;
        this.mModeVideoDrawable = null;
        initModeDrawable();
    }

    public SwitcherLeverVertical(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mModeDrawable = null;
        this.mModeCamDrawable = null;
        this.mModeVideoDrawable = null;
        initModeDrawable();
    }

    private void initModeDrawable() {
        this.mModeCamDrawable = getResources().getDrawable(R.drawable.btn_mode_camera);
        this.mModeVideoDrawable = getResources().getDrawable(R.drawable.btn_mode_video);
        this.mModeCamDrawable.setBounds(0, 0, this.mModeCamDrawable.getIntrinsicWidth(), this.mModeCamDrawable.getIntrinsicHeight());
        this.mModeVideoDrawable.setBounds(0, 0, this.mModeVideoDrawable.getIntrinsicWidth(), this.mModeVideoDrawable.getIntrinsicHeight());
    }

    protected void trackTouchEvent(MotionEvent event) {
        int drawableHeight = getDrawable().getIntrinsicHeight();
        int available = ((getHeight() - getPaddingTop()) - getPaddingBottom()) - drawableHeight;
        this.mPosition = ((((int) event.getY()) - getPaddingTop()) - getPaddingBottom()) - (drawableHeight / 2);
        if (this.mPosition < 0) {
            this.mPosition = 0;
        }
        if (this.mPosition > available) {
            this.mPosition = available;
        }
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        int drawableHeight = drawable.getIntrinsicHeight();
        int drawableWidth = drawable.getIntrinsicWidth();
        if (drawableWidth != 0 && drawableHeight != 0 && this.mRotationInfo != null) {
            if (this.mAnimationParkingStartTime != -1) {
                setAnimationStartTime(drawableHeight);
            }
            int offsetTop = getPaddingTop() + this.mPosition;
            int offsetLeft = (((getWidth() - drawableWidth) - getPaddingStart()) - getPaddingEnd()) / 2;
            if (this.mRotationInfo.getCurrentDegree() != this.mRotationInfo.getTargetDegree() && this.mRotationInfo.calcCurrentDegree()) {
                invalidate();
            }
            int saveCount = canvas.getSaveCount();
            canvas.save();
            canvas.translate((float) ((drawableWidth / 2) + offsetLeft), (float) ((drawableHeight / 2) + offsetTop));
            canvas.translate((float) ((-drawableWidth) / 2), (float) ((-drawableHeight) / 2));
            drawable.draw(canvas);
            canvas.translate((float) (drawableWidth / 2), (float) (drawableHeight / 2));
            canvas.rotate((float) (-this.mRotationInfo.getCurrentDegree()));
            canvas.translate((float) ((-drawableWidth) / 2), (float) ((-drawableHeight) / 2));
            if (this.mModeDrawable != null) {
                this.mModeDrawable.draw(canvas);
            }
            canvas.restoreToCount(saveCount);
        }
    }

    public void unbind() {
        this.mListener = null;
        Drawable d = getDrawable();
        if (d != null) {
            d.setCallback(null);
            setImageDrawable(null);
        }
        d = getBackground();
        if (d != null) {
            d.setCallback(null);
            setBackground(null);
        }
        this.mModeDrawable = null;
        if (this.mModeCamDrawable != null) {
            this.mModeCamDrawable.setCallback(null);
            this.mModeCamDrawable = null;
        }
        if (this.mModeVideoDrawable != null) {
            this.mModeVideoDrawable.setCallback(null);
            this.mModeVideoDrawable = null;
        }
    }

    public void setSwitcherAlpha(int alpha) {
        Drawable d = getDrawable();
        if (d != null) {
            d.setAlpha(alpha);
        }
        if (this.mModeDrawable != null) {
            this.mModeDrawable.setAlpha(alpha);
        }
    }

    public void startRotation(int degree) {
        if (degree == 0 || degree == MediaProviderUtils.ROTATION_180) {
            setImageResource(R.drawable.selector_switcher_knob_bg);
        } else {
            setImageResource(R.drawable.selector_switcher_knob_bg_portrait);
        }
    }

    public void setSwitcherImage(int orientation, int mode) {
        if (mode == 0) {
            this.mModeDrawable = this.mModeCamDrawable;
        } else {
            this.mModeDrawable = this.mModeVideoDrawable;
        }
    }

    protected int getTouchPositionAvailable() {
        return ((getHeight() - getPaddingTop()) - getPaddingBottom()) - getDrawable().getIntrinsicHeight();
    }

    protected int getAniPositionAvailable(int drawablePosition) {
        return ((getHeight() - getPaddingTop()) - getPaddingBottom()) - drawablePosition;
    }
}
