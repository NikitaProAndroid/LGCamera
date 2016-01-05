package com.lge.camera.controller.camcorder;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.VideoFile;
import com.lge.camera.VideoRecorder;
import com.lge.camera.command.Command;
import com.lge.camera.components.RecProgressBar;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.controller.Controller;
import com.lge.camera.listeners.MediaRecorderListener;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.MmsProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.AppControlUtil;
import com.lge.camera.util.AudioUtil;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CheckStatusManager;
import com.lge.camera.util.Common;
import com.lge.camera.util.FileNamer;
import com.lge.camera.util.SecureImageUtil;
import com.lge.camera.util.SharedPreferenceUtil;
import com.lge.camera.util.Util;
import com.lge.morpho.app.morphopanorama.PanoramaApplication;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.FaceDetector;
import java.io.File;
import java.util.concurrent.CountDownLatch;

public class RecordingController extends Controller {
    private boolean mCheckResumeVideo;
    private long mEndTime;
    private boolean mIsFileSizeLimitReached;
    private boolean mIsStopRecordingByMountedAction;
    private CountDownLatch mLatch;
    private long mPauseTime;
    public int mRecIndicatorHeight;
    public int mRecIndicatorLeftMargin;
    public int mRecIndicatorWidth;
    private long mRecordingDurationLimit;
    private long mRecordingSizeLimit;
    public float mScaleWidthHeight;
    private long mStartTime;
    private boolean mStopRecordingDuringCall;
    private Thread mStopRecordingThread;
    private VideoFile mVideoFile;
    private long mVideoFileSize;

    public long getStartTime() {
        return this.mStartTime;
    }

    public long getEndTime() {
        return this.mEndTime;
    }

    public void setEndTime(long endTime) {
        this.mEndTime = endTime;
    }

    public RecordingController(ControllerFunction function) {
        super(function);
        this.mStartTime = 0;
        this.mEndTime = 0;
        this.mPauseTime = 0;
        this.mRecIndicatorWidth = -1;
        this.mRecIndicatorHeight = -1;
        this.mRecIndicatorLeftMargin = 0;
        this.mScaleWidthHeight = 0.2f;
        this.mStopRecordingDuringCall = false;
        this.mCheckResumeVideo = true;
        this.mIsStopRecordingByMountedAction = false;
    }

    public void initController() {
        this.mGet.inflateStub(R.id.stub_rec_indicator);
        this.mStartTime = 0;
        this.mEndTime = 0;
        ((TextView) this.mGet.findViewById(R.id.text_rec_time)).setTypeface(Typeface.DEFAULT);
        int currentCarrierCode = ModelProperties.getCarrierCode();
        RecProgressBar pb = (RecProgressBar) this.mGet.findViewById(R.id.progress_rec_time);
        LayoutParams lp = pb.getLayoutParams();
        pb.initRecProgressBar(lp.width, lp.height);
        if (needProgressBar()) {
            this.mGet.findViewById(R.id.progress_rec_time_parent).setVisibility(0);
        } else {
            this.mGet.findViewById(R.id.progress_rec_time_parent).setVisibility(4);
        }
        if (!this.mGet.isMMSRecording()) {
            this.mScaleWidthHeight = 0.4f;
        } else if (currentCarrierCode == 6) {
            this.mScaleWidthHeight = 0.3f;
        } else {
            this.mScaleWidthHeight = 0.4f;
        }
        this.mInit = true;
        setRecLayout();
        startRotation(this.mGet.getOrientationDegree());
    }

    public void setRecIndicatorLayout(int width, int height, int leftMargin) {
        this.mRecIndicatorWidth = width;
        this.mRecIndicatorHeight = height;
        this.mRecIndicatorLeftMargin = leftMargin;
        if (this.mInit) {
            setRecLayout();
        }
    }

    public void setRecLayout() {
        if (this.mInit) {
            View recAreaLayout = this.mGet.findViewById(R.id.rec_indicator_preview);
            int paddingLeft = getPixelFromDimens(R.dimen.text_rec_time_paddingLeft);
            int paddingTop = getPixelFromDimens(R.dimen.text_rec_time_paddingTop);
            if (this.mGet.getOrientation() == 0 || this.mGet.getOrientation() == 2) {
                recAreaLayout.setPaddingRelative(paddingLeft, paddingTop, 0, 0);
            } else {
                recAreaLayout.setPaddingRelative(paddingTop, 0, 0, 0);
            }
            if (recAreaLayout != null) {
                RelativeLayout.LayoutParams recIndicatorArea = (RelativeLayout.LayoutParams) recAreaLayout.getLayoutParams();
                Common.resetLayoutParameter(recIndicatorArea);
                if (this.mGet.isConfigureLandscape()) {
                    recIndicatorArea.width = this.mRecIndicatorWidth;
                    recIndicatorArea.height = this.mRecIndicatorHeight;
                    recIndicatorArea.leftMargin = this.mRecIndicatorLeftMargin;
                    if (this.mRecIndicatorLeftMargin == 0) {
                        recIndicatorArea.addRule(20, 1);
                        recIndicatorArea.addRule(15, 1);
                    } else {
                        recIndicatorArea.addRule(15, 1);
                    }
                } else {
                    recIndicatorArea.height = this.mRecIndicatorWidth;
                    recIndicatorArea.width = this.mRecIndicatorHeight;
                    recIndicatorArea.topMargin = this.mRecIndicatorLeftMargin;
                    if (this.mRecIndicatorLeftMargin == 0) {
                        recIndicatorArea.addRule(10, 1);
                        recIndicatorArea.addRule(14, 1);
                    } else {
                        recIndicatorArea.addRule(14, 1);
                    }
                }
                recAreaLayout.setLayoutParams(recIndicatorArea);
                return;
            }
            return;
        }
        CamLog.d(FaceDetector.TAG, "RecordingController is not initialize.");
    }

    public void initVideoFile(int purpose) {
        String fileDirectory = this.mGet.getCurrentStorageDirectory();
        String fileName = FileNamer.get().getFileNewName(this.mGet.getApplicationContext(), 1, this.mGet.getCurrentStorage(), this.mGet.getCurrentStorageDirectory(), true, this.mGet.getSettingValue(Setting.KEY_CAMERA_SHOT_MODE), false);
        if (this.mGet.getApplicationContext() != null && fileDirectory != null && fileName != null) {
            this.mVideoFile = new VideoFile(this.mGet.getApplicationContext(), fileDirectory, fileName, purpose);
        }
    }

    public void setVideoFile(VideoFile videoFile) {
        if (videoFile != null) {
            this.mVideoFile = videoFile;
        }
    }

    public VideoFile getVideoFile() {
        return this.mVideoFile;
    }

    public void setStopRecordingDuringCall(boolean value) {
        this.mStopRecordingDuringCall = value;
    }

    public boolean getStopRecordingDuringCall() {
        return this.mStopRecordingDuringCall;
    }

    public boolean needProgressBar() {
        if (this.mGet.isEffectsCamcorderActive() || !this.mGet.isAttachMode()) {
            return false;
        }
        if (this.mGet.isAttachIntent() && this.mGet.getRequestedVideoSizeLimit() == 0 && this.mGet.getMaxVideoDurationInMs() == 0) {
            return false;
        }
        return true;
    }

    public void show() {
        if (this.mInit && this.mGet.getSubMenuMode() != 5 && this.mGet.getSubMenuMode() != 16) {
            if (!this.mGet.isAttachMode()) {
                this.mGet.findViewById(R.id.progress_rec_time_parent).setVisibility(4);
            } else if (needProgressBar()) {
                this.mGet.findViewById(R.id.progress_rec_time_parent).setVisibility(0);
            } else {
                this.mGet.findViewById(R.id.progress_rec_time_parent).setVisibility(4);
            }
            if (this.mGet.getVideoState() == 3 || this.mGet.getVideoState() == 4) {
                this.mGet.findViewById(R.id.rec_time_indicator).setVisibility(0);
                if (this.mGet.checkSlowMotionMode()) {
                    this.mGet.findViewById(R.id.rec_slow_motion).setVisibility(0);
                }
            }
        }
    }

    public void hide() {
        if (this.mInit) {
            if (this.mGet.findViewById(R.id.progress_rec_time_parent) != null) {
                this.mGet.findViewById(R.id.progress_rec_time_parent).setVisibility(4);
            }
            if (this.mGet.findViewById(R.id.rec_time_indicator) != null) {
                this.mGet.findViewById(R.id.rec_time_indicator).setVisibility(4);
            }
            if (this.mGet.checkSlowMotionMode() && this.mGet.findViewById(R.id.rec_slow_motion) != null) {
                this.mGet.findViewById(R.id.rec_slow_motion).setVisibility(4);
            }
        }
    }

    public void hideTimeIndicator() {
        if (this.mInit) {
            if (this.mGet.findViewById(R.id.rec_time_indicator) != null) {
                this.mGet.findViewById(R.id.rec_time_indicator).setVisibility(4);
            }
            if (this.mGet.checkSlowMotionMode() && this.mGet.findViewById(R.id.rec_slow_motion) != null) {
                this.mGet.findViewById(R.id.rec_slow_motion).setVisibility(4);
            }
        }
    }

    public long getRecordingSizeLimit() {
        return this.mRecordingSizeLimit;
    }

    public long getRecordingDurationLimit() {
        return this.mRecordingDurationLimit;
    }

    public long getVideoFileSize() {
        return this.mVideoFileSize;
    }

    public void setVideoFileSize(long size) {
        this.mVideoFileSize = size;
    }

    public void startRecording() {
        this.mGet.showDefaultQuickButton(false);
        boolean bInitRecording = false;
        AppControlUtil.StopVoiceRec(this.mGet.getActivity(), this.mGet.getApplicationMode());
        AudioUtil.setStopNotificationStream(this.mGet.getApplicationContext());
        setStopRecordingDuringCall(false);
        this.mLatch = new CountDownLatch(1);
        setVideoSize();
        CamLog.d(FaceDetector.TAG, "RecordingSizeLimit : " + this.mRecordingSizeLimit + ", mRecordingDurationLimit:" + this.mRecordingDurationLimit);
        VideoRecorder.setMaxFileSize(this.mRecordingSizeLimit, this.mGet.getFreeSpace(), 0);
        VideoRecorder.setOrientationHint(this.mGet.getDeviceDegree());
        this.mGet.setShowCameraErrorPopup(false);
        this.mRecordingDurationLimit = getRecordingDurationLimit() == 0 ? 7200000 : getRecordingDurationLimit();
        if (this.mGet.isEffectsCamcorderActive()) {
            if (this.mVideoFile != null) {
                this.mGet.initializeRecordingEffect(this.mVideoFile.getFilePath(), this.mRecordingSizeLimit, (int) this.mRecordingDurationLimit, this.mGet.getFreeSpace());
                bInitRecording = true;
            }
        } else if (!this.mGet.isDualRecordingActive()) {
            VideoRecorder.init(this.mGet);
            bInitRecording = VideoRecorder.isInitialized();
            MediaRecorderListener listener = new MediaRecorderListener(this.mGet);
            VideoRecorder.setInfoListener(listener);
            VideoRecorder.setErrorListener(listener);
        } else if (this.mVideoFile != null) {
            this.mGet.initializeRecordingDual(this.mVideoFile.getFilePath(), this.mRecordingSizeLimit, (int) this.mRecordingDurationLimit, this.mGet.getFreeSpace());
            bInitRecording = true;
        }
        if (bInitRecording) {
            if (FunctionProperties.isSupportAudiozoom()) {
                AudioUtil.setAudiodevice(this.mGet.getApplicationContext(), this.mGet.getOrientation());
            }
            callStartRecording();
            if (FunctionProperties.isSupportAudiozoom() && this.mVideoFile.getAudiozoomcontent() == 1) {
                this.mVideoFile.setAudiozoomcontent(0);
            }
            if (FunctionProperties.isSupportAudiozoom() && this.mGet.getVideoState() == 3) {
                getVideoFile().setAudiozoomExection_state(false);
                boolean isAudiozoom_exection = this.mGet.isAudiozoom_ExceptionCase(true);
                this.mGet.setStartrecordingdegree(this.mGet.getOrientationDegree());
                if (this.mGet.getCameraId() != 0 || !CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_CAMCORDER_AUDIOZOOM))) {
                    this.mGet.setAudiozoomStartInRecording(false);
                } else if (!isAudiozoom_exection) {
                    this.mGet.startAudiozoom();
                    this.mGet.setAudiozoomStartInRecording(true);
                }
            }
        } else {
            restoreToIdle();
            this.mLatch.countDown();
        }
        startHeatingWarning();
    }

    private boolean isUHDmode() {
        if ("3840x2160".equalsIgnoreCase(this.mGet.getSettingValue(Setting.KEY_PREVIEW_SIZE_ON_DEVICE))) {
            return true;
        }
        return false;
    }

    private void callStartRecording() {
        int compensationTime = 0;
        boolean videoRecordingStartRetval = true;
        try {
            if (AudioUtil.isAudioRecording()) {
                videoRecordingStartRetval = false;
                this.mGet.toastMiddleLong(this.mGet.getString(R.string.error_cannot_start_recording_with_audio));
            } else if (this.mGet.isEffectsCamcorderActive()) {
                this.mGet.startRecordingEffect();
            } else {
                videoRecordingStartRetval = VideoRecorder.start();
                CamLog.d(FaceDetector.TAG, "videoRecordingStartRetval = " + videoRecordingStartRetval);
            }
            if (videoRecordingStartRetval) {
                AudioUtil.setStreamMute(this.mGet.getApplicationContext(), true);
                AudioUtil.setVibrationMute(this.mGet.getApplicationContext(), true);
                this.mGet.setVideoState(3);
                this.mGet.setCurrentRecordingTime(0);
                show();
                if (!(!FunctionProperties.isAvailableLiveShot() || this.mGet.isEffectsCamcorderActive() || this.mGet.isAttachMode() || !videoRecordingStartRetval || this.mGet.isDualRecordingActive() || isUHDmode())) {
                    this.mGet.doCommandUi(Command.SHOW_LIVE_SNAPSHOT_BUTTON);
                }
                if (MultimediaProperties.isPauseAndResumeSupported()) {
                    this.mGet.showRecoridngStopButton();
                }
                if (this.mGet.isEffectsCamcorderActive() || this.mGet.isDualRecordingActive()) {
                    compensationTime = PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME;
                }
                long uptimeMillis = SystemClock.uptimeMillis() + ((long) compensationTime);
                this.mStartTime = uptimeMillis;
                this.mEndTime = uptimeMillis;
                this.mGet.doCommandDelayed(Command.UPDATE_RECORDING_TIME, (long) compensationTime);
                ((RecProgressBar) this.mGet.findViewById(R.id.progress_rec_time)).setProgress(0);
                setQuickButton(true, this.mGet.getOrientationDegree());
                if (ProjectVariables.isUseFlashTemperature() && CameraConstants.FLASH_TORCH.equals(this.mGet.getParameters().getFlashMode()) && CheckStatusManager.GetXo_thermal() >= CheckStatusManager.TEMPERATURE_FLASH_RECORDING_STANDARD) {
                    this.mGet.setSetting(Setting.KEY_FLASH, CameraConstants.SMART_MODE_OFF);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("fromQuickButton", true);
                    this.mGet.doCommand(Command.CAMERA_FLASH_MODE, null, bundle);
                    this.mGet.toast((int) R.string.warning_high_temp_action_flash_off);
                    this.mGet.setFlashOffByHighTemperature(true);
                    CamLog.v(FaceDetector.TAG, "flash off by callStartRecording");
                }
            } else {
                VideoRecorder.release();
                restoreToIdle();
                AudioUtil.setStreamMute(this.mGet.getApplicationContext(), false);
                AudioUtil.setVibrationMute(this.mGet.getApplicationContext(), false);
                this.mGet.setVideoFlash(false);
            }
            this.mLatch.countDown();
            if (!videoRecordingStartRetval) {
                VideoRecorder.release();
                restoreToIdle();
                CamLog.e(FaceDetector.TAG, "Could not start media recorder");
            }
        } catch (RuntimeException e) {
            VideoRecorder.release();
            restoreToIdle();
            AudioUtil.setStreamMute(this.mGet.getApplicationContext(), false);
            AudioUtil.setVibrationMute(this.mGet.getApplicationContext(), false);
            CamLog.e(FaceDetector.TAG, "Could not start media recorder = " + e);
            e.printStackTrace();
            this.mLatch.countDown();
            if (!videoRecordingStartRetval) {
                VideoRecorder.release();
                restoreToIdle();
                CamLog.e(FaceDetector.TAG, "Could not start media recorder");
            }
        } catch (Exception e2) {
            VideoRecorder.release();
            restoreToIdle();
            AudioUtil.setStreamMute(this.mGet.getApplicationContext(), false);
            AudioUtil.setVibrationMute(this.mGet.getApplicationContext(), false);
            CamLog.e(FaceDetector.TAG, e2.toString());
            e2.printStackTrace();
            this.mLatch.countDown();
            if (!videoRecordingStartRetval) {
                VideoRecorder.release();
                restoreToIdle();
                CamLog.e(FaceDetector.TAG, "Could not start media recorder");
            }
        } catch (Throwable th) {
            this.mLatch.countDown();
            if (!videoRecordingStartRetval) {
                VideoRecorder.release();
                restoreToIdle();
                CamLog.e(FaceDetector.TAG, "Could not start media recorder");
            }
        }
    }

    private void setVideoSize() {
        if (this.mGet.isMMSRecording()) {
            initVideoFile(1);
            this.mRecordingSizeLimit = this.mGet.getRequestedVideoSizeLimit();
            this.mRecordingDurationLimit = (long) this.mGet.getMaxVideoDurationInMs();
            if (this.mRecordingSizeLimit == 0 && !this.mGet.isAttachIntent()) {
                this.mRecordingSizeLimit = MmsProperties.getMmsVideoSizeLimit(this.mGet.getContentResolver());
            }
            VideoRecorder.setVideoSize(1);
            return;
        }
        initVideoFile(0);
        if (this.mGet.isAttachIntent()) {
            this.mRecordingSizeLimit = this.mGet.getRequestedVideoSizeLimit();
            this.mRecordingDurationLimit = (long) this.mGet.getMaxVideoDurationInMs();
            if (this.mRecordingSizeLimit == 0 && !this.mGet.isAttachIntent()) {
                this.mRecordingSizeLimit = MmsProperties.getMmsVideoSizeLimit(this.mGet.getContentResolver());
            }
        } else {
            this.mRecordingSizeLimit = 0;
        }
        VideoRecorder.setVideoSize(0);
    }

    private void startHeatingWarning() {
        if (!ProjectVariables.isSupportHeat_detection()) {
            return;
        }
        if (this.mGet.getVideoState() == 3 || this.mGet.getVideoState() == 4) {
            String RecordingSize = this.mGet.getPreviewSizeOnDevice();
            if (RecordingSize == null) {
                CamLog.d(FaceDetector.TAG, "Recording Size reference NULL Value, please CHECK 'getPreviewSizeOnDevice() function~!!!' ");
                return;
            }
            CamLog.i(FaceDetector.TAG, "Recording Size :" + RecordingSize);
            if (Common.IsHeatingVideoSize(RecordingSize) && this.mGet.getIsCharging()) {
                this.mGet.startHeatingwarning();
            }
        }
    }

    private void restoreToIdle() {
        CamLog.d(FaceDetector.TAG, "startRecording is NOT started. Restore environment to idle.");
        this.mGet.setVideoState(0);
        setQuickButton(false, this.mGet.getOrientationDegree());
        this.mGet.postOnUiThread(new Runnable() {
            public void run() {
                if (RecordingController.this.mGet.isAttachIntent()) {
                    RecordingController.this.mGet.setSwitcherVisible(false);
                } else {
                    RecordingController.this.mGet.setSwitcherVisible(true);
                }
                if (ModelProperties.is3dSupportedModel()) {
                    RecordingController.this.mGet.set3DSwitchVisible(true);
                }
                RecordingController.this.mGet.updateThumbnailButtonVisibility();
                RecordingController.this.mGet.showQuickFunctionController();
                RecordingController.this.mGet.quickFunctionControllerRefresh(true);
                RecordingController.this.mGet.showDefaultQuickButton(true);
                if (FunctionProperties.isAvailableLiveShot()) {
                    RecordingController.this.mGet.doCommandUi(Command.HIDE_LIVE_SNAPSHOT_BUTTON);
                }
                RecordingController.this.mGet.enableCommand(true);
                RecordingController.this.mGet.setMainButtonEnable();
                RecordingController.this.hideTimeIndicator();
            }
        });
    }

    public void waitStartRecordingThreadDone() {
        if (this.mLatch != null) {
            try {
                CamLog.d(FaceDetector.TAG, "Wait for start recording done..");
                this.mLatch.await();
                CamLog.d(FaceDetector.TAG, "Start recording done.");
            } catch (InterruptedException e) {
                CamLog.e(FaceDetector.TAG, "Failed to wait for start recording done!");
                e.printStackTrace();
            }
        }
    }

    public void pauseRecording() {
        if (this.mGet.getVideoState() == 3) {
            this.mGet.setVideoState(4);
            this.mPauseTime = SystemClock.uptimeMillis();
            if (FunctionProperties.isSupportAudiozoom()) {
                if (this.mGet.getAudiozoomStart()) {
                    this.mGet.stopAudiozoom();
                }
                this.mGet.setAudiozoombuttonstate();
            }
            if (this.mGet.isEffectsCamcorderActive() || this.mGet.isDualRecordingActive()) {
                this.mGet.pauseAndResumeRecording(true);
            } else {
                VideoRecorder.pause();
            }
            this.mCheckResumeVideo = false;
            this.mGet.setVideoFlash(false);
        }
    }

    public void resumeRecording() {
        if (this.mGet.getVideoState() == 4) {
            this.mGet.setVideoFlash(true);
            this.mGet.setVideoState(3);
            if (this.mGet.isEffectsCamcorderActive() || this.mGet.isDualRecordingActive()) {
                this.mGet.pauseAndResumeRecording(false);
            } else {
                VideoRecorder.resume();
            }
            if (FunctionProperties.isSupportAudiozoom()) {
                if (!this.mGet.isAudiozoom_ExceptionCase(true) && this.mGet.getAudiozoomStartInRecording()) {
                    this.mGet.startAudiozoom();
                }
                this.mGet.setAudiozoombuttonstate();
            }
            if (ProjectVariables.isUseFlashTemperature() && CameraConstants.FLASH_TORCH.equals(this.mGet.getParameters().getFlashMode()) && CheckStatusManager.GetXo_thermal() >= CheckStatusManager.TEMPERATURE_FLASH_RECORDING_STANDARD) {
                this.mGet.setSetting(Setting.KEY_FLASH, CameraConstants.SMART_MODE_OFF);
                Bundle bundle = new Bundle();
                bundle.putBoolean("fromQuickButton", true);
                this.mGet.doCommand(Command.CAMERA_FLASH_MODE, null, bundle);
                this.mGet.toast((int) R.string.warning_high_temp_action_flash_off);
                this.mGet.setFlashOffByHighTemperature(true);
                this.mGet.setButtonRemainEnabled(10, false, true);
                CamLog.v(FaceDetector.TAG, "flash off by callStartRecording");
            }
        }
    }

    public void resumeUpdateReordingTime() {
        this.mStartTime += SystemClock.uptimeMillis() - this.mPauseTime;
        this.mPauseTime = 0;
        this.mGet.doCommandUi(Command.UPDATE_RECORDING_TIME);
        this.mCheckResumeVideo = true;
    }

    public boolean isAvailableResumeVideo() {
        return this.mCheckResumeVideo;
    }

    public void setRecDurationTime(long startTime, long endTime) {
        if (MultimediaProperties.isPauseAndResumeSupported() && this.mPauseTime > 0) {
            int compensationTime = (this.mGet.isEffectsCamcorderActive() || this.mGet.isDualRecordingActive()) ? 0 : CameraConstants.TIME_MACHINE_ANI_INTERVAL;
            startTime += (endTime - this.mPauseTime) + ((long) compensationTime);
            this.mPauseTime = 0;
            if (startTime < 0) {
                startTime = 0;
            }
        }
        this.mVideoFile.setRecordingTime_duration(endTime - startTime);
    }

    public boolean isStopRecordingByMountedAction() {
        return this.mIsStopRecordingByMountedAction;
    }

    public void stopRecording() {
        stopRecording(false);
    }

    public void stopRecording(boolean isFromMountedAction) {
        this.mIsStopRecordingByMountedAction = isFromMountedAction;
        AppControlUtil.UnblockAlarmInRecording(this.mGet.getActivity());
        hideTimeIndicator();
        setQuickButton(false, this.mGet.getOrientationDegree());
        if (this.mGet.getAudiozoomStart()) {
            this.mGet.stopAudiozoom();
        }
        if (FunctionProperties.isSupportAudiozoom() && this.mGet.getHeadsetstate() != 2) {
            AudioUtil.setAudiodevice(this.mGet.getApplicationContext(), 4);
        }
        if (FunctionProperties.isSupportAudiozoom()) {
            this.mGet.setStartrecordingdegree(Util.isConfigureLandscape(this.mGet.getResources()) ? 0 : Tag.IMAGE_DESCRIPTION);
            this.mGet.setForced_audiozoom(true);
            if (getVideoFile().getAudiozoomExection_state()) {
                VideoRecorder.setAudiozoomException();
            }
        }
        this.mStopRecordingThread = new Thread(new Runnable() {
            public void run() {
                RecordingController.this.mEndTime = SystemClock.uptimeMillis();
                RecordingController.this.setRecDurationTime(RecordingController.this.mStartTime, RecordingController.this.mEndTime);
                if (RecordingController.this.mGet.isEffectsCamcorderActive()) {
                    RecordingController.this.mGet.stopRecordingEffect();
                    RecordingController.this.mGet.waitStopRecordingEffectThreadDone();
                } else {
                    VideoRecorder.release();
                    if (!RecordingController.this.getStopRecordingDuringCall()) {
                        RecordingController.this.mGet.playRecordingSound(false);
                    }
                }
                AudioUtil.setStreamMute(RecordingController.this.mGet.getApplicationContext(), false);
                AudioUtil.setVibrationMute(RecordingController.this.mGet.getApplicationContext(), false);
                RecordingController.this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        RecordingController.this.mGet.removePostRunnable(this);
                        RecordingController.this.doAfterRecordingProcess();
                        RecordingController.this.mGet.setVideoFlash(false);
                    }
                });
                RecordingController.this.mCheckResumeVideo = true;
                RecordingController.this.mGet.setInCaptureProgress(false);
                RecordingController.this.mGet.setFlashOffByHighTemperature(false);
            }
        });
        this.mStopRecordingThread.start();
        if (CheckStatusManager.useBackLightControlInRecording()) {
            Common.backlightControl(this.mGet.getActivity());
        }
        if (!this.mGet.isAttachMode()) {
            this.mGet.doCommandUi(Command.HIDE_LIVE_SNAPSHOT_BUTTON);
        }
        if (MultimediaProperties.isPauseAndResumeSupported()) {
            this.mGet.hideRecoridngStopButton();
        }
        if (ProjectVariables.isSupportHeat_detection()) {
            this.mGet.stopHeatingwarning();
        }
        if (!(this.mGet.getObjectTrackingState() == 0 || this.mGet.isSmartZoomRecordingActive())) {
            this.mGet.startObjectTrackingFocus(0, 0, 0, 0, 0);
        }
        AudioUtil.setAudioFocus(this.mGet.getApplicationContext(), false);
    }

    public void waitStopRecordingThreadDone() {
        try {
            if (this.mStopRecordingThread != null && this.mStopRecordingThread.isAlive()) {
                CamLog.d(FaceDetector.TAG, "Wait for stop recording done..");
                this.mStopRecordingThread.join();
                this.mStopRecordingThread = null;
                this.mIsStopRecordingByMountedAction = false;
                CamLog.d(FaceDetector.TAG, "Stop recording done..");
            }
        } catch (InterruptedException e) {
            CamLog.e(FaceDetector.TAG, "Failed to join stop recording thread!");
            e.printStackTrace();
        }
    }

    public void doAfterRecordingProcess() {
        String videoSizeString = this.mGet.getPreviewSizeOnDevice();
        if (CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE))) {
            videoSizeString = MultimediaProperties.getLiveeffectResolutions(this.mGet.getCameraMode());
        } else if (ModelProperties.isUVGAmodel() && (CameraConstants.TYPE_RECORDMODE_DUAL.equals(this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE)) || CameraConstants.TYPE_RECORDMODE_SMART_ZOOM.equals(this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE)))) {
            videoSizeString = MultimediaProperties.EFFECTS_ENFORCED_SIZE_FOR_UVGA;
        }
        Uri savedUri = setSaveUri(videoSizeString);
        CamLog.d(FaceDetector.TAG, "doAfterRecordingProcess-start");
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                if (RecordingController.this.checkMediator()) {
                    RecordingController.this.mGet.removePostRunnable(this);
                    RecordingController.this.resetRecTime();
                    RecordingController.this.mGet.setSwitcherVisible(true);
                    RecordingController.this.mGet.updateThumbnailButtonVisibility();
                }
            }
        });
        CamLog.d(FaceDetector.TAG, "mIsStopRecordingByMountedAction=" + this.mIsStopRecordingByMountedAction);
        if (savedUri == null || this.mIsStopRecordingByMountedAction || (!this.mGet.isAttachIntent() && (!CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_VIDEO_AUTO_REVIEW)) || this.mGet.getBackKeyRecStop()))) {
            final Uri savedUriForThread = savedUri;
            new Thread(new Runnable() {
                public void run() {
                    if (RecordingController.this.checkMediator()) {
                        try {
                            if (!RecordingController.this.mGet.isPausing() && !RecordingController.this.mGet.isFinishingActivity()) {
                                RecordingController.this.mGet.setLastThumb(savedUriForThread, false);
                                RecordingController.this.mGet.doCommandUi(Command.ON_DELAY_OFF);
                                RecordingController.this.mGet.setBackKeyRecStop(false);
                                if (RecordingController.this.mIsStopRecordingByMountedAction) {
                                    CamLog.d(FaceDetector.TAG, "do not show quick post view (2sec, 5sec)");
                                    return;
                                }
                                long duration;
                                long autoReviewDuration;
                                String autoReview = RecordingController.this.mGet.getSettingValue(Setting.KEY_VIDEO_AUTO_REVIEW);
                                if ("on_delay_2sec".equals(autoReview)) {
                                    duration = CameraConstants.TOAST_LENGTH_SHORT;
                                } else {
                                    duration = 0;
                                }
                                if ("on_delay_5sec".equals(autoReview)) {
                                    autoReviewDuration = CameraConstants.TOAST_LENGTH_LONG;
                                } else {
                                    autoReviewDuration = duration;
                                }
                                if (autoReviewDuration != 0) {
                                    RecordingController.this.mGet.enableInput(false);
                                    RecordingController.this.mGet.postOnUiThread(new Runnable() {
                                        public void run() {
                                            if (RecordingController.this.mGet != null) {
                                                RecordingController.this.mGet.removePostRunnable(this);
                                                RecordingController.this.mGet.showGalleryQuickViewWindow(false, autoReviewDuration);
                                            }
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            CamLog.e(FaceDetector.TAG, "get Video Thumbnail error : ", e);
                        }
                    }
                }
            }).start();
        } else {
            if (ProjectVariables.isStopPreviewAfterRecordStop()) {
                this.mGet.stopPreview();
            }
            this.mGet.setVideoStateOnly(5);
            this.mGet.doCommandUi(Command.DISPLAY_CAMCORDER_POSTVIEW);
        }
        if (savedUri != null) {
            CamLog.d(FaceDetector.TAG, "saved uri: " + savedUri);
            SharedPreferenceUtil.saveLastVideo(this.mGet.getActivity(), savedUri);
        }
    }

    private Uri setSaveUri(String videoSizeString) {
        return checkMinTimeAndDeleteForSaveUri(videoSizeString, SystemClock.uptimeMillis() - this.mStartTime < ((long) MultimediaProperties.getMinRecordingTime()));
    }

    private Uri checkMinTimeAndDeleteForSaveUri(String videoSizeString, boolean condition) {
        Uri savedUri = null;
        if (condition) {
            File vFile = this.mVideoFile.getFile();
            if (vFile != null) {
                CamLog.d(FaceDetector.TAG, "checkMinTimeAndDeleteForSaveUri delete! condition = " + condition);
                vFile.delete();
            }
        } else if (this.mGet.checkFsWritable()) {
            if (this.mGet.isMMSRecording()) {
                savedUri = this.mVideoFile.registerUri(0, videoSizeString, this.mGet.getCurrentLocation(), true, 1);
            } else {
                savedUri = this.mVideoFile.registerUri(0, videoSizeString, this.mGet.getCurrentLocation(), true, 0);
            }
            addSecureImageList(savedUri);
        } else {
            CamLog.d(FaceDetector.TAG, "checkMinTimeAndDeleteForSaveUri delete! condition = " + condition);
            this.mVideoFile.deleteFile();
        }
        return savedUri;
    }

    public boolean getIsFileSizeLimitReached() {
        return this.mIsFileSizeLimitReached;
    }

    public void setIsFileSizeLimitReached(boolean set) {
        this.mIsFileSizeLimitReached = set;
    }

    public void startRotation(int degree) {
        if (this.mInit && this.mGet.isCamcorderRotation(true)) {
            View progressLayout = this.mGet.findViewById(R.id.progress_rec_time_layout);
            RotateLayout progressRotate = (RotateLayout) this.mGet.findViewById(R.id.progress_rec_time_rotate);
            if (progressLayout == null || progressRotate == null) {
                CamLog.d(FaceDetector.TAG, "cannot startRotation progress is null.");
                return;
            }
            progressRotate.rotateLayout(degree);
            setRecLayout();
            CamLog.d(FaceDetector.TAG, "ori = " + degree);
            RotateLayout recTimeText = (RotateLayout) this.mGet.findViewById(R.id.text_rec_time_rotate);
            RotateLayout recSlowMotion = (RotateLayout) this.mGet.findViewById(R.id.text_slow_motion_rotate);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) progressLayout.getLayoutParams();
            int margin_landscape = getPixelFromDimens(R.dimen.progress_rec_time_marginBottom_landscape);
            int margin_portrait = getPixelFromDimens(R.dimen.progress_rec_time_marginBottom_portrait);
            Common.resetLayoutParameter(params);
            progressLayout.setLayoutDirection(0);
            progressRotate.setLayoutDirection(0);
            recTimeText.setLayoutDirection(0);
            if (this.mGet.checkSlowMotionMode() && recSlowMotion != null) {
                recSlowMotion.setLayoutDirection(0);
                recSlowMotion.setAngle(degree);
            }
            if (Util.isEqualDegree(this.mGet.getResources(), degree, 0)) {
                recTimeText.setAngle(degree);
                if (this.mGet.isConfigureLandscape()) {
                    params.addRule(12, 1);
                    params.addRule(14, 1);
                    params.setMargins(0, 0, 0, margin_landscape);
                } else {
                    params.addRule(20, 1);
                    params.addRule(15, 1);
                    params.setMargins(margin_landscape, 0, 0, 0);
                }
            } else if (Util.isEqualDegree(this.mGet.getResources(), degree, 90)) {
                recTimeText.setAngle(degree);
                if (this.mGet.isConfigureLandscape()) {
                    params.addRule(21, 1);
                    params.addRule(15, 1);
                    params.setMargins(0, 0, margin_portrait, 0);
                } else {
                    params.addRule(12, 1);
                    params.addRule(14, 1);
                    params.setMargins(0, 0, 0, margin_portrait);
                }
            } else if (Util.isEqualDegree(this.mGet.getResources(), degree, MediaProviderUtils.ROTATION_180)) {
                recTimeText.setAngle(degree);
                if (this.mGet.isConfigureLandscape()) {
                    params.addRule(12, 1);
                    params.addRule(14, 1);
                    params.setMargins(0, 0, 0, margin_landscape);
                } else {
                    params.addRule(20, 1);
                    params.addRule(15, 1);
                    params.setMargins(margin_landscape, 0, 0, 0);
                }
            } else {
                recTimeText.setAngle(degree);
                if (this.mGet.isConfigureLandscape()) {
                    params.addRule(21, 1);
                    params.addRule(15, 1);
                    params.setMargins(0, 0, margin_portrait, 0);
                } else {
                    params.addRule(12, 1);
                    params.addRule(14, 1);
                    params.setMargins(0, 0, 0, margin_portrait);
                }
            }
            progressLayout.setLayoutParams(params);
            return;
        }
        CamLog.d(FaceDetector.TAG, "RecordingController is not initialize.");
    }

    public void resetRecTime() {
        TextView recTimeText = (TextView) this.mGet.findViewById(R.id.text_rec_time);
        recTimeText.setTypeface(Typeface.DEFAULT);
        int currentCarrierCode = ModelProperties.getCarrierCode();
        if (this.mGet.isMMSRecording() && currentCarrierCode == 6) {
            recTimeText.setText("00/60");
        } else {
            recTimeText.setText(this.mGet.getString(R.string.video_rec_time));
        }
        ((RecProgressBar) this.mGet.findViewById(R.id.progress_rec_time)).setProgress(0);
        this.mIsFileSizeLimitReached = false;
    }

    public void stopRecordingByPausing() {
        CamLog.d(FaceDetector.TAG, "stopRecordingByPausing()");
        AppControlUtil.UnblockAlarmInRecording(this.mGet.getActivity());
        if (this.mGet.getAudiozoomStart()) {
            this.mGet.stopAudiozoom();
        }
        if (this.mGet.getVideoState() != 2) {
            this.mGet.setVideoState(2);
            this.mEndTime = SystemClock.uptimeMillis();
            setRecDurationTime(this.mStartTime, this.mEndTime);
            this.mStartTime = 0;
        }
        if (FunctionProperties.isSupportAudiozoom()) {
            this.mGet.setStartrecordingdegree(Util.isConfigureLandscape(this.mGet.getResources()) ? 0 : Tag.IMAGE_DESCRIPTION);
            this.mGet.setForced_audiozoom(true);
            if (getVideoFile().getAudiozoomExection_state()) {
                VideoRecorder.setAudiozoomException();
            }
        }
        hideTimeIndicator();
        setQuickButton(false, this.mGet.getOrientationDegree());
        this.mGet.showDefaultQuickButton(true);
        if (this.mGet.isEffectsCamcorderActive()) {
            this.mGet.stopRecordingEffect();
        } else {
            VideoRecorder.release();
            this.mCheckResumeVideo = true;
            this.mGet.playRecordingSound(false);
            if (this.mGet.getCameraDevice() != null) {
                this.mGet.getCameraDevice().stopPreview();
            }
        }
        AudioUtil.setStreamMute(this.mGet.getApplicationContext(), false);
        AudioUtil.setVibrationMute(this.mGet.getApplicationContext(), false);
        this.mGet.setInCaptureProgress(false);
        Uri savedUri = getSaveUri(this.mGet.getSettingValue(Setting.KEY_PREVIEW_SIZE_ON_DEVICE));
        this.mGet.checkStorage(false);
        this.mGet.setFlashOffByHighTemperature(false);
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                RecordingController.this.mGet.removePostRunnable(this);
                RecordingController.this.resetRecTime();
            }
        });
        if (savedUri != null) {
            CamLog.d(FaceDetector.TAG, "saved uri: " + savedUri);
            SharedPreferenceUtil.saveLastVideo(this.mGet.getActivity(), savedUri);
            if (CameraConstants.SMART_MODE_ON.equals(this.mGet.getSettingValue(Setting.KEY_VIDEO_AUTO_REVIEW)) | this.mGet.isAttachIntent()) {
                this.mGet.setVideoState(5);
                this.mGet.setShutterButtonImage(false, this.mGet.getOrientationDegree());
            }
        } else {
            this.mGet.setVideoState(0);
            this.mGet.doCommandUi(Command.DISPLAY_PREVIEW);
        }
        if (!this.mGet.isAttachMode()) {
            this.mGet.doCommandUi(Command.HIDE_LIVE_SNAPSHOT_BUTTON);
            this.mGet.setSwitcherVisible(true);
            this.mGet.updateThumbnailButtonVisibility();
        }
        if (MultimediaProperties.isPauseAndResumeSupported()) {
            this.mGet.hideRecoridngStopButton();
        }
        if (ProjectVariables.isSupportHeat_detection()) {
            this.mGet.stopHeatingwarning();
        }
        if (FunctionProperties.isSupportObjectTracking() && this.mGet.getObjectTrackingState() != 0) {
            this.mGet.unregisterObjectCallback();
            this.mGet.startObjectTrackingFocus(0, 0, 0, 0, 0);
        }
        AudioUtil.setAudioFocus(this.mGet.getApplicationContext(), false);
    }

    private Uri getSaveUri(String videoSizeString) {
        File vFile = this.mVideoFile.getFile();
        if (vFile == null || !vFile.exists()) {
            return null;
        }
        return checkMinRecordingTimeAndDeleteVideo(SystemClock.uptimeMillis(), vFile, videoSizeString);
    }

    private void addSecureImageList(Uri savedUri) {
        if (Common.useSecureLockImage() || AppControlUtil.checkGuestModeAndAppDisabled(this.mGet.getContentResolver(), true, 1)) {
            SecureImageUtil.get().addSecureLocVideokUri(savedUri);
        }
    }

    private Uri checkMinRecordingTimeAndDeleteVideo(long checkTime, File vFile, String videoSizeString) {
        Uri savedUri = null;
        if (checkTime - this.mStartTime < ((long) MultimediaProperties.getMinRecordingTime())) {
            if (vFile != null) {
                CamLog.d(FaceDetector.TAG, "vFile.delete()");
                vFile.delete();
            }
        } else if (this.mGet.checkFsWritable()) {
            if (this.mGet.isMMSRecording()) {
                savedUri = this.mVideoFile.registerUri(0, videoSizeString, this.mGet.getCurrentLocation(), false, 1);
            } else {
                savedUri = this.mVideoFile.registerUri(0, videoSizeString, this.mGet.getCurrentLocation(), false, 0);
            }
            addSecureImageList(savedUri);
        } else {
            this.mVideoFile.deleteFile();
        }
        return savedUri;
    }

    public void onResume() {
        if (this.mInit) {
            this.mGet.setPreviewVisibility(0);
            this.mGet.setEffectRecorderPausing(false);
        }
        super.onResume();
    }

    public void onPause() {
        CamLog.d(FaceDetector.TAG, "onPause-start");
        if (this.mInit) {
            if (getStopRecordingDuringCall()) {
                CamLog.d(FaceDetector.TAG, "While recording, Received a call. Video state set to idle!");
                this.mGet.setVideoState(0);
                return;
            }
            int state = this.mGet.getVideoState();
            CamLog.d(FaceDetector.TAG, String.format("Camcorder state: %d", new Object[]{Integer.valueOf(state)}));
            if (state == 2) {
                waitStartRecordingThreadDone();
                waitStopRecordingThreadDone();
            }
            if (state == 3 || state == 4) {
                this.mGet.setVideoState(2);
                this.mEndTime = SystemClock.uptimeMillis();
                setRecDurationTime(this.mStartTime, this.mEndTime);
                stopRecordingByPausing();
                this.mStartTime = 0;
            } else if (state == 2 || state == 1) {
                CamLog.d(FaceDetector.TAG, "RecordingController: Video state NO_REACTION after wait threads.");
                CamLog.d(FaceDetector.TAG, "Force video state to idle");
                this.mGet.setVideoState(0);
            }
            this.mCheckResumeVideo = true;
        }
        if (!this.mGet.isChangingToOtherActivity()) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("animation", false);
            this.mGet.doCommand(Command.ROTATE, bundle);
        }
        if (ProjectVariables.isSupportHeat_detection()) {
            this.mGet.stopHeatingwarning();
        }
        super.onPause();
        CamLog.d(FaceDetector.TAG, " onPause-end");
    }

    public boolean isRecordedLengthTooShort() {
        if (SystemClock.uptimeMillis() - this.mStartTime >= ((long) MultimediaProperties.getMinRecordingTime())) {
            return false;
        }
        CamLog.d(FaceDetector.TAG, String.format("recorded time: %d ms", new Object[]{Long.valueOf(currentTime - this.mStartTime)}));
        return true;
    }

    public void setScaleWidthHeight(float ScaleWidthHeight) {
        this.mScaleWidthHeight = ScaleWidthHeight;
    }

    public boolean isRecordingControllerInit() {
        return this.mInit;
    }

    private void setQuickButton(final boolean start, final int degree) {
        this.mGet.postOnUiThread(new Runnable() {
            public void run() {
                if (RecordingController.this.checkMediator()) {
                    RecordingController.this.mGet.removePostRunnable(this);
                    if (RecordingController.this.mGet.getCameraId() == 0) {
                        if (start) {
                            if (!FunctionProperties.isNoneFlashModel()) {
                                RecordingController.this.mGet.addQuickButton(RecordingController.this.mGet.getApplicationContext(), 10, degree, 8);
                            }
                            if (!(!FunctionProperties.isSupportAudiozoom() || RecordingController.this.mGet.isDualRecordingActive() || RecordingController.this.mGet.isSmartZoomRecordingActive() || RecordingController.this.mGet.isLiveEffectActive())) {
                                RecordingController.this.mGet.addQuickButton(RecordingController.this.mGet.getApplicationContext(), 2, degree, 7);
                            }
                            RecordingController.this.mGet.setQuickButtonVisible(100, 0, true);
                        } else {
                            RecordingController.this.mGet.removeQuickButtonAll();
                        }
                        if (ProjectVariables.isUseFlashTemperature() && RecordingController.this.mGet.isFlashOffByHighTemperature()) {
                            RecordingController.this.mGet.setButtonRemainEnabled(10, false, true);
                        }
                    }
                }
            }
        });
    }

    public void changeMaxFileSize(long size) {
        VideoRecorder.changeMaxFileSize(size);
    }
}
