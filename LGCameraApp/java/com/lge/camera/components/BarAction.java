package com.lge.camera.components;

import android.app.Activity;
import android.view.View;

public interface BarAction {
    void doCommand(String str);

    void doCommand(String str, Object obj, Object obj2);

    void doCommandDelayed(String str, int i);

    void doCommandNoneParameter(String str);

    void doCommandNoneParameter(String str, Object obj);

    View findViewById(int i);

    Activity getActivity();

    int getApplicationMode();

    int getBarPreferenceSettingValue(String str);

    int getCameraId();

    int getDegreeInBarAction();

    int getMaxZoom();

    int getOrientation();

    int getPixelFromDimens(int i);

    String getSettingValue(String str);

    int getZoomCursorMaxStep();

    float getZoomMaxValue();

    float getZoomRatio();

    boolean isConfigureLandscape();

    boolean isPausing();

    boolean isPreviewing();

    boolean isSettingViewRemoving();

    void postOnUiThread(Runnable runnable);

    void removePostRunnable(Runnable runnable);

    void removeScheduledCommand(String str);

    void rotateSettingBar(int i, int i2);

    void runOnUiThread(Runnable runnable);

    void setDegreeInBarAction(int i, int i2, boolean z);

    boolean setSetting(String str, String str2);

    void updateAllBars(int i, int i2);
}
