package com.lge.camera.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SettingExpandParentImage extends ImageView {
    public int column;
    public String key;
    public int row;
    public boolean selected;

    public SettingExpandParentImage(Context context) {
        super(context);
        this.selected = false;
    }

    public SettingExpandParentImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingExpandParentImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.selected = false;
    }
}
