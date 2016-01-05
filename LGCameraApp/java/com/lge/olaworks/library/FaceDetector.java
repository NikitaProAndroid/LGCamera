package com.lge.olaworks.library;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.datastruct.JOlaBitmap;
import com.lge.olaworks.datastruct.Ola_FaceDetectorInfo;
import com.lge.olaworks.jni.OlaFaceDetectorJNI;

public class FaceDetector extends BaseEngine {
    public static final String TAG = "CameraApp";
    private boolean mAutoGet;
    private Callback mCallback;
    private Ola_FaceDetectorInfo mFDInfo;
    private Paint mPaint;
    private int mPreviewHeight;
    private int mPreviewWidth;
    private boolean mShowFaceRect;

    public interface Callback {
        void onFaceDetected(int i, Rect[] rectArr);
    }

    public FaceDetector() {
        this.mFDInfo = new Ola_FaceDetectorInfo();
        this.mAutoGet = true;
        this.mShowFaceRect = false;
        this.mPaint = new Paint(1);
        this.mCallback = null;
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setColor(-1);
        this.mPreviewHeight = 0;
        this.mPreviewWidth = 0;
    }

    public FaceDetector(Callback callback) {
        this.mFDInfo = new Ola_FaceDetectorInfo();
        this.mAutoGet = true;
        this.mShowFaceRect = false;
        this.mPaint = new Paint(1);
        this.mCallback = null;
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setColor(-1);
        this.mPreviewHeight = 0;
        this.mPreviewWidth = 0;
        this.mCallback = callback;
    }

    public boolean needRenderMode() {
        return false;
    }

    public void showFaceRect(boolean show) {
        this.mShowFaceRect = show;
    }

    public void setARGB(int a, int r, int g, int b) {
        this.mPaint.setARGB(a, r, g, b);
    }

    public void setStrokeWidth(float width) {
        this.mPaint.setStrokeWidth(width);
    }

    public Ola_FaceDetectorInfo getFDInfo() {
        return this.mFDInfo;
    }

    public String getTag() {
        return TAG;
    }

    public int create() {
        int retVal = OlaFaceDetectorJNI.create();
        if (retVal != 0) {
            return retVal;
        }
        return OlaFaceDetectorJNI.initialize();
    }

    public int destroy() {
        CamLog.v(TAG, "destroy()");
        int destroy = OlaFaceDetectorJNI.destroy();
        return OlaFaceDetectorJNI.initialize();
    }

    public int processPreview(JOlaBitmap rawContext) {
        int retVal = OlaFaceDetectorJNI.processPreviewRaw(rawContext);
        if (this.mAutoGet) {
            this.mFDInfo.clear();
            OlaFaceDetectorJNI.getProcessInfo(this.mFDInfo);
            if (this.mCallback != null) {
                this.mCallback.onFaceDetected(this.mFDInfo.numDetectedFaces, this.mFDInfo.detectedFaces);
            }
            if (rawContext.orientation == 0) {
                this.mPreviewWidth = rawContext.width;
                this.mPreviewHeight = rawContext.height;
            } else if (rawContext.orientation == 3) {
                this.mPreviewWidth = rawContext.height;
                this.mPreviewHeight = rawContext.width;
            }
        }
        return retVal;
    }

    public int processImage(Bitmap bitmap, int orientation) {
        int retVal = OlaFaceDetectorJNI.processImageBitmap(bitmap, 0);
        if (this.mAutoGet) {
            if (this.mPreviewWidth == 0 || this.mPreviewHeight == 0) {
                this.mFDInfo.clear();
                OlaFaceDetectorJNI.getProcessInfo(this.mFDInfo);
            } else {
                float bitmapWidth = (float) bitmap.getWidth();
                float wRatio = bitmapWidth / ((float) this.mPreviewWidth);
                float hRatio = ((float) bitmap.getHeight()) / ((float) this.mPreviewHeight);
                int i;
                if (orientation == 0) {
                    CamLog.d(TAG, "wRatio = " + wRatio + ", hRatio = " + hRatio);
                    for (i = 0; i < this.mFDInfo.numDetectedFaces; i++) {
                        this.mFDInfo.detectedFaces[i].left = (int) (((float) this.mFDInfo.detectedFaces[i].left) * wRatio);
                        this.mFDInfo.detectedFaces[i].right = (int) (((float) this.mFDInfo.detectedFaces[i].right) * wRatio);
                        this.mFDInfo.detectedFaces[i].top = (int) (((float) this.mFDInfo.detectedFaces[i].top) * hRatio);
                        this.mFDInfo.detectedFaces[i].bottom = (int) (((float) this.mFDInfo.detectedFaces[i].bottom) * hRatio);
                    }
                } else {
                    for (i = 0; i < this.mFDInfo.numDetectedFaces; i++) {
                        float left = wRatio * ((float) this.mFDInfo.detectedFaces[i].left);
                        float right = wRatio * ((float) this.mFDInfo.detectedFaces[i].right);
                        float top = hRatio * ((float) this.mFDInfo.detectedFaces[i].top);
                        this.mFDInfo.detectedFaces[i].left = (int) (bitmapWidth - (hRatio * ((float) this.mFDInfo.detectedFaces[i].bottom)));
                        this.mFDInfo.detectedFaces[i].right = (int) (bitmapWidth - top);
                        this.mFDInfo.detectedFaces[i].top = (int) left;
                        this.mFDInfo.detectedFaces[i].bottom = (int) right;
                    }
                }
            }
            OlaFaceDetectorJNI.initialize();
        }
        return retVal;
    }

    public Ola_FaceDetectorInfo getFaceDetectorInfo() {
        if (!this.mAutoGet) {
            this.mFDInfo.clear();
            OlaFaceDetectorJNI.getProcessInfo(this.mFDInfo);
        }
        return this.mFDInfo;
    }

    public void drawOverlay(Canvas canvas) {
        if (this.mFDInfo != null && this.mShowFaceRect) {
            for (int i = 0; i < this.mFDInfo.numDetectedFaces; i++) {
                canvas.drawRect(this.mFDInfo.detectedFaces[i], this.mPaint);
            }
        }
    }
}
