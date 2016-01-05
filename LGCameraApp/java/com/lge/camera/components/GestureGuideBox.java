package com.lge.camera.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.lge.camera.R;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.util.Util;
import com.lge.olaworks.define.Ola_Exif.Tag;

public class GestureGuideBox extends ImageView {
    private NinePatchDrawable mBoxHorizon;
    private NinePatchDrawable mBoxVertical;
    protected int mDegree;
    private int mHandHeight;
    private int mHandWidth;
    private boolean mInit;
    private int mLeftTopX;
    private int mLeftTopY;
    private int mNaviW;
    private int mPannelW;
    private int mPreviewHeight;
    private int mPreviewWidth;
    private int mQuickBtnW;

    public GestureGuideBox(Context context) {
        super(context);
        this.mLeftTopX = 0;
        this.mLeftTopY = 0;
        this.mHandWidth = 0;
        this.mHandHeight = 0;
        this.mPreviewWidth = 1;
        this.mPreviewHeight = 1;
        this.mQuickBtnW = 0;
        this.mPannelW = 0;
        this.mNaviW = 0;
        this.mInit = false;
        this.mBoxHorizon = null;
        this.mBoxVertical = null;
        this.mDegree = -1;
        setWillNotDraw(false);
    }

    public GestureGuideBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLeftTopX = 0;
        this.mLeftTopY = 0;
        this.mHandWidth = 0;
        this.mHandHeight = 0;
        this.mPreviewWidth = 1;
        this.mPreviewHeight = 1;
        this.mQuickBtnW = 0;
        this.mPannelW = 0;
        this.mNaviW = 0;
        this.mInit = false;
        this.mBoxHorizon = null;
        this.mBoxVertical = null;
        this.mDegree = -1;
    }

    public GestureGuideBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mLeftTopX = 0;
        this.mLeftTopY = 0;
        this.mHandWidth = 0;
        this.mHandHeight = 0;
        this.mPreviewWidth = 1;
        this.mPreviewHeight = 1;
        this.mQuickBtnW = 0;
        this.mPannelW = 0;
        this.mNaviW = 0;
        this.mInit = false;
        this.mBoxHorizon = null;
        this.mBoxVertical = null;
        this.mDegree = -1;
    }

    public void init() {
        if (!this.mInit) {
            initResources();
            this.mInit = true;
        }
    }

    public void setInitialDegree(int degree) {
        this.mDegree = degree;
        setState(0);
    }

    public void unbind() {
        this.mInit = false;
        this.mBoxHorizon = null;
        this.mBoxVertical = null;
    }

    public void initResources() {
        this.mBoxHorizon = getNinePatchDrawable(R.drawable.camera_preview_object_l_focused);
        this.mBoxVertical = getNinePatchDrawable(R.drawable.camera_preview_object_focused);
    }

    public NinePatchDrawable getNinePatchDrawable(int resid) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), resid);
        return new NinePatchDrawable(getResources(), bmp, bmp.getNinePatchChunk(), new Rect(), null);
    }

    public void setState(int state) {
        setImageDrawable(isHorizontal() ? this.mBoxHorizon : this.mBoxVertical);
        invalidate();
    }

    public void setDegree(int degree) {
        if (this.mDegree != degree) {
            this.mDegree = degree;
            setState(0);
        }
    }

    public boolean isHorizontal() {
        return this.mDegree == 90 || this.mDegree == Tag.IMAGE_DESCRIPTION;
    }

    public void setRectangleArea(int x, int y, int w, int h) {
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        this.mLeftTopX = x;
        this.mLeftTopY = y;
        this.mHandWidth = w;
        this.mHandHeight = h;
        invalidate();
    }

    public void setCoorinate(int q, int p, int n) {
        this.mQuickBtnW = q;
        this.mPannelW = p;
        this.mNaviW = n;
    }

    public void setPreviewSize(Context context, int previewW, int previewH) {
        if (context != null) {
            if (Util.isConfigureLandscape(context.getResources())) {
                this.mPreviewWidth = previewW;
                this.mPreviewHeight = previewH;
                return;
            }
            this.mPreviewWidth = previewH;
            this.mPreviewHeight = previewW;
        }
    }

    protected void onDraw(Canvas canvas) {
        if (getVisibility() == 0) {
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }
            if ((this.mLeftTopX != 0 || this.mLeftTopY != 0 || this.mHandWidth != 0 || this.mHandHeight != 0) && this.mPreviewWidth != 0 && this.mPreviewHeight != 0) {
                int canvasHeight = getMeasuredHeight();
                int sum = (this.mQuickBtnW + this.mPannelW) + this.mNaviW;
                int finalY = this.mLeftTopY;
                if (((double) (((float) getMeasuredHeight()) / ((float) getMeasuredWidth()))) > ((double) (((float) this.mPreviewHeight) / ((float) this.mPreviewWidth))) + 0.1d) {
                    if (ModelProperties.isSoftKeyNavigationBarModel()) {
                        canvasHeight = getMeasuredHeight() - sum;
                    } else {
                        canvasHeight = getMeasuredHeight() - (this.mQuickBtnW + this.mPannelW);
                    }
                    finalY = this.mLeftTopY + this.mQuickBtnW;
                }
                canvas.scale(((float) getMeasuredWidth()) / ((float) this.mPreviewWidth), ((float) canvasHeight) / ((float) this.mPreviewHeight));
                drawable.setBounds(this.mLeftTopX, finalY, this.mLeftTopX + this.mHandWidth, this.mHandHeight + finalY);
                drawable.draw(canvas);
            }
        }
    }
}
