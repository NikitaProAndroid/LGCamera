package com.lge.camera.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import com.lge.camera.properties.CameraConstants;

public class DragView extends View {
    private static final int DRAG_SCALE = 0;
    public static final String TAG = "DragView";
    private float mAnimationScale;
    private Bitmap mBitmap;
    private int mHeight;
    private ViewGroup mLayout;
    private Paint mPaint;
    private int mRegistrationX;
    private int mRegistrationY;
    private int mWidth;

    public DragView(Context context) {
        super(context);
        this.mAnimationScale = RotateView.DEFAULT_TEXT_SCALE_X;
    }

    public DragView(Context context, ViewGroup vg, Bitmap bitmap, int regX, int regY, int left, int top, int width, int height) {
        super(context);
        this.mAnimationScale = RotateView.DEFAULT_TEXT_SCALE_X;
        this.mLayout = vg;
        Matrix scale = new Matrix();
        float scaleFactor = (float) width;
        scaleFactor = (0.0f + scaleFactor) / scaleFactor;
        scale.setScale(scaleFactor, scaleFactor);
        this.mBitmap = Bitmap.createBitmap(bitmap, left, top, width, height, scale, true);
        this.mWidth = this.mBitmap.getWidth();
        this.mHeight = this.mBitmap.getHeight();
        this.mRegistrationX = regX + DRAG_SCALE;
        this.mRegistrationY = regY + DRAG_SCALE;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(this.mBitmap.getWidth(), this.mBitmap.getHeight());
    }

    protected void onDraw(Canvas canvas) {
        Paint p = new Paint();
        p.setStyle(Style.FILL);
        p.setColor(-1998782447);
        canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), p);
        float scale = this.mAnimationScale;
        if (scale < 0.999f) {
            float width = (float) this.mBitmap.getWidth();
            float offset = (width - (width * scale)) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK;
            canvas.translate(offset, offset);
            canvas.scale(scale, scale);
        }
        canvas.drawBitmap(this.mBitmap, 0.0f, 0.0f, this.mPaint);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mBitmap.recycle();
    }

    public void setPaint(Paint paint) {
        this.mPaint = paint;
        invalidate();
    }

    public void show(int touchX, int touchY) {
        int l = touchX - this.mRegistrationX;
        int t = touchY - this.mRegistrationY;
        int r = (touchX - this.mRegistrationX) + this.mWidth;
        int b = (touchY - this.mRegistrationY) + this.mHeight;
        this.mLayout.addView(this);
        MarginLayoutParams mp = (MarginLayoutParams) getLayoutParams();
        mp.leftMargin = l;
        mp.topMargin = t;
        mp.rightMargin = r;
        mp.bottomMargin = b;
        setLayoutParams(mp);
    }

    public void move(int touchX, int touchY) {
        layout(touchX - this.mRegistrationX, touchY - this.mRegistrationY, (touchX - this.mRegistrationX) + getWidth(), (touchY - this.mRegistrationY) + getHeight());
    }

    public void remove() {
        this.mLayout.removeView(this);
    }
}
