.class public Lcom/lge/hardware/LGCamera;
.super Ljava/lang/Object;
.source "LGCamera.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/lge/hardware/LGCamera$ProxyData;,
        Lcom/lge/hardware/LGCamera$ProxyDataListener;,
        Lcom/lge/hardware/LGCamera$EventHandler;,
        Lcom/lge/hardware/LGCamera$LGParameters;,
        Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;,
        Lcom/lge/hardware/LGCamera$CameraDataCallback;
    }
.end annotation


# static fields
.field private static final CAMERA_META_DATA_FLASH_INDICATOR:I = 0x8

.field private static final CAMERA_META_DATA_HDR_INDICATOR:I = 0x4

.field private static final CAMERA_META_DATA_LG_MANUAL_MODE_INDICATOR:I = 0x12

.field private static final CAMERA_MSG_META_DATA:I = 0x2000

.field private static final CAMERA_MSG_OBT_DATA:I = 0x5000

.field private static final CAMERA_MSG_PROXY_DATA:I = 0x8000

.field private static final CAMERA_MSG_STATS_DATA:I = 0x1000

.field private static final TAG:Ljava/lang/String; = "LGCamera"

.field private static sSplitAreaMethod:Ljava/lang/Object;


# instance fields
.field private mCamera:Landroid/hardware/Camera;

.field private mCameraDataCallback:Lcom/lge/hardware/LGCamera$CameraDataCallback;

.field private mCameraId:I

.field private mEnabledMetaData:I

.field private mEventHandler:Lcom/lge/hardware/LGCamera$EventHandler;

.field private mFlashMetaDataCallback:Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;

.field private mHdrMetaDataCallback:Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;

.field private mLGManualModeMetaDataCallback:Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;

.field private mMetaDataCallbackLock:Ljava/lang/Object;

.field private mProxyDataListener:Lcom/lge/hardware/LGCamera$ProxyDataListener;

.field private mProxyDataRunning:Z


# direct methods
.method static constructor <clinit>()V
    .registers 5

    .prologue
    .line 40
    const-string v0, "hook_jni"

    invoke-static {v0}, Ljava/lang/System;->loadLibrary(Ljava/lang/String;)V

    .line 333
    const-class v0, Landroid/hardware/Camera$Parameters;

    const-string v1, "splitArea"

    const/4 v2, 0x1

    new-array v2, v2, [Ljava/lang/Class;

    const/4 v3, 0x0

    const-class v4, Ljava/lang/String;

    aput-object v4, v2, v3

    invoke-static {v0, v1, v2}, Lcom/lge/util/ProxyUtil;->loadMethod(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/Object;

    move-result-object v0

    sput-object v0, Lcom/lge/hardware/LGCamera;->sSplitAreaMethod:Ljava/lang/Object;

    .line 334
    return-void
.end method

.method public constructor <init>(I)V
    .registers 3
    .param p1, "cameraId"    # I

    .prologue
    .line 78
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 59
    const/4 v0, 0x0

    iput-boolean v0, p0, Lcom/lge/hardware/LGCamera;->mProxyDataRunning:Z

    .line 66
    new-instance v0, Ljava/lang/Object;

    invoke-direct {v0}, Ljava/lang/Object;-><init>()V

    iput-object v0, p0, Lcom/lge/hardware/LGCamera;->mMetaDataCallbackLock:Ljava/lang/Object;

    .line 79
    invoke-static {p1}, Landroid/hardware/Camera;->open(I)Landroid/hardware/Camera;

    move-result-object v0

    iput-object v0, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    .line 81
    iget-object v0, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    invoke-direct {p0, p1, v0}, Lcom/lge/hardware/LGCamera;->cameraInit(ILandroid/hardware/Camera;)V

    .line 82
    return-void
.end method

.method public constructor <init>(II)V
    .registers 4
    .param p1, "cameraId"    # I
    .param p2, "halVersion"    # I

    .prologue
    .line 97
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 59
    const/4 v0, 0x0

    iput-boolean v0, p0, Lcom/lge/hardware/LGCamera;->mProxyDataRunning:Z

    .line 66
    new-instance v0, Ljava/lang/Object;

    invoke-direct {v0}, Ljava/lang/Object;-><init>()V

    iput-object v0, p0, Lcom/lge/hardware/LGCamera;->mMetaDataCallbackLock:Ljava/lang/Object;

    .line 98
    invoke-static {p1, p2}, Landroid/hardware/Camera;->openLegacy(II)Landroid/hardware/Camera;

    move-result-object v0

    iput-object v0, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    .line 100
    iget-object v0, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    invoke-direct {p0, p1, v0}, Lcom/lge/hardware/LGCamera;->cameraInit(ILandroid/hardware/Camera;)V

    .line 101
    return-void
.end method

.method private final native _enableProxyDataListen(Landroid/hardware/Camera;Z)V
.end method

.method static synthetic access$000(Lcom/lge/hardware/LGCamera;)Landroid/hardware/Camera;
    .registers 2
    .param p0, "x0"    # Lcom/lge/hardware/LGCamera;

    .prologue
    .line 37
    iget-object v0, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    return-object v0
.end method

.method static synthetic access$100()Ljava/lang/Object;
    .registers 1

    .prologue
    .line 37
    sget-object v0, Lcom/lge/hardware/LGCamera;->sSplitAreaMethod:Ljava/lang/Object;

    return-object v0
.end method

.method static synthetic access$200(Lcom/lge/hardware/LGCamera;)Lcom/lge/hardware/LGCamera$CameraDataCallback;
    .registers 2
    .param p0, "x0"    # Lcom/lge/hardware/LGCamera;

    .prologue
    .line 37
    iget-object v0, p0, Lcom/lge/hardware/LGCamera;->mCameraDataCallback:Lcom/lge/hardware/LGCamera$CameraDataCallback;

    return-object v0
.end method

.method static synthetic access$300([BI)I
    .registers 3
    .param p0, "x0"    # [B
    .param p1, "x1"    # I

    .prologue
    .line 37
    invoke-static {p0, p1}, Lcom/lge/hardware/LGCamera;->byteToInt([BI)I

    move-result v0

    return v0
.end method

.method static synthetic access$400(Lcom/lge/hardware/LGCamera;)Lcom/lge/hardware/LGCamera$ProxyDataListener;
    .registers 2
    .param p0, "x0"    # Lcom/lge/hardware/LGCamera;

    .prologue
    .line 37
    iget-object v0, p0, Lcom/lge/hardware/LGCamera;->mProxyDataListener:Lcom/lge/hardware/LGCamera$ProxyDataListener;

    return-object v0
.end method

.method static synthetic access$500(Lcom/lge/hardware/LGCamera;)I
    .registers 2
    .param p0, "x0"    # Lcom/lge/hardware/LGCamera;

    .prologue
    .line 37
    iget v0, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    return v0
.end method

.method static synthetic access$600(Lcom/lge/hardware/LGCamera;)Ljava/lang/Object;
    .registers 2
    .param p0, "x0"    # Lcom/lge/hardware/LGCamera;

    .prologue
    .line 37
    iget-object v0, p0, Lcom/lge/hardware/LGCamera;->mMetaDataCallbackLock:Ljava/lang/Object;

    return-object v0
.end method

.method static synthetic access$700(Lcom/lge/hardware/LGCamera;)Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;
    .registers 2
    .param p0, "x0"    # Lcom/lge/hardware/LGCamera;

    .prologue
    .line 37
    iget-object v0, p0, Lcom/lge/hardware/LGCamera;->mHdrMetaDataCallback:Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;

    return-object v0
.end method

.method static synthetic access$800(Lcom/lge/hardware/LGCamera;)Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;
    .registers 2
    .param p0, "x0"    # Lcom/lge/hardware/LGCamera;

    .prologue
    .line 37
    iget-object v0, p0, Lcom/lge/hardware/LGCamera;->mFlashMetaDataCallback:Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;

    return-object v0
.end method

.method static synthetic access$900(Lcom/lge/hardware/LGCamera;)Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;
    .registers 2
    .param p0, "x0"    # Lcom/lge/hardware/LGCamera;

    .prologue
    .line 37
    iget-object v0, p0, Lcom/lge/hardware/LGCamera;->mLGManualModeMetaDataCallback:Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;

    return-object v0
.end method

.method private static byteToInt([BI)I
    .registers 6
    .param p0, "b"    # [B
    .param p1, "offset"    # I

    .prologue
    .line 142
    const/4 v2, 0x0

    .line 143
    .local v2, "value":I
    const/4 v0, 0x0

    .local v0, "i":I
    :goto_2
    const/4 v3, 0x4

    if-ge v0, v3, :cond_15

    .line 144
    rsub-int/lit8 v3, v0, 0x3

    mul-int/lit8 v1, v3, 0x8

    .line 145
    .local v1, "shift":I
    rsub-int/lit8 v3, v0, 0x3

    add-int/2addr v3, p1

    aget-byte v3, p0, v3

    and-int/lit16 v3, v3, 0xff

    shl-int/2addr v3, v1

    add-int/2addr v2, v3

    .line 143
    add-int/lit8 v0, v0, 0x1

    goto :goto_2

    .line 147
    .end local v1    # "shift":I
    :cond_15
    return v2
.end method

.method private cameraInit(ILandroid/hardware/Camera;)V
    .registers 6
    .param p1, "cameraId"    # I
    .param p2, "camera"    # Landroid/hardware/Camera;

    .prologue
    const/4 v2, 0x0

    .line 104
    new-instance v1, Ljava/lang/ref/WeakReference;

    invoke-direct {v1, p0}, Ljava/lang/ref/WeakReference;-><init>(Ljava/lang/Object;)V

    invoke-direct {p0, v1, p2}, Lcom/lge/hardware/LGCamera;->native_change_listener(Ljava/lang/Object;Landroid/hardware/Camera;)V

    .line 105
    iput-object v2, p0, Lcom/lge/hardware/LGCamera;->mCameraDataCallback:Lcom/lge/hardware/LGCamera$CameraDataCallback;

    .line 106
    iput-object v2, p0, Lcom/lge/hardware/LGCamera;->mProxyDataListener:Lcom/lge/hardware/LGCamera$ProxyDataListener;

    .line 107
    iput-object v2, p0, Lcom/lge/hardware/LGCamera;->mHdrMetaDataCallback:Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;

    .line 108
    iput-object v2, p0, Lcom/lge/hardware/LGCamera;->mFlashMetaDataCallback:Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;

    .line 109
    iput-object v2, p0, Lcom/lge/hardware/LGCamera;->mLGManualModeMetaDataCallback:Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;

    .line 110
    const/4 v1, 0x0

    iput v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    .line 111
    iput p1, p0, Lcom/lge/hardware/LGCamera;->mCameraId:I

    .line 114
    invoke-static {}, Landroid/os/Looper;->myLooper()Landroid/os/Looper;

    move-result-object v0

    .local v0, "looper":Landroid/os/Looper;
    if-eqz v0, :cond_26

    .line 115
    new-instance v1, Lcom/lge/hardware/LGCamera$EventHandler;

    invoke-direct {v1, p0, p0, v0}, Lcom/lge/hardware/LGCamera$EventHandler;-><init>(Lcom/lge/hardware/LGCamera;Lcom/lge/hardware/LGCamera;Landroid/os/Looper;)V

    iput-object v1, p0, Lcom/lge/hardware/LGCamera;->mEventHandler:Lcom/lge/hardware/LGCamera$EventHandler;

    .line 121
    :goto_25
    return-void

    .line 116
    :cond_26
    invoke-static {}, Landroid/os/Looper;->getMainLooper()Landroid/os/Looper;

    move-result-object v0

    if-eqz v0, :cond_34

    .line 117
    new-instance v1, Lcom/lge/hardware/LGCamera$EventHandler;

    invoke-direct {v1, p0, p0, v0}, Lcom/lge/hardware/LGCamera$EventHandler;-><init>(Lcom/lge/hardware/LGCamera;Lcom/lge/hardware/LGCamera;Landroid/os/Looper;)V

    iput-object v1, p0, Lcom/lge/hardware/LGCamera;->mEventHandler:Lcom/lge/hardware/LGCamera$EventHandler;

    goto :goto_25

    .line 119
    :cond_34
    iput-object v2, p0, Lcom/lge/hardware/LGCamera;->mEventHandler:Lcom/lge/hardware/LGCamera$EventHandler;

    goto :goto_25
.end method

.method private final native native_cancelPicture(Landroid/hardware/Camera;)V
.end method

.method private final native native_change_listener(Ljava/lang/Object;Landroid/hardware/Camera;)V
.end method

.method private final native native_sendObjectTrackingCmd(Landroid/hardware/Camera;)V
.end method

.method private final native native_setISPDataCallbackMode(Landroid/hardware/Camera;Z)V
.end method

.method private final native native_setMetadataCb(Landroid/hardware/Camera;Z)V
.end method

.method private final native native_setOBTDataCallbackMode(Landroid/hardware/Camera;Z)V
.end method

.method private static postEventFromNative(Ljava/lang/Object;IIILjava/lang/Object;)V
    .registers 8
    .param p0, "camera_ref"    # Ljava/lang/Object;
    .param p1, "what"    # I
    .param p2, "arg1"    # I
    .param p3, "arg2"    # I
    .param p4, "obj"    # Ljava/lang/Object;

    .prologue
    .line 1027
    check-cast p0, Ljava/lang/ref/WeakReference;

    .end local p0    # "camera_ref":Ljava/lang/Object;
    invoke-virtual {p0}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lcom/lge/hardware/LGCamera;

    .line 1028
    .local v0, "c":Lcom/lge/hardware/LGCamera;
    if-nez v0, :cond_b

    .line 1036
    :cond_a
    :goto_a
    return-void

    .line 1032
    :cond_b
    iget-object v2, v0, Lcom/lge/hardware/LGCamera;->mEventHandler:Lcom/lge/hardware/LGCamera$EventHandler;

    if-eqz v2, :cond_a

    .line 1033
    iget-object v2, v0, Lcom/lge/hardware/LGCamera;->mEventHandler:Lcom/lge/hardware/LGCamera$EventHandler;

    invoke-virtual {v2, p1, p2, p3, p4}, Lcom/lge/hardware/LGCamera$EventHandler;->obtainMessage(IIILjava/lang/Object;)Landroid/os/Message;

    move-result-object v1

    .line 1034
    .local v1, "m":Landroid/os/Message;
    iget-object v2, v0, Lcom/lge/hardware/LGCamera;->mEventHandler:Lcom/lge/hardware/LGCamera$EventHandler;

    invoke-virtual {v2, v1}, Lcom/lge/hardware/LGCamera$EventHandler;->sendMessage(Landroid/os/Message;)Z

    goto :goto_a
.end method


# virtual methods
.method public final cancelPicture()V
    .registers 2

    .prologue
    .line 315
    iget-object v0, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    invoke-direct {p0, v0}, Lcom/lge/hardware/LGCamera;->native_cancelPicture(Landroid/hardware/Camera;)V

    .line 316
    return-void
.end method

.method protected finalize()V
    .registers 2

    .prologue
    .line 135
    iget-object v0, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    if-eqz v0, :cond_c

    .line 136
    iget-object v0, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    invoke-virtual {v0}, Landroid/hardware/Camera;->release()V

    .line 137
    const/4 v0, 0x0

    iput-object v0, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    .line 139
    :cond_c
    return-void
.end method

.method public getCamera()Landroid/hardware/Camera;
    .registers 2

    .prologue
    .line 131
    iget-object v0, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    return-object v0
.end method

.method public getLGParameters()Lcom/lge/hardware/LGCamera$LGParameters;
    .registers 2

    .prologue
    .line 327
    new-instance v0, Lcom/lge/hardware/LGCamera$LGParameters;

    invoke-direct {v0, p0}, Lcom/lge/hardware/LGCamera$LGParameters;-><init>(Lcom/lge/hardware/LGCamera;)V

    .line 328
    .local v0, "p":Lcom/lge/hardware/LGCamera$LGParameters;
    return-object v0
.end method

.method public final runObjectTracking()V
    .registers 2

    .prologue
    .line 277
    iget-object v0, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    invoke-direct {p0, v0}, Lcom/lge/hardware/LGCamera;->native_sendObjectTrackingCmd(Landroid/hardware/Camera;)V

    .line 278
    return-void
.end method

.method public final setFlashdataCb(Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;)V
    .registers 5
    .param p1, "cb"    # Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;

    .prologue
    .line 249
    iget-object v2, p0, Lcom/lge/hardware/LGCamera;->mMetaDataCallbackLock:Ljava/lang/Object;

    monitor-enter v2

    .line 250
    :try_start_3
    iput-object p1, p0, Lcom/lge/hardware/LGCamera;->mFlashMetaDataCallback:Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;

    .line 251
    monitor-exit v2
    :try_end_6
    .catchall {:try_start_3 .. :try_end_6} :catchall_19

    .line 254
    if-nez p1, :cond_1c

    .line 255
    :try_start_8
    iget v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    and-int/lit8 v1, v1, -0x9

    iput v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    .line 256
    iget v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    if-nez v1, :cond_18

    .line 257
    iget-object v1, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    const/4 v2, 0x0

    invoke-direct {p0, v1, v2}, Lcom/lge/hardware/LGCamera;->native_setMetadataCb(Landroid/hardware/Camera;Z)V
    :try_end_18
    .catch Ljava/lang/RuntimeException; {:try_start_8 .. :try_end_18} :catch_29

    .line 267
    :cond_18
    :goto_18
    return-void

    .line 251
    :catchall_19
    move-exception v1

    :try_start_1a
    monitor-exit v2
    :try_end_1b
    .catchall {:try_start_1a .. :try_end_1b} :catchall_19

    throw v1

    .line 260
    :cond_1c
    :try_start_1c
    iget v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    or-int/lit8 v1, v1, 0x8

    iput v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    .line 261
    iget-object v1, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    const/4 v2, 0x1

    invoke-direct {p0, v1, v2}, Lcom/lge/hardware/LGCamera;->native_setMetadataCb(Landroid/hardware/Camera;Z)V
    :try_end_28
    .catch Ljava/lang/RuntimeException; {:try_start_1c .. :try_end_28} :catch_29

    goto :goto_18

    .line 263
    :catch_29
    move-exception v0

    .line 264
    .local v0, "e":Ljava/lang/RuntimeException;
    const-string v1, "LGCamera"

    const-string v2, "setFlashdataCb failed"

    invoke-static {v1, v2}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 265
    iget v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    and-int/lit8 v1, v1, -0x9

    iput v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    goto :goto_18
.end method

.method public final setISPDataCallbackMode(Lcom/lge/hardware/LGCamera$CameraDataCallback;)V
    .registers 4
    .param p1, "cb"    # Lcom/lge/hardware/LGCamera$CameraDataCallback;

    .prologue
    .line 289
    iput-object p1, p0, Lcom/lge/hardware/LGCamera;->mCameraDataCallback:Lcom/lge/hardware/LGCamera$CameraDataCallback;

    .line 290
    iget-object v1, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    if-eqz p1, :cond_b

    const/4 v0, 0x1

    :goto_7
    invoke-direct {p0, v1, v0}, Lcom/lge/hardware/LGCamera;->native_setISPDataCallbackMode(Landroid/hardware/Camera;Z)V

    .line 291
    return-void

    .line 290
    :cond_b
    const/4 v0, 0x0

    goto :goto_7
.end method

.method public final setLGManualModedataCb(Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;)V
    .registers 5
    .param p1, "cb"    # Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;

    .prologue
    .line 221
    iget-object v2, p0, Lcom/lge/hardware/LGCamera;->mMetaDataCallbackLock:Ljava/lang/Object;

    monitor-enter v2

    .line 222
    :try_start_3
    iput-object p1, p0, Lcom/lge/hardware/LGCamera;->mLGManualModeMetaDataCallback:Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;

    .line 223
    monitor-exit v2
    :try_end_6
    .catchall {:try_start_3 .. :try_end_6} :catchall_19

    .line 225
    if-nez p1, :cond_1c

    .line 226
    :try_start_8
    iget v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    and-int/lit8 v1, v1, 0x12

    iput v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    .line 227
    iget v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    if-nez v1, :cond_18

    .line 228
    iget-object v1, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    const/4 v2, 0x0

    invoke-direct {p0, v1, v2}, Lcom/lge/hardware/LGCamera;->native_setMetadataCb(Landroid/hardware/Camera;Z)V
    :try_end_18
    .catch Ljava/lang/RuntimeException; {:try_start_8 .. :try_end_18} :catch_29

    .line 238
    :cond_18
    :goto_18
    return-void

    .line 223
    :catchall_19
    move-exception v1

    :try_start_1a
    monitor-exit v2
    :try_end_1b
    .catchall {:try_start_1a .. :try_end_1b} :catchall_19

    throw v1

    .line 231
    :cond_1c
    :try_start_1c
    iget v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    or-int/lit8 v1, v1, 0x12

    iput v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    .line 232
    iget-object v1, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    const/4 v2, 0x1

    invoke-direct {p0, v1, v2}, Lcom/lge/hardware/LGCamera;->native_setMetadataCb(Landroid/hardware/Camera;Z)V
    :try_end_28
    .catch Ljava/lang/RuntimeException; {:try_start_1c .. :try_end_28} :catch_29

    goto :goto_18

    .line 234
    :catch_29
    move-exception v0

    .line 235
    .local v0, "e":Ljava/lang/RuntimeException;
    const-string v1, "LGCamera"

    const-string v2, "setLGManualModedataCb failed"

    invoke-static {v1, v2}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 236
    iget v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    and-int/lit8 v1, v1, 0x12

    iput v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    goto :goto_18
.end method

.method public final setMetadataCb(Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;)V
    .registers 5
    .param p1, "cb"    # Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;

    .prologue
    .line 192
    iget-object v2, p0, Lcom/lge/hardware/LGCamera;->mMetaDataCallbackLock:Ljava/lang/Object;

    monitor-enter v2

    .line 193
    :try_start_3
    iput-object p1, p0, Lcom/lge/hardware/LGCamera;->mHdrMetaDataCallback:Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;

    .line 194
    monitor-exit v2
    :try_end_6
    .catchall {:try_start_3 .. :try_end_6} :catchall_19

    .line 197
    if-nez p1, :cond_1c

    .line 198
    :try_start_8
    iget v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    and-int/lit8 v1, v1, -0x5

    iput v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    .line 199
    iget v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    if-nez v1, :cond_18

    .line 200
    iget-object v1, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    const/4 v2, 0x0

    invoke-direct {p0, v1, v2}, Lcom/lge/hardware/LGCamera;->native_setMetadataCb(Landroid/hardware/Camera;Z)V
    :try_end_18
    .catch Ljava/lang/RuntimeException; {:try_start_8 .. :try_end_18} :catch_29

    .line 210
    :cond_18
    :goto_18
    return-void

    .line 194
    :catchall_19
    move-exception v1

    :try_start_1a
    monitor-exit v2
    :try_end_1b
    .catchall {:try_start_1a .. :try_end_1b} :catchall_19

    throw v1

    .line 203
    :cond_1c
    :try_start_1c
    iget v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    or-int/lit8 v1, v1, 0x4

    iput v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    .line 204
    iget-object v1, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    const/4 v2, 0x1

    invoke-direct {p0, v1, v2}, Lcom/lge/hardware/LGCamera;->native_setMetadataCb(Landroid/hardware/Camera;Z)V
    :try_end_28
    .catch Ljava/lang/RuntimeException; {:try_start_1c .. :try_end_28} :catch_29

    goto :goto_18

    .line 206
    :catch_29
    move-exception v0

    .line 207
    .local v0, "e":Ljava/lang/RuntimeException;
    const-string v1, "LGCamera"

    const-string v2, "setMetadataCb failed"

    invoke-static {v1, v2}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 208
    iget v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    and-int/lit8 v1, v1, -0x5

    iput v1, p0, Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I

    goto :goto_18
.end method

.method public final setOBTDataCallbackMode(Lcom/lge/hardware/LGCamera$CameraDataCallback;)V
    .registers 4
    .param p1, "cb"    # Lcom/lge/hardware/LGCamera$CameraDataCallback;

    .prologue
    .line 303
    iput-object p1, p0, Lcom/lge/hardware/LGCamera;->mCameraDataCallback:Lcom/lge/hardware/LGCamera$CameraDataCallback;

    .line 304
    iget-object v1, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    if-eqz p1, :cond_b

    const/4 v0, 0x1

    :goto_7
    invoke-direct {p0, v1, v0}, Lcom/lge/hardware/LGCamera;->native_setOBTDataCallbackMode(Landroid/hardware/Camera;Z)V

    .line 305
    return-void

    .line 304
    :cond_b
    const/4 v0, 0x0

    goto :goto_7
.end method

.method public final setProxyDataListener(Lcom/lge/hardware/LGCamera$ProxyDataListener;)V
    .registers 5
    .param p1, "listener"    # Lcom/lge/hardware/LGCamera$ProxyDataListener;

    .prologue
    const/4 v2, 0x1

    const/4 v1, 0x0

    .line 1095
    iput-object p1, p0, Lcom/lge/hardware/LGCamera;->mProxyDataListener:Lcom/lge/hardware/LGCamera$ProxyDataListener;

    .line 1096
    if-eqz p1, :cond_12

    iget-boolean v0, p0, Lcom/lge/hardware/LGCamera;->mProxyDataRunning:Z

    if-nez v0, :cond_12

    .line 1097
    iput-boolean v2, p0, Lcom/lge/hardware/LGCamera;->mProxyDataRunning:Z

    .line 1098
    iget-object v0, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    invoke-direct {p0, v0, v2}, Lcom/lge/hardware/LGCamera;->_enableProxyDataListen(Landroid/hardware/Camera;Z)V

    .line 1104
    :cond_11
    :goto_11
    return-void

    .line 1100
    :cond_12
    if-nez p1, :cond_11

    iget-boolean v0, p0, Lcom/lge/hardware/LGCamera;->mProxyDataRunning:Z

    if-eqz v0, :cond_11

    .line 1101
    iput-boolean v1, p0, Lcom/lge/hardware/LGCamera;->mProxyDataRunning:Z

    .line 1102
    iget-object v0, p0, Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;

    invoke-direct {p0, v0, v1}, Lcom/lge/hardware/LGCamera;->_enableProxyDataListen(Landroid/hardware/Camera;Z)V

    goto :goto_11
.end method
