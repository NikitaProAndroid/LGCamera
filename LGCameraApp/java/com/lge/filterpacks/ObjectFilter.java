package com.lge.filterpacks;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.util.Log;
import java.util.Arrays;

public class ObjectFilter extends Filter {
    private static final int OBJECT_TRACKING_AVERAGE_COUNT = 10;
    private static final String TAG = "ObjectFilter";
    private static boolean mAverageOT;
    private static int mFrameCount;
    private static boolean mObTracking;
    @GenerateFieldPort(hasDefault = true, name = "objectHeight")
    private static int mObjectHeight;
    @GenerateFieldPort(hasDefault = true, name = "objectWidth")
    private static int mObjectWidth;
    @GenerateFieldPort(hasDefault = true, name = "initObjectX")
    private static int mObjectX;
    @GenerateFieldPort(hasDefault = true, name = "initObjectY")
    private static int mObjectY;
    private static final String[] mOutputNames;
    private static int[] mPosX;
    private static int[] mPosY;
    private static ShaderProgram mProgram;
    @GenerateFieldPort(hasDefault = true, name = "previewHeight")
    private static int mScreenHeight;
    @GenerateFieldPort(hasDefault = true, name = "previewWidth")
    private static int mScreenWidth;
    private final String mFragShader;
    private FrameFormat mOutputFormat;

    static {
        mObjectWidth = 0;
        mObjectHeight = 0;
        mScreenWidth = 0;
        mScreenHeight = 0;
        mObjectX = 0;
        mObjectY = 0;
        mOutputNames = new String[]{"video"};
        mObTracking = false;
    }

    public ObjectFilter(String name) {
        super(name);
        this.mFragShader = "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nuniform sampler2D tex_sampler_0;\nvarying vec2 v_texcoord;\nuniform float start_x;\nuniform float start_y;\nuniform float end_x;\nuniform float end_y;\nvoid main() {\n\tvec2 t_coord = clamp(v_texcoord, vec2(start_x, start_y), vec2(end_x, end_y));\n\tgl_FragColor = texture2D(tex_sampler_0, t_coord);\n}\n";
    }

    public void process(FilterContext context) {
        Frame input = pullInput("video");
        Frame output = context.getFrameManager().newFrame(this.mOutputFormat);
        setObjectPosition();
        mProgram.process(input, output);
        pushOutput("video", output);
        output.release();
    }

    private void setObjectPosition() {
        float start_x = ((float) mObjectX) / ((float) mScreenWidth);
        float start_y = ((float) mObjectY) / ((float) mScreenHeight);
        float end_x = start_x + (((float) mObjectWidth) / ((float) mScreenWidth));
        float end_y = start_y + (((float) mObjectHeight) / ((float) mScreenHeight));
        mProgram.setHostValue("start_x", Float.valueOf(start_x));
        mProgram.setHostValue("start_y", Float.valueOf(start_y));
        mProgram.setHostValue("end_x", Float.valueOf(end_x));
        mProgram.setHostValue("end_y", Float.valueOf(end_y));
        mProgram.setSourceRect(start_x, start_y, end_x - start_x, end_y - start_y);
    }

    public void setupPorts() {
        Log.i(TAG, "[SmartZoom] ObjectFilter:setupPorts");
        addMaskedInputPort("video", ImageFormat.create(3, 0));
        for (String outputName : mOutputNames) {
            addOutputBasedOnInput(outputName, "video");
        }
    }

    protected void prepare(FilterContext context) {
        Log.i(TAG, "[SmartZoom] ObjectFilter:prepare");
        mProgram = new ShaderProgram(context, "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nuniform sampler2D tex_sampler_0;\nvarying vec2 v_texcoord;\nuniform float start_x;\nuniform float start_y;\nuniform float end_x;\nuniform float end_y;\nvoid main() {\n\tvec2 t_coord = clamp(v_texcoord, vec2(start_x, start_y), vec2(end_x, end_y));\n\tgl_FragColor = texture2D(tex_sampler_0, t_coord);\n}\n");
        this.mOutputFormat = ImageFormat.create(mObjectWidth, mObjectHeight, 3, 3);
        mPosX = new int[OBJECT_TRACKING_AVERAGE_COUNT];
        mPosY = new int[OBJECT_TRACKING_AVERAGE_COUNT];
        initValues();
        updateObjPosition(mObjectX, mObjectY);
    }

    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        Log.i(TAG, "[SmartZoom] ObjectFilter:getOutputFormat");
        MutableFrameFormat format = inputFormat.mutableCopy();
        if (!Arrays.asList(mOutputNames).contains(portName)) {
            format.setDimensions(0, 0);
        }
        return format;
    }

    public static void updateObjPosition(int x, int y) {
        Log.i(TAG, "updateObjPosition - mObTracking: " + mObTracking + "  / " + x + " x " + y);
        if (mObTracking) {
            updateObTrackingPosition(x, y);
        } else {
            updateManualPosition(x, y);
        }
    }

    private static void updateObTrackingPosition(int x, int y) {
        mPosX[mFrameCount] = clipX(x - (mObjectWidth / 2));
        mPosY[mFrameCount] = clipY(y - (mObjectHeight / 2));
        if (mAverageOT) {
            int sumX = 0;
            int sumY = 0;
            for (int i = 0; i < mPosX.length; i++) {
                sumX += mPosX[i];
                sumY += mPosY[i];
            }
            mObjectX = sumX / OBJECT_TRACKING_AVERAGE_COUNT;
            mObjectY = sumY / OBJECT_TRACKING_AVERAGE_COUNT;
        } else {
            updateManualPosition(x, y);
        }
        mFrameCount++;
        if (mFrameCount == OBJECT_TRACKING_AVERAGE_COUNT) {
            mFrameCount = 0;
            if (!mAverageOT) {
                mAverageOT = true;
            }
        }
    }

    private static void updateManualPosition(int x, int y) {
        mObjectX = clipX(x - (mObjectWidth / 2));
        mObjectY = clipY(y - (mObjectHeight / 2));
    }

    private static int clipY(int i) {
        int th = mScreenHeight - mObjectHeight;
        if (i > th) {
            return th;
        }
        return i < 0 ? 0 : i;
    }

    private static int clipX(int i) {
        int th = mScreenWidth - mObjectWidth;
        if (i > th) {
            return th;
        }
        return i < 0 ? 0 : i;
    }

    public static void setObjectTrackingMode(boolean mode) {
        mObTracking = mode;
        if (!mode) {
            initValues();
        }
    }

    private static void initValues() {
        mFrameCount = 0;
        mAverageOT = false;
        for (int i = 0; i < mPosX.length; i++) {
            mPosX[i] = 0;
            mPosY[i] = 0;
        }
    }
}
