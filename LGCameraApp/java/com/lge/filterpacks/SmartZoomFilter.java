package com.lge.filterpacks;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GLFrame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;
import com.lge.camera.components.RotateView;
import com.lge.camera.properties.CameraConstants;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.voiceshutter.library.LGKeyRec;
import java.util.Arrays;

public class SmartZoomFilter extends Filter {
    private static final int DEFAULT_LEARNING_DURATION = 5;
    private static final int DEFAULT_SCREEN_HEIGHT = 1080;
    private static final int DEFAULT_SCREEN_WIDTH = 1920;
    private static final int HAS_FISHEYE_MASK = 9;
    private static final int HAS_NO_MASK = 2;
    private static final int HAS_OVERLAY_MASK = 4;
    private static final int HAS_SIMPLE_MASK = 3;
    private static final String TAG = "SmartZoomFilter";
    private static float degree;
    private static String[] mInputNames;
    private static Bitmap mMaskBitmap;
    private static boolean mMaskChanged;
    private static int mMaskMode;
    private static int mNextMaskMode;
    private static String[] mOutputNames;
    private static Bitmap mOverlayBitmap;
    private static int mPIPRectThick;
    private static int mPositionPIP_height;
    private static int mPositionPIP_width;
    private static int mPositionPIP_x;
    private static int mPositionPIP_y;
    private static boolean mRequestMaskUpdate;
    private static String mSmartZoomShader;
    private static String mSmartZoomShaderwithMask;
    private static String mSmartZoomShaderwithMaskFishEye;
    private static String mSmartZoomShaderwithOverlay;
    private static boolean misSetBitmapActive;
    private int mFrameCount;
    @GenerateFieldPort(hasDefault = true, name = "learningDoneListener")
    private LearningDoneListener mLearningDoneListener;
    @GenerateFieldPort(hasDefault = true, name = "learningDuration")
    private int mLearningDuration;
    private final boolean mLogVerbose;
    private GLFrame mMaskFrame;
    private FrameFormat mOutputFormat;
    private GLFrame mOverlayFrame;
    @GenerateFieldPort(hasDefault = true, name = "pHeight")
    private int mPipHeight;
    @GenerateFieldPort(hasDefault = true, name = "pPosX")
    private int mPipPosX;
    @GenerateFieldPort(hasDefault = true, name = "pPosY")
    private int mPipPosY;
    @GenerateFieldPort(hasDefault = true, name = "pWidth")
    private int mPipWidth;
    @GenerateFieldPort(hasDefault = true, name = "rThick")
    private int mRectThick;
    @GenerateFieldPort(hasDefault = true, name = "screenHeight")
    private int mScreenHeight;
    @GenerateFieldPort(hasDefault = true, name = "screenWidth")
    private int mScreenWidth;
    @GenerateFieldPort(hasDefault = true, name = "setBitmapDoneListener")
    private SetBitmapDoneListener mSetBitmapDoneListener;
    private ShaderProgram mSmartZoomProgram;
    private ShaderProgram mSmartZoomProgram_selected;
    private ShaderProgram mSmartZoomProgramwithMask;
    private ShaderProgram mSmartZoomProgramwithMaskFishEye;
    private ShaderProgram mSmartZoomProgramwithOverlay;
    private long startTime;

    public interface LearningDoneListener {
        void onLearningDone(SmartZoomFilter smartZoomFilter);
    }

    public interface SetBitmapDoneListener {
        void onSetBitmapDone(SmartZoomFilter smartZoomFilter);
    }

    static {
        String[] strArr = new String[HAS_NO_MASK];
        strArr[0] = "video";
        strArr[1] = "background";
        mInputNames = strArr;
        mOutputNames = new String[]{"video"};
        mRequestMaskUpdate = false;
        misSetBitmapActive = false;
        degree = 0.0f;
        mMaskMode = HAS_NO_MASK;
        mNextMaskMode = HAS_NO_MASK;
        mMaskBitmap = null;
        mOverlayBitmap = null;
        mMaskChanged = false;
        mSmartZoomShader = "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform float start_x_pip;\nuniform float start_y_pip;\nuniform float end_x_pip;\nuniform float end_y_pip;\nuniform float width_scale;\nuniform float height_scale;\nuniform float rect_x;\nuniform float rect_y;\nvarying vec2 v_texcoord;\nvoid main() {\n    vec4 main_view;\n    vec4 pip_view;\n    vec4 rect_view;\n    vec2 pip_texcoord;\n    pip_texcoord.x = width_scale * (v_texcoord.x - start_x_pip);\n    pip_texcoord.y = height_scale * (v_texcoord.y - start_y_pip);\n    main_view = texture2D(tex_sampler_0, v_texcoord);\n    pip_view = texture2D(tex_sampler_1, pip_texcoord);\n    vec2 t_coord = clamp(v_texcoord, vec2(start_x_pip, start_y_pip), vec2(end_x_pip, end_y_pip));\n    vec2 rect_coord = clamp(pip_texcoord, vec2(rect_x, rect_y), vec2(1.0-rect_x, 1.0-rect_y));\n    rect_view = (rect_coord != pip_texcoord) ? vec4(1.0,1.0,1.0,1.0) : pip_view;\n    gl_FragColor = (t_coord != v_texcoord) ? main_view : rect_view;\n}\n";
        mSmartZoomShaderwithMask = "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform float start_x_pip;\nuniform float start_y_pip;\nuniform float end_x_pip;\nuniform float end_y_pip;\nuniform float width_scale;\nuniform float height_scale;\nuniform float rotate;\nvarying vec2 v_texcoord;\nvoid main() {\n    const float mask_offset = 0.6;\n    const float pos_down = 0.39;\n    const float window_ratio = 16.0/9.0;\n    vec4 main_view;\n    vec4 pip_view;\n    vec4 rect_view;\n    vec2 pip_texcoord;\n    pip_texcoord.x = width_scale * (v_texcoord.x - start_x_pip);\n    pip_texcoord.y = height_scale * (v_texcoord.y - start_y_pip);\n    main_view = texture2D(tex_sampler_0, v_texcoord);\n    pip_view = texture2D(tex_sampler_1, pip_texcoord);\n    highp vec2 effect_texcoord;\n    highp vec2 effect_texcoord_temp;\n    highp vec4 effect_mask; \n    if ( rotate == 0.0 ) {\n        effect_texcoord_temp = vec2(pip_texcoord.y, 1.0 - pip_texcoord.x); \n \t   effect_texcoord.x = ( window_ratio ) * effect_texcoord_temp.x - pos_down;\n        effect_texcoord.y = effect_texcoord_temp.y;\n        effect_mask = texture2D(tex_sampler_2, effect_texcoord); \n    } else if ( rotate == 1.0 ) {\n        effect_texcoord_temp = vec2(pip_texcoord.x, pip_texcoord.y); \n        effect_texcoord.x = effect_texcoord_temp.x;\n        effect_texcoord.y = ( window_ratio ) * effect_texcoord_temp.y - pos_down;\n        if ( 0.0 <= effect_texcoord.y && effect_texcoord.y <= 1.0 ){\n            effect_mask = texture2D(tex_sampler_2, effect_texcoord); \n        } else {\n            effect_mask = vec4(1.0,0.0,0.0,0.0); \n        }\n    } else if ( rotate == 2.0 ) {\n        effect_texcoord_temp = vec2(1.0 - pip_texcoord.y, pip_texcoord.x); \n \t   effect_texcoord.x = ( window_ratio ) * effect_texcoord_temp.x - pos_down;\n        effect_texcoord.y = effect_texcoord_temp.y;\n        effect_mask = texture2D(tex_sampler_2, effect_texcoord); \n    } else {\n        effect_texcoord_temp = vec2(1.0 - pip_texcoord.x, 1.0 - pip_texcoord.y); \n        effect_texcoord.x = effect_texcoord_temp.x;\n \t   effect_texcoord.y = ( window_ratio ) * effect_texcoord_temp.y - pos_down;\n        if ( 0.0 <= effect_texcoord.y && effect_texcoord.y <= 1.0 ){\n            effect_mask = texture2D(tex_sampler_2, effect_texcoord); \n        } else {\n            effect_mask = vec4(1.0,0.0,0.0,0.0); \n        }\n    }\n    highp vec2 t_coord = clamp(v_texcoord, vec2(start_x_pip, start_y_pip), vec2(end_x_pip, end_y_pip));\n    if (t_coord == v_texcoord && effect_mask.g > mask_offset) {\n        pip_view = vec4( 1.0, 1.0, 1.0, 1.0 );\n    } else {\n        pip_view = effect_mask.b * pip_view + (1.0 - effect_mask.b) * main_view;\n    }\n    gl_FragColor = ( t_coord != v_texcoord ) ? main_view : pip_view; \n }\n";
        mSmartZoomShaderwithOverlay = "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform sampler2D tex_sampler_3;\nuniform float start_x_pip;\nuniform float start_y_pip;\nuniform float end_x_pip;\nuniform float end_y_pip;\nuniform float width_scale;\nuniform float height_scale;\nuniform float rotate;\nvarying vec2 v_texcoord;\nvoid main() {\n    const float pos_down = 0.39;\n    const float window_ratio = 16.0/9.0;\n    highp vec4 main_view;\n    highp vec4 pip_view;\n    highp vec4 rect_view;\n    highp vec2 pip_texcoord;\n    pip_texcoord.x = width_scale * (v_texcoord.x - start_x_pip);\n    pip_texcoord.y = height_scale * (v_texcoord.y - start_y_pip);\n    main_view = texture2D(tex_sampler_0, v_texcoord);\n    pip_view = texture2D(tex_sampler_1, pip_texcoord);\n    highp vec2 effect_texcoord;\n    highp vec2 effect_texcoord_temp;\n    highp vec4 effect_mask; \n    highp vec4 effect_overlay; \n    if ( rotate == 0.0 ) {\n        effect_texcoord_temp = vec2(pip_texcoord.y, 1.0 - pip_texcoord.x); \n \t   effect_texcoord.x = ( window_ratio ) * effect_texcoord_temp.x - pos_down;\n        effect_texcoord.y = effect_texcoord_temp.y;\n        effect_mask = texture2D(tex_sampler_2, effect_texcoord); \n        effect_overlay = texture2D(tex_sampler_3, effect_texcoord); \n    } else if ( rotate == 1.0 ) {\n        effect_texcoord_temp = vec2(pip_texcoord.x, pip_texcoord.y); \n        effect_texcoord.x = effect_texcoord_temp.x;\n        effect_texcoord.y = ( window_ratio ) * effect_texcoord_temp.y - pos_down;\n        if ( 0.0 <= effect_texcoord.y && effect_texcoord.y <= 1.0 ){\n            effect_mask = texture2D(tex_sampler_2, effect_texcoord); \n            effect_overlay = texture2D(tex_sampler_3, effect_texcoord); \n        } else {\n            effect_mask = vec4(1.0,0.0,0.0,0.0); \n            effect_overlay = vec4(0.0,0.0,0.0,0.0); \n        }\n    } else if ( rotate == 2.0 ) {\n        effect_texcoord_temp = vec2(1.0 - pip_texcoord.y, pip_texcoord.x); \n        effect_texcoord.x = ( window_ratio ) * effect_texcoord_temp.x - pos_down;\n        effect_texcoord.y = effect_texcoord_temp.y;\n        effect_mask = texture2D(tex_sampler_2, effect_texcoord); \n        effect_overlay = texture2D(tex_sampler_3, effect_texcoord); \n    } else {\n        effect_texcoord_temp = vec2(1.0 - pip_texcoord.x, 1.0 - pip_texcoord.y); \n        effect_texcoord.x = effect_texcoord_temp.x;\n        effect_texcoord.y = ( window_ratio ) * effect_texcoord_temp.y - pos_down;\n        if ( 0.0 <= effect_texcoord.y && effect_texcoord.y <= 1.0 ){\n            effect_mask = texture2D(tex_sampler_2, effect_texcoord); \n            effect_overlay = texture2D(tex_sampler_3, effect_texcoord); \n        } else {\n            effect_mask = vec4(1.0,0.0,0.0,0.0); \n            effect_overlay = vec4(0.0,0.0,0.0,0.0); \n        }\n    }\n    highp vec2 t_coord = clamp(v_texcoord, vec2(start_x_pip, start_y_pip), vec2(end_x_pip, end_y_pip));\n    if (t_coord == v_texcoord && effect_mask.r >= effect_mask.b ) {\n        rect_view = main_view;\n    } else {\n        rect_view = pip_view;\n    }\n    if ( t_coord == v_texcoord && effect_overlay.a == 0.0) {\n        rect_view = rect_view;\n    } else {\n        rect_view = smoothstep(0.05,0.95,effect_overlay.a) * effect_overlay + ( 1.0 - smoothstep(0.05,0.95,effect_overlay.a) ) * rect_view;\n    }\n    gl_FragColor = ( t_coord != v_texcoord ) ? main_view : rect_view; \n }\n";
        mSmartZoomShaderwithMaskFishEye = "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform sampler2D tex_sampler_3;\nuniform float start_x_pip;\nuniform float start_y_pip;\nuniform float end_x_pip;\nuniform float end_y_pip;\nuniform float width_scale;\nuniform float height_scale;\nuniform float rect_x;\nuniform float rect_y;\nuniform float rotate;\nvarying vec2 v_texcoord;\nvoid main() {\n    const float pos_down = 0.39;\n    const float window_ratio = 16.0/9.0;\n    const float effect_radius = 1.5;\n    const float m_pi_2 = 1.570963;\n    const float alpha = 3.750000;\n    const float radius2 = 0.661250;\n    const float factor = 1.610678;\n    vec4 main_view;\n    vec4 fish_view;\n    vec4 pip_view;\n    vec4 rect_view;\n    vec2 pip_texcoord;\n    vec2 fish_texcoord;\n    pip_texcoord.x = width_scale * ( v_texcoord.x - start_x_pip );\n    pip_texcoord.y = height_scale * ( v_texcoord.y - start_y_pip );\n    main_view = texture2D( tex_sampler_0 , v_texcoord);\n    fish_texcoord = pip_texcoord;\n    vec2 coord = fish_texcoord - vec2( 0.5 , 0.5 );\n    float dist = length( coord * effect_radius );\n    float radian = m_pi_2 - atan( alpha * sqrt( radius2 - dist * dist ), dist );\n    float scalar = radian * factor / dist;\n    vec2 new_coord = coord * scalar + vec2( 0.5 , 0.5 );\n    fish_view = texture2D( tex_sampler_1, vec2( new_coord.x , new_coord.y ));\n    highp vec2 effect_texcoord;\n    highp vec2 effect_texcoord_temp;\n    highp vec4 effect_mask; \n    highp vec4 effect_overlay; \n    if ( rotate == 0.0 ) {\n        effect_texcoord_temp = vec2(pip_texcoord.y, 1.0 - pip_texcoord.x); \n        effect_texcoord.x = ( window_ratio ) * effect_texcoord_temp.x - pos_down;\n        effect_texcoord.y = effect_texcoord_temp.y;\n        effect_mask = texture2D(tex_sampler_2, effect_texcoord); \n        effect_overlay = texture2D(tex_sampler_3, effect_texcoord); \n    } else if ( rotate == 1.0 ) {\n        effect_texcoord_temp = vec2(pip_texcoord.x, pip_texcoord.y); \n        effect_texcoord.x = effect_texcoord_temp.x;\n        effect_texcoord.y = ( window_ratio ) * effect_texcoord_temp.y - pos_down;\n        if ( 0.0 <= effect_texcoord.y && effect_texcoord.y <= 1.0 ){\n            effect_mask = texture2D(tex_sampler_2, effect_texcoord); \n            effect_overlay = texture2D(tex_sampler_3, effect_texcoord); \n        } else {\n            effect_mask = vec4(1.0,0.0,0.0,0.0); \n            effect_overlay = vec4(0.0,0.0,0.0,0.0); \n        }\n    } else if ( rotate == 2.0 ) {\n        effect_texcoord_temp = vec2(1.0 - pip_texcoord.y, pip_texcoord.x); \n        effect_texcoord.x = ( window_ratio ) * effect_texcoord_temp.x - pos_down;\n        effect_texcoord.y = effect_texcoord_temp.y;\n        effect_mask = texture2D(tex_sampler_2, effect_texcoord); \n        effect_overlay = texture2D(tex_sampler_3, effect_texcoord); \n    } else {\n        effect_texcoord_temp = vec2(1.0 - pip_texcoord.x, 1.0 - pip_texcoord.y); \n        effect_texcoord.x = effect_texcoord_temp.x;\n        effect_texcoord.y = ( window_ratio ) * effect_texcoord_temp.y - pos_down;\n        if ( 0.0 <= effect_texcoord.y && effect_texcoord.y <= 1.0 ){\n            effect_mask = texture2D(tex_sampler_2, effect_texcoord); \n            effect_overlay = texture2D(tex_sampler_3, effect_texcoord); \n        } else {\n            effect_mask = vec4(1.0,0.0,0.0,0.0); \n            effect_overlay = vec4(0.0,0.0,0.0,0.0); \n        }\n    }\n    vec2 t_coord = clamp( v_texcoord, vec2( start_x_pip , start_y_pip ), vec2( end_x_pip , end_y_pip ));\n    vec2 rect_coord = clamp( fish_texcoord , vec2( rect_x, rect_y ), vec2( 1.0 - rect_x , 1.0 - rect_y ));\n    if ( t_coord == v_texcoord && effect_mask.r > effect_mask.b ) {\n        rect_view = effect_overlay.a * effect_overlay + ( 1.0 - effect_overlay.a ) * main_view;\n    } else {\n        rect_view = effect_overlay.a * effect_overlay + ( 1.0 - effect_overlay.a ) * fish_view;\n    }\n    gl_FragColor = ( t_coord != v_texcoord ) ? main_view : rect_view; \n }\n";
    }

    public SmartZoomFilter(String name) {
        super(name);
        this.mLearningDuration = DEFAULT_LEARNING_DURATION;
        this.mLearningDoneListener = null;
        this.mSetBitmapDoneListener = null;
        this.mScreenWidth = DEFAULT_SCREEN_WIDTH;
        this.mScreenHeight = DEFAULT_SCREEN_HEIGHT;
        this.mPipPosX = 0;
        this.mPipPosY = 0;
        this.mPipWidth = LGT_Limit.PREVIEW_SIZE_HEIGHT;
        this.mPipHeight = Tag.IMAGE_DESCRIPTION;
        this.mRectThick = 0;
        this.mLogVerbose = false;
        this.startTime = -1;
        this.mMaskFrame = null;
        this.mOverlayFrame = null;
    }

    public void setupPorts() {
        Log.v(TAG, "[filter framework] setupPorts()");
        FrameFormat imageFormat = ImageFormat.create(HAS_SIMPLE_MASK, 0);
        for (String inputName : mInputNames) {
            addMaskedInputPort(inputName, imageFormat);
        }
        for (String outputName : mOutputNames) {
            addOutputBasedOnInput(outputName, "video");
        }
    }

    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        MutableFrameFormat format = inputFormat.mutableCopy();
        if (!Arrays.asList(mOutputNames).contains(portName)) {
            format.setDimensions(0, 0);
        }
        return format;
    }

    public void prepare(FilterContext context) {
        Log.v(TAG, "[filter framework] prepare()");
        this.mOutputFormat = ImageFormat.create(this.mScreenWidth, this.mScreenHeight, HAS_SIMPLE_MASK, HAS_SIMPLE_MASK);
        this.mSmartZoomProgram = new ShaderProgram(context, mSmartZoomShader);
        this.mSmartZoomProgramwithMask = new ShaderProgram(context, mSmartZoomShaderwithMask);
        this.mSmartZoomProgramwithOverlay = new ShaderProgram(context, mSmartZoomShaderwithOverlay);
        this.mSmartZoomProgramwithMaskFishEye = new ShaderProgram(context, mSmartZoomShaderwithMaskFishEye);
        this.mSmartZoomProgram_selected = this.mSmartZoomProgram;
        this.mFrameCount = 0;
        mPositionPIP_x = this.mPipPosX;
        mPositionPIP_y = this.mPipPosY;
        mPositionPIP_width = this.mPipWidth;
        mPositionPIP_height = this.mPipHeight;
        mPIPRectThick = this.mRectThick;
    }

    public void close(FilterContext context) {
        Log.v(TAG, "[filter framework] close()");
        mMaskMode = HAS_NO_MASK;
        mNextMaskMode = HAS_NO_MASK;
        if (this.mMaskFrame != null) {
            this.mMaskFrame.release();
            this.mMaskFrame = null;
        }
        if (this.mOverlayFrame != null) {
            this.mOverlayFrame.release();
            this.mOverlayFrame = null;
        }
        if (mMaskBitmap != null) {
            mMaskBitmap.recycle();
            mMaskBitmap = null;
        }
        if (mOverlayBitmap != null) {
            mOverlayBitmap.recycle();
            mOverlayBitmap = null;
        }
    }

    public void process(FilterContext context) {
        Frame video = pullInput("video");
        Frame background = pullInput("background");
        learning_done();
        if (mRequestMaskUpdate) {
            mMaskMode = mNextMaskMode;
            mRequestMaskUpdate = false;
        }
        Frame[] cameraInputs = new Frame[(mMaskMode % DEFAULT_LEARNING_DURATION)];
        updateBitmapMask(context);
        insertInputs(video, background, cameraInputs);
        calculatePIPPosition();
        Frame output = context.getFrameManager().newFrame(this.mOutputFormat);
        this.mSmartZoomProgram_selected.process(cameraInputs, output);
        pushOutput("video", output);
        output.release();
        SetBitmapDone();
        this.mFrameCount++;
        log_framerate(context);
    }

    private void SetBitmapDone() {
        if (misSetBitmapActive && !mMaskChanged && this.mSetBitmapDoneListener != null) {
            this.mSetBitmapDoneListener.onSetBitmapDone(this);
            misSetBitmapActive = false;
        }
    }

    private void updateBitmapMask(FilterContext context) {
        if (mMaskMode != HAS_NO_MASK && mMaskChanged) {
            GLES20.glHint(33170, 4354);
            this.mMaskFrame = (GLFrame) context.getFrameManager().newFrame(ImageFormat.create(mMaskBitmap.getWidth(), mMaskBitmap.getHeight(), HAS_SIMPLE_MASK, HAS_SIMPLE_MASK));
            this.mMaskFrame.setBitmap(mMaskBitmap);
            this.mMaskFrame.generateMipMap();
            this.mMaskFrame.setTextureParameter(10241, 9729);
            this.mMaskFrame.setTextureParameter(10240, 9729);
            if (mMaskMode != HAS_SIMPLE_MASK) {
                this.mOverlayFrame = (GLFrame) context.getFrameManager().newFrame(ImageFormat.create(mOverlayBitmap.getWidth(), mOverlayBitmap.getHeight(), HAS_SIMPLE_MASK, HAS_SIMPLE_MASK));
                this.mOverlayFrame.setBitmap(mOverlayBitmap);
                this.mOverlayFrame.generateMipMap();
                this.mOverlayFrame.setTextureParameter(10241, 9729);
                this.mOverlayFrame.setTextureParameter(10240, 9729);
            }
            mMaskChanged = false;
        }
    }

    private void insertInputs(Frame video, Frame background, Frame[] cameraInputs) {
        cameraInputs[0] = video;
        cameraInputs[1] = background;
        switch (mMaskMode) {
            case HAS_NO_MASK /*2*/:
                this.mSmartZoomProgram_selected = this.mSmartZoomProgram;
                this.mSmartZoomProgram_selected.setHostValue("rect_x", Float.valueOf(((float) mPIPRectThick) / ((float) mPositionPIP_width)));
                this.mSmartZoomProgram_selected.setHostValue("rect_y", Float.valueOf(((float) mPIPRectThick) / ((float) mPositionPIP_height)));
            case HAS_SIMPLE_MASK /*3*/:
                cameraInputs[HAS_NO_MASK] = this.mMaskFrame;
                this.mSmartZoomProgram_selected = this.mSmartZoomProgramwithMask;
                this.mSmartZoomProgram_selected.setHostValue("rotate", Float.valueOf(degree));
            case HAS_OVERLAY_MASK /*4*/:
                cameraInputs[HAS_NO_MASK] = this.mMaskFrame;
                cameraInputs[HAS_SIMPLE_MASK] = this.mOverlayFrame;
                this.mSmartZoomProgram_selected = this.mSmartZoomProgramwithOverlay;
                this.mSmartZoomProgram_selected.setHostValue("rotate", Float.valueOf(degree));
            case HAS_FISHEYE_MASK /*9*/:
                cameraInputs[HAS_NO_MASK] = this.mMaskFrame;
                cameraInputs[HAS_SIMPLE_MASK] = this.mOverlayFrame;
                this.mSmartZoomProgram_selected = this.mSmartZoomProgramwithMaskFishEye;
                this.mSmartZoomProgram_selected.setHostValue("rect_x", Float.valueOf(((float) mPIPRectThick) / ((float) mPositionPIP_width)));
                this.mSmartZoomProgram_selected.setHostValue("rect_y", Float.valueOf(((float) mPIPRectThick) / ((float) mPositionPIP_height)));
                this.mSmartZoomProgram_selected.setHostValue("rotate", Float.valueOf(degree));
            default:
                ShouldNotBeHere("SmartZoomFilter.process(default) : Should not be here. **WARNING**");
                this.mSmartZoomProgram_selected = this.mSmartZoomProgram;
                this.mSmartZoomProgram_selected.setHostValue("rect_x", Float.valueOf(((float) mPIPRectThick) / ((float) mPositionPIP_width)));
                this.mSmartZoomProgram_selected.setHostValue("rect_y", Float.valueOf(((float) mPIPRectThick) / ((float) mPositionPIP_height)));
        }
    }

    private void calculatePIPPosition() {
        float width_scale = ((float) this.mScreenWidth) / ((float) mPositionPIP_width);
        float height_scale = ((float) this.mScreenHeight) / ((float) mPositionPIP_height);
        float start_x = ((float) mPositionPIP_x) / ((float) this.mScreenWidth);
        float start_y = ((float) mPositionPIP_y) / ((float) this.mScreenHeight);
        float end_x = start_x + (((float) mPositionPIP_width) / ((float) this.mScreenWidth));
        float end_y = start_y + (((float) mPositionPIP_height) / ((float) this.mScreenHeight));
        this.mSmartZoomProgram_selected.setHostValue("start_x_pip", Float.valueOf(start_x));
        this.mSmartZoomProgram_selected.setHostValue("start_y_pip", Float.valueOf(start_y));
        this.mSmartZoomProgram_selected.setHostValue("end_x_pip", Float.valueOf(end_x));
        this.mSmartZoomProgram_selected.setHostValue("end_y_pip", Float.valueOf(end_y));
        this.mSmartZoomProgram_selected.setHostValue("width_scale", Float.valueOf(width_scale));
        this.mSmartZoomProgram_selected.setHostValue("height_scale", Float.valueOf(height_scale));
    }

    public static void setPIPPosition(int x, int y, int width, int height) {
        mPositionPIP_x = x;
        mPositionPIP_y = y;
        mPositionPIP_width = width;
        mPositionPIP_height = height;
    }

    public static void setPIPRectThick(int mThick) {
        mPIPRectThick = mThick;
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
                mNextMaskMode = HAS_SIMPLE_MASK;
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
        ShouldNotBeHere("SmartZoomFilter.setPIPMask() : mbitmap == null.");
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
        Log.v(TAG, "SmartZoomFilter.setPIPMask() misSetBitmapActive :" + misSetBitmapActive);
        if (!misSetBitmapActive) {
            misSetBitmapActive = true;
            switch (type) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    if (!isMaskBitmapNull(mbitmap)) {
                        if (mbitmap_overlay != null) {
                            Log.v(TAG, "SmartZoomFilter.setPIPMask() : mbitmap_overlay width = " + mbitmap_overlay.getWidth() + " height = " + mbitmap_overlay.getHeight());
                            mNextMaskMode = HAS_OVERLAY_MASK;
                            mMaskBitmap = mbitmap;
                            mOverlayBitmap = mbitmap_overlay;
                            mMaskChanged = true;
                            break;
                        }
                        mNextMaskMode = HAS_SIMPLE_MASK;
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
                        ShouldNotBeHere("SmartZoomFilter.setPIPMask(1) : Should not be here. **WARNING**");
                        break;
                    }
                    break;
                default:
                    ShouldNotBeHere("SmartZoomFilter.setPIPMask(default) : Should not be here. **WARNING**");
                    break;
            }
            mRequestMaskUpdate = true;
        }
    }

    private void learning_done() {
        if (this.mFrameCount == this.mLearningDuration) {
            Log.v(TAG, "[filter framework] Learning done");
            if (this.mLearningDoneListener != null) {
                this.mLearningDoneListener.onLearningDone(this);
            }
        }
    }

    private void log_framerate(FilterContext context) {
    }
}
