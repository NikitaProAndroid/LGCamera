package com.lge.almalence.app.clearshot;

public class Size {
    private int height;
    private int width;

    public Size(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public boolean isValid() {
        if (this.width <= 0 || this.height <= 0) {
            return false;
        }
        return true;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
