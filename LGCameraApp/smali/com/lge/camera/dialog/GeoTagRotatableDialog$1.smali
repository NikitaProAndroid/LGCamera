.class Lcom/lge/camera/dialog/GeoTagRotatableDialog$1;
.super Ljava/lang/Object;
.source "GeoTagRotatableDialog.java"

# interfaces
.implements Landroid/view/View$OnClickListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/lge/camera/dialog/GeoTagRotatableDialog;->create()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/lge/camera/dialog/GeoTagRotatableDialog;


# direct methods
.method constructor <init>(Lcom/lge/camera/dialog/GeoTagRotatableDialog;)V
    .locals 0

    .prologue
    .line 45
    iput-object p1, p0, Lcom/lge/camera/dialog/GeoTagRotatableDialog$1;->this$0:Lcom/lge/camera/dialog/GeoTagRotatableDialog;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .locals 4
    .param p1, "v"    # Landroid/view/View;

    .prologue
    .line 47
    const-string v1, "CameraApp"

    const-string v2, "ok button click...."

    invoke-static {v1, v2}, Lcom/lge/camera/util/CamLog;->d(Ljava/lang/String;Ljava/lang/String;)V

    .line 49
    :try_start_0
    iget-object v1, p0, Lcom/lge/camera/dialog/GeoTagRotatableDialog$1;->this$0:Lcom/lge/camera/dialog/GeoTagRotatableDialog;

    iget-object v1, v1, Lcom/lge/camera/dialog/GeoTagRotatableDialog;->mGet:Lcom/lge/camera/ControllerFunction;

    const/4 v2, 0x1

    invoke-interface {v1, v2}, Lcom/lge/camera/ControllerFunction;->setOpenLBSSetting(Z)V

    .line 50
    iget-object v1, p0, Lcom/lge/camera/dialog/GeoTagRotatableDialog$1;->this$0:Lcom/lge/camera/dialog/GeoTagRotatableDialog;

    iget-object v1, v1, Lcom/lge/camera/dialog/GeoTagRotatableDialog;->mGet:Lcom/lge/camera/ControllerFunction;

    invoke-interface {v1}, Lcom/lge/camera/ControllerFunction;->getActivity()Landroid/app/Activity;

    move-result-object v1

    new-instance v2, Landroid/content/Intent;

    const-string v3, "android.settings.LOCATION_SOURCE_SETTINGS"

    invoke-direct {v2, v3}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    const/4 v3, 0x0

    invoke-virtual {v1, v2, v3}, Landroid/app/Activity;->startActivityForResult(Landroid/content/Intent;I)V
    :try_end_0
    .catch Landroid/content/ActivityNotFoundException; {:try_start_0 .. :try_end_0} :catch_0

    .line 56
    :goto_0
    iget-object v1, p0, Lcom/lge/camera/dialog/GeoTagRotatableDialog$1;->this$0:Lcom/lge/camera/dialog/GeoTagRotatableDialog;

    invoke-virtual {v1}, Lcom/lge/camera/dialog/GeoTagRotatableDialog;->onDismiss()V

    .line 57
    return-void

    .line 53
    :catch_0
    move-exception v0

    .line 54
    .local v0, "ex":Landroid/content/ActivityNotFoundException;
    const-string v1, "CameraApp"

    const-string v2, "gps setting menu open fail"

    invoke-static {v1, v2, v0}, Lcom/lge/camera/util/CamLog;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V

    goto :goto_0
.end method
