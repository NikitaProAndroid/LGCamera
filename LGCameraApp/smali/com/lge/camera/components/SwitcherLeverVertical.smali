.class public Lcom/lge/camera/components/SwitcherLeverVertical;
.super Lcom/lge/camera/components/SwitcherLever;
.source "SwitcherLeverVertical.java"

# interfaces
.implements Landroid/view/View$OnTouchListener;


# instance fields
.field private mModeCamDrawable:Landroid/graphics/drawable/Drawable;

.field private mModeDrawable:Landroid/graphics/drawable/Drawable;

.field private mModeVideoDrawable:Landroid/graphics/drawable/Drawable;


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    .prologue
    const/4 v0, 0x0

    .line 41
    invoke-direct {p0, p1}, Lcom/lge/camera/components/SwitcherLever;-><init>(Landroid/content/Context;)V

    .line 36
    iput-object v0, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeDrawable:Landroid/graphics/drawable/Drawable;

    .line 37
    iput-object v0, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeCamDrawable:Landroid/graphics/drawable/Drawable;

    .line 38
    iput-object v0, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeVideoDrawable:Landroid/graphics/drawable/Drawable;

    .line 42
    invoke-direct {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->initModeDrawable()V

    .line 43
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    .prologue
    const/4 v0, 0x0

    .line 46
    invoke-direct {p0, p1, p2}, Lcom/lge/camera/components/SwitcherLever;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    .line 36
    iput-object v0, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeDrawable:Landroid/graphics/drawable/Drawable;

    .line 37
    iput-object v0, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeCamDrawable:Landroid/graphics/drawable/Drawable;

    .line 38
    iput-object v0, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeVideoDrawable:Landroid/graphics/drawable/Drawable;

    .line 47
    invoke-direct {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->initModeDrawable()V

    .line 48
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyle"    # I

    .prologue
    const/4 v0, 0x0

    .line 51
    invoke-direct {p0, p1, p2, p3}, Lcom/lge/camera/components/SwitcherLever;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    .line 36
    iput-object v0, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeDrawable:Landroid/graphics/drawable/Drawable;

    .line 37
    iput-object v0, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeCamDrawable:Landroid/graphics/drawable/Drawable;

    .line 38
    iput-object v0, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeVideoDrawable:Landroid/graphics/drawable/Drawable;

    .line 52
    invoke-direct {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->initModeDrawable()V

    .line 53
    return-void
.end method

.method private initModeDrawable()V
    .locals 4

    .prologue
    const/4 v3, 0x0

    .line 56
    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getResources()Landroid/content/res/Resources;

    move-result-object v0

    const v1, 0x7f020033

    invoke-virtual {v0, v1}, Landroid/content/res/Resources;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    iput-object v0, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeCamDrawable:Landroid/graphics/drawable/Drawable;

    .line 57
    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getResources()Landroid/content/res/Resources;

    move-result-object v0

    const v1, 0x7f020042

    invoke-virtual {v0, v1}, Landroid/content/res/Resources;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    move-result-object v0

    iput-object v0, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeVideoDrawable:Landroid/graphics/drawable/Drawable;

    .line 58
    iget-object v0, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeCamDrawable:Landroid/graphics/drawable/Drawable;

    iget-object v1, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeCamDrawable:Landroid/graphics/drawable/Drawable;

    invoke-virtual {v1}, Landroid/graphics/drawable/Drawable;->getIntrinsicWidth()I

    move-result v1

    iget-object v2, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeCamDrawable:Landroid/graphics/drawable/Drawable;

    invoke-virtual {v2}, Landroid/graphics/drawable/Drawable;->getIntrinsicHeight()I

    move-result v2

    invoke-virtual {v0, v3, v3, v1, v2}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    .line 60
    iget-object v0, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeVideoDrawable:Landroid/graphics/drawable/Drawable;

    iget-object v1, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeVideoDrawable:Landroid/graphics/drawable/Drawable;

    invoke-virtual {v1}, Landroid/graphics/drawable/Drawable;->getIntrinsicWidth()I

    move-result v1

    iget-object v2, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeVideoDrawable:Landroid/graphics/drawable/Drawable;

    invoke-virtual {v2}, Landroid/graphics/drawable/Drawable;->getIntrinsicHeight()I

    move-result v2

    invoke-virtual {v0, v3, v3, v1, v2}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    .line 62
    return-void
.end method


# virtual methods
.method protected getAniPositionAvailable(I)I
    .locals 2
    .param p1, "drawablePosition"    # I

    .prologue
    .line 184
    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getHeight()I

    move-result v0

    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getPaddingTop()I

    move-result v1

    sub-int/2addr v0, v1

    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getPaddingBottom()I

    move-result v1

    sub-int/2addr v0, v1

    sub-int/2addr v0, p1

    return v0
.end method

.method protected getTouchPositionAvailable()I
    .locals 2

    .prologue
    .line 178
    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getHeight()I

    move-result v0

    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getPaddingTop()I

    move-result v1

    sub-int/2addr v0, v1

    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getPaddingBottom()I

    move-result v1

    sub-int/2addr v0, v1

    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    invoke-virtual {v1}, Landroid/graphics/drawable/Drawable;->getIntrinsicHeight()I

    move-result v1

    sub-int/2addr v0, v1

    return v0
.end method

.method protected onDraw(Landroid/graphics/Canvas;)V
    .locals 10
    .param p1, "canvas"    # Landroid/graphics/Canvas;

    .prologue
    .line 85
    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    .line 86
    .local v0, "drawable":Landroid/graphics/drawable/Drawable;
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->getIntrinsicHeight()I

    move-result v1

    .line 87
    .local v1, "drawableHeight":I
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->getIntrinsicWidth()I

    move-result v2

    .line 89
    .local v2, "drawableWidth":I
    if-eqz v2, :cond_0

    if-eqz v1, :cond_0

    iget-object v6, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mRotationInfo:Lcom/lge/camera/components/RotationInfo;

    if-nez v6, :cond_1

    .line 120
    :cond_0
    :goto_0
    return-void

    .line 93
    :cond_1
    iget-wide v6, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mAnimationParkingStartTime:J

    const-wide/16 v8, -0x1

    cmp-long v6, v6, v8

    if-eqz v6, :cond_2

    .line 94
    invoke-virtual {p0, v1}, Lcom/lge/camera/components/SwitcherLeverVertical;->setAnimationStartTime(I)V

    .line 97
    :cond_2
    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getPaddingTop()I

    move-result v6

    iget v7, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mPosition:I

    add-int v4, v6, v7

    .line 98
    .local v4, "offsetTop":I
    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getWidth()I

    move-result v6

    sub-int/2addr v6, v2

    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getPaddingStart()I

    move-result v7

    sub-int/2addr v6, v7

    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getPaddingEnd()I

    move-result v7

    sub-int/2addr v6, v7

    div-int/lit8 v3, v6, 0x2

    .line 100
    .local v3, "offsetLeft":I
    iget-object v6, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mRotationInfo:Lcom/lge/camera/components/RotationInfo;

    invoke-virtual {v6}, Lcom/lge/camera/components/RotationInfo;->getCurrentDegree()I

    move-result v6

    iget-object v7, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mRotationInfo:Lcom/lge/camera/components/RotationInfo;

    invoke-virtual {v7}, Lcom/lge/camera/components/RotationInfo;->getTargetDegree()I

    move-result v7

    if-eq v6, v7, :cond_3

    iget-object v6, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mRotationInfo:Lcom/lge/camera/components/RotationInfo;

    invoke-virtual {v6}, Lcom/lge/camera/components/RotationInfo;->calcCurrentDegree()Z

    move-result v6

    if-eqz v6, :cond_3

    .line 102
    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->invalidate()V

    .line 105
    :cond_3
    invoke-virtual {p1}, Landroid/graphics/Canvas;->getSaveCount()I

    move-result v5

    .line 106
    .local v5, "saveCount":I
    invoke-virtual {p1}, Landroid/graphics/Canvas;->save()I

    .line 108
    div-int/lit8 v6, v2, 0x2

    add-int/2addr v6, v3

    int-to-float v6, v6

    div-int/lit8 v7, v1, 0x2

    add-int/2addr v7, v4

    int-to-float v7, v7

    invoke-virtual {p1, v6, v7}, Landroid/graphics/Canvas;->translate(FF)V

    .line 109
    neg-int v6, v2

    div-int/lit8 v6, v6, 0x2

    int-to-float v6, v6

    neg-int v7, v1

    div-int/lit8 v7, v7, 0x2

    int-to-float v7, v7

    invoke-virtual {p1, v6, v7}, Landroid/graphics/Canvas;->translate(FF)V

    .line 110
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    .line 112
    div-int/lit8 v6, v2, 0x2

    int-to-float v6, v6

    div-int/lit8 v7, v1, 0x2

    int-to-float v7, v7

    invoke-virtual {p1, v6, v7}, Landroid/graphics/Canvas;->translate(FF)V

    .line 113
    iget-object v6, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mRotationInfo:Lcom/lge/camera/components/RotationInfo;

    invoke-virtual {v6}, Lcom/lge/camera/components/RotationInfo;->getCurrentDegree()I

    move-result v6

    neg-int v6, v6

    int-to-float v6, v6

    invoke-virtual {p1, v6}, Landroid/graphics/Canvas;->rotate(F)V

    .line 114
    neg-int v6, v2

    div-int/lit8 v6, v6, 0x2

    int-to-float v6, v6

    neg-int v7, v1

    div-int/lit8 v7, v7, 0x2

    int-to-float v7, v7

    invoke-virtual {p1, v6, v7}, Landroid/graphics/Canvas;->translate(FF)V

    .line 115
    iget-object v6, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeDrawable:Landroid/graphics/drawable/Drawable;

    if-eqz v6, :cond_4

    .line 116
    iget-object v6, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeDrawable:Landroid/graphics/drawable/Drawable;

    invoke-virtual {v6, p1}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    .line 119
    :cond_4
    invoke-virtual {p1, v5}, Landroid/graphics/Canvas;->restoreToCount(I)V

    goto/16 :goto_0
.end method

.method public setSwitcherAlpha(I)V
    .locals 2
    .param p1, "alpha"    # I

    .prologue
    .line 149
    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    .line 150
    .local v0, "d":Landroid/graphics/drawable/Drawable;
    if-eqz v0, :cond_0

    .line 151
    invoke-virtual {v0, p1}, Landroid/graphics/drawable/Drawable;->setAlpha(I)V

    .line 153
    :cond_0
    iget-object v1, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeDrawable:Landroid/graphics/drawable/Drawable;

    if-eqz v1, :cond_1

    .line 154
    iget-object v1, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeDrawable:Landroid/graphics/drawable/Drawable;

    invoke-virtual {v1, p1}, Landroid/graphics/drawable/Drawable;->setAlpha(I)V

    .line 156
    :cond_1
    return-void
.end method

.method public setSwitcherImage(II)V
    .locals 1
    .param p1, "orientation"    # I
    .param p2, "mode"    # I

    .prologue
    .line 169
    if-nez p2, :cond_0

    .line 170
    iget-object v0, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeCamDrawable:Landroid/graphics/drawable/Drawable;

    iput-object v0, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeDrawable:Landroid/graphics/drawable/Drawable;

    .line 174
    :goto_0
    return-void

    .line 172
    :cond_0
    iget-object v0, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeVideoDrawable:Landroid/graphics/drawable/Drawable;

    iput-object v0, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeDrawable:Landroid/graphics/drawable/Drawable;

    goto :goto_0
.end method

.method public startRotation(I)V
    .locals 1
    .param p1, "degree"    # I

    .prologue
    .line 160
    if-eqz p1, :cond_0

    const/16 v0, 0xb4

    if-ne p1, v0, :cond_1

    .line 161
    :cond_0
    const v0, 0x7f0204ab

    invoke-virtual {p0, v0}, Lcom/lge/camera/components/SwitcherLeverVertical;->setImageResource(I)V

    .line 165
    :goto_0
    return-void

    .line 163
    :cond_1
    const v0, 0x7f0204ac

    invoke-virtual {p0, v0}, Lcom/lge/camera/components/SwitcherLeverVertical;->setImageResource(I)V

    goto :goto_0
.end method

.method protected trackTouchEvent(Landroid/view/MotionEvent;)V
    .locals 7
    .param p1, "event"    # Landroid/view/MotionEvent;

    .prologue
    .line 66
    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object v1

    .line 68
    .local v1, "drawable":Landroid/graphics/drawable/Drawable;
    invoke-virtual {v1}, Landroid/graphics/drawable/Drawable;->getIntrinsicHeight()I

    move-result v2

    .line 69
    .local v2, "drawableHeight":I
    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getHeight()I

    move-result v3

    .line 70
    .local v3, "height":I
    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getPaddingTop()I

    move-result v5

    sub-int v5, v3, v5

    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getPaddingBottom()I

    move-result v6

    sub-int/2addr v5, v6

    sub-int v0, v5, v2

    .line 71
    .local v0, "available":I
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getY()F

    move-result v5

    float-to-int v4, v5

    .line 72
    .local v4, "y":I
    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getPaddingTop()I

    move-result v5

    sub-int v5, v4, v5

    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getPaddingBottom()I

    move-result v6

    sub-int/2addr v5, v6

    div-int/lit8 v6, v2, 0x2

    sub-int/2addr v5, v6

    iput v5, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mPosition:I

    .line 74
    iget v5, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mPosition:I

    if-gez v5, :cond_0

    .line 75
    const/4 v5, 0x0

    iput v5, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mPosition:I

    .line 77
    :cond_0
    iget v5, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mPosition:I

    if-le v5, v0, :cond_1

    .line 78
    iput v0, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mPosition:I

    .line 80
    :cond_1
    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->invalidate()V

    .line 81
    return-void
.end method

.method public unbind()V
    .locals 3

    .prologue
    const/4 v2, 0x0

    .line 124
    iput-object v2, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mListener:Lcom/lge/camera/components/SwitcherLever$OnSwitchLeverListener;

    .line 125
    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getDrawable()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    .line 126
    .local v0, "d":Landroid/graphics/drawable/Drawable;
    if-eqz v0, :cond_0

    .line 127
    invoke-virtual {v0, v2}, Landroid/graphics/drawable/Drawable;->setCallback(Landroid/graphics/drawable/Drawable$Callback;)V

    .line 128
    invoke-virtual {p0, v2}, Lcom/lge/camera/components/SwitcherLeverVertical;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    .line 130
    :cond_0
    invoke-virtual {p0}, Lcom/lge/camera/components/SwitcherLeverVertical;->getBackground()Landroid/graphics/drawable/Drawable;

    move-result-object v0

    .line 131
    if-eqz v0, :cond_1

    .line 132
    invoke-virtual {v0, v2}, Landroid/graphics/drawable/Drawable;->setCallback(Landroid/graphics/drawable/Drawable$Callback;)V

    .line 133
    invoke-virtual {p0, v2}, Lcom/lge/camera/components/SwitcherLeverVertical;->setBackground(Landroid/graphics/drawable/Drawable;)V

    .line 136
    :cond_1
    iput-object v2, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeDrawable:Landroid/graphics/drawable/Drawable;

    .line 138
    iget-object v1, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeCamDrawable:Landroid/graphics/drawable/Drawable;

    if-eqz v1, :cond_2

    .line 139
    iget-object v1, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeCamDrawable:Landroid/graphics/drawable/Drawable;

    invoke-virtual {v1, v2}, Landroid/graphics/drawable/Drawable;->setCallback(Landroid/graphics/drawable/Drawable$Callback;)V

    .line 140
    iput-object v2, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeCamDrawable:Landroid/graphics/drawable/Drawable;

    .line 142
    :cond_2
    iget-object v1, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeVideoDrawable:Landroid/graphics/drawable/Drawable;

    if-eqz v1, :cond_3

    .line 143
    iget-object v1, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeVideoDrawable:Landroid/graphics/drawable/Drawable;

    invoke-virtual {v1, v2}, Landroid/graphics/drawable/Drawable;->setCallback(Landroid/graphics/drawable/Drawable$Callback;)V

    .line 144
    iput-object v2, p0, Lcom/lge/camera/components/SwitcherLeverVertical;->mModeVideoDrawable:Landroid/graphics/drawable/Drawable;

    .line 146
    :cond_3
    return-void
.end method
