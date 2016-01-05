package com.lge.camera.setting;

import android.content.Context;
import com.lge.camera.R;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.setting.SettingMenu.SettingMenuFunction;
import java.util.ArrayList;

public abstract class CameraSettingMenu extends SettingMenu {
    public CameraSettingMenu(SettingMenuFunction function, Context context, CameraSetting setting) {
        super(function);
        this.mSetting = setting;
        this.mSetting.addObserver(this);
    }

    public CameraSetting getSetting() {
        return (CameraSetting) this.mSetting;
    }

    protected void initMenu() {
        String menuName = "";
        String key = "";
        this.menu = new ArrayList();
        ArrayList<Integer> removeIndex = new ArrayList();
        int prefSize = this.mSetting.getPreferenceGroup().size();
        for (int i = 0; i < prefSize; i++) {
            ListPreference listPref = this.mSetting.getPreferenceGroup().getListPreference(i);
            if (listPref != null) {
                CharSequence[] entries = listPref.getEntries();
                CharSequence[] values = listPref.getEntryValues();
                key = listPref.getKey();
                if (!(Setting.KEY_SETTING.equals(key) || Setting.KEY_ZOOM.equals(key))) {
                    SettingMenuItem parentMenu = new SettingMenuItem(i, getParentSettingMenuName(listPref));
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
                    parentMenu.setKey(key);
                    this.menu.add(parentMenu);
                    for (int j = 0; j < entries.length; j++) {
                        SettingMenuItem childMenu = new SettingMenuItem(j, helpGuideVariation(entries[j].toString(), listPref));
                        childMenu.setParameterValue(values[j].toString());
                        childMenu.setCommand(listPref.getEntryCommand());
                        parentMenu.addChild(childMenu);
                    }
                }
            }
        }
        removeMenuIndexList(removeIndex);
    }

    private String helpGuideVariation(String menuName, ListPreference listPref) {
        if (FunctionProperties.isVoiceShutter() && FunctionProperties.useCheeseShutterTitle() && Setting.KEY_HELP_GUIDE.equals(listPref.getKey()) && menuName.equals(this.mGet.getString(R.string.sp_voiceshutter_NORMAL))) {
            menuName = this.mGet.getString(R.string.sp_cheeseshutter_NORMAL);
        }
        if (!FunctionProperties.isTimeMachinShotSupported() || !FunctionProperties.useTimeCatchShotTitle()) {
            return menuName;
        }
        if ((Setting.KEY_HELP_GUIDE.equals(listPref.getKey()) || Setting.KEY_CAMERA_SHOT_MODE.equals(listPref.getKey())) && menuName.equals(this.mGet.getString(R.string.sp_shot_mode_time_machine_NORMAL))) {
            return this.mGet.getString(R.string.sp_shot_mode_time_catch_NORMAL);
        }
        return menuName;
    }

    private void removeMenuIndexList(ArrayList<Integer> removeIndex) {
        if (removeIndex != null && removeIndex.size() > 0) {
            for (int i = removeIndex.size() - 1; i >= 0; i--) {
                this.mSetting.getPreferenceGroup().removePreference(((Integer) removeIndex.get(i)).intValue());
            }
        }
    }

    private String getParentSettingMenuName(ListPreference listPref) {
        String menuName = listPref.getTitle();
        if (FunctionProperties.isVoiceShutter() && FunctionProperties.useCheeseShutterTitle() && Setting.KEY_VOICESHUTTER.equals(listPref.getKey())) {
            menuName = this.mGet.getString(R.string.sp_cheeseshutter_NORMAL);
        }
        if (FunctionProperties.isTimeMachinShotSupported() && FunctionProperties.useTimeCatchShotTitle() && Setting.KEY_TIME_MACHINE.equals(listPref.getKey())) {
            return this.mGet.getString(R.string.sp_shot_mode_time_catch_NORMAL);
        }
        return menuName;
    }
}
