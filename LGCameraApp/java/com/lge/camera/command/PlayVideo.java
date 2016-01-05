package com.lge.camera.command;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class PlayVideo extends Command {
    public PlayVideo(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "PlayVideo");
        Intent intent = createPlayIntent(this.mGet.getVideoFile().getUri());
        intent.putExtra("android.intent.extra.finishOnCompletion", true);
        try {
            this.mGet.getActivity().startActivity(Intent.createChooser(intent, this.mGet.getString(R.string.sp_play_twoline_SHORT)));
        } catch (ActivityNotFoundException ex) {
            CamLog.e(FaceDetector.TAG, "ActivityNotFoundException:", ex);
            this.mGet.toast((int) R.string.error_not_exist_app);
        }
    }

    public Intent createPlayIntent(Uri uri) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, MultimediaProperties.VIDEO_MIME_TYPE);
        intent.putExtra("mimeType", MultimediaProperties.VIDEO_MIME_TYPE);
        return intent;
    }
}
