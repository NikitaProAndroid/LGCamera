package com.lge.camera.command;

import android.graphics.Typeface;
import android.os.SystemClock;
import android.widget.ImageView;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.components.RecProgressBar;
import com.lge.camera.components.RotateImageButton;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.CheckStatusManager;
import com.lge.camera.util.Common;
import com.lge.morpho.app.morphopanorama.PanoramaApplication;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.io.File;

public class UpdateRecordingTime extends Command {
    private final int mThousand;

    public UpdateRecordingTime(ControllerFunction function) {
        super(function);
        this.mThousand = PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME;
    }

    public void execute() {
        CamLog.v(FaceDetector.TAG, "UpdateRecordingTime, video state: " + this.mGet.getVideoState());
        switch (this.mGet.getVideoState()) {
            case LGKeyRec.EVENT_INVALID /*0*/:
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                updateRecordingTimeStateIdle();
            case LGKeyRec.EVENT_STARTED /*3*/:
                long delta = SystemClock.uptimeMillis() - this.mGet.getStartTime();
                long next_update_delay = 1000 - (delta % 1000);
                long seconds = (long) Math.round(((float) delta) / 1000.0f);
                this.mGet.setCurrentRecordingTime(seconds);
                updateRecordingTimeStateRecording(seconds);
                if (CheckStatusManager.useBackLightControlInRecording() && seconds == CheckStatusManager.TEMPERATURE_LCD_CONTROL_SECOND && Common.IsHeatingVideoSize(this.mGet.getPreviewSizeOnDevice())) {
                    Common.backlightControlByVal(this.mGet.getActivity(), CheckStatusManager.TEMPERATURE_LCD_CONTROL_RATIO);
                    CamLog.v(FaceDetector.TAG, "backlight set to :" + CheckStatusManager.TEMPERATURE_LCD_CONTROL_RATIO);
                }
                if (!checkUHDStopCondition(seconds)) {
                    this.mGet.doCommandDelayed(Command.UPDATE_RECORDING_TIME, next_update_delay);
                }
            default:
        }
    }

    public void execute(Object time) {
        long seconds = ((Integer) time).longValue();
        switch (this.mGet.getVideoState()) {
            case LGKeyRec.EVENT_INVALID /*0*/:
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                updateRecordingTimeStateIdle();
            case LGKeyRec.EVENT_STARTED /*3*/:
                updateRecordingTimeStateRecording(seconds);
            default:
        }
    }

    private void updateRecordingTimeStateIdle() {
        TextView recTimeText = (TextView) this.mGet.findViewById(R.id.text_rec_time);
        recTimeText.setTypeface(Typeface.DEFAULT);
        int currentCarrierCode = ModelProperties.getCarrierCode();
        boolean isMMSMode = this.mGet.isMMSIntent();
        boolean isAttachIntent = this.mGet.isAttachIntent();
        boolean isAttachMode = this.mGet.isAttachMode();
        if ((isMMSMode || (!isAttachIntent && isAttachMode)) && currentCarrierCode == 6) {
            recTimeText.setText("00/60");
        } else {
            recTimeText.setText(this.mGet.getString(R.string.video_rec_time));
        }
        ((RecProgressBar) this.mGet.findViewById(R.id.progress_rec_time)).setProgress(0);
    }

    private void updateRecordingTimeStateRecording(long seconds) {
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long remainderMinutes = minutes - (60 * hours);
        long remainderSeconds = seconds - (60 * minutes);
        boolean isAttachMode = this.mGet.isAttachMode();
        boolean isMMSMode = this.mGet.isMMSIntent();
        boolean isAttachIntent = this.mGet.isAttachIntent();
        setTextRecordingIndicator(minutes, hours, remainderMinutes, remainderSeconds);
        recordingIconBlink(seconds);
        shutterButtonBlink();
        int progress = 0;
        boolean isFileSizeLimitReached = this.mGet.getIsFileSizeLimitReached();
        long recordingSizeLimit = this.mGet.getRecordingSizeLimit();
        long recordingDurationLimit = this.mGet.getRecordingDurationLimit();
        long videoFileSize = this.mGet.getVideoFileSize();
        RecProgressBar recordingProgressBar = (RecProgressBar) this.mGet.findViewById(R.id.progress_rec_time);
        if ((isMMSMode || (!isAttachIntent && isAttachMode)) && (ModelProperties.getCarrierCode() == 6 || ModelProperties.getCarrierCode() == 10)) {
            if (isFileSizeLimitReached) {
                progress = recordingProgressBar.getMax();
                CamLog.d(FaceDetector.TAG, "Limit reached! barmax:" + progress);
            } else {
                float mMSMaxDuration = (float) (MultimediaProperties.getMMSMaxDuration() / PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME);
                progress = (int) ((((float) remainderSeconds) / r0) * ((float) recordingProgressBar.getMax()));
                if (remainderSeconds == 0 && progress != 0) {
                    progress = recordingProgressBar.getMax();
                }
            }
            recordingProgressBar.setProgress(progress);
            return;
        }
        File videoFile;
        if (isMMSMode || (isAttachMode && recordingSizeLimit != 0 && (recordingDurationLimit == 0 || recordingDurationLimit == 7200000))) {
            videoFile = this.mGet.getVideoFile().getFile();
            recordingProgressBar.setVisibility(0);
            if (isFileSizeLimitReached) {
                videoFileSize = recordingSizeLimit;
                progress = recordingProgressBar.getMax();
                CamLog.d(FaceDetector.TAG, "Limit reached! barmax:" + progress);
            } else if (videoFile.exists()) {
                videoFileSize = videoFile.length();
                CamLog.d(FaceDetector.TAG, "FileSize: " + videoFileSize);
                progress = (int) ((((long) recordingProgressBar.getMax()) * videoFileSize) / recordingSizeLimit);
            }
            recordingProgressBar.setProgress(progress);
        } else if (isAttachMode && recordingDurationLimit != 0 && recordingSizeLimit == 0) {
            recordingProgressBar.setVisibility(0);
            if (1000 * seconds >= recordingDurationLimit) {
                progress = recordingProgressBar.getMax();
                CamLog.d(FaceDetector.TAG, "Limit reached! barmax:" + progress);
            } else {
                CamLog.d(FaceDetector.TAG, "rectime: " + seconds);
                progress = (int) (((1000 * seconds) * ((long) recordingProgressBar.getMax())) / recordingDurationLimit);
            }
            recordingProgressBar.setProgress(progress);
        } else if (isAttachMode && (recordingDurationLimit != 0 || (recordingDurationLimit == 7200000 && recordingSizeLimit != 0))) {
            recordingProgressBar.setVisibility(0);
            if (1000 * seconds >= recordingDurationLimit || isFileSizeLimitReached) {
                progress = recordingProgressBar.getMax();
            } else {
                videoFile = this.mGet.getVideoFile().getFile();
                if (videoFile != null && videoFile.exists()) {
                    long max = (long) recordingProgressBar.getMax();
                    int progressBySize = (int) ((r0 * videoFile.length()) / recordingSizeLimit);
                    int progressByDuration = (int) (((1000 * seconds) * ((long) recordingProgressBar.getMax())) / recordingDurationLimit);
                    if (progressBySize > progressByDuration) {
                        progress = progressBySize;
                    } else {
                        progress = progressByDuration;
                    }
                } else {
                    return;
                }
            }
            recordingProgressBar.setProgress(progress);
        }
        if (isFileSizeLimitReached) {
            recordingProgressBar.invalidate();
        }
    }

    private void recordingIconBlink(long seconds) {
        ImageView recIcon = (ImageView) this.mGet.findViewById(R.id.rec_status_icon);
        if (recIcon != null) {
            recIcon.setImageResource(R.drawable.rec_recording);
            if (seconds == 0) {
                recIcon.setVisibility(0);
            } else if (recIcon.getVisibility() != 0) {
                recIcon.setVisibility(0);
            } else {
                recIcon.setVisibility(4);
            }
        }
    }

    private void shutterButtonBlink() {
        RotateImageButton shutterBlinkAnimation = (RotateImageButton) this.mGet.findViewById(R.id.main_button_animation);
        ImageView recIcon = (ImageView) this.mGet.findViewById(R.id.rec_status_icon);
        if (shutterBlinkAnimation != null && recIcon != null) {
            shutterBlinkAnimation.setImageResource(R.drawable.btn_rec_outline);
            shutterBlinkAnimation.setVisibility(recIcon.getVisibility());
        }
    }

    private void setTextRecordingIndicator(long minutes, long hours, long remainderMinutes, long remainderSeconds) {
        TextView recTimeText = (TextView) this.mGet.findViewById(R.id.text_rec_time);
        recTimeText.setTypeface(Typeface.DEFAULT);
        recTimeText.setVisibility(0);
        if ((this.mGet.isMMSIntent() || (!this.mGet.isAttachIntent() && this.mGet.isAttachMode())) && ModelProperties.getCarrierCode() == 6) {
            recTimeText.setText(String.format("%02d/60", new Object[]{Long.valueOf(remainderSeconds)}));
            if (remainderMinutes == 1) {
                recTimeText.setText("60/60");
                return;
            }
            return;
        }
        recTimeText.setText(String.format("%02d:%02d:%02d", new Object[]{Long.valueOf(hours), Long.valueOf(remainderMinutes), Long.valueOf(remainderSeconds)}));
    }

    private boolean checkUHDStopCondition(long recSeconds) {
        if (!"3840x2160".equalsIgnoreCase(this.mGet.getSettingValue(Setting.KEY_PREVIEW_SIZE_ON_DEVICE))) {
            return false;
        }
        if (recSeconds == 295) {
            this.mGet.toastLong(this.mGet.getString(R.string.sp_uhd_heat_warning));
            return false;
        } else if (recSeconds != 301) {
            return false;
        } else {
            this.mGet.doCommandUi(Command.STOP_RECORDING);
            return true;
        }
    }
}
