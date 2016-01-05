package com.lge.camera.controller;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.Mediator;
import com.lge.camera.R;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.components.RotateTextView;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.util.Locale;

public class BubblePopupController {
    private static BubblePopupController mBubblePopupController;
    private int mBubblePopupType;
    private boolean mNotiComplete;
    private View mPopupView;

    public BubblePopupController() {
        this.mBubblePopupType = 0;
        this.mPopupView = null;
        this.mNotiComplete = false;
    }

    static {
        mBubblePopupController = null;
    }

    public static BubblePopupController get() {
        if (mBubblePopupController == null) {
            mBubblePopupController = new BubblePopupController();
        }
        return mBubblePopupController;
    }

    public void unbind() {
        mBubblePopupController = null;
    }

    public void showBubblePopup(Mediator mediator, int popupType, long duration) {
        boolean result = false;
        ViewGroup vg;
        View view;
        switch (popupType) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                if (!(mediator == null || !mediator.isTimeMachineModeOn() || this.mNotiComplete)) {
                    vg = (ViewGroup) mediator.findViewById(R.id.init);
                    if (this.mPopupView == null) {
                        this.mPopupView = mediator.inflateView(R.layout.bubble_popup);
                    }
                    if (!(vg == null || this.mPopupView == null)) {
                        vg.addView(this.mPopupView);
                        view = mediator.findViewById(R.id.bubble_popup_layout);
                        this.mBubblePopupType = popupType;
                        startRotation(mediator, mediator.getOrientationDegree(), false);
                        setBubblePopupAnimation(mediator, view, true);
                        result = true;
                        this.mNotiComplete = true;
                        break;
                    }
                }
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                if (!(mediator == null || !mediator.checkSettingValue(Setting.KEY_CAMERA_SHOT_MODE, CameraConstants.TYPE_SHOTMODE_REFOCUS) || this.mNotiComplete)) {
                    vg = (ViewGroup) mediator.findViewById(R.id.init);
                    if (this.mPopupView == null) {
                        this.mPopupView = mediator.inflateView(R.layout.bubble_popup);
                    }
                    if (!(vg == null || this.mPopupView == null)) {
                        vg.addView(this.mPopupView);
                        view = mediator.findViewById(R.id.bubble_popup_layout);
                        this.mBubblePopupType = popupType;
                        startRotation(mediator, mediator.getOrientationDegree(), false);
                        setBubblePopupAnimation(mediator, view, true);
                        result = true;
                        this.mNotiComplete = true;
                        break;
                    }
                }
        }
        if (result) {
            removeBubblePopup(mediator, duration);
        }
    }

    private void setLayout(Mediator mediator, RotateLayout rl, int degree) {
        String currentLanguage = Locale.getDefault().getLanguage();
        if (mediator != null && rl != null) {
            LayoutParams params = (LayoutParams) rl.getLayoutParams();
            View bubbleView = mediator.findViewById(R.id.bubble_popup);
            rl.measure(0, 0);
            RotateTextView rtv = (RotateTextView) mediator.findViewById(R.id.bubble_popup_message);
            switch (this.mBubblePopupType) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                case LGKeyRec.EVENT_NO_MATCH /*1*/:
                    int tempBottomMargin;
                    String message = mediator.getString(R.string.sp_timemachine_bubble_popup_NORMAL);
                    int i = this.mBubblePopupType;
                    if (r0 == 1) {
                        message = mediator.getString(R.string.sp_refocus_bubble_popup_NORMAL);
                    }
                    if (ModelProperties.getCarrierCode() == 4 && this.mBubblePopupType == 0) {
                        message = mediator.getString(R.string.sp_timemachine_bubble_popup_jp_NORMAL);
                    }
                    Common.resetLayoutParameter(params);
                    boolean isConfigureLandscape = true;
                    if (mediator.isConfigureLandscape()) {
                        params.addRule(12, 1);
                        params.addRule(21, 1);
                    } else {
                        params.addRule(12, 1);
                        params.addRule(20, 1);
                        isConfigureLandscape = false;
                    }
                    int lcd_width = Common.getPixelFromDimens(mediator.getApplicationContext(), R.dimen.lcd_width);
                    int lcd_height = Common.getPixelFromDimens(mediator.getApplicationContext(), R.dimen.lcd_height);
                    int topMargin = Common.getPixelFromDimens(mediator.getApplicationContext(), R.dimen.bubble_popup_time_machine_marginTop);
                    int bottomMargin = Common.getPixelFromDimens(mediator.getApplicationContext(), R.dimen.bubble_popup_time_machine_marginBottom_port);
                    int leftMargin = Common.getPixelFromDimens(mediator.getApplicationContext(), R.dimen.quickfunction_layout_width);
                    int rightMargin = Common.getPixelFromDimens(mediator.getApplicationContext(), R.dimen.preview_panel_width) + Common.getPixelFromDimens(mediator.getApplicationContext(), R.dimen.preview_panel_marginBottom);
                    int maxWidth;
                    if (isEqualDegree(isConfigureLandscape, degree, 0) || isEqualDegree(isConfigureLandscape, degree, MediaProviderUtils.ROTATION_180)) {
                        tempBottomMargin = Common.getPixelFromDimens(mediator.getApplicationContext(), R.dimen.camera_switch_marginTop) + ((Common.getPixelFromDimens(mediator.getApplicationContext(), R.dimen.review_thumbnail_height) - bottomMargin) / 2);
                        if (isBottomMarginNeeded()) {
                            tempBottomMargin += Common.getPixelFromDimens(mediator.getApplicationContext(), R.dimen.bubble_popup_width_bottom_margin_marginBottom);
                        }
                        maxWidth = (((lcd_width - leftMargin) - rightMargin) - bubbleView.getPaddingStart()) - bubbleView.getPaddingEnd();
                        if (!"gu".equals(currentLanguage)) {
                            if (!"te".equals(currentLanguage)) {
                                if (!"mr".equals(currentLanguage)) {
                                    message = Common.breakTextToMultiLine(rtv.getTextPaint(), message, maxWidth);
                                }
                            }
                        }
                        rtv.setText(message);
                        if (isEqualDegree(isConfigureLandscape, degree, 0)) {
                            bubbleView.setBackgroundResource(!isBottomMarginNeeded() ? R.drawable.camera_bubble_bg : R.drawable.camera_bubble_bg_with_bottommargin);
                        } else {
                            bubbleView.setBackgroundResource(!isBottomMarginNeeded() ? R.drawable.camera_bubble_bg_opposite : R.drawable.camera_bubble_bg_opposite_with_bottommargin);
                        }
                    } else {
                        tempBottomMargin = bottomMargin;
                        if (isBottomMarginNeeded()) {
                            tempBottomMargin -= Common.getPixelFromDimens(mediator.getApplicationContext(), R.dimen.bubble_popup_width_bottom_margin_marginLeft);
                        }
                        maxWidth = (((lcd_height - topMargin) - bottomMargin) - bubbleView.getPaddingStart()) - bubbleView.getPaddingEnd();
                        if (!"gu".equals(currentLanguage)) {
                            if (!"te".equals(currentLanguage)) {
                                if (!"mr".equals(currentLanguage)) {
                                    message = Common.breakTextToMultiLine(rtv.getTextPaint(), message, maxWidth);
                                }
                            }
                        }
                        rtv.setText(message);
                        if (isEqualDegree(isConfigureLandscape, degree, 90)) {
                            bubbleView.setBackgroundResource(!isBottomMarginNeeded() ? R.drawable.camera_bubble_bg_port : R.drawable.camera_bubble_bg_port_with_bottommargin);
                        } else {
                            bubbleView.setBackgroundResource(!isBottomMarginNeeded() ? R.drawable.camera_bubble_bg_port_opposite : R.drawable.camera_bubble_bg_port_opposite_with_bottommargin);
                        }
                    }
                    if (isConfigureLandscape) {
                        params.rightMargin = rightMargin;
                        params.topMargin = topMargin;
                        params.leftMargin = leftMargin;
                        params.bottomMargin = tempBottomMargin;
                    } else {
                        params.rightMargin = topMargin;
                        params.topMargin = leftMargin;
                        params.leftMargin = tempBottomMargin;
                        params.bottomMargin = rightMargin;
                    }
                    rl.setLayoutParams(params);
                    rl.invalidate();
                default:
            }
        }
    }

    private boolean isEqualDegree(boolean landscape, int current, int input) {
        if (landscape) {
            if (current == input) {
                return true;
            }
            return false;
        } else if ((current + 90) % CameraConstants.DEGREE_360 != input) {
            return false;
        } else {
            return true;
        }
    }

    public void setNotiComplete(boolean complete) {
        this.mNotiComplete = complete;
    }

    public void removeBubblePopup(final Mediator mediator, long duration) {
        if (mediator != null) {
            mediator.postOnUiThread(new Runnable() {
                public void run() {
                    if (mediator != null) {
                        mediator.removePostRunnable(this);
                        if (BubblePopupController.this.mPopupView != null) {
                            BubblePopupController.this.setBubblePopupAnimation(mediator, mediator.findViewById(R.id.bubble_popup_layout), false);
                            ViewGroup vg = (ViewGroup) mediator.findViewById(R.id.init);
                            if (vg != null) {
                                vg.removeView(BubblePopupController.this.mPopupView);
                                BubblePopupController.this.mPopupView = null;
                            }
                        }
                    }
                }
            }, duration);
        }
    }

    private void setBubblePopupAnimation(final Mediator mediator, final View aniView, final boolean show) {
        CamLog.d(FaceDetector.TAG, "qflSettingAnimation-start");
        if (mediator != null && aniView != null) {
            try {
                Animation showAni = AnimationUtils.loadAnimation(mediator.getApplicationContext(), R.anim.on_screen_hint_enter);
                Animation hideAni = AnimationUtils.loadAnimation(mediator.getApplicationContext(), R.anim.on_screen_hint_exit);
                if (mediator.getActivity().isFinishing() || mediator.isPausing()) {
                    aniView.clearAnimation();
                    aniView.setVisibility(4);
                    return;
                }
                Animation animation;
                aniView.clearAnimation();
                aniView.setVisibility(4);
                if (show) {
                    animation = showAni;
                } else {
                    animation = hideAni;
                }
                if (animation != null) {
                    animation.setAnimationListener(new AnimationListener() {
                        public void onAnimationStart(Animation animation) {
                        }

                        public void onAnimationRepeat(Animation animation) {
                        }

                        public void onAnimationEnd(Animation animation) {
                            if (aniView != null && mediator != null) {
                                if (show) {
                                    aniView.setVisibility(0);
                                } else {
                                    mediator.postOnUiThread(new Runnable() {
                                        public void run() {
                                            mediator.removePostRunnable(this);
                                            aniView.setVisibility(8);
                                        }
                                    });
                                }
                            }
                        }
                    });
                    aniView.startAnimation(animation);
                }
            } catch (NullPointerException e) {
                CamLog.e(FaceDetector.TAG, "NullPointerException : ", e);
            }
        }
    }

    public void startRotation(Mediator mediator, int degree, boolean animation) {
        if (mediator != null) {
            View view = mediator.findViewById(R.id.bubble_popup_layout);
            if (view != null) {
                RotateLayout rl = (RotateLayout) view;
                setLayout(mediator, rl, degree);
                rl.rotateLayout(degree);
            }
        }
    }

    public void initializeNotiComplete() {
        this.mNotiComplete = false;
    }

    private boolean isBottomMarginNeeded() {
        if (this.mBubblePopupType == 1) {
            return true;
        }
        return false;
    }
}
