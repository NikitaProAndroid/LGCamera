package com.lge.camera.setting;

import java.util.ArrayList;

public class SettingMenuItem {
    public boolean enable;
    public int iconResourceId;
    private ArrayList<SettingMenuItem> mChildList;
    private String mCommand;
    private String mKey;
    private String mParameterValue;
    private int mSettingIndex;
    private String mSettingMenuCommand;
    public String name;
    public int selectedChildPosition;

    public SettingMenuItem(int settingIndex, String name) {
        this.mChildList = new ArrayList();
        this.mCommand = "";
        this.mKey = "";
        this.mSettingMenuCommand = "";
        this.mParameterValue = "";
        this.selectedChildPosition = 0;
        this.enable = true;
        this.mSettingIndex = settingIndex;
        this.name = name;
    }

    public int getSettingIndex() {
        return this.mSettingIndex;
    }

    public String getCommand() {
        return this.mCommand;
    }

    public String getSettingMenuCommand() {
        return this.mSettingMenuCommand;
    }

    public String getKey() {
        return this.mKey;
    }

    public void setCommand(String command) {
        this.mCommand = command;
    }

    public void setSettingMenuCommand(String command) {
        this.mSettingMenuCommand = command;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public void setIconResource(int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }

    public void setSelectedChildPosition(int selectedChildPosition) {
        this.selectedChildPosition = selectedChildPosition;
    }

    public int getSelectedChildPosition() {
        return this.selectedChildPosition;
    }

    public void setSelectedChildBySettingIndex(int settingIndex) {
        int count = this.mChildList.size();
        for (int i = 0; i < count; i++) {
            if (((SettingMenuItem) this.mChildList.get(i)).getSettingIndex() == settingIndex) {
                this.selectedChildPosition = i;
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public String getParameterValue() {
        return this.mParameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.mParameterValue = parameterValue;
    }

    public void addChild(SettingMenuItem child) {
        this.mChildList.add(child);
    }

    public SettingMenuItem getChild(int index) {
        return (SettingMenuItem) this.mChildList.get(index);
    }

    public SettingMenuItem getSelectedChild() {
        return (SettingMenuItem) this.mChildList.get(this.selectedChildPosition);
    }

    public int getChildCount() {
        return this.mChildList.size();
    }

    public int getChildIndex(String value) {
        for (int i = 0; i < getChildCount(); i++) {
            if (value.equals(getChild(i).getParameterValue())) {
                return i;
            }
        }
        return -1;
    }

    public boolean setEnabled(boolean enable) {
        if (this.enable == enable) {
            return false;
        }
        this.enable = enable;
        return true;
    }

    public void close() {
        this.mChildList.clear();
        if (this.mChildList.isEmpty()) {
            this.mChildList = null;
        }
    }
}
