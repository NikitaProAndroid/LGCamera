package com.lge.camera.util;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import com.lge.camera.components.RotateView;

public class AnimationUtil {
    public static void startAlphaAnimation(View view, float start, float end, long duration, AnimationListener al) {
        if (view != null) {
            Animation anim = new AlphaAnimation(start, end);
            anim.setDuration(duration);
            anim.setAnimationListener(al);
            view.clearAnimation();
            view.startAnimation(anim);
        }
    }

    public static void startShowingAnimation(View aniView, boolean show, long duration, AnimationListener listener, boolean showAgain) {
        int i = 0;
        float end = RotateView.DEFAULT_TEXT_SCALE_X;
        if (aniView != null) {
            float start;
            aniView.clearAnimation();
            if (!showAgain) {
                int i2;
                int visibility = aniView.getVisibility();
                if (show) {
                    i2 = 0;
                } else {
                    i2 = 4;
                }
                if (visibility == i2) {
                    return;
                }
            }
            if (!show) {
                i = 4;
            }
            aniView.setVisibility(i);
            if (show) {
                start = 0.0f;
            } else {
                start = RotateView.DEFAULT_TEXT_SCALE_X;
            }
            if (!show) {
                end = 0.0f;
            }
            Animation ani = new AlphaAnimation(start, end);
            ani.setDuration(duration);
            ani.setInterpolator(new DecelerateInterpolator());
            ani.setAnimationListener(listener);
            aniView.startAnimation(ani);
        }
    }

    public static void startAnimationList(ImageView view, boolean show) {
        AnimationDrawable ad = (AnimationDrawable) view.getBackground();
        if (show) {
            ad.start();
        } else {
            ad.stop();
        }
    }
}
