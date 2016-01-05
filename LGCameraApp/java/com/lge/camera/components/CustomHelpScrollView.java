package com.lge.camera.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class CustomHelpScrollView extends ScrollView {
    public CustomHelpScrollView(Context context) {
        super(context);
    }

    public CustomHelpScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onLayout(boolean change, int left, int top, int right, int bottom) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() != 8) {
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            }
        }
    }
}
