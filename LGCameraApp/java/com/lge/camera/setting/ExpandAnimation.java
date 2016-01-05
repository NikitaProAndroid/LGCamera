package com.lge.camera.setting;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;

public class ExpandAnimation extends Animation {
    private View mAnimatedView;
    private Context mContext;
    private boolean mIsVisibleAfter;
    private int mMarginEnd;
    private int mMarginGap;
    private int mMarginStart;
    private LayoutParams mViewLayoutParams;
    private boolean mWasEndedAlready;

    public ExpandAnimation(Context context, View view, int duration, boolean isOpen) {
        int height;
        this.mIsVisibleAfter = false;
        this.mWasEndedAlready = false;
        this.mContext = context;
        setInterpolator(this.mContext, 17432582);
        setDuration((long) duration);
        this.mAnimatedView = view;
        this.mViewLayoutParams = (LayoutParams) this.mAnimatedView.getLayoutParams();
        this.mAnimatedView.measure(MeasureSpec.makeMeasureSpec(0, 0), MeasureSpec.makeMeasureSpec(0, 0));
        this.mViewLayoutParams.height = this.mAnimatedView.getMeasuredHeight();
        this.mIsVisibleAfter = !isOpen;
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
        this.mMarginGap = this.mMarginEnd - this.mMarginStart;
        this.mAnimatedView.setVisibility(0);
    }

    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if (interpolatedTime < 0.9f) {
            this.mViewLayoutParams.bottomMargin = this.mMarginStart + ((int) (((float) this.mMarginGap) * interpolatedTime));
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
