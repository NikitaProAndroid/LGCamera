package com.lge.camera.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.voiceshutter.library.LGKeyRec;

public class InnerListView extends ListView {
    public InnerListView(Context context) {
        super(context);
    }

    public InnerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InnerListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked() & Ola_ShotParam.AnimalMask_Random) {
            case LGKeyRec.EVENT_INVALID /*0*/:
                if (getListViewHeight() >= getHeight()) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public int getHeaderViewHeight() {
        int result = 0;
        for (int i = 0; i < getHeaderViewsCount(); i++) {
            result += getChildAt(i).getHeight();
        }
        return result;
    }

    public int getFooterViewHeight() {
        int result = 0;
        int childCount = getChildCount();
        for (int i = 0; i < getFooterViewsCount(); i++) {
            result += getChildAt(childCount + i).getHeight();
        }
        return result;
    }

    public int getListViewHeight() {
        int result = 0;
        for (int i = 0; i < getChildCount(); i++) {
            result += getChildAt(i).getHeight();
        }
        return result;
    }
}
