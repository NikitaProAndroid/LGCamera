package com.lge.camera.dialog;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class MassStorageRotatableDialog extends RotateDialog {
    public MassStorageRotatableDialog(ControllerFunction function) {
        super(function);
    }

    public void create() {
        View v = this.mGet.inflateView(R.layout.rotate_dialog);
        if (v != null) {
            TextView messageText = (TextView) v.findViewById(R.id.message_text);
            Button btnOk = (Button) v.findViewById(R.id.ok_button);
            Button btnCancel = (Button) v.findViewById(R.id.cancel_button);
            ((TextView) v.findViewById(R.id.title_text)).setText(R.string.sp_connected_mass_storage_NORMAL);
            messageText.setText(R.string.sp_not_available_connected_mass_storage_NORMAL);
            btnOk.setText(R.string.sp_ok_NORMAL);
            LayoutParams params = (LayoutParams) btnOk.getLayoutParams();
            params.gravity = 1;
            params.weight = 0.5f;
            btnOk.setLayoutParams(params);
            btnCancel.setVisibility(8);
            super.create(v);
            btnOk.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    CamLog.d(FaceDetector.TAG, "ok button click....");
                    MassStorageRotatableDialog.this.mGet.getActivity().finish();
                }
            });
        }
    }

    public void onDismiss() {
        super.onDismiss();
        this.mGet.getActivity().finish();
    }
}
