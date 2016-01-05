package com.lge.camera.components;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import com.lge.camera.R;

public class SettingBrightnessBarExpand extends BrightnessBar {
    public SettingBrightnessBarExpand(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SettingBrightnessBarExpand(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SettingBrightnessBarExpand(Context context) {
        super(context);
    }

    public void setLayoutDimension() {
        this.MIN_CURSOR_POS = this.mBarAction.getPixelFromDimens(R.dimen.setting_adj_brightness_cursor_min_position);
        this.CURSOR_WIDTH = (float) this.mBarAction.getPixelFromDimens(R.dimen.setting_adj_cursor_expand_width);
        this.CURSOR_POS_WIDTH = this.mBarAction.getPixelFromDimens(R.dimen.setting_adj_cursor_bg_expand_width);
        this.MAX_CURSOR_POS = this.MIN_CURSOR_POS + this.CURSOR_POS_WIDTH;
        this.RELEASE_EXPAND_LEFT = this.mBarAction.getPixelFromDimens(R.dimen.adj_plus_button_releaseLeft);
        this.RELEASE_EXPAND_TOP = this.mBarAction.getPixelFromDimens(R.dimen.adj_plus_button_releaseTop);
        this.RELEASE_EXPAND_RIGHT = this.mBarAction.getPixelFromDimens(R.dimen.adj_plus_button_releaseRight);
        this.RELEASE_EXPAND_BOTTOM = this.mBarAction.getPixelFromDimens(R.dimen.adj_plus_button_releaseBottom);
    }

    public void startRotation(int degree, boolean animation) {
        if (this.mBarAction != null) {
            this.mBarAction.rotateSettingBar(5, degree);
        }
    }

    protected void disallowTouchInParentView(View view) {
        for (ViewParent parent = view.getParent(); parent != null; parent = parent.getParent()) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
    }

    public void updateBar(int step, boolean others, boolean isLongTouch, boolean actionEnd) {
        int lValue = this.mValue;
        if (!this.mInitial) {
            return;
        }
        if ((!actionEnd && step == 0) || this.mBarAction == null || !this.mBarAction.isPreviewing()) {
            return;
        }
        if (actionEnd) {
            setBarSettingValue(this.barSettingKey, getCursorValue());
            if (isLongTouch) {
                this.mBarAction.doCommand(this.barSettingCommand);
                return;
            }
            return;
        }
        int updatedValue = lValue + step;
        if (updatedValue > this.mCursorMaxStep) {
            updatedValue = this.mCursorMaxStep;
        }
        if (updatedValue < 0) {
            updatedValue = 0;
        }
        if (updatedValue != lValue) {
            setCursorValue(updatedValue);
            setCursor(getCursorValue());
            Bundle bundle = new Bundle();
            bundle.putInt("mValue", lValue);
            if (isLongTouch) {
                this.mBarAction.doCommand(this.barSettingCommand, null, bundle);
            } else {
                this.mBarAction.doCommand(this.barSettingCommand);
            }
            resetDisplayTimeout();
            updateAllBars();
        }
    }
}
