package com.lge.camera;

import android.hardware.Camera;
import android.location.Location;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.PreferenceGroup;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Util;
import com.lge.media.MediaRecorderEx;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class VideoRecorder {
    public static final int DEFAULT_DURATION = 7200000;
    public static final int MMS = 1;
    private static final int MMS_DURATION;
    public static final int NORMAL = 0;
    private static final String TAG = "CameraApp";
    private static final int VALUE_VIDEO_FPS = 30;
    private static final int VALUE_VIDEO_FPS_MMS = 15;
    private static WeakReference<Camera> mCameraDevice;
    private static boolean mIsInitialized;
    private static boolean mIsRecording;
    private static long mMaxFileSize;
    private static MediaRecorderEx mMediaRecorder;
    private static int mOrientationHint;
    private static int mPurpose;
    private static VideoFile mVideoFile;
    private static int maxDuration;

    static {
        mMediaRecorder = null;
        mCameraDevice = null;
        mIsInitialized = false;
        mVideoFile = null;
        mIsRecording = false;
        mPurpose = NORMAL;
        mMaxFileSize = 0;
        maxDuration = -1;
        MMS_DURATION = MultimediaProperties.getMMSMaxDuration();
        mOrientationHint = NORMAL;
    }

    public static boolean isInitialized() {
        return mIsInitialized;
    }

    public static boolean isRecording() {
        return mIsRecording;
    }

    public static boolean setErrorListener(OnErrorListener l) {
        if (mMediaRecorder == null) {
            return false;
        }
        mMediaRecorder.setOnErrorListener(l);
        return true;
    }

    public static boolean setInfoListener(OnInfoListener l) {
        if (mMediaRecorder == null) {
            return false;
        }
        mMediaRecorder.setOnInfoListener(l);
        return true;
    }

    public static boolean setVideoSize(int purpose) {
        mPurpose = purpose;
        return true;
    }

    public static boolean setMaxFileSize(long maxFileSize, long freeSpace, int storage) {
        mMaxFileSize = maxFileSize;
        if (mMaxFileSize == 0) {
            mMaxFileSize = freeSpace - CameraConstants.VIDEO_LOW_STORAGE_THRESHOLD;
            if (mMaxFileSize > MultimediaProperties.getMediaRecoderLimitSize()) {
                mMaxFileSize = MultimediaProperties.getMediaRecoderLimitSize();
            }
            if (mMaxFileSize < 0) {
                mMaxFileSize = CameraConstants.VIDEO_LOW_STORAGE_THRESHOLD;
                CamLog.d(TAG, "mMaxFileSize: " + mMaxFileSize + " freeSpace: " + freeSpace + " storage: " + storage);
                return false;
            }
        } else if (freeSpace <= mMaxFileSize) {
            mMaxFileSize = freeSpace;
        }
        CamLog.d(TAG, "mMaxFileSize: " + mMaxFileSize + " freeSpace: " + freeSpace + " storage: " + storage);
        return true;
    }

    public static void setMaxDuration(boolean isMMS) {
        maxDuration = isMMS ? MMS_DURATION : -1;
    }

    public static MediaRecorderEx init(ControllerFunction function) {
        ListPreference pref = null;
        CamLog.d(TAG, "Camcorder(MediaRecorder) init()-start");
        if (!mIsInitialized) {
            if (function.checkSurfaceHolder()) {
                if (mMediaRecorder == null) {
                    CamLog.i(TAG, "Camcorder new MediaRecorder()");
                    mMediaRecorder = new MediaRecorderEx();
                }
                mCameraDevice = new WeakReference(function.getCameraDevice());
                Camera camera = (Camera) mCameraDevice.get();
                if (camera == null) {
                    return null;
                }
                camera.unlock();
                mMediaRecorder.setCamera(camera);
                PreferenceGroup prefGroup = function.getPreferenceGroup();
                if (prefGroup != null) {
                    pref = prefGroup.findPreference(Setting.KEY_VIDEO_AUDIO_RECORDING);
                }
                if (pref == null || !"mute".equals(pref.getValue())) {
                    CamLog.i(TAG, "AUDIO is unmute");
                    mMediaRecorder.setAudioSource(5);
                } else {
                    CamLog.i(TAG, "AUDIO is mute");
                }
                mMediaRecorder.setVideoSource(MMS);
                if (mPurpose == MMS) {
                    mMediaRecorder.setOutputFormat(MMS);
                } else {
                    mMediaRecorder.setOutputFormat(2);
                }
                Location loc = function.getRecordLocation() ? function.getCurrentLocation() : null;
                if (loc != null) {
                    mMediaRecorder.setLocation((float) loc.getLatitude(), (float) loc.getLongitude());
                }
                String videoSize = null;
                int[] size = null;
                if (function.checkPreviewController()) {
                    videoSize = function.getPreviewSizeOnDevice();
                }
                if (videoSize != null) {
                    size = Util.SizeString2WidthHeight(videoSize);
                    mMediaRecorder.setVideoSize(size[NORMAL], size[MMS]);
                    CamLog.i(TAG, String.format("setVideoSize width = %d , hegiht = %d", new Object[]{Integer.valueOf(size[NORMAL]), Integer.valueOf(size[MMS])}));
                } else {
                    CamLog.d(TAG, "error!! videoSize is null");
                }
                if (prefGroup != null) {
                    pref = prefGroup.findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
                }
                String sVideoFps = CameraConstants.SMART_MODE_OFF;
                if (pref != null) {
                    sVideoFps = pref.getExtraInfo3();
                }
                int profileBitrate = MultimediaProperties.getBitrate(function.getCameraId(), MultimediaProperties.getProfileQulity(function.getCameraId(), size));
                CamLog.i(TAG, "VideoRecorder-Init : Preference bitrate is larger than profile bitrate.");
                CamLog.i(TAG, "VideoRecorder-Init : Preference bitrate = " + NORMAL + ", Profile bitrate = " + profileBitrate);
                int iVideoBitrate = profileBitrate;
                if (mPurpose == MMS) {
                    mMediaRecorder.setVideoEncodingBitRate(CameraConstants.VALUE_VIDEO_BITRATE_MMS);
                    if (ModelProperties.isOMAP4Chipset()) {
                        mMediaRecorder.setVideoFrameRate(VALUE_VIDEO_FPS);
                    } else {
                        mMediaRecorder.setVideoFrameRate(VALUE_VIDEO_FPS_MMS);
                    }
                } else {
                    if (MultimediaProperties.isHighFramRateVideoSupported() && function.getCameraId() == 0) {
                        if (CameraConstants.TYPE_RECORDMODE_NORMAL.equals(function.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE)) && !CameraConstants.SMART_MODE_OFF.equals(sVideoFps)) {
                            mMediaRecorder.setVideoFrameRate(Integer.parseInt(sVideoFps));
                            iVideoBitrate = MultimediaProperties.getHFRBitrate(function.getCameraId());
                            mMediaRecorder.setVideoEncodingBitRate(iVideoBitrate);
                        }
                    }
                    mMediaRecorder.setVideoFrameRate(VALUE_VIDEO_FPS);
                    mMediaRecorder.setVideoEncodingBitRate(iVideoBitrate);
                }
                if (prefGroup != null) {
                    pref = prefGroup.findPreference(Setting.KEY_VIDEO_AUDIO_RECORDING);
                }
                if (pref != null && "mute".equals(pref.getValue())) {
                    CamLog.i(TAG, "AUDIO REC OFF -> Audio Encorder NOT SET");
                } else if (mPurpose == MMS) {
                    mMediaRecorder.setAudioEncoder(MultimediaProperties.getMmsAudioEncodingType());
                    if (MultimediaProperties.getMmsAudioEncodingType() == 3) {
                        mMediaRecorder.setAudioEncodingBitRate(48000);
                        mMediaRecorder.setAudioChannels(MMS);
                        mMediaRecorder.setAudioSamplingRate(8000);
                    }
                } else {
                    mMediaRecorder.setAudioEncoder(3);
                    if (ModelProperties.isOMAP4Chipset()) {
                        mMediaRecorder.setAudioEncodingBitRate(48000);
                        mMediaRecorder.setAudioChannels(MMS);
                        mMediaRecorder.setAudioSamplingRate(22050);
                    }
                }
                if (mPurpose == MMS) {
                    mMediaRecorder.setVideoEncoder(MultimediaProperties.getMmsVideoEncodingType());
                } else if (ModelProperties.isMTKChipset()) {
                    CamLog.i(TAG, "setVideoEncoder => MediaRecorderEX.VideoEncoder.MPEG_4_SP");
                    mMediaRecorder.setVideoEncoder(3);
                } else {
                    mMediaRecorder.setVideoEncoder(2);
                }
                mMediaRecorder.setMaxFileSize(mMaxFileSize);
                if (mPurpose == MMS) {
                    maxDuration = DEFAULT_DURATION;
                    if (MMS_DURATION != -1 && (function.isMMSIntent() || function.needProgressBar())) {
                        maxDuration = MMS_DURATION;
                    } else if (function.getRecordingDurationLimit() != 0) {
                        maxDuration = (int) function.getRecordingDurationLimit();
                    }
                } else if (function.getRecordingDurationLimit() != 0) {
                    maxDuration = (int) function.getRecordingDurationLimit();
                } else {
                    maxDuration = DEFAULT_DURATION;
                }
                mMediaRecorder.setMaxDuration(maxDuration);
                mMediaRecorder.setPreviewDisplay(function.getSurfaceHolder().getSurface());
                mMediaRecorder.setOrientationHint(mOrientationHint);
                VideoFile videoFile = function.getVideoFile();
                if (videoFile == null || !videoFile.isInitialized()) {
                    CamLog.d(TAG, "Video file not ready!");
                    release();
                } else {
                    mVideoFile = videoFile;
                    mMediaRecorder.setOutputFile(videoFile.getFileExternalPath());
                    try {
                        mMediaRecorder.prepare();
                        CamLog.i(TAG, "Media recorder initialized.");
                        mIsInitialized = true;
                        CamLog.i(TAG, "RECORDER_INIT_DONE");
                    } catch (IllegalStateException exception) {
                        CamLog.e(TAG, "IllegalStateException in init recorder prepare : " + exception.toString());
                        release();
                    } catch (IOException exception2) {
                        CamLog.e(TAG, "recorder prepare error: " + exception2.toString());
                        CamLog.e(TAG, "prepare failed (" + videoFile.getFilePath() + ")");
                        release();
                    }
                }
            } else {
                CamLog.d(TAG, "Surface holder is null. Wait for surface changed.");
                return null;
            }
        }
        CamLog.d(TAG, "Camcorder(MediaRecorder) init()-end");
        return mMediaRecorder;
    }

    public static boolean start() {
        CamLog.d(TAG, "Camcorder start()-start");
        if (mIsInitialized) {
            try {
                CamLog.d(TAG, "##### video recording start - mMediaRecorder.start()");
                mMediaRecorder.start();
                mIsRecording = true;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                CamLog.e(TAG, "error recording start : +" + e.toString());
                mIsRecording = false;
            } catch (Exception e2) {
                e2.printStackTrace();
                CamLog.e(TAG, "error recording start");
                mIsRecording = false;
            }
        }
        CamLog.d(TAG, "Camcorder start()-end, return " + mIsRecording);
        return mIsRecording;
    }

    public static boolean stop() throws Exception {
        boolean z;
        CamLog.d(TAG, "Camcorder stop()-start");
        if (mIsRecording) {
            CamLog.d(TAG, "##### video recording stop - mMediaRecorder.stop()");
            mMediaRecorder.setOnInfoListener(null);
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.stop();
            mIsRecording = false;
            mMediaRecorder.reset();
            mIsInitialized = false;
            mVideoFile.clearEmptyFile();
        }
        String str = TAG;
        StringBuilder append = new StringBuilder().append("Camcorder stop()-end, return ");
        if (mIsRecording) {
            z = false;
        } else {
            z = true;
        }
        CamLog.d(str, append.append(z).toString());
        if (mIsRecording) {
            return false;
        }
        return true;
    }

    public static void pause() {
        CamLog.d(TAG, "Camcorder pause()-start");
        if (MultimediaProperties.isPauseAndResumeSupported()) {
            if (mIsInitialized) {
                try {
                    CamLog.d(TAG, "##### video recording start - mMediaRecorder.pause()");
                    mMediaRecorder.pause();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    CamLog.e(TAG, "error recording pause");
                    if (mVideoFile != null) {
                        mVideoFile.clearEmptyFile();
                    }
                    mMediaRecorder.release();
                }
            }
            CamLog.d(TAG, "Camcorder pause()-end.");
            return;
        }
        CamLog.i(TAG, "Model not supported pause and resume.");
    }

    public static void resume() {
        CamLog.d(TAG, "Camcorder resume()-start");
        if (MultimediaProperties.isPauseAndResumeSupported()) {
            if (mIsInitialized) {
                try {
                    CamLog.d(TAG, "##### video recording resume - mMediaRecorder.resume()");
                    mMediaRecorder.resume();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    CamLog.e(TAG, "error recording pause");
                    if (mVideoFile != null) {
                        mVideoFile.clearEmptyFile();
                    }
                    mMediaRecorder.release();
                }
            }
            CamLog.d(TAG, "Camcorder resume()-end.");
            return;
        }
        CamLog.i(TAG, "Model not supported pause and resume.");
    }

    public static synchronized void release() {
        Camera camera = null;
        synchronized (VideoRecorder.class) {
            CamLog.d(TAG, "Camcorder release()-start");
            if (mMediaRecorder != null) {
                if (mIsRecording) {
                    try {
                        stop();
                    } catch (Exception e) {
                        CamLog.d(TAG, "[VideoRecorder::release()] stop Exception !!");
                        e.printStackTrace();
                        if (mVideoFile != null) {
                            CamLog.e(TAG, "[VideoRecorder::release()] videoFile delete !!");
                            mVideoFile.deleteFile();
                        }
                    }
                } else {
                    try {
                        mVideoFile.clearEmptyFile();
                    } catch (Exception e2) {
                        CamLog.d(TAG, "[VideoRecorder::release()] stop Exception !!");
                        e2.printStackTrace();
                    }
                }
                CamLog.d(TAG, "Release MediaRecorder start");
                mMediaRecorder.release();
                mMediaRecorder = null;
                CamLog.d(TAG, "Release MediaRecorder end");
                CamLog.d(TAG, "UNLOCK CAMERA");
                if (mCameraDevice != null) {
                    camera = (Camera) mCameraDevice.get();
                }
                if (camera != null) {
                    try {
                        camera.lock();
                        CamLog.d(TAG, "### mCameraDevice.reconnect()");
                        camera.reconnect();
                    } catch (RuntimeException e3) {
                        CamLog.d(TAG, "mCameraDevice.get().lock() or reconnect() RuntimeException: " + e3);
                        e3.printStackTrace();
                    } catch (IOException e4) {
                        CamLog.d(TAG, "reconnect failed: " + e4);
                        e4.printStackTrace();
                    }
                }
                CamLog.d(TAG, "camera reconnected");
                mCameraDevice = null;
            }
            mIsInitialized = false;
            CamLog.d(TAG, "Camcorder release()-end");
        }
    }

    public static void setOrientationHint(int degree) {
        if (degree < 0) {
            degree = NORMAL;
        }
        mOrientationHint = degree;
    }

    public static int getOrientationHint() {
        return mOrientationHint;
    }

    public static long getMaxFileSize() {
        return mMaxFileSize;
    }

    public static void setAudiozoommetadata() {
        if (mMediaRecorder != null) {
            mMediaRecorder.setAudioZooming();
        }
    }

    public static void setAudiozoom(int zoomAngle, int mode) {
        if (mMediaRecorder != null) {
            mMediaRecorder.setRecordZoomEnable(zoomAngle, mode);
        }
    }

    public static void updateAudiozoom(int zoomAngle) {
        if (mMediaRecorder != null) {
            mMediaRecorder.setRecordAngle(zoomAngle);
        }
    }

    public static void setAudiozoomException() {
        if (mMediaRecorder != null) {
            mMediaRecorder.setAudioZoomExceptionCase();
        }
    }

    public static int getAudioZoomLevelMeter() {
        if (mMediaRecorder != null) {
            return (int) (((double) mMediaRecorder.getMaxAmplitude()) * 0.003052d);
        }
        return NORMAL;
    }

    public static void changeMaxFileSize(long size) {
        CamLog.d(TAG, "changeMaxFileSize START");
        if (mMediaRecorder != null && mIsRecording) {
            try {
                CamLog.d(TAG, "changeMaxFileSize DO");
                mMediaRecorder.changeMaxFileSize(size);
            } catch (NoSuchMethodError e) {
                CamLog.d(TAG, "Catch Exception : " + e);
            } catch (IllegalStateException e2) {
                CamLog.d(TAG, "Catch IllegalStateException " + e2);
            }
        }
        CamLog.d(TAG, "changeMaxFileSize END");
    }
}
