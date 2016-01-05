package com.lge.filterpacks;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.GLFrame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.GenerateFinalPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.opengl.GLES20;
import android.os.SystemClock;
import android.util.Log;
import com.lge.camera.components.RotateView;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.util.DialogCreater;
import com.lge.morpho.app.morphopanorama.PanoramaApplication;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_Exif.ThumbNailSize;
import com.lge.olaworks.define.Ola_ImageFormat;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.voiceshutter.library.LGKeyRec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class DualRecorderFilter extends Filter {
    private static final int DEFAULT_LEARNING_DURATION = 3;
    private static final int DEFAULT_SCREEN_HEIGHT = 1080;
    private static final int DEFAULT_SCREEN_WIDTH = 1920;
    private static final int HAS_FISHEYE_MASK = 9;
    private static final int HAS_NO_MASK = 2;
    private static final int HAS_OVALBLUR_MASK = 3;
    private static final int HAS_OVERLAY_MASK = 4;
    private static final int HAS_SPLIT_MASK = 14;
    private static final int NEWFRAME_TIMEOUT = 100;
    private static final int NEWFRAME_TIMEOUT_REPEAT = 100;
    private static final String TAG = "DualRecorderFilter";
    private static boolean closeCalledBeforeManualStop;
    private static float degree;
    private static String fs_BasicPIPTexcoord;
    private static String fs_Clamp_t_coord;
    private static String fs_DefaultConstValue;
    private static String fs_DefaultParameters;
    private static String fs_DefaultSetting;
    private static String fs_DefaultViewandCoord;
    private static String fs_Default_Main_PIP_VIew;
    private static String fs_EffectMaskandCoord;
    private static String fs_EffectOverlayandCoord;
    private static String fs_Set_Mask_and_Overlay;
    private static String fs_UniformTexture;
    private static Camera mCamera;
    private static boolean mCameraState;
    private static SurfaceTexture mCameraSurfaceTexture;
    private static boolean mCameracheck;
    private static String mDualRecorderShader;
    private static String mDualRecorderShaderwithMaskFishEye;
    private static String mDualRecorderShaderwithMaskSplit;
    private static String mDualRecorderShaderwithOvalblur;
    private static String mDualRecorderShaderwithOverlay;
    private static SurfaceTexture mFrameSurfaceTexture;
    private static String[] mInputNames;
    private static boolean mIsCameraOpened;
    private static Bitmap mMaskBitmap;
    private static boolean mMaskChanged;
    private static GLFrame mMaskFrame;
    private static int mMaskMode;
    private static int mNextMaskMode;
    private static String[] mOutputNames;
    private static Bitmap mOverlayBitmap;
    private static GLFrame mOverlayFrame;
    private static int mPIPRectThick;
    private static int mPositionPIP_height;
    private static int mPositionPIP_width;
    private static int mPositionPIP_x;
    private static int mPositionPIP_y;
    private static boolean mRequestCapture;
    private static boolean mRequestMaskUpdate;
    private static int mSelectedPIP;
    private static float[] mSourceCoords;
    @GenerateFinalPort(name = "sourceListener")
    private static SurfaceTextureSourceListener mSourceListener;
    private static boolean mSurfaceCheck;
    private static boolean manualStopPreviewCalledBeforeOpen;
    private static boolean misSetBitmapActive;
    private AutoFocusCallback CameraAutoFocusOnCafCallback;
    private GLFrame mCameraFrame;
    @GenerateFieldPort(hasDefault = true, name = "cameraHeight")
    private int mCameraHeight;
    @GenerateFieldPort(hasDefault = true, name = "id")
    private int mCameraId;
    @GenerateFieldPort(hasDefault = true, name = "orientationDegree")
    private int mCameraOrientation;
    private Parameters mCameraParameters;
    @GenerateFieldPort(hasDefault = true, name = "cameraWidth")
    private int mCameraWidth;
    @GenerateFieldPort(hasDefault = true, name = "captureRotationDegree")
    private int mCaptureRotationDegree;
    private ShaderProgram mDualRecorderProgram;
    private ShaderProgram mDualRecorderProgram_selected;
    private ShaderProgram mDualRecorderProgramwithMaskFishEye;
    private ShaderProgram mDualRecorderProgramwithMaskSplit;
    private ShaderProgram mDualRecorderProgramwithOvalblur;
    private ShaderProgram mDualRecorderProgramwithOverlay;
    private boolean mFirstFrame;
    @GenerateFieldPort(hasDefault = true, name = "focusmode")
    private String mFocusmode;
    @GenerateFieldPort(hasDefault = true, name = "framerate")
    private int mFps;
    private int mFrameCount;
    private float[] mFrameTransform;
    @GenerateFieldPort(hasDefault = true, name = "learningDoneListener")
    private LearningDoneListener mLearningDoneListener;
    @GenerateFieldPort(hasDefault = true, name = "learningDuration")
    private int mLearningDuration;
    private final boolean mLogVerbose;
    private float[] mMappedCoords;
    private boolean mNewCameraFrameAvailable;
    private boolean mNewFrameAvailable;
    private MutableFrameFormat mOutputFormat;
    @GenerateFieldPort(hasDefault = true, name = "pHeight")
    private int mPipHeight;
    @GenerateFieldPort(hasDefault = true, name = "pPosX")
    private int mPipPosX;
    @GenerateFieldPort(hasDefault = true, name = "pPosY")
    private int mPipPosY;
    @GenerateFieldPort(hasDefault = true, name = "pWidth")
    private int mPipWidth;
    @GenerateFieldPort(hasDefault = true, name = "previewFrameListener")
    private PreviewFrameListener mPreviewFrameListener;
    private int mProcessCount;
    @GenerateFieldPort(hasDefault = true, name = "quality")
    private int mQuality;
    @GenerateFieldPort(hasDefault = true, name = "rThick")
    private int mRectThick;
    @GenerateFieldPort(hasDefault = true, name = "screenHeight")
    private int mScreenHeight;
    @GenerateFieldPort(hasDefault = true, name = "screenWidth")
    private int mScreenWidth;
    @GenerateFieldPort(hasDefault = true, name = "setBitmapDoneListener")
    private SetBitmapDoneListener mSetBitmapDoneListener;
    private GLFrame mSurfaceTextureFrame;
    @GenerateFieldPort(hasDefault = true, name = "sTextureHeight")
    private int mSurfaceTextureHeight;
    @GenerateFieldPort(hasDefault = true, name = "sTextureWidth")
    private int mSurfaceTextureWidth;
    @GenerateFieldPort(hasDefault = true, name = "waitForNewFrame")
    private boolean mWaitForNewFrame;
    @GenerateFieldPort(hasDefault = true, name = "waitTimeout")
    private int mWaitTimeout;
    private OnFrameAvailableListener onCameraFrameAvailableListener;
    private OnFrameAvailableListener onFrameAvailableListener;
    private long startTime;

    public interface LearningDoneListener {
        void onLearningDone(DualRecorderFilter dualRecorderFilter);
    }

    public interface PreviewFrameListener {
        void onPreviewFrame(byte[] bArr);
    }

    public interface SetBitmapDoneListener {
        void onSetBitmapDone(DualRecorderFilter dualRecorderFilter);
    }

    public interface SurfaceTextureSourceListener {
        void onSurfaceTextureSourceReady(SurfaceTexture surfaceTexture);
    }

    static {
        String[] strArr = new String[HAS_NO_MASK];
        strArr[0] = "backcamera";
        strArr[1] = "frontcamera";
        mInputNames = strArr;
        mOutputNames = new String[]{"video"};
        mMaskBitmap = null;
        mOverlayBitmap = null;
        mMaskFrame = null;
        mOverlayFrame = null;
        mRequestCapture = false;
        mRequestMaskUpdate = false;
        mCameraState = true;
        mCameracheck = false;
        mSurfaceCheck = false;
        misSetBitmapActive = false;
        mMaskChanged = false;
        mIsCameraOpened = false;
        manualStopPreviewCalledBeforeOpen = false;
        closeCalledBeforeManualStop = false;
        degree = 0.0f;
        mMaskMode = HAS_NO_MASK;
        mNextMaskMode = HAS_NO_MASK;
        mSourceCoords = new float[]{0.0f, RotateView.DEFAULT_TEXT_SCALE_X, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, RotateView.DEFAULT_TEXT_SCALE_X, RotateView.DEFAULT_TEXT_SCALE_X, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, RotateView.DEFAULT_TEXT_SCALE_X, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X};
        fs_DefaultSetting = "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nuniform samplerExternalOES tex_sampler_0;\nuniform samplerExternalOES tex_sampler_1;\n";
        fs_UniformTexture = "uniform sampler2D tex_sampler_2;\n#ifdef USE_OVERLAY_MASK\nuniform sampler2D tex_sampler_3;\n#endif\nuniform float rotate;\n";
        fs_DefaultParameters = "uniform float start_x_pip;\nuniform float start_y_pip;\nuniform float end_x_pip;\nuniform float end_y_pip;\nuniform float width_scale;\nuniform float height_scale;\nuniform float pip_mirror;\nvarying vec2 v_texcoord;\n";
        fs_DefaultViewandCoord = "    highp vec4 main_view;\n    highp vec4 pip_view;\n    highp vec4 rect_view;\n    highp vec2 pip_texcoord;\n    highp vec2 t_coord;\n";
        fs_DefaultConstValue = "    const float zero_point_zero = 0.0;\n    const float one_point_zero = 1.0;\n    const float two_point_zero = 2.0;\n    const float window_ratio = 16.0/9.0;\n    const float pos_down = 0.39;\n    const float color_min_offset = 0.05;\n    const float color_max_offset = 0.95;\n";
        fs_EffectMaskandCoord = "    highp vec4 effect_mask; \n    highp vec2 effect_texcoord;\n    highp vec2 effect_texcoord_temp;\n";
        fs_EffectOverlayandCoord = fs_EffectMaskandCoord + "    highp vec4 effect_overlay; \n";
        fs_Default_Main_PIP_VIew = "    if (pip_mirror == one_point_zero) {\n        main_view = texture2D(tex_sampler_0, vec2(v_texcoord.y, one_point_zero - v_texcoord.x));\n        pip_view = texture2D(tex_sampler_1, vec2(one_point_zero - pip_texcoord.y, one_point_zero - pip_texcoord.x));\n    } else {\n        main_view = texture2D(tex_sampler_0, vec2(one_point_zero - v_texcoord.y, one_point_zero - v_texcoord.x));\n        pip_view = texture2D(tex_sampler_1, vec2(pip_texcoord.y, one_point_zero - pip_texcoord.x));\n    }\n";
        fs_BasicPIPTexcoord = "    pip_texcoord.x = width_scale * (v_texcoord.x - start_x_pip);\n    pip_texcoord.y = height_scale * (v_texcoord.y - start_y_pip);\n";
        fs_Clamp_t_coord = "    t_coord = clamp( v_texcoord, vec2( start_x_pip , start_y_pip ), vec2( end_x_pip , end_y_pip ));\n";
        fs_Set_Mask_and_Overlay = "    if ( rotate == zero_point_zero ) {\n        effect_texcoord_temp = vec2(pip_texcoord.y, one_point_zero - pip_texcoord.x); \n        effect_texcoord.x = ( window_ratio ) * effect_texcoord_temp.x - pos_down;\n        effect_texcoord.y = effect_texcoord_temp.y;\n        effect_mask = texture2D(tex_sampler_2, effect_texcoord); \n#ifdef USE_OVERLAY_MASK\n        effect_overlay = texture2D(tex_sampler_3, effect_texcoord); \n#endif\n    } else if ( rotate == one_point_zero ) {\n        effect_texcoord_temp = vec2(pip_texcoord.x, pip_texcoord.y); \n        effect_texcoord.x = effect_texcoord_temp.x;\n        effect_texcoord.y = ( window_ratio ) * effect_texcoord_temp.y - pos_down;\n        if ( zero_point_zero <= effect_texcoord.y && effect_texcoord.y <= one_point_zero ){\n            effect_mask = texture2D(tex_sampler_2, effect_texcoord); \n#ifdef USE_OVERLAY_MASK\n            effect_overlay = texture2D(tex_sampler_3, effect_texcoord); \n#endif\n        } else {\n            effect_mask = vec4(one_point_zero,zero_point_zero,zero_point_zero,zero_point_zero); \n#ifdef USE_OVERLAY_MASK\n            effect_overlay = vec4(zero_point_zero,zero_point_zero,zero_point_zero,zero_point_zero); \n#endif\n        }\n    } else if ( rotate == two_point_zero ) {\n        effect_texcoord_temp = vec2(one_point_zero - pip_texcoord.y, pip_texcoord.x); \n        effect_texcoord.x = ( window_ratio ) * effect_texcoord_temp.x - pos_down;\n        effect_texcoord.y = effect_texcoord_temp.y;\n        effect_mask = texture2D(tex_sampler_2, effect_texcoord); \n#ifdef USE_OVERLAY_MASK\n        effect_overlay = texture2D(tex_sampler_3, effect_texcoord); \n#endif\n    } else {\n        effect_texcoord_temp = vec2(one_point_zero - pip_texcoord.x, one_point_zero - pip_texcoord.y); \n        effect_texcoord.x = effect_texcoord_temp.x;\n        effect_texcoord.y = ( window_ratio ) * effect_texcoord_temp.y - pos_down;\n        if ( zero_point_zero <= effect_texcoord.y && effect_texcoord.y <= one_point_zero ){\n            effect_mask = texture2D(tex_sampler_2, effect_texcoord); \n#ifdef USE_OVERLAY_MASK\n            effect_overlay = texture2D(tex_sampler_3, effect_texcoord); \n#endif\n        } else {\n            effect_mask = vec4(one_point_zero,zero_point_zero,zero_point_zero,zero_point_zero); \n#ifdef USE_OVERLAY_MASK\n            effect_overlay = vec4(zero_point_zero,zero_point_zero,zero_point_zero,zero_point_zero); \n#endif\n        }\n    }\n";
        mDualRecorderShader = fs_DefaultSetting + fs_DefaultParameters + "void main() {\n" + "    const float one_point_zero = 1.0;\n" + fs_DefaultViewandCoord + fs_BasicPIPTexcoord + fs_Default_Main_PIP_VIew + fs_Clamp_t_coord + "    pip_view = (t_coord != pip_texcoord) ? vec4(one_point_zero ,one_point_zero, one_point_zero ,one_point_zero) : pip_view;\n" + "    gl_FragColor = (t_coord != v_texcoord) ? main_view : pip_view;\n" + "}\n";
        mDualRecorderShaderwithOvalblur = fs_DefaultSetting + fs_UniformTexture + fs_DefaultParameters + "void main() {\n" + fs_DefaultConstValue + "    const float mask_offset = 0.6;\n" + fs_DefaultViewandCoord + fs_EffectMaskandCoord + fs_BasicPIPTexcoord + fs_Default_Main_PIP_VIew + fs_Set_Mask_and_Overlay + fs_Clamp_t_coord + "    if ( all( equal( t_coord, v_texcoord ) ) && ( effect_mask.g > mask_offset ) ) {\n" + "        pip_view = vec4( one_point_zero, one_point_zero, one_point_zero, one_point_zero );\n" + "    } else {\n" + "        pip_view = vec4( mix( main_view.xyz , pip_view.xyz, smoothstep(color_min_offset,color_max_offset,effect_mask.b) ) , one_point_zero );\n" + "    }\n" + "    gl_FragColor = ( t_coord != v_texcoord ) ? main_view : pip_view; \n " + "}\n";
        mDualRecorderShaderwithOverlay = "#define USE_OVERLAY_MASK\n" + fs_DefaultSetting + fs_UniformTexture + fs_DefaultParameters + "void main() {\n" + fs_DefaultConstValue + fs_DefaultViewandCoord + fs_EffectOverlayandCoord + fs_BasicPIPTexcoord + fs_Default_Main_PIP_VIew + fs_Set_Mask_and_Overlay + fs_Clamp_t_coord + "    if ( all( equal( t_coord, v_texcoord ) ) && ( effect_mask.r >= effect_mask.b ) ) {\n" + "        rect_view = main_view;\n" + "    } else {\n" + "        rect_view = pip_view;\n" + "    }\n" + "    if ( all( equal( t_coord, v_texcoord ) ) && effect_overlay.a == zero_point_zero ) {\n" + "        rect_view = rect_view;\n" + "    } else {\n" + "        rect_view = vec4( mix( rect_view.xyz , effect_overlay.xyz, smoothstep(color_min_offset,color_max_offset,effect_overlay.a) ) , one_point_zero );\n" + "    }\n" + "    gl_FragColor = ( t_coord != v_texcoord ) ? main_view : rect_view; \n " + "}\n";
        mDualRecorderShaderwithMaskFishEye = "#define USE_OVERLAY_MASK\n" + fs_DefaultSetting + fs_UniformTexture + fs_DefaultParameters + "void main() {\n" + fs_DefaultConstValue + "    const float zero_point_five = 0.5;\n" + "    const float effect_radius = 1.5;\n" + "    const float m_pi_2 = 1.570963;\n" + "    const float alpha = 3.750000;\n" + "    const float radius2 = 0.661250;\n" + "    const float factor = 1.610678;\n" + fs_DefaultViewandCoord + fs_EffectOverlayandCoord + "    vec4 fish_view;\n" + "    vec2 fish_texcoord;\n" + "    vec2 coord;\n" + "    vec2 new_coord;\n" + fs_BasicPIPTexcoord + "    if ( pip_mirror == one_point_zero ) {\n" + "        main_view = texture2D( tex_sampler_0 , vec2( v_texcoord.y , one_point_zero - v_texcoord.x ));\n" + "        fish_texcoord = vec2( one_point_zero - pip_texcoord.y , one_point_zero - pip_texcoord.x );\n" + "    } else {\n" + "        main_view = texture2D( tex_sampler_0 , vec2( one_point_zero - v_texcoord.y , one_point_zero - v_texcoord.x ));\n" + "        fish_texcoord = vec2( pip_texcoord.y , one_point_zero - pip_texcoord.x );\n" + "    }\n" + "    coord = fish_texcoord - vec2( zero_point_five , zero_point_five );\n" + "    float dist = length( coord * effect_radius );\n" + "    float radian = m_pi_2 - atan( alpha * sqrt( radius2 - dist * dist ), dist );\n" + "    float scalar = radian * factor / dist;\n" + "    new_coord = coord * scalar + vec2( zero_point_five , zero_point_five );\n" + "    fish_view = texture2D( tex_sampler_1, vec2( new_coord.x , new_coord.y ));\n" + fs_Set_Mask_and_Overlay + fs_Clamp_t_coord + "    if ( all( equal( t_coord, v_texcoord ) ) && effect_mask.r > effect_mask.b ) {\n" + "        rect_view = vec4( mix( main_view.xyz , effect_overlay.xyz, smoothstep(color_min_offset,color_max_offset,effect_overlay.a) ) , one_point_zero );\n" + "    } else {\n" + "        rect_view = vec4( mix( fish_view.xyz , effect_overlay.xyz, smoothstep(color_min_offset,color_max_offset,effect_overlay.a) ) , one_point_zero );\n" + "    }\n" + "    if( effect_texcoord.x <= 0.0 || effect_texcoord.x >= 1.0 || effect_texcoord.y <= 0.02 || effect_texcoord.y >= 0.98 ) \n" + "        rect_view = main_view;\n" + "    gl_FragColor = ( t_coord != v_texcoord ) ? main_view : rect_view; \n " + "}\n";
        mDualRecorderShaderwithMaskSplit = fs_DefaultSetting + "uniform sampler2D tex_sampler_2;\n" + "uniform sampler2D tex_sampler_3;\n" + "uniform float pip_mirror;\n" + "varying vec2 v_texcoord;\n" + "void main() {\n" + "    const float zero_point_zero = 0.0;\n" + "    const float one_point_zero = 1.0;\n" + "    const float x_coord_offset = 0.25;\n" + "    const float color_min_offset = 0.05;\n" + "    const float color_max_offset = 0.95;\n" + "    const float start_x_pip = zero_point_zero;\n" + "    const float start_y_pip = zero_point_zero;\n" + "    const float end_x_pip = one_point_zero;\n" + "    const float end_y_pip = one_point_zero;\n" + "    const float width_scale = one_point_zero;\n" + "    const float height_scale = one_point_zero;\n" + fs_DefaultViewandCoord + fs_BasicPIPTexcoord + "    if (pip_mirror == one_point_zero) {\n" + "        main_view = texture2D(tex_sampler_0, vec2(v_texcoord.y - x_coord_offset , one_point_zero - v_texcoord.x));\n" + "        pip_view = texture2D(tex_sampler_1, vec2(one_point_zero - pip_texcoord.y - x_coord_offset, one_point_zero - pip_texcoord.x));\n" + "    } else {\n" + "        main_view = texture2D(tex_sampler_0, vec2(one_point_zero - v_texcoord.y + x_coord_offset, one_point_zero - v_texcoord.x));\n" + "        pip_view = texture2D(tex_sampler_1, vec2(pip_texcoord.y + x_coord_offset, one_point_zero - pip_texcoord.x));\n" + "    }\n" + "    vec4 effect_mask = texture2D(tex_sampler_2, vec2(pip_texcoord.y, one_point_zero - pip_texcoord.x)); \n" + "    vec4 effect_overlay = texture2D(tex_sampler_3, vec2(pip_texcoord.y, one_point_zero - pip_texcoord.x)); \n" + fs_Clamp_t_coord + "    if ( all( equal( t_coord, v_texcoord ) ) && effect_mask.r > effect_mask.b) {\n" + "        rect_view = vec4( mix( main_view.xyz , effect_overlay.xyz, smoothstep(color_min_offset,color_max_offset,effect_overlay.a) ) , one_point_zero );\n" + "    } else {\n" + "        rect_view = vec4( mix( pip_view.xyz , effect_overlay.xyz, smoothstep(color_min_offset,color_max_offset,effect_overlay.a) ) , one_point_zero );\n" + "    }\n" + "    gl_FragColor = ( t_coord != v_texcoord ) ? main_view : rect_view; \n " + "}\n";
    }

    public DualRecorderFilter(String name) {
        super(name);
        this.mLearningDuration = HAS_OVALBLUR_MASK;
        this.mLearningDoneListener = null;
        this.mSetBitmapDoneListener = null;
        this.mScreenWidth = DEFAULT_SCREEN_WIDTH;
        this.mScreenHeight = DEFAULT_SCREEN_HEIGHT;
        this.mPipPosX = 0;
        this.mPipPosY = 0;
        this.mPipWidth = LGT_Limit.PREVIEW_SIZE_HEIGHT;
        this.mPipHeight = Tag.IMAGE_DESCRIPTION;
        this.mRectThick = 0;
        this.mPreviewFrameListener = null;
        this.mQuality = NEWFRAME_TIMEOUT_REPEAT;
        this.mCaptureRotationDegree = 0;
        this.mCameraWidth = ThumbNailSize.width;
        this.mCameraHeight = Ola_ShotParam.Sampler_Complete;
        this.mSurfaceTextureWidth = ThumbNailSize.width;
        this.mSurfaceTextureHeight = Ola_ShotParam.Sampler_Complete;
        this.mWaitForNewFrame = true;
        this.mWaitTimeout = PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME;
        this.mCameraId = 0;
        this.mFps = 30;
        this.mCameraOrientation = 0;
        this.mFocusmode = LGT_Limit.ISP_AUTOMODE_AUTO;
        this.mLogVerbose = true;
        this.startTime = -1;
        this.CameraAutoFocusOnCafCallback = new AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
            }
        };
        this.onFrameAvailableListener = new OnFrameAvailableListener() {
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                synchronized (DualRecorderFilter.this) {
                    DualRecorderFilter.this.mNewFrameAvailable = true;
                    DualRecorderFilter.this.notify();
                }
            }
        };
        this.onCameraFrameAvailableListener = new OnFrameAvailableListener() {
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                synchronized (DualRecorderFilter.this) {
                    DualRecorderFilter.this.mNewCameraFrameAvailable = true;
                    DualRecorderFilter.this.notify();
                }
            }
        };
        this.mFrameTransform = new float[16];
        this.mMappedCoords = new float[16];
    }

    public void setupPorts() {
        addOutputPort("video", ImageFormat.create(HAS_OVALBLUR_MASK, HAS_OVALBLUR_MASK));
    }

    private void createFormats() {
        this.mOutputFormat = ImageFormat.create(this.mSurfaceTextureWidth, this.mSurfaceTextureHeight, HAS_OVALBLUR_MASK, HAS_OVALBLUR_MASK);
    }

    private void mCameraopen(FilterContext context) {
        Log.v(TAG, "DualRecorderFilter.mCameraopen() mCameraState = " + mCameraState);
        if (mCamera == null && mCameraState) {
            Log.v(TAG, "DualRecorderFilter.mCameraopen() mCamera = Camera.open(mCameraId=" + this.mCameraId + ")");
            try {
                mCamera = Camera.openLegacy(this.mCameraId, Ola_ImageFormat.RGB_LABEL);
                try {
                    mIsCameraOpened = mCamera != null;
                    Log.v(TAG, "DualRecorderFilter.mCameraopen() : mIsCameraOpened = " + mIsCameraOpened);
                } catch (Exception e) {
                    Log.e(TAG, "DualRecorderFilter.mCameraopen() failed to open Camera");
                    e.printStackTrace();
                }
            } catch (Exception e2) {
                Log.e(TAG, "DualRecorderFilter.Camera.openLegacy() is failed : try to Camera.open");
                mCamera = Camera.open(this.mCameraId);
            }
        }
        if (mCamera != null) {
            getCameraParameters();
            this.mCameraParameters.set(CameraConstants.PARAM_KEY_DUAL_RECORDER, 1);
            mCamera.setParameters(this.mCameraParameters);
            this.mCameraFrame = (GLFrame) context.getFrameManager().newBoundFrame(this.mOutputFormat, DialogCreater.DIALOG_ID_HELP_CONTINUOUS_SHOT, 0);
            mCameraSurfaceTexture = new SurfaceTexture(this.mCameraFrame.getTextureId());
            try {
                mCamera.setPreviewTexture(mCameraSurfaceTexture);
                mCameraSurfaceTexture.setOnFrameAvailableListener(this.onCameraFrameAvailableListener);
                return;
            } catch (IOException e3) {
                throw new RuntimeException("Could not bind camera surface texture: " + e3.getMessage() + "!");
            }
        }
        Log.v(TAG, "DualRecorderFilter.mCameraopen() mCameraState = " + mCameraState);
        Log.v(TAG, "DualRecorderFilter.mCameraopen() mCamera = " + mCamera);
    }

    public void open(FilterContext context) {
        DisplayCurrentState("open");
        mFrameSurfaceTexture = new SurfaceTexture(this.mSurfaceTextureFrame.getTextureId());
        mFrameSurfaceTexture.setOnFrameAvailableListener(this.onFrameAvailableListener);
        mSourceListener.onSurfaceTextureSourceReady(mFrameSurfaceTexture);
        Log.v(TAG, "DualRecorderFilter.open() mSourceListener Setted.");
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            Log.e(TAG, "Delay exception");
        }
        if (closeCalledBeforeManualStop) {
            Log.e(TAG, "closeCalledBeforeManualStop is ture : change mCameraState to true");
            mCameraState = true;
        }
        if (mCamera == null && mCameraState) {
            Log.v(TAG, "DualRecorderFilter.open() calling mCameraopen(context)");
            mCameraopen(context);
            Log.v(TAG, "DualRecorderFilter.open() mCameraopen(context) done");
            Parameters params;
            if (this.mCameraId == 0) {
                if (mCamera != null && mCameraState) {
                    Log.v(TAG, "DualRecorderFilter.open(mCameraId == 0 ) setting parameters.");
                    params = mCamera.getParameters();
                    params.setFocusMode("continuous-video");
                    params.setRecordingHint(true);
                    mCamera.setParameters(params);
                    Log.v(TAG, "DualRecorderFilter.open(mCameraId == 0 ) setting parameters done.");
                    Log.v(TAG, "DualRecorderFilter.open(mCameraId == 0 ) calling mCamera.startPreview().");
                    mCamera.startPreview();
                    Log.v(TAG, "DualRecorderFilter.open(mCameraId == 0 ) calling mCamera.startPreview() done.");
                    mCamera.autoFocus(new AutoFocusCallback() {
                        public void onAutoFocus(boolean success, Camera camera) {
                            if (success) {
                                Log.v(DualRecorderFilter.TAG, "DualRecorderFilter.open() mCameraId 0 setting onAutoFocus");
                            } else {
                                Log.v(DualRecorderFilter.TAG, "DualRecorderFilter.open() mCameraId 0 setting fail");
                            }
                        }
                    });
                }
            } else if (mCamera == null || !mCameraState) {
                Log.v(TAG, "DualRecorderFilter.open() else mCameraopen: " + mCamera + " mCameraState: " + mCameraState);
            } else {
                Log.v(TAG, "Jae mCameraId 1 not setting onAutoFocus in, mCameraState: " + mCameraState);
                params = mCamera.getParameters();
                params.setRecordingHint(true);
                mCamera.setParameters(params);
                mCamera.startPreview();
            }
        } else {
            Log.v(TAG, "DualRecorderFilter.open() Camera is not opened");
        }
        this.mFirstFrame = true;
        this.mNewFrameAvailable = false;
        this.mNewCameraFrameAvailable = false;
        Log.v(TAG, "DualRecorderFilter.open() done");
    }

    public void prepare(FilterContext context) {
        mCameraState = true;
        Log.v(TAG, "Preparing DualRecorderFilter!");
        createFormats();
        this.mSurfaceTextureFrame = (GLFrame) context.getFrameManager().newBoundFrame(this.mOutputFormat, DialogCreater.DIALOG_ID_HELP_CONTINUOUS_SHOT, 0);
        this.mDualRecorderProgram = new ShaderProgram(context, mDualRecorderShader);
        this.mDualRecorderProgramwithOvalblur = new ShaderProgram(context, mDualRecorderShaderwithOvalblur);
        this.mDualRecorderProgramwithOverlay = new ShaderProgram(context, mDualRecorderShaderwithOverlay);
        this.mDualRecorderProgramwithMaskFishEye = new ShaderProgram(context, mDualRecorderShaderwithMaskFishEye);
        this.mDualRecorderProgramwithMaskSplit = new ShaderProgram(context, mDualRecorderShaderwithMaskSplit);
        this.mDualRecorderProgram_selected = this.mDualRecorderProgram;
        this.mFrameCount = 0;
        this.mProcessCount = 0;
        mSelectedPIP = 1;
        mPositionPIP_x = this.mPipPosX;
        mPositionPIP_y = this.mPipPosY;
        mPositionPIP_width = this.mPipWidth;
        mPositionPIP_height = this.mPipHeight;
        mPIPRectThick = this.mRectThick;
    }

    private void calculatePIPPosition() {
        float width_scale = ((float) this.mScreenWidth) / ((float) mPositionPIP_width);
        float height_scale = ((float) this.mScreenHeight) / ((float) mPositionPIP_height);
        float start_x = ((float) mPositionPIP_x) / ((float) this.mScreenWidth);
        float start_y = ((float) mPositionPIP_y) / ((float) this.mScreenHeight);
        float end_x = start_x + (((float) mPositionPIP_width) / ((float) this.mScreenWidth));
        float end_y = start_y + (((float) mPositionPIP_height) / ((float) this.mScreenHeight));
        this.mDualRecorderProgram_selected.setHostValue("start_x_pip", Float.valueOf(start_x));
        this.mDualRecorderProgram_selected.setHostValue("start_y_pip", Float.valueOf(start_y));
        this.mDualRecorderProgram_selected.setHostValue("end_x_pip", Float.valueOf(end_x));
        this.mDualRecorderProgram_selected.setHostValue("end_y_pip", Float.valueOf(end_y));
        this.mDualRecorderProgram_selected.setHostValue("width_scale", Float.valueOf(width_scale));
        this.mDualRecorderProgram_selected.setHostValue("height_scale", Float.valueOf(height_scale));
    }

    private boolean WaitForNewFrame() {
        if (this.mWaitForNewFrame) {
            int waitCount = 0;
            while (!this.mNewFrameAvailable) {
                if (waitCount == NEWFRAME_TIMEOUT_REPEAT) {
                    throw new RuntimeException("Timeout waiting for new frame");
                } else if (!mCameraState) {
                    return false;
                } else {
                    try {
                        wait(100);
                        waitCount++;
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Interrupted while waiting for new frame");
                    }
                }
            }
            if (this.mFirstFrame) {
                int waitCount2 = 0;
                while (!this.mNewCameraFrameAvailable) {
                    if (waitCount2 == NEWFRAME_TIMEOUT_REPEAT) {
                        throw new RuntimeException("Timeout waiting for new frame");
                    } else if (!mCameraState) {
                        return false;
                    } else {
                        try {
                            wait(100);
                            waitCount2++;
                        } catch (InterruptedException e2) {
                            Log.e(TAG, "Interrupted while waiting for new frame");
                        }
                    }
                }
                this.mNewCameraFrameAvailable = false;
                this.mFirstFrame = false;
            }
            this.mNewFrameAvailable = false;
            this.mProcessCount++;
        }
        return true;
    }

    private void DisplayFPS(FilterContext context) {
        if (this.mFrameCount % 30 != 0) {
            return;
        }
        if (this.startTime == -1) {
            context.getGLEnvironment().activate();
            GLES20.glFinish();
            this.startTime = SystemClock.elapsedRealtime();
            return;
        }
        context.getGLEnvironment().activate();
        GLES20.glFinish();
        long endTime = SystemClock.elapsedRealtime();
        Log.v(TAG, "Avg. frame duration: " + String.format("%.2f", new Object[]{Double.valueOf(((double) (endTime - this.startTime)) / 30.0d)}) + " ms. Avg. fps: " + String.format("%.2f", new Object[]{Double.valueOf(1000.0d / (((double) (endTime - this.startTime)) / 30.0d))}));
        this.startTime = endTime;
    }

    public void process(FilterContext context) {
        if (this.mProcessCount > 0) {
            mCameracheck = true;
        }
        if (mCameraState) {
            if (this.mFrameCount == this.mLearningDuration) {
                Log.v(TAG, "DualRecorderFilter.process() Learning done");
                if (this.mLearningDoneListener != null) {
                    this.mLearningDoneListener.onLearningDone(this);
                }
            }
            if (WaitForNewFrame()) {
                mFrameSurfaceTexture.updateTexImage();
                mCameraSurfaceTexture.updateTexImage();
                if (this.mFrameCount >= HAS_OVALBLUR_MASK) {
                    if (mRequestMaskUpdate) {
                        mMaskMode = mNextMaskMode;
                        mRequestMaskUpdate = false;
                    }
                    Frame[] cameraInputs = new Frame[(mMaskMode % 5)];
                    if (mMaskMode != HAS_NO_MASK && mMaskChanged) {
                        GLES20.glHint(33170, 4354);
                        mMaskFrame = (GLFrame) context.getFrameManager().newFrame(ImageFormat.create(mMaskBitmap.getWidth(), mMaskBitmap.getHeight(), HAS_OVALBLUR_MASK, HAS_OVALBLUR_MASK));
                        mMaskFrame.setBitmap(mMaskBitmap);
                        mMaskFrame.generateMipMap();
                        mMaskFrame.setTextureParameter(10241, 9729);
                        mMaskFrame.setTextureParameter(10240, 9729);
                        if (mMaskMode != HAS_OVALBLUR_MASK) {
                            mOverlayFrame = (GLFrame) context.getFrameManager().newFrame(ImageFormat.create(mOverlayBitmap.getWidth(), mOverlayBitmap.getHeight(), HAS_OVALBLUR_MASK, HAS_OVALBLUR_MASK));
                            mOverlayFrame.setBitmap(mOverlayBitmap);
                            mOverlayFrame.generateMipMap();
                            mOverlayFrame.setTextureParameter(10241, 9729);
                            mOverlayFrame.setTextureParameter(10240, 9729);
                        }
                        mMaskChanged = false;
                    }
                    if (mSelectedPIP == 1) {
                        cameraInputs[0] = this.mSurfaceTextureFrame;
                        cameraInputs[1] = this.mCameraFrame;
                    } else {
                        cameraInputs[0] = this.mCameraFrame;
                        cameraInputs[1] = this.mSurfaceTextureFrame;
                    }
                    switch (mMaskMode) {
                        case HAS_NO_MASK /*2*/:
                            this.mDualRecorderProgram_selected = this.mDualRecorderProgram;
                            calculatePIPPosition();
                            break;
                        case HAS_OVALBLUR_MASK /*3*/:
                            cameraInputs[HAS_NO_MASK] = mMaskFrame;
                            this.mDualRecorderProgram_selected = this.mDualRecorderProgramwithOvalblur;
                            calculatePIPPosition();
                            break;
                        case HAS_OVERLAY_MASK /*4*/:
                            cameraInputs[HAS_NO_MASK] = mMaskFrame;
                            cameraInputs[HAS_OVALBLUR_MASK] = mOverlayFrame;
                            this.mDualRecorderProgram_selected = this.mDualRecorderProgramwithOverlay;
                            calculatePIPPosition();
                            break;
                        case HAS_FISHEYE_MASK /*9*/:
                            cameraInputs[HAS_NO_MASK] = mMaskFrame;
                            cameraInputs[HAS_OVALBLUR_MASK] = mOverlayFrame;
                            this.mDualRecorderProgram_selected = this.mDualRecorderProgramwithMaskFishEye;
                            calculatePIPPosition();
                            break;
                        case HAS_SPLIT_MASK /*14*/:
                            cameraInputs[HAS_NO_MASK] = mMaskFrame;
                            cameraInputs[HAS_OVALBLUR_MASK] = mOverlayFrame;
                            this.mDualRecorderProgram_selected = this.mDualRecorderProgramwithMaskSplit;
                            break;
                        default:
                            ShouldNotBeHere("DualRecorderFilter.process(default) : Should not be here. **WARNING**");
                            this.mDualRecorderProgram_selected = this.mDualRecorderProgram;
                            calculatePIPPosition();
                            break;
                    }
                    Frame output = context.getFrameManager().newFrame(this.mOutputFormat);
                    if (this.mCameraId == mSelectedPIP) {
                        this.mDualRecorderProgram_selected.setHostValue("pip_mirror", Float.valueOf(RotateView.DEFAULT_TEXT_SCALE_X));
                    } else {
                        this.mDualRecorderProgram_selected.setHostValue("pip_mirror", Float.valueOf(0.0f));
                    }
                    if (mMaskMode == HAS_OVERLAY_MASK || mMaskMode == HAS_FISHEYE_MASK || mMaskMode == HAS_OVALBLUR_MASK) {
                        this.mDualRecorderProgram_selected.setHostValue("rotate", Float.valueOf(degree));
                    }
                    this.mDualRecorderProgram_selected.process(cameraInputs, output);
                    output.setTimestamp(mFrameSurfaceTexture.getTimestamp());
                    if (mRequestCapture) {
                        final Frame mPreviewFrame = context.getFrameManager().duplicateFrame(output);
                        final Bitmap mPreviewBitmap_origin = mPreviewFrame.getBitmap();
                        new Thread(new Runnable() {
                            public void run() {
                                Bitmap mPreviewBitmap = null;
                                if (DualRecorderFilter.this.mCaptureRotationDegree != 0) {
                                    mPreviewBitmap = DualRecorderFilter.rotateBitmap(mPreviewBitmap_origin, DualRecorderFilter.this.mCaptureRotationDegree);
                                }
                                ByteArrayOutputStream mPreviewStream = new ByteArrayOutputStream();
                                if (mPreviewBitmap != null) {
                                    mPreviewBitmap.compress(CompressFormat.JPEG, DualRecorderFilter.this.mQuality, mPreviewStream);
                                    if (DualRecorderFilter.this.mPreviewFrameListener != null) {
                                        DualRecorderFilter.this.mPreviewFrameListener.onPreviewFrame(mPreviewStream.toByteArray());
                                    }
                                    try {
                                        mPreviewStream.close();
                                    } catch (Exception e) {
                                        Log.e(DualRecorderFilter.TAG, "mPreviewStream.close() failed");
                                    }
                                    mPreviewBitmap.recycle();
                                }
                                mPreviewFrame.release();
                            }
                        }).start();
                        mRequestCapture = false;
                    }
                    pushOutput("video", output);
                    output.release();
                }
                if (misSetBitmapActive && !mMaskChanged) {
                    if (this.mSetBitmapDoneListener != null) {
                        this.mSetBitmapDoneListener.onSetBitmapDone(this);
                    }
                    misSetBitmapActive = false;
                }
                this.mFrameCount++;
                return;
            }
            Log.v(TAG, "DualRecorderFilter.process() Wait Error - called manual stopPreview");
            return;
        }
        this.mProcessCount++;
    }

    private static void DisplayCurrentState(String str) {
        Log.v(TAG, "DualRecorderFilter." + str + "() mRequestCapture =" + mRequestCapture);
        Log.v(TAG, "DualRecorderFilter." + str + "() mRequestMaskUpdate =" + mRequestMaskUpdate);
        Log.v(TAG, "DualRecorderFilter." + str + "() mCameraState =" + mCameraState);
        Log.v(TAG, "DualRecorderFilter." + str + "() mCameracheck =" + mCameracheck);
        Log.v(TAG, "DualRecorderFilter." + str + "() mSurfaceCheck =" + mSurfaceCheck);
        Log.v(TAG, "DualRecorderFilter." + str + "() misSetBitmapActive =" + misSetBitmapActive);
        Log.v(TAG, "DualRecorderFilter." + str + "() mMaskChanged =" + mMaskChanged);
        Log.v(TAG, "DualRecorderFilter." + str + "() manualStopPreviewCalledBeforeOpen =" + manualStopPreviewCalledBeforeOpen);
    }

    public void close(FilterContext context) {
        Log.v(TAG, "DualRecorderFilter Filter Closing !");
        mCameracheck = false;
        misSetBitmapActive = false;
        manualStopPreviewCalledBeforeOpen = false;
        mMaskMode = HAS_NO_MASK;
        mNextMaskMode = HAS_NO_MASK;
        if (mFrameSurfaceTexture != null) {
            mFrameSurfaceTexture.release();
            mFrameSurfaceTexture = null;
        }
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            mIsCameraOpened = false;
            closeCalledBeforeManualStop = true;
        }
        if (mCameraSurfaceTexture != null) {
            mCameraSurfaceTexture.release();
            mCameraSurfaceTexture = null;
        }
        if (mMaskFrame != null) {
            mMaskFrame.release();
            mMaskFrame = null;
        }
        if (mOverlayFrame != null) {
            mOverlayFrame.release();
            mOverlayFrame = null;
        }
        if (mMaskBitmap != null) {
            mMaskBitmap.recycle();
            mMaskBitmap = null;
        }
        if (mOverlayBitmap != null) {
            mOverlayBitmap.recycle();
            mOverlayBitmap = null;
        }
        mSourceListener.onSurfaceTextureSourceReady(null);
    }

    public void tearDown(FilterContext context) {
        if (this.mSurfaceTextureFrame != null) {
            this.mSurfaceTextureFrame.release();
        }
        if (this.mCameraFrame != null) {
            this.mCameraFrame.release();
        }
    }

    public void fieldPortValueUpdated(String name, FilterContext context) {
        if (mCamera != null && name.equals("framerate")) {
            getCameraParameters();
            if (this.mCameraId == 1) {
                this.mCameraParameters.setPreviewFpsRange(MultimediaProperties.VALUE_VIDEO_FRAMERATE_NORMAL_RANGE_MAX, MultimediaProperties.VALUE_VIDEO_FRAMERATE_NORMAL_RANGE_MAX);
            }
            mCamera.setParameters(this.mCameraParameters);
        }
        if (name.equals("sTextureWidth") || name.equals("sTextureHeight")) {
            this.mOutputFormat.setDimensions(this.mSurfaceTextureWidth, this.mSurfaceTextureHeight);
        }
    }

    public synchronized Parameters getCameraParameters() {
        if (this.mCameraParameters == null) {
            this.mCameraParameters = mCamera.getParameters();
        }
        int[] closestSize = findClosestSize(this.mCameraWidth, this.mCameraHeight, this.mCameraParameters);
        this.mCameraWidth = closestSize[0];
        this.mCameraHeight = closestSize[1];
        Log.v(TAG, "DualRecorderFilter.getCameraParameters() : Try to setPreviewSize().");
        this.mCameraParameters.setPreviewSize(1280, 720);
        this.mCameraParameters.setPictureSize(1280, 720);
        int[] closestRange = findClosestFpsRange(this.mFps, this.mCameraParameters);
        Log.v(TAG, "DualRecorderFilter.getCameraParameters() : Try to setPreviewFpsRange().");
        this.mCameraParameters.setPreviewFpsRange(MultimediaProperties.VALUE_VIDEO_FRAMERATE_NORMAL_RANGE_MAX, MultimediaProperties.VALUE_VIDEO_FRAMERATE_NORMAL_RANGE_MAX);
        if (this.mCameraId == 0) {
            Log.v(TAG, "DualRecorderFilter.getCameraParameters() : Try to setFocusMode().");
            this.mCameraParameters.setFocusMode(this.mFocusmode);
        }
        Log.v(TAG, "DualRecorderFilter.getCameraParameters() : Try to setDisplayOrientation().");
        if (mCamera != null) {
            mCamera.setDisplayOrientation(this.mCameraOrientation);
        }
        Log.v(TAG, "DualRecorderFilter.getCameraParameters() : DONE.");
        return this.mCameraParameters;
    }

    public synchronized void setCameraParameters(Parameters params) {
        params.setPreviewSize(this.mCameraWidth, this.mCameraHeight);
        this.mCameraParameters = params;
        if (isOpen()) {
            mCamera.setParameters(this.mCameraParameters);
        }
    }

    private int[] findClosestSize(int width, int height, Parameters parameters) {
        List<Size> previewSizes = parameters.getSupportedPreviewSizes();
        int closestWidth = -1;
        int closestHeight = -1;
        if (previewSizes != null) {
            int smallestWidth = ((Size) previewSizes.get(0)).width;
            int smallestHeight = ((Size) previewSizes.get(0)).height;
            for (Size size : previewSizes) {
                if (size.width <= width && size.height <= height && size.width >= closestWidth && size.height >= closestHeight) {
                    closestWidth = size.width;
                    closestHeight = size.height;
                }
                if (size.width < smallestWidth && size.height < smallestHeight) {
                    smallestWidth = size.width;
                    smallestHeight = size.height;
                }
            }
            if (closestWidth == -1) {
                closestWidth = smallestWidth;
                closestHeight = smallestHeight;
            }
        } else {
            closestWidth = 1280;
            closestHeight = 720;
        }
        Log.v(TAG, "Requested resolution: (" + width + ", " + height + "). Closest match: (" + closestWidth + ", " + closestHeight + ").");
        int[] closestSize = new int[HAS_NO_MASK];
        closestSize[0] = closestWidth;
        closestSize[1] = closestHeight;
        return closestSize;
    }

    private int[] findClosestFpsRange(int fps, Parameters params) {
        int[] closestRange;
        List<int[]> supportedFpsRanges = params.getSupportedPreviewFpsRange();
        if (supportedFpsRanges != null) {
            closestRange = (int[]) supportedFpsRanges.get(0);
            for (int[] range : supportedFpsRanges) {
                if (range[0] < fps * PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME && range[1] > fps * PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME && range[0] > closestRange[0] && range[1] < closestRange[1]) {
                    closestRange = range;
                }
            }
        } else {
            closestRange = new int[]{PanoramaApplication.SENSOR_CORRECTION_TIME_EVERYTIME, MultimediaProperties.VALUE_VIDEO_FRAMERATE_NORMAL_RANGE_MAX};
        }
        Log.v(TAG, "Requested fps: " + fps + ".Closest frame rate range: [" + (((double) closestRange[0]) / 1000.0d) + "," + (((double) closestRange[1]) / 1000.0d) + "]");
        return closestRange;
    }

    public static void selectPIP(int cameraId) {
        mSelectedPIP = cameraId;
    }

    public static void setPIPPosition(int x, int y, int width, int height) {
        mPositionPIP_x = x;
        mPositionPIP_y = y;
        mPositionPIP_width = width;
        mPositionPIP_height = height;
    }

    public static void setPIPRotate(int incomedegree) {
        if (incomedegree == 0) {
            degree = 0.0f;
        } else if (incomedegree == 90) {
            degree = RotateView.DEFAULT_TEXT_SCALE_X;
        } else if (incomedegree == MediaProviderUtils.ROTATION_180) {
            degree = CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK;
        } else if (incomedegree == Tag.IMAGE_DESCRIPTION) {
            degree = 3.0f;
        } else {
            degree = 0.0f;
        }
    }

    public static void setPIPMask(Bitmap mbitmap) {
        if (!misSetBitmapActive) {
            misSetBitmapActive = true;
            if (mbitmap == null) {
                mNextMaskMode = HAS_NO_MASK;
                if (mMaskBitmap != null) {
                    mMaskBitmap.recycle();
                    mMaskBitmap = null;
                }
            } else {
                mNextMaskMode = HAS_OVALBLUR_MASK;
                mMaskBitmap = mbitmap;
                mMaskChanged = true;
            }
            if (mOverlayBitmap != null) {
                mOverlayBitmap.recycle();
                mOverlayBitmap = null;
            }
            mRequestMaskUpdate = true;
        }
    }

    private static boolean isMaskBitmapNull(Bitmap mbitmap) {
        if (mbitmap != null) {
            return false;
        }
        ShouldNotBeHere("DualRecorderFilter.setPIPMask() : mbitmap == null.");
        return true;
    }

    private static void ShouldNotBeHere(String string) {
        Log.e(TAG, string);
        mNextMaskMode = HAS_NO_MASK;
        if (mMaskBitmap != null) {
            mMaskBitmap.recycle();
            mMaskBitmap = null;
        }
        if (mOverlayBitmap != null) {
            mOverlayBitmap.recycle();
            mOverlayBitmap = null;
        }
        mMaskChanged = true;
    }

    public static void setPIPMask(Bitmap mbitmap, Bitmap mbitmap_overlay, int type) {
        Log.v(TAG, "DualRecorderFilter.setPIPMask() misSetBitmapActive :" + misSetBitmapActive);
        if (!misSetBitmapActive) {
            misSetBitmapActive = true;
            switch (type) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    if (!isMaskBitmapNull(mbitmap)) {
                        if (mbitmap_overlay != null) {
                            Log.v(TAG, "DualRecorderFilter.setPIPMask() : mbitmap_overlay width = " + mbitmap_overlay.getWidth() + " height = " + mbitmap_overlay.getHeight());
                            mNextMaskMode = HAS_OVERLAY_MASK;
                            mMaskBitmap = mbitmap;
                            mOverlayBitmap = mbitmap_overlay;
                            mMaskChanged = true;
                            break;
                        }
                        mNextMaskMode = HAS_OVALBLUR_MASK;
                        mMaskBitmap = mbitmap;
                        if (mOverlayBitmap != null) {
                            mOverlayBitmap.recycle();
                            mOverlayBitmap = null;
                        }
                        mMaskChanged = true;
                        break;
                    }
                    break;
                case LGKeyRec.EVENT_NO_MATCH /*1*/:
                    if (!isMaskBitmapNull(mbitmap)) {
                        if (mbitmap_overlay != null) {
                            mNextMaskMode = HAS_FISHEYE_MASK;
                            mMaskBitmap = mbitmap;
                            mOverlayBitmap = mbitmap_overlay;
                            mMaskChanged = true;
                            break;
                        }
                        ShouldNotBeHere("DualRecorderFilter.setPIPMask(1) : Should not be here. **WARNING**");
                        break;
                    }
                    break;
                case HAS_NO_MASK /*2*/:
                    if (!isMaskBitmapNull(mbitmap)) {
                        if (mbitmap_overlay != null) {
                            mNextMaskMode = HAS_SPLIT_MASK;
                            mMaskBitmap = mbitmap;
                            mOverlayBitmap = mbitmap_overlay;
                            mMaskChanged = true;
                            break;
                        }
                        ShouldNotBeHere("DualRecorderFilter.setPIPMask(2) : Should not be here. **WARNING**");
                        break;
                    }
                    break;
                default:
                    ShouldNotBeHere("DualRecorderFilter.setPIPMask(default) : Should not be here. **WARNING**");
                    break;
            }
            mRequestMaskUpdate = true;
        }
    }

    public static void setPIPRectThick(int mThick) {
        mPIPRectThick = mThick;
    }

    public static Bitmap rotateBitmap(Bitmap b, int degrees) {
        if (degrees == 0 || b == null) {
            return b;
        }
        Matrix m = new Matrix();
        m.setRotate((float) degrees, ((float) b.getWidth()) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK, ((float) b.getHeight()) / CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK);
        try {
            Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
            if (b == b2) {
                return b;
            }
            b.recycle();
            return b2;
        } catch (OutOfMemoryError ex) {
            Log.e(TAG, "OutOfMemoryError : " + ex);
            return b;
        }
    }

    public static void manualcheckstop() {
        mCameraState = false;
        if (!mIsCameraOpened) {
            manualStopPreviewCalledBeforeOpen = true;
        }
        Log.v(TAG, "DualRecorderFilter.manualcheckstop() mCameraState: " + mCameraState);
    }

    public static void manualStopPreview() {
        DisplayCurrentState("manualStopPreview");
        manualcheckstop();
        if (mCameracheck) {
            Log.v(TAG, "DualRecorderFilter.manualStopPreview()  mCameracheck: " + mCameracheck);
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
                mIsCameraOpened = false;
                closeCalledBeforeManualStop = false;
                return;
            }
            return;
        }
        Log.v(TAG, "DualRecorderFilter.manualStopPreview() mCameracheck: " + mCameracheck);
        if (!manualStopPreviewCalledBeforeOpen) {
        }
    }

    public static boolean isCameraOpened() {
        return mIsCameraOpened;
    }

    public static void takePreviewFrame() {
        mRequestCapture = true;
    }
}
