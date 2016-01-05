.class public Lcom/lge/hardware/LGCamera$LGParameters;
.super Ljava/lang/Object;
.source "LGCamera.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/lge/hardware/LGCamera;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x1
    name = "LGParameters"
.end annotation


# static fields
.field private static final KEY_BACKLIGHT_CONDITION:Ljava/lang/String; = "backlight-condition"

.field private static final KEY_BEAUTY:Ljava/lang/String; = "beautyshot"

.field private static final KEY_FLASH_MODE:Ljava/lang/String; = "flash-mode"

.field private static final KEY_FLASH_STATUS:Ljava/lang/String; = "flash-status"

.field private static final KEY_FOCUS_MODE_OBJECT_TRACKING:Ljava/lang/String; = "object-tracking"

.field private static final KEY_HDR_MODE:Ljava/lang/String; = "hdr-mode"

.field private static final KEY_LG_MULTI_WINDOW_FOCUS_AREA:Ljava/lang/String; = "multi-window-focus-area"

.field private static final KEY_LUMINANCE_CONDITION:Ljava/lang/String; = "luminance-condition"

.field private static final KEY_PANORAMA:Ljava/lang/String; = "panorama-shot"

.field private static final KEY_QC_SCENE_DETECT:Ljava/lang/String; = "scene-detect"

.field private static final KEY_SUPERZOOM:Ljava/lang/String; = "superzoom"

.field private static final KEY_ZOOM:Ljava/lang/String; = "zoom"

.field public static final SCENE_MODE_AUTO:Ljava/lang/String; = "auto"

.field public static final SCENE_MODE_NIGHT:Ljava/lang/String; = "night"


# instance fields
.field backlightCondition:Ljava/lang/String;

.field luminanceCondition:Ljava/lang/String;

.field mCurrentFlash:Ljava/lang/String;

.field mFlashStatus:Ljava/lang/String;

.field mHDRstatus:Ljava/lang/String;

.field mIsBeauty:Ljava/lang/String;

.field mIsCurrentFlash:Z

.field mIsFlashAuto:Z

.field mIsFlashOff:Z

.field mIsFlashOn:Z

.field mIsHDRAuto:Z

.field mIsHDROff:Z

.field mIsHDROn:Z

.field mIsHighBackLight:Z

.field mIsLuminanceEis:Z

.field mIsLuminanceHigh:Z

.field mIsSuperZoomEnabled:Z

.field private mParameters:Landroid/hardware/Camera$Parameters;

.field mSuperZoomStatus:I

.field mshotMode:Ljava/lang/String;

.field final synthetic this$0:Lcom/lge/hardware/LGCamera;


# direct methods
.method public constructor <init>(Lcom/lge/hardware/LGCamera;)V
    .registers 4

    .prologue
    .line 428
    iput-object p1, p0, Lcom/lge/hardware/LGCamera$LGParameters;->this$0:Lcom/lge/hardware/LGCamera;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 430
    # getter for: Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;
    invoke-static {p1}, Lcom/lge/hardware/LGCamera;->access$000(Lcom/lge/hardware/LGCamera;)Landroid/hardware/Camera;

    move-result-object v0

    if-nez v0, :cond_13

    .line 431
    const-string v0, "LGCamera"

    const-string v1, "Camera hardware is not opened!. open camera first."

    invoke-static {v0, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 438
    :cond_12
    :goto_12
    return-void

    .line 434
    :cond_13
    # getter for: Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;
    invoke-static {p1}, Lcom/lge/hardware/LGCamera;->access$000(Lcom/lge/hardware/LGCamera;)Landroid/hardware/Camera;

    move-result-object v0

    invoke-virtual {v0}, Landroid/hardware/Camera;->getParameters()Landroid/hardware/Camera$Parameters;

    move-result-object v0

    iput-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    .line 435
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    if-nez v0, :cond_12

    .line 436
    const-string v0, "LGCamera"

    const-string v1, "didn\'t get native parameters."

    invoke-static {v0, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    goto :goto_12
.end method

.method private checkBacklightStatus()V
    .registers 4

    .prologue
    .line 532
    iget-boolean v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsHighBackLight:Z

    if-eqz v0, :cond_f

    .line 533
    const-string v0, "LGCamera"

    const-string v1, "[LGSF] HDR_auto BL_high SZ_off"

    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    .line 534
    invoke-direct {p0}, Lcom/lge/hardware/LGCamera$LGParameters;->setHDROnParam()V

    .line 542
    :goto_e
    return-void

    .line 538
    :cond_f
    const-string v0, "LGCamera"

    const-string v1, "[LGSF] BL_low HDR_off"

    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    .line 539
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "hdr-mode"

    const-string v2, "0"

    invoke-virtual {v0, v1, v2}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;Ljava/lang/String;)V

    .line 540
    invoke-direct {p0}, Lcom/lge/hardware/LGCamera$LGParameters;->checkSceneStatus()V

    goto :goto_e
.end method

.method private checkFlashStatus()V
    .registers 3

    .prologue
    .line 579
    iget-boolean v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsFlashOn:Z

    if-nez v0, :cond_c

    iget-boolean v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsFlashAuto:Z

    if-eqz v0, :cond_17

    iget-boolean v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsCurrentFlash:Z

    if-eqz v0, :cond_17

    .line 580
    :cond_c
    const-string v0, "LGCamera"

    const-string v1, "[LGSF] flash_on"

    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    .line 581
    invoke-direct {p0}, Lcom/lge/hardware/LGCamera$LGParameters;->setDefaultParam()V

    .line 587
    :goto_16
    return-void

    .line 584
    :cond_17
    const-string v0, "LGCamera"

    const-string v1, "[LGSF] flash_off"

    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    .line 585
    invoke-direct {p0}, Lcom/lge/hardware/LGCamera$LGParameters;->checkLuminanceStatus()V

    goto :goto_16
.end method

.method private checkHDRStatus()V
    .registers 4

    .prologue
    .line 546
    iget-boolean v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsHDRAuto:Z

    if-eqz v0, :cond_8

    .line 547
    invoke-direct {p0}, Lcom/lge/hardware/LGCamera$LGParameters;->checkBacklightStatus()V

    .line 561
    :goto_7
    return-void

    .line 551
    :cond_8
    iget-boolean v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsHDROn:Z

    if-eqz v0, :cond_17

    .line 552
    const-string v0, "LGCamera"

    const-string v1, "[LGSF] HDR_on SZ_off"

    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    .line 553
    invoke-direct {p0}, Lcom/lge/hardware/LGCamera$LGParameters;->setHDROnParam()V

    goto :goto_7

    .line 557
    :cond_17
    const-string v0, "LGCamera"

    const-string v1, "[LGSF] HDR_off"

    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    .line 558
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "hdr-mode"

    const-string v2, "0"

    invoke-virtual {v0, v1, v2}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;Ljava/lang/String;)V

    .line 559
    invoke-direct {p0}, Lcom/lge/hardware/LGCamera$LGParameters;->checkSceneStatus()V

    goto :goto_7
.end method

.method private checkLuminanceStatus()V
    .registers 4

    .prologue
    .line 565
    iget-boolean v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsLuminanceHigh:Z

    if-nez v0, :cond_8

    iget-boolean v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsLuminanceEis:Z

    if-eqz v0, :cond_c

    .line 567
    :cond_8
    invoke-direct {p0}, Lcom/lge/hardware/LGCamera$LGParameters;->checkHDRStatus()V

    .line 575
    :goto_b
    return-void

    .line 572
    :cond_c
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "hdr-mode"

    const-string v2, "0"

    invoke-virtual {v0, v1, v2}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;Ljava/lang/String;)V

    .line 573
    invoke-direct {p0}, Lcom/lge/hardware/LGCamera$LGParameters;->checkSuperZoomStatus()V

    goto :goto_b
.end method

.method private checkSceneStatus()V
    .registers 3

    .prologue
    .line 519
    iget-boolean v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsLuminanceEis:Z

    if-eqz v0, :cond_f

    .line 521
    invoke-direct {p0}, Lcom/lge/hardware/LGCamera$LGParameters;->checkSuperZoomStatus()V

    .line 522
    const-string v0, "LGCamera"

    const-string v1, "[LGSF] EIS Scene_Night"

    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    .line 528
    :goto_e
    return-void

    .line 525
    :cond_f
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "auto"

    invoke-virtual {v0, v1}, Landroid/hardware/Camera$Parameters;->setSceneMode(Ljava/lang/String;)V

    .line 526
    const-string v0, "LGCamera"

    const-string v1, "[LGSF] Scene_Auto"

    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    goto :goto_e
.end method

.method private checkSuperZoomStatus()V
    .registers 4

    .prologue
    .line 503
    iget-boolean v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsSuperZoomEnabled:Z

    if-eqz v0, :cond_1c

    .line 504
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "superzoom"

    const-string v2, "on"

    invoke-virtual {v0, v1, v2}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;Ljava/lang/String;)V

    .line 506
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "auto"

    invoke-virtual {v0, v1}, Landroid/hardware/Camera$Parameters;->setSceneMode(Ljava/lang/String;)V

    .line 507
    const-string v0, "LGCamera"

    const-string v1, "[LGSF] lumi_low : SZ_on Scene_Auto"

    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    .line 516
    :goto_1b
    return-void

    .line 511
    :cond_1c
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "superzoom"

    const-string v2, "off"

    invoke-virtual {v0, v1, v2}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;Ljava/lang/String;)V

    .line 513
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "night"

    invoke-virtual {v0, v1}, Landroid/hardware/Camera$Parameters;->setSceneMode(Ljava/lang/String;)V

    .line 514
    const-string v0, "LGCamera"

    const-string v1, "[LGSF] lumi_low : SZ_off Scene_Night"

    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    goto :goto_1b
.end method

.method private setDefaultParam()V
    .registers 4

    .prologue
    .line 487
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "superzoom"

    const-string v2, "off"

    invoke-virtual {v0, v1, v2}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;Ljava/lang/String;)V

    .line 488
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "hdr-mode"

    const-string v2, "0"

    invoke-virtual {v0, v1, v2}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;Ljava/lang/String;)V

    .line 489
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "auto"

    invoke-virtual {v0, v1}, Landroid/hardware/Camera$Parameters;->setSceneMode(Ljava/lang/String;)V

    .line 491
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "panorama-shot"

    const-string v2, "0"

    invoke-virtual {v0, v1, v2}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;Ljava/lang/String;)V

    .line 493
    return-void
.end method

.method private setHDROnParam()V
    .registers 4

    .prologue
    .line 496
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "hdr-mode"

    const-string v2, "1"

    invoke-virtual {v0, v1, v2}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;Ljava/lang/String;)V

    .line 497
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "superzoom"

    const-string v2, "off"

    invoke-virtual {v0, v1, v2}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;Ljava/lang/String;)V

    .line 498
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "auto"

    invoke-virtual {v0, v1}, Landroid/hardware/Camera$Parameters;->setSceneMode(Ljava/lang/String;)V

    .line 499
    return-void
.end method

.method private setLGParameters()V
    .registers 4

    .prologue
    .line 727
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mshotMode:Ljava/lang/String;

    const-string v1, "mode_normal"

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-nez v0, :cond_6c

    .line 728
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mshotMode:Ljava/lang/String;

    const-string v1, "mode_burst"

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_18

    .line 729
    invoke-direct {p0}, Lcom/lge/hardware/LGCamera$LGParameters;->setDefaultParam()V

    .line 757
    :cond_17
    :goto_17
    return-void

    .line 731
    :cond_18
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsBeauty:Ljava/lang/String;

    const-string v1, "mode_beauty"

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_51

    .line 733
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mshotMode:Ljava/lang/String;

    const-string v1, "mode_beauty=0"

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_40

    .line 735
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "beautyshot"

    const-string v2, "off"

    invoke-virtual {v0, v1, v2}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;Ljava/lang/String;)V

    .line 736
    const-string v0, "LGCamera"

    const-string v1, "[LGSF]Beautyshot : level is 0 and normal mode"

    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    .line 743
    :goto_3c
    invoke-direct {p0}, Lcom/lge/hardware/LGCamera$LGParameters;->checkLuminanceStatus()V

    goto :goto_17

    .line 740
    :cond_40
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "beautyshot"

    const-string v2, "on"

    invoke-virtual {v0, v1, v2}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;Ljava/lang/String;)V

    .line 741
    const-string v0, "LGCamera"

    const-string v1, "[LGSF]Beautyshot : level is higher than 0 and  not normal mode"

    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    goto :goto_3c

    .line 746
    :cond_51
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mshotMode:Ljava/lang/String;

    const-string v1, "mode_panorama"

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_17

    .line 747
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "panorama-shot"

    const-string v2, "1"

    invoke-virtual {v0, v1, v2}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;Ljava/lang/String;)V

    .line 748
    const-string v0, "LGCamera"

    const-string v1, "[LGSF]Panorama shot mode"

    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    goto :goto_17

    .line 753
    :cond_6c
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mshotMode:Ljava/lang/String;

    const-string v1, "mode_normal"

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_17

    .line 754
    invoke-direct {p0}, Lcom/lge/hardware/LGCamera$LGParameters;->checkFlashStatus()V

    goto :goto_17
.end method


# virtual methods
.method public getMultiWindowFocusAreas()Ljava/util/List;
    .registers 7
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/List",
            "<",
            "Landroid/hardware/Camera$Area;",
            ">;"
        }
    .end annotation

    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/UnsupportedOperationException;
        }
    .end annotation

    .prologue
    .line 843
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v3, "multi-window-focus-area"

    invoke-virtual {v2, v3}, Landroid/hardware/Camera$Parameters;->get(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 845
    .local v0, "area":Ljava/lang/String;
    :try_start_8
    # getter for: Lcom/lge/hardware/LGCamera;->sSplitAreaMethod:Ljava/lang/Object;
    invoke-static {}, Lcom/lge/hardware/LGCamera;->access$100()Ljava/lang/Object;

    move-result-object v2

    iget-object v3, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const/4 v4, 0x1

    new-array v4, v4, [Ljava/lang/Object;

    const/4 v5, 0x0

    aput-object v0, v4, v5

    invoke-static {v2, v3, v4}, Lcom/lge/util/ProxyUtil;->invokeMethod(Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Ljava/util/List;
    :try_end_1a
    .catch Ljava/lang/Exception; {:try_start_8 .. :try_end_1a} :catch_1b

    .line 849
    :goto_1a
    return-object v2

    .line 846
    :catch_1b
    move-exception v1

    .line 847
    .local v1, "e":Ljava/lang/Exception;
    const-string v2, "LGCamera"

    invoke-virtual {v1}, Ljava/lang/Exception;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v2, v3}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 849
    const/4 v2, 0x0

    goto :goto_1a
.end method

.method public getParamStatus(Ljava/lang/String;Ljava/lang/String;)Z
    .registers 4
    .param p1, "Param"    # Ljava/lang/String;
    .param p2, "Status"    # Ljava/lang/String;

    .prologue
    .line 478
    if-eqz p1, :cond_c

    if-eqz p2, :cond_c

    invoke-virtual {p2, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_c

    .line 479
    const/4 v0, 0x1

    .line 482
    :goto_b
    return v0

    :cond_c
    const/4 v0, 0x0

    goto :goto_b
.end method

.method public getParameters()Landroid/hardware/Camera$Parameters;
    .registers 2

    .prologue
    .line 448
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    return-object v0
.end method

.method public setNightandHDRorAuto(Landroid/hardware/Camera$Parameters;Ljava/lang/String;Z)Landroid/hardware/Camera$Parameters;
    .registers 9
    .param p1, "Param"    # Landroid/hardware/Camera$Parameters;
    .param p2, "modeType"    # Ljava/lang/String;
    .param p3, "recording_flag"    # Z

    .prologue
    const/4 v3, 0x0

    .line 602
    iput-object p1, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    .line 604
    iput-object p2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mshotMode:Ljava/lang/String;

    .line 605
    const-string v0, "mode_beauty"

    .line 608
    .local v0, "beautyShot":Ljava/lang/String;
    if-eqz p3, :cond_1a

    .line 610
    invoke-direct {p0}, Lcom/lge/hardware/LGCamera$LGParameters;->setDefaultParam()V

    .line 612
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->this$0:Lcom/lge/hardware/LGCamera;

    # getter for: Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;
    invoke-static {v2}, Lcom/lge/hardware/LGCamera;->access$000(Lcom/lge/hardware/LGCamera;)Landroid/hardware/Camera;

    move-result-object v2

    iget-object v3, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    invoke-virtual {v2, v3}, Landroid/hardware/Camera;->setParameters(Landroid/hardware/Camera$Parameters;)V

    .line 613
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    .line 717
    :goto_19
    return-object v2

    .line 619
    :cond_1a
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v4, "zoom"

    invoke-virtual {v2, v4}, Landroid/hardware/Camera$Parameters;->get(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    .line 620
    .local v1, "temp":Ljava/lang/String;
    if-nez v1, :cond_a5

    .line 621
    iput v3, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mSuperZoomStatus:I

    .line 626
    :goto_26
    iget v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mSuperZoomStatus:I

    const/16 v4, 0x36

    if-le v2, v4, :cond_b1

    const/4 v2, 0x1

    :goto_2d
    iput-boolean v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsSuperZoomEnabled:Z

    .line 629
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v4, "luminance-condition"

    invoke-virtual {v2, v4}, Landroid/hardware/Camera$Parameters;->get(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    iput-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->luminanceCondition:Ljava/lang/String;

    .line 631
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->luminanceCondition:Ljava/lang/String;

    const-string v4, "high"

    invoke-virtual {p0, v2, v4}, Lcom/lge/hardware/LGCamera$LGParameters;->getParamStatus(Ljava/lang/String;Ljava/lang/String;)Z

    move-result v2

    iput-boolean v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsLuminanceHigh:Z

    .line 632
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->luminanceCondition:Ljava/lang/String;

    const-string v4, "eis"

    invoke-virtual {p0, v2, v4}, Lcom/lge/hardware/LGCamera$LGParameters;->getParamStatus(Ljava/lang/String;Ljava/lang/String;)Z

    move-result v2

    iput-boolean v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsLuminanceEis:Z

    .line 635
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v4, "backlight-condition"

    invoke-virtual {v2, v4}, Landroid/hardware/Camera$Parameters;->get(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    iput-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->backlightCondition:Ljava/lang/String;

    .line 636
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->backlightCondition:Ljava/lang/String;

    const-string v4, "high"

    invoke-virtual {p0, v2, v4}, Lcom/lge/hardware/LGCamera$LGParameters;->getParamStatus(Ljava/lang/String;Ljava/lang/String;)Z

    move-result v2

    iput-boolean v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsHighBackLight:Z

    .line 639
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v4, "flash-mode"

    invoke-virtual {v2, v4}, Landroid/hardware/Camera$Parameters;->get(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    iput-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mFlashStatus:Ljava/lang/String;

    .line 640
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mFlashStatus:Ljava/lang/String;

    const-string v4, "off"

    invoke-virtual {p0, v2, v4}, Lcom/lge/hardware/LGCamera$LGParameters;->getParamStatus(Ljava/lang/String;Ljava/lang/String;)Z

    move-result v2

    iput-boolean v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsFlashOff:Z

    .line 648
    iget-boolean v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsHighBackLight:Z

    if-nez v2, :cond_b4

    iget-boolean v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsLuminanceHigh:Z

    if-eqz v2, :cond_b4

    iget-boolean v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsSuperZoomEnabled:Z

    if-nez v2, :cond_b4

    iget-boolean v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsFlashOff:Z

    if-eqz v2, :cond_b4

    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mshotMode:Ljava/lang/String;

    const-string v4, "mode_normal"

    invoke-virtual {v2, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2

    if-eqz v2, :cond_b4

    .line 650
    const-string v2, "LGCamera"

    const-string v3, "[LGSF] return1"

    invoke-static {v2, v3}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 651
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->this$0:Lcom/lge/hardware/LGCamera;

    # getter for: Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;
    invoke-static {v2}, Lcom/lge/hardware/LGCamera;->access$000(Lcom/lge/hardware/LGCamera;)Landroid/hardware/Camera;

    move-result-object v2

    iget-object v3, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    invoke-virtual {v2, v3}, Landroid/hardware/Camera;->setParameters(Landroid/hardware/Camera$Parameters;)V

    .line 652
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    goto/16 :goto_19

    .line 624
    :cond_a5
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v4, "zoom"

    invoke-virtual {v2, v4}, Landroid/hardware/Camera$Parameters;->getInt(Ljava/lang/String;)I

    move-result v2

    iput v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mSuperZoomStatus:I

    goto/16 :goto_26

    :cond_b1
    move v2, v3

    .line 626
    goto/16 :goto_2d

    .line 673
    :cond_b4
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v4, "hdr-mode"

    invoke-virtual {v2, v4}, Landroid/hardware/Camera$Parameters;->get(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    iput-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mHDRstatus:Ljava/lang/String;

    .line 675
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v4, "flash-status"

    invoke-virtual {v2, v4}, Landroid/hardware/Camera$Parameters;->get(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    iput-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mCurrentFlash:Ljava/lang/String;

    .line 677
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mshotMode:Ljava/lang/String;

    invoke-virtual {v2}, Ljava/lang/String;->length()I

    move-result v2

    invoke-virtual {v0}, Ljava/lang/String;->length()I

    move-result v4

    if-le v2, v4, :cond_e0

    .line 678
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mshotMode:Ljava/lang/String;

    invoke-virtual {v0}, Ljava/lang/String;->length()I

    move-result v4

    invoke-virtual {v2, v3, v4}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v2

    iput-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsBeauty:Ljava/lang/String;

    .line 690
    :cond_e0
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mFlashStatus:Ljava/lang/String;

    const-string v3, "on"

    invoke-virtual {p0, v2, v3}, Lcom/lge/hardware/LGCamera$LGParameters;->getParamStatus(Ljava/lang/String;Ljava/lang/String;)Z

    move-result v2

    iput-boolean v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsFlashOn:Z

    .line 691
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mFlashStatus:Ljava/lang/String;

    const-string v3, "auto"

    invoke-virtual {p0, v2, v3}, Lcom/lge/hardware/LGCamera$LGParameters;->getParamStatus(Ljava/lang/String;Ljava/lang/String;)Z

    move-result v2

    iput-boolean v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsFlashAuto:Z

    .line 692
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mHDRstatus:Ljava/lang/String;

    const-string v3, "0"

    invoke-virtual {p0, v2, v3}, Lcom/lge/hardware/LGCamera$LGParameters;->getParamStatus(Ljava/lang/String;Ljava/lang/String;)Z

    move-result v2

    iput-boolean v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsHDROff:Z

    .line 693
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mHDRstatus:Ljava/lang/String;

    const-string v3, "1"

    invoke-virtual {p0, v2, v3}, Lcom/lge/hardware/LGCamera$LGParameters;->getParamStatus(Ljava/lang/String;Ljava/lang/String;)Z

    move-result v2

    iput-boolean v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsHDROn:Z

    .line 694
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mHDRstatus:Ljava/lang/String;

    const-string v3, "2"

    invoke-virtual {p0, v2, v3}, Lcom/lge/hardware/LGCamera$LGParameters;->getParamStatus(Ljava/lang/String;Ljava/lang/String;)Z

    move-result v2

    iput-boolean v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsHDRAuto:Z

    .line 696
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mCurrentFlash:Ljava/lang/String;

    const-string v3, "on"

    invoke-virtual {p0, v2, v3}, Lcom/lge/hardware/LGCamera$LGParameters;->getParamStatus(Ljava/lang/String;Ljava/lang/String;)Z

    move-result v2

    iput-boolean v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsCurrentFlash:Z

    .line 709
    iget-boolean v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsHighBackLight:Z

    if-nez v2, :cond_136

    iget-boolean v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsLuminanceHigh:Z

    if-eqz v2, :cond_136

    iget-boolean v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsSuperZoomEnabled:Z

    if-nez v2, :cond_136

    iget-boolean v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsFlashOff:Z

    if-eqz v2, :cond_136

    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mshotMode:Ljava/lang/String;

    const-string v3, "mode_normal"

    invoke-virtual {v2, v3}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2

    if-nez v2, :cond_148

    .line 711
    :cond_136
    invoke-direct {p0}, Lcom/lge/hardware/LGCamera$LGParameters;->setLGParameters()V

    .line 716
    :goto_139
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->this$0:Lcom/lge/hardware/LGCamera;

    # getter for: Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;
    invoke-static {v2}, Lcom/lge/hardware/LGCamera;->access$000(Lcom/lge/hardware/LGCamera;)Landroid/hardware/Camera;

    move-result-object v2

    iget-object v3, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    invoke-virtual {v2, v3}, Landroid/hardware/Camera;->setParameters(Landroid/hardware/Camera$Parameters;)V

    .line 717
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    goto/16 :goto_19

    .line 714
    :cond_148
    const-string v2, "LGCamera"

    const-string v3, "[LGSF] return2"

    invoke-static {v2, v3}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    goto :goto_139
.end method

.method public setObjectTracking(Ljava/lang/String;)V
    .registers 4
    .param p1, "value"    # Ljava/lang/String;

    .prologue
    .line 859
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "object-tracking"

    invoke-virtual {v0, v1, p1}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;Ljava/lang/String;)V

    .line 860
    return-void
.end method

.method public setParameters(Landroid/hardware/Camera$Parameters;)V
    .registers 4
    .param p1, "param"    # Landroid/hardware/Camera$Parameters;

    .prologue
    .line 459
    iput-object p1, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    .line 460
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->this$0:Lcom/lge/hardware/LGCamera;

    # getter for: Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;
    invoke-static {v0}, Lcom/lge/hardware/LGCamera;->access$000(Lcom/lge/hardware/LGCamera;)Landroid/hardware/Camera;

    move-result-object v0

    iget-object v1, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    invoke-virtual {v0, v1}, Landroid/hardware/Camera;->setParameters(Landroid/hardware/Camera$Parameters;)V

    .line 461
    return-void
.end method

.method public setSceneDetectMode(Ljava/lang/String;)V
    .registers 4
    .param p1, "value"    # Ljava/lang/String;

    .prologue
    .line 474
    iget-object v0, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v1, "scene-detect"

    invoke-virtual {v0, v1, p1}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;Ljava/lang/String;)V

    .line 475
    return-void
.end method

.method public setSuperZoom(Landroid/hardware/Camera$Parameters;)Landroid/hardware/Camera$Parameters;
    .registers 6
    .param p1, "Param"    # Landroid/hardware/Camera$Parameters;

    .prologue
    const/4 v1, 0x0

    .line 769
    iput-object p1, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    .line 771
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v3, "zoom"

    invoke-virtual {v2, v3}, Landroid/hardware/Camera$Parameters;->get(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 773
    .local v0, "temp":Ljava/lang/String;
    if-nez v0, :cond_33

    .line 774
    iput v1, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mSuperZoomStatus:I

    .line 780
    :goto_f
    iget v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mSuperZoomStatus:I

    const/16 v3, 0x36

    if-le v2, v3, :cond_16

    const/4 v1, 0x1

    :cond_16
    iput-boolean v1, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsSuperZoomEnabled:Z

    .line 781
    iget-boolean v1, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mIsSuperZoomEnabled:Z

    if-eqz v1, :cond_3e

    .line 782
    iget-object v1, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v2, "superzoom"

    const-string v3, "on"

    invoke-virtual {v1, v2, v3}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;Ljava/lang/String;)V

    .line 787
    :goto_25
    iget-object v1, p0, Lcom/lge/hardware/LGCamera$LGParameters;->this$0:Lcom/lge/hardware/LGCamera;

    # getter for: Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;
    invoke-static {v1}, Lcom/lge/hardware/LGCamera;->access$000(Lcom/lge/hardware/LGCamera;)Landroid/hardware/Camera;

    move-result-object v1

    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    invoke-virtual {v1, v2}, Landroid/hardware/Camera;->setParameters(Landroid/hardware/Camera$Parameters;)V

    .line 788
    iget-object v1, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    return-object v1

    .line 777
    :cond_33
    iget-object v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v3, "zoom"

    invoke-virtual {v2, v3}, Landroid/hardware/Camera$Parameters;->getInt(Ljava/lang/String;)I

    move-result v2

    iput v2, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mSuperZoomStatus:I

    goto :goto_f

    .line 785
    :cond_3e
    iget-object v1, p0, Lcom/lge/hardware/LGCamera$LGParameters;->mParameters:Landroid/hardware/Camera$Parameters;

    const-string v2, "superzoom"

    const-string v3, "off"

    invoke-virtual {v1, v2, v3}, Landroid/hardware/Camera$Parameters;->set(Ljava/lang/String;Ljava/lang/String;)V

    goto :goto_25
.end method
