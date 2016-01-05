package com.lge.camera.setting;

import android.content.Context;
import android.util.AttributeSet;
import java.util.ArrayList;
import java.util.Iterator;

public class PreferenceGroup extends CameraPreference {
    private ArrayList<CameraPreference> list;

    public PreferenceGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.list = new ArrayList();
    }

    public void addChild(CameraPreference child) {
        child.setSharedPreferenceName(getSharedPreferenceName());
        this.list.add(child);
    }

    public void addChildAt(CameraPreference child, int index) {
        child.setSharedPreferenceName(getSharedPreferenceName());
        this.list.add(index, child);
    }

    public void removePreference(int index) {
        this.list.remove(index);
    }

    public CameraPreference get(int index) {
        return (CameraPreference) this.list.get(index);
    }

    public int size() {
        return this.list.size();
    }

    public void reloadValue() {
        Iterator i$ = this.list.iterator();
        while (i$.hasNext()) {
            ((CameraPreference) i$.next()).reloadValue();
        }
    }

    public ListPreference findPreference(String key) {
        Iterator i$ = this.list.iterator();
        while (i$.hasNext()) {
            CameraPreference pref = (CameraPreference) i$.next();
            ListPreference listPref;
            if (pref instanceof ListPreference) {
                listPref = (ListPreference) pref;
                if (listPref.getKey().equals(key)) {
                    return listPref;
                }
            } else if (pref instanceof PreferenceGroup) {
                listPref = ((PreferenceGroup) pref).findPreference(key);
                if (listPref != null) {
                    return listPref;
                }
            } else {
                continue;
            }
        }
        return null;
    }

    public ListPreference getListPreference(int index) {
        try {
            CameraPreference pref = (CameraPreference) this.list.get(index);
            if (pref instanceof ListPreference) {
                return (ListPreference) pref;
            }
            if (pref instanceof PreferenceGroup) {
                return ((PreferenceGroup) pref).getListPreference(index);
            }
            return null;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public int findPreferenceIndex(String key) {
        int i = 0;
        Iterator i$ = this.list.iterator();
        while (i$.hasNext()) {
            CameraPreference pref = (CameraPreference) i$.next();
            if (!(pref instanceof ListPreference)) {
                if ((pref instanceof PreferenceGroup) && ((PreferenceGroup) pref).findPreference(key) != null) {
                    break;
                }
            } else if (((ListPreference) pref).getKey().equals(key)) {
                break;
            }
            i++;
        }
        return i;
    }
}
