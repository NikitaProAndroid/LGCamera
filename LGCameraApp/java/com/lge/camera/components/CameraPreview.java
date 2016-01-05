package com.lge.camera.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View.MeasureSpec;
import com.lge.camera.R;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.Util;
import com.lge.olaworks.datastruct.JOlaBitmap;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ImageFormat;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.EngineProcessor;
import com.lge.voiceshutter.library.LGKeyRec;

public class CameraPreview extends SurfaceView {
    public static final float DONT_CARE = 0.0f;
    protected static final int MAX_PREVIEW_BUFFER = 1;
    public static final String TAG = "CameraApp";
    private RectF drawRectF;
    private float mAspectRatio;
    private OnDeviceListener mDeviceListener;
    private int mDrawMode;
    protected EngineProcessor mEngineProcessor;
    private int mFaceCount;
    private Rect[] mFaceRect;
    private float mFaceRoundRectCorner;
    private int mFaceStrokeWidth;
    protected boolean mFlipH;
    private int mHorizontalTileSize;
    protected int mOrientation;
    protected int mOrientationFlip;
    private Paint mPaint;
    private float mPanoramaGuideBottom;
    private int mPanoramaGuideColor;
    private int mPanoramaGuideHeight;
    private float mPanoramaGuideLeft;
    private float mPanoramaGuideMoveRate;
    private float mPanoramaGuideRight;
    private float mPanoramaGuideTop;
    private int mPanoramaGuideWidth;
    private boolean mPanoramaInitialized;
    private PreviewCallback mParentPreviewCallback;
    private CameraPreviewCallback mPreviewCallback;
    private byte[] mPreviewCallbackBuffer;
    protected JOlaBitmap mRawContext;
    private int[] mRectColor;
    private int mSrcPreviewHeight;
    private int mSrcPreviewWidth;
    private int mVerticalTileSize;

    class CameraPreviewCallback implements PreviewCallback {
        private boolean completeFrame;
        private EngineProcessor engineProcessorCallback;
        private PreviewCallback mParentCallback;

        CameraPreviewCallback() {
            this.engineProcessorCallback = null;
            this.mParentCallback = null;
            this.completeFrame = false;
        }

        public void setEngineProcessor(EngineProcessor engine) {
            this.engineProcessorCallback = engine;
        }

        public void setParentPreviewCallback(PreviewCallback callback) {
            this.mParentCallback = callback;
        }

        public boolean isCompleteFrame() {
            return this.completeFrame;
        }

        public void setCompleteFrame(boolean set) {
            this.completeFrame = set;
        }

        public void closeCallback() {
            this.engineProcessorCallback = null;
            this.mParentCallback = null;
        }

        public void onPreviewFrame(byte[] data, Camera camera) {
            if (this.engineProcessorCallback != null) {
                if (CameraPreview.this.mDeviceListener == null || !CameraPreview.this.mDeviceListener.isCameraDeviceClosed()) {
                    Size previewSize = camera.getParameters().getPreviewSize();
                    if (previewSize != null) {
                        int resultWide = (int) (((double) (previewSize.width * previewSize.height)) * 1.5d);
                        if (!(previewSize == null || resultWide == data.length)) {
                            return;
                        }
                    }
                    if (!this.engineProcessorCallback.isEmptyEngine()) {
                        CameraPreview.this.processPreviewFrame(data);
                        if (this.mParentCallback != null) {
                            this.mParentCallback.onPreviewFrame(data, camera);
                        }
                    }
                    camera.addCallbackBuffer(data);
                    this.completeFrame = true;
                }
            }
        }
    }

    public interface OnDeviceListener {
        boolean isCameraDeviceClosed();
    }

    public CameraPreview(Context context) {
        super(context);
        this.mHorizontalTileSize = MAX_PREVIEW_BUFFER;
        this.mVerticalTileSize = MAX_PREVIEW_BUFFER;
        this.mEngineProcessor = null;
        this.mRawContext = null;
        this.mOrientation = 0;
        this.mOrientationFlip = 0;
        this.mFlipH = false;
        this.mParentPreviewCallback = null;
        this.mPreviewCallback = new CameraPreviewCallback();
        this.mPreviewCallbackBuffer = null;
        this.mDeviceListener = null;
        this.mDrawMode = 0;
        this.drawRectF = new RectF();
        this.mPaint = new Paint();
        this.mFaceRect = new Rect[5];
        this.mRectColor = new int[]{-1, -1, -1, -1, -1};
        this.mFaceStrokeWidth = 2;
        this.mFaceRoundRectCorner = 16.0f;
        this.mPanoramaInitialized = false;
        this.mPanoramaGuideColor = -65536;
        this.mPanoramaGuideMoveRate = RotateView.DEFAULT_TEXT_SCALE_X;
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mHorizontalTileSize = MAX_PREVIEW_BUFFER;
        this.mVerticalTileSize = MAX_PREVIEW_BUFFER;
        this.mEngineProcessor = null;
        this.mRawContext = null;
        this.mOrientation = 0;
        this.mOrientationFlip = 0;
        this.mFlipH = false;
        this.mParentPreviewCallback = null;
        this.mPreviewCallback = new CameraPreviewCallback();
        this.mPreviewCallbackBuffer = null;
        this.mDeviceListener = null;
        this.mDrawMode = 0;
        this.drawRectF = new RectF();
        this.mPaint = new Paint();
        this.mFaceRect = new Rect[5];
        this.mRectColor = new int[]{-1, -1, -1, -1, -1};
        this.mFaceStrokeWidth = 2;
        this.mFaceRoundRectCorner = 16.0f;
        this.mPanoramaInitialized = false;
        this.mPanoramaGuideColor = -65536;
        this.mPanoramaGuideMoveRate = RotateView.DEFAULT_TEXT_SCALE_X;
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mHorizontalTileSize = MAX_PREVIEW_BUFFER;
        this.mVerticalTileSize = MAX_PREVIEW_BUFFER;
        this.mEngineProcessor = null;
        this.mRawContext = null;
        this.mOrientation = 0;
        this.mOrientationFlip = 0;
        this.mFlipH = false;
        this.mParentPreviewCallback = null;
        this.mPreviewCallback = new CameraPreviewCallback();
        this.mPreviewCallbackBuffer = null;
        this.mDeviceListener = null;
        this.mDrawMode = 0;
        this.drawRectF = new RectF();
        this.mPaint = new Paint();
        this.mFaceRect = new Rect[5];
        this.mRectColor = new int[]{-1, -1, -1, -1, -1};
        this.mFaceStrokeWidth = 2;
        this.mFaceRoundRectCorner = 16.0f;
        this.mPanoramaInitialized = false;
        this.mPanoramaGuideColor = -65536;
        this.mPanoramaGuideMoveRate = RotateView.DEFAULT_TEXT_SCALE_X;
    }

    public void setDeviceListener(OnDeviceListener listener) {
        this.mDeviceListener = listener;
    }

    public void close(Camera camera) {
        CamLog.d(TAG, "close()-start, camera is null? :" + (camera == null));
        this.mEngineProcessor = null;
        this.mRawContext = null;
        if (camera != null) {
            try {
                camera.setPreviewCallbackWithBuffer(null);
                camera.setPreviewCallback(null);
                camera.addCallbackBuffer(null);
                camera.setErrorCallback(null);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        this.mPreviewCallbackBuffer = null;
        CamLog.d(TAG, "close()-end");
    }

    public void destroy() {
        this.mEngineProcessor = null;
        this.mRawContext = null;
        this.mPreviewCallbackBuffer = null;
        this.mParentPreviewCallback = null;
        if (this.mPreviewCallback != null) {
            this.mPreviewCallback.closeCallback();
            this.mPreviewCallback = null;
        }
        this.mPaint = null;
        this.mFaceRect = null;
    }

    public void setPreviewCallback(PreviewCallback callback) {
        this.mParentPreviewCallback = callback;
    }

    public void setFlipHorizontal(boolean flipH) {
        if (this.mEngineProcessor != null) {
            this.mEngineProcessor.setFlipHorizontal(flipH);
        }
        this.mFlipH = flipH;
    }

    public void setOrientation(int orientation) {
        orientation %= CameraConstants.DEGREE_360;
        if (orientation < 45) {
            this.mOrientation = 0;
            this.mOrientationFlip = 0;
        } else if (orientation < 135) {
            this.mOrientation = 3;
            this.mOrientationFlip = MAX_PREVIEW_BUFFER;
        } else if (orientation < Ola_ShotParam.Sampler_Next) {
            this.mOrientation = 2;
            this.mOrientationFlip = 2;
        } else if (orientation < Tag.ARTIST) {
            this.mOrientation = MAX_PREVIEW_BUFFER;
            this.mOrientationFlip = 3;
        } else {
            this.mOrientation = 0;
            this.mOrientationFlip = 0;
        }
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public boolean isCompleteProcessFrame() {
        if (this.mPreviewCallback != null) {
            return this.mPreviewCallback.isCompleteFrame();
        }
        return true;
    }

    public void initEngineProcessor(EngineProcessor processor, Camera camera) {
        if (camera != null) {
            CamLog.d(TAG, "initEngineProcessor-start");
            this.mEngineProcessor = null;
            camera.setPreviewCallback(null);
            camera.addCallbackBuffer(null);
            this.mEngineProcessor = processor;
            this.mRawContext = null;
            this.mPreviewCallbackBuffer = null;
            Parameters param = camera.getParameters();
            Size previewSize = param.getPreviewSize();
            if (previewSize == null) {
                CamLog.d(TAG, "initEngineProcessor-end. it doesn`t get a previewSize");
                return;
            }
            int bufSize;
            try {
                PixelFormat pixelinfo = new PixelFormat();
                PixelFormat.getPixelFormatInfo(param.getPreviewFormat(), pixelinfo);
                bufSize = ((previewSize.width * previewSize.height) * pixelinfo.bitsPerPixel) / 8;
            } catch (Exception e) {
                CamLog.e(TAG, "initEngineProcessor Exception : ", e);
                bufSize = ((previewSize.width * previewSize.height) * 12) / 8;
            }
            for (int i = 0; i < MAX_PREVIEW_BUFFER; i += MAX_PREVIEW_BUFFER) {
                this.mPreviewCallbackBuffer = new byte[bufSize];
                camera.addCallbackBuffer(this.mPreviewCallbackBuffer);
            }
            if (this.mPreviewCallback != null) {
                this.mPreviewCallback.setEngineProcessor(this.mEngineProcessor);
                this.mPreviewCallback.setParentPreviewCallback(this.mParentPreviewCallback);
                this.mPreviewCallback.setCompleteFrame(false);
            }
            camera.setPreviewCallbackWithBuffer(this.mPreviewCallback);
            this.mRawContext = new JOlaBitmap(previewSize.width, previewSize.height, Ola_ImageFormat.YUVPLANAR_NV21, this.mOrientation, null);
            CamLog.d(TAG, "initEngineProcessor-end");
        }
    }

    public void releasePreviewCallback(Camera camera) {
        if (camera != null) {
            camera.setPreviewCallbackWithBuffer(null);
            camera.setPreviewCallback(null);
        }
    }

    public void setEngineProcessor(EngineProcessor processor, Camera camera) {
        if (camera == null) {
            CamLog.d(TAG, "exit setEngineProcessor by camera == null");
        } else {
            initEngineProcessor(processor, camera);
        }
    }

    public void processPreviewFrame(byte[] data) {
        JOlaBitmap rawContext = this.mRawContext;
        if (this.mEngineProcessor != null) {
            rawContext.imageData = data;
            rawContext.orientation = this.mOrientation;
            this.mEngineProcessor.processPreview(rawContext);
        }
    }

    public void setAspectRatio(int width, int height) {
        float aspectRatio = ((float) width) / ((float) height);
        CamLog.i(TAG, "setAspectRatio:" + aspectRatio);
        setAspectRatio(aspectRatio);
    }

    public void setAspectRatio(float aspectRatio) {
        if (Float.valueOf(aspectRatio).compareTo(Float.valueOf(this.mAspectRatio)) != 0) {
            this.mAspectRatio = aspectRatio;
            requestLayout();
            invalidate();
            return;
        }
        CamLog.i(TAG, "setAspectRatio and aspectRatio are same");
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (Float.valueOf(this.mAspectRatio).compareTo(Float.valueOf(DONT_CARE)) != 0) {
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
            int width = widthSpecSize;
            int height = heightSpecSize;
            if (width > 0 && height > 0) {
                float defaultRatio = ((float) width) / ((float) height);
                if (!Util.isConfigureLandscape(getResources())) {
                    defaultRatio = RotateView.DEFAULT_TEXT_SCALE_X / defaultRatio;
                }
                if (defaultRatio < this.mAspectRatio) {
                    height = (int) (((float) width) / this.mAspectRatio);
                } else if (defaultRatio > this.mAspectRatio) {
                    width = (int) (((float) height) * this.mAspectRatio);
                }
                setMeasuredDimension(roundUpToTile(width, this.mHorizontalTileSize, widthSpecSize), roundUpToTile(height, this.mVerticalTileSize, heightSpecSize));
                initPanoramaGuide();
                return;
            }
        }
        CamLog.i(TAG, "mAspectRatio is 0.0f");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int roundUpToTile(int dimension, int tileSize, int maxDimension) {
        return Math.min((((dimension + tileSize) - 1) / tileSize) * tileSize, maxDimension);
    }

    public void setDrawMode(int mode) {
        this.mDrawMode = mode;
    }

    public int getDrawMode() {
        return this.mDrawMode;
    }

    protected void onDraw(Canvas canvas) {
        switch (this.mDrawMode) {
            case MAX_PREVIEW_BUFFER /*1*/:
                if (this.mFaceCount > 0) {
                    if (!(getMeasuredWidth() == this.mSrcPreviewWidth || getMeasuredHeight() == this.mSrcPreviewHeight)) {
                        canvas.scale(((float) getMeasuredWidth()) / ((float) this.mSrcPreviewWidth), ((float) getMeasuredHeight()) / ((float) this.mSrcPreviewHeight));
                    }
                    for (int i = 0; i < this.mFaceCount; i += MAX_PREVIEW_BUFFER) {
                        this.mPaint.setStyle(Style.STROKE);
                        this.mPaint.setColor(this.mRectColor[i]);
                        this.mPaint.setStrokeWidth((float) this.mFaceStrokeWidth);
                        if (Util.isConfigureLandscape(getResources())) {
                            this.drawRectF.left = (float) this.mFaceRect[i].left;
                            this.drawRectF.right = (float) this.mFaceRect[i].right;
                            this.drawRectF.top = (float) this.mFaceRect[i].top;
                            this.drawRectF.bottom = (float) this.mFaceRect[i].bottom;
                        } else {
                            this.drawRectF.left = (float) (this.mSrcPreviewWidth - this.mFaceRect[i].bottom);
                            this.drawRectF.right = (float) (this.mSrcPreviewWidth - this.mFaceRect[i].top);
                            this.drawRectF.top = (float) this.mFaceRect[i].left;
                            this.drawRectF.bottom = (float) this.mFaceRect[i].right;
                        }
                        canvas.drawRoundRect(this.drawRectF, this.mFaceRoundRectCorner, this.mFaceRoundRectCorner, this.mPaint);
                    }
                }
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                if (!(getMeasuredWidth() == this.mSrcPreviewWidth || getMeasuredHeight() == this.mSrcPreviewHeight)) {
                    canvas.scale(((float) getMeasuredWidth()) / ((float) this.mSrcPreviewWidth), ((float) getMeasuredHeight()) / ((float) this.mSrcPreviewHeight));
                }
                this.mPaint.setStyle(Style.STROKE);
                this.mPaint.setColor(this.mPanoramaGuideColor);
                this.mPaint.setStrokeWidth(4.0f);
                canvas.drawRect(this.mPanoramaGuideLeft, this.mPanoramaGuideTop, this.mPanoramaGuideRight, this.mPanoramaGuideBottom, this.mPaint);
            default:
        }
    }

    public void setSrcImageSize(int width, int height) {
        if (Util.isConfigureLandscape(getResources())) {
            this.mSrcPreviewWidth = width;
            this.mSrcPreviewHeight = height;
        } else {
            this.mSrcPreviewWidth = height;
            this.mSrcPreviewHeight = width;
        }
        float scale = getContext().getResources().getDisplayMetrics().density;
        this.mFaceStrokeWidth = (int) Math.max(CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK, 1.5f * scale);
        this.mFaceRoundRectCorner = 8.0f * scale;
    }

    public int getSrcImageWidth() {
        return this.mSrcPreviewWidth;
    }

    public int getSrcImageHeight() {
        return this.mSrcPreviewHeight;
    }

    public void setFaceRectangles(Rect[] rect, int faceCount) {
        this.mFaceRect = rect;
        this.mFaceCount = faceCount;
        postInvalidate();
    }

    public void resetFaceRectangles() {
        this.mFaceRect = null;
        this.mFaceCount = 0;
        postInvalidate();
    }

    public void setFaceRectangleColor(int index, int color) {
        this.mRectColor[index] = color;
    }

    public void initPanoramaGuide() {
        float hScale = ((float) this.mSrcPreviewHeight) / ((float) getMeasuredHeight());
        this.mPanoramaGuideWidth = (int) (((float) Common.getPixelFromDimens(getContext(), R.dimen.panorama_guide_box_width)) * (((float) this.mSrcPreviewWidth) / ((float) getMeasuredWidth())));
        this.mPanoramaGuideHeight = (int) (((float) Common.getPixelFromDimens(getContext(), R.dimen.panorama_guide_box_height)) * hScale);
        this.mPanoramaGuideMoveRate = (((float) this.mSrcPreviewWidth) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK) / (((float) this.mSrcPreviewWidth) * 0.5f);
        int weightLeft = MAX_PREVIEW_BUFFER;
        int weightTop = MAX_PREVIEW_BUFFER;
        if (ModelProperties.isHDmodel()) {
            weightLeft = 12;
        } else if (ModelProperties.isXGAmodel() || ModelProperties.isUVGAmodel()) {
            weightTop = -2;
            weightLeft = 10;
        }
        this.mPanoramaGuideLeft = (((float) (this.mSrcPreviewWidth - weightLeft)) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK) - (((float) this.mPanoramaGuideWidth) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK);
        this.mPanoramaGuideRight = this.mPanoramaGuideLeft + ((float) this.mPanoramaGuideWidth);
        this.mPanoramaGuideTop = (((float) (this.mSrcPreviewHeight - weightTop)) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK) - (((float) this.mPanoramaGuideHeight) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK);
        this.mPanoramaGuideBottom = this.mPanoramaGuideTop + ((float) this.mPanoramaGuideHeight);
    }

    public void setPanoramaGuideColor(boolean capture) {
        if (capture) {
            this.mPanoramaGuideColor = -16711936;
        } else {
            this.mPanoramaGuideColor = -65536;
        }
    }

    public void initializePanorama(int orientation) {
        this.mPanoramaInitialized = true;
    }

    public void stopPanoramaDrawing() {
        this.mPanoramaInitialized = false;
    }

    public void goOnPanoramaDrawing() {
        this.mPanoramaGuideColor = -65536;
        this.mPanoramaInitialized = true;
    }

    public void resetPanoramaGuide() {
        initPanoramaGuide();
        postInvalidate();
    }

    public void resetAutoPanorama() {
        postInvalidate();
    }

    public void setPanoramaGuidePosition(int direction, int hDist, int vDist, int status) {
        hDist = (int) (((float) hDist) * this.mPanoramaGuideMoveRate);
        vDist = (int) (((float) vDist) * this.mPanoramaGuideMoveRate);
        if (this.mPanoramaInitialized) {
            switch (direction) {
                case MAX_PREVIEW_BUFFER /*1*/:
                    this.mPanoramaGuideLeft = ((float) (this.mSrcPreviewWidth - hDist)) - (((float) this.mPanoramaGuideWidth) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK);
                    this.mPanoramaGuideRight = this.mPanoramaGuideLeft + ((float) this.mPanoramaGuideWidth);
                    this.mPanoramaGuideTop = ((((float) this.mSrcPreviewHeight) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK) - (((float) this.mPanoramaGuideHeight) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK)) - ((float) vDist);
                    this.mPanoramaGuideBottom = this.mPanoramaGuideTop + ((float) this.mPanoramaGuideHeight);
                    break;
                case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                    this.mPanoramaGuideLeft = ((float) (0 - hDist)) - (((float) this.mPanoramaGuideWidth) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK);
                    this.mPanoramaGuideRight = this.mPanoramaGuideLeft + ((float) this.mPanoramaGuideWidth);
                    this.mPanoramaGuideTop = ((((float) this.mSrcPreviewHeight) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK) - (((float) this.mPanoramaGuideHeight) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK)) - ((float) vDist);
                    this.mPanoramaGuideBottom = this.mPanoramaGuideTop + ((float) this.mPanoramaGuideHeight);
                    break;
                case LGKeyRec.EVENT_STARTED /*3*/:
                    this.mPanoramaGuideLeft = ((((float) this.mSrcPreviewWidth) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK) - (((float) this.mPanoramaGuideWidth) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK)) - ((float) hDist);
                    this.mPanoramaGuideRight = this.mPanoramaGuideLeft + ((float) this.mPanoramaGuideWidth);
                    this.mPanoramaGuideTop = (((float) this.mSrcPreviewHeight) - (((float) this.mPanoramaGuideHeight) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK)) - ((float) vDist);
                    this.mPanoramaGuideBottom = this.mPanoramaGuideTop + ((float) this.mPanoramaGuideHeight);
                    break;
                case LGKeyRec.EVENT_STOPPED /*4*/:
                    this.mPanoramaGuideLeft = ((((float) this.mSrcPreviewWidth) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK) - (((float) this.mPanoramaGuideWidth) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK)) - ((float) hDist);
                    this.mPanoramaGuideRight = this.mPanoramaGuideLeft + ((float) this.mPanoramaGuideWidth);
                    this.mPanoramaGuideTop = ((-((float) this.mPanoramaGuideHeight)) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK) - ((float) vDist);
                    this.mPanoramaGuideBottom = this.mPanoramaGuideTop + ((float) this.mPanoramaGuideHeight);
                    break;
            }
            postInvalidate();
        }
    }
}
