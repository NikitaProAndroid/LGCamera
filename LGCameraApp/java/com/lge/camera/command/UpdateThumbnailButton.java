package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class UpdateThumbnailButton extends Command {
    public UpdateThumbnailButton(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "UpdateThumbnailButton()");
        this.mGet.removeScheduledCommand(Command.UPDATE_THUMBNAIL_BUTTON);
        if (this.mGet.checkUpdateThumbnail()) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    UpdateThumbnailButton.this.mGet.removePostRunnable(this);
                    UpdateThumbnailButton.this.mGet.updateThumbnailButtonVisibility();
                    UpdateThumbnailButton.this.mGet.getThumbnailAndUpdateButton();
                }
            });
        } else {
            CamLog.d(FaceDetector.TAG, "UpdateThumbnailButton() return");
        }
    }
}
