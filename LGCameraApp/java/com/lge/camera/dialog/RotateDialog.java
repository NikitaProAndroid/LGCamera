package com.lge.camera.dialog;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.Util;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Limit;
import com.lge.olaworks.library.FaceDetector;

public class RotateDialog {
    protected ControllerFunction mGet;
    private OnTouchListener mOnBackCoverTouchListener;
    protected int mOrientation;
    protected View mView;

    protected RotateDialog(ControllerFunction function) {
        this.mOrientation = -1;
        this.mGet = null;
        this.mOnBackCoverTouchListener = new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (RotateDialog.this.mView != null && event.getAction() == 0) {
                    float x = event.getX();
                    float y = event.getY();
                    View rdl = RotateDialog.this.mView.findViewById(R.id.rotate_dialog_layout);
                    if (rdl != null && (x < ((float) rdl.getLeft()) || x > ((float) rdl.getRight()) || y < ((float) rdl.getTop()) || y > ((float) rdl.getBottom()))) {
                        RotateDialog.this.onDismiss();
                    }
                }
                return false;
            }
        };
        this.mGet = function;
    }

    protected void create(View v) {
        create(v, true);
    }

    protected void create(View v, boolean isCloseByBackCoverTouch) {
        this.mView = v;
        if (this.mGet != null) {
            ((ViewGroup) this.mGet.getActivity().findViewById(R.id.init)).addView(v);
            LayoutParams params = (LayoutParams) v.getLayoutParams();
            params.addRule(13, 1);
            v.setLayoutParams(params);
            this.mGet.postOnUiThread(new Runnable() {
                public void run() {
                    RotateDialog.this.mGet.removePostRunnable(this);
                    if (RotateDialog.this.mView != null) {
                        RotateDialog.this.onPrepare();
                        RotateDialog.this.mView.setVisibility(0);
                        RotateDialog.this.requestFocus();
                    }
                }
            });
            showBackcoverAnimation();
            if (isCloseByBackCoverTouch) {
                this.mView.findViewById(R.id.backcover).setOnTouchListener(this.mOnBackCoverTouchListener);
            }
        }
    }

    public void requestFocus() {
        if (((AccessibilityManager) this.mGet.getApplicationContext().getSystemService("accessibility")).isEnabled()) {
            this.mGet.getHandler().postDelayed(new Thread(new Runnable() {
                public void run() {
                    RotateDialog.this.mGet.removeCallbacks(this);
                    if (RotateDialog.this.mView != null) {
                        ViewGroup dialog = (ViewGroup) RotateDialog.this.mView.findViewById(R.id.rotate_dialog_layout);
                        dialog.setFocusableInTouchMode(true);
                        Common.setContentDescriptionForAccessibility(RotateDialog.this.mGet.getApplicationContext(), dialog);
                        dialog.sendAccessibilityEvent(32768);
                        dialog.requestFocus();
                    }
                }
            }), 500);
        }
    }

    protected void alignButtonLine() {
        if (this.mView != null) {
            Button btnOk = (Button) this.mView.findViewById(R.id.ok_button);
            Button btnCancel = (Button) this.mView.findViewById(R.id.cancel_button);
            if (btnOk != null && btnCancel != null) {
                int lineCountBtnOk = btnOk.getLineCount();
                int lineCountBtnCancel = btnCancel.getLineCount();
                if (lineCountBtnOk > lineCountBtnCancel) {
                    btnCancel.setLines(lineCountBtnOk);
                }
                if (lineCountBtnCancel > lineCountBtnOk) {
                    btnOk.setLines(lineCountBtnCancel);
                }
            }
        }
    }

    public void onPrepare() {
        alignButtonLine();
    }

    public void onDismiss() {
        if (this.mGet != null && this.mView != null) {
            ViewGroup dialog = (ViewGroup) this.mView.findViewById(R.id.rotate_dialog_layout);
            dialog.setContentDescription("\u00a0");
            dialog.clearFocus();
            dialog.sendAccessibilityEvent(65536);
            ((ViewGroup) this.mGet.getActivity().findViewById(R.id.init)).removeView(this.mView);
            this.mGet.dialogControllerOnDismiss();
            this.mOrientation = -1;
            this.mView = null;
        }
    }

    public void startRotation(int degree) {
        CamLog.d(FaceDetector.TAG, "RotatableDialog startRotataion(degree) start = " + degree);
        if (this.mGet != null && this.mGet.getActivity().findViewById(R.id.rotate_dialog_layout) != null) {
            if (this.mOrientation == degree) {
                CamLog.d(FaceDetector.TAG, "RotatableDialog startRotataion : rotate same.");
                return;
            }
            this.mOrientation = degree;
            hideRotateDialogAnimation();
            if (ModelProperties.isSoftKeyNavigationBarModel()) {
                RelativeLayout marginLayout = (RelativeLayout) this.mView.findViewById(R.id.rotate_dialog_margin_layout);
                LayoutParams marginParams = (LayoutParams) marginLayout.getLayoutParams();
                Common.resetLayoutParameter(marginParams);
                int naviMargin = Common.getPixelFromDimens(this.mGet.getApplicationContext(), R.dimen.preview_panel_marginBottom);
                if (Util.isConfigureLandscape(this.mGet.getResources())) {
                    marginParams.addRule(20, 1);
                    marginParams.rightMargin = naviMargin;
                } else {
                    marginParams.addRule(10, 1);
                    marginParams.bottomMargin = naviMargin;
                }
                marginLayout.setLayoutParams(marginParams);
            }
            ((RotateLayout) this.mGet.findViewById(R.id.rotate_dialog_layout)).rotateLayout(degree);
            showRotateDialogAnimation();
            CamLog.d(FaceDetector.TAG, "RotatableDialog startRotataion(degree) end = " + degree);
        }
    }

    protected void startAlphaAnimation(View v, int start, int end, int duration) {
        Animation anim = new AlphaAnimation((float) start, (float) end);
        anim.setDuration((long) duration);
        v.startAnimation(anim);
    }

    protected void showBackcoverAnimation() {
        if (this.mView != null) {
            startAlphaAnimation((RelativeLayout) this.mView.findViewById(R.id.backcover), 0, 1, LGT_Limit.IMAGE_SIZE_WALLPAPER_HEIGHT);
        }
    }

    protected void showRotateDialogAnimation() {
        if (this.mView != null) {
            startAlphaAnimation(this.mView.findViewById(R.id.rotate_dialog_layout), 0, 1, Limit.OLA_FIFO_DATA_MAX_SIZE_BYTE);
        }
    }

    protected void hideRotateDialogAnimation() {
        if (this.mView != null) {
            startAlphaAnimation(this.mView.findViewById(R.id.rotate_dialog_layout), 1, 0, Limit.OLA_FIFO_DATA_MAX_SIZE_BYTE);
        }
    }
}
