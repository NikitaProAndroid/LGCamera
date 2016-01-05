package com.lge.camera.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.lge.camera.R;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class CameraFocusView extends RelativeLayout {
    public static final int STATE_CONTINUOUS_FAIL = 5;
    public static final int STATE_CONTINUOUS_SEARCHING = 3;
    public static final int STATE_CONTINUOUS_SUCCESS = 4;
    public static final int STATE_FAIL = 2;
    public static final int STATE_FRONT_AE = 9;
    public static final int STATE_NORMAL = 0;
    public static final int STATE_SUCCESS = 1;
    public static final int STATE_TOUCH_FAIL = 8;
    public static final int STATE_TOUCH_NORMAL = 6;
    public static final int STATE_TOUCH_SUCCESS = 7;

    public CameraFocusView(Context context) {
        super(context);
    }

    public CameraFocusView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraFocusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setState(int state) {
        CamLog.i(FaceDetector.TAG, "setState state = " + state);
        switch (state) {
            case STATE_NORMAL /*0*/:
            case STATE_FAIL /*2*/:
            case STATE_CONTINUOUS_SEARCHING /*3*/:
            case STATE_CONTINUOUS_FAIL /*5*/:
                setDrawable(R.drawable.focus_guide);
            case STATE_SUCCESS /*1*/:
            case STATE_CONTINUOUS_SUCCESS /*4*/:
                setDrawable(R.drawable.focus_guide_succeed);
            case STATE_TOUCH_NORMAL /*6*/:
            case STATE_TOUCH_FAIL /*8*/:
                setDrawable(R.drawable.focus_fail_taf);
            case STATE_TOUCH_SUCCESS /*7*/:
                setDrawable(R.drawable.focus_succeed_taf);
            case STATE_FRONT_AE /*9*/:
                setDrawable(R.drawable.focus_touch_ae);
            default:
                CamLog.d(FaceDetector.TAG, "focus indicator state out of range!");
        }
    }

    private void setDrawable(int resid) {
        ImageView rectangle = (ImageView) findViewById(R.id.focus_guide);
        if (rectangle != null) {
            rectangle.setBackground(getResources().getDrawable(resid));
        }
    }
}
