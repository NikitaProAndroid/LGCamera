package com.lge.camera.listeners;

import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.VideoRecorder;
import com.lge.camera.command.Command;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.util.AudioUtil;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.library.FaceDetector;
import java.math.BigDecimal;

public final class MediaRecorderListener implements OnInfoListener, OnErrorListener {
    private ControllerFunction mGet;

    public MediaRecorderListener(ControllerFunction function) {
        this.mGet = null;
        this.mGet = function;
    }

    public void onInfo(MediaRecorder mr, int what, int extra) {
        CamLog.d(FaceDetector.TAG, "MediaRecorder onInfo what = " + what + " / extra = " + extra);
        if (MultimediaProperties.isPauseAndResumeSupported() && what == MultimediaProperties.MEDIA_RECORDER_INFO_PROGRESS_TIME_DURATION) {
            this.mGet.resumeUpdateReordingTime();
        }
        if (!this.mGet.getIsFileSizeLimitReached()) {
            if (what == LGT_Limit.IMAGE_SIZE_WALLPAPER_HEIGHT || what == 801) {
                this.mGet.setIsFileSizeLimitReached(true);
                if (!(what != 801 || this.mGet.isAttachMode() || this.mGet.isMMSIntent())) {
                    if (this.mGet.isStorageFull()) {
                        this.mGet.toastLong(this.mGet.getString(R.string.popup_storage_full_save));
                    } else {
                        String FILE_MAX_SIZE;
                        CamLog.d(FaceDetector.TAG, "MediaRecorder max filesize reached");
                        CamLog.d(FaceDetector.TAG, "File Size: " + this.mGet.getVideoFile().getFile().length());
                        double mStringSize = ((double) this.mGet.getVideoFile().getFile().length()) / 1.073741824E9d;
                        if (mStringSize >= 1.0d) {
                            FILE_MAX_SIZE = new BigDecimal(mStringSize).setScale(2, 3) + " GB";
                        } else {
                            FILE_MAX_SIZE = new BigDecimal(mStringSize * 1024.0d).setScale(2, 3) + " MB";
                        }
                        this.mGet.toastLong(String.format(this.mGet.getString(R.string.sp_popup_storage_limit_with_exact_size_NORMAL), new Object[]{FILE_MAX_SIZE}));
                    }
                }
                this.mGet.doCommand(Command.UPDATE_RECORDING_TIME);
                this.mGet.doCommand(Command.STOP_RECORDING);
            }
        }
    }

    public void onError(MediaRecorder mr, int what, int extra) {
        if (this.mGet == null) {
            CamLog.w(FaceDetector.TAG, "mGet interface is null.");
            return;
        }
        CamLog.e(FaceDetector.TAG, String.format("MediaRecorder onError-what:%d, extra:%d, bPopup:%b", new Object[]{Integer.valueOf(what), Integer.valueOf(extra), Boolean.valueOf(this.mGet.getShowCameraErrorPopup())}));
        if (what == 1 || what == 2 || what == 100) {
            VideoRecorder.release();
            AudioUtil.setStreamMute(this.mGet.getApplicationContext(), false);
            AudioUtil.setVibrationMute(this.mGet.getApplicationContext(), false);
            this.mGet.setVideoState(0);
            this.mGet.setInCaptureProgress(false);
            this.mGet.resetScreenTimeout();
            if (extra != -1007 || this.mGet.getCurrentRecordingTime() >= ((long) MultimediaProperties.getMinRecordingTime())) {
                if (!this.mGet.getShowCameraErrorPopup()) {
                    this.mGet.runOnUiThread(new Runnable() {
                        public void run() {
                            if (MediaRecorderListener.this.mGet != null) {
                                MediaRecorderListener.this.mGet.removePostRunnable(this);
                                MediaRecorderListener.this.mGet.showCameraStoppedAndFinish();
                            }
                        }
                    });
                }
                this.mGet.setShowCameraErrorPopup(true);
                return;
            }
            CamLog.e(FaceDetector.TAG, "Short recording time error!! time : " + this.mGet.getCurrentRecordingTime() + "ms");
        }
    }
}
