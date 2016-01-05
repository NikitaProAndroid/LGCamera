package com.lge.camera.components;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import com.lge.camera.components.OpenGLYUVRenderer.RendererAction;
import com.lge.camera.components.ProcessorThread.FrameCallback;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Util;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.datastruct.JOlaBitmap;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ImageFormat;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.EngineProcessor;
import com.lge.olaworks.library.FaceDetector;

public class OpenGLSurfaceView extends GLSurfaceView implements FrameCallback, RendererAction {
    protected static final int MAX_PREVIEW_BUFFER = 2;
    protected static final boolean PRINT_FUNC_PROFILING = false;
    private static final boolean USE_THREAD = true;
    private boolean isPause;
    protected Camera mCameraDevice;
    protected EngineProcessor mEngineProcessor;
    protected boolean mFlipH;
    private boolean mIsCompleteFrame;
    private GLSurfaceListener mListener;
    protected int mOrientation;
    protected int mOrientationFlip;
    private CameraPreviewCallback mPreviewCallback;
    private byte[] mPreviewCallbackBuffer;
    private OpenGLYUVRenderer mPreviewRenderer;
    protected JOlaBitmap mRawContext;
    private ProcessorThread mThread;
    private int previewH;
    private int previewW;

    class CameraPreviewCallback implements PreviewCallback {
        public static final String TAG = "CameraPreviewCallback";

        CameraPreviewCallback() {
        }

        public void onPreviewFrame(byte[] data, Camera camera) {
            if (data == null) {
                CamLog.v(TAG, "OnPreview With NULL data");
                return;
            }
            if (OpenGLSurfaceView.this.mListener != null) {
                OpenGLSurfaceView.this.mListener.onPreviewFrame(data, camera);
            }
            OpenGLSurfaceView.this.processPreviewFrameAsync(data);
        }
    }

    public interface GLSurfaceListener {
        boolean isPreviewing();

        void onPreviewFrame(byte[] bArr, Camera camera);

        void startPreview(LGParameters lGParameters, boolean z);
    }

    public OpenGLSurfaceView(Context context) {
        super(context);
        this.mCameraDevice = null;
        this.mPreviewCallback = null;
        this.mPreviewRenderer = null;
        this.mRawContext = null;
        this.mFlipH = PRINT_FUNC_PROFILING;
        this.mOrientation = 0;
        this.mOrientationFlip = 0;
        this.mIsCompleteFrame = USE_THREAD;
        this.mEngineProcessor = null;
        this.mPreviewCallbackBuffer = null;
        this.mListener = null;
        this.mThread = null;
        this.isPause = PRINT_FUNC_PROFILING;
        setDebugFlags(3);
        createOpenGLYUVRenderer(context);
    }

    public OpenGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCameraDevice = null;
        this.mPreviewCallback = null;
        this.mPreviewRenderer = null;
        this.mRawContext = null;
        this.mFlipH = PRINT_FUNC_PROFILING;
        this.mOrientation = 0;
        this.mOrientationFlip = 0;
        this.mIsCompleteFrame = USE_THREAD;
        this.mEngineProcessor = null;
        this.mPreviewCallbackBuffer = null;
        this.mListener = null;
        this.mThread = null;
        this.isPause = PRINT_FUNC_PROFILING;
        createOpenGLYUVRenderer(context);
    }

    public void createOpenGLYUVRenderer(Context context) {
        if (detectOpenGLES20(context)) {
            setEGLContextClientVersion(MAX_PREVIEW_BUFFER);
            this.mPreviewRenderer = new OpenGLYUVRenderer(this, Util.isConfigureLandscape(getResources()));
            setRenderer(this.mPreviewRenderer);
            setRenderMode(0);
        }
    }

    public void initOpenGLSurfaceView(GLSurfaceListener listener) {
        this.mListener = listener;
    }

    public void setEngineProcessor(EngineProcessor processor, Camera camera) {
        this.mIsCompleteFrame = PRINT_FUNC_PROFILING;
        initEngineProcessor(processor, camera);
    }

    public void initEngineProcessor(EngineProcessor processor, Camera camera) {
        if (camera != null) {
            camera.setPreviewCallbackWithBuffer(null);
            camera.setPreviewCallback(null);
            camera.addCallbackBuffer(null);
            this.mEngineProcessor = processor;
            this.mRawContext = null;
            setPreviewCallback(camera);
            startProcessorThread();
        }
    }

    private boolean detectOpenGLES20(Context context) {
        ConfigurationInfo info = ((ActivityManager) context.getSystemService("activity")).getDeviceConfigurationInfo();
        if (info != null && info.reqGlEsVersion >= 131072) {
            return USE_THREAD;
        }
        return PRINT_FUNC_PROFILING;
    }

    public void setFlipHorizontal(boolean flipH) {
        if (this.mEngineProcessor != null) {
            this.mEngineProcessor.setFlipHorizontal(flipH);
        }
        this.mFlipH = flipH;
        if (this.mPreviewRenderer != null) {
            this.mPreviewRenderer.setflipH(flipH);
        }
    }

    public void setRenderWindowConfig() {
        if (this.mPreviewRenderer != null) {
            this.mPreviewRenderer.setSquareVertices(Util.isConfigureLandscape(getResources()));
        }
    }

    public void setOrientation(int orientation) {
        if (Util.isConfigureLandscape(getResources())) {
            orientation %= CameraConstants.DEGREE_360;
        } else {
            orientation = (orientation + 90) % CameraConstants.DEGREE_360;
        }
        if (orientation < 45) {
            this.mOrientation = 0;
            this.mOrientationFlip = 0;
        } else if (orientation < 135) {
            this.mOrientation = 3;
            this.mOrientationFlip = 1;
        } else if (orientation < Ola_ShotParam.Sampler_Next) {
            this.mOrientation = MAX_PREVIEW_BUFFER;
            this.mOrientationFlip = MAX_PREVIEW_BUFFER;
        } else if (orientation < Tag.ARTIST) {
            this.mOrientation = 1;
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
        if (this.mThread != null) {
            return this.mThread.getcompleteFrame();
        }
        return this.mIsCompleteFrame;
    }

    public void setPreviewCallback(Camera camera) {
        CamLog.d(FaceDetector.TAG, "OpenGLSurfaceView - setPreviewCallback");
        this.mCameraDevice = camera;
        Parameters param = camera.getParameters();
        Size previewSize = param.getPreviewSize();
        if (previewSize == null) {
            CamLog.d(FaceDetector.TAG, "initEngineProcessor-end. it doesn`t get a previewSize");
            return;
        }
        PixelFormat pixelinfo = new PixelFormat();
        PixelFormat.getPixelFormatInfo(param.getPreviewFormat(), pixelinfo);
        if (this.mPreviewCallback == null) {
            this.mPreviewCallback = new CameraPreviewCallback();
        }
        int bufSize = ((previewSize.width * previewSize.height) * pixelinfo.bitsPerPixel) / 8;
        for (int i = 0; i < MAX_PREVIEW_BUFFER; i++) {
            this.mPreviewCallbackBuffer = new byte[bufSize];
            camera.addCallbackBuffer(this.mPreviewCallbackBuffer);
        }
        camera.setPreviewCallbackWithBuffer(this.mPreviewCallback);
        this.mRawContext = new JOlaBitmap(previewSize.width, previewSize.height, Ola_ImageFormat.YUVPLANAR_NV21, this.mOrientation, null);
        this.previewW = previewSize.width;
        this.previewH = previewSize.height;
    }

    public void releasePreviewCallback(Camera camera) {
        stopProcessorThread();
        if (camera != null) {
            camera.setPreviewCallbackWithBuffer(null);
            camera.setPreviewCallback(null);
            camera.addCallbackBuffer(null);
        }
        this.mPreviewCallbackBuffer = null;
        this.mCameraDevice = null;
    }

    public void processPreviewFrameAsync(byte[] data) {
        if (this.mThread != null && !this.mThread.nextFrame(data)) {
            addCallbackBuffer(data);
        }
    }

    public void addCallbackBuffer(byte[] data) {
        if (this.mCameraDevice != null) {
            this.mCameraDevice.addCallbackBuffer(data);
        }
    }

    public void processPreviewFrame(byte[] data) {
        if (this.mRawContext == null) {
            CamLog.w(FaceDetector.TAG, "mRawContext null");
        }
        if (!(this.mEngineProcessor == null || this.mRawContext == null)) {
            this.mRawContext.imageData = data;
            if (this.mFlipH) {
                this.mRawContext.orientation = this.mOrientationFlip;
            } else {
                this.mRawContext.orientation = this.mOrientation;
            }
            this.mEngineProcessor.processPreview(this.mRawContext);
        }
        if (data.length == ((int) Math.abs(((double) (this.previewW * this.previewH)) * 1.5d)) && this.mPreviewRenderer != null && this.mPreviewRenderer.setYuvData(data, this.previewW, this.previewH)) {
            requestRender();
        }
        addCallbackBuffer(data);
    }

    private void startProcessorThread() {
        if (this.mThread == null) {
            this.mThread = new ProcessorThread(this, FunctionProperties.isVoiceShutter() ? 5 : 10);
            this.mThread.start();
        }
        this.mThread.setCompleteFrame(PRINT_FUNC_PROFILING);
        if (!this.mThread.isAlive()) {
            try {
                this.mThread.start();
            } catch (IllegalThreadStateException e) {
                this.mThread = null;
                startProcessorThread();
            }
        }
    }

    private void stopProcessorThread() {
        if (this.mThread != null && this.mThread.isAlive()) {
            CamLog.v(FaceDetector.TAG, "stopPreviewRender(): Thread interrupted!!");
            this.mThread.interrupt();
            this.mThread.finish();
            this.mThread = null;
            CamLog.v(FaceDetector.TAG, "stopPreviewRender(): thread joined");
        }
    }

    public void setCheckFPS(boolean set) {
        if (this.mThread != null) {
            this.mThread.setCheckFPS(set);
        }
    }

    public void close(Camera camera) {
        CamLog.d(FaceDetector.TAG, "close()-start, camera is null? :" + (camera == null ? USE_THREAD : PRINT_FUNC_PROFILING));
        stopProcessorThread();
        this.mEngineProcessor = null;
        this.mRawContext = null;
        if (camera != null) {
            try {
                camera.setPreviewCallbackWithBuffer(null);
                camera.setPreviewCallback(null);
                camera.addCallbackBuffer(null);
                camera.setErrorCallback(null);
            } catch (RuntimeException e) {
                CamLog.i(FaceDetector.TAG, "close stop:", e);
            }
        }
        this.mPreviewCallbackBuffer = null;
        this.mCameraDevice = null;
        CamLog.d(FaceDetector.TAG, "close()-end");
    }

    public void clearData(boolean bReqeustRender) {
        CamLog.d(FaceDetector.TAG, "clearData() bReqeustRender:" + bReqeustRender);
        if (this.mPreviewRenderer != null) {
            this.mPreviewRenderer.clearYuvData();
            if (bReqeustRender) {
                requestRender();
            }
        }
    }

    public void setActivityPausing(boolean set) {
        this.isPause = set;
    }

    public void onResume() {
        CamLog.d(FaceDetector.TAG, "OpenGLSurfaceView onResume()");
        if (this.mPreviewRenderer != null) {
            this.mPreviewRenderer.setNeedCreateTexture(USE_THREAD);
        }
        super.onResume();
    }

    public void onDestroy() {
        CamLog.d(FaceDetector.TAG, "OpenGLSurfaceView onDestroy()");
        if (this.mPreviewRenderer != null) {
            try {
                setRenderer(null);
            } catch (IllegalStateException e) {
                CamLog.d(FaceDetector.TAG, "OpenGLSurfaceView onDestroy() - setRenderer IllegalStateException");
            }
            this.mPreviewRenderer.closeYUVRenderer();
            this.mPreviewRenderer = null;
        }
        if (this.mListener != null) {
            this.mListener = null;
        }
    }

    public void onPause() {
        CamLog.d(FaceDetector.TAG, "OpenGLSurfaceView onPause()");
        super.onPause();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        CamLog.d(FaceDetector.TAG, "OpenGLSurfaceView surfaceCreated holder = " + holder + ", isPause = " + this.isPause);
        if (!this.isPause) {
            super.surfaceCreated(holder);
        }
    }

    public void processFrameOnThread(byte[] data) {
        processPreviewFrame(data);
    }

    public void startPreview() {
        if (this.mListener != null && !this.mListener.isPreviewing()) {
            this.mListener.startPreview(null, USE_THREAD);
        }
    }
}
