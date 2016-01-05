package com.lge.camera.command;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class SnapshotEffect extends Command {
    private AnimationListener capturedImageAniListener;

    public SnapshotEffect(ControllerFunction function) {
        super(function);
        this.capturedImageAniListener = new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                View view = SnapshotEffect.this.mGet.findViewById(R.id.on_delay_off_ani);
                if (view != null) {
                    view.clearAnimation();
                    view.setVisibility(8);
                }
            }
        };
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "SnapshotEffect");
        startAnimation(this.capturedImageAniListener, (ImageView) this.mGet.findViewById(R.id.on_delay_off_ani));
    }

    private void startAnimation(AnimationListener aniListener, View captureImageView) {
        captureImageView.setBackgroundColor(-1);
        captureImageView.setVisibility(0);
        AlphaAnimation aa = new AlphaAnimation(0.5f, 0.0f);
        aa.setAnimationListener(aniListener);
        aa.setDuration(200);
        aa.setInterpolator(new DecelerateInterpolator());
        captureImageView.startAnimation(aa);
    }
}
