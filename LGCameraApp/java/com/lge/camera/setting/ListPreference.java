package com.lge.camera.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.lge.camera.R;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Util;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;
import java.util.List;

public class ListPreference extends CameraPreference {
    private String mDefaultValue;
    private CharSequence[] mEntries;
    private String mEntryCommand;
    private CharSequence[] mEntryValues;
    private CharSequence[] mExtraInfos;
    private CharSequence[] mExtraInfos2;
    private CharSequence[] mExtraInfos3;
    private CharSequence[] mExtraInfos4;
    private int[] mIndicatorIconResources;
    private boolean mKeepLastValue;
    private String mKey;
    private boolean mLoaded;
    private String mMenuCommand;
    private int[] mMenuIconResources;
    private int[] mMenuIconResourcesExpand;
    private boolean mPersist;
    private boolean mSaveSettingEnabled;
    private String mSettingMenuCommand;
    private int[] mSettingMenuIconResources;
    private int[] mSettingMenuIconResourcesExpand;
    private String mTitle;
    private String mValue;

    public ListPreference(Context context, String prefName) {
        super(context, prefName);
        this.mLoaded = false;
        this.mKeepLastValue = false;
        this.mSaveSettingEnabled = true;
    }

    public ListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLoaded = false;
        this.mKeepLastValue = false;
        this.mSaveSettingEnabled = true;
        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.ListPreference, 0, 0);
        this.mKey = (String) Util.checkNotNull(styledAttrs.getString(0));
        this.mTitle = styledAttrs.getString(1);
        this.mDefaultValue = styledAttrs.getString(9);
        setEntries(styledAttrs.getTextArray(10));
        setEntryValues(styledAttrs.getTextArray(11));
        setMenuIconResources(context, styledAttrs);
        setMenuIconResourcesExpand(context, styledAttrs);
        setSettingMenuIconResources(context, styledAttrs);
        setSettingMenuIconResourcesExpand(context, styledAttrs);
        this.mEntryCommand = styledAttrs.getString(8);
        this.mMenuCommand = styledAttrs.getString(4);
        this.mSettingMenuCommand = styledAttrs.getString(7);
        setExtraInfos(styledAttrs.getTextArray(13));
        setExtraInfos2(styledAttrs.getTextArray(15));
        setExtraInfos3(styledAttrs.getTextArray(16));
        setExtraInfos4(styledAttrs.getTextArray(17));
        this.mPersist = styledAttrs.getBoolean(14, true);
        int iconsResId = styledAttrs.getResourceId(12, 0);
        if (iconsResId != 0) {
            TypedArray iconsTypedArray = context.getResources().obtainTypedArray(iconsResId);
            int arrayLength = iconsTypedArray.length();
            this.mIndicatorIconResources = new int[arrayLength];
            for (int i = 0; i < arrayLength; i++) {
                this.mIndicatorIconResources[i] = iconsTypedArray.getResourceId(i, 0);
            }
        }
        styledAttrs.recycle();
    }

    private void setSettingMenuIconResources(Context context, TypedArray styledAttrs) {
        int settingIconResId = styledAttrs.getResourceId(6, 0);
        if (settingIconResId != 0) {
            TypedArray ta = context.getResources().obtainTypedArray(settingIconResId);
            int arrayLength = ta.length();
            this.mSettingMenuIconResources = new int[arrayLength];
            for (int i = 0; i < arrayLength; i++) {
                this.mSettingMenuIconResources[i] = ta.getResourceId(i, 0);
            }
        }
    }

    private void setSettingMenuIconResourcesExpand(Context context, TypedArray styledAttrs) {
        int settingIconResId = styledAttrs.getResourceId(6, 0);
        if (settingIconResId != 0) {
            TypedArray ta = context.getResources().obtainTypedArray(settingIconResId);
            int arrayLength = ta.length();
            this.mSettingMenuIconResourcesExpand = new int[arrayLength];
            for (int i = 0; i < arrayLength; i++) {
                this.mSettingMenuIconResourcesExpand[i] = ta.getResourceId(i, 0);
            }
        }
    }

    private void setMenuIconResources(Context context, TypedArray styledAttrs) {
        int selectedIconsResId = styledAttrs.getResourceId(3, 0);
        if (selectedIconsResId != 0) {
            TypedArray ta = context.getResources().obtainTypedArray(selectedIconsResId);
            int arrayLength = ta.length();
            this.mMenuIconResources = new int[arrayLength];
            for (int i = 0; i < arrayLength; i++) {
                this.mMenuIconResources[i] = ta.getResourceId(i, 0);
            }
        }
    }

    private void setMenuIconResourcesExpand(Context context, TypedArray styledAttrs) {
        int selectedIconsResId = styledAttrs.getResourceId(3, 0);
        if (selectedIconsResId != 0) {
            TypedArray ta = context.getResources().obtainTypedArray(selectedIconsResId);
            int arrayLength = ta.length();
            this.mMenuIconResourcesExpand = new int[arrayLength];
            for (int i = 0; i < arrayLength; i++) {
                this.mMenuIconResourcesExpand[i] = ta.getResourceId(i, 0);
            }
        }
    }

    public String getKey() {
        return this.mKey;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public CharSequence[] getEntries() {
        return this.mEntries;
    }

    public CharSequence[] getEntryValues() {
        return this.mEntryValues;
    }

    public int[] getMenuIconResources() {
        return this.mMenuIconResources;
    }

    public void setMenuIconResources(int[] values) {
        if (values == null) {
            values = new int[0];
        }
        this.mMenuIconResources = values;
    }

    public int[] getMenuIconResourcesExpand() {
        return this.mMenuIconResourcesExpand;
    }

    public void setMenuIconResourcesExpnd(int[] values) {
        if (values == null) {
            values = new int[0];
        }
        this.mMenuIconResourcesExpand = values;
    }

    public int[] getSettingMenuIconResources() {
        return this.mSettingMenuIconResources;
    }

    public void setSettingMenuIconResources(int[] values) {
        if (values == null) {
            values = new int[0];
        }
        this.mSettingMenuIconResources = values;
    }

    public int[] getSettingMenuIconResourcesExpand() {
        return this.mSettingMenuIconResourcesExpand;
    }

    public void setSettingMenuIconResourcesExpand(int[] values) {
        if (values == null) {
            values = new int[0];
        }
        this.mSettingMenuIconResourcesExpand = values;
    }

    public int[] getIndicatorIconResources() {
        if (this.mIndicatorIconResources == null) {
            this.mIndicatorIconResources = new int[1];
            this.mIndicatorIconResources[0] = 0;
        }
        return this.mIndicatorIconResources;
    }

    public void setIndicatorIconResources(int[] values) {
        if (values == null) {
            values = new int[0];
        }
        this.mIndicatorIconResources = values;
    }

    public int getIndicatorIconResource() {
        if (this.mIndicatorIconResources == null) {
            return 0;
        }
        int index = findIndexOfValue(getValue());
        if (index < 0) {
            return 0;
        }
        return this.mIndicatorIconResources[index];
    }

    public String getDefaultValue() {
        return this.mDefaultValue;
    }

    public void setDefaultValue(String defValue) {
        this.mDefaultValue = defValue;
    }

    public void setEntries(CharSequence[] entries) {
        if (entries == null) {
            entries = new CharSequence[0];
        }
        this.mEntries = entries;
    }

    public void setEntryValues(CharSequence[] values) {
        if (values == null) {
            values = new CharSequence[0];
        }
        this.mEntryValues = values;
    }

    public void setExtraInfos(CharSequence[] extraInfos) {
        if (extraInfos == null) {
            extraInfos = new CharSequence[0];
        }
        this.mExtraInfos = extraInfos;
    }

    public void setExtraInfos2(CharSequence[] extraInfos) {
        if (extraInfos == null) {
            extraInfos = new CharSequence[0];
        }
        this.mExtraInfos2 = extraInfos;
    }

    public void setExtraInfos3(CharSequence[] extraInfos) {
        if (extraInfos == null) {
            extraInfos = new CharSequence[0];
        }
        this.mExtraInfos3 = extraInfos;
    }

    public void setExtraInfos4(CharSequence[] extraInfos) {
        if (extraInfos == null) {
            extraInfos = new CharSequence[0];
        }
        this.mExtraInfos4 = extraInfos;
    }

    public String getEntryCommand() {
        return this.mEntryCommand;
    }

    public void setEntryCommand(String entryCommand) {
        this.mEntryCommand = entryCommand;
    }

    public String getCommand() {
        return this.mMenuCommand;
    }

    public void setCommand(String menuCommand) {
        this.mMenuCommand = menuCommand;
    }

    public String getSettingMenuCommand() {
        return this.mSettingMenuCommand;
    }

    public void setSettingMenuCommand(String settingMenuCommand) {
        this.mSettingMenuCommand = settingMenuCommand;
    }

    public String getExtraInfo() {
        int index = findIndexOfValue(getValue());
        if (index < 0) {
            return "";
        }
        return this.mExtraInfos[index].toString();
    }

    public String getExtraInfo2() {
        int index = findIndexOfValue(getValue());
        if (index < 0) {
            return "";
        }
        return this.mExtraInfos2[index].toString();
    }

    public String getExtraInfo3() {
        int index = findIndexOfValue(getValue());
        if (index < 0) {
            return "";
        }
        return this.mExtraInfos3[index].toString();
    }

    public String getExtraInfo4() {
        int index = findIndexOfValue(getValue());
        if (index < 0 || index >= this.mExtraInfos4.length) {
            return "";
        }
        return this.mExtraInfos4[index].toString();
    }

    public CharSequence[] getExtraInfos() {
        return this.mExtraInfos;
    }

    public CharSequence[] getExtraInfos2() {
        return this.mExtraInfos2;
    }

    public CharSequence[] getExtraInfos3() {
        return this.mExtraInfos3;
    }

    public CharSequence[] getExtraInfos4() {
        return this.mExtraInfos4;
    }

    public void setSaveSettingEnabled(boolean state) {
        this.mSaveSettingEnabled = state;
    }

    public void setPersist(boolean persist) {
        this.mPersist = persist;
    }

    public String getValue() {
        if (!this.mLoaded) {
            SharedPreferences pref = getSharedPreferences();
            if (pref == null || !(this.mPersist || this.mKeepLastValue)) {
                this.mValue = this.mDefaultValue;
                setValue(this.mValue);
            } else {
                this.mValue = pref.getString(this.mKey, this.mDefaultValue);
                if (this.mEntryValues.length != 0 && findIndexOfValue(this.mValue) == -1) {
                    this.mValue = this.mDefaultValue;
                    if (this.mSaveSettingEnabled) {
                        persistStringValue(this.mValue);
                    }
                }
                this.mKeepLastValue = false;
            }
            this.mLoaded = true;
        }
        return this.mValue;
    }

    public void setValue(String value) {
        if (findIndexOfValue(value) >= 0 || this.mEntryValues.length <= 0) {
            this.mValue = value;
            if (this.mSaveSettingEnabled) {
                persistStringValue(value);
                return;
            }
            return;
        }
        throw new IllegalArgumentException();
    }

    public void setValueIndex(int index) {
        if (index >= 0 && index < this.mEntryValues.length) {
            setValue(this.mEntryValues[index].toString());
        }
    }

    public int findIndexOfValue(String value) {
        int n = this.mEntryValues.length;
        for (int i = 0; i < n; i++) {
            if (Util.equals(this.mEntryValues[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public String getEntry() {
        int index = findIndexOfValue(getValue());
        if (index < 0) {
            return "";
        }
        return this.mEntries[index].toString();
    }

    protected void persistStringValue(String value) {
        Editor editor = getSharedPreferences().edit();
        editor.putString(this.mKey, value);
        editor.apply();
    }

    public void reloadValue() {
        this.mLoaded = false;
    }

    public void filterUnsupported(List<String> supported) {
        int i;
        ArrayList<CharSequence> entries = new ArrayList();
        ArrayList<CharSequence> entryValues = new ArrayList();
        CamLog.d(FaceDetector.TAG, "Preference ---------------------------------------------------------");
        for (i = 0; i < supported.size(); i++) {
            CamLog.d(FaceDetector.TAG, "Preference Device support item [" + String.format("%02d", new Object[]{Integer.valueOf(i)}) + "]\t\t: [" + ((String) supported.get(i)) + "]");
        }
        CamLog.d(FaceDetector.TAG, "Preference ---------------------------------------------------------");
        int len = this.mEntryValues.length;
        for (i = 0; i < len; i++) {
            CamLog.d(FaceDetector.TAG, "Preference XML Defined values/entries\t: [" + this.mEntryValues[i].toString() + "] / [" + this.mEntries[i].toString() + "]");
            if (supported.indexOf(this.mEntryValues[i].toString()) >= 0) {
                entries.add(this.mEntries[i]);
                entryValues.add(this.mEntryValues[i]);
            }
        }
        int size = entries.size();
        CamLog.d(FaceDetector.TAG, "Preference supported entries count [" + size + "]");
        CamLog.d(FaceDetector.TAG, "Preference ---------------------------------------------------------");
        this.mEntries = (CharSequence[]) entries.toArray(new CharSequence[size]);
        this.mEntryValues = (CharSequence[]) entryValues.toArray(new CharSequence[size]);
    }

    public void keepLastValue() {
        this.mKeepLastValue = true;
    }
}
