.class Lcom/lge/camera/controller/PreviewController$21;
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
    .line 2623
    iput-object p1, p0, Lcom/lge/camera/controller/PreviewController$21;->this$0:Lcom/lge/camera/controller/PreviewController;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 2

    .prologue
    .line 2625
    const-string v0, "CameraApp"

    const-string v1, "##### below onPreviewFrame is just a fake..it is not from sensor callback."

    invoke-static {v0, v1}, Lcom/lge/camera/util/CamLog;->d(Ljava/lang/String;Ljava/lang/String;)V

    .line 2627
    iget-object v0, p0, Lcom/lge/camera/controller/PreviewController$21;->this$0:Lcom/lge/camera/controller/PreviewController;

    invoke-virtual {v0}, Lcom/lge/camera/controller/PreviewController;->checkStartPreviewCallback()V

    .line 2628
    return-void
.end method
