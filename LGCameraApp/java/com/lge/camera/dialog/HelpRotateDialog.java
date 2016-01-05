package com.lge.camera.dialog;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.controller.EnteringViewController;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.DialogCreater;
import com.lge.camera.util.Util;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.library.FaceDetector;

public class HelpRotateDialog extends RotateDialog {
    protected boolean mDoNotShowAgain;

    public HelpRotateDialog(ControllerFunction function) {
        super(function);
        this.mDoNotShowAgain = true;
    }

    public void create(boolean useCheckBox, int dialogId) {
        this.mGet.removeScheduledCommand(Command.EXIT_ZOOM_BRIGHTNESS_INTERACTION);
        View rotateHelpView = inflateHelpDialogView();
        TextView titleText = (TextView) rotateHelpView.findViewById(R.id.title_text);
        TextView contentTitleText = (TextView) rotateHelpView.findViewById(R.id.content_title_text);
        TextView messageText = (TextView) rotateHelpView.findViewById(R.id.message_text);
        ImageView messageImage = (ImageView) rotateHelpView.findViewById(R.id.message_image);
        Button btnOk = (Button) rotateHelpView.findViewById(R.id.ok_button);
        int[] helpResources = DialogCreater.getHelpItemResources(dialogId, this.mGet.getCameraId());
        titleText.setText(helpResources[0]);
        contentTitleText.setText(helpResources[0]);
        messageText.setText(helpResources[1]);
        messageImage.setImageResource(helpResources[2]);
        setExtraContents(rotateHelpView);
        btnOk.setText(R.string.sp_ok_NORMAL);
        setCheckBox(rotateHelpView, useCheckBox);
        super.create(rotateHelpView);
        setPositiveButtonListener(btnOk, useCheckBox, DialogCreater.getHelpDialogKeyValue(dialogId));
    }

    protected View inflateHelpDialogView() {
        return this.mGet.inflateView(R.layout.rotate_help_dialog);
    }

    protected void setExtraContents(View rotateHelpView) {
    }

    protected void setCheckBox(View rotateHelpView, boolean useCheckBox) {
        CheckBox userCheck = (CheckBox) rotateHelpView.findViewById(R.id.checkbox_do_not_show_again);
        if (userCheck == null) {
            return;
        }
        if (useCheckBox) {
            userCheck.setText(R.string.sp_eula_popup_do_not_show_this_again_NORMAL);
            userCheck.setChecked(this.mDoNotShowAgain);
            userCheck.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == 1) {
                        v.playSoundEffect(0);
                    }
                    return false;
                }
            });
            userCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    HelpRotateDialog.this.mDoNotShowAgain = isChecked;
                }
            });
            return;
        }
        RelativeLayout userCheckLayout = (RelativeLayout) rotateHelpView.findViewById(R.id.checkbox_do_not_show_again_layout);
        if (userCheckLayout != null) {
            userCheckLayout.setVisibility(8);
        } else {
            userCheck.setVisibility(8);
        }
    }

    protected void setPositiveButtonListener(Button btnOk, final boolean useCheckBox, final String helpKeyString) {
        if (btnOk != null) {
            btnOk.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    CamLog.d(FaceDetector.TAG, "ok button click. help key is : " + helpKeyString);
                    SharedPreferences pref = HelpRotateDialog.this.mGet.getActivity().getSharedPreferences(Setting.SETTING_PRIMARY, 0);
                    if (pref != null && ((HelpRotateDialog.this.mDoNotShowAgain && useCheckBox) || !useCheckBox)) {
                        Editor edit = pref.edit();
                        edit.putBoolean(helpKeyString, true);
                        edit.apply();
                        EnteringViewController.setSystemHelpSettingOff(HelpRotateDialog.this.mGet.getActivity());
                    }
                    HelpRotateDialog.this.onDismiss();
                }
            });
        }
    }

    public void startRotation(int degree) {
        CamLog.d(FaceDetector.TAG, "RotatableDialog startRotataion(degree) start = " + degree);
        if (this.mGet != null) {
            if (this.mGet.getActivity().findViewById(R.id.rotate_dialog_layout) != null) {
                int i = this.mOrientation;
                if (r0 == degree) {
                    CamLog.d(FaceDetector.TAG, "RotatableDialog startRotataion : rotate same.");
                } else if (this.mView != null) {
                    boolean isLand;
                    int marginValue;
                    int topValue;
                    int bottomValue;
                    int msgTopValue;
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
                    ((RotateLayout) this.mView.findViewById(R.id.rotate_dialog_layout)).rotateLayout(degree);
                    TextView contentTitleText = (TextView) this.mView.findViewById(R.id.content_title_text);
                    ScrollView messageScroll = (ScrollView) this.mView.findViewById(R.id.message_scroll);
                    ImageView messageImage = (ImageView) this.mView.findViewById(R.id.message_image);
                    LinearLayout rotateInnerLayout = (LinearLayout) this.mView.findViewById(R.id.rotate_dialog_inner_layout);
                    LayoutParams contentTitleParams = (LayoutParams) contentTitleText.getLayoutParams();
                    LayoutParams messageScrollParams = (LayoutParams) messageScroll.getLayoutParams();
                    LayoutParams messageImageParams = (LayoutParams) messageImage.getLayoutParams();
                    LayoutParams rotateInnerLayoutParams = (LayoutParams) rotateInnerLayout.getLayoutParams();
                    Common.resetLayoutParameter(contentTitleParams);
                    Common.resetLayoutParameter(messageScrollParams);
                    Common.resetLayoutParameter(messageImageParams);
                    Common.resetLayoutParameter(rotateInnerLayoutParams);
                    if (!Util.isEqualDegree(this.mGet.getResources(), degree, 0)) {
                        if (!Util.isEqualDegree(this.mGet.getResources(), degree, MediaProviderUtils.ROTATION_180)) {
                            isLand = false;
                            marginValue = Math.round(Util.dpToPx(this.mGet.getApplicationContext(), 12.0f));
                            topValue = Math.round(Util.dpToPx(this.mGet.getApplicationContext(), 8.0f));
                            bottomValue = Math.round(Util.dpToPx(this.mGet.getApplicationContext(), isLand ? 13.0f : 5.67f));
                            msgTopValue = Math.round(Util.dpToPx(this.mGet.getApplicationContext(), isLand ? 0.0f : 12.0f));
                            if (isLand) {
                                rotateInnerLayoutParams.height = -2;
                                rotateInnerLayoutParams.width = -1;
                                contentTitleText.setPaddingRelative(marginValue, 0, marginValue, 0);
                                contentTitleParams.width = -1;
                                contentTitleParams.topMargin = topValue;
                                contentTitleParams.bottomMargin = bottomValue;
                                messageImage.setPaddingRelative(0, 0, 0, 0);
                                messageImageParams.addRule(3, R.id.content_title_text);
                                messageImageParams.addRule(14);
                                messageScroll.setPaddingRelative(marginValue, 0, marginValue, 0);
                                messageScrollParams.addRule(3, R.id.message_image);
                                messageScrollParams.addRule(17, 0);
                                messageScrollParams.width = getVerticalMessageScrollWidth();
                                messageScrollParams.topMargin = msgTopValue;
                                messageImage.setImageLevel(0);
                            } else {
                                rotateInnerLayoutParams.width = getHorizontalHelpDialogWidth();
                                rotateInnerLayoutParams.height = -2;
                                messageImage.setPaddingRelative(marginValue, 0, 0, 0);
                                contentTitleText.setPaddingRelative(marginValue, 0, marginValue, 0);
                                contentTitleParams.addRule(18);
                                contentTitleParams.addRule(17, R.id.message_image);
                                contentTitleParams.width = -1;
                                contentTitleParams.topMargin = topValue;
                                contentTitleParams.bottomMargin = 0;
                                messageScroll.setPaddingRelative(marginValue, 0, marginValue, 0);
                                messageScrollParams.addRule(18);
                                messageScrollParams.addRule(17, R.id.message_image);
                                messageScrollParams.addRule(3, R.id.content_title_text);
                                messageScrollParams.width = -1;
                                if (contentTitleText.getVisibility() == 8) {
                                    msgTopValue = 0;
                                }
                                messageScrollParams.topMargin = msgTopValue;
                                messageImage.setImageLevel(1);
                            }
                            rotateInnerLayout.setLayoutParams(rotateInnerLayoutParams);
                            contentTitleText.setLayoutParams(contentTitleParams);
                            messageScroll.setLayoutParams(messageScrollParams);
                            messageImage.setLayoutParams(messageImageParams);
                            showRotateDialogAnimation();
                            CamLog.d(FaceDetector.TAG, "RotatableDialog startRotataion(degree) end = " + degree);
                        }
                    }
                    isLand = true;
                    marginValue = Math.round(Util.dpToPx(this.mGet.getApplicationContext(), 12.0f));
                    topValue = Math.round(Util.dpToPx(this.mGet.getApplicationContext(), 8.0f));
                    if (isLand) {
                    }
                    bottomValue = Math.round(Util.dpToPx(this.mGet.getApplicationContext(), isLand ? 13.0f : 5.67f));
                    if (isLand) {
                    }
                    msgTopValue = Math.round(Util.dpToPx(this.mGet.getApplicationContext(), isLand ? 0.0f : 12.0f));
                    if (isLand) {
                        rotateInnerLayoutParams.height = -2;
                        rotateInnerLayoutParams.width = -1;
                        contentTitleText.setPaddingRelative(marginValue, 0, marginValue, 0);
                        contentTitleParams.width = -1;
                        contentTitleParams.topMargin = topValue;
                        contentTitleParams.bottomMargin = bottomValue;
                        messageImage.setPaddingRelative(0, 0, 0, 0);
                        messageImageParams.addRule(3, R.id.content_title_text);
                        messageImageParams.addRule(14);
                        messageScroll.setPaddingRelative(marginValue, 0, marginValue, 0);
                        messageScrollParams.addRule(3, R.id.message_image);
                        messageScrollParams.addRule(17, 0);
                        messageScrollParams.width = getVerticalMessageScrollWidth();
                        messageScrollParams.topMargin = msgTopValue;
                        messageImage.setImageLevel(0);
                    } else {
                        rotateInnerLayoutParams.width = getHorizontalHelpDialogWidth();
                        rotateInnerLayoutParams.height = -2;
                        messageImage.setPaddingRelative(marginValue, 0, 0, 0);
                        contentTitleText.setPaddingRelative(marginValue, 0, marginValue, 0);
                        contentTitleParams.addRule(18);
                        contentTitleParams.addRule(17, R.id.message_image);
                        contentTitleParams.width = -1;
                        contentTitleParams.topMargin = topValue;
                        contentTitleParams.bottomMargin = 0;
                        messageScroll.setPaddingRelative(marginValue, 0, marginValue, 0);
                        messageScrollParams.addRule(18);
                        messageScrollParams.addRule(17, R.id.message_image);
                        messageScrollParams.addRule(3, R.id.content_title_text);
                        messageScrollParams.width = -1;
                        if (contentTitleText.getVisibility() == 8) {
                            msgTopValue = 0;
                        }
                        messageScrollParams.topMargin = msgTopValue;
                        messageImage.setImageLevel(1);
                    }
                    rotateInnerLayout.setLayoutParams(rotateInnerLayoutParams);
                    contentTitleText.setLayoutParams(contentTitleParams);
                    messageScroll.setLayoutParams(messageScrollParams);
                    messageImage.setLayoutParams(messageImageParams);
                    showRotateDialogAnimation();
                    CamLog.d(FaceDetector.TAG, "RotatableDialog startRotataion(degree) end = " + degree);
                }
            }
        }
    }

    protected int getHorizontalHelpDialogWidth() {
        return DialogCreater.getHorizontalHelpDialogWidth(this.mGet.getApplicationContext(), false);
    }

    protected int getVerticalMessageScrollWidth() {
        return -2;
    }
}
