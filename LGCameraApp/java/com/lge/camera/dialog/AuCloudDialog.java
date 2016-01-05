package com.lge.camera.dialog;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class AuCloudDialog extends RotateDialog {
    private static final String AU_CLOUD_PKG_NAME = "com.kddi.android.auclouduploader";
    private static final String SCHEME = "package";

    public AuCloudDialog(ControllerFunction function) {
        super(function);
    }

    public void create() {
        View v = this.mGet.inflateView(R.layout.rotate_dialog);
        TextView messageText = (TextView) v.findViewById(R.id.message_text);
        Button btnOk = (Button) v.findViewById(R.id.ok_button);
        Button btnCancel = (Button) v.findViewById(R.id.cancel_button);
        ((TextView) v.findViewById(R.id.title_text)).setText(R.string.sp_confirm_NORMAL);
        messageText.setText(R.string.sp_dialog_change_setting_au_cloud);
        btnOk.setText(R.string.sp_ok_NORMAL);
        btnCancel.setText(R.string.cancel);
        super.create(v);
        btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "ok button click....");
                try {
                    AuCloudDialog.showInstalledAppDetails(AuCloudDialog.this.mGet.getApplicationContext(), AuCloudDialog.AU_CLOUD_PKG_NAME);
                } catch (ActivityNotFoundException ex) {
                    CamLog.e(FaceDetector.TAG, "Au Cloud setting menu open fail", ex);
                }
                AuCloudDialog.this.onDismiss();
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CamLog.d(FaceDetector.TAG, "cancel button click....");
                AuCloudDialog.this.onDismiss();
            }
        });
    }

    public static void showInstalledAppDetails(Context context, String packageName) {
        Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts(SCHEME, packageName, null));
        context.startActivity(intent);
    }
}
