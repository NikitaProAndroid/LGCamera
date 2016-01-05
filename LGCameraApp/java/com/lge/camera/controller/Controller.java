package com.lge.camera.controller;

import android.view.MotionEvent;
import android.view.View;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.Setting;
import com.lge.camera.setting.SettingMenu;
import com.lge.camera.util.Common;

public abstract class Controller {
    protected ControllerFunction mGet;
    protected boolean mInit;

    public Controller(ControllerFunction function) {
        this.mInit = false;
        this.mGet = null;
        this.mGet = function;
    }

    protected boolean checkMediator() {
        return this.mGet != null;
    }

    public boolean isInitialized() {
        return this.mInit;
    }

    public void reInitialize() {
        this.mInit = false;
    }

    protected int getPixelFromDimens(int id) {
        return Common.getPixelFromDimens(this.mGet.getApplicationContext(), id);
    }

    protected void clearAnimation(int id) {
        View view = this.mGet.findViewById(id);
        if (view != null && view.getAnimation() != null) {
            view.getAnimation().cancel();
            view.clearAnimation();
        }
    }

    protected boolean checkPreference(int menuPosition) {
        SettingMenu currentSetting = this.mGet.getCurrentSettingMenu();
        if (currentSetting == null || currentSetting.getSetting() == null || currentSetting.getSetting().getPreferenceGroup() == null || menuPosition >= this.mGet.getSettingMenuCount()) {
            return false;
        }
        return true;
    }

    protected boolean isInView(View view, MotionEvent me) {
        if (me.getX() <= ((float) view.getLeft()) || me.getX() >= ((float) view.getRight()) || me.getY() <= ((float) view.getTop()) || me.getY() >= ((float) view.getBottom())) {
            return false;
        }
        return true;
    }

    protected String getMenuIconStringResource(String key) {
        if (key == null) {
            return null;
        }
        ListPreference pref = this.mGet.getListPreference(this.mGet.findPreferenceIndex(key));
        if (pref == null) {
            return null;
        }
        String mMenuIconString;
        if (Setting.KEY_VOICESHUTTER.equals(pref.getKey()) && FunctionProperties.useCheeseShutterTitle()) {
            mMenuIconString = this.mGet.getString(R.string.sp_cheeseshutter_NORMAL);
        } else if (Setting.KEY_TIME_MACHINE.equals(pref.getKey()) && FunctionProperties.useTimeCatchShotTitle()) {
            mMenuIconString = this.mGet.getString(R.string.sp_shot_mode_time_catch_NORMAL);
        } else {
            mMenuIconString = pref.getTitle();
        }
        return mMenuIconString;
    }

    public void initController() {
    }

    public void onCreate() {
    }

    public void onStart() {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onDestroy() {
    }

    public void onStop() {
    }
}
