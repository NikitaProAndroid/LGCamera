package com.lge.camera.setting;

import android.content.Context;
import com.lge.camera.setting.SettingMenu.SettingMenuFunction;
import java.util.ArrayList;

public abstract class VideoSettingMenu extends SettingMenu {
    public VideoSettingMenu(SettingMenuFunction function, Context context, VideoSetting setting) {
        super(function);
        this.mSetting = setting;
        this.mSetting.addObserver(this);
    }

    public VideoSetting getSetting() {
        return (VideoSetting) this.mSetting;
    }

    protected void initMenu() {
        int i;
        this.menu = new ArrayList();
        String key = "";
        ArrayList<Integer> removeIndex = new ArrayList();
        int prefSize = this.mSetting.getPreferenceGroup().size();
        for (i = 0; i < prefSize; i++) {
            ListPreference listPref = this.mSetting.getPreferenceGroup().getListPreference(i);
            if (listPref != null) {
                CharSequence[] entries = listPref.getEntries();
                CharSequence[] values = listPref.getEntryValues();
                key = listPref.getKey();
                if (!(Setting.KEY_SETTING.equals(key) || Setting.KEY_ZOOM.equals(key))) {
                    SettingMenuItem parentMenu = new SettingMenuItem(i, listPref.getTitle());
                    int selectedPos = listPref.findIndexOfValue(listPref.getValue());
                    if (selectedPos < 0) {
                        selectedPos = 0;
                    }
                    parentMenu.setSelectedChildPosition(selectedPos);
                    if (listPref.getSettingMenuIconResources() != null) {
                        parentMenu.setIconResource(listPref.getSettingMenuIconResources()[0]);
                    }
                    parentMenu.setCommand(listPref.getCommand());
                    parentMenu.setSettingMenuCommand(listPref.getSettingMenuCommand());
                    parentMenu.setKey(listPref.getKey());
                    this.menu.add(parentMenu);
                    for (int j = 0; j < entries.length; j++) {
                        SettingMenuItem childMenu = new SettingMenuItem(j, entries[j].toString());
                        childMenu.setParameterValue(values[j].toString());
                        childMenu.setCommand(listPref.getEntryCommand());
                        parentMenu.addChild(childMenu);
                    }
                }
            }
        }
        if (removeIndex != null && removeIndex.size() > 0) {
            for (i = removeIndex.size() - 1; i >= 0; i--) {
                this.mSetting.getPreferenceGroup().removePreference(((Integer) removeIndex.get(i)).intValue());
            }
        }
    }
}
