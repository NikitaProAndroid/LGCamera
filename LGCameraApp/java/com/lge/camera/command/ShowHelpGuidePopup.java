package com.lge.camera.command;

import android.os.Bundle;
import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.DialogCreater;
import com.lge.olaworks.library.FaceDetector;

public class ShowHelpGuidePopup extends Command {
    public ShowHelpGuidePopup(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        execute(new Bundle());
    }

    public void execute(Object arg) {
        CamLog.d(FaceDetector.TAG, "ShowHelpGuidePopup - start");
        int dialog_id = ((Bundle) arg).getInt("dialog_id", 0);
        if (dialog_id == 0) {
            String helpGuide = null;
            try {
                if (checkMediator()) {
                    helpGuide = this.mGet.getSettingParameterValue();
                }
                CamLog.d(FaceDetector.TAG, "helpGuide = " + helpGuide);
                if (helpGuide != null) {
                    dialog_id = DialogCreater.getHelpDialogId(helpGuide);
                }
            } catch (NullPointerException e) {
                CamLog.w(FaceDetector.TAG, "NullPointerException:", e);
            }
        }
        showHelpGuideDialogPopup(dialog_id);
    }

    public void showHelpGuideDialogPopup(int dialog_id) {
        switch (dialog_id) {
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
            case DialogCreater.DIALOG_ID_HELP_CLEAR_SHOT /*115*/:
            case DialogCreater.DIALOG_ID_HELP_DUAL_CAMERA /*116*/:
            case DialogCreater.DIALOG_ID_HELP_SMART_ZOOM_RECORDING /*117*/:
            case DialogCreater.DIALOG_ID_HELP_HDR_MOVIE /*118*/:
            case DialogCreater.DIALOG_ID_HELP_PLANE_PANORAMA /*122*/:
                this.mGet.showDialogPopup(dialog_id);
            default:
        }
    }
}
