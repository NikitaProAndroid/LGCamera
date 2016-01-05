package com.lge.camera.components;

import android.view.animation.AnimationUtils;
import com.lge.camera.properties.CameraConstants;
import com.lge.morpho.app.morphopanorama.PanoramaApplication;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;

public class RotationInfo {
    protected static final int ANIMATION_SPEED = 240;
    protected long mAnimationEndTime;
    protected long mAnimationStartTime;
    protected boolean mClockwise;
    protected int mCurrentDegree;
    protected int mStartDegree;
    protected int mTargetDegree;

    public RotationInfo() {
        this.mCurrentDegree = 0;
        this.mStartDegree = 0;
        this.mTargetDegree = 0;
        this.mAnimationStartTime = 0;
        this.mAnimationEndTime = 0;
        this.mClockwise = false;
    }

    public int getCurrentDegree() {
        return this.mCurrentDegree;
    }

    public void setCurrentDegree(int mCurrentDegree) {
        this.mCurrentDegree = mCurrentDegree;
    }

    public int getStartDegree() {
        return this.mStartDegree;
    }

    public void setStartDegree(int mStartDegree) {
        this.mStartDegree = mStartDegree;
    }

    public int getTargetDegree() {
        return this.mTargetDegree;
    }

    public void setTargetDegree(int mTargetDegree) {
        this.mTargetDegree = mTargetDegree;
    }

    public long getAnimationStartTime() {
        return this.mAnimationStartTime;
    }

    public void setAnimationStartTime(long mAnimationStartTime) {
        this.mAnimationStartTime = mAnimationStartTime;
    }

    public long getAnimationEndTime() {
        return this.mAnimationEndTime;
    }

    public void setAnimationEndTime(long mAnimationEndTime) {
        this.mAnimationEndTime = mAnimationEndTime;
    }

    public boolean isClockwise() {
        return this.mClockwise;
    }

    public void setClockwise(boolean mClockwise) {
        this.mClockwise = mClockwise;
    }

    public void setDegree(int degree) {
        setDegree(degree, true);
    }

    public void setDegree(int degree, boolean animation) {
        degree = degree >= 0 ? degree % CameraConstants.DEGREE_360 : (degree % CameraConstants.DEGREE_360) + CameraConstants.DEGREE_360;
        if (degree != this.mTargetDegree) {
            if (animation) {
                this.mTargetDegree = degree;
                this.mStartDegree = this.mCurrentDegree;
            } else {
                this.mCurrentDegree = degree;
                this.mStartDegree = degree;
                this.mTargetDegree = degree;
            }
            this.mAnimationStartTime = AnimationUtils.currentAnimationTimeMillis();
            int diff = this.mTargetDegree - this.mCurrentDegree;
            if (diff < 0) {
                diff += CameraConstants.DEGREE_360;
            }
            if (diff > MediaProviderUtils.ROTATION_180) {
                diff -= 360;
            }
            this.mClockwise = diff >= 0;
            this.mAnimationEndTime = this.mAnimationStartTime + ((long) ((Math.abs(diff) * PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME) / ANIMATION_SPEED));
        }
    }

    public boolean calcCurrentDegree() {
        long time = AnimationUtils.currentAnimationTimeMillis();
        if (time < this.mAnimationEndTime) {
            int deltaTime = (int) (time - this.mAnimationStartTime);
            int i = this.mStartDegree;
            if (!this.mClockwise) {
                deltaTime = -deltaTime;
            }
            int degree = i + ((deltaTime * ANIMATION_SPEED) / PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME);
            this.mCurrentDegree = degree >= 0 ? degree % CameraConstants.DEGREE_360 : (degree % CameraConstants.DEGREE_360) + CameraConstants.DEGREE_360;
            return true;
        }
        this.mCurrentDegree = this.mTargetDegree;
        return false;
    }
}
