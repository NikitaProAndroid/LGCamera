package com.lge.camera.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.lge.camera.R;
import com.lge.camera.command.Command;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class BeautyshotBar extends BarView {
    public BeautyshotBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BeautyshotBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BeautyshotBar(Context context) {
        super(context);
    }

    public void getBarSettingValue() {
        int lValue = 0;
        if (this.mBarAction.getApplicationMode() == 0) {
            lValue = this.mBarAction.getBarPreferenceSettingValue(Setting.KEY_BEAUTYSHOT);
            this.mBarAction.setSetting(Setting.KEY_BEAUTYSHOT, Integer.toString(lValue));
        }
        setCursor(lValue);
        setCursorValue(lValue);
        this.mBarAction.doCommandNoneParameter(Command.SET_BEAUTYSHOT);
    }

    public void setLayoutDimension() {
        this.MIN_CURSOR_POS = this.mBarAction.getPixelFromDimens(R.dimen.setting_adj_brightness_cursor_min_position);
        this.MAX_CURSOR_POS = this.mBarAction.getPixelFromDimens(R.dimen.setting_adj_cursor_bg_height);
        this.MAX_CURSOR_POS_PORT = this.mBarAction.getPixelFromDimens(R.dimen.setting_adj_cursor_bg_port_height);
        this.CURSOR_HEIGHT = (float) this.mBarAction.getPixelFromDimens(R.dimen.setting_adj_cursor_height);
        this.CURSOR_HEIGHT_PORT = (float) this.mBarAction.getPixelFromDimens(R.dimen.setting_adj_cursor_port_height);
        this.CURSOR_POS_HEIGHT = (int) ((((float) this.MAX_CURSOR_POS) - this.CURSOR_HEIGHT) - ((float) (this.MIN_CURSOR_POS * 2)));
        this.CURSOR_POS_HEIGHT_PORT = (int) ((((float) this.MAX_CURSOR_POS_PORT) - this.CURSOR_HEIGHT_PORT) - ((float) (this.MIN_CURSOR_POS * 2)));
        this.RELEASE_EXPAND_LEFT = this.mBarAction.getPixelFromDimens(R.dimen.adj_plus_button_releaseLeft);
        this.RELEASE_EXPAND_TOP = this.mBarAction.getPixelFromDimens(R.dimen.adj_plus_button_releaseTop);
        this.RELEASE_EXPAND_RIGHT = this.mBarAction.getPixelFromDimens(R.dimen.adj_plus_button_releaseRight);
        this.RELEASE_EXPAND_BOTTOM = this.mBarAction.getPixelFromDimens(R.dimen.adj_plus_button_releaseBottom);
    }

    protected RotateLayout getBarLayout() {
        return (RotateLayout) this.mBarAction.findViewById(R.id.beautyshot_rotate_view);
    }

    protected View getBarParentLayout() {
        return this.mBarAction.findViewById(R.id.beautyshot);
    }

    public void releaseBar() {
        if (this.mInitial) {
            int value = getValue();
            CamLog.d(FaceDetector.TAG, "mValue = " + value);
            this.mBarAction.setSetting(this.barSettingKey, Integer.toString(value));
            this.mBarAction.updateAllBars(2, getCursorValue());
        }
    }
}
