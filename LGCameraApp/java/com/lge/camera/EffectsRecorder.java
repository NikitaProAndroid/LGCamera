package com.lge.camera;

import android.content.Context;
import android.filterfw.core.Filter;
import com.lge.camera.EffectsBase.EffectBaseInterface;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class EffectsRecorder extends EffectsBase {
    private static final String PAUSE_AND_RESUME_INPUT_NAME = "pauseNresumeRecording";

    public EffectsRecorder(Context context, EffectBaseInterface inf) {
        super(context, inf);
        CamLog.v(FaceDetector.TAG, "EffectsRecorder created (" + this + ")");
    }

    public synchronized void startRecording(boolean beUnmute) {
        boolean captureTimeLapse = true;
        synchronized (this) {
            CamLog.d(FaceDetector.TAG, "Starting recording (" + this + ")");
            if (this.mState == 4 || this.mState == 5) {
                CamLog.d(FaceDetector.TAG, "startRecording cannot be called while " + this.mState);
            } else if (this.mOutputFile == null && this.mFd == null) {
                CamLog.d(FaceDetector.TAG, "No output file name or descriptor provided!");
            } else {
                if (this.mState == 0) {
                    startPreview();
                }
                Filter recorder = this.mRunner.getGraph().getFilter("recorder");
                if (recorder != null) {
                    if (this.mFd != null) {
                        recorder.setInputValue("outputFileDescriptor", this.mFd);
                    } else {
                        recorder.setInputValue("outputFile", this.mOutputFile);
                    }
                    if (beUnmute) {
                        recorder.setInputValue("audioSource", Integer.valueOf(5));
                    } else {
                        recorder.setInputValue("audioSource", Integer.valueOf(-1));
                        recorder.setInputValue("outputFormat", Integer.valueOf(2));
                        recorder.setInputValue("videoEncoder", Integer.valueOf(2));
                        recorder.setInputValue("videoEncoderBitrate", Integer.valueOf(this.mVideoBitrate));
                        recorder.setInputValue("width", Integer.valueOf(this.mProfile.videoFrameWidth));
                        recorder.setInputValue("height", Integer.valueOf(this.mProfile.videoFrameHeight));
                    }
                    recorder.setInputValue("recordingProfile", this.mProfile);
                    recorder.setInputValue("orientationHint", Integer.valueOf(this.mOrientationHint));
                    if (this.mCaptureRate <= 0.0d) {
                        captureTimeLapse = false;
                    }
                    if (captureTimeLapse) {
                        recorder.setInputValue("timelapseRecordingIntervalUs", Long.valueOf((long) (1000000.0d * (1.0d / this.mCaptureRate))));
                    } else {
                        recorder.setInputValue("timelapseRecordingIntervalUs", Long.valueOf(0));
                    }
                    if (this.mInfoListener != null) {
                        recorder.setInputValue("infoListener", this.mInfoListener);
                    }
                    if (this.mErrorListener != null) {
                        recorder.setInputValue("errorListener", this.mErrorListener);
                    }
                    recorder.setInputValue("maxFileSize", Long.valueOf(this.mMaxFileSize));
                    CamLog.d(FaceDetector.TAG, " #####  effect.maxDurationMs:" + this.mMaxDurationMs + " mMaxFileSize: " + this.mMaxFileSize + " profile:" + this.mProfile.videoFrameWidth + "x" + this.mProfile.videoFrameHeight);
                    recorder.setInputValue("maxDurationMs", Integer.valueOf(this.mMaxDurationMs));
                    pauseAndResumeRecording(recorder, false);
                    CamLog.d(FaceDetector.TAG, " #####  recorder.setInputValue recording true");
                    recorder.setInputValue("recording", Boolean.valueOf(true));
                }
                this.mState = 4;
            }
        }
    }

    public synchronized void stopRecording() {
        CamLog.v(FaceDetector.TAG, "Stop recording (" + this + ")");
        switch (this.mState) {
            case LGKeyRec.EVENT_INVALID /*0*/:
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
            case LGKeyRec.EVENT_STARTED /*3*/:
                CamLog.w(FaceDetector.TAG, "StopRecording called when recording not active!");
                break;
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                throw new RuntimeException("stopRecording called on released EffectsRecorder!");
            default:
                Filter recorder = this.mRunner.getGraph().getFilter("recorder");
                if (recorder != null) {
                    CamLog.d(FaceDetector.TAG, " #####  recorder.setInputValue recording false");
                    recorder.setInputValue("recording", Boolean.valueOf(false));
                }
                this.mState = 3;
                break;
        }
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
}
