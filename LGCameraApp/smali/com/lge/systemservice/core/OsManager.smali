.class public Lcom/lge/systemservice/core/OsManager;
.super Ljava/lang/Object;
.source "OsManager.java"


# static fields
.field static final SERVICE_NAME:Ljava/lang/String; = "osservice"

.field private static final TAG:Ljava/lang/String;


# instance fields
.field private mService:Lcom/lge/systemservice/core/IOsManager;


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .prologue
    .line 23
    const-class v0, Lcom/lge/systemservice/core/OsManager;

    invoke-virtual {v0}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    move-result-object v0

    sput-object v0, Lcom/lge/systemservice/core/OsManager;->TAG:Ljava/lang/String;

    return-void
.end method

.method constructor <init>(Landroid/content/Context;)V
    .registers 2
    .param p1, "context"    # Landroid/content/Context;

    .prologue
    .line 27
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 29
    return-void
.end method

.method static synthetic access$002(Lcom/lge/systemservice/core/OsManager;Lcom/lge/systemservice/core/IOsManager;)Lcom/lge/systemservice/core/IOsManager;
    .registers 2
    .param p0, "x0"    # Lcom/lge/systemservice/core/OsManager;
    .param p1, "x1"    # Lcom/lge/systemservice/core/IOsManager;

    .prologue
    .line 20
    iput-object p1, p0, Lcom/lge/systemservice/core/OsManager;->mService:Lcom/lge/systemservice/core/IOsManager;

    return-object p1
.end method

.method private final getService()Lcom/lge/systemservice/core/IOsManager;
    .registers 5

    .prologue
    .line 32
    iget-object v1, p0, Lcom/lge/systemservice/core/OsManager;->mService:Lcom/lge/systemservice/core/IOsManager;

    if-nez v1, :cond_23

    .line 33
    const-string v1, "osservice"

    invoke-static {v1}, Landroid/os/ServiceManager;->getService(Ljava/lang/String;)Landroid/os/IBinder;

    move-result-object v1

    invoke-static {v1}, Lcom/lge/systemservice/core/IOsManager$Stub;->asInterface(Landroid/os/IBinder;)Lcom/lge/systemservice/core/IOsManager;

    move-result-object v1

    iput-object v1, p0, Lcom/lge/systemservice/core/OsManager;->mService:Lcom/lge/systemservice/core/IOsManager;

    .line 34
    iget-object v1, p0, Lcom/lge/systemservice/core/OsManager;->mService:Lcom/lge/systemservice/core/IOsManager;

    if-eqz v1, :cond_23

    .line 36
    :try_start_14
    iget-object v1, p0, Lcom/lge/systemservice/core/OsManager;->mService:Lcom/lge/systemservice/core/IOsManager;

    invoke-interface {v1}, Lcom/lge/systemservice/core/IOsManager;->asBinder()Landroid/os/IBinder;

    move-result-object v1

    new-instance v2, Lcom/lge/systemservice/core/OsManager$1;

    invoke-direct {v2, p0}, Lcom/lge/systemservice/core/OsManager$1;-><init>(Lcom/lge/systemservice/core/OsManager;)V

    const/4 v3, 0x0

    invoke-interface {v1, v2, v3}, Landroid/os/IBinder;->linkToDeath(Landroid/os/IBinder$DeathRecipient;I)V
    :try_end_23
    .catch Landroid/os/RemoteException; {:try_start_14 .. :try_end_23} :catch_26

    .line 43
    :cond_23
    :goto_23
    iget-object v1, p0, Lcom/lge/systemservice/core/OsManager;->mService:Lcom/lge/systemservice/core/IOsManager;

    return-object v1

    .line 40
    :catch_26
    move-exception v0

    .local v0, "e":Landroid/os/RemoteException;
    const/4 v1, 0x0

    iput-object v1, p0, Lcom/lge/systemservice/core/OsManager;->mService:Lcom/lge/systemservice/core/IOsManager;

    goto :goto_23
.end method


# virtual methods
.method public goToSleepWithForce(JI)V
    .registers 9
    .param p1, "time"    # J
    .param p3, "reason"    # I

    .prologue
    .line 98
    :try_start_0
    invoke-direct {p0}, Lcom/lge/systemservice/core/OsManager;->getService()Lcom/lge/systemservice/core/IOsManager;

    move-result-object v1

    .line 99
    .local v1, "service":Lcom/lge/systemservice/core/IOsManager;
    if-eqz v1, :cond_9

    invoke-interface {v1, p1, p2, p3}, Lcom/lge/systemservice/core/IOsManager;->goToSleepWithForce(JI)V
    :try_end_9
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_9} :catch_a

    .line 104
    .end local v1    # "service":Lcom/lge/systemservice/core/IOsManager;
    :cond_9
    :goto_9
    return-void

    .line 101
    :catch_a
    move-exception v0

    .line 102
    .local v0, "e":Landroid/os/RemoteException;
    sget-object v2, Lcom/lge/systemservice/core/OsManager;->TAG:Ljava/lang/String;

    const-string v3, "Failed to start goToSleepWithForce"

    invoke-static {v2, v3, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    goto :goto_9
.end method

.method public setSystemProperty(Ljava/lang/String;Ljava/lang/String;)V
    .registers 8
    .param p1, "key"    # Ljava/lang/String;
    .param p2, "val"    # Ljava/lang/String;

    .prologue
    .line 61
    :try_start_0
    invoke-direct {p0}, Lcom/lge/systemservice/core/OsManager;->getService()Lcom/lge/systemservice/core/IOsManager;

    move-result-object v1

    .line 62
    .local v1, "service":Lcom/lge/systemservice/core/IOsManager;
    if-eqz v1, :cond_9

    invoke-interface {v1, p1, p2}, Lcom/lge/systemservice/core/IOsManager;->setSystemProperty(Ljava/lang/String;Ljava/lang/String;)V
    :try_end_9
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_9} :catch_a

    .line 66
    .end local v1    # "service":Lcom/lge/systemservice/core/IOsManager;
    :cond_9
    :goto_9
    return-void

    .line 63
    :catch_a
    move-exception v0

    .line 64
    .local v0, "e":Landroid/os/RemoteException;
    sget-object v2, Lcom/lge/systemservice/core/OsManager;->TAG:Ljava/lang/String;

    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "Failed to set system property: "

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    const-string v4, " as "

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v2, v3, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    goto :goto_9
.end method

.method public stopRingtoneSound()V
    .registers 5

    .prologue
    .line 77
    :try_start_0
    invoke-direct {p0}, Lcom/lge/systemservice/core/OsManager;->getService()Lcom/lge/systemservice/core/IOsManager;

    move-result-object v1

    .line 78
    .local v1, "service":Lcom/lge/systemservice/core/IOsManager;
    if-eqz v1, :cond_9

    invoke-interface {v1}, Lcom/lge/systemservice/core/IOsManager;->stopRingtoneSound()V
    :try_end_9
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_9} :catch_a

    .line 83
    .end local v1    # "service":Lcom/lge/systemservice/core/IOsManager;
    :cond_9
    :goto_9
    return-void

    .line 80
    :catch_a
    move-exception v0

    .line 81
    .local v0, "e":Landroid/os/RemoteException;
    sget-object v2, Lcom/lge/systemservice/core/OsManager;->TAG:Ljava/lang/String;

    const-string v3, "Failed to stop ringtone sound"

    invoke-static {v2, v3, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    goto :goto_9
.end method

.method public wakeUpWithForce(J)V
    .registers 8
    .param p1, "time"    # J

    .prologue
    .line 118
    :try_start_0
    invoke-direct {p0}, Lcom/lge/systemservice/core/OsManager;->getService()Lcom/lge/systemservice/core/IOsManager;

    move-result-object v1

    .line 119
    .local v1, "service":Lcom/lge/systemservice/core/IOsManager;
    if-eqz v1, :cond_9

    invoke-interface {v1, p1, p2}, Lcom/lge/systemservice/core/IOsManager;->wakeUpWithForce(J)V
    :try_end_9
    .catch Landroid/os/RemoteException; {:try_start_0 .. :try_end_9} :catch_a

    .line 124
    .end local v1    # "service":Lcom/lge/systemservice/core/IOsManager;
    :cond_9
    :goto_9
    return-void

    .line 121
    :catch_a
    move-exception v0

    .line 122
    .local v0, "e":Landroid/os/RemoteException;
    sget-object v2, Lcom/lge/systemservice/core/OsManager;->TAG:Ljava/lang/String;

    const-string v3, "Failed to start wakeUpWithForce"

    invoke-static {v2, v3, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    goto :goto_9
.end method
