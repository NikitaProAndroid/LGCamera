package com.lge.camera.listeners;

import android.app.Activity;
import android.content.res.Resources;
import android.hardware.Camera;
import android.view.View;
import android.widget.ImageView;
import com.lge.camera.components.RotateLayout;
import com.lge.morpho.core.MorphoPanoramaGP;
import com.lge.morpho.core.MorphoPanoramaGP.InitParam;
import com.lge.morpho.utils.multimedia.StillImageData;

public interface PlanePanoramaCallbackFunction {
    void addStillImage(StillImageData stillImageData);

    View findViewById(int i);

    Activity getActivity();

    ImageView getBar();

    View getBarLayout();

    Camera getCameraDevice();

    int[] getDirection();

    InitParam getInitParam();

    MorphoPanoramaGP getMorphoPanoramaGP();

    int getNumOfShoot();

    int getOrientationDegree();

    byte[] getPreviewBuff();

    ImageView getPreviewMini();

    RotateLayout getPreviewMiniLayout();

    Resources getResources();

    int getRoratePreview();

    int getRotateUI();

    String getSaveInputDirPath();

    String getShootingDate();

    int getState();

    Object getSyncObj();

    void increseCntReqShoot();

    boolean isProcessingFinishTask();

    boolean isShooting();

    void perfLockAcquire();

    void playPanoramaShutterSound();

    void playRecordingSound(boolean z);

    void setRequestTakePicture(boolean z);

    void setShutterButtonImage(boolean z, int i);

    void setStatus(int i);

    void setVisibleArrowGuide(boolean z, boolean z2, boolean z3);

    void setVisiblePreviewBar(boolean z, boolean z2);

    void setVisiblePreviewMini(boolean z, boolean z2);

    void setVisibleTakingGuide(boolean z, boolean z2);

    void stopPanorama();

    void toastLong(String str);
}
