package com.lge.camera.components;

import android.graphics.drawable.Drawable;

public class OptionMenu {
    Drawable menuIcon;
    String title;

    public OptionMenu(String title, Drawable icon) {
        this.title = null;
        this.menuIcon = null;
        this.title = title;
        this.menuIcon = icon;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getMenuIcon() {
        return this.menuIcon;
    }

    public void setMenuIcon(Drawable icon) {
        this.menuIcon = icon;
    }
}
