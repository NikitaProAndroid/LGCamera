package com.lge.olaworks.datastruct;

public class Ola_PanoramaInfo {
    public static final int Ola_PanoramaDirection_EDown = 3;
    public static final int Ola_PanoramaDirection_ELeft = 2;
    public static final int Ola_PanoramaDirection_ERight = 1;
    public static final int Ola_PanoramaDirection_EStill = 0;
    public static final int Ola_PanoramaDirection_EUp = 4;
    public static final int Ola_PanoramaDirection_None = -1;
    public static final int Ola_PanoramaParam_EColorCompensationEnable = 0;
    public static final int Ola_PanoramaStatus_ECanceled = 6;
    public static final int Ola_PanoramaStatus_ECompleteSynthesis = 4;
    public static final int Ola_PanoramaStatus_ECreated = 0;
    public static final int Ola_PanoramaStatus_EError = 7;
    public static final int Ola_PanoramaStatus_EImageSetted = 2;
    public static final int Ola_PanoramaStatus_EInSynthesis = 3;
    public static final int Ola_PanoramaStatus_EInitialized = 1;
    public static final int Ola_PanoramaStatus_EReqCancel = 5;
    public int direction;
    public int hRealDisp;
    public int status;
    public int vRealDisp;

    public Ola_PanoramaInfo() {
        this.direction = Ola_PanoramaDirection_None;
        this.hRealDisp = Ola_PanoramaStatus_ECreated;
        this.vRealDisp = Ola_PanoramaStatus_ECreated;
        this.status = Ola_PanoramaStatus_ECreated;
    }

    public void clear() {
        this.direction = Ola_PanoramaDirection_None;
        this.hRealDisp = Ola_PanoramaStatus_ECreated;
        this.vRealDisp = Ola_PanoramaStatus_ECreated;
    }
}
