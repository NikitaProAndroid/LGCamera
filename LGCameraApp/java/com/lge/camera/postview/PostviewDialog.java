package com.lge.camera.postview;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.CheckBox;
import android.widget.TextView;
import com.lge.camera.R;
import com.lge.camera.ShotPostviewActivity;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ImageFormat;
import com.lge.olaworks.library.FaceDetector;

public class PostviewDialog extends DialogFragment {
    public static final int DIALOG_ID_CLEARSHOT_WARNING = 7;
    public static final int DIALOG_ID_DELETE_CONFIRM = 4;
    public static final int DIALOG_ID_DELETE_CONFIRM_MULT = 3;
    public static final int DIALOG_ID_ENABLE_GALLERY = 5;
    public static final int DIALOG_ID_NONE = 0;
    public static final int DIALOG_ID_PROGRESS = 9;
    public static final int DIALOG_ID_PROGRESS_SAVING = 10;
    public static final int DIALOG_ID_REFOCUS_WARNING = 8;
    public static final int DIALOG_ID_SETAS_LIST = 2;
    public static final int DIALOG_ID_SHARE_LIST = 1;
    public static final int DIALOG_ID_TIMEMACHINE_WARNING = 6;
    private static int mApplicationMode;
    private static PostviewDialog mDialog;
    private PostviewMenuAdapter mAdapter;
    private int mDialogId;

    public PostviewDialog() {
        this.mDialogId = -1;
        this.mAdapter = null;
    }

    static {
        mDialog = null;
        mApplicationMode = DIALOG_ID_NONE;
    }

    public static PostviewDialog getPostviewDialog(int dialogId, int applicatoinMode) {
        boolean needCreate = false;
        if (mDialog != null && mDialog.isAdded()) {
            FragmentTransaction ft = mDialog.getFragmentManager().beginTransaction();
            Fragment prev = mDialog.getFragmentManager().findFragmentByTag(CameraConstants.TAG_DIALOG_POSTVIEW);
            if (prev != null) {
                ft.remove(prev);
                ft.addToBackStack(null);
                ShotPostviewActivity act = (ShotPostviewActivity) mDialog.getActivity();
                if (!(act == null || act.isPausing())) {
                    ft.commit();
                }
                needCreate = true;
            }
        }
        if (mDialog == null) {
            needCreate = true;
        }
        if (needCreate) {
            mDialog = new PostviewDialog();
        }
        try {
            Bundle args = new Bundle();
            args.putInt("dialogId", dialogId);
            mDialog.setArguments(args);
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "Exception : " + e);
        }
        mApplicationMode = applicatoinMode;
        return mDialog;
    }

    public int getCurrentDialogId() {
        return this.mDialogId;
    }

    public static PostviewDialog getPostviewDialog() {
        return mDialog;
    }

    public void unbind() {
        mDialog = null;
        if (this.mAdapter != null) {
            this.mAdapter.clear();
            this.mAdapter = null;
        }
    }

    private void setWindowDimBehindDialog(Dialog dialog) {
        dialog.show();
        LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().addFlags(DIALOG_ID_SETAS_LIST);
    }

    public Dialog onCreateDialog(Bundle savedInstatnceState) {
        this.mDialogId = getArguments().getInt("dialogId", DIALOG_ID_NONE);
        switch (this.mDialogId) {
            case DIALOG_ID_SHARE_LIST /*1*/:
            case DIALOG_ID_SETAS_LIST /*2*/:
                return createPostviewMenuListPopup();
            case DIALOG_ID_DELETE_CONFIRM_MULT /*3*/:
                return createDeleteMultiPopup();
            case DIALOG_ID_DELETE_CONFIRM /*4*/:
                return createDeletePopup();
            case DIALOG_ID_ENABLE_GALLERY /*5*/:
                return createEnableGalleryPopup();
            case DIALOG_ID_TIMEMACHINE_WARNING /*6*/:
                return createTimeMachineWarningPopup();
            case DIALOG_ID_CLEARSHOT_WARNING /*7*/:
                return createClearShotWarningPopup();
            case DIALOG_ID_REFOCUS_WARNING /*8*/:
                return createRefocusWarningPopup();
            case DIALOG_ID_PROGRESS /*9*/:
            case DIALOG_ID_PROGRESS_SAVING /*10*/:
                return createProgressPopup();
            default:
                return createEnableGalleryPopup();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mDialogId = getArguments().getInt("dialogId", DIALOG_ID_NONE);
        switch (this.mDialogId) {
            case DIALOG_ID_PROGRESS /*9*/:
            case DIALOG_ID_PROGRESS_SAVING /*10*/:
                View progressDialog = inflater.inflate(R.layout.postview_progress_dialog, container);
                ((TextView) progressDialog.findViewById(R.id.message_text)).setText(getActivity().getString(this.mDialogId == DIALOG_ID_PROGRESS ? R.string.pd_message_processing : R.string.msg_save_progress));
                return progressDialog;
            default:
                return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    private AlertDialog createDeletePopup() {
        Builder builder = new Builder(getActivity());
        String notifyMsg = "";
        if (mApplicationMode == 0) {
            notifyMsg = getString(R.string.sp_photo_will_be_deleted_NORMAL);
        } else {
            notifyMsg = getString(R.string.sp_video_will_be_deleted_NORMAL);
        }
        builder.setTitle(R.string.dlg_title_delete).setIconAttribute(16843605).setMessage(notifyMsg).setPositiveButton(R.string.yes, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((ShotPostviewActivity) PostviewDialog.this.getActivity()).doDeletePositiveClick();
            }
        }).setNegativeButton(R.string.no, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PostviewDialog.this.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        setWindowDimBehindDialog(dialog);
        return dialog;
    }

    private AlertDialog createDeleteMultiPopup() {
        Builder builder = new Builder(getActivity());
        builder.setTitle(R.string.dlg_title_delete).setIconAttribute(16843605).setMessage(getString(R.string.sp_photo_will_be_deleted_NORMAL)).setPositiveButton(R.string.yes, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((ShotPostviewActivity) PostviewDialog.this.getActivity()).doDeleteMultiPositiveClick();
            }
        }).setNegativeButton(R.string.no, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PostviewDialog.this.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        setWindowDimBehindDialog(dialog);
        return dialog;
    }

    public void setSharedListDialogAdater(PostviewMenuAdapter adapter) {
        this.mAdapter = adapter;
    }

    private AlertDialog createPostviewMenuListPopup() {
        Builder builder = new Builder(getActivity());
        builder.setTitle(this.mDialogId == 20 ? R.string.sp_share_via_NORMAL : R.string.sp_set_photo_as_NORMAL);
        if (this.mAdapter != null) {
            builder.setAdapter(this.mAdapter, new OnClickListener() {
                public void onClick(DialogInterface dialoginterface, int i) {
                    ((ShotPostviewActivity) PostviewDialog.this.getActivity()).adapterPositiveClick(PostviewDialog.this.mDialogId, i);
                }
            });
        }
        AlertDialog dialog = builder.create();
        setWindowDimBehindDialog(dialog);
        return dialog;
    }

    private AlertDialog createEnableGalleryPopup() {
        Builder builder = new Builder(getActivity());
        String appName = "";
        try {
            appName = (String) getActivity().getPackageManager().getApplicationLabel(getActivity().getPackageManager().getApplicationInfo("com.android.gallery3d", 8192));
        } catch (NameNotFoundException e) {
            CamLog.e(FaceDetector.TAG, "Application name is not found");
            e.printStackTrace();
        }
        String string = getString(R.string.sp_enable_app_msg_NORMAL);
        Object[] objArr = new Object[DIALOG_ID_SHARE_LIST];
        objArr[DIALOG_ID_NONE] = appName;
        builder.setTitle(R.string.sp_note_dialog_title_NORMAL).setIconAttribute(16843605).setMessage(String.format(string, objArr)).setPositiveButton(R.string.sp_ok_NORMAL, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((ShotPostviewActivity) PostviewDialog.this.getActivity()).doEnableGalleryPositiveClick();
            }
        }).setNegativeButton(R.string.cancel, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PostviewDialog.this.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        setWindowDimBehindDialog(dialog);
        return dialog;
    }

    private AlertDialog createTimeMachineWarningPopup() {
        View checkBoxView = getActivity().getLayoutInflater().inflate(R.layout.dialog_checkbox, null);
        if (checkBoxView == null) {
            return null;
        }
        final CheckBox userCheck = (CheckBox) checkBoxView.findViewById(R.id.checkbox_do_not_show_again);
        if (userCheck == null) {
            return null;
        }
        Builder builder = new Builder(getActivity());
        userCheck.setSaveEnabled(false);
        userCheck.setText(R.string.sp_eula_popup_do_not_show_this_again_NORMAL);
        userCheck.setChecked(false);
        userCheck.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == PostviewDialog.DIALOG_ID_SHARE_LIST) {
                    v.playSoundEffect(PostviewDialog.DIALOG_ID_NONE);
                }
                return false;
            }
        });
        builder.setTitle(R.string.sp_note_dialog_title_NORMAL).setIconAttribute(16843605).setMessage(R.string.sp_photo_has_not_yet_been_saved_warning_popup).setView(checkBoxView).setPositiveButton(R.string.yes, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((ShotPostviewActivity) PostviewDialog.this.getActivity()).doTimeMachineWarningPositiveClick(userCheck);
            }
        }).setNegativeButton(R.string.no, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((ShotPostviewActivity) PostviewDialog.this.getActivity()).doTimeMachineWarningNegativeClick(userCheck);
                PostviewDialog.this.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        setWindowDimBehindDialog(dialog);
        return dialog;
    }

    private AlertDialog createClearShotWarningPopup() {
        View checkBoxView = getActivity().getLayoutInflater().inflate(R.layout.dialog_checkbox, null);
        if (checkBoxView == null) {
            return null;
        }
        final CheckBox userCheck = (CheckBox) checkBoxView.findViewById(R.id.checkbox_do_not_show_again);
        if (userCheck == null) {
            return null;
        }
        Builder builder = new Builder(getActivity());
        userCheck.setSaveEnabled(false);
        userCheck.setText(R.string.sp_eula_popup_do_not_show_this_again_NORMAL);
        userCheck.setChecked(false);
        userCheck.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == PostviewDialog.DIALOG_ID_SHARE_LIST) {
                    v.playSoundEffect(PostviewDialog.DIALOG_ID_NONE);
                }
                return false;
            }
        });
        builder.setTitle(R.string.sp_note_dialog_title_NORMAL).setIconAttribute(16843605).setMessage(R.string.sp_photo_has_not_yet_been_saved_warning_popup).setView(checkBoxView).setPositiveButton(R.string.yes, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ShotPostviewActivity act = (ShotPostviewActivity) PostviewDialog.this.getActivity();
                if (act != null) {
                    act.doClearShotWarningPositiveClick(userCheck);
                }
            }
        }).setNegativeButton(R.string.no, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ShotPostviewActivity act = (ShotPostviewActivity) PostviewDialog.this.getActivity();
                if (act != null) {
                    act.doClearShotWarningNegativeClick(userCheck);
                }
                PostviewDialog.this.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        setWindowDimBehindDialog(dialog);
        return dialog;
    }

    private AlertDialog createRefocusWarningPopup() {
        View checkBoxView = getActivity().getLayoutInflater().inflate(R.layout.dialog_checkbox, null);
        if (checkBoxView == null) {
            return null;
        }
        final CheckBox userCheck = (CheckBox) checkBoxView.findViewById(R.id.checkbox_do_not_show_again);
        if (userCheck == null) {
            return null;
        }
        Builder builder = new Builder(getActivity());
        userCheck.setSaveEnabled(false);
        userCheck.setText(R.string.sp_eula_popup_do_not_show_this_again_NORMAL);
        userCheck.setChecked(false);
        userCheck.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == PostviewDialog.DIALOG_ID_SHARE_LIST) {
                    v.playSoundEffect(PostviewDialog.DIALOG_ID_NONE);
                }
                return false;
            }
        });
        builder.setTitle(R.string.sp_Exit_NORMAL).setIconAttribute(16843605).setMessage(R.string.sp_refocus_warning_popup).setView(checkBoxView).setPositiveButton(R.string.yes, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ShotPostviewActivity act = (ShotPostviewActivity) PostviewDialog.this.getActivity();
                if (act != null) {
                    act.doRefocusWarningPositiveClick(userCheck);
                }
            }
        }).setNegativeButton(R.string.no, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ShotPostviewActivity act = (ShotPostviewActivity) PostviewDialog.this.getActivity();
                if (act != null) {
                    act.doRefocusWarningNegativeClick(userCheck);
                }
                PostviewDialog.this.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        setWindowDimBehindDialog(dialog);
        return dialog;
    }

    private ProgressDialog createProgressPopup() {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setProgressStyle(DIALOG_ID_NONE);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case PostviewDialog.DIALOG_ID_DELETE_CONFIRM /*4*/:
                    case Tag.GPS_DEST_BEAR /*24*/:
                    case Tag.GPS_DEST_DIST_REF /*25*/:
                    case 84:
                        return true;
                    default:
                        return false;
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        setWindowDimBehindDialog(dialog);
        return dialog;
    }

    public void onDismiss(DialogInterface dialog) {
        if (this.mDialogId != -1) {
            switch (this.mDialogId) {
                case DIALOG_ID_TIMEMACHINE_WARNING /*6*/:
                    ShotPostviewActivity act = (ShotPostviewActivity) getActivity();
                    if (act != null) {
                        act.doTimeMachineWarningDismiss();
                        break;
                    }
                    break;
            }
        }
        if (getActivity() != null) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(Ola_ImageFormat.RGB_LABEL);
        }
        super.onDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog) {
        if (getActivity() != null) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(Ola_ImageFormat.RGB_LABEL);
        }
        super.onCancel(dialog);
    }
}
