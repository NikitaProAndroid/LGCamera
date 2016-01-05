package com.lge.camera.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class SwitcherLever extends ImageView implements OnTouchListener {
    protected static final int MINIMUM_ANIMATION_DELTA_TIME = 10;
    protected static final long NO_ANIMATION = -1;
    protected long mAnimationParkingStartTime;
    protected int mBgAlpha;
    protected OnSwitchLeverListener mListener;
    protected int mPosition;
    protected RotationInfo mRotationInfo;
    protected boolean mSwitch;
    protected boolean mSwitchEnable;

    public interface OnSwitchLeverListener {
        boolean onSwitchChanged(SwitcherLever switcherLever, boolean z);
    }

    public SwitcherLever(Context context) {
        super(context);
        this.mSwitch = false;
        this.mPosition = 0;
        this.mAnimationParkingStartTime = 0;
        this.mBgAlpha = Ola_ShotParam.AnimalMask_Random;
        this.mSwitchEnable = false;
        this.mRotationInfo = new RotationInfo();
    }

    public SwitcherLever(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mSwitch = false;
        this.mPosition = 0;
        this.mAnimationParkingStartTime = 0;
        this.mBgAlpha = Ola_ShotParam.AnimalMask_Random;
        this.mSwitchEnable = false;
        this.mRotationInfo = new RotationInfo();
    }

    public SwitcherLever(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mSwitch = false;
        this.mPosition = 0;
        this.mAnimationParkingStartTime = 0;
        this.mBgAlpha = Ola_ShotParam.AnimalMask_Random;
        this.mSwitchEnable = false;
        this.mRotationInfo = new RotationInfo();
    }

    public void setSwitch(boolean onOff) {
        if (this.mSwitch != onOff) {
            this.mSwitch = onOff;
            invalidate();
        }
    }

    public void setSwitchEnable(boolean mEnable) {
        CamLog.d(FaceDetector.TAG, "setSwitchEnable : " + mEnable);
        this.mSwitchEnable = mEnable;
    }

    public boolean isSwitcherPressed() {
        return isPressed();
    }

    protected void tryToSetSwitch(boolean onOff) {
        try {
            if (this.mSwitch == onOff || this.mListener == null || !this.mListener.onSwitchChanged(this, onOff)) {
                startParkingAnimation();
                return;
            }
            this.mSwitch = onOff;
            startParkingAnimation();
        } catch (Throwable th) {
            startParkingAnimation();
        }
    }

    public void setOnSwitchLeverListener(OnSwitchLeverListener listener) {
        this.mListener = listener;
    }

    public boolean isEnabled() {
        return super.isEnabled() ? true : isPressed();
    }

    public boolean onTouchEvent(MotionEvent event) {
        CamLog.d(FaceDetector.TAG, "##MH TOUCH");
        if (isEnabled() && this.mSwitchEnable) {
            switch (event.getAction()) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    CamLog.d(FaceDetector.TAG, "##MH Down");
                    this.mAnimationParkingStartTime = NO_ANIMATION;
                    setPressed(true);
                    trackTouchEvent(event);
                    return true;
                case LGKeyRec.EVENT_NO_MATCH /*1*/:
                    if (!isPressed()) {
                        return true;
                    }
                    trackTouchEvent(event);
                    if (this.mSwitch) {
                        tryToSetSwitch(this.mPosition > (getTouchPositionAvailable() + 2) / 2);
                    } else {
                        boolean z;
                        if (this.mPosition >= getTouchPositionAvailable() / 2) {
                            z = true;
                        } else {
                            z = false;
                        }
                        tryToSetSwitch(z);
                    }
                    setPressed(false);
                    return true;
                case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                    CamLog.d(FaceDetector.TAG, "##MH Move");
                    if (!isPressed()) {
                        return true;
                    }
                    trackTouchEvent(event);
                    return true;
                case LGKeyRec.EVENT_STARTED /*3*/:
                    if (!isPressed()) {
                        return true;
                    }
                    tryToSetSwitch(this.mSwitch);
                    setPressed(false);
                    return true;
                default:
                    return true;
            }
        }
        CamLog.d(FaceDetector.TAG, "##MH TOUCH RETURN" + isEnabled() + "M" + this.mSwitchEnable);
        return false;
    }

    public void resetSwitcherLever(boolean onOff) {
        this.mSwitch = onOff;
        setPressed(false);
        int available = ((getHeight() - getPaddingTop()) - getPaddingBottom()) - getDrawable().getIntrinsicHeight();
        if (this.mSwitch) {
            this.mPosition = available;
        } else {
            this.mPosition = 0;
        }
        invalidate();
    }

    protected void startParkingAnimation() {
        this.mAnimationParkingStartTime = AnimationUtils.currentAnimationTimeMillis();
    }

    protected void trackTouchEvent(MotionEvent event) {
        int drawableHeight = getDrawable().getIntrinsicHeight();
        int available = ((getHeight() - getPaddingTop()) - getPaddingBottom()) - drawableHeight;
        this.mPosition = ((((int) event.getY()) - getPaddingTop()) - getPaddingBottom()) - (drawableHeight / 2);
        if (this.mPosition < 0) {
            this.mPosition = 0;
        }
        if (this.mPosition > available) {
            this.mPosition = available;
        }
        invalidate();
    }

    public void setDegree(int degree) {
        setDegree(degree, true);
    }

    public void setDegree(int degree, boolean animation) {
        if (this.mRotationInfo != null) {
            this.mRotationInfo.setDegree(degree, animation);
        }
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        setPaddingRelative(-2, 0, 0, 0);
        Drawable drawable = getDrawable();
        int drawableHeight = drawable.getIntrinsicHeight();
        int drawableWidth = drawable.getIntrinsicWidth();
        if (drawableWidth != 0 && drawableHeight != 0 && this.mRotationInfo != null) {
            if (this.mAnimationParkingStartTime != NO_ANIMATION) {
                setAnimationStartTime(drawableHeight);
            }
            int offsetTop = getPaddingTop() + this.mPosition;
            int offsetLeft = (((getWidth() - drawableWidth) - getPaddingStart()) - getPaddingEnd()) / 2;
            if (this.mRotationInfo.getCurrentDegree() != this.mRotationInfo.getTargetDegree() && this.mRotationInfo.calcCurrentDegree()) {
                invalidate();
            }
            int saveCount = canvas.getSaveCount();
            canvas.save();
            canvas.translate((float) ((drawableWidth / 2) + offsetLeft), (float) ((drawableHeight / 2) + offsetTop));
            canvas.rotate((float) (-this.mRotationInfo.getCurrentDegree()));
            canvas.translate((float) ((-drawableWidth) / 2), (float) ((-drawableHeight) / 2));
            drawable.draw(canvas);
            canvas.restoreToCount(saveCount);
        }
    }

    protected int getAniPositionAvailable(int drawablePosition) {
        return ((getHeight() - getPaddingTop()) - getPaddingBottom()) - drawablePosition;
    }

    protected void setAnimationStartTime(int drawableHeight) {
        int available = getAniPositionAvailable(drawableHeight);
        long time = AnimationUtils.currentAnimationTimeMillis();
        long deltaTime = time - this.mAnimationParkingStartTime;
        if (deltaTime < 10) {
            deltaTime = 10;
        }
        long j = (long) this.mPosition;
        if (!this.mSwitch) {
            deltaTime = -deltaTime;
        }
        this.mPosition = (int) (j + ((240 * deltaTime) / 1000));
        this.mAnimationParkingStartTime = time;
        if (this.mPosition < 0) {
            this.mPosition = 0;
        }
        if (this.mPosition > available) {
            this.mPosition = available;
        }
        if (this.mPosition == 0 || this.mPosition == available) {
            this.mAnimationParkingStartTime = NO_ANIMATION;
        } else {
            postInvalidate();
        }
    }

    public void addTouchView(View v) {
        v.setOnTouchListener(this);
    }

    public boolean onTouch(View v, MotionEvent event) {
        onTouchEvent(event);
        return true;
    }

    public void startRotation(int degree) {
        if (degree == 90) {
            getBackground().setAlpha(this.mBgAlpha);
        } else {
            getBackground().setAlpha(this.mBgAlpha);
        }
    }

    public void unbind() {
        this.mListener = null;
        Drawable d = getDrawable();
        if (d != null) {
            d.setCallback(null);
            setImageDrawable(null);
        }
        d = getBackground();
        if (d != null) {
            d.setCallback(null);
            setBackground(null);
        }
    }

    public void setSwitcherImage(int orientation, int mode) {
    }

    protected int getTouchPositionAvailable() {
        return ((getHeight() - getPaddingTop()) - getPaddingBottom()) - getDrawable().getIntrinsicHeight();
    }
}
