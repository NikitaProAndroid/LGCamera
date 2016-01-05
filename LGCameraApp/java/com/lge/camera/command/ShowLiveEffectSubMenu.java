package com.lge.camera.command;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.EffectsBase;
import com.lge.camera.R;
import com.lge.camera.components.RotateView;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.DialogCreater;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;

public class ShowLiveEffectSubMenu extends Command {
    private static int BOARDER_WIDTH = 0;
    private static final int EFFECT = 1;
    private static final int NONE_EFFECT = 0;
    private boolean hasVideoRecordMode;
    private ArrayList<Integer> mBackEffectImage;
    private ArrayList<Integer> mFaceEffectImage;
    private int mFaceMenuOffset;
    private int mFaceSelectedMenu;
    private OnClickListener mOnFaceEffectClickListener;

    static {
        BOARDER_WIDTH = 10;
    }

    public ShowLiveEffectSubMenu(ControllerFunction function) {
        super(function);
        this.mFaceMenuOffset = EFFECT;
        this.mFaceSelectedMenu = 0;
        this.hasVideoRecordMode = false;
        this.mOnFaceEffectClickListener = new OnClickListener() {
            public void onClick(View v) {
                if (ShowLiveEffectSubMenu.this.mGet.isPressedShutterButton()) {
                    CamLog.d(FaceDetector.TAG, "ShutterButton pressed -> block goofyface click");
                    return;
                }
                String value;
                ShowLiveEffectSubMenu.this.hide();
                ShowLiveEffectSubMenu.this.mFaceSelectedMenu = ((Integer) v.getTag()).intValue();
                if (ShowLiveEffectSubMenu.this.mFaceSelectedMenu == 0) {
                    ShowLiveEffectSubMenu.this.mFaceMenuOffset = ShowLiveEffectSubMenu.EFFECT;
                } else {
                    ShowLiveEffectSubMenu.this.mFaceMenuOffset = 0;
                }
                int menuIndex;
                ListPreference pref;
                if (ShowLiveEffectSubMenu.this.hasVideoRecordMode) {
                    value = (String) ShowLiveEffectSubMenu.this.mGet.getLiveEffectList().get(ShowLiveEffectSubMenu.this.mFaceSelectedMenu);
                    menuIndex = ShowLiveEffectSubMenu.this.mGet.getQfIndex(Setting.KEY_VIDEO_RECORD_MODE);
                    ShowLiveEffectSubMenu.this.mGet.setLiveEffect(value);
                    pref = ShowLiveEffectSubMenu.this.mGet.findPreference(Setting.KEY_VIDEO_RECORD_MODE);
                    if (pref != null) {
                        if (ShowLiveEffectSubMenu.this.mGet.isQuickFunctionList(menuIndex)) {
                            ShowLiveEffectSubMenu.this.mGet.setSelectedChild(ShowLiveEffectSubMenu.this.mGet.getChildIndex(CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT));
                            ShowLiveEffectSubMenu.this.mGet.setQuickFunctionControllerAllMenuIcons();
                        } else {
                            ShowLiveEffectSubMenu.this.mGet.setSelectedChild(ShowLiveEffectSubMenu.this.mGet.getCurrentSettingMenuIndex(pref.getKey()), pref.findIndexOfValue(CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT));
                        }
                        ShowLiveEffectSubMenu.this.mGet.setPreviousRecordModeString(CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT);
                    }
                } else {
                    pref = ShowLiveEffectSubMenu.this.mGet.findPreference(Setting.KEY_LIVE_EFFECT);
                    if (pref == null) {
                        CamLog.d(FaceDetector.TAG, "pref is null");
                        return;
                    }
                    value = pref.getEntryValues()[ShowLiveEffectSubMenu.this.mFaceSelectedMenu].toString();
                    ShowLiveEffectSubMenu.this.mGet.setSetting(Setting.KEY_LIVE_EFFECT, value);
                    menuIndex = ShowLiveEffectSubMenu.this.mGet.getQfIndex(Setting.KEY_LIVE_EFFECT);
                    if (ShowLiveEffectSubMenu.this.mGet.isQuickFunctionList(menuIndex)) {
                        ShowLiveEffectSubMenu.this.mGet.setQuickFunctionControllerMenuIcon(menuIndex, ShowLiveEffectSubMenu.this.mFaceSelectedMenu);
                    }
                }
                CamLog.v(FaceDetector.TAG, "mOnFaceEffectClickListener idx:" + ShowLiveEffectSubMenu.this.mFaceSelectedMenu + ", onClick:" + value);
                ShowLiveEffectSubMenu.this.mGet.doCommandUi(Command.SET_LIVE_EFFECT);
            }
        };
        addImageToArray();
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "ShowLiveEffectSubMenu is EXECUTE !!!");
        if (this.mGet.getApplicationMode() != 0) {
            if (!EffectsBase.isEffectSupported(4)) {
                CamLog.d(FaceDetector.TAG, "NOT WORKING!!! live effect does not support in framework!!!");
            } else if (this.mGet.getSubMenuMode() != 18) {
                if (this.mGet.getSubMenuMode() != 0) {
                    this.mGet.clearSubMenu();
                }
                if (this.mGet.isQuickFunctionDragControllerVisible()) {
                    this.mGet.doCommand(Command.HIDE_QUICK_FUNCTION_DRAG_DROP);
                }
                if (this.mGet.isQuickFunctionSettingControllerShowing()) {
                    this.mGet.doCommand(Command.HIDE_QUICK_FUNCTION_SETTING_MENU);
                }
                if (!this.mGet.isNullSettingView()) {
                    this.mGet.setSubMenuMode(0);
                    this.mGet.doCommandUi(Command.REMOVE_SETTING_MENU);
                }
                initializeMenu();
                this.mGet.setSubMenuMode(18);
                if (this.mGet.findViewById(R.id.effect_menu_view).getVisibility() == 0) {
                    hide();
                    return;
                }
                show();
                this.mGet.setPreferenceMenuEnable(Setting.KEY_LIVE_EFFECT, true, false);
                this.mGet.hideFocus();
            }
        }
    }

    private void addImageToArray() {
        this.mFaceEffectImage = new ArrayList();
        this.mFaceEffectImage.add(Integer.valueOf(R.drawable.selector_sub_menu_liveeffect_off));
        this.mFaceEffectImage.add(Integer.valueOf(R.drawable.camera_quickfunction_faces_big_mouth));
        this.mFaceEffectImage.add(Integer.valueOf(R.drawable.camera_quickfunction_faces_big_eyes));
        this.mFaceEffectImage.add(Integer.valueOf(R.drawable.camera_quickfunction_faces_small_mouth));
        this.mFaceEffectImage.add(Integer.valueOf(R.drawable.camera_quickfunction_faces_big_nose));
        this.mFaceEffectImage.add(Integer.valueOf(R.drawable.camera_quickfunction_faces_small_eyes));
        this.mFaceEffectImage.add(Integer.valueOf(R.drawable.camera_quickfunction_faces_squeeze));
        this.mBackEffectImage = new ArrayList();
        this.mBackEffectImage.add(Integer.valueOf(R.drawable.selector_sub_menu_liveeffect_off));
        this.mBackEffectImage.add(Integer.valueOf(R.drawable.camera_quickfunction_background_disco));
        this.mBackEffectImage.add(Integer.valueOf(R.drawable.camera_quickfunction_background_inspace));
        this.mBackEffectImage.add(Integer.valueOf(R.drawable.camera_quickfunction_background_sunset));
        this.mBackEffectImage.add(Integer.valueOf(R.drawable.camera_quickfunction_background_myvideos));
    }

    private void initializeMenu() {
        ArrayList<String> liveEffecEntries;
        ArrayList<String> liveEffecEntriValues;
        ListPreference liveEffectPref = this.mGet.findPreference(Setting.KEY_LIVE_EFFECT);
        ListPreference videoModePref = this.mGet.findPreference(Setting.KEY_VIDEO_RECORD_MODE);
        if (liveEffectPref == null && videoModePref != null) {
            this.hasVideoRecordMode = true;
        }
        String effectSelection = "";
        int len$;
        int i$;
        if (this.hasVideoRecordMode) {
            String[] effectList = this.mGet.getResources().getStringArray(R.array.camcorder_liveeffect_entries);
            liveEffecEntries = new ArrayList();
            String[] arr$ = effectList;
            len$ = arr$.length;
            for (i$ = 0; i$ < len$; i$ += EFFECT) {
                liveEffecEntries.add(arr$[i$]);
            }
            liveEffecEntriValues = this.mGet.getLiveEffectList();
            effectSelection = this.mGet.getLiveEffect();
        } else if (liveEffectPref != null) {
            liveEffecEntries = new ArrayList();
            CharSequence[] arr$2 = liveEffectPref.getEntries();
            len$ = arr$2.length;
            for (i$ = 0; i$ < len$; i$ += EFFECT) {
                liveEffecEntries.add(arr$2[i$].toString());
            }
            liveEffecEntriValues = new ArrayList();
            arr$2 = liveEffectPref.getEntryValues();
            len$ = arr$2.length;
            for (i$ = 0; i$ < len$; i$ += EFFECT) {
                liveEffecEntriValues.add(arr$2[i$].toString());
            }
            effectSelection = this.mGet.getSettingValue(Setting.KEY_LIVE_EFFECT);
        } else {
            return;
        }
        CamLog.d(FaceDetector.TAG, "effectSelection :" + effectSelection);
        if (effectSelection != null) {
            if (effectSelection.equals(CameraConstants.SMART_MODE_OFF)) {
                this.mFaceMenuOffset = EFFECT;
                this.mFaceSelectedMenu = 0;
                if (!this.hasVideoRecordMode) {
                    int menuIndex = this.mGet.getQfIndex(Setting.KEY_LIVE_EFFECT);
                    if (this.mGet.isQuickFunctionList(menuIndex)) {
                        this.mGet.setQuickFunctionControllerMenuIcon(menuIndex, 0);
                    }
                }
            } else {
                int size = liveEffecEntriValues.size();
                for (int i = 0; i < size; i += EFFECT) {
                    if (effectSelection.equals(liveEffecEntriValues.get(i))) {
                        if (i <= 6) {
                            this.mFaceSelectedMenu = i;
                            this.mFaceMenuOffset = 0;
                            break;
                        }
                        this.mFaceSelectedMenu = 0;
                        this.mFaceMenuOffset = EFFECT;
                    }
                }
            }
            makeFaceMenu(liveEffecEntries);
            if (ModelProperties.isOMAP4Chipset() && !this.mGet.isSensorSupportBackdropper()) {
            }
        }
    }

    private void makeFaceMenu(ArrayList<String> liveEffecEntries) {
        int i = this.mFaceMenuOffset;
        int j = 0;
        while (i < this.mFaceEffectImage.size()) {
            ImageView iv = new ImageView(this.mGet.getActivity());
            iv.setImageResource(((Integer) this.mFaceEffectImage.get(i)).intValue());
            iv.setBackgroundResource(R.drawable.selector_osd_menu);
            iv.setTag(Integer.valueOf(i));
            if (this.mFaceSelectedMenu != 0 && this.mFaceSelectedMenu == i) {
                iv.setSelected(true);
            }
            iv.setOnClickListener(this.mOnFaceEffectClickListener);
            String menuString = (String) liveEffecEntries.get(i);
            LayoutParams param1 = new LayoutParams(getPixelFromDimens(R.dimen.liveeffect_test_width), getPixelFromDimens(R.dimen.liveeffect_test_height));
            param1.leftMargin = getPixelFromDimens(R.dimen.liveeffect_test_gap) * j;
            ((RelativeLayout) this.mGet.findViewById(R.id.face_effect_menu)).addView(iv, param1);
            TextView tv = new TextView(this.mGet.getActivity());
            float width = (float) getPixelFromDimens(R.dimen.liveeffect_test_width);
            TextPaint tp = tv.getPaint();
            tv.setTextScaleX(RotateView.DEFAULT_TEXT_SCALE_X);
            tv.setTextColor(-16777216);
            tv.setShadowLayer(0.7f, 0.0f, 0.0f, -1);
            tv.setGravity(17);
            tv.setTextSize(0, (float) getPixelFromDimens(R.dimen.liveeffect_layout_titletextSize));
            float textWidth = tp.measureText(menuString);
            if (textWidth > width - ((float) BOARDER_WIDTH)) {
                tv.setTextScaleX((width - ((float) BOARDER_WIDTH)) / textWidth);
            } else {
                tv.setTextScaleX(RotateView.DEFAULT_TEXT_SCALE_X);
            }
            tv.setText(menuString);
            tv.setTypeface(Typeface.DEFAULT, EFFECT);
            LayoutParams param2 = new LayoutParams(getPixelFromDimens(R.dimen.liveeffect_test_width), -2);
            param2.leftMargin = getPixelFromDimens(R.dimen.liveeffect_test_gap) * j;
            ((RelativeLayout) this.mGet.findViewById(R.id.face_effect_title)).addView(tv, param2);
            i += EFFECT;
            j += EFFECT;
        }
    }

    private void show() {
        CamLog.v(FaceDetector.TAG, "show");
        if (this.hasVideoRecordMode) {
            this.mGet.startSubMenuRotation(this.mGet.getOrientationDegree());
        } else {
            if (this.mGet.isQuickFunctionList(this.mGet.getQfIndex(Setting.KEY_LIVE_EFFECT))) {
                this.mGet.startSubMenuRotation(this.mGet.getOrientationDegree());
            }
        }
        this.mGet.findViewById(R.id.effect_menu_view).setVisibility(0);
        this.mGet.showHelpGuidePopup(Setting.HELP_LIVE_EFFECT, DialogCreater.DIALOG_ID_HELP_LIVE_EFFECT, true);
    }

    private void hide() {
        CamLog.v(FaceDetector.TAG, "hide");
        this.mGet.clearSubMenu();
        this.mGet.setSubMenuMode(0);
        this.mGet.findViewById(R.id.effect_menu_view).setVisibility(8);
        ((RelativeLayout) this.mGet.findViewById(R.id.face_effect_menu)).removeAllViews();
        ((RelativeLayout) this.mGet.findViewById(R.id.face_effect_title)).removeAllViews();
    }
}
