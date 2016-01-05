package com.lge.camera.dialog;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class GeoTagRotatableDialog extends RotateDialog {
    public GeoTagRotatableDialog(ControllerFunction function) {
        super(function);
    }

    public void create() {
        View v = this.mGet.inflateView(R.layout.rotate_dialog);
        TextView messageText = (TextView) v.findViewById(R.id.message_text);
        Button btnOk = (Button) v.findViewById(R.id.ok_button);
        Button btnCancel = (Button) v.findViewById(R.id.cancel_button);
        ((TextView) v.findViewById(R.id.title_text)).setText(R.string.sp_gps_settings_title_NORMAL);
        messageText.setText(R.string.sp_gps_setting_msg_NORMAL);
        btnOk.setText(R.string.settings);
        btnCancel.setText(R.string.cancel);
        super.create(v);
        btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "ok button click....");
                try {
                    GeoTagRotatableDialog.this.mGet.setOpenLBSSetting(true);
                    GeoTagRotatableDialog.this.mGet.getActivity().startActivityForResult(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"), 0);
                } catch (ActivityNotFoundException ex) {
                    CamLog.e(FaceDetector.TAG, "gps setting menu open fail", ex);
                }
                GeoTagRotatableDialog.this.onDismiss();
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "cancel button click....");
                GeoTagRotatableDialog.this.onDismiss();
            }
        });
    }
}
