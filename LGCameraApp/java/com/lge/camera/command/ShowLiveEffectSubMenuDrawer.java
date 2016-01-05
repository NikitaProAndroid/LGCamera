package com.lge.camera.command;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.EffectsBase;
import com.lge.camera.R;
import com.lge.camera.components.MultiDirectionSlidingDrawer;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.components.RotateView;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;

public class ShowLiveEffectSubMenuDrawer extends Command {
    private static int BOARDER_WIDTH = 0;
    public static final int DEFAULT_EFFECT = 1;
    private static final int NONE_EFFECT = 0;
    private static int mFaceSelectedMenu;
    private boolean hasVideoRecordMode;
    private boolean isLayoutInited;
    private ArrayList<RotateLayout> mEffectViewList;
    private ArrayList<Integer> mFaceEffectImage;
    private int mFaceMenuOffset;
    private OnClickListener mOnFaceEffectClickListener;

    static {
        BOARDER_WIDTH = 10;
        mFaceSelectedMenu = DEFAULT_EFFECT;
    }

    public ShowLiveEffectSubMenuDrawer(ControllerFunction function) {
        super(function);
        this.mFaceMenuOffset = checkRecordMode() ? DEFAULT_EFFECT : 0;
        this.hasVideoRecordMode = false;
        this.mEffectViewList = null;
        this.isLayoutInited = false;
        this.mOnFaceEffectClickListener = new OnClickListener() {
            public void onClick(View v) {
                if (ShowLiveEffectSubMenuDrawer.this.mGet.isPressedShutterButton()) {
                    CamLog.d(FaceDetector.TAG, "ShutterButton pressed -> block goofyface click");
                    return;
                }
                ShowLiveEffectSubMenuDrawer.mFaceSelectedMenu = ((Integer) v.getTag()).intValue();
                ShowLiveEffectSubMenuDrawer.this.updateSettingMenu(ShowLiveEffectSubMenuDrawer.mFaceSelectedMenu);
                ShowLiveEffectSubMenuDrawer.this.clearSelection();
                v.setSelected(true);
            }
        };
        this.hasVideoRecordMode = checkRecordMode();
        addImageToArray();
    }

    private boolean checkRecordMode() {
        ListPreference liveEffectPref = this.mGet.findPreference(Setting.KEY_LIVE_EFFECT);
        ListPreference videoModePref = this.mGet.findPreference(Setting.KEY_VIDEO_RECORD_MODE);
        if (liveEffectPref != null || videoModePref == null) {
            return false;
        }
        return true;
    }

    public void execute() {
        execute(new Bundle());
    }

    public void execute(Object arg) {
        CamLog.d(FaceDetector.TAG, "ShowLiveEffectSubMenuDrawer is EXECUTE !!!");
        boolean isDrawerOpen = true;
        if (arg instanceof Bundle) {
            isDrawerOpen = ((Bundle) arg).getBoolean("menu_open", true);
        }
        if (this.mGet.getApplicationMode() != 0) {
            if (EffectsBase.isEffectSupported(4)) {
                setCurrentLiveEffectWithDefault();
                View view = this.mGet.findViewById(R.id.live_effect_sliding_drawer_menu_view);
                if (view == null || view.getVisibility() != 0 || isDrawerOpen) {
                    if (!this.hasVideoRecordMode && isDrawerOpen) {
                        updateSettingMenu(DEFAULT_EFFECT);
                        setCurrentLiveEffectWithDefault();
                    }
                    if (!this.isLayoutInited) {
                        initializeMenu();
                    }
                    clearSelection();
                    updateMenuViewWithCurrentLiveEffect();
                    show(isDrawerOpen);
                    this.mGet.setPreferenceMenuEnable(Setting.KEY_LIVE_EFFECT, true, false);
                    this.mGet.hideFocus();
                    return;
                } else if (this.hasVideoRecordMode) {
                    CamLog.d(FaceDetector.TAG, "live_effect_sliding_drawer_menu_view is already visible");
                    return;
                } else {
                    CamLog.d(FaceDetector.TAG, "live_effect_sliding_drawer_menu_view is already visible, only open sliding drawer");
                    animateOpen();
                    return;
                }
            }
            CamLog.d(FaceDetector.TAG, "NOT WORKING!!! live effect is not supported by framework!!!");
        }
    }

    public void setCurrentLiveEffectWithDefault() {
        mFaceSelectedMenu = this.mGet.getLiveEffectList().indexOf(this.mGet.getLiveEffect());
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
    }

    private void initializeMenu() {
        ArrayList<String> liveEffecEntries;
        ArrayList<String> liveEffecEntriValues;
        String effectSelection = "";
        int len$;
        int i$;
        if (this.hasVideoRecordMode) {
            String[] effectList = this.mGet.getResources().getStringArray(R.array.camcorder_liveeffect_entries);
            liveEffecEntries = new ArrayList();
            String[] arr$ = effectList;
            len$ = arr$.length;
            for (i$ = 0; i$ < len$; i$ += DEFAULT_EFFECT) {
                liveEffecEntries.add(arr$[i$]);
            }
            liveEffecEntriValues = this.mGet.getLiveEffectList();
            effectSelection = this.mGet.getLiveEffect();
        } else {
            ListPreference liveEffectPref = this.mGet.findPreference(Setting.KEY_LIVE_EFFECT);
            liveEffecEntries = new ArrayList();
            CharSequence[] arr$2 = liveEffectPref.getEntries();
            len$ = arr$2.length;
            for (i$ = 0; i$ < len$; i$ += DEFAULT_EFFECT) {
                liveEffecEntries.add(arr$2[i$].toString());
            }
            liveEffecEntriValues = new ArrayList();
            arr$2 = liveEffectPref.getEntryValues();
            len$ = arr$2.length;
            for (i$ = 0; i$ < len$; i$ += DEFAULT_EFFECT) {
                liveEffecEntriValues.add(arr$2[i$].toString());
            }
            effectSelection = this.mGet.getSettingValue(Setting.KEY_LIVE_EFFECT);
        }
        CamLog.d(FaceDetector.TAG, "effectSelection :" + effectSelection);
        if (effectSelection != null) {
            int size = liveEffecEntriValues.size();
            for (int i = 0; i < size; i += DEFAULT_EFFECT) {
                if (effectSelection.equals(liveEffecEntriValues.get(i))) {
                    if (i <= 6) {
                        mFaceSelectedMenu = i;
                        break;
                    }
                    mFaceSelectedMenu = 0;
                }
            }
            makeFaceMenu(liveEffecEntries);
            this.isLayoutInited = true;
        }
    }

    private void makeFaceMenu(ArrayList<String> liveEffecEntries) {
        this.mEffectViewList = new ArrayList();
        int i = this.mFaceMenuOffset;
        while (true) {
            if (i < this.mFaceEffectImage.size()) {
                RotateLayout rl = new RotateLayout(this.mGet.getActivity());
                rl.setLayoutParams(new LayoutParams(R.dimen.live_effect_drawer_rotatelayout_width, R.dimen.live_effect_drawer_rotatelayout_height));
                rl.setBackgroundResource(R.drawable.selector_pip_frame_menu);
                rl.setTag(Integer.valueOf(i));
                this.mEffectViewList.add(rl);
                rl.setOnClickListener(this.mOnFaceEffectClickListener);
                LinearLayout ll = new LinearLayout(this.mGet.getActivity());
                ll.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
                ll.setOrientation(DEFAULT_EFFECT);
                ImageView iv = new ImageView(this.mGet.getActivity());
                iv.setImageResource(((Integer) this.mFaceEffectImage.get(i)).intValue());
                String menuString = (String) liveEffecEntries.get(i);
                View textView = new TextView(this.mGet.getActivity());
                float width = (float) getPixelFromDimens(R.dimen.live_effect_drawer_rotatelayout_width);
                float strokeSize = (float) getPixelFromDimens(R.dimen.live_effect_drawer_textview_strokeSize);
                TextPaint tp = textView.getPaint();
                textView.setTextScaleX(RotateView.DEFAULT_TEXT_SCALE_X);
                textView.setTextColor(-16777216);
                textView.setShadowLayer(strokeSize, 0.0f, 0.0f, Color.argb(153, 0, 0, 0));
                textView.setGravity(17);
                textView.setTextSize(0, (float) getPixelFromDimens(R.dimen.live_effect_drawer_textview_textSize));
                float textWidth = tp.measureText(menuString);
                if (textWidth > width - ((float) BOARDER_WIDTH)) {
                    textView = textView;
                    textView.setTextScaleX((width - ((float) BOARDER_WIDTH)) / textWidth);
                } else {
                    textView.setTextScaleX(RotateView.DEFAULT_TEXT_SCALE_X);
                }
                textView.setText(menuString);
                ll.addView(iv);
                ll.addView(textView);
                rl.addView(ll);
                MarginLayoutParams params = (MarginLayoutParams) iv.getLayoutParams();
                params.topMargin = getPixelFromDimens(R.dimen.live_effect_drawer_imageview_imageTopMargin);
                params.bottomMargin = getPixelFromDimens(R.dimen.live_effect_drawer_imageview_imageBottomMargin);
                iv.setLayoutParams(params);
                LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(getPixelFromDimens(R.dimen.live_effect_drawer_rotatelayout_width), getPixelFromDimens(R.dimen.live_effect_drawer_rotatelayout_height));
                View view = this.mGet.findViewById(R.id.live_effect_sliding_drawer_menu);
                if (view != null) {
                    ((LinearLayout) view).addView(rl, param1);
                }
                i += DEFAULT_EFFECT;
            } else {
                return;
            }
        }
    }

    private void show(boolean open) {
        CamLog.v(FaceDetector.TAG, "show");
        View menuView = this.mGet.findViewById(R.id.live_effect_sliding_drawer_menu);
        if (menuView != null) {
            if (menuView.findViewWithTag(Integer.valueOf(DEFAULT_EFFECT)) == null) {
                initializeMenu();
                clearSelection();
                updateMenuViewWithCurrentLiveEffect();
            }
            this.mGet.startLiveEffectDrawerSubMenuRotation(this.mGet.getOrientationDegree());
            View view = this.mGet.findViewById(R.id.live_effect_sliding_drawer_menu_view);
            if (view != null) {
                view.setVisibility(0);
            }
            if (open) {
                animateOpen();
            }
        }
    }

    private void animateOpen() {
        CamLog.v(FaceDetector.TAG, "animation open");
        this.mGet.clearSubMenu();
        this.mGet.setSubMenuMode(0);
        MultiDirectionSlidingDrawer drawer = (MultiDirectionSlidingDrawer) this.mGet.findViewById(R.id.live_effect_sliding_drawer_menu_slide);
        if (drawer != null && !drawer.isOpened()) {
            drawer.animateOpen();
        }
    }

    private void clearSelection() {
        if (this.mEffectViewList != null) {
            for (int i = 0; i < this.mEffectViewList.size(); i += DEFAULT_EFFECT) {
                ((RotateLayout) this.mEffectViewList.get(i)).setSelected(false);
            }
        }
    }

    private void updateMenuViewWithCurrentLiveEffect() {
        int i = DEFAULT_EFFECT;
        if (this.mEffectViewList != null) {
            CamLog.d(FaceDetector.TAG, "mFaceSelectedMenu: " + mFaceSelectedMenu + " mFaceMenuOffset: " + this.mFaceMenuOffset);
            if (mFaceSelectedMenu - this.mFaceMenuOffset >= this.mEffectViewList.size() || mFaceSelectedMenu - this.mFaceMenuOffset < 0) {
                mFaceSelectedMenu = DEFAULT_EFFECT;
                if (!checkRecordMode()) {
                    i = 0;
                }
                this.mFaceMenuOffset = i;
            }
            try {
                ((RotateLayout) this.mEffectViewList.get(mFaceSelectedMenu - this.mFaceMenuOffset)).setSelected(true);
            } catch (ArrayIndexOutOfBoundsException e) {
                CamLog.d(FaceDetector.TAG, "ArrayIndexOutOfBoundsException");
            }
        }
    }

    private void updateSettingMenu(int selectedMenu) {
        String value;
        int menuIndex;
        ListPreference pref;
        if (this.hasVideoRecordMode) {
            value = (String) this.mGet.getLiveEffectList().get(selectedMenu);
            menuIndex = this.mGet.getQfIndex(Setting.KEY_VIDEO_RECORD_MODE);
            this.mGet.setLiveEffect(value);
            pref = this.mGet.findPreference(Setting.KEY_VIDEO_RECORD_MODE);
            if (pref != null) {
                if (this.mGet.isQuickFunctionList(menuIndex)) {
                    this.mGet.setSelectedChild(this.mGet.getChildIndex(CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT));
                    this.mGet.setQuickFunctionControllerAllMenuIcons();
                } else {
                    this.mGet.setSelectedChild(this.mGet.getCurrentSettingMenuIndex(pref.getKey()), pref.findIndexOfValue(CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT));
                }
                this.mGet.setPreviousRecordModeString(CameraConstants.TYPE_RECORDMODE_LIVE_EFFECT);
            }
        } else {
            pref = this.mGet.findPreference(Setting.KEY_LIVE_EFFECT);
            if (pref == null) {
                CamLog.d(FaceDetector.TAG, "pref is null");
                return;
            }
            value = pref.getEntryValues()[selectedMenu].toString();
            this.mGet.setSetting(Setting.KEY_LIVE_EFFECT, value);
            menuIndex = this.mGet.getQfIndex(Setting.KEY_LIVE_EFFECT);
            if (this.mGet.isQuickFunctionList(menuIndex)) {
                this.mGet.setQuickFunctionControllerMenuIcon(menuIndex, selectedMenu);
            }
        }
        this.mGet.doCommandUi(Command.SET_LIVE_EFFECT);
        CamLog.v(FaceDetector.TAG, "mOnFaceEffectClickListener idx:" + selectedMenu + ", onClick:" + value);
    }
}
