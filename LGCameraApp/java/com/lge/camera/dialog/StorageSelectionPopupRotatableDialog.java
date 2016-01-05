package com.lge.camera.dialog;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.StorageProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class StorageSelectionPopupRotatableDialog extends RotateDialog {
    public StorageSelectionPopupRotatableDialog(ControllerFunction function) {
        super(function);
    }

    public void create() {
        View v = this.mGet.inflateView(R.layout.rotate_dialog);
        TextView messageText = (TextView) v.findViewById(R.id.message_text);
        Button btnOk = (Button) v.findViewById(R.id.ok_button);
        Button btnCancel = (Button) v.findViewById(R.id.cancel_button);
        ((TextView) v.findViewById(R.id.title_text)).setText(R.string.sp_storage_NORMAL);
        messageText.setText(R.string.sp_sdcard_inserted_noti);
        btnOk.setText(this.mGet.getString(R.string.sp_ok_NORMAL));
        btnCancel.setText(this.mGet.getString(R.string.cancel));
        btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "ok button click....");
                StorageSelectionPopupRotatableDialog.this.OnPositiveButtonClick();
                StorageSelectionPopupRotatableDialog.this.onDismiss();
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "hb cancel button click....");
                StorageSelectionPopupRotatableDialog.this.mGet.setSetting(Setting.KEY_STORAGE, StorageProperties.getEmmcName());
                StorageSelectionPopupRotatableDialog.this.mGet.doCommand(Command.SET_STORAGE);
                StorageSelectionPopupRotatableDialog.this.onDismiss();
            }
        });
        super.create(v, false);
    }

    private void OnPositiveButtonClick() {
        this.mGet.setSetting(Setting.KEY_STORAGE, CameraConstants.NAME_EXTERNAL_MEMORY);
        this.mGet.doCommand(Command.SET_STORAGE);
    }
}
