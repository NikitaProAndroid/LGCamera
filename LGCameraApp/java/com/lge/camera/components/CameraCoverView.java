package com.lge.camera.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.lge.camera.properties.CameraConstants;
import com.lge.voiceshutter.library.LGKeyRec;

public class CameraCoverView extends ImageView {
    public static final int APERTURE_ANGLE = 45;
    public static final int APERTURE_COUNT = 8;
    public static final int DEGREE_END = 40;
    public static final int DRAW_ANI_START = 1;
    public static final int DRAW_END = 2;
    public static final int DRAW_IDLE = 0;
    public static boolean mIsOpen;
    private float currentDegree;
    private float drawableHeight;
    private float drawableWidth;
    private int mDrawState;
    private OnCameraCoverListener mListener;
    private float pX;
    private float rootDx;
    private float rootDy;
    private int saveCount;
    private float transX;
    private float viewHeight;
    private float viewWidth;

    public interface OnCameraCoverListener {
        void onCoverCloseAnimationEnd();

        void onCoverOpenAnimationEnd();
    }

    public CameraCoverView(Context context) {
        super(context);
        this.saveCount = 0;
        this.currentDegree = 0.0f;
        this.drawableWidth = 0.0f;
        this.drawableHeight = 0.0f;
        this.viewWidth = 0.0f;
        this.viewHeight = 0.0f;
        this.rootDx = 0.0f;
        this.rootDy = 0.0f;
        this.mDrawState = 0;
        this.pX = 0.0f;
        this.transX = 0.0f;
        this.mListener = null;
    }

    public CameraCoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.saveCount = 0;
        this.currentDegree = 0.0f;
        this.drawableWidth = 0.0f;
        this.drawableHeight = 0.0f;
        this.viewWidth = 0.0f;
        this.viewHeight = 0.0f;
        this.rootDx = 0.0f;
        this.rootDy = 0.0f;
        this.mDrawState = 0;
        this.pX = 0.0f;
        this.transX = 0.0f;
        this.mListener = null;
    }

    public CameraCoverView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.saveCount = 0;
        this.currentDegree = 0.0f;
        this.drawableWidth = 0.0f;
        this.drawableHeight = 0.0f;
        this.viewWidth = 0.0f;
        this.viewHeight = 0.0f;
        this.rootDx = 0.0f;
        this.rootDy = 0.0f;
        this.mDrawState = 0;
        this.pX = 0.0f;
        this.transX = 0.0f;
        this.mListener = null;
    }

    static {
        mIsOpen = true;
    }

    public void setOnCameraCoverListener(OnCameraCoverListener listener) {
        this.mListener = listener;
    }

    public void setDrawState(int state, boolean open) {
        if (getVisibility() != 0) {
            this.mDrawState = DRAW_END;
        } else {
            this.mDrawState = state;
        }
        mIsOpen = open;
        this.currentDegree = mIsOpen ? 0.0f : 40.0f;
        if (this.mDrawState == DRAW_ANI_START) {
            invalidate();
        }
    }

    public static boolean isCoverOpen() {
        return mIsOpen;
    }

    public int getDrawState() {
        return this.mDrawState;
    }

    public void setVisibility(int visibility) {
        if (visibility != 0) {
            mIsOpen = true;
        }
        super.setVisibility(visibility);
    }

    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        this.drawableWidth = (float) drawable.getIntrinsicWidth();
        this.drawableHeight = (float) drawable.getIntrinsicHeight();
        if (this.drawableWidth > 0.0f && this.drawableHeight > 0.0f) {
            switch (this.mDrawState) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    this.currentDegree = 0.0f;
                    invalidate();
                    if (this.mListener != null) {
                        this.mListener.onCoverCloseAnimationEnd();
                        this.mListener = null;
                        break;
                    }
                    break;
                case DRAW_ANI_START /*1*/:
                    if (!mIsOpen) {
                        this.currentDegree -= (this.currentDegree / 5.0f) + CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK;
                        invalidate();
                        if (this.currentDegree <= 0.0f) {
                            this.mDrawState = 0;
                            this.currentDegree = 0.0f;
                            break;
                        }
                    }
                    this.currentDegree += (this.currentDegree / 5.0f) + CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK;
                    invalidate();
                    if (this.currentDegree >= 40.0f) {
                        this.mDrawState = DRAW_END;
                        this.currentDegree = 0.0f;
                        return;
                    }
                    break;
                case DRAW_END /*2*/:
                    this.currentDegree = 0.0f;
                    if (this.mListener != null) {
                        this.mListener.onCoverOpenAnimationEnd();
                        this.mListener = null;
                        return;
                    }
                    return;
            }
            this.saveCount = canvas.getSaveCount();
            canvas.save();
            this.viewWidth = (float) getWidth();
            this.viewHeight = (float) getHeight();
            this.rootDx = ((this.drawableWidth * CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK) - this.viewWidth) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK;
            this.rootDy = ((this.drawableHeight * CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK) - this.viewHeight) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK;
            this.pX = this.drawableWidth * 0.15f;
            this.transX = this.drawableWidth / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK;
            canvas.translate(-this.rootDx, -this.rootDy);
            for (int i = 0; i < APERTURE_COUNT; i += DRAW_ANI_START) {
                canvas.translate(this.transX, 0.0f);
                if (i > 0) {
                    canvas.rotate(45.0f, this.transX, this.drawableHeight);
                }
                canvas.rotate(this.currentDegree, this.pX, 0.0f);
                drawable.draw(canvas);
                canvas.rotate(-this.currentDegree, this.pX, 0.0f);
                canvas.translate(-this.transX, 0.0f);
            }
            canvas.restoreToCount(this.saveCount);
        }
    }
}
