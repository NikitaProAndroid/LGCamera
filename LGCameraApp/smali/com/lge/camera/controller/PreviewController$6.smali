.class Lcom/lge/camera/controller/PreviewController$6;
.super Ljava/lang/Object;
.source "PreviewController.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lcom/lge/camera/controller/PreviewController;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/lge/camera/controller/PreviewController;


# direct methods
.method constructor <init>(Lcom/lge/camera/controller/PreviewController;)V
    .locals 0

    .prologue
    .line 1157
    iput-object p1, p0, Lcom/lge/camera/controller/PreviewController$6;->this$0:Lcom/lge/camera/controller/PreviewController;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 6

    .prologue
    const/4 v5, 0x1

    .line 1160
    :try_start_0
    const-string v2, "CameraApp"

    const-string v3, "mStartPreviewThread start"

    invoke-static {v2, v3}, Lcom/lge/camera/util/CamLog;->d(Ljava/lang/String;Ljava/lang/String;)V

    .line 1161
    iget-object v2, p0, Lcom/lge/camera/controller/PreviewController$6;->this$0:Lcom/lge/camera/controller/PreviewController;

    const/4 v3, 0x0

    # setter for: Lcom/lge/camera/controller/PreviewController;->mStartPreviewFail:Z
    invoke-static {v2, v3}, Lcom/lge/camera/controller/PreviewController;->access$202(Lcom/lge/camera/controller/PreviewController;Z)Z

    .line 1163
    iget-object v2, p0, Lcom/lge/camera/controller/PreviewController$6;->this$0:Lcom/lge/camera/controller/PreviewController;

    # getter for: Lcom/lge/camera/controller/PreviewController;->mUseOnResume:Z
    invoke-static {v2}, Lcom/lge/camera/controller/PreviewController;->access$300(Lcom/lge/camera/controller/PreviewController;)Z

    move-result v2

    if-eqz v2, :cond_0

    .line 1164
    iget-object v2, p0, Lcom/lge/camera/controller/PreviewController$6;->this$0:Lcom/lge/camera/controller/PreviewController;

    const/4 v3, 0x0

    const/4 v4, 0x0

    invoke-virtual {v2, v3, v4}, Lcom/lge/camera/controller/PreviewController;->startPreview(Lcom/lge/hardware/LGCamera$LGParameters;Z)V

    .line 1192
    :goto_0
    return-void

    .line 1168
    :cond_0
    iget-object v2, p0, Lcom/lge/camera/controller/PreviewController$6;->this$0:Lcom/lge/camera/controller/PreviewController;

    # invokes: Lcom/lge/camera/controller/PreviewController;->ensureCameraDevice()V
    invoke-static {v2}, Lcom/lge/camera/controller/PreviewController;->access$400(Lcom/lge/camera/controller/PreviewController;)V
    :try_end_0
    .catch Lcom/lge/camera/util/CameraHardwareException; {:try_start_0 .. :try_end_0} :catch_0

    .line 1171
    :try_start_1
    iget-object v2, p0, Lcom/lge/camera/controller/PreviewController$6;->this$0:Lcom/lge/camera/controller/PreviewController;

    # getter for: Lcom/lge/camera/controller/PreviewController;->mLatch:Ljava/util/concurrent/CountDownLatch;
    invoke-static {v2}, Lcom/lge/camera/controller/PreviewController;->access$500(Lcom/lge/camera/controller/PreviewController;)Ljava/util/concurrent/CountDownLatch;

    move-result-object v2

    if-eqz v2, :cond_1

    .line 1172
    iget-object v2, p0, Lcom/lge/camera/controller/PreviewController$6;->this$0:Lcom/lge/camera/controller/PreviewController;

    # getter for: Lcom/lge/camera/controller/PreviewController;->mLatch:Ljava/util/concurrent/CountDownLatch;
    invoke-static {v2}, Lcom/lge/camera/controller/PreviewController;->access$500(Lcom/lge/camera/controller/PreviewController;)Ljava/util/concurrent/CountDownLatch;

    move-result-object v2

    invoke-virtual {v2}, Ljava/util/concurrent/CountDownLatch;->await()V
    :try_end_1
    .catch Ljava/lang/InterruptedException; {:try_start_1 .. :try_end_1} :catch_1
    .catch Lcom/lge/camera/util/CameraHardwareException; {:try_start_1 .. :try_end_1} :catch_0

    .line 1178
    :cond_1
    :goto_1
    :try_start_2
    iget-object v2, p0, Lcom/lge/camera/controller/PreviewController$6;->this$0:Lcom/lge/camera/controller/PreviewController;

    iget-boolean v2, v2, Lcom/lge/camera/controller/PreviewController;->bRendered:Z

    if-nez v2, :cond_2

    .line 1179
    iget-object v2, p0, Lcom/lge/camera/controller/PreviewController$6;->this$0:Lcom/lge/camera/controller/PreviewController;

    const/4 v3, 0x0

    const/4 v4, 0x1

    invoke-virtual {v2, v3, v4}, Lcom/lge/camera/controller/PreviewController;->startPreview(Lcom/lge/hardware/LGCamera$LGParameters;Z)V

    .line 1181
    :cond_2
    const-string v2, "CameraApp"

    const-string v3, "mStartPreviewThread end"

    invoke-static {v2, v3}, Lcom/lge/camera/util/CamLog;->d(Ljava/lang/String;Ljava/lang/String;)V
    :try_end_2
    .catch Lcom/lge/camera/util/CameraHardwareException; {:try_start_2 .. :try_end_2} :catch_0

    goto :goto_0

    .line 1182
    :catch_0
    move-exception v0

    .line 1183
    .local v0, "e":Lcom/lge/camera/util/CameraHardwareException;
    iget-object v2, p0, Lcom/lge/camera/controller/PreviewController$6;->this$0:Lcom/lge/camera/controller/PreviewController;

    # setter for: Lcom/lge/camera/controller/PreviewController;->mStartPreviewFail:Z
    invoke-static {v2, v5}, Lcom/lge/camera/controller/PreviewController;->access$202(Lcom/lge/camera/controller/PreviewController;Z)Z

    .line 1184
    iget-object v2, p0, Lcom/lge/camera/controller/PreviewController$6;->this$0:Lcom/lge/camera/controller/PreviewController;

    iget-object v2, v2, Lcom/lge/camera/controller/PreviewController;->mGet:Lcom/lge/camera/ControllerFunction;

    new-instance v3, Lcom/lge/camera/controller/PreviewController$6$1;

    invoke-direct {v3, p0}, Lcom/lge/camera/controller/PreviewController$6$1;-><init>(Lcom/lge/camera/controller/PreviewController$6;)V

    invoke-interface {v2, v3}, Lcom/lge/camera/ControllerFunction;->runOnUiThread(Ljava/lang/Runnable;)V

    .line 1190
    const-string v2, "CameraApp"

    const-string v3, "CameraHardwareException : "

    invoke-static {v2, v3, v0}, Lcom/lge/camera/util/CamLog;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V

    goto :goto_0

    .line 1174
    .end local v0    # "e":Lcom/lge/camera/util/CameraHardwareException;
    :catch_1
    move-exception v1

    .line 1175
    .local v1, "ie":Ljava/lang/InterruptedException;
    :try_start_3
    const-string v2, "CameraApp"

    const-string v3, "InterruptedException : "

    invoke-static {v2, v3, v1}, Lcom/lge/camera/util/CamLog;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V

    .line 1176
    iget-object v2, p0, Lcom/lge/camera/controller/PreviewController$6;->this$0:Lcom/lge/camera/controller/PreviewController;

    const/4 v3, 0x1

    # setter for: Lcom/lge/camera/controller/PreviewController;->mStartPreviewFail:Z
    invoke-static {v2, v3}, Lcom/lge/camera/controller/PreviewController;->access$202(Lcom/lge/camera/controller/PreviewController;Z)Z
    :try_end_3
    .catch Lcom/lge/camera/util/CameraHardwareException; {:try_start_3 .. :try_end_3} :catch_0

    goto :goto_1
.end method
