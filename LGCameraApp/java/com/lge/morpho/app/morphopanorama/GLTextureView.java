package com.lge.morpho.app.morphopanorama;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView.EGLContextFactory;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import com.lge.camera.util.CamLog;
import com.lge.morpho.utils.opengl.EGLHandler;
import com.lge.morpho.utils.opengl.GLESVersion;
import com.lge.voiceshutter.library.LGKeyRec;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

public class GLTextureView extends TextureView implements SurfaceTextureListener {
    private static final String LOG_TAG;
    public static final int RENDERMODE_WHEN_DIRTY = 0;
    private EGLContextFactory mEGLContextFactory;
    private EGLHandler mEGLHandler;
    private GL10 mGL;
    private GLESVersion mGLESVersion;
    private GLRenderingThread mGLThread;
    private final Object mGLThreadLockObj;
    private boolean mIsAvailable;
    private final Object mLockObj;
    private int mRenderMode;
    private Renderer mRenderer;
    private final AtomicBoolean mRequestedRendering;
    private int mSurfaceHeight;
    private int mSurfaceWidth;

    public class DefaultEGLContextFactory implements EGLContextFactory {
        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
            return egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, GLTextureView.this.mGLESVersion.getContextAttributeList());
        }

        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            if (!egl.eglDestroyContext(display, context)) {
                CamLog.e(GLTextureView.LOG_TAG, "display:" + display + " context: " + context);
            }
        }
    }

    public interface EGLConfigChooser {
        EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay, GLESVersion gLESVersion);
    }

    public class GLRenderingThread {
        private static final int TIMEOUT = 3;
        private final ExecutorService mEexecutor;

        public GLRenderingThread() {
            this.mEexecutor = Executors.newSingleThreadExecutor();
        }

        public void finish() {
            this.mEexecutor.shutdown();
            try {
                this.mEexecutor.awaitTermination(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.mEexecutor.shutdownNow();
        }

        public void queueEvent(GLRunnable r) {
            if (r.getEventId() == 2) {
                if (!GLTextureView.this.mRequestedRendering.get()) {
                    GLTextureView.this.mRequestedRendering.set(true);
                } else {
                    return;
                }
            }
            this.mEexecutor.submit(r);
        }
    }

    private class GLRunnable implements Runnable {
        public static final int EVENT_ID_AVAILABLE = 0;
        public static final int EVENT_ID_RENDER = 2;
        public static final int EVENT_ID_REQUEST_EVENT = 3;
        public static final int EVENT_ID_SIZE_CHANGED = 1;
        private int mHeight;
        private int mId;
        private SurfaceTexture mSurface;
        private Runnable mTask;
        private int mWidth;

        public GLRunnable(Runnable r) {
            this.mId = EVENT_ID_REQUEST_EVENT;
            this.mTask = r;
        }

        public GLRunnable(int id) {
            this.mId = id;
        }

        public GLRunnable(int id, SurfaceTexture surface, int w, int h) {
            this.mId = id;
            this.mSurface = surface;
            this.mWidth = w;
            this.mHeight = h;
        }

        public int getEventId() {
            return this.mId;
        }

        public void run() {
            synchronized (GLTextureView.this.mLockObj) {
                gl_process();
            }
        }

        private void gl_process() {
            if (GLTextureView.this.mRenderer == null) {
                throw new RuntimeException("");
            }
            switch (this.mId) {
                case EVENT_ID_AVAILABLE /*0*/:
                    CamLog.d(GLTextureView.LOG_TAG, "EVENT_ID_CREATE start");
                    if (GLTextureView.this.mEGLHandler == null) {
                        GLTextureView.this.mEGLHandler = new EGLHandler();
                        GLTextureView.this.mEGLHandler.initialize(GLTextureView.this.mGLESVersion, GLTextureView.this.mEGLContextFactory);
                        if (GLTextureView.this.mGLESVersion == GLESVersion.GLES10) {
                            GLTextureView.this.mGL = GLTextureView.this.mEGLHandler.getGL();
                        }
                    }
                    GLTextureView.this.mEGLHandler.setSurface(this.mSurface);
                    GLTextureView.this.mEGLHandler.bind();
                    GLTextureView.this.mRenderer.onSurfaceCreated(GLTextureView.this.mGL, GLTextureView.this.mEGLHandler.getConfig());
                    GLTextureView.this.mRenderer.onSurfaceChanged(GLTextureView.this.mGL, this.mWidth, this.mHeight);
                    GLTextureView.this.mEGLHandler.unbind();
                    GLTextureView.this.mIsAvailable = true;
                    CamLog.d(GLTextureView.LOG_TAG, "EVENT_ID_CREATE end");
                case EVENT_ID_SIZE_CHANGED /*1*/:
                    String access$100 = GLTextureView.LOG_TAG;
                    StringBuilder append = new StringBuilder().append("EVENT_ID_SIZE_CHANGE start ");
                    Object[] objArr = new Object[EVENT_ID_RENDER];
                    objArr[EVENT_ID_AVAILABLE] = Integer.valueOf(this.mWidth);
                    objArr[EVENT_ID_SIZE_CHANGED] = Integer.valueOf(this.mHeight);
                    CamLog.d(access$100, append.append(String.format("w:%d h:%d", objArr)).toString());
                    if (GLTextureView.this.mEGLHandler != null) {
                        GLTextureView.this.mEGLHandler.setSurface(this.mSurface);
                        GLTextureView.this.mEGLHandler.bind();
                        GLTextureView.this.mRenderer.onSurfaceChanged(GLTextureView.this.mGL, this.mWidth, this.mHeight);
                        GLTextureView.this.mRenderer.onDrawFrame(GLTextureView.this.mGL);
                        GLTextureView.this.mEGLHandler.swapBuffers();
                        GLTextureView.this.mEGLHandler.unbind();
                        CamLog.d(GLTextureView.LOG_TAG, "EVENT_ID_SIZE_CHANGE end ");
                    }
                case EVENT_ID_RENDER /*2*/:
                    CamLog.d(GLTextureView.LOG_TAG, "EVENT_ID_RENDER start");
                    if (GLTextureView.this.mEGLHandler != null && GLTextureView.this.mIsAvailable) {
                        GLTextureView.this.mEGLHandler.bind();
                        GLTextureView.this.mRenderer.onDrawFrame(GLTextureView.this.mGL);
                        GLTextureView.this.mEGLHandler.swapBuffers();
                        GLTextureView.this.mEGLHandler.unbind();
                        GLTextureView.this.mRequestedRendering.set(false);
                        CamLog.d(GLTextureView.LOG_TAG, "EVENT_ID_RENDER end");
                    }
                case EVENT_ID_REQUEST_EVENT /*3*/:
                    if (GLTextureView.this.mEGLHandler != null && GLTextureView.this.mIsAvailable && this.mTask != null) {
                        GLTextureView.this.mEGLHandler.bind();
                        this.mTask.run();
                        GLTextureView.this.mEGLHandler.unbind();
                    }
                default:
            }
        }
    }

    public interface Renderer {
        void onDrawFrame(GL10 gl10);

        void onSurfaceChanged(GL10 gl10, int i, int i2);

        void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig);
    }

    static {
        LOG_TAG = GLTextureView.class.getSimpleName();
    }

    public GLTextureView(Context context) {
        super(context);
        this.mLockObj = new Object();
        this.mGLThreadLockObj = new Object();
        this.mRequestedRendering = new AtomicBoolean();
        initialize();
    }

    public GLTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLockObj = new Object();
        this.mGLThreadLockObj = new Object();
        this.mRequestedRendering = new AtomicBoolean();
        initialize();
    }

    public GLTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mLockObj = new Object();
        this.mGLThreadLockObj = new Object();
        this.mRequestedRendering = new AtomicBoolean();
        initialize();
    }

    private void initialize() {
        CamLog.d(LOG_TAG, "initialize");
        setSurfaceTextureListener(this);
        this.mGLESVersion = GLESVersion.GLES10;
        this.mRenderMode = 0;
    }

    public void setEGLContextClientVersion(int version) {
        CamLog.d(LOG_TAG, "setEGLContextClientVersion version:" + version);
        switch (version) {
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                this.mGLESVersion = GLESVersion.GLES10;
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                this.mGLESVersion = GLESVersion.GLES20;
            default:
                throw new UnsupportedOperationException("Unsupported version");
        }
    }

    public void setEGLContextFactory(EGLContextFactory factory) {
        CamLog.d(LOG_TAG, "setEGLContextFactory ");
        this.mEGLContextFactory = factory;
    }

    public void setRenderer(Renderer renderer) {
        CamLog.d(LOG_TAG, "setRenderer");
        if (this.mEGLContextFactory == null) {
            this.mEGLContextFactory = new DefaultEGLContextFactory();
        }
        this.mRenderer = renderer;
    }

    public void onResume() {
        CamLog.d(LOG_TAG, "onResume");
    }

    public void onPause() {
        CamLog.d(LOG_TAG, "onPause");
    }

    public void setRenderMode(int render_mode) {
        CamLog.d(LOG_TAG, "setRenderMode mode:" + render_mode);
        this.mRenderMode = render_mode;
    }

    public void queueEvent(Runnable runnable) {
        CamLog.d(LOG_TAG, "queueEvent");
        synchronized (this.mGLThreadLockObj) {
            if (this.mGLThread != null) {
                this.mGLThread.queueEvent(new GLRunnable(runnable));
            }
        }
    }

    public void requestRender() {
        CamLog.d(LOG_TAG, "requestRender");
        synchronized (this.mGLThreadLockObj) {
            if (this.mGLThread != null) {
                this.mGLThread.queueEvent(new GLRunnable(2));
            }
        }
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        CamLog.d(LOG_TAG, "onSurfaceTextureAvailable start " + String.format("w:%d h:%d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)}) + " " + surface);
        this.mSurfaceWidth = width;
        this.mSurfaceHeight = height;
        synchronized (this.mGLThreadLockObj) {
            if (this.mGLThread == null) {
                this.mGLThread = new GLRenderingThread();
            }
        }
        this.mGLThread.queueEvent(new GLRunnable(0, surface, width, height));
        CamLog.d(LOG_TAG, "onSurfaceTextureAvailable end");
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        CamLog.d(LOG_TAG, "onSurfaceTextureDestroyed start " + surface);
        synchronized (this.mGLThreadLockObj) {
            if (this.mGLThread != null) {
                this.mGLThread.finish();
                this.mGLThread = null;
            }
        }
        synchronized (this.mLockObj) {
            if (this.mEGLHandler != null) {
                this.mEGLHandler.release();
                this.mEGLHandler = null;
            }
            this.mIsAvailable = false;
        }
        CamLog.d(LOG_TAG, "onSurfaceTextureDestroyed end");
        return true;
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        CamLog.d(LOG_TAG, "onSurfaceTextureSizeChanged start " + String.format("w:%d h:%d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)}));
        if (!(this.mSurfaceWidth == width && this.mSurfaceHeight == height)) {
            this.mSurfaceWidth = width;
            this.mSurfaceHeight = height;
            this.mGLThread.queueEvent(new GLRunnable(1, surface, width, height));
        }
        CamLog.d(LOG_TAG, "onSurfaceTextureSizeChanged end");
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        CamLog.d(LOG_TAG, "onSurfaceTextureUpdated");
    }
}
