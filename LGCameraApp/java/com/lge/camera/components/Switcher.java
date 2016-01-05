package com.lge.camera.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.lge.camera.R;

public class Switcher extends RotateImageButton {
    private OnSwitchListener mListener;
    private boolean mSwitch;

    public interface OnSwitchListener {
        void onSwitcherClick(Switcher switcher);
    }

    public Switcher(Context context) {
        super(context);
        this.mSwitch = false;
    }

    public Switcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mSwitch = false;
    }

    public Switcher(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mSwitch = false;
    }

    public void setOnSwitchListener(OnSwitchListener listener) {
        this.mListener = listener;
    }

    public boolean performClick() {
        boolean result = super.performClick();
        if (isEnabled() && this.mListener != null) {
            this.mListener.onSwitcherClick(this);
        }
        return result;
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
            setBackgroundDrawable(null);
        }
    }

    public void setSwitch(boolean onOff) {
        if (this.mSwitch != onOff) {
            this.mSwitch = onOff;
            invalidate();
        }
    }

    public void setSwitcherImage(int orientation, int mode) {
        if (mode == 0) {
            setBackgroundResource(R.drawable.selector_switch_camera_btn);
        } else {
            setBackgroundResource(R.drawable.selector_switch_video_btn);
        }
    }
}
