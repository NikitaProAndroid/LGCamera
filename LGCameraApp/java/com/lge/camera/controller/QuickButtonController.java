package com.lge.camera.controller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.components.RotateImageButton;
import com.lge.camera.components.RotateView;
import com.lge.camera.listeners.SmartCameraModeCallback;
import com.lge.camera.listeners.SmartCameraModeCallback.SmartCameraModeFunction;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.ColorUtil;
import com.lge.camera.util.Common;
import com.lge.camera.util.DialogCreater;
import com.lge.camera.util.Util;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.BaseEngine;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.util.ArrayList;
import java.util.Iterator;

public class QuickButtonController extends Controller implements SmartCameraModeFunction {
    private Object blockObject;
    private boolean isForcedMenuDisable;
    private boolean isNeedEnableSwitcherLever;
    private Animation mButtonAnim;
    private boolean[] mButtonGroupEnable;
    private int mCurrentDegree;
    private ArrayList<QuickButtonType> mMenuViewGroup;
    private OnClickListener mOnMenuClickListener;
    private OnTouchListener mOnMenuTouchListener;
    private boolean mSliding;
    private SmartCameraModeCallback mSmartCameraModeCallback;

    private class QuickButtonType {
        private RotateImageButton mButton;
        private boolean mEnable;
        private boolean mNeedDefault;

        public QuickButtonType(RotateImageButton button, boolean enable) {
            this.mButton = null;
            this.mEnable = true;
            this.mNeedDefault = false;
            this.mButton = button;
            this.mEnable = enable;
        }

        public QuickButtonType(RotateImageButton button, boolean enable, boolean needDefault) {
            this.mButton = null;
            this.mEnable = true;
            this.mNeedDefault = false;
            this.mButton = button;
            this.mEnable = enable;
            this.mNeedDefault = needDefault;
        }

        public RotateImageButton getButton() {
            return this.mButton;
        }

        public void setEnable(boolean enable) {
            this.mEnable = enable;
        }

        public boolean isEnable() {
            return this.mEnable;
        }

        public void setNeedDefault(boolean set) {
            this.mNeedDefault = set;
        }

        public boolean isNeedDefault() {
            return this.mNeedDefault;
        }
    }

    public QuickButtonController(ControllerFunction function) {
        super(function);
        this.mMenuViewGroup = new ArrayList();
        this.mButtonAnim = null;
        this.mCurrentDegree = 0;
        this.mSmartCameraModeCallback = null;
        this.mButtonGroupEnable = new boolean[12];
        this.blockObject = new Object();
        this.mOnMenuClickListener = new OnClickListener() {
            public void onClick(View v) {
                Throwable th;
                if (!QuickButtonController.this.checkOnClick()) {
                    return;
                }
                if (v.getAnimation() == null || v.getAnimation().hasEnded()) {
                    Bundle bundle;
                    switch (((Integer) v.getTag()).intValue()) {
                        case LGKeyRec.EVENT_NO_MATCH /*1*/:
                            if (CameraConstants.SMART_MODE_OFF.equals(QuickButtonController.this.mGet.getSettingValue(Setting.KEY_FLASH))) {
                                QuickButtonController.this.mGet.setSetting(Setting.KEY_FLASH, CameraConstants.FLASH_TORCH);
                            } else {
                                QuickButtonController.this.mGet.setSetting(Setting.KEY_FLASH, CameraConstants.SMART_MODE_OFF);
                            }
                            bundle = new Bundle();
                            if (QuickButtonController.this.mGet.getVideoState() == 3) {
                                bundle.putBoolean("fromQuickButton", true);
                            }
                            QuickButtonController.this.mGet.doCommand(Command.CAMERA_FLASH_MODE, null, bundle);
                            QuickButtonController.this.setQuickButtonResources(1);
                            break;
                        case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                            if (QuickButtonController.this.mGet.getHeadsetstate() != 2) {
                                QuickButtonController.this.setQuickButtonResources(2);
                                break;
                            }
                            return;
                        case LGKeyRec.EVENT_STARTED /*3*/:
                            if (QuickButtonController.this.mGet.getObjectTrackingState() != 0) {
                                QuickButtonController.this.mGet.startObjectTrackingFocus(0, 0, 0, 0, 0);
                                break;
                            }
                            break;
                        case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
                            QuickButtonController.this.checkSettingMenu();
                            synchronized (QuickButtonController.this.blockObject) {
                                if (QuickButtonController.this.mGet.setCheckToggleTime(2)) {
                                    QuickButtonController.this.mGet.doCommandDelayed(Command.SHOW_MODE_MENU, 0);
                                    QuickButtonController.this.mGet.setCheckClickTime(System.currentTimeMillis());
                                }
                            }
                            return;
                        case LGKeyRec.EVENT_NEED_MORE_AUDIO /*8*/:
                            QuickButtonController.this.checkSettingMenu();
                            if (QuickButtonController.this.mGet.getSubMenuMode() != 0) {
                                QuickButtonController.this.mGet.clearSubMenu();
                                if (QuickButtonController.this.mGet.getApplicationMode() == 0) {
                                    QuickButtonController.this.mGet.setClearFocusAnimation();
                                    if (!(Setting.HELP_FACE_TRACKING_LED.equals(QuickButtonController.this.mGet.getSettingValue(Setting.KEY_FOCUS)) || CameraConstants.FOCUS_SETTING_VALUE_MANUAL.equals(QuickButtonController.this.mGet.getSettingValue(Setting.KEY_FOCUS)))) {
                                        QuickButtonController.this.mGet.showFocus();
                                    }
                                }
                            }
                            QuickButtonController.this.mGet.doCommandUi(Command.DISPLAY_SETTING_MENU);
                            return;
                        case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
                            synchronized (QuickButtonController.this.blockObject) {
                                try {
                                    if (QuickButtonController.this.mGet.setCheckToggleTime(1)) {
                                        if ("back".equals(QuickButtonController.this.mGet.getSettingValue(Setting.KEY_SWAP))) {
                                            QuickButtonController.this.mGet.setSetting(Setting.KEY_SWAP, "front");
                                        } else {
                                            QuickButtonController.this.mGet.setSetting(Setting.KEY_SWAP, "back");
                                        }
                                        if (QuickButtonController.this.mGet.isDualRecordingActive() || QuickButtonController.this.mGet.isSmartZoomRecordingActive() || QuickButtonController.this.mGet.isDualCameraActive()) {
                                            QuickButtonController.this.mGet.doCommand(Command.HIDE_PIP_SUBWINDOW_RESIZE_HANDLER);
                                            QuickButtonController.this.mGet.doCommandUi(Command.HIDE_PIP_FRAME_SUB_MENU);
                                        }
                                        if (MultimediaProperties.isLiveEffectSupported() && QuickButtonController.this.mGet.getApplicationMode() == 1 && QuickButtonController.this.mGet.isEffectsCamcorderActive()) {
                                            QuickButtonController.this.doSwapForEffectsRecording();
                                            return;
                                        } else if (MultimediaProperties.isDualCameraSupported() && QuickButtonController.this.mGet.getApplicationMode() == 0 && QuickButtonController.this.mGet.isEffectsCameraActive()) {
                                            QuickButtonController.this.doSwapForEffectsCamera();
                                            return;
                                        } else {
                                            QuickButtonController.this.mGet.setQuickFunctionMenuForcedDisable(true);
                                            QuickButtonController.this.mGet.setQuickFunctionAllMenuEnabled(false, false);
                                            QuickButtonController.this.setQuickButtonForcedDisable(true);
                                            QuickButtonController.this.setAllMenuEnable(false, true);
                                            Bundle bundle2 = new Bundle();
                                            try {
                                                bundle2.putBoolean("subMenuClicked", true);
                                                QuickButtonController.this.mGet.doCommandDelayed(Command.SWAP_CAMERA, bundle2, 0);
                                                QuickButtonController.this.mGet.setCheckClickTime(System.currentTimeMillis());
                                                bundle = bundle2;
                                            } catch (Throwable th2) {
                                                th = th2;
                                                bundle = bundle2;
                                                throw th;
                                            }
                                        }
                                    }
                                    return;
                                } catch (Throwable th3) {
                                    th = th3;
                                    throw th;
                                }
                            }
                        case BaseEngine.DEFAULT_PRIORITY /*10*/:
                            String flash = QuickButtonController.this.mGet.getSettingValue(Setting.KEY_FLASH);
                            if (QuickButtonController.this.mGet.getApplicationMode() == 0 || FunctionProperties.isSupportedVideoFlashAuto()) {
                                if (CameraConstants.SMART_MODE_OFF.equals(flash)) {
                                    QuickButtonController.this.mGet.setSetting(Setting.KEY_FLASH, CameraConstants.SMART_MODE_ON);
                                } else if (CameraConstants.SMART_MODE_ON.equals(flash)) {
                                    QuickButtonController.this.mGet.setSetting(Setting.KEY_FLASH, LGT_Limit.ISP_AUTOMODE_AUTO);
                                } else {
                                    QuickButtonController.this.mGet.setSetting(Setting.KEY_FLASH, CameraConstants.SMART_MODE_OFF);
                                }
                            } else if (CameraConstants.SMART_MODE_OFF.equals(flash)) {
                                QuickButtonController.this.mGet.setSetting(Setting.KEY_FLASH, CameraConstants.SMART_MODE_ON);
                            } else {
                                QuickButtonController.this.mGet.setSetting(Setting.KEY_FLASH, CameraConstants.SMART_MODE_OFF);
                            }
                            if (QuickButtonController.this.mGet.getApplicationMode() == 1) {
                                bundle = new Bundle();
                                if (QuickButtonController.this.mGet.getVideoState() == 3) {
                                    bundle.putBoolean("fromQuickButton", true);
                                }
                                QuickButtonController.this.mGet.doCommand(Command.CAMERA_FLASH_MODE, null, bundle);
                            } else {
                                QuickButtonController.this.mGet.doCommand(Command.CAMERA_FLASH_MODE);
                            }
                            QuickButtonController.this.setQuickButtonResources(10);
                            return;
                        case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
                            String light = QuickButtonController.this.mGet.getSettingValue(Setting.KEY_LIGHT);
                            if (CameraConstants.SMART_MODE_OFF.equals(light)) {
                                QuickButtonController.this.mGet.setSetting(Setting.KEY_LIGHT, CameraConstants.SMART_MODE_ON);
                                QuickButtonController.this.mGet.setMainBarAlpha(0);
                                QuickButtonController.this.mGet.setBackgroundColorWhite();
                                QuickButtonController.this.startLightOnAni();
                                Common.setBacklightToMax(QuickButtonController.this.mGet.getActivity());
                                QuickButtonController.this.mGet.showHelpGuidePopup(Setting.HELP_LIGHT_FRAME, DialogCreater.DIALOG_ID_HELP_LIGHT_FRAME, true);
                            } else if (CameraConstants.SMART_MODE_ON.equals(light)) {
                                CamLog.d(FaceDetector.TAG, "LIGHT_ON : ");
                                QuickButtonController.this.mGet.setSetting(Setting.KEY_LIGHT, CameraConstants.SMART_MODE_OFF);
                                QuickButtonController.this.mGet.setBackgroundColorBlack();
                                QuickButtonController.this.startLightOffAni();
                                Common.setBacklightToSystemSetting(QuickButtonController.this.mGet.getActivity());
                            }
                            QuickButtonController.this.setQuickButtonResources(11);
                            break;
                    }
                    if (QuickButtonController.this.mButtonAnim == null) {
                        QuickButtonController.this.mButtonAnim = AnimationUtils.loadAnimation(QuickButtonController.this.mGet.getApplicationContext(), R.anim.quick_button);
                    }
                    v.startAnimation(QuickButtonController.this.mButtonAnim);
                }
            }
        };
        this.isNeedEnableSwitcherLever = true;
        this.mOnMenuTouchListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked() & Ola_ShotParam.AnimalMask_Random) {
                    case LGKeyRec.EVENT_INVALID /*0*/:
                        QuickButtonController.this.isNeedEnableSwitcherLever = QuickButtonController.this.mGet.isSwitcherLeverEnable();
                        if (QuickButtonController.this.isNeedEnableSwitcherLever) {
                            QuickButtonController.this.mGet.resetSwitcherLever();
                        }
                        Iterator i$ = QuickButtonController.this.mMenuViewGroup.iterator();
                        while (i$.hasNext()) {
                            QuickButtonType button = (QuickButtonType) i$.next();
                            if (button.getButton().getTag() != v.getTag()) {
                                button.getButton().setPressed(false);
                            }
                        }
                        break;
                }
                return false;
            }
        };
        this.mSliding = false;
        this.isForcedMenuDisable = false;
        this.mSmartCameraModeCallback = new SmartCameraModeCallback(this);
        this.mCurrentDegree = function.getOrientationDegree();
        setButtonGroupEnable(100, true);
    }

    public void unbind() {
        removeQuickButtonAll();
        this.mMenuViewGroup = null;
        this.mButtonAnim = null;
        this.mSmartCameraModeCallback = null;
    }

    public void initController() {
        if (!this.mInit) {
            try {
                this.mGet.inflateStub(R.id.stub_quick_button_rec);
            } catch (Exception e) {
                e.printStackTrace();
                this.mGet.toast((int) R.string.error_not_enough_memory);
            }
            this.mInit = true;
        }
    }

    public void addQuickButton(Context context, int buttonType, int orientation, int location) {
        if (this.mInit && checkMediator() && !hasButton(buttonType, location)) {
            switch (buttonType) {
                case LGKeyRec.EVENT_NO_MATCH /*1*/:
                    addFlashButton(context, location);
                    break;
                case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                    addAudioZoomButton(context, location);
                    break;
                case LGKeyRec.EVENT_STARTED /*3*/:
                    addObjectTrackingButton(context, location);
                    break;
                case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
                    addModeMenuButton(context, location);
                    break;
                case LGKeyRec.EVENT_NEED_MORE_AUDIO /*8*/:
                    addSettingButton(context, location);
                    break;
                case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
                    addNewSwapButton(context, location);
                    break;
                case BaseEngine.DEFAULT_PRIORITY /*10*/:
                    addNewFlashButton(context, location);
                    break;
                case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
                    addNewLightButton(context, location);
                    break;
            }
            if (!this.mGet.isCamcorderRotation(true)) {
                orientation = this.mCurrentDegree;
            }
            if (this.mGet.getOrientationDegree() != orientation) {
                orientation = this.mGet.getOrientationDegree();
            }
            setRotation(orientation, false);
        }
    }

    public void addQuickButton(Context context, int buttonType, int location) {
        addQuickButton(context, buttonType, this.mGet.getOrientationDegree(), location);
    }

    private boolean hasButton(int buttonType, int location) {
        if (!this.mInit || this.mMenuViewGroup == null || this.mMenuViewGroup.size() == 0) {
            return false;
        }
        Iterator i$ = this.mMenuViewGroup.iterator();
        while (i$.hasNext()) {
            RotateImageButton button = ((QuickButtonType) i$.next()).getButton();
            if (button != null && ((Integer) button.getTag()).intValue() == buttonType) {
                int[] margin = setButtonLocation(location);
                LayoutParams params = (LayoutParams) button.getLayoutParams();
                if (this.mGet.isConfigureLandscape()) {
                    params.leftMargin = margin[0];
                    params.topMargin = margin[1];
                } else {
                    params.addRule(21);
                    params.topMargin = margin[0];
                    params.rightMargin = margin[1];
                }
                button.setLayoutParams(params);
                return true;
            }
        }
        return false;
    }

    private RotateImageButton getButton(Context context, int buttonType, int buttonRes, int bgRes) {
        RotateImageButton rib = new RotateImageButton(context);
        rib.setRotateIconOnly(true);
        if (bgRes > 0) {
            rib.setBackgroundResource(bgRes);
        }
        if (buttonRes > 0) {
            rib.setImageResource(buttonRes);
        }
        rib.setFocusable(false);
        rib.setTag(Integer.valueOf(buttonType));
        return rib;
    }

    private void addModeMenuButton(Context context, int location) {
        int[] margin = setButtonLocation(location);
        RotateImageButton rib = getButton(context, 7, R.drawable.selector_quick_button_mode, R.drawable.btn_mode_empty);
        String message = context.getString(R.string.camera_quick_button_index_mode).toUpperCase();
        rib.setContentDescription(this.mGet.getString(R.string.accessiblity_mode_button));
        rib.setTextColor(Color.rgb(49, 49, 49));
        rib.setTextShadowColor(-1);
        rib.setText(message);
        this.mMenuViewGroup.add(new QuickButtonType(rib, true));
        setButtonLayout(rib, margin[0], margin[1]);
        rib.setTextSize(Common.getPixelFromDimens(context, R.dimen.quick_button_mode_text_size));
        rib.setTextScaleX(RotateView.DEFAULT_TEXT_SCALE_X);
        Paint tp = rib.getTextPaint();
        tp.setTextSize((float) Common.getPixelFromDimens(context, R.dimen.quick_button_mode_text_size));
        float quickButtonTargetWidth = (float) Common.getPixelFromDimens(context, R.dimen.quick_button_mode_textview_width);
        float mearsureText = tp.measureText(message);
        float scaleFactor = 0.0f;
        if (!(Float.compare(mearsureText, 0.0f) == 0 || Float.compare(quickButtonTargetWidth, 0.0f) == 0 || Float.compare(mearsureText, quickButtonTargetWidth) == 0)) {
            if (Float.compare(mearsureText, quickButtonTargetWidth) >= 0) {
                scaleFactor = quickButtonTargetWidth / mearsureText;
            }
            CamLog.d(FaceDetector.TAG, "scaleFactor = " + scaleFactor);
            if (Float.compare(scaleFactor, 0.0f) != 0) {
                rib.setTextScaleX(scaleFactor);
            }
        }
        setMenuClickListener(rib);
        setMenuTouchListener(rib);
        rib.setVisibility(4);
    }

    private void addSettingButton(Context context, int location) {
        int[] margin = setButtonLocation(location);
        RotateImageButton rib = getButton(context, 8, R.drawable.selector_btn_mode_setting_button, R.drawable.btn_mode_empty);
        rib.setContentDescription(this.mGet.getString(R.string.settings));
        this.mMenuViewGroup.add(new QuickButtonType(rib, this.mButtonGroupEnable[8]));
        setButtonLayout(rib, margin[0], margin[1]);
        setMenuClickListener(rib);
        setMenuTouchListener(rib);
        rib.setVisibility(4);
    }

    private void addNewSwapButton(Context context, int location) {
        int[] margin = setButtonLocation(location);
        RotateImageButton rib = getButton(context, 9, R.drawable.selector_btn_mode_swap_button, R.drawable.btn_mode_empty);
        rib.setContentDescription(this.mGet.getString(R.string.dual_camera_select));
        this.mMenuViewGroup.add(new QuickButtonType(rib, this.mButtonGroupEnable[9]));
        setButtonLayout(rib, margin[0], margin[1]);
        setMenuClickListener(rib);
        setMenuTouchListener(rib);
        rib.setVisibility(4);
    }

    private void addNewFlashButton(Context context, int location) {
        RotateImageButton rib;
        boolean z;
        int[] margin = setButtonLocation(location);
        String flash = this.mGet.getSettingValue(Setting.KEY_FLASH);
        boolean enable = this.mButtonGroupEnable[10];
        if (CameraConstants.SMART_MODE_OFF.equals(flash) || !enable) {
            rib = getButton(context, 10, R.drawable.selector_btn_mode_flash_off_button, R.drawable.btn_mode_empty);
            rib.setContentDescription(this.mGet.getString(R.string.sp_help_flash_off_desc_v2_NORMAL));
        } else if (CameraConstants.SMART_MODE_ON.equals(flash) || CameraConstants.FLASH_TORCH.equals(flash)) {
            rib = getButton(context, 10, R.drawable.selector_btn_mode_flash_on_button, R.drawable.btn_mode_empty);
            rib.setContentDescription(this.mGet.getString(R.string.sp_help_flash_on_desc_v2_NORMAL));
        } else if (LGT_Limit.ISP_AUTOMODE_AUTO.equals(flash)) {
            rib = getButton(context, 10, R.drawable.selector_btn_mode_flash_auto_button, R.drawable.btn_mode_empty);
            rib.setContentDescription(this.mGet.getString(R.string.sp_help_flash_auto_desc_v2_NORMAL));
        } else {
            return;
        }
        setMenuDim(rib, enable);
        ArrayList arrayList = this.mMenuViewGroup;
        if (enable) {
            z = false;
        } else {
            z = true;
        }
        arrayList.add(new QuickButtonType(rib, enable, z));
        setButtonLayout(rib, margin[0], margin[1]);
        setMenuClickListener(rib);
        setMenuTouchListener(rib);
        rib.setVisibility(4);
    }

    private void addNewLightButton(Context context, int location) {
        RotateImageButton rib;
        boolean z;
        int[] margin = setButtonLocation(location);
        String light = this.mGet.getSettingValue(Setting.KEY_LIGHT);
        boolean enable = this.mButtonGroupEnable[11];
        if (CameraConstants.SMART_MODE_OFF.equals(light) || !enable) {
            rib = getButton(context, 11, R.drawable.selector_btn_mode_flash_off_button, R.drawable.btn_mode_empty);
            rib.setContentDescription(this.mGet.getString(R.string.sp_help_flash_off_desc_v2_NORMAL));
        } else if (CameraConstants.SMART_MODE_ON.equals(light)) {
            rib = getButton(context, 11, R.drawable.selector_btn_mode_flash_on_button, R.drawable.btn_mode_empty);
            rib.setContentDescription(this.mGet.getString(R.string.sp_help_flash_on_desc_v2_NORMAL));
        } else {
            return;
        }
        setMenuDim(rib, enable);
        ArrayList arrayList = this.mMenuViewGroup;
        if (enable) {
            z = false;
        } else {
            z = true;
        }
        arrayList.add(new QuickButtonType(rib, enable, z));
        setButtonLayout(rib, margin[0], margin[1]);
        setMenuClickListener(rib);
        setMenuTouchListener(rib);
        rib.setVisibility(4);
    }

    private void addFlashButton(Context context, int location) {
        RotateImageButton rib;
        int[] margin = setButtonLocation(location);
        if (CameraConstants.SMART_MODE_OFF.equals(this.mGet.getSettingValue(Setting.KEY_FLASH))) {
            rib = getButton(context, 1, R.drawable.selector_btn_mode_flash_off_button, R.drawable.btn_mode_empty);
            rib.setContentDescription(this.mGet.getString(R.string.sp_help_flash_off_desc_v2_NORMAL));
        } else {
            rib = getButton(context, 1, R.drawable.selector_btn_mode_flash_on_button, R.drawable.btn_mode_empty);
            rib.setContentDescription(this.mGet.getString(R.string.sp_help_flash_on_desc_v2_NORMAL));
        }
        this.mMenuViewGroup.add(new QuickButtonType(rib, this.mButtonGroupEnable[1]));
        setButtonLayout(rib, margin[0], margin[1]);
        setMenuClickListener(rib);
        setMenuTouchListener(rib);
        rib.setVisibility(4);
    }

    private void addAudioZoomButton(Context context, int location) {
        int resid;
        boolean isDim = true;
        boolean isEnable = true;
        int[] margin = setButtonLocation(location);
        if (this.mGet.isAudiozoom_ExceptionCase(true)) {
            isEnable = false;
            isDim = false;
        }
        if (this.mGet.getHeadsetstate() == 2 || "mute".equals(this.mGet.getSettingValue(Setting.KEY_VIDEO_AUDIO_RECORDING))) {
            isEnable = false;
            isDim = false;
        }
        if (this.mGet.getAudiozoomStart()) {
            resid = R.drawable.selector_btn_mode_audio_zoom_on_button;
        } else {
            resid = R.drawable.selector_btn_mode_audio_zoom_off_button;
        }
        RotateImageButton rib = getButton(context, 2, resid, R.drawable.btn_mode_empty);
        rib.setContentDescription(this.mGet.getString(R.string.sp_audio_zoom_NORMAL));
        if (rib != null) {
            rib.setEnabled(isEnable);
            setButtonGroupEnable(2, isEnable);
            setMenuDim(rib, isDim);
            this.mMenuViewGroup.add(new QuickButtonType(rib, this.mButtonGroupEnable[2]));
            setButtonLayout(rib, margin[0], margin[1]);
            setMenuClickListener(rib);
            setMenuTouchListener(rib);
            rib.setVisibility(4);
        }
    }

    private void addObjectTrackingButton(Context context, int location) {
        int[] margin = setButtonLocation(location);
        RotateImageButton rib = getButton(context, 3, R.drawable.selector_btn_mode_object_on_button, R.drawable.btn_mode_empty);
        this.mMenuViewGroup.add(new QuickButtonType(rib, this.mButtonGroupEnable[3]));
        setButtonLayout(rib, margin[0], margin[1]);
        setMenuClickListener(rib);
        setMenuTouchListener(rib);
        rib.setVisibility(4);
        rib.setContentDescription(this.mGet.getString(R.string.sp_tracking_focus_NORMAL));
    }

    private void setQuickButtonResources(int type) {
        switch (type) {
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                if (CameraConstants.SMART_MODE_OFF.equals(this.mGet.getSettingValue(Setting.KEY_FLASH))) {
                    changeButtonResource(1, R.drawable.selector_btn_mode_flash_off_button, R.string.sp_help_flash_off_desc_v2_NORMAL);
                } else {
                    changeButtonResource(1, R.drawable.selector_btn_mode_flash_on_button, R.string.sp_help_flash_on_desc_v2_NORMAL);
                }
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                boolean start = this.mGet.getAudiozoomStart();
                if (this.mGet.getObjectTrackingState() != 0) {
                    this.mGet.startObjectTrackingFocus(0, 0, 0, 0, 0);
                }
                if (start) {
                    this.mGet.stopAudiozoom();
                    this.mGet.setAudiozoomStartInRecording(false);
                    changeButtonResource(2, R.drawable.selector_btn_mode_audio_zoom_off_button);
                    return;
                }
                this.mGet.startAudiozoom();
                this.mGet.setAudiozoomStartInRecording(true);
                changeButtonResource(2, R.drawable.selector_btn_mode_audio_zoom_on_button);
            case BaseEngine.DEFAULT_PRIORITY /*10*/:
                String flash = this.mGet.getSettingValue(Setting.KEY_FLASH);
                if (CameraConstants.SMART_MODE_OFF.equals(flash) || isNeedDisableDefaultIcon(10)) {
                    changeButtonResource(10, R.drawable.selector_btn_mode_flash_off_button, R.string.sp_help_flash_off_desc_v2_NORMAL);
                } else if (CameraConstants.SMART_MODE_ON.equals(flash) || CameraConstants.FLASH_TORCH.equals(flash)) {
                    changeButtonResource(10, R.drawable.selector_btn_mode_flash_on_button, R.string.sp_help_flash_on_desc_v2_NORMAL);
                } else if (LGT_Limit.ISP_AUTOMODE_AUTO.equals(flash)) {
                    changeButtonResource(10, R.drawable.selector_btn_mode_flash_auto_button, R.string.sp_help_flash_auto_desc_v2_NORMAL);
                }
            case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
                if (CameraConstants.SMART_MODE_OFF.equals(this.mGet.getSettingValue(Setting.KEY_LIGHT)) || isNeedDisableDefaultIcon(11)) {
                    changeButtonResource(11, R.drawable.selector_btn_mode_flash_off_button, R.string.sp_help_flash_off_desc_v2_NORMAL);
                } else {
                    changeButtonResource(11, R.drawable.selector_btn_mode_flash_on_button, R.string.sp_help_flash_on_desc_v2_NORMAL);
                }
            default:
        }
    }

    private boolean isNeedDisableDefaultIcon(int type) {
        if (!this.mInit || this.mMenuViewGroup == null) {
            return false;
        }
        Iterator i$ = this.mMenuViewGroup.iterator();
        while (i$.hasNext()) {
            QuickButtonType quickButton = (QuickButtonType) i$.next();
            if (((Integer) quickButton.getButton().getTag()).intValue() == type) {
                return quickButton.isNeedDefault();
            }
        }
        return false;
    }

    public void changeButtonResource(int type, int resId) {
        if (this.mInit && this.mMenuViewGroup != null) {
            Iterator i$ = this.mMenuViewGroup.iterator();
            while (i$.hasNext()) {
                QuickButtonType quickButton = (QuickButtonType) i$.next();
                if (((Integer) quickButton.getButton().getTag()).intValue() == type) {
                    quickButton.getButton().setImageResource(resId);
                }
            }
        }
    }

    public void changeButtonResource(int type, int resId, int descriptionId) {
        if (this.mInit && this.mMenuViewGroup != null) {
            Iterator i$ = this.mMenuViewGroup.iterator();
            while (i$.hasNext()) {
                QuickButtonType quickButton = (QuickButtonType) i$.next();
                if (((Integer) quickButton.getButton().getTag()).intValue() == type) {
                    quickButton.getButton().setImageResource(resId);
                    quickButton.getButton().setContentDescription(this.mGet.getString(descriptionId));
                }
            }
        }
    }

    private void setButtonLayout(RotateImageButton buttonView, int leftMargin, int topMargin) {
        if (this.mInit && buttonView != null) {
            LayoutParams param = new LayoutParams(-2, -2);
            if (this.mGet.isConfigureLandscape()) {
                param.leftMargin = leftMargin;
                param.topMargin = topMargin;
            } else {
                param.addRule(21);
                param.topMargin = leftMargin;
                param.rightMargin = topMargin;
            }
            View quickButtonView = this.mGet.findViewById(R.id.quick_button_rec);
            if (quickButtonView != null) {
                ((RelativeLayout) quickButtonView).addView(buttonView, param);
            }
        }
    }

    private void setMenuClickListener(RotateImageButton rib) {
        if (rib != null) {
            rib.setOnClickListener(this.mOnMenuClickListener);
        }
    }

    public void startLightOffAni() {
        View view = this.mGet.findViewById(R.id.light_frame_ani);
        ImageView imgView = (ImageView) this.mGet.findViewById(R.id.light_frame_ani_image);
        imgView.setBackgroundColor(-16777216);
        view.setVisibility(0);
        AlphaAnimation mAniFocusAlpha = new AlphaAnimation(0.25f, RotateView.DEFAULT_TEXT_SCALE_X);
        AnimationSet aniSet = new AnimationSet(true);
        aniSet.addAnimation(mAniFocusAlpha);
        aniSet.setFillAfter(true);
        aniSet.setDuration(200);
        aniSet.setInterpolator(new AccelerateDecelerateInterpolator());
        aniSet.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation arg0) {
                CamLog.v(FaceDetector.TAG, "onAnimationEnd");
                View view = QuickButtonController.this.mGet.findViewById(R.id.light_frame_ani);
                view.clearAnimation();
                view.setVisibility(4);
                QuickButtonController.this.mGet.startPreview(null, true);
            }

            public void onAnimationRepeat(Animation arg0) {
            }

            public void onAnimationStart(Animation arg0) {
            }
        });
        this.mGet.stopPreview();
        imgView.startAnimation(aniSet);
    }

    public void startLightOnAni() {
        View view = this.mGet.findViewById(R.id.light_frame_ani);
        ((ImageView) this.mGet.findViewById(R.id.light_frame_ani_image)).setBackgroundResource(R.drawable.lightframe_bg);
        view.setVisibility(0);
        AlphaAnimation mAniFocusAlpha = new AlphaAnimation(0.25f, RotateView.DEFAULT_TEXT_SCALE_X);
        AnimationSet aniSet = new AnimationSet(true);
        aniSet.addAnimation(mAniFocusAlpha);
        aniSet.setFillAfter(true);
        aniSet.setDuration(200);
        aniSet.setInterpolator(new AccelerateDecelerateInterpolator());
        aniSet.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation arg0) {
                CamLog.v(FaceDetector.TAG, "onAnimationEnd");
                QuickButtonController.this.mGet.startPreview(null, true);
                View view = QuickButtonController.this.mGet.findViewById(R.id.light_frame_ani);
                view.clearAnimation();
                view.setVisibility(4);
            }

            public void onAnimationRepeat(Animation arg0) {
            }

            public void onAnimationStart(Animation arg0) {
            }
        });
        this.mGet.stopPreview();
        view.startAnimation(aniSet);
    }

    private void doSwapForEffectsRecording() {
        CamLog.v(FaceDetector.TAG, "SwapCameraPrepared-start, liveeffect is active");
        this.mGet.effectRecorderStopPreviewByCallFrom(DialogCreater.DIALOG_ID_HELP_PANORAMA);
        this.mGet.hideSmartZoomFocusView();
        this.mGet.setCheckClickTime(System.currentTimeMillis());
        this.mGet.setQuickFunctionMenuForcedDisable(true);
        this.mGet.setQuickFunctionAllMenuEnabled(false, false);
        setQuickButtonForcedDisable(true);
        setAllMenuEnable(false, false);
    }

    private void doSwapForEffectsCamera() {
        CamLog.v(FaceDetector.TAG, "SwapCameraPrepared-start, dual camera is active");
        this.mGet.effectCameraStopPreviewByCallFrom(DialogCreater.DIALOG_ID_HELP_PANORAMA);
        this.mGet.setCheckClickTime(System.currentTimeMillis());
        this.mGet.setQuickFunctionMenuForcedDisable(true);
        this.mGet.setQuickFunctionAllMenuEnabled(false, false);
        setQuickButtonForcedDisable(true);
        setAllMenuEnable(false, false);
    }

    private void setMenuTouchListener(RotateImageButton rib) {
        if (rib != null) {
            rib.setOnTouchListener(this.mOnMenuTouchListener);
        }
    }

    private void checkSettingMenu() {
        if (this.mGet.getSubMenuMode() == 5 || this.mGet.getSubMenuMode() == 16) {
            this.mGet.setSubMenuMode(0);
            this.mGet.doCommandUi(Command.REMOVE_SETTING_MENU);
        }
    }

    private boolean checkOnClick() {
        if (!checkMediator() || this.mGet.isPausing() || this.mGet.isEnteringViewShowing() || this.mGet.getInCaptureProgress() || !this.mGet.getEnableInput()) {
            return false;
        }
        if (!this.mGet.isNullSettingView() && this.mGet.isSettingViewRemoving()) {
            CamLog.d(FaceDetector.TAG, "settingview is not null && Removing!!!  ->> block menu click");
            return false;
        } else if (this.mGet.isShutterButtonLongKey() || this.mGet.isPressedShutterButton()) {
            CamLog.d(FaceDetector.TAG, "ShutterButton pressed.. ->> block menu click");
            return false;
        } else if (this.mGet.isRotateDialogVisible()) {
            return false;
        } else {
            return true;
        }
    }

    public void setVisible(int type, int visible, boolean animation) {
        if (this.mInit && this.mMenuViewGroup != null) {
            if ((!ProjectVariables.isSupportClearView() && !ProjectVariables.isSupportKDDICleanView()) || !this.mGet.isClearView()) {
                Iterator i$ = this.mMenuViewGroup.iterator();
                while (i$.hasNext()) {
                    QuickButtonType quickButton = (QuickButtonType) i$.next();
                    int tag = ((Integer) quickButton.getButton().getTag()).intValue();
                    if (type == 100 || type == tag) {
                        if (visible == 0) {
                            if (!(quickButton.getButton().getVisibility() == 0 || Common.isQuickWindowCameraMode())) {
                                if (animation) {
                                    setAlphaAnimation(quickButton.getButton(), 0, 1, CameraConstants.GALLERY_QUICKVIEW_ANI_INTERVAL);
                                } else {
                                    quickButton.getButton().setColorFilter(ColorUtil.getDefaultColor());
                                    quickButton.getButton().setVisibility(visible);
                                }
                            }
                        } else if (animation) {
                            setAlphaAnimation(quickButton.getButton(), 1, 0, CameraConstants.GALLERY_QUICKVIEW_ANI_INTERVAL);
                        } else {
                            quickButton.getButton().setColorFilter(ColorUtil.getQuickButtonDimColor());
                            quickButton.getButton().setVisibility(visible);
                        }
                    }
                }
            }
        }
    }

    public void removeQuickButtonAll() {
        if (this.mInit && this.mMenuViewGroup != null) {
            try {
                RelativeLayout quickButtonView = (RelativeLayout) this.mGet.findViewById(R.id.quick_button_rec);
                Iterator i$ = this.mMenuViewGroup.iterator();
                while (i$.hasNext()) {
                    QuickButtonType quickButton = (QuickButtonType) i$.next();
                    quickButton.getButton().clearAnimation();
                    quickButton.getButton().setVisibility(8);
                    quickButtonView.removeView(quickButton.getButton());
                }
                this.mMenuViewGroup.clear();
                setButtonGroupEnable(100, true);
            } catch (Exception e) {
                CamLog.w(FaceDetector.TAG, "Excetion:", e);
            }
        }
    }

    public void removeQuickButton(int buttonType) {
        if (this.mInit && this.mMenuViewGroup != null) {
            try {
                RelativeLayout quickButtonView = (RelativeLayout) this.mGet.findViewById(R.id.quick_button_rec);
                int size = this.mMenuViewGroup.size();
                for (int i = 0; i < size; i++) {
                    RotateImageButton rib = ((QuickButtonType) this.mMenuViewGroup.get(i)).getButton();
                    if (((Integer) rib.getTag()).intValue() == buttonType) {
                        rib.clearAnimation();
                        rib.setVisibility(8);
                        quickButtonView.removeView(rib);
                        this.mMenuViewGroup.remove(i);
                        return;
                    }
                }
            } catch (Exception e) {
                CamLog.w(FaceDetector.TAG, "Excetion:", e);
            }
        }
    }

    public void startRotation(int degree, boolean animation) {
        if (checkMediator() && this.mGet.isCamcorderRotation(true)) {
            this.mCurrentDegree = degree;
            setRotation(degree, animation);
        }
    }

    private void setRotation(int degree, boolean animation) {
        if (this.mInit && this.mMenuViewGroup != null) {
            Iterator i$ = this.mMenuViewGroup.iterator();
            while (i$.hasNext()) {
                QuickButtonType quickButton = (QuickButtonType) i$.next();
                if (!(quickButton == null || quickButton.getButton() == null)) {
                    quickButton.getButton().setDegree(degree, animation);
                }
            }
        }
    }

    public boolean isSliding() {
        return this.mSliding;
    }

    private void setAlphaAnimation(final View view, final int start, int end, int duration) {
        if (view != null) {
            view.setVisibility(4);
            Util.startAlphaAnimation(view, start, end, duration, new AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    QuickButtonController.this.mSliding = true;
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    if (view != null) {
                        view.clearAnimation();
                        view.setVisibility(start == 0 ? 0 : 8);
                        QuickButtonController.this.mSliding = false;
                    }
                }
            });
        }
    }

    public void setQuickButtonForcedDisable(boolean set) {
        this.isForcedMenuDisable = set;
    }

    public void releaseAllButtonsEnabled() {
        Iterator i$ = this.mMenuViewGroup.iterator();
        while (i$.hasNext()) {
            ((QuickButtonType) i$.next()).setEnable(true);
        }
        setButtonGroupEnable(100, true);
    }

    public void setButtonRemainRefresh() {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                QuickButtonController.this.mGet.removePostRunnable(this);
                if (QuickButtonController.this.mMenuViewGroup != null) {
                    Iterator i$ = QuickButtonController.this.mMenuViewGroup.iterator();
                    while (i$.hasNext()) {
                        QuickButtonType quickButton = (QuickButtonType) i$.next();
                        RotateImageButton button = quickButton.getButton();
                        if (button != null) {
                            int tag = ((Integer) button.getTag()).intValue();
                            boolean enable = QuickButtonController.this.mButtonGroupEnable[tag];
                            button.setEnabled(enable);
                            quickButton.setEnable(enable);
                            quickButton.setNeedDefault(!enable);
                            QuickButtonController.this.setQuickButtonResources(tag);
                            QuickButtonController.this.setMenuDim(button, enable);
                        }
                    }
                }
            }
        });
    }

    public void setButtonRemainEnabled(final int buttonType, final boolean enable) {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                QuickButtonController.this.mGet.removePostRunnable(this);
                if (buttonType != 100) {
                    QuickButtonController.this.setButtonGroupEnable(buttonType, enable);
                }
                if (QuickButtonController.this.mMenuViewGroup != null) {
                    Iterator i$ = QuickButtonController.this.mMenuViewGroup.iterator();
                    while (i$.hasNext()) {
                        QuickButtonType quickButton = (QuickButtonType) i$.next();
                        RotateImageButton button = quickButton.getButton();
                        if (button != null) {
                            int tag = ((Integer) button.getTag()).intValue();
                            if (buttonType == 100 || buttonType == tag) {
                                button.setEnabled(enable);
                                quickButton.setEnable(enable);
                                quickButton.setNeedDefault(!enable);
                                QuickButtonController.this.setQuickButtonResources(buttonType);
                                QuickButtonController.this.setButtonGroupEnable(tag, enable);
                            }
                        }
                    }
                }
            }
        });
    }

    public void setButtonRemainEnabled(final int buttonType, final boolean enable, final boolean useDefaultIconDisable) {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                QuickButtonController.this.mGet.removePostRunnable(this);
                if (buttonType != 100) {
                    QuickButtonController.this.setButtonGroupEnable(buttonType, enable);
                }
                if (QuickButtonController.this.mMenuViewGroup != null && !QuickButtonController.this.isForcedMenuDisable) {
                    Iterator i$ = QuickButtonController.this.mMenuViewGroup.iterator();
                    while (i$.hasNext()) {
                        QuickButtonType quickButton = (QuickButtonType) i$.next();
                        RotateImageButton button = quickButton.getButton();
                        if (button != null) {
                            int tag = ((Integer) button.getTag()).intValue();
                            if (buttonType == 100 || buttonType == tag) {
                                button.setEnabled(enable);
                                quickButton.setEnable(enable);
                                quickButton.setNeedDefault(!useDefaultIconDisable);
                                QuickButtonController.this.setButtonGroupEnable(tag, enable);
                                QuickButtonController.this.setQuickButtonResources(buttonType);
                                QuickButtonController.this.setMenuDim(button, enable);
                            }
                        }
                    }
                }
            }
        });
    }

    public void setMenuEnable(final int buttonType, final boolean enable, final boolean dimByEnable) {
        if (this.mInit && this.mMenuViewGroup != null) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    QuickButtonController.this.mGet.removePostRunnable(this);
                    if (buttonType != 100) {
                        QuickButtonController.this.setButtonGroupEnable(buttonType, enable);
                    }
                    if (QuickButtonController.this.mMenuViewGroup != null) {
                        boolean setEnable = QuickButtonController.this.isForcedMenuDisable ? false : enable;
                        Iterator i$ = QuickButtonController.this.mMenuViewGroup.iterator();
                        while (i$.hasNext()) {
                            QuickButtonType quickButton = (QuickButtonType) i$.next();
                            if (quickButton != null) {
                                RotateImageButton button = quickButton.getButton();
                                int tag = ((Integer) button.getTag()).intValue();
                                if (buttonType == 100 || buttonType == tag) {
                                    if (enable && quickButton.isEnable()) {
                                        button.setEnabled(setEnable);
                                        QuickButtonController.this.setButtonGroupEnable(tag, setEnable);
                                        if (dimByEnable || QuickButtonController.this.isForcedMenuDisable) {
                                            QuickButtonController.this.setMenuDim(button, setEnable);
                                        } else {
                                            QuickButtonController.this.setMenuDim(button, true);
                                        }
                                    } else {
                                        button.setEnabled(false);
                                        QuickButtonController.this.setButtonGroupEnable(tag, false);
                                        QuickButtonController.this.setMenuDim(button, false);
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    public void setAllMenuEnable(final boolean enable, final boolean dimByEnable) {
        if (this.mInit && this.mMenuViewGroup != null) {
            this.mGet.runOnUiThread(new Runnable() {
                public void run() {
                    QuickButtonController.this.mGet.removePostRunnable(this);
                    if (QuickButtonController.this.mMenuViewGroup != null) {
                        boolean setEnable = QuickButtonController.this.isForcedMenuDisable ? false : enable;
                        Iterator i$ = QuickButtonController.this.mMenuViewGroup.iterator();
                        while (i$.hasNext()) {
                            QuickButtonType quickButton = (QuickButtonType) i$.next();
                            if (quickButton != null) {
                                RotateImageButton button = quickButton.getButton();
                                if (enable && quickButton.isEnable()) {
                                    button.setEnabled(setEnable);
                                    if (dimByEnable || QuickButtonController.this.isForcedMenuDisable) {
                                        QuickButtonController.this.setMenuDim(button, setEnable);
                                    } else {
                                        QuickButtonController.this.setMenuDim(button, true);
                                    }
                                } else {
                                    button.setEnabled(false);
                                    QuickButtonController.this.setMenuDim(button, false);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private void setButtonGroupEnable(int buttonType, boolean enable) {
        if (buttonType == 100) {
            for (int i = 0; i < 12; i++) {
                this.mButtonGroupEnable[i] = enable;
            }
            return;
        }
        this.mButtonGroupEnable[buttonType] = enable;
    }

    private void setMenuDim(final RotateImageButton rib, final boolean enable) {
        if (rib != null) {
            this.mGet.postOnUiThread(new Runnable() {
                public void run() {
                    QuickButtonController.this.mGet.removePostRunnable(this);
                    if (enable) {
                        rib.setColorFilter(ColorUtil.getDefaultColor());
                    } else {
                        rib.setColorFilter(ColorUtil.getQuickButtonDimColor());
                    }
                }
            });
        }
    }

    public void refreshQuickButton() {
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                QuickButtonController.this.mGet.removePostRunnable(this);
                if (QuickButtonController.this.mMenuViewGroup != null) {
                    Iterator i$ = QuickButtonController.this.mMenuViewGroup.iterator();
                    while (i$.hasNext()) {
                        QuickButtonController.this.setQuickButtonResources(((Integer) ((QuickButtonType) i$.next()).getButton().getTag()).intValue());
                    }
                }
            }
        });
    }

    public void setSmartCameraMode(LGParameters lgParams, boolean enable) {
        if (!FunctionProperties.isSupportSmartMode()) {
            CamLog.i(FaceDetector.TAG, "SetSmartCameraMode : model is not supported.");
        } else if (this.mGet.getCameraDevice() != null && this.mGet.getLGParam() != null) {
            CamLog.d(FaceDetector.TAG, "setSmartCameraMode : " + enable);
            boolean needParameters = false;
            if (lgParams == null) {
                needParameters = true;
                lgParams = this.mGet.getLGParam();
            }
            if (enable) {
                lgParams.setSceneDetectMode(CameraConstants.SMART_MODE_ON);
                this.mGet.getLG().setISPDataCallbackMode(this.mSmartCameraModeCallback);
                lgParams.getParameters().set(CameraConstants.HDR_MODE, "0");
            } else {
                lgParams.setSceneDetectMode(CameraConstants.SMART_MODE_OFF);
                this.mGet.getLG().setISPDataCallbackMode(null);
            }
            if (needParameters && lgParams != null) {
                try {
                    lgParams.setParameters(lgParams.getParameters());
                } catch (RuntimeException e) {
                    CamLog.e(FaceDetector.TAG, "setParameters failed: ", e);
                }
            }
        }
    }

    private int[] setButtonLocation(int location) {
        int marginLeft = 0;
        int marginTop = 0;
        switch (location) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                marginLeft = getPixelFromDimens(R.dimen.quick_button_new_first_marginLeft);
                marginTop = getPixelFromDimens(R.dimen.quick_button_new_first_marginTop);
                break;
            case LGKeyRec.EVENT_NO_MATCH /*1*/:
                marginLeft = getPixelFromDimens(R.dimen.quick_button_new_second_marginLeft);
                marginTop = getPixelFromDimens(R.dimen.quick_button_new_second_marginTop);
                break;
            case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                marginLeft = getPixelFromDimens(R.dimen.quick_button_new_third_marginLeft);
                marginTop = getPixelFromDimens(R.dimen.quick_button_new_third_marginTop);
                break;
            case LGKeyRec.EVENT_STARTED /*3*/:
                marginLeft = getPixelFromDimens(R.dimen.quick_button_new_fourth_marginLeft);
                marginTop = getPixelFromDimens(R.dimen.quick_button_new_fourth_marginTop);
                break;
            case LGKeyRec.EVENT_STOPPED /*4*/:
                marginLeft = getPixelFromDimens(R.dimen.quick_button_first_marginLeft);
                marginTop = getPixelFromDimens(R.dimen.quick_button_first_marginTop);
                break;
            case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                marginLeft = getPixelFromDimens(R.dimen.quick_button_second_marginLeft);
                marginTop = getPixelFromDimens(R.dimen.quick_button_second_marginTop);
                break;
            case LGKeyRec.EVENT_RECOGNITION_RESULT /*6*/:
                marginLeft = getPixelFromDimens(R.dimen.quick_button_third_marginLeft);
                int id = (ModelProperties.getProjectCode() == 17 || ModelProperties.getProjectCode() == 33 || ModelProperties.getProjectCode() == 27 || ModelProperties.getProjectCode() == 30) ? R.dimen.progress_burst_width : R.dimen.quick_button_third_marginTop;
                marginTop = getPixelFromDimens(id);
                break;
            case LGKeyRec.EVENT_RECOGNITION_TIMEOUT /*7*/:
                marginLeft = getPixelFromDimens(R.dimen.quick_button_fourth_marginLeft);
                marginTop = getPixelFromDimens(R.dimen.quick_button_fourth_marginTop);
                break;
            case LGKeyRec.EVENT_NEED_MORE_AUDIO /*8*/:
                marginLeft = getPixelFromDimens(R.dimen.quick_button_fifth_marginLeft);
                marginTop = getPixelFromDimens(R.dimen.quick_button_fifth_marginTop);
                break;
            case LGKeyRec.EVENT_MAX_SPEECH /*9*/:
                marginLeft = getPixelFromDimens(R.dimen.quick_button_time_machine_marginLeft);
                marginTop = getPixelFromDimens(R.dimen.quick_button_time_machine_marginTop);
                break;
        }
        return new int[]{marginLeft, marginTop};
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        this.isForcedMenuDisable = false;
        removeQuickButtonAll();
        super.onPause();
    }

    public void onDestroy() {
        unbind();
        super.onDestroy();
    }

    public void doSmartCameraModeCallback(int[] data) {
        this.mGet.doSmartCameraModeCallback(data);
    }
}
