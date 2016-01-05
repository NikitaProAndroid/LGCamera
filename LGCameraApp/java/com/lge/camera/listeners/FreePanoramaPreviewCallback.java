package com.lge.camera.listeners;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.FloatMath;
import com.lge.camera.R;
import com.lge.camera.components.RotateView;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.morpho.app.morphopanorama.GLTextureView;
import com.lge.morpho.app.morphopanorama.MathUtil;
import com.lge.morpho.app.morphopanorama.PanoramaApplication;
import com.lge.morpho.app.morphopanorama.PanoramaViewRenderer;
import com.lge.morpho.app.quickpanorama.MorphoImageStitcher;
import com.lge.morpho.app.quickpanorama.MorphoImageStitcher.FrameColor;
import com.lge.morpho.app.quickpanorama.MorphoImageStitcher.PanoramaInitParam;
import com.lge.morpho.core.MorphoSensorFusion;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class FreePanoramaPreviewCallback implements PreviewCallback {
    private final float BOUNDARY_DIFF_ANGLE;
    private double[] mACMatrix;
    private double mAngleOfViewDegree;
    private FreePanoramaPreviewCallbackFunction mGet;
    private double[] mGyroMatrix;
    private int[] mMaxHeapSize;
    private PanoramaInitParam mPanoramaInitParam;
    private int mPanoramaPreviewCount;
    private int mPanoramaProcessCount;
    private double[] mPrevSensorMat;
    private double[] mRVMatrix;
    private int[] mStatus;
    private long mTimeCurrentFrame;
    private long mTimeFrameInterval;
    private long mTimePreFrame;
    private int[] mUseImage;
    private boolean mUseStillImage;

    public interface FreePanoramaPreviewCallbackFunction {
        boolean checkOK(int i, String str, boolean z);

        Activity getActivity();

        byte[] getCameraBuff();

        int getCameraBuffID();

        Camera getCameraDevice();

        boolean getFinishFlg();

        GLTextureView getGLPanoramaView();

        MorphoImageStitcher getMorphoImageStitcher();

        MorphoSensorFusion getMorphoSensorFusion();

        int getPanoramaEngineState();

        int getPanoramaState();

        int[] getPreviewSize();

        PanoramaViewRenderer getRenderer();

        FreePanoramaSensorEventListener getSensorListener();

        void setCameraBuffID(int i);

        void setPanoramaEngineState(int i);

        void setPanoramaState(int i);

        void setSensorCorrectionGuideCounter(int i);

        void setShutterButtonImage(boolean z);
    }

    public FreePanoramaPreviewCallback(FreePanoramaPreviewCallbackFunction function) {
        this.mGyroMatrix = new double[9];
        this.mRVMatrix = new double[9];
        this.mACMatrix = new double[9];
        this.mUseStillImage = false;
        this.mMaxHeapSize = new int[1];
        this.mStatus = new int[1];
        this.mUseImage = new int[1];
        this.mPrevSensorMat = new double[9];
        this.BOUNDARY_DIFF_ANGLE = (float) Math.toRadians(1.0d);
        this.mPanoramaInitParam = null;
        this.mAngleOfViewDegree = 0.0d;
        this.mTimePreFrame = 0;
        this.mTimeFrameInterval = 0;
        this.mTimeCurrentFrame = 0;
        this.mGet = function;
        this.mPanoramaInitParam = new PanoramaInitParam();
    }

    public void resetCount() {
        this.mPanoramaPreviewCount = 0;
        this.mPanoramaProcessCount = 0;
    }

    public void setUseStillImage(boolean value) {
        this.mUseStillImage = value;
    }

    public int getMaxHeapSize() {
        return this.mMaxHeapSize[0];
    }

    public int[] getStatus() {
        return this.mStatus;
    }

    public void setStatus(int value) {
        this.mStatus[0] = value;
    }

    public int[] getUseImage() {
        return this.mUseImage;
    }

    public void setUseImage(int value) {
        this.mUseImage[0] = value;
    }

    public void unbind() {
        this.mPanoramaInitParam = null;
    }

    public void onPreviewFrame(byte[] cameraOutputRaw, Camera camera) {
        if (this.mGet.getFinishFlg() || this.mGet.getPanoramaState() == 4 || this.mGet.getMorphoImageStitcher() == null) {
            CamLog.d(FaceDetector.TAG, "mPreviewCallback mFinishFlg, getPanoramaState=" + this.mGet.getFinishFlg() + "," + this.mGet.getPanoramaState() + "," + this.mGet.getMorphoImageStitcher());
            return;
        }
        switch (this.mGet.getPanoramaEngineState()) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                panorama_initialize();
                this.mPanoramaPreviewCount = 0;
                this.mPanoramaProcessCount = 0;
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                panorama_preview(cameraOutputRaw);
                this.mPanoramaPreviewCount++;
            case LGKeyRec.EVENT_STARTED /*3*/:
                panorama_process(cameraOutputRaw);
                if (this.mPanoramaProcessCount < 5) {
                    this.mPanoramaProcessCount++;
                    if (this.mPanoramaProcessCount == 5) {
                        this.mGet.setPanoramaState(3);
                        this.mGet.setShutterButtonImage(true);
                    }
                }
            default:
        }
    }

    private void setFrameShape(FrameColor frame, float red, float green, float blue, float alpha, float width) {
        frame.R = red;
        frame.G = green;
        frame.B = blue;
        frame.A = alpha;
        frame.Width = width;
    }

    private void initFrameShape() {
        float frameWidth = (float) Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.free_panorama_guide_outline_width);
        float registeredFrameWidth = (float) Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.free_panorama_registered_outline_width);
        setFrameShape(this.mPanoramaInitParam.wire_frame_color, RotateView.DEFAULT_TEXT_SCALE_X, RotateView.DEFAULT_TEXT_SCALE_X, RotateView.DEFAULT_TEXT_SCALE_X, RotateView.DEFAULT_TEXT_SCALE_X, RotateView.DEFAULT_TEXT_SCALE_X);
        setFrameShape(this.mPanoramaInitParam.preview_frame_color, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, frameWidth);
        setFrameShape(this.mPanoramaInitParam.effective_input_frame_color, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, frameWidth);
        setFrameShape(this.mPanoramaInitParam.state_warning_need_to_stop_frame_color, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, frameWidth);
        setFrameShape(this.mPanoramaInitParam.state_info_stitchable_frame_color, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, frameWidth);
        setFrameShape(this.mPanoramaInitParam.state_warning_toofast_frame_color, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, frameWidth);
        setFrameShape(this.mPanoramaInitParam.state_warning_toofar_frame_color, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, frameWidth);
        setFrameShape(this.mPanoramaInitParam.state_error_alignment_frame_color, 0.0f, 0.0f, 0.0f, RotateView.DEFAULT_TEXT_SCALE_X, frameWidth);
        setFrameShape(this.mPanoramaInitParam.guide_frame_color, 0.015686f, 0.756863f, 0.929412f, RotateView.DEFAULT_TEXT_SCALE_X, frameWidth);
        setFrameShape(this.mPanoramaInitParam.registered_frame_color, 0.9f, 0.9f, 0.9f, RotateView.DEFAULT_TEXT_SCALE_X, registeredFrameWidth);
        this.mPanoramaInitParam.all_guide_disp_remaining_num = 1;
    }

    private void panorama_initParam() {
        this.mPanoramaInitParam.mode = 0;
        this.mPanoramaInitParam.render_mode = 1;
        this.mPanoramaInitParam.input_angle_of_view_degree = this.mAngleOfViewDegree;
        this.mPanoramaInitParam.input_width = this.mGet.getPreviewSize()[0];
        this.mPanoramaInitParam.input_height = this.mGet.getPreviewSize()[1];
        this.mPanoramaInitParam.use_still_capture = this.mUseStillImage ? 1 : 0;
        this.mPanoramaInitParam.still_width = this.mGet.getCameraDevice().getParameters().getPictureSize().width;
        this.mPanoramaInitParam.still_height = this.mGet.getCameraDevice().getParameters().getPictureSize().height;
        this.mPanoramaInitParam.still_angle_of_view_degree = this.mAngleOfViewDegree;
        this.mPanoramaInitParam.format = CameraConstants.FREE_PANO_YVU420_SP;
        this.mPanoramaInitParam.alpha_blending_image_frame = 0;
        this.mPanoramaInitParam.gradually_disp_guide_frame = 1;
        this.mPanoramaInitParam.fix_current_image = 1;
        this.mPanoramaInitParam.disp_current_image = 0;
        this.mPanoramaInitParam.blink_preview_mode = 1;
        this.mPanoramaInitParam.version = 1;
        this.mPanoramaInitParam.mask_poles = 0;
        int disp_w = Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.lcd_width);
        int disp_h = Common.getPixelFromDimens(this.mGet.getActivity().getApplicationContext(), R.dimen.lcd_height);
        double ls_fov = (80.0d * (disp_w > disp_h ? (double) disp_w : (double) disp_h)) / (disp_w > disp_h ? (double) disp_h : (double) disp_w);
        this.mPanoramaInitParam.angle_fov = (int) Math.sqrt((ls_fov * ls_fov) + (80.0d * 80.0d));
        CamLog.d(FaceDetector.TAG, "angle_fov=" + this.mPanoramaInitParam.angle_fov + " disp_w h =" + disp_w + "," + disp_h);
        this.mPanoramaInitParam.bg_color.R = 0.211765f;
        this.mPanoramaInitParam.bg_color.G = 0.231373f;
        this.mPanoramaInitParam.bg_color.B = 0.243137f;
        this.mPanoramaInitParam.bg_color.A = 0.0f;
    }

    private boolean panorama_initStitcher() {
        if (this.mGet.checkOK(this.mGet.getMorphoImageStitcher().initialize(this.mPanoramaInitParam, this.mMaxHeapSize), "mMorphoImageStitcher.initialize error ret:", true) && this.mGet.checkOK(this.mGet.getMorphoImageStitcher().setProjectionType(3), "mMorphoImageStitcher.setProjectionType error ret:", true) && this.mGet.checkOK(this.mGet.getMorphoImageStitcher().setGuideType(5), "mMorphoImageStitcher.setGuideType error ret:", true) && this.mGet.checkOK(this.mGet.getMorphoImageStitcher().setMotionlessThreshold(CameraConstants.TIME_MACHINE_ANI_INTERVAL), "mMorphoImageStitcher.setMotionlessThreshold error ret:", true) && this.mGet.checkOK(this.mGet.getMorphoImageStitcher().setUseThreshold(3), "mMorphoImageStitcher.setUseThreshold error ret:", true) && this.mGet.checkOK(this.mGet.getMorphoImageStitcher().setUseSensorAssist(0, 1), "mMorphoImageStitcher.setUseSensorAssist error ret:", true) && this.mGet.checkOK(this.mGet.getMorphoImageStitcher().setUseSensorAssist(1, 1), "mMorphoImageStitcher.setUseSensorAssist error ret:", true) && this.mGet.checkOK(this.mGet.getMorphoImageStitcher().setUseSensorThreshold(LGT_Limit.IMAGE_SIZE_WALLPAPER_HEIGHT), "mMorphoImageStitcher.setUseSensorThreshold error ret:", true) && this.mGet.checkOK(this.mGet.getMorphoImageStitcher().setTextureShrinkRatio(3), "mMorphoImageStitcher.setTextureShrinkRatio error ret:", true)) {
            return true;
        }
        return false;
    }

    public void panorama_initialize() {
        this.mGet.setPanoramaEngineState(1);
        panorama_initParam();
        initFrameShape();
        if (panorama_initStitcher()) {
            this.mGet.checkOK(this.mGet.getMorphoImageStitcher().start(1), "mMorphoImageStitcher.start error ret:", true);
            this.mUseImage[0] = 0;
            if (this.mGet.getCameraDevice() != null) {
                this.mGet.getCameraDevice().addCallbackBuffer(this.mGet.getCameraBuff());
                return;
            }
            return;
        }
        CamLog.d(FaceDetector.TAG, "init fail and exit");
    }

    private void printFrameInterval(boolean setCurrentTime) {
    }

    public void panorama_preview(byte[] cameraOutputRaw) {
        boolean isFirst;
        int i = 0;
        printFrameInterval(true);
        if (this.mPanoramaPreviewCount == 0) {
            isFirst = true;
        } else {
            isFirst = false;
        }
        if (isFirst) {
            this.mStatus[0] = 0;
            resetProcessingTimeInfo();
        }
        synchronized (this.mGet.getSensorListener().getSensorLockObj()) {
            double[] g_mat = null;
            double[] rv_mat = null;
            double[] ac_mat = null;
            if (this.mGet.getSensorListener().isUseSensor()) {
                this.mGet.getSensorListener().getSensorMatrix(this.mGyroMatrix, this.mRVMatrix, this.mACMatrix);
                g_mat = this.mGyroMatrix;
                rv_mat = this.mRVMatrix;
                ac_mat = this.mACMatrix;
            }
            FreePanoramaPreviewCallbackFunction freePanoramaPreviewCallbackFunction = this.mGet;
            if (this.mGet.getCameraBuffID() != 1) {
                i = 1;
            }
            freePanoramaPreviewCallbackFunction.setCameraBuffID(i);
            this.mGet.getCameraDevice().addCallbackBuffer(this.mGet.getCameraBuff());
            this.mGet.getRenderer().setRenderInfo(cameraOutputRaw, null, g_mat, rv_mat, ac_mat, -1, 0);
            this.mGet.getGLPanoramaView().requestRender();
            if (this.mGet.getPanoramaState() == 2 && this.mGet.getSensorListener().getWaitTime() < PanoramaApplication.SENSOR_CORRECTION_TIME_EVERYTIME && this.mGet.getSensorListener().getWaitTime() >= 0 && g_mat != null) {
                boolean check = checkAngleDiff(g_mat, this.mPrevSensorMat, (double) this.BOUNDARY_DIFF_ANGLE);
                System.arraycopy(g_mat, 0, this.mPrevSensorMat, 0, this.mPrevSensorMat.length);
                if (!check) {
                    this.mGet.getSensorListener().setWaitTime(0);
                    if (this.mGet.getSensorListener().getGyroscope() != null) {
                        this.mGet.getSensorListener().getGyroscopeValueList().clear();
                    }
                    this.mGet.setSensorCorrectionGuideCounter(0);
                    this.mGet.getMorphoSensorFusion().setAppState(1);
                    this.mGet.getMorphoSensorFusion().calc();
                }
            }
        }
    }

    private void resetProcessingTimeInfo() {
        this.mTimePreFrame = 0;
        this.mTimeFrameInterval = 0;
        this.mTimeCurrentFrame = 0;
        this.mGet.getRenderer().resetMesureInfo();
    }

    public void panorama_process(byte[] cameraOutputRaw) {
        boolean isFirst;
        int i = 0;
        if (this.mPanoramaProcessCount == 0) {
            isFirst = true;
        } else {
            isFirst = false;
        }
        if (isFirst) {
            resetProcessingTimeInfo();
            this.mGet.checkOK(this.mGet.getMorphoImageStitcher().end(), "mMorphoImageStitcher.start error ret:", true);
            this.mGet.checkOK(this.mGet.getMorphoImageStitcher().start(0), "mMorphoImageStitcher.start error ret:", true);
        }
        printFrameInterval(false);
        synchronized (this.mGet.getSensorListener().getSensorLockObj()) {
            double[] g_mat = null;
            double[] rv_mat = null;
            double[] ac_mat = null;
            if (this.mGet.getSensorListener().isUseSensor()) {
                this.mGet.getSensorListener().getSensorMatrix(this.mGyroMatrix, this.mRVMatrix, this.mACMatrix);
                g_mat = this.mGyroMatrix;
                rv_mat = this.mRVMatrix;
                ac_mat = this.mACMatrix;
            }
            FreePanoramaPreviewCallbackFunction freePanoramaPreviewCallbackFunction = this.mGet;
            if (this.mGet.getCameraBuffID() != 1) {
                i = 1;
            }
            freePanoramaPreviewCallbackFunction.setCameraBuffID(i);
            if (isStopPanoramaShooting(this.mStatus[0])) {
                return;
            }
            this.mGet.getCameraDevice().addCallbackBuffer(this.mGet.getCameraBuff());
            this.mGet.getRenderer().setRenderInfo(cameraOutputRaw, null, g_mat, rv_mat, ac_mat, -1, this.mPanoramaProcessCount);
            this.mGet.getGLPanoramaView().requestRender();
        }
    }

    private boolean isStopPanoramaShooting(int attach_status) {
        if (attach_status == 1 || attach_status == 3 || attach_status == 4 || attach_status == 2 || attach_status == 11 || attach_status == 12) {
            return true;
        }
        return false;
    }

    private double calcAngleOfViewDegree(float h_view_angle, float v_view_angle) {
        double value = (double) FloatMath.sqrt((h_view_angle * h_view_angle) + (v_view_angle * v_view_angle));
        if (((double) Math.abs((h_view_angle / 16.0f) - (v_view_angle / 9.0f))) < 0.1d) {
            return value * (Math.sqrt(225.0d) / Math.sqrt(337.0d));
        }
        return value;
    }

    public void setAngleOfViewDegree(float viewAngleH, float viewAngleV) {
        this.mAngleOfViewDegree = calcAngleOfViewDegree(viewAngleH, viewAngleV);
        if (this.mAngleOfViewDegree < 20.0d || 120.0d < this.mAngleOfViewDegree) {
            this.mAngleOfViewDegree = 60.0d;
        }
        CamLog.d(FaceDetector.TAG, "mAngleOfViewDegree=" + this.mAngleOfViewDegree);
    }

    private boolean checkAngleDiff(double[] cur_mat, double[] prev_mat, double abs_boundary_angle) {
        double[] diff = new double[3];
        boolean ret = true;
        MathUtil.getAngleDiff(diff, cur_mat, prev_mat);
        for (double abs : diff) {
            if (Math.abs(abs) > abs_boundary_angle) {
                ret = false;
            }
        }
        return ret;
    }
}
