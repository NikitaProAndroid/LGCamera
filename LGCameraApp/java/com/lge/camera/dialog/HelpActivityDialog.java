package com.lge.camera.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.R;
import com.lge.camera.util.Common;
import com.lge.camera.util.DialogCreater;
import com.lge.olaworks.define.Ola_Exif.Tag;

public class HelpActivityDialog {
    protected Activity mActivity;
    private int mCameraId;
    private CustomAlertDialog mDialog;
    private int mImgResource;
    private String mMessage;
    protected int mOrientation;
    private String mTitle;
    private int selectedPopupId;

    public HelpActivityDialog(Activity activity) {
        this.mOrientation = -1;
        this.mTitle = null;
        this.mMessage = null;
        this.mImgResource = 0;
        this.mCameraId = 0;
        this.selectedPopupId = -1;
        this.mDialog = null;
        this.mActivity = activity;
        DialogCreater.makeHelpDialog();
    }

    public HelpActivityDialog(Activity activity, int cameraId) {
        this(activity);
        this.mCameraId = cameraId;
    }

    public void create(int whichPopup, int orientation) {
        int imgLevel;
        boolean isVoiceShutterGuideViewNeeded;
        int[] helpResources = DialogCreater.getHelpItemResources(DialogCreater.getHelpPopupID(whichPopup), this.mCameraId);
        if (orientation == 0 || orientation == 2) {
            imgLevel = 1;
        } else {
            imgLevel = 0;
        }
        this.mTitle = this.mActivity.getString(helpResources[0]);
        this.mMessage = this.mActivity.getString(helpResources[1]);
        this.mImgResource = helpResources[2];
        this.selectedPopupId = whichPopup;
        if (DialogCreater.getHelpPopupID(whichPopup) == DialogCreater.DIALOG_ID_HELP_VOICE_PHOTO) {
            isVoiceShutterGuideViewNeeded = true;
        } else {
            isVoiceShutterGuideViewNeeded = false;
        }
        this.mDialog = new CustomAlertDialog(this.mActivity, orientation);
        if (Common.isSecureCamera()) {
            this.mDialog.getWindow().addFlags(4718592);
        }
        this.mDialog.setTitle(this.mTitle);
        this.mDialog.setMessage(this.mMessage);
        this.mDialog.setMessageImage(this.mImgResource, imgLevel);
        this.mDialog.setVoiceShutterVisibility(isVoiceShutterGuideViewNeeded);
        this.mDialog.show();
        if (orientation == 0 || orientation == 2) {
            LinearLayout innerLayout = (LinearLayout) this.mDialog.findViewById(R.id.inner_layout);
            LayoutParams lp = (LayoutParams) innerLayout.getLayoutParams();
            lp.width = DialogCreater.getHorizontalHelpDialogWidth(this.mDialog.getContext(), isVoiceShutterGuideViewNeeded);
            innerLayout.setLayoutParams(lp);
        }
        if (isVoiceShutterGuideViewNeeded) {
            this.mDialog.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    switch (keyCode) {
                        case Tag.GPS_DEST_BEAR /*24*/:
                        case Tag.GPS_DEST_DIST_REF /*25*/:
                            if (HelpActivityDialog.this.mDialog != null) {
                                HelpActivityDialog.this.mDialog.setVolumeControlStream(3);
                                break;
                            }
                            break;
                    }
                    return false;
                }
            });
        }
    }

    public void dismissHelpDialog() {
        if (this.mDialog != null) {
            if (Common.isSecureCamera()) {
                this.mDialog.getWindow().clearFlags(4718592);
            }
            this.mDialog.dismiss();
        }
    }

    public boolean isShowing() {
        if (this.mDialog != null) {
            return this.mDialog.isShowing();
        }
        return false;
    }

    public void refreshDialog(int orientation) {
        if (this.mDialog != null && isShowing()) {
            dismissHelpDialog();
            create(this.selectedPopupId, orientation);
        }
    }

    public void unbind() {
        if (this.mDialog != null) {
            this.mDialog.dismiss();
            this.mDialog.unbind();
            this.mDialog = null;
        }
        this.mActivity = null;
    }
}
