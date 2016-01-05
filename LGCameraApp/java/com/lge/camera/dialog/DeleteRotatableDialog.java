package com.lge.camera.dialog;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class DeleteRotatableDialog extends RotateDialog {
    public DeleteRotatableDialog(ControllerFunction function) {
        super(function);
    }

    public void create() {
        View v = this.mGet.inflateView(R.layout.rotate_dialog);
        TextView messageText = (TextView) v.findViewById(R.id.message_text);
        Button btnOk = (Button) v.findViewById(R.id.ok_button);
        Button btnCancel = (Button) v.findViewById(R.id.cancel_button);
        ((TextView) v.findViewById(R.id.title_text)).setText(R.string.dlg_title_delete);
        messageText.setText(this.mGet.getApplicationMode() == 0 ? R.string.sp_photo_will_be_deleted_NORMAL : R.string.sp_video_will_be_deleted_NORMAL);
        btnOk.setText(R.string.yes);
        btnCancel.setText(R.string.no);
        super.create(v);
        btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "ok button click....");
                DeleteRotatableDialog.this.onDismiss(true);
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "cancel button click....");
                DeleteRotatableDialog.this.onDismiss(false);
            }
        });
    }

    public void onDismiss() {
        super.onDismiss();
        this.mGet.closeGalleryQuickView(false);
    }

    public void onDismiss(boolean clickOk) {
        onDismiss();
        this.mGet.closeGalleryQuickView(clickOk);
    }
}
