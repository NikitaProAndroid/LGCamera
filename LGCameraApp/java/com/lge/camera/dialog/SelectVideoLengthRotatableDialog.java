package com.lge.camera.dialog;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.MmsProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class SelectVideoLengthRotatableDialog extends RotateDialog {
    public SelectVideoLengthRotatableDialog(ControllerFunction function) {
        super(function);
    }

    public void create() {
        View v = this.mGet.inflateView(R.layout.rotate_record_mode_dialog);
        Button btnOk = (Button) v.findViewById(R.id.ok_button);
        Button btnCancel = (Button) v.findViewById(R.id.cancel_button);
        ((TextView) v.findViewById(R.id.title_text)).setText(R.string.sp_video_length_dialog_title_v2_NORMAL);
        btnOk.setText(R.string.sp_video_length_dialog_mms_NORMAL);
        btnCancel.setText(R.string.sp_video_length_dialog_long_NORMAL);
        super.create(v, false);
        btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "ok button click....");
                SelectVideoLengthRotatableDialog.this.onMmsClick();
                SelectVideoLengthRotatableDialog.this.onDismiss();
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "cancel button click....");
                SelectVideoLengthRotatableDialog.this.onLongVideoClick();
                SelectVideoLengthRotatableDialog.this.onDismiss();
            }
        });
    }

    private void onMmsClick() {
        CamLog.d(FaceDetector.TAG, String.format("Mms Selected", new Object[0]));
        String resolution = MmsProperties.getMmsResolutions(this.mGet.getContentResolver())[0];
        String shotmode = this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE);
        if (CameraConstants.TYPE_RECORDMODE_DUAL.equals(shotmode) || CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(shotmode) || CameraConstants.TYPE_RECORDMODE_SMART_ZOOM.equals(shotmode)) {
            this.mGet.setSetting(Setting.KEY_VIDEO_RECORD_MODE, CameraConstants.TYPE_RECORDMODE_NORMAL);
            this.mGet.setPreviousResolution(resolution);
            this.mGet.doCommand(Command.SET_VIDEO_MODE);
            this.mGet.setPreviousRecordModeString(CameraConstants.TYPE_RECORDMODE_NORMAL);
            this.mGet.setQuickButtonForcedDisable(false);
            this.mGet.setButtonRemainRefresh();
        } else {
            this.mGet.setSetting(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, resolution);
            this.mGet.doCommand(Command.VIDEO_IMAGE_SIZE);
        }
        this.mGet.recordingControllerShow();
    }

    private void onLongVideoClick() {
        CamLog.d(FaceDetector.TAG, String.format("Long Video Selected", new Object[0]));
        ListPreference pref = this.mGet.getPreferenceGroup().findPreference(Setting.KEY_PREVIEW_SIZE_ON_DEVICE);
        String shotmode = this.mGet.getSettingValue(Setting.KEY_VIDEO_RECORD_MODE);
        if (!CameraConstants.TYPE_RECORDMODE_DUAL.equals(shotmode) && !CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT.equals(shotmode) && !CameraConstants.TYPE_RECORDMODE_SMART_ZOOM.equals(shotmode)) {
            if (!(pref == null || pref.getDefaultValue() == null)) {
                this.mGet.setSetting(Setting.KEY_PREVIEW_SIZE_ON_DEVICE, pref.getDefaultValue());
            }
            this.mGet.doCommand(Command.VIDEO_IMAGE_SIZE);
        } else if (pref != null) {
            this.mGet.setPreviousResolution(pref.getDefaultValue());
        }
        this.mGet.recordingControllerHide();
    }

    public void onDismiss() {
        super.onDismiss();
        if (this.mGet != null) {
            this.mGet.afterOnDismissForSelectVideoLength();
        }
    }
}
