package com.lge.camera.dialog;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.StorageProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class StoragePopupRotatableDialog extends RotateDialog {
    public StoragePopupRotatableDialog(ControllerFunction function) {
        super(function);
    }

    public void create() {
        View v = this.mGet.inflateView(R.layout.rotate_dialog);
        TextView titleText = (TextView) v.findViewById(R.id.title_text);
        TextView messageText = (TextView) v.findViewById(R.id.message_text);
        Button btnOk = (Button) v.findViewById(R.id.ok_button);
        Button btnCancel = (Button) v.findViewById(R.id.cancel_button);
        LayoutParams params = (LayoutParams) btnOk.getLayoutParams();
        params.gravity = 1;
        btnOk.setLayoutParams(params);
        btnCancel.setVisibility(8);
        titleText.setText(R.string.sp_storage_full_popup_ics_title_NORMAL);
        String message = this.mGet.getStoragePopupMessage();
        if (message != null) {
            messageText.setText(message);
            ((ScrollView) messageText.getParent()).scrollTo(0, 0);
        }
        btnOk.setText(this.mGet.getString(R.string.sp_ok_NORMAL));
        btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "ok button click....");
                StoragePopupRotatableDialog.this.OnPositiveButtonClick();
                StoragePopupRotatableDialog.this.onDismiss();
            }
        });
        super.create(v);
    }

    private void OnPositiveButtonClick() {
        if (FunctionProperties.isVoiceShutter()) {
            String mVoiceShutterValue = this.mGet.getSettingValue(Setting.KEY_VOICESHUTTER);
            if (mVoiceShutterValue != null && mVoiceShutterValue.equals(CameraConstants.SMART_MODE_ON)) {
                this.mGet.doCommandUi(Command.SET_VOICE_SHUTTER);
            }
        }
        if (StorageProperties.isAllMemorySupported() && this.mGet.getStorageMessageId() == 2) {
            if (StorageProperties.getEmmcName().equals(this.mGet.getSettingValue(Setting.KEY_STORAGE))) {
                this.mGet.setSetting(Setting.KEY_STORAGE, CameraConstants.NAME_EXTERNAL_MEMORY);
            } else {
                this.mGet.setSetting(Setting.KEY_STORAGE, StorageProperties.getEmmcName());
            }
            this.mGet.doCommand(Command.SET_STORAGE);
        }
    }
}
