package com.lge.camera.controller;

import android.app.Activity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import com.lge.camera.ControllerFunction;
import com.lge.camera.components.BarAction;
import com.lge.camera.components.BarView;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.components.RotateView;

public class BarController extends Controller implements BarAction {
    private static final long SETTING_ANIMATION_TIME = 300;
    private BarView mNormalBarView;
    private BarView mSettingBarView;

    public BarController(ControllerFunction function) {
        super(function);
    }

    public void setNormalBarView(BarView normalBarView) {
        this.mNormalBarView = normalBarView;
    }

    public void setSettingBarView(BarView settingBarView) {
        this.mSettingBarView = settingBarView;
    }

    public void refreshController() {
        if (this.mInit) {
            this.mNormalBarView.initBar(this);
            if (this.mSettingBarView != null) {
                this.mSettingBarView.initBar(this);
            }
        }
    }

    public void showControl(boolean visible) {
        if (this.mInit) {
            this.mNormalBarView.showControl(visible);
        }
    }

    public boolean isBarVisible() {
        if (this.mInit) {
            return this.mNormalBarView.isBarVisible();
        }
        return false;
    }

    public void showSettingBarControl(int ResId, boolean useAnim) {
        if (!this.mInit) {
            return;
        }
        if (useAnim) {
            RotateLayout rlC = (RotateLayout) findViewById(ResId);
            if (rlC != null) {
                rlC.clearAnimation();
                Animation anim = new AlphaAnimation(0.0f, RotateView.DEFAULT_TEXT_SCALE_X);
                anim.setAnimationListener(new AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                        if (BarController.this.mSettingBarView != null) {
                            BarController.this.mSettingBarView.showControl(true);
                        }
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                    }
                });
                anim.setDuration(SETTING_ANIMATION_TIME);
                rlC.startAnimation(anim);
            }
        } else if (this.mSettingBarView != null) {
            this.mSettingBarView.showControl(true);
        }
    }

    public void hideSettingBarControl(int ResId, boolean useAnim) {
        if (!this.mInit) {
            return;
        }
        if (useAnim) {
            RotateLayout rlC = (RotateLayout) findViewById(ResId);
            if (rlC != null) {
                Animation anim = new AlphaAnimation(RotateView.DEFAULT_TEXT_SCALE_X, 0.0f);
                anim.setAnimationListener(new AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                        if (BarController.this.mSettingBarView != null) {
                            BarController.this.mSettingBarView.showControl(false);
                        }
                    }
                });
                anim.setDuration(SETTING_ANIMATION_TIME);
                rlC.startAnimation(anim);
            }
        } else if (this.mSettingBarView != null) {
            this.mSettingBarView.showControl(false);
        }
    }

    public void startRotation(int degree, boolean animation) {
        if (this.mInit) {
            this.mNormalBarView.startRotation(degree, animation);
        }
    }

    public void resetDisplayTimeout() {
        if (this.mInit) {
            this.mNormalBarView.resetDisplayTimeout();
            if (this.mSettingBarView != null) {
                this.mSettingBarView.resetDisplayTimeout();
            }
        }
    }

    public void updateAllBars(int value) {
        if (this.mNormalBarView != null) {
            this.mNormalBarView.setBarValue(value);
        }
        if (this.mSettingBarView != null) {
            this.mSettingBarView.setBarValue(value);
        }
    }

    public void refreshSettingBars() {
        if (this.mSettingBarView != null) {
            this.mSettingBarView.refreshBar();
        }
    }

    public void onPause() {
        if (this.mInit) {
            if (this.mNormalBarView != null) {
                this.mNormalBarView.stopTimerTask();
            }
            if (this.mSettingBarView != null) {
                this.mSettingBarView.stopTimerTask();
            }
            super.onPause();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mNormalBarView != null) {
            this.mNormalBarView.unbind();
            this.mNormalBarView = null;
        }
        if (this.mSettingBarView != null) {
            this.mSettingBarView.unbind();
            this.mSettingBarView = null;
        }
    }

    public int getDegreeInBarAction() {
        return this.mGet.getOrientationDegree();
    }

    public void setDegreeInBarAction(int resId, int degree, boolean animation) {
        this.mGet.setDegree(resId, degree, animation);
    }

    public int getOrientation() {
        return this.mGet.getOrientation();
    }

    public boolean isSettingViewRemoving() {
        return this.mGet.isSettingViewRemoving();
    }

    public void runOnUiThread(Runnable r) {
        this.mGet.runOnUiThread(r);
    }

    public void postOnUiThread(Runnable r) {
        this.mGet.postOnUiThread(r);
    }

    public void removePostRunnable(Runnable r) {
        this.mGet.removePostRunnable(r);
    }

    public void removeScheduledCommand(String command) {
        this.mGet.removeScheduledCommand(command);
    }

    public void doCommand(String command) {
        this.mGet.doCommand(command);
    }

    public void doCommand(String msg, Object arg1, Object arg2) {
        this.mGet.doCommand(msg, arg1, arg2);
    }

    public void doCommandNoneParameter(String msg) {
        this.mGet.doCommandNoneParameter(msg);
    }

    public void doCommandNoneParameter(String msg, Object arg1) {
        this.mGet.doCommandNoneParameter(msg, arg1);
    }

    public void doCommandDelayed(String command, int delay) {
        this.mGet.doCommandDelayed(command, (long) delay);
    }

    public void updateAllBars(int mBarType, int value) {
        this.mGet.updateAllBars(mBarType, value);
    }

    public void rotateSettingBar(int mBarType, int value) {
        this.mGet.rotateSettingBar(mBarType, value);
    }

    public boolean isPausing() {
        return this.mGet.isPausing();
    }

    public boolean isPreviewing() {
        return this.mGet.isPreviewing();
    }

    public int getApplicationMode() {
        return this.mGet.getApplicationMode();
    }

    public int getCameraId() {
        return this.mGet.getCameraId();
    }

    public int getPixelFromDimens(int resId) {
        return super.getPixelFromDimens(resId);
    }

    public View findViewById(int id) {
        return this.mGet.findViewById(id);
    }

    public String getSettingValue(String key) {
        return this.mGet.getSettingValue(key);
    }

    public int getBarPreferenceSettingValue(String key) {
        if (this.mGet.findPreference(key) != null) {
            return Integer.parseInt(getSettingValue(key));
        }
        return 0;
    }

    public boolean setSetting(String key, String value) {
        return this.mGet.setSetting(key, value);
    }

    public float getZoomMaxValue() {
        return 0.0f;
    }

    public int getZoomCursorMaxStep() {
        return 0;
    }

    public int getMaxZoom() {
        return 0;
    }

    public float getZoomRatio() {
        return 0.0f;
    }

    public Activity getActivity() {
        return this.mGet.getActivity();
    }

    public boolean isConfigureLandscape() {
        return this.mGet.isConfigureLandscape();
    }
}
