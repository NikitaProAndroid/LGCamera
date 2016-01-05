package com.lge.camera;

import android.content.Context;
import android.filterfw.GraphEnvironment;
import android.filterfw.core.Filter;
import android.filterfw.core.GLEnvironment;
import android.filterfw.core.GraphRunner;
import android.filterfw.core.GraphRunner.OnRunnerDoneListener;
import android.filterfw.geometry.Point;
import android.filterfw.geometry.Quad;
import android.filterpacks.videosrc.SurfaceTextureSource;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;
import com.lge.camera.components.RotateView;
import com.lge.camera.module.Module;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Util;
import com.lge.filterpacks.DualRecorderFilter;
import com.lge.filterpacks.DualRecorderFilter.LearningDoneListener;
import com.lge.filterpacks.DualRecorderFilter.PreviewFrameListener;
import com.lge.filterpacks.DualRecorderFilter.SetBitmapDoneListener;
import com.lge.filterpacks.DualRecorderFilter.SurfaceTextureSourceListener;
import com.lge.filterpacks.MediaEncoderFilter.OnRecordingDoneListener;
import com.lge.filterpacks.SmartZoomFilter;
import com.lge.media.CamcorderProfileEx;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.FaceDetector;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class EffectsBase {
    public static final int EFFECT_ALL = 4;
    public static final int EFFECT_BACKDROPPER = 2;
    public static final int EFFECT_DUAL_CAMERA = 6;
    public static final int EFFECT_DUAL_RECORD = 5;
    public static final int EFFECT_GF_BIG_EYES = 1;
    public static final int EFFECT_GF_BIG_MOUTH = 2;
    public static final int EFFECT_GF_BIG_NOSE = 4;
    public static final int EFFECT_GF_SMALL_EYES = 5;
    public static final int EFFECT_GF_SMALL_MOUTH = 3;
    public static final int EFFECT_GF_SQUEEZE = 0;
    public static final int EFFECT_GOOFY_FACE = 1;
    public static final int EFFECT_MSG_DONE_LEARNING = 1;
    public static final int EFFECT_MSG_EFFECTS_STOPPED = 3;
    public static final int EFFECT_MSG_ERROR = 6;
    public static final int EFFECT_MSG_PREVIEW_DONE = 5;
    public static final int EFFECT_MSG_RECORDING_DONE = 4;
    public static final int EFFECT_MSG_STARTED_LEARNING = 0;
    public static final int EFFECT_MSG_SURFACE_TEARDOWN = 7;
    public static final int EFFECT_MSG_SWITCHING_EFFECT = 2;
    public static final int EFFECT_NONE = 0;
    public static final int EFFECT_SMART_ZOOM_RECORD = 7;
    public static final int EFFECT_STOP_CALL_FROM_RESET_MENU = 101;
    public static final int EFFECT_STOP_CALL_FROM_SWAP_CAMCORDER = 102;
    public static final int EFFECT_STOP_CALL_NORMAL = 100;
    public static final int NUM_OF_GF_EFFECTS = 6;
    private static final String PAUSE_AND_RESUME_INPUT_NAME = "pauseNresumeRecording";
    protected static final int STATE_CONFIGURE = 0;
    protected static final int STATE_PREVIEW = 3;
    public static final int STATE_RECORD = 4;
    protected static final int STATE_RELEASED = 5;
    protected static final int STATE_STARTING_PREVIEW = 2;
    protected static final int STATE_WAITING_FOR_SURFACE = 1;
    protected static Context sContext;
    private static EffectBaseInterface sGet;
    public static LearningDoneListener sLearningDoneListener;
    public static SmartZoomFilter.LearningDoneListener sLearningDoneListener_SmartZoom;
    public static PreviewFrameListener sPreviewFrameListener;
    public static SetBitmapDoneListener sSetBitmapDoneListener;
    public static SmartZoomFilter.SetBitmapDoneListener sSetBitmapDoneListener_SmartZoom;
    private boolean isPausing;
    public int mCalledFrom;
    protected Camera mCameraDevice;
    protected int mCameraFacing;
    protected double mCaptureRate;
    protected int mCurrentEffect;
    private SurfaceTextureSourceListener mDualRecorderSourceReadyCallback;
    protected int mEffect;
    private Object mEffectParameter;
    private EffectsListener mEffectsListener;
    protected OnErrorListener mErrorListener;
    protected FileDescriptor mFd;
    protected GraphEnvironment mGraphEnv;
    protected int mGraphId;
    private Handler mHandler;
    protected OnInfoListener mInfoListener;
    private CountDownLatch mLatch;
    protected int mMaxDurationMs;
    protected long mMaxFileSize;
    protected GraphRunner mOldRunner;
    protected int mOrientationHint;
    protected String mOutputFile;
    protected int mPIP_SubWindow_Height;
    protected int mPIP_SubWindow_Thick;
    protected int mPIP_SubWindow_Width;
    protected int mPIP_SubWindow_X;
    protected int mPIP_SubWindow_Y;
    protected int mPreviewHeight;
    protected SurfaceHolder mPreviewSurfaceHolder;
    protected int mPreviewWidth;
    protected CamcorderProfileEx mProfile;
    private OnRecordingDoneListener mRecordingDoneListener;
    protected GraphRunner mRunner;
    protected OnRunnerDoneListener mRunnerDoneCallback;
    protected SurfaceTextureSource.SurfaceTextureSourceListener mSourceReadyCallback;
    protected int mState;
    protected int mTargetZoomAreaHeight;
    protected int mTargetZoomAreaWidth;
    protected int mTextureHeight;
    private SurfaceTexture mTextureSource;
    protected int mTextureWidth;
    protected int mVideoBitrate;

    public interface EffectBaseInterface {
        Module getCurrentModule();

        void jpegCallbackOnDualCameraPictureTaken(byte[] bArr);

        void onLearningDoneProcess();

        void onSetBitmapDoneProcess();
    }

    public interface EffectsListener {
        void onEffectsError(Exception exception, String str);

        void onEffectsUpdate(int i, int i2);
    }

    public static boolean isEffectSupported(int effectId) {
        switch (effectId) {
            case STATE_WAITING_FOR_SURFACE /*1*/:
                return Filter.isAvailable("com.google.android.filterpacks.facedetect.GoofyRenderFilter");
            case STATE_RECORD /*4*/:
                if (Filter.isAvailable("com.google.android.filterpacks.facedetect.GoofyRenderFilter") || Filter.isAvailable("android.filterpacks.videoproc.BackDropperFilter")) {
                    return true;
                }
                return false;
            case STATE_RELEASED /*5*/:
                return Filter.isAvailable("com.lge.filterpacks.DualRecorderFilter");
            default:
                return false;
        }
    }

    public EffectsBase(Context context, EffectBaseInterface inf) {
        this.mCaptureRate = 0.0d;
        this.mOrientationHint = STATE_CONFIGURE;
        this.mMaxFileSize = 0;
        this.mMaxDurationMs = STATE_CONFIGURE;
        this.mCameraFacing = STATE_CONFIGURE;
        this.mVideoBitrate = 2000000;
        this.mEffect = STATE_CONFIGURE;
        this.mCurrentEffect = STATE_CONFIGURE;
        this.mRunner = null;
        this.mOldRunner = null;
        this.mState = STATE_CONFIGURE;
        this.mCalledFrom = EFFECT_STOP_CALL_NORMAL;
        this.mPIP_SubWindow_X = STATE_CONFIGURE;
        this.mPIP_SubWindow_Y = STATE_CONFIGURE;
        this.mPIP_SubWindow_Width = CameraConstants.ORIENTATION_PORTRAIT_OPPOSITE_DEGREE_FROM;
        this.mPIP_SubWindow_Height = 90;
        this.mPIP_SubWindow_Thick = STATE_PREVIEW;
        this.mLatch = null;
        this.isPausing = false;
        this.mDualRecorderSourceReadyCallback = new SurfaceTextureSourceListener() {
            public void onSurfaceTextureSourceReady(SurfaceTexture source) {
                CamLog.d(FaceDetector.TAG, "----- onsurfaceTextureSourceReady callback (DualRecorderSourceReadyCallback) received: state:" + EffectsBase.this.mState + ",source:" + source);
                if (source != null) {
                    EffectsBase.this.awaitLatch();
                }
                synchronized (EffectsBase.this) {
                    if (source != null) {
                        if (source == EffectsBase.this.mTextureSource) {
                            CamLog.d(FaceDetector.TAG, "source is same with mTextureSource, return.");
                            return;
                        }
                    }
                    EffectsBase.this.mTextureSource = source;
                    if (checkTextureSourceAndState(source)) {
                        EffectsBase.this.tryEnable3ALocks(true);
                        CamLog.d(FaceDetector.TAG, "----- START : Changing the target display of mCameraDevice (SurfaceView -> SurfaceTexture)");
                        CamLog.d(FaceDetector.TAG, "### EffectBase - mCameraDevice.stopPreview()");
                        EffectsBase.this.mCameraDevice.stopPreview();
                        try {
                            EffectsBase.this.mCameraDevice.setPreviewTexture(EffectsBase.this.mTextureSource);
                            CamLog.d(FaceDetector.TAG, "### EffectBase - mCameraDevice.setPreviewTexture()");
                            Parameters params = EffectsBase.this.mCameraDevice.getParameters();
                            Size oldSizeOnDevice = params.getPreviewSize();
                            if (oldSizeOnDevice == null) {
                                CamLog.d(FaceDetector.TAG, "oldSizeOnDevice is Null");
                                return;
                            }
                            String recordSize;
                            if (EffectsBase.this.mEffect == EffectsBase.STATE_RELEASED || EffectsBase.this.mEffect == EffectsBase.NUM_OF_GF_EFFECTS) {
                                String profileVideoSize = EffectsBase.this.mProfile.videoFrameWidth + "x" + EffectsBase.this.mProfile.videoFrameHeight;
                                recordSize = MultimediaProperties.getDualRecordingResolution(profileVideoSize);
                                CamLog.v(FaceDetector.TAG, "mProfile.video size = " + profileVideoSize);
                                CamLog.v(FaceDetector.TAG, "record size = " + recordSize);
                            } else {
                                recordSize = MultimediaProperties.getLiveeffectResolutions(EffectsBase.this.mCameraFacing);
                            }
                            setPreviewSizeByModel(params, oldSizeOnDevice, recordSize);
                            CamLog.d(FaceDetector.TAG, "### EffectBase - mCameraDevice.setParameters, Set video-size " + recordSize);
                            params.set("video-size", recordSize);
                            if (EffectsBase.this.mEffect == EffectsBase.STATE_RELEASED) {
                                CamLog.d(FaceDetector.TAG, "Min FPS is set to 30000 Max FPS is set to 30000");
                                params.setPreviewFpsRange(MultimediaProperties.VALUE_VIDEO_FRAMERATE_NORMAL_RANGE_MAX, MultimediaProperties.VALUE_VIDEO_FRAMERATE_NORMAL_RANGE_MAX);
                            }
                            EffectsBase.this.mCameraDevice.setParameters(params);
                            CamLog.d(FaceDetector.TAG, "### EffectBase - mCameraDevice.startPreview()");
                            EffectsBase.this.mCameraDevice.startPreview();
                            EffectsBase.this.tryEnable3ALocks(false);
                            EffectsBase.this.mState = EffectsBase.STATE_PREVIEW;
                            EffectsBase.this.sendMessage(EffectsBase.this.mCurrentEffect, EffectsBase.STATE_RELEASED);
                            CamLog.d(FaceDetector.TAG, "----- END : Changing the target display of mCameraDevice (SurfaceView -> SurfaceTexture)");
                            return;
                        } catch (IOException e) {
                            EffectsBase.this.sendMessage(EffectsBase.this.mCurrentEffect, EffectsBase.NUM_OF_GF_EFFECTS);
                            throw new RuntimeException("Unable to connect camera to effect input", e);
                        }
                    }
                    if (source == null) {
                        EffectsBase.this.countDownLatch();
                    }
                }
            }

            private void setPreviewSizeByModel(Parameters params, Size oldSizeOnDevice, String recordSize) {
                int[] previewSizeOnDevice = Util.SizeString2WidthHeight(recordSize);
                if (oldSizeOnDevice.width != previewSizeOnDevice[EffectsBase.STATE_CONFIGURE] || oldSizeOnDevice.height != previewSizeOnDevice[EffectsBase.STATE_WAITING_FOR_SURFACE]) {
                    params.setPreviewSize(previewSizeOnDevice[EffectsBase.STATE_CONFIGURE], previewSizeOnDevice[EffectsBase.STATE_WAITING_FOR_SURFACE]);
                    CamLog.d(FaceDetector.TAG, "#### reset Preview size :" + previewSizeOnDevice[EffectsBase.STATE_CONFIGURE] + "x" + previewSizeOnDevice[EffectsBase.STATE_WAITING_FOR_SURFACE]);
                }
            }

            private boolean checkTextureSourceAndState(SurfaceTexture source) {
                if (EffectsBase.this.mState == 0) {
                    CamLog.v(FaceDetector.TAG, "Ready callback: Already stopped, skipping.");
                    return false;
                } else if (EffectsBase.this.mState == EffectsBase.STATE_RELEASED) {
                    CamLog.v(FaceDetector.TAG, "Ready callback: Already released, skipping.");
                    return false;
                } else if (source != null) {
                    return true;
                } else {
                    if (EffectsBase.this.mState != EffectsBase.STATE_PREVIEW && EffectsBase.this.mState != EffectsBase.STATE_STARTING_PREVIEW && EffectsBase.this.mState != EffectsBase.STATE_RECORD) {
                        return false;
                    }
                    if (EffectsBase.this.mState != EffectsBase.STATE_RECORD) {
                        CamLog.d(FaceDetector.TAG, "effectrecorder StopPreview and return");
                        if (EffectsBase.this.mEffect == EffectsBase.NUM_OF_GF_EFFECTS || EffectsBase.this.mEffect == EffectsBase.STATE_RELEASED) {
                            DualRecorderFilter.manualStopPreview();
                        } else {
                            EffectsBase.this.mCameraDevice.stopPreview();
                        }
                    } else {
                        CamLog.d(FaceDetector.TAG, "### EffectsBase state is STATE_RECORD, set STATE_PREVIEW");
                        EffectsBase.this.mState = EffectsBase.STATE_PREVIEW;
                        EffectsBase.this.sendMessage(EffectsBase.this.mCurrentEffect, EffectsBase.NUM_OF_GF_EFFECTS);
                    }
                    try {
                        EffectsBase.this.mCameraDevice.setPreviewTexture(null);
                        return false;
                    } catch (IOException e) {
                        EffectsBase.this.sendMessage(EffectsBase.this.mCurrentEffect, EffectsBase.NUM_OF_GF_EFFECTS);
                        throw new RuntimeException("Unable to disconnect camera from effect input", e);
                    }
                }
            }
        };
        this.mSourceReadyCallback = new SurfaceTextureSource.SurfaceTextureSourceListener() {
            public void onSurfaceTextureSourceReady(SurfaceTexture source) {
                CamLog.d(FaceDetector.TAG, "##### onsurfaceTextureSourceReady callback (SourceReadyCallback) received: state:" + EffectsBase.this.mState + ",source:" + source);
                if (source != null) {
                    EffectsBase.this.awaitLatch();
                }
                synchronized (EffectsBase.this) {
                    if (source != null) {
                        if (source == EffectsBase.this.mTextureSource) {
                            CamLog.d(FaceDetector.TAG, "source is same with mTextureSource, return.");
                            return;
                        }
                    }
                    EffectsBase.this.mTextureSource = source;
                    if (checkTextureSourceAndState(source)) {
                        EffectsBase.this.tryEnable3ALocks(true);
                        CamLog.d(FaceDetector.TAG, "### EffectsBase mCameraDevice.stopPreview()");
                        EffectsBase.this.mCameraDevice.stopPreview();
                        CamLog.v(FaceDetector.TAG, "Runner active, connecting effects preview");
                        try {
                            EffectsBase.this.mCameraDevice.setPreviewTexture(EffectsBase.this.mTextureSource);
                            Parameters params = EffectsBase.this.mCameraDevice.getParameters();
                            Size oldSizeOnDevice = params.getPreviewSize();
                            if (oldSizeOnDevice == null) {
                                CamLog.d(FaceDetector.TAG, "oldSizeOnDevice is Null");
                                return;
                            }
                            String recordSize;
                            String previewSizeOnDevice;
                            String profileVideoSize = EffectsBase.this.mProfile.videoFrameWidth + "x" + EffectsBase.this.mProfile.videoFrameHeight;
                            if (EffectsBase.this.mEffect == EffectsBase.STATE_RELEASED || EffectsBase.this.mEffect == EffectsBase.NUM_OF_GF_EFFECTS) {
                                recordSize = MultimediaProperties.getDualRecordingResolution(profileVideoSize);
                                previewSizeOnDevice = recordSize;
                                CamLog.v(FaceDetector.TAG, "mProfile.video size = " + profileVideoSize);
                                CamLog.v(FaceDetector.TAG, "record size = " + recordSize);
                            } else if (EffectsBase.this.mEffect == EffectsBase.EFFECT_SMART_ZOOM_RECORD) {
                                recordSize = MultimediaProperties.getSmartZoomResolution(profileVideoSize);
                                if (ModelProperties.isUVGAmodel()) {
                                    previewSizeOnDevice = MultimediaProperties.SMARTZOOM_PREVIEWSIZE_SET_ON_DEVICE_FOR_UVGA;
                                } else {
                                    previewSizeOnDevice = MultimediaProperties.SMARTZOOM_PREVIEWSIZE_SET_ON_DEVICE;
                                }
                            } else {
                                recordSize = MultimediaProperties.getLiveeffectResolutions(EffectsBase.this.mCameraFacing);
                                previewSizeOnDevice = recordSize;
                            }
                            setPreviewSizeByModel(params, oldSizeOnDevice, previewSizeOnDevice);
                            CamLog.d(FaceDetector.TAG, "### set video-size " + recordSize);
                            params.set("video-size", recordSize);
                            EffectsBase.this.mCameraDevice.setParameters(params);
                            CamLog.d(FaceDetector.TAG, "### EffectsBase mCameraDevice.startPreview()");
                            EffectsBase.this.mCameraDevice.startPreview();
                            EffectsBase.this.tryEnable3ALocks(false);
                            EffectsBase.this.mState = EffectsBase.STATE_PREVIEW;
                            EffectsBase.this.sendMessage(EffectsBase.this.mCurrentEffect, EffectsBase.STATE_RELEASED);
                            CamLog.v(FaceDetector.TAG, "Start preview/effect switch complete");
                            return;
                        } catch (IOException e) {
                            EffectsBase.this.sendMessage(EffectsBase.this.mCurrentEffect, EffectsBase.NUM_OF_GF_EFFECTS);
                            throw new RuntimeException("Unable to connect camera to effect input", e);
                        }
                    }
                    if (source == null) {
                        EffectsBase.this.countDownLatch();
                    }
                }
            }

            private void setPreviewSizeByModel(Parameters params, Size oldSizeOnDevice, String recordSize) {
                int[] previewSizeOnDevice = Util.SizeString2WidthHeight(recordSize);
                if (oldSizeOnDevice.width != previewSizeOnDevice[EffectsBase.STATE_CONFIGURE] || oldSizeOnDevice.height != previewSizeOnDevice[EffectsBase.STATE_WAITING_FOR_SURFACE]) {
                    params.setPreviewSize(previewSizeOnDevice[EffectsBase.STATE_CONFIGURE], previewSizeOnDevice[EffectsBase.STATE_WAITING_FOR_SURFACE]);
                    CamLog.d(FaceDetector.TAG, "#### reset Preview size :" + previewSizeOnDevice[EffectsBase.STATE_CONFIGURE] + "x" + previewSizeOnDevice[EffectsBase.STATE_WAITING_FOR_SURFACE]);
                }
            }

            private boolean checkTextureSourceAndState(SurfaceTexture source) {
                if (EffectsBase.this.mState == 0) {
                    CamLog.v(FaceDetector.TAG, "Ready callback: Already stopped, skipping.");
                    return false;
                } else if (EffectsBase.this.mState == EffectsBase.STATE_RELEASED) {
                    CamLog.v(FaceDetector.TAG, "Ready callback: Already released, skipping.");
                    return false;
                } else if (source != null) {
                    return true;
                } else {
                    if (EffectsBase.this.mState != EffectsBase.STATE_PREVIEW && EffectsBase.this.mState != EffectsBase.STATE_STARTING_PREVIEW && EffectsBase.this.mState != EffectsBase.STATE_RECORD) {
                        return false;
                    }
                    if (EffectsBase.this.mState != EffectsBase.STATE_RECORD) {
                        CamLog.d(FaceDetector.TAG, "### EffectsBase mCameraDevice.stopPreview() and return");
                        EffectsBase.this.mCameraDevice.stopPreview();
                    } else {
                        CamLog.d(FaceDetector.TAG, "### EffectsBase state is STATE_RECORD, set STATE_PREVIEW");
                        EffectsBase.this.mState = EffectsBase.STATE_PREVIEW;
                        EffectsBase.this.sendMessage(EffectsBase.this.mCurrentEffect, EffectsBase.NUM_OF_GF_EFFECTS);
                    }
                    try {
                        EffectsBase.this.mCameraDevice.setPreviewTexture(null);
                        return false;
                    } catch (IOException e) {
                        EffectsBase.this.sendMessage(EffectsBase.this.mCurrentEffect, EffectsBase.NUM_OF_GF_EFFECTS);
                        throw new RuntimeException("Unable to disconnect camera from effect input", e);
                    }
                }
            }
        };
        this.mRecordingDoneListener = new OnRecordingDoneListener() {
            public void onRecordingDone() {
                CamLog.v(FaceDetector.TAG, "Recording done callback triggered");
                if (EffectsBase.this.mEffectsListener != null) {
                    EffectsBase.this.mEffectsListener.onEffectsUpdate(EffectsBase.STATE_CONFIGURE, EffectsBase.STATE_RECORD);
                }
            }
        };
        this.mRunnerDoneCallback = new OnRunnerDoneListener() {
            public void onRunnerDone(int result) {
                Exception exception = null;
                synchronized (EffectsBase.this) {
                    CamLog.v(FaceDetector.TAG, "onRunnerDone:Graph runner done (" + EffectsBase.this + ", mRunner " + EffectsBase.this.mRunner + ", mOldRunner " + EffectsBase.this.mOldRunner + ")" + ", result " + result + ", mState " + EffectsBase.this.mState);
                    if (result == EffectsBase.NUM_OF_GF_EFFECTS) {
                        CamLog.e(FaceDetector.TAG, "Error running filter graph!");
                        EffectsBase effectsBase = EffectsBase.this;
                        if (EffectsBase.this.mRunner != null) {
                            exception = EffectsBase.this.mRunner.getError();
                        }
                        effectsBase.raiseError(exception);
                    }
                    if (EffectsBase.this.mOldRunner != null) {
                        CamLog.v(FaceDetector.TAG, "Tearing down old graph.");
                        GLEnvironment glEnv = EffectsBase.this.mGraphEnv.getContext().getGLEnvironment();
                        if (!(glEnv == null || glEnv.isActive())) {
                            try {
                                glEnv.activate();
                            } catch (RuntimeException e) {
                                CamLog.e(FaceDetector.TAG, "Cannot activate glEnv");
                                return;
                            }
                        }
                        EffectsBase.this.mOldRunner.getGraph().tearDown(EffectsBase.this.mGraphEnv.getContext());
                        if (glEnv != null && glEnv.isActive()) {
                            glEnv.deactivate();
                        }
                        EffectsBase.this.mOldRunner = null;
                        EffectsBase.this.sendMessage(EffectsBase.STATE_CONFIGURE, EffectsBase.EFFECT_SMART_ZOOM_RECORD);
                    }
                    if (EffectsBase.this.mState == EffectsBase.STATE_PREVIEW || EffectsBase.this.mState == EffectsBase.STATE_STARTING_PREVIEW) {
                        CamLog.v(FaceDetector.TAG, "Previous effect halted, starting new effect.");
                        EffectsBase.this.tryEnable3ALocks(false);
                        if (!(EffectsBase.this.mRunner == null || EffectsBase.this.mRunner.isRunning())) {
                            EffectsBase.this.mRunner.run();
                            CamLog.v(FaceDetector.TAG, "mRunner.run() is called in onRunnerDone callback");
                        }
                    } else if (EffectsBase.this.mState != EffectsBase.STATE_RELEASED) {
                        CamLog.v(FaceDetector.TAG, "Runner halted, restoring direct preview");
                        EffectsBase.this.tryEnable3ALocks(false);
                        if (EffectsBase.this.mCalledFrom != EffectsBase.EFFECT_STOP_CALL_FROM_SWAP_CAMCORDER) {
                            EffectsBase.this.sendMessage(EffectsBase.STATE_CONFIGURE, EffectsBase.STATE_PREVIEW);
                        }
                    }
                }
            }
        };
        sContext = context;
        sGet = inf;
        this.mPIP_SubWindow_Thick = (int) Util.dpToPx(sContext, CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK);
        if (ModelProperties.isUVGAmodel()) {
            if (isConfigureLandscape()) {
                this.mTargetZoomAreaWidth = (int) Util.dpToPx(sContext, CameraConstants.SMARTZOOM_ZOOM_AREA_WIDTH_UVGA);
                this.mTargetZoomAreaHeight = (int) Util.dpToPx(sContext, CameraConstants.SMARTZOOM_ZOOM_AREA_HEIGHT_UVGA);
            } else {
                this.mTargetZoomAreaWidth = (int) Util.dpToPx(sContext, CameraConstants.SMARTZOOM_ZOOM_AREA_HEIGHT_UVGA);
                this.mTargetZoomAreaHeight = (int) Util.dpToPx(sContext, CameraConstants.SMARTZOOM_ZOOM_AREA_WIDTH_UVGA);
            }
        } else if (isConfigureLandscape()) {
            this.mTargetZoomAreaWidth = (int) Util.dpToPx(sContext, CameraConstants.SMARTZOOM_ZOOM_AREA_WIDTH);
            this.mTargetZoomAreaHeight = (int) Util.dpToPx(sContext, CameraConstants.SMARTZOOM_ZOOM_AREA_HEIGHT_UVGA);
        } else {
            this.mTargetZoomAreaWidth = (int) Util.dpToPx(sContext, CameraConstants.SMARTZOOM_ZOOM_AREA_HEIGHT_UVGA);
            this.mTargetZoomAreaHeight = (int) Util.dpToPx(sContext, CameraConstants.SMARTZOOM_ZOOM_AREA_WIDTH);
        }
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    public void close() {
        CamLog.v(FaceDetector.TAG, "EffectsBase close ");
        this.mHandler = null;
        sContext = null;
        sGet = null;
        this.mGraphEnv = null;
        if (this.mRunner != null) {
            this.mRunner.stop();
            this.mRunner = null;
        }
        if (this.mOldRunner != null) {
            this.mOldRunner.stop();
            this.mOldRunner = null;
        }
        this.mInfoListener = null;
        this.mErrorListener = null;
        this.mEffectsListener = null;
        this.mEffectParameter = null;
        this.mPreviewSurfaceHolder = null;
        this.mTextureSource = null;
        this.mFd = null;
        this.mCameraDevice = null;
        this.mLatch = null;
    }

    public int getState() {
        return this.mState;
    }

    public void setCamera(Camera cameraDevice) {
        if (this.mState == STATE_PREVIEW || this.mState == STATE_RECORD || this.mState == STATE_RELEASED) {
            CamLog.d(FaceDetector.TAG, "setCamera cannot be called while " + this.mState);
            return;
        }
        CamLog.v(FaceDetector.TAG, "setCamera:" + cameraDevice);
        this.mCameraDevice = cameraDevice;
    }

    public void setProfile(CamcorderProfileEx profile) {
        if (this.mState == STATE_RECORD || this.mState == STATE_RELEASED) {
            CamLog.d(FaceDetector.TAG, "setProfile cannot be called while " + this.mState);
            return;
        }
        CamLog.v(FaceDetector.TAG, "setProfile:" + profile);
        this.mProfile = profile;
    }

    public void setOutputFile(String outputFile) {
        if (this.mState == STATE_RECORD || this.mState == STATE_RELEASED) {
            CamLog.d(FaceDetector.TAG, "setOutputFile cannot be called while " + this.mState);
            return;
        }
        CamLog.v(FaceDetector.TAG, "outpufile:" + outputFile);
        this.mOutputFile = outputFile;
        this.mFd = null;
    }

    public void setOutputFile(FileDescriptor fd) {
        if (this.mState == STATE_RECORD || this.mState == STATE_RELEASED) {
            CamLog.d(FaceDetector.TAG, "setOutputFile cannot be called while " + this.mState);
            return;
        }
        CamLog.v(FaceDetector.TAG, "FileDescriptor:" + fd);
        this.mOutputFile = null;
        this.mFd = fd;
    }

    public synchronized void setMaxFileSize(long maxFileSize) {
        if (this.mState == STATE_RECORD || this.mState == STATE_RELEASED) {
            CamLog.d(FaceDetector.TAG, "setMaxFileSize cannot be called while " + this.mState);
        } else {
            CamLog.v(FaceDetector.TAG, " setMaxFileSize:" + maxFileSize);
            this.mMaxFileSize = maxFileSize;
        }
    }

    public synchronized void setMaxDuration(int maxDurationMs) {
        if (this.mState == STATE_RECORD || this.mState == STATE_RELEASED) {
            CamLog.d(FaceDetector.TAG, "setMaxDuration cannot be called while " + this.mState);
        } else {
            CamLog.v(FaceDetector.TAG, " setMaxDuration:" + maxDurationMs);
            this.mMaxDurationMs = maxDurationMs;
        }
    }

    public void setCaptureRate(double fps) {
        if (this.mState == STATE_RECORD || this.mState == STATE_RELEASED) {
            CamLog.d(FaceDetector.TAG, "setCaptureRate cannot be called while " + this.mState);
            return;
        }
        CamLog.v(FaceDetector.TAG, " setCaptureRate:" + fps);
        this.mCaptureRate = fps;
    }

    public void setPreviewDisplay(SurfaceHolder previewSurfaceHolder, int previewWidth, int previewHeight) {
        if (this.mState == STATE_RECORD || this.mState == STATE_RELEASED) {
            CamLog.d(FaceDetector.TAG, "setPreviewDisplay cannot be called while " + this.mState);
            return;
        }
        CamLog.v(FaceDetector.TAG, " setPreviewDisplay (" + this + "), mState:" + this.mState + "," + previewWidth + "x" + previewHeight);
        this.mPreviewSurfaceHolder = previewSurfaceHolder;
        this.mPreviewWidth = previewWidth;
        this.mPreviewHeight = previewHeight;
        switch (this.mState) {
            case STATE_WAITING_FOR_SURFACE /*1*/:
                startPreview();
            case STATE_STARTING_PREVIEW /*2*/:
            case STATE_PREVIEW /*3*/:
                initializeEffect(true);
            default:
        }
    }

    public void setTextureSize(String size) {
        int[] textureSize = Util.SizeString2WidthHeight(size);
        this.mTextureWidth = textureSize[STATE_CONFIGURE];
        this.mTextureHeight = textureSize[STATE_WAITING_FOR_SURFACE];
        CamLog.d(FaceDetector.TAG, "texture size is explictly set as " + this.mTextureWidth + "x" + this.mTextureHeight);
    }

    public void setEffect(int effect, Object effectParameter) {
        if (this.mState == STATE_RECORD || this.mState == STATE_RELEASED) {
            CamLog.d(FaceDetector.TAG, "setEffect cannot be called while " + this.mState);
            return;
        }
        CamLog.v(FaceDetector.TAG, " setEffect: effect ID " + effect + ", parameter " + (effectParameter == null ? null : effectParameter.toString()) + ", mState : " + this.mState);
        this.mEffect = effect;
        this.mEffectParameter = effectParameter;
        if (this.mState == STATE_PREVIEW || this.mState == STATE_STARTING_PREVIEW) {
            initializeEffect(false);
        }
    }

    public void setEffectsListener(EffectsListener listener) {
        this.mEffectsListener = listener;
    }

    protected void setFaceDetectOrientation() {
        if (this.mCurrentEffect == STATE_WAITING_FOR_SURFACE) {
            Filter rotateFilter = this.mRunner.getGraph().getFilter("rotate");
            Filter metaRotateFilter = this.mRunner.getGraph().getFilter("metarotate");
            if (rotateFilter != null && metaRotateFilter != null) {
                int rotation = isConfigureLandscape() ? this.mOrientationHint : (this.mOrientationHint + Tag.IMAGE_DESCRIPTION) % CameraConstants.DEGREE_360;
                rotateFilter.setInputValue("rotation", Integer.valueOf(rotation));
                metaRotateFilter.setInputValue("rotation", Integer.valueOf((360 - rotation) % CameraConstants.DEGREE_360));
            }
        }
    }

    protected void setRecordingOrientation() {
        if (this.mState != STATE_RECORD && this.mRunner != null) {
            Quad recordingRegion;
            Point bl = new Point(0.0f, 0.0f);
            Point br = new Point(RotateView.DEFAULT_TEXT_SCALE_X, 0.0f);
            Point tl = new Point(0.0f, RotateView.DEFAULT_TEXT_SCALE_X);
            Point tr = new Point(RotateView.DEFAULT_TEXT_SCALE_X, RotateView.DEFAULT_TEXT_SCALE_X);
            if (this.mCameraFacing == 0 || this.mCurrentEffect == STATE_RELEASED || this.mCurrentEffect == NUM_OF_GF_EFFECTS) {
                if (isConfigureLandscape()) {
                    recordingRegion = new Quad(bl, br, tl, tr);
                } else {
                    recordingRegion = new Quad(br, tr, bl, tl);
                }
            } else if (isConfigureLandscape()) {
                if (this.mOrientationHint == 0 || this.mOrientationHint == MediaProviderUtils.ROTATION_180) {
                    recordingRegion = new Quad(br, bl, tr, tl);
                } else {
                    recordingRegion = new Quad(tl, tr, bl, br);
                }
            } else if (this.mOrientationHint == 0 || this.mOrientationHint == MediaProviderUtils.ROTATION_180) {
                recordingRegion = new Quad(tr, br, tl, bl);
            } else {
                recordingRegion = new Quad(bl, tl, br, tr);
            }
            Filter recorder = this.mRunner.getGraph().getFilter("recorder");
            if (recorder != null) {
                recorder.setInputValue("inputRegion", recordingRegion);
            }
        }
    }

    public int getOrientationHint() {
        return this.mOrientationHint;
    }

    public void setOrientationHint(int degrees) {
        if (this.mState == STATE_RELEASED) {
            CamLog.d(FaceDetector.TAG, "setOrientationHint cannot be called while " + this.mState);
            return;
        }
        CamLog.v(FaceDetector.TAG, "Setting orientation hint to: " + degrees);
        this.mOrientationHint = degrees;
        setFaceDetectOrientation();
        setRecordingOrientation();
    }

    public void setCameraFacing(int facing) {
        if (this.mState == STATE_RELEASED) {
            CamLog.d(FaceDetector.TAG, "setCameraFacing cannot be called while " + this.mState);
            return;
        }
        CamLog.v(FaceDetector.TAG, "setCameraFacing facing : " + facing);
        this.mCameraFacing = facing;
        setRecordingOrientation();
    }

    public void setVideoEncodingBitRate(int videoEncodingBitRate) {
        this.mVideoBitrate = videoEncodingBitRate;
    }

    public void setOnInfoListener(OnInfoListener infoListener) {
        if (this.mState == STATE_RECORD || this.mState == STATE_RELEASED) {
            CamLog.d(FaceDetector.TAG, "setInfoListener cannot be called while " + this.mState);
        } else {
            this.mInfoListener = infoListener;
        }
    }

    public void setOnErrorListener(OnErrorListener errorListener) {
        if (this.mState == STATE_RECORD || this.mState == STATE_RELEASED) {
            CamLog.d(FaceDetector.TAG, "setInfoListener cannot be called while " + this.mState);
        } else {
            this.mErrorListener = errorListener;
        }
    }

    static {
        sLearningDoneListener = new LearningDoneListener() {
            public void onLearningDone(DualRecorderFilter filter) {
                CamLog.v(FaceDetector.TAG, "Learning done callback triggered");
                CamLog.v(FaceDetector.TAG, "onLearningDone callback is called");
                EffectsBase.sGet.onLearningDoneProcess();
            }
        };
        sLearningDoneListener_SmartZoom = new SmartZoomFilter.LearningDoneListener() {
            public void onLearningDone(SmartZoomFilter filter) {
                CamLog.v(FaceDetector.TAG, "onLearningDone callback is called - SmartZoomFilter");
                EffectsBase.sGet.onLearningDoneProcess();
            }
        };
        sPreviewFrameListener = new PreviewFrameListener() {
            public void onPreviewFrame(byte[] previewFrameJpeg) {
                CamLog.v(FaceDetector.TAG, "onPreviewFrame callback is called");
                EffectsBase.sGet.jpegCallbackOnDualCameraPictureTaken(previewFrameJpeg);
            }
        };
        sSetBitmapDoneListener = new SetBitmapDoneListener() {
            public void onSetBitmapDone(DualRecorderFilter filter) {
                CamLog.v(FaceDetector.TAG, "SetBitmapDone callback triggered");
                CamLog.v(FaceDetector.TAG, "onSetBitmapDone callback is called");
                EffectsBase.sGet.onSetBitmapDoneProcess();
            }
        };
        sSetBitmapDoneListener_SmartZoom = new SmartZoomFilter.SetBitmapDoneListener() {
            public void onSetBitmapDone(SmartZoomFilter filter) {
                CamLog.v(FaceDetector.TAG, "SetBitmapDone_SmartZoom callback triggered");
                CamLog.v(FaceDetector.TAG, "onSetBitmapDone_SmartZoom callback is called");
                EffectsBase.sGet.onSetBitmapDoneProcess();
            }
        };
    }

    private void initializeFilterFramework() {
        int textureWidth;
        int textureHeight;
        CamLog.d(FaceDetector.TAG, "----- START: initializeFilterFramework()");
        if (this.mGraphEnv != null) {
            this.mGraphEnv = null;
        }
        this.mGraphEnv = new GraphEnvironment();
        this.mGraphEnv.createGLEnvironment();
        CamLog.d(FaceDetector.TAG, "----- Effects framework initializing. profile size " + this.mProfile.videoFrameWidth + ", " + this.mProfile.videoFrameHeight);
        int frameWidth = this.mProfile.videoFrameWidth;
        int frameHeight = this.mProfile.videoFrameHeight;
        if (isConfigureLandscape()) {
            textureWidth = this.mProfile.videoFrameWidth;
            textureHeight = this.mProfile.videoFrameHeight;
        } else {
            textureWidth = this.mProfile.videoFrameHeight;
            textureHeight = this.mProfile.videoFrameWidth;
        }
        GraphEnvironment graphEnvironment;
        Object[] objArr;
        if (this.mEffect == STATE_RELEASED) {
            graphEnvironment = this.mGraphEnv;
            objArr = new Object[STATE_STARTING_PREVIEW];
            objArr[STATE_CONFIGURE] = "textureSourceCallback";
            objArr[STATE_WAITING_FOR_SURFACE] = this.mDualRecorderSourceReadyCallback;
            graphEnvironment.addReferences(objArr);
            this.mGraphEnv.addReferences(new Object[]{"recordingWidth", Integer.valueOf(frameWidth), "recordingHeight", Integer.valueOf(frameHeight), "textureWidth", Integer.valueOf(textureWidth), "textureHeight", Integer.valueOf(textureHeight), "recordingProfile", this.mProfile, "learningDoneListener", sLearningDoneListener, "previewFrameListener", sPreviewFrameListener, "recordingDoneListener", this.mRecordingDoneListener});
        } else if (this.mEffect == NUM_OF_GF_EFFECTS) {
            graphEnvironment = this.mGraphEnv;
            objArr = new Object[STATE_STARTING_PREVIEW];
            objArr[STATE_CONFIGURE] = "textureSourceCallback";
            objArr[STATE_WAITING_FOR_SURFACE] = this.mDualRecorderSourceReadyCallback;
            graphEnvironment.addReferences(objArr);
            this.mGraphEnv.addReferences(new Object[]{"recordingWidth", Integer.valueOf(frameWidth), "recordingHeight", Integer.valueOf(frameHeight), "textureWidth", Integer.valueOf(textureWidth), "textureHeight", Integer.valueOf(textureHeight), "learningDoneListener", sLearningDoneListener, "previewFrameListener", sPreviewFrameListener});
        } else if (this.mEffect == EFFECT_SMART_ZOOM_RECORD) {
            this.mGraphEnv.addReferences(new Object[]{"textureSourceCallback", this.mSourceReadyCallback, "recordingWidth", Integer.valueOf(frameWidth), "recordingHeight", Integer.valueOf(frameHeight), "recordingProfile", this.mProfile, "learningDoneListener", sLearningDoneListener_SmartZoom, "recordingDoneListener", this.mRecordingDoneListener});
        } else {
            this.mGraphEnv.addReferences(new Object[]{"textureSourceCallback", this.mSourceReadyCallback, "recordingWidth", Integer.valueOf(textureWidth), "recordingHeight", Integer.valueOf(textureHeight), "recordingProfile", this.mProfile, "learningDoneListener", null, "recordingDoneListener", this.mRecordingDoneListener});
        }
        if (this.mRunner != null) {
            this.mRunner.stop();
            CamLog.d(FaceDetector.TAG, "mRunner.stop() is called in initializeFilterFramework()");
        }
        this.mRunner = null;
        this.mGraphId = -1;
        this.mCurrentEffect = STATE_CONFIGURE;
        CamLog.d(FaceDetector.TAG, "----- END: initializeFilterFramework()");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void initializeEffect(boolean r14) {
        /*
        r13 = this;
        r12 = 6;
        r11 = 3;
        r10 = 2;
        r1 = 1;
        r6 = 0;
        monitor-enter(r13);
        r7 = "CameraApp";
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0298 }
        r8.<init>();	 Catch:{ all -> 0x0298 }
        r9 = "-----  START: initializeEffect() - Reset anyway? ";
        r8 = r8.append(r9);	 Catch:{ all -> 0x0298 }
        r8 = r8.append(r14);	 Catch:{ all -> 0x0298 }
        r8 = r8.toString();	 Catch:{ all -> 0x0298 }
        com.lge.camera.util.CamLog.d(r7, r8);	 Catch:{ all -> 0x0298 }
        if (r14 != 0) goto L_0x0026;
    L_0x0020:
        r7 = r13.mCurrentEffect;	 Catch:{ all -> 0x0298 }
        r8 = r13.mEffect;	 Catch:{ all -> 0x0298 }
        if (r7 == r8) goto L_0x048f;
    L_0x0026:
        r7 = "CameraApp";
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0298 }
        r8.<init>();	 Catch:{ all -> 0x0298 }
        r9 = "Initializing effect. Preview size ";
        r8 = r8.append(r9);	 Catch:{ all -> 0x0298 }
        r9 = r13.mPreviewWidth;	 Catch:{ all -> 0x0298 }
        r8 = r8.append(r9);	 Catch:{ all -> 0x0298 }
        r9 = ", ";
        r8 = r8.append(r9);	 Catch:{ all -> 0x0298 }
        r9 = r13.mPreviewHeight;	 Catch:{ all -> 0x0298 }
        r8 = r8.append(r9);	 Catch:{ all -> 0x0298 }
        r8 = r8.toString();	 Catch:{ all -> 0x0298 }
        com.lge.camera.util.CamLog.v(r7, r8);	 Catch:{ all -> 0x0298 }
        r7 = "CameraApp";
        r8 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0298 }
        r8.<init>();	 Catch:{ all -> 0x0298 }
        r9 = "Effect is changed from mCurrentEffect(";
        r8 = r8.append(r9);	 Catch:{ all -> 0x0298 }
        r9 = r13.mCurrentEffect;	 Catch:{ all -> 0x0298 }
        r8 = r8.append(r9);	 Catch:{ all -> 0x0298 }
        r9 = ") to mEffect(";
        r8 = r8.append(r9);	 Catch:{ all -> 0x0298 }
        r9 = r13.mEffect;	 Catch:{ all -> 0x0298 }
        r8 = r8.append(r9);	 Catch:{ all -> 0x0298 }
        r9 = ")";
        r8 = r8.append(r9);	 Catch:{ all -> 0x0298 }
        r8 = r8.toString();	 Catch:{ all -> 0x0298 }
        com.lge.camera.util.CamLog.v(r7, r8);	 Catch:{ all -> 0x0298 }
        r7 = r13.mCurrentEffect;	 Catch:{ all -> 0x0298 }
        if (r7 == 0) goto L_0x0092;
    L_0x007c:
        r7 = r13.mEffect;	 Catch:{ all -> 0x0298 }
        if (r7 == r1) goto L_0x0092;
    L_0x0080:
        r7 = r13.mEffect;	 Catch:{ all -> 0x0298 }
        if (r7 == r12) goto L_0x0092;
    L_0x0084:
        r7 = "CameraApp";
        r8 = "Re-initialize FilterFramework before starting initializeEffect()";
        com.lge.camera.util.CamLog.v(r7, r8);	 Catch:{ all -> 0x0298 }
        r0 = r13.mCurrentEffect;	 Catch:{ all -> 0x0298 }
        r13.initializeFilterFramework();	 Catch:{ all -> 0x0298 }
        r13.mCurrentEffect = r0;	 Catch:{ all -> 0x0298 }
    L_0x0092:
        r7 = r13.isConfigureLandscape();	 Catch:{ all -> 0x0298 }
        if (r7 == 0) goto L_0x029b;
    L_0x0098:
        r4 = r6;
    L_0x0099:
        r7 = r13.isConfigureLandscape();	 Catch:{ all -> 0x0298 }
        if (r7 == 0) goto L_0x029f;
    L_0x009f:
        r3 = r6;
    L_0x00a0:
        r7 = r13.mPreviewSurfaceHolder;	 Catch:{ all -> 0x0298 }
        if (r7 == 0) goto L_0x0264;
    L_0x00a4:
        r7 = r13.mCameraFacing;	 Catch:{ all -> 0x0298 }
        if (r7 != 0) goto L_0x02a3;
    L_0x00a8:
        r6 = r13.isConfigureLandscape();	 Catch:{ all -> 0x0298 }
        if (r6 == 0) goto L_0x02a6;
    L_0x00ae:
        r5 = "landscape";
    L_0x00b0:
        r6 = r13.mEffect;	 Catch:{ all -> 0x0298 }
        r7 = 5;
        if (r6 != r7) goto L_0x02aa;
    L_0x00b5:
        r6 = r13.mGraphEnv;	 Catch:{ all -> 0x0298 }
        r7 = 24;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0298 }
        r8 = 0;
        r9 = "previewSurface";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 1;
        r9 = r13.mPreviewSurfaceHolder;	 Catch:{ all -> 0x0298 }
        r9 = r9.getSurface();	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 2;
        r9 = "previewWidth";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 3;
        r9 = r13.mPreviewWidth;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 4;
        r9 = "previewHeight";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 5;
        r9 = r13.mPreviewHeight;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 6;
        r9 = "cameraId";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 7;
        r9 = java.lang.Integer.valueOf(r1);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 8;
        r9 = "pipPosX";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 9;
        r9 = r13.mPIP_SubWindow_X;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 10;
        r9 = "pipPosY";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 11;
        r9 = r13.mPIP_SubWindow_Y;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 12;
        r9 = "pipWidth";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 13;
        r9 = r13.mPIP_SubWindow_Width;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 14;
        r9 = "pipHeight";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 15;
        r9 = r13.mPIP_SubWindow_Height;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 16;
        r9 = "pipRectThick";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 17;
        r9 = r13.mPIP_SubWindow_Thick;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 18;
        r9 = "orientationDegree";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 19;
        r9 = java.lang.Integer.valueOf(r4);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 20;
        r9 = "setBitmapDoneListener";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 21;
        r9 = sSetBitmapDoneListener;	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 22;
        r9 = "screenOrientation";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 23;
        r7[r8] = r5;	 Catch:{ all -> 0x0298 }
        r6.addReferences(r7);	 Catch:{ all -> 0x0298 }
    L_0x0168:
        r6 = r13.mEffect;	 Catch:{ all -> 0x0298 }
        r7 = 7;
        if (r6 != r7) goto L_0x0371;
    L_0x016d:
        r6 = r13.mGraphEnv;	 Catch:{ all -> 0x0298 }
        r7 = 32;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0298 }
        r8 = 0;
        r9 = "previewSurface";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 1;
        r9 = r13.mPreviewSurfaceHolder;	 Catch:{ all -> 0x0298 }
        r9 = r9.getSurface();	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 2;
        r9 = "previewWidth";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 3;
        r9 = r13.mPreviewWidth;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 4;
        r9 = "previewHeight";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 5;
        r9 = r13.mPreviewHeight;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 6;
        r9 = "textureWidth";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 7;
        r9 = r13.mTextureWidth;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 8;
        r9 = "textureHeight";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 9;
        r9 = r13.mTextureHeight;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 10;
        r9 = "pipPosX";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 11;
        r9 = r13.mPIP_SubWindow_X;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 12;
        r9 = "pipPosY";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 13;
        r9 = r13.mPIP_SubWindow_Y;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 14;
        r9 = "pipWidth";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 15;
        r9 = r13.mPIP_SubWindow_Width;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 16;
        r9 = "pipHeight";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 17;
        r9 = r13.mPIP_SubWindow_Height;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 18;
        r9 = "pipRectThick";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 19;
        r9 = r13.mPIP_SubWindow_Thick;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 20;
        r9 = "objectWidth";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 21;
        r9 = r13.mTargetZoomAreaWidth;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 22;
        r9 = "objectHeight";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 23;
        r9 = r13.mTargetZoomAreaHeight;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 24;
        r9 = "initObjectX";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 25;
        r9 = com.lge.camera.controller.PIPController.SMARTZOOM_DEFAULT_X;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 26;
        r9 = "initObjectY";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 27;
        r9 = com.lge.camera.controller.PIPController.SMARTZOOM_DEFAULT_Y;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 28;
        r9 = "setBitmapDoneListener";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 29;
        r9 = sSetBitmapDoneListener_SmartZoom;	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 30;
        r9 = "screenOrientation";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 31;
        r7[r8] = r5;	 Catch:{ all -> 0x0298 }
        r6.addReferences(r7);	 Catch:{ all -> 0x0298 }
    L_0x0264:
        r6 = r13.mState;	 Catch:{ all -> 0x0298 }
        if (r6 == r11) goto L_0x026c;
    L_0x0268:
        r6 = r13.mState;	 Catch:{ all -> 0x0298 }
        if (r6 != r10) goto L_0x0272;
    L_0x026c:
        r6 = r13.mCurrentEffect;	 Catch:{ all -> 0x0298 }
        r7 = 2;
        r13.sendMessage(r6, r7);	 Catch:{ all -> 0x0298 }
    L_0x0272:
        r6 = r13.mEffect;	 Catch:{ all -> 0x0298 }
        switch(r6) {
            case 1: goto L_0x03b4;
            case 2: goto L_0x0277;
            case 3: goto L_0x0277;
            case 4: goto L_0x0277;
            case 5: goto L_0x03f4;
            case 6: goto L_0x0402;
            case 7: goto L_0x0410;
            default: goto L_0x0277;
        };	 Catch:{ all -> 0x0298 }
    L_0x0277:
        r6 = new java.lang.RuntimeException;	 Catch:{ all -> 0x0298 }
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0298 }
        r7.<init>();	 Catch:{ all -> 0x0298 }
        r8 = "Unknown effect ID";
        r7 = r7.append(r8);	 Catch:{ all -> 0x0298 }
        r8 = r13.mEffect;	 Catch:{ all -> 0x0298 }
        r7 = r7.append(r8);	 Catch:{ all -> 0x0298 }
        r8 = "!";
        r7 = r7.append(r8);	 Catch:{ all -> 0x0298 }
        r7 = r7.toString();	 Catch:{ all -> 0x0298 }
        r6.<init>(r7);	 Catch:{ all -> 0x0298 }
        throw r6;	 Catch:{ all -> 0x0298 }
    L_0x0298:
        r6 = move-exception;
        monitor-exit(r13);
        throw r6;
    L_0x029b:
        r4 = 90;
        goto L_0x0099;
    L_0x029f:
        r3 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        goto L_0x00a0;
    L_0x02a3:
        r1 = r6;
        goto L_0x00a8;
    L_0x02a6:
        r5 = "portrait";
        goto L_0x00b0;
    L_0x02aa:
        r6 = r13.mEffect;	 Catch:{ all -> 0x0298 }
        if (r6 != r12) goto L_0x0168;
    L_0x02ae:
        r6 = r13.mGraphEnv;	 Catch:{ all -> 0x0298 }
        r7 = 26;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0298 }
        r8 = 0;
        r9 = "previewSurface";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 1;
        r9 = r13.mPreviewSurfaceHolder;	 Catch:{ all -> 0x0298 }
        r9 = r9.getSurface();	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 2;
        r9 = "previewWidth";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 3;
        r9 = r13.mPreviewWidth;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 4;
        r9 = "previewHeight";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 5;
        r9 = r13.mPreviewHeight;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 6;
        r9 = "cameraId";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 7;
        r9 = java.lang.Integer.valueOf(r1);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 8;
        r9 = "pipPosX";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 9;
        r9 = r13.mPIP_SubWindow_X;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 10;
        r9 = "pipPosY";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 11;
        r9 = r13.mPIP_SubWindow_Y;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 12;
        r9 = "pipWidth";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 13;
        r9 = r13.mPIP_SubWindow_Width;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 14;
        r9 = "pipHeight";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 15;
        r9 = r13.mPIP_SubWindow_Height;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 16;
        r9 = "pipRectThick";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 17;
        r9 = r13.mPIP_SubWindow_Thick;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 18;
        r9 = "orientationDegree";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 19;
        r9 = java.lang.Integer.valueOf(r4);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 20;
        r9 = "jpegRotationDegree";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 21;
        r9 = java.lang.Integer.valueOf(r3);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 22;
        r9 = "setBitmapDoneListener";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 23;
        r9 = sSetBitmapDoneListener;	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 24;
        r9 = "screenOrientation";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 25;
        r7[r8] = r5;	 Catch:{ all -> 0x0298 }
        r6.addReferences(r7);	 Catch:{ all -> 0x0298 }
        goto L_0x0168;
    L_0x0371:
        r6 = r13.mGraphEnv;	 Catch:{ all -> 0x0298 }
        r7 = 8;
        r7 = new java.lang.Object[r7];	 Catch:{ all -> 0x0298 }
        r8 = 0;
        r9 = "previewSurface";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 1;
        r9 = r13.mPreviewSurfaceHolder;	 Catch:{ all -> 0x0298 }
        r9 = r9.getSurface();	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 2;
        r9 = "previewWidth";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 3;
        r9 = r13.mPreviewWidth;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 4;
        r9 = "previewHeight";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 5;
        r9 = r13.mPreviewHeight;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 6;
        r9 = "orientation";
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r8 = 7;
        r9 = r13.mOrientationHint;	 Catch:{ all -> 0x0298 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ all -> 0x0298 }
        r7[r8] = r9;	 Catch:{ all -> 0x0298 }
        r6.addReferences(r7);	 Catch:{ all -> 0x0298 }
        goto L_0x0264;
    L_0x03b4:
        r6 = r13.mGraphEnv;	 Catch:{ all -> 0x0298 }
        r7 = sContext;	 Catch:{ all -> 0x0298 }
        r8 = 2131099661; // 0x7f06000d float:1.7811682E38 double:1.0529031304E-314;
        r6 = r6.loadGraph(r7, r8);	 Catch:{ all -> 0x0298 }
        r13.mGraphId = r6;	 Catch:{ all -> 0x0298 }
    L_0x03c1:
        r6 = r13.mEffect;	 Catch:{ all -> 0x0298 }
        r13.mCurrentEffect = r6;	 Catch:{ all -> 0x0298 }
        r6 = r13.mRunner;	 Catch:{ all -> 0x0298 }
        r13.mOldRunner = r6;	 Catch:{ all -> 0x0298 }
        r6 = r13.mGraphEnv;	 Catch:{ all -> 0x0298 }
        r7 = r13.mGraphId;	 Catch:{ all -> 0x0298 }
        r8 = 1;
        r6 = r6.getRunner(r7, r8);	 Catch:{ all -> 0x0298 }
        r13.mRunner = r6;	 Catch:{ all -> 0x0298 }
        r6 = r13.mRunner;	 Catch:{ all -> 0x0298 }
        if (r6 != 0) goto L_0x041e;
    L_0x03d8:
        r6 = "CameraApp";
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0298 }
        r7.<init>();	 Catch:{ all -> 0x0298 }
        r8 = "Error, mGraphEnv.getRunner is null, mGraphId:";
        r7 = r7.append(r8);	 Catch:{ all -> 0x0298 }
        r8 = r13.mGraphId;	 Catch:{ all -> 0x0298 }
        r7 = r7.append(r8);	 Catch:{ all -> 0x0298 }
        r7 = r7.toString();	 Catch:{ all -> 0x0298 }
        com.lge.camera.util.CamLog.d(r6, r7);	 Catch:{ all -> 0x0298 }
    L_0x03f2:
        monitor-exit(r13);
        return;
    L_0x03f4:
        r6 = r13.mGraphEnv;	 Catch:{ all -> 0x0298 }
        r7 = sContext;	 Catch:{ all -> 0x0298 }
        r8 = 2131099660; // 0x7f06000c float:1.781168E38 double:1.05290313E-314;
        r6 = r6.loadGraph(r7, r8);	 Catch:{ all -> 0x0298 }
        r13.mGraphId = r6;	 Catch:{ all -> 0x0298 }
        goto L_0x03c1;
    L_0x0402:
        r6 = r13.mGraphEnv;	 Catch:{ all -> 0x0298 }
        r7 = sContext;	 Catch:{ all -> 0x0298 }
        r8 = 2131099659; // 0x7f06000b float:1.7811677E38 double:1.0529031294E-314;
        r6 = r6.loadGraph(r7, r8);	 Catch:{ all -> 0x0298 }
        r13.mGraphId = r6;	 Catch:{ all -> 0x0298 }
        goto L_0x03c1;
    L_0x0410:
        r6 = r13.mGraphEnv;	 Catch:{ all -> 0x0298 }
        r7 = sContext;	 Catch:{ all -> 0x0298 }
        r8 = 2131099663; // 0x7f06000f float:1.7811686E38 double:1.0529031314E-314;
        r6 = r6.loadGraph(r7, r8);	 Catch:{ all -> 0x0298 }
        r13.mGraphId = r6;	 Catch:{ all -> 0x0298 }
        goto L_0x03c1;
    L_0x041e:
        r6 = r13.mRunner;	 Catch:{ all -> 0x0298 }
        r7 = r13.mRunnerDoneCallback;	 Catch:{ all -> 0x0298 }
        r6.setDoneCallback(r7);	 Catch:{ all -> 0x0298 }
        r6 = "CameraApp";
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0298 }
        r7.<init>();	 Catch:{ all -> 0x0298 }
        r8 = "New runner: ";
        r7 = r7.append(r8);	 Catch:{ all -> 0x0298 }
        r8 = r13.mRunner;	 Catch:{ all -> 0x0298 }
        r7 = r7.append(r8);	 Catch:{ all -> 0x0298 }
        r8 = ". Old runner: ";
        r7 = r7.append(r8);	 Catch:{ all -> 0x0298 }
        r8 = r13.mOldRunner;	 Catch:{ all -> 0x0298 }
        r7 = r7.append(r8);	 Catch:{ all -> 0x0298 }
        r7 = r7.toString();	 Catch:{ all -> 0x0298 }
        com.lge.camera.util.CamLog.v(r6, r7);	 Catch:{ all -> 0x0298 }
        r6 = r13.mState;	 Catch:{ all -> 0x0298 }
        if (r6 == r11) goto L_0x0453;
    L_0x044f:
        r6 = r13.mState;	 Catch:{ all -> 0x0298 }
        if (r6 != r10) goto L_0x048f;
    L_0x0453:
        r6 = "CameraApp";
        r7 = "### EffectsBase mCameraDevice.stopPreview()";
        com.lge.camera.util.CamLog.d(r6, r7);	 Catch:{ all -> 0x0298 }
        r6 = r13.mCameraDevice;	 Catch:{ all -> 0x0298 }
        r6.stopPreview();	 Catch:{ all -> 0x0298 }
        r6 = r13.mCameraDevice;	 Catch:{ IOException -> 0x04a1 }
        r7 = 0;
        r6.setPreviewTexture(r7);	 Catch:{ IOException -> 0x04a1 }
        r6 = "CameraApp";
        r7 = "### EffectsBase mCameraDevice.setPreviewTexture(null)";
        com.lge.camera.util.CamLog.d(r6, r7);	 Catch:{ IOException -> 0x04a1 }
        r6 = r13.mOldRunner;	 Catch:{ all -> 0x0298 }
        if (r6 == 0) goto L_0x048f;
    L_0x0470:
        r6 = r13.mOldRunner;	 Catch:{ all -> 0x0298 }
        r6.stop();	 Catch:{ all -> 0x0298 }
        r6 = "CameraApp";
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0298 }
        r7.<init>();	 Catch:{ all -> 0x0298 }
        r8 = "mOldRunner.stop() is called in initializeEffect(): mOldRunner = ";
        r7 = r7.append(r8);	 Catch:{ all -> 0x0298 }
        r8 = r13.mOldRunner;	 Catch:{ all -> 0x0298 }
        r7 = r7.append(r8);	 Catch:{ all -> 0x0298 }
        r7 = r7.toString();	 Catch:{ all -> 0x0298 }
        com.lge.camera.util.CamLog.v(r6, r7);	 Catch:{ all -> 0x0298 }
    L_0x048f:
        r13.setEffectInputValue();	 Catch:{ all -> 0x0298 }
        r13.setFaceDetectOrientation();	 Catch:{ all -> 0x0298 }
        r13.setRecordingOrientation();	 Catch:{ all -> 0x0298 }
        r6 = "CameraApp";
        r7 = "-----  END: initializeEffect()";
        com.lge.camera.util.CamLog.d(r6, r7);	 Catch:{ all -> 0x0298 }
        goto L_0x03f2;
    L_0x04a1:
        r2 = move-exception;
        r6 = new java.lang.RuntimeException;	 Catch:{ all -> 0x0298 }
        r7 = "Unable to connect camera to effect input";
        r6.<init>(r7, r2);	 Catch:{ all -> 0x0298 }
        throw r6;	 Catch:{ all -> 0x0298 }
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.camera.EffectsBase.initializeEffect(boolean):void");
    }

    protected void setEffectInputValue() {
        if (this.mRunner == null) {
            CamLog.d(FaceDetector.TAG, "mRunner is null !!");
            return;
        }
        switch (this.mCurrentEffect) {
            case STATE_WAITING_FOR_SURFACE /*1*/:
                Filter goofyFilter = this.mRunner.getGraph().getFilter("goofyrenderer");
                int effectValue = this.mEffectParameter == null ? STATE_CONFIGURE : ((Integer) this.mEffectParameter).intValue();
                if (goofyFilter != null) {
                    goofyFilter.setInputValue("currentEffect", Integer.valueOf(effectValue));
                }
            default:
        }
    }

    public synchronized void startPreview() {
        CamLog.v(FaceDetector.TAG, " Starting preview (" + this + "), mState:" + this.mState);
        if (this.mState == STATE_STARTING_PREVIEW || this.mState == STATE_PREVIEW) {
            CamLog.d(FaceDetector.TAG, "Do NOTHING because EffectsBase.startPreview() is called while EffectsBase.startPreview() is on-going, mState: " + this.mState);
        } else if (this.mState == STATE_RECORD || this.mState == STATE_RELEASED) {
            CamLog.d(FaceDetector.TAG, "Do NOTHING because startPreview cannot not be called in STATE_RECORD and STATE_RELEASED, mState: " + this.mState);
        } else if (this.mEffect == 0) {
            CamLog.d(FaceDetector.TAG, "No effect selected!");
        } else if (this.mEffectParameter == null) {
            CamLog.d(FaceDetector.TAG, "No effect selected!");
        } else if (this.mProfile == null) {
            CamLog.d(FaceDetector.TAG, "No effect selected!");
        } else if (this.mPreviewSurfaceHolder == null) {
            CamLog.v(FaceDetector.TAG, "Passed a null surface holder; waiting for valid one");
            this.mState = STATE_WAITING_FOR_SURFACE;
        } else if (this.mCameraDevice == null) {
            sendMessage(this.mCurrentEffect, NUM_OF_GF_EFFECTS);
            throw new RuntimeException("No camera to record from!");
        } else {
            CamLog.v(FaceDetector.TAG, "Initializing filter graph");
            initializeFilterFramework();
            initializeEffect(true);
            this.mState = STATE_STARTING_PREVIEW;
            if (this.mRunner != null) {
                CamLog.v(FaceDetector.TAG, "mRunner.run() is called, Starting filter graph, mState = " + this.mState);
                this.mRunner.run();
            }
        }
    }

    public synchronized void stopRecording() {
    }

    public synchronized void stopPreview(int calledFrom) {
        CamLog.v(FaceDetector.TAG, "EffectsBase stopPreview-start (" + this + "), mState:" + this.mState);
        if (this.mCameraDevice == null) {
            CamLog.w(FaceDetector.TAG, "EffectsBase mCameraDevice is null");
        } else {
            switch (this.mState) {
                case STATE_CONFIGURE /*0*/:
                    CamLog.w(FaceDetector.TAG, "EffectsBase StopPreview called when preview not active!");
                    break;
                case STATE_RELEASED /*5*/:
                    CamLog.w(FaceDetector.TAG, "EffectsBase stopPreview called on released EffectsBase!");
                    break;
                default:
                    if (this.mState == STATE_RECORD) {
                        stopRecording();
                    }
                    if (this.mCurrentEffect == STATE_RELEASED || this.mCurrentEffect == NUM_OF_GF_EFFECTS) {
                        DualRecorderFilter.manualStopPreview();
                    }
                    this.mCurrentEffect = STATE_CONFIGURE;
                    this.mCalledFrom = calledFrom;
                    CamLog.d(FaceDetector.TAG, "### EffectsBase mCameraDevice.stopPreview()");
                    this.mCameraDevice.stopPreview();
                    try {
                        this.mCameraDevice.setPreviewTexture(null);
                        this.mState = STATE_CONFIGURE;
                        this.mOldRunner = this.mRunner;
                        if (this.mRunner != null) {
                            CamLog.d(FaceDetector.TAG, "### mRunner.stop() is called in EffectsBase.stopPreview()");
                            this.mRunner.stop();
                            this.mLatch = new CountDownLatch(STATE_WAITING_FOR_SURFACE);
                        }
                        this.mRunner = null;
                        CamLog.v(FaceDetector.TAG, "EffectsBase stopPreview-end");
                        break;
                    } catch (IOException e) {
                        sendMessage(this.mCurrentEffect, NUM_OF_GF_EFFECTS);
                        throw new RuntimeException("Unable to connect camera to effect input", e);
                    }
            }
        }
    }

    boolean tryEnableVideoStabilization(boolean toggle) {
        Parameters params = this.mCameraDevice.getParameters();
        if (CameraConstants.ONEKEY_CONTROL_ENABLE_STRING.equals(params.get("video-stabilization-supported"))) {
            CamLog.v(FaceDetector.TAG, "Setting video stabilization to " + toggle);
            params.set("video-stabilization", toggle ? CameraConstants.ONEKEY_CONTROL_ENABLE_STRING : CameraConstants.ONEKEY_CONTROL_DISABLE_STRING);
            this.mCameraDevice.setParameters(params);
            return true;
        }
        CamLog.v(FaceDetector.TAG, "Video stabilization not supported");
        return false;
    }

    boolean tryEnable3ALocks(boolean toggle) {
        try {
            Parameters params = this.mCameraDevice.getParameters();
            if (params.isAutoExposureLockSupported() && params.isAutoWhiteBalanceLockSupported()) {
                params.setAutoExposureLock(toggle);
                params.setAutoWhiteBalanceLock(toggle);
                this.mCameraDevice.setParameters(params);
                return true;
            }
        } catch (RuntimeException e) {
            CamLog.e(FaceDetector.TAG, "RuntimeException-3A lock: ", e);
        }
        return false;
    }

    public void enable3ALocks(boolean toggle) {
        if (!tryEnable3ALocks(toggle)) {
            throw new RuntimeException("Attempt to lock 3A on camera with no locking support!");
        }
    }

    public synchronized void release() {
        CamLog.v(FaceDetector.TAG, "EffectsBase Releasing-start(" + this + "), mState:" + this.mState);
        switch (this.mState) {
            case STATE_STARTING_PREVIEW /*2*/:
            case STATE_PREVIEW /*3*/:
            case STATE_RECORD /*4*/:
                stopPreview(EFFECT_STOP_CALL_NORMAL);
                break;
        }
        this.mState = STATE_RELEASED;
        CamLog.v(FaceDetector.TAG, "EffectsBase Releasing-end");
    }

    public void sendMessage(final int effect, final int msg) {
        CamLog.d(FaceDetector.TAG, "sendMessage() effect:" + effect + " msg:" + msg + "isPausing=" + this.isPausing);
        if (this.mEffectsListener == null || this.mHandler == null) {
            CamLog.d(FaceDetector.TAG, "Warning: do nothing - mEffectsListener = " + this.mEffectsListener + ", mHandler = " + this.mHandler);
        } else {
            this.mHandler.post(new Runnable() {
                public void run() {
                    EffectsBase.this.mEffectsListener.onEffectsUpdate(effect, msg);
                }
            });
        }
    }

    private void raiseError(final Exception exception) {
        CamLog.e(FaceDetector.TAG, "Error!! raiseError()");
        if (this.mEffectsListener != null && this.mHandler != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (EffectsBase.this.mFd != null) {
                        EffectsBase.this.mEffectsListener.onEffectsError(exception, null);
                    } else {
                        EffectsBase.this.mEffectsListener.onEffectsError(exception, EffectsBase.this.mOutputFile);
                    }
                }
            });
        }
    }

    public void setPIPSubWindowPosition(int x0, int y0, int x1, int y1) {
        this.mPIP_SubWindow_X = x0;
        this.mPIP_SubWindow_Y = y0;
        this.mPIP_SubWindow_Width = x1 - x0;
        this.mPIP_SubWindow_Height = y1 - y0;
    }

    public void setPIPSubWindowThick(int thick) {
        this.mPIP_SubWindow_Thick = thick;
    }

    public void pauseAndResumeRecording(Filter recorder, boolean pause) {
        if (MultimediaProperties.isPauseAndResumeSupported()) {
            if (!(recorder != null || this.mRunner == null || this.mRunner.getGraph() == null)) {
                recorder = this.mRunner.getGraph().getFilter("recorder");
            }
            if (recorder != null) {
                CamLog.d(FaceDetector.TAG, " #####  recorder.setInputValue pauseNresume = " + pause);
                recorder.setInputValue(PAUSE_AND_RESUME_INPUT_NAME, Boolean.valueOf(pause));
                return;
            }
            CamLog.i(FaceDetector.TAG, "recorder is null.");
        }
    }

    protected boolean isConfigureLandscape() {
        if (sContext != null) {
            return Util.isConfigureLandscape(sContext.getResources());
        }
        return true;
    }

    private void awaitLatch() {
        try {
            if (this.mLatch != null) {
                this.mLatch.await(ProjectVariables.keepDuration, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e1) {
            CamLog.e(FaceDetector.TAG, "Latch fail : ", e1);
        }
    }

    private void countDownLatch() {
        if (this.mLatch != null) {
            this.mLatch.countDown();
            this.mLatch = null;
        }
    }

    public void isOnPausing(boolean state) {
        CamLog.d(FaceDetector.TAG, "set effect pausing=" + state);
        this.isPausing = state;
    }

    public boolean getIsOnPausing() {
        CamLog.d(FaceDetector.TAG, "effect pausing=" + this.isPausing);
        return this.isPausing;
    }
}
