package com.lge.camera.postview;

import android.content.Context;
import android.os.Handler;
import com.lge.camera.util.OnScreenHint;

public class PostViewToast {
    public static final long TOAST_LENGTH_LONG = 5000;
    public static final long TOAST_LENGTH_NORMAL = 3000;
    public static final long TOAST_LENGTH_SHORT = 2000;
    private Handler mHandler;
    private Runnable mHide;
    private OnScreenHint mOnScreenHint;

    public PostViewToast() {
        this.mOnScreenHint = null;
        this.mHandler = new Handler();
        this.mHide = new Runnable() {
            public void run() {
                PostViewToast.this.cancel(true);
            }
        };
    }

    public void show(Context context, String message, int orientation) {
        show(context, message, false, orientation);
    }

    public synchronized void show(Context context, String message, boolean immediately, int orientation) {
        if (isShowing()) {
            this.mHandler.removeCallbacks(this.mHide);
            cancel();
        }
        this.mOnScreenHint = OnScreenHint.makeText(context, message, orientation);
        if (this.mOnScreenHint != null) {
            if (immediately) {
                this.mOnScreenHint.showImmediately();
            } else {
                this.mOnScreenHint.show();
            }
        }
    }

    public void show(Context context, String message, long hideDelayMillis, int orientation) {
        if (hideDelayMillis <= 0) {
            hideDelayMillis = TOAST_LENGTH_SHORT;
        }
        show(context, message, orientation);
        this.mHandler.postDelayed(this.mHide, hideDelayMillis);
    }

    public void cancel() {
        cancel(false);
    }

    public synchronized void cancel(boolean immediately) {
        if (this.mOnScreenHint != null) {
            if (immediately) {
                this.mOnScreenHint.cancelImmediately();
            } else {
                this.mOnScreenHint.cancel();
            }
        }
        this.mOnScreenHint = null;
    }

    public boolean isShowing() {
        return this.mOnScreenHint != null;
    }

    public void unbind() {
        this.mOnScreenHint = null;
        this.mHide = null;
        this.mHandler = null;
    }
}
