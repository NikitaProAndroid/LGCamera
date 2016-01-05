package com.lge.olaworks.datastruct;

public class Ola_AutoPanoramaThumbInfo {
    public int dH;
    public int dW;
    public int height;
    public int tHeight;
    public int tWidth;
    public int width;

    public Ola_AutoPanoramaThumbInfo() {
        clear();
    }

    public void clear() {
        this.width = 0;
        this.height = 0;
        this.dW = 0;
        this.dH = 0;
        this.tWidth = 0;
        this.tHeight = 0;
    }
}
