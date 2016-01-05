package com.lge.camera.components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class RotateImageButton extends RotateView {
    private static final float DIAGONAL_FACTOR = 1.41421f;
    private Drawable mRotateBgDrawable;
    private int mRotateBgResource;

    public RotateImageButton(Context context) {
        this(context, null);
    }

    public RotateImageButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mRotateBgResource = 0;
        this.BASE_TEXT_PADDING_RATE = RotateView.BASE_TEXT_SCALE_X_RATE;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth;
        int measuredHeight;
        int contentWidth = 0;
        int contentHeight = 0;
        if (this.mRotateBgDrawable != null) {
            int bgWidth = this.mRotateBgDrawable.getMinimumWidth();
            int bgHeight = this.mRotateBgDrawable.getMinimumHeight();
            if (0 < bgWidth) {
                contentWidth = bgWidth;
            }
            if (0 < bgHeight) {
                contentHeight = bgHeight;
            }
        }
        Drawable imageDrawable = getDrawable();
        if (imageDrawable != null) {
            int imageWidth = imageDrawable.getMinimumWidth();
            int imageHeight = imageDrawable.getMinimumHeight();
            if (contentWidth < imageWidth) {
                contentWidth = imageWidth;
            }
            if (contentHeight < imageHeight) {
                contentHeight = imageHeight;
            }
        }
        if (MeasureSpec.getMode(widthMeasureSpec) == 1073741824) {
            measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            measuredWidth = contentWidth;
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == 1073741824) {
            measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            measuredHeight = contentHeight;
        }
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    protected void canvasRotate(Canvas canvas, int viewWidth, int viewHeight) {
        int bgCenterX = viewWidth / 2;
        int bgCenterY = viewHeight / 2;
        canvas.rotate((float) (-this.mRotationInfo.getCurrentDegree()), (float) bgCenterX, (float) bgCenterY);
        if (this.mRotateInsideView) {
            applyRotateImageScale(canvas, viewWidth, viewHeight, bgCenterX, bgCenterY);
        }
        if (!this.mRotateIconOnly) {
            getBackground().draw(canvas);
        }
    }

    protected boolean checkBackground(Canvas canvas) {
        Drawable drawable = getBackground();
        if (drawable == null) {
            drawable = getDrawable();
            if (drawable == null) {
                return false;
            }
        }
        if ((drawable instanceof BitmapDrawable) && ((BitmapDrawable) drawable).getBitmap().isRecycled()) {
            return false;
        }
        Rect bounds = drawable.getBounds();
        int drawableHeight = bounds.bottom - bounds.top;
        if (bounds.right - bounds.left == 0 || drawableHeight == 0) {
            CamLog.d(FaceDetector.TAG, "drawable width,height is zero, return");
            return false;
        }
        if (this.mRotateIconOnly) {
            drawable.draw(canvas);
        }
        return true;
    }

    public int getTextPaintWidth() {
        Paint p = new Paint();
        p.setTextSize((float) this.mTextSize);
        int textWidth = (int) p.measureText(this.mText);
        return ((int) (((float) textWidth) * this.BASE_TEXT_PADDING_RATE)) + textWidth;
    }

    public void setRotated(int degree) {
        if (degree > 0) {
            this.mRotationInfo.setCurrentDegree(degree - 1);
        } else {
            this.mRotationInfo.setCurrentDegree(1);
        }
        this.mRotationInfo.setTargetDegree(degree);
        invalidate();
    }

    public void setBackgroundResource(int resId) {
        if (this.mRotateBgResource != resId) {
            updateDrawable(null);
            this.mRotateBgResource = resId;
            Drawable d = null;
            if (this.mRotateBgResource != 0) {
                try {
                    Resources rsrc = getResources();
                    if (rsrc != null) {
                        d = rsrc.getDrawable(this.mRotateBgResource);
                    }
                } catch (Exception e) {
                    CamLog.w(FaceDetector.TAG, "Unable to find resource: " + this.mRotateBgResource, e);
                }
            }
            updateDrawable(d);
            requestLayout();
            invalidate();
        }
    }

    public int getBackgroundResource() {
        return this.mRotateBgResource;
    }

    public void setBackgroundDrawable(Drawable drawable) {
        if (this.mRotateBgDrawable != drawable) {
            updateDrawable(drawable);
            requestLayout();
            invalidate();
        }
    }

    public Drawable getBackground() {
        return this.mRotateBgDrawable;
    }

    private void updateDrawable(Drawable d) {
        if (this.mRotateBgDrawable != null) {
            this.mRotateBgDrawable.setCallback(null);
            unscheduleDrawable(this.mRotateBgDrawable);
        }
        this.mRotateBgDrawable = d;
        if (d != null) {
            d.setCallback(this);
            if (d.isStateful()) {
                d.setState(getDrawableState());
            }
            configureBounds();
        }
    }

    private void configureBounds() {
        if (this.mRotateBgDrawable != null) {
            this.mRotateBgDrawable.setBounds(0, 0, getWidth(), getHeight());
        }
        int longerSide = getWidth();
        if (getWidth() < getHeight()) {
            longerSide = getHeight();
        }
        this.mExpand4Rotate = (int) ((((float) longerSide) * DIAGONAL_FACTOR) - ((float) longerSide));
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable d = this.mRotateBgDrawable;
        if (d != null && d.isStateful()) {
            d.setState(getDrawableState());
        }
    }

    protected boolean verifyDrawable(Drawable dr) {
        return this.mRotateBgDrawable == dr || super.verifyDrawable(dr);
    }

    public void invalidateDrawable(Drawable dr) {
        if (dr == this.mRotateBgDrawable) {
            invalidate();
        } else {
            super.invalidateDrawable(dr);
        }
    }

    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        configureBounds();
        return changed;
    }

    public void setRotateIconOnly(boolean rotateIconOnly) {
        if (this.mRotateIconOnly != rotateIconOnly) {
            this.mRotateIconOnly = rotateIconOnly;
            invalidate();
        }
    }

    private void applyRotateImageScale(Canvas canvas, int viewWidth, int viewHeight, int bgCenterX, int bgCenterY) {
        Drawable imageSrc = getDrawable();
        if (imageSrc != null) {
            float rotatedImageScale;
            Rect imageBounds = imageSrc.getBounds();
            int imageWidth = imageBounds.right - imageBounds.left;
            int imageHeight = imageBounds.bottom - imageBounds.top;
            float viewRatio = ((float) viewWidth) / ((float) viewHeight);
            float f;
            if (viewRatio < ((float) imageWidth) / ((float) imageHeight)) {
                f = (float) imageHeight;
                imageHeight = (int) (r0 * (((float) viewWidth) / ((float) imageWidth)));
                imageWidth = viewWidth;
            } else {
                f = (float) imageWidth;
                imageWidth = (int) (r0 * (((float) viewHeight) / ((float) imageHeight)));
                imageHeight = viewHeight;
            }
            double cosA = Math.cos(Math.toRadians((double) this.mRotationInfo.getCurrentDegree()));
            double cosRevA = Math.cos(Math.toRadians((double) (90 - this.mRotationInfo.getCurrentDegree())));
            int rw = (int) (Math.abs(((double) imageWidth) * cosA) + Math.abs(((double) imageHeight) * cosRevA));
            int rh = (int) (Math.abs(((double) imageWidth) * cosRevA) + Math.abs(((double) imageHeight) * cosA));
            float rotatedImageScaleW = ((float) viewWidth) / ((float) rw);
            float rotatedImageScaleH = ((float) viewHeight) / ((float) rh);
            if (viewRatio < ((float) rw) / ((float) rh)) {
                rotatedImageScale = rotatedImageScaleW;
            } else {
                rotatedImageScale = rotatedImageScaleH;
            }
            canvas.scale(rotatedImageScale, rotatedImageScale, (float) bgCenterX, (float) bgCenterY);
        }
    }
}
