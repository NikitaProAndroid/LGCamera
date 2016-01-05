package com.lge.camera.components;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera.Area;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.lge.camera.R;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.camera.util.Util;
import com.lge.morpho.app.morphopanorama.PanoramaApplication;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CameraMultiWindowAFView extends RelativeLayout {
    private Handler animationHandler;
    Runnable animationSetter;
    private List<Area> areaList;
    private Context mContext;
    private boolean mInit;
    private int previewOnScreenHeight;
    private int previewOnScreenWidth;

    public CameraMultiWindowAFView(Context context) {
        super(context);
        this.previewOnScreenWidth = 0;
        this.previewOnScreenHeight = 0;
        this.mInit = false;
        this.animationHandler = new Handler();
        this.areaList = new ArrayList();
        this.animationSetter = new Runnable() {
            public void run() {
                CameraMultiWindowAFView.this.animationHandler.removeCallbacks(this);
                CameraMultiWindowAFView.this.setAnimation();
                CameraMultiWindowAFView.this.animationHandler.postDelayed(this, 400);
            }
        };
    }

    public CameraMultiWindowAFView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.previewOnScreenWidth = 0;
        this.previewOnScreenHeight = 0;
        this.mInit = false;
        this.animationHandler = new Handler();
        this.areaList = new ArrayList();
        this.animationSetter = new Runnable() {
            public void run() {
                CameraMultiWindowAFView.this.animationHandler.removeCallbacks(this);
                CameraMultiWindowAFView.this.setAnimation();
                CameraMultiWindowAFView.this.animationHandler.postDelayed(this, 400);
            }
        };
    }

    public void init(Context context, List<Area> list, int[] previewSizeOnScreen) {
        this.mContext = context;
        this.areaList = list;
        this.previewOnScreenWidth = previewSizeOnScreen[0];
        this.previewOnScreenHeight = previewSizeOnScreen[1];
        if (Common.isQuickWindowCameraMode()) {
            this.previewOnScreenWidth = CameraConstants.smartCoverSizeWidth;
            this.previewOnScreenHeight = CameraConstants.smartCoverSizeHeight;
        }
        makeMultiFocusView();
        this.mInit = true;
    }

    public boolean isInitialize() {
        return this.mInit;
    }

    private void makeMultiFocusView() {
        if (this.areaList == null || this.mContext == null) {
            CamLog.d(FaceDetector.TAG, "Cannot make multi focus window because areaList/mContext == null");
            return;
        }
        float scaleX;
        float scaleY;
        int qflMargin;
        int topMargin;
        DisplayMetrics outMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) this.mContext.getSystemService("window");
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int LCD_WIDTH = outMetrics.widthPixels;
        int LCD_HEIGHT = outMetrics.heightPixels;
        Point size = new Point();
        wm.getDefaultDisplay().getRealSize(size);
        int NAVIGATION_BAR_HEIGHT = size.y - LCD_HEIGHT;
        if (Util.isConfigureLandscape(getResources())) {
            LCD_WIDTH += NAVIGATION_BAR_HEIGHT;
        } else {
            LCD_HEIGHT += NAVIGATION_BAR_HEIGHT;
        }
        if (Util.isConfigureLandscape(getResources())) {
            scaleX = ((float) this.previewOnScreenWidth) / 2000.0f;
            scaleY = ((float) this.previewOnScreenHeight) / 2000.0f;
        } else {
            scaleX = ((float) this.previewOnScreenHeight) / 2000.0f;
            scaleY = ((float) this.previewOnScreenWidth) / 2000.0f;
        }
        int qflWidth = Common.getPixelFromDimens(this.mContext, R.dimen.quickfunction_layout_width);
        if (Common.isQuickWindowCameraMode()) {
            qflWidth = Common.getPixelFromDimens(this.mContext, R.dimen.smart_cover_exit_button_marginTop) * -1;
        }
        if (Util.isConfigureLandscape(getResources())) {
            qflMargin = LCD_WIDTH - this.previewOnScreenWidth != 0 ? qflWidth : 0;
            topMargin = LCD_HEIGHT - this.previewOnScreenHeight != 0 ? (LCD_HEIGHT - this.previewOnScreenHeight) / 2 : 0;
        } else {
            qflMargin = LCD_HEIGHT - this.previewOnScreenWidth != 0 ? qflWidth : 0;
            topMargin = LCD_WIDTH - this.previewOnScreenHeight != 0 ? (LCD_WIDTH - this.previewOnScreenHeight) / 2 : 0;
        }
        int i = 0;
        while (true) {
            if (i < this.areaList.size() && this.areaList != null) {
                if (this.areaList.get(i) != null) {
                    int left;
                    int right;
                    int top;
                    int bottom;
                    ImageView multiWindowAFGuide = new ImageView(this.mContext);
                    multiWindowAFGuide.setBackground(getResources().getDrawable(R.drawable.focus_guide_multiwindow_af));
                    if (i == this.areaList.size() - 1) {
                        multiWindowAFGuide.setVisibility(8);
                    }
                    if (Util.isConfigureLandscape(getResources())) {
                        left = ((int) (((float) (((Area) this.areaList.get(i)).rect.left + PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME)) * scaleX)) + qflMargin;
                        right = (int) ((((float) LCD_WIDTH) - (((float) (((Area) this.areaList.get(i)).rect.right + PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME)) * scaleX)) - ((float) qflMargin));
                        top = ((int) (((float) (((Area) this.areaList.get(i)).rect.top + PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME)) * scaleY)) + topMargin;
                        bottom = ((int) (((float) LCD_HEIGHT) - (((float) (((Area) this.areaList.get(i)).rect.bottom + PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME)) * scaleY))) - topMargin;
                    } else {
                        left = ((int) (((float) LCD_WIDTH) - (((float) (((Area) this.areaList.get(i)).rect.bottom + PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME)) * scaleX))) - topMargin;
                        right = ((int) (((float) (((Area) this.areaList.get(i)).rect.top + PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME)) * scaleX)) + topMargin;
                        top = ((int) (((float) (((Area) this.areaList.get(i)).rect.left + PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME)) * scaleY)) + qflMargin;
                        bottom = (int) ((((float) LCD_HEIGHT) - (((float) (((Area) this.areaList.get(i)).rect.right + PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME)) * scaleY)) - ((float) qflMargin));
                    }
                    LayoutParams lp = new LayoutParams(-1, -1);
                    lp.setMargins(left, top, right, bottom);
                    addView(multiWindowAFGuide, lp);
                    i++;
                } else {
                    return;
                }
            }
            return;
        }
    }

    public void refresh(int[] previewSizeOnScreen) {
        this.previewOnScreenWidth = previewSizeOnScreen[0];
        this.previewOnScreenHeight = previewSizeOnScreen[1];
        if (Common.isQuickWindowCameraMode()) {
            this.previewOnScreenWidth = CameraConstants.smartCoverSizeWidth;
            this.previewOnScreenHeight = CameraConstants.smartCoverSizeHeight;
        }
        removeAllViews();
        makeMultiFocusView();
    }

    public void update() {
        if (this.areaList != null) {
            int i = 0;
            while (i < this.areaList.size()) {
                View childView = getChildAt(i);
                if (!(childView == null || this.areaList.get(i) == null)) {
                    if (((Area) this.areaList.get(i)).weight == 1) {
                        childView.setBackground(getResources().getDrawable(R.drawable.focus_succeed_multiwindow_af));
                        childView.setVisibility(0);
                    } else if (i != this.areaList.size() - 1) {
                        childView.setBackground(null);
                    } else if (((Area) this.areaList.get(i)).weight == 0) {
                        childView.setVisibility(8);
                        childView.setBackground(null);
                    } else if (((Area) this.areaList.get(i)).weight == 1) {
                        childView.setVisibility(0);
                        childView.setBackground(getResources().getDrawable(R.drawable.focus_succeed_multiwindow_af));
                    } else {
                        childView.setVisibility(0);
                        childView.setBackground(getResources().getDrawable(R.drawable.focus_fail_multiwindow_af));
                    }
                }
                i++;
            }
        }
    }

    public void setList(List<Area> list) {
        this.areaList = list;
    }

    public void setCenterWindowVisibility(int visibility) {
        int childViewVisibility = 8;
        if (visibility == 8) {
            childViewVisibility = 0;
        }
        for (int i = 0; i < getChildCount() - 1; i++) {
            View childView = getChildAt(i);
            if (childView != null) {
                childView.setVisibility(childViewVisibility);
            }
        }
        View centerWindow = getChildAt(getChildCount() - 1);
        if (centerWindow != null) {
            CamLog.d(FaceDetector.TAG, "visibility = " + visibility);
            centerWindow.setBackground(getResources().getDrawable(R.drawable.focus_guide_multiwindow_af));
            centerWindow.setVisibility(visibility);
        }
    }

    public void setCenterWindowStatus(int state) {
        View centerWindow = getChildAt(getChildCount() - 1);
        if (centerWindow != null) {
            switch (state) {
                case Ola_ShotParam.ImageEffect_MotionBlur /*11*/:
                    centerWindow.setBackground(getResources().getDrawable(R.drawable.focus_guide_multiwindow_af));
                case Ola_ShotParam.ImageEffect_Solarize /*12*/:
                    centerWindow.setBackground(getResources().getDrawable(R.drawable.focus_succeed_multiwindow_af));
                case Ola_ShotParam.ImageEffect_Glow /*13*/:
                    centerWindow.setBackground(getResources().getDrawable(R.drawable.focus_fail_multiwindow_af));
                default:
            }
        }
    }

    public void setBackgroundDefault() {
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (i != getChildCount() - 1) {
                childView.setVisibility(0);
                childView.setBackground(getResources().getDrawable(R.drawable.focus_guide_multiwindow_af));
            } else {
                childView.setBackground(getResources().getDrawable(R.drawable.focus_guide_multiwindow_af));
                childView.setVisibility(8);
            }
        }
    }

    public void setAnimation() {
        AlphaAnimation mAniAlphaFadeIn = new AlphaAnimation(0.0f, RotateView.DEFAULT_TEXT_SCALE_X);
        mAniAlphaFadeIn.setDuration(400);
        mAniAlphaFadeIn.setRepeatCount(0);
        AlphaAnimation mAniAlphaFadeOut = new AlphaAnimation(RotateView.DEFAULT_TEXT_SCALE_X, 0.0f);
        mAniAlphaFadeOut.setDuration(400);
        mAniAlphaFadeOut.setStartOffset(400);
        mAniAlphaFadeOut.setRepeatCount(0);
        AnimationSet aniSet = new AnimationSet(true);
        aniSet.setFillAfter(true);
        aniSet.addAnimation(mAniAlphaFadeIn);
        aniSet.addAnimation(mAniAlphaFadeOut);
        ArrayList<Integer> animatedWindow = getWindowIndexArrayForAnimation();
        if (animatedWindow != null) {
            for (int i = 0; i < animatedWindow.size(); i++) {
                View childView = getChildAt(((Integer) animatedWindow.get(i)).intValue());
                if (childView != null) {
                    childView.startAnimation(aniSet);
                }
            }
        }
    }

    public void startAnimation() {
        if (this.animationSetter != null) {
            this.animationSetter.run();
        }
    }

    public void clearAnimation() {
        for (int i = 0; i < getChildCount() - 1; i++) {
            View childView = getChildAt(i);
            if (!(childView == null || childView.getAnimation() == null)) {
                childView.getAnimation().cancel();
                childView.clearAnimation();
            }
        }
        if (!(this.animationHandler == null || this.animationSetter == null)) {
            this.animationHandler.removeCallbacks(this.animationSetter);
        }
        super.clearAnimation();
    }

    public ArrayList<Integer> getWindowIndexArrayForAnimation() {
        ArrayList<Integer> windowIndexArray = new ArrayList();
        Random rnd = new Random();
        int numberOfWindows = getChildCount() / 3;
        int i = 0;
        while (i < numberOfWindows) {
            int windowNumber;
            if (windowIndexArray.size() > i && windowIndexArray.get(i) != null) {
                windowIndexArray.remove(i);
            }
            do {
                windowNumber = rnd.nextInt(getChildCount() - 1);
            } while (windowIndexArray.contains(Integer.valueOf(windowNumber)));
            windowIndexArray.add(i, Integer.valueOf(windowNumber));
            i++;
        }
        return windowIndexArray;
    }

    public void destroyMultiWindowAFGuide() {
        this.mContext = null;
        this.areaList = null;
        this.animationHandler = null;
        this.mInit = false;
    }
}
