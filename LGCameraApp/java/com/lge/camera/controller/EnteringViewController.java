package com.lge.camera.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings.System;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.lge.camera.R;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.PreferenceProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.PreferenceGroup;
import com.lge.camera.setting.PreferenceInflater;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.ColorUtil;
import com.lge.camera.util.Common;
import com.lge.camera.util.Util;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;

public class EnteringViewController {
    private static EnteringViewController mEngeringViewController;
    public boolean mDoNotShowAgain;
    private ViewGroup mEnteringView;

    public EnteringViewController() {
        this.mEnteringView = null;
        this.mDoNotShowAgain = false;
    }

    static {
        mEngeringViewController = null;
    }

    public static EnteringViewController get() {
        if (mEngeringViewController == null) {
            mEngeringViewController = new EnteringViewController();
        }
        return mEngeringViewController;
    }

    public void unbind() {
        mEngeringViewController = null;
        this.mEnteringView = null;
    }

    private void showGuide(Activity activity, QuickFunctionController qfl, int degree) {
        ViewGroup vg = (ViewGroup) activity.findViewById(R.id.init);
        LayoutInflater li = (LayoutInflater) activity.getSystemService("layout_inflater");
        if (this.mEnteringView == null) {
            this.mEnteringView = (ViewGroup) li.inflate(R.layout.entering_popup, null);
        }
        if (vg != null) {
            if (vg.getParent() != null) {
                vg.removeView(this.mEnteringView);
            }
            vg.addView(this.mEnteringView);
        }
        setEnteringViewQFLImages(activity, qfl);
        this.mEnteringView.setVisibility(0);
        this.mDoNotShowAgain = false;
        if (FunctionProperties.isSupportCameraCleanGuide()) {
            int textWidth = (Common.getPixelFromDimens(activity.getApplicationContext(), R.dimen.lcd_height) - Common.getPixelFromDimens(activity, R.dimen.entering_guide_clean_text_view_portrait_marginTop)) - Common.getPixelFromDimens(activity, R.dimen.entering_guide_clean_text_view_portrait_marginBottom);
            activity.findViewById(R.id.entering_guide_clean_text_view).setVisibility(0);
            TextView tv = (TextView) activity.findViewById(R.id.entering_guide_clean_text);
            String message = activity.getResources().getString(R.string.sp_entering_clean_guide_NORMAL);
            tv.setText(Common.breakTextToMultiLine(tv.getPaint(), message, textWidth));
        }
        if (FunctionProperties.isSupportCameraHandGuide()) {
            activity.findViewById(R.id.entering_guide_hand).setVisibility(0);
            activity.findViewById(R.id.entering_guide_hand_gallery).setVisibility(0);
        }
        View thumbnailView = activity.findViewById(R.id.review_thumbnail);
        if (!(thumbnailView == null || thumbnailView.getVisibility() == 0)) {
            activity.findViewById(R.id.entering_guide_gallery).setVisibility(8);
            activity.findViewById(R.id.entering_guide_arrow_gallery).setVisibility(8);
            activity.findViewById(R.id.entering_guide_hand_gallery).setVisibility(8);
            activity.findViewById(R.id.entering_guide_text_view_gallery).setVisibility(8);
        }
        if (ModelProperties.getCarrierCode() == 6) {
            activity.findViewById(R.id.entering_guide_circles).setVisibility(8);
            activity.findViewById(R.id.entering_guide_circle_vzw).setVisibility(8);
            activity.findViewById(R.id.entering_guide_arrow).setVisibility(8);
            activity.findViewById(R.id.entering_guide_text_view).setVisibility(8);
            ((Button) activity.findViewById(R.id.botton_do_not_show_again)).setText(activity.getResources().getString(R.string.sp_ok_NORMAL));
            TextView textView = (TextView) activity.findViewById(R.id.entering_guide_text);
            tv1.setText(activity.getResources().getString(R.string.entering_guide_vzw));
            textView = (TextView) activity.findViewById(R.id.entering_guide_text_gallery);
            tv2.setText(activity.getResources().getString(R.string.sp_entering_gallery_guide_vzw));
            if (ModelProperties.getProjectCode() == 9 || ModelProperties.getProjectCode() == 14) {
                RelativeLayout rl = (RelativeLayout) activity.findViewById(R.id.entering_guide_gallery);
                LayoutParams rlp = (LayoutParams) rl.getLayoutParams();
                int lcdWidth = Common.getPixelFromDimens(activity.getApplicationContext(), R.dimen.lcd_width);
                int navigationWidth = Common.getPixelFromDimens(activity.getApplicationContext(), R.dimen.preview_panel_marginBottom);
                rlp.topMargin = (lcdWidth - navigationWidth) - Common.getPixelFromDimens(activity.getApplicationContext(), R.dimen.preview_panel_width);
                rlp.addRule(12, 0);
                rl.setLayoutParams(rlp);
            }
        }
        if (FunctionProperties.isSupportHelpSetting()) {
            activity.findViewById(R.id.entering_guide_close_button_View).setVisibility(0);
        }
        enteringGuideRotate(activity, degree);
        requestFocus();
    }

    public void requestFocus() {
        if (((AccessibilityManager) this.mEnteringView.getContext().getSystemService("accessibility")).isEnabled()) {
            Common.setContentDescriptionForAccessibility(this.mEnteringView.getContext(), this.mEnteringView);
            this.mEnteringView.sendAccessibilityEvent(32768);
            this.mEnteringView.requestFocus();
        }
    }

    private void removeGuide(Activity activity) {
        ViewGroup vg = (ViewGroup) activity.findViewById(R.id.init);
        if (this.mEnteringView != null) {
            this.mEnteringView.setContentDescription("\u00a0");
            this.mEnteringView.clearFocus();
            this.mEnteringView.sendAccessibilityEvent(65536);
            this.mEnteringView.setVisibility(8);
            vg.removeView(this.mEnteringView);
            this.mEnteringView = null;
            CamLog.d(FaceDetector.TAG, "showQuickMenuEnteringGuide-gone");
        }
    }

    private void resetDoNotShowCheck(Activity activity, boolean isCheck) {
        Editor edit = activity.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
        ListPreference listPref = ((PreferenceGroup) new PreferenceInflater(activity).inflate(PreferenceProperties.getBackCameraPreferenceResource())).findPreference(Setting.KEY_CAMERA_SHOT_MODE);
        CamLog.d(FaceDetector.TAG, "help-setting resetDoNotShowCheck " + isCheck);
        edit.putBoolean(CameraConstants.ENTERING_GUIDE, isCheck);
        if (!(listPref == null || listPref.findIndexOfValue(CameraConstants.TYPE_SHOTMODE_HDR) == -1)) {
            edit.putBoolean(Setting.HELP_HDR, isCheck);
        }
        if (FunctionProperties.getFunctionFrontCameraBeautyShot() == 1) {
            edit.putBoolean(Setting.HELP_BEAUTY_SHOT, isCheck);
        }
        if (FunctionProperties.isTimeMachinShotSupported()) {
            edit.putBoolean(Setting.HELP_TIMEMACHINE, isCheck);
        }
        edit.putBoolean(Setting.HELP_VOICE_PHOTO, isCheck);
        if (!(listPref == null || listPref.findIndexOfValue(CameraConstants.TYPE_SHOTMODE_PANORAMA) == -1)) {
            edit.putBoolean(Setting.HELP_PANORAMA, isCheck);
        }
        if (!(listPref == null || listPref.findIndexOfValue(CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA) == -1)) {
            edit.putBoolean(Setting.HELP_PLANE_PANORAMA, isCheck);
        }
        if (FunctionProperties.isFreePanoramaSupported()) {
            edit.putBoolean(Setting.HELP_FREE_PANORAMA, isCheck);
        }
        if (FunctionProperties.isSupportBurstShot()) {
            edit.putBoolean(Setting.HELP_BURST_SHOT, isCheck);
        } else {
            edit.putBoolean(Setting.HELP_CONTINUOUS_SHOT, isCheck);
        }
        if (FunctionProperties.isSupportSmartMode()) {
            edit.putBoolean(Setting.HELP_INTELLIGENT_AUTO_MODE, isCheck);
        }
        if (FunctionProperties.isWDRSupported()) {
            if (FunctionProperties.isHDRRecordingNameUsed()) {
                edit.putBoolean(Setting.HELP_HDR_MOVIE, isCheck);
            } else {
                edit.putBoolean(Setting.HELP_WDR_MOVIE, isCheck);
            }
        }
        if (MultimediaProperties.isDualRecordingSupported()) {
            edit.putBoolean(Setting.HELP_DUAL_RECORDING, isCheck);
        }
        if (MultimediaProperties.isDualCameraSupported()) {
            edit.putBoolean(Setting.HELP_DUAL_CAMERA, isCheck);
        }
        if (FunctionProperties.isSupportAudiozoom()) {
            edit.putBoolean(Setting.HELP_AUDIOZOOM, isCheck);
        }
        if (FunctionProperties.isClearShotSupported()) {
            edit.putBoolean(Setting.HELP_CLEAR_SHOT, isCheck);
        }
        if (MultimediaProperties.isSmartZoomSupported()) {
            edit.putBoolean(Setting.HELP_SMART_ZOOM_RECORDING, isCheck);
        }
        if (FunctionProperties.isSupportSportShot()) {
            edit.putBoolean(Setting.HELP_SPORTS, isCheck);
        }
        if (FunctionProperties.isSupportNightShotModeMenu(1)) {
            edit.putBoolean(LGParameters.SCENE_MODE_NIGHT, isCheck);
        }
        edit.apply();
    }

    public static boolean isAllCheckDoNotShowAgain(Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences(Setting.SETTING_PRIMARY, 0);
        PreferenceInflater inflater = new PreferenceInflater(activity);
        PreferenceGroup cameraPrefGroup = (PreferenceGroup) inflater.inflate(PreferenceProperties.getBackCameraPreferenceResource());
        PreferenceGroup camcorderPrefGroup = (PreferenceGroup) inflater.inflate(PreferenceProperties.getBackCamcorderPreferenceResource());
        if (pref != null && pref.getBoolean(CameraConstants.ENTERING_GUIDE, false) && pref.getBoolean(Setting.HELP_VOICE_PHOTO, false) && checkShotModeHelp(cameraPrefGroup, CameraConstants.TYPE_SHOTMODE_HDR, pref, Setting.HELP_HDR) && checkShotModeHelp(cameraPrefGroup, CameraConstants.TYPE_SHOTMODE_TIMEMACHINE, pref, Setting.HELP_TIMEMACHINE) && checkShotModeHelp(cameraPrefGroup, CameraConstants.TYPE_SHOTMODE_PANORAMA, pref, Setting.HELP_PANORAMA) && checkShotModeHelp(cameraPrefGroup, CameraConstants.TYPE_SHOTMODE_PLANE_PANORAMA, pref, Setting.HELP_PLANE_PANORAMA) && checkShotModeHelp(cameraPrefGroup, CameraConstants.TYPE_SHOTMODE_FREE_PANORAMA, pref, Setting.HELP_FREE_PANORAMA) && checkShotModeHelp(cameraPrefGroup, CameraConstants.TYPE_SHOTMODE_DUAL_CAMERA, pref, Setting.HELP_DUAL_CAMERA) && checkShotModeHelp(cameraPrefGroup, CameraConstants.TYPE_SHOTMODE_CLEAR_SHOT, pref, Setting.HELP_CLEAR_SHOT) && checkRecordModeHelp(camcorderPrefGroup, CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT, pref, Setting.HELP_LIVE_EFFECT) && checkRecordModeHelp(camcorderPrefGroup, CameraConstants.TYPE_RECORDMODE_DUAL, pref, Setting.HELP_DUAL_RECORDING) && checkRecordModeHelp(camcorderPrefGroup, CameraConstants.TYPE_RECORDMODE_SMART_ZOOM, pref, Setting.HELP_SMART_ZOOM_RECORDING) && checkOtherHelp(FunctionProperties.isSupportSmartMode(), pref, Setting.HELP_INTELLIGENT_AUTO_MODE) && checkOtherHelp(FunctionProperties.isSupportAudiozoom(), pref, Setting.HELP_AUDIOZOOM) && checkOtherHelp(FunctionProperties.isSupportSportShot(), pref, Setting.HELP_SPORTS) && checkOtherHelp(FunctionProperties.isSupportNightShotModeMenu(1), pref, LGParameters.SCENE_MODE_NIGHT) && checkOtherHelp(FunctionProperties.isSupportBurstShot(), pref, Setting.HELP_BURST_SHOT) && checkOtherHelp(FunctionProperties.isFullFrameContinuousShotSupported(), pref, Setting.HELP_CONTINUOUS_SHOT)) {
            boolean z;
            if (FunctionProperties.isWDRSupported() && FunctionProperties.isHDRRecordingNameUsed()) {
                z = true;
            } else {
                z = false;
            }
            if (checkOtherHelp(z, pref, Setting.HELP_HDR_MOVIE)) {
                if (!FunctionProperties.isWDRSupported() || FunctionProperties.isHDRRecordingNameUsed()) {
                    z = false;
                } else {
                    z = true;
                }
                if (checkOtherHelp(z, pref, Setting.HELP_WDR_MOVIE) && ((FunctionProperties.getFunctionFrontCameraBeautyShot() == 1 && pref.getBoolean(Setting.HELP_BEAUTY_SHOT, false)) || FunctionProperties.getFunctionFrontCameraBeautyShot() == 0)) {
                    CamLog.d(FaceDetector.TAG, "help-setting isAllCheckDoNotShowAgain all checked");
                    return true;
                }
            }
        }
        CamLog.d(FaceDetector.TAG, "help-setting isAllCheckDoNotShowAgain not all checked");
        return false;
    }

    private static boolean checkShotModeHelp(PreferenceGroup prefGroup, String shotMode, SharedPreferences pref, String SettingValue) {
        ListPreference listPref = prefGroup.findPreference(Setting.KEY_CAMERA_SHOT_MODE);
        if (listPref == null || listPref.findIndexOfValue(shotMode) == -1 || pref.getBoolean(SettingValue, false)) {
            return true;
        }
        return false;
    }

    private static boolean checkRecordModeHelp(PreferenceGroup prefGroup, String shotMode, SharedPreferences pref, String settingValue) {
        ListPreference listPref = prefGroup.findPreference(Setting.KEY_VIDEO_RECORD_MODE);
        if (listPref == null || listPref.findIndexOfValue(shotMode) == -1 || pref.getBoolean(settingValue, false)) {
            return true;
        }
        return false;
    }

    private static boolean checkOtherHelp(boolean checkCondition, SharedPreferences pref, String settingValue) {
        if (checkCondition && !pref.getBoolean(settingValue, false)) {
            return false;
        }
        return true;
    }

    public static void setSystemHelpSettingOff(Activity activity) {
        if (FunctionProperties.isSupportHelpSetting() && isAllCheckDoNotShowAgain(activity)) {
            CamLog.d(FaceDetector.TAG, "help-setting setSystemHelpSettingOff ");
            System.putInt(activity.getContentResolver(), "help_settings_camera_tips", 0);
            setCameraHelpSetting(activity, false);
        }
    }

    public static void setCameraHelpSetting(Activity activity, boolean set) {
        if (FunctionProperties.isSupportHelpSetting()) {
            Editor edit = activity.getSharedPreferences(Setting.SETTING_PRIMARY, 0).edit();
            edit.putBoolean(CameraConstants.HELP_SETTING, set);
            edit.apply();
            CamLog.d(FaceDetector.TAG, "help-setting setCameraHelpSetting " + set);
        }
    }

    public void showQuickMenuEnteringGuide(Activity activity, QuickFunctionController qfl, boolean isShow, int degree) {
        int help_setting = System.getInt(activity.getContentResolver(), "help_settings_camera_tips", 0);
        int refresh_help = System.getInt(activity.getContentResolver(), "help_settings_camera_refresh", 0);
        SharedPreferences pref = activity.getSharedPreferences(Setting.SETTING_PRIMARY, 0);
        if (activity != null && qfl != null && pref != null) {
            Editor edit;
            if (FunctionProperties.isSupportHelpSetting()) {
                if (help_setting == 1 && (refresh_help == 1 || !pref.getBoolean(CameraConstants.HELP_SETTING, true))) {
                    CamLog.d(FaceDetector.TAG, "showQuickMenuEnteringGuide DB state change off=>on");
                    resetDoNotShowCheck(activity, false);
                    setCameraHelpSetting(activity, true);
                } else if (help_setting == 0 && pref.getBoolean(CameraConstants.HELP_SETTING, true)) {
                    CamLog.d(FaceDetector.TAG, "showQuickMenuEnteringGuide DB state change on=>off");
                    resetDoNotShowCheck(activity, true);
                    setCameraHelpSetting(activity, false);
                }
                System.putInt(activity.getContentResolver(), "help_settings_camera_refresh", 0);
                if (!pref.getBoolean(CameraConstants.ENTERING_GUIDE, false)) {
                    if (isShow) {
                        showGuide(activity, qfl, degree);
                    } else if (this.mEnteringView != null) {
                        removeGuide(activity);
                        if (this.mDoNotShowAgain) {
                            edit = pref.edit();
                            edit.putBoolean(CameraConstants.ENTERING_GUIDE, true);
                            edit.apply();
                            setSystemHelpSettingOff(activity);
                        }
                    }
                }
            } else if (!pref.getBoolean(CameraConstants.ENTERING_GUIDE, false)) {
                if (isShow) {
                    showGuide(activity, qfl, degree);
                } else if (this.mEnteringView != null) {
                    removeGuide(activity);
                    edit = pref.edit();
                    edit.putBoolean(CameraConstants.ENTERING_GUIDE, true);
                    edit.apply();
                }
            }
        }
    }

    public void hideOnPauseEnteringGuide(Activity activity) {
        if (activity != null) {
            SharedPreferences pref = activity.getSharedPreferences(Setting.SETTING_PRIMARY, 0);
            if (pref != null && !pref.getBoolean(CameraConstants.ENTERING_GUIDE, false)) {
                removeGuide(activity);
            }
        }
    }

    public void setEnteringViewQFLImages(Activity activity, QuickFunctionController qfl) {
        if (this.mEnteringView != null && activity != null && qfl != null) {
            ImageView circle1 = (ImageView) activity.findViewById(R.id.entering_guide_circle_image1);
            circle1.setImageResource(qfl.getMenuIconResource(0));
            circle1.setColorFilter(ColorUtil.getEnteringQFLColor());
            ImageView circle2 = (ImageView) activity.findViewById(R.id.entering_guide_circle_image2);
            circle2.setImageResource(qfl.getMenuIconResource(1));
            circle2.setColorFilter(ColorUtil.getEnteringQFLColor());
            ImageView circle3 = (ImageView) activity.findViewById(R.id.entering_guide_circle_image3);
            circle3.setImageResource(qfl.getMenuIconResource(2));
            circle3.setColorFilter(ColorUtil.getEnteringQFLColor());
            ImageView circle4 = (ImageView) activity.findViewById(R.id.entering_guide_circle_image4);
            circle4.setImageResource(qfl.getMenuIconResource(3));
            circle4.setColorFilter(ColorUtil.getEnteringQFLColor());
        }
    }

    public boolean isEnteringViewShowing() {
        return this.mEnteringView != null && this.mEnteringView.getVisibility() == 0;
    }

    public void enteringGuideRotate(Activity activity, int degree) {
        int constDegree = Tag.IMAGE_DESCRIPTION;
        if (activity != null && isEnteringViewShowing()) {
            rotateLayout(activity, R.id.entering_guide_circle1, degree);
            rotateLayout(activity, R.id.entering_guide_circle2, degree);
            rotateLayout(activity, R.id.entering_guide_circle3, degree);
            rotateLayout(activity, R.id.entering_guide_circle4, degree);
            if (ModelProperties.isWVGAmodel()) {
                TextView tv = (TextView) activity.findViewById(R.id.entering_guide_text_gallery);
                if (tv != null) {
                    String msg = activity.getResources().getString(R.string.sp_entering_gallery_guide_vzw);
                    int convDegree = Util.convertDegree(activity.getApplicationContext().getResources(), degree);
                    if (convDegree == 90 || convDegree == Tag.IMAGE_DESCRIPTION) {
                        tv.setText(Common.breakTextToMultiLine(tv.getPaint(), msg.replace("\n", ""), (Common.getPixelFromDimens(activity.getApplicationContext(), R.dimen.lcd_height) - Common.getPixelFromDimens(activity, R.dimen.entering_guide_clean_text_view_portrait_marginTop)) - Common.getPixelFromDimens(activity, R.dimen.entering_guide_clean_text_view_portrait_marginBottom)));
                    } else {
                        tv.setText(msg);
                    }
                }
            }
            rotateTextLayout(activity, R.id.entering_guide_text_view, degree);
            rotateTextLayout(activity, R.id.entering_guide_text_view_gallery, degree);
            rotateTextLayout(activity, R.id.entering_guide_clean_text_view, degree);
            if (Util.isConfigureLandscape(activity.getResources())) {
                constDegree = 0;
            }
            rotateLayout(activity, R.id.entering_guide_hand, constDegree);
            rotateLayout(activity, R.id.entering_guide_arrow, constDegree);
            rotateLayoutWithMargin(activity, R.id.entering_guide_arrow_gallery, constDegree);
            rotateLayoutWithMargin(activity, R.id.entering_guide_hand_gallery, constDegree);
            rotateTextLayout(activity, R.id.entering_guide_close_button_View, degree);
        }
    }

    private void rotateLayout(Activity activity, int resId, int degree) {
        try {
            RotateLayout rl = (RotateLayout) activity.findViewById(resId);
            if (rl != null) {
                rl.rotateLayout(degree);
            }
        } catch (ClassCastException e) {
            CamLog.w(FaceDetector.TAG, "ClassCastException:", e);
        }
    }

    private void rotateTextLayout(Activity activity, int resId, int degree) {
        try {
            RotateLayout rl = (RotateLayout) activity.findViewById(resId);
            if (rl != null) {
                rl.measure(0, 0);
                LayoutParams rlp = (LayoutParams) rl.getLayoutParams();
                if (rlp != null) {
                    int adjLong;
                    int adjShort;
                    int lcdWidth = Common.getPixelFromDimens(activity.getApplicationContext(), R.dimen.lcd_width);
                    int lcdHeight = Common.getPixelFromDimens(activity.getApplicationContext(), R.dimen.lcd_height);
                    int measureWidth = rl.getMeasuredWidth();
                    int measureHeight = rl.getMeasuredHeight();
                    if (measureWidth >= measureHeight) {
                        adjLong = measureWidth;
                        adjShort = measureHeight;
                    } else {
                        adjLong = measureHeight;
                        adjShort = measureWidth;
                    }
                    Common.resetLayoutParameter(rlp);
                    Context context = activity.getApplicationContext();
                    int convDegree = Util.convertDegree(context.getResources(), degree);
                    int leftMargin = 0;
                    int topMargin = 0;
                    int rightMargin = 0;
                    int bottomMargin = 0;
                    switch (convDegree) {
                        case LGKeyRec.EVENT_INVALID /*0*/:
                        case MediaProviderUtils.ROTATION_180 /*180*/:
                            if (resId != 2131558523) {
                                if (resId != 2131558531) {
                                    if (resId != 2131558529) {
                                        if (resId == 2131558533) {
                                            if (!Util.isConfigureLandscape(context.getResources())) {
                                                rlp.addRule(15, 1);
                                                rlp.addRule(21, 1);
                                                topMargin = Common.getPixelFromDimens(context, R.dimen.entering_guide_button_marginTop);
                                                break;
                                            }
                                            rlp.addRule(14, 1);
                                            topMargin = Common.getPixelFromDimens(context, R.dimen.entering_guide_button_marginTop);
                                            break;
                                        }
                                    }
                                    rightMargin = Common.getPixelFromDimens(context, R.dimen.entering_guide_text_view_gallery_marginRight) - adjShort;
                                    topMargin = Common.getPixelFromDimens(context, R.dimen.entering_guide_text_view_gallery_marginTop);
                                    leftMargin = (lcdWidth - rightMargin) - adjLong;
                                    bottomMargin = (lcdHeight - topMargin) - adjShort;
                                    rlp.addRule(21, 1);
                                    break;
                                }
                                topMargin = Common.getPixelFromDimens(context, R.dimen.entering_guide_clean_text_view_marginTop);
                                if (!Util.isConfigureLandscape(context.getResources())) {
                                    rlp.addRule(15, 1);
                                    rlp.addRule(21, 1);
                                    break;
                                }
                                rlp.addRule(14, 1);
                                rlp.addRule(10, 1);
                                break;
                            }
                            leftMargin = Common.getPixelFromDimens(context, R.dimen.entering_guide_text_view_marginLeft);
                            topMargin = Common.getPixelFromDimens(context, R.dimen.entering_guide_text_view_marginTop);
                            rightMargin = 0;
                            bottomMargin = 0;
                            if (!Util.isConfigureLandscape(context.getResources())) {
                                rlp.addRule(21, 1);
                                break;
                            } else {
                                rlp.addRule(20, 1);
                                break;
                            }
                            break;
                        case MediaProviderUtils.ROTATION_90 /*90*/:
                        case Tag.IMAGE_DESCRIPTION /*270*/:
                            if (resId != 2131558523) {
                                if (resId != 2131558531) {
                                    if (resId != 2131558529) {
                                        if (resId == 2131558533) {
                                            if (!Util.isConfigureLandscape(context.getResources())) {
                                                if (ModelProperties.getProjectCode() == 14) {
                                                    rlp.addRule(14, 1);
                                                } else {
                                                    rlp.addRule(21, 1);
                                                }
                                                leftMargin = Common.getPixelFromDimens(context, R.dimen.entering_guide_button_portrait_marginLeft);
                                                break;
                                            }
                                            leftMargin = Common.getPixelFromDimens(context, R.dimen.entering_guide_button_portrait_marginLeft);
                                            break;
                                        }
                                    }
                                    rightMargin = Common.getPixelFromDimens(context, R.dimen.entering_guide_text_view_gallery_marginRight);
                                    topMargin = (lcdHeight - adjLong) / 2;
                                    if (topMargin < 0) {
                                        topMargin = 0;
                                    }
                                    leftMargin = ((lcdWidth - rightMargin) - adjShort) - Common.getPixelFromDimens(activity, R.dimen.entering_guide_text_view_gallery_marginLeft_adjust);
                                    bottomMargin = topMargin;
                                    rlp.addRule(21, 1);
                                    break;
                                }
                                rlp.addRule(13, 1);
                                break;
                            }
                            leftMargin = Common.getPixelFromDimens(context, R.dimen.entering_guide_text_view_marginLeft) + adjShort;
                            topMargin = Common.getPixelFromDimens(context, R.dimen.entering_guide_text_view_marginTop) - (adjLong / 2);
                            rightMargin = 0;
                            bottomMargin = 0;
                            if (!Util.isConfigureLandscape(context.getResources())) {
                                rlp.addRule(21, 1);
                                break;
                            } else {
                                rlp.addRule(20, 1);
                                break;
                            }
                            break;
                    }
                    if (Util.isConfigureLandscape(context.getResources())) {
                        rlp.leftMargin = leftMargin;
                        rlp.topMargin = topMargin;
                        rlp.rightMargin = rightMargin;
                        rlp.bottomMargin = bottomMargin;
                    } else {
                        rlp.leftMargin = bottomMargin;
                        rlp.topMargin = leftMargin;
                        rlp.rightMargin = topMargin;
                        rlp.bottomMargin = rightMargin;
                    }
                    if (resId == 2131558529) {
                        TextView tv = (TextView) activity.findViewById(R.id.entering_guide_text_gallery);
                        if (tv != null) {
                            if (convDegree == 0 || convDegree == 270) {
                                tv.setGravity(8388613);
                            } else {
                                tv.setGravity(8388611);
                            }
                        }
                    }
                    rl.setLayoutParams(rlp);
                    rl.rotateLayout(degree);
                }
            }
        } catch (Exception e) {
            CamLog.w(FaceDetector.TAG, "Exception:", e);
        }
    }

    private void rotateLayoutWithMargin(Activity activity, int resId, int degree) {
        try {
            RotateLayout rl = (RotateLayout) activity.findViewById(resId);
            if (rl != null) {
                rl.measure(0, 0);
                LayoutParams rlp = (LayoutParams) rl.getLayoutParams();
                if (rlp != null) {
                    int adjLong;
                    int adjShort;
                    int lcdWidth = Common.getPixelFromDimens(activity.getApplicationContext(), R.dimen.lcd_width);
                    int lcdHeight = Common.getPixelFromDimens(activity.getApplicationContext(), R.dimen.lcd_height);
                    int measureWidth = rl.getMeasuredWidth();
                    int measureHeight = rl.getMeasuredHeight();
                    if (measureWidth >= measureHeight) {
                        adjLong = measureWidth;
                        adjShort = measureHeight;
                    } else {
                        adjLong = measureHeight;
                        adjShort = measureWidth;
                    }
                    Context context = activity.getApplicationContext();
                    int leftMargin = 0;
                    int topMargin = 0;
                    int rightMargin = 0;
                    int bottomMargin = 0;
                    switch (resId) {
                        case R.id.entering_guide_arrow_gallery /*2131558527*/:
                            rightMargin = Common.getPixelFromDimens(context, R.dimen.entering_guide_arrow_gallery_marginRight);
                            topMargin = Common.getPixelFromDimens(context, R.dimen.entering_guide_arrow_gallery_marginTop);
                            leftMargin = (lcdWidth - rightMargin) - adjLong;
                            bottomMargin = (lcdHeight - topMargin) - adjShort;
                            break;
                        case R.id.entering_guide_hand_gallery /*2131558528*/:
                            rightMargin = Common.getPixelFromDimens(context, R.dimen.entering_guide_hand_gallery_marginRight);
                            topMargin = Common.getPixelFromDimens(context, R.dimen.entering_guide_hand_gallery_marginTop);
                            leftMargin = (lcdWidth - rightMargin) - adjLong;
                            bottomMargin = (lcdHeight - topMargin) - adjShort;
                            break;
                    }
                    if (Util.isConfigureLandscape(context.getResources())) {
                        rlp.leftMargin = leftMargin;
                        rlp.topMargin = topMargin;
                        rlp.rightMargin = rightMargin;
                        rlp.bottomMargin = bottomMargin;
                    } else {
                        rlp.leftMargin = bottomMargin;
                        rlp.topMargin = leftMargin;
                        rlp.rightMargin = topMargin;
                        rlp.bottomMargin = rightMargin;
                    }
                    rl.setLayoutParams(rlp);
                    rl.rotateLayout(degree);
                }
            }
        } catch (Exception e) {
            CamLog.w(FaceDetector.TAG, "Exception:", e);
        }
    }
}
