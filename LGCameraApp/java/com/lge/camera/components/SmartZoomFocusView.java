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
import com.lge.camera.util.DialogCreater;
import com.lge.camera.util.Util;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_ShotParam;

public class SmartZoomFocusView extends View {
    private int mCurrentDegree;
    private Bitmap mFocusBitmapAuto;
    private int mFocusBitmapAutoHeight;
    private int mFocusBitmapAutoRes;
    private int mFocusBitmapAutoWidth;
    private Bitmap mFocusBitmapManual;
    private int mFocusBitmapManualHeight;
    private int mFocusBitmapManualRes;
    private int mFocusBitmapManualWidth;
    private int mFocusMode;
    private int mLeftTopX;
    private int mLeftTopY;
    private Paint mPaint;
    private int mPreviousDegree;
    private int mPreviousFocusMode;
    private Rect mRect;
    private int mRightBottomX;
    private int mRightBottomY;
    private int mSubWindowHandlerThick;

    public SmartZoomFocusView(Context context) {
        super(context);
        this.mLeftTopX = 0;
        this.mLeftTopY = 0;
        this.mRightBottomX = 0;
        this.mRightBottomY = 0;
        this.mFocusMode = 0;
        this.mPreviousFocusMode = 0;
        this.mSubWindowHandlerThick = (int) Util.dpToPx(getContext(), CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK);
        this.mFocusBitmapAuto = null;
        this.mFocusBitmapManual = null;
        this.mPreviousDegree = 0;
        this.mCurrentDegree = 0;
        this.mFocusBitmapAutoRes = R.drawable.focus_smart_zoom;
        this.mFocusBitmapAutoWidth = 0;
        this.mFocusBitmapAutoHeight = 0;
        this.mFocusBitmapManualRes = R.drawable.focus_smart_zoom_manual;
        this.mFocusBitmapManualWidth = 0;
        this.mFocusBitmapManualHeight = 0;
        this.mPaint = new Paint();
        this.mRect = new Rect();
        prepare();
    }

    public SmartZoomFocusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mLeftTopX = 0;
        this.mLeftTopY = 0;
        this.mRightBottomX = 0;
        this.mRightBottomY = 0;
        this.mFocusMode = 0;
        this.mPreviousFocusMode = 0;
        this.mSubWindowHandlerThick = (int) Util.dpToPx(getContext(), CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK);
        this.mFocusBitmapAuto = null;
        this.mFocusBitmapManual = null;
        this.mPreviousDegree = 0;
        this.mCurrentDegree = 0;
        this.mFocusBitmapAutoRes = R.drawable.focus_smart_zoom;
        this.mFocusBitmapAutoWidth = 0;
        this.mFocusBitmapAutoHeight = 0;
        this.mFocusBitmapManualRes = R.drawable.focus_smart_zoom_manual;
        this.mFocusBitmapManualWidth = 0;
        this.mFocusBitmapManualHeight = 0;
        this.mPaint = new Paint();
        this.mRect = new Rect();
        prepare();
    }

    public SmartZoomFocusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLeftTopX = 0;
        this.mLeftTopY = 0;
        this.mRightBottomX = 0;
        this.mRightBottomY = 0;
        this.mFocusMode = 0;
        this.mPreviousFocusMode = 0;
        this.mSubWindowHandlerThick = (int) Util.dpToPx(getContext(), CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK);
        this.mFocusBitmapAuto = null;
        this.mFocusBitmapManual = null;
        this.mPreviousDegree = 0;
        this.mCurrentDegree = 0;
        this.mFocusBitmapAutoRes = R.drawable.focus_smart_zoom;
        this.mFocusBitmapAutoWidth = 0;
        this.mFocusBitmapAutoHeight = 0;
        this.mFocusBitmapManualRes = R.drawable.focus_smart_zoom_manual;
        this.mFocusBitmapManualWidth = 0;
        this.mFocusBitmapManualHeight = 0;
        this.mPaint = new Paint();
        this.mRect = new Rect();
        prepare();
    }

    public SmartZoomFocusView(Context context, int x0, int y0, int x1, int y1) {
        super(context);
        this.mLeftTopX = 0;
        this.mLeftTopY = 0;
        this.mRightBottomX = 0;
        this.mRightBottomY = 0;
        this.mFocusMode = 0;
        this.mPreviousFocusMode = 0;
        this.mSubWindowHandlerThick = (int) Util.dpToPx(getContext(), CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK);
        this.mFocusBitmapAuto = null;
        this.mFocusBitmapManual = null;
        this.mPreviousDegree = 0;
        this.mCurrentDegree = 0;
        this.mFocusBitmapAutoRes = R.drawable.focus_smart_zoom;
        this.mFocusBitmapAutoWidth = 0;
        this.mFocusBitmapAutoHeight = 0;
        this.mFocusBitmapManualRes = R.drawable.focus_smart_zoom_manual;
        this.mFocusBitmapManualWidth = 0;
        this.mFocusBitmapManualHeight = 0;
        this.mPaint = new Paint();
        this.mRect = new Rect();
        this.mLeftTopX = x0;
        this.mLeftTopY = y0;
        this.mRightBottomX = x1;
        this.mRightBottomY = y1;
        prepare();
    }

    public void prepare() {
        this.mFocusBitmapAuto = BitmapFactory.decodeResource(getContext().getResources(), this.mFocusBitmapAutoRes);
        this.mFocusBitmapManual = BitmapFactory.decodeResource(getContext().getResources(), this.mFocusBitmapManualRes);
        this.mFocusBitmapAutoWidth = this.mFocusBitmapAuto.getWidth();
        this.mFocusBitmapAutoHeight = this.mFocusBitmapAuto.getHeight();
        this.mFocusBitmapManualWidth = this.mFocusBitmapManual.getWidth();
        this.mFocusBitmapManualHeight = this.mFocusBitmapManual.getHeight();
        CameraConstants.SMARTZOOM_AUTO_ZOOM_AREA_MARGIN_WIDTH = this.mFocusBitmapAutoWidth;
        CameraConstants.SMARTZOOM_AUTO_ZOOM_AREA_MARGIN_HEIGHT = this.mFocusBitmapAutoHeight;
        CameraConstants.SMARTZOOM_MANUAL_ZOOM_AREA_MARGIN_WIDTH = this.mFocusBitmapManualWidth;
        CameraConstants.SMARTZOOM_MANUAL_ZOOM_AREA_MARGIN_HEIGHT = this.mFocusBitmapManualHeight;
    }

    public void unbind() {
        this.mRect = null;
        this.mPaint = null;
        if (this.mFocusBitmapAuto != null) {
            this.mFocusBitmapAuto.recycle();
            this.mFocusBitmapAuto = null;
        }
        if (this.mFocusBitmapManual != null) {
            this.mFocusBitmapManual.recycle();
            this.mFocusBitmapManual = null;
        }
    }

    private void setPosition(int x0, int y0, int x1, int y1) {
        this.mLeftTopX = x0;
        this.mLeftTopY = y0;
        this.mRightBottomX = x1;
        this.mRightBottomY = y1;
    }

    private void updatePosition() {
        this.mRect.set(this.mLeftTopX, this.mLeftTopY, this.mRightBottomX, this.mRightBottomY);
        invalidate();
    }

    public void setPositionAndUpdate(int x0, int y0, int x1, int y1, int orientation) {
        this.mCurrentDegree = orientation;
        setPosition(x0, y0, x1, y1);
        updatePosition();
    }

    public void setSmartZoomFocusViewMode(int mode) {
        this.mFocusMode = mode;
    }

    public int getSmartZoomFocusViewMode() {
        return this.mFocusMode;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mPaint != null && this.mRect != null) {
            if (this.mFocusMode == 2) {
                this.mPaint.setARGB(Ola_ShotParam.AnimalMask_Random, DialogCreater.DIALOG_ID_HELP_PANORAMA, 211, 236);
            } else {
                this.mPaint.setARGB(Ola_ShotParam.AnimalMask_Random, 0, Ola_ShotParam.AnimalMask_Random, 0);
            }
            this.mPaint.setStyle(Style.STROKE);
            this.mPaint.setStrokeWidth((float) this.mSubWindowHandlerThick);
            this.mPaint.setAntiAlias(true);
            this.mRect.set(this.mLeftTopX, this.mLeftTopY, this.mRightBottomX, this.mRightBottomY);
            int centerX = (this.mRightBottomX + this.mLeftTopX) / 2;
            int centerY = (this.mRightBottomY + this.mLeftTopY) / 2;
            if (this.mFocusMode == 2) {
                if (!(this.mCurrentDegree == this.mPreviousDegree && this.mFocusMode == this.mPreviousFocusMode)) {
                    if (this.mFocusBitmapAuto != null) {
                        this.mFocusBitmapAuto.recycle();
                    }
                    this.mFocusBitmapAuto = BitmapFactory.decodeResource(getContext().getResources(), this.mFocusBitmapAutoRes);
                    if (this.mCurrentDegree == 0 || this.mCurrentDegree == MediaProviderUtils.ROTATION_180) {
                        this.mFocusBitmapAuto = Util.rotate(this.mFocusBitmapAuto, this.mCurrentDegree);
                    } else {
                        this.mFocusBitmapAuto = Util.rotate(this.mFocusBitmapAuto, (this.mCurrentDegree + MediaProviderUtils.ROTATION_180) % CameraConstants.DEGREE_360);
                    }
                    this.mPreviousDegree = this.mCurrentDegree;
                    this.mPreviousFocusMode = this.mFocusMode;
                }
                canvas.drawBitmap(this.mFocusBitmapAuto, (float) (centerX - (this.mFocusBitmapAutoWidth / 2)), (float) (centerY - (this.mFocusBitmapAutoHeight / 2)), null);
                return;
            }
            if (!(this.mCurrentDegree == this.mPreviousDegree && this.mFocusMode == this.mPreviousFocusMode)) {
                if (this.mFocusBitmapManual != null) {
                    this.mFocusBitmapManual.recycle();
                }
                this.mFocusBitmapManual = BitmapFactory.decodeResource(getContext().getResources(), this.mFocusBitmapManualRes);
                if (this.mCurrentDegree == 0 || this.mCurrentDegree == MediaProviderUtils.ROTATION_180) {
                    this.mFocusBitmapManual = Util.rotate(this.mFocusBitmapManual, this.mCurrentDegree);
                } else {
                    this.mFocusBitmapManual = Util.rotate(this.mFocusBitmapManual, (this.mCurrentDegree + MediaProviderUtils.ROTATION_180) % CameraConstants.DEGREE_360);
                }
                this.mPreviousDegree = this.mCurrentDegree;
                this.mPreviousFocusMode = this.mFocusMode;
            }
            canvas.drawBitmap(this.mFocusBitmapManual, (float) (centerX - (this.mFocusBitmapManualWidth / 2)), (float) (centerY - (this.mFocusBitmapManualHeight / 2)), null);
        }
    }
}
