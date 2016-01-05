package com.lge.camera.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.lge.camera.R;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ManualFocusBar extends BarView {
    private int mManualFocusValue;

    public ManualFocusBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mManualFocusValue = 0;
    }

    public ManualFocusBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mManualFocusValue = 0;
    }

    public ManualFocusBar(Context context) {
        super(context);
        this.mManualFocusValue = 0;
    }

    public void getBarSettingValue() {
        this.mManualFocusValue = getCursorValue();
        setCursorValue(this.mManualFocusValue);
        setCursor(this.mManualFocusValue);
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
        return (RotateLayout) this.mBarAction.findViewById(R.id.manual_focus_rotate_view);
    }

    protected View getBarParentLayout() {
        return this.mBarAction.findViewById(R.id.manual_focus);
    }

    public void releaseBar() {
        if (this.mInitial) {
            CamLog.d(FaceDetector.TAG, "mValue = " + getValue());
            this.mManualFocusValue = getCursorValue();
            this.mBarAction.updateAllBars(8, this.mManualFocusValue);
        }
    }

    public void setBarSettingValue(String key, int value) {
        this.mManualFocusValue = value;
    }

    public int getCurrentManualFocusValue() {
        return this.mManualFocusValue;
    }
}
