package com.lge.camera.dialog;

import android.content.SharedPreferences.Editor;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class EulaPopupRotatableDialog extends RotateDialog {
    private boolean mDoNotShowAgain;

    public EulaPopupRotatableDialog(ControllerFunction function) {
        super(function);
        this.mDoNotShowAgain = false;
    }

    public void create() {
        View v = this.mGet.inflateView(R.layout.rotate_dialog_checkbox);
        TextView messageText = (TextView) v.findViewById(R.id.message_text);
        Button btnOk = (Button) v.findViewById(R.id.ok_button);
        Button btnCancel = (Button) v.findViewById(R.id.cancel_button);
        CheckBox checkboxText = (CheckBox) v.findViewById(R.id.checkbox_do_not_show_again);
        ((TextView) v.findViewById(R.id.title_text)).setText(R.string.sp_eula_popup_dialog_title_NORMAL);
        messageText.setText(R.string.sp_eula_popup_dialog_msg_NORMAL);
        checkboxText.setText(R.string.sp_eula_popup_do_not_show_this_again_NORMAL);
        btnOk.setText(R.string.sp_ok_NORMAL);
        btnCancel.setText(R.string.cancel);
        checkboxText.setChecked(false);
        checkboxText.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EulaPopupRotatableDialog.this.mDoNotShowAgain = isChecked;
            }
        });
        checkboxText.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 1) {
                    v.playSoundEffect(0);
                }
                return false;
            }
        });
        super.create(v);
        btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "ok button click....");
                if (EulaPopupRotatableDialog.this.mDoNotShowAgain) {
                    Editor edit = EulaPopupRotatableDialog.this.mGet.getActivity().getSharedPreferences(CameraConstants.EULA_PREFERENCE_NAME, 0).edit();
                    edit.putBoolean(CameraConstants.EULA_PREFERENCE_VALUE, true);
                    edit.apply();
                }
                EulaPopupRotatableDialog.this.onDismiss();
                EulaPopupRotatableDialog.this.mGet.showDialogPopup(10);
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "cancel button click....");
                EulaPopupRotatableDialog.this.onDismiss();
            }
        });
    }
}
