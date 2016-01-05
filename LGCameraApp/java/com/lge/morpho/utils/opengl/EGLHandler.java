package com.lge.morpho.utils.opengl;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView.EGLContextFactory;
import com.lge.camera.util.CamLog;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

public class EGLHandler {
    private static final String LOG_TAG;
    private EGL10 mEGL;
    private EGLConfig mEGLConfig;
    private EGLContext mEGLContext;
    private EGLContextFactory mEGLContextFactory;
    private EGLDisplay mEGLDisplay;
    private EGLSurface mEGLSurface;
    private GL10 mGL;

    static {
        LOG_TAG = EGLHandler.class.getSimpleName();
    }

    public void initialize(GLESVersion version, EGLContextFactory factory) {
        CamLog.d(LOG_TAG, "initialize " + version.toString() + " >>>");
        this.mEGL = (EGL10) EGLContext.getEGL();
        this.mEGLDisplay = this.mEGL.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (this.mEGLDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }
        if (this.mEGL.eglInitialize(this.mEGLDisplay, new int[2])) {
            this.mEGLConfig = new DefaultEGLConfigChooser().chooseConfig(this.mEGL, this.mEGLDisplay, version);
            if (this.mEGLConfig == null) {
                throw new RuntimeException("chooseConfig failed");
            }
            this.mEGLContextFactory = factory;
            this.mEGLContext = this.mEGLContextFactory.createContext(this.mEGL, this.mEGLDisplay, this.mEGLConfig);
            if (this.mEGLContext == EGL10.EGL_NO_CONTEXT) {
                throw new RuntimeException("eglCreateContext failed");
            }
            if (version == GLESVersion.GLES10) {
                this.mGL = (GL10) this.mEGLContext.getGL();
            }
            CamLog.d(LOG_TAG, "<<< initialize");
            return;
        }
        throw new RuntimeException("eglInitialize failed");
    }

    public void release() {
        CamLog.d(LOG_TAG, "release >>>");
        if (this.mEGL == null) {
            throw new RuntimeException("EGLHandler release failed");
        }
        if (this.mEGLSurface != null) {
            this.mEGL.eglDestroySurface(this.mEGLDisplay, this.mEGLSurface);
            this.mEGLSurface = null;
        }
        if (this.mEGLContext != null) {
            this.mEGLContextFactory.destroyContext(this.mEGL, this.mEGLDisplay, this.mEGLContext);
            this.mEGLContext = null;
            this.mEGLContextFactory = null;
        }
        if (this.mEGLDisplay != null) {
            this.mEGL.eglTerminate(this.mEGLDisplay);
            this.mEGLDisplay = null;
        }
        this.mEGLConfig = null;
        this.mEGL = null;
        CamLog.d(LOG_TAG, "<<< release");
    }

    public EGLContext getContext() {
        return this.mEGLContext;
    }

    public void setSurface(SurfaceTexture surface) {
        CamLog.d(LOG_TAG, "setSurface >>>");
        if (this.mEGLSurface != null) {
            this.mEGL.eglDestroySurface(this.mEGLDisplay, this.mEGLSurface);
        }
        this.mEGLSurface = this.mEGL.eglCreateWindowSurface(this.mEGLDisplay, this.mEGLConfig, surface, null);
        if (this.mEGLSurface == EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("eglCreateWindowSurface failed");
        }
        CamLog.d(LOG_TAG, "<<< setSurface");
    }

    public void bind() {
        CamLog.d(LOG_TAG, "bind");
        if (!this.mEGL.eglMakeCurrent(this.mEGLDisplay, this.mEGLSurface, this.mEGLSurface, this.mEGLContext)) {
            CamLog.e(LOG_TAG, "bind error -> " + this.mEGL.eglGetError());
            throw new RuntimeException("EGLHandler bind failed");
        }
    }

    public void unbind() {
        CamLog.d(LOG_TAG, "unbind");
        if (!this.mEGL.eglMakeCurrent(this.mEGLDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)) {
            CamLog.e(LOG_TAG, "unbind error -> " + this.mEGL.eglGetError());
            throw new RuntimeException("EGLHandler unbind failed");
        }
    }

    public void swapBuffers() {
        CamLog.d(LOG_TAG, "swapBuffers");
        if (!this.mEGL.eglSwapBuffers(this.mEGLDisplay, this.mEGLSurface)) {
            CamLog.e(LOG_TAG, "swapBuffers error -> " + this.mEGL.eglGetError());
            throw new RuntimeException("EGLHandler swapBuffers failed");
        }
    }

    public GL10 getGL() {
        return this.mGL;
    }

    public EGLConfig getConfig() {
        return this.mEGLConfig;
    }
}
