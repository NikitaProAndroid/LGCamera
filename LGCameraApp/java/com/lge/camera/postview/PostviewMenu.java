package com.lge.camera.postview;

import android.graphics.drawable.Drawable;

public class PostviewMenu {
    Drawable Icon;
    String PackageName;
    String labelApp;
    String labelImage;

    public PostviewMenu(String labelImage, String labelApp, String PackageName, Drawable Icon) {
        this.labelImage = null;
        this.labelApp = null;
        this.PackageName = null;
        this.Icon = null;
        this.labelImage = labelImage;
        this.labelApp = labelApp;
        this.PackageName = PackageName;
        this.Icon = Icon;
    }

    public String getLabelImage() {
        return this.labelImage;
    }

    public void setLabelImage(String labelImage) {
        this.labelImage = labelImage;
    }

    public String getLabelApp() {
        return this.labelApp;
    }

    public void setLabelApp(String labelApp) {
        this.labelApp = labelApp;
    }

    public String getPackageName() {
        return this.PackageName;
    }

    public void setPackageName(String packageName) {
        this.PackageName = packageName;
    }

    public Drawable getIcon() {
        return this.Icon;
    }

    public void setIcon(Drawable icon) {
        this.Icon = icon;
    }
}
