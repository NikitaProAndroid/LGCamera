package com.lge.camera.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ComboPreferences implements SharedPreferences, OnSharedPreferenceChangeListener {
    private static WeakHashMap<Context, ComboPreferences> sMap;
    private CopyOnWriteArrayList<OnSharedPreferenceChangeListener> mListeners;
    private SharedPreferences mPrefGlobal;
    private SharedPreferences mPrefLocal;

    private class MyEditor implements Editor {
        private Editor mEditorGlobal;
        private Editor mEditorLocal;

        MyEditor() {
            this.mEditorGlobal = ComboPreferences.this.mPrefGlobal.edit();
            this.mEditorLocal = ComboPreferences.this.mPrefLocal.edit();
        }

        public boolean commit() {
            return this.mEditorGlobal.commit() && this.mEditorLocal.commit();
        }

        public void apply() {
            this.mEditorGlobal.apply();
            this.mEditorLocal.apply();
        }

        public Editor clear() {
            this.mEditorGlobal.clear();
            this.mEditorLocal.clear();
            return this;
        }

        public Editor remove(String key) {
            this.mEditorGlobal.remove(key);
            this.mEditorLocal.remove(key);
            return this;
        }

        public Editor putString(String key, String value) {
            if (ComboPreferences.isGlobal(key)) {
                this.mEditorGlobal.putString(key, value);
            } else {
                this.mEditorLocal.putString(key, value);
            }
            return this;
        }

        public Editor putInt(String key, int value) {
            if (ComboPreferences.isGlobal(key)) {
                this.mEditorGlobal.putInt(key, value);
            } else {
                this.mEditorLocal.putInt(key, value);
            }
            return this;
        }

        public Editor putLong(String key, long value) {
            if (ComboPreferences.isGlobal(key)) {
                this.mEditorGlobal.putLong(key, value);
            } else {
                this.mEditorLocal.putLong(key, value);
            }
            return this;
        }

        public Editor putFloat(String key, float value) {
            if (ComboPreferences.isGlobal(key)) {
                this.mEditorGlobal.putFloat(key, value);
            } else {
                this.mEditorLocal.putFloat(key, value);
            }
            return this;
        }

        public Editor putBoolean(String key, boolean value) {
            if (ComboPreferences.isGlobal(key)) {
                this.mEditorGlobal.putBoolean(key, value);
            } else {
                this.mEditorLocal.putBoolean(key, value);
            }
            return this;
        }

        public Editor putStringSet(String arg0, Set<String> set) {
            return null;
        }
    }

    static {
        sMap = new WeakHashMap();
    }

    public ComboPreferences(Context context) {
        this.mPrefGlobal = context.getSharedPreferences(Setting.SETTING_PRIMARY, 0);
        this.mPrefGlobal.registerOnSharedPreferenceChangeListener(this);
        synchronized (sMap) {
            sMap.put(context, this);
        }
        this.mListeners = new CopyOnWriteArrayList();
    }

    public static ComboPreferences get(Context context) {
        ComboPreferences comboPreferences;
        synchronized (sMap) {
            comboPreferences = (ComboPreferences) sMap.get(context);
        }
        return comboPreferences;
    }

    public void setLocalId(Context context, int cameraId) {
        String prefName = context.getPackageName() + "_preferences_" + cameraId;
        if (this.mPrefLocal != null) {
            this.mPrefLocal.unregisterOnSharedPreferenceChangeListener(this);
        }
        try {
            this.mPrefLocal = context.getSharedPreferences(prefName, 0);
            this.mPrefLocal.registerOnSharedPreferenceChangeListener(this);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    public SharedPreferences getGlobal() {
        return this.mPrefGlobal;
    }

    public SharedPreferences getLocal() {
        return this.mPrefLocal;
    }

    public Map<String, ?> getAll() {
        throw new UnsupportedOperationException();
    }

    private static boolean isGlobal(String key) {
        return key.equals(Setting.KEY_CAMERA_ID) || key.equals(Setting.KEY_RECORD_LOCATION);
    }

    public String getString(String key, String defValue) {
        if (isGlobal(key) || !this.mPrefLocal.contains(key)) {
            return this.mPrefGlobal.getString(key, defValue);
        }
        return this.mPrefLocal.getString(key, defValue);
    }

    public int getInt(String key, int defValue) {
        if (isGlobal(key) || !this.mPrefLocal.contains(key)) {
            return this.mPrefGlobal.getInt(key, defValue);
        }
        return this.mPrefLocal.getInt(key, defValue);
    }

    public long getLong(String key, long defValue) {
        if (isGlobal(key) || !this.mPrefLocal.contains(key)) {
            return this.mPrefGlobal.getLong(key, defValue);
        }
        return this.mPrefLocal.getLong(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        if (isGlobal(key) || !this.mPrefLocal.contains(key)) {
            return this.mPrefGlobal.getFloat(key, defValue);
        }
        return this.mPrefLocal.getFloat(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        if (isGlobal(key) || !this.mPrefLocal.contains(key)) {
            return this.mPrefGlobal.getBoolean(key, defValue);
        }
        return this.mPrefLocal.getBoolean(key, defValue);
    }

    public Set<String> getStringSet(String key, Set<String> set) {
        throw new UnsupportedOperationException();
    }

    public boolean contains(String key) {
        if (this.mPrefLocal.contains(key) || this.mPrefGlobal.contains(key)) {
            return true;
        }
        return false;
    }

    public Editor edit() {
        return new MyEditor();
    }

    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        this.mListeners.add(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        this.mListeners.remove(listener);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Iterator i$ = this.mListeners.iterator();
        while (i$.hasNext()) {
            ((OnSharedPreferenceChangeListener) i$.next()).onSharedPreferenceChanged(this, key);
        }
    }
}
