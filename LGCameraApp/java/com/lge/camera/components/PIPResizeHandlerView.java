package com.lge.camera.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.lge.camera.R;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.Util;
import com.lge.olaworks.define.Ola_ShotParam;

public class PIPResizeHandlerView extends View {
    private int mLeftTopX;
    private int mLeftTopY;
    private int mMovingEdges;
    private Paint mPaint;
    private Rect mRect;
    private Bitmap mResizeHandler_Bottom_Left;
    private Bitmap mResizeHandler_Bottom_Right;
    private Bitmap mResizeHandler_Top_Left;
    private Bitmap mResizeHandler_Top_Right;
    private int mRightBottomX;
    private int mRightBottomY;

    public PIPResizeHandlerView(Context context) {
        super(context);
        this.mLeftTopX = 0;
        this.mLeftTopY = 0;
        this.mRightBottomX = 0;
        this.mRightBottomY = 0;
        this.mResizeHandler_Bottom_Left = null;
        this.mResizeHandler_Bottom_Right = null;
        this.mResizeHandler_Top_Left = null;
        this.mResizeHandler_Top_Right = null;
        this.mMovingEdges = 0;
        this.mPaint = new Paint();
        this.mRect = new Rect();
    }

    public PIPResizeHandlerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mLeftTopX = 0;
        this.mLeftTopY = 0;
        this.mRightBottomX = 0;
        this.mRightBottomY = 0;
        this.mResizeHandler_Bottom_Left = null;
        this.mResizeHandler_Bottom_Right = null;
        this.mResizeHandler_Top_Left = null;
        this.mResizeHandler_Top_Right = null;
        this.mMovingEdges = 0;
        this.mPaint = new Paint();
        this.mRect = new Rect();
    }

    public PIPResizeHandlerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLeftTopX = 0;
        this.mLeftTopY = 0;
        this.mRightBottomX = 0;
        this.mRightBottomY = 0;
        this.mResizeHandler_Bottom_Left = null;
        this.mResizeHandler_Bottom_Right = null;
        this.mResizeHandler_Top_Left = null;
        this.mResizeHandler_Top_Right = null;
        this.mMovingEdges = 0;
        this.mPaint = new Paint();
        this.mRect = new Rect();
    }

    public PIPResizeHandlerView(Context context, int x0, int y0, int x1, int y1) {
        super(context);
        this.mLeftTopX = 0;
        this.mLeftTopY = 0;
        this.mRightBottomX = 0;
        this.mRightBottomY = 0;
        this.mResizeHandler_Bottom_Left = null;
        this.mResizeHandler_Bottom_Right = null;
        this.mResizeHandler_Top_Left = null;
        this.mResizeHandler_Top_Right = null;
        this.mMovingEdges = 0;
        this.mPaint = new Paint();
        this.mRect = new Rect();
        setPosition(x0, y0, x1, y1);
    }

    public void unbind() {
        this.mRect = null;
        this.mPaint = null;
    }

    public void setPosition(int x0, int y0, int x1, int y1) {
        this.mLeftTopX = x0;
        this.mLeftTopY = y0;
        this.mRightBottomX = x1;
        this.mRightBottomY = y1;
    }

    public void updatePosition(int direction) {
        this.mMovingEdges = direction;
        this.mRect.set(this.mLeftTopX, this.mLeftTopY, this.mRightBottomX, this.mRightBottomY);
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!(this.mPaint == null || this.mRect == null)) {
            int subWindowHandlerThick = (int) Util.dpToPx(getContext(), 3.0f);
            this.mPaint.setARGB(Ola_ShotParam.AnimalMask_Random, 93, 205, 230);
            this.mPaint.setStyle(Style.STROKE);
            this.mPaint.setStrokeWidth((float) subWindowHandlerThick);
            this.mPaint.setTextSize(22.0f);
            this.mPaint.setAntiAlias(true);
            this.mRect.set(this.mLeftTopX, this.mLeftTopY, this.mRightBottomX, this.mRightBottomY);
            canvas.drawRect(this.mRect, this.mPaint);
        }
        drawResizeHandlerCorner(canvas);
    }

    private void drawResizeHandlerCorner(Canvas canvas) {
        int subWindowHandlerThick = Math.round(Util.dpToPx(getContext(), 3.0f) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK);
        if (this.mResizeHandler_Bottom_Left == null) {
            this.mResizeHandler_Bottom_Left = BitmapFactory.decodeResource(getResources(), R.drawable.sub_window_handler_left_bottom);
        }
        if (this.mResizeHandler_Bottom_Right == null) {
            this.mResizeHandler_Bottom_Right = BitmapFactory.decodeResource(getResources(), R.drawable.sub_window_handler_right_bottom);
        }
        if (this.mResizeHandler_Top_Left == null) {
            this.mResizeHandler_Top_Left = BitmapFactory.decodeResource(getResources(), R.drawable.sub_window_handler_left_top);
        }
        if (this.mResizeHandler_Top_Right == null) {
            this.mResizeHandler_Top_Right = BitmapFactory.decodeResource(getResources(), R.drawable.sub_window_handler_right_top);
        }
        boolean notMoving = this.mMovingEdges == 0;
        if (!((this.mMovingEdges & 8) == 0 || (this.mMovingEdges & 1) == 0) || notMoving) {
            canvas.drawBitmap(this.mResizeHandler_Bottom_Left, (float) (this.mLeftTopX - subWindowHandlerThick), (float) ((this.mRightBottomY - this.mResizeHandler_Bottom_Left.getHeight()) + subWindowHandlerThick), null);
        }
        if (!((this.mMovingEdges & 8) == 0 || (this.mMovingEdges & 4) == 0) || notMoving) {
            canvas.drawBitmap(this.mResizeHandler_Bottom_Right, (float) ((this.mRightBottomX - this.mResizeHandler_Bottom_Right.getWidth()) + subWindowHandlerThick), (float) ((this.mRightBottomY - this.mResizeHandler_Bottom_Right.getHeight()) + subWindowHandlerThick), null);
        }
        if (!((this.mMovingEdges & 2) == 0 || (this.mMovingEdges & 1) == 0) || notMoving) {
            canvas.drawBitmap(this.mResizeHandler_Top_Left, (float) (this.mLeftTopX - subWindowHandlerThick), (float) (this.mLeftTopY - subWindowHandlerThick), null);
        }
        if (((this.mMovingEdges & 2) != 0 && (this.mMovingEdges & 4) != 0) || notMoving) {
            canvas.drawBitmap(this.mResizeHandler_Top_Right, (float) ((this.mRightBottomX - this.mResizeHandler_Top_Right.getWidth()) + subWindowHandlerThick), (float) (this.mLeftTopY - subWindowHandlerThick), null);
        }
    }
}
