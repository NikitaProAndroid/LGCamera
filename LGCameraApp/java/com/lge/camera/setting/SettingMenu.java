package com.lge.camera.setting;

import android.content.ContentResolver;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.MmsProperties;
import com.lge.camera.properties.ModelProperties;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public abstract class SettingMenu extends Observable implements Observer {
    public static final int DEFAULT_VIDEO_DURATION = 1800;
    public static final String VIDEO_QUALITY_HIGH = "high";
    public static final String VIDEO_QUALITY_YOUTUBE = "youtube";
    public static final int YOUTUBE_VIDEO_DURATION = 600;
    protected int backupCurrentMenuIndex;
    protected int currentMenuIndex;
    protected int mBrightnessValue;
    public SettingMenuFunction mGet;
    protected Setting mSetting;
    protected int mZoomValue;
    protected ArrayList<SettingMenuItem> menu;

    public interface SettingMenuFunction {
        void addMMSTexture(PreferenceGroup preferenceGroup);

        ContentResolver getContentResolver();

        String getString(int i);

        void initSettingMenu();

        boolean isAttachIntent();

        boolean isMMSIntent();

        void removePostRunnable(Runnable runnable);

        void removeSettingItem();

        void runOnUiThread(Runnable runnable);

        void setAllChildMenuEnabled(String str, boolean z);
    }

    public abstract Setting getSetting();

    protected abstract void initMenu();

    public SettingMenu(SettingMenuFunction function) {
        this.backupCurrentMenuIndex = -1;
        this.mZoomValue = -1;
        this.mBrightnessValue = -1;
        this.mGet = null;
        this.mGet = function;
    }

    public void setSettingPreferenceGroupForVideo(PreferenceGroup preferenceGroup) {
        if (this.mGet.isMMSIntent() || !(this.mGet.isAttachIntent() || MmsProperties.getMmsResolutionsLength(this.mGet.getContentResolver()) == 0)) {
            this.mGet.addMMSTexture(preferenceGroup);
            this.mSetting.setPreferenceGroup(preferenceGroup);
            return;
        }
        this.mSetting.setPreferenceGroup(preferenceGroup);
    }

    public SettingMenuItem getMenu(int index) {
        return (SettingMenuItem) this.menu.get(index);
    }

    public ArrayList<SettingMenuItem> getMenuList() {
        return this.menu;
    }

    public SettingMenuItem getMenu(String key) {
        if (key == null) {
            return null;
        }
        int index = getMenuIndex(key);
        if (index >= 0) {
            return (SettingMenuItem) this.menu.get(index);
        }
        return null;
    }

    public int getMenuIndex(String key) {
        int index = -1;
        if (key == null) {
            return -1;
        }
        int menuSize = this.menu.size();
        for (int i = 0; i < menuSize; i++) {
            if (key.equals(((SettingMenuItem) this.menu.get(i)).getKey())) {
                index = i;
                break;
            }
        }
        return index;
    }

    public int getSelectedChild(String key) {
        if (key == null) {
            return -1;
        }
        int index = getMenuIndex(key);
        if (index >= 0) {
            return ((SettingMenuItem) this.menu.get(index)).selectedChildPosition;
        }
        return -1;
    }

    public void removeMenuItem(int index) {
        this.menu.remove(index);
    }

    public void insertMenuItem(int index, SettingMenuItem item) {
        this.menu.add(index, item);
    }

    public SettingMenuItem findMenuBySettingIndex(int settingIndex) {
        int menuCount = this.menu.size();
        SettingMenuItem menuItem = null;
        for (int i = 0; i < menuCount; i++) {
            menuItem = (SettingMenuItem) this.menu.get(i);
            if (menuItem.getSettingIndex() == settingIndex) {
                break;
            }
        }
        return menuItem;
    }

    public SettingMenuItem getCurrentMenu() {
        try {
            return (SettingMenuItem) this.menu.get(this.currentMenuIndex);
        } catch (IndexOutOfBoundsException e) {
            this.currentMenuIndex = 0;
            return (SettingMenuItem) this.menu.get(this.currentMenuIndex);
        }
    }

    public int getCurrentMenuIndex() {
        return this.currentMenuIndex;
    }

    public void setBackupCurrentMenuIndex(int index) {
        this.backupCurrentMenuIndex = index;
    }

    public int getBackupCurrentMenuIndex() {
        return this.backupCurrentMenuIndex;
    }

    public boolean setCurrentMenu(int index) {
        if (this.currentMenuIndex == index) {
            return false;
        }
        this.currentMenuIndex = index;
        setChanged();
        notifyObservers();
        return true;
    }

    public boolean setCurrentMenuOnly(int index) {
        if (this.currentMenuIndex == index) {
            return false;
        }
        this.currentMenuIndex = index;
        return true;
    }

    public int getMenuCount() {
        return this.menu.size();
    }

    public SettingMenuItem getSelectedChild() {
        return ((SettingMenuItem) this.menu.get(this.currentMenuIndex)).getChild(((SettingMenuItem) this.menu.get(this.currentMenuIndex)).selectedChildPosition);
    }

    public int getChildIndex(String value) {
        return ((SettingMenuItem) this.menu.get(this.currentMenuIndex)).getChildIndex(value);
    }

    public int getSelectedChildIndex() {
        return ((SettingMenuItem) this.menu.get(this.currentMenuIndex)).selectedChildPosition;
    }

    public boolean setSelectedChild(int index) {
        SettingMenuItem currentMenu = (SettingMenuItem) this.menu.get(this.currentMenuIndex);
        if (currentMenu.selectedChildPosition == index) {
            return false;
        }
        currentMenu.selectedChildPosition = index;
        this.mSetting.setSetting(((SettingMenuItem) this.menu.get(this.currentMenuIndex)).getKey(), index);
        setChanged();
        notifyObservers();
        return true;
    }

    public boolean setSelectedChild(int menuIndex, int index) {
        SettingMenuItem currentMenu = (SettingMenuItem) this.menu.get(menuIndex);
        if (currentMenu.selectedChildPosition == index) {
            return false;
        }
        currentMenu.selectedChildPosition = index;
        this.mSetting.setSetting(currentMenu.getKey(), index);
        setChanged();
        notifyObservers();
        return true;
    }

    public void setSelectedChild(String key, int index) {
        int parentMenuIndex = getMenuIndex(key);
        boolean isQFLMenu = false;
        if (parentMenuIndex < 0 || parentMenuIndex >= this.menu.size()) {
            if (this.mSetting.getQfIndex(key) != -1) {
                this.mGet.initSettingMenu();
                parentMenuIndex = getMenuIndex(key);
                isQFLMenu = true;
            } else {
                return;
            }
        }
        SettingMenuItem currentMenu = (SettingMenuItem) this.menu.get(parentMenuIndex);
        if (currentMenu.selectedChildPosition != index) {
            currentMenu.selectedChildPosition = index;
            this.mSetting.setSetting(currentMenu.getKey(), index);
            if (isQFLMenu) {
                this.mGet.removeSettingItem();
            }
            setChanged();
            notifyObservers();
        } else if (isQFLMenu) {
            this.mGet.removeSettingItem();
        }
    }

    public void setEnabled(String key, String value, boolean enable) {
        int parentMenuIndex = getMenuIndex(key);
        boolean isQFLMenu = false;
        if (parentMenuIndex < 0 || parentMenuIndex >= this.menu.size()) {
            if (this.mSetting.getQfIndex(key) != -1) {
                this.mGet.initSettingMenu();
                parentMenuIndex = getMenuIndex(key);
                isQFLMenu = true;
            } else {
                return;
            }
        }
        ListPreference listPref = this.mSetting.getSettingListPreference(key);
        if (listPref != null) {
            SettingMenuItem parentMenuItem = (SettingMenuItem) this.menu.get(parentMenuIndex);
            int childMenuIndex = listPref.findIndexOfValue(value);
            if (childMenuIndex < 0) {
                childMenuIndex = 0;
            }
            SettingMenuItem childMenuItem = parentMenuItem.getChild(childMenuIndex);
            if (childMenuItem.enable != enable) {
                childMenuItem.enable = enable;
                if (isQFLMenu) {
                    this.mGet.removeSettingItem();
                }
                setChanged();
                notifyObservers();
            } else if (isQFLMenu) {
                this.mGet.removeSettingItem();
            }
        }
    }

    public void setEnabled(String key, boolean enable) {
        int parentMenuIndex = getMenuIndex(key);
        boolean isQFLMenu = false;
        if (parentMenuIndex < 0 || parentMenuIndex >= this.menu.size()) {
            if (this.mSetting.getQfIndex(key) != -1) {
                this.mGet.initSettingMenu();
                parentMenuIndex = getMenuIndex(key);
                isQFLMenu = true;
            } else {
                return;
            }
        }
        SettingMenuItem parentMenuItem = (SettingMenuItem) this.menu.get(parentMenuIndex);
        if (parentMenuItem.enable != enable) {
            parentMenuItem.enable = enable;
            if (isQFLMenu) {
                this.mGet.removeSettingItem();
            }
            setChanged();
            notifyObservers();
        } else if (isQFLMenu) {
            this.mGet.removeSettingItem();
        }
    }

    public void setAllChildMenuEnabled(String key, boolean enable) {
        if (this.mGet != null) {
            this.mGet.setAllChildMenuEnabled(key, enable);
        }
    }

    public void setSelectedChild(String key, String value, boolean saveSetting) {
        int parentMenuIndex = getMenuIndex(key);
        boolean isQFLMenu = false;
        if (parentMenuIndex < 0 || parentMenuIndex >= this.menu.size()) {
            if (this.mSetting.getQfIndex(key) != -1) {
                this.mGet.initSettingMenu();
                parentMenuIndex = getMenuIndex(key);
                isQFLMenu = true;
            } else {
                return;
            }
        }
        ListPreference listPref = this.mSetting.getSettingListPreference(key);
        if (listPref != null) {
            int childMenuIndex = listPref.findIndexOfValue(value);
            if (parentMenuIndex < 0 || parentMenuIndex >= this.menu.size()) {
                if (isQFLMenu) {
                    this.mGet.removeSettingItem();
                }
            } else if (childMenuIndex >= 0) {
                SettingMenuItem parentMenuItem = (SettingMenuItem) this.menu.get(parentMenuIndex);
                if (parentMenuItem.selectedChildPosition != childMenuIndex) {
                    parentMenuItem.selectedChildPosition = childMenuIndex;
                    if (saveSetting) {
                        this.mSetting.setSetting(parentMenuItem.getKey(), childMenuIndex);
                    }
                    if (isQFLMenu) {
                        this.mGet.removeSettingItem();
                    }
                    setChanged();
                    notifyObservers();
                } else if (isQFLMenu) {
                    this.mGet.removeSettingItem();
                }
            } else if (isQFLMenu) {
                this.mGet.removeSettingItem();
            }
        }
    }

    public void update(Observable arg0, Object arg1) {
        int menuSize = this.menu.size();
        for (int i = 0; i < menuSize; i++) {
            SettingMenuItem parentMenu = (SettingMenuItem) this.menu.get(i);
            parentMenu.selectedChildPosition = this.mSetting.getSettingIndex(parentMenu.getKey());
        }
        setChanged();
        notifyObservers();
    }

    public String getCurrentChildSettingValue(int menuIndex) {
        if (this.mSetting == null || this.menu == null || this.menu.get(menuIndex) == null) {
            return "";
        }
        String key = ((SettingMenuItem) this.menu.get(menuIndex)).getKey();
        ListPreference listPref = this.mSetting.getSettingListPreference(key);
        if (listPref == null) {
            return "";
        }
        String entry = listPref.getEntry();
        if (entry != null && !"".equals(entry)) {
            return entry;
        }
        if (!Setting.KEY_BRIGHTNESS.equals(key)) {
            return this.mSetting.getSettingValue(key);
        }
        float value = ((float) (Integer.parseInt(this.mSetting.getSettingValue(key)) - (CameraConstants.MAX_BRIGHTNESS_STEP / 2))) / 3.0f;
        if (ModelProperties.isMTKChipset()) {
            value *= CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK;
        }
        return String.format("%.1f", new Object[]{Float.valueOf(value)});
    }

    public String getCurrentChildSettingValue(String key) {
        if (this.mSetting == null || this.menu == null) {
            return "";
        }
        ListPreference listPref = this.mSetting.getSettingListPreference(key);
        if (listPref == null) {
            return "";
        }
        String entry = listPref.getEntry();
        if (entry != null && !"".equals(entry)) {
            return entry;
        }
        if (!Setting.KEY_BRIGHTNESS.equals(key)) {
            return this.mSetting.getSettingValue(key);
        }
        float value = ((float) (Integer.parseInt(this.mSetting.getSettingValue(key)) - (CameraConstants.MAX_BRIGHTNESS_STEP / 2))) / 3.0f;
        if (ModelProperties.isMTKChipset()) {
            value *= CameraConstants.PIP_SMARTZOOMFOCUS_GUIDE_THICK;
        }
        return String.format("%.1f", new Object[]{Float.valueOf(value)});
    }

    public int getCurrentChildSettingIcon(String key) {
        if (this.mSetting == null || this.menu == null) {
            return -1;
        }
        ListPreference listPref = this.mSetting.getSettingListPreference(key);
        if (listPref == null) {
            return -1;
        }
        int iconIndex = listPref.findIndexOfValue(listPref.getValue());
        int[] iconResources = listPref.getMenuIconResources();
        if (iconResources == null) {
            return -1;
        }
        if (iconIndex == -1) {
            return iconResources[0];
        }
        return iconResources[iconIndex];
    }

    public int[] getCurrentChildSettingIcons(String key) {
        if (this.mSetting == null || this.menu == null) {
            return null;
        }
        ListPreference listPref = this.mSetting.getSettingListPreference(key);
        if (listPref != null) {
            return listPref.getSettingMenuIconResources();
        }
        return null;
    }

    public void close() {
        for (int i = this.menu.size() - 1; i >= 0; i--) {
            ((SettingMenuItem) this.menu.get(i)).close();
            this.menu.set(i, null);
            this.menu.remove(i);
        }
        this.menu = null;
        this.mSetting.deleteObserver(this);
        this.mSetting = null;
    }
}
