package com.lge.camera.components;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class OpenGLYUVRenderer implements Renderer {
    private static final int ONE = 65535;
    private static final int[] squareVertices;
    private static final int[] squareVerticesPort;
    private static final int[] textureVertices;
    private static final int[] textureVertices_hFlip;
    float color;
    int g_index;
    private final String mFragmentShader;
    private boolean mIsRendererReady;
    private boolean mIsRenderingState;
    private int mProgram;
    private RendererAction mRA;
    private IntBuffer mSquareVerLand;
    private IntBuffer mSquareVerPort;
    private IntBuffer mSquareVers;
    private int mTexCoordsAttr;
    private IntBuffer mTextureVer;
    private IntBuffer mTextureVerHflip;
    private int mUniformVideoFrame;
    private int mUniformVideoFrameUV;
    private final String mVertexShader;
    private int mVerticesAttr;
    private int[] mVideoFrameTexture;
    private byte[] mYuvData;
    private int mYuvHeight;
    private int mYuvWidth;
    private boolean mflipH;
    boolean needCreateTexture;
    byte[] uv_array;
    byte[] y_array;

    public interface RendererAction {
        void startPreview();
    }

    public OpenGLYUVRenderer(RendererAction action) {
        this.mYuvData = null;
        this.mVideoFrameTexture = new int[2];
        this.mflipH = true;
        this.mIsRendererReady = false;
        this.mIsRenderingState = false;
        this.mRA = null;
        this.y_array = null;
        this.uv_array = null;
        this.needCreateTexture = true;
        this.color = 0.0f;
        this.mVertexShader = "attribute vec4 position;\nattribute highp vec4 inputTextureCoordinate;\nvarying highp vec2 textureCoordinate;\nvoid main() {\n          gl_Position = position;\n          textureCoordinate = inputTextureCoordinate.xy;\n}\n";
        this.mFragmentShader = "varying highp vec2 textureCoordinate;                                                                                           \n                                                                                                                                                                                         \nuniform highp sampler2D videoFrame;                                                                                                           \nuniform highp sampler2D videoFrameUV;                                                                                                       \n                                                                                                                                                                                              \nconst highp mat3 yuv2rgb = mat3(                                                                                                    \n                          1, 0, 1.596,                                                                            \n                          1, -0.391, -0.813,                                                              \n                          1, 2.018, 0                                                                                     \n                          );                                                                                                              \n                                                                                                                                                                                              \nvoid main() {                                                                                                                                                               \n     highp vec3 yuv = vec3(                                                                                                                                  \n     1.1643 * (texture2D(videoFrame, textureCoordinate).r - 0.0625),        \n     texture2D(videoFrameUV, textureCoordinate).a - 0.5,                                          \n     texture2D(videoFrameUV, textureCoordinate).r - 0.5                                \n     );                                                                                                                                                                       \n                                                                                                                                                                                         \n    highp vec3 rgb = yuv * yuv2rgb;                                                                                                             \n                                                                                                                                                                                         \n    gl_FragColor = vec4(rgb, 1.0);                                                                                                       \n}                                                                                                                                                                                        \n";
        this.mSquareVerLand = loadBuffer(squareVertices);
        this.mSquareVerPort = loadBuffer(squareVerticesPort);
        this.mTextureVer = loadBuffer(textureVertices);
        this.mTextureVerHflip = loadBuffer(textureVertices_hFlip);
        this.mRA = action;
    }

    public OpenGLYUVRenderer(RendererAction action, boolean landscape) {
        this(action);
        this.mSquareVers = landscape ? this.mSquareVerLand : this.mSquareVerPort;
    }

    private IntBuffer loadBuffer(int[] intBuffer) {
        IntBuffer intBuf = ByteBuffer.allocateDirect(intBuffer.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        intBuf.put(intBuffer).position(0);
        return intBuf;
    }

    public void clearYuvData() {
        this.mYuvData = null;
        this.y_array = null;
        this.uv_array = null;
        this.mIsRenderingState = false;
    }

    public void closeYUVRenderer() {
        clearYuvData();
        this.mSquareVers = null;
        if (this.mSquareVerLand != null) {
            this.mSquareVerLand.clear();
            this.mSquareVerLand = null;
        }
        if (this.mSquareVerPort != null) {
            this.mSquareVerPort.clear();
            this.mSquareVerPort = null;
        }
        if (this.mTextureVer != null) {
            this.mTextureVer.clear();
            this.mTextureVer = null;
        }
        if (this.mTextureVerHflip != null) {
            this.mTextureVerHflip.clear();
            this.mTextureVerHflip = null;
        }
        this.mRA = null;
    }

    public boolean setYuvData(byte[] data, int width, int height) {
        if (this.mIsRenderingState) {
            return false;
        }
        this.mYuvData = data;
        this.mYuvWidth = width;
        this.mYuvHeight = height;
        int y_size = width * height;
        int uv_size = (width * height) / 2;
        if (this.y_array == null || this.y_array.length != y_size) {
            this.y_array = new byte[y_size];
            this.needCreateTexture = true;
        }
        if (this.uv_array == null || this.uv_array.length != uv_size) {
            this.uv_array = new byte[uv_size];
        }
        synchronized (this) {
            System.arraycopy(data, 0, this.y_array, 0, y_size);
            System.arraycopy(data, y_size, this.uv_array, 0, uv_size);
        }
        this.mIsRendererReady = true;
        return true;
    }

    public void setNeedCreateTexture(boolean value) {
        this.needCreateTexture = value;
    }

    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X);
        GLES20.glClear(16640);
        if (this.mYuvData != null && this.mProgram != 0) {
            if (this.mIsRendererReady) {
                this.mIsRenderingState = true;
                GLES20.glUseProgram(this.mProgram);
                checkGlError("glUseProgram");
                if (this.needCreateTexture) {
                    GLES20.glBindTexture(3553, this.mVideoFrameTexture[0]);
                    GLES20.glTexParameteri(3553, 10241, 9729);
                    GLES20.glTexParameteri(3553, 10240, 9729);
                    GLES20.glTexParameteri(3553, 10242, 33071);
                    GLES20.glTexParameteri(3553, 10243, 33071);
                    GLES20.glTexImage2D(3553, 0, 6409, this.mYuvWidth, this.mYuvHeight, 0, 6409, 5121, ByteBuffer.wrap(this.y_array));
                    checkGlError("set Y image");
                    GLES20.glBindTexture(3553, this.mVideoFrameTexture[1]);
                    GLES20.glTexParameteri(3553, 10241, 9728);
                    GLES20.glTexParameteri(3553, 10240, 9728);
                    GLES20.glTexParameteri(3553, 10242, 33071);
                    GLES20.glTexParameteri(3553, 10243, 33071);
                    try {
                        GLES20.glTexImage2D(3553, 0, 6410, this.mYuvWidth / 2, this.mYuvHeight / 2, 0, 6410, 5121, ByteBuffer.wrap(this.uv_array));
                        checkGlError("set UV image");
                    } catch (NullPointerException e) {
                        CamLog.e(FaceDetector.TAG, "OpenGLYUVRenderer error : " + e);
                    } catch (RuntimeException e2) {
                        CamLog.e(FaceDetector.TAG, "OpenGLYUVRenderer error : " + e2);
                    }
                    this.needCreateTexture = false;
                    CamLog.d(FaceDetector.TAG, "DrawFrame :Need End");
                    this.mIsRenderingState = false;
                    return;
                }
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(3553, this.mVideoFrameTexture[0]);
                try {
                    GLES20.glTexSubImage2D(3553, 0, 0, 0, this.mYuvWidth, this.mYuvHeight, 6409, 5121, ByteBuffer.wrap(this.y_array));
                    checkGlError("set Y image");
                } catch (NullPointerException e3) {
                    CamLog.e(FaceDetector.TAG, "OpenGLYUVRenderer error : " + e3);
                } catch (RuntimeException e22) {
                    CamLog.e(FaceDetector.TAG, "OpenGLYUVRenderer error : " + e22);
                }
                GLES20.glActiveTexture(33985);
                GLES20.glBindTexture(3553, this.mVideoFrameTexture[1]);
                try {
                    GLES20.glTexSubImage2D(3553, 0, 0, 0, this.mYuvWidth / 2, this.mYuvHeight / 2, 6410, 5121, ByteBuffer.wrap(this.uv_array));
                    checkGlError("set UV image");
                } catch (NullPointerException e32) {
                    CamLog.e(FaceDetector.TAG, "OpenGLYUVRenderer error : " + e32);
                } catch (RuntimeException e222) {
                    CamLog.e(FaceDetector.TAG, "OpenGLYUVRenderer error : " + e222);
                }
                GLES20.glUniform1i(this.mUniformVideoFrame, 0);
                GLES20.glUniform1i(this.mUniformVideoFrameUV, 1);
                GLES20.glVertexAttribPointer(this.mVerticesAttr, 2, 5132, false, 0, this.mSquareVers);
                GLES20.glEnableVertexAttribArray(this.mVerticesAttr);
                if (this.mflipH) {
                    GLES20.glVertexAttribPointer(this.mTexCoordsAttr, 2, 5132, false, 0, this.mTextureVerHflip);
                    GLES20.glEnableVertexAttribArray(this.mTexCoordsAttr);
                } else {
                    GLES20.glVertexAttribPointer(this.mTexCoordsAttr, 2, 5132, false, 0, this.mTextureVer);
                    GLES20.glEnableVertexAttribArray(this.mTexCoordsAttr);
                }
                checkGlError("test4");
                GLES20.glDrawArrays(5, 0, 4);
                this.mIsRenderingState = false;
                return;
            }
            this.mIsRenderingState = false;
        }
    }

    public void setflipH(boolean flipH) {
        this.mflipH = flipH;
    }

    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        CamLog.d(FaceDetector.TAG, "GLSurface onSurfaceChanged width = " + width + " / height = " + height);
        this.mIsRendererReady = false;
        GLES20.glViewport(0, 0, width, height);
        if (this.mRA != null) {
            CamLog.d(FaceDetector.TAG, "OpenGL rederer-onSurfaceChanged:startPreview");
            this.mRA.startPreview();
        }
    }

    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        this.mProgram = createProgram("attribute vec4 position;\nattribute highp vec4 inputTextureCoordinate;\nvarying highp vec2 textureCoordinate;\nvoid main() {\n          gl_Position = position;\n          textureCoordinate = inputTextureCoordinate.xy;\n}\n", "varying highp vec2 textureCoordinate;                                                                                           \n                                                                                                                                                                                         \nuniform highp sampler2D videoFrame;                                                                                                           \nuniform highp sampler2D videoFrameUV;                                                                                                       \n                                                                                                                                                                                              \nconst highp mat3 yuv2rgb = mat3(                                                                                                    \n                          1, 0, 1.596,                                                                            \n                          1, -0.391, -0.813,                                                              \n                          1, 2.018, 0                                                                                     \n                          );                                                                                                              \n                                                                                                                                                                                              \nvoid main() {                                                                                                                                                               \n     highp vec3 yuv = vec3(                                                                                                                                  \n     1.1643 * (texture2D(videoFrame, textureCoordinate).r - 0.0625),        \n     texture2D(videoFrameUV, textureCoordinate).a - 0.5,                                          \n     texture2D(videoFrameUV, textureCoordinate).r - 0.5                                \n     );                                                                                                                                                                       \n                                                                                                                                                                                         \n    highp vec3 rgb = yuv * yuv2rgb;                                                                                                             \n                                                                                                                                                                                         \n    gl_FragColor = vec4(rgb, 1.0);                                                                                                       \n}                                                                                                                                                                                        \n");
        if (this.mProgram != 0) {
            checkGlError("mProgram");
            this.mVerticesAttr = GLES20.glGetAttribLocation(this.mProgram, "position");
            this.mTexCoordsAttr = GLES20.glGetAttribLocation(this.mProgram, "inputTextureCoordinate");
            this.mUniformVideoFrame = GLES20.glGetUniformLocation(this.mProgram, "videoFrame");
            this.mUniformVideoFrameUV = GLES20.glGetUniformLocation(this.mProgram, "videoFrameUV");
            checkGlError("glGetUniformLocation");
            GLES20.glGenTextures(2, this.mVideoFrameTexture, 0);
            checkGlError("glGenTextures");
        }
    }

    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader == 0) {
            return shader;
        }
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, 35713, compiled, 0);
        if (compiled[0] != 0) {
            return shader;
        }
        CamLog.d(FaceDetector.TAG, "Could not compile shader " + shaderType + ":");
        CamLog.d(FaceDetector.TAG, GLES20.glGetShaderInfoLog(shader));
        GLES20.glDeleteShader(shader);
        return 0;
    }

    private int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(35633, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(35632, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }
        int program = GLES20.glCreateProgram();
        if (program == 0) {
            return program;
        }
        GLES20.glAttachShader(program, vertexShader);
        checkGlError("glAttachShader");
        GLES20.glAttachShader(program, pixelShader);
        checkGlError("glAttachShader");
        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, 35714, linkStatus, 0);
        if (linkStatus[0] == 1) {
            return program;
        }
        CamLog.d(FaceDetector.TAG, "Could not link program: ");
        CamLog.d(FaceDetector.TAG, GLES20.glGetProgramInfoLog(program));
        GLES20.glDeleteProgram(program);
        return 0;
    }

    private void checkGlError(String op) {
        int error = GLES20.glGetError();
        if (error != 0) {
            CamLog.e(FaceDetector.TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    public synchronized void setSquareVertices(boolean windowLandscape) {
        if (windowLandscape) {
            this.mSquareVers = this.mSquareVerLand;
        } else {
            this.mSquareVers = this.mSquareVerPort;
        }
    }

    static {
        squareVertices = new int[]{-65535, -65535, ONE, -65535, -65535, ONE, ONE, ONE};
        squareVerticesPort = new int[]{-65535, ONE, -65535, -65535, ONE, ONE, ONE, -65535};
        textureVertices = new int[]{0, ONE, ONE, ONE, 0, 0, ONE, 0};
        textureVertices_hFlip = new int[]{ONE, ONE, 0, ONE, ONE, 0, 0, 0};
    }
}
