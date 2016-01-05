package com.lge.camera.setting;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;
import com.lge.camera.components.RotateView;

public class ExpandAnimationToggle extends Animation {
    private View mAnimatedView;
    private Context mContext;
    private boolean mIsVisibleAfter;
    private int mMarginEnd;
    private int mMarginStart;
    private LayoutParams mViewLayoutParams;
    private boolean mWasEndedAlready;

    public ExpandAnimationToggle(Context context, View view, int duration) {
        int height;
        this.mIsVisibleAfter = false;
        this.mWasEndedAlready = false;
        this.mContext = context;
        setInterpolator(this.mContext, 17432580);
        setDuration((long) duration);
        this.mAnimatedView = view;
        this.mViewLayoutParams = (LayoutParams) this.mAnimatedView.getLayoutParams();
        this.mAnimatedView.measure(MeasureSpec.makeMeasureSpec(0, 0), MeasureSpec.makeMeasureSpec(0, 0));
        this.mViewLayoutParams.height = this.mAnimatedView.getMeasuredHeight();
        this.mIsVisibleAfter = view.getVisibility() == 0;
        if (!this.mIsVisibleAfter) {
            this.mViewLayoutParams.bottomMargin = -this.mViewLayoutParams.height;
        }
        this.mMarginStart = this.mViewLayoutParams.bottomMargin;
        if (this.mMarginStart == 0) {
            height = 0 - view.getHeight();
        } else {
            height = 0;
        }
        this.mMarginEnd = height;
        this.mAnimatedView.setVisibility(0);
        setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                if (ExpandAnimationToggle.this.mAnimatedView.getVisibility() != 0) {
                    ((ViewGroup) ExpandAnimationToggle.this.mAnimatedView).removeAllViews();
                }
            }
        });
    }

    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if (interpolatedTime < RotateView.DEFAULT_TEXT_SCALE_X) {
            this.mViewLayoutParams.bottomMargin = this.mMarginStart + ((int) (((float) (this.mMarginEnd - this.mMarginStart)) * interpolatedTime));
            this.mAnimatedView.requestLayout();
        } else if (!this.mWasEndedAlready) {
            this.mViewLayoutParams.bottomMargin = this.mMarginEnd;
            this.mAnimatedView.requestLayout();
            if (this.mIsVisibleAfter) {
                this.mAnimatedView.setVisibility(8);
            }
            this.mWasEndedAlready = true;
        }
    }
}
