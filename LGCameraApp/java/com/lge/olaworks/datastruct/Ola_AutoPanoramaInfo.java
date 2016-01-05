package com.lge.olaworks.datastruct;

import com.lge.camera.properties.CameraConstants;

public class Ola_AutoPanoramaInfo {
    public static final int OLA_AUTOPANORAMADIRECTION_EDOWN = 3;
    public static final int OLA_AUTOPANORAMADIRECTION_ELEFT = 2;
    public static final int OLA_AUTOPANORAMADIRECTION_ERIGHT = 1;
    public static final int OLA_AUTOPANORAMADIRECTION_ESTILL = 0;
    public static final int OLA_AUTOPANORAMADIRECTION_EUP = 4;
    public static final int OLA_AUTOPANORAMAPARAM_EDIRECTION_HORIZONTAL_ONLY = 0;
    public static final int OLA_AUTOPANORAMAPARAM_ETHUMBNAIL_ENABLE = 1;
    public static final int OLA_AUTOPANORAMASTATUS_ECANCELED = 5;
    public static final int OLA_AUTOPANORAMASTATUS_ECOMPLETESYNTHESIS = 4;
    public static final int OLA_AUTOPANORAMASTATUS_ECREATED = 0;
    public static final int OLA_AUTOPANORAMASTATUS_EERROR = 6;
    public static final int OLA_AUTOPANORAMASTATUS_EINITIALIZED = 1;
    public static final int OLA_AUTOPANORAMASTATUS_EINPROCESSING = 2;
    public static final int OLA_AUTOPANORAMASTATUS_EINSYNTHESIS = 3;
    public static final int OLA_AUTOPANORAMASTATUS_NONE = -1;
    public int direction;
    public int displacement;
    public int hRealDisp;
    public int progressMax;
    public int progressNow;
    public int status;
    public int vRealDisp;

    public Ola_AutoPanoramaInfo() {
        this.progressMax = CameraConstants.PIP_SUBWINDOW_RESIZE_HANDLER_DURATION;
        clear();
    }

    public void clear() {
        this.status = OLA_AUTOPANORAMASTATUS_NONE;
        this.direction = OLA_AUTOPANORAMASTATUS_ECREATED;
        this.progressMax = CameraConstants.PIP_SUBWINDOW_RESIZE_HANDLER_DURATION;
        this.progressNow = OLA_AUTOPANORAMASTATUS_NONE;
        this.displacement = OLA_AUTOPANORAMASTATUS_ECREATED;
        this.hRealDisp = OLA_AUTOPANORAMASTATUS_ECREATED;
        this.vRealDisp = OLA_AUTOPANORAMASTATUS_ECREATED;
    }
}
