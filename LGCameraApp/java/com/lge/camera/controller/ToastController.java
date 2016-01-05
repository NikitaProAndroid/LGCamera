package com.lge.camera.controller;

import android.os.Handler;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.OnScreenHint;
import com.lge.olaworks.library.FaceDetector;

public class ToastController extends Controller {
    private final Handler mHandler;
    private final Runnable mHide;
    private boolean mIsAllowDisturb;
    private int mOrientation;
    private final Runnable mResetAllowDisturb;
    private OnScreenHint mStorageToast;
    private final Runnable mStorageToastHide;
    private OnScreenHint mToast;

    public ToastController(ControllerFunction function) {
        super(function);
        this.mToast = null;
        this.mStorageToast = null;
        this.mOrientation = 0;
        this.mHandler = new Handler();
        this.mIsAllowDisturb = true;
        this.mResetAllowDisturb = new Runnable() {
            public void run() {
                ToastController.this.mIsAllowDisturb = true;
            }
        };
        this.mHide = new Runnable() {
            public void run() {
                ToastController.this.hide(false);
            }
        };
        this.mStorageToastHide = new Runnable() {
            public void run() {
                ToastController.this.storageToasthide(false);
            }
        };
    }

    public void show(String message) {
        show(message, false);
    }

    public void show(String message, boolean immediately) {
        show(message, immediately, this.mGet.getOrientation());
    }

    public synchronized void show(String message, boolean immediately, int orientation) {
        if (isShowing() && this.mIsAllowDisturb) {
            this.mHandler.removeCallbacks(this.mHide);
            hide();
        }
        this.mToast = OnScreenHint.makeText(this.mGet.getActivity(), message, orientation);
        if (this.mToast != null) {
            if (immediately) {
                this.mToast.showImmediately();
            } else {
                this.mToast.show();
            }
            this.mOrientation = orientation;
        }
    }

    private void hideAndResetDisturb(long hideDelayMillis) {
        this.mHandler.removeCallbacks(this.mHide);
        if (!this.mHandler.postDelayed(this.mHide, hideDelayMillis)) {
            hide(true);
        }
        this.mHandler.removeCallbacks(this.mResetAllowDisturb);
        if (!this.mHandler.postDelayed(this.mResetAllowDisturb, hideDelayMillis)) {
            this.mIsAllowDisturb = true;
        }
    }

    private synchronized boolean checkDisturb(boolean needDisturb, long hideDelayMillis) {
        boolean needStopShow;
        needStopShow = false;
        if (this.mIsAllowDisturb) {
            if (!needDisturb) {
                this.mIsAllowDisturb = false;
                this.mHandler.removeCallbacks(this.mHide);
                hide(false);
                hideAndResetDisturb(hideDelayMillis);
            }
        } else if (needDisturb) {
            this.mHandler.removeCallbacks(this.mResetAllowDisturb);
            this.mIsAllowDisturb = true;
        } else {
            hideAndResetDisturb(hideDelayMillis);
            needStopShow = true;
        }
        return needStopShow;
    }

    public void showShortToast(String message) {
        showShortToast(message, true);
    }

    public void showShortToast(String message, boolean needDisturb) {
        show(message, (long) CameraConstants.TOAST_LENGTH_SHORT, needDisturb);
    }

    public void showLongToast(String message) {
        showLongToast(message, true);
    }

    public void showLongToast(String message, boolean needDisturb) {
        show(message, (long) CameraConstants.TOAST_LENGTH_LONG, needDisturb);
    }

    public void show(String message, long hideDelayMillis) {
        show(message, hideDelayMillis, true);
    }

    public void show(String message, long hideDelayMillis, boolean needDisturb) {
        final boolean z = needDisturb;
        final long j = hideDelayMillis;
        final String str = message;
        this.mGet.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (!ToastController.this.checkDisturb(z, j)) {
                    ToastController.this.show(str);
                    if (ToastController.this.mIsAllowDisturb && !ToastController.this.mHandler.postDelayed(ToastController.this.mHide, j)) {
                        ToastController.this.hide(true);
                    }
                }
            }
        });
    }

    public void hide() {
        hide(false);
    }

    public synchronized void hide(boolean immediately) {
        if (this.mToast != null) {
            if (immediately) {
                this.mToast.cancelImmediately();
            } else {
                this.mToast.cancel();
            }
        }
        this.mToast = null;
    }

    public synchronized boolean hideForPhotoStory() {
        boolean bRet;
        bRet = false;
        if (this.mToast != null) {
            this.mToast.cancelImmediately();
            bRet = true;
        }
        if (this.mStorageToast != null) {
            this.mStorageToast.cancelImmediately();
            bRet = true;
        }
        return bRet;
    }

    public synchronized void storageToastShow(String message, boolean immediately, boolean shortToast) {
        if (isStorageToastShowing()) {
            this.mHandler.removeCallbacks(this.mStorageToastHide);
            storageToasthide(false);
        }
        int orientation = this.mGet.getOrientation();
        this.mStorageToast = OnScreenHint.makeText(this.mGet.getActivity(), message, orientation, 1);
        if (this.mStorageToast != null) {
            if (immediately) {
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        ToastController.this.mGet.removePostRunnable(this);
                        ToastController.this.mStorageToast.showImmediately();
                    }
                });
            } else {
                this.mStorageToast.show();
            }
            this.mOrientation = orientation;
        }
        if (shortToast && !this.mHandler.postDelayed(this.mStorageToastHide, CameraConstants.TOAST_LENGTH_SHORT)) {
            storageToasthide(true);
        }
    }

    public synchronized void storageToasthide(boolean immediately) {
        if (this.mStorageToast != null) {
            if (immediately) {
                this.mGet.runOnUiThread(new Runnable() {
                    public void run() {
                        ToastController.this.mGet.removePostRunnable(this);
                        ToastController.this.mStorageToast.cancelImmediately();
                        ToastController.this.mStorageToast = null;
                    }
                });
            } else {
                this.mStorageToast.cancel();
                this.mStorageToast = null;
            }
        }
    }

    public void rotate() {
        rotate(this.mGet.getOrientation());
    }

    public synchronized void rotate(int orientation) {
        if (this.mOrientation != orientation) {
            if (this.mToast != null) {
                this.mToast.cancel();
                this.mToast = OnScreenHint.changeOrientation(this.mGet.getActivity(), orientation);
            }
            if (this.mStorageToast != null) {
                this.mStorageToast.cancel();
                this.mStorageToast = OnScreenHint.changeOrientation(this.mGet.getActivity(), orientation, 1);
            }
            this.mOrientation = orientation;
        }
    }

    public boolean isShowing() {
        return this.mToast != null;
    }

    public boolean isStorageToastShowing() {
        return this.mStorageToast != null;
    }

    public void onResume() {
        CamLog.d(FaceDetector.TAG, "onResume");
        super.onResume();
    }

    public void onPause() {
        CamLog.d(FaceDetector.TAG, "onPause");
        if (this.mToast != null) {
            this.mHandler.removeCallbacks(this.mHide);
            this.mToast.cancel();
            this.mToast = null;
        }
        if (this.mStorageToast != null) {
            this.mHandler.removeCallbacks(this.mStorageToastHide);
            this.mStorageToast.cancel();
            this.mStorageToast = null;
        }
        super.onPause();
    }
}
