package com.lge.camera.dialog;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;

public class EnableGalleryRotatableDialog extends RotateDialog {
    public EnableGalleryRotatableDialog(ControllerFunction function) {
        super(function);
    }

    public void create() {
        String appName = "";
        try {
            appName = (String) this.mGet.getActivity().getPackageManager().getApplicationLabel(this.mGet.getActivity().getPackageManager().getApplicationInfo("com.android.gallery3d", 8192));
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        View v = this.mGet.inflateView(R.layout.rotate_dialog);
        LinearLayout inLayout = (LinearLayout) v.findViewById(R.id.rotate_dialog_inner_layout);
        LayoutParams param = (LayoutParams) inLayout.getLayoutParams();
        param.width = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.lcd_height);
        inLayout.setLayoutParams(param);
        ((ImageView) v.findViewById(R.id.title_icon)).setVisibility(8);
        TextView messageText = (TextView) v.findViewById(R.id.message_text);
        Button btnOk = (Button) v.findViewById(R.id.ok_button);
        Button btnCancel = (Button) v.findViewById(R.id.cancel_button);
        ((TextView) v.findViewById(R.id.title_text)).setText(R.string.sp_note_dialog_title_NORMAL);
        messageText.setText(String.format(this.mGet.getString(R.string.sp_enable_app_msg_NORMAL), new Object[]{appName}));
        btnOk.setText(R.string.sp_ok_NORMAL);
        btnCancel.setText(R.string.cancel);
        super.create(v);
        btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "ok button click....");
                EnableGalleryRotatableDialog.this.mGet.getActivity().startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:com.android.gallery3d")));
                EnableGalleryRotatableDialog.this.onDismiss();
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "cancel button click....");
                EnableGalleryRotatableDialog.this.onDismiss();
            }
        });
    }
}
