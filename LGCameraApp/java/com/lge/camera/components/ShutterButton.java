package com.lge.camera.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnLongClickListener;
import com.lge.camera.properties.FunctionProperties;

public class ShutterButton extends RotateImageButton implements OnLongClickListener {
    private boolean isShutterButtonFocusEnable;
    private boolean isShutterButtonReleaseImmediately;
    private OnShutterButtonListener mListener;
    private OnShutterButtonLongPressListener mLongPressListener;
    private boolean mOldPressed;
    private boolean mShutterButtonClicked;

    public interface OnShutterButtonListener {
        void onShutterButtonClick(ShutterButton shutterButton);

        void onShutterButtonFocus(ShutterButton shutterButton, boolean z);
    }

    public interface OnShutterButtonLongPressListener {
        void onShutterButtonLongPressed(ShutterButton shutterButton);
    }

    public ShutterButton(Context context) {
        super(context);
        this.mShutterButtonClicked = false;
        this.isShutterButtonFocusEnable = true;
        this.isShutterButtonReleaseImmediately = false;
        setOnLongClickListener(this);
    }

    public ShutterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mShutterButtonClicked = false;
        this.isShutterButtonFocusEnable = true;
        this.isShutterButtonReleaseImmediately = false;
        setOnLongClickListener(this);
    }

    public ShutterButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mShutterButtonClicked = false;
        this.isShutterButtonFocusEnable = true;
        this.isShutterButtonReleaseImmediately = false;
        setOnLongClickListener(this);
    }

    public void setOnShutterButtonListener(OnShutterButtonListener listener) {
        this.mListener = listener;
    }

    public void setOnShutterButtonLongPressListener(OnShutterButtonLongPressListener listener) {
        this.mLongPressListener = listener;
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        final boolean pressed = isPressed();
        if (pressed != this.mOldPressed) {
            this.mOldPressed = pressed;
            if (pressed) {
                callShutterButtonFocus(pressed);
            } else if (this.isShutterButtonReleaseImmediately) {
                callShutterButtonFocus(pressed);
                this.isShutterButtonReleaseImmediately = false;
            } else {
                post(new Runnable() {
                    public void run() {
                        ShutterButton.this.callShutterButtonFocus(pressed);
                    }
                });
            }
        }
    }

    private void callShutterButtonFocus(boolean pressed) {
        if (isShutterButtonFocusEnable()) {
            this.mListener.onShutterButtonFocus(this, pressed);
            if (!pressed) {
                this.mShutterButtonClicked = false;
            }
        }
    }

    public boolean isShutterButtonClicked() {
        return this.mShutterButtonClicked;
    }

    public boolean performClick() {
        boolean result = super.performClick();
        if (isShutterButtonFocusEnable()) {
            this.mShutterButtonClicked = true;
            this.mListener.onShutterButtonClick(this);
        }
        return result;
    }

    public void performFocus(boolean pressed) {
        if (isShutterButtonFocusEnable()) {
            this.mListener.onShutterButtonFocus(this, pressed);
        }
    }

    public boolean onLongClick(View v) {
        if (FunctionProperties.isSupportShutterButtonBurst() && isEnabled() && this.mLongPressListener != null) {
            this.mLongPressListener.onShutterButtonLongPressed(this);
        }
        return false;
    }

    public boolean isShutterButtonFocusEnable() {
        return isEnabled() && this.mListener != null && this.isShutterButtonFocusEnable;
    }

    public void setShutterButtonFocusEnable(boolean enable) {
        this.isShutterButtonFocusEnable = enable;
    }

    public void setShutterButtonReleaseImmediately(boolean enable) {
        this.isShutterButtonReleaseImmediately = enable;
    }

    public void unbind() {
        this.mListener = null;
        this.mLongPressListener = null;
        this.isShutterButtonFocusEnable = true;
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
}
