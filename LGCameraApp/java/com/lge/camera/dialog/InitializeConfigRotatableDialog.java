package com.lge.camera.dialog;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class InitializeConfigRotatableDialog extends RotateDialog {
    public InitializeConfigRotatableDialog(ControllerFunction function) {
        super(function);
    }

    public void create() {
        View v = this.mGet.inflateView(R.layout.rotate_dialog);
        TextView messageText = (TextView) v.findViewById(R.id.message_text);
        Button btnOk = (Button) v.findViewById(R.id.ok_button);
        Button btnCancel = (Button) v.findViewById(R.id.cancel_button);
        ((TextView) v.findViewById(R.id.title_text)).setText(R.string.reset);
        messageText.setText(R.string.sp_reset_message_NORMAL);
        btnOk.setText(R.string.yes);
        btnCancel.setText(R.string.no);
        super.create(v);
        btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "ok button click....");
                if ((MultimediaProperties.isLiveEffectSupported() && InitializeConfigRotatableDialog.this.mGet.getApplicationMode() == 1 && InitializeConfigRotatableDialog.this.mGet.isEffectsCamcorderActive()) || (MultimediaProperties.isDualCameraSupported() && InitializeConfigRotatableDialog.this.mGet.isEffectsCameraActive())) {
                    InitializeConfigRotatableDialog.this.mGet.doCommand(Command.HIDE_PIP_SUBWINDOW_RESIZE_HANDLER);
                    InitializeConfigRotatableDialog.this.mGet.hideSmartZoomFocusView();
                    InitializeConfigRotatableDialog.this.mGet.doCommand(Command.RESET_MENU_PREPARED);
                } else {
                    InitializeConfigRotatableDialog.this.mGet.doCommand(Command.RESET_MENU);
                }
                if (MultimediaProperties.isDualRecordingSupported() && InitializeConfigRotatableDialog.this.mGet.getApplicationMode() == 1) {
                    InitializeConfigRotatableDialog.this.mGet.enableInput(false);
                }
                InitializeConfigRotatableDialog.this.onDismiss();
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "cancel button click....");
                InitializeConfigRotatableDialog.this.mGet.quickFunctionAllMenuSelected(false);
                InitializeConfigRotatableDialog.this.onDismiss();
            }
        });
    }
}
