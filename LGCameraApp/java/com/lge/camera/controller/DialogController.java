package com.lge.camera.controller;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.KeyEvent;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.dialog.AuCloudDialog;
import com.lge.camera.dialog.DeleteRotatableDialog;
import com.lge.camera.dialog.EnableGalleryRotatableDialog;
import com.lge.camera.dialog.EulaPopupRotatableDialog;
import com.lge.camera.dialog.GeoTagRotatableDialog;
import com.lge.camera.dialog.HelpRotateDialog;
import com.lge.camera.dialog.HelpVoicePhotoPopupRotatableDialog;
import com.lge.camera.dialog.InitializeConfigRotatableDialog;
import com.lge.camera.dialog.MassStorageRotatableDialog;
import com.lge.camera.dialog.ProgressRotatableDialog;
import com.lge.camera.dialog.RotateDialog;
import com.lge.camera.dialog.SelectVideoLengthRotatableDialog;
import com.lge.camera.dialog.StoragePopupRotatableDialog;
import com.lge.camera.dialog.StorageSelectionPopupRotatableDialog;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.DialogCreater;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.BaseEngine;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class DialogController extends Controller {
    private int dialogId;
    private OnKeyListener dialogKeyListener;
    private Dialog mCurrentDialog;
    private ControllerFunction mGet;
    private SparseArray<RotateDialog> mRotateDialogs;

    public DialogController(ControllerFunction function) {
        super(function);
        this.mCurrentDialog = null;
        this.dialogId = -1;
        this.mRotateDialogs = new SparseArray();
        this.mGet = null;
        this.dialogKeyListener = new OnKeyListener() {
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == 84 || keyCode == 25 || keyCode == 24) {
                    return true;
                }
                if (keyCode != 4 || !DialogController.this.checkMediator() || ((DialogController.this.getDialogID() != 18 && DialogController.this.getDialogID() != 19) || !DialogController.this.getCurrentDialog().isShowing())) {
                    return false;
                }
                DialogController.this.mGet.getActivity().dismissDialog(DialogController.this.getDialogID());
                DialogController.this.mGet.getActivity().finish();
                return true;
            }
        };
        this.mGet = function;
    }

    public void setCurrentDialog(Dialog dialog) {
        this.mCurrentDialog = dialog;
    }

    public Dialog getCurrentDialog() {
        return this.mCurrentDialog;
    }

    public void setDialogID(int id) {
        this.dialogId = id;
    }

    public int getDialogID() {
        return this.dialogId;
    }

    public void showDialogPopup(int id) {
        showDialogPopup(id, false);
    }

    public void showDialogPopup(int id, boolean useCheckBox) {
        this.dialogId = id;
        switch (id) {
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
            case LGKeyRec.EVENT_STOPPED /*4*/:
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
                this.mGet.getActivity().showDialog(id);
            case LGKeyRec.EVENT_STARTED /*3*/:
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
            case Ola_ShotParam.ImageEffect_Vivid /*14*/:
            case Ola_ShotParam.Panorama_Begin /*16*/:
            case Ola_ShotParam.ImageEffect__Max /*17*/:
            case Tag.GPS_DEST_LON /*22*/:
            case Tag.GPS_DEST_BEAR_REF /*23*/:
            case Tag.GPS_DEST_BEAR /*24*/:
            case Tag.GPS_DEST_DIST /*26*/:
            case Tag.GPS_PROCESS_METHOD /*27*/:
            case Tag.GPS_AREA_INFO /*28*/:
                onCreateRotateableDialog(id);
                startRotation(this.mGet.getOrientationDegree());
            case DialogCreater.DIALOG_ID_HELP_HDR /*101*/:
            case DialogCreater.DIALOG_ID_HELP_PANORAMA /*102*/:
            case DialogCreater.DIALOG_ID_HELP_TIMEMACHINE /*103*/:
            case DialogCreater.DIALOG_ID_HELP_CONTINUOUS_SHOT /*104*/:
            case DialogCreater.DIALOG_ID_HELP_BEAUTY_SHOT /*105*/:
            case DialogCreater.DIALOG_ID_HELP_VOICE_PHOTO /*106*/:
            case DialogCreater.DIALOG_ID_HELP_LIVE_EFFECT /*107*/:
            case DialogCreater.DIALOG_ID_HELP_FREE_PANORAMA /*108*/:
            case DialogCreater.DIALOG_ID_HELP_BURST_SHOT /*109*/:
            case DialogCreater.DIALOG_ID_HELP_INTELLIGENT_AUTO_MODE /*110*/:
            case DialogCreater.DIALOG_ID_HELP_WDR_MOVIE /*111*/:
            case DialogCreater.DIALOG_ID_HELP_DUAL_RECORDING /*112*/:
            case DialogCreater.DIALOG_ID_HELP_UPLUS_BOX /*113*/:
            case DialogCreater.DIALOG_ID_HELP_AUDIOZOOM /*114*/:
            case DialogCreater.DIALOG_ID_HELP_CLEAR_SHOT /*115*/:
            case DialogCreater.DIALOG_ID_HELP_DUAL_CAMERA /*116*/:
            case DialogCreater.DIALOG_ID_HELP_SMART_ZOOM_RECORDING /*117*/:
            case DialogCreater.DIALOG_ID_HELP_HDR_MOVIE /*118*/:
            case DialogCreater.DIALOG_ID_HELP_SPORTS /*119*/:
            case DialogCreater.DIALOG_ID_HELP_NIGHT /*120*/:
            case DialogCreater.DIALOG_ID_HELP_FACE_TRACKING_LED /*121*/:
            case DialogCreater.DIALOG_ID_HELP_PLANE_PANORAMA /*122*/:
            case DialogCreater.DIALOG_ID_HELP_LIGHT_FRAME /*123*/:
            case DialogCreater.DIALOG_ID_HELP_REFOCUS /*124*/:
            case DialogCreater.DIALOG_ID_HELP_GESTURESHOT /*125*/:
                onCreateHelpGuideDialog(id, useCheckBox);
                startRotation(this.mGet.getOrientationDegree());
            default:
                this.mGet.getActivity().showDialog(id);
        }
    }

    public void onCreateRotateableDialog(int id) {
        switch (id) {
            case LGKeyRec.EVENT_STARTED /*3*/:
                DeleteRotatableDialog deleteDialog = new DeleteRotatableDialog(this.mGet);
                deleteDialog.create();
                this.mRotateDialogs.put(this.dialogId, deleteDialog);
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
                InitializeConfigRotatableDialog icDialog = new InitializeConfigRotatableDialog(this.mGet);
                icDialog.create();
                this.mRotateDialogs.put(this.dialogId, icDialog);
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
                GeoTagRotatableDialog gtDialog = new GeoTagRotatableDialog(this.mGet);
                gtDialog.create();
                this.mRotateDialogs.put(this.dialogId, gtDialog);
            case Ola_ShotParam.ImageEffect_Vivid /*14*/:
                StoragePopupRotatableDialog storageDialog = new StoragePopupRotatableDialog(this.mGet);
                storageDialog.create();
                this.mRotateDialogs.put(this.dialogId, storageDialog);
            case Ola_ShotParam.Panorama_Begin /*16*/:
                SelectVideoLengthRotatableDialog videoLengthDialog = new SelectVideoLengthRotatableDialog(this.mGet);
                videoLengthDialog.create();
                this.mRotateDialogs.put(this.dialogId, videoLengthDialog);
            case Ola_ShotParam.ImageEffect__Max /*17*/:
                MassStorageRotatableDialog massDialog = new MassStorageRotatableDialog(this.mGet);
                massDialog.create();
                this.mRotateDialogs.put(this.dialogId, massDialog);
            case Tag.GPS_DEST_LON /*22*/:
                ProgressRotatableDialog prDialog = new ProgressRotatableDialog(this.mGet);
                prDialog.create(R.string.pd_message_processing);
                this.mRotateDialogs.put(this.dialogId, prDialog);
            case Tag.GPS_DEST_BEAR_REF /*23*/:
                EulaPopupRotatableDialog epDialog = new EulaPopupRotatableDialog(this.mGet);
                epDialog.create();
                this.mRotateDialogs.put(this.dialogId, epDialog);
            case Tag.GPS_DEST_BEAR /*24*/:
                EnableGalleryRotatableDialog disabledDialog = new EnableGalleryRotatableDialog(this.mGet);
                disabledDialog.create();
                this.mRotateDialogs.put(this.dialogId, disabledDialog);
            case Tag.GPS_DEST_DIST /*26*/:
                StorageSelectionPopupRotatableDialog storageselDialog = new StorageSelectionPopupRotatableDialog(this.mGet);
                storageselDialog.create();
                this.mRotateDialogs.put(this.dialogId, storageselDialog);
            case Tag.GPS_PROCESS_METHOD /*27*/:
                ProgressRotatableDialog savingPrDialog = new ProgressRotatableDialog(this.mGet);
                savingPrDialog.create(R.string.msg_save_progress);
                this.mRotateDialogs.put(this.dialogId, savingPrDialog);
            case Tag.GPS_AREA_INFO /*28*/:
                AuCloudDialog auCloudDialog = new AuCloudDialog(this.mGet);
                auCloudDialog.create();
                this.mRotateDialogs.put(this.dialogId, auCloudDialog);
            default:
        }
    }

    public void onCreateHelpGuideDialog(int dialogId, boolean useCheckBox) {
        switch (dialogId) {
            case DialogCreater.DIALOG_ID_HELP_VOICE_PHOTO /*106*/:
                HelpVoicePhotoPopupRotatableDialog voicePhotoDialog = new HelpVoicePhotoPopupRotatableDialog(this.mGet);
                voicePhotoDialog.create(useCheckBox, dialogId);
                this.mRotateDialogs.put(dialogId, voicePhotoDialog);
            default:
                HelpRotateDialog dialog = new HelpRotateDialog(this.mGet);
                dialog.create(useCheckBox, dialogId);
                this.mRotateDialogs.put(dialogId, dialog);
        }
    }

    public void startRotation(int degree) {
        RotateDialog dialog = (RotateDialog) this.mRotateDialogs.get(this.dialogId);
        if (dialog != null) {
            dialog.startRotation(degree);
        }
    }

    public Dialog onCreateDialog(int id, Bundle args) {
        CamLog.d(FaceDetector.TAG, "onCreateDialog");
        Dialog dialog = null;
        if (this.dialogId == -1) {
            return null;
        }
        OnClickListener buttonListener;
        Builder builder;
        switch (id) {
            case Tag.GPS_MAP_DATUM /*18*/:
                buttonListener = new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DialogController.this.mGet.getActivity().finish();
                    }
                };
                builder = new Builder(this.mGet.getActivity());
                builder.setCancelable(false).setIconAttribute(16843605).setTitle(this.mGet.getString(R.string.camera_error_title)).setMessage(this.mGet.getString(R.string.cannot_connect_camera)).setNeutralButton(R.string.sp_ok_NORMAL, buttonListener);
                dialog = builder.create();
                break;
            case Tag.GPS_DEST_LAT_REF /*19*/:
                buttonListener = new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DialogController.this.mGet.getActivity().finish();
                    }
                };
                builder = new Builder(this.mGet.getActivity());
                builder.setCancelable(false).setIconAttribute(16843605).setTitle(this.mGet.getString(R.string.camera_application_stopped)).setMessage(this.mGet.getString(R.string.camera_driver_needs_reset)).setNeutralButton(R.string.sp_ok_NORMAL, buttonListener);
                dialog = builder.create();
                break;
        }
        if (dialog != null) {
            dialog.setOnKeyListener(this.dialogKeyListener);
        }
        return dialog;
    }

    public void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        if (dialog != null) {
            this.dialogId = id;
            this.mCurrentDialog = dialog;
            CamLog.d(FaceDetector.TAG, "onPrepare");
        }
    }

    public void dismissCurrentDialog() {
        if (this.mCurrentDialog != null) {
            try {
                if (this.dialogId != 0) {
                    this.mGet.getActivity().dismissDialog(this.dialogId);
                    this.dialogId = -1;
                    this.mCurrentDialog = null;
                }
            } catch (IllegalArgumentException e) {
                CamLog.e(FaceDetector.TAG, String.format("dialogId %d is not displaying!", new Object[]{Integer.valueOf(this.dialogId)}), e);
            }
        }
    }

    public void onDismissRotateDialog() {
        if (checkMediator()) {
            RotateDialog dialog = (RotateDialog) this.mRotateDialogs.get(this.dialogId);
            if (dialog != null) {
                dialog.onDismiss();
                this.mRotateDialogs.remove(this.dialogId);
            }
        }
    }

    public void onDismiss() {
        if (checkMediator()) {
            if (isRotateDialogVisible()) {
                if (this.dialogId == DialogCreater.DIALOG_ID_HELP_VOICE_PHOTO) {
                    this.mGet.stopVoiceCommandSound();
                } else if (this.dialogId == DialogCreater.DIALOG_ID_HELP_BEAUTY_SHOT) {
                    if (this.mGet.isPausing()) {
                        this.mGet.doCommand(Command.EXIT_ZOOM_BRIGHTNESS_INTERACTION);
                    } else {
                        this.mGet.doCommandDelayed(Command.EXIT_ZOOM_BRIGHTNESS_INTERACTION, ProjectVariables.keepDuration);
                    }
                } else if (this.dialogId == DialogCreater.DIALOG_ID_HELP_LIVE_EFFECT && this.mGet.isPausing()) {
                    this.mGet.clearSubMenu();
                } else if (this.dialogId == 6) {
                    this.mGet.quickFunctionAllMenuSelected(false);
                } else if (this.dialogId == 26 && this.mGet.isMediaScanning()) {
                    this.mGet.toast(this.mGet.getString(R.string.scanning_sdcard_media_files), false);
                }
                this.mRotateDialogs.remove(this.dialogId);
            }
            this.dialogId = -1;
            this.mCurrentDialog = null;
        }
    }

    public boolean isRotateDialogVisible() {
        if (this.mRotateDialogs.get(this.dialogId) == null) {
            return false;
        }
        return true;
    }

    public void onPause() {
        if (this.dialogId == 6 || this.dialogId == 3 || this.dialogId == 9 || this.dialogId == 18 || this.dialogId == 8) {
            dismissCurrentDialog();
        }
        onDismissRotateDialog();
        super.onPause();
    }

    public void onResume() {
        CamLog.d(FaceDetector.TAG, "onResume-start");
        super.onResume();
    }

    public void onDestroy() {
        if (this.mRotateDialogs != null) {
            this.mRotateDialogs.clear();
            this.mRotateDialogs = null;
        }
        this.mGet = null;
        super.onDestroy();
    }

    public boolean showHelpGuidePopup(String shotModeHelp, final int dialogId, final boolean useCheckBox) {
        SharedPreferences pref = this.mGet.getActivity().getSharedPreferences(Setting.SETTING_PRIMARY, 0);
        if (pref == null || pref.getBoolean(shotModeHelp, false)) {
            return false;
        }
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                DialogController.this.mGet.removePostRunnable(this);
                DialogController.this.showDialogPopup(dialogId, useCheckBox);
            }
        });
        return true;
    }

    public void showProgressDialog() {
        CamLog.d(FaceDetector.TAG, "showProgressDialog");
        if (this.mGet.isPausing()) {
            CamLog.d(FaceDetector.TAG, "showProgressDialog():: (isPausing() == true)");
        } else if (getCurrentDialog() != null) {
        } else {
            if (!isRotateDialogVisible() || getDialogID() != 22) {
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        DialogController.this.mGet.removePostRunnable(this);
                        DialogController.this.showDialogPopup(22);
                    }
                });
            }
        }
    }

    public void deleteProgressDialog() {
        CamLog.d(FaceDetector.TAG, "deleteProgressDialog");
        if (getDialogID() == 22) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    DialogController.this.mGet.removePostRunnable(this);
                    DialogController.this.onDismissRotateDialog();
                }
            });
        }
    }

    public void showSavingProgressDialog() {
        CamLog.d(FaceDetector.TAG, "showSavingProgressDialog");
        if (this.mGet.isPausing()) {
            CamLog.d(FaceDetector.TAG, "showSavingProgressDialog():: (isPausing() == true)");
        } else if (getCurrentDialog() != null) {
        } else {
            if (isRotateDialogVisible() && getDialogID() == 27) {
                CamLog.d(FaceDetector.TAG, "current Dialog is showSavingProgressDialog");
            } else {
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        DialogController.this.mGet.removePostRunnable(this);
                        DialogController.this.showDialogPopup(27);
                    }
                });
            }
        }
    }

    public void deleteSavingProgressDialog() {
        CamLog.d(FaceDetector.TAG, "deleteSavingProgressDialog");
        if (getDialogID() == 27) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    DialogController.this.mGet.removePostRunnable(this);
                    DialogController.this.onDismissRotateDialog();
                }
            });
        }
    }
}
