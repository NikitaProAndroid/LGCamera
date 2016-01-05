package com.lge.olaworks.library;

import android.graphics.Canvas;

public abstract class BaseEngine implements IEngine {
    public static final int DEFAULT_PRIORITY = 10;
    protected boolean mEnable;
    protected boolean mFlipH;
    protected int mPriority;
    protected boolean mStart;

    public BaseEngine() {
        this.mPriority = DEFAULT_PRIORITY;
        this.mStart = true;
        this.mEnable = true;
        this.mFlipH = false;
    }

    public void enable(boolean enable) {
        this.mEnable = enable;
    }

    public boolean needRenderMode() {
        return false;
    }

    protected void setPriority(int priority) {
        this.mPriority = priority;
    }

    protected int getPriority() {
        return this.mPriority;
    }

    public void setFlipHorizontal(boolean flipH) {
        this.mFlipH = flipH;
    }

    public void drawOverlay(Canvas canvas) {
    }
}
