.class Lcom/lge/hardware/LGCamera$EventHandler;
.super Landroid/os/Handler;
.source "LGCamera.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/lge/hardware/LGCamera;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "EventHandler"
.end annotation


# instance fields
.field private mLGCamera:Lcom/lge/hardware/LGCamera;

.field final synthetic this$0:Lcom/lge/hardware/LGCamera;


# direct methods
.method public constructor <init>(Lcom/lge/hardware/LGCamera;Lcom/lge/hardware/LGCamera;Landroid/os/Looper;)V
    .registers 4
    .param p2, "c"    # Lcom/lge/hardware/LGCamera;
    .param p3, "looper"    # Landroid/os/Looper;

    .prologue
    .line 867
    iput-object p1, p0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    .line 868
    invoke-direct {p0, p3}, Landroid/os/Handler;-><init>(Landroid/os/Looper;)V

    .line 869
    iput-object p2, p0, Lcom/lge/hardware/LGCamera$EventHandler;->mLGCamera:Lcom/lge/hardware/LGCamera;

    .line 870
    return-void
.end method


# virtual methods
.method public handleMessage(Landroid/os/Message;)V
    .registers 33
    .param p1, "msg"    # Landroid/os/Message;

    .prologue
    .line 874
    move-object/from16 v0, p1

    iget v0, v0, Landroid/os/Message;->what:I

    move/from16 v26, v0

    sparse-switch v26, :sswitch_data_524

    .line 1019
    const-string v26, "LGCamera"

    new-instance v27, Ljava/lang/StringBuilder;

    invoke-direct/range {v27 .. v27}, Ljava/lang/StringBuilder;-><init>()V

    const-string v28, "Unknown message type "

    invoke-virtual/range {v27 .. v28}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v27

    move-object/from16 v0, p1

    iget v0, v0, Landroid/os/Message;->what:I

    move/from16 v28, v0

    invoke-virtual/range {v27 .. v28}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v27

    invoke-virtual/range {v27 .. v27}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v27

    invoke-static/range {v26 .. v27}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 1020
    :cond_27
    :goto_27
    return-void

    .line 877
    :sswitch_28
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v26, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mCameraDataCallback:Lcom/lge/hardware/LGCamera$CameraDataCallback;
    invoke-static/range {v26 .. v26}, Lcom/lge/hardware/LGCamera;->access$200(Lcom/lge/hardware/LGCamera;)Lcom/lge/hardware/LGCamera$CameraDataCallback;

    move-result-object v26

    if-eqz v26, :cond_27

    .line 880
    const/16 v26, 0x5

    move/from16 v0, v26

    new-array v0, v0, [S

    move-object/from16 v21, v0

    .line 881
    .local v21, "obt_data":[S
    move-object/from16 v0, p1

    iget-object v0, v0, Landroid/os/Message;->obj:Ljava/lang/Object;

    move-object/from16 v26, v0

    check-cast v26, [B

    move-object/from16 v6, v26

    check-cast v6, [B

    .line 889
    .local v6, "byteData":[B
    const/16 v26, 0x0

    const/16 v27, 0x1

    aget-byte v27, v6, v27

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x8

    const/16 v28, 0x0

    aget-byte v28, v6, v28

    move/from16 v0, v28

    and-int/lit16 v0, v0, 0xff

    move/from16 v28, v0

    or-int v27, v27, v28

    move/from16 v0, v27

    int-to-short v0, v0

    move/from16 v27, v0

    aput-short v27, v21, v26

    .line 890
    const/16 v26, 0x1

    const/16 v27, 0x3

    aget-byte v27, v6, v27

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x8

    const/16 v28, 0x2

    aget-byte v28, v6, v28

    move/from16 v0, v28

    and-int/lit16 v0, v0, 0xff

    move/from16 v28, v0

    or-int v27, v27, v28

    move/from16 v0, v27

    int-to-short v0, v0

    move/from16 v27, v0

    aput-short v27, v21, v26

    .line 891
    const/16 v26, 0x2

    const/16 v27, 0x5

    aget-byte v27, v6, v27

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x8

    const/16 v28, 0x4

    aget-byte v28, v6, v28

    move/from16 v0, v28

    and-int/lit16 v0, v0, 0xff

    move/from16 v28, v0

    or-int v27, v27, v28

    move/from16 v0, v27

    int-to-short v0, v0

    move/from16 v27, v0

    aput-short v27, v21, v26

    .line 892
    const/16 v26, 0x3

    const/16 v27, 0x7

    aget-byte v27, v6, v27

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x8

    const/16 v28, 0x6

    aget-byte v28, v6, v28

    move/from16 v0, v28

    and-int/lit16 v0, v0, 0xff

    move/from16 v28, v0

    or-int v27, v27, v28

    move/from16 v0, v27

    int-to-short v0, v0

    move/from16 v27, v0

    aput-short v27, v21, v26

    .line 893
    const/16 v26, 0x4

    const/16 v27, 0x9

    aget-byte v27, v6, v27

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x8

    const/16 v28, 0x8

    aget-byte v28, v6, v28

    move/from16 v0, v28

    and-int/lit16 v0, v0, 0xff

    move/from16 v28, v0

    or-int v27, v27, v28

    move/from16 v0, v27

    int-to-short v0, v0

    move/from16 v27, v0

    aput-short v27, v21, v26

    .line 895
    const/16 v26, 0x5

    move/from16 v0, v26

    new-array v0, v0, [I

    move-object/from16 v22, v0

    .line 896
    .local v22, "obt_data_i":[I
    const/4 v15, 0x0

    .local v15, "i":I
    :goto_f6
    const/16 v26, 0x5

    move/from16 v0, v26

    if-ge v15, v0, :cond_103

    .line 897
    aget-short v26, v21, v15

    aput v26, v22, v15

    .line 896
    add-int/lit8 v15, v15, 0x1

    goto :goto_f6

    .line 901
    :cond_103
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v26, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mCameraDataCallback:Lcom/lge/hardware/LGCamera$CameraDataCallback;
    invoke-static/range {v26 .. v26}, Lcom/lge/hardware/LGCamera;->access$200(Lcom/lge/hardware/LGCamera;)Lcom/lge/hardware/LGCamera$CameraDataCallback;

    move-result-object v26

    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v27, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;
    invoke-static/range {v27 .. v27}, Lcom/lge/hardware/LGCamera;->access$000(Lcom/lge/hardware/LGCamera;)Landroid/hardware/Camera;

    move-result-object v27

    move-object/from16 v0, v26

    move-object/from16 v1, v22

    move-object/from16 v2, v27

    invoke-interface {v0, v1, v2}, Lcom/lge/hardware/LGCamera$CameraDataCallback;->onCameraData([ILandroid/hardware/Camera;)V

    goto/16 :goto_27

    .line 907
    .end local v6    # "byteData":[B
    .end local v15    # "i":I
    .end local v21    # "obt_data":[S
    .end local v22    # "obt_data_i":[I
    :sswitch_122
    const/16 v26, 0x101

    move/from16 v0, v26

    new-array v0, v0, [I

    move-object/from16 v25, v0

    .line 908
    .local v25, "statsdata":[I
    const/4 v15, 0x0

    .restart local v15    # "i":I
    :goto_12b
    const/16 v26, 0x101

    move/from16 v0, v26

    if-ge v15, v0, :cond_146

    .line 909
    move-object/from16 v0, p1

    iget-object v0, v0, Landroid/os/Message;->obj:Ljava/lang/Object;

    move-object/from16 v26, v0

    check-cast v26, [B

    check-cast v26, [B

    mul-int/lit8 v27, v15, 0x4

    # invokes: Lcom/lge/hardware/LGCamera;->byteToInt([BI)I
    invoke-static/range {v26 .. v27}, Lcom/lge/hardware/LGCamera;->access$300([BI)I

    move-result v26

    aput v26, v25, v15

    .line 908
    add-int/lit8 v15, v15, 0x1

    goto :goto_12b

    .line 911
    :cond_146
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v26, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mCameraDataCallback:Lcom/lge/hardware/LGCamera$CameraDataCallback;
    invoke-static/range {v26 .. v26}, Lcom/lge/hardware/LGCamera;->access$200(Lcom/lge/hardware/LGCamera;)Lcom/lge/hardware/LGCamera$CameraDataCallback;

    move-result-object v26

    if-eqz v26, :cond_27

    .line 912
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v26, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mCameraDataCallback:Lcom/lge/hardware/LGCamera$CameraDataCallback;
    invoke-static/range {v26 .. v26}, Lcom/lge/hardware/LGCamera;->access$200(Lcom/lge/hardware/LGCamera;)Lcom/lge/hardware/LGCamera$CameraDataCallback;

    move-result-object v26

    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v27, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;
    invoke-static/range {v27 .. v27}, Lcom/lge/hardware/LGCamera;->access$000(Lcom/lge/hardware/LGCamera;)Landroid/hardware/Camera;

    move-result-object v27

    move-object/from16 v0, v26

    move-object/from16 v1, v25

    move-object/from16 v2, v27

    invoke-interface {v0, v1, v2}, Lcom/lge/hardware/LGCamera$CameraDataCallback;->onCameraData([ILandroid/hardware/Camera;)V

    goto/16 :goto_27

    .line 918
    .end local v15    # "i":I
    .end local v25    # "statsdata":[I
    :sswitch_171
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v26, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mProxyDataListener:Lcom/lge/hardware/LGCamera$ProxyDataListener;
    invoke-static/range {v26 .. v26}, Lcom/lge/hardware/LGCamera;->access$400(Lcom/lge/hardware/LGCamera;)Lcom/lge/hardware/LGCamera$ProxyDataListener;

    move-result-object v26

    if-eqz v26, :cond_27

    .line 919
    new-instance v12, Lcom/lge/hardware/LGCamera$ProxyData;

    invoke-direct {v12}, Lcom/lge/hardware/LGCamera$ProxyData;-><init>()V

    .line 920
    .local v12, "data":Lcom/lge/hardware/LGCamera$ProxyData;
    move-object/from16 v0, p1

    iget-object v0, v0, Landroid/os/Message;->obj:Ljava/lang/Object;

    move-object/from16 v26, v0

    check-cast v26, [B

    move-object/from16 v6, v26

    check-cast v6, [B

    .line 921
    .restart local v6    # "byteData":[B
    const/16 v23, 0x0

    .line 922
    .local v23, "ptr":I
    if-eqz v6, :cond_2c7

    .line 924
    add-int/lit8 v24, v23, 0x1

    .end local v23    # "ptr":I
    .local v24, "ptr":I
    aget-byte v26, v6, v23

    move/from16 v0, v26

    and-int/lit16 v0, v0, 0xff

    move/from16 v26, v0

    add-int/lit8 v23, v24, 0x1

    .end local v24    # "ptr":I
    .restart local v23    # "ptr":I
    aget-byte v27, v6, v24

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x8

    or-int v26, v26, v27

    add-int/lit8 v24, v23, 0x1

    .end local v23    # "ptr":I
    .restart local v24    # "ptr":I
    aget-byte v27, v6, v23

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x10

    or-int v26, v26, v27

    add-int/lit8 v23, v24, 0x1

    .end local v24    # "ptr":I
    .restart local v23    # "ptr":I
    aget-byte v27, v6, v24

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x18

    or-int v26, v26, v27

    move/from16 v0, v26

    iput v0, v12, Lcom/lge/hardware/LGCamera$ProxyData;->val:I

    .line 926
    add-int/lit8 v24, v23, 0x1

    .end local v23    # "ptr":I
    .restart local v24    # "ptr":I
    aget-byte v26, v6, v23

    move/from16 v0, v26

    and-int/lit16 v0, v0, 0xff

    move/from16 v26, v0

    add-int/lit8 v23, v24, 0x1

    .end local v24    # "ptr":I
    .restart local v23    # "ptr":I
    aget-byte v27, v6, v24

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x8

    or-int v26, v26, v27

    add-int/lit8 v24, v23, 0x1

    .end local v23    # "ptr":I
    .restart local v24    # "ptr":I
    aget-byte v27, v6, v23

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x10

    or-int v26, v26, v27

    add-int/lit8 v23, v24, 0x1

    .end local v24    # "ptr":I
    .restart local v23    # "ptr":I
    aget-byte v27, v6, v24

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x18

    or-int v26, v26, v27

    move/from16 v0, v26

    iput v0, v12, Lcom/lge/hardware/LGCamera$ProxyData;->conv:I

    .line 928
    add-int/lit8 v24, v23, 0x1

    .end local v23    # "ptr":I
    .restart local v24    # "ptr":I
    aget-byte v26, v6, v23

    move/from16 v0, v26

    and-int/lit16 v0, v0, 0xff

    move/from16 v26, v0

    add-int/lit8 v23, v24, 0x1

    .end local v24    # "ptr":I
    .restart local v23    # "ptr":I
    aget-byte v27, v6, v24

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x8

    or-int v26, v26, v27

    add-int/lit8 v24, v23, 0x1

    .end local v23    # "ptr":I
    .restart local v24    # "ptr":I
    aget-byte v27, v6, v23

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x10

    or-int v26, v26, v27

    add-int/lit8 v23, v24, 0x1

    .end local v24    # "ptr":I
    .restart local v23    # "ptr":I
    aget-byte v27, v6, v24

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x18

    or-int v26, v26, v27

    move/from16 v0, v26

    iput v0, v12, Lcom/lge/hardware/LGCamera$ProxyData;->sig:I

    .line 930
    add-int/lit8 v24, v23, 0x1

    .end local v23    # "ptr":I
    .restart local v24    # "ptr":I
    aget-byte v26, v6, v23

    move/from16 v0, v26

    and-int/lit16 v0, v0, 0xff

    move/from16 v26, v0

    add-int/lit8 v23, v24, 0x1

    .end local v24    # "ptr":I
    .restart local v23    # "ptr":I
    aget-byte v27, v6, v24

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x8

    or-int v26, v26, v27

    add-int/lit8 v24, v23, 0x1

    .end local v23    # "ptr":I
    .restart local v24    # "ptr":I
    aget-byte v27, v6, v23

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x10

    or-int v26, v26, v27

    add-int/lit8 v23, v24, 0x1

    .end local v24    # "ptr":I
    .restart local v23    # "ptr":I
    aget-byte v27, v6, v24

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x18

    or-int v26, v26, v27

    move/from16 v0, v26

    iput v0, v12, Lcom/lge/hardware/LGCamera$ProxyData;->amb:I

    .line 932
    add-int/lit8 v24, v23, 0x1

    .end local v23    # "ptr":I
    .restart local v24    # "ptr":I
    aget-byte v26, v6, v23

    move/from16 v0, v26

    and-int/lit16 v0, v0, 0xff

    move/from16 v26, v0

    add-int/lit8 v23, v24, 0x1

    .end local v24    # "ptr":I
    .restart local v23    # "ptr":I
    aget-byte v27, v6, v24

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x8

    or-int v26, v26, v27

    add-int/lit8 v24, v23, 0x1

    .end local v23    # "ptr":I
    .restart local v24    # "ptr":I
    aget-byte v27, v6, v23

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x10

    or-int v26, v26, v27

    add-int/lit8 v23, v24, 0x1

    .end local v24    # "ptr":I
    .restart local v23    # "ptr":I
    aget-byte v27, v6, v24

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x18

    or-int v26, v26, v27

    move/from16 v0, v26

    iput v0, v12, Lcom/lge/hardware/LGCamera$ProxyData;->raw:I

    .line 938
    :goto_2aa
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v26, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mProxyDataListener:Lcom/lge/hardware/LGCamera$ProxyDataListener;
    invoke-static/range {v26 .. v26}, Lcom/lge/hardware/LGCamera;->access$400(Lcom/lge/hardware/LGCamera;)Lcom/lge/hardware/LGCamera$ProxyDataListener;

    move-result-object v26

    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v27, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;
    invoke-static/range {v27 .. v27}, Lcom/lge/hardware/LGCamera;->access$000(Lcom/lge/hardware/LGCamera;)Landroid/hardware/Camera;

    move-result-object v27

    move-object/from16 v0, v26

    move-object/from16 v1, v27

    invoke-interface {v0, v12, v1}, Lcom/lge/hardware/LGCamera$ProxyDataListener;->onDataListen(Lcom/lge/hardware/LGCamera$ProxyData;Landroid/hardware/Camera;)V

    goto/16 :goto_27

    .line 936
    :cond_2c7
    const/16 v26, -0x1

    move/from16 v0, v26

    iput v0, v12, Lcom/lge/hardware/LGCamera$ProxyData;->val:I

    goto :goto_2aa

    .line 946
    .end local v6    # "byteData":[B
    .end local v12    # "data":Lcom/lge/hardware/LGCamera$ProxyData;
    .end local v23    # "ptr":I
    :sswitch_2ce
    move-object/from16 v0, p1

    iget-object v0, v0, Landroid/os/Message;->obj:Ljava/lang/Object;

    move-object/from16 v26, v0

    check-cast v26, [B

    move-object/from16 v5, v26

    check-cast v5, [B

    .line 947
    .local v5, "buf":[B
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v26, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I
    invoke-static/range {v26 .. v26}, Lcom/lge/hardware/LGCamera;->access$500(Lcom/lge/hardware/LGCamera;)I

    move-result v26

    if-lez v26, :cond_27

    if-eqz v5, :cond_27

    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v26, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;
    invoke-static/range {v26 .. v26}, Lcom/lge/hardware/LGCamera;->access$000(Lcom/lge/hardware/LGCamera;)Landroid/hardware/Camera;

    move-result-object v26

    if-eqz v26, :cond_27

    .line 950
    const/4 v8, 0x0

    .line 951
    .local v8, "cb1":Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;
    const/4 v9, 0x0

    .line 952
    .local v9, "cb2":Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;
    const/4 v10, 0x0

    .line 953
    .local v10, "cb3":Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v26, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mMetaDataCallbackLock:Ljava/lang/Object;
    invoke-static/range {v26 .. v26}, Lcom/lge/hardware/LGCamera;->access$600(Lcom/lge/hardware/LGCamera;)Ljava/lang/Object;

    move-result-object v27

    monitor-enter v27

    .line 954
    :try_start_302
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v26, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mHdrMetaDataCallback:Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;
    invoke-static/range {v26 .. v26}, Lcom/lge/hardware/LGCamera;->access$700(Lcom/lge/hardware/LGCamera;)Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;

    move-result-object v8

    .line 955
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v26, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mFlashMetaDataCallback:Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;
    invoke-static/range {v26 .. v26}, Lcom/lge/hardware/LGCamera;->access$800(Lcom/lge/hardware/LGCamera;)Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;

    move-result-object v9

    .line 956
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v26, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mLGManualModeMetaDataCallback:Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;
    invoke-static/range {v26 .. v26}, Lcom/lge/hardware/LGCamera;->access$900(Lcom/lge/hardware/LGCamera;)Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;

    move-result-object v10

    .line 957
    monitor-exit v27
    :try_end_321
    .catchall {:try_start_302 .. :try_end_321} :catchall_3cc

    .line 960
    const/16 v26, 0x0

    aget-byte v26, v5, v26

    and-int/lit8 v26, v26, 0x4

    if-eqz v26, :cond_356

    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v26, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I
    invoke-static/range {v26 .. v26}, Lcom/lge/hardware/LGCamera;->access$500(Lcom/lge/hardware/LGCamera;)I

    move-result v26

    and-int/lit8 v26, v26, 0x4

    if-eqz v26, :cond_356

    if-eqz v8, :cond_356

    .line 961
    const/16 v26, 0x1

    move/from16 v0, v26

    new-array v14, v0, [B

    .line 962
    .local v14, "hdr_data":[B
    const/16 v26, 0x0

    const/16 v27, 0x4

    aget-byte v27, v5, v27

    aput-byte v27, v14, v26

    .line 963
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v26, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;
    invoke-static/range {v26 .. v26}, Lcom/lge/hardware/LGCamera;->access$000(Lcom/lge/hardware/LGCamera;)Landroid/hardware/Camera;

    move-result-object v26

    move-object/from16 v0, v26

    invoke-interface {v8, v14, v0}, Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;->onCameraMetaData([BLandroid/hardware/Camera;)V

    .line 966
    .end local v14    # "hdr_data":[B
    :cond_356
    const/16 v26, 0x0

    aget-byte v26, v5, v26

    and-int/lit8 v26, v26, 0x8

    if-eqz v26, :cond_38b

    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v26, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I
    invoke-static/range {v26 .. v26}, Lcom/lge/hardware/LGCamera;->access$500(Lcom/lge/hardware/LGCamera;)I

    move-result v26

    and-int/lit8 v26, v26, 0x8

    if-eqz v26, :cond_38b

    if-eqz v9, :cond_38b

    .line 967
    const/16 v26, 0x1

    move/from16 v0, v26

    new-array v13, v0, [B

    .line 968
    .local v13, "flash_data":[B
    const/16 v26, 0x0

    const/16 v27, 0x8

    aget-byte v27, v5, v27

    aput-byte v27, v13, v26

    .line 969
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v26, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;
    invoke-static/range {v26 .. v26}, Lcom/lge/hardware/LGCamera;->access$000(Lcom/lge/hardware/LGCamera;)Landroid/hardware/Camera;

    move-result-object v26

    move-object/from16 v0, v26

    invoke-interface {v9, v13, v0}, Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;->onCameraMetaData([BLandroid/hardware/Camera;)V

    .line 973
    .end local v13    # "flash_data":[B
    :cond_38b
    const/16 v26, 0x0

    aget-byte v26, v5, v26

    and-int/lit8 v26, v26, 0x12

    if-eqz v26, :cond_27

    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v26, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mEnabledMetaData:I
    invoke-static/range {v26 .. v26}, Lcom/lge/hardware/LGCamera;->access$500(Lcom/lge/hardware/LGCamera;)I

    move-result v26

    and-int/lit8 v26, v26, 0x12

    if-eqz v26, :cond_27

    if-eqz v10, :cond_27

    .line 974
    const/16 v26, 0x10

    move/from16 v0, v26

    new-array v0, v0, [B

    move-object/from16 v20, v0

    .line 975
    .local v20, "lg_manual_data":[B
    array-length v0, v5

    move/from16 v26, v0

    const/16 v27, 0x1c

    move/from16 v0, v26

    move/from16 v1, v27

    if-lt v0, v1, :cond_3cf

    .line 976
    const/16 v23, 0xc

    .line 978
    .restart local v23    # "ptr":I
    const/4 v15, 0x0

    .restart local v15    # "i":I
    move/from16 v24, v23

    .end local v23    # "ptr":I
    .restart local v24    # "ptr":I
    :goto_3bb
    const/16 v26, 0x10

    move/from16 v0, v26

    if-ge v15, v0, :cond_3e4

    .line 980
    add-int/lit8 v23, v24, 0x1

    .end local v24    # "ptr":I
    .restart local v23    # "ptr":I
    aget-byte v26, v5, v24

    aput-byte v26, v20, v15

    .line 978
    add-int/lit8 v15, v15, 0x1

    move/from16 v24, v23

    .end local v23    # "ptr":I
    .restart local v24    # "ptr":I
    goto :goto_3bb

    .line 957
    .end local v15    # "i":I
    .end local v20    # "lg_manual_data":[B
    .end local v24    # "ptr":I
    :catchall_3cc
    move-exception v26

    :try_start_3cd
    monitor-exit v27
    :try_end_3ce
    .catchall {:try_start_3cd .. :try_end_3ce} :catchall_3cc

    throw v26

    .line 984
    .restart local v20    # "lg_manual_data":[B
    :cond_3cf
    const/4 v15, 0x0

    .restart local v15    # "i":I
    :goto_3d0
    const/16 v26, 0x10

    move/from16 v0, v26

    if-ge v15, v0, :cond_3dd

    .line 985
    const/16 v26, 0x0

    aput-byte v26, v20, v15

    .line 984
    add-int/lit8 v15, v15, 0x1

    goto :goto_3d0

    .line 987
    :cond_3dd
    const-string v26, "LGCamera"

    const-string v27, "error! Manual mode was set but data was not matched."

    invoke-static/range {v26 .. v27}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 989
    :cond_3e4
    const/16 v23, 0x0

    .line 990
    .restart local v23    # "ptr":I
    add-int/lit8 v24, v23, 0x1

    .end local v23    # "ptr":I
    .restart local v24    # "ptr":I
    aget-byte v26, v20, v23

    move/from16 v0, v26

    and-int/lit16 v0, v0, 0xff

    move/from16 v26, v0

    add-int/lit8 v23, v24, 0x1

    .end local v24    # "ptr":I
    .restart local v23    # "ptr":I
    aget-byte v27, v20, v24

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x8

    or-int v26, v26, v27

    add-int/lit8 v24, v23, 0x1

    .end local v23    # "ptr":I
    .restart local v24    # "ptr":I
    aget-byte v27, v20, v23

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x10

    or-int v26, v26, v27

    add-int/lit8 v23, v24, 0x1

    .end local v24    # "ptr":I
    .restart local v23    # "ptr":I
    aget-byte v27, v20, v24

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x18

    or-int v16, v26, v27

    .line 994
    .local v16, "ia":I
    add-int/lit8 v24, v23, 0x1

    .end local v23    # "ptr":I
    .restart local v24    # "ptr":I
    aget-byte v26, v20, v23

    move/from16 v0, v26

    and-int/lit16 v0, v0, 0xff

    move/from16 v26, v0

    add-int/lit8 v23, v24, 0x1

    .end local v24    # "ptr":I
    .restart local v23    # "ptr":I
    aget-byte v27, v20, v24

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x8

    or-int v26, v26, v27

    add-int/lit8 v24, v23, 0x1

    .end local v23    # "ptr":I
    .restart local v24    # "ptr":I
    aget-byte v27, v20, v23

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x10

    or-int v26, v26, v27

    add-int/lit8 v23, v24, 0x1

    .end local v24    # "ptr":I
    .restart local v23    # "ptr":I
    aget-byte v27, v20, v24

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x18

    or-int v17, v26, v27

    .line 998
    .local v17, "ib":I
    add-int/lit8 v24, v23, 0x1

    .end local v23    # "ptr":I
    .restart local v24    # "ptr":I
    aget-byte v26, v20, v23

    move/from16 v0, v26

    and-int/lit16 v0, v0, 0xff

    move/from16 v26, v0

    add-int/lit8 v23, v24, 0x1

    .end local v24    # "ptr":I
    .restart local v23    # "ptr":I
    aget-byte v27, v20, v24

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x8

    or-int v26, v26, v27

    add-int/lit8 v24, v23, 0x1

    .end local v23    # "ptr":I
    .restart local v24    # "ptr":I
    aget-byte v27, v20, v23

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x10

    or-int v26, v26, v27

    add-int/lit8 v23, v24, 0x1

    .end local v24    # "ptr":I
    .restart local v23    # "ptr":I
    aget-byte v27, v20, v24

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x18

    or-int v18, v26, v27

    .line 1002
    .local v18, "ic":I
    add-int/lit8 v24, v23, 0x1

    .end local v23    # "ptr":I
    .restart local v24    # "ptr":I
    aget-byte v26, v20, v23

    move/from16 v0, v26

    and-int/lit16 v0, v0, 0xff

    move/from16 v26, v0

    add-int/lit8 v23, v24, 0x1

    .end local v24    # "ptr":I
    .restart local v23    # "ptr":I
    aget-byte v27, v20, v24

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x8

    or-int v26, v26, v27

    add-int/lit8 v24, v23, 0x1

    .end local v23    # "ptr":I
    .restart local v24    # "ptr":I
    aget-byte v27, v20, v23

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x10

    or-int v26, v26, v27

    add-int/lit8 v23, v24, 0x1

    .end local v24    # "ptr":I
    .restart local v23    # "ptr":I
    aget-byte v27, v20, v24

    move/from16 v0, v27

    and-int/lit16 v0, v0, 0xff

    move/from16 v27, v0

    shl-int/lit8 v27, v27, 0x18

    or-int v19, v26, v27

    .line 1006
    .local v19, "id":I
    invoke-static/range {v16 .. v16}, Ljava/lang/Float;->intBitsToFloat(I)F

    move-result v3

    .line 1007
    .local v3, "a":F
    invoke-static/range {v17 .. v17}, Ljava/lang/Float;->intBitsToFloat(I)F

    move-result v4

    .line 1008
    .local v4, "b":F
    invoke-static/range {v18 .. v18}, Ljava/lang/Float;->intBitsToFloat(I)F

    move-result v7

    .line 1009
    .local v7, "c":F
    invoke-static/range {v19 .. v19}, Ljava/lang/Float;->intBitsToFloat(I)F

    move-result v11

    .line 1010
    .local v11, "d":F
    const-string v26, "LGCamera"

    const-string v27, "dennis: %f %f %f %f length=%d buf.length=%d"

    const/16 v28, 0x6

    move/from16 v0, v28

    new-array v0, v0, [Ljava/lang/Object;

    move-object/from16 v28, v0

    const/16 v29, 0x0

    invoke-static {v3}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;

    move-result-object v30

    aput-object v30, v28, v29

    const/16 v29, 0x1

    invoke-static {v4}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;

    move-result-object v30

    aput-object v30, v28, v29

    const/16 v29, 0x2

    invoke-static {v7}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;

    move-result-object v30

    aput-object v30, v28, v29

    const/16 v29, 0x3

    invoke-static {v11}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;

    move-result-object v30

    aput-object v30, v28, v29

    const/16 v29, 0x4

    move-object/from16 v0, v20

    array-length v0, v0

    move/from16 v30, v0

    invoke-static/range {v30 .. v30}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v30

    aput-object v30, v28, v29

    const/16 v29, 0x5

    array-length v0, v5

    move/from16 v30, v0

    invoke-static/range {v30 .. v30}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v30

    aput-object v30, v28, v29

    invoke-static/range {v27 .. v28}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v27

    invoke-static/range {v26 .. v27}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    .line 1012
    move-object/from16 v0, p0

    iget-object v0, v0, Lcom/lge/hardware/LGCamera$EventHandler;->this$0:Lcom/lge/hardware/LGCamera;

    move-object/from16 v26, v0

    # getter for: Lcom/lge/hardware/LGCamera;->mCamera:Landroid/hardware/Camera;
    invoke-static/range {v26 .. v26}, Lcom/lge/hardware/LGCamera;->access$000(Lcom/lge/hardware/LGCamera;)Landroid/hardware/Camera;

    move-result-object v26

    move-object/from16 v0, v20

    move-object/from16 v1, v26

    invoke-interface {v10, v0, v1}, Lcom/lge/hardware/LGCamera$CameraMetaDataCallback;->onCameraMetaData([BLandroid/hardware/Camera;)V

    goto/16 :goto_27

    .line 874
    :sswitch_data_524
    .sparse-switch
        0x1000 -> :sswitch_122
        0x2000 -> :sswitch_2ce
        0x5000 -> :sswitch_28
        0x8000 -> :sswitch_171
    .end sparse-switch
.end method
