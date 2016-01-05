package com.lge.camera.postview;

import android.app.Activity;
import android.view.View;

public interface PostViewBarListener {
    View findViewById(int i);

    Activity getActivity();

    int getOrientation();

    int getPx(int i);

    boolean isPausing();

    void onCursorMoving(boolean z);

    void onCursorUpdated(int i);

    void postOnUiThread(Runnable runnable);

    void removePostRunnable(Object obj);

    void runOnUiThread(Runnable runnable);
}
