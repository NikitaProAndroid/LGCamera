package com.lge.camera.command;

import android.widget.ImageView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class UpdateRecIndicator extends Command {
    public UpdateRecIndicator(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "UpdateRecIndicator");
        CamLog.d(FaceDetector.TAG, "video state: " + this.mGet.getVideoState());
        if (this.mGet.findViewById(R.id.rec_indicator) != null) {
            switch (this.mGet.getVideoState()) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    this.mGet.findViewById(R.id.rec_indicator).setVisibility(0);
                    ((ImageView) this.mGet.findViewById(R.id.rec_status_icon)).setImageResource(R.drawable.rec_before);
                case LGKeyRec.EVENT_NO_MATCH /*1*/:
                case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                    this.mGet.findViewById(R.id.rec_indicator).setVisibility(0);
                    ((ImageView) this.mGet.findViewById(R.id.rec_status_icon)).setImageResource(R.drawable.rec_before);
                case LGKeyRec.EVENT_STARTED /*3*/:
                    this.mGet.findViewById(R.id.rec_indicator).setVisibility(0);
                    ((ImageView) this.mGet.findViewById(R.id.rec_status_icon)).setImageResource(R.drawable.rec_recording);
                    ((ImageView) this.mGet.findViewById(R.id.rec_status_text)).setVisibility(0);
                case LGKeyRec.EVENT_STOPPED /*4*/:
                    this.mGet.findViewById(R.id.rec_indicator).setVisibility(0);
                    ImageView recIcon = (ImageView) this.mGet.findViewById(R.id.rec_status_icon);
                    recIcon.setImageResource(R.drawable.rec_pause);
                    recIcon.setVisibility(0);
                    ((ImageView) this.mGet.findViewById(R.id.rec_status_text)).setVisibility(4);
                case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                    this.mGet.findViewById(R.id.rec_indicator).setVisibility(4);
                    ((ImageView) this.mGet.findViewById(R.id.rec_status_icon)).setImageResource(R.drawable.rec_before);
                default:
            }
        }
    }
}
